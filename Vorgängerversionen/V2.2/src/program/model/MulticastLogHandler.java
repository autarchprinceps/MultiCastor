package program.model;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import program.controller.ViewController;

/**
 * Selbstdefinierter Handler, der die zu loggenden Nachrichten formatiert und ausgibt.
 * @author Thomas L�der
 */

public class MulticastLogHandler extends ConsoleHandler	{
	private ViewController viewController;

	/**
	 * Normaler Konstruktor.
	 */

	public MulticastLogHandler()	{
		super();
		}

	/**
	 * �berladener Konstruktor. Hier mit �bergabe vom GUI-Controller.
	 * @param gui Die Referenz zum GUI-Controller
	 */

	public MulticastLogHandler(ViewController gui)	{
		super();
		viewController=gui;
		}

	/**
	 * Zu loggende Nachrichten formatieren und mit Level- und Zeitstempel ausgeben.
	 * Die Ausgabe erfolgt auf system.out, in die GUI-Konsole und in die Datei log.txt.
	 * @param record Die zu loggende unformatierte Nachricht.
	 */
	@Override
	public void publish(LogRecord record) {
		//Formatiere das Datum und die Ausgabe in die Konsole und in Datei
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$
		String date = sdf.format(new Date(record.getMillis()));

		SimpleDateFormat sdf_file = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss"); //$NON-NLS-1$
		String date_file = sdf_file.format(new Date(record.getMillis()));

		//Ausgabe mit Level- und Datuminformationen
		Level level = record.getLevel();

		//String message=("["+level+"]" + "["+date+"] " + record.getMessage());
		//String message=(level + ": " + record.getMessage() + "\t(" + date + ")");
		String message=(level + "(" + date + "): " + record.getMessage());
		//String message_file=("["+level+"]" + "["+date_file+"] " +record.getMessage());
		//String message_file=(level + ": " + record.getMessage() + "\t(" + date_file + ")");
		String message_file=(level + "(" + date_file + "): " + record.getMessage());
		String message_window=(record.getMessage());

		System.out.println(message);

		//Zeigt Info-Fenster an, wenn Fehler oder Warnungen auftreten
		//Nur wenn GUI gestartet ist
		if(viewController!=null)	{
			if(level.equals(Level.SEVERE) || level.equals(Level.WARNING))	{
			viewController.showMessage(ViewController.MessageTyp.ERROR, message_window);
			}
			if(viewController.isInitFinished())		{
				viewController.printConsole(message);
				}
		}

		//Ausgabe in Datei log.txt
		try {
			BufferedWriter os = new BufferedWriter(new FileWriter("log.txt",true)); //$NON-NLS-1$
			os.write(message_file + "\n"); //$NON-NLS-1$
		    os.close();
		} catch (IOException e) {
			System.out.println("Log file could not be written to disk. Please check if you have writing permission in the MultiCastor directory."); //$NON-NLS-1$
			//System.out.println("Logdatei konnte nicht erstellt werden. Bitte prüfen sie, ob Sie im MultiCastor-Verzeichnis Schreibberechtigung besitzen.");
			e.printStackTrace();
		}
	}
}
