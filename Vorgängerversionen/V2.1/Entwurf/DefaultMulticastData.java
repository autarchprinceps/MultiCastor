   
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
 
  

import java.awt.font.ImageGraphicAttribute;

import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.model.InputValidator;
import dhbw.multicastor.program.model.MacAddress;


public class DefaultMulticastData {
	
	private static IgmpMldData igmp = new IgmpMldData(ProtocolType.IGMP);
	private static MMRPData mmrp = new MMRPData(ProtocolType.MMRP);
	private static IgmpMldData mld = new IgmpMldData(ProtocolType.MLD);
	
	private DefaultMulticastData(){
		initIgmp();
		initMmrp();
		initMld();
	}
	
	private void initMld() {
		igmp.setGroupIp(InputValidator.checkMC_IPv6("ff01:0:0:0:0:0:0:1"));
		igmp.setUdpPort(4711);
		igmp.setPacketLength(2048);
		igmp.setPacketRateDesired(50);
		igmp.setTtl(32);
	}

	private void initMmrp() {
		mmrp.setMacGroupId(InputValidator.checkMAC("01:80:C2:00:00:FF"));
		mmrp.setPacketLength(2048);
		mmrp.setPacketRateDesired(50);
		
	}

	private void initIgmp() {
		igmp.setGroupIp(InputValidator.checkMC_IPv4("230.0.0.1"));
		igmp.setUdpPort(4711);
		igmp.setPacketLength(2048);
		igmp.setPacketRateDesired(50);
		igmp.setTtl(32);
		
	}

	public static IgmpMldData getIgmp() {
		return igmp;
	}
	public static void setIgmp(IgmpMldData igmp) {
		DefaultMulticastData.igmp = igmp;
	}
	public static MMRPData getMmrp() {
		return mmrp;
	}
	public static void setMmrp(MMRPData mmrp) {
		DefaultMulticastData.mmrp = mmrp;
	}
	public static IgmpMldData getMld() {
		return mld;
	}
	public static void setMld(IgmpMldData mld) {
		DefaultMulticastData.mld = mld;
	}
}
