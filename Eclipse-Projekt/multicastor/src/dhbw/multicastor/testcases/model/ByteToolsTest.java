   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenstr�men. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie k�nnen es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation ver�ffentlicht, 
 *	weitergeben und/oder modifizieren, gem�� Version 3 der Lizenz.
 *
 *  Die Ver�ffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT F�R EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
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
 
 package dhbw.multicastor.testcases.model;

import static org.junit.Assert.*;

import java.awt.image.ConvolveOp;

import org.junit.Before;
import org.junit.Test;

import dhbw.multicastor.program.model.ByteTools;


public class ByteToolsTest {
	static int 		int_min 		= Integer.MIN_VALUE,
			   		int_max 		= Integer.MAX_VALUE,
			   		int_0 			= 0,
			   		// (2 byte auf 1)
			   		short_max		= 0xFFFF,
			   		short_0			= 0x0000;
	
	byte[] 			hexArray2_max,
				   	hexArray2_0,
				   	hexArray4_max,
				   	hexArray4_min,
				   	hexArray4_0,
				   	hexArray4_bool_true,
				   	hexArray4_bool_false,
				   	hexArray8_max,
				   	hexArray8_min,
				   	hexArray8_0;
	
	static long		long_min		= Long.MIN_VALUE,
					long_max		= Long.MAX_VALUE,
					long_0  		= 0;

	@Before
	public void setUp(){
		//2^31 - 1 = 2147483647 = 7FFFFFFF (Hex)
		hexArray4_max = new byte[]{
									(byte) 0x7F,
									(byte) 0xFF,
									(byte) 0xFF,
									(byte) 0xFF};
		
		//int_min entspricht vorzeichenbit auf 1, rest 0
		//=> bin�r 10000000 00000000 00000000 00000000
		//=> hex   80		00		 00		  00
		hexArray4_min = new byte[]{
									(byte) 0x80,
									(byte) 0x00,
									(byte) 0x00,
									(byte) 0x00};
		
		//int_null = 0
		hexArray4_0 = new byte[]{0,0,0,0};
		
//long-arrays �quivalent zu int-arrays, nur l�ngere arrays und h�here Wertebereiche
		hexArray8_max = new byte[]{
				(byte) 0x7F,
				(byte) 0xFF,
				(byte) 0xFF,
				(byte) 0xFF,
				(byte) 0xFF,
				(byte) 0xFF,
				(byte) 0xFF,
				(byte) 0xFF};
	
		hexArray8_min = new byte[]{
				(byte) 0x80,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00};
		
		hexArray8_0 = new byte[]{
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00};
		
		hexArray4_bool_false = new byte[]{
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00,
				(byte) 0x00};
		
		hexArray4_bool_true = new byte[]{
				(byte) 0xFF,
				(byte) 0xFF,
				(byte) 0xFF,
				(byte) 0xFF};
		
//short-arrays sind auch �quivalent zu den int-arrays
//es werden allerdings nur die letzten 2 Bytes genommen.
//negative Werte sind per Definition nicht erlaubt.
		hexArray2_max = new byte[]{
				(byte) 255,
				(byte) 255};
		
		hexArray2_0 = new byte[]{
				(byte) 0,
				(byte) 0};
	}
	
	@Test
	public void testConvertByteInt() {
		//int_max == 2^31 - 1 = 2147483647 = 7FFFFFFF (Hex)
		byte[] result = ByteTools.convertToByte(int_max);
		assertArrayEquals(hexArray4_max, result);
		assertEquals(int_max, ByteTools.byteToInt(result));
		
		result = ByteTools.convertToByte(int_min);
		assertArrayEquals(hexArray4_min, result);
		assertEquals(int_min, ByteTools.byteToInt(result));
		
		result = ByteTools.convertToByte(int_0);
		assertArrayEquals(hexArray4_0, result);
		assertEquals(int_0, ByteTools.byteToInt(result));
	}

	@Test
	public void testConvertByteLong() {
		byte[] result = ByteTools.convertToByte(long_max);
		assertArrayEquals(hexArray8_max, result);
		assertEquals(long_max, ByteTools.byteToLong(result));
		
		result = ByteTools.convertToByte(long_min);
		assertArrayEquals(hexArray8_min, result);
		assertEquals(long_min, ByteTools.byteToLong(result));
		
		result = ByteTools.convertToByte(long_0);
		assertArrayEquals(hexArray8_0, result);
		assertEquals(long_0, ByteTools.byteToLong(result));
	}

	@Test
	public void testConvertShortByte() {
		byte[] result = ByteTools.convertToShortByte(short_max);
		assertArrayEquals(hexArray2_max, result);
		assertEquals(short_max, ByteTools.shortByteToInt(result));
		
		result = ByteTools.convertToShortByte(short_0);
		assertArrayEquals(hexArray2_0, result);
		assertEquals(short_0, ByteTools.shortByteToInt(result));
		
		//Pr�fen ob die Zahl richtig abgeschnitten wird
		result = ByteTools.convertToShortByte(int_max);
		assertArrayEquals(hexArray2_max, result);
		assertEquals(short_max, ByteTools.shortByteToInt(result));
	}

	@Test
	public void testConvertToByteBoolean() {
		byte[] result = ByteTools.convertToByte(true);
		assertArrayEquals(hexArray4_bool_true, result);
		
		result = ByteTools.convertToByte(false);
		assertArrayEquals(hexArray4_bool_false, result);
	}

	@Test
	public void testCrc16() {
		byte[] result1 = ByteTools.crc16(new byte[]{0,0,0,0,0,0,0,0});
		assertArrayEquals(new byte[]{(byte)0x31,(byte)0x3E}, result1);
		
		result1 = ByteTools.crc16(new byte[]{(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255});
		assertArrayEquals(new byte[]{(byte)0x97,(byte)0xDF}, result1);
	}
}