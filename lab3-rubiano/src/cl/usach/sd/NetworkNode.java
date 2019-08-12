package cl.usach.sd;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import peersim.core.CommonState;
import peersim.core.GeneralNode;
import peersim.core.Linkable;
import peersim.core.Network;

public class NetworkNode extends GeneralNode {
	/*Creamos un nodo que posee un valor y un tipo*/
	private int valor; // corresponde a un valor del nodo
	private int tipo;  // si es 1: corresponde a un super peer
	                   // si es 0: corresponde a un peer normal
	// Asignación de ip
	private int IP;
	// Asignación de puerto
	private int puerto;
	// Id para generarlo con el hash
	private String idHash;
	// variable para guardar el nombre de la subred
	private int nameSubred;
	// variable que guarda el tamaño de la subred
	private int tamSubred; 
	// ArrayList que guarda la subred 
	private ArrayList<Integer> Subred;
	// ArrayList que guarda los vecinos de cada nodo
	private ArrayList<Integer> Vecinos;
	// Matriz que simula el cache de los super Peer
	private int[][] Cache;
	// ArrayList que simula el DHT
	private ArrayList<Integer> DHT;
	// Guarda el k de random walks asignado a la red
	private int k;
	// Guarda el tiepo de vida del proceso
	private int ttl;
	// Guarda la cantida de super peers que hay en la red
	private int cantSuperPeers;

	public NetworkNode(String prefix) {
		super(prefix);
		this.setValor(0);
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	public int getIP() {
		return IP;
	}

	public void setIP(int IP) {
		this.IP = IP;
	}
	
	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}
	
	public String getIdHash() {
		return idHash;
	}

	public void setIdHash(String idHash) {
		this.idHash = idHash;
	}
	
	public int getNameSubred() {
		return nameSubred;
	}

	public void setNameSubred(int nameSubred) {
		this.nameSubred = nameSubred;
	}
	
	public int getTamSubred() {
		return tamSubred;
	}

	public void setTamSubred(int tamSubred) {
		this.tamSubred = tamSubred;
	}
	
	public ArrayList<Integer> getSubred() {
		return Subred;
	}

	public void setSubred(ArrayList<Integer> Subred) {
		this.Subred = Subred;
	}
	
	public ArrayList<Integer> getVecinos() {
		return Vecinos;
	}

	public void setVecinos(ArrayList<Integer> Vecinos) {
		this.Vecinos = Vecinos;
	}
	
	public int[][] getCache() {
		return Cache;
	}

	public void setCache(int[][] Cache) {
		this.Cache = Cache;
	}
	
	public ArrayList<Integer> getDHT() {
		return DHT;
	}

	public void setDHT(ArrayList<Integer> DHT) {
		this.DHT = DHT;
	}
	
	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public int getTtl() {
		return ttl;
	}

	public void setTtl(int ttl) {
		this.ttl = ttl;
	}
	
	public int getCantSuperPeers() {
		return cantSuperPeers;
	}

	public void setCantSuperPeers(int cantSuperPeers) {
		this.cantSuperPeers = cantSuperPeers;
	}
	
	
	/* Funcion que codifica en SHA1 un string determinado
	 */
	protected String sha1(String input) throws NoSuchAlgorithmException {
	    MessageDigest mDigest = MessageDigest.getInstance("SHA1");
	    byte[] result = mDigest.digest(input.getBytes());
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < result.length; i++) {
	        sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    return sb.toString();
	}
	
	/* Funcion que calcule los elementos del dht en base a 
	 * que la distancia entre los nodos esta dada por 2^x 
	 *  segun el protocolo chord
	 * Parametros: - idNodo en el cual generar la DHT
	 */
	public void generateDHTs(int idNode, int d, int cantSuperPeers){
		int i, x, distancia;
		// Calculo de cuantos elementos tiene el DHT
		x = (int) (Math.floor(Math.log(cantSuperPeers)/Math.log(2)));
		
		for(i=0; i<x; i++){
			// Calculo de la distancia que debe tener el nodo con los otros nodos del DHT
			distancia = (int) (Math.floor(Math.pow(2,i)));
			if(((NetworkNode) Network.get(idNode)).getDHT().size() <= 1+2*d){ // se verifica que no se escede el maximo de elementos que debe tener el DHT
			    if(idNode+distancia < cantSuperPeers)
				    ((NetworkNode) Network.get(idNode)).getDHT().add(idNode+distancia);
			    else if(idNode+distancia >= cantSuperPeers){// esto para cumplir con la red estructurada circular 
			    	if(idNode+distancia-cantSuperPeers != idNode) // se verifica que el nodo calculado no sea igual al id del nodo al que se le genera el DHT
			    		((NetworkNode) Network.get(idNode)).getDHT().add(idNode+distancia-cantSuperPeers);
			    }
			}
		}
	}
	
	/*¨Funcion que obtiene el vecino mas cercano al nodo
	 * segun la busqueda que esta haciendo.
	 * Parametros: - ipNode que no es receptor
	 *             - ipNode receptor
	 */
	public int getVecinoMasCercano(int ipPeer, int ipPeerReceptor){
		int i, vecino, aux, aux1;
		vecino = -1;
		
		// Caso en en que el nodo de destino es mayor que el emisor
		if(ipPeer<ipPeerReceptor){
			aux1 = ((NetworkNode) Network.get(ipPeer)).getDHT().get(0);
			aux = Math.abs(ipPeerReceptor-((NetworkNode) Network.get(ipPeer)).getDHT().get(0));
			//System.out.println(""+idNodeReceptor+":"+aux);
			
	        // Verifica cual es el vecino mas cercano en base a los nodos del DHT
	        for(i=0; i<((NetworkNode) Network.get(ipPeer)).getDHT().size(); i++){
		        if(Math.abs(ipPeerReceptor-((NetworkNode) Network.get(ipPeer)).getDHT().get(i)) <= aux && ((NetworkNode) Network.get(ipPeer)).getDHT().get(i) <= ipPeerReceptor){
		           aux = Math.abs(ipPeerReceptor-((NetworkNode) Network.get(ipPeer)).getDHT().get(i));
	               aux1 = ((NetworkNode) Network.get(ipPeer)).getDHT().get(i);
		           //System.out.println(aux1+":"+Math.abs(idNodeReceptor-((NetworkNode) Network.get(idNode)).getDHT().get(i))+" ");
		        }
		        if(ipPeerReceptor ==((NetworkNode) Network.get(ipPeer)).getDHT().get(i))
	            	aux1 = ((NetworkNode) Network.get(ipPeer)).getDHT().get(i);
	        }
	        // asigna el vecino más cercano
		    vecino = aux1;
		    //System.out.println("."+vecino);
		}
		// caso en que el nodo de destino es menor que el emisor
		else if(ipPeer>ipPeerReceptor){
			aux1 = ((NetworkNode) Network.get(ipPeer)).getDHT().get(0);
			vecino = -1;
			aux = ((NetworkNode) Network.get(ipPeer)).getDHT().get(0);
			//System.out.println(""+idNodeReceptor+":"+aux);
			
	        // Verifica cual es el vecino mas cercano en base a los nodos del DHT
            for(i=0; i<((NetworkNode) Network.get(ipPeer)).getDHT().size(); i++){
	            if(Math.abs(ipPeerReceptor-((NetworkNode) Network.get(ipPeer)).getDHT().get(i))< aux && ((NetworkNode) Network.get(ipPeer)).getDHT().get(i) <= ipPeerReceptor){
	    	        aux = Math.abs(ipPeerReceptor-((NetworkNode) Network.get(ipPeer)).getDHT().get(i));
		            aux1 = ((NetworkNode) Network.get(ipPeer)).getDHT().get(i);
	                //System.out.println(aux1+":"+Math.abs(idNodeReceptor-((NetworkNode) Network.get(idNode)).getDHT().get(i))+" ");
	            }
	            if(ipPeerReceptor ==((NetworkNode) Network.get(ipPeer)).getDHT().get(i))
	            	aux1 = ((NetworkNode) Network.get(ipPeer)).getDHT().get(i);
            }
            
		    vecino = aux1;
		    //System.out.println("."+vecino);
		}
		
		// Caso en el cual ya se encuentra en el nodo de destinoi
		else if(ipPeer == ipPeerReceptor){
			vecino = ipPeerReceptor;
			//System.out.println(";"+vecino);
		}
		return vecino;
	}
	
	/* Función para imprimir la matriz del cache
	 */
	public String imprimirCache(int idNode){
		int[][] cache = ((NetworkNode) Network.get(idNode)).getCache();
		int size_cache = cache.length;
		String aux = new String("");
		// recorre cada fila que indica una posicion de la cache e imprime destino, consulta, si es que hay respuesta y si fue recientemente usado
		for(int i=0; i<size_cache; i++){
			aux+="["+cache[i][0]+","+cache[i][1]+","+cache[i][2]+","+cache[i][3]+"]";
		}
		return aux;
	}
	
	/* Funcion que busca la consulta en cache
	 * Parametros: - IP del nodo actual 
	 *             - IP super peer donde esta la consulta
	 *             - consulta
	 */
	public boolean buscarEnCache(int idNode, int ipSpConsulta, int consulta){
		
		int [][] cache = ((NetworkNode) Network.get(idNode)).getCache();
		for(int i=0; i<cache.length; i++){
			if(cache[i][0]==ipSpConsulta && cache[i][1]==consulta)
				return true;
		}
		return false;
	}
	
	/* Funcion que retorna la respuesta a la consulta si esta en cache e indica el ultimo elemento usado
	 * para poder utilizar la politica de LRU
	 * Parametros: - ipNode actual
	 *             - IP super peer donde esta la consulta
	 *             - consulta
	 */
	public int[] getRespuestaCache(int idNode, int ipSpConsulta, int consulta){
		
		// variable auxiliar para el retorno
		int[] aux= {-1,-1,-1};
		int [][] cache = ((NetworkNode) Network.get(idNode)).getCache();
		for(int i=0; i<cache.length; i++){
			// se verifica si esta la consulta y se guarda en el arreglo de retorno
			if(cache[i][0]==ipSpConsulta && cache[i][1]==consulta){
				aux[0] = cache[i][0];
				aux[1] = cache[i][1];
				aux[2] = cache[i][2];
				cache[i][3] = 1; // para indicar que fue recientemente usado para utilizar la politica de LRU
			}
		}
		((NetworkNode) Network.get(idNode)).setCache(cache);
		return aux;
	}
	
	/* Funcion que dado la consulta, actualiza la cache de los nodos por donde pasa la consulta
	 * y la de sus vecinos segun el DHT
	 * Se utiliza la politica de reemplazo LRU
	 * Parametros: - idNodo en el que se debe actualizar
	 *             - idNodo donde esta la consulta
	 *             - Resultado de la consulta
	 *             - size_cache tamaño del cache 
	 */
	public void actualizarCache(int idNode, int idNodeConsulta, int consulta, int hayRespuesta){
		
		int[][] cache;
		int[][] cache_aux;
		int size_cache;
		int i, j, nodeAux;
		int esta = -1;
		int temp = -1;
		int aux = -1;
		int temp2 = -1;
		ArrayList<Integer> Conocidos = new ArrayList<Integer>();
		Conocidos.add((Integer)idNode);
		
		// se hace una lista de los conocidos que son los nodos del dht icluyendo el mismo nodo
		for(i=0; i<((NetworkNode) Network.get(idNode)).getDHT().size(); i++){
			Conocidos.add(((NetworkNode) Network.get(idNode)).getDHT().get(i));
		}
		// Para cada uno de los conocidos se realiza una actualizacion del cache
		for(j=0; j<Conocidos.size(); j++){
			nodeAux = Conocidos.get(j);
			cache = ((NetworkNode) Network.get(nodeAux)).getCache();
			size_cache = cache.length;
			cache_aux = cache;
			
			for(i=0; i<size_cache; i++){
				if(cache[i][0]==idNodeConsulta && cache[i][1]==consulta)
					esta = 1;
			}
			if(esta == -1){
        		for(i=0; i<size_cache; i++){
        			if(cache[i][0]==-1 && cache[i][1]==-1){ // posicion no ocupada de la cache
        				aux = i;
        				temp = 1;
        			}
        		}		
    	    	if(temp == 1){ // aun existen posiciones sin ocupar en la cache
    	    		cache[aux][0] = idNodeConsulta;
    	    		cache[aux][1] = consulta;
    	    		cache[aux][2] = hayRespuesta;
    	    		cache[aux][3] = -1;
    	    	}		
    	    	else if(temp == -1){ // no hay espacio en la cache, entonces se utiliza la politica de reemplazo LRU
    	    		i = 0;
    	    		aux = -1;
    	    		for(i=0; i<size_cache; i++){
    	    			if(cache[i][3]==1){ // ultima posición ocupada
    	    				aux = i;
    					temp2 = 1;
    	    			}
    	    		}
    	    		if(temp2 == 1){ // se efectua la politica de reemplazo
    	        		cache[aux][0] = idNodeConsulta;
    	        		cache[aux][1] = consulta;
    	    			cache[aux][2] = hayRespuesta;
    	    			cache[aux][3] = -1;
    	    		}
    	    		else if(temp2 == -1){ // si no se ha utilizado ningun elemento de la cache, se procede a utilizar FIFO
    	    			// se elimina el primero en entrar por la politica de reemplazo FIFO
    	    			for(i=0; i<size_cache; i++){
    	    				if(i+1<size_cache){
    	    					cache_aux[i][0] = cache[i+1][0];
    	    					cache_aux[i][1] = cache[i+1][1];
    	    					cache_aux[i][2] = cache[i+1][2];
    	    				}
    	    			}
    	    			// se setea la consulta y el nodo que tiene la consulta en la ultima fila de la matriz
    	    			cache_aux[size_cache-1][0] = idNodeConsulta;
    	    			cache_aux[size_cache-1][1] = consulta;
    	    			cache_aux[size_cache-1][2] = hayRespuesta;
    	    			
    	    			// se setea la cache con la cache auxiliar generada segun la politica
    	    			cache = cache_aux;
    	    		}
	        	}
		        // se setea la cache con la cache auxiliar generada segun la politica
        		((NetworkNode) Network.get(nodeAux)).setCache(cache);
        	}
    	}
	}
	
	/* Funcion que inicializa los valores de un peer normal que se añade a la red
	 */
	public NetworkNode inicializacionNode(){
		NetworkNode nodo = null;
		nodo = new NetworkNode("init.1statebuilder");
		Network.add(nodo);
		int idNodeAux = (int) nodo.getID();
		((NetworkNode) Network.get(idNodeAux)).setTipo(0); // Asignacion de Tipo
		((NetworkNode) Network.get(idNodeAux)).setValor(idNodeAux); // Asignacion de un valor
		((NetworkNode) Network.get(idNodeAux)).setIP(idNodeAux); // Asignacion de IP
		int puerto = (int)(Math.random()*(4000-3000))+3000;
		((NetworkNode) Network.get(idNodeAux)).setPuerto(puerto); // Asignación de puerto
		ArrayList<Integer> Vecinos = new ArrayList<Integer>();
		((NetworkNode) Network.get(idNodeAux)).setVecinos(Vecinos);
		// Asignacion de parametros de la red que cada nodo debe conocer
		((NetworkNode) Network.get(idNodeAux)).setCantSuperPeers((int) ((NetworkNode) Network.get(0)).getCantSuperPeers());
		((NetworkNode) Network.get(idNodeAux)).setK((int) ((NetworkNode) Network.get(0)).getK());
		((NetworkNode) Network.get(idNodeAux)).setTtl((int) ((NetworkNode) Network.get(0)).getTtl());
		
		return nodo;
	}
	
	/* Funcion que asigna peers normales a la red de un super peer
	 * Parametros: - ip del Super Peer
	 *             - n que corresponde al valor minimo de peers que debe tener la subred
	 *             - m que corresponde al valor maximo de peers que debe tener la subred
	 */
	public void asignarNodosASuperPeer(int idNode, int n, int m){
		int cantNodos = (int)(Math.random()*(m-n))+n;
		int i,j, idNodeAux, cantVecinos, vecinoAux, anexarASuperPeer, asignarVecino, aux;
		NetworkNode nodo;
		ArrayList <Integer> Subred = new ArrayList<Integer>();
		// Asignacion del nombre de la subred
		((NetworkNode) Network.get(idNode)).setNameSubred(idNode);
		// Asignacion del tamaño de la subred
		((NetworkNode) Network.get(idNode)).setTamSubred(cantNodos);
		
		// Creación de todos los nodos de la subred
		for(i=0; i<cantNodos; i++){
			nodo = inicializacionNode();
			idNodeAux = (int) nodo.getID();
			Subred.add(idNodeAux); // Asignacion de los nodos de la subred del super peer
		}
		((NetworkNode) Network.get(idNode)).setSubred(Subred);
		
		
		// Asignacion de por lo menos un peer como vecino del super peer
		aux = CommonState.r.nextInt(Subred.size()); // random sobre los peers de la subred
		idNodeAux = ((NetworkNode) Network.get(idNode)).getSubred().get((int) aux); // obtencion del peer
		((Linkable) Network.get(idNode).getProtocol(0)).addNeighbor((NetworkNode) Network.get(idNodeAux)); // asignacion del peer como vecino del super peer
		((NetworkNode) Network.get(idNode)).getVecinos().add(idNodeAux); // se agrega a la lista de los vecinos del super peer
		((Linkable) Network.get(idNodeAux).getProtocol(0)).addNeighbor((NetworkNode) Network.get(idNode)); // asignacion del super peer como vecino del peer
		((NetworkNode) Network.get(idNodeAux)).getVecinos().add(idNode); // se agrega a la lista de los vecinos del peer
		
		for(i=0; i<cantNodos; i++){
			idNodeAux = ((NetworkNode) Network.get(idNode)).getSubred().get((int) i); 
			anexarASuperPeer = CommonState.r.nextInt(2); // random para ver si se asigna al super peer o no, si es 0 no se asigna, si es 1 si
			if(anexarASuperPeer==1 && ((NetworkNode) Network.get(idNodeAux)).getVecinos().contains((Integer) idNode)==false){ // verificacion de que ya no este añadido como vecino
				((Linkable) Network.get(idNode).getProtocol(0)).addNeighbor((NetworkNode) Network.get(idNodeAux)); // asignacion del peer como vecino del super peer
				((NetworkNode) Network.get(idNode)).getVecinos().add(idNodeAux); // se agrega a la lista de los vecinos del super peer
				((Linkable) Network.get(idNodeAux).getProtocol(0)).addNeighbor((NetworkNode) Network.get(idNode)); // asignacion del super peer como vecino del peer
				((NetworkNode) Network.get(idNodeAux)).getVecinos().add(idNode); // se agrega a la lista de los vecinos del peer
			}
			cantVecinos = CommonState.r.nextInt(cantNodos);
			
			// Asignación de por lo menos un vecino a cada peer
			ArrayList<Integer> Vecinos = ((NetworkNode) Network.get(idNode)).getSubred();
			aux = CommonState.r.nextInt(Vecinos.size()); // random para elegir como vecino a un nodo de la subred
			vecinoAux = Vecinos.get((int)aux); // obtencion del vecino aleatorio
			if(((NetworkNode) Network.get(idNodeAux)).getVecinos().contains((Integer) vecinoAux)==false && vecinoAux!=idNodeAux){ // verificacion de que ya no este añadido como vecino
				((Linkable) Network.get(idNodeAux).getProtocol(0)).addNeighbor((NetworkNode) Network.get(vecinoAux)); // asignacion del vecino al peer
				((NetworkNode) Network.get(idNodeAux)).getVecinos().add(vecinoAux); // se agrega a la lista de los vecinos del peer
				((Linkable) Network.get(vecinoAux).getProtocol(0)).addNeighbor((NetworkNode) Network.get(idNodeAux)); // asignacion del peer como vecino del otro peer
				((NetworkNode) Network.get(vecinoAux)).getVecinos().add(idNodeAux); // se agrega el peer como vecino del otro peer
			}
			
			for(j=0; j<cantVecinos; j++){
				vecinoAux = Vecinos.get((int)j);
				asignarVecino = CommonState.r.nextInt(2); // random para ver si se asigna como vecino al peer, 1 si, 0 no
				if(((NetworkNode) Network.get(idNodeAux)).getVecinos().contains((Integer) vecinoAux)==false && asignarVecino==1 && vecinoAux!=idNodeAux){
					((Linkable) Network.get(idNodeAux).getProtocol(0)).addNeighbor((NetworkNode) Network.get(vecinoAux)); // asignacion del vecino al peer
					((NetworkNode) Network.get(idNodeAux)).getVecinos().add(vecinoAux); // se agrega a la lista de los vecinos del peer
					((Linkable) Network.get(vecinoAux).getProtocol(0)).addNeighbor((NetworkNode) Network.get(idNodeAux)); // asignacion del peer como vecino del otro peer
					((NetworkNode) Network.get(vecinoAux)).getVecinos().add(idNodeAux); // se agrega el peer como vecino del otro peer
				}
			}
		}
		
		// Verificacion de que si existe algun nodo que no tenga vecinos
		for(i=0; i<cantNodos; i++){
			idNodeAux = ((NetworkNode) Network.get(idNode)).getSubred().get((int) i);
			aux = CommonState.r.nextInt(((NetworkNode) Network.get(idNode)).getSubred().size());
			vecinoAux = ((NetworkNode) Network.get(idNode)).getSubred().get((int) aux);
			if(((NetworkNode) Network.get(idNodeAux)).getVecinos().isEmpty()==true){ // verificacion de que el nodo no tenga vecinos
				((Linkable) Network.get(idNodeAux).getProtocol(0)).addNeighbor((NetworkNode) Network.get(vecinoAux)); // asignacion del peer como vecino del super peer
				((NetworkNode) Network.get(idNodeAux)).getVecinos().add(vecinoAux); // se agrega a la lista de los vecinos del super peer
				((Linkable) Network.get(vecinoAux).getProtocol(0)).addNeighbor((NetworkNode) Network.get(idNodeAux)); // asignacion del super peer como vecino del peer
				((NetworkNode) Network.get(vecinoAux)).getVecinos().add(idNodeAux); // se agrega a la lista de los vecinos del peer
			}
		}
	}
	
	/* Funcion recursiva que simula un random walks para la busqueda del 
	 * super peer con un ttl determinado 
	 * Parametros: - ttl
	 *             - idNode en el cual buscar
	 *             - ArrayList que guarda el camino que sigue el random walks
	 */
	public static int randomWalks1(int ttl, int idNode, ArrayList<Integer> camino){
		if(((NetworkNode) Network.get(idNode)).getTipo()==1 && ttl!=0)
			return idNode;
		else if(ttl == 0)
			return -1;
		else{
			int temp = CommonState.r.nextInt(((NetworkNode) Network.get(idNode)).getVecinos().size());
		    int nodeSig = ((NetworkNode) Network.get(idNode)).getVecinos().get((int) temp);
		    camino.add(nodeSig);
			return randomWalks1(ttl-1,nodeSig, camino);
		}
	}
	
	/* Funcion recursiva que simula un random walks para la busqueda del 
	 * peer que contenga la consulta 
	 * Parametros: - ttl
	 *             - consulta
	 *             - idNode en el cual buscar
	 *             - ArrayList que guarda el camino que sigue el random walks
	 */
	public static int randomWalks2(int ttl, int consulta, int idNode, ArrayList<Integer> camino){
		if(((NetworkNode) Network.get(idNode)).getValor()==consulta && ttl!=0)
			return idNode;
		else if(ttl == 0)
			return -1;
		else{
			int temp = CommonState.r.nextInt(((NetworkNode) Network.get(idNode)).getVecinos().size());
		    int nodeSig = ((NetworkNode) Network.get(idNode)).getVecinos().get((int) temp);
		    camino.add(nodeSig);
			return randomWalks2(ttl-1, consulta, nodeSig, camino);
		}
	}
}
