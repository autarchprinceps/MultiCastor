package multicastor.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import multicastor.controller.ViewController;
import multicastor.lang.LanguageManager;


/**
 * Selbstdefinierter Handler, der die zu loggenden Nachrichten formatiert und
 * ausgibt.
 */

public class MulticastLogHandler extends ConsoleHandler {
	/**
	 * Language Manager ist wichtig fuer die multi Language Unterstuetzung
	 */
	private final LanguageManager lang = LanguageManager.getInstance();

	/**
	 * ViewController Referenz
	 */
	private ViewController viewController;

	/**
	 * Normaler Konstruktor.
	 */
	public MulticastLogHandler() {
		super();
	}

	/**
	 * ueberladener Konstruktor. Hier mit uebergabe vom GUI-Controller.
	 * 
	 * @param gui
	 *            Die Referenz zum GUI-Controller
	 */

	public MulticastLogHandler(final ViewController gui) {
		super();
		viewController = gui;
	}

	/**
	 * Zu loggende Nachrichten formatieren und mit Level- und Zeitstempel
	 * ausgeben. Die Ausgabe erfolgt auf system.out, in die GUI-Konsole und in
	 * die Datei log.txt.
	 * 
	 * @param record
	 *            Die zu loggende unformatierte Nachricht.
	 */
	@Override
	public void publish(final LogRecord record) {
		// Formatiere das Datum und die Ausgabe in die Konsole und in Datei
		final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		final String date = sdf.format(new Date(record.getMillis()));

		final SimpleDateFormat sdf_file = new SimpleDateFormat(
				"dd.MM.yyyy-HH:mm:ss");
		final String date_file = sdf_file.format(new Date(record.getMillis()));

		// Ausgabe mit Level- und Datuminformationen
		final Level level = record.getLevel();
		final String message = ("[" + date + "]" + "[" + level + "] " + record
				.getMessage());
		final String message_file = ("[" + date_file + "]" + "[" + level + "] " + record
				.getMessage());
		final String message_window = (record.getMessage());

		System.out.println(message);

		// Zeigt Info-Fenster an, wenn Fehler oder Warnungen auftreten
		// Nur wenn GUI gestartet ist
		if(viewController != null) {
			if(level.equals(Level.SEVERE) || level.equals(Level.WARNING)) {
				viewController.showMessage(ViewController.MessageTyp.ERROR,
						message_window);
			}
			if(viewController.isInitFinished()) {
				viewController.printConsole(message);
			}
		}

		// Ausgabe in Datei log.txt
		try {
			final BufferedWriter os = new BufferedWriter(new FileWriter(
					"log.txt", true));
			os.write(message_file + "\r\n");
			os.close();
		} catch(final IOException e) {
			System.out.println(lang.getProperty("error.logfile.canNotWrite"));
			e.printStackTrace();
		}
	}
}
