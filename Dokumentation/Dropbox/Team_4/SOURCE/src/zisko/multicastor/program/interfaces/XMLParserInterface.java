package zisko.multicastor.program.interfaces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import org.xml.sax.SAXException;
import zisko.multicastor.program.data.GUIData;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.model.WrongConfigurationException;

public interface XMLParserInterface {

	/**
	 * luedt die GUI Konfigurationen aus einer XML-Datei.
	 * 
	 * @param string
	 *            Der Ort, an dem die Datei liegt
	 * @param data
	 *            GUIData mit GUI Daten
	 * @throws IOException
	 */
	public void loadGUIConfig(String string, GUIData data) throws SAXException,
			FileNotFoundException, IOException, WrongConfigurationException;

	/**
	 * Liest eine XML-Konfigurationsdatei ein.
	 * 
	 * @param path
	 *            Ort, an dem die Konfigurationsdatei liegt
	 * @param v
	 *            Enthuelt nach dem Laden alle Multicast-Eintraege der XML-Datei
	 * @throws IOException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 */
	public void loadMultiCastConfig(String path, Vector<MulticastData> v)
			throws SAXException, FileNotFoundException, IOException,
			WrongConfigurationException;

	/**
	 * Speichert die getuetigten GUI Konfigurationen in einer XML-Datei ab.
	 * 
	 * @param p
	 *            Der Ort, an dem die Datei gespeichert werden soll
	 * @param data
	 *            GUIData mit GUI Daten
	 * @throws IOException
	 */
	public void saveGUIConfig(String p, GUIData data) throws IOException; // [FF]
																			// GUI
																			// Config
																			// Zeug

	/**
	 * Speichert die getuetigten Konfigurationen in einer XML-Datei ab.
	 * 
	 * @param path
	 *            Der Ort, an dem die Datei gespeichert werden soll
	 * @param v
	 *            Vektor aus Multicast Konfigurationseinstellungen
	 * @throws IOException
	 */
	public void saveMulticastConfig(String path, Vector<MulticastData> v)
			throws IOException;

}
