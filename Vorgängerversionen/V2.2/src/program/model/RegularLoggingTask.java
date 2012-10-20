package program.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import program.data.MulticastData;
import program.interfaces.MulticastThreadSuper;

public class RegularLoggingTask extends TimerTask {
	private Logger logger;
	private Map<MulticastData, MulticastThreadSuper> mc_sender_v4;
//	private Map<MulticastData, MulticastThreadSuper> mc_sender_v6;
	private Map<MulticastData, MulticastThreadSuper> mc_receiver_v4;
//	private Map<MulticastData, MulticastThreadSuper> mc_receiver_v6;

	public RegularLoggingTask(Logger logger,
			Map<MulticastData, MulticastThreadSuper> mcSenderV4,
//			Map<MulticastData, MulticastThreadSuper> mcSenderV6,
			Map<MulticastData, MulticastThreadSuper> mcReceiverV4
//			,	Map<MulticastData, MulticastThreadSuper> mcReceiverV6
			) {
		super();
		this.logger = logger;
		mc_sender_v4 = mcSenderV4;
//		mc_sender_v6 = mcSenderV6;
		mc_receiver_v4 = mcReceiverV4;
//		mc_receiver_v6 = mcReceiverV6;
	}

	@Override
	public void run() {
		MulticastThreadSuper value = null;
		Map<MulticastData, MulticastThreadSuper> v = null;
		for(int i=0; i<4; i++,i++){
			switch(i){
				case 0: v = mc_sender_v4; break;
//				case 1: v = mc_sender_v6; break;
				case 2: v = mc_receiver_v4; break;
//				case 3: v = mc_receiver_v6; break;
			}
			for(Entry<MulticastData, MulticastThreadSuper> m : v.entrySet()){
				value = m.getValue();
				if(value.getMultiCastData().isActive()){
					value.updateMin();
					logger.log(Level.FINEST, value.getMultiCastData().toStringConsole());
				}
			}
		}
	}

}
