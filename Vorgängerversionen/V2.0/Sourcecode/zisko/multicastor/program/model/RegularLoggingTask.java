package zisko.multicastor.program.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.interfaces.MulticastThreadSuper;

/**
 * RegularLoggingTask ist ein Timer, der in regelmaessigen Abstaenden die Sender und Empfaenger auf der Konsole ausgibt.
 *
 */
public class RegularLoggingTask extends TimerTask {
	private Logger logger;
	private Map<MulticastData, MulticastThreadSuper> mc_sender_l3;
	private Map<MulticastData, MulticastThreadSuper> mc_receiver_l3;
	
	/**
	 * 
	 * @param logger Logger der fuer die Ausgabe verwendet wird
	 * @param mcSenderL3 Multicast Sender Map mit den Sender Daten
	 * @param mcReceiverL3 Multicast Empfaenger Map mit den Empfaenger Daten
	 */
	public RegularLoggingTask(Logger logger,
			Map<MulticastData, MulticastThreadSuper> mcSenderL3,
			Map<MulticastData, MulticastThreadSuper> mcReceiverL3) {
		super();
		this.logger = logger;
		mc_sender_l3 = mcSenderL3;
		mc_receiver_l3 = mcReceiverL3;
	}

	@Override
	public void run() {
		MulticastThreadSuper value = null;
		Map<MulticastData, MulticastThreadSuper> v = null;
		for(int i=0; i<2; i++){
			switch(i){
				case 0: v = mc_sender_l3; break;
				case 1: v = mc_receiver_l3; break;
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
