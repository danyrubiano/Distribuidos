package cl.usach.sd;

import peersim.core.Network;
import java.util.ArrayList;
import java.util.Random;

public class Publisher extends ExampleNode{

	public Publisher(String prefix) {
		super(prefix);
		// TODO Auto-generated constructor stub
	}
	
	// Funcion para registrar un nodo como publisher en un cierto topico
	// Recibe como parametro el id del nodo que quiere ser publisher
	public ExampleNode RegisterPublisher(int idNodo) {
		
		ExampleNode nodo = null;
		// Topico a Inscribir
		int topico, aux, i;
		ArrayList<Integer> TopicsList = new ArrayList<Integer>(); // lista para buscar topicos en la red
		
		for(i=0; i<Network.size(); i++){
			if(((ExampleNode) Network.get(i)).getIsTopic()==1) {
				TopicsList.add(i);
			}
		}
		
		// Si la lista de topicos no esta vacia
		if(!(TopicsList.isEmpty())) {
			Random r = new Random(); // Se genera un valor random para elegir el topico al cual inscribirse
			aux = r.nextInt(TopicsList.size());
			topico = TopicsList.get(aux);
			nodo = (ExampleNode) Network.get(topico);
			
			if(nodo.getTopicsPublisherList().contains(topico)) { // Verifica si el nodo ya esta inscrito en el topico
				System.out.println("El nodo "+idNodo+" ya esta inscrito al tópico "+topico+" como publisher");
			}
			else { // si el nodo no esta inscrito entonces
				// Se añade a la lista de los topicos que tiene el publisher
				((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().add(topico);
				// Se añade a la lista de los publishers que tiene el topico
				((ExampleNode) Network.get(topico)).getPublishersTopicList().add(idNodo);
				System.out.println("El nodo "+idNodo+" se ha inscrito al tópico "+topico+" como publisher");
			}
		}
		// Si la lista de topicos esta vacia
		else if(TopicsList.isEmpty()){
			nodo = (ExampleNode) Network.get(idNodo);
			
			if(nodo.getIsTopic()==0){ // Verifica si este nodo no es topico, para hacerlo como topico
				// Se asigna el nodo como topico
				((ExampleNode) Network.get(idNodo)).setIsTopic(1);
				topico = idNodo;
				// Se añade a la lista de los topicos que tiene el publisher
				((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().add(idNodo);
				// Se añade a la lista de los publishers que tiene el topico
				((ExampleNode) Network.get(idNodo)).getPublishersTopicList().add(idNodo);
				System.out.println("El nodo "+idNodo+" se ha inscrito al tópico "+topico+" como publisher.");
			}
			else if(nodo.getIsTopic()==1) { // Este nodo ya es topico, entonces se crea uno nuevo
				nodo = new ExampleNode("init.1statebuilder");
				Network.add(nodo);
				topico = (int) nodo.getID();
				((ExampleNode) Network.get(topico)).setIsTopic(1);
				ArrayList<Integer> PublishersTopicList = new ArrayList<Integer>();
				((ExampleNode) Network.get(topico)).setPublishersTopicList(PublishersTopicList);
				ArrayList<Integer> SubscribersTopicList = new ArrayList<Integer>();
				((ExampleNode) Network.get(topico)).setSubscribersTopicList(SubscribersTopicList);
				ArrayList<Integer> PublicationsTopicList = new ArrayList<Integer>();
				((ExampleNode) Network.get(topico)).setPublicationsTopicList(PublicationsTopicList);
				ArrayList<Integer> TopicsPublisherList = new ArrayList<Integer>();
				((ExampleNode) Network.get(topico)).setTopicsPublisherList(TopicsPublisherList);
				ArrayList<Integer> TopicsSubscriberList = new ArrayList<Integer>();
				((ExampleNode) Network.get(topico)).setTopicsSubscriberList(TopicsSubscriberList);
				// Se añade a la lista de los topicos que tiene el publisher
				((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().add(topico);
				// Se añade a la lista de los publishers que tiene el topico
				((ExampleNode) Network.get(topico)).getPublishersTopicList().add(idNodo);
				System.out.println("El nodo "+idNodo+" se ha inscrito al tópico "+topico+" como publisher.");
			}
		}
		return (Publisher) nodo;
	}
	
	// Funcion para publicar en un topico
	// Recibe como parametro el id del nodo que quiere publicar
	public Publisher Publish(int idNodo) {
		
		//ExampleNode nodo;
		int topico, aux, publicacion;
		ExampleNode nodo = null;
		
		// Se verifica si el nodo es publisher de algun topico
		if(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().isEmpty()) {
			// como no es publisher de ningun topico, aviso por pantalla
			System.out.println("El nodo "+idNodo+" no es publisher en ningún tópico");
		}
		else { // el nodo es publisher de algun topico 
			Random r = new Random(); 
			// Se genera un valor random para elegir el topico en el cual publicar
			aux = r.nextInt(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().size());
			topico = ((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().get(aux);
			nodo = (ExampleNode) Network.get(topico);
			publicacion = r.nextInt(999999999); // Publicacion, un numero aleatorio
			// Se añade la publicacion al topico
			((ExampleNode) Network.get(topico)).getPublicationsTopicList().add(publicacion);
			System.out.println("El nodo "+idNodo+" publicó "+publicacion+" en el tópico "+topico);
		}
		
		return (Publisher) nodo;
	}
	
	public Publisher DeletePublication(int idNodo) {
		
		int topico, aux, publicacion;
		Random r = new Random();
		ExampleNode nodo = null;
		
		// Se verfifica si el nodo es publisher de algun topico
		if(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().isEmpty()) {
			// como no es publisher de ningun topico, aviso por pantalla
			System.out.println("El nodo "+idNodo+" no es publisher de ningún tópico");
		}
		
		else { // el nodo es publisher de algun topico
			// Se genera un valor random para elegir el topico en el cual eliminar una publicacion
			aux = r.nextInt(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().size());
			topico = ((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().get(aux);
			
			// Se verifica si el topico seleccionado tiene alguna publicacion
			if(((ExampleNode) Network.get(topico)).getPublicationsTopicList().isEmpty()) {
				//como no tiene ninguna publicacion, aviso por pantalla
				System.out.println("El topico "+topico+" no tiene ninguna publicacion para eliminar");
			}
			else { // el topico tiene publicaciones
				nodo = (ExampleNode) Network.get(topico);
				// Se genera un valor random para elegir la posicion de una publicacion a eliminar el el topico seleccionado
				aux = r.nextInt(((ExampleNode) Network.get(topico)).getPublicationsTopicList().size());
				// Se asigna la publicacion a la variable
				publicacion = ((ExampleNode) Network.get(topico)).getPublicationsTopicList().get(aux);
				//se elimina el objeto publicacion de la lista
				((ExampleNode) Network.get(topico)).getPublicationsTopicList().remove((Integer) publicacion);
				System.out.println("El nodo "+idNodo+" eliminó la publicación "+publicacion+" del tópico "+topico);
			}
		}
		return (Publisher) nodo;
	}
	
	public Publisher DeregisterPublisher(int idNodo) {
		
		int topico, aux;
		ExampleNode nodo = null;
		
		// se verifica si el nodo es publisher de algun topico
		if(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().isEmpty()) {
			// como no es publisher de ningun topico, aviso por pantalla
			System.out.println("El nodo "+idNodo+" no es publisher de ningún tópico");
		}
		else { // el nodo es publisher de algun topico
			Random r = new Random();
			// random para elegir la posicion de la lista de algun topico 
			aux = r.nextInt(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().size());
			// el topico es elegido segun la posicion de la lista 
			topico = ((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().get(aux);
			nodo = (ExampleNode) Network.get(topico);
			// se elimina el objeto nodo de la lista de publishers que tiene el topico
			((ExampleNode) Network.get(topico)).getPublishersTopicList().remove((Integer) idNodo);
			// se elimina el objeto nodo del topico de la lista de topicos que tiene el publisher
			((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().remove((Integer) topico);
			System.out.println("El nodo "+idNodo+" se desinscribio como publisher del topico "+topico);
		}
		return (Publisher) nodo;
	}
	

}
