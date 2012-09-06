package zisko.multicastor.testcases.model;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.Vector;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.UserInputData;
import zisko.multicastor.program.data.UserlevelData;
import zisko.multicastor.program.data.UserlevelData.Userlevel;
import zisko.multicastor.program.model.InputValidator;
import zisko.multicastor.program.model.WrongConfigurationException;
import zisko.multicastor.program.model.xmlParser;

/**
 * @author Daniela Gerz
 */
public class XmlParserTest extends TestCase
{	/*
	public final void testMCD() 
	{
		Logger log = Logger.getLogger("zisko.multicastor.program.controller.main");
		xmlParser parser = new xmlParser(log);
		Vector<MulticastData> v1 = new Vector<MulticastData>();
		
		MulticastData mcd = new MulticastData();
		
		mcd.setTyp(MulticastData.Typ.SENDER_V4);
		
		//Teste GroupIP
		String variable = "224.0.0.224";
		Inet4Address add1 = (Inet4Address) InputValidator.checkMC_IPv4(variable);
		mcd.setGroupIp(add1);
		//Teste SourceIP
		variable = "127.0.0.1";
		Inet4Address add2 = (Inet4Address) InputValidator.checkIPv4(variable);
		mcd.setSourceIp(add2);
		mcd.setTtl(32);
		mcd.setUdpPort(12335);
		mcd.setPacketLength(122);
		mcd.setPacketRateDesired(122);
		v1.add(mcd);

		try
		{
			parser.saveConfig("C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\XML_Config.xml", v1, null);
			parser.loadConfig("C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\XML_Config.xml", v1, null);
		}
		catch(WrongConfigurationException e)
		{
			System.out.println("ERROR in testMCD: "+e.getErrorMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	    assertEquals(MulticastData.Typ.SENDER_V4, v1.lastElement().getTyp());
	    assertEquals(add1, v1.lastElement().getGroupIp());
	}
	
	public final void testWrongMCD() 
	{
		Logger log = Logger.getLogger("zisko.multicastor.program.controller.main");
		xmlParser parser = new xmlParser(log);
		Vector<MulticastData> v1 = new Vector<MulticastData>();

		try
		{
			parser.loadConfig("C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\wrong.xml", v1, null);
		}
		catch(WrongConfigurationException e)
		{
			System.out.println("ERROR in testWrongMCD:"+e.getErrorMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	     assertEquals(MulticastData.Typ.SENDER_V4, v1.firstElement().getTyp());
	}*/
	
	public final void testULD()
	{
		Logger log = Logger.getLogger("zisko.multicastor.program.controller.main");
		xmlParser parser = new xmlParser(log);
		Vector<UserlevelData> v2 = new Vector<UserlevelData>();
		
		UserlevelData uld = new UserlevelData();
		uld.setUserlevel(Userlevel.CUSTOM);
		uld.setActiveField(true);
		
		v2.add(uld);
		
		try
		{
			parser.saveConfig("C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\uldTest.xml", null, v2);
			parser.loadConfig("C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\uldTest.xml", null, v2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		assertEquals(Userlevel.CUSTOM, v2.get(0).getUserlevel());
		assertEquals(true, v2.get(0).isActiveField());
		
	}
	/*
	public final void testUID() 
	{
		Logger log = Logger.getLogger("zisko.multicastor.program.controller.main");
		xmlParser parser = new xmlParser(log);
		Vector<UserInputData> v3 = new Vector<UserInputData>();
		
		UserInputData uid = new UserInputData();
		
		uid.setActiveButton(true);
		uid.setSelectedTab(MulticastData.Typ.SENDER_V4);
		uid.setSelectedUserlevel(Userlevel.BEGINNER);
		uid.setTtl("5");
		uid.setSelectedRows("1,2,3");
		uid.setColumnOrderString("4,5,6");
		uid.setColumnVisibilityString("7,8,9");
		
		v3.add(uid);

		try
		{
			parser.saveConfig("C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\XML_Config.xml", null, null, v3, null);
			parser.loadConfig("C:\\Users\\gerz\\Documents\\DHBW\\4. Semester\\swe\\configtest\\XML_Config.xml", null, null, v3, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	     assertEquals("true", v3.get(0).getActiveButton());
	     assertEquals("SENDER_V4", v3.get(0).getSelectedTab());
	     assertEquals("BEGINNER", v3.get(0).getSelectedUserlevel());
	     assertEquals("5", v3.get(0).getTtl());
	     assertEquals("1,2,3",v3.get(0).getSelectedRows());
	     assertEquals("4,5,6",v3.get(0).getColumnOrderString());
	     assertEquals("7,8,9",v3.get(0).getColumnVisibilityString());
	}*/
	
	public final void testLoadDefaultULD()
	{
		Logger log = Logger.getLogger("zisko.multicastor.program.controller.main");
		xmlParser parser = new xmlParser(log);
		Vector<MulticastData> v1 = new Vector<MulticastData>();
		Vector<UserlevelData> v2 = new Vector<UserlevelData>();
		
		try{
			parser.loadDefaultULD(v1, v2);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(SAXException e)
		{
			e.printStackTrace();
		}
		catch(WrongConfigurationException e)
		{
			e.printStackTrace();
		}
		
		//Teste Korrekte Standartwerte für Beginner Userlevel
		assertEquals(32, v1.get(0).getTtl());
		assertEquals(100, v1.get(0).getPacketLength());
		assertEquals(20, v1.get(0).getPacketRateDesired());
		assertEquals(MulticastData.Typ.SENDER_V4, v1.get(0).getTyp());
		assertEquals(MulticastData.Typ.SENDER_V6, v1.get(1).getTyp());

		assertEquals(UserlevelData.Userlevel.EXPERT, v2.get(0).getUserlevel());
		assertEquals(true,v2.get(0).isActiveField());
		
	}
}
