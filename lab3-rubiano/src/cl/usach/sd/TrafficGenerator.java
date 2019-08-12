package cl.usach.sd;

import java.util.ArrayList;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

public class TrafficGenerator implements Control {
	private final static String PAR_PROT = "protocol";
	private final int layerId;

	public TrafficGenerator(String prefix) {
		layerId = Configuration.getPid(prefix + "." + PAR_PROT);

	}

	@Override
	public boolean execute() {
		int cantSuperPeers = ((NetworkNode) Network.get(0)).getCantSuperPeers();
		int emisor = (int)(Math.random()*(Network.size()-cantSuperPeers))+cantSuperPeers; //random entre los peers de la red
		int consulta = CommonState.r.nextInt(Network.size());
		int spDestino = CommonState.r.nextInt(cantSuperPeers);
		ArrayList<Integer> Camino = new ArrayList<Integer>();
		int idaVuelta = 0; // se setea en ceo para indicar que es el camino de ida
		int respuesta = -1; // la respuesta es -1 mientras se obtiene
		int contador = 0; // se inicializa el contador en cero
		int ttl=((NetworkNode) Network.get(0)).getTtl();
		int k=((NetworkNode) Network.get(0)).getK();
		int kwr = 1; // indicando que el primer paso es buscar el super peer de la subred
		
		// Consideraremos cualquier nodo de manera aleatoria de la red para comenzar y finalizar
		Node initNode = Network.get(emisor);
		
		Message message = new Message(emisor, consulta, spDestino, idaVuelta, respuesta, Camino, contador, ttl, k, kwr);
		System.out.println("["+emisor+","+spDestino+","+consulta+"]\tNodo "+emisor+" consultará "+consulta+" en la red del Super Peer "+spDestino);

		// Y se envía, para realizar la simulación
		// Los parámetros corresponde a:
		// long arg0: Delay del evento
		// Object arg1: Evento enviado
		// Node arg2: Nodo por el cual inicia el envÃ­o del dato
		// int arg3: Número de la capa del protocolo que creamos (en este caso
		// de layerId)
		EDSimulator.add(0, message, initNode, layerId);

		return false;
	}

}
