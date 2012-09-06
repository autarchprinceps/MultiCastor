   
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
 
 package dhbw.multicastor.program.model.mmrp;

import java.util.ArrayList;

import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JHeader;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.annotate.Bind;
import org.jnetpcap.packet.annotate.Dynamic;
import org.jnetpcap.packet.annotate.Field;
import org.jnetpcap.packet.annotate.Header;
import org.jnetpcap.packet.annotate.Header.Characteristic;
import org.jnetpcap.packet.annotate.Header.Layer;
import org.jnetpcap.packet.annotate.HeaderLength;
import org.jnetpcap.protocol.lan.Ethernet;

/**
 *	Klasse zur Erkennung von MMRPD-Units. 
 *	Registrierung bei der JRegistry vor der ersten Verwendung ist erforderlich.
 *	Der MMRP-Header ist in folgende drei Felder aufgelteilt:
 *	<ul><li>protocolVersion</li>
 *	<li>messages</li>
 *	<li>endMark</li></ul>
 *	Die Unterfelder des Feldes messages werden in listMes gehalten. Dabei werden Felder mit irrelevanten Informationen aussortiert.
 */
@Header(name = "MMRP", osi = Layer.DATALINK, nicname = "mmrp",characteristics = Characteristic.POINT_TO_MULTIPOINT)
public class MMRP extends JHeader {
	
	/** Eintrag ins EtherType-Feld des Ethernet-Headers **/
	public static final int mmrpEtherType = 0x88f6;
	
	/** Event **/
	public static final byte NEW = 0;
	
	/** Event **/
	public static final byte JOIN_IN = 1;
	
	/** Event **/
	public static final byte IN = 2;
	
	/** Event **/
	public static final byte JOIN_MT = 3;
	
	/** Event **/
	public static final byte MT = 4;
	
	/** Event **/
	public static final byte LV = 5;
	
	/** Haelt alle Vector Attribute Felder des vorliegenden MMRPD-Units **/
	private ArrayList<Attribute> listMes = new ArrayList<Attribute>();
	
	@HeaderLength(HeaderLength.Type.HEADER)
	public static int headerLength(JBuffer buffer, int offset) {
		return buffer.size()-offset;
	}
	
	@Bind(to = Ethernet.class)
	public static boolean bindToEthernet(JPacket packet, Ethernet eth) {
		return eth.type() == mmrpEtherType;
	}
	
	@Field(offset= 0 * BYTE, length = 1 * BYTE)
	public byte protocolVersion(){
		return getByte(0);
	}
	
	//format = "#hexdump#" 
	@Field(offset = 1 *BYTE, format = "#mac#")
	public byte[] messages(){
		return getByteArray(1,getHeaderLength()-3);
	}
	
	@Dynamic(Field.Property.LENGTH)
	public int messagesLength(){
		return getHeaderLength()-3;
	}


	@Field(length = 2 * BYTE)
	public short endMark(){
		return (short) getUShort(endMarkOffset());
	}
	
	@Dynamic(Field.Property.OFFSET)
	public int endMarkOffset(){
		return getHeaderLength()-2;
	}
	
	/**
	 * Liefert die message-Felder des vorliegenden MMRPD-Units zurueck.
	 * 
	 * @return listMes
	 */
	public ArrayList<Attribute> getListMes() {
		return listMes;
	}
	
	/* (non-Javadoc)
	 * @see org.jnetpcap.packet.JHeader#decodeHeader()
	 */
	@Override
	protected void decodeHeader() {
		this.listMes.clear();
		//only protocol version 0 is supported
		if(getUByte(0) == 0){
			//skips the field ProtocolType 
			int pos = 1;
			while(this.getUByte(pos)!= 0){
				pos = pos + decodeMessage(pos);
			}	
		}
	}
	
	/**
	 * Dekodiert ein Message-Feld eines MMRPD-Units
	 * 
	 * @param pos Position des naechsten Message-Feldes innerhalb eines MMRPD-Units
	 * @return Groesse des Message-Feldes in Bytes
	 */
	private int decodeMessage(int pos){
		//needs to save the size
		int size = pos;
		//saves the length of the attribute (first value)
		int lengthAtt = getUByte(pos + 1);
		//size of the fields AttributeType and AttributeLength
		pos = pos + 2;
		while (getUByte(pos) != 0 || getUByte(pos + 1) != 0) {
			//adds the size of the field VectorAttribute
			pos = pos + calculateVectorAttribute(pos, lengthAtt);
		}
		//adds the size of the endMark of the message
		size = (pos +2)-size;
		return size;
	}
	
	/**
	 * Dekodiert ein AttributeVector-Feld eines MMRPD-Units.
	 * 
	 * @param pos Position des naechsten AttributeVector-Feldes innerhalb eines MMRPD-Units
	 * @param lengthAtt Laenge des Attributs (entweder 6 oder 1 Byte(s))
	 * @return Groesse des AttributeVector-Feldes in Bytes
	 */
	private int calculateVectorAttribute(int pos, int lengthAtt) {
		
		//***gets the necessary values***
		int leaveAll = getUShort(pos)>>13;
		short numOfValues =(short) (getUShort(pos) & 8191);
		int sizeNumOfValues = (int) Math.ceil((double)numOfValues/3);
		//goes to the position of the field firstValue
		pos = pos + 2;
		//gets the firstValue
		byte[] firstV = getByteArray(pos, lengthAtt);
		//goes to the position of the unpacked events
		pos = pos + lengthAtt;
		
		//gets the unpacked events
		byte[] unpackedEvents = getByteArray(pos, sizeNumOfValues);
		
		// ***saves this VectorAttribute***
		Attribute a = new Attribute(firstV, unpackedEvents, leaveAll,
				numOfValues);
		listMes.add(a);
		
		// ***calculates size of the current VectorAttribute***
		int size = lengthAtt;
		size = size + sizeNumOfValues;
		// VectorHeader
		size = size + 2;
		// returns the size of this VectorAttribute
		return size;
		
	}

	/* (non-Javadoc)
	 * @see org.jnetpcap.packet.JHeader#toString()
	 */
	@Override
	public String toString() {
		String string="";
		for(Byte b: messages()){
			string = string.concat(b.toString());
		}
		return string;
	}
	
}