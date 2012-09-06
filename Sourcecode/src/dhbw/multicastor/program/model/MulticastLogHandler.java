   
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
 
 package dhbw.multicastor.program.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import dhbw.multicastor.program.controller.ViewController;


/**
 * Selbstdefinierter Handler, der die zu loggenden Nachrichten formatiert und
 * ausgibt.
 * 
 * @author Thomas Lüder
 */

public class MulticastLogHandler extends ConsoleHandler {
	private ViewController viewController;
	
	public enum Event{
		ADDED, ACTIVATED, DEACTIVATED, DELETED, ERROR, INFO, WARNING, JOINED, LEAVE, MMRP, CLI
	}

	/**
	 * Normaler Konstruktor.
	 */

	public MulticastLogHandler() {
		super();
	}

	/**
	 * Überladener Konstruktor. Hier mit Übergabe vom GUI-Controller.
	 * 
	 * @param gui
	 *            Die Referenz zum GUI-Controller
	 */

	public MulticastLogHandler(ViewController gui) {
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
	public void publish(LogRecord record) {
		// Formatiere das Datum und die Ausgabe in die Konsole und in Datei
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String date = sdf.format(new Date(record.getMillis()));

		SimpleDateFormat sdf_file = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
		String date_file = sdf_file.format(new Date(record.getMillis()));

		// Ausgabe mit Level- und Datuminformationen
		Level level = record.getLevel();
		Event event = Event.INFO;
		if(record.getParameters() != null){
			event = (Event) record.getParameters()[0];
		}
		
		String message = ("[" + event + "]" + "[" + date + "] " + record
				.getMessage());
		String message_file = ("[" + event + "]" + "[" + date_file + "] \t" + record
				.getMessage());
		String message_window = (record.getMessage());


		System.out.println(message);

		// Zeigt Info-Fenster an, wenn Fehler oder Warnungen auftreten
		// Nur wenn GUI gestartet ist
		if (viewController != null) {
			if (level.equals(Level.SEVERE) || level.equals(Level.WARNING)) {
//				viewController.showMessage(ViewController.MessageTyp.ERROR,
//						message_window);
			}
			if (viewController.isInitFinished()) {
				viewController.printConsole(message);
			}
		}
		// Ausgabe in Datei log.txt
		try {
			BufferedWriter os = new BufferedWriter(new FileWriter("log.txt",
					true));
			os.write(message_file);
			os.newLine();
			os.flush();
			os.close();
		} catch (IOException e) {
			System.out
					.println("Log file could not be written to disk. Please check if you have writing permission in the MultiCastor directory.");
			// System.out.println("Logdatei konnte nicht erstellt werden. Bitte prÃ¼fen sie, ob Sie im MultiCastor-Verzeichnis Schreibberechtigung besitzen.");
			e.printStackTrace();
		}
	}
}
