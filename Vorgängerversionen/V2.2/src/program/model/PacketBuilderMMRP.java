package program.model;

import program.data.MulticastData;

/**
 * Eine Klasse zum Erstellen des Byte-Arrays, das per MMRP-Multicast versendet
 * wird.
 * 
 * @author jannik
 * @author emkey
 * 
 */
public class PacketBuilderMMRP implements
		program.interfaces.PacketBuilderInterface {
	MulticastData mcData = null;

	private String hostID = "undefined"; //$NON-NLS-1$
	private boolean reset = false;

	private int senderID = 0, txPktCnt = 0, txPktRate = 1, pktLength = 0;

	private byte[] buf;

	/**
	 * Einziger Konstruktor. Extrahiert alle ben�tigten Daten aus dem
	 * MultiCastData-Object und erstellt ein neues Paket. Dieses Paket wird
	 * beibehalten und nur noch minimal ab- ge�ndert, wenn mit getPacket() ein
	 * neues Paket angefordert wird. Das Erstellen von mehreren Paketen mit
	 * derselben Instanz ist nicht m�glich
	 * 
	 * @param mcBean
	 *            Bean des Typs {@link MultiCastData}, enth�lt alle n�tigen
	 *            Daten zum Erstellen eines Pakets
	 */
	public PacketBuilderMMRP(MulticastData mcBean) {
		this.mcData = mcBean;

		// Zu �bertragene Werte setzen
		hostID = mcBean.getHostID();
		mcBean.getSourceMac();
		reset = false;
		senderID = mcBean.getThreadID();
		txPktCnt = 0;
		txPktRate = mcBean.getPacketRateDesired();
		pktLength = mcBean.getPacketLength();
		// Neues Paket bilden
		buildNewPacket();
	}

	/**
	 * Setzt alle mit den Hilfsfunktionen zum Konvertieren ermittelten
	 * Byte-Arrays zu einem Byte-Array zusammen und f�gt eventuell F�llbits
	 * ein.
	 */
	private void buildNewPacket() {
		int pos = 0, hIDlength = 0;

		buf = new byte[pktLength];

		// Set Destination to GroupMac
		for (int i = 0; i < 6; i++) {
			buf[pos + i] = mcData.getGroupMac()[i];
		}
		pos += 6;

		// Set Source to HostMac
		for (int i = 0; i < 6; i++) {
			buf[pos + i] = mcData.getSourceMac()[i];
		}
		pos += 6;

		// Set EtherType to 1337 (unused value)
		buf[pos] = (byte) 0x13;
		pos++;
		buf[pos] = (byte) 0x37;
		pos++;

		// Wenn die Host-ID zu lang ist, wird sie abgeschnitten
		if (hostID.length() < 29)
			hIDlength = hostID.length();
		else
			hIDlength = 29;

		// Rest der hostID mit Nullen auff�llen
		for (int i = 0; i < 29; i++) {
			buf[pos + i] = 0x00;
		}

		// Setzen der Host-ID
		for (int i = 0; i < hIDlength; i++) {
			buf[pos + i] = hostID.getBytes()[i];
		}
		pos += 29;

		for (int i = 0; i < ByteTools.convertToShortByte(senderID).length; i++) {
			buf[pos + i] = ByteTools.convertToShortByte(senderID)[i];
		}
		pos += 2;

		for (int i = 0; i < ByteTools.convertToShortByte(txPktCnt).length; i++) {
			buf[pos + i] = ByteTools.convertToShortByte(txPktCnt)[i];
		}
		pos += 2;

		for (int i = 0; i < ByteTools.convertToShortByte(txPktRate).length; i++) {
			buf[pos + i] = ByteTools.convertToShortByte(txPktRate)[i];
		}
		pos += 2;

		for (int i = 0; i < ByteTools.convertToByte(reset).length; i++) {
			buf[pos + i] = ByteTools.convertToByte(reset)[i];
		}
		pos += 4;

		buf[pos] = (byte) 0x00;
		for (int i = 0; i < 8; i++) {
			buf[pos + i] = (byte) 0x00;
		}
		pos += 4;

		// Bis zur gegebenen Menge mit Nullen auff�llen
		// Wenn ptkLength kleiner als die erforderte Mindestl�nge ist, wird
		// pktLength
		// praktisch ignoriert

		for (; pos < pktLength; pos++) {
			buf[pos] = (byte) 0x00;
		}

	}

	/**
	 * Methode, mit der nachtr�glich die ThreadID ge�ndert werden kann
	 * 
	 * @param threadID
	 *            die neue ThreadID
	 */
	public void alterThreadID(int threadID) {

		buf[43] = ByteTools.convertToShortByte(threadID)[0];
		buf[44] = ByteTools.convertToShortByte(threadID)[1];
	}

	/**
	 * Setzt den reset-Wert des Pakets
	 * 
	 * @param reset
	 *            der neue reset-Wert, der versendet wird
	 */
	public void setReset(boolean reset) {

		buf[49] = ByteTools.convertToByte(reset)[0];
		buf[50] = ByteTools.convertToByte(reset)[1];
		buf[51] = ByteTools.convertToByte(reset)[2];
		buf[52] = ByteTools.convertToByte(reset)[3];
	}

	/**
	 * Methode,mit der ein neues Byte-Array mit den Nutzdaten angefordert wird.
	 * Dabei wird jedes mal der Paketz�hler erh�ht und der Zeitstempel
	 * aktualisiert.
	 * 
	 * @return Das aktualisierte Byte[]-Paket
	 */
	@Override
	public byte[] getPacket() {
		// Paketz�hler erh�hen
		txPktCnt++;
		buf[45] = ByteTools.convertToShortByte(txPktCnt)[0];
		buf[46] = ByteTools.convertToShortByte(txPktCnt)[1];

		// timestamp einsetzen
		System.arraycopy(ByteTools.convertToByte(System.nanoTime()), 0, buf,
				53, 8);

		return buf;
	}
}