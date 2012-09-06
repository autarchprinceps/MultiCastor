   
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
 
 package dhbw.multicastor.program.model;

/**
 * Diese Klasse stellt static Methoden zur Verfügung, um mit Byte-Arrays zu arbeiten
 * @author jannik
 */
public class ByteTools {
	/**
	 * Wandelt in ein Byte-Array um.
	 * @param theInteger int Wert, der in ein Byte Array umgewandelt werden soll.
	 * @return byte[4] Array
	 */
	public static byte[] convertToByte(int theInteger){
		return new byte[]{
				(byte) (theInteger >>> 24),
				(byte) (theInteger >>> 16),
				(byte) (theInteger >>>  8),
				(byte) theInteger
		};
	}
	
	/**
	 * Wandelt in ein Byte-Array um.
	 * @param theLong long Wert, der in ein Byte Array umgewandelt werden soll.
	 * return byte[8]-Array
	 */
	public static byte[] convertToByte(long theLong){
		return new byte[]{
				(byte) (theLong >>> 56),
				(byte) (theLong >>> 48),
				(byte) (theLong >>> 40),
				(byte) (theLong >>> 32),
				(byte) (theLong >>> 24),
				(byte) (theLong >>> 16),
				(byte) (theLong >>>  8),
				(byte) theLong
		};
	}
	
	/**
	 * Wandelt in ein Byte-Array um.
	 * Negative Werte sind nicht erlaubt, werden aber nicht abgefangen (das
	 * Ergebnis ist nur crap)
	 * @param theInteger Integer, der wie ein short behandelt wird (nur 2 Byte werden genutzt, negative Werte sind nicht erlaubt)
	 * @return byte[2]-Array
	 */
	public static byte[] convertToShortByte(int theInteger){
		return new byte[]{
				(byte) (theInteger >> 8),
				(byte) theInteger
		};
	}
	
	/**
	 * Wandelt in ein Byte Array um.
	 * @param theBoolean boolean, der in ein Byte Array umgewandelt werden soll.
	 * @return byte[4] Array
	 */
	public static byte[] convertToByte(boolean theBoolean){
		if(theBoolean) return new byte[]{						//true: mit Einsen füllen
							(byte) 255,
							(byte) 255,
							(byte) 255,
							(byte) 255
						};
		else			return new byte[]{						//false: mit Nullen füllen
							(byte) 0,
							(byte) 0,
							(byte) 0,
							(byte) 0
						};
	}
	
	/**
	 * Wandelt das Byte-Array in ein Boolean um.
	 * Es wird davon ausgegangen, dass alle Bits endweder 0 (false)
	 * oder 1 (true) sind.
	 * @param theByte das Byte-Array
	 * @return das Boolean
	 */
	public static boolean byteToBoolean(byte[] theByte){
		if(theByte[0]!=0) return true;
		else return false;
	}
	
	/**
	 * Errechnet eine CRC-CCITT (CRC-16) Prüfsumme des übergebenen byte-Arrays.
	 * Der Initialwert der Prüfsumme ist 0xFFFF (11111111 11111111).
	 * Verwendetes Grundpolynom: x^16 + x^12 + x^5 + 1
	 * (Nach Wikipedia)
	 * @param byteArray
	 * @return byte[2]-Array mit der Checksumme
	 */
	public static byte[] crc16(byte[] byteArray){
		//int, da Bitoperatoren mit 32bit arbeiten
		int  pol	= 0x1021,
			 crc	= 0xFFFF;
        byte mybyte;

        //for-Schleife über alle bytes
        for (int i=0;i<byteArray.length;i++){
        	mybyte = byteArray[i];
        	//for-Schleife über alle Bits des jeweiligen bytes
            for (int j = 0; j < 8; j++) {
                if(	((mybyte   >> (7-j) & 1) == 1)
                  ^ ((crc 	   >>   15  & 1) == 1))
                {
                		crc = crc << 1;
                		crc = crc ^ pol;
                }
                else	crc = crc << 1;
            }
        }
        
        //Return mit "cast" zum 2-byte-Array
        return new byte[]{
        		(byte)((crc & 0xffff)>> 8),
        		(byte) (crc & 0xffff)
        };
	}

	/**
	 * Wandelt in ein Int um.
	 * @param theByte byte[2] Array, das in ein int umgewandelt werden soll
	 * @return das resultierende int
	 */
	public static int shortByteToInt(byte[] theByte){
		return (  (    (theByte[0]&0xFF)   << 8 )
			    |      (theByte[1]&0xFF)
			   );
	}
	
	/**
	 * Wandelt in ein Int um.
	 * @param theByte byte[4] Array, das in ein int umgewandelt werden soll
	 * @return das resultierende int
	 */
	public static int byteToInt(byte[] theByte){
		return (  (    (theByte[0]&0xFF)   << 24 )
				| (    (theByte[1]&0xFF)   << 16 )
				| (    (theByte[2]&0xFF)   <<  8 )
			    |      (theByte[3]&0xFF)
		       );
	}
	
	/**
	 * Wandelt in ein Int um.
	 * Die Bitoperatoren würden ohne die Konvertierung der Bytes im
	 * 32-bit Bereich arbeiten, das würde hier zum falschen Ergebnis führen.
	 * @param theByte byte[8] Array, das in ein int umgewandelt werden soll
	 * @return das resultierende int
	 */
	public static long byteToLong(byte[] theByte){
		return (  (    (long) (theByte[0]&0xFF)   << 56 )
				| (    (long) (theByte[1]&0xFF)   << 48 )
				| (    (long) (theByte[2]&0xFF)   << 40 )
			    | (    (long) (theByte[3]&0xFF)   << 32 )
			    | (    (long) (theByte[4]&0xFF)   << 24 )
				| (    (long) (theByte[5]&0xFF)   << 16 )
				| (    (long) (theByte[6]&0xFF)   <<  8 )
			    |      (long) (theByte[7]&0xFF)
		       );
	}
	
	public static long macToLong(byte[] macAddress){
		return ((long)macAddress[5] & 0xff) 
	    + (((long)macAddress[4] & 0xff) << 8) 
	    + (((long)macAddress[3] & 0xff) << 16) 
	    + (((long)macAddress[2] & 0xff) << 24) 
	    + (((long)macAddress[1] & 0xff) << 32) 
	    + (((long)macAddress[0] & 0xff) << 40);
	}
	
	public static byte[] LongToMac(long macAddress){
		byte[] buffer = new byte[6];
		int currentOffset = 0;
		buffer[currentOffset++] = (byte) (macAddress >> 40);
		buffer[currentOffset++] = (byte) (macAddress >> 32);
		buffer[currentOffset++] = (byte) (macAddress >> 24);
		buffer[currentOffset++] = (byte) (macAddress >> 16);
		buffer[currentOffset++] = (byte) (macAddress >> 8);
		buffer[currentOffset++] = (byte) macAddress;
		return buffer;

	}
	
}
