package zisko.multicastor.program.model;

import zisko.multicastor.program.data.MulticastData;


/**
 * Eine Klasse zum Erstellen des Byte-Arrays, das per Multicast versendet wird.
 *
 */
public class PacketBuilder implements zisko.multicastor.program.interfaces.PacketBuilderInterface{
	
	private String hostID   ="undefined";
	private String randomID = null;
	private boolean reset   = false;
	
	private int senderID    = 0,
	            txPktCnt    = 0,
	            txPktRate   = 1,
	            pktLength   = 0,
	            ttl		    = 0;
	
	private byte[] buf,
				   bufForCRC16;
	
	/**
	 * Einziger Konstruktor. Extrahiert alle benoetigten Daten aus dem MultiCastData-Object
	 * und erstellt ein neues Paket. Dieses Paket wird beibehalten und nur noch minimal ab-
	 * geaendert, wenn mit getPacket() ein neues Paket angefordert wird. Das Erstellen von
	 * mehreren Paketen mit derselben Instanz ist nicht moeglich
	 * @param mcBean Bean des Typs {@link MultiCastData}, enthaelt alle noetigen Daten zum Erstellen eines Pakets
	 */
	public PacketBuilder(MulticastData mcBean){
		//Zu uebertragene Werte setzen
		hostID      = mcBean.getHostID();
		randomID	= mcBean.getRandomID();
		reset       = false;
		senderID    = mcBean.getThreadID();
		txPktCnt    = 0;
		txPktRate   = mcBean.getPacketRateDesired();
		ttl		    = mcBean.getTtl();
		pktLength   = mcBean.getPacketLength();
		//Neues Paket bilden
		buildNewPacket();
	}
	
	/**
	 * Setzt alle mit den Hilfsfunktionen zum Konvertieren ermittelten Byte-Arrays
	 * zu einem Byte-Array zusammen und fuegt eventuell Fuellbits ein.
	 */
	private void buildNewPacket(){
		buf 			= new byte[pktLength];
		bufForCRC16		= new byte[42];
		int pos			= 0,
			hIDlength	= 0;
		
		//Wenn die Host-ID zu lang ist, wird sie abgeschnitten
		if(hostID.length()<=25) hIDlength = hostID.length();
		else					hIDlength = 25;
		//Setzen der Host-ID
		System.arraycopy(hostID.getBytes(), 0, buf, pos, hIDlength);
		//Rest der hostID mit Nullen auffuellen
		for(pos = hIDlength;pos<25;pos++)	buf[pos] = 0;					//pos: 25
		
		int tmp = randomID.length();
		if (tmp > 7) {
			System.arraycopy(
				ByteTools.convertToByte(
					Integer.parseInt(randomID.substring(tmp-1), 16) + 
					Integer.parseInt(randomID.substring(0, tmp-1), 16) * 16
				), 
				0, buf, pos, 4
			);  
		} else {
			System.arraycopy(
				ByteTools.convertToByte(Integer.parseInt(randomID, 16)), 
				0, buf, pos, 4
			);
		} //pos: 29
		pos += 4;
		System.arraycopy(ByteTools.convertToShortByte(senderID), 0, buf, pos, 2);
		pos += 2;															//pos: 31
		System.arraycopy(ByteTools.convertToByte(txPktCnt), 0, buf, pos, 4);
		pos += 4;															//pos: 35
		System.arraycopy(ByteTools.convertToShortByte(txPktRate), 0, buf, pos, 2);
		pos += 2;															//pos: 37
		buf[pos] = (byte) ttl;
		pos ++;																//pos: 38
		System.arraycopy(ByteTools.convertToByte(reset), 0, buf, pos, 4);
		pos += 4;															//pos: 42
		//Checksumme ueber die ersten 42 Bytes
		System.arraycopy(buf, 0, bufForCRC16, 0, 42);
		System.arraycopy(ByteTools.crc16(bufForCRC16), 0, buf, pos, 2);
		pos += 2;															//pos: 44
		System.arraycopy(ByteTools.convertToByte(System.nanoTime()), 0, buf, pos, 8);
		pos += 8;															//pos: 52

		// Bis zur gegebenen Menge mit Nullen auffuellen
		// Wenn ptkLength kleiner als die erforderte Mindestlaenge ist, wird pktLength
		// praktisch ignoriert
		for(;pos<pktLength;pos++) buf[pos] = (byte) 0;
	}
	
	/**
	 * Methode, mit der nachtraeglich die RandomID geaendert werden kann
	 * @param randomID die neue randomID
	 */
	public void alterRandomID(String randomID){
		int l = randomID.length();
		System.arraycopy(ByteTools.convertToByte(Integer.parseInt(randomID.substring(l-1), 16) + Integer.parseInt(randomID.substring(0,l-1), 16) * 16), 0, buf, 25, 4);
	}
	
	/**
	 * Methode, mit der nachtraeglich die ThreadID geaendert werden kann
	 * @param threadID die neue ThreadID
	 */
	public void alterThreadID(int threadID){
		System.arraycopy(ByteTools.convertToShortByte(threadID), 0, buf, 29, 2);
	}
	
	/**
	 * Setzt den reset-Wert des Pakets
	 * @param reset der neue reset-Wert, der versendet wird
	 */
	public void setReset(boolean reset){
		System.arraycopy(ByteTools.convertToByte(reset), 0, buf, 38, 4);
	}
	
	/**
	 * Methode,mit der ein neues Byte-Array mit den Nutzdaten angefordert wird.
	 * Dabei wird jedes mal der Paketzaehler erhoeht und der Zeitstempel aktualisiert.
	 * @return Das aktualisierte Byte[]-Paket
	 */
	@Override
	public byte[] getPacket() {
		//Paketzaehler erhoehen
		txPktCnt++;
		System.arraycopy(ByteTools.convertToByte(txPktCnt), 0, buf, 31, 4);
		
		//Checksumme ueber die ersten 42 Bytes
		System.arraycopy(buf, 0, bufForCRC16, 0, 42);
		System.arraycopy(ByteTools.crc16(bufForCRC16), 0, buf, 42, 2);
		
		//Zeitstempel erneuern
		System.arraycopy(ByteTools.convertToByte(System.nanoTime()), 0, buf, 44, 8);
		
		return buf;
	}
}