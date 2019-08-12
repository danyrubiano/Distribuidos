package cl.usach.sd;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

public class Initialization implements Control {
	String prefix;

	int idLayer;
	int idTransport;

	//Valores que sacaremos del archivo de configuración	
	int argExample;
	int initValue;
	int cantSuperPeers;
	int minNodesSuperPeer;
	int maxNodesSuperPeer;
	int vecinos;
	int kwr;
	int ttl;
	int size_cache;
	int size_d;

	public Initialization(String prefix) {
		this.prefix = prefix;
		/**
		 * Para obtener valores que deseamos como argumento del archivo de
		 * configuración, podemos colocar el prefijo de la inicialización en
		 * este caso "init.1statebuilder" y luego la variable de entrada
		 */
		// Configuration.getPid retornar al número de la capa
		// que corresponden esa parte del protocolo
		this.idLayer = Configuration.getPid(prefix + ".protocol");
		this.idTransport = Configuration.getPid(prefix + ".transport");
		// Configuration.getInt retorna el número del argumento
		// que se encuentra en el archivo de configuración.
		// También hay Configuration.getBoolean, .getString, etc...
		
		// en el archivo de configuración init.1statebuilder.argExample 100 y se puede usar ese valor.
		this.argExample = Configuration.getInt(prefix + ".argExample");
		this.initValue = Configuration.getInt(prefix + ".initValue");
		this.cantSuperPeers = Configuration.getInt(prefix + ".s");
		this.minNodesSuperPeer = Configuration.getInt(prefix + ".n");
		this.maxNodesSuperPeer = Configuration.getInt(prefix + ".m");
		this.vecinos = Configuration.getInt(prefix + ".vecinos");
		this.kwr = Configuration.getInt(prefix + ".kwr");
		this.ttl = Configuration.getInt(prefix + ".ttl");
		this.size_cache = Configuration.getInt(prefix + ".cache");
		this.size_d = Configuration.getInt(prefix + ".d");
		
		//System.out.println("Arg: " + argExample);
		//System.out.println("Valor inicial: "+ initValue);
		
		System.out.println("Valores Iniciales:");
		System.out.println("\tN: " + Network.size()+"\t[Tamaño de la Red]");
		System.out.println("\tS: " + cantSuperPeers+"\t[Cantidad de Super Peers]");
		System.out.println("\tn: " + minNodesSuperPeer+"\t[Minimo de Nodos por super peer]");
		System.out.println("\tm: " + maxNodesSuperPeer+"\t[Maximo de Nodos por super peer]");
		System.out.println("\tKWR: " + kwr+"\t[Tamaño del k random walks]");
		System.out.println("\tTTL: " + ttl+"\t[Tiempo de vida]");
		System.out.println("\tvecinos: " + vecinos+"\t[Vecinos]\n");
		System.out.println("\tn: " + size_cache+"\t[Tamaño del Cache]");
	}

	/**
	 * Ejecución de la inicialización en el momento de crear el overlay en el
	 * sistema
	 */
	@Override
	public boolean execute() {
		System.out.println("EJECUTAMOS EL SIMULADOR");
		/**
		 * Para comenzar tomaremos un nodo cualquiera de la red, a través de un random
		 */
		//int nodoInicial = CommonState.r.nextInt(Network.size());
		
		
	
		/**Es conveniente inicializar los nodos, puesto que los nodos 
		 * son una clase clonable y si asignan valores desde el constructor
		 *  todas tomaran los mismos valores, puesto que tomaran la misma dirección
		 * de memoria*/
		
		int puerto = -1;
		String IPMasPuerto = "";
		String idHash = "";
		
		System.out.println("Inicializamos los nodos:");
		for (int i = 0; i < cantSuperPeers; i++) {	
			
			((NetworkNode) Network.get(i)).setTipo(1);
			((NetworkNode) Network.get(i)).setValor((int)((NetworkNode) Network.get(i)).getID()); // se le asigna como el valor del nodo al id, forma simple
			
			// Inicializacion de la cache para cada nodo
			int[][] Cache = new int[size_cache][4];
						
			/* seteo de los valores de cache en -1
			 * Cache[j][0] es para el nodo consultado
		     * Cache[j][1] es para guardar la consulta "respuesta"
		     * Cache[j][4] es para determinar si fue recientemente usado, para la politica de reemplazo LRU
			 * Cache[j][3] es para ver si tiene respuesta a esa consulta
			 *             si se setea con 0, quiere decir que no hay respuesta
			 *             si se setea con 1, quiere decir que si hay respuesta
			 */
			for(int j=0; j<size_cache; j++){
				Cache[j][0]=-1;
				Cache[j][1]=-1;
				Cache[j][2]=-1;
				Cache[j][3]=-1;
			}
			((NetworkNode) Network.get(i)).setCache(Cache);
						
			// Inicializacion del DHT de cada nodo
			ArrayList<Integer> DHT = new ArrayList<Integer>();
			((NetworkNode) Network.get(i)).setDHT(DHT);
			((NetworkNode) Network.get(i)).generateDHTs(i, size_d, cantSuperPeers);
			
			// Asignación de Ip
			((NetworkNode) Network.get(i)).setIP((int)((NetworkNode) Network.get(i)).getID());
			
			// Asignación de puerto
			puerto = (int)(Math.random()*(4000-3000))+3000;
			((NetworkNode) Network.get(i)).setPuerto(puerto);
			
			// Asignacion de idHash
			IPMasPuerto = String.valueOf(((NetworkNode) Network.get(i)).getIP()+((NetworkNode) Network.get(i)).getPuerto());
			try {
				idHash = ((NetworkNode) Network.get(i)).sha1(IPMasPuerto);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			((NetworkNode) Network.get(i)).setIdHash(idHash);
			
			ArrayList<Integer> Vecinos = new ArrayList<Integer>();
			((NetworkNode) Network.get(i)).setVecinos(Vecinos);
			
			// asignacion de nodos al super peer
			((NetworkNode) Network.get(i)).asignarNodosASuperPeer(i, minNodesSuperPeer, maxNodesSuperPeer);
			
			((NetworkNode) Network.get(i)).setCantSuperPeers(cantSuperPeers);
			((NetworkNode) Network.get(i)).setK(kwr);
			((NetworkNode) Network.get(i)).setTtl(ttl);
			
			
			System.out.println("\tIP: "+((NetworkNode) Network.get(i)).getIP()+"\tPuerto: "+ ((NetworkNode) Network.get(i)).getPuerto()+"\tHashID: "+((NetworkNode) Network.get(i)).getIdHash()+"\tValor: "+ ((NetworkNode) Network.get(i)).getValor()+ "\tDHT: "+ ((NetworkNode) Network.get(i)).getDHT()+"\tCache: "+ ((NetworkNode) Network.get(i)).imprimirCache(i)+"\tSubred: "+ ((NetworkNode) Network.get(i)).getNameSubred()+", # "+ ((NetworkNode) Network.get(i)).getTamSubred()+" : "+ ((NetworkNode) Network.get(i)).getSubred()+"\t\tVecinos: "+ ((NetworkNode) Network.get(i)).getVecinos());
		}
		
		System.out.println("\nTamaño total de la red: "+Network.size()+"\n");
		
		// Recorrido de las subredes
		
		int aux;
		for (int i=0; i<cantSuperPeers; i++){
			System.out.println("INFORMACION DE SUBRED "+((NetworkNode) Network.get(i)).getNameSubred()+" ("+((NetworkNode) Network.get(i)).getTamSubred()+" peers + 1 SuperPeer)");
			for(int j=0; j<((NetworkNode) Network.get(i)).getTamSubred(); j++){
				aux = ((NetworkNode) Network.get(i)).getSubred().get((int) j);
				System.out.println("\tIP: "+aux+"\tValor: "+((NetworkNode) Network.get(aux)).getValor()+ "\tVecinos: "+((NetworkNode) Network.get(aux)).getVecinos());
			}
			System.out.println();
		}
		
		return true;
	}

}
