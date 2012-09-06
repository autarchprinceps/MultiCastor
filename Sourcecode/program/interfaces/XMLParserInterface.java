package zisko.multicastor.program.interfaces;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.xml.sax.SAXException;

import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.UserInputData;
import zisko.multicastor.program.data.UserlevelData;
import zisko.multicastor.program.model.WrongConfigurationException;


public interface XMLParserInterface
{

	/** Liest eine XML-Konfigurationsdatei ein.
	 * @param pfad
	 * Ort, an dem die Konfigurationsdatei liegt
	 * @param v1
	 * MultiCastData enth�lt Multicast Konfigurationseinstellungen
	 * @param v2
	 * UserLevelData enth�lt pers�nliche Einstellungen des Users, zB. welche GUI Elemente angezeigt werden sollen
	 * @param v3
	 * UserInputData enth�lt den momentanen Stand der GUI, inklusive ge�ffnetem Fenster und gerade eingetragenen Werten
	 * @param v4
	 * Die zuletzt ge�ffneten Konfigurationsdateien
	 * @throws IOException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 */
	public void loadConfig(String pfad, Vector<MulticastData> v1, Vector<UserlevelData> v2, Vector<UserInputData> v3, Vector<String> v4) throws SAXException, FileNotFoundException, IOException, WrongConfigurationException;
	
	/** Auslesen einer XML-Konfigurationsdatei f�r den User und aus der Kommandozeile
	 * @param pfad
	 * Ort, an dem die Konfigurationsdatei liegt
	 * @param v1
	 * MultiCastData enth�lt Multicast Konfigurationseinstellungen
	 * @param v2
	 * UserLevelData enth�lt pers�nliche Einstellungen des Users, zB. welche GUI Elemente angezeigt werden sollen
	 * @throws SAXException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadConfig( String pfad, Vector<MulticastData> v1, Vector<UserlevelData> v2 ) throws SAXException, FileNotFoundException, IOException, WrongConfigurationException;
	
	/**Liest die Default ULD Konfiguration und Standartwerte aus dem JAR-File
	 * @param v1
	 * Vektor aus Multicast Konfigurationseinstellungen
	 * @param v2
	 * Vektor aus GUI Konfigurationseinstellungen
	 * @throws IOException
	 * @throws SAXException
	 */
	public void loadDefaultULD(Vector<MulticastData> v1, Vector<UserlevelData> v2) throws IOException, SAXException, WrongConfigurationException;
	
	/** Speichert die get�tigten Konfigurationen in einer XML-Datei ab.
	 * @param pfad
	 * Der Ort, an dem die Datei gespeichert werden soll
	 * @param v1
	 * Vektor aus Multicast Konfigurationseinstellungen
	 * @param v2
	 * Vektor aus GUI Konfigurationseinstellungen
	 * @param v3
	 * Vektor aus UserInputData Objekten, enthalten den momentanen Stand der GUI.
	 * @param v4
	 * Vektor aus Pfaden zu zuletzt benutzten Konfigurationsdateien
	 * @throws IOException
	 */
	public void saveConfig(String pfad, Vector<MulticastData> v1, Vector<UserlevelData> v2, Vector<UserInputData> v3, Vector<String> v4) throws IOException;

	/** Speichert die get�tigten Konfigurationen in einer XML-Datei ab.
	 *  F�r Kommandozeile und manuelle Speicherung einer Konfigurationsdatei durch den Benutzer.
	 * @param pfad
	 * Der Ort, an dem die Datei gespeichert werden soll
	 * @param v1
	 * Vektor aus Multicast Konfigurationseinstellungen
	 * @param v2
	 * Vektor aus GUI Konfigurationseinstellungen
	 * @throws IOException
	 */
	public void saveConfig(String pfad, Vector<MulticastData> v1, Vector<UserlevelData> v2) throws IOException;

}
