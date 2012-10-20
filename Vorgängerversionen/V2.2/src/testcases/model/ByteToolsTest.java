package testcases.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import program.model.ByteTools;

public class ByteToolsTest {
	static int 		int_min 		= Integer.MIN_VALUE,
			   		int_max 		= Integer.MAX_VALUE,
			   		int_0 			= 0,
			   		// (2 byte of 1)
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

		//int_min is leading sign of 1, rest 0
		//=> binary 10000000 00000000 00000000 00000000
		//=> hex   80		00		 00		  00
		hexArray4_min = new byte[]{
									(byte) 0x80,
									(byte) 0x00,
									(byte) 0x00,
									(byte) 0x00};

		//int_null = 0
		hexArray4_0 = new byte[]{0,0,0,0};

		//long-arrays aquivalent to int-arrays, only longer arrays and bigger valuepart
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

		//short-arrays aquivalent to int-arrays
		//only last two bits are used
		//negative values not allowed (definition)
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

		//check if number is great cutted
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