   
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
 
  
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

/**
 * Abstrakte Hilfsklasse zum Laden von Schriftarten
 * @author Daniel Becker
 *
 */
@SuppressWarnings("unused")
public abstract class MiscFont{
	private static String font_type = "";
	private static float font_size = 12;
	private static int font_style = 1;
	private static Font mc_font = null;
	//private static URL path = MiscFont.class.getResource("/dhbw/multicastor/resources/fonts/calibri.ttf");
	/**
	 * Statischer Codeblock zum laden der Schriftart
	 */
	static{
		try {
			mc_font = Font.createFont(Font.PLAIN, MiscFont.class.getResourceAsStream("/dhbw/multicastor/resources/fonts/DejaVuSans.ttf"));
		} catch (FontFormatException e) {
			System.out.println("ERROR: Font format exception! Program Exit!");
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			System.out.println("ERROR: Font File not found! Program Exit!");
			e.printStackTrace();
			System.exit(0);
		}
	}
	/**
	 * Statische Funktion zum Anfordern der Standardschriftart mit Standardformatierung.
	 * @return die angeforderte Standardschrift.
	 */
	public static Font getFont(){
		font_size=11;
		font_style=0;
		return createFont();
	}
	/**
	 * Statische Funktion zum Anfordern der Standardschriftart betimmter Formatierung.
	 * @param style 0=normal, 1=bold, 2=italic, 3=bolditalic
	 * @param size die gewählte Schriftgröße
	 * @return die angeforderte Standardschrift.
	 */
	public static Font getFont(int style, float size){
		font_size=size;
		font_style=style;
		//System.out.println(type);
		return createFont();
	}
	private static Font createFont(){
		//System.out.println("size: "+font_size);
		//System.out.println("style: "+font_style);
		mc_font=mc_font.deriveFont(font_size);
		mc_font=mc_font.deriveFont(font_style);
		return mc_font;
	}
}
