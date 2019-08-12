package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

import java.util.*;

public class Initialization implements Control {
	String prefix;

	int idLayer;
	int idTransport;

	//Valores que sacaremos del archivo de configuración	
	int argExample;
	int initValue;

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
		System.out.println("Arg: " + argExample);
		System.out.println("Valor inicial: "+ initValue);
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
		int nodoInicial = CommonState.r.nextInt(Network.size());
		
		
	
		/**Es conveniente inicializar los nodos, puesto que los nodos 
		 * son una clase clonable y si asignan valores desde el constructor
		 *  todas tomaran los mismos valores, puesto que tomaran la misma dirección
		 * de memoria*/
		
		System.out.println("Inicializamos los nodos:");
		for (int i = 0; i < Network.size(); i++) {			
			//int random = CommonState.r.nextInt(2);
			((ExampleNode) Network.get(i)).setIsTopic(0);
			ArrayList<Integer> PublishersTopicList = new ArrayList<Integer>();
			((ExampleNode) Network.get(i)).setPublishersTopicList(PublishersTopicList);
			ArrayList<Integer> SubscribersTopicList = new ArrayList<Integer>();
			((ExampleNode) Network.get(i)).setSubscribersTopicList(SubscribersTopicList);
			ArrayList<Integer> PublicationsTopicList = new ArrayList<Integer>();
			((ExampleNode) Network.get(i)).setPublicationsTopicList(PublicationsTopicList);
			ArrayList<Integer> TopicsPublisherList = new ArrayList<Integer>();
			((ExampleNode) Network.get(i)).setTopicsPublisherList(TopicsPublisherList);
			ArrayList<Integer> TopicsSubscriberList = new ArrayList<Integer>();
			((ExampleNode) Network.get(i)).setTopicsSubscriberList(TopicsSubscriberList);
			//System.out.println("NodeID: "+((ExampleNode) Network.get(i)).getID()+"\tNode Type: "+ ((ExampleNode) Network.get(i)).getType()+"\tValue: "+ ((ExampleNode) Network.get(i)).getValue());
		}
		System.out.println("Nodos listos");
		
		
		return true;
	}

}
