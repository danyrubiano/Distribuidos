package cl.usach.sd;

import peersim.core.GeneralNode;
import peersim.core.Network;

import java.util.ArrayList;
import java.util.Random;

public class ExampleNode extends GeneralNode {
	
	private int count;
	// Para determinar el topico, en este caso con numeros solamente, se detrmino por que el topico sea igual al id del nodo
	// es por esto que no se usa casi despues
	protected int topic;
	// Lista de los publishers que tiene el topico 
	protected ArrayList<Integer> PublishersTopicList;
	// Lista de los suscribers que tiene el topico
	protected ArrayList<Integer> SubscribersTopicList;
	// Lista de las publicaciones del topico
	protected ArrayList<Integer> PublicationsTopicList;
	// Lista de los id de los topicos que tiene el Publisher
	protected ArrayList<Integer> TopicsPublisherList;
	// Lista de los id de los topicos que tiene el Suscriber
	protected ArrayList<Integer> TopicsSubscriberList;
	// Para ver si el nodo es un topico
	protected int isTopic;
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public int getTopic() {
		return topic;
	}
	
	public void setTopic(int topic) {
		this.topic = topic;
	}
	
	public int getIsTopic() {
		return isTopic;
	}
	
	public void setIsTopic(int isTopic) {
		this.isTopic = isTopic;
	}
	
	public ExampleNode(String prefix) {
		super(prefix);
		this.setCount(0);
	}
	
	public ArrayList<Integer> getPublishersTopicList() {
		return PublishersTopicList;
	}

	public void setPublishersTopicList(ArrayList<Integer> PublishersTopicList) {
		this.PublishersTopicList = PublishersTopicList;
	}
	
	public ArrayList<Integer> getSubscribersTopicList() {
		return SubscribersTopicList;
	}

	public void setSubscribersTopicList(ArrayList<Integer> SubscribersTopicList) {
		this.SubscribersTopicList = SubscribersTopicList;
	}
	
	public ArrayList<Integer> getPublicationsTopicList() {
		return PublicationsTopicList;
	}

	public void setPublicationsTopicList(ArrayList<Integer> PublicationsTopicList) {
		this.PublicationsTopicList = PublicationsTopicList;
	}
	
	public ArrayList<Integer> getTopicsPublisherList() {
		return TopicsPublisherList;
	}

	public void setTopicsPublisherList(ArrayList<Integer> TopicsPublisherList) {
		this.TopicsPublisherList = TopicsPublisherList;
	}
	
	public ArrayList<Integer> getTopicsSubscriberList() {
		return TopicsSubscriberList;
	}

	public void setTopicsSubscriberList(ArrayList<Integer> TopicsSubscriberList) {
		this.TopicsSubscriberList = TopicsSubscriberList;
	}
	
	// Funcion para registrar un nodo como publisher en un cierto topico
	// Recibe como parametro el id del nodo que quiere ser publisher
	public ExampleNode RegisterPublisher(int idNodo) {
		
		ArrayList<Integer> TopicsList = new ArrayList<Integer>(); // lista para buscar topicos en la red
		ExampleNode nodo = null;
		// Topico a Inscribir
		int topico, aux, i;
		//Busqueda de topicos en la red
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
			
			for(i=0; i<Network.size(); i++) {
				if(((ExampleNode) Network.get(i)).getIsTopic()==0){
					break;
				}
			}
			aux = i;
			
			if(((ExampleNode) Network.get(aux)).getIsTopic()==0){ // asignar un nodo que no este aun como topico a un topico
				((ExampleNode) Network.get(aux)).setIsTopic(1);
				((ExampleNode) Network.get(aux)).setTopic(aux); // se setea el nombre del topico con el id
				topico = aux;
				// Se añade a la lista de los topicos que tiene el publisher
				((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().add(topico);
				// Se añade a la lista de los publishers que tiene el topico
				((ExampleNode) Network.get(topico)).getPublishersTopicList().add(idNodo);
				System.out.println("El nodo "+idNodo+" se ha inscrito al tópico "+topico+" como publisher.");
			}
			
			else if(nodo.getIsTopic()==0){ // Verifica si este nodo no es topico, para hacerlo como topico
				// Se asigna el nodo como topico
				((ExampleNode) Network.get(idNodo)).setIsTopic(1);
				((ExampleNode) Network.get(idNodo)).setTopic(idNodo); // se setea el nombre del topico con el id
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
		return nodo;
	}
	
	// Funcion para publicar en un topico
	// Recibe como parametro el id del nodo que quiere publicar
	public ExampleNode Publish(int idNodo) {
		
		//ExampleNode nodo;
		int topico, aux, publicacion;
		ExampleNode nodo = null;
		
		// Se verifica si el nodo es publisher de algun topico
		if(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().isEmpty()) {
			// como no es publisher de ningun topico, aviso por pantalla
			System.out.println("El nodo "+idNodo+" no es publisher en ningún tópico");
			nodo = (ExampleNode) Network.get(idNodo); // para no enviar un null
		}
		else { // el nodo es publisher de algun topico 
			Random r = new Random(); 
			// Se genera un valor random para elegir el topico en el cual publicar
			aux = r.nextInt(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().size());
			topico = ((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().get(aux);
			nodo = (ExampleNode) Network.get(topico);
			publicacion = r.nextInt(999999999)+0; // Publicacion, un numero aleatorio
			// Se añade la publicacion al topico
			((ExampleNode) Network.get(topico)).getPublicationsTopicList().add(publicacion);
			System.out.println("El nodo "+idNodo+" publicó "+publicacion+" en el tópico "+topico);
		}
		
		return nodo;
	}
	
	public ExampleNode DeletePublication(int idNodo) {
		
		int topico, aux, publicacion;
		Random r = new Random();
		ExampleNode nodo = null;
		
		// Se verfifica si el nodo es publisher de algun topico
		if(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().isEmpty()) {
			// como no es publisher de ningun topico, aviso por pantalla
			System.out.println("El nodo "+idNodo+" no es publisher de ningún tópico");
			nodo = (ExampleNode) Network.get(idNodo); // para no enviar un null
		}
		
		else { // el nodo es publisher de algun topico
			// Se genera un valor random para elegir el topico en el cual eliminar una publicacion
			aux = r.nextInt(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().size());
			topico = ((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().get(aux);
			
			// Se verifica si el topico seleccionado tiene alguna publicacion
			if(((ExampleNode) Network.get(topico)).getPublicationsTopicList().isEmpty()) {
				//como no tiene ninguna publicacion, aviso por pantalla
				System.out.println("El topico "+topico+" no tiene ninguna publicacion para eliminar");
				nodo = (ExampleNode) Network.get(idNodo); // para no enviar un null
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
		return nodo;
	}
	
	public ExampleNode DeregisterPublisher(int idNodo) {
		
		int topico, aux;
		ExampleNode nodo = null;
		
		// se verifica si el nodo es publisher de algun topico
		if(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().isEmpty()) {
			// como no es publisher de ningun topico, aviso por pantalla
			System.out.println("El nodo "+idNodo+" no es publisher de ningún tópico");
			nodo = (ExampleNode) Network.get(idNodo); // para no enviar un null
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
		return nodo;
	}


	public ExampleNode RegisterSubscriber(int idNodo) {
		
		int topico, aux, i;
		ExampleNode nodo = null;
		ArrayList<Integer> TopicsList = new ArrayList<Integer>();
		
		for(i=0; i<Network.size(); i++){
			if(((ExampleNode) Network.get(i)).getIsTopic()==1) {
				TopicsList.add(i);
			}
		}
		
		if(TopicsList.isEmpty()){ // si la lista de topicos esta vacia, aviso por pantalla
			System.out.println("No hay ningun topico en el cual subscribirse");
			nodo = (ExampleNode) Network.get(idNodo); // para no enviar un null
		}
		
		else { // Si la lista de topicos no esta vacia
			Random r = new Random(); // Se genera un valor random para elegir el topico al cual subscribirse
			aux = r.nextInt(TopicsList.size());
			topico = TopicsList.get(aux);
			
			if(((ExampleNode) Network.get(idNodo)).getTopicsPublisherList().contains(topico)) { // Verifica si el nodo ya esta subscrito al topico
				System.out.println("El nodo "+idNodo+" ya esta subscrito al tópico "+topico);
				nodo = (ExampleNode) Network.get(idNodo); // para no enviar un null
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
		return nodo;
	}
	
	public ExampleNode RequestUpdate(int idNodo) {
		
		int topico, aux;
		ExampleNode nodo = null;
		
		
		if(!((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().isEmpty()){
			Random r = new Random(); // Se genera un valor random para elegir el topico al cual inscribirse
			aux = r.nextInt(((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().size());
			topico = ((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().get(aux);
			// se guarda en nodo el objeto topico 
			nodo = ((ExampleNode) Network.get(topico));
		}
		
		else {
			nodo = (ExampleNode) Network.get(idNodo); // para no enviar un null
		}
		return nodo;
	}
	
	public ExampleNode DeregisterSubscriber(int idNodo) {
		
		int topico, aux;
		ExampleNode nodo = null;
		
		// se verifica si el nodo esta subscrito a algun topico
		if(((ExampleNode) Network.get(idNodo)).getTopicsSubscriberList().isEmpty()) {
			// como no es su de ningun topico, aviso por pantalla
			System.out.println("El nodo "+idNodo+" no esta subscrito a ningun topico");
			nodo = (ExampleNode) Network.get(idNodo); // para no enviar un null
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
		return nodo;
	}

}
