   
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
 
  

import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.interfaces.PacketBuilderInterface;

/**
 * Eine Klasse zum Erstellen des Byte-Arrays, das per Multicast versendet wird.
 * @author jannik
 *
 */
public class PacketBuilder implements PacketBuilderInterface{
	
	private int  txPktCnt;
	private byte[] buf,
				   bufForCRC16;
	
	/**
	 * Einziger Konstruktor. Extrahiert alle benï¿½tigten Daten aus dem MultiCastData-Object
	 * und erstellt ein neues Paket. Dieses Paket wird beibehalten und nur noch minimal ab-
	 * geï¿½ndert, wenn mit getPacket() ein neues Paket angefordert wird. Das Erstellen von
	 * mehreren Paketen mit derselben Instanz ist nicht mï¿½glich
	 * @param mcBean Bean des Typs {@link MultiCastData}, enthï¿½lt alle nï¿½tigen Daten zum Erstellen eines Pakets
	 */
	public PacketBuilder(MulticastData mcBean){
		//Neues Paket bilden
		buildNewPacket(mcBean,0);
	}
	
	/**
	 * Methode, mit der nachtrï¿½glich die ThreadID geï¿½ndert werden kann
	 * @param threadID die neue ThreadID
	 */
	public void alterThreadID(int threadID){
		System.arraycopy(ByteTools.convertToShortByte(threadID), 0, buf, 29, 2);
	}

	/**
	 * Setzt alle mit den Hilfsfunktionen zum Konvertieren ermittelten Byte-Arrays
	 * zu einem Byte-Array zusammen und fï¿½gt eventuell Fï¿½llbits ein.
	 */
	protected void buildNewPacket(MulticastData mcBean, int pos){
		//Zu ï¿½bertragene Werte setzen
		if(buf == null){
			buf = new byte[mcBean.getPacketLength()+ pos];
		}
		bufForCRC16		= new byte[42];
		int hIDlength = 0, grenze = 0;

		// Wenn die Host-ID zu lang ist, wird sie abgeschnitten
		if (mcBean.getHostID().length() > 29) {
			hIDlength = 29;
		} else {
			hIDlength = mcBean.getHostID().length();
		}
		//Setzen der Host-ID
		System.arraycopy(mcBean.getHostID().getBytes(), 0, buf, pos, hIDlength);
		//Rest der hostID mit Nullen auffï¿½llen
		grenze = pos + 29;
		for(pos = hIDlength+pos;pos<grenze;pos++)	buf[pos] = 0;					//pos: 29
		System.arraycopy(ByteTools.convertToShortByte(mcBean.getThreadID()), 0, buf, pos, 2);
		pos += 2;															//pos: 31
		System.arraycopy(ByteTools.convertToByte(txPktCnt), 0, buf, pos, 4);
		pos += 4;															//pos: 35
		System.arraycopy(ByteTools.convertToShortByte(mcBean.getPacketRateDesired()), 0, buf, pos, 2);
		pos += 2;															//pos: 37
		if(mcBean.getProtocolType().equals(ProtocolType.IGMP) || mcBean.getProtocolType().equals(ProtocolType.MLD)){
			buf[pos] = (byte)  ((IgmpMldData)mcBean).getTtl();
		} else {
			buf[pos] = (byte) 0;
		}
		pos ++;																//pos: 38
		System.arraycopy(ByteTools.convertToByte(false), 0, buf, pos, 4);
		pos += 4;															//pos: 42
		//Checksumme ï¿½ber die ersten 42 Bytes
		System.arraycopy(buf, 0, bufForCRC16, 0, 42);
		System.arraycopy(ByteTools.crc16(bufForCRC16), 0, buf, pos, 2);
		pos += 2;															//pos: 44
		System.arraycopy(ByteTools.convertToByte(System.nanoTime()), 0, buf, pos, 8);
		pos += 8;															//pos: 52

		// Bis zur gegebenen Menge mit Nullen auffï¿½llen
		// Wenn ptkLength kleiner als die erforderte Mindestlï¿½nge ist, wird pktLength
		// praktisch ignoriert
		for(;pos<mcBean.getPacketLength();pos++) buf[pos] = (byte) 0;
	}

	protected byte[] getBuf() {
		return buf;
	}

	protected byte[] getBufForCRC16() {
		return bufForCRC16;
	}

	/**
	 * Methode,mit der ein neues Byte-Array mit den Nutzdaten angefordert wird.
	 * Dabei wird jedes mal der Paketzï¿½hler erhï¿½ht und der Zeitstempel aktualisiert.
	 * @return Das aktualisierte Byte[]-Paket
	 */
	@Override
	public byte[] getPacket() {
		//Paketzï¿½hler erhï¿½hen
		txPktCnt++;
		System.arraycopy(ByteTools.convertToByte(txPktCnt), 0, buf, 31, 4);
		
		//Checksumme ï¿½ber die ersten 42 Bytes
		System.arraycopy(buf, 0, bufForCRC16, 0, 42);
		System.arraycopy(ByteTools.crc16(bufForCRC16), 0, buf, 42, 2);
		
		//Zeitstempel erneuern
		System.arraycopy(ByteTools.convertToByte(System.nanoTime()), 0, buf, 44, 8);
		
		return buf;
	}

	protected int getTxPktCnt() {
		return txPktCnt;
	}

	protected void setBuf(byte[] buf) {
		this.buf = buf;
	}
	
	protected void setBufForCRC16(byte[] bufForCRC16) {
		this.bufForCRC16 = bufForCRC16;
	}
	
	/**
	 * Setzt den reset-Wert des Pakets
	 * @param reset der neue reset-Wert, der versendet wird
	 */
	public void setReset(boolean reset){
		System.arraycopy(ByteTools.convertToByte(reset), 0, buf, 38, 4);
	}
	
	protected void setTxPktCnt(int txPktCnt) {
		this.txPktCnt = txPktCnt;
	}
}