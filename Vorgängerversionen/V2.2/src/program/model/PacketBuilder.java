package program.model;

import program.data.MulticastData;

/**
 * Eine Klasse zum Erstellen des Byte-Arrays, das per Multicast versendet wird.
 * @author jannik
 *
 */
public class PacketBuilder implements program.interfaces.PacketBuilderInterface{

	private String hostID   ="undefined"; //$NON-NLS-1$
	private boolean reset   = false;

	private int senderID    = 0,
	            txPktCnt    = 0,
	            txPktRate   = 1,
	            pktLength   = 0,
	            ttl		    = 0;

	private byte[] buf,
				   bufForCRC16;

	/**
	 * Einziger Konstruktor. Extrahiert alle ben�tigten Daten aus dem MultiCastData-Object
	 * und erstellt ein neues Paket. Dieses Paket wird beibehalten und nur noch minimal ab-
	 * ge�ndert, wenn mit getPacket() ein neues Paket angefordert wird. Das Erstellen von
	 * mehreren Paketen mit derselben Instanz ist nicht m�glich
	 * @param mcBean Bean des Typs {@link MultiCastData}, enth�lt alle n�tigen Daten zum Erstellen eines Pakets
	 */
	public PacketBuilder(MulticastData mcBean){
		//Zu �bertragene Werte setzen
		hostID      = mcBean.getHostID();
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
	 * zu einem Byte-Array zusammen und f�gt eventuell F�llbits ein.
	 */
	private void buildNewPacket(){
		buf 			= new byte[pktLength];
		bufForCRC16		= new byte[42];
		int pos			= 0,
			hIDlength	= 0;

		//Wenn die Host-ID zu lang ist, wird sie abgeschnitten
		if(hostID.length()<=29) hIDlength = hostID.length();
		else					hIDlength = 29;
		//Setzen der Host-ID
		System.arraycopy(hostID.getBytes(), 0, buf, pos, hIDlength);
		//Rest der hostID mit Nullen auff�llen
		for(pos = hIDlength;pos<29;pos++)	buf[pos] = 0;					//pos: 29
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
		//Checksumme �ber die ersten 42 Bytes
		System.arraycopy(buf, 0, bufForCRC16, 0, 42);
		System.arraycopy(ByteTools.crc16(bufForCRC16), 0, buf, pos, 2);
		pos += 2;															//pos: 44
		System.arraycopy(ByteTools.convertToByte(System.nanoTime()), 0, buf, pos, 8);
		pos += 8;															//pos: 52

		// Bis zur gegebenen Menge mit Nullen auff�llen
		// Wenn ptkLength kleiner als die erforderte Mindestl�nge ist, wird pktLength
		// praktisch ignoriert
		for(;pos<pktLength;pos++) buf[pos] = (byte) 0;
	}

	/**
	 * Methode, mit der nachtr�glich die ThreadID ge�ndert werden kann
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
	 * Dabei wird jedes mal der Paketz�hler erh�ht und der Zeitstempel aktualisiert.
	 * @return Das aktualisierte Byte[]-Paket
	 */
	@Override
	public byte[] getPacket() {
		//Paketz�hler erh�hen
		txPktCnt++;
		System.arraycopy(ByteTools.convertToByte(txPktCnt), 0, buf, 31, 4);

		//Checksumme �ber die ersten 42 Bytes
		System.arraycopy(buf, 0, bufForCRC16, 0, 42);
		System.arraycopy(ByteTools.crc16(bufForCRC16), 0, buf, 42, 2);

		//Zeitstempel erneuern
		System.arraycopy(ByteTools.convertToByte(System.nanoTime()), 0, buf, 44, 8);

		return buf;
	}
}