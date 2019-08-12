package cl.usach.sd;

import java.util.ArrayList;

import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.WireKOut;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;

public class Layer implements Cloneable, EDProtocol {
	private static final String PAR_TRANSPORT = "transport";
	private static String prefix = null;
	private int transportId;
	private int layerId;

	/**
	 * Método en el cual se va a procesar el mensaje que ha llegado al Nodo
	 * desde otro Nodo. Cabe destacar que el mensaje va a ser el evento descrito
	 * en la clase, a través de la simulación de eventos discretos.
	 */
	@Override
	public void processEvent(Node myNode, int layerId, Object event) {
		/**Este metodo trabajará sobre el mensaje*/
		
		Message message = (Message) event;
		NetworkNode node = (NetworkNode) myNode;
		int spDestino = message.getSpDestino();
		
		// Verificacion para ver si se esta de ida o de vuelta para devolver el mensaje
		if((int) node.getID() == spDestino){
			message.setKwr(2);; // Ahora hay que hacer un k random walks para ver si la consulta esta en la subred del super peer
		}
		
		if(message.getIdaVuelta()==0){
		    // Guardar el nodo en el camino realizado
			((Message)message).getCamino().add((int) node.getIP());
			// aumenta el contador para el camino
			((Message)message).setContador(((Message)message).getContador()+1);
		}
		//System.out.println(""+((Message)message).getCamino()+" cont: "+((Message)message).getContador());
		
		sendmessage(myNode, layerId, message);
		getStats();
	}

	private void getStats() {
		Observer.message.add(1);
	}
	
	public void sendmessage(Node currentNode, int layerId, Object message) {
		/**Con este método se enviará el mensaje de un nodo a otro
		 * CurrentNode, es el nodo actual
		 * message, es el mensaje que viene como objeto, por lo cual se debe trabajar sobre él
		 */
		
		Message mensaje = (Message) message;
		NetworkNode node = (NetworkNode) currentNode;
		
		int consulta = mensaje.getConsulta();
		int emisor = ((Message) mensaje).getEmisor();
		int spDestino = ((Message) mensaje).getSpDestino();
		int idaVuelta = ((Message) mensaje).getIdaVuelta();
		int kwr = ((Message) mensaje).getKwr();
		int k = ((Message) mensaje).getK();
		int ttl = ((Message) mensaje).getTtl();
		int sigNode = -1;
		int temp = -1;
		int temp2 = -1;
		int i = 0;
		
		Node sendNode = null;
		
		////////////////////////////////////// Camino de Ida ////////////////////////////////////////////
		
		// Camino de ida para buscar la consulta en la subred del super peer de destino
		if(idaVuelta == 0){
			
			//---------------- Proceso para realizar el primer k random walks -----------------//
			if(kwr == 1){
				ArrayList<Integer> caminoAux = new ArrayList<Integer>();
				// k random walks para encontrar el super peer de la subred
				for(i=0; i<k; i++){
					while(temp2==-1){	
						ArrayList<Integer> Camino = new ArrayList<Integer>();
						temp2 = NetworkNode.randomWalks1(ttl, emisor, Camino);
						//System.out.println(""+temp2+", "+Camino);
					if(temp2!=-1)
						caminoAux.addAll(Camino);
					}
				}
				if(temp2 != -1){
					//System.out.println(""+temp2+","+caminoAux);
					((Message) mensaje).setKwr(0);
					((Message) mensaje).getCamino().addAll(caminoAux); // agrego el camino utilizado para encontar al super peer
					((Message) mensaje).getCamino().remove((int) ((Message) mensaje).getCamino().size()-1); // elimino el ultimo nodo de la lista
					((Message) mensaje).setContador(((Message) mensaje).getContador()+caminoAux.size()-2);
					sigNode = temp2;
					sendNode = Network.get(sigNode);
					((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
	    		    System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl peer con IP "+node.getIP()+" encuentra camino hacia el super peer con IP "+sigNode+" de la subred a traves de "+caminoAux);
				}
				else{
					System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl peer con IP "+node.getIP()+" no encontro un camino hacia el super peer de la subred");
				}
			}
			
			//---------------- Proceso para realizar los saltos por los super peers ----------------//
			else if(kwr == 0){
				if(node.getTipo()==1){ // si el nodo corresponde a un Super Peer
    				// se verifica si la consulta esta en el cache del nodo actual
    		    	if(((NetworkNode)Network.get((int) node.getID())).buscarEnCache((int) node.getID(), spDestino, consulta)==true){
    		    		System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con IP "+node.getIP()+ " tiene consulta "+consulta+" en cache");
    		    		// se setea ahora que ahora se tiene que ir de vuelta
    		    		mensaje.setIdaVuelta(1);
    		    		// se obtien la cache para verificar las consultas
    		    		int[] resp = ((NetworkNode) Network.get((int) node.getID())).getRespuestaCache((int) node.getID(), spDestino, consulta);
    		    		if(resp[0]==spDestino && resp[1]==consulta){
    		    			if(resp[2]==0){ // quiere decir que el nodo no tiene respuesta a esa consulta
    		    				mensaje.setRespuesta(-1); // se setea como si no hubiera respuesta
    		    				System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con IP "+node.getIP()+" responde que el super peer con IP "+spDestino+" no tiene resultado a la consulta "+resp[1]+" según su cache");
    		    			}
    		    			else if(resp[2]==1){ // quiere decir que el nodo si tiene respuesta a la consulta
    		    				mensaje.setRespuesta(resp[0]); // se setea la respuesta entregada
    		    				System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con IP "+node.getIP()+" responde que el super peer con IP "+spDestino+" tiene como resultado "+resp[1]+" a la consulta según su cache");
    		    			}
    		    		}
    		    		if(mensaje.getContador()>0){ // verifica si aun hay un camino que recorrer
    		    			// seteo como siguiente nodo el ultimo elemento de la lista del camino
    		    		    sigNode = mensaje.getCamino().get(mensaje.getContador()-1);
    		    		    //System.out.println(""+sigNode+":"+(mensaje.getContador()-1));
    		    		    sendNode = Network.get(sigNode);
    		    		    
    		    		    // se resta 1 al contador porque ya se comenzó el camino de vuelta
    		    		    mensaje.setContador(mensaje.getContador()-1);
    				
    		    		    //Envío del dato a través de la capa de transporte, la cual enviará según el ID del emisor y el receptor
    		    		    ((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
    		    		    System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con ip "+node.getIP()+" envia respuesta al peer con IP "+ ((NetworkNode) sendNode).getIP());
    		    		}
    		    	}
    		    	// en el caso de que no este en cache, sigo el camino hasta el siguiente nodo
    			    else if(((NetworkNode)Network.get((int) node.getID())).buscarEnCache((int) node.getID(), spDestino, consulta)==false){
    			    	String aux = new String("");
    			    	System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con IP "+node.getIP()+ " no tiene consulta "+consulta+" en cache");
    				
    			    	// se ven los posibles nodos con el DHT y el vecino
    			    	aux+="\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con IP "+node.getIP()+" calcula distancia más cercana al super peer con IP "+spDestino+" con";
    			    	for(int j=0; j<node.getDHT().size(); j++){
    					    aux+=", "+node.getDHT().get(j);
    		    		}
    		    		System.out.println(aux);
    		    		// se setea como nodo siguiente al vecino mas cercano con respecto a la distancia con el nodo de destino
    			    	sigNode = ((NetworkNode) Network.get((int) node.getID())).getVecinoMasCercano((int) node.getID(), spDestino);
    			    	sendNode = Network.get(sigNode);
    			    	
    			    	//Envío del dato a través de la capa de transporte, la cual enviará según el ID del emisor y el receptor
    			    	((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
    			    	System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con IP "+node.getIP()+" envia consulta al super peer con IP "+ ((NetworkNode) sendNode).getIP());

        			}
	    		}
	    	}
			
			//---------------- Proceso para realizar el segundo k random walks -----------------
			else if(kwr == 2){
				if(node.getIP()==spDestino){
					// se verifica si la consulta esta en el cache del nodo actual
    		    	if(((NetworkNode)Network.get((int) node.getID())).buscarEnCache((int) node.getID(), spDestino, consulta)==true){
    		    		System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con IP "+node.getIP()+ " tiene consulta "+consulta+" en cache");
    		    		// se setea ahora que ahora se tiene que ir de vuelta
    		    		mensaje.setIdaVuelta(1);
    		    		// se obtien la cache para verificar las consultas
    		    		int[] resp = ((NetworkNode) Network.get((int) node.getID())).getRespuestaCache((int) node.getID(), spDestino, consulta);
    		    		if(resp[0]==spDestino && resp[1]==consulta){
    		    			if(resp[2]==0){ // quiere decir que el nodo no tiene respuesta a esa consulta
    		    				mensaje.setRespuesta(-1); // se setea como si no hubiera respuesta
    		    				System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con IP "+node.getIP()+" responde que el super peer con IP "+spDestino+" no tiene resultado a la consulta "+resp[1]+" según su cache");
    		    			}
    		    			else if(resp[2]==1){ // quiere decir que el nodo si tiene respuesta a la consulta
    		    				mensaje.setRespuesta(resp[0]); // se setea la respuesta entregada
    		    				System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con IP "+node.getIP()+" responde que el super peer con IP "+spDestino+" tiene como resultado "+resp[1]+" a la consulta según su cache");
    		    			}
    		    		}
    		    		if(mensaje.getContador()>0){ // verifica si aun hay un camino que recorrer
    		    			// seteo como siguiente nodo el ultimo elemento de la lista del camino
    		    		    sigNode = mensaje.getCamino().get(mensaje.getContador()-1);
    		    		    //System.out.println(""+sigNode+":"+(mensaje.getContador()-1));
    		    		    sendNode = Network.get(sigNode);
    		    		    
    		    		    // se resta 1 al contador porque ya se comenzó el camino de vuelta
    		    		    mensaje.setContador(mensaje.getContador()-1);
    				
    		    		    //Envío del dato a través de la capa de transporte, la cual enviará según el ID del emisor y el receptor
    		    		    ((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
    		    		    System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con ip "+node.getIP()+" envia respuesta al peer con IP "+ ((NetworkNode) sendNode).getIP());
    		    		}
    		    	}
    		    	// en el caso de que no este en cache, sigo el camino hasta el siguiente nodo
    			    else if(((NetworkNode)Network.get((int) node.getID())).buscarEnCache((int) node.getID(), spDestino, consulta)==false){
    			    	System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con IP "+node.getIP()+ " no tiene consulta "+consulta+" en cache");
        				
    			    	ArrayList<Integer> caminoAux = new ArrayList<Integer>();
        				// k random walks para encontrar la consulta en la subred
        				for(i=0; i<k; i++){	
        					ArrayList<Integer> Camino = new ArrayList<Integer>();
        					temp2 = NetworkNode.randomWalks2(ttl, consulta, emisor, Camino);
        					//System.out.println(""+temp2+", "+Camino);
        					if(temp2!=-1)
        						caminoAux.addAll(Camino);
        				}
        				//System.out.println(""+temp2+","+caminoAux);
        				if(temp2 != -1){ // quiere decir que encontre la respuesta en la sub red
        					//System.out.println(""+temp2+","+caminoAux);
        					((Message) mensaje).getCamino().addAll(caminoAux); // agrego el camino utilizado para encontar al super peer
        					((Message) mensaje).getCamino().remove((int) ((Message) mensaje).getCamino().size()-1); // elimino el ultimo nodo de la lista
        					((Message) mensaje).setContador(((Message) mensaje).getContador()+caminoAux.size()-2);
        					sigNode = temp2;
        					sendNode = Network.get(sigNode);
        					((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
        	    		    System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\t*** El super peer con ip "+node.getIP()+" encuentra la consulta "+consulta+" en el peer con IP "+sigNode+" a traves del camino "+caminoAux);
        				}
        				else if(temp2 == -1){
        					System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\t*** El super peer con IP "+node.getIP()+" no encontro la consulta "+consulta+" en su subred");
        					((Message) mensaje).setIdaVuelta(1); //Ahora voy de vuelta
        					((Message) mensaje).setRespuesta(-1); // indico que no hay respuesta
        					
      		    		    // seteo como siguiente nodo el ultimo elemento de la lista del camino
     		    		    sigNode = mensaje.getCamino().get(mensaje.getContador()-1);
     		    		    //System.out.println(""+sigNode+":"+(mensaje.getContador()-1));
     		    		    sendNode = Network.get(sigNode);
       		    		    
        		    		// se resta 1 al contador porque esta en el camino de vuelta
        		    		mensaje.setContador(mensaje.getContador()-1);
        		    		 
        		    		// como el nodo corresponde a un super peer, actualizo su cache y tambien la de sus vecinos segun el dht
        		    		((NetworkNode) Network.get((int) node.getID())).actualizarCache((int) node.getID(), spDestino, consulta,0);
    		    		    
    		    		    //Envío del dato a través de la capa de transporte, la cual enviará según el ID del emisor y el receptor
    		    		    ((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
    		    		    System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl super peer con IP "+node.getIP()+" envia respuesta al super peer con IP"+sendNode.getID());	
        				}
        			}
				}
			}
			if(node.getTipo()==0 && node.getValor()==consulta){ // este es el momento en el cual se llega al nodo que tiene la colsuta
				((Message) mensaje).setRespuesta(node.getValor());
				((Message) mensaje).setIdaVuelta(1);
				
	    	    // seteo como siguiente nodo el ultimo elemento de la lista del camino
	    	    sigNode = mensaje.getCamino().get(mensaje.getContador()-1);
	    	    //System.out.println(""+sigNode+":"+(mensaje.getContador()-1));
	    	    sendNode = Network.get(sigNode);
	    		    
	    	    // se resta 1 al contador porque esta en el camino de vuelta
	    	    mensaje.setContador(mensaje.getContador()-1);
	    		    
	    	    //Envío del dato a través de la capa de transporte, la cual enviará según el ID del emisor y el receptor
	    	    ((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
	    	    System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl peer con IP "+node.getIP()+" envia respuesta al super peer con IP "+sendNode.getID());	    		
			}
		}
		
		//////////////////////////////////////////// Camino de vuelta ////////////////////////////////////////////
		
		// Camino de vuelta para llegar hasta el emisor
    	else if(idaVuelta == 1){
    		if(node.getTipo()==1){
    			if(((Message)mensaje).getRespuesta()==-1)
		    		((NetworkNode) Network.get((int) node.getID())).actualizarCache((int) node.getIP(), spDestino, consulta,0); // atualiza el cache del super peer y el de sus vecinos segun el dht
		    	else
		    		((NetworkNode) Network.get((int) node.getID())).actualizarCache((int) node.getIP(), spDestino, consulta,1); // atualiza el cache del super peer y el de sus vecinos segun el dht
    		}
    			
    		if(mensaje.getContador()>0){ // se verifica si aun hay camino por recorrer
	    		// seteo como siguiente nodo el ultimo elemento de la lista del camino
	    		sigNode = mensaje.getCamino().get(mensaje.getContador()-1);
	    		//System.out.println(""+sigNode+":"+(mensaje.getContador()-1));
	    		sendNode = Network.get(sigNode);
	    		    
	    		// se resta 1 al contador porque esta en el camino de vuelta
	    		mensaje.setContador(mensaje.getContador()-1);
			
	    	    //Envío del dato a través de la capa de transporte, la cual enviará según el ID del emisor y el receptor
	    	    ((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
	    	    String aux2 = "";
	    	    String aux3 = "";
	    	    if(node.getTipo()==1)
	    	    	aux2 = " super peer ";
	    	    if(node.getTipo()==0)
	    	    	aux2 = " peer ";
	    	    if(((NetworkNode) Network.get(sigNode)).getTipo()==1)
	    	    	aux3 = " super peer ";
	    	    if(((NetworkNode) Network.get(sigNode)).getTipo()==0)
	    	    	aux3 = " peer ";
	   		    System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tEl"+aux2+"con IP "+node.getIP()+" envia respuesta al"+aux3+"con IP "+ ((NetworkNode) sendNode).getID());
    	    }
    		
    		// Verificacion para ver si se llega al emisor nuevamente
    		if(node.getID()==emisor && mensaje.getContador()==0){
    			System.out.println("\t["+emisor+","+spDestino+","+consulta+"]\tProceso terminado para esta consulta.\n");
    			return;
    		}
    	}
	}

	/**
	 * Constructor por defecto de la capa Layer del protocolo construido
	 * 
	 * @param prefix
	 */
	public Layer(String prefix) {
		/**
		 * InicializaciÃ³n del Nodo
		 */
		Layer.prefix = prefix;
		transportId = Configuration.getPid(prefix + "." + PAR_TRANSPORT);
		/**
		 * Siguiente capa del protocolo
		 */
		layerId = transportId + 1;
	}

	private Node searchNode(int id) {
		return Network.get(id);
	}

	/**
	 * Definir Clone() para la replicacion de protocolo en nodos
	 */
	public Object clone() {
		Layer dolly = new Layer(Layer.prefix);
		return dolly;
	}
}
