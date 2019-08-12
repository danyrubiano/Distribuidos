package cl.usach.sd;

import java.util.ArrayList;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

// Se configura el nodo inicial
/* Se realiza la primera consulta
 * 
 */

public class TrafficGenerator implements Control {
	private final static String PAR_PROT = "protocol";
	private final int layerId;

	public TrafficGenerator(String prefix) {
		layerId = Configuration.getPid(prefix + "." + PAR_PROT);

	}

	@Override
	public boolean execute() {
		
		int destino = CommonState.r.nextInt(Network.size());
		int emisor = CommonState.r.nextInt(Network.size());
		int consulta = CommonState.r.nextInt(((NetworkNode) Network.get(destino)).getBD().size()*Network.size());
		ArrayList<Integer> Camino = new ArrayList<Integer>();
		int idaVuelta = 0; // se setea en ceo para indicar que es el camino de ida
		int respuesta = -1; // la respuesta es -1 mientras se obtiene
		int contador = 0; // se inicializa el contador en cero
		
		Node initNode = Network.get(emisor); 


		// Se crea un nuevo mensaje utilizando el valor que tiene el nodo inicial

		Message message = new Message(emisor, destino, consulta, idaVuelta, respuesta, Camino, contador);
		System.out.println("["+emisor+","+destino+","+consulta+"]\tNodo "+emisor+" consultará a nodo "+destino+" por consulta "+consulta);

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
