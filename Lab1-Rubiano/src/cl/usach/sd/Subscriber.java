package cl.usach.sd;

import peersim.core.Network;

import java.util.ArrayList;
import java.util.Random;

public class Subscriber extends ExampleNode {

	public Subscriber(String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
	}
	
	public Subscriber RegisterSubscriber(int idNodo) {
		
		int topico, aux, i;
		ExampleNode nodo = null;
		ArrayList<Integer> TopicsList = new ArrayList<Integer>(); // lista para buscar topicos en la red
		
		for(i=0; i<Network.size(); i++){
			if(((ExampleNode) Network.get(i)).getIsTopic()==1) {
				TopicsList.add(i);
			}
		}
		
		if(TopicsList.isEmpty()){ // si la lista de topicos esta vacia, aviso por pantalla
			System.out.println("No hay ningun topico en el cual subscribirse");
		}
		
		else { // Si la lista de topicos no esta vacia
			Random r = new Random(); // Se genera un valor random para elegir el topico al cual subscribirse
			aux = r.nextInt(TopicsList.size());
			topico = TopicsList.get(aux);
			
			if(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().contains(topico)) { // Verifica si el nodo ya esta subscrito al topico
				System.out.println("El nodo "+idNodo+" ya esta subscrito al tópico "+topico);
			}
			else { // si el nodo no esta inscrito entonces
				nodo = ((ExampleNode) Network.get(topico));
				// Se añade a la lista de los topicos que tiene el publisher
				((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().add(topico);
				// Se añade a la lista de los publishers que tiene el topico
				((ExampleNode) Network.get(topico)).getSubscribersTopicList().add(idNodo);
				System.out.println("El nodo "+idNodo+" se ha subscrito al tópico "+topico);
			}
		}
		return (Subscriber) nodo;
	}
	
	public Subscriber RequestUpdate(int idNodo) {
		
		int topico, aux;
		ExampleNode nodo = null;
		
		if(!((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().isEmpty()){
			Random r = new Random(); // Se genera un valor random para elegir el topico al cual inscribirse
			aux = r.nextInt(((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().size());
			topico = ((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().get(aux);
			// se guarda en nodo el objeto topico 
			nodo = ((ExampleNode) Network.get(topico));
		}
		return (Subscriber) nodo;
	}
	
	public Subscriber DeregisterSubscriber(int idNodo) {
		
		int topico, aux;
		ExampleNode nodo = null;
		
		// se verifica si el nodo esta subscrito a algun topico
		if(((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().isEmpty()) {
			// como no es su de ningun topico, aviso por pantalla
			System.out.println("El nodo "+idNodo+" no esta subscrito a ningun topico");
		}
		else { // el nodo esta subscrito a algun topico
			Random r = new Random();
			// random para elegir la posicion de la lista de algun topico 
			aux = r.nextInt(((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().size());
			// el topico es elegido segun la posicion de la lista 
			topico = ((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().get(aux);
			nodo = ((ExampleNode) Network.get(topico));
			// se elimina el objeto nodo de la lista de subscribers que tiene el topico
			((ExampleNode) Network.get(topico)).getSubscribersTopicList().remove((Integer) idNodo);
			// se elimina el objeto nodo del topico de la lista de topicos que el nodo esta subscrito
			((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().remove((Integer) topico);
			System.out.println("El nodo "+idNodo+" se ha desuscrito del topico "+topico);
		}
		return (Subscriber) nodo;
	}

}
