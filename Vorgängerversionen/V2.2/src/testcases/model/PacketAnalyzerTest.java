package testcases.model;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Vector;
import java.util.logging.Logger;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.junit.Before;
import org.junit.Test;

import program.data.MulticastData;
import program.model.PacketAnalyzer;
import program.model.PacketAnalyzerMMRP;
import program.model.PacketBuilder;
import program.model.PacketBuilderMMRP;
import junit.framework.TestCase;

public class PacketAnalyzerTest extends TestCase {
		PacketAnalyzer packetAnalyzer;
		PacketAnalyzerMMRP packetAnalyzerMmrp;
		MulticastData mcData;
		MulticastData mcDataMmrp;
		PacketBuilder packetBuilder;
		PacketBuilderMMRP packetBuilderMmrp;
		Vector<PcapIf>  mmrpInterfaces 		= new Vector<PcapIf>();

		final int packetLength = 3000;
		String hostID = "hostIDdummy";
		int threadID = 5;
		int ttl = 3;
		int packetRateDesired = 200;

		public void setUpIpv4(){
			mcData = new MulticastData();
			try {
				mcData.setGroupIp(InetAddress.getByName("224.0.0.1"));
				mcData.setUdpPort(4711);
			} catch (UnknownHostException e) {
				fail();
			}
			Logger logger = Logger.getLogger("zisko.multicastor.testcases.model");
			packetAnalyzer = new PacketAnalyzer(mcData, logger);

			assertNotNull(packetAnalyzer);

			MulticastData mcBean = new MulticastData();
			mcBean.setPacketLength(packetLength);
			mcBean.setHostID(hostID);
			mcBean.setThreadID(threadID);
			mcBean.setPacketRateDesired(packetRateDesired);
			mcBean.setTtl(ttl);
			packetBuilder = new PacketBuilder(mcBean);
		}
		
		public void setUpMmrp(){
			mcDataMmrp = new MulticastData();
			try{
				mcDataMmrp.setGroupMac(new byte[] {1, 0, 94, 0, 0, 1});
				
				StringBuilder errbuf = new StringBuilder(); // For any error msgs
				int r = Pcap.findAllDevs(mmrpInterfaces, errbuf);
				mcDataMmrp.setDevice(mmrpInterfaces.elementAt(0));
			} catch (Exception e) {
				fail();
			}
			Logger logger = Logger.getLogger("zisko.multicastor.testcases.model");
			packetAnalyzerMmrp = new PacketAnalyzerMMRP(mcDataMmrp, logger);
			
			assertNotNull(packetAnalyzerMmrp);
			
			MulticastData mcBean = new MulticastData();
			mcBean.setPacketLength(packetLength);
			mcBean.setHostID(hostID);
			mcBean.setThreadID(threadID);
			mcBean.setPacketRateDesired(packetRateDesired);
			mcBean.setGroupMac(new byte[] {1, 0, 94, 0, 0, 1});
			mcBean.setSourceMac(new byte[] {0, 12, 41, 35, 72, 59});
			packetBuilderMmrp = new PacketBuilderMMRP(mcBean);
		}

		/**
		 * Erzeugt zwei aufeinander folgende Pakete und wertet diese aus
		 */
		@Test
		public void testAnalysePacket1Ipv4(){
			setUpIpv4();
			mcData.resetValues();
			packetAnalyzer.setInternerPacketCount(-1);

			packetAnalyzer.analyzePacket(createPacket());
			packetAnalyzer.analyzePacket(createPacket());

			packetAnalyzer.update();

			assertEquals(packetLength, mcData.getPacketLength());
			assertEquals(hostID, mcData.getHostID());
			assertEquals(threadID, mcData.getThreadID());
			assertEquals(ttl, mcData.getTtl());
			assertEquals(packetRateDesired, mcData.getPacketRateDesired());
			assertEquals(2, mcData.getPacketCount());
			assertEquals(2, mcData.getPacketRateMeasured());
			assertEquals(0, mcData.getNumberOfInterruptions());
			// Jitter wird nicht getestet....hab mir noch keinen guten Test dafür überlegt
			assertEquals(0, mcData.getPacketLossPerSecond());
			assertEquals(packetLength*2, mcData.getTraffic());

			packetAnalyzer.updateMin();
			assertEquals(packetLength, mcData.getPacketLength());
			assertEquals(hostID, mcData.getHostID());
			assertEquals(threadID, mcData.getThreadID());
			assertEquals(ttl, mcData.getTtl());
			assertEquals(packetRateDesired, mcData.getPacketRateDesired());
			assertEquals(2, mcData.getPacketCount());
			assertEquals(2, mcData.getPacketRateMeasured());
			assertEquals(0, mcData.getNumberOfInterruptions());
			assertEquals(0, mcData.getPacketLossPerSecond());

	//		assertEquals(0, mcData.getNumberOfInterruptionsAvg());
			assertEquals(0, mcData.getPacketLossPerSecondAvg());
			assertEquals(2, mcData.getPacketRateAvg());
			assertEquals(packetLength*2, mcData.getTrafficAvg());
		}

		/**
		 * Erzeugt zwei aufeinander folgende Pakete und wertet diese aus
		 */
		@Test
		public void testAnalysePacket1Mmrp(){
			setUpMmrp();
			mcDataMmrp.resetValues();
			packetAnalyzerMmrp.setInternerPacketCount(-1);

			packetAnalyzerMmrp.analyzePacket(createPacketMmrp());
			packetAnalyzerMmrp.analyzePacket(createPacketMmrp());

			packetAnalyzerMmrp.update();

			assertEquals(packetLength, mcDataMmrp.getPacketLength());
			assertEquals(hostID, mcDataMmrp.getHostID());
			assertEquals(threadID, mcDataMmrp.getThreadID());
			assertEquals(packetRateDesired, mcDataMmrp.getPacketRateDesired());
			assertEquals(2, mcDataMmrp.getPacketCount());
			assertEquals(2, mcDataMmrp.getPacketRateMeasured());
			assertEquals(0, mcDataMmrp.getNumberOfInterruptions());
			// Jitter wird nicht getestet....hab mir noch keinen guten Test dafür überlegt
			assertEquals(0, mcDataMmrp.getPacketLossPerSecond());
			assertEquals(packetLength*2, mcDataMmrp.getTraffic());

			packetAnalyzerMmrp.updateMin();
			assertEquals(packetLength, mcDataMmrp.getPacketLength());
			assertEquals(hostID, mcDataMmrp.getHostID());
			assertEquals(threadID, mcDataMmrp.getThreadID());
			//assertEquals(ttl, mcDataMmrp.getTtl());
			assertEquals(packetRateDesired, mcDataMmrp.getPacketRateDesired());
			assertEquals(2, mcDataMmrp.getPacketCount());
			assertEquals(2, mcDataMmrp.getPacketRateMeasured());
			assertEquals(0, mcDataMmrp.getNumberOfInterruptions());
			assertEquals(0, mcDataMmrp.getPacketLossPerSecond());

	//		assertEquals(0, mcData.getNumberOfInterruptionsAvg());
			assertEquals(0, mcDataMmrp.getPacketLossPerSecondAvg());
			assertEquals(2, mcDataMmrp.getPacketRateAvg());
			assertEquals(packetLength*2, mcDataMmrp.getTrafficAvg());
		}
		
		/**
		 * Testet mit 10 Paketen bei denen 2 in Reihe nicht "empfangen" wurden
		 */
		@Test
		public void testAnalysePacket2Ipv4(){
			setUpIpv4();
			mcData.resetValues();
			packetAnalyzer.setInternerPacketCount(-1);

			byte[][] packets = new byte[10][65575];
			for(int i=0;i<10;i++){
				packets[i] = createPacket();
			}

			packetAnalyzer.analyzePacket(packets[0]);
			packetAnalyzer.analyzePacket(packets[1]);
			packetAnalyzer.analyzePacket(packets[2]);
		//	packetAnalyzer.analyzePacket(packets[3]);
		//	packetAnalyzer.analyzePacket(packets[4]);
			packetAnalyzer.analyzePacket(packets[5]);
			packetAnalyzer.analyzePacket(packets[6]);
			packetAnalyzer.analyzePacket(packets[7]);
			packetAnalyzer.analyzePacket(packets[8]);
			packetAnalyzer.analyzePacket(packets[9]);

			packetAnalyzer.update();

			assertEquals(8, mcData.getPacketCount());
			assertEquals(8, mcData.getPacketRateMeasured());
			assertEquals(0, mcData.getNumberOfInterruptions());
			// Jitter wird nicht getestet....hab mir noch keinen guten Test dafür überlegt
			assertEquals(2, mcData.getPacketLossPerSecond());
			assertEquals(packetLength*8, mcData.getTraffic());

			packetAnalyzer.updateMin();

		//	assertEquals(0, mcData.getNumberOfInterruptionsAvg());
			assertEquals(2, mcData.getPacketLossPerSecondAvg());
			assertEquals(8, mcData.getPacketRateAvg());
			assertEquals(packetLength*8, mcData.getTrafficAvg());
		}

		/**
		 * Testet mit 10 Paketen bei denen 2 in Reihe nicht "empfangen" wurden
		 */
		@Test
		public void testAnalysePacket2Mmrp(){
			setUpMmrp();
			mcDataMmrp.resetValues();
			packetAnalyzerMmrp.setInternerPacketCount(-1);

			byte[][] packets = new byte[10][packetLength];
			for(int i=0;i<10;i++){
				packets[i] = createPacketMmrp();
			}

			packetAnalyzerMmrp.analyzePacket(packets[0]);
			packetAnalyzerMmrp.analyzePacket(packets[1]);
			packetAnalyzerMmrp.analyzePacket(packets[2]);
		//	packetAnalyzerMmrp.analyzePacket(packets[3]);
		//	packetAnalyzerMmrp.analyzePacket(packets[4]);
			packetAnalyzerMmrp.analyzePacket(packets[5]);
			packetAnalyzerMmrp.analyzePacket(packets[6]);
			packetAnalyzerMmrp.analyzePacket(packets[7]);
			packetAnalyzerMmrp.analyzePacket(packets[8]);
			packetAnalyzerMmrp.analyzePacket(packets[9]);

			packetAnalyzerMmrp.update();

			assertEquals(8, mcDataMmrp.getPacketCount());
			assertEquals(8, mcDataMmrp.getPacketRateMeasured());
			assertEquals(0, mcDataMmrp.getNumberOfInterruptions());
			assertEquals(2, mcDataMmrp.getPacketLossPerSecond());
			assertEquals(packetLength*8, mcDataMmrp.getTraffic());

			packetAnalyzerMmrp.updateMin();

			assertEquals(2, mcDataMmrp.getPacketLossPerSecondAvg());
			assertEquals(8, mcDataMmrp.getPacketRateAvg());
			assertEquals(packetLength*8, mcDataMmrp.getTrafficAvg());
		}
		
		/**
		 * Testet auf average-values mit zwei Gruppen von mc-paketen
		 */
		@Test
		public void testAnalysePacket3Ipv4(){
			setUpIpv4();
			mcData.resetValues();
			packetAnalyzer.setInternerPacketCount(-1);

			byte[][] packets = new byte[20][65575];
			for(int i=0;i<20;i++){
				packets[i] = createPacket();
			}

			packetAnalyzer.analyzePacket(packets[0]);
			packetAnalyzer.analyzePacket(packets[1]);
			packetAnalyzer.analyzePacket(packets[2]);
		//	packetAnalyzer.analyzePacket(packets[3]);
		//	packetAnalyzer.analyzePacket(packets[4]);
			packetAnalyzer.analyzePacket(packets[5]);
			packetAnalyzer.analyzePacket(packets[6]);
			packetAnalyzer.analyzePacket(packets[7]);
			packetAnalyzer.analyzePacket(packets[8]);
			packetAnalyzer.analyzePacket(packets[9]);

			packetAnalyzer.update();

			packetAnalyzer.analyzePacket(packets[10]);
		//	packetAnalyzer.analyzePacket(packets[11]);
			packetAnalyzer.analyzePacket(packets[12]);
		//	packetAnalyzer.analyzePacket(packets[13]);
		//	packetAnalyzer.analyzePacket(packets[14]);
			packetAnalyzer.analyzePacket(packets[15]);
		//	packetAnalyzer.analyzePacket(packets[16]);
			packetAnalyzer.analyzePacket(packets[17]);
		//	packetAnalyzer.analyzePacket(packets[18]);
			packetAnalyzer.analyzePacket(packets[19]);

			packetAnalyzer.update();

			assertEquals(13, mcData.getPacketCount());
			assertEquals(5, mcData.getPacketRateMeasured());
			assertEquals(0, mcData.getNumberOfInterruptions());
			// Jitter wird nicht getestet.
			assertEquals(5, mcData.getPacketLossPerSecond());
			assertEquals(packetLength*5, mcData.getTraffic());

			packetAnalyzer.updateMin();

	//		assertEquals(2, mcData.getNumberOfInterruptionsAvg());
			assertEquals(3, mcData.getPacketLossPerSecondAvg());
			assertEquals(6, mcData.getPacketRateAvg());
			assertEquals(packetLength*13/2, mcData.getTrafficAvg());
		}

		/**
		 * Testet auf average-values mit zwei Gruppen von mc-paketen
		 */
		@Test
		public void testAnalysePacket3Mmrp(){
			setUpMmrp();
			mcDataMmrp.resetValues();
			packetAnalyzerMmrp.setInternerPacketCount(-1);

			byte[][] packets = new byte[20][65575];
			for(int i=0;i<20;i++){
				packets[i] = createPacketMmrp();
			}

			packetAnalyzerMmrp.analyzePacket(packets[0]);
			packetAnalyzerMmrp.analyzePacket(packets[1]);
			packetAnalyzerMmrp.analyzePacket(packets[2]);
			//packetAnalyzerMmrp.analyzePacket(packets[3]);
			//packetAnalyzerMmrp.analyzePacket(packets[4]);
			packetAnalyzerMmrp.analyzePacket(packets[5]);
			packetAnalyzerMmrp.analyzePacket(packets[6]);
			packetAnalyzerMmrp.analyzePacket(packets[7]);
			packetAnalyzerMmrp.analyzePacket(packets[8]);
			packetAnalyzerMmrp.analyzePacket(packets[9]);

			packetAnalyzerMmrp.update();

			packetAnalyzerMmrp.analyzePacket(packets[10]);
			//packetAnalyzerMmrp.analyzePacket(packets[11]);
			packetAnalyzerMmrp.analyzePacket(packets[12]);
			//packetAnalyzerMmrp.analyzePacket(packets[13]);
			//packetAnalyzerMmrp.analyzePacket(packets[14]);
			packetAnalyzerMmrp.analyzePacket(packets[15]);
			//packetAnalyzerMmrp.analyzePacket(packets[16]);
			packetAnalyzerMmrp.analyzePacket(packets[17]);
			//packetAnalyzerMmrp.analyzePacket(packets[18]);
			packetAnalyzerMmrp.analyzePacket(packets[19]);

			packetAnalyzerMmrp.update();

			assertEquals(13, mcDataMmrp.getPacketCount());
			assertEquals(5, mcDataMmrp.getPacketRateMeasured());
			assertEquals(0, mcDataMmrp.getNumberOfInterruptions());
			// Jitter wird nicht getestet.
			assertEquals(5, mcDataMmrp.getPacketLossPerSecond());
			assertEquals(packetLength*5, mcDataMmrp.getTraffic());

			packetAnalyzerMmrp.updateMin();

			assertEquals(3, mcDataMmrp.getPacketLossPerSecondAvg());
			assertEquals(6, mcDataMmrp.getPacketRateAvg());
			assertEquals(packetLength*13/2, mcDataMmrp.getTrafficAvg());
		}
		
		/**
		 * Testet ob die Interruptionmessung funktioniert, unter der Vorraussetzung, dass
		 * die update()- und setTimeout()-Methoden richtig verwendet werden.
		 */
		@Test
		public void testAnalysePacket4Ipv4(){
			setUpIpv4();
			mcData.resetValues();

			packetAnalyzer.setTimeout(false);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();

			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(false);

			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();

			assertEquals(1, mcData.getNumberOfInterruptions());
			assertEquals(4, mcData.getMaxInterruptionTime());
			assertEquals(4, mcData.getAverageInterruptionTime());

			packetAnalyzer.setTimeout(false);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();
			packetAnalyzer.update();

			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(true);
			packetAnalyzer.setTimeout(false);

			packetAnalyzer.update();
			assertEquals(2, mcData.getNumberOfInterruptions());
			assertEquals(6, mcData.getMaxInterruptionTime());
			assertEquals(5, mcData.getAverageInterruptionTime());
		}
		
		/**
		 * Testet ob die Interruptionmessung funktioniert, unter der Vorraussetzung, dass
		 * die update()- und setTimeout()-Methoden richtig verwendet werden.
		 */
		@Test
		public void testAnalysePacket4Mmrp(){
			setUpMmrp();
			mcDataMmrp.resetValues();

			packetAnalyzerMmrp.setTimeout(false);
			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.update();
			packetAnalyzerMmrp.update();
			packetAnalyzerMmrp.update();
			packetAnalyzerMmrp.update();

			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.setTimeout(false);

			packetAnalyzerMmrp.update();
			packetAnalyzerMmrp.update();
			packetAnalyzerMmrp.update();

			assertEquals(1, mcDataMmrp.getNumberOfInterruptions());
			assertEquals(4, mcDataMmrp.getMaxInterruptionTime());
			assertEquals(4, mcDataMmrp.getAverageInterruptionTime());

			packetAnalyzerMmrp.setTimeout(false);
			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.update();
			packetAnalyzerMmrp.update();
			packetAnalyzerMmrp.update();
			packetAnalyzerMmrp.update();
			packetAnalyzerMmrp.update();
			packetAnalyzerMmrp.update();

			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.setTimeout(true);
			packetAnalyzerMmrp.setTimeout(false);

			packetAnalyzerMmrp.update();
			assertEquals(2, mcDataMmrp.getNumberOfInterruptions());
			assertEquals(6, mcDataMmrp.getMaxInterruptionTime());
			assertEquals(5, mcDataMmrp.getAverageInterruptionTime());
		}

		/**
		 * Testet was bei falscher Arraygr��e passiert
		 */
		@Test
		public void testAnalysePacket5Ipv4(){
			setUpIpv4();
			mcData.resetValues();

			byte[] array = new byte[50];
			packetAnalyzer.analyzePacket(array);

			packetAnalyzer.update();

			assertEquals(0, mcData.getNumberOfInterruptions());
			assertEquals(0, mcData.getMaxInterruptionTime());
			assertEquals(0, mcData.getAverageInterruptionTime());
		}

		/**
		 * Testet was bei falscher Arraygr��e passiert
		 */
		@Test
		public void testAnalysePacket5Mmrp(){
			setUpMmrp();
			mcDataMmrp.resetValues();

			byte[] array = new byte[60];
			try {
				packetAnalyzerMmrp.analyzePacket(array);
			} catch(IndexOutOfBoundsException e) {
				System.out.println("Test successfully!");
			}
			//packetAnalyzerMmrp.update();

			//assertEquals(0, mcDataMmrp.getNumberOfInterruptions());
			//assertEquals(0, mcDataMmrp.getMaxInterruptionTime());
			//assertEquals(0, mcDataMmrp.getAverageInterruptionTime());
		}
		
		/**
		 * Erzeugt die Pakete gefüllt mit restlichen einsen. Dies ist kein perfektes
		 * Ebenbild der realen Pakete, da diese nicht immer mit 1en gefüllt werden,
		 * sondern nur einmal pro Sekunde.
		 * Dies kann jedoch hierbei außeracht gelassen werden.
		 * @return
		 */
		private byte[] createPacket(){
			byte[] p = packetBuilder.getPacket();
			byte[] packet = new byte[65575];
			int i = 0;
			for(;i<p.length;i++){
				packet[i] = p[i];
			}
			for(;i<65575;i++){
				packet[i] = 1;
			}
			return packet;
		}

		/**
		 * Erzeugt die Pakete gefüllt mit restlichen einsen. Dies ist kein perfektes
		 * Ebenbild der realen Pakete, da diese nicht immer mit 1en gefüllt werden,
		 * sondern nur einmal pro Sekunde.
		 * Dies kann jedoch hierbei außeracht gelassen werden.
		 * @return
		 */
		private byte[] createPacketMmrp(){
			byte[] p = packetBuilderMmrp.getPacket();
			byte[] packet = new byte[p.length];
			int i = 0;
			for(;i<p.length;i++){
				packet[i] = p[i];
			}
			for(;i<p.length;i++){
				packet[i] = 1;
			}
			return packet;
		}
}
