package com.ibm.MMRP;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.tcpip.Tcp;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with
		// NICs
		StringBuilder errbuf = new StringBuilder(); // For any error msgs

		/***************************************************************************
		 * First get a list of devices on this system
		 **************************************************************************/
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s",
					errbuf.toString());
			return;
		}
		
		PcapIf device = alldevs.get(0); // We know we have atleast 1 device
		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10 * 1000; // 10 seconds in millis
		Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout,
				errbuf);

		/***************************************************************************
		 * Third we create our crude packet we will transmit out This creates a
		 * broadcast packet
		 **************************************************************************/
		MMRPBuilder builder = new MMRPBuilder();
		byte[] a = new byte[6];
		a[0] = (byte) 0x00;
		a[1] = (byte) 0x27;
		a[2] = (byte) 0x13;
		a[3] = (byte) 0x69;
		a[4] = (byte) 0xab;
		a[5] = (byte) 0x63;
		
		byte[] c = builder.getPacket(a, device, MMRPBuilder.mac, false, a, MMRPBuilder.joinEmpty);

		ByteBuffer b = ByteBuffer.wrap(c);

		/***************************************************************************
		 * Fourth We send our packet off using open device
		 **************************************************************************/
		if (pcap.sendPacket(b) != Pcap.OK) {
			System.err.println(pcap.getErr());
		}
		
		/***************************************************************************
		 * Lastly we close
		 **************************************************************************/

		pcap.close();
		
		
	}

}
