package cl.usach.sd;

import peersim.core.GeneralNode;
import peersim.core.Linkable;
import peersim.core.Network;

import java.util.ArrayList;

public class NetworkNode extends GeneralNode {
	/*Creamos un nodo que posee un valor y un tipo*/
	private int value;
	private int type;
	// variable que guarda el vecino proximo de la red
	private int vecino;
	// Matriz que simula el cache
	private int[][] Cache;
	// ArrayList que simula la base de datos
	private ArrayList<Integer> DB;
	// ArrayList que simula el DHT
	private ArrayList<Integer> DHT;
	
	
	public NetworkNode(String prefix) {
		super(prefix);
		this.setValue(0);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getVecino() {
		return vecino;
	}

	public void setVecino(int vecino) {
		this.vecino = vecino;
	}
	
	public int[][] getCache() {
		return Cache;
	}

	public void setCache(int[][] Cache) {
		this.Cache = Cache;
	}

	public ArrayList<Integer> getBD() {
		return DB;
	}

	public void setDB(ArrayList<Integer> DB) {
		this.DB = DB;
	}

	public ArrayList<Integer> getDHT() {
		return DHT;
	}

	public void setDHT(ArrayList<Integer> DHT) {
		this.DHT = DHT;
	}
	
	
	// Funciones
	
	/* Función para imprimir la matriz del cache
	 */
	public String imprimirCache(int idNode){
		int[][] cache = ((NetworkNode) Network.get(idNode)).getCache();
		int size_cache = cache.length;
		String aux = new String("");
		// recorre cada fila que indica una posicion de la cache e imprime destino consulta y si es que hay respuesta
		for(int i=0; i<size_cache; i++){
			aux+="["+cache[i][0]+","+cache[i][1]+","+cache[i][2]+"]";
		}
		return aux;
	}
	
	/* Funcion que genera la base de datos de cada nodo.
	 * Cada Nodo tiene x elementos y se genera en base a su id 
	 * Parametros: - idNodo en el cual generar la BD
	 *             - size_bd tamaño de la base de datos
	 */
	public void generateBD(int idNode, int size_bd){
		int i;
		// el primer elemento se fija como el id del nodo por el tamaño de su base de datos
		// luego se adiciona en 1, para los otros elementos hasta que se llena la base de datos del nodo
		for(i=idNode*size_bd; i<idNode*size_bd+size_bd; i++){
			((NetworkNode) Network.get(idNode)).getBD().add(i);
		}
	}
	
	/* Funcion que calcule los elementos del dht en base a 
	 * que la distancia entre los nodos esta dada por n/2^x
	 * Parametros: - idNodo en el cual generar la DHT
	 */
	public void generateDHTs(int idNode, int d){
		int i, x, distancia;
		// Calculo de cuantos elementos tiene el DHT
		x = (int) (Math.floor(Math.log(Network.size())/Math.log(2)));
		
		for(i=x-1; i>=0; i--){
			// Calculo de la distancia que debe tener el nodo con los otros nodos del DHT
			distancia = (int) (Math.floor(Network.size()/Math.pow(2,i)));
			if(((NetworkNode) Network.get(idNode)).getDHT().size() <= 1+2*d){ // se verifica que no se escede el maximo de elementos que debe tener el DHT
			    if(idNode+distancia < Network.size())
				    ((NetworkNode) Network.get(idNode)).getDHT().add(idNode+distancia);
			    else if(idNode+distancia >= Network.size()){// esto para cumplir con la red estructurada circular 
			    	if(idNode+distancia-Network.size() != idNode) // se verifica que el nodo calculado no sea igual al id del nodo al que se le genera el DHT
			    		((NetworkNode) Network.get(idNode)).getDHT().add(idNode+distancia-Network.size());
			    }
			}
		}
	}
	
	/*¨Funcion que obtiene el vecino mas cercano al nodo
	 * segun la busqueda que esta haciendo.
	 * Parametros: - idNode que no es receptor
	 *             - idNode receptor
	 */
	public int getVecinoMasCercano(int idNode, int idNodeReceptor){
		int i, vecino, aux, aux1;
		vecino = -1;
		
		// Caso en en que el nodo de destino es mayor que el emisor
		if(idNode<idNodeReceptor){
			aux1 = ((NetworkNode) Network.get(idNode)).getVecino();
			aux = Math.abs(idNodeReceptor-((NetworkNode) Network.get(idNode)).getVecino());
			//System.out.println(""+idNodeReceptor+":"+aux);
			
		    // Verifica si el nodo vecino en la red, es el vecino mas cercano a nodo receptor
	        if(aux==1){
	    	   aux = Math.abs(idNodeReceptor-((NetworkNode) Network.get(idNode)).getVecino());
		       aux1 = ((NetworkNode) Network.get(idNode)).getVecino();
	        }
	        else{
	            // Verifica cual es el vecino mas cercano en base a los nodos del DHT
	            for(i=0; i<((NetworkNode) Network.get(idNode)).getDHT().size(); i++){
		            if(Math.abs(idNodeReceptor-((NetworkNode) Network.get(idNode)).getDHT().get(i))< aux && ((NetworkNode) Network.get(idNode)).getDHT().get(i) <= idNodeReceptor){
		    	        aux = Math.abs(idNodeReceptor-((NetworkNode) Network.get(idNode)).getDHT().get(i));
			            aux1 = ((NetworkNode) Network.get(idNode)).getDHT().get(i);
		                //System.out.println(aux1+":"+Math.abs(idNodeReceptor-((NetworkNode) Network.get(idNode)).getDHT().get(i))+" ");
		            }
	            }
	        }
	        // asigna el vecino más cercano
		    vecino = aux1;
		    //System.out.println("."+vecino);
		}
		// caso en que el nodo de destino es menor que el emisor
		else if(idNode>idNodeReceptor){
			aux1 = ((NetworkNode) Network.get(idNode)).getVecino();
			vecino = -1;
			aux = ((NetworkNode) Network.get(idNode)).getVecino();
			//System.out.println(""+idNodeReceptor+":"+aux);
			
	        // Verifica cual es el vecino mas cercano en base a los nodos del DHT
            for(i=0; i<((NetworkNode) Network.get(idNode)).getDHT().size(); i++){
	            if(Math.abs(idNodeReceptor-((NetworkNode) Network.get(idNode)).getDHT().get(i))< aux && ((NetworkNode) Network.get(idNode)).getDHT().get(i) <= idNodeReceptor){
	    	        aux = Math.abs(idNodeReceptor-((NetworkNode) Network.get(idNode)).getDHT().get(i));
		            aux1 = ((NetworkNode) Network.get(idNode)).getDHT().get(i);
	                //System.out.println(aux1+":"+Math.abs(idNodeReceptor-((NetworkNode) Network.get(idNode)).getDHT().get(i))+" ");
	            }
            }
            
		    vecino = aux1;
		    //System.out.println("."+vecino);
		}
		
		// Caso en el cual ya se encuentra en el nodo de destinoi
		else if(idNode == idNodeReceptor){
			vecino = idNodeReceptor;
			//System.out.println(";"+vecino);
		}
		return vecino;
	}
	
	/* Función que dado el id del nodo le asigna como
	 * vecino el siguiente nodo (id+1) de manera que se forme la 
	 * red estructurada.
	 * Parametros: - idNodo al cual asignarle el vecino
	 */
	public void asignarVecino(int idNode){
		
		if(idNode+1<Network.size()){
			//((NetworkNode) Network.get(i)).setVecino(i+1);
			// Creacion de la red estructurada
		    ((Linkable) Network.get(idNode).getProtocol(0)).addNeighbor((NetworkNode) Network.get(idNode+1));
		}
		else if(idNode+1==Network.size()){
			// Se setea el vecino como el nodo inicial, de manera de tener la red estructurada circular
			((Linkable) Network.get(idNode).getProtocol(0)).addNeighbor((NetworkNode) Network.get(0));
		}
		// Setea la variable vecino del NetworkNode, solo para acceder un poco mas facil sin la necesidad de linkable
		((NetworkNode) Network.get(idNode)).setVecino((int)((Linkable) Network.get(idNode).getProtocol(0)).getNeighbor(0).getID());
	}
	
	/* Funcion que dado la consulta, actualiza la cache de 
	 * los nodos por donde pasa la consulta
	 * Se utiliza la politica de reemplazo FIFO
	 * Parametros: - idNodo en el que se debe actualizar
	 *             - idNodo donde esta la consulta
	 *             - Resultado de la consulta
	 *             - size_cache tamaño del cache 
	 */
	public void actualizarCache(int idNode, int idNodeConsulta, int consulta, int hayRespuesta){
		
		int[][] cache = ((NetworkNode) Network.get(idNode)).getCache();
		int[][] cache_aux = cache;
		int size_cache = cache.length;
		
		// se elimina el primero en entrar por la politica de reemplazo FIFO
		for(int i=0; i<size_cache; i++){
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
		((NetworkNode) Network.get(idNode)).setCache(cache_aux);
	}
	
	
	/* Funcion que busca la consulta en cache
	 * Parametros: - idNode en donde se debe buscar
	 *             - consulta 
	 *             - id Nodo donde esta la consulta
	 */
	public boolean buscarEnCache(int idNode, int idNodeConsulta, int consulta){
		
		int [][] cache = ((NetworkNode) Network.get(idNode)).getCache();
		for(int i=0; i<cache.length; i++){
			if(cache[i][0]==idNodeConsulta && cache[i][1]==consulta)
				return true;
		}
		return false;
	}
	
	/* Funcion que retorna la respuesta a la consulta si esta en cache
	 * Parametros: - idNode en donde se debe buscar
	 *             - consulta 
	 *             - id Nodo donde esta la consulta
	 */
	public int[] getRespuestaCache(int idNode, int idNodeConsulta, int consulta){
		
		// variable auxiliar para el retorno
		int[] aux= {-1,-1,-1};
		int [][] cache = ((NetworkNode) Network.get(idNode)).getCache();
		for(int i=0; i<cache.length; i++){
			// se verifica si esta la consulta y se guarda en el arreglo de retorno
			if(cache[i][0]==idNodeConsulta && cache[i][1]==consulta){
				aux[0] = cache[i][0];
				aux[1] = cache[i][1];
				aux[2] = cache[i][2];
			}
		}
		return aux;
	}
	
	/* Funcion que busca la consulta en cache y retorna la respuesta
	 * Parametros: - idNode en donde se debe buscar
	 *             - consulta
	 */
	public int buscarConsultaEnBD(int idNode, int consulta){
		int aux = -1;
		// se verifica si esta la consulta y se retorna
		if(((NetworkNode) Network.get(idNode)).getBD().contains((Integer) consulta)==true){
			aux = ((NetworkNode) Network.get(idNode)).getBD().indexOf((Integer) consulta);
		    return ((NetworkNode) Network.get(idNode)).getBD().get(aux);
		}
		// si no retorna -1 : false
		else
			return -1;
	}
}
