   
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
 
 package dhbw.multicastor.testcases.mmrpsender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;
import org.jnetpcap.PcapSockAddr;

public class StartSenderAndReceiver {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		Pcap pcap = setUpPcap();

		//new Thread(new PacketReceiver(pcap)).start();
		new Thread(new PacketSender(pcap)).start();

	}


	private static Pcap setUpPcap() throws IOException {
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
		StringBuilder errbuf = new StringBuilder(); // For any error msgs  

		/*************************************************************************** 
		 * First get a list of devices on this system 
		 **************************************************************************/  
		int r = Pcap.findAllDevs(alldevs, errbuf);  

		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());  
			System.exit(0);
		}  

		PcapIf device = new PcapIf();

		for(int i=0; i<alldevs.size(); i++)
		{
			device = alldevs.get(i);
			List<PcapAddr> adresses = device.getAddresses();
			PcapSockAddr addr = adresses.get(0).getAddr();
			
			if((addr.getFamily()==2) && ((addr.getData()[0]!=0) || 
					(addr.getData()[1]!=0) || (addr.getData()[2]!=0) || 
					(addr.getData()[3]!=0))){
				break;
			}
		}


		System.out  
		.printf("\nChoosing '%s' on your behalf:\n",  
				(device.getDescription() != null) ? device.getDescription()  
						: device.getName());  

		/***************************************** 
		 * Second we open a network interface 
		 *****************************************/  
		int snaplen = 64 * 1024; // Capture all packets, no trucation  
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
		int timeout = 10 * 1000; // 10 seconds in millis  
		System.out.println("Open pcap...");
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

		return pcap;
	}

}
