   
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
 
 package dhbw.multicastor.program.controller;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;


/**
 * Main-Methode.
 * @author Thomas Lï¿½der
 */

public class Main {

	public static Thread hook;
	/**
	 * Initialisiert den MulticastController sowie die GUI und liest die 
	 * Parameter ein, die dem Programm ï¿½begeben wurden und startet den entsprechenden Programmteil.
	 * @param args Ein Feld aus Strings, das die Parameter enthï¿½lt, die dem Programm in der Kommandozeile ï¿½bergeben wurden.
	 */
	
	public static void main(String[] args) {
		try {
			File libFolder = new File("libs"+ File.separator +"jnetpcap-1.3.0");
			addLibraryPath(libFolder.getAbsolutePath());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MulticastController mcCtrl = new MulticastController();
		hook = new Thread(new ShutdownHook(mcCtrl.getLogger(), mcCtrl));
		// Registrierung
		Runtime.getRuntime().addShutdownHook(hook);
		if(args.length==0){
			mcCtrl.registerUIController(new ViewController());
		}else {
			mcCtrl.registerUIController(new CLIController(args));
		}
	}
	
	/*
	 * Method to add the jnetpcap library at runtime
	 * Found at: http://fahdshariff.blogspot.de/2011/08/changing-java-library-path-at-runtime.html
	 */
	public static void addLibraryPath(String pathToAdd) throws Exception{
		String[] mainCommand = System.getProperty("sun.java.command").split(" ");
		// program main is a jar
		if (!mainCommand[0].endsWith(".jar")) {
			return;
		}
		
	    final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
	    usrPathsField.setAccessible(true);
	 
	    //get array of paths
	    final String[] paths = (String[])usrPathsField.get(null);
	 
	    //check if the path to add is already present
	    for(String path : paths) {
	        if(path.equals(pathToAdd)) {
	            return;
	        }
	    }
	 
	    //add the new path
	    final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
	    newPaths[newPaths.length-1] = pathToAdd;
	    usrPathsField.set(null, newPaths);
	}
}
