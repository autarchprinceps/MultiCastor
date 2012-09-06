   
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
 
 package dhbw.multicastor.testcases.model;

import dhbw.multicastor.program.model.InputValidator;
import junit.framework.TestCase;

/**
 * @author Johannes Beutel
 */
public class InputValidatorTest extends TestCase {

	/**
	 * Test method for {@link dhbw.multicastor.program.model.InputValidator#checkIPv4(java.lang.String)}.
	 */
	public final void testCheckIPv4(){
        String variable1 = "192.168.178.001";
        String variable2 = "192.168.178.1";
        String variable3 = "255.255.255.254";
        String variable4 = "0.0.0.0";
        String variable5 = "1.0.0.1";
        String variable6 = "001.0.0.001";
        String variable7 = "225.0.0.131";
        String variable8 = "192.0.0.255";
        String variable9 = "127.0.0.1";

        
        assertEquals("192.168.178.1",InputValidator.checkIPv4(variable1).getHostAddress());
        assertEquals("192.168.178.1",InputValidator.checkIPv4(variable2).getHostAddress());
        assertEquals("255.255.255.254",InputValidator.checkIPv4(variable3).getHostAddress());
        assertEquals("0.0.0.0",InputValidator.checkIPv4(variable4).getHostAddress());
        assertEquals("1.0.0.1",InputValidator.checkIPv4(variable5).getHostAddress());
        assertEquals("1.0.0.1",InputValidator.checkIPv4(variable6).getHostAddress());
        assertNull(InputValidator.checkIPv4(variable7));
        assertEquals("192.0.0.255",InputValidator.checkIPv4(variable8).getHostAddress());
        assertEquals("127.0.0.1",InputValidator.checkIPv4(variable9).getHostAddress());
	}
	
	
	public final void testCheckMAC(){
        String variable1 = "10:00:00:00:00:00";
        String variable2 = "11";
        String variable3 = "99:33:BB:00:00:FF";
        
        byte[] b1 = {'1','0','0','0','0','0','0','0','0','0','0','0'};
        System.out.println(b1[0]+"   " + InputValidator.checkMAC(variable1).getMacAddress()[0]);
        assertNull(InputValidator.checkMAC(variable2));
        for(int i = 0; i<12; i++){
        	assertEquals(b1[i], InputValidator.checkMAC(variable1).getMacAddress()[i]);
        }
	}

	/**
	 * Test method for {@link dhbw.multicastor.program.model.InputValidator#checkIPv6(java.lang.String)}.
	 */
	public final void testCheckIPv6() {
        String variable1 = "::1";
        String variable2 = "::0";
        String variable3 = "FF00::1";
        String variable4 = "0:0:0:0:0:0:0:1";
        String variable5 = "0:0:0:0:0:0:1";
        String variable6 = "192.168.178.1";
        String variable7 = "::192:168:178:1";
        String variable8 = "test";
        String variable9 = "2001:0DB8:85A3:08D3:1319:8A2E:0370:7344";
        
        
        assertEquals("0:0:0:0:0:0:0:1",InputValidator.checkIPv6(variable1).getHostAddress());
        assertNull(InputValidator.checkIPv6(variable2));
        assertNull(InputValidator.checkIPv6(variable3));
        assertEquals("0:0:0:0:0:0:0:1",InputValidator.checkIPv6(variable4).getHostAddress());
        assertNull(InputValidator.checkIPv6(variable5));
        assertNull(InputValidator.checkIPv6(variable6));
        assertEquals("0:0:0:0:192:168:178:1",InputValidator.checkIPv6(variable7).getHostAddress());
        assertNull(InputValidator.checkIPv6(variable8));
        assertEquals("2001:db8:85a3:8d3:1319:8a2e:370:7344",InputValidator.checkIPv6(variable9).getHostAddress());
	}

	/**
	 * Test method for {@link dhbw.multicastor.program.model.InputValidator#checkMC_IPv4(java.lang.String)}.
	 */
	public final void testCheckMC_IPv4() {
		String variable1 = "224.0.0.1";
        String variable2 = "233.22.34.9";
        String variable3 = "224.000.000.001";
        String variable4 = "224.0.0.0";
        String variable5 = "192.168.178.1";
        String variable6 = "0.0.0.0";
        String variable7 = "test";
        String variable8 = "";
        	        
        assertEquals("224.0.0.1",InputValidator.checkMC_IPv4(variable1).getHostAddress());
        assertEquals("233.22.34.9",InputValidator.checkMC_IPv4(variable2).getHostAddress());
        assertEquals("224.0.0.1",InputValidator.checkMC_IPv4(variable3).getHostAddress());
        assertEquals("224.0.0.0",InputValidator.checkMC_IPv4(variable4).getHostAddress());
        assertNull(InputValidator.checkMC_IPv4(variable5));
        assertNull(InputValidator.checkMC_IPv4(variable6));
        assertNull(InputValidator.checkMC_IPv4(variable7));
        assertNull(InputValidator.checkMC_IPv4(variable8));
	}

	/**
	 * Test method for {@link dhbw.multicastor.program.model.InputValidator#checkMC_IPv6(java.lang.String)}.
	 */
	public final void testCheckMC_IPv6() {
        String variable1 = "FF00::1";
        String variable2 = "::1";
        String variable3 = "test";
        String variable4 = "FF23:2342:2343::2345";
        String variable5 ="ff00:0:0:0:0:0:0:5";
        	        
        assertEquals("ff00:0:0:0:0:0:0:1",InputValidator.checkMC_IPv6(variable1).getHostAddress());
        assertNull(InputValidator.checkMC_IPv6(variable2));
        assertNull(InputValidator.checkMC_IPv6(variable3));
        assertEquals("ff23:2342:2343:0:0:0:0:2345",InputValidator.checkMC_IPv6(variable4).getHostAddress());
        assertNotNull(InputValidator.checkMC_IPv6(variable5).getHostAddress());
        
        /*assertFalse(InputValidator.checkMC_IPv6(variable5));
        assertFalse(InputValidator.checkMC_IPv6(variable6));
        assertFalse(InputValidator.checkMC_IPv6(variable7));
        assertFalse(InputValidator.checkMC_IPv6(variable8));*/
		
	}

	/**
	 * Test method for {@link dhbw.multicastor.program.model.InputValidator#checkIPv4PacketLength(java.lang.String)}.
	 */
	public final void testCheckIPv4PacketLength() {
		String variable1 = "52";
        String variable2 = "65507";
        String variable3 = "0";
        String variable4 = "65508";
        String variable5 = "test";
        
        assertEquals(52,InputValidator.checkIPv4PacketLength(variable1));
        assertEquals(65507,InputValidator.checkIPv4PacketLength(variable2));
        assertEquals(-1,InputValidator.checkIPv4PacketLength(variable3));
        assertEquals(-1,InputValidator.checkIPv4PacketLength(variable4));
        assertEquals(-2,InputValidator.checkIPv4PacketLength(variable5));
	}
	
	/**
	 * Test method for {@link dhbw.multicastor.program.model.InputValidator#checkIPv6PacketLength(java.lang.String)}.
	 */

	public final void testCheckIPv6PacketLength() {
		String variable1 = "52";
        String variable2 = "65527";
        String variable3 = "0";
        String variable4 = "65528";
        String variable5 = "test";
        
        assertEquals(52,InputValidator.checkIPv6PacketLength(variable1));
        assertEquals(65527,InputValidator.checkIPv6PacketLength(variable2));
        assertEquals(-1,InputValidator.checkIPv6PacketLength(variable3));
        assertEquals(-1,InputValidator.checkIPv6PacketLength(variable4));
        assertEquals(-2,InputValidator.checkIPv6PacketLength(variable5));	
	}

	/**
	 * Test method for {@link dhbw.multicastor.program.model.InputValidator#checkPort(java.lang.String)}.
	 */
	public final void testCheckPort() {
		
		String variable1  = "65535";
        String variable2 = "12345";
        String variable3 = "0";
        String variable4 = "01020";
        String variable5 = "65536";
        String variable6 = "test";
        
        assertEquals(65535,InputValidator.checkPort(variable1));
        assertEquals(12345,InputValidator.checkPort(variable2));
        assertEquals(-1,InputValidator.checkPort(variable3));
        assertEquals(1020,InputValidator.checkPort(variable4));
        assertEquals(-1,InputValidator.checkPort(variable5));
        assertEquals(-2,InputValidator.checkPort(variable6));
	}
	
	/**
	 * Test method for {@link dhbw.multicastor.program.model.InputValidator#checkTimeToLive(java.lang.String)}.
	 */
	public final void testCheckTimeToLive() {
		String variable = "1";
        String variable2 = "32";
        String variable3 = "0";
        String variable4 = "33";
        String variable5 = "test";
        
        assertEquals(1,InputValidator.checkTimeToLive(variable));
        assertEquals(32,InputValidator.checkTimeToLive(variable2));
        assertEquals(-1,InputValidator.checkTimeToLive(variable3));
        assertEquals(33,InputValidator.checkTimeToLive(variable4));
        assertEquals(-2,InputValidator.checkTimeToLive(variable5));
	}

	/**
	 * Test method for {@link dhbw.multicastor.program.model.InputValidator#checkPacketRate(java.lang.String)}.
	 */
	public final void testCheckPacketRate() {
		String variable = "1";
        String variable2 = "1000";
        String variable3 = "0";
        String variable4 = "0001";
        String variable5 = "test";
        
        assertEquals(1,InputValidator.checkPacketRate(variable));
        assertEquals(1000,InputValidator.checkPacketRate(variable2));
        assertEquals(-1,InputValidator.checkPacketRate(variable3));
        assertEquals(1,InputValidator.checkPacketRate(variable4));
        assertEquals(-2,InputValidator.checkPacketRate(variable5));
	}

}
