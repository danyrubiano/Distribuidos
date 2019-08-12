package cl.usach.sd;

import peersim.config.Configuration;

import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import java.util.ArrayList;

public class Initialization implements Control {
	String prefix;

	int idLayer;
	int idTransport;

	//Valores que sacaremos del archivo de configuración	
	int argExample;
	int initValue;
	int size_cache;
	int size_bd;
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
		this.size_bd = Configuration.getInt(prefix + ".bd");
		this.size_cache = Configuration.getInt(prefix + ".cache");
		this.size_d = Configuration.getInt(prefix + ".d");
		
		System.out.println("Valores Iniciales:");
		System.out.println("\tn: " + Network.size()+"\t[Tamaño de la Red]");
		System.out.println("\tb: " + size_bd+"\t[Tamaño de la Base de Datos]");
		System.out.println("\tn: " + size_cache+"\t[Tamaño del Cache]");
		System.out.println("\tn: " + size_d+"\n");
		
		/*
		System.out.println("Calculos Iniciales:");
		System.out.println("\tDHT: " + size_d+"\n");
		*/
		//System.out.println("Arg: " + argExample);
		//System.out.println("Valor inicial: "+ initValue);
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
		 * de memoria
		 * 
		 * Dado el tamaño de la red, se inicializa cada nodo
		 * Se setea una base de datos, un cache vacio y un DHT
		 * Ademas se setea el 
		 */
		
		
		System.out.println("Inicializamos los nodos:");
		for (int i = 0; i < Network.size(); i++) {
			
			int random = CommonState.r.nextInt(2);
			((NetworkNode) Network.get(i)).setType(random);
			((NetworkNode) Network.get(i)).setValue(this.initValue);
			
			// Añade el vecino del nodo, segun la red estructurada
			((NetworkNode) Network.get(i)).asignarVecino(i);
			
			// Inicializacion de la base de datos de cada nodo
			ArrayList<Integer> DB = new ArrayList<Integer>();
			((NetworkNode) Network.get(i)).setDB(DB);
			((NetworkNode) Network.get(i)).generateBD(i, size_bd);
			
			// Inicializacion del DHT de cada nodo
			ArrayList<Integer> DHT = new ArrayList<Integer>();
			((NetworkNode) Network.get(i)).setDHT(DHT);
			((NetworkNode) Network.get(i)).generateDHTs(i, size_d);
			
			// Inicializacion de la cache para cada nodo
			int[][] Cache = new int[size_cache][3];
			((NetworkNode) Network.get(i)).setCache(Cache);
			
			/* seteo de los valores de cache en -1
			 * Cache[j][0] es para el nodo consultado
			 * Cache[j][1] es para guardar la consulta "respuesta"
			 * Cache[j][3] es para ver si tiene respuesta a esa consulta
			 *             si se setea con 0, quiere decir que no hay respuesta
			 *             si se setea con 1, quiere decir que si hay respuesta
			 */
			for(int j=0; j<size_cache; j++){
				Cache[j][0]=-1;
				Cache[j][1]=-1;
				Cache[j][2]=-1;
			}
			
			System.out.println("\tNodeID: "+((NetworkNode) Network.get(i)).getID()+" \tVecino: "+ ((NetworkNode) Network.get(i)).getVecino() + "\tDHT: "+ ((NetworkNode) Network.get(i)).getDHT()+"  \tBD: "+ ((NetworkNode) Network.get(i)).getBD()+"  \tCache: "+ ((NetworkNode) Network.get(i)).imprimirCache(i));
		}
		System.out.print("\n");

		return true;
	}

}
