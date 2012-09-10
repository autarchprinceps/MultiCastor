package zisko.multicastor.program.model;

/**
 * Diese Klasse stellt static Methoden zur Verfuegung, um mit Byte-Arrays zu arbeiten
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
		if(theBoolean) return new byte[]{						//true: mit Einsen fuellen
							(byte) 255,
							(byte) 255,
							(byte) 255,
							(byte) 255
						};
		else			return new byte[]{						//false: mit Nullen fuellen
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
	 * Errechnet eine CRC-CCITT (CRC-16) Pruefsumme des uebergebenen byte-Arrays.
	 * Der Initialwert der Pruefsumme ist 0xFFFF (11111111 11111111).
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

        //for-Schleife ueber alle bytes
        for (int i=0;i<byteArray.length;i++){
        	mybyte = byteArray[i];
        	//for-Schleife ueber alle Bits des jeweiligen bytes
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
	 * Die Bitoperatoren wuerden ohne die Konvertierung der Bytes im
	 * 32-bit Bereich arbeiten, das wuerde hier zum falschen Ergebnis fuehren.
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
	
}
