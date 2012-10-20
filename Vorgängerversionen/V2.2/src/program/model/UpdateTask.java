package program.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.logging.Logger;

import program.controller.ViewController;
import program.data.MulticastData;
import program.interfaces.MulticastThreadSuper;

public class UpdateTask extends TimerTask{
	private ViewController viewController;
	private Map<MulticastData, MulticastThreadSuper> mc_sender;
	private Map<MulticastData, MulticastThreadSuper> mc_receiver;
	
	public UpdateTask(Logger logger,
			Map<MulticastData, MulticastThreadSuper> mcSender,
			Map<MulticastData, MulticastThreadSuper> mcReceiver,
			ViewController viewController) {
		super();
		mc_sender = mcSender;
		mc_receiver = mcReceiver;
		this.viewController = viewController;
	}

	@Override
	public void run() {
		MulticastThreadSuper value = null;
		Map<MulticastData, MulticastThreadSuper> v = null;
		for(int i=0; i<4; i++,i++){
			switch(i){
				case 0: v = mc_sender; break;
				case 2: v = mc_receiver; break;
			}
			for(Entry<MulticastData, MulticastThreadSuper> m : v.entrySet()){
				value = m.getValue();
				if(value==null){
					System.out.println("DEBUG: "+ m); //$NON-NLS-1$
				}
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

	}

}
