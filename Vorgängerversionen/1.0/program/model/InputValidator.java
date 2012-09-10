package zisko.multicastor.program.model;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import zisko.multicastor.program.data.MulticastData.Typ;

/**
 * 
 * Stellt Methoden zur Validierung von Eingabewerten zu Verfügung
 * @author Johannes Beutel
 *
 */
public class InputValidator {
	
	/**
	 * Checkt die Validität einer IPv4-Hostadresse. Ausgeschlossen sind Multicast-Addressen.
	 * @param adresse Der zu prüfende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Objekt für valide Addressen, null wenn der String keine valide Adresse ist
	 */
	public static InetAddress checkIPv4(String adresse) {
				Inet4Address adr;
				if(!adresse.matches("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})"))
					return null;
				
				try {
					try{
						adr = (Inet4Address) Inet4Address.getByName(adresse);
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
	 * Prüft ob ein valider IPv4-Netzwerkadapter mit der angegebenen Adresse existiert.
	 * @param address Das {@link java.net.InetAddress}-Objekt welches geprüft werden soll
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
	 * Prüft ob ein valider IPv6-Netzwerkadapter mit der angegebenen Adresse existiert.
	 * @param address Das {@link java.net.InetAddress}-Objekt welches geprüft werden soll
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
	 * Checkt die Validität einer IPv6-Hostadresse. Ausgeschlossen sind Multicast-Addressen.
	 * @param adresse Der zu prüfende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object für valide Addressen, null wenn der String keine valide IP-Addresse ist
	 */
	public static InetAddress checkIPv6(String adresse){
		
		boolean bool;
		Inet6Address add;
		

		
		if(!adresse.matches("\\A(?:" +
							"(?:" +
							"(?:[A-Fa-f0-9]{1,4}:){6}" +
							"|" +
							"(?=(?:[A-Fa-f0-9]{0,4}:){0,6}" +
							"(?:[0-9]{1,3}\\.){3}[0-9]{1,3}" +
							"\\Z)" +
							
							"(([0-9a-fA-F]{1,4}:){0,5}|:)((:[0-9a-fA-F]{1,4}){1,5}:|:)" +
							")" +
							"(?:(:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
							"(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" +
							"|(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}" +
							"|(?=(?:[A-Fa-f0-9]{0,4}:){0,7}[A-Fa-f0-9]{0,4}" +
							"\\Z)" +
							
							"(([0-9a-fA-F]{1,4}:){1,7}|:)((:[0-9a-fA-F]{1,4}){1,7}|:))" +
							"\\Z"
							)
		)
			return null;
		
		try {
			try{
				add = (Inet6Address) Inet6Address.getByName(adresse);
			} catch(ClassCastException e)
			{
				return null;
			}
		} catch (UnknownHostException e) {
			return null;
		}
		bool = !add.isMulticastAddress() && !add.getHostAddress().matches("0:0:0:0:0:0:0:0") ;
		if (bool)
			return add;
		else 
			return null;
	}
	
	/**
	 * Prüft die Validität einer IPv4-Multicast-Addresse.
	 * @param adresse Der zu prüfende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object für valide Addressen, null wenn der String keine valide Multicast-IP-Addresse ist
	 */
	public static InetAddress checkMC_IPv4(String adresse){
		Inet4Address adr;
		if(!adresse.matches("([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})\\.+([0-9]{1,3})"))
			return null;
		
		try {
			try{
				adr = (Inet4Address) Inet4Address.getByName(adresse);
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
	 * Prüft die Validität einer IPv6-Multicast-Addresse.
	 * @param adresse Der zu prüfende Wert als {@link java.lang.String}
	 * @return {@link java.net.InetAddress}-Object für valide Addressen, null wenn der String keine valide Multicast-IP-Addresse ist
	 */
	public static InetAddress checkMC_IPv6(String adresse){
		Inet6Address add;
		
		if(!adresse.matches("\\A(?:" +
							"(?:" +
							"(?:[A-Fa-f0-9]{1,4}:){6}" +
							"|" +
							"(?=(?:[A-Fa-f0-9]{0,4}:){0,6}" +
							"(?:[0-9]{1,3}\\.){3}[0-9]{1,3}" +
							"\\Z)" +
							
							"(([0-9a-fA-F]{1,4}:){0,5}|:)((:[0-9a-fA-F]{1,4}){1,5}:|:)" +
							")" +
							"(?:(:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
							"(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)" +
							"|(?:[A-Fa-f0-9]{1,4}:){7}[A-Fa-f0-9]{1,4}" +
							"|(?=(?:[A-Fa-f0-9]{0,4}:){0,7}[A-Fa-f0-9]{0,4}" +
							"\\Z)" +
							
							"(([0-9a-fA-F]{1,4}:){1,7}|:)((:[0-9a-fA-F]{1,4}){1,7}|:))" +
							"\\Z"
							)
		)
			return null;
		
		try {
			try{
				add = (Inet6Address) Inet6Address.getByName(adresse);
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
	 * Prüft die Validität eines UDP-Port
	 * @param port Der zu prüfende Wert als {@link java.lang.String}
	 * @return Port als int für valide Ports, -1 wenn String kein Valider Port ist, -2 wenn keine Zahl
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
	 * Prüft die Validität der Länge eines IPv4-Packet's
	 * @param pacLen Der zu prüfende Wert als {@link java.lang.String}
	 * @return PacketLength als int für eine valide Packetlength, -1 wenn String keine valide Länge, -2 wenn keine Zahl 
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
	 * Prüft die Validität der Länge eines IPv6-Paket's
	 * @param pacLen Der zu prüfende Wert als {@link java.lang.String}
	 * @return PacketLength als int für eine valide Packetlength, -1 wenn String keine valide PacketLength, -2 wenn keine Zahl
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
	 * Prüft die Validität der Anzahl der Hops 
	 * @param ttl Der zu prüfende Wert als {@link java.lang.String}
	 * @return TimeToLive als int, -1 wenn String keine valide TimeToLive ist, -2 wenn keine Zahl
	 */
	public static int checkTimeToLive(String ttl){
		// 1 - 32
		int tl;
		try{
			tl = Integer.parseInt(ttl);
		}catch(NumberFormatException e){
			return -2;
		}
		if (tl>=1 && tl<=32)
			return tl;
		else 
			return -1;
	}
	
	/**
	 * Prüft die Validität der PaketRate
	 * @param pRate Der zu prüfende Wert als {@link java.lang.String}
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
}
