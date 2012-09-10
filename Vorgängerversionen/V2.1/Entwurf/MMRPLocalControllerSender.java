   
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
import dhbw.multicastor.program.interfaces.MulticastThreadSuper;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;
import dhbw.multicastor.program.model.mmrp.IMmrpSenderAndReceiver;
import dhbw.multicastor.program.model.mmrp.MMRP;
import dhbw.multicastor.program.model.mmrp.MulticastSenderMmrp;



public class MMRPLocalControllerSender extends MMRPLocalController {

	public MMRPLocalControllerSender(Pcap pcap, IMmrpSenderAndReceiver mc, MMRPData data) {
		super(pcap,mc, data);
	}

	@Override
	protected void handleLeaveEvents() {
		((MulticastSenderMmrp) mc).setSending(false);
	}

	/**
	 * Verhalten beim Erhalten von JoinIn-Event.
	 * 
	 */
	protected void handleJoinIn() {
		if (!((MulticastSenderMmrp) mc).isSending()) {
			long time = System.currentTimeMillis() + 2000;
			while (((MulticastThreadSuper) mc).isStillRunning()) {
				if (time < System.currentTimeMillis()) {
					return;
				}
			}
			((MulticastSenderMmrp) mc).setSending(true);
			getThread().start();
		}
	}

	@Override
	protected void analyseMessage(int event) {
			switch (event) {
			case MMRP.LV:
				handleLeaveEvents();
				logger.log(Level.FINEST, "MMRP-Packet received \t Multicast-Address: "+mc.getGroupAddress()+"\t Event: Leave \t ", Event.MMRP );
				break;
			case MMRP.JOIN_IN:
				handleJoinIn();
				logger.log(Level.FINEST, "MMRP-Packet received \t Multicast-Address: "+mc.getGroupAddress()+"\t Event: JoinIn \t ", Event.MMRP );
				break;
			case MMRP.JOIN_MT:
				handleJoinMt();
				logger.log(Level.FINEST, "MMRP-Packet received \t Multicast-Address: "+mc.getGroupAddress()+"\t Event: JoinMt \t ", Event.MMRP );
			default:
				break;
			}
	}

	protected void handleJoinMt() {
			handleJoinIn();
	}

}
