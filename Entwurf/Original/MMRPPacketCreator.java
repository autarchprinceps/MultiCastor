   
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
 
  

import org.jnetpcap.nio.JBuffer;

public class MMRPPacketCreator {
	
	private final static byte[] mmrpAddress = {	(byte) 0x01,
												(byte) 0x80,
												(byte) 0xC2,
												(byte) 0x00,
												(byte) 0x00,
												(byte) 0x20 
												}; 
	
	private JBuffer buffer = new JBuffer(60);
	
	public MMRPPacketCreator(IMmrpSenderAndReceiver mc) {
		int i = 0;
		setBytesToBuffer(0, mmrpAddress);
		setBytesToBuffer(6, mc.getMyAddress());
		buffer.setUByte(12,0x88);
		buffer.setUByte(13,0xf6);
		//protocol version
		buffer.setUByte(14, 0x0);
		//type
		buffer.setUByte(15, 0x2);
		//length
		buffer.setUByte(16, 0x6);
		//leaveAll and events number
		buffer.setUByte(17, 0x00);
		buffer.setUByte(18, 0x01);
		//first value
		setBytesToBuffer(19,mc.getGroupAddress());
		//fills the rest with zeros
		for(i=25;i<60;i++){
			buffer.setUByte(i, 0x00);
		}	
	}
	private void setBytesToBuffer(int pos, byte[] bytes){
			buffer.setByteArray(pos, bytes);
	}
	
	private void setContent(int leaveAll,int event ){
		buffer.setUByte(17, (byte) leaveAll*32);
		buffer.setUByte(25, (byte) event*36);
	}
	
	public JBuffer createNewPacket(int leaveAll,int event){
		setContent(leaveAll, event);
		return buffer;
	}
	
}
