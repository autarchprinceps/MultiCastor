   
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
 
 package dhbw.multicastor.program.model.mmrp.consumer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JBuffer;

import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;
import dhbw.multicastor.program.model.mmrp.Attribute;
import dhbw.multicastor.program.model.mmrp.IMmrpSenderAndReceiver;
import dhbw.multicastor.program.model.mmrp.MMRP;
import dhbw.multicastor.program.model.mmrp.MMRPPacketCreator;


public abstract class MMRPLocalController implements Runnable {
	private Queue<Attribute> incomingMmrpPacketQueue = new ConcurrentLinkedQueue<Attribute>();
	Pcap pcap;
	MMRP mmrp;
	MMRPLocalController neighbour;
	IMmrpSenderAndReceiver mc;
	MMRPPacketCreator creator;
	private Thread t;
	protected static Logger logger = Logger
			.getLogger("dhbw.multicastor.program.controller.main");

	MMRPLocalController(Pcap pcap, IMmrpSenderAndReceiver mc, MMRPData data) {
		this.mmrp = new MMRP();
		this.mc = mc;
		this.creator = new MMRPPacketCreator(mc);
		this.pcap = pcap;
	}

	/**
	 * Analysiert die einzelnen Messages. Reagiert auf entsprechende Events.
	 * 
	 * @param att
	 */
	protected abstract void analyseMessage(int event) ;

	/**
	 * Analysiert die MMRP-Packete. Reagiert auf entsprechende Events.
	 */
	protected void handleMmrpPacket() {
		Attribute att = incomingMmrpPacketQueue.poll();
		//prueft, ob noch events, da sind
		int event;
		event = att.getMyEvent(mc.getGroupAddressNum());
		if(event == -1){
			return;
		}
		notifyNeighbour(att);
			if (att.getLeaveAll() == 0) {
				analyseMessage(event);
			} else {
				handleLeaveEvents();
			}
	}

	public Queue<Attribute> getIncomingPacketQueue() {
		return incomingMmrpPacketQueue;
	}

	/**
	 * Verhalten beim Erhalten vom LeaveAll-Event oder Leave-Event.
	 */
	protected abstract void handleLeaveEvents();


	/**
	 * Prï¿½ft, ob neue Packete angekommen sind
	 */
	@Override
	public void run() {
		while (mc.isAlive()) {
			if (incomingMmrpPacketQueue.isEmpty()) {
				synchronized (incomingMmrpPacketQueue) {
					try {
						incomingMmrpPacketQueue.wait();
					} catch (InterruptedException e) {
						logger.log(Level.FINEST, "InterruptedException in Consumer-run()-method");
					}
				}
			} else {
				handleMmrpPacket();
			}
		}
	}

	public void setIncomingPacketQueue(Queue<Attribute> incomingPacketQueue) {
		this.incomingMmrpPacketQueue = incomingPacketQueue;
	}

	public synchronized void createPacket(int event, int leaveAll){
		sendPacket(creator.createNewPacket(leaveAll, event));

		String stringEvent = "";
		switch(event){
		case MMRP.LV: 	stringEvent = "Leave";
		break;
		case MMRP.MT: 	stringEvent = "Mt";
		break;
		case MMRP.JOIN_IN: stringEvent = "JoinIn";
		break;
		case MMRP.JOIN_MT: stringEvent = "JoinMt";
		break;
		case MMRP.NEW: 	stringEvent = "New";
		break;
		case MMRP.IN: stringEvent = "In";
		break;
		}
		
		if(leaveAll == 1){
			logger.log(Level.FINEST, "MMRP-Packet sent Event: "+stringEvent+" + LeaveAll\t ", Event.MMRP );
		}else{
			logger.log(Level.FINEST, "MMRP-Packet sent Event: "+stringEvent+"\t ", Event.MMRP );
		}

	}

	public synchronized void sendPacket(JBuffer paketToBeSended) {
			if (pcap.sendPacket(paketToBeSended) != Pcap.OK) {
				System.err.println(pcap.getErr());
		}
	}
	
	private void notifyNeighbour(Attribute att) {
		if(neighbour == null){
			return;
		}
		synchronized (neighbour) {
			Queue<Attribute> mmrpQueue = neighbour.getIncomingPacketQueue();
			synchronized (mmrpQueue) {
				mmrpQueue.add(att);
				mmrpQueue.notify();
			}
		}
	}
	
	public void setThread(Thread t) {
		this.t = t;
	}

	protected Thread getThread() {
		return this.t;
	}

	public void addNeighbour(MMRPLocalController newNeighbour) {
		this.neighbour = newNeighbour;
	}
	
	public void unconnect(){
		this.neighbour = null;
	}

}
