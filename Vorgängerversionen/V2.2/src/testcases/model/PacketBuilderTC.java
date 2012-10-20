package testcases.model;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import program.data.MulticastData;
import program.data.MulticastData.Typ;
import program.model.PacketBuilder;
import program.model.ByteTools;
import program.model.PacketBuilderMMRP;


public class PacketBuilderTC{
	private PacketBuilder pb;
	private MulticastData myBean;
	private PacketBuilderMMRP packetBuilderMmrp;

	//nicht verarbeitet, nur fï¿½r Bean-Konstruktor
	InetAddress 	groupIp,
					sourceIp;
	byte[]			groupMac,
					sourceMac;
	boolean			active				= false;
	int		 		udpPort	 			= 4711;

	Typ				typ					= Typ.RECEIVER_V4;
	
	//geprï¿½fte Werte
	String			hostID_normal		= "",
					hostID_void			= "",
					hostID_tooLong		= "dasIstEinSehrLangerHostNameDerGekuerztWerdenMuesste";
	int				packetLength_normal	= 100,
					packetLength_null	= 0,		
					packetLength_short	= 52,
					packetLength_short_mmrp = 62,
					packetLength_long	= 65575,	
					ttl_min				= 1,
					ttl_max				= 32,	
					packetRateDes_norm	= 100,
					packetRateDes_max	= 65535,
					packetRateDes_min	= 1,
					threadID_normal		= 1,
					threadID_max		= 65535,
					threadID_min		= 0;
	byte[]			checksum			= new byte[2],
					snippetForChecksum  = new byte[42];
	long			timeStamp			= 0,			//Wird erst spï¿½ter gesetzt
					timeP1				= 0,			//timeStamp muss spï¿½ter zwischen timeP1 und timeP2 liegen
					timeP2				= 0;


	@Test
	public void testGetPacketIpv4() {
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

		myBean = new MulticastData(groupIp, sourceIp, udpPort, packetLength_normal, ttl_min, packetRateDes_norm, active, typ);
		pb = new PacketBuilder(myBean);
 		mcPacket = pb.getPacket();

//Wird ein Paket zurï¿½ckgegeben?
 		assertTrue(mcPacket!=null);

//Lï¿½nge des Pakets prï¿½fen
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
		assertEquals(ttl_min, new PacketBuilder(myBean).getPacket()[37]);

		myBean.setTtl(ttl_max);
		assertEquals(ttl_max, new PacketBuilder(myBean).getPacket()[37]);

//stimmt der "reset"-Wert? (Bei uns immer "0")
		System.arraycopy(mcPacket, 38, intSnippet, 0, 4);
		assertEquals(0, ByteTools.byteToInt(intSnippet));

//checksumme
		System.arraycopy(mcPacket, 42, checksum, 0, 2);
		System.arraycopy(mcPacket, 0, snippetForChecksum, 0, 42);
		assertEquals(checksum[0], ByteTools.crc16(snippetForChecksum)[0]);
		assertEquals(checksum[1], ByteTools.crc16(snippetForChecksum)[1]);

//Der Zeitstempel ist ab einem Zufï¿½lligen zeitpunkt, deswegen nur mï¿½glich zu
//sehen ob er in dem richtigen Zeitraum liegt
		timeP1		= System.nanoTime();
		mcPacket 	= new PacketBuilder(myBean).getPacket();
		timeP2		= System.nanoTime();
		System.arraycopy(mcPacket, 44, longSnippet, 0, 8);
		timeStamp = ByteTools.byteToLong(longSnippet);
		assertTrue(	(timeStamp>timeP1)	&&	(timeStamp<timeP2));

	}

	@Test
	public void testGetPacketMmrp() {
		groupMac			= new byte[] {1, 0, 94, 0, 0, 1};
		sourceMac 			= new byte[] {0, 12, 41, 35, 72, 59};
		
		//Den hostName setzen
		try{
			hostID_normal		= InetAddress.getLocalHost().getHostName();
		}catch(UnknownHostException e){
			fail("UnknownHostException while setting hostID");
		};

		byte[] 	mcPacket;
		byte[] 	macSnippet 		= new byte[6];
		byte[]	shortSnippet	= new byte[2];
		byte[] 	intSnippet		= new byte[4];
		byte[]  longSnippet		= new byte[8];
		byte[] 	snippet 		= new byte[29];

		myBean = new MulticastData(groupIp, sourceIp, udpPort, packetLength_normal, ttl_min, packetRateDes_norm, active, typ);
		myBean.setSourceMac(sourceMac);
		myBean.setGroupMac(groupMac);
		packetBuilderMmrp = new PacketBuilderMMRP(myBean);
 		mcPacket = packetBuilderMmrp.getPacket();

 		//Wird ein Paket zurï¿½ckgegeben?
 		assertTrue(mcPacket!=null);

 		//Lï¿½nge des Pakets prï¿½fen
 		myBean.setPacketLength(packetLength_normal);
		assertEquals(packetLength_normal, new PacketBuilderMMRP(myBean).getPacket().length);

		myBean.setPacketLength(packetLength_short_mmrp);
		assertEquals(packetLength_short_mmrp, new PacketBuilderMMRP(myBean).getPacket().length);

		myBean.setPacketLength(packetLength_long);
		assertEquals(packetLength_long, new PacketBuilderMMRP(myBean).getPacket().length);

		//stimmt das Host-ID Feld?
		myBean.setHostID(hostID_normal);
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 14, snippet, 0, 29);
		assertEquals(hostID_normal, new String(snippet).substring(0, hostID_normal.length()));

		myBean.setHostID(hostID_void);
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 14, snippet, 0, 29);
		assertEquals(hostID_void, new String(snippet).substring(0, hostID_void.length()));

		myBean.setHostID(hostID_tooLong);
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 14, snippet, 0, 29);
		assertEquals(hostID_tooLong.substring(0, 29), new String(snippet).substring(0, snippet.length));

		//stimmt die Sender-Mac?
		myBean.setSourceMac(sourceMac);
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 6, macSnippet, 0, 6);
		assertEquals(new String(sourceMac), new String(macSnippet));

		//stimmt die Empfänger-Mac?
		myBean.setGroupMac(groupMac);
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 0, macSnippet, 0, 6);
		assertEquals(new String(groupMac), new String(macSnippet));

		//stimmt die Sender-Id?
		myBean.setThreadID(threadID_normal);
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 43, shortSnippet, 0, 2);
		assertEquals(threadID_normal, ByteTools.shortByteToInt(shortSnippet));

		myBean.setThreadID(threadID_min);
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 43, shortSnippet, 0, 2);
		assertEquals(threadID_min, ByteTools.shortByteToInt(shortSnippet));

		myBean.setThreadID(threadID_max);
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 43, shortSnippet, 0, 2);
		assertEquals(threadID_max, ByteTools.shortByteToInt(shortSnippet));
		
		//right packet count?
		packetBuilderMmrp = new PacketBuilderMMRP(myBean);
		packetBuilderMmrp.getPacket();
		packetBuilderMmrp.getPacket();
		packetBuilderMmrp.getPacket();
		System.arraycopy(packetBuilderMmrp.getPacket(), 45, shortSnippet, 0, 2);
		assertEquals(4, ByteTools.shortByteToInt(shortSnippet));
		
		//right packet rate?
		myBean.setPacketRateDesired(packetRateDes_norm);
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 47, shortSnippet, 0, 2);
		assertEquals(packetRateDes_norm, ByteTools.shortByteToInt(shortSnippet));
		
		myBean.setPacketRateDesired(packetRateDes_min);
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 47, shortSnippet, 0, 2);
		assertEquals(packetRateDes_min, ByteTools.shortByteToInt(shortSnippet));
		
		myBean.setPacketRateDesired(packetRateDes_max);
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 47, shortSnippet, 0, 2);
		assertEquals(packetRateDes_max, ByteTools.shortByteToInt(shortSnippet));
		
		//right reset? always false
		System.arraycopy(new PacketBuilderMMRP(myBean).getPacket(), 49, intSnippet, 0, 4);
		assertEquals(false, ByteTools.byteToBoolean(longSnippet));
	}
	
	@Test
	public void testAlterThreadIDIpv4(){
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

		myBean = new MulticastData(groupIp, sourceIp, udpPort, packetLength_normal, ttl_min, packetRateDes_norm, active, typ);
		pb = new PacketBuilder(myBean);

		byte[] 	shortSnippet 	= new byte[2];

		//Thread ID Vorher: bei initialisierung d. Multicast-Data-Objekt
		//ist die Thread ID -1, die letzten 2 Bytes sind dann 11111111 11111111 = 65535
		myBean.setThreadID(threadID_normal);
		System.arraycopy(pb.getPacket(), 29, shortSnippet, 0, 2);
		assertEquals(65535, ByteTools.shortByteToInt(shortSnippet));

		//Thread ID nach ï¿½nderung
		pb.alterThreadID(threadID_max);
		System.arraycopy(pb.getPacket(), 29, shortSnippet, 0, 2);
		assertEquals(threadID_max, ByteTools.shortByteToInt(shortSnippet));

		pb.alterThreadID(threadID_min);
		System.arraycopy(pb.getPacket(), 29, shortSnippet, 0, 2);
		assertEquals(threadID_min, ByteTools.shortByteToInt(shortSnippet));
	}

	@Test
	public void testAlterThreadIDMmrp(){
		groupMac			= new byte[] {1, 0, 94, 0, 0, 1};
		sourceMac 			= new byte[] {0, 12, 41, 35, 72, 59};
		
		//Den hostName setzen
		try{
			hostID_normal		= InetAddress.getLocalHost().getHostName();
		}catch(UnknownHostException e){
			fail("UnknownHostException while setting hostID");
		};

		myBean = new MulticastData(groupIp, sourceIp, udpPort, packetLength_normal, ttl_min, packetRateDes_norm, active, typ);
		myBean.setSourceMac(sourceMac);
		myBean.setGroupMac(groupMac);
		packetBuilderMmrp = new PacketBuilderMMRP(myBean);

		byte[] 	shortSnippet 	= new byte[2];

		//Thread ID Vorher: bei initialisierung d. Multicast-Data-Objekt
		//ist die Thread ID -1, die letzten 2 Bytes sind dann 11111111 11111111 = 65535
		myBean.setThreadID(threadID_normal);
		System.arraycopy(packetBuilderMmrp.getPacket(), 43, shortSnippet, 0, 2);
		assertEquals(65535, ByteTools.shortByteToInt(shortSnippet));

		//Thread ID nach ï¿½nderung
		packetBuilderMmrp.alterThreadID(threadID_max);
		System.arraycopy(packetBuilderMmrp.getPacket(), 43, shortSnippet, 0, 2);
		assertEquals(threadID_max, ByteTools.shortByteToInt(shortSnippet));

		packetBuilderMmrp.alterThreadID(threadID_min);
		System.arraycopy(packetBuilderMmrp.getPacket(), 43, shortSnippet, 0, 2);
		assertEquals(threadID_min, ByteTools.shortByteToInt(shortSnippet));
	}
	
	@Test
	public void testSetResetIpv4(){
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

		myBean = new MulticastData(groupIp, sourceIp, udpPort, packetLength_normal, ttl_min, packetRateDes_norm, active, typ);
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
	
	@Test
	public void testSetResetMmrp(){
		groupMac			= new byte[] {1, 0, 94, 0, 0, 1};
		sourceMac 			= new byte[] {0, 12, 41, 35, 72, 59};
		
		byte[] boolSnippet = new byte[4];

		//Den hostName setzen
		try{
			hostID_normal		= InetAddress.getLocalHost().getHostName();
		}catch(UnknownHostException e){
			fail("UnknownHostException while setting hostID");
		};

		myBean = new MulticastData(groupIp, sourceIp, udpPort, packetLength_normal, ttl_min, packetRateDes_norm, active, typ);
		myBean.setGroupMac(groupMac);
		myBean.setSourceMac(sourceMac);
		packetBuilderMmrp = new PacketBuilderMMRP(myBean);

		System.arraycopy(packetBuilderMmrp.getPacket(), 49, boolSnippet, 0, 4);

		//Standart ist false
		assertTrue(!ByteTools.byteToBoolean(boolSnippet));

		packetBuilderMmrp.setReset(true);
		System.arraycopy(packetBuilderMmrp.getPacket(), 49, boolSnippet, 0, 4);
		assertTrue(ByteTools.byteToBoolean(boolSnippet));

		packetBuilderMmrp.setReset(false);
		System.arraycopy(packetBuilderMmrp.getPacket(), 49, boolSnippet, 0, 4);
		assertTrue(!ByteTools.byteToBoolean(boolSnippet));
	}
}
