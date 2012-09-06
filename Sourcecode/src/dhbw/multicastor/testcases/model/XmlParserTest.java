   
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

import java.io.IOException;
import java.net.Inet4Address;
import java.util.Vector;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.interfaces.IgmpMldInterface;
import dhbw.multicastor.program.model.InputValidator;
import dhbw.multicastor.program.model.WrongConfigurationException;
import dhbw.multicastor.program.model.xmlParser;
import dhbw.multicastor.program.model.xmlParserReceiver;
import dhbw.multicastor.program.model.xmlParserSender;


/**
 * @author Daniela Gerz
 */
public class XmlParserTest extends TestCase { /*
											 * public final void testMCD() {
											 * Logger log = Logger.getLogger(
											 * "dhbw.multicastor.program.controller.main"
											 * ); xmlParser parser = new
											 * xmlParser(log);
											 * Vector<MulticastData> v1 = new
											 * Vector<MulticastData>();
											 * 
											 * MulticastData mcd = new
											 * MulticastData();
											 * 
											 * mcd.setTyp(MulticastData.Typ.
											 * SENDER_V4);
											 * 
											 * //Teste GroupIP String variable =
											 * "224.0.0.224"; Inet4Address add1
											 * = (Inet4Address)
											 * InputValidator.checkMC_IPv4
											 * (variable); mcd.setGroupIp(add1);
											 * //Teste SourceIP variable =
											 * "127.0.0.1"; Inet4Address add2 =
											 * (Inet4Address)
											 * InputValidator.checkIPv4
											 * (variable);
											 * mcd.setSourceIp(add2);
											 * mcd.setTtl(32);
											 * mcd.setUdpPort(12335);
											 * mcd.setPacketLength(122);
											 * mcd.setPacketRateDesired(122);
											 * v1.add(mcd);
											 * 
											 * try { parser.saveConfig(
											 * "C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\XML_Config.xml"
											 * , v1, null); parser.loadConfig(
											 * "C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\XML_Config.xml"
											 * , v1, null); }
											 * catch(WrongConfigurationException
											 * e) { System.out.println(
											 * "ERROR in testMCD: "
											 * +e.getErrorMessage()); }
											 * catch(Exception e) {
											 * e.printStackTrace(); }
											 * 
											 * assertEquals(MulticastData.Typ.
											 * SENDER_V4,
											 * v1.lastElement().getTyp());
											 * assertEquals(add1,
											 * v1.lastElement().getGroupIp()); }
											 * 
											 * public final void testWrongMCD()
											 * { Logger log = Logger.getLogger(
											 * "dhbw.multicastor.program.controller.main"
											 * ); xmlParser parser = new
											 * xmlParser(log);
											 * Vector<MulticastData> v1 = new
											 * Vector<MulticastData>();
											 * 
											 * try { parser.loadConfig(
											 * "C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\wrong.xml"
											 * , v1, null); }
											 * catch(WrongConfigurationException
											 * e) { System.out.println(
											 * "ERROR in testWrongMCD:"
											 * +e.getErrorMessage()); }
											 * catch(Exception e) {
											 * e.printStackTrace(); }
											 * 
											 * assertEquals(MulticastData.Typ.
											 * SENDER_V4,
											 * v1.firstElement().getTyp()); }
											 */
//	public void testLoadDefaultMulticastData() {
//		Logger log = Logger
//				.getLogger("dhbw.multicastor.program.controller.main");
//		xmlParser parser = new xmlParser(log);
//		
//		try {
//			parser.loadDefaultMulticastData();
//		} catch (WrongConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SAXException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println();
//		assertEquals(DefaultMulticastData.getIgmp().getActive(), true);
//		assertEquals(DefaultMulticastData.getMmrp().getActive(), true);
//		assertEquals(DefaultMulticastData.getMld().getActive(), true);
//		assertEquals(DefaultMulticastData.getIgmp().getPacketRateDesired(), 50);
//		assertEquals(DefaultMulticastData.getMmrp().getPacketRateDesired(), 50);
//		assertEquals(DefaultMulticastData.getMld().getPacketRateDesired(), 50);
//		assertEquals(DefaultMulticastData.getIgmp().getPacketLength(), 2048);
//		assertEquals(DefaultMulticastData.getMmrp().getPacketLength(), 2048);
//		assertEquals(DefaultMulticastData.getMld().getPacketLength(), 2048);
//		assertEquals(DefaultMulticastData.getIgmp().getGroupIp().getHostAddress(), "230.0.0.1");
//		assertEquals(DefaultMulticastData.getMmrp().getMacGroupId().toString(), "12:34:56:78:9A:BC");
//		assertEquals(DefaultMulticastData.getMld().getGroupIp().getHostAddress(), "ff00:0:0:0:0:0:0:5");
//	}

	public void testSaveConfig() {
		Logger log = Logger
				.getLogger("dhbw.multicastor.program.controller.main");
		xmlParser parser = new xmlParserSender(log);
		Vector<MulticastData> v1 = new Vector<MulticastData>();
		IgmpMldData data = new IgmpMldData(ProtocolType.IGMP);
		data.setActive(true);
		data.setHostID("192.168.20.10");
		data.setAverageInterruptionTime(1);
		data.setPacketLength(32);
		data.setProtocolType(MulticastData.ProtocolType.IGMP);
		data.setTtl(7);
		data.setUdpPort(80);
		Inet4Address adr = (Inet4Address) InputValidator.checkIPv4("127.0.0.1");
		data.setGroupIp((Inet4Address) InputValidator
				.checkMC_IPv4("230.0.0.10"));
		data.setSourceIp(adr);
		data.setPacketRateDesired(55);
		v1.add(data);
		try {
			parser.saveConfig(
					"src/dhbw/multicastor/testcases/testdata/Multicastor.xml",
					v1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void testSaveAndLoadConfig() {
		Logger log = Logger
				.getLogger("dhbw.multicastor.program.controller.main");
		xmlParser parser = new xmlParserSender(log);
		Vector<MulticastData> v1 = new Vector<MulticastData>();
		Vector<MulticastData> v2 = new Vector<MulticastData>();
		IgmpMldData data = new IgmpMldData(ProtocolType.IGMP);
		data.setActive(true);
		data.setHostID("192.168.20.10");
		data.setAverageInterruptionTime(1);
		data.setPacketLength(52);
		data.setProtocolType(MulticastData.ProtocolType.IGMP);
		data.setTtl(7);
		data.setUdpPort(80);
		Inet4Address adr = (Inet4Address) InputValidator.checkIPv4("127.0.0.1");
		data.setGroupIp((Inet4Address) InputValidator
				.checkMC_IPv4("230.0.0.10"));
		data.setSourceIp(adr);
		data.setPacketRateDesired(55);
		v1.add(data);
		try {
			parser.saveConfig(
					"src/dhbw/multicastor/testcases/testdata/Multicastor.xml",
					v1);
			parser.loadConfig(
					"src/dhbw/multicastor/testcases/testdata/Multicastor.xml",
					v2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IgmpMldInterface igmp = (IgmpMldInterface) v1.get(0);
		IgmpMldInterface igmp2 = (IgmpMldInterface) v2.get(0);
		assertEquals(igmp.getActive(), igmp2.getActive());
		assertEquals(igmp.getColor(), igmp2.getColor());
		assertEquals(igmp.getPacketLength(), igmp2.getPacketLength());
		assertEquals(igmp.getProtocolType(), igmp2.getProtocolType());
		assertEquals(igmp.getUdpPort(), igmp2.getUdpPort());
		assertEquals(igmp.getGroupIp(), igmp2.getGroupIp());
		assertEquals(igmp.getSourceIp(), igmp2.getSourceIp());

	}

	public void testLoadConfig() {
		Logger log = Logger
				.getLogger("dhbw.multicastor.program.controller.main");
		xmlParser parser = new xmlParserReceiver(log);
		Vector<MulticastData> v1 = new Vector<MulticastData>();
		try {
			parser.loadConfig(
					"src/dhbw/multicastor/testcases/testdata/Multicastor.xml",
					v1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < v1.size(); i++) {
			System.out.println(v1.get(i).getProtocolType());
		}
		assertEquals(v1.get(0).getActive(), true);
	}

	// public final void testULD()
	// {
	// Logger log =
	// Logger.getLogger("dhbw.multicastor.program.controller.main");
	// xmlParser parser = new xmlParser(log);
	// Vector<UserlevelData> v2 = new Vector<UserlevelData>();
	//
	// UserlevelData uld = new UserlevelData();
	// uld.setUserlevel(Userlevel.CUSTOM);
	// uld.setActiveField(true);
	//
	// v2.add(uld);
	//
	// try
	// {
	// parser.saveConfig("C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\uldTest.xml",
	// null, v2);
	// parser.loadConfig("C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\uldTest.xml",
	// null, v2);
	// }
	// catch(Exception e)
	// {
	// e.printStackTrace();
	// }
	//
	// assertEquals(Userlevel.CUSTOM, v2.get(0).getUserlevel());
	// assertEquals(true, v2.get(0).isActiveField());
	//
	// }
	/*
	 * public final void testUID() { Logger log =
	 * Logger.getLogger("dhbw.multicastor.program.controller.main"); xmlParser
	 * parser = new xmlParser(log); Vector<UserInputData> v3 = new
	 * Vector<UserInputData>();
	 * 
	 * UserInputData uid = new UserInputData();
	 * 
	 * uid.setActiveButton(true);
	 * uid.setSelectedTab(MulticastData.Typ.SENDER_V4);
	 * uid.setSelectedUserlevel(Userlevel.BEGINNER); uid.setTtl("5");
	 * uid.setSelectedRows("1,2,3"); uid.setColumnOrderString("4,5,6");
	 * uid.setColumnVisibilityString("7,8,9");
	 * 
	 * v3.add(uid);
	 * 
	 * try { parser.saveConfig(
	 * "C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\XML_Config.xml"
	 * , null, null, v3, null); parser.loadConfig(
	 * "C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\XML_Config.xml"
	 * , null, null, v3, null); } catch(Exception e) { e.printStackTrace(); }
	 * 
	 * assertEquals("true", v3.get(0).getActiveButton());
	 * assertEquals("SENDER_V4", v3.get(0).getSelectedTab());
	 * assertEquals("BEGINNER", v3.get(0).getSelectedUserlevel());
	 * assertEquals("5", v3.get(0).getTtl());
	 * assertEquals("1,2,3",v3.get(0).getSelectedRows());
	 * assertEquals("4,5,6",v3.get(0).getColumnOrderString());
	 * assertEquals("7,8,9",v3.get(0).getColumnVisibilityString()); }
	 */

	// public final void testLoadDefaultULD()
	// {
	// Logger log =
	// Logger.getLogger("dhbw.multicastor.program.controller.main");
	// xmlParser parser = new xmlParser(log);
	// Vector<MulticastData> v1 = new Vector<MulticastData>();
	// Vector<UserlevelData> v2 = new Vector<UserlevelData>();
	//
	// try{
	// parser.loadDefaultULD(v1, v2);
	// }catch(IOException e)
	// {
	// e.printStackTrace();
	// }
	// catch(SAXException e)
	// {
	// e.printStackTrace();
	// }
	// catch(WrongConfigurationException e)
	// {
	// e.printStackTrace();
	// }
	//
	// //Teste Korrekte Standartwerte für Beginner Userlevel
	// assertEquals(32, v1.get(0).getTtl());
	// assertEquals(100, v1.get(0).getPacketLength());
	// assertEquals(20, v1.get(0).getPacketRateDesired());
	// assertEquals(MulticastData.ProtocolType.SENDER_V4, v1.get(0).getTyp());
	// assertEquals(MulticastData.ProtocolType.SENDER_V6, v1.get(1).getTyp());
	//
	// assertEquals(UserlevelData.Userlevel.EXPERT, v2.get(0).getUserlevel());
	// assertEquals(true,v2.get(0).isActiveField());
	//
	// }
}
