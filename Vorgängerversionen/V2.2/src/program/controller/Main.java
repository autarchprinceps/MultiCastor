package program.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import program.data.MulticastData;
import program.data.MulticastData.Typ;
import program.data.UserInputData;
import program.model.InputValidator;
import program.model.MulticastLogHandler;
import program.model.NetworkAdapter;
import program.model.XmlParserConfig;

/**
 * Main-Methode.
 * @author Thomas Lüder
 * 
 */

public class Main {
	
	static String version="2.0.3";

	/**
	 * Initialisiert den MulticastController sowie die GUI und liest die
	 * Parameter ein, die dem Programm übegeben wurden und startet den entsprechenden Programmteil.
	 * @param args Ein Feld aus Strings, das die Parameter enthält, die dem Programm in der Kommandozeile übergeben wurden.
	 * @throws UnknownHostException
	 * 
	 * ExitStatus:
	 * 0 -> normaler Exit
	 * 1 -> Parameter Fehler
	 * 2 -> Configfile Fehler
	 * 3 -> Rechte Fehler
	 */

	public static void main(String[] args) throws UnknownHostException {

		if(args.length == 0) {
			startWithGui(true);
		}
		else {
			Map<String, String> m = handleParameter(args);
			
			if(m.containsKey("gui")) {
				if(m.containsKey("mode")) {
					if(m.get("mode") == "receiver") {
						startWithGui(false);
					} else{
						startWithGui(true);
					}
				} else {
					startWithGui(true);
				}
			} 
			else if(m.containsKey("config"))
				startWithConfig(m.get("config"));
			else {
				startWithParameter(m);
			}
		}


	}
	
	/**
	 * Startet das Programm ohne graphische Oberfläche mit dem Multicast-Stream, der über die angegebenen Parameter definiert wurde.
	 * 
	 * @param m HashMap aus zwei Strings in der sich die Auswertung der eingelesenen Parameter befinden 
	 */

	private static void startWithParameter(Map<String, String> m) {
		Typ typ = Typ.UNDEFINED;
		int port = 4711;
		InetAddress groupip = null;
		InetAddress sourceip = null;
		byte[] groupmac = null;
		String sourcemac = null;
		int packetLength = 1024;
		int ttl = 32;
		int packetRateDesired = 10;
		
		/*
		 * MulticastData Typ herausfinden
		 */
		if(m.containsKey("mode") && m.containsKey("netmode")) {
			typ =  MulticastData.Typ.valueOf(m.get("mode").toUpperCase() + "_" + m.get("netmode").toUpperCase());
		}
		else {
			help("Unkown mode");
		}

		/*
		 * Check permissions of network device, für mmrp
		 */
		
		if(typ.toShortString() == "MMRP" && !NetworkAdapter.isPcapOk()) {
			help("no permissions for network device");
			System.exit(3);
		}
		

		/*
		 * Group IP herausfinden
		 */
		if(m.containsKey("group-ip")) {
			if(typ.toShortString() == "IPv4" && InputValidator.checkMC_IPv4(m.get("group-ip")) != null)
				groupip = InputValidator.checkMC_IPv4(m.get("group-ip"));
			if(typ.toShortString() == "IPv6" && InputValidator.checkMC_IPv6(m.get("group-ip")) != null)
				groupip = InputValidator.checkMC_IPv6(m.get("group-ip"));
			if(typ.toShortString() == "MMRP" && InputValidator.checkMC_MMRP(m.get("group-ip")))
				groupmac = NetworkAdapter.macToByteArray(m.get("group-ip"));
			if(groupip == null && groupmac == null)
				help("wrong group ip/mac given");
		} else {
			help("no group ip/mac given");
		}
		
		/*
		 * Source IP herausfinden
		 */
		if(m.containsKey("source-ip")) {
			if(typ.toShortString() == "IPv4" && InputValidator.checkIPv4(m.get("source-ip")) != null)
				sourceip = InputValidator.checkIPv4(m.get("source-ip"));
			if(typ.toShortString() == "IPv6" && InputValidator.checkIPv6(m.get("source-ip")) != null)
				sourceip = InputValidator.checkIPv6(m.get("source-ip"));
			if(typ.toShortString() == "MMRP" && InputValidator.check_MMRP(m.get("source-ip")))
				sourcemac = NetworkAdapter.byteArrayToMac(NetworkAdapter.macToByteArray(m.get("source-ip")));
			if(sourceip == null && sourcemac == null)
				help("wrong source ip/mac given");
		} else {
			help("no source ip/mac given");
		}
		
		
		/*
		 * Port herausfinden
		 */
		try{
			if(m.containsKey("port")) {
				port = Integer.parseInt(m.get("port"));
				if(port < 1 || port > 65535) {
					help("port is out of range");
				}
			}
		}
		catch(NumberFormatException e) {
			help("format of port is not correct");
		}
			
		/*
		 * Packet Length herausfinden
		 */
		try{
			if(m.containsKey("length")) {
				if(typ.toShortString() == "IPv4") {
					packetLength = InputValidator.checkIPv4PacketLength(m.get("length"));
				}
				if(typ.toShortString() == "IPv6") {
					packetLength = InputValidator.checkIPv6PacketLength(m.get("length"));
				}
				if(typ.toShortString() == "MMRP") {
					packetLength = InputValidator.checkMmrpPacketLength(m.get("length"));
				}
				if(packetLength < 0) {
					help("packet length is out of rage");
				}
			}
		}
		catch(NumberFormatException e) {
			help("format of length is not correct");
		}
		
		/*
		 * TTL herausfinden
		 */
		try{
			if(m.containsKey("ttl")) {
				ttl = Integer.parseInt(m.get("ttl"));
				if(ttl < 1 || ttl > 256) {
					help("ttl is out of range");
				}
			}
		}
		catch(NumberFormatException e) {
			help("format of ttl is not correct");
		}
		
		/*
		 * packetRateDesired herausfinden
		 */
		try{
		if(m.containsKey("rate")) {
				packetRateDesired= Integer.parseInt(m.get("rate"));
				if(packetRateDesired < 1 || packetRateDesired > 65535) {
					help("package rate out of range");
				}
			}
		}
		catch(NumberFormatException e) {
			help("format of range is not correct");
		}

		/*
		 * Initialisieren von controller und logger
		 */
		MulticastController controller;
		MulticastLogHandler consoleHandler;
		Logger logger =  Logger.getLogger("program.controller.main");
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.FINEST);

		controller = new MulticastController(null,logger);

		consoleHandler = new MulticastLogHandler();
		consoleHandler.setLevel(Level.FINEST);
		logger.addHandler(consoleHandler);
		logger.config("Starting MultiCastor with parameter");
		logger.config("Date: " + new Date().toString());
		logger.config("User: " + System.getProperty("user.name"));
		try {
			logger.config("Host: " + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			logger.config("Host: unknown");
		}

		/*
		 * Generien des MulticastData Objektes und Übergabe an den Controller je nach gewähltem Typ
		 * 
		 */
		MulticastData md = new MulticastData();
		switch(typ) {
		case SENDER_V4:
			controller.addMC(new MulticastData(groupip, sourceip, port, packetLength, ttl, packetRateDesired, true, typ));
			break;
		case SENDER_V6:
			controller.addMC(new MulticastData(groupip, sourceip, port, packetLength, ttl, packetRateDesired, true, typ));
			break;
		case SENDER_MMRP:
			md.setGroupMac(groupmac);
			try {
				md.setDevice(NetworkAdapter.getMmrpAdapters().get(NetworkAdapter.findAddressIndex(typ, sourcemac)));
			}catch(Exception e)
			{
				help("source mac is not correct");
			}
			md.setPacketLength(packetLength);
			md.setTtl(-1);
			md.setActive(true);
			md.setTyp(typ);
			controller.addMC(md);
			break;
		case RECEIVER_V4:
			controller.addMC(new MulticastData(groupip, sourceip, port, true, typ));
			break;
		case RECEIVER_V6:
			controller.addMC(new MulticastData(groupip, sourceip, port, true, typ));
			break;
		case RECEIVER_MMRP:
			md.setGroupMac(groupmac);
			try {
				md.setDevice(NetworkAdapter.getMmrpAdapters().get(NetworkAdapter.findAddressIndex(typ, sourcemac)));
			}catch(Exception e)
			{
				help("source mac is not correct");
			}	
			md.setActive(true);
			md.setTyp(typ);
			controller.addMC(md);
			break;
		default:	help("wrong Parameter given");
		}
	}

	private static void startWithConfig(String pfad) {
		
		if(!NetworkAdapter.isPcapOk()) {
			help("no permissions for network device");
			System.exit(3);
		}
		
		MulticastController controller;
		MulticastLogHandler consoleHandler;
		Logger logger =  Logger.getLogger("program.controller.main");
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.FINEST);

		try {
			controller = new MulticastController(null,logger);
			//Handler für formatierte Ausgabe in der Konsole
			consoleHandler = new MulticastLogHandler();
			consoleHandler.setLevel(Level.FINEST);
			logger.addHandler(consoleHandler);
			logger.config("Starting MultiCastor with a streamconfig");
			logger.config("Date: " + new Date().toString());
			logger.config("User: " + System.getProperty("user.name"));
			try {
				logger.config("Host: " + InetAddress.getLocalHost().getHostName());
			} catch (UnknownHostException e) {
				logger.config("Host: unknown");
			}
			
			controller.loadConfigWithoutGUI(pfad);

		} catch (FileNotFoundException e) {
			help("Configuration file not found.");
			System.exit(2);
		} catch (SAXException e) {
			help("Configuration file in wrong format or corrupted. Please use a valid configuration file.");
			System.exit(2);
		} catch (IOException e) {
			help("IO Exeption.");
			System.exit(2);
		}
		for(Handler h:logger.getHandlers())	{
			h.close();
		}


	}

	/**
	 * Startet das Programm mit der graphischen Oberfläche
	 * 
	 * @param sender Boolean Wert, welcher darüber entscheidet, ob das Programm als Sender oder Receiver voreingestellt wird.
	 */

	private static void startWithGui(boolean sender) {
		

		//Initialisierung
		UserInputData uid;
		ViewController gui;
		XmlParserConfig parser; 
		MulticastController controller;
		MulticastLogHandler consoleHandlerWithGUI;
	
	
		//Erstellen und Einrichten vom Logger
		Logger logger =  Logger.getLogger("program.controller.main");
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.FINEST);
	
		//Wenn keine Parameter angegen werden, startet der Multicastor mit der GUI
		gui = new ViewController();
		controller = new MulticastController(gui,logger);
		uid = new UserInputData();
		parser = new XmlParserConfig();
		try {
			parser.loadConfig("MultiCastor.xml", uid);
		} catch (Exception e) {
			uid.setLanguage("en");
		}
		Messages.setLanguage(uid.getLanguage());

		
		gui.initialize(controller, uid);
	
		//Handler für formatierte Ausgabe in der Konsole- und GUI-Konsole
		consoleHandlerWithGUI = new MulticastLogHandler(gui);
		consoleHandlerWithGUI.setLevel(Level.FINEST);
		logger.addHandler(consoleHandlerWithGUI);
		logger.config("Starting MultiCastor with a GUI");
		logger.config("Date: " + new Date().toString());
		logger.config("User: " + System.getProperty("user.name"));
		try {
			logger.config("Host: " + InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			logger.info("Host: unknown");
		}
		controller.loadCompleteConfig();
		if(!sender){
			gui.pressBTReceiver();
		}
	
		for(Handler h:logger.getHandlers())	{
			h.close();
		}
	}
	
	/**
	 * Wertet die beim Programm Aufruf übergebene Parameter aus und sortiert diese in eine Hash-Map ein.
	 * Bei syntaktisch falschen Parametern wird die Hilfe-Funktion mit einer Fehlermeldung aufgerufen. 
	 * @param args Parameter die dem Programm beim Aufruf übergeben werden
	 * @return Eine HashMap aus zwei String, welche die Parameter zugeordnet zu den jeweiligen Einstellungen enthält
	 */

	private static Map<String,String> handleParameter(String[] args) {
		Map<String,String> r=new HashMap<String,String>();
		for (int i = 0; i < args.length; i++) {

			/*
			 * Gibt die Hilfe zu den Parametern aus
			 */
			if(args[i].equals("-h") || args[i].equals("--help")) {
				help("");
			}

			/*
			 * Liest den Pfad des Configfiles ein
			 */
			if(args[i].equals("-c")) {
				if(++i < args.length) {
					r.put("config", args[i]);
				}
				else {
					help("No config-file given.");
				}
				break;
			}

			if(args[i].contains("--config=")) {
				String[] params = args[i].split("0");
				if(params[1].isEmpty()) {
					help("no config-file given");
				}
				r.put("config", args[i]);
				break;
			}

			/*
			 * Anlegen eines Streams mit Parametern
			 */

			if(args[i].equals("-S")) {
		    	r.put("mode", "sender");
		    	continue;
			}

			if(args[i].equals("--sender")) {
		    	r.put("mode", "sender");
		    	continue;
			}

			if(args[i].equals("-R")) {
		    	r.put("mode", "receiver");
		    	continue;
			}

			if(args[i].equals("--receiver")) {
		    	r.put("mode", "receiver");
		    	continue;
			}

			if(args[i].equals("-4")) {
		    	r.put("netmode", "v4");
		    	continue;
			}

			if(args[i].equals("--ipv4")) {
		    	r.put("netmode", "v4");
		    	continue;
			}

			if(args[i].equals("-6")) {
		    	r.put("netmode", "v6");
		    	continue;
			}

			if(args[i].equals("--ipv6")) {
		    	r.put("netmode", "v6");
		    	continue;
			}

			if(args[i].equals("-m")) {
		    	r.put("netmode", "mmrp");
		    	continue;
			}

			if(args[i].equals("--mmrp")) {
		    	r.put("netmode", "mmrp");
		    	continue;
			}

			if(args[i].equals("-g")) {
				if(++i < args.length) {
					r.put("group-ip", args[i]);
				}
				else {
					help("no group ip/mac given");
				}
				continue;
			}

			if(args[i].contains("--group=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no group ip/mac given");
				}
				r.put("group-ip", params[1]);
				continue;
			}

			if(args[i].equals("-s")) {
				if(++i < args.length) {
					r.put("source-ip", args[i]);
				}
				else {
					help("no source ip/mac given");
				}
				continue;
			}

			if(args[i].contains("--source=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no source ip/mac given");
				}
				r.put("source-ip", params[1]);
				continue;
			}

			if(args[i].equals("-p")) {
				if(++i < args.length) {
					r.put("port", args[i]);
				}
				else {
					help("no port given");
				}
				continue;
			}

			if(args[i].contains("--port=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no port given");
				}
				r.put("port", params[1]);
				continue;
			}

            if(args[i].equals("-l")) {
				if(++i < args.length) {
					r.put("length", args[i]);
				}
				else {
					help("no length given");
				}
				continue;
			}

			if(args[i].contains("--length=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no length given");
				}
				r.put("length", params[1]);
				continue;
			}

            if(args[i].equals("-t")) {
				if(++i < args.length) {
					r.put("ttl", args[i]);
				}
				else {
					help("no ttl given");
				}
				continue;
			}

			if(args[i].contains("--ttl=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no ttl given");
				}
				r.put("ttl", params[1]);
				continue;
			}

            if(args[i].equals("-n")) {
				if(++i < args.length) {
					r.put("rate", args[i]);
				}
				else {
					help("no rate given");
				}
				continue;
			}

			if(args[i].contains("--rate=")) {
				String[] params = args[i].split("=");
				if(params[1].isEmpty()) {
					help("no rate given");
				}
				r.put("rate", params[1]);
				continue;
			}
			
			if(args[i].equals("--gui")) {
		    	r.put("gui", "true");
		    	continue;
			}

			/*
			 * Beenden, wenn ein falscher Parameter angegeben wird.
			 */
			help("unrecognized option '" + args[i] + "'");
		}

		return r;
	}

	/**
	 * Hilfe-Funktion gibt den Hilfetext und bei bedarf den jeweiligen Fehler aus
	 * @param error Fehlermeldung die der Hilfefunktion übergeben werden kann
	 */
	
	private static void help(String error) {
		int r = 0;
		/*
		 * Wenn ein unbekanter Parameter angegeben wird, wird ein Fehler in der ersten Zeile ausgegeben.
		 */
		if(!error.isEmpty()) {
			System.out.println("ERROR: " + error);
			r = 1;
		}
		/*
		 * Kopfzeilen ausgeben.
		 */
		System.out.println("Usage: multicastor [Options]...");
		System.out.println("Send and recive IPv4, IPv6 and MMRP multicastpackages.");
		System.out.println("");
		System.out.println("If you want to start multicastor with a graphical interface,\ndo not use a parameter or an option");
		System.out.println("");
		System.out.println("There are also two ways to start multicasor preconfigurated:");
		System.out.println("\t" + "With a stream-config-file");
		System.out.println("\t" + "With a options definded mutlicaststream");
		System.out.println("");
		/*
		 * Optionen ausgeben.
		 */
		System.out.println("Options:");
		String[][] params =
			{
				{"-h", "--help", "Print this help message"},
				{"-c", "--config", "Start program with a config-file"},
				{"-S", "--sender", "Start program as a sender"},
				{"-R", "--receiver", "Start program as a receiver"},
				{"-4", "--ipv4", "Set in IPv4 mode"},
				{"-6", "--ipv6", "Set in IPv6 mode"},
				{"-m", "--mmrp", "Set in MMRP mode"},
				{"-g", "--group", "Set group IP/MAC"},
				{"-s", "--source", "Set source IP/MAC"},
				{"-p", "--port", "Set (UDP)-port"},
				{"-l", "--length", "Set package length"},
				{"-t", "--ttl", "Set time to live"},
				{"-n", "--rate", "Set package rate desired"}
			};

		/*
		 * Maximum der ParameterLlänge ermitteln.
		 */
		int maxLengthParam = 0;
		for(String[] param : params)
			if(param[1].length() > maxLengthParam)
				maxLengthParam = param[1].length();

		/*
		 * Optionen formatiert ausgeben.
		 */
		for(String[] param : params) {
			System.out.print("   " + param[0] + ",   " + param[1] + "   ");
			for(int i = param[1].length(); i < maxLengthParam; i++)
				System.out.print(" ");
			System.out.println(param[2]);
		}

		/*
		 * Beispiele ausgeben.
		 */
		System.out.println("");
		System.out.println("Examples:");
		System.out.println("Start multicastor with a graphical interface:");
		System.out.println("\t" + "java -jar MultiCastor.jar");
		System.out.println("");
		System.out.println("Start multicastor with a stream-config-file:");
		System.out.println("\t" + "java -jar MultiCastor.jar --config=/path/to/stream-config-file.xml");
		System.out.println("");
		System.out.println("Start multicastor with a multicaststream devinded by parameters:");
		System.out.println("\t" + "java -jar MultiCastor.jar -S -ipv4 -g 224.0.0.1 -s 192.168.0.1 -p 4000 -l 100 -t 10 -n 20");
		/*
		 * Programm beenden
		 */
		
		System.exit(r);
	}
}