package zisko.multicastor.program.model;

import java.util.TimerTask;

import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData.Typ;

/**
 * EasterEgg Snake
 */
public class RunSnakeRun extends TimerTask {
	ViewController view;
	
	public RunSnakeRun(ViewController view){
		this.view = view;
	}
	
	@Override
	public void run() {
		if(view.isInitFinished()){
			if(view.getSelectedTab() != Typ.UNDEFINED && view.getSelectedTab() != Typ.CONFIG){
				view.getPanTabbed(view.getSelectedTab()).getPan_graph().moveSnakeAndUpdateView(view.getSnakeDir());
			}
		}	
	}

}
