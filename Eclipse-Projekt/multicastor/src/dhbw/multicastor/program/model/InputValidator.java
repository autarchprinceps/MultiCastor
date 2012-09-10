   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenströmen. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, 
 *	weitergeben und/oder modifizieren, gemäß Version 3 der Lizenz.
 *
 *  Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 *  Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 ****************************************************************************************************************************************************************
 *  MultiCastor is a Tool for sending and receiving of Multicast-Data Streams. This project was created for the subject "Software Engineering" at 
 *	Dualen Hochschule Stuttgart under the direction of Markus Rentschler and Andreas Stuckert.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; 
 *  either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
 
 package dhbw.multicastor.program.model;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dhbw.multicastor.program.data.MulticastData.ProtocolType;


/**
 * 
 * Stellt Methoden zur Validierung von Eingabewerten zu Verfägung
 * 
 * @author Johannes Beutel
 * 
 */
public class InputValidator {

	/**
	 * Checkt die Validität einer IPv4-Hostadresse. Ausgeschlossen sind
	 * Multicast-Addressen.
	 * 
	 * @param adresse
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Objekt fär valide Addressen, null
	 *         wenn der String keine valide Adresse ist
	 */
	public static InetAddress checkIPv4(String adresse) {
		if (adresse != null) {
			Inet4Address adr;
			// if (!adresse
			// .matches("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})"))
			// return null;
			Pattern pattern = Pattern
					.compile("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})");
			Matcher mat = pattern.matcher(adresse);
			while (mat.find()) {
				adresse = mat.group(0);
				break;
			}
			// }
			if (!adresse
					.matches("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})")) {
				return null;
			}
			try {
				try {
					adr = (Inet4Address) Inet4Address.getByName(adresse);
				} catch (ClassCastException e) {
					return null;
				}
			} catch (UnknownHostException e) {
				return null;
			}
			if (!adr.isMulticastAddress())
				return adr;
			else
				return null;
		}
		return null;
	}

	/**
	 * Prüft ob ein valider IPv4-Netzwerkadapter mit der angegebenen Adresse
	 * existiert.
	 * 
	 * @param address
	 *            Das {@link java.net.InetAddress}-Objekt welches gepräft werden
	 *            soll
	 * @return boolean Existiert ein Interface mit der gegebenen Adresse true,
	 *         falls kein Interface mit der Adresse existier false
	 */
	public static Boolean checkv4Adapters(InetAddress address) {
		if (NetworkAdapter.findAddressIndex(ProtocolType.IGMP,
				address.toString()) == -1)
			return false;
		else
			return true;
	}

	/**
	 * Prüft ob ein valider IPv6-Netzwerkadapter mit der angegebenen Adresse
	 * existiert.
	 * 
	 * @param address
	 *            Das {@link java.net.InetAddress}-Objekt welches gepräft werden
	 *            soll
	 * @return boolean Existiert ein Interface mit der gegebenen Adresse true,
	 *         falls kein Interface mit der Adresse existier false
	 */
	public static Boolean checkv6Adapters(InetAddress address) {
		if (NetworkAdapter.findAddressIndex(ProtocolType.MLD,
				address.toString()) == -1)
			return false;
		else
			return true;
	}

	/**
	 * Checkt die Validität einer IPv6-Hostadresse. Ausgeschlossen sind
	 * Multicast-Addressen.
	 * 
	 * @param adresse
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object fär valide Addressen, null
	 *         wenn der String keine valide IP-Addresse ist
	 */
	public static InetAddress checkIPv6(String adresse) {
		if (adresse != null) {
			boolean bool;
			Inet6Address add;

			Pattern pattern = Pattern
					.compile("([0-9a-fA-F]{0,4})\\:+([0-9a-fA-F]{0,4})\\:+([0-9a-fA-F]{0,4})\\:+([0-9a-fA-F]{0,4})\\:+([0-9a-fA-F]{0,4})\\:+([0-9a-fA-F]{0,4})\\:+([0-9a-fA-F]{0,4})\\:+([0-9a-fA-F]{0,4})");
			Matcher mat = pattern.matcher(adresse);
			while (mat.find()) {
				adresse = mat.group(0);
				break;
			}
			if (!adresse.matches("\\A(?:" + "(?:" + "(?:[A-Fa-f0-9]{1,4}:){6}"
					+ "|" + "(?=(?:[A-Fa-f0-9]{0,4}:){0,6}"
					+ "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}" + "\\Z)" +

					"(([0-9a-fA-F]{1,4}:){0,5}|:)((:[0-9a-fA-F]{1,4}){1,5}:|:)"
					+ ")" + "(?:(:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
					+ "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
					+ "|(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}"
					+ "|(?=(?:[A-Fa-f0-9]{0,4}:){0,7}[A-Fa-f0-9]{0,4}" + "\\Z)"
					+

					"(([0-9a-fA-F]{1,4}:){1,7}|:)((:[0-9a-fA-F]{1,4}){1,7}|:))"
					+ "\\Z"))
				return null;

			try {
				try {
					add = (Inet6Address) Inet6Address.getByName(adresse);
				} catch (ClassCastException e) {
					return null;
				}
			} catch (UnknownHostException e) {
				return null;
			}
			bool = !add.isMulticastAddress()
					&& !add.getHostAddress().matches("0:0:0:0:0:0:0:0");
			if (bool)
				return add;
			else
				return null;
		}
		return null;
	}

	/**
	 * Checkt die Validität einer MAC-Hostadresse. Ausgeschlossen sind
	 * Multicast-Addressen.
	 * 
	 * @param adresse
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return MAC-Adressen-Objekt fär valide Addressen, null wenn der String
	 *         keine valide Adresse ist
	 */
	public static MacAddress checkMAC(String adresse) {
			if (adresse != null) {
				Pattern pattern = Pattern
						.compile("([0-9a-fA-F]{2}:){5}([0-9a-fA-F]{2})");
				Matcher mat = pattern.matcher(adresse);
				while (mat.find()) {
					adresse = mat.group(0);
					break;
				}
				if (adresse
						.matches("([0-9a-fA-F]{2}:){5}([0-9a-fA-F]{2})")) {
					String[] bytes = adresse.split(":");
					byte[] parsed = new byte[bytes.length];
					for (int x = 0; x < bytes.length; x++) {
						BigInteger temp = new BigInteger(bytes[x], 16);
						byte[] raw = temp.toByteArray();
						parsed[x] = raw[raw.length - 1];
					}

					return new MacAddress(parsed);
				}
			}
			return null;
	}
	/**
	 * Checkt die Validität einer MAC-Group-Address
	 * Range between 01:00:00:00:00:00 to 01:FF:FF:FF:FF:FF
	 * excluded are 01:80:C2:00:00:00 to 01:80:C2:FF:FF:FF and 01:00:5E:00:00:01
	 * 
	 * @param adresse
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return MAC-Adressen-Objekt fär valide Addressen, null wenn der String
	 *         keine valide Adresse ist
	 */
	public static MacAddress checkMACGroupAddress(String adresse) {
			if (adresse != null) {
				Pattern pattern = Pattern
						.compile("01:([0-9a-fA-F]{2}[:-]){4}[0-9a-fA-F]{2}");
				Matcher mat = pattern.matcher(adresse);
				while (mat.find()) {
					adresse = mat.group(0);
					break;
				}
				if (adresse.matches("01:([0-9a-fA-F]{2}[:-]){4}[0-9a-fA-F]{2}") && !adresse
						.matches("01:80:C2:([0-9a-fA-F]{2}[:-]){2}[0-9a-fA-F]{2}") && !adresse
						.matches("01:00:5[Ee]:00:00:01")) {
					String[] bytes = adresse.split(":");
					byte[] parsed = new byte[bytes.length];
					for (int x = 0; x < bytes.length; x++) {
						BigInteger temp = new BigInteger(bytes[x], 16);
						byte[] raw = temp.toByteArray();
						parsed[x] = raw[raw.length - 1];
					}

					return new MacAddress(parsed);
				}
			}
			return null;
	}

	/**
	 * Prüft die Validität einer IPv4-Multicast-Addresse.
	 * 
	 * @param adresse
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object fär valide Addressen, null
	 *         wenn der String keine valide Multicast-IP-Addresse ist
	 */
	public static InetAddress checkMC_IPv4(String adresse) {
		Inet4Address adr;
		// if (!adresse
		// .matches("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})")){
		// return null;
		// }else {
		Pattern pattern = Pattern
				.compile("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})");
		Matcher mat = pattern.matcher(adresse);
		while (mat.find()) {
			adresse = mat.group(0);
			break;
		}
		// }
		if (!adresse
				.matches("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})")) {
			return null;
		}
		try {
			try {
				adr = (Inet4Address) Inet4Address.getByName(adresse);
			} catch (ClassCastException e) {
				return null;
			}
		} catch (UnknownHostException e) {
			return null;
		}
		if (adr.isMulticastAddress())
			return adr;
		else
			return null;
	}

	/**
	 * Prüft die Validität einer IPv6-Multicast-Addresse.
	 * 
	 * @param adresse
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object fär valide Addressen, null
	 *         wenn der String keine valide Multicast-IP-Addresse ist
	 */
	public static InetAddress checkMC_IPv6(String adresse) {
		Inet6Address add;

		if (!adresse.matches("\\A(?:" + "(?:" + "(?:[A-Fa-f0-9]{1,4}:){6}"
				+ "|" + "(?=(?:[A-Fa-f0-9]{0,4}:){0,6}"
				+ "(?:[0-9]{1,3}\\.){3}[0-9]{1,3}" + "\\Z)" +

				"(([0-9a-fA-F]{1,4}:){0,5}|:)((:[0-9a-fA-F]{1,4}){1,5}:|:)"
				+ ")" + "(?:(:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}"
				+ "(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
				+ "|(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}"
				+ "|(?=(?:[A-Fa-f0-9]{0,4}:){0,7}[A-Fa-f0-9]{0,4}" + "\\Z)" +

				"(([0-9a-fA-F]{1,4}:){1,7}|:)((:[0-9a-fA-F]{1,4}){1,7}|:))"
				+ "\\Z"))
			return null;

		try {
			try {
				add = (Inet6Address) Inet6Address.getByName(adresse);
			} catch (ClassCastException e) {
				return null;
			}
		} catch (UnknownHostException e) {
			return null;
		}

		if (add.isMulticastAddress())
			return add;
		else
			return null;
	}

	/**
	 * Prüft die Validität eines UDP-Port
	 * 
	 * @param port
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return Port als int fär valide Ports, -1 wenn String kein Valider Port
	 *         ist, -2 wenn keine Zahl
	 */
	public static int checkPort(String port) {
		// 1-65535
		int prt;
		try {
			prt = Integer.parseInt(port);
		} catch (NumberFormatException e) {
			return -2;
		}
		if ((prt >= 1 && prt <= 65535))
			return prt;
		else
			return -1;
	}

	/**
	 * Prüft die Validität der Länge eines IPv4-Packet's
	 * 
	 * @param pacLen
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return PacketLength als int fär eine valide Packetlength, -1 wenn String
	 *         keine valide Länge, -2 wenn keine Zahl
	 */
	public static int checkIPv4PacketLength(String pacLen) {

		// 52 - 65507
		int plen;
		try {
			plen = Integer.parseInt(pacLen);
		} catch (NumberFormatException e) {
			return -2;
		}
		if (plen >= 52 && plen <= 65507)
			return plen;
		else
			return -1;
	}

	/**
	 * Prüft die Validität der Länge eines IPv6-Paket's
	 * 
	 * @param pacLen
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return PacketLength als int fär eine valide Packetlength, -1 wenn String
	 *         keine valide PacketLength, -2 wenn keine Zahl
	 */
	public static int checkIPv6PacketLength(String pacLen) {

		// 52 - 65527
		int plen;
		try {
			plen = Integer.parseInt(pacLen);
		} catch (NumberFormatException e) {
			return -2;
		}
		if (plen >= 52 && plen <= 65527)
			return plen;
		else
			return -1;
	}

	/**
	 * Prüft die Validität der Länge eines Mmrp-Paket's
	 * 
	 * @param pacLen
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return PacketLength als int fär eine valide Packetlength, -1 wenn String
	 *         keine valide PacketLength, -2 wenn keine Zahl
	 */
	public static int checkMmrpPacketLength(String pacLen) {

		// 84 - 1538
		int plen;
		try {
			plen = Integer.parseInt(pacLen);
		} catch (NumberFormatException e) {
			return -2;
		}
		if (plen >= 52 && plen <= 1500)
			return plen;
		else
			return -1;
	}

	/**
	 * Prüft die Validität der Anzahl der Hops
	 * 
	 * @param ttl
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return TimeToLive als int, -1 wenn String keine valide TimeToLive ist,
	 *         -2 wenn keine Zahl
	 */
	public static int checkTimeToLive(String ttl) {
		// 1 - 255
		int tl;
		try {
			tl = Integer.parseInt(ttl);
		} catch (NumberFormatException e) {
			return -2;
		}
		if (tl >= 1 && tl <= 255)
			return tl;
		else
			return -1;
	}

	/**
	 * Prüft die Validität der PaketRate
	 * 
	 * @param pRate
	 *            Der zu prüfende Wert als {@link java.lang.String}
	 * @return PacketRate als int bei valider PacketRate, -1 wenn string keine
	 *         valide PacketRate ist, -2 wenn keine Zahl
	 */
	public static int checkPacketRate(String pRate) {

		// 1-65536
		int pr;
		try {
			pr = Integer.parseInt(pRate);
		} catch (NumberFormatException e) {
			return -2;
		}
		if (pr >= 1 && pr <= 65535)
			return pr;
		else
			return -1;
	}
	
	/**
	 * Prueft die Validitaet von JoinMT Timer
	 * @param joinMtTimer
	 * 			Der zu pruefende Wert als {@link java.lang.String}
	 * @return JoinMt Timer als int bei valider JoinMt Timer, -1 wenn string keine
	 *         valide JoinMt Timer ist, -2 wenn keine Zahl
	 */
	public static int checkJoinMtTimer(String joinMtTimer){
		
		// -1-65536
		int join;
		try {
			join = Integer.parseInt(joinMtTimer);
		} catch (NumberFormatException e) {
			return -3;
		}
		System.out.println(join);
		if (join >= -1 && join <= Integer.MAX_VALUE)
			return join;
		else
			return -2;
	}
}
