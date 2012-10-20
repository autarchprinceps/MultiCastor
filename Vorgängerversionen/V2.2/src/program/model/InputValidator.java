package program.model;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import program.data.MulticastData.Typ;

/**
 *
 * Stellt Methoden zur Validierung von Eingabewerten zu Verf�gung
 * @author Johannes Beutel
 *
 */
public class InputValidator {
	
	public static int mmrpMax=1500;

	/**
	 * Checkt die Validit�t einer IPv4-Hostadresse. Ausgeschlossen sind Multicast-Addressen.
	 * @param adresse Der zu pr�fende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Objekt f�r valide Addressen, null wenn der String keine valide Adresse ist
	 */
	public static InetAddress checkIPv4(String adresse) {
				Inet4Address adr;
				if(!adresse.matches("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})")) //$NON-NLS-1$
					return null;

				try {
					try{
						adr = (Inet4Address) InetAddress.getByName(adresse);
					} catch(ClassCastException e)
					{
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
	/**
	 * Funktion die Eine eingegebene Multicast MAC Adresse validiert
	 * @param address
	 * @return
	 */
	public static boolean checkMC_MMRP(String address) {
		if(address.matches("((01:00:5[eE]:[0-7][a-fA-F0-9]:[a-fA-F0-9]{2}:[a-fA-F0-9]{2})|(01-00-5[eE]-[0-7][a-fA-F0-9]-[a-fA-F0-9]{2}-[a-fA-F0-9]{2}))")) //$NON-NLS-1$
			return true;
		else
			return false;
	}
	/**
	 * Funktion die Eine eingegebene MAC Adresse validiert
	 * @param address
	 * @return
	 */
	public static boolean check_MMRP(String address){
		if(!address.matches("(([a-fA-F0-9]{2}:[a-fA-F0-9]{2}:[a-fA-F0-9]{2}:[a-fA-F0-9]{2}:[a-fA-F0-9]{2}:[a-fA-F0-9]{2})|([a-fA-F0-9]{2}-[a-fA-F0-9]{2}-[a-fA-F0-9]{2}-[a-fA-F0-9]{2}-[a-fA-F0-9]{2}-[a-fA-F0-9]{2}))")) //$NON-NLS-1$
			{return false;} //$NON-NLS-1$
		else
			{return true;} //$NON-NLS-1$
	}

	/**
	 * Pr�ft ob ein valider IPv4-Netzwerkadapter mit der angegebenen Adresse existiert.
	 * @param address Das {@link java.net.InetAddress}-Objekt welches gepr�ft werden soll
	 * @return boolean Existiert ein Interface mit der gegebenen Adresse true, falls kein Interface mit der Adresse existier false
	 */
	public static Boolean checkv4Adapters(InetAddress address)
	{
		if(NetworkAdapter.findAddressIndex(Typ.SENDER_V4,address.toString() )==-1)
			return false;
		else
			return true;
	}

	/**
	 * Pr�ft ob ein valider IPv6-Netzwerkadapter mit der angegebenen Adresse existiert.
	 * @param address Das {@link java.net.InetAddress}-Objekt welches gepr�ft werden soll
	 * @return boolean Existiert ein Interface mit der gegebenen Adresse true, falls kein Interface mit der Adresse existier false
	 */
	public static Boolean checkv6Adapters(InetAddress address)
	{
		if(NetworkAdapter.findAddressIndex(Typ.SENDER_V6,address.toString())==-1)
			return false;
		else
			return true;
	}

	/**
	 * Checkt die Validit�t einer IPv6-Hostadresse. Ausgeschlossen sind Multicast-Addressen.
	 * @param adresse Der zu pr�fende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object f�r valide Addressen, null wenn der String keine valide IP-Addresse ist
	 */
	public static InetAddress checkIPv6(String adresse){

		boolean bool;
		Inet6Address add;



		if(!adresse.matches("\\A(?:" + //$NON-NLS-1$
							"(?:" + //$NON-NLS-1$
							"(?:[A-Fa-f0-9]{1,4}:){6}" + //$NON-NLS-1$
							"|" + //$NON-NLS-1$
							"(?=(?:[A-Fa-f0-9]{0,4}:){0,6}" + //$NON-NLS-1$
							"(?:[0-9]{1,3}\\.){3}[0-9]{1,3}" + //$NON-NLS-1$
							"\\Z)" + //$NON-NLS-1$

							"(([0-9a-fA-F]{1,4}:){0,5}|:)((:[0-9a-fA-F]{1,4}){1,5}:|:)" + //$NON-NLS-1$
							")" + //$NON-NLS-1$
							"(?:(:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" + //$NON-NLS-1$
							"(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" + //$NON-NLS-1$
							"|(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}" + //$NON-NLS-1$
							"|(?=(?:[A-Fa-f0-9]{0,4}:){0,7}[A-Fa-f0-9]{0,4}" + //$NON-NLS-1$
							"\\Z)" + //$NON-NLS-1$

							"(([0-9a-fA-F]{1,4}:){1,7}|:)((:[0-9a-fA-F]{1,4}){1,7}|:))" + //$NON-NLS-1$
							"\\Z" //$NON-NLS-1$
							)
		)
			return null;

		try {
			try{
				add = (Inet6Address) InetAddress.getByName(adresse);
			} catch(ClassCastException e)
			{
				return null;
			}
		} catch (UnknownHostException e) {
			return null;
		}
		bool = !add.isMulticastAddress() && !add.getHostAddress().matches("0:0:0:0:0:0:0:0") ; //$NON-NLS-1$
		if (bool)
			return add;
		else
			return null;
	}

	/**
	 * Pr�ft die Validit�t einer IPv4-Multicast-Addresse.
	 * @param adresse Der zu pr�fende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object f�r valide Addressen, null wenn der String keine valide Multicast-IP-Addresse ist
	 */
	public static InetAddress checkMC_IPv4(String adresse){
		Inet4Address adr;
		if(!adresse.matches("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})")) //$NON-NLS-1$
			return null;

		try {
			try{
				adr = (Inet4Address) InetAddress.getByName(adresse);
			} catch(ClassCastException e)
			{
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
	 * Pr�ft die Validit�t einer IPv6-Multicast-Addresse.
	 * @param adresse Der zu pr�fende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object f�r valide Addressen, null wenn der String keine valide Multicast-IP-Addresse ist
	 */
	public static InetAddress checkMC_IPv6(String adresse){
		Inet6Address add;

		if(!adresse.matches("\\A(?:" + //$NON-NLS-1$
							"(?:" + //$NON-NLS-1$
							"(?:[A-Fa-f0-9]{1,4}:){6}" + //$NON-NLS-1$
							"|" + //$NON-NLS-1$
							"(?=(?:[A-Fa-f0-9]{0,4}:){0,6}" + //$NON-NLS-1$
							"(?:[0-9]{1,3}\\.){3}[0-9]{1,3}" + //$NON-NLS-1$
							"\\Z)" + //$NON-NLS-1$

							"(([0-9a-fA-F]{1,4}:){0,5}|:)((:[0-9a-fA-F]{1,4}){1,5}:|:)" + //$NON-NLS-1$
							")" + //$NON-NLS-1$
							"(?:(:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" + //$NON-NLS-1$
							"(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" + //$NON-NLS-1$
							"|(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}" + //$NON-NLS-1$
							"|(?=(?:[A-Fa-f0-9]{0,4}:){0,7}[A-Fa-f0-9]{0,4}" + //$NON-NLS-1$
							"\\Z)" + //$NON-NLS-1$

							"(([0-9a-fA-F]{1,4}:){1,7}|:)((:[0-9a-fA-F]{1,4}){1,7}|:))" + //$NON-NLS-1$
							"\\Z" //$NON-NLS-1$
							)
		)
			return null;

		try {
			try{
				add = (Inet6Address) InetAddress.getByName(adresse);
			} catch(ClassCastException e)
			{
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
	 * Pr�ft die Validit�t eines UDP-Port
	 * @param port Der zu pr�fende Wert als {@link java.lang.String}
	 * @return Port als int f�r valide Ports, -1 wenn String kein Valider Port ist, -2 wenn keine Zahl
	 */
	public static int checkPort(String port){
		//1-65535
		int prt;
		try{
			prt = Integer.parseInt(port);
		}catch(NumberFormatException e){
			return -2;
		}
		if ((prt>=1 && prt<=65535))
			return prt;
		else
			return -1;
	}

	/**
	 * Pr�ft die Validit�t der L�nge eines IPv4-Packet's
	 * @param pacLen Der zu pr�fende Wert als {@link java.lang.String}
	 * @return PacketLength als int f�r eine valide Packetlength, -1 wenn String keine valide L�nge, -2 wenn keine Zahl
	 */
	public static int checkIPv4PacketLength(String pacLen){

		// 52 - 65507
		int plen;
		try{
			plen = Integer.parseInt(pacLen);
		}catch(NumberFormatException e){
			return -2;
		}
		if (plen>=52 && plen<=65507)
			return plen;
		else return -1;
	}

	/**
	 * Pr�ft die Validit�t der L�nge eines IPv6-Paket's
	 * @param pacLen Der zu pr�fende Wert als {@link java.lang.String}
	 * @return PacketLength als int f�r eine valide Packetlength, -1 wenn String keine valide PacketLength, -2 wenn keine Zahl
	*/
	public static int checkIPv6PacketLength(String pacLen){

		// 52 - 65527
		int plen;
		try{
			plen = Integer.parseInt(pacLen);
		}catch(NumberFormatException e){
			return -2;
		}
		if (plen>=52&& plen<=65527)
			return plen;
		else
			return -1;
	}

	/**
	 * Pr�ft die Validit�t der L�nge eines MMRP-Paket's
	 * @param pacLen Der zu pr�fende Wert als {@link java.lang.String}
	 * @return PacketLength als int f�r eine valide Packetlength, -1 wenn String keine valide PacketLength, -2 wenn keine Zahl
	*/
	public static int checkMmrpPacketLength(String pacLen){
		// 62 - 1500

		
		int plen;
		try{
			plen = Integer.parseInt(pacLen);
		}catch(NumberFormatException e){
			return -2;
		}
		if (plen>=62&& plen<=mmrpMax)
			return plen;
		else
			return -1;
	}
	/**
	 * Pr�ft die Validit�t der Anzahl der Hops
	 * @param ttl Der zu pr�fende Wert als {@link java.lang.String}
	 * @return TimeToLive als int, -1 wenn String keine valide TimeToLive ist, -2 wenn keine Zahl
	 */
	public static int checkTimeToLive(String ttl){
		// 1 - 127
		int tl;
		try{
			tl = Integer.parseInt(ttl);
		}catch(NumberFormatException e){
			return -2;
		}
		if (tl>=1 && tl<=127)
			return tl;
		else
			return -1;
	}

	/**
	 * Pr�ft die Validit�t der PaketRate
	 * @param pRate Der zu pr�fende Wert als {@link java.lang.String}
	 * @return PacketRate als int bei valider PacketRate, -1 wenn string keine valide PacketRate ist, -2 wenn keine Zahl
	 */
	public static int checkPacketRate(String pRate){

		//1-65536
		int pr;
		try{
			pr = Integer.parseInt(pRate);
		}catch(NumberFormatException e){
			return -2;
		}
		if (pr>=1 && pr<= 65535)
			return pr;
		else
			return -1;
	}

	public static void setMmrpMax(String networkInterfaceName){
		try {
			mmrpMax=NetworkInterface.getByName(networkInterfaceName).getMTU();
		} catch (SocketException e) {
			mmrpMax=1500;
		} catch (NullPointerException e){
			mmrpMax=1500;
		}
	}
}

