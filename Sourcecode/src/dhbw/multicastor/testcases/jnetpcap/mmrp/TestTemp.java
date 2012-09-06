   
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
 
 package dhbw.multicastor.testcases.jnetpcap.mmrp;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JMemoryPacket;

import dhbw.multicastor.program.controller.MulticastController;
import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.model.MacAddress;
import dhbw.multicastor.program.model.mmrp.IMmrpSenderAndReceiver;
import dhbw.multicastor.program.model.mmrp.MMRP;
import dhbw.multicastor.program.model.mmrp.MMRPPacketCreator;
import dhbw.multicastor.program.model.mmrp.consumer.MMRPLocalControllerSender;


public class TestTemp {
	public static final String[] adresses = {
											"01:90:C2:00:00:F0",
											"01:90:C3:00:00:F1",
											"01:34:56:78:9A:BC",
											"01:34:56:78:9A:AA",
											"01:34:56:78:9A:AB",
											"01:34:56:78:9A:AC",
											"01:34:56:78:9A:AD",
											"01:34:56:78:9A:AE",
											};
	public static final int[] packetRate = {
											1,
											10,
											50,
											100,
											500,
											1000,
											5000,
											10000,
											65000							
											};
	public static final int[] packetLength = {
												52,
												100,
												500,
												1000,
												1500
												};
	MulticastController mc;
	static byte[] myAddress;
	static Pcap pcap;
	static MMRPPacketCreator creator;
	
	public TestTemp(){
		this.mc = new MulticastController();
	}
	

	public static void setUp(){
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
		StringBuilder errbuf = new StringBuilder(); // For any error msgs   
		int r = Pcap.findAllDevs(alldevs, errbuf);  
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());  
			return;  
		} 

		PcapIf device = null;
		for (PcapIf device1 : alldevs) {
			if (device1.getDescription() != null) {
				if (Pattern.matches(".*eth0.*", device1.getDescription())) {
					device = device1;
					System.out.println(device1.getDescription());
					break;
				}
			}
				if (device1.getName() != null) {
					if (Pattern.matches(".*eth0.*", device1.getName())) {
						device = device1;
						System.out.println(device1.getName());
						break;
					}
				}
			

		}
		
		 	
		System.out.printf("\nChoosing '%s' on your behalf:\n" ,(device.getDescription() != null) ? device.getDescription()  
				: device.getName());  
		try {
			myAddress = device.getHardwareAddress();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int snaplen = 64 * 1024; // Capture all packets, no trucation  
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
		int timeout = 10; // 10 seconds in millis  

				pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
		
	}
	
	private MMRPData initMulticastData(String ad,int pl, int pr){
		MMRPData data = new MMRPData(ProtocolType.MMRP);
		data.setHostID("Roman_Host");
		data.setMacGroupId(new MacAddress((new MacAddressForTests(ad)).getMacAddress()));
		data.setMacSourceId(new MacAddress((new MacAddressForTests(myAddress)).getMacAddress()));
		data.setPacketLength(pl);
		data.setPacketRateDesired(pr);
		data.setActive(false);
		return data;
	}
	

	public void initSender(MMRPData data){
		mc.addMC(data);
		creator = new MMRPPacketCreator((IMmrpSenderAndReceiver)mc.getMcMap().get(data));
		System.out.println(data.toString());
	}
	

	public void initReceiver(MMRPData data){
		mc.addMC(data);
		System.out.println(data.toString());
	}
	
	public static void main(String[] args){
		setUp();
		int leaveAll = 0;
		int eing = -2;
		MacAddress adr = new MacAddress(new MacAddressForTests( adresses[1]).getMacAddress());
		TestTemp receiver = new TestTemp();
		MMRPData data = receiver.initMulticastData(adr.toString(), packetLength[1],packetRate[0]);
		System.out.println("******Daten erstellt!*******");
		
		int event= 0;
		
		
		receiver.mc.setCurrentModus(Modus.RECEIVER);
		Wait.manySec(1);
//		receiver.initReceiver(data);
//		System.out.println("Receiver gestartet!");
		
		//hole mir seine Pcap um darï¿½ber Packete raussenden zu kï¿½nnen.
		//pcap = t.mc.getMmrp_controller().pcaps.get(Arrays.asList(myAdress));

		TestTemp sender = new TestTemp();
		sender.mc.setCurrentModus(Modus.SENDER);
		Wait.manySec(1);
		sender.initSender(data);
		
		System.out.println("Sender initialisiert");
		System.out.println("Warte auf Eingabe:");
		System.out.println("6: aktivieren\n7: deaktivieren");
		
		String inputString = null;
	    BufferedReader bufferedReader = null;
	    bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		while(eing!=-1){
				  try
				    {
				      inputString = bufferedReader.readLine();
				      eing = Integer.parseInt(inputString);
				    }
				    catch (IOException e)
				    {
				      //do nothing
				    }
			switch(eing){
			case 0: {event= MMRP.NEW;System.out.println("New");break;}
			case 1:	{event=MMRP.JOIN_IN;System.out.println("Join_In");break;}
			case 2:	{event=MMRP.IN;System.out.println("IN");break;}
			case 3:	{event=MMRP.JOIN_MT;System.out.println("Join_Mt");break;}
			case 4:	{event=MMRP.MT;System.out.println("Mt");break;}
			case 5:	{event=MMRP.LV;System.out.println("Lv");break;}
			case 6: {
				
				sender.mc.startMC(sender.mc.getMCByIndex(0));
//				((ConsumerMMRPSender)((IMmrpSenderAndReceiver)sender.mc.getMcMap().get(sender.mc.getMCByIndex(0))).getConsumer()).handleJoinIn();
				System.out.println("Sender gestartet!");
				break;
			}
			case 7 : {
				sender.mc.stopMC(sender.mc.getMCByIndex(0));
				System.out.println("Sender gestoppt!");
				break;
			}
			case 8: {
				leaveAll = 1; event = MMRP.NEW; System.out.println("LeaveAllSent");
			}
			default: break;
			}
//			if(event != -1){
//				createPacket(leaveAll, event);
//				event = MMRP.NEW;
//				leaveAll = 0;
////				createPacket(leaveAll, event);
//				event = -1;
////				leaveAll = 0;
//			}			
		}
		
		System.out.println("******Beendet*******");
		System.exit(0);
				
	}
	
	public static void createPacket(int leaveAll, int event){
			sendPacket(creator.createNewPacket(leaveAll,event));
	}
	
	public static synchronized void sendPacket(JBuffer packet) {
		if (pcap.sendPacket(packet) != Pcap.OK) {
			System.err.println(pcap.getErr());
		}

	}
	
	

}
