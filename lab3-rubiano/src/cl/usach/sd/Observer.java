package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;

public class Observer implements Control {

	private int layerId;
	private String prefix;
	int cantSuperPeers;

	public static IncrementalStats message = new IncrementalStats();

	public Observer(String prefix) {
		this.prefix = prefix;
		this.layerId = Configuration.getPid(prefix + ".protocol");
		this.cantSuperPeers = Configuration.getInt(prefix + ".s");
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
		
		for (int i = 0; i < cantSuperPeers; i++) {			
			System.err.println("\tIP: "+((NetworkNode) Network.get(i)).getIP()+"\tPuerto: "+ ((NetworkNode) Network.get(i)).getPuerto()+"\tValor: "+ ((NetworkNode) Network.get(i)).getValor()+ "\tDHT: "+ ((NetworkNode) Network.get(i)).getDHT()+"\tCache: "+ ((NetworkNode) Network.get(i)).imprimirCache(i)+"\tSubred: "+ ((NetworkNode) Network.get(i)).getNameSubred()+", # "+ ((NetworkNode) Network.get(i)).getTamSubred()+" : "+ ((NetworkNode) Network.get(i)).getSubred()+"\t\tVecinos: "+ ((NetworkNode) Network.get(i)).getVecinos());
		}
		
		System.err.println(s);

		return false;
	}

}
