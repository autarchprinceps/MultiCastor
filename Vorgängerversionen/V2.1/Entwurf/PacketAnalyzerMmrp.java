   
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
 
  

import java.util.logging.Level;
import java.util.logging.Logger;

import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.Source;
import dhbw.multicastor.program.model.ByteTools;
import dhbw.multicastor.program.model.PacketAnalyzer;


public class PacketAnalyzerMmrp extends PacketAnalyzer {



	public PacketAnalyzerMmrp(MulticastData multicastData, Logger logger) {
		super(multicastData, logger);
		// TODO Auto-generated constructor stub
	}


	public void analyzePacket(byte[] mcPacket){
		// Die Bufferlaenge fuer Pakete ist im MulticastReceui8iver 
		// 	festgelegt und darf davon nicht abweichen

		if(getComplete()){
			analyzePacketOnce(mcPacket);
			setComplete(false);
		}

		//********************************************
		// Paketanalyse - geklaut von Janniks TestCase
		//********************************************

		byte[] snippet 		= new byte[29];
		byte[] intSnippet	= new byte[4];
		byte[] shortSnippet = new byte[2];
		byte[] longSnippet	= new byte[8];

		// Perform value-reset
		System.arraycopy(mcPacket, 38, intSnippet, 0, 4);
		boolean reset = ByteTools.byteToBoolean(intSnippet);
		if(reset == true){
			mcData.resetValues();
			this.resetValues();
		}

		// HostID
		System.arraycopy(mcPacket, 0, snippet, 0, 29);
		//Ende des Strings suchen
		int eof = 0;
		for(;eof<29&&snippet[eof]!=0;eof++);
		hostID = new String(snippet).substring(0, eof);
		// Hirschmann Tool Erkennung. Wenn auch nicht mit Checksumme, sondern ueber den Text
		if(hostID.equals("Hirschmann IP Test-Multicast")){
			if(check){
				logger.log(Level.INFO,"Muahaha - Pakete vom Hirschmann Tool entdeckt.");
				check = false;	
			}
			hostID="Hirschmann-Tool SenderID:";
		}

		// Thread-ID
		System.arraycopy(mcPacket, 29, shortSnippet, 0, 2);
		threadID = ByteTools.shortByteToInt(shortSnippet);

		// tracks how much the senderID changes
		//senderID = hostID + threadID;
		if(!senderID.equals(hostID + threadID)){
			senderID = hostID + threadID;
			senderChanges++;
			//	System.out.println("oldSenderID: " + senderID + " newSenderID: " + hostID + threadID);
		}

		// PacketLoss
		System.arraycopy(mcPacket, 31, intSnippet, 0, 4);
		int packetcount =  ByteTools.byteToInt(intSnippet);

		//	System.out.println("packetcount: " + packetcount + "\t\tinternerPC: " + internerPacketCount);
		if(packetcount != internerPacketCount){
			if(missingPackets.contains(packetcount)){	// werden hier die Pointer oder die Werte verglichen???
				missingPackets.remove((Object)packetcount);
			} else {
				//	if (internerPacketCount > packetcount)
				//		System.out.println("packetcount entspricht nicht dem erwarteten Wert - MulticastReceiver !!!!!! packetcount: " + packetcount + "\tinternerPC: " + internerPacketCount);
				if((packetcount-internerPacketCount < 1000)&&(internerPacketCount>0)){
					for(int n=internerPacketCount;n<packetcount;n++){
						missingPackets.add(internerPacketCount);
					}
				} else {
					//	proclaim(2,"Probably multiple Senders detected. Packetcount differs very much from expected value.");
				}
				internerPacketCount = packetcount + 1;
			}
		} else {
			internerPacketCount++;
		}

		// MeasuredPacketRate
		packetrate_counter++;

		// time stamp
		System.arraycopy(mcPacket, 44, longSnippet, 0, 8);
		timeStampSe = ByteTools.byteToLong(longSnippet);
		timeStampRe = System.nanoTime();
		//System.out.println("timeStamp: " + timeStamp);
		//	jitterMessung(timeStamp);
		if(dTimeStamp1 != 0){
			//Verzögerung zwischen Ankunft und Absenden des letzten Paketes
			dTimeStamp2 = dTimeStamp1;

			//Verzögerung zwischen Ankunft und Absenden dieses Paketes
			dTimeStamp1 = timeStampRe - timeStampSe;
			
			//Differenz der Verzögerungen = Taktzitter
			jitterHelper += Math.abs(dTimeStamp2 - dTimeStamp1);
		} else {
			dTimeStamp1 = timeStampRe - timeStampSe;
		}

		// total packets received
		packetCount++;
	}
	
	/**
	 * Analysiert das Paket auf Werte, die sich nicht so oft aendern.
	 * <ul>
	 * <li>PacketLength
	 * <li>DesiredPacketRate
	 * <li>TTL
	 * <li>Source
	 * </ul>
	 * @param mcPacket Zu untersuchendes Datenpaket.
	 */
	public void analyzePacketOnce(byte[] mcPacket){
		byte[] shortSnippet = new byte[2];
		
		// figure out packet length and save it to mcData
		packetLength = mcPacket.length;
		
		// DesiredPacketRate
		System.arraycopy(mcPacket, 35, shortSnippet, 0, 2);
		desiredPacketRate = ByteTools.shortByteToInt(shortSnippet);
		
		// TTL Start-Wert beim Senden
		// aktueller TTL der Pakete ist leider hier nicht so einfach einsehbar
		ttl = (int) mcPacket[37];
		
		// check Checksum and packetsource
		System.arraycopy(mcPacket, 42, checksum, 0, 2);
		System.arraycopy(mcPacket, 0, snippetForChecksum, 0, 42);
		byte[] cs = ByteTools.crc16(snippetForChecksum);
		if((checksum[0] == cs[0])&&(checksum[1] == cs[1])){
			source = Source.MULTICASTOR;
		} else {
			source = Source.UNDEFINED;
		}
		// ist hier etwas redundant ... aber ist noch, weil es die Checksumme nicht nutzt
		if((hostID!=null)&&(hostID.equals("Hirschmann-Tool SenderID:"))){
			source = Source.HIRSCHMANN;	
		}
	}

}
