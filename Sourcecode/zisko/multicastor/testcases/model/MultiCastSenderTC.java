package zisko.multicastor.testcases.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;

import org.junit.Test;

import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.model.MulticastSender;
import zisko.multicastor.program.model.MulticastSender.sendingMethod;



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
	Typ				typ					= Typ.RECEIVER_V4;
	
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
		typ					= Typ.SENDER_V4;
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
		typ					= Typ.SENDER_V6;
	}
	
	public void testLauf(sendingMethod sm) {		
		
		MulticastSender myMcSender;
			
		MulticastData myBean = new MulticastData(groupIp, sourceIp, udpPort, packetLength, ttl, packetRateDesired, active, typ);
		
		Logger logger = Logger.getLogger("zisko.multicastor.testcases.model");
			myMcSender = new MulticastSender(myBean, logger);

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
