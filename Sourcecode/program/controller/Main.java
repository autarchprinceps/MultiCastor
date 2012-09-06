package zisko.multicastor.program.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

import zisko.multicastor.program.model.MulticastLogHandler;
import zisko.multicastor.program.model.WrongConfigurationException;

/**
 * Main-Methode.
 * @author Thomas Lüder
 */

public class Main {

	/**
	 * Initialisiert den MulticastController sowie die GUI und liest die 
	 * Parameter ein, die dem Programm übegeben wurden und startet den entsprechenden Programmteil.
	 * @param args Ein Feld aus Strings, das die Parameter enthält, die dem Programm in der Kommandozeile übergeben wurden.
	 */
	
	public static void main(String[] args) {
		//Initialisierung
		ViewController gui;
		MulticastController controller;
		MulticastLogHandler consoleHandlerWithGUI;
		MulticastLogHandler consoleHandler;
		
		//Erstellen und Einrichten vom Logger
		Logger logger =  Logger.getLogger("zisko.multicastor.program.controller.main");
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.FINEST);

		//Hauptprogrammteil - Parameter aus der Kommandozeile auswerten
		if(args.length==0) {
			gui = new ViewController();
			controller = new MulticastController(gui,logger);
			gui.initialize(controller);
			
			//Handler für formatierte Ausgabe in der Konsole- und GUI-Konsole
			consoleHandlerWithGUI = new MulticastLogHandler(gui);
			consoleHandlerWithGUI.setLevel(Level.FINEST);
			logger.addHandler(consoleHandlerWithGUI);
			logger.info("Starting MultiCastor with a gui");	
			
			controller.loadCompleteConfig();
			
			for(Handler h:logger.getHandlers())	{
				h.close();
			}
		}else 
			if(args[0].equals("-g"))	{
				//System.out.println("Parameter -g mitgegeben");
				if(args.length==1)	{
					System.out.println("Configuration file not specified.\n\nUsage:\tjava -jar multiCastor.jar [-h] [-g] configuration-file-path\n\t" +
							"type 'java -jar multiCastor.jar -h' for more information\n\n" +
							"To start the tool without a graphical user interface,\n" +
							"the configuration-file-path is required.");
				}else	{
					//File checkfile = new File(args[1]);
					//if(checkfile.exists())	{
					//xml_parser = new xmlParser();
					
					String pfad=args[1];
					try {
						controller = new MulticastController(null,logger);
						//System.out.println("Parsing");
						//Handler für formatierte Ausgabe in der Konsole
						consoleHandler = new MulticastLogHandler();
						consoleHandler.setLevel(Level.FINEST);
						logger.addHandler(consoleHandler);
						logger.info("Starting MultiCastor without a gui");
						
						controller.loadConfigWithoutGUI(pfad);
						
					} catch (FileNotFoundException e) {
						System.out.println("Configuration file not found.\n\nUsage:\tjava -jar multiCastor.jar [-h] [-g] configuration-file-path\n\t" +
								"type 'java -jar multiCastor.jar -h' for more information\n\n" +
								"To start the tool without a graphical user interface,\n" +
								"the configuration-file-path is required.");
						//e.printStackTrace();
						System.exit(0);
					} catch (SAXException e) {
						System.out.println("Configuration file in wrong format or corrupted. Please use a valid configuration file.\n\nUsage:\tjava -jar multiCastor.jar [-h] [-g] configuration-file-path\n\t" +
								"type 'java -jar multiCastor.jar -h' for more information\n\n" +
								"To start the tool without a graphical user interface,\n" +
								"the configuration-file-path is required.");
						//e.printStackTrace();
						System.exit(0);
					} catch (IOException e) {
						System.out.println("IOE");
						//e.printStackTrace();
						System.exit(0);
					} catch (WrongConfigurationException e) {
						System.out.println("Configuration file in wrong format or corrupted. Please use a valid configuration file.\n\nUsage:\tjava -jar multiCastor.jar [-h] [-g] configuration-file-path\n\t" +
								"type 'java -jar multiCastor.jar -h' for more information\n\n" +
								"To start the tool without a graphical user interface,\n" +
								"the configuration-file-path is required.");
						e.printStackTrace();
						System.exit(0);
					}
					for(Handler h:logger.getHandlers())	{
						h.close();
					}
				}
			}else 
				if(args[0].equals("-h")){
					//System.out.println("Parameter -h mitgegeben");
					System.out.println("Usage:\tjava -jar multiCastor.jar [-h] [-g] configuration-file-path\n\t" +
							"type 'java -jar multiCastor.jar -h' for more information\n\n" +
							"To start the tool without a graphical user interface,\n" +
							"the configuration-file-path is required.\n\n" +
							"For more information about a valid configuration file\n" +
							"consult the user manual.\n\n" +
							"Options:\n" +
							"\t\t-h\tdisplays this help-message\n" +
							"\t\t-g\tstart the MultiCastor-Tool without\n" +
							"\t\t\ta graphical user interface");	
				}else 
					if(args.length!=0){
						File checkfile = new File(args[0]);
						if(checkfile.exists())	{
							gui = new ViewController();
							controller = new MulticastController(gui,logger);
							gui.initialize(controller);
							
							//Handler für formatierte Ausgabe in der Konsole- und GUI-Konsole
							consoleHandlerWithGUI = new MulticastLogHandler(gui);
							consoleHandlerWithGUI.setLevel(Level.FINEST);
							logger.addHandler(consoleHandlerWithGUI);
							logger.info("Starting MultiCastor with a gui and file");	
							
							controller.loadConfigFile(args[0], true, true, true, true);
							
							for(Handler h:logger.getHandlers())	{
								h.close();
							}
						}else	{
							//System.out.println("Falscher Parameter mitgegeben");
							System.out.println("Not a valid parameter.\n\nUsage:\tjava -jar multiCastor.jar [-h] [-g] configuration-file-path\n\t" +
									"type 'java -jar multiCastor.jar -h' for more information");
						}
					}
	} 
}
