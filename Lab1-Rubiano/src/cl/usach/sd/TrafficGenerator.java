package cl.usach.sd;

import java.util.Random;
import peersim.config.Configuration;
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
		
		Random r = new Random();
		int aux;
		aux = (int)(r.nextInt(Network.size()-1)+ 0);
		//Node initNode = Network.get(CommonState.r.nextInt(Network.size())); 
		Node initNode = Network.get(aux);

		Message message = new Message("Inicio de la Simulación.");
		EDSimulator.add(0, message, initNode, layerId);
		//System.out.println("Hola");
		return false;
	}

}