   
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
 
 package dhbw.multicastor.testcases.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import org.junit.Test;

import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.model.MulticastSender;
import dhbw.multicastor.program.model.MulticastSenderIgmpMld;
import dhbw.multicastor.program.model.MulticastSender.sendingMethod;




public class MultiCastSenderTC {
	
	InetAddress 	groupIp,
					sourceIp;
	boolean			active				= false;
	int		 		udpPort	 			= 4711,
					packetLength		= 2222,
					ttl					= 1,
					packetRateDesired	= 2000,
					packetRateMeasured	= 100,
					threadID			= 1,
					numberOfInt			= 0,
					averageIntTime		= 0,
					packetLossPerSecond = 0,
					jitter				= 0;
	ProtocolType				typ					= ProtocolType.IGMP;
	
private void setupIPv4(){
		
		//Werte der MultiCastData-Bean "myBean"
		try{
			groupIp				= InetAddress.getByName("224.1.2.3");
			sourceIp 			= InetAddress.getByName("99.168.232.1");
		}catch(UnknownHostException e){
			System.out.println("Es ist ein Fehler beim Setzen der IPs aufgetreten: " + e.getMessage());
			groupIp = null;
			sourceIp = null;
		}
		String hostID		= "hallo";//sourceIp.getHostName();
		active				= false;
		udpPort	 			= 4711;
		packetLength		= 65528;
		ttl					= 1;
		packetRateDesired	= 10000;
		packetRateMeasured	= 100;
		threadID			= 1;
		numberOfInt			= 0;
		averageIntTime		= 0;
		packetLossPerSecond = 0;
		jitter				= 0;
		typ					= ProtocolType.IGMP;
	}
	
	private void setupIPv6(){
		
		//Werte der MultiCastData-Bean "myBean"
		try{
			groupIp				= InetAddress.getByName("fe80::3471:23ca:f5ba:3d28");
			sourceIp 			= InetAddress.getByName("fe80::3471:23ca:f5ba:3d28");
		}catch(UnknownHostException e){
			System.out.println("Es ist ein Fehler beim Setzen der IPs aufgetreten: " + e.getMessage());
			groupIp = null;
			sourceIp = null;
		}
		String hostID		= sourceIp.getHostName();
		active				= false;
		udpPort	 			= 4711;
		packetLength		= 100;
		ttl					= 1;
		packetRateDesired	= 100000000;
		packetRateMeasured	= 100;
		threadID			= 1;
		numberOfInt			= 0;
		averageIntTime		= 0;
		packetLossPerSecond = 0;
		jitter				= 0;
		typ					= ProtocolType.MLD;
	}
	
	public void testLauf(sendingMethod sm) {		
		
		MulticastSender myMcSender;
			
		MulticastData myBean = new IgmpMldData(groupIp, sourceIp, udpPort, packetLength, ttl, packetRateDesired, active, typ);
		
		Logger logger = Logger.getLogger("dhbw.multicastor.testcases.model");
			myMcSender = new MulticastSenderIgmpMld(myBean, logger);

		Thread myThread = new Thread(myMcSender);
		myMcSender.setActive(true, sm);
		myThread.start();
		
		try{
			Thread.sleep(1000);
			myMcSender.setActive(false);
		}catch(Exception e){
			System.out.println("lalalaa..." + e.getMessage());
		}
	}
	
	@Test
	public void senderTest(){
		System.out.println("+++++++++++++++1. Durchlauf mit IPv4++++++++++++++++");
		setupIPv4();
		testLauf(sendingMethod.SLEEP_MULTIPLE);
		testLauf(sendingMethod.PEAK);
		testLauf(sendingMethod.NO_SLEEP);
		//System.out.println("+++++++++++++++2. Durchlauf mit IPv6++++++++++++++++");
		//setupIPv6();
		//testLauf();
	}
}
