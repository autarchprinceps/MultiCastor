   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenströmen. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, 
 *	weitergeben und/oder modifizieren, gemäß Version 3 der Lizenz.
 *
 *  Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 *  Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 ****************************************************************************************************************************************************************
 *  MultiCastor is a Tool for sending and receiving of Multicast-Data Streams. This project was created for the subject "Software Engineering" at 
 *	Dualen Hochschule Stuttgart under the direction of Markus Rentschler and Andreas Stuckert.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; 
 *  either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
 
 package dhbw.multicastor.program.model;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import dhbw.multicastor.program.controller.CLIController;
import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.interfaces.MulticastThreadSuper;
import dhbw.multicastor.program.interfaces.UIController;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;


public class UpdateTask extends TimerTask{
	private ViewController viewController;
	private CLIController cliController;
	private Logger logger;
	private Map<MulticastData, MulticastThreadSuper> mc_data;
	
	public UpdateTask(Logger logger, Map<MulticastData, MulticastThreadSuper> mc_data, ViewController viewController, CLIController cliController){
		super();
		this.logger = logger;
		this.mc_data = mc_data;
		this.viewController = viewController;
		this.cliController = cliController;
	}
	
	public UpdateTask(Logger logger, Map<MulticastData, MulticastThreadSuper> mc_data, UIController uiController){
		super();
		this.logger = logger;
		this.mc_data = mc_data;
		if(uiController instanceof ViewController){
			this.viewController = (ViewController) uiController;
		} else if(uiController instanceof CLIController){
			this.cliController = (CLIController) uiController;
		}
	}
	
	@Override
	public void run() {
		
		long time1 = System.nanoTime();
		updateEntries(mc_data);
		if(viewController != null){
			if(viewController.isInitFinished()){
				viewController.viewUpdate();
			}		
		} else if(cliController != null){
			if(cliController.isOneMulticast()){
				cliController.updateCli();	
			}
		}
		if(((System.nanoTime() - time1)/1000000)>200){
			//System.out.println("Updatetime is rather long: " + ((System.nanoTime() - time1)/1000000) + " ms !!!!!!!!!!!!");
			logger.log(Level.INFO,"Updatetime is rather long: " + ((System.nanoTime() - time1)/1000000) + " ms !!!!!!!!!!!!", Event.INFO);
			if(((System.nanoTime() - time1)/1000000)>300)
				if(viewController!=null)
					logger.log(Level.WARNING, "Updating the user interface takes very long. Consider the help for more information.", Event.INFO);
		}
			
	}
	
	public void updateEntries(Map<MulticastData, MulticastThreadSuper> mc){
		MulticastThreadSuper value = null;
		for(Entry<MulticastData, MulticastThreadSuper> m : mc.entrySet()){
			value = m.getValue();
			if(value != null){
				if(value.getMultiCastData().getActive()){
					value.update();
				}
			}			
		}
	}
	
}
