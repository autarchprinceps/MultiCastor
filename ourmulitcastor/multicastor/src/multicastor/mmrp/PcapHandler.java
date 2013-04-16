package multicastor.mmrp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

/**
 * Try to get the device by the MAC address which is given by the user
 */
public class PcapHandler {

	private static List<PcapIf> alldevs;
	private static List<byte[]> alldevsAdress;

	/**
	 * Convert the MAC address which is an byte array to a string
	 * 
	 * @param mac
	 *            which should be convert to a string
	 * @return the converted string
	 */
	public static String byteMACToString(final byte[] mac) {
		if (mac == null) {
			return null;
		}

		final StringBuilder sb = new StringBuilder(18);

		for (final byte b : mac) {
			if (sb.length() > 0) {
				sb.append(':');
			}
			sb.append(String.format("%02x", b));
		}
		return sb.toString();

	}

	/**
	 * Compare two MAC addresses
	 * 
	 * @param a
	 *            is a MAC address
	 * @param b
	 *            is a MAC address
	 * @return true if the MAC addresses are the same. False if not.
	 */
	public static boolean compareMACs(final byte[] a, final byte[] b) {
		boolean sameMAC = true;

		for (int j = 0; j < 6; j++) {
			if (a[j] != b[j]) {
				sameMAC = false;
				break;
			}
		}

		return sameMAC;
	}

	/**
	 * Return the pcap object of the network device
	 * 
	 * @param deviceMACAddress
	 *            of the network device
	 * @return The pcap object of the network device
	 * @throws IOException
	 */
	public static Pcap getPcapInstance(final byte[] deviceMACAddress)
			throws IOException {
		final PcapIf device = getDevice(deviceMACAddress);
		final int snaplen = 64 * 1024;
		final int flags = Pcap.MODE_PROMISCUOUS;
		final int timeout = 10 * 1000;
		final StringBuilder errbuf = new StringBuilder();

		if (device == null) {
			throw new IOException();
		}

		final Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags,
				timeout, errbuf);

		return pcap;
	}

	/**
	 * Return the network device
	 * 
	 * @param deviceMACAddress
	 *            of the network device
	 * @return the network device
	 * @throws IOException
	 */
	private static PcapIf getDevice(final byte[] deviceMACAddress)
			throws IOException {
		if (alldevs == null) {
			alldevs = new ArrayList<PcapIf>(); // Will be filled with
			alldevsAdress = new ArrayList<byte[]>();
			// NICs

			final StringBuilder errbuf = new StringBuilder(); // For any error
																// msgs
			int r = 0;
			try {
				r = Pcap.findAllDevs(alldevs, errbuf);
			} catch (final NoClassDefFoundError e) {
				// System.out.println("[Warning] NoClassDefFoundError. jnetpcap probably not installed.");
				r = 0;
			}
			if ((r == Pcap.NOT_OK) || alldevs.isEmpty()) {
				throw new IOException();
			}
			for (int i = 0; i < alldevs.size(); i++) {
				alldevsAdress.add(alldevs.get(i).getHardwareAddress());
			}
		}

		for (final byte[] address : alldevsAdress) {
			if ((address != null) && compareMACs(deviceMACAddress, address)) {
				return alldevs.get(alldevsAdress.indexOf(address));
			}
		}

		return null;
	}
}
