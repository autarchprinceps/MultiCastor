   
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
 
 package dhbw.multicastor.testcases.jnetpcap;

import java.util.ArrayList;

public class YArrayContent {

	// EtherType: 0x88f6 (4 bytes)
	// MMRP: MRPDU
	
	
	public static short etherType = (short) 0x88f6;
	public static byte protocolVersion = 0x0;

	// AttributeType (either ... or ...)
	public static byte attrTypeSRG = 0x1;
	public static byte attrTypeMAC = 0x2;

	// AttributeLength (depending on AttributeType)
	public static byte attrLength1 = 0x6;
	public static byte attrLength2 = 0x1;

	// VectorHeader
	/*
	 * LeaveAll: the first 3 bits NumberOfValues: the next 13 bits
	 */
	public static byte leaveAllEvAndNumOfValue1 = 0x00;
	public static byte leaveAllEvAndNumOfValue2 = 0x03;
	public static byte leaveAllEvAndNumOfValue3 = 0x06;

	// FirstValue
	// Multicast MAC-Address
	public static byte firstValue1 = 0x01;
	public static byte firstValue2 = 0x08;
	public static byte firstValue3 = 0x62;
	public static byte firstValue4 = 0x00;
	public static byte firstValue5 = 0x00;
	public static byte firstValue6 = 0x20;
	// ServiceRequirement
	public static byte firstValueSR = 0x0;

	// ThreePackedEvents
	// Events
	public static byte firstEv = 0x01;
	public static byte secondEv = 0x02;
	public static byte thirdEv = 0x03;
	 public static byte threePackedEv = (byte) ((byte) ((firstEv * 6) +
	 (secondEv)) * 6 + thirdEv);

	// Endmark
	public byte endMark1 = 0x00;
	public static byte endMark2 = 0x00;

	public byte[] createPDU() {
		return toLittleByte(initBigByte());
	}

	public byte[] createPDUNoProtVersion() {
		return toLittleByte(initBigByteNoProtVersion());
	}

	private ArrayList<Byte> initBigByte() {
		ArrayList<Byte> ar = new ArrayList<Byte>();
		//destination
		ar.add((byte) 0x01);
		ar.add((byte) 0x80);
		ar.add((byte) 0xC2);
		ar.add((byte) 0x00);
		ar.add((byte) 0x00);
		ar.add((byte) 0x20);
		//source
		ar.add((byte) 0x01);
		ar.add((byte) 0x80);
		ar.add((byte) 0xC2);
		ar.add((byte) 0x00);
		ar.add((byte) 0x00);
		ar.add((byte) 0x20);
		//etherType
		ar.add((byte)0x88);
		ar.add((byte)0xf6);
		ar.add(protocolVersion);
		/*** message begin***/
		ar.add(attrTypeMAC);
		ar.add(attrLength1);

		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue2);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		// ***VectorEnd***
		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue2);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		// ***VectorEnd***
		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue2);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		// ***VectorEnd***
		ar.add(endMark1);
		ar.add(endMark2);
		/**message end**/

		
		/*** message begin***/
		ar.add(attrTypeMAC);
		ar.add(attrLength1);

		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue3);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		ar.add(threePackedEv);
		// ***VectorEnd***
		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue2);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		// ***VectorEnd***
		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue2);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		// ***VectorEnd***
		ar.add(endMark1);
		ar.add(endMark2);
		/**message end**/
		
		/*** message begin***/
		ar.add(attrTypeMAC);
		ar.add(attrLength1);

		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue3);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		ar.add(threePackedEv);
		// ***VectorEnd***
		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue2);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		// ***VectorEnd***
		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue2);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		// ***VectorEnd***
		ar.add(endMark1);
		ar.add(endMark2);
		/**message end**/
		
		/*** message begin***/
		ar.add(attrTypeMAC);
		ar.add(attrLength1);

		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue3);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		ar.add(threePackedEv);
		// ***VectorEnd***
		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue2);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		// ***VectorEnd***
		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue2);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		// ***VectorEnd***
		ar.add(endMark1);
		ar.add(endMark2);
		/**message end**/
		
		/*** message begin***/
		ar.add(attrTypeMAC);
		ar.add(attrLength1);

		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue3);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		ar.add(threePackedEv);
		// ***VectorEnd***
		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue2);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		// ***VectorEnd***
		// ***VectorBegin***
		// VectorHeader
		ar.add(leaveAllEvAndNumOfValue1);
		ar.add(leaveAllEvAndNumOfValue2);
		// FirstValue
		ar.add(firstValue1);
		ar.add(firstValue2);
		ar.add(firstValue3);
		ar.add(firstValue4);
		ar.add(firstValue5);
		ar.add(firstValue6);
		// Events
		ar.add(threePackedEv);
		// ***VectorEnd***
		ar.add(endMark1);
		ar.add(endMark2);
		/**message end**/
		
		ar.add(endMark1);
		ar.add(endMark2);
		// for (Byte b : ar) {
		// System.out.println(b.byteValue());
		// }
		return ar;
	}

	private ArrayList<Byte> initBigByteNoProtVersion() {
		ArrayList<Byte> array = initBigByte();
		array.remove(0);
		return array;
	}

	private byte[] toLittleByte(ArrayList<Byte> ar) {
		byte[] byteAr = new byte[ar.size()];
		int i = 0;
		for (Byte b : ar) {
			byteAr[i] = b.byteValue();
			i++;
		}
		return byteAr;
	}

	public static void main(String[] args) {
		YArrayContent content = new YArrayContent();
		content.createPDU();
		System.out.println(threePackedEv);
	}

}
