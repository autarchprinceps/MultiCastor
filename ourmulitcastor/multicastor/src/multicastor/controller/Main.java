package multicastor.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import multicastor.lang.LanguageManager;
import multicastor.model.MulticastLogHandler;
import multicastor.model.NetworkAdapter;
import multicastor.model.WrongConfigurationException;

import org.xml.sax.SAXException;

/**
 * Main Class of MultiCastor. here all the stuff begins.
 */
public class Main {

	/**
	 * Minimum of available heap needed to start MultiCastor. Will be proofed at
	 * program start.
	 */
	public static final long MIN_MAX_HEAP = 32 * 1024 * 1024;

	/**
	 * Maximum of available heap in the current environment
	 */
	public static long REAL_MAX_HEAP;

	/**
	 * LanguageManager instance provides access to language files
	 */
	private static LanguageManager lang = LanguageManager.getInstance();

	/**
	 * Initialisiert den MulticastController sowie die GUI und liest die
	 * Parameter ein, die dem Programm uebergeben wurden und startet den
	 * entsprechenden Programmteil.
	 * 
	 * @param args
	 *            Ein Feld aus Strings, das die Parameter enthaelt, die dem
	 *            Programm in der Kommandozeile uebergeben wurden.
	 */
	public static void main(final String[] args) {

		Main.REAL_MAX_HEAP = Runtime.getRuntime().maxMemory();

		// Initialisierung
		ViewController gui;
		MulticastController controller;
		MulticastLogHandler consoleHandlerWithGUI;
		MulticastLogHandler consoleHandler;

		// Erstellen und Einrichten vom Logger
		final Logger logger = Logger.getLogger("multicastor.controller.main");
		logger.setUseParentHandlers(false);
		logger.setLevel(Level.FINEST);

		if (Main.REAL_MAX_HEAP < Main.MIN_MAX_HEAP) {

			consoleHandler = new MulticastLogHandler();
			consoleHandler.setLevel(Level.FINEST);
			logger.addHandler(consoleHandler);

			logger.severe(lang.getProperty("error.memory.part1") + " "
					+ (Main.REAL_MAX_HEAP / (1024 * 1024)) + " MB. "
					+ lang.getProperty("error.memory.part2") + " "
					+ (Main.MIN_MAX_HEAP / (1024 * 1024)) + " MB "
					+ lang.getProperty("error.memory.part3"));

			if ((args.length == 0) || !args[0].equals("-g")) {
				JOptionPane.showMessageDialog(
						new JFrame(),
						lang.getProperty("error.memory.part1") + " "
								+ (Main.REAL_MAX_HEAP / (1024 * 1024))
								+ " MB. "
								+ lang.getProperty("error.memory.part2") + " "
								+ (Main.MIN_MAX_HEAP / (1024 * 1024)) + " MB "
								+ lang.getProperty("error.memory.part3"),
						lang.getProperty("error.memory.title"),
						JOptionPane.WARNING_MESSAGE);
			}
			logSystemInformation(logger);
			System.exit(1);
		} else if (args.length == 0) {
			gui = new ViewController();
			controller = new MulticastController(gui, logger);
			gui.initialize(controller);

			// Handler fuer formatierte Ausgabe in der Konsole- und GUI-Konsole
			consoleHandlerWithGUI = new MulticastLogHandler(gui);
			consoleHandlerWithGUI.setLevel(Level.FINEST);
			logger.addHandler(consoleHandlerWithGUI);
			logSystemInformation(logger);
			logger.info(lang.getProperty("logger.info.startWithGui"));

			// Fehler für fehlende JNetPcap
			if (NetworkAdapter.hasJpcapMissing) {
				logger.warning(lang.getProperty("message.pcapDeviceFail"));
			}

			controller.loadDefaultMulticastConfig();
			controller.loadGUIConfig("", true);

			for (final Handler h : logger.getHandlers()) {
				h.close();
			}
		} else if (args[0].equals("-g")) {
			// System.out.println("Parameter -g mitgegeben");
			if (args.length == 1) {
				System.out.println(lang
						.getProperty("error.config.notSpecified"));
			} else {
				// File checkfile = new File(args[1]);
				// if(checkfile.exists()) {
				// xml_parser = new xmlParser();

				final String pfad = args[1];
				try {
					controller = new MulticastController(null, logger, 4000);
					// System.out.println("Parsing");
					// Handler fuer formatierte Ausgabe in der Konsole
					consoleHandler = new MulticastLogHandler();
					consoleHandler.setLevel(Level.FINEST);
					logger.addHandler(consoleHandler);
					logSystemInformation(logger);
					logger.info(lang.getProperty("logger.info.startNoGui"));

					// Fehler für fehlende JNetPcap
					if (NetworkAdapter.hasJpcapMissing) {
						logger.info(lang.getProperty("message.pcapDeviceFail"));
					}

					controller.loadConfigWithoutGUI(pfad);

				} catch (final FileNotFoundException e) {
					System.out.println(lang
							.getProperty("error.config.notSpecified"));
					// e.printStackTrace();
					System.exit(0);
				} catch (final SAXException e) {
					System.out.println(lang
							.getProperty("error.config.wrongFormat"));
					// e.printStackTrace();
					System.exit(0);
				} catch (final IOException e) {
					System.out.println("IOE");
					// e.printStackTrace();
					System.exit(0);
				} catch (final WrongConfigurationException e) {
					System.out.println(lang
							.getProperty("error.config.wrongFormat"));
					e.printStackTrace();
					System.exit(0);
				}
				for (final Handler h : logger.getHandlers()) {
					h.close();
				}
			}
		} else if (args[0].equals("-h")) {
			// System.out.println("Parameter -h mitgegeben");
			System.out.println(lang.getProperty("console.helptext"));
		} else if (args.length != 0) {
			final File checkfile = new File(args[0]);
			if (checkfile.exists()) {
				gui = new ViewController();
				controller = new MulticastController(gui, logger);
				gui.initialize(controller);

				// Handler fuer formatierte Ausgabe in der Konsole- und
				// GUI-Konsole
				consoleHandlerWithGUI = new MulticastLogHandler(gui);
				consoleHandlerWithGUI.setLevel(Level.FINEST);
				logger.addHandler(consoleHandlerWithGUI);
				logSystemInformation(logger);
				logger.info(lang.getProperty("logger.info.startGuiFile"));

				controller.loadMulticastConfig(args[0], true);
				controller.loadGUIConfig("", true);

				for (final Handler h : logger.getHandlers()) {
					h.close();
				}
			} else {
				// System.out.println("Falscher Parameter mitgegeben");
				System.out.println(lang.getProperty("error.invalidParameter"));
			}
		}
	}

	/**
	 * Method will log some basic information about the system
	 * 
	 * @param logger
	 *            the current logger
	 */
	private static void logSystemInformation(final Logger logger) {
		// Systeminformationen loggen
		try {
			logger.info("Hostname: "
					+ java.net.InetAddress.getLocalHost().getHostName());
			logger.info("Hostadress: "
					+ java.net.InetAddress.getLocalHost().getHostAddress());
		} catch (final UnknownHostException e1) {
			logger.info("Unable to get hostname and hostadress");
		}
		logger.info("Username: " + System.getProperty("user.name")
				+ "; User location: " + System.getProperty("user.region")
				+ " - " + System.getProperty("user.timezone"));
		logger.info("Java version: " + System.getProperty("java.version")
				+ "; Java vendor: " + System.getProperty("java.vendor"));
		logger.info("File encoding: " + System.getProperty("file.encoding"));
	}
}
