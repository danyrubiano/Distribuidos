package cl.usach.sd;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.util.IncrementalStats;

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
			System.err.println("\tNodeID: "+((ExampleNode) Network.get(i)).getID()+", Publisher Topics List "+((ExampleNode) Network.get(i)).getTopicsPublisherList()+", Suscriber Topics List "+((ExampleNode) Network.get(i)).getTopicsSubscriberList()+", Topic Publishers List "+((ExampleNode) Network.get(i)).getPublishersTopicList()+", Topic Subscribers List "+((ExampleNode) Network.get(i)).getSubscribersTopicList());
		}
		
		System.err.println(s);

		return false;
	}

}
