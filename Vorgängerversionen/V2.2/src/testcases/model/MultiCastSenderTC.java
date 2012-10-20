package testcases.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.junit.Test;

import program.data.MulticastData;
import program.data.MulticastData.Typ;
import program.model.MulticastSender;
import program.model.MulticastSender.sendingMethod;
import program.model.MulticastSenderMMRP;



public class MultiCastSenderTC {

	InetAddress 	groupIp,
					sourceIp;
	byte[]			groupMac,
					sourceMac;
	Vector<PcapIf>  mmrpInterfaces 		= new Vector<PcapIf>();
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
		packetLength		= 65507;
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
			groupIp				= InetAddress.getByName("ff00::1");
			sourceIp 			= InetAddress.getByName("fe80::b84e:bd6e:6cbb:3ed7");
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

	private void setupMmrp() {
		
		groupMac			= new byte[] {1, 0, 94, 0, 0, 1};
		sourceMac			= new byte[] {0, 12, 41, 35, 72, 59};
		active				= false;
		packetLength		= 100;
		packetRateDesired	= 100000000;
		packetRateMeasured	= 100;
		threadID			= 1;
		numberOfInt			= 0;
		averageIntTime		= 0;
		packetLossPerSecond = 0;
		jitter				= 0;
		typ					= Typ.SENDER_MMRP;
	}
	
	public void testLauf(sendingMethod sm, MulticastData.Typ type) {

		MulticastData myBean = new MulticastData(groupIp, sourceIp, udpPort, packetLength, ttl, packetRateDesired, active, typ);

		Logger logger = Logger.getLogger("zisko.multicastor.testcases.model");
		
		if(type.equals(MulticastData.Typ.SENDER_V4) || type.equals(MulticastData.Typ.SENDER_V6)) {
			MulticastSender myMcSender = new MulticastSender(myBean, logger);
			
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
		else {
			//pcap for mmrp
			StringBuilder errbuf = new StringBuilder(); // For any error msgs
			int r = Pcap.findAllDevs(mmrpInterfaces, errbuf);
			myBean.setDevice(mmrpInterfaces.elementAt(0));
			
			myBean.setGroupMac(groupMac);
			myBean.setSourceMac(sourceMac);
			
			MulticastSenderMMRP myMcSenderMmrp = new MulticastSenderMMRP(myBean, logger);
			
			Thread myThread = new Thread(myMcSenderMmrp);
			myMcSenderMmrp.setActive(true, sm);
			myThread.start();

			try{
				Thread.sleep(1000);
				myMcSenderMmrp.setActive(false);
			}catch(Exception e){
				System.out.println("lalalaa..." + e.getMessage());
			}
		}

		
	}

	@Test
	public void senderTest(){
		System.out.println("+++++++++++++++1. Durchlauf mit IPv4++++++++++++++++");
		setupIPv4();
		testLauf(sendingMethod.SLEEP_MULTIPLE, typ.SENDER_V4);
		testLauf(sendingMethod.PEAK, typ.SENDER_V4);
		testLauf(sendingMethod.NO_SLEEP, typ.SENDER_V4);
		System.out.println("+++++++++++++++2. Durchlauf mit IPv6++++++++++++++++");
		setupIPv6();
		testLauf(sendingMethod.SLEEP_MULTIPLE, typ.SENDER_V6);
		testLauf(sendingMethod.PEAK, typ.SENDER_V6);
		testLauf(sendingMethod.NO_SLEEP, typ.SENDER_V6);
		System.out.println("+++++++++++++++3. Durchlauf mit MMRP++++++++++++++++");
		setupMmrp();
		testLauf(sendingMethod.SLEEP_MULTIPLE, typ.SENDER_MMRP);
		testLauf(sendingMethod.PEAK, typ.SENDER_MMRP);
		testLauf(sendingMethod.NO_SLEEP, typ.SENDER_MMRP);
	}
}
