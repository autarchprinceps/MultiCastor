package zisko.multicastor.program.model;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;

import zisko.multicastor.program.controller.ViewController.MessageTyp;
import zisko.multicastor.program.data.MulticastData.Typ;
/**
 * Abstrakte Hilfsklasse welche die Netzwerkadapter des Systems ausliest und nach IPv4 und IPv6 sortiert.
 * @author Daniel Becker
 *
 */
public abstract class NetworkAdapter {
	/**
	 * Vector welcher alle vorgebenen IPv4 Netzwerkadressen im System hält.
	 */
	public static Vector<InetAddress> ipv4Interfaces=new Vector<InetAddress>();
	/**
	 * Vector welcher alle vorgebenen IPv6 Netzwerkadressen im System hält.
	 */
	public static Vector<InetAddress> ipv6Interfaces=new Vector<InetAddress>();
	
	/**
	 * Static Block welcher zu Beginn des Programms die Netzwerk Adapter ausliest und auf die beiden Vectoren aufteilt.
	 */
	static{
		Enumeration<NetworkInterface> adapters = null;
		NetworkInterface current=null;
		try {
			adapters = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(adapters.hasMoreElements()){
			current = adapters.nextElement();
//			if(current.getDisplayName().contains("Microsoft") || current.getDisplayName().contains("Loop")){
//				continue;
//			}
			Enumeration<InetAddress> addresses = current.getInetAddresses();
			while(addresses.hasMoreElements()){
				InetAddress currentaddress = addresses.nextElement();
				//System.out.println("checking: "+currentaddress.toString().substring(1));
				//if(InputValidator.checkIPv4(currentaddress.toString().substring(1))!=null){
				if(InputValidator.checkIPv4(currentaddress.getHostAddress())!=null){
					ipv4Interfaces.add(currentaddress);
				}
				//System.out.println("checking: "+currentaddress.toString().substring(1).split("%")[0]);
				//if(InputValidator.checkIPv6(currentaddress.toString().substring(1).split("%")[0])!=null){
				//if(InputValidator.checkIPv6(currentaddress.toString().substring(1))!=null){
				if(InputValidator.checkIPv6(currentaddress.getHostAddress().split("%")[0])!=null){
					ipv6Interfaces.add(currentaddress);
				}
			}
		}
	}
	/**
	 * Funktion welche alle vergebenen IPv4 Netzwerkadressen im System als Vector zurück gibt.
	 * @return Vector mit IPv4 Adressen
	 */
	public static Vector getipv4Adapters(){
		return ipv4Interfaces;
	}
	/**
	 * Funktion welche alle vergebenen IPv6 Netzwerkadressen im System als Vector zurück gibt.
	 * @return Vector mit IPv6 Adressen
	 */
	public static Vector getipv6Adapters(){
		return ipv6Interfaces;
	}
	/**
	 * Überprüft ob eine bestimmte IP Adresse im System vergeben ist.
	 * @param typ Unterscheidet ob es sich um IPv4 oder IPv6 Adresse handelt.
	 * @param address Adresse welche überprüft werden soll
	 * @return falls die Adresse vergeben ist wird der Index im jeweiligen Vector zurückgegeben, ansonsten -1
	 */
	public static int findAddressIndex(Typ typ, String address){
		int ret = -1;
		if(typ == Typ.SENDER_V4 || typ == Typ.RECEIVER_V4){
			for(int i = 0; i < ipv4Interfaces.size() ; i++){
				//System.out.println("comparing index "+i+": \""+ipv4Interfaces.get(i).toString()+"\" against \""+address+"\"");
				if(ipv4Interfaces.get(i).toString().equals(address)){
					//System.out.println("found!");
					ret = i;
				}
			}
		}
		else if(typ == Typ.SENDER_V6 || typ == Typ.RECEIVER_V6){
			for(int i = 0; i < ipv6Interfaces.size() ; i++){
				if(ipv6Interfaces.get(i).toString().startsWith(address)){
					ret = i;
				}
			}
		}
		return ret;
	}
}
