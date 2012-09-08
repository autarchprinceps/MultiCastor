   
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
 
  

import java.util.logging.Level;

import org.jnetpcap.Pcap;

import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.model.MacAddress;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;
import dhbw.multicastor.program.model.mmrp.IMmrpSenderAndReceiver;
import dhbw.multicastor.program.model.mmrp.MMRP;



public class MMRPLocalControllerReceiver extends MMRPLocalController {
	private MMRPData data;
	public MMRPLocalControllerReceiver( Pcap pcap, IMmrpSenderAndReceiver mc, MMRPData data) {
		super( pcap, mc, data);
		this.data = data;
	}


	@Override
	protected void handleLeaveEvents() {
		int event = 0;
		//sendet fuer aktiven Strom JoinIn, fuer inaktiven JoinEmpty-Event
		if(data.getActive()){
			event = MMRP.JOIN_IN;
		} else {
			event = MMRP.JOIN_MT;
		}
		createPacket(event,0);	
	}

	@Override
	protected void analyseMessage(int event) {
			if (event == MMRP.LV) {
				handleLeaveEvents();
				logger.log(Level.FINEST, "MMRP-Packet received \t Multicast-Address: "+new MacAddress(mc.getGroupAddress())+"\t Event: Leave \t ", Event.MMRP );
			} else {
				if (event == MMRP.MT) {
					handleLeaveEvents();
					logger.log(Level.FINEST, "MMRP-Packet received \t Multicast-Address: "+new MacAddress(mc.getGroupAddress())+"\t Event: Mt \t ", Event.MMRP );
				}
			}
	}
}
