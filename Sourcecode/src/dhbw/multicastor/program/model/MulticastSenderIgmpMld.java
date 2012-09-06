   
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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.logging.Logger;

import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;


public class MulticastSenderIgmpMld extends MulticastSender{
	
	private MulticastSocket mcSocket;
	private InetAddress   	mcGroupIp;
	private InetAddress		sourceIp;
	private int 		  	udpPort;
	private byte 			ttl;

	
	/**
	 * Einziger Konstruktor der Klasse (Sieht man vom Konstruktor der Superklasse ab).
	 * Im Konstruktor wird die hostID gesetzt (entspricht dem hostnamen des Gerï¿½ts),
	 * der MultiCastSocket initialisiert, das Network-Interface
	 * ï¿½ber das die Multicasts gesendet werden sollen gesetzt und das Datenpaket
	 * mit dem {@link PacketBuilder} erstellt.
	 * @param mcBean Das {@link MulticastData}-Object, dass alle fï¿½r den Betrieb nï¿½tigen Daten enthï¿½lt.
	 * @param _messages Eine {@link Queue}, ï¿½ber den der Sender seine Ausgaben an den Controller weitergibt.
	 */
	public MulticastSenderIgmpMld(MulticastData mcBean, Logger _logger) {
		super(mcBean, _logger);
		
		//Den hostName setzen
		try{
			mcData.setHostID(InetAddress.getLocalHost().getHostName());
		}catch(UnknownHostException e){
			proclaim(1, Event.ERROR, "Unable to get Host-Name. Error is: " + e.getMessage());
		}
		
		//ï¿½brige Variablen initialisieren
		messages				= _logger;
		myPacketBuilder 		= new PacketBuilder(mcData);
		if(mcData.getProtocolType().equals(ProtocolType.IGMP) || mcData.getProtocolType().equals(ProtocolType.MLD)){
			udpPort					= ((IgmpMldData)mcData).getUdpPort();
			ttl						= (byte) ((IgmpMldData)mcData).getTtl();
			mcGroupIp 				= ((IgmpMldData)mcData).getGroupIp();		
			sourceIp  				= ((IgmpMldData)mcData).getSourceIp();
		}
		
		packetRateDes			= mcData.getPacketRateDesired();
		totalPacketCount = mcData.getPacketCount();
		
		
		//einen MultiCastSocket initiieren
		try{
			mcSocket 			= new MulticastSocket(udpPort);			
		}catch(IOException e){
			proclaim(1, Event.ERROR, "'" + e.getMessage() + "': Try to set default Port 4711...");
			
			try{
				udpPort = 4711;	//Default-Port
				((IgmpMldData)mcData).setUdpPort(udpPort);
				mcSocket = new MulticastSocket(udpPort);
			}catch(IOException e2){
				proclaim(1, Event.ERROR, "While Setting UDP-Port: " + e.getMessage());
				proclaim(-1, Event.ERROR,"FATAL ERROR: DEFAULT-PORT IS NOT ASSIGNABLE");
				return;
			}
		}
		
		//Network-Adapter setzen (Source-IP)
		try{
			mcSocket.setInterface(sourceIp);
		}catch(IOException e){
			proclaim(1, Event.ERROR,"While setting the Source IP "  + sourceIp + ": " + e.getMessage());
		} 
		
		//Lï¿½nge der Pause zwischen den Multicasts in Millisekunden und Nanusekunden setzen
		pausePeriodMs = (int)  (1000.0 / (double)mcData.getPacketRateDesired());	//Pause in Milisekunden
		pausePeriodNs = (int)(((1000.0 / (double)mcData.getPacketRateDesired())-(double)pausePeriodMs)*1000000.0);	//"Rest"-Pausenzeit(alles kleiner als 1ms) in ns
	}
	
	
	public void run() {
		//Paketzï¿½hler auf 0 setzen
		resetablePcktCnt			= 0;
		cumulatedResetablePcktCnt	= 0;
		
		//Der Multicastgruppe beitreten
		try{
			mcSocket.joinGroup(mcGroupIp);
			proclaim(2, Event.JOINED,((IgmpMldData)mcData).getGroupIp() + " as sender using method " + usedMethod + ".");
		}catch(IOException e){
			proclaim(1, Event.ERROR, "Was not able to join multicast-group " + ((IgmpMldData)mcData).getGroupIp() + ": " + e.getMessage());
			//ï¿½berspringen der Sende-Whileschleife
			setSending(false);
			mcData.setActive(false);
		}
		
		//TTL setzen
		try{
			mcSocket.setTimeToLive(ttl);
		}catch(IOException e){
			proclaim(1, Event.ERROR, "error setting TTL to " + ttl);
		}
		
		//Multicasts zum resetten des Receivers Senden
		//zur Sicherheit ein paar mehr als eins...
		myPacketBuilder.setReset(true);
		try{
			for(int i=0;i<3;i++)	mcSocket.send(new DatagramPacket(myPacketBuilder.getPacket(),
																	 mcData.getPacketLength(),
																	 mcGroupIp,
																	 udpPort));
		}catch(IOException e){
			proclaim(1, Event.ERROR, "While sending reset Packets: " + e.getMessage());
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
						while(isSending()){
							try{
								mcSocket.send(new DatagramPacket(myPacketBuilder.getPacket(),
																 mcData.getPacketLength(),
																 mcGroupIp,
																 udpPort)
										      );
								if(totalPacketCount<Long.MAX_VALUE)	totalPacketCount++;
								else						totalPacketCount = 0;
								resetablePcktCnt++;
								
							}catch(IOException e1){
								proclaim(1, Event.ERROR, "While sending: " + e1.getMessage());
							}
						}
						break;
			case PEAK:	//Misst wie lange er sendt um die Paketrate zu erhalten
						//und sleept den Rest der Sekunde
						long endTime	= 0,
							 timeLeft	= 0;
						while(isSending()){
							//Sleep wenn noch etwas von der letzten Sekunde ï¿½brig
							timeLeft = endTime-System.nanoTime();
							if(timeLeft>0)	try{
												Thread.sleep(timeLeft/1000000, (int) (timeLeft%1000000));
											}catch(InterruptedException e){
												proclaim(1, Event.ERROR, "Sleep after sending with method PEAK failed: " + e.getMessage());
											}
							endTime	  = System.nanoTime() + 1000000000;	//Plus 1s (in ns)
							do{
								try{
									mcSocket.send(new DatagramPacket(myPacketBuilder.getPacket(),
																	 mcData.getPacketLength(),
																	 mcGroupIp,
																	 udpPort)
											      );
									if(totalPacketCount<Long.MAX_VALUE)	totalPacketCount++;
									else						totalPacketCount = 0;
									resetablePcktCnt++;
								}catch(IOException e1){
									proclaim(1, Event.ERROR, "While sending: " + e1.getMessage());
								}
							}while( ((totalPacketCount%packetRateDes)!=0) && isSending());
						}
						break;
			case SLEEP_MULTIPLE:	//mit Thread.sleep zwischen jedem Senden
			default:
						while(isSending()){
							try{
								mcSocket.send(new DatagramPacket(myPacketBuilder.getPacket(),
																 mcData.getPacketLength(),
																 mcGroupIp,
																 udpPort)
										      );
								if(totalPacketCount<Long.MAX_VALUE)	totalPacketCount++;
								else						totalPacketCount = 0;
								resetablePcktCnt++;
								Thread.sleep(pausePeriodMs,pausePeriodNs);
							}catch(IOException e1){
								proclaim(1, Event.ERROR, "While sending: " + e1.getMessage());
							}catch(InterruptedException e2){
								proclaim(1, Event.ERROR, "While sending: (sleep fails): " + e2.getMessage());
							}
						}
		}
		
		//MulticastGruppe verlassen, da Senden von Multicast (im Moment) beendet
		try{
			mcSocket.leaveGroup(mcGroupIp);
		}catch(IOException e){
			proclaim(1, Event.ERROR, e.getMessage());
		}
		proclaim(2, Event.DEACTIVATED, totalPacketCount + " packets send in total");
		//Counter reseten
		totalPacketCount = mcData.getPacketCount();
		resetablePcktCnt = 0;
		cumulatedResetablePcktCnt = 0;
		update();
		updateMin();
		setStillRunning(false);
	}

}
