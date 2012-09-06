   
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
 
 package dhbw.multicastor.program.model.mmrp;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JPacket;

import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.model.MacAddress;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;
import dhbw.multicastor.program.model.MulticastReceiver;
import dhbw.multicastor.program.model.mmrp.consumer.MMRPLocalController;
import dhbw.multicastor.program.model.mmrp.consumer.MMRPLocalControllerReceiver;





public class MulticastReceiverMmrp extends MulticastReceiver implements Runnable, IMmrpSenderAndReceiver{
	private MMRPLocalController consumer;
	private MMRPJoinMtSender sendJoinMt;
 
	private Queue<JPacket> incomingEthernetPacketQueue = new ConcurrentLinkedQueue<JPacket>();
	private MacAddress groupAddress;
	private long groupAddressNum;
	private MacAddress sourceAddress;
	private int event;
	//in ms
	protected Integer timerJoinMt;
	private boolean active_JoinMt;




	private boolean mc_alive;
	
	public MulticastReceiverMmrp(MMRPData mcBean, Logger logger, Pcap pcap) {
		super(mcBean, logger);
		packetAnalyzer = new PacketAnalyzerMmrp(mcBean, logger);
		mc_alive = true;
		groupAddress 				= mcBean.getMacGroupId();		
		sourceAddress  				= mcBean.getMacSourceId();
		groupAddressNum 			= mcBean.getMacGroupIdnum();

		timerJoinMt = mcBean.getTimerJoinMt();
				//mcBean.getTimerJoinMt();
		consumer = new MMRPLocalControllerReceiver(pcap, this, mcBean);		
		sendJoinMt = new MMRPJoinMtSender(this);
		//sendet periodisch ein Event, wenn der Receiver angelegt ist
		if(timerJoinMt > -1){
			sendJoinMt();
		}
		if(timerJoinMt > 0){
			startJoinMt();
		}
		//startet den Kontrollmechanismus ueber MMRP-Consumer
		(new Thread(consumer)).start();
		
	}


	@Override
	public Queue<Attribute> getMmrpQueue() {
		return this.consumer.getIncomingPacketQueue();
	}


	public Queue<JPacket> getEthernetPacketQueue() {
		return this.incomingEthernetPacketQueue;
	}



	@Override
	public byte[] getGroupAddress() {
		return this.groupAddress.getMacAddress();
	}
	
	public void startJoinMt(){
		active_JoinMt = true;
		(new Thread(sendJoinMt)).start();
	}
	
	public void stopJoinMt(){
		active_JoinMt = false;
	}

	
	public boolean isAlive() {
		return mc_alive;
	}


	@Override
	public boolean joinGroup() {
		stopJoinMt();
		event = MMRP.JOIN_IN;
		consumer.createPacket(event,0);
		//gibt immer true zurueck
		return true;
	}
	

	private void sendJoinMt() {
		event = MMRP.JOIN_MT;
		consumer.createPacket(event,0);
	}

	public void leaveGroup() {
		event= MMRP.LV;
		consumer.createPacket(event,0);
		logger.log(Level.INFO, mcData.identify()
				+ ": Left Multicastgroup " + ((MMRPData) mcData).getPacketCount() + " packets received", Event.LEAVE);
	}
	
	public void leaveAll(){
		event = MMRP.LV;
		consumer.createPacket(event,1);
	}
	

	/**
	 * Prï¿½ft, ob neue Packete angekommen sind
	 */
	public void run() {
		while (active) {
			synchronized (incomingEthernetPacketQueue) {
				if (incomingEthernetPacketQueue.isEmpty()) {
					try {
						incomingEthernetPacketQueue.wait();
					} catch (InterruptedException e) {
						logger.log(Level.FINEST, "InterruptedException in MulticastReceiverMmrp-run()-method");
					}
				} else {
					JPacket p = incomingEthernetPacketQueue.poll();
					// Gibt nur die Daten (ohne Ethernet-Header) weiter
					packetAnalyzer.analyzePacket(p.getByteArray(14,
							p.size() - 14));
					if(mcData.getReceivedPackSource()== null){
						mcData.setReceivedPackSource(new MacAddress(p.getByteArray(6,6)));
					}
				}
			}
		}
		// Verlaesst die Multicast Gruppe
		leaveGroup();
		// Resetted gemessene Werte (SenderID bleibt erhalten, wegen des
		// Wiedererkennungswertes)
		packetAnalyzer.resetValues();
		packetAnalyzer.update();
		packetAnalyzer.updateMin();
		//aktiviere JoinMt-Meldung
		if(timerJoinMt > 0){
			startJoinMt();
		}
		// Thread ist beendet
		setStillRunning(false);
	}
	
	

	@Override
	public void setActive(boolean b) {
		super.setActive(b);
		if (!b) {
			notifyQueue();
		}
	}

	public void destroy() {
		long time = System.currentTimeMillis() + 2000;
		while (isStillRunning()) {
			if (time < System.currentTimeMillis()) { 
				logger.log(Level.SEVERE, "Could not delete Multicast "
						+ mcData + " Advised a Change on that Multicast.", Event.WARNING);
				return;
			}
		}
		this.mc_alive = false;
		notifyQueueMmrp();
		this.active_JoinMt = false;
		leaveGroup();
	}

	private void notifyQueueMmrp(){
		synchronized (this.getMmrpQueue()) {
			this.getMmrpQueue().notify();
		}
	}
	
	private void notifyQueue(){
		synchronized (this.getEthernetPacketQueue()) {
			this.getEthernetPacketQueue().notify();
		}
	}
	
	public byte[] getMyAddress(){
		return this.sourceAddress.getMacAddress();
	}

	@Override
	public UUID getMulticastID() {
		return this.mcData.getMulticastID();
	}
	
	public MMRPLocalController getConsumer() {
		return consumer;
	}

	public boolean isActive_JoinMt() {
		return active_JoinMt;
	}



	@Override
	public void setAlive(boolean b) {
		this.mc_alive = b;
	}

	@Override
	public boolean isSender() {
		return false;
	}



	@Override
	public boolean isReceiver() {
		return true;
	}



	@Override
	public void notifyMmrpPacketQueue() {
		synchronized (this.getMmrpQueue()) {
			this.getMmrpQueue().notify();
		}		
	}

	public void notifyEthernetPacketQueue() {
		synchronized (this.getEthernetPacketQueue()) {
			this.getEthernetPacketQueue().notify();
		}		
	}



	@Override
	public long getGroupAddressNum() {
		return groupAddressNum;
	}
}
