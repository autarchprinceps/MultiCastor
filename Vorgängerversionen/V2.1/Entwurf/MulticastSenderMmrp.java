   
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
 
  

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JMemoryPacket;

import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.model.MacAddress;
import dhbw.multicastor.program.model.MulticastSender;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;
import dhbw.multicastor.program.model.mmrp.consumer.MMRPLocalController;
import dhbw.multicastor.program.model.mmrp.consumer.MMRPLocalControllerSender;



public class MulticastSenderMmrp extends MulticastSender implements IMmrpSenderAndReceiver{




	private MMRPLocalController consumer;
	private int event;
	
	private MacAddress groupAddress;
	private long groupAddressNum;
	private MacAddress sourceAddress;
	private boolean alive;
	private JMemoryPacket packet;



	public MulticastSenderMmrp(MMRPData mcBean, Logger _logger, Pcap pcap) {
		super(mcBean, _logger);
		
		this.alive = true;
		
		
		this.packet = new JMemoryPacket(mcBean.getPacketLength()+14);

		//ï¿½brige Variablen initialisieren
		messages				= _logger;
		//muss ersetzt werden
		myPacketBuilder 		= new EthernetPacketCreator(mcData);
		groupAddress 				= mcBean.getMacGroupId();		
		sourceAddress  				= mcBean.getMacSourceId();
		groupAddressNum 			= mcBean.getMacGroupIdnum();


		packetRateDes			= mcData.getPacketRateDesired();
		
		try {
			mcData.setHostID(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.err.print("It is not possible to get the host id");
		} catch(Exception e){
			//diese Funktion wird nicht unterstuetzt
		}
		
		//startet den Kontrollmechanismus ueber MMRP-Consumer
		this.consumer = new MMRPLocalControllerSender(pcap, this, mcBean);
		//(new Thread(consumer)).start();

		//Lï¿½nge der Pause zwischen den Multicasts in Millisekunden und Nanusekunden setzen
		pausePeriodMs = (int)  (1000.0 / (double)mcData.getPacketRateDesired());	//Pause in Milisekunden
		pausePeriodNs = (int)(((1000.0 / (double)mcData.getPacketRateDesired())-(double)pausePeriodMs)*1000000.0);	//"Rest"-Pausenzeit(alles kleiner als 1ms) in ns
	}


	public void run() {
		//replaced because of change between consumer and sender
		setStillRunning(true);
		
		//Paketzï¿½hler auf 0 setzen
		totalPacketCount			= 0;
		resetablePcktCnt			= 0;
		cumulatedResetablePcktCnt	= 0;


		//Multicasts zum resetten des Receivers Senden
		//zur Sicherheit ein paar mehr als eins...
		myPacketBuilder.setReset(true);
		for(int i=0;i<3;i++){
			sendPacket();
		}
		myPacketBuilder.setReset(false);
		resetablePcktCnt+=3;
		totalPacketCount+=3;

		//So lange senden, bis setActive(false) aufgerufen wird
		//Variable 'usedMethod' bestimmt auf welche Art und Weise.
		//Wurde bei setActive keine Methode angegeben, wird standartmï¿½ï¿½ig die
		//PEAK-Methode verwendet.
		switch(usedMethod){
		case NO_SLEEP:		//ohne sleep, unter Volllast
			while(isSending){
				sendPacket();
				if(totalPacketCount<Long.MAX_VALUE)	totalPacketCount++;
				else						totalPacketCount = 0;
				resetablePcktCnt++;
			}
			break;
		case PEAK:	//Misst wie lange er sendt um die Paketrate zu erhalten
			//und sleept den Rest der Sekunde
			long endTime	= 0,
			timeLeft	= 0;
			while(isSending){
				//Sleep wenn noch etwas von der letzten Sekunde ï¿½brig
				timeLeft = endTime-System.nanoTime();
				if(timeLeft>0)	try{
					Thread.sleep(timeLeft/1000000, (int) (timeLeft%1000000));
				}catch(InterruptedException e){
					proclaim(1, Event.ERROR, "Sleep after sending with method PEAK failed: " + e.getMessage());
				}
				endTime	  = System.nanoTime() + 1000000000;	//Plus 1s (in ns)
				do{
					sendPacket();
					if(totalPacketCount<Long.MAX_VALUE)	totalPacketCount++;
					else						totalPacketCount = 0;
					resetablePcktCnt++;
				}while( ((totalPacketCount%packetRateDes)!=0) && isSending);
			}
			break;
		case SLEEP_MULTIPLE:	//mit Thread.sleep zwischen jedem Senden
		default:
			while(isSending){
				try{
					sendPacket();
					if(totalPacketCount<Long.MAX_VALUE)	totalPacketCount++;
					else						totalPacketCount = 0;
					resetablePcktCnt++;
					Thread.sleep(pausePeriodMs,pausePeriodNs);
				}catch(InterruptedException e2){
					proclaim(1, Event.ERROR, "While sending: (sleep fails): " + e2.getMessage());
				}
			}
		}
		proclaim(2, Event.DEACTIVATED, totalPacketCount + " packets send in total");
		//Counter reseten
		totalPacketCount = 0;
		resetablePcktCnt = 0;
		cumulatedResetablePcktCnt = 0;
		update();
		updateMin();

		consumer.setThread(new Thread(this));
		setStillRunning(false);
	}
	
	private void sendPacket(){
		consumer.sendPacket(createEthernetPacket());
	}
	
	private JMemoryPacket createEthernetPacket(){
		packet.setByteArray(0, myPacketBuilder.getPacket());
		return packet;
	}
	
	public void leaveGroup(){
		event = MMRP.LV;
		consumer.createPacket(event,0);
	}
	
	public void leaveAll(){
		event = MMRP.MT;
		consumer.createPacket(event, 1);
	}

	@Override
	public byte[] getGroupAddress() {
		return this.groupAddress.getMacAddress();
	}
	
	public byte[] getMyAddress(){
		return this.sourceAddress.getMacAddress();
	}
	
	public void destroy() {
		this.alive = false;
		notifyQueueMmrp();
	}

	private void notifyQueueMmrp(){
		synchronized (this.getMmrpQueue()) {
			this.getMmrpQueue().notify();
		}
	}
	
	public boolean isActive(){
		return alive;
	}

	@Override
	public Queue<Attribute> getMmrpQueue() {
		return this.consumer.getIncomingPacketQueue();
	}

	@Override
	public UUID getMulticastID() {
		return this.mcData.getMulticastID();
	}
	
	public boolean isAlive() {
		return alive;
	}


	@Override
	public MMRPLocalController getConsumer() {
		return this.consumer;
	}
	
	@Override
	public void setActive(boolean active) {
		mcData.setActive(active);
		if(active) {
			destroy();
			//Setzen der ThreadID, da diese evtl.
			//im Controller noch einmal geï¿½ndert wird
			myPacketBuilder.alterThreadID(mcData.getThreadID());
			proclaim(2, Event.ACTIVATED, "");
		} else {
			destroy();
			isSending = active;
			proclaim(2, Event.DEACTIVATED, "");
		}
	}


	@Override
	public void setAlive(boolean b) {
		this.alive = b;
	}

	@Override
	public boolean isSender() {
		return true;
	}


	@Override
	public boolean isReceiver() {
		return false;
	}


	@Override
	public void notifyMmrpPacketQueue() {
		synchronized (this.getMmrpQueue()) {
			this.getMmrpQueue().notify();
		}	
	}


	@Override
	public long getGroupAddressNum() {
		return groupAddressNum;
	}

}
