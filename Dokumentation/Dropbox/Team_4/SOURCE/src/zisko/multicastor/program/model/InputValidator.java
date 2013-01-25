package zisko.multicastor.program.model;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Stellt Methoden zur Validierung von Eingabewerten zu Verfuegung
 */
public class InputValidator {

	/**
	 * Prueft ob ein valider Netzwerkadapter (IPv4 oder IPv6) mit der
	 * angegebenen Adresse existiert.
	 * 
	 * @param address
	 *            Das {@link java.net.InetAddress}-Objekt welches geprueft
	 *            werden soll
	 * @return boolean Existiert ein Interface mit der gegebenen Adresse true,
	 *         falls kein Interface mit der Adresse existiert false
	 */
	public static Boolean checkAdapters(final InetAddress address) {
		if(NetworkAdapter.findAddressIndex(address.toString().substring(1)) == -1) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Checkt die Validituet einer IPv4-Hostadresse. Ausgeschlossen sind
	 * Multicast-Addressen.
	 * 
	 * @param adresse
	 *            Der zu pruefende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Objekt fuer valide Addressen, null
	 *         wenn der String keine valide Adresse ist
	 */
	public static InetAddress checkIPv4(final String adresse) {
		Inet4Address adr;
		if(!adresse
				.matches("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})")) {
			return null;
		}

		try {
			try {
				adr = (Inet4Address)InetAddress.getByName(adresse);
			} catch(final ClassCastException e) {
				return null;
			}
		} catch(final UnknownHostException e) {
			return null;
		}
		if(!adr.isMulticastAddress()) {
			return adr;
		} else {
			return null;
		}
	}

	/**
	 * Prueft die Validituet der Luenge eines IPv4-Packet's
	 * 
	 * @param pacLen
	 *            Der zu pruefende Wert als {@link java.lang.String}
	 * @return PacketLength als int fuer eine valide Packetlength, -1 wenn
	 *         String keine valide Luenge, -2 wenn keine Zahl
	 */
	public static int checkIPv4PacketLength(final String pacLen) {

		// 52 - 65507
		int plen;
		try {
			plen = Integer.parseInt(pacLen);
		} catch(final NumberFormatException e) {
			return -2;
		}
		if((plen >= 52) && (plen <= 65507)) {
			return plen;
		} else {
			return -1;
		}
	}

	/**
	 * Checkt die Validituet einer IPv6-Hostadresse. Ausgeschlossen sind
	 * Multicast-Addressen.
	 * 
	 * @param adresse
	 *            Der zu pruefende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object fuer valide Addressen, null
	 *         wenn der String keine valide IP-Addresse ist
	 */
	public static InetAddress checkIPv6(final String adresse) {

		boolean bool;
		Inet6Address add;

		if(!adresse.matches("\\A(?:" + "(?:" + "(?:[A-Fa-f0-9]{1,4}:){6}" + "|"
				+ "(?=(?:[A-Fa-f0-9]{0,4}:){0,6}"
				+ "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}" + "\\Z)" +

				"(([0-9a-fA-F]{1,4}:){0,5}|:)((:[0-9a-fA-F]{1,4}){1,5}:|:)"
				+ ")" + "(?:(:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
				+ "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
				+ "|(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}"
				+ "|(?=(?:[A-Fa-f0-9]{0,4}:){0,7}[A-Fa-f0-9]{0,4}" + "\\Z)" +

				"(([0-9a-fA-F]{1,4}:){1,7}|:)((:[0-9a-fA-F]{1,4}){1,7}|:))"
				+ "\\Z")) {
			return null;
		}

		try {
			try {
				add = (Inet6Address)InetAddress.getByName(adresse);
			} catch(final ClassCastException e) {
				return null;
			}
		} catch(final UnknownHostException e) {
			return null;
		}
		bool = !add.isMulticastAddress()
				&& !add.getHostAddress().matches("0:0:0:0:0:0:0:0");
		if(bool) {
			return add;
		} else {
			return null;
		}
	}

	/**
	 * Prueft die Validituet der Luenge eines IPv6-Paket's
	 * 
	 * @param pacLen
	 *            Der zu pruefende Wert als {@link java.lang.String}
	 * @return PacketLength als int fuer eine valide Packetlength, -1 wenn
	 *         String keine valide PacketLength, -2 wenn keine Zahl
	 */
	public static int checkIPv6PacketLength(final String pacLen) {

		// 52 - 65527
		int plen;
		try {
			plen = Integer.parseInt(pacLen);
		} catch(final NumberFormatException e) {
			return -2;
		}
		if((plen >= 52) && (plen <= 65527)) {
			return plen;
		} else {
			return -1;
		}
	}

	/**
	 * Prueft die Validituet einer IPv4-Multicast-Addresse.
	 * 
	 * @param adresse
	 *            Der zu pruefende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object fuer valide Addressen, null
	 *         wenn der String keine valide Multicast-IP-Addresse ist
	 */
	public static InetAddress checkMC_IPv4(final String adresse) {
		Inet4Address adr;
		if(!adresse
				.matches("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})")) {
			return null;
		}

		try {
			try {
				adr = (Inet4Address)InetAddress.getByName(adresse);
			} catch(final ClassCastException e) {
				return null;
			}
		} catch(final UnknownHostException e) {
			return null;
		}
		if(adr.isMulticastAddress()) {
			return adr;
		} else {
			return null;
		}
	}

	/**
	 * Prueft die Validituet einer IPv6-Multicast-Addresse.
	 * 
	 * @param adresse
	 *            Der zu pruefende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object fuer valide Addressen, null
	 *         wenn der String keine valide Multicast-IP-Addresse ist
	 */
	public static InetAddress checkMC_IPv6(final String adresse) {
		Inet6Address add;

		if(!adresse.matches("\\A(?:" + "(?:" + "(?:[A-Fa-f0-9]{1,4}:){6}" + "|"
				+ "(?=(?:[A-Fa-f0-9]{0,4}:){0,6}"
				+ "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}" + "\\Z)" +

				"(([0-9a-fA-F]{1,4}:){0,5}|:)((:[0-9a-fA-F]{1,4}){1,5}:|:)"
				+ ")" + "(?:(:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
				+ "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
				+ "|(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}"
				+ "|(?=(?:[A-Fa-f0-9]{0,4}:){0,7}[A-Fa-f0-9]{0,4}" + "\\Z)" +

				"(([0-9a-fA-F]{1,4}:){1,7}|:)((:[0-9a-fA-F]{1,4}){1,7}|:))"
				+ "\\Z")) {
			return null;
		}

		try {
			try {
				add = (Inet6Address)InetAddress.getByName(adresse);
			} catch(final ClassCastException e) {
				return null;
			}
		} catch(final UnknownHostException e) {
			return null;
		}

		if(add.isMulticastAddress()) {
			return add;
		} else {
			return null;
		}
	}

	/**
	 * Prueft die Validaet der Laenge eines MMRP-Daten Packets
	 * 
	 * @param pacLen
	 *            Der zu pruefende Wert als {@link java.lang.String}
	 * @return PacketLength als int fuer eine valide Packetlength, -1 wenn
	 *         String keine valide PacketLength, -2 wenn keine Zahl
	 */
	public static int checkMMRPPacketLength(final String pacLen) {
		// 52 - 1500
		int plen;
		try {
			plen = Integer.parseInt(pacLen);
		} catch(final NumberFormatException e) {
			return -2;
		}
		if((plen >= 52) && (plen <= 1500)) {
			return plen;
		} else {
			return -1;
		}
	}

	/**
	 * Checkt ob es sich um eine Mac-Multicast Group Adresse handelt oder nicht
	 */
	public static boolean checkMulticastGroup(final String mac) {
		final String adresse = mac.toString();

		if(!adresse.matches("([A-Fa-f0-9]{0,1}(1|3|5|7|9|B|D|F))(:)"
				+ "([A-Fa-f0-9]{0,2}(:)){4}[A-Fa-f0-9]{0,2}")
				|| // Erstmal check auf richtige Standartadresse
				adresse.matches("((f|F){2}(:)){5}(f|F){2}")) {
			// Broadcast
			// Adresse
			// (FF:FF:FF:FF:FF:FF)
			return false;
		}
		return true;
	}

	/**
	 * Prueft die Validituet der PaketRate
	 * 
	 * @param pRate
	 *            Der zu pruefende Wert als {@link java.lang.String}
	 * @return PacketRate als int bei valider PacketRate, -1 wenn string keine
	 *         valide PacketRate ist, -2 wenn keine Zahl
	 */
	public static int checkPacketRate(final String pRate) {

		// 1-65536
		int pr;
		try {
			pr = Integer.parseInt(pRate);
		} catch(final NumberFormatException e) {
			return -2;
		}
		if((pr >= 1) && (pr <= 65535)) {
			return pr;
		} else {
			return -1;
		}
	}

	/**
	 * Prueft die Validituet der PaketRate
	 * 
	 * @param pRate
	 *            Der zu pruefende Wert als {@link java.lang.String}
	 * @return PacketRate als int bei valider PacketRate, -1 wenn string keine
	 *         valide PacketRate ist, -2 wenn keine Zahl
	 */
	public static int checkPacketRateMMRP(final String pRate) {

		// 1-65536
		int pr;
		try {
			pr = Integer.parseInt(pRate);
		} catch(final NumberFormatException e) {
			return -2;
		}
		if((pr >= 10) && (pr <= 65535)) {
			return pr;
		} else {
			return -1;
		}
	}

	/**
	 * Prueft die Validituet eines UDP-Port
	 * 
	 * @param port
	 *            Der zu pruefende Wert als {@link java.lang.String}
	 * @return Port als int fuer valide Ports, -1 wenn String kein Valider Port
	 *         ist, -2 wenn keine Zahl
	 */
	public static int checkPort(final String port) {
		// 1-65535
		int prt;
		try {
			prt = Integer.parseInt(port);
		} catch(final NumberFormatException e) {
			return -2;
		}
		if(((prt >= 1) && (prt <= 65535))) {
			return prt;
		} else {
			return -1;
		}
	}

	/**
	 * Prueft die Validituet der Anzahl der Hops
	 * 
	 * @param ttl
	 *            Der zu pruefende Wert als {@link java.lang.String}
	 * @return TimeToLive als int, -1 wenn String keine valide TimeToLive ist,
	 *         -2 wenn keine Zahl
	 */
	public static int checkTimeToLive(final String ttl) {
		// 1 - 32
		int tl;
		try {
			tl = Integer.parseInt(ttl);
		} catch(final NumberFormatException e) {
			return -2;
		}
		if((tl >= 1) && (tl <= 32)) {
			return tl;
		} else {
			return -1;
		}
	}
}
