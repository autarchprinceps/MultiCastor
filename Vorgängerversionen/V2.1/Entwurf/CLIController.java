   
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
 
  

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;

import org.xml.sax.SAXException;

import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.interfaces.UIController;
import dhbw.multicastor.program.model.ConfigHandler;
import dhbw.multicastor.program.model.InputValidator;
import dhbw.multicastor.program.model.WrongConfigurationException;
import dhbw.multicastor.program.model.MulticastLogHandler.Event;


public class CLIController implements UIController {

	private MulticastController mc;
	private String[] args;
	private Boolean firstUpdate = true;
	private Boolean oneMulticast = false;

	public CLIController(String[] args) {
		this.args = args;
	}

	/**
	 * method to initialize the controller, here Arguments are analyzed
	 * 
	 * @param multicastController
	 */
	public void initialize(MulticastController multicastController) {
		this.mc = multicastController;
		
		if(args[0].equals("-g")){
			if (args[1].equals("s")) {
				startWithoutGui(Modus.SENDER);	
			}else if (args[1].equals("r")) {
				startWithoutGui(Modus.RECEIVER);
			}else {
				displayHelp();
			}
		}
		if(args[0].equals("-h")){
			displayHelp();
		}
		if(args[0].equals("-s")){
			mc.setCurrentModus(Modus.SENDER);
			startWithOneMC();
		}
		if(args[0].equals("-r")){
			mc.setCurrentModus(Modus.RECEIVER);
			startWithOneMC();
		}
	}

	/**
	 * method to start Multicastor with one Multicast. The Parameter of the Multicast must be 
	 * specified in the arguments
	 */
	private void startWithOneMC() {
		if(this.args.length <= 1){
			displayHelp();
		}
		mc.getLogger().log(Level.INFO, "Starting MultiCastor without a gui");
		setOneMulticast(true);
		if(mc.getCurrentModus() == Modus.SENDER){
			MulticastData m = null;
			if(InputValidator.checkMC_IPv4(args[1]) != null){			
				m = new IgmpMldData(	InputValidator.checkMC_IPv4(args[1]), //GroupIP
													InputValidator.checkIPv4(args[2]), //SourceIP
													InputValidator.checkPort(args[3]), //UDP Port
													InputValidator.checkIPv4PacketLength(args[4]), //PacketLength
													InputValidator.checkTimeToLive(args[5]), //TTL
													InputValidator.checkPacketRate(args[6]), //PacketRate
													true, //active 
													ProtocolType.IGMP);	 //Type
			}
			if(InputValidator.checkMC_IPv6(args[1]) != null){
				m = new IgmpMldData(	InputValidator.checkMC_IPv6(args[1]), //GroupIP
													InputValidator.checkIPv6(args[2]), //SourceIP
													InputValidator.checkPort(args[3]), //UDP Port
													InputValidator.checkIPv6PacketLength(args[4]), //PacketLength
													InputValidator.checkTimeToLive(args[5]), //TTL
													InputValidator.checkPacketRate(args[6]), //PacketRate
													true, //active 
													ProtocolType.MLD);
			}
			if(InputValidator.checkMAC(args[1]) != null){
				m = new MMRPData(ProtocolType.MMRP);
				((MMRPData) m).setMacGroupId(InputValidator.checkMACGroupAddress(args[1]));
				((MMRPData) m).setMacSourceId(InputValidator.checkMAC(args[2]));
				m.setPacketRateDesired(InputValidator.checkPacketRate(args[3]));
				m.setPacketLength(InputValidator.checkMmrpPacketLength(args[4]));
				m.setActive(true);
			}
			if(checkMCData(m))
			{
				mc.addMC(m);	
			}
			
		}
		else if(mc.getCurrentModus() == Modus.RECEIVER){
			if(InputValidator.checkMC_IPv4(args[1]) != null){			
				IgmpMldData m = new IgmpMldData(ProtocolType.IGMP);
				m.setGroupIp(InputValidator.checkMC_IPv4(args[1]));
				m.setUdpPort(InputValidator.checkPort(args[2]));
				m.setActive(true);
				mc.addMC(m);
			
			}
			if(InputValidator.checkMC_IPv6(args[1]) != null){
				IgmpMldData m = new IgmpMldData(ProtocolType.MLD);
				m.setGroupIp(InputValidator.checkMC_IPv6(args[1]));
				m.setUdpPort(InputValidator.checkPort(args[2]));
				m.setActive(true);
				mc.addMC(m);
			}
			if(InputValidator.checkMAC(args[1]) != null && InputValidator.checkMAC(args[2]) != null){
				MMRPData m = new MMRPData(ProtocolType.MMRP);
				m.setMacGroupId(InputValidator.checkMACGroupAddress(args[1]));
				m.setMacSourceId(InputValidator.checkMAC(args[2]));
				m.setTimerJoinMt(InputValidator.checkJoinMtTimer(args[3]));
				m.setActive(true);
				mc.addMC(m);
			}
		}
//		mc.getRegularLoggingTask().notify();
	}

	/**
	 * Method that validates the Parmameter, that are specified on the console to start
	 * with one Multicast
	 * 
	 * @param m - the MulticastData object that should be validated
	 * @return true if MulticastData is valid
	 */
	private boolean checkMCData(MulticastData m) {
		boolean error = false;
		if(m==null){
			System.out.println("Wrong Parameter: GroupID");
			error = true;
		}else
		{
			if(m.getProtocolType() == ProtocolType.IGMP || m.getProtocolType() == ProtocolType.MLD){			
				error = checkIgmpMld(m);			
			}
			else if(m.getProtocolType() == ProtocolType.MMRP){
				error = checkMmrp(m);
			}			
		}
		if(error){
			System.exit(0);
			return false;
		}
		return true;
	}

	/**
	 * Validate a MMRP MulticastData object
	 * @param m - the MMRP MulticastData object that should be validated
	 * @return true if MulticastData object is valid
	 */
	private boolean checkMmrp(MulticastData m) {
		boolean error = false;
		if(((MMRPData)m).getMacGroupId() == null)
		{
			System.out.println("Wrong Parameter: GroupID");
			error = true;
		}
		if(((MMRPData)m).getMacSourceId() == null)
		{
			System.out.println("Wrong Parameter: SourceID");
			error = true;
		}
		if(mc.getCurrentModus() == Modus.SENDER){
			if(((MMRPData)m).getPacketLength() < 0)
			{
				System.out.println("Wrong Parameter: Packet Length");
				error = true;
			}
			if(((MMRPData)m).getPacketRateDesired() < 0)
			{
				System.out.println("Wrong Parameter: Packet Rate");
				error = true;
			}
		}else if(mc.getCurrentModus() == Modus.RECEIVER){
			if(((MMRPData)m).getTimerJoinMt() < 0)
			{
				System.out.println("Wrong Parameter: Join MT Timer");
				error = true;
			}
		}
		return error;
	}

	/**
	 * Validate a IGMP or MLD MulticastData object
	 * @param m - the MulticastData object that should be validated
	 * @return true if MulticastData object is valid
	 */
	private boolean checkIgmpMld(MulticastData m) {
		boolean error = false;
		if(((IgmpMldData)m).getGroupIp() == null)
		{
			System.out.println("Wrong Parameter: GroupID");
			error = true;
		}
		if(((IgmpMldData)m).getUdpPort() < 0)
		{
			System.out.println("Wrong Parameter: Port");
			error = true;
		}
		if(mc.getCurrentModus() == Modus.SENDER){
			if(((IgmpMldData)m).getSourceIp() == null)
			{
				System.out.println("Wrong Parameter: SourceID");
				error = true;
			}			
			if(((IgmpMldData)m).getPacketLength() < 0)
			{
				System.out.println("Wrong Parameter: Packet Length");
				error = true;
			}
			if(((IgmpMldData)m).getTtl() < 0)
			{
				System.out.println("Wrong Parameter: TTL");
				error = true;
			}
			if(((IgmpMldData)m).getPacketRateDesired() < 0)
			{
				System.out.println("Wrong Parameter: Packet Rate");
				error = true;
			}
		}
		return error;
	}

	/**
	 * Method that prints a usage Message to the console
	 */
	private void displayHelp() {
		System.out.println("Usage:\tjava -jar multiCastor.jar [-h] [-g] configuration-file-path\n\t" +
				"type 'java -jar multiCastor.jar -h' for more information\n\n" +
				"To start the tool without a graphical user interface,\n" +
				"the configuration-file-path is required.\n\n" +
				"For more information about a valid configuration file\n" +
				"consult the user manual.\n\n" +
				"Options:\n" +
				"\t\t-h\tdisplays this help-message\n" +
				"\t\t-g\tstart the MultiCastor-Tool without\n" +
				"\t\t\ta graphical user interface\n" +
				"\t\t\tSyntax: -g [Modus: s (Sender)| r (Receiver)] <Config File>" + 
				"\t\t-s\tstart Sender with one active Multicast\n" + 
				"\t\t\t-s <Grp Adress> <Interface IP/MAC> <Port> \n" +
				"\t\t\t   <Packet Length> <TTL> <Packet Rate>\n" + 
				"\t\t\tExample: -s 224.0.0.1 127.0.0.1 4711 128 10 50\n" + 
				"\t\t-r\tstart Receiver with one active Multicast\n" +
				"\t\t\tIGMP/MLD: -r <GrpIP> <Port>\n" + 
				"\t\t\tMMRP:     -r <Group MAC> <Src MAC>\n" + 
				"\t\t\t");
		try {
            Runtime.getRuntime().removeShutdownHook(Main.hook);
        } catch (IllegalStateException ignore) {
            // VM already was shutting down
        } catch (NullPointerException ex){
        	//Shutdown Hook not added yet
        }
		System.exit(0);
	}

	/**
	 * Method to start Multicastor without GUI but with a configuration file
	 * which will be loaded
	 */
	private void startWithoutGui(Modus modus) {
		if(args.length == 3){
			mc.setCurrentModus(modus);
			mc.getLogger().log(Level.INFO, "Starting MultiCastor without a gui");
			//load Config
			ConfigHandler config = mc.getConfig();
			try {
				config.loadConfigWithoutGUI(args[2], mc);
			} catch (FileNotFoundException e) {
				mc.getLogger().log(Level.SEVERE, "Couldn't find Configuration File", Event.ERROR);
				System.exit(0);
			} catch (SAXException e) {
				mc.getLogger().log(Level.SEVERE, "Configuration File could not be loaded correctly", Event.ERROR);
				System.exit(0);
			} catch (IOException e) {
				mc.getLogger().log(Level.SEVERE, "Couldn't load Configuration File", Event.ERROR);
				System.exit(0);
			} catch (WrongConfigurationException e) {
				mc.getLogger().log(Level.SEVERE, e.getMessage(), Event.ERROR);
				System.exit(0);
			}
		}
		else{
			displayHelp();
		}
		
	}

	/**
	 * Method that prints current Values to the Console
	 * this method is frequently called by the update thread
	 */
	public void updateCli() {
		MulticastData stream = mc.getMCByIndex(0);
		if(isFirstUpdate()){
			if(mc.getCurrentModus() == Modus.SENDER){
				//Header
				System.out.println("");
				System.out.println("| Group IP           | Source	          | Port | Mbit/s | #Sent  |");
				System.out.println("+--------------------+--------------------+------+--------+--------+");
			}else if(mc.getCurrentModus() == Modus.RECEIVER){
				System.out.println("");
				System.out.println("| Group IP           | Source             | Port | Mbit/s | #Rec   |");
				System.out.println("+--------------------+--------------------+------+--------+--------+");
			}
			this.setFirstUpdate(false);
		}		
		System.out.print("\r" + buildUpdateString(stream));
	}
	
	/**
	 * Method that puts together the string which will be printed to the console
	 * It makes sure that the string prints a table like output to the console
	 * @param mc - the current active MulticastData object
	 * @return the formatted string
	 */
	private String buildUpdateString(MulticastData mc){
		String groupID = "";
		String SourceID = "";
		String Port = "";
		String traffic = "";
		String Counter = "";
		
		DecimalFormat ret = new DecimalFormat("##0.000");
		traffic = ret.format((mc.getTraffic() / 1024.0 / 1024.0 * 8.0));
		Counter = new Long(mc.getPacketCount()).toString();
		if(mc.getProtocolType() == ProtocolType.IGMP || mc.getProtocolType() == ProtocolType.MLD){
			IgmpMldData m=(IgmpMldData)mc;
			groupID = m.getGroupIp().toString();
			if(this.mc.getCurrentModus() == Modus.SENDER){
				SourceID = m.getSourceIp().toString();	
			}else {
				SourceID = "        ----        ";
			}			
			Port = new Integer(m.getUdpPort()).toString();
		} else if(mc.getProtocolType() == ProtocolType.MMRP){
			MMRPData m = (MMRPData)mc;
			groupID = m.getMacGroupId().toString();
			SourceID = m.getMacSourceId().toString();
			Port = " ---- ";
		}
		return fillOutput(groupID, SourceID, Port, traffic, Counter);
	}

	/**
	 * method to make sure the output string has the correct length
	 * 
	 * @param groupID Value in column 1
	 * @param SourceID value in column 2
	 * @param Port value in column 3
	 * @param traffic value in column 4
	 * @param Counter value in column 5
	 * @return the string wiht correct lenght
	 */
	private String fillOutput(String groupID, String SourceID, String Port,
			String traffic, String Counter) {
		String output;
		while(groupID.length() < 20){
			groupID = groupID + " ";
		}
		while(SourceID.length() < 20){
			SourceID = SourceID + " ";
		}
		while(Port.length() < 6){
			Port = Port + " ";
		}
		while(traffic.length()< 8){
			traffic = traffic + " ";
		}
		while(Counter.length() < 8){
			Counter = Counter + " ";
		}
		output = "|" + groupID + "|" + SourceID + "|" + Port + "|" + traffic + "|" + Counter + "|";
		return output;
	}

	public Boolean isFirstUpdate() {
		return firstUpdate;
	}

	public void setFirstUpdate(Boolean firstUpdate) {
		this.firstUpdate = firstUpdate;
	}

	public Boolean isOneMulticast() {
		return oneMulticast;
	}

	public void setOneMulticast(Boolean oneMulticast) {
		this.oneMulticast = oneMulticast;
	}

}
