package zisko.multicastor.testcases.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.logging.Logger;

import javax.management.Query;

import org.junit.Assert;
import org.junit.Test;

import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.model.MulticastReceiver;
import zisko.multicastor.program.model.MulticastSender;
import zisko.multicastor.program.model.NetworkAdapter;
import zisko.multicastor.program.model.PacketBuilder;

/**
 * UNGÜLTIGE USEREINGABEN WERDEN VOM IMPUTVALIDATOR ABGEFANGEN
 * Methode setActive wird aus zeitgründen nur indirekt getestet (da ohne aktivierten Sender keine
 * Veränderung, muss das aktivieren des Senders erfolgt sein)
 * @author muellerj
 *
 */
public class Multicast_SenderTC {
	MulticastSender myMcSender;
	MulticastReceiver myMcReceiver;
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
    String source_ipV6_8 = "2001:0DB8:85A3:08D3:1319:8A2E:0370:7344";
    
    String mcGroup_ipV6_1 = "FF00::1";
    String mcGroup_ipV6_2 = "FF23:2342:2343::2345";
    
    //Packet length
    int packetLength_ipV4_1 = 52;
    int packetLength_ipV4_2 = 65507;
    
    int packetLength_ipV6_1 = 52;
    int packetLength_ipV6_2 = 65527;
    
    //Time to live
    int ttl_1 = 1;
    int ttl_2 = 32;
    
    //Packet rate
    int pRate_1 = 10000;
    int pRate_2 = 65536;
    
    //Ports
    int port1  = 65535;
    int port2 =  12345;
    int port3 =  0;
    int port4 =  1020;
	
	
	@Test
	public void testSender(){
		testIpv6();
	}

	public void testIpv4(){
		//Für jede gültige,konfigurierte IPv4-Adresse:
		Vector<InetAddress> sourceIPv4 = NetworkAdapter.ipv4Interfaces;
		
		for(int i=0; i<sourceIPv4.size();i++){
			try{
				mcData.setSourceIp(InetAddress.getByName("10.12.25.126"));
				mcData.setGroupIp(InetAddress.getByName(mcGroup_ipV4[i%4])); //eine von den 4 Group IPs. Auf diese weise wird jede group-IP einmal verwendet
				mcDataReceived.setGroupIp(InetAddress.getByName(mcGroup_ipV4[i%4])); //eine von den 4 Group IPs. Auf diese weise wird jede group-IP einmal verwendet
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
			
			//Setup 1
			prepareSampleDataV6();
			mcDataReceived.setUdpPort(port1);
			mcData.setUdpPort(port1);
			mcData.setPacketRateDesired(pRate_1);
			mcData.setTtl(ttl_1);
			mcData.setPacketLength(packetLength_ipV6_1);
			
			runSenderAndReceiver();

//ANALYSE
			//Überhaupt etwas angekommen?
			Assert.assertTrue(mcDataReceived.getPacketRateMeasured()!=0);
			//Wurden die Daten richtig übertragen?
			Assert.assertEquals(ttl_1, mcDataReceived.getTtl());
			Assert.assertEquals(packetLength_ipV6_1, mcDataReceived.getPacketLength());
			staticTests();			
//ANALYSE-ENDE
			
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
			Assert.assertTrue(mcDataReceived.getPacketRateMeasured()!=0);
			
			//Setup 3
			prepareSampleDataV6();
			mcDataReceived.setUdpPort(port3);
			mcData.setUdpPort(port3);
			mcData.setPacketRateDesired(pRate_2);
			mcData.setTtl(ttl_2);
			mcData.setPacketLength(packetLength_ipV6_2);
			
			runSenderAndReceiver();
			System.out.println(mcDataReceived);
			Assert.assertTrue(mcDataReceived.getPacketRateMeasured()!=0);
			
			//Setup 4
			prepareSampleDataV6();
			mcDataReceived.setUdpPort(port4);
			mcData.setUdpPort(port4);
			mcData.setPacketRateDesired(pRate_2);
			mcData.setTtl(ttl_2);
			mcData.setPacketLength(packetLength_ipV6_2);
			
			runSenderAndReceiver();
			Assert.assertTrue(mcDataReceived.getPacketRateMeasured()!=0);
	}
	
	private void runSenderAndReceiver(){
		Logger logger = Logger.getLogger("zisko.multicastor.testcases.model");
		myMcSender = new MulticastSender(mcData, logger);
		myMcReceiver = new MulticastReceiver(mcDataReceived, logger);
		
		//Receiver starten
		Thread myRecThread = new Thread(myMcReceiver);
		myMcReceiver.setActive(true);
		myMcReceiver.joinGroup();
		myRecThread.start();
		
		
		//Sender starten
		Thread myThread = new Thread(myMcSender);
		myMcSender.setActive(true);
		myThread.start();
		
		try{
			Thread.sleep(1000);
			myMcReceiver.setActive(false);
			myMcSender.setActive(false);
			Thread.sleep(1000);
		}catch(Exception e){
			Assert.fail(e.getMessage());
		}
		
		myMcReceiver.update();
		myMcSender.update();
		
		while(!messagesRec.isEmpty())		System.out.println(messagesRec.poll());
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
	
	private void staticTests(){
		Assert.assertEquals(4711, mcDataReceived.getThreadID());
		try{
			Assert.assertEquals(InetAddress.getLocalHost().getHostName(), mcDataReceived.getHostID());
		}catch(Exception e){
			Assert.fail(e.getMessage());
		}
	}

}

