   
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
import java.util.Iterator;

import javax.activation.UnsupportedDataTypeException;

public class MMRPPacketCreator {
	
	private ArrayList<Byte> packet;

	public byte[] generateMessage(byte leaveAll, byte[] firstValue, ArrayList<Byte> events) throws UnsupportedDataTypeException{
		byte attributeLength = (byte) firstValue.length;
		byte attributeType;
		
		switch(firstValue.length){
		case 1: {
			attributeType = 0x1; break;
		}
		case 6: {
			attributeType = 0x2;break;
		}
		default: throw new UnsupportedDataTypeException();
		}
		if (events.size() >= 8191){
			throw new UnsupportedDataTypeException();
		}
		return generateMessage(leaveAll, firstValue,events, attributeType, attributeLength);
	}
	
	private byte[] generateMessage(byte leaveAll ,byte[] firstValue, ArrayList<Byte> events, byte attType, byte attLength){
		int countEventsBytes = (int) Math.ceil((double)events.size()/3); 
		byte[] bArray = new byte[6+attLength+countEventsBytes];
		bArray[0]= attType;
		bArray[1]=attLength;
		bArray[2]= (byte) (leaveAll*32 + (events.size()>> 8));
		bArray[3]= (byte) (events.size() & 255) ; 		
		return generateMessage(firstValue, events, bArray);
	}
	
	private byte[] generateMessage(byte[] firstValue, ArrayList<Byte> events, byte[] bArray){
		int i=4;
		int end = firstValue.length+4;
		//sets the first value
		for (i=4;i<end;i++){
			bArray[i] = firstValue[i-4];
		}
		//packs and sets all events
		Iterator<Byte> it = events.iterator();
		while(it.hasNext()){
			int value= it.next()*36;
			if(it.hasNext()){
				value = value + it.next()*6;
				if(it.hasNext()){
					value = value + it.next();
				}
			}
			bArray[i++]= (byte) value;
		}
		
		//sets the end mark
		bArray[i] = 0x0;
		bArray[i+1] = 0x0;
		return bArray;
	}
	
	public byte[] createNewPacket(byte[] source, byte leaveAll, byte[] firstValue, ArrayList<Byte> events ) throws UnsupportedDataTypeException{
		initArray(source);
		initBigByte(generateMessage(leaveAll,firstValue,events));
		return finalizePacket();
	}
	

	
	public void initArray(byte[] source){
		packet = new ArrayList<Byte>();
		//destination
		packet.add((byte) 0x01);
		packet.add((byte) 0x80);
		packet.add((byte) 0xC2);
		packet.add((byte) 0x00);
		packet.add((byte) 0x00);
		packet.add((byte) 0x20);
		//source
		int i=0;
		while(i<source.length){
			packet.add(source[i++]);
		}
		//etherType
		packet.add((byte) 0x88);
		packet.add((byte) 0xf6);
		//protocol version
		packet.add((byte) 0x0);
	}
	
	public byte[] finalizePacket(){
		//end mark
		packet.add((byte) 0x00);
		packet.add((byte) 0x00);
		//fill to the min length of ethernet packet
		while(packet.size()<60){
			packet.add((byte) 0x00);
		}
		return toLittleByte(packet);
	}
	
	public ArrayList<Byte> initBigByte(byte[] message) {
		int i=0;
		while(i<message.length){
			packet.add(message[i++]);
		}
		return packet;
	}

	public byte[] toLittleByte(ArrayList<Byte> ar) {
		byte[] byteAr = new byte[ar.size()];
		int i = 0;
		for (Byte b : ar) {
			byteAr[i] = b.byteValue();
			i++;
		}
		return byteAr;
	}
	
}
