   
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

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.model.ByteTools;
import dhbw.multicastor.program.model.PacketBuilder;



public class PacketBuilderTC{
	private PacketBuilder pb;
	private IgmpMldData myBean;
	
	//nicht verarbeitet, nur für Bean-Konstruktor
	InetAddress 	groupIp,
					sourceIp;	
	boolean			active				= false;
	int		 		udpPort	 			= 4711;

	ProtocolType				typ					= ProtocolType.IGMP;

	//geprüfte Werte
	String			hostID_normal		= "",
					hostID_void			= "",
					hostID_tooLong		= "dasIstEinSehrLangerHostNameDerGekuerztWerdenMuesste";
	int				packetLength_normal	= 100,
					packetLength_null	= 0,			//??? wird vorher geprüft?
					packetLength_short	= 52,			//??? wird vorher geprüft?	
					packetLength_long	= 65575,		//??? wird vorher geprüft?
					ttl_min				= 1,
					ttl_max				= 32,			//???
					packetRateDes_norm	= 100,
					packetRateDes_max	= 65535,
					packetRateDes_min	= 1,
					threadID_normal		= 1,
					threadID_max		= 65535,
					threadID_min		= 0;
	byte[]			checksum			= new byte[2],
					snippetForChecksum  = new byte[42];
	long			timeStamp			= 0,			//Wird erst später gesetzt
					timeP1				= 0,			//timeStamp muss später zwischen timeP1 und timeP2 liegen
					timeP2				= 0;
	
	
	@Test
	public void testGetPacket() {
		//Da IP nicht vom Packetbuilder verarbeitet wird,
		//egal
		try{
			groupIp				= InetAddress.getByName("224.1.2.3");
			sourceIp 			= InetAddress.getByName("192.168.232.1");
		}catch(UnknownHostException e){
			groupIp 			= null;
			sourceIp			= null;
		}
		//Den hostName setzen
		try{
			hostID_normal		= InetAddress.getLocalHost().getHostName();
		}catch(UnknownHostException e){
			fail("UnknownHostException while setting hostID");
		};
		
		byte[] 	mcPacket;
		byte[] 	shortSnippet 	= new byte[2];
		byte[] 	intSnippet		= new byte[4];
		byte[]  longSnippet		= new byte[8];
		byte[] 	snippet 		= new byte[29];
		
		myBean = new IgmpMldData(groupIp, sourceIp, udpPort, packetLength_normal, ttl_min, packetRateDes_norm, active, typ);
		pb = new PacketBuilder(myBean);
 		mcPacket = pb.getPacket();
 		
//Wird ein Paket zurückgegeben?
 		assertTrue(mcPacket!=null);
		
//Länge des Pakets prüfen
 		myBean.setPacketLength(packetLength_normal);
		assertEquals(packetLength_normal, new PacketBuilder(myBean).getPacket().length);
		
		myBean.setPacketLength(packetLength_short);
		assertEquals(packetLength_short, new PacketBuilder(myBean).getPacket().length);
		
		myBean.setPacketLength(packetLength_long);
		assertEquals(packetLength_long, new PacketBuilder(myBean).getPacket().length);
		
//stimmt das Host-ID Feld?
		myBean.setHostID(hostID_normal);
		System.arraycopy(new PacketBuilder(myBean).getPacket(), 0, snippet, 0, 29);
		int eof = 0;
		for(;eof<29&&snippet[eof]!=0;eof++);
		assertEquals(hostID_normal, new String(snippet).substring(0, eof));
		
		myBean.setHostID(hostID_void);
		System.arraycopy(new PacketBuilder(myBean).getPacket(), 0, snippet, 0, 29);
		eof = 0;
		for(;eof<29&&snippet[eof]!=0;eof++);
		assertEquals(hostID_void, new String(snippet).substring(0, eof));
		
		myBean.setHostID(hostID_tooLong);
		System.arraycopy(new PacketBuilder(myBean).getPacket(), 0, snippet, 0, 29);
		eof = 0;
		for(;eof<29&&snippet[eof]!=0;eof++);
		assertEquals(hostID_tooLong.substring(0, 29), new String(snippet).substring(0, eof));
		
//stimmt die Sender-ID (threadID)?
		myBean.setThreadID(threadID_normal);
		System.arraycopy(new PacketBuilder(myBean).getPacket(), 29, shortSnippet, 0, 2);
		assertEquals(threadID_normal, ByteTools.shortByteToInt(shortSnippet));
		
		myBean.setThreadID(threadID_min);
		System.arraycopy(new PacketBuilder(myBean).getPacket(), 29, shortSnippet, 0, 2);
		assertEquals(threadID_min, ByteTools.shortByteToInt(shortSnippet));
		
		myBean.setThreadID(threadID_max);
		System.arraycopy(new PacketBuilder(myBean).getPacket(), 29, shortSnippet, 0, 2);
		assertEquals(threadID_max, ByteTools.shortByteToInt(shortSnippet));
		
//stimmt der packetCount?
		pb = new PacketBuilder(myBean);
		mcPacket = pb.getPacket();
 		
		//Erstes Paket, also ==1
		System.arraycopy(mcPacket, 31, intSnippet, 0, 4);
		assertEquals(1, ByteTools.byteToInt(intSnippet));
		
		for(int i=0;i<499;i++) mcPacket = pb.getPacket();
 		
		//499+1. Paket
		System.arraycopy(mcPacket, 31, intSnippet, 0, 4);
		assertEquals(500, ByteTools.byteToInt(intSnippet));
		
//stimmt die Soll-Paketrate?
		myBean.setPacketRateDesired(packetRateDes_norm);
		System.arraycopy(new PacketBuilder(myBean).getPacket(), 35, shortSnippet, 0, 2);
		assertEquals(packetRateDes_norm, ByteTools.shortByteToInt(shortSnippet));
		
		myBean.setPacketRateDesired(packetRateDes_min);
		System.arraycopy(new PacketBuilder(myBean).getPacket(), 35, shortSnippet, 0, 2);
		assertEquals(packetRateDes_min, ByteTools.shortByteToInt(shortSnippet));
		
		myBean.setPacketRateDesired(packetRateDes_max);
		System.arraycopy(new PacketBuilder(myBean).getPacket(), 35, shortSnippet, 0, 2);
		assertEquals(packetRateDes_max, ByteTools.shortByteToInt(shortSnippet));
		
//stimmt der ttl-Wert?
		myBean.setTtl(ttl_min);
		assertEquals(ttl_min, (int) new PacketBuilder(myBean).getPacket()[37]);
		
		myBean.setTtl(ttl_max);
		assertEquals(ttl_max, (int) new PacketBuilder(myBean).getPacket()[37]);
		
//stimmt der "reset"-Wert? (Bei uns immer "0")
		System.arraycopy(mcPacket, 38, intSnippet, 0, 4);
		assertEquals(0, ByteTools.byteToInt(intSnippet));
		
//checksumme
		System.arraycopy(mcPacket, 42, checksum, 0, 2);
		System.arraycopy(mcPacket, 0, snippetForChecksum, 0, 42);
		assertEquals(checksum[0], ByteTools.crc16(snippetForChecksum)[0]);
		assertEquals(checksum[1], ByteTools.crc16(snippetForChecksum)[1]);
		
//Der Zeitstempel ist ab einem Zufälligen zeitpunkt, deswegen nur möglich zu
//sehen ob er in dem richtigen Zeitraum liegt
		timeP1		= System.nanoTime();
		mcPacket 	= new PacketBuilder(myBean).getPacket();
		timeP2		= System.nanoTime();
		System.arraycopy(mcPacket, 44, longSnippet, 0, 8);
		timeStamp = ByteTools.byteToLong(longSnippet);
		assertTrue(	(timeStamp>timeP1)	&&	(timeStamp<timeP2));
		
	}
	
	@Test
	public void testAlterThreadID(){
		//Da IP nicht vom Packetbuilder verarbeitet wird,
		//egal
		try{
			groupIp				= InetAddress.getByName("224.1.2.3");
			sourceIp 			= InetAddress.getByName("192.168.232.1");
		}catch(UnknownHostException e){
			groupIp 			= null;
			sourceIp			= null;
		}
		//Den hostName setzen
		try{
			hostID_normal		= InetAddress.getLocalHost().getHostName();
		}catch(UnknownHostException e){
			fail("UnknownHostException while setting hostID");
		};
		
		myBean = new IgmpMldData(groupIp, sourceIp, udpPort, packetLength_normal, ttl_min, packetRateDes_norm, active, typ);
		pb = new PacketBuilder(myBean);
		
		byte[] 	shortSnippet 	= new byte[2];
		
		//myBean wurde zum zweiten Mal erstellt -> ThreadId == 2
		myBean.setThreadID(threadID_normal);
		System.arraycopy(pb.getPacket(), 29, shortSnippet, 0, 2);
		assertEquals(2, ByteTools.shortByteToInt(shortSnippet));
		
		//Thread ID nach Änderung
		pb.alterThreadID(threadID_max);
		System.arraycopy(pb.getPacket(), 29, shortSnippet, 0, 2);
		assertEquals(threadID_max, ByteTools.shortByteToInt(shortSnippet));
		
		pb.alterThreadID(threadID_min);
		System.arraycopy(pb.getPacket(), 29, shortSnippet, 0, 2);
		assertEquals(threadID_min, ByteTools.shortByteToInt(shortSnippet));
		
	}
	
	@Test
	public void testSetReset(){
		byte[] boolSnippet = new byte[4];

		//Da IP nicht vom Packetbuilder verarbeitet wird,
		//egal
		try{
			groupIp				= InetAddress.getByName("224.1.2.3");
			sourceIp 			= InetAddress.getByName("192.168.232.1");
		}catch(UnknownHostException e){
			groupIp 			= null;
			sourceIp			= null;
		}
		//Den hostName setzen
		try{
			hostID_normal		= InetAddress.getLocalHost().getHostName();
		}catch(UnknownHostException e){
			fail("UnknownHostException while setting hostID");
		};
		
		myBean = new IgmpMldData(groupIp, sourceIp, udpPort, packetLength_normal, ttl_min, packetRateDes_norm, active, typ);
		pb = new PacketBuilder(myBean);
		
		System.arraycopy(pb.getPacket(), 38, boolSnippet, 0, 4);
		
		//Standart ist false
		assertTrue(!ByteTools.byteToBoolean(boolSnippet));
		
		pb.setReset(true);
		System.arraycopy(pb.getPacket(), 38, boolSnippet, 0, 4);
		assertTrue(ByteTools.byteToBoolean(boolSnippet));
		
		pb.setReset(false);
		System.arraycopy(pb.getPacket(), 38, boolSnippet, 0, 4);
		assertTrue(!ByteTools.byteToBoolean(boolSnippet));
	}
}
