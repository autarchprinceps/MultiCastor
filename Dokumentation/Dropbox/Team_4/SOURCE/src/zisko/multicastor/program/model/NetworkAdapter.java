package zisko.multicastor.program.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import zisko.multicastor.program.lang.LanguageManager;

/**
 * Abstrakte Hilfsklasse welche die Netzwerkadapter des Systems ausliest und
 * nach IPv4 und IPv6 sortiert.
 */
public abstract class NetworkAdapter {

	/**
	 * Network Type
	 */
	public enum IPType {
		IPv4, IPv6, MAC
	}

	public static boolean hasJpcapMissing = false;

	/**
	 * Vector welcher alle vorgebenen IPv4 Netzwerkadressen im System huelt.
	 */
	public static Vector<InetAddress> ipv4Interfaces = new Vector<InetAddress>();
	/**
	 * Vector welcher alle vorgebenen IPv6 Netzwerkadressen im System huelt.
	 */
	public static Vector<InetAddress> ipv6Interfaces = new Vector<InetAddress>();
	/**
	 * Vector welcher alle vorgebenen MAP Netzwerkadressen im System huelt.
	 */
	public static Vector<byte[]> macInterfaces = new Vector<byte[]>();
	public static Vector<String> macInterfacesName = new Vector<String>();

	/**
	 * Static Block welcher zu Beginn des Programms die Netzwerk Adapter
	 * ausliest und auf die beiden Vectoren aufteilt.
	 */
	static {

		final LanguageManager lang = LanguageManager.getInstance();

		Enumeration<NetworkInterface> adapters = null;
		NetworkInterface current = null;
		try {
			adapters = NetworkInterface.getNetworkInterfaces();
		} catch(final SocketException e) {
			e.printStackTrace();
		}
		while(adapters.hasMoreElements()) {

			current = adapters.nextElement();
			final Enumeration<InetAddress> addresses = current
					.getInetAddresses();

			while(addresses.hasMoreElements()) {

				final InetAddress currentaddress = addresses.nextElement();
				if(InputValidator.checkIPv4(currentaddress.getHostAddress()) != null) {
					ipv4Interfaces.add(currentaddress);
				}

				if(InputValidator.checkIPv6(currentaddress.getHostAddress()
						.split("%")[0]) != null) {
					ipv6Interfaces.add(currentaddress);
				}
			}
			try {
				if((current.getHardwareAddress() != null)
						&& (current.getHardwareAddress().length == 6)) {
					macInterfaces.add(current.getHardwareAddress());
					macInterfacesName.add(current.getDisplayName());
				}
			} catch(final SocketException e) {

			}
		}

		// New Code
		final ArrayList<PcapIf> alldevs = new ArrayList<PcapIf>();
		final Vector<byte[]> tmpMacAdress = new Vector<byte[]>();
		final Vector<String> tmpNameList = new Vector<String>();
		int macCounter = 0;

		final StringBuilder errbuf = new StringBuilder(); // For any error msgs
		int r;
		try {
			r = Pcap.findAllDevs(alldevs, errbuf);
		} catch(final UnsatisfiedLinkError e) {
			// System.out.println(lang.getProperty("message.unsatisfiedLinkError"));
			r = 0;
			hasJpcapMissing = true;
		}
		if(!(r == Pcap.NOT_OK) && !(alldevs.isEmpty())) {
			for(final PcapIf p : alldevs) {
				try {
					if(p.getHardwareAddress() != null) {
						String name = NetworkAdapter.getNameToMacAdress(p
								.getHardwareAddress());
						tmpMacAdress.add(p.getHardwareAddress());
						if(name == null) {
							// p.getName() looks cryptic under windows
							// Therefore also Use the Dev 0,1,2,...
							name = lang.getProperty("message.device") + " "
									+ macCounter + "(" + p.getName() + ")";
							macCounter++;
						}
						tmpNameList.add(name);
					}
				} catch(final IOException e) {

				}
			}
			macInterfaces = tmpMacAdress;
			macInterfacesName = tmpNameList;
		} else {
			// System.out.println(lang.getProperty("message.loadDeviceFail"));
			hasJpcapMissing = true;
		}
	}

	/**
	 * ueberprueft ob eine bestimmte IP Adresse im System vergeben ist.
	 * 
	 * @param address
	 *            Adresse welche ueberprueft werden soll
	 * @return falls die Adresse vergeben ist wird der Index im jeweiligen
	 *         Vector zurueckgegeben, ansonsten -1
	 */
	public static int findAddressIndex(final String address) {
		int ret = -1;
		if(getAddressType(address) == IPType.IPv4) {
			for(int i = 0; i < ipv4Interfaces.size(); i++) {
				if(ipv4Interfaces.get(i).toString().substring(1)
						.equals(address)) {
					ret = i;
				}
			}
		} else if(getAddressType(address) == IPType.IPv6) {
			for(int i = 0; i < ipv6Interfaces.size(); i++) {
				if(ipv6Interfaces.get(i).toString().substring(1)
						.startsWith(address)) {
					ret = i;
				}
			}
		}
		return ret;
	}

	public static int findAddressIndexMMRP(final String address) {
		int ret = -1;
		for(int i = 0; i < macInterfaces.size(); i++) {
			if(Arrays.toString(macInterfaces.get(i)).equals(address)) {
				ret = i;
			}
		}
		return ret + 1;
	}

	public static IPType getAddressType(final String address) {
		if((InputValidator.checkMC_IPv4(address) != null)
				|| (InputValidator.checkIPv4(address) != null)) {
			return IPType.IPv4;
		} else if((InputValidator.checkMC_IPv6(address) != null)
				|| (InputValidator.checkIPv6(address) != null)) {
			return IPType.IPv6;
		}
		return null;
	}

	/**
	 * Funktion welche alle vergebenen IPv4 Netzwerkadressen im System als
	 * Vector zurueck gibt.
	 * 
	 * @return Vector mit IPv4 Adressen
	 */
	public static Vector<InetAddress> getipv4Adapters() {
		return ipv4Interfaces;
	}

	/**
	 * Funktion welche alle vergebenen IPv6 Netzwerkadressen im System als
	 * Vector zurueck gibt.
	 * 
	 * @return Vector mit IPv6 Adressen
	 */
	public static Vector<InetAddress> getipv6Adapters() {
		return ipv6Interfaces;
	}

	public static Vector<String> getMacAdapterNames() {
		return macInterfacesName;
	}

	/**
	 * Funktion welche die Mac Netzwerkadressen im System als Vector zurueck
	 * gibt.
	 * 
	 * @return Vector mit Mac Adressen
	 */
	public static Vector<byte[]> getMacAdapters() {
		return macInterfaces;
	}

	public static byte[] getMacToMMRP(final int sourceMac) {
		return macInterfaces.get(sourceMac);
	}

	public static byte[] getMacToMMRP(final String sourceMac) {
		return macInterfaces.get(macInterfacesName.indexOf(sourceMac));
	}

	public static String getNameToMacAdress(final byte[] mac) {
		for(int i = 0; i < macInterfaces.size(); i++) {
			if(Arrays.equals(macInterfaces.get(i), mac)) {
				return macInterfacesName.get(i);
			}
		}
		return null;
	}
}
