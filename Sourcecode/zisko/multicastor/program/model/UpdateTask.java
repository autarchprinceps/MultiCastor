package zisko.multicastor.program.model;

import java.util.Map;
import java.util.TimerTask;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import zisko.multicastor.program.controller.MulticastController;
import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.interfaces.MulticastThreadSuper;

public class UpdateTask extends TimerTask{
	private ViewController viewController;
	private Logger logger;
	private Map<MulticastData, MulticastThreadSuper> mc_sender_v4;
	private Map<MulticastData, MulticastThreadSuper> mc_sender_v6;
	private Map<MulticastData, MulticastThreadSuper> mc_receiver_v4;
	private Map<MulticastData, MulticastThreadSuper> mc_receiver_v6;
	
	public UpdateTask(Logger logger,
			Map<MulticastData, MulticastThreadSuper> mcSenderV4,
			Map<MulticastData, MulticastThreadSuper> mcSenderV6,
			Map<MulticastData, MulticastThreadSuper> mcReceiverV4,
			Map<MulticastData, MulticastThreadSuper> mcReceiverV6,
			ViewController viewController) {
		super();
		this.logger = logger;
		mc_sender_v4 = mcSenderV4;
		mc_sender_v6 = mcSenderV6;
		mc_receiver_v4 = mcReceiverV4;
		mc_receiver_v6 = mcReceiverV6;
		this.viewController = viewController;
	}
	
	@Override
	public void run() {
		MulticastThreadSuper value = null;
		long time1 = System.nanoTime();
		Map<MulticastData, MulticastThreadSuper> v = null;
		for(int i=0; i<4; i++){
			switch(i){
				case 0: v = mc_sender_v4; break;
				case 1: v = mc_sender_v6; break;
				case 2: v = mc_receiver_v4; break;
				case 3: v = mc_receiver_v6; break;
			}
			for(Entry<MulticastData, MulticastThreadSuper> m : v.entrySet()){
				value = m.getValue();
				if(value.getMultiCastData().isActive()){
					value.update();
				}
			}
		}
		if(viewController != null){
			if(viewController.isInitFinished()){
				viewController.viewUpdate();
			}		
		}	
		if(((System.nanoTime() - time1)/1000000)>200){
			//System.out.println("Updatetime is rather long: " + ((System.nanoTime() - time1)/1000000) + " ms !!!!!!!!!!!!");
			logger.log(Level.INFO,"Updatetime is rather long: " + ((System.nanoTime() - time1)/1000000) + " ms !!!!!!!!!!!!");
			if(((System.nanoTime() - time1)/1000000)>300)
				if(viewController!=null)
					logger.log(Level.WARNING, "Updating the user interface takes very long. Consider the help for more information.");
		}
			
	}
	
}
