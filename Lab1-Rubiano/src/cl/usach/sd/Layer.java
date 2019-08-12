package cl.usach.sd;

import java.util.Random;

import peersim.config.Configuration;
import peersim.core.Network;
import peersim.core.Node;
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
		Message message = (Message) event;
		//Imprimir el mensaje enviado
		System.out.println(message.getText());
		//	ExampleNode nodo = (ExampleNode) myNode;
		ExampleNode nodoDestinatario = null;
		ExampleNode	nodoPublicador = (ExampleNode) myNode;
		ExampleNode	nodoSubscriber = (ExampleNode) myNode;
		
		Random r = new Random();
		int opcion1, opcion2, auxNode, aux1, aux2, i;
		aux1= (int)myNode.getID();
		opcion1 = (int)(r.nextInt(2)+ 1);
		//stem.out.println(opcion1);
		
		if(opcion1 == 1){ // opcion 1 corresponde a acciones del publisher
			//Publisher nodoPublicador = (Publisher) myNode;
			opcion2 = (int)(r.nextInt(3)+ 1); //opcion de las acciones
			
			if(opcion2 == 1) { // corresponde a registrarse como publisher
				nodoDestinatario = nodoPublicador.RegisterPublisher((int) nodoPublicador.getID());
				aux2 = (int) nodoDestinatario.getID();
				message.setText("\tNodeID: "+((ExampleNode) Network.get(aux1)).getID()+", Publisher Topics List "+((ExampleNode) Network.get(aux1)).getTopicsPublisherList()+"\tTopic: "+((ExampleNode) Network.get(aux2)).getID()+", Topic Publishers List "+((ExampleNode) Network.get(aux2)).getPublishersTopicList());
				sendmessage(nodoPublicador, (int) nodoDestinatario.getID(), message, layerId);
			}
			else if(opcion2 == 2) { // corresponde a publicar 
				nodoDestinatario = nodoPublicador.Publish((int) nodoPublicador.getID());
				aux2 = (int) nodoDestinatario.getID();
				message.setText("\tNodeID: "+((ExampleNode) Network.get(aux1)).getID()+", Publisher Topics List "+((ExampleNode) Network.get(aux1)).getTopicsPublisherList()+"\tTopic: "+((ExampleNode) Network.get(aux2)).getID()+", Topic Publishers List "+((ExampleNode) Network.get(aux2)).getPublishersTopicList()+", Publications Topic List "+((ExampleNode) Network.get(aux2)).getPublicationsTopicList());
				sendmessage(nodoPublicador, (int) nodoDestinatario.getID(), message, layerId);
				
				for(i=0; i<nodoDestinatario.getSubscribersTopicList().size();i++){ // Notificacion a todos los subscriber de una nueva publicacion
					auxNode = nodoDestinatario.getSubscribersTopicList().get(i);
					System.out.println("Se le informa al nodo "+auxNode+" que el publisher "+nodoPublicador.getID()+" ha realizado una publicacion en el topico "+nodoDestinatario.getID());
				}
			}
			else if(opcion2 == 3) { // corresponde a eliminar publicacion
				nodoDestinatario = nodoPublicador.DeletePublication((int) nodoPublicador.getID());
				aux2 = (int) nodoDestinatario.getID();
				message.setText("\tNodeID: "+((ExampleNode) Network.get(aux1)).getID()+", Publisher Topics List "+((ExampleNode) Network.get(aux1)).getTopicsPublisherList()+"\tTopic: "+((ExampleNode) Network.get(aux2)).getID()+", Topic Publishers List "+((ExampleNode) Network.get(aux2)).getPublishersTopicList()+", Publications Topic List "+((ExampleNode) Network.get(aux2)).getPublicationsTopicList());
				sendmessage(nodoPublicador, (int) nodoDestinatario.getID(), message, layerId);
				
				for(i=0; i<nodoDestinatario.getSubscribersTopicList().size();i++){ // Notificacion a todos los subscriber de la eliminacion de una publicacion
					auxNode = nodoDestinatario.getSubscribersTopicList().get(i);
					System.out.println("Se le informa al nodo "+auxNode+" que el publisher "+nodoPublicador.getID()+" ha eliminado una publicacion en el topico "+nodoDestinatario.getID());
				}
			}
			else if(opcion2 == 4) { // corresponde a deinscribirse como publisher de un topico
				nodoDestinatario = nodoPublicador.DeregisterPublisher((int) nodoPublicador.getID());
				aux2 = (int) nodoDestinatario.getID();
				message.setText("\tNodeID: "+((ExampleNode) Network.get(aux1)).getID()+", Publisher Topics List "+((ExampleNode) Network.get(aux1)).getTopicsPublisherList()+"\tTopic: "+((ExampleNode) Network.get(aux2)).getID()+", Topic Publishers List "+((ExampleNode) Network.get(aux2)).getPublishersTopicList()+", Publications Topic List "+((ExampleNode) Network.get(aux2)).getPublicationsTopicList());
				sendmessage(nodoPublicador, (int) nodoDestinatario.getID(), message, layerId);
			}
	
		}
		
		if(opcion1 == 2) { // opcion 2 corresponde a acciones del suscriber
			opcion2 = (int)(r.nextInt(3)+ 1); //opcion de las acciones
			
			if(opcion2 == 1) { // corresponde a registrarse como publisher
				nodoDestinatario = nodoSubscriber.RegisterSubscriber((int) nodoSubscriber.getID());
				aux2 = (int) nodoDestinatario.getID();
				message.setText("\tNodeID: "+((ExampleNode) Network.get(aux1)).getID()+", Subscriber Topics List "+((ExampleNode) Network.get(aux1)).getTopicsPublisherList()+"\tTopic: "+((ExampleNode) Network.get(aux2)).getID()+", Topic Subscribers List "+((ExampleNode) Network.get(aux2)).getPublishersTopicList());
				sendmessage(nodoSubscriber, (int) nodoDestinatario.getID(), message, layerId);
			}
			else if(opcion2 == 2) { // corresponde a requerir un update
				nodoDestinatario = nodoSubscriber.RequestUpdate((int) nodoSubscriber.getID());
				aux2 = (int) nodoDestinatario.getID();
				message.setText("\tNodeID: "+((ExampleNode) Network.get(aux1)).getID()+", Subscriber Topics List "+((ExampleNode) Network.get(aux1)).getTopicsPublisherList()+"\tTopic: "+((ExampleNode) Network.get(aux2)).getID()+", Topic Subscribers List "+((ExampleNode) Network.get(aux2)).getPublishersTopicList());
				sendmessage(nodoSubscriber, (int) nodoDestinatario.getID(), message, layerId);
			}
			else if(opcion2 == 3) { // corresponde a desubscribirse
				nodoDestinatario = nodoSubscriber.DeregisterSubscriber((int) nodoSubscriber.getID());
				aux2 = (int) nodoDestinatario.getID();
				message.setText("\tNodeID: "+((ExampleNode) Network.get(aux1)).getID()+", Subscriber Topics List "+((ExampleNode) Network.get(aux1)).getTopicsPublisherList()+"\tTopic: "+((ExampleNode) Network.get(aux2)).getID()+", Topic Subscribers List "+((ExampleNode) Network.get(aux2)).getPublishersTopicList());
				sendmessage(nodoSubscriber, (int) nodoDestinatario.getID(), message, layerId);
			}
		}
		getStats();
	}

	private void getStats() {
		Observer.message.add(1);
	}

	public void sendmessage(Node currentNode, int nextNode, Object message, int layerId) {
		//t i;
		((Transport) currentNode.getProtocol(transportId)).send(currentNode, searchNode(nextNode), message, layerId);
		//= (int)currentNode.getID();
		//stem.out.println("\tNodeID: "+((ExampleNode) Network.get(i)).getID()+", Publisher Topics List "+((ExampleNode) Network.get(i)).getTopicsPublisherList()+", Suscriber Topics List "+((ExampleNode) Network.get(i)).getTopicsSubscriberList()+", Topic Publishers List "+((ExampleNode) Network.get(i)).getPublishersTopicList()+", Topic Subscribers List "+((ExampleNode) Network.get(i)).getSubscribersTopicList());

		
	}

	/**
	 * Constructor por defecto de la capa Layer del protocolo construido
	 * 
	 * @param prefix
	 */
	public Layer(String prefix) {
		/**
		 * Inicialización del Nodo
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
