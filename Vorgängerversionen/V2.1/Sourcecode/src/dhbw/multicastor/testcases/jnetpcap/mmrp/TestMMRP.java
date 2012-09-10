   
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.junit.Before;
import org.junit.Test;

import dhbw.multicastor.program.controller.MulticastController;
import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.model.MacAddress;


public class TestMMRP {
	public static final String[] adresses = {
											"01:20:30:40:50:60",
											"01:20:30:40:50:61",
											"01:20:30:40:50:62",
											"01:20:30:40:50:63",
											"01:20:30:40:50:64",
											"01:20:30:40:50:65",
											"01:20:30:40:50:66",
											};
	public static final int[] packetRate = {
											10,
											50,
											100,
											500,
											1000,
											5000,
											10000,
											65000							
											};
	public final int[] packetLength = {
										52,
										100,
										500,
										1000,
										1500
										};
	MulticastController mc;
	byte[] myAddress;
	@Before
	public void setUp(){
		mc = new MulticastController();
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
		StringBuilder errbuf = new StringBuilder(); // For any error msgs   
		int r = Pcap.findAllDevs(alldevs, errbuf);  
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());  
			return;  
		}  
		
		PcapIf device = alldevs.get(4); 	
		System.out  
		.printf("\nChoosing '%s' on your behalf:\n",  
				(device.getDescription() != null) ? device.getDescription()  
						: device.getName());  
		try {
			myAddress = device.getHardwareAddress();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private MMRPData initMulticastData(String ad,int pl, int pr){
		MMRPData data = new MMRPData(ProtocolType.MMRP);
		data.setHostID("Roman_Host");
		data.setMacGroupId(new MacAddress((new MacAddressForTests(ad)).getMacAddress()));
		data.setMacSourceId(new MacAddress((new MacAddressForTests(myAddress)).getMacAddress()));
		data.setPacketLength(pl);
		data.setPacketRateDesired(pr);
		data.setActive(true);
		return data;
	}
	
	@Test
	public void testSender(){
		MMRPData data = initMulticastData(adresses[0], packetLength[1],packetRate[2]);
		mc.setCurrentModus(Modus.SENDER);
		mc.addMC(data);
		Wait.manySec(3);
		System.out.println(data.toString());
	}
	
	@Test
	public void testReceiver(){
		MMRPData data = initMulticastData(adresses[0], packetLength[1],packetRate[2]);
		mc.setCurrentModus(Modus.RECEIVER);
		mc.addMC(data);
		Wait.manySec(3);
		System.out.println(data.toString());
	}
	
	

}
