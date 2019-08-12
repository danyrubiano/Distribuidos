package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;
import peersim.dynamics.WireKOut;
import peersim.edsim.EDProtocol;
import peersim.transport.Transport;

// el Layer es utilizado por todos los nodos
/* Lo primero que se recibe es un mensaje del nodo
 * Cada nodo utiliza el layer cada vez que recibe un mensaje
 * Hay un nodo receptor, y los nodos no receptores
 * Si es un nodo no receptor lee el mensaje, ve la consulta, luego verifica si en su cache 
 * si la consulta no esta en su cache, mira el DHT y su vecino, para calcular el nodo mas cercano al receptor.
 * Si es receptor envia la respuesta a la consulta al nodo emisor
 */

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
		
		int consulta = ((Message) message).getConsulta();
		int destino = ((Message) message).getDestino();
		int sigNode, idaVuelta;
		int temp;
		

		// Verificacion para ver si se esta de ida o de vuelta para devolver el mensaje
		if((int) node.getID() == destino){
			message.setIdaVuelta(1); // Ahora hay que ir de vuelta
		}
		
		if(message.getIdaVuelta()==0){
		    // Guardar el nodo en el camino realizado
			((Message)message).getCamino().add((int) node.getID());
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
		
		int consulta = ((Message) mensaje).getConsulta();
		int emisor = ((Message) mensaje).getEmisor();
		int destino = ((Message) mensaje).getDestino();
		int idaVuelta = ((Message) mensaje).getIdaVuelta();
		int sigNode = -1;
		int temp = -1;
		int temp2 = -1;
		
		Node sendNode = null;
		
		
		    // Camino de ida hasta llegar al destino 
		    if(idaVuelta==0){
		    	// se verifica si la consulta esta en el cache del nodo actual
		    	if(((NetworkNode)Network.get((int) node.getID())).buscarEnCache((int) node.getID(), destino, consulta)==true){
		    		System.out.println("\t["+emisor+","+destino+","+consulta+"]\tNodo "+node.getID()+ " tiene consulta "+consulta+" en cache");
		    		// se setea ahora que ahora se tiene que ir de vuelta
		    		mensaje.setIdaVuelta(1);
		    		// se obtien la cache para verificar las consultas
		    		int[] resp = ((NetworkNode) Network.get((int) node.getID())).getRespuestaCache((int) node.getID(), destino, consulta);
		    		if(resp[0]==destino && resp[1]==consulta){
		    			if(resp[2]==0){ // quiere decir que el nodo no tiene respuesta a esa consulta
		    				mensaje.setRespuesta(-1); // se setea como si no hubiera respuesta
		    				System.out.println("\t["+emisor+","+destino+","+consulta+"]\tNodo "+node.getID()+" responde que el nodo "+destino+" no tiene resultado a la consulta "+resp[1]+" según su cache");
		    			}
		    			else if(resp[2]==1){ // quiere decir que el nodo si tiene respuesta a la consulta
		    				mensaje.setRespuesta(resp[0]); // se setea la respuesta entregada
		    				System.out.println("\t["+emisor+","+destino+","+consulta+"]\tNodo "+node.getID()+" responde que el nodo "+destino+" tiene como resultado "+resp[1]+" a la consulta según su cache");
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
		    		    System.out.println("\t["+emisor+","+destino+","+consulta+"]\tNodo "+node.getID()+" envia respuesta a nodo "+ ((NetworkNode) sendNode).getID());
		    		}
		    	}
		    	// en el caso de que no este en cache, sigo el camino hasta el siguiente nodo
			    else if(((NetworkNode)Network.get((int) node.getID())).buscarEnCache((int) node.getID(), destino, consulta)==false){
			    	String aux = new String("");
			    	System.out.println("\t["+emisor+","+destino+","+consulta+"]\tNodo "+node.getID()+ " no tiene consulta "+consulta+" en cache");
				
			    	// se ven los posibles nodos con el DHT y el vecino
			    	aux+="\t["+emisor+","+destino+","+consulta+"]\tNodo "+node.getID()+" calcula distancia más cercana al nodo "+destino+" con "+node.getVecino();
			    	for(int j=0; j<node.getDHT().size(); j++){
					aux+=", "+node.getDHT().get(j);
		    		}
		    		System.out.println(aux);
		    		// se setea como nodo siguiente al vecino mas cercano con respecto a la distancia con el nodo de destino
			    	sigNode = ((NetworkNode) Network.get((int) node.getID())).getVecinoMasCercano((int) node.getID(), destino);
			    	sendNode = Network.get(sigNode);
			    	
			    	//Envío del dato a través de la capa de transporte, la cual enviará según el ID del emisor y el receptor
			    	((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
			    	System.out.println("\t["+emisor+","+destino+","+consulta+"]\tNodo "+node.getID()+" envia consulta a nodo "+ ((NetworkNode) sendNode).getID());

    			}
    		}
    		
	    	// Camino de vuelta para llegar hasta el emisor
	    	else if(idaVuelta == 1){
	    		// verifico si el nodo actual efectivamente es igual al nodo de destino
		    	if((int) (node.getID())==destino){
		    		temp2 = ((NetworkNode) Network.get(destino)).buscarConsultaEnBD(destino, consulta);
		    		if(temp2==-1) // quiere decir que no hay respuesta para la consulta
		    			System.out.println("\t["+emisor+","+destino+","+consulta+"]\tNodo "+node.getID()+" responde que no tiene resultado a la consulta "+consulta);
		    		else // si hay respuesta a la consulta
		    			System.out.println("\t["+emisor+","+destino+","+consulta+"]\tNodo "+node.getID()+" responde que el resultado a la consulta es "+temp2);
		    		mensaje.setRespuesta(temp2);
	    		}
	    		
		    	if(mensaje.getContador()>0){ // se verifica si aun hay camino por recorrer
	    		    // seteo como siguiente nodo el ultimo elemento de la lista del camino
	    		    sigNode = mensaje.getCamino().get(mensaje.getContador()-1);
	    		    //System.out.println(""+sigNode+":"+(mensaje.getContador()-1));
	    		    sendNode = Network.get(sigNode);
	    		    
	    		 // se resta 1 al contador porque esta en el camino de vuelta
	    		    mensaje.setContador(mensaje.getContador()-1);
	    		    
	    		    if((int) node.getID()!=destino){
	    		    	// Actualizacion de la cache
	    		    	if(mensaje.getRespuesta()==-1) // verifica que no tiene respuesta a la consulta para guardar en cache el resultado
	    		    		((NetworkNode) Network.get((int) node.getID())).actualizarCache((int) node.getID(), destino, consulta,0);
	    		    	else // verifica que si hay respuesta a la consulta para guardar en cache el resultado
	    		    		((NetworkNode) Network.get((int) node.getID())).actualizarCache((int) node.getID(), destino, consulta,1);
	    		        //System.out.println(node.getID()+" "+((NetworkNode) Network.get((int) node.getID())).imprimirCache((int) node.getID()));
	    		    }
			
	    		    //Envío del dato a través de la capa de transporte, la cual enviará según el ID del emisor y el receptor
	    		    ((Transport) currentNode.getProtocol(transportId)).send(currentNode, sendNode, message, layerId);
	    		    System.out.println("\t["+emisor+","+destino+","+consulta+"]\tNodo "+node.getID()+" envia respuesta a nodo "+ ((NetworkNode) sendNode).getID());
	    		}
		    	
		    	if((int) node.getID()==emisor){ // en este punto ya se regreso la respuesta de la consulta al nodo que la emitio
		    		// Actualizacion de la cache
		    		if(mensaje.getRespuesta()==-1)
    		    		((NetworkNode) Network.get((int) node.getID())).actualizarCache((int) node.getID(), destino, consulta,0);
    		    	else
    		    		((NetworkNode) Network.get((int) node.getID())).actualizarCache((int) node.getID(), destino, consulta,1);
    		        //System.out.println(node.getID()+" "+((NetworkNode) Network.get((int) node.getID())).imprimirCache((int) node.getID()));
		    		System.out.println("\t["+emisor+","+destino+","+consulta+"]\tProceso terminado para esta consulta.\n");
		    		/*
		    		for (int i = 0; i < Network.size(); i++) {
		    			System.out.println("\tNodeID: "+((NetworkNode) Network.get(i)).getID()+" \tVecino: "+ ((NetworkNode) Network.get(i)).getVecino() + "\tDHT: "+ ((NetworkNode) Network.get(i)).getDHT()+"  \tBD: "+ ((NetworkNode) Network.get(i)).getBD()+"  \tCache: "+ ((NetworkNode) Network.get(i)).imprimirCache(i));
		    		}
		    		System.out.print("\n");
		    		*/
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
