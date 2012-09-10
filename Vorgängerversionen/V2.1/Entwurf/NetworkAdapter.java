   
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
 
  

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;

import dhbw.multicastor.program.data.MulticastData.ProtocolType;

/**
 * Abstrakte Hilfsklasse welche die Netzwerkadapter des Systems ausliest und nach IPv4 und IPv6 sortiert.
 * @author Daniel Becker
 *
 */
public abstract class NetworkAdapter {
	/**
	 * Vector welcher alle vorgebenen IPv4 Netzwerkadressen im System hï¿½lt.
	 */
	public static Vector<NetworkInterface> ipv4Interfaces=new Vector<NetworkInterface>();
	/**
	 * Vector welcher alle vorgebenen IPv6 Netzwerkadressen im System hï¿½lt.
	 */
	public static Vector<NetworkInterface> ipv6Interfaces=new Vector<NetworkInterface>();
	/**
	 * Vector welcher alle vorgebenen IPv6 Netzwerkadressen im System hï¿½lt.
	 */
	public static Vector<MmrpAdapter> macInterfaces = new Vector<MmrpAdapter>();
	
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
					ipv4Interfaces.add(current);
				}
				//System.out.println("checking: "+currentaddress.toString().substring(1).split("%")[0]);
				//if(InputValidator.checkIPv6(currentaddress.toString().substring(1).split("%")[0])!=null){
				//if(InputValidator.checkIPv6(currentaddress.toString().substring(1))!=null){
				if(InputValidator.checkIPv6(currentaddress.getHostAddress().split("%")[0])!=null){
					ipv6Interfaces.add(current);
				}
			}
			
//			byte[] hardwareAddress = null;
//			try {
//				hardwareAddress = current.getHardwareAddress();
//			} catch (SocketException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			if (hardwareAddress != null) {
//				String result = "";
//				for (int i = 0; i < hardwareAddress.length; i++)
//					result += String.format((i == 0 ? "" : "-") + "%02X",
//							hardwareAddress[i]);
//				if(InputValidator.checkMAC(result)!= null){
//					macInterfaces.add(new MacAddress(InputValidator.checkMAC(result)));
//				}
//				
//			}
		}
	}
	/**
	 * Funktion welche alle vergebenen IPv4 Netzwerkadressen im System als Vector zurï¿½ck gibt.
	 * Es wird vorher kontrolliert ob die Schnittstellen noch vorhanden sind
	 * @return Vector mit IPv4 Adressen
	 */
	public static Vector<NetworkInterface> getipv4Adapters(){
		return ipv4Interfaces;
	}
	/**
	 * Funktion welche alle vergebenen IPv6 Netzwerkadressen im System als Vector zurï¿½ck gibt.
	 * @return Vector mit IPv6 Adressen
	 */
	public static Vector<NetworkInterface> getipv6Adapters(){
		return ipv6Interfaces;
	}
	
	public static Vector<MmrpAdapter> getMacInterfaces() {
		return macInterfaces;
	}
	
	/**
	 * ï¿½berprï¿½ft ob eine bestimmte IP Adresse im System vergeben ist.
	 * @param typ Unterscheidet ob es sich um IPv4 oder IPv6 Adresse handelt.
	 * @param address Adresse welche ï¿½berprï¿½ft werden soll
	 * @return falls die Adresse vergeben ist wird der Index im jeweiligen Vector zurï¿½ckgegeben, ansonsten -1
	 */
	public static int findAddressIndex(ProtocolType typ, String address){
		int ret = -1;
		if(typ == ProtocolType.IGMP){
			for(int i = 0; i < ipv4Interfaces.size() ; i++){
				//System.out.println("comparing index "+i+": \""+ipv4Interfaces.get(i).toString()+"\" against \""+address+"\"");
				if(ipv4Interfaces.get(i).toString().equals(address)){
					//System.out.println("found!");
					ret = i;
				}
			}
		}
		else if(typ == ProtocolType.MLD){
			for(int i = 0; i < ipv6Interfaces.size() ; i++){
				if(ipv6Interfaces.get(i).toString().startsWith(address)){
					ret = i;
				}
			}
		}else if(typ == ProtocolType.MMRP){
			for(int i = 0; i < macInterfaces.size() ; i++){
				if(macInterfaces.get(i).getMacAdress().toString().contains(address)){
					ret = i;
				}
			}
		}
		return ret;
	}
	
}
