package testcases.model;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.junit.Assert;
import org.junit.Test;

import program.data.MulticastData;
import program.data.MulticastData.Typ;
import program.model.MulticastReceiver;
import program.model.MulticastReceiverMMRP;
import program.model.MulticastSender;
import program.model.MulticastSenderMMRP;
import program.model.NetworkAdapter;

/**
 * UNG�LTIGE USEREINGABEN WERDEN VOM IMPUTVALIDATOR ABGEFANGEN
 * Methode setActive wird aus zeitgr�nden nur indirekt getestet (da ohne aktivierten Sender keine
 * Ver�nderung, muss das aktivieren des Senders erfolgt sein)
 * @author muellerj
 *
 */
public class Multicast_SenderTC {
	MulticastSender myMcSender;
	MulticastReceiver myMcReceiver;
	MulticastSenderMMRP myMcSenderMmrp;
	MulticastReceiverMMRP myMcReceiverMmrp;
	
	Vector<PcapIf> mmrpInterfaces = new Vector<PcapIf>();
	
	Queue<String> messages       = new LinkedList<String>(),
				  messagesRec	 = new LinkedList<String>();
	MulticastData mcData 		 = new MulticastData(),
				  mcDataReceived = new MulticastData();

	//IPv4-Adressen
    String mcGroup_ipV4[] = new String[]{
    			"224.0.0.1",
    			"233.22.34.9",
    			"224.000.000.001",
    			"224.0.0.0"};

    //IPv6-Adressen
    String source_ipV6_1 = "::1";
    String source_ipV6_2 = "::0";
    String source_ipV6_3 = "FF00::1";
    String source_ipV6_4 = "0:0:0:0:0:0:0:1";
    String source_ipV6_5 = "0:0:0:0:0:0:1";
    String source_ipV6_6 = "192.168.178.1";
    String source_ipV6_7 = "::192:168:178:1";
    //String source_ipV6_8 = "2001:0DB8:85A3:08D3:1319:8A2E:0370:7344";
    String source_ipV6_8 = "fe80::b84e:bd6e:6cbb:3ed7%11";

    String mcGroup_ipV6_1 = "ff00::1";
    String mcGroup_ipV6_2 = "FF23:2342:2343::2345";

    //MMRP-Adressen
    byte[] source_mac_1 = {0, 12, 41, 35, 72, 59};
    
    byte[] mcGroup_mmrp_1 = {1, 0, 94, 0, 0, 1};
    byte[] mcGroup_mmrp_2 = {1, 0, 94, 127, (byte)255, (byte)255};
    
    //Packet length
    int packetLength_ipV4_1 = 52;
    int packetLength_ipV4_2 = 65507;

    int packetLength_ipV6_1 = 52;
    int packetLength_ipV6_2 = 65527;
    
    int packetLength_mmrp_1 = 62;
    int packetLength_mmrp_2 = 1500;

    //Time to live
    int ttl_1 = 1;
    int ttl_2 = 32;

    //Packet rate
    //int pRate_1 = 10000;
    int pRate_1 = 1;
    int pRate_2 = 65536;

    //Ports
    int port1  = 65535;
    int port2 =  12345;
    int port3 =  0;
    int port4 =  1020;

	@Test
	public void testSender(){
		//testIpv4();
		//testIpv6();
		testMmrp();
	}

	public void testIpv4(){
		//for each legal configured IPv4-Address
		Vector<InetAddress> sourceIPv4 = NetworkAdapter.ipv4Interfaces;

		for(int i=0; i<sourceIPv4.size();i++){
			try{
				mcData.setSourceIp(InetAddress.getByName("10.12.25.126"));
				//one of four group IPs. Each group-IP will be used
				mcData.setGroupIp(InetAddress.getByName(mcGroup_ipV4[i%4]));
				//one of four group IPs. Each group-IP will be used
				mcDataReceived.setGroupIp(InetAddress.getByName(mcGroup_ipV4[i%4]));
			}catch (Exception e) {
				System.out.println(e.getMessage());
			}

			//Setup 1
			mcDataReceived.setUdpPort(port1);
			mcData.setUdpPort(port1);
			mcData.setPacketRateDesired(pRate_1);
			mcData.setTtl(ttl_1);
			mcData.setPacketLength(packetLength_ipV4_1);

			runSenderAndReceiver();

			//Setup 2
			mcDataReceived.setUdpPort(port2);
			mcData.setUdpPort(port2);
			mcData.setPacketRateDesired(pRate_2);
			mcData.setTtl(ttl_2);
			mcData.setPacketLength(packetLength_ipV4_2);

			runSenderAndReceiver();

			//Setup 3
			mcDataReceived.setUdpPort(port3);
			mcData.setUdpPort(port3);
			mcData.setPacketRateDesired(pRate_2);
			mcData.setTtl(ttl_2);
			mcData.setPacketLength(packetLength_ipV4_2);

			runSenderAndReceiver();

			//Setup 4
			mcDataReceived.setUdpPort(port4);
			mcData.setUdpPort(port4);
			mcData.setPacketRateDesired(pRate_2);
			mcData.setTtl(ttl_2);
			mcData.setPacketLength(packetLength_ipV4_2);

			runSenderAndReceiver();
		}
	}

	public void testIpv6(){
			@SuppressWarnings("unused")
			Logger logger = Logger.getLogger("zisko.multicastor.testcases.model");
			
			//Setup 1
			prepareSampleDataV6();
			mcDataReceived.setUdpPort(port1);
			mcData.setUdpPort(port1);
			mcData.setPacketRateDesired(pRate_1);
			mcData.setTtl(ttl_1);
			mcData.setPacketLength(packetLength_ipV6_1);

			runSenderAndReceiver();
			
			try{
				Thread.sleep(1000);
				myMcReceiver.update();
				myMcSender.update();
				//have you received?
				Assert.assertTrue(mcDataReceived.getPacketRateMeasured()!=0);
				//right input data
				Assert.assertEquals(ttl_1, mcDataReceived.getTtl());
				Assert.assertEquals(packetLength_ipV6_1, mcDataReceived.getPacketLength());
				staticTests();
				myMcReceiver.setActive(false);
				myMcSender.setActive(false);
				Thread.sleep(1000);
			}catch(Exception e){
				Assert.fail(e.getMessage());
			}
			
			//Setup 2
			prepareSampleDataV6();
			try{
				mcData.setGroupIp(InetAddress.getByName(mcGroup_ipV6_2));
				mcDataReceived.setGroupIp(InetAddress.getByName(mcGroup_ipV6_2));
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			mcDataReceived.setUdpPort(port2);
			mcData.setUdpPort(port2);
			mcData.setPacketRateDesired(pRate_2);
			mcData.setTtl(ttl_2);
			mcData.setPacketLength(packetLength_ipV6_2);

			runSenderAndReceiver();
			
			try{
				Thread.sleep(1000);
				myMcReceiver.update();
				myMcSender.update();
				Assert.assertTrue(mcDataReceived.getPacketRateMeasured()!=0);
				myMcReceiver.setActive(false);
				myMcSender.setActive(false);
				Thread.sleep(1000);
			}catch(Exception e){
				Assert.fail(e.getMessage());
			}
			
			//Setup 3
			prepareSampleDataV6();
			mcDataReceived.setUdpPort(port3);
			mcData.setUdpPort(port3);
			mcData.setPacketRateDesired(pRate_2);
			mcData.setTtl(ttl_2);
			mcData.setPacketLength(packetLength_ipV6_2);

			runSenderAndReceiver();
			
			try{
				Thread.sleep(1000);
				myMcReceiver.update();
				myMcSender.update();
				System.out.println(mcDataReceived);
				Assert.assertTrue(mcDataReceived.getPacketRateMeasured()!=0);
				myMcReceiver.setActive(false);
				myMcSender.setActive(false);
				Thread.sleep(1000);
			}catch(Exception e){
				Assert.fail(e.getMessage());
			}
			
			//Setup 4
			prepareSampleDataV6();
			mcDataReceived.setUdpPort(port4);
			mcData.setUdpPort(port4);
			mcData.setPacketRateDesired(pRate_2);
			mcData.setTtl(ttl_2);
			mcData.setPacketLength(packetLength_ipV6_2);

			runSenderAndReceiver();
			
			try{
				Thread.sleep(1000);
				myMcReceiver.update();
				myMcSender.update();
				Assert.assertTrue(mcDataReceived.getPacketRateMeasured()!=0);
				myMcReceiver.setActive(false);
				myMcSender.setActive(false);
				Thread.sleep(1000);
			}catch(Exception e){
				Assert.fail(e.getMessage());
			}
	}

	public void testMmrp() {
		@SuppressWarnings("unused")
		Logger logger = Logger.getLogger("zisko.multicastor.testcases.model");
		
		//Setup 1
		prepareSampleDataMmrp(1);
		mcData.setPacketRateDesired(pRate_1);
		mcData.setPacketLength(packetLength_mmrp_1);
		
		runSenderAndReceiverMMRP();
		
		try{
			Thread.sleep(1000);
			myMcReceiverMmrp.update();
			myMcSenderMmrp.update();
			//have you received?
			Assert.assertTrue(mcDataReceived.getPacketRateMeasured()!=0);
			//right input data
			Assert.assertEquals(packetLength_mmrp_1, mcDataReceived.getPacketLength());
			staticTests();
			myMcReceiverMmrp.setActive(false);
			myMcSenderMmrp.setActive(false);
			Thread.sleep(1000);
		}catch(Exception e){
			Assert.fail(e.getMessage());
		}
		
		//Setup 2
		prepareSampleDataMmrp(2);
		mcData.setPacketRateDesired(pRate_2);
		mcData.setPacketLength(packetLength_mmrp_2);
				
		runSenderAndReceiverMMRP();
			
		try{
			Thread.sleep(1000);
			myMcReceiverMmrp.update();
			myMcSenderMmrp.update();
			//have you received?
			Assert.assertTrue(mcDataReceived.getPacketRateMeasured()!=0);
			//right input data);
			Assert.assertEquals(packetLength_mmrp_2, mcDataReceived.getPacketLength());
			staticTests();
			myMcReceiverMmrp.setActive(false);
			myMcSenderMmrp.setActive(false);
			Thread.sleep(1000);
		}catch(Exception e){
			Assert.fail(e.getMessage());
		}
	}
	
	private void runSenderAndReceiver(){
		Logger logger = Logger.getLogger("zisko.multicastor.testcases.model");
		myMcSender = new MulticastSender(mcData, logger);
		myMcReceiver = new MulticastReceiver(mcDataReceived, logger);
				
		//Sender starten
		Thread myThread = new Thread(myMcSender);
		myMcSender.setActive(true);
		myThread.start();

		//Receiver starten
		Thread myRecThread = new Thread(myMcReceiver);
		myMcReceiver.setActive(true);
		myMcReceiver.joinGroup();
		myRecThread.start();
	}
	
	private void runSenderAndReceiverMMRP(){
		Logger logger = Logger.getLogger("zisko.multicastor.testcases.model");
		myMcSenderMmrp = new MulticastSenderMMRP(mcData, logger);
		myMcReceiverMmrp = new MulticastReceiverMMRP(mcDataReceived, logger);
				
		//Sender starten
		Thread myThread = new Thread(myMcSenderMmrp);
		myMcSenderMmrp.setActive(true);
		myThread.start();

		//Receiver starten
		Thread myRecThread = new Thread(myMcReceiverMmrp);
		myMcReceiverMmrp.setActive(true);
		myMcReceiverMmrp.joinGroup();
		myRecThread.start();
	}

	private void prepareSampleDataV6(){
		mcData = new MulticastData();
		mcDataReceived = new MulticastData();

		//Beispiel Group-IP
		try{
			mcData.setGroupIp(InetAddress.getByName(mcGroup_ipV6_1));
			mcDataReceived.setGroupIp(InetAddress.getByName(mcGroup_ipV6_1));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}

		//Source-IP
		try{
			mcData.setSourceIp(InetAddress.getByName("::1"));
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}


		//HostID wird automatisch gesetzt
		mcData.setThreadID(4711);

		//Typ
		mcData.setTyp(Typ.UNDEFINED);
		mcDataReceived.setTyp(Typ.UNDEFINED);
	}

	private void prepareSampleDataMmrp(int testcase) {
		mcData = new MulticastData();
		mcDataReceived = new MulticastData();

		//Beispiel Group-IP
		try{
			if(testcase == 1) {
				mcData.setGroupMac(mcGroup_mmrp_1);
				mcDataReceived.setGroupMac(mcGroup_mmrp_1);
			}
			else {
				mcData.setGroupMac(mcGroup_mmrp_2);
				mcDataReceived.setGroupMac(mcGroup_mmrp_2);
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}

		//Source-IP
		try{
			mcData.setSourceMac(source_mac_1);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		//HostID wird automatisch gesetzt
		mcData.setThreadID(4711);

		//Typ
		mcData.setTyp(Typ.SENDER_MMRP);
		mcDataReceived.setTyp(Typ.RECEIVER_MMRP);
		
		//pcap for mmrp
		StringBuilder errbuf = new StringBuilder(); // For any error msgs
		@SuppressWarnings("unused")
		int r = Pcap.findAllDevs(mmrpInterfaces, errbuf);
		mcData.setDevice(mmrpInterfaces.elementAt(0));
		mcDataReceived.setDevice(mmrpInterfaces.elementAt(0));
	}
	
	private void staticTests(){
		Assert.assertEquals(4711, mcDataReceived.getThreadID());
		try{
			Assert.assertEquals(InetAddress.getLocalHost().getHostName(), mcDataReceived.getHostID());
		}catch(Exception e){
			Assert.fail(e.getMessage());
		}
	}

}

