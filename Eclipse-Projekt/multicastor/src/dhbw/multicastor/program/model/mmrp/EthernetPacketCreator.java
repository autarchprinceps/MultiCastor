   
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

import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.model.ByteTools;
import dhbw.multicastor.program.model.PacketBuilder;

public class EthernetPacketCreator extends PacketBuilder{
	
	

	public EthernetPacketCreator(MulticastData mcBean) {
		super(mcBean);
	}

	@Override
	protected void buildNewPacket(MulticastData mcBean, int pos) {
		//initialisiert Data Buffer mit Ethernet-Frame Attributen
		setBuf(new byte[mcBean.getPacketLength() + 14]);
		MMRPData mc = (MMRPData) mcBean;
		//destination address
		System.arraycopy(mc.getMacGroupId().getMacAddress(), 0, getBuf(), pos, 6);
		pos = pos + 6;
		//source address
		System.arraycopy(mc.getMacSourceId().getMacAddress(), 0, getBuf(), pos, 6);
		pos = pos + 6;
		//EtherType
		System.arraycopy(ByteTools.convertToShortByte(DataHeader.dataEtherType), 0, getBuf(), pos, 2);
		pos = pos + 2;
		super.buildNewPacket(mc,pos);
	}

	@Override
	public void alterThreadID(int threadID) {
		System.arraycopy(ByteTools.convertToShortByte(threadID), 0, getBuf(), 43, 2);
	}

	@Override
	public void setReset(boolean reset) {
		System.arraycopy(ByteTools.convertToByte(reset), 0, getBuf(), 52, 4);
	}

	@Override
	public byte[] getPacket() {
		//Paketzï¿½hler erhï¿½hen
		setTxPktCnt(getTxPktCnt()+1);
		System.arraycopy(ByteTools.convertToByte(getTxPktCnt()), 0, getBuf(), 45, 4);
		
		//Checksumme ï¿½ber die ersten 42 Bytes
		System.arraycopy(getBuf(), 14, getBufForCRC16(), 0, 42);
		System.arraycopy(ByteTools.crc16(getBufForCRC16()), 0, getBuf(), 56, 2);
		
		//Zeitstempel erneuern
		System.arraycopy(ByteTools.convertToByte(System.nanoTime()), 0, getBuf(), 58, 8);
		
		return getBuf();
	}

}
