package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;

/* Debe Imprimir todos los estado de la Red 
 */

public class Observer implements Control {

	private int layerId;
	private String prefix;

	public static IncrementalStats message = new IncrementalStats();

	public Observer(String prefix) {
		this.prefix = prefix;
		this.layerId = Configuration.getPid(prefix + ".protocol");
	}

	@Override
	public boolean execute() {
		int size = Network.size();
		for (int i = 0; i < Network.size(); i++) {
			if (!Network.get(i).isUp()) {
				size--;
			}
		}

		String s = String.format("[time=%d]:[with N=%d nodes] [%d Total send message]", CommonState.getTime(), size,
				(int) message.getSum());
		
		for (int i = 0; i < Network.size(); i++) {			
			//System.err.println("\tNodeID: "+((NetworkNode) Network.get(i)).getID()+"\tNode Type: "+ ((NetworkNode) Network.get(i)).getType()+"\tValue: "+ ((NetworkNode) Network.get(i)).getValue());
			System.err.println("\tNodeID: "+((NetworkNode) Network.get(i)).getID()+" \tVecino: "+ ((NetworkNode) Network.get(i)).getVecino()+"\tDHT: "+ ((NetworkNode) Network.get(i)).getDHT()+"  \tBD: "+ ((NetworkNode) Network.get(i)).getBD()+"\tCache: "+ ((NetworkNode) Network.get(i)).imprimirCache(i));
		}
		
		System.err.println(s);

		return false;
	}

}
