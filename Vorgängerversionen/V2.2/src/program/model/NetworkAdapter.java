package program.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import program.data.MulticastData.Typ;
/**
 * Abstrakte Hilfsklasse welche die Netzwerkadapter des Systems ausliest und nach IPv4 und IPv6 sortiert.
 * @author Daniel Becker
 *
 */
public abstract class NetworkAdapter {
	/**
	 * Vector welcher alle vorgebenen IPv4 Netzwerkadressen im System h�lt.
	 */
	public static Vector<InetAddress> ipv4Interfaces=new Vector<InetAddress>();
	/**
	 * Vector welcher alle vorgebenen IPv6 Netzwerkadressen im System h�lt.
	 */
	public static Vector<InetAddress> ipv6Interfaces=new Vector<InetAddress>();
	/**
	 * Vector welcher alle vorgebenen MMRP Interfaces hält.
	 */
	private static Vector<PcapIf> mmrpInterfaces = new Vector<PcapIf>();

	private static boolean pcapOk = true;


	/**
	 * Static Block welcher zu Beginn des Programms die Netzwerk Adapter ausliest und auf die beiden Vectoren aufteilt.
	 */
	static{
		//pcap for mmrp
		StringBuilder errbuf = new StringBuilder(); // For any error msgs

		/***************************************************************************
		 * First get a list of devices on this system
		 **************************************************************************/
		int r = Pcap.findAllDevs(mmrpInterfaces, errbuf);
		for(int i=0;mmrpInterfaces.size()>i;i++){
			try {
				if(mmrpInterfaces.get(i).getHardwareAddress()==null){
					mmrpInterfaces.remove(i);
					i--;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (r == Pcap.NOT_OK || mmrpInterfaces.isEmpty()) {
			pcapOk=false;
		} /*else{
			for (PcapIf pcapIf : mmrpInterfaces) {

				System.out.println(pcapIf.getName()+"\n"+pcapIf.getDescription());
			}
		}*/
		//ip
		Enumeration<NetworkInterface> adapters = null;
		NetworkInterface current=null;
		try {
			adapters = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			System.err.println("Unexpecte Exception: could not load interface list.");
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
				if(InputValidator.checkIPv6(currentaddress.getHostAddress().split("%")[0])!=null){ //$NON-NLS-1$
					ipv6Interfaces.add(currentaddress);
				}
			}
		}
	}
	public static boolean isPcapOk() {
		return pcapOk;
	}
	public static void setPcapOk(boolean pcapOk) {
		NetworkAdapter.pcapOk = pcapOk;
	}
	/**
	 * Funktion welche alle vergebenen IPv4 Netzwerkadressen im System als Vector zur�ck gibt.
	 * @return Vector mit IPv4 Adressen
	 */
	public static Vector<InetAddress> getipv4Adapters(){
		return ipv4Interfaces;
	}
	/**
	 * Funktion welche alle vergebenen IPv6 Netzwerkadressen im System als Vector zur�ck gibt.
	 * @return Vector mit IPv6 Adressen
	 */
	public static Vector<InetAddress> getipv6Adapters(){
		return ipv6Interfaces;
	}
	/**
	 * Funktion welche alle vergebenen MMRP Netzwerkadressen im System als Vector zur�ck gibt.
	 * @return Vector mit MMRP Adressen
	 */
	public static Vector<PcapIf> getMmrpAdapters(){
		return mmrpInterfaces;
	}
	/**
	 * Wandelt ein 6 Byte array in einen Mac Adressen String um
	 * @param byteArray mit 6 Byte
	 * @return MacAdressen String im Format: 00:00:00:00:00:00
	 */
	public static String byteArrayToMac(byte[] byteArray){
		if(byteArray.length!=6){
			return null;
		}
		String tmpString=""; //$NON-NLS-1$
		// Macht aus Byte[] einen MAc-Adressen String
		for(int j=0;j<6;j++){
			tmpString += String.format("%02x",byteArray[j]); //$NON-NLS-1$
			if(j!=5) tmpString += ":"; //$NON-NLS-1$
		}
		return tmpString;
	}
	/**
	 * Wandelt beliebig lange Byte array in einen Mac Adressen String um
	 * @param ByteArray
	  	 * @return String im format 00 00 00 00 00 00 00 00
	 */
	public static String byteArrayToString(byte[] byteArray){
		String tmpString="ByteArray["+byteArray.length+"]"; //$NON-NLS-1$ //$NON-NLS-2$
		for(int j=0;j<byteArray.length;j++){
			if(j%8==0){
				tmpString+="\n"; //$NON-NLS-1$
			}
			tmpString += String.format("%02x",byteArray[j]); //$NON-NLS-1$
			tmpString += " "; //$NON-NLS-1$
		}
		return tmpString;
	}
	/**
	 * Wandelt einen String der Form 00:00:00:00:00 oder 00-00-00-00-00 in ein MacAdressen byte array um
	 * @param String der Mac Adresse enthält
	 * @return byte[6] Array das Mac Adresse enthält(Achtung da Byte= 8 bit sind Zahlen in Vorzeichenbehafeter Java Darstellung negativ)
	 */
		public static byte[] macToByteArray(String macAddress){
		byte[] tmpArray = new byte[6];
		for(int i=0;i<6;i++){
			tmpArray[i] =(byte) Integer.parseInt(macAddress.substring(i*3,i*3+2), 16);
		}
		return tmpArray;
	}
	/**
	 * �berpr�ft ob eine bestimmte IP Adresse im System vergeben ist.
	 * @param typ Unterscheidet ob es sich um IPv4 oder IPv6 Adresse handelt.
	 * @param address Adresse welche �berpr�ft werden soll
	 * @return falls die Adresse vergeben ist wird der Index im jeweiligen Vector zur�ckgegeben, ansonsten -1
	 * @throws IOException 
	 */
	public static int findAddressIndex(Typ typ, String address){
		int ret = -1;
		if(typ == Typ.SENDER_V4 || typ == Typ.RECEIVER_V4){
			for(int i = 0; i < ipv4Interfaces.size() ; i++){
	
				if(ipv4Interfaces.get(i).toString().endsWith(address)){
					ret = i;
				}
			}
		}
		else if(typ == Typ.SENDER_V6 || typ == Typ.RECEIVER_V6){
			for(int i = 0; i < ipv6Interfaces.size() ; i++){
				if(ipv6Interfaces.get(i).toString().contains(address)){
					ret = i;
				}
			}
		}
		else if(typ == Typ.SENDER_MMRP || typ == Typ.RECEIVER_MMRP){
			for(int i = 0; i < mmrpInterfaces.size() ; i++){
				try {if(byteArrayToMac(mmrpInterfaces.get(i).getHardwareAddress()).endsWith(address.toLowerCase())){
						ret = i;
					}
				} catch (IOException e) {
					System.out.println("IO Exception in NetworkAdapter: findAddresBy()"); //$NON-NLS-1$
				}
			}
		}
		return ret;
	}
}
