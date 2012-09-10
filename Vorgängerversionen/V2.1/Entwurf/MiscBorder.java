   
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
 
  


import java.awt.Color;
import java.awt.Font;
import java.util.Vector;

import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
/**
 * Hilfsklasse zum verwalten der Ränder für Textfelder.
 * @author Daniel Becker 
 *
 */
@SuppressWarnings("serial")
public class MiscBorder extends TitledBorder {
	/**
	 * Enum welches bestimmt ob der Rahmen rot, grün oder neutral gezeichnet werden muss, 
	 * je nach dem ob ein Korrekter Input vorliegt.
	 * @author Daniel Becker
	 *
	 */
	public enum BorderType{
		NEUTRAL, TRUE, FALSE
	}
	/**
	 * Enum welches bestimmt um welchen Rahmen es sich handelt
	 * @author Daniel Becker
	 *
	 */
	public enum BorderTitle{
		IPv4GROUP, IPv4SOURCE, IPv6GROUP, IPv6SOURCE, PORT, RATE, LENGTH, TTL 
	}
	private static String[] ipv4Names = {"IPv4 Group Address","IPv4 Network Interface","UDP Port","Packet Rate", "Packet Length", "Time to Live", "IPv6 Group Address","IPv6 Network Interface" };
	private static Vector<TitledBorder> b_neutral = new Vector<TitledBorder>();
	private static Vector<TitledBorder> b_true = new Vector<TitledBorder>();
	private static Vector<TitledBorder> b_false = new Vector<TitledBorder>();
	private static Color enabledColor=new Color(0,175,0);
	private static Color disabledColor=new Color(200,0,0);
	private static Color neutralLineColor=  new Color(240,240,240);
	private static Color neutralTextColor= Color.gray;
	private static Color lineColor=Color.lightGray;
	static Font f = MiscFont.getFont(0,12);
	private Color c = Color.black;
	/**
	 * initialisiert die 3 Vectoren welche die fertigen Ränder halten, so müssen zur Laufzeit keine neuen genertiert werden.
	 * Die können abgefragt werden sobald sie benötigt werden.
	 */
	static {
		for(int i = 0; i < ipv4Names.length; i++){
			b_neutral.add(createBorder(ipv4Names[i], neutralTextColor, neutralLineColor));
			b_true.add(createBorder(ipv4Names[i], enabledColor, enabledColor));
			b_false.add(createBorder(ipv4Names[i], disabledColor, disabledColor));			
		}
	}
	/**
	 * Kontruktor welcher eine LineBorder mit einem Bestimmten Namen erstellt.
	 * @param title Der Name der neuen LineBorder.
	 */
	public MiscBorder(String title) {
		super(title);
		setBorder(new LineBorder(lineColor));
		titleFont = f;
		titleColor = c;
	}
	
	/**
	 * Statische Funktion zum erstellen von Lineborders.
	 * @param title Name der LineBorder.
	 * @param titleColor ZeichenFarbe der LineBorder.
	 * @param lineColor LinienFarbe der LineBorder.
	 * @return Die LineBorder mit dem bestimmten Namen.
	 */
	private static TitledBorder createBorder(String title, Color titleColor, Color lineColor){
		TitledBorder b = new TitledBorder(new LineBorder(lineColor), title, TitledBorder.LEFT, TitledBorder.TOP, f, titleColor);
		return b;
	}
	/**
	 * Statische Funktion zum Abrufen einer LineBorder
	 * @param title Name der LineBorder (Titel)
	 * @param bordertype Farbe der Border
	 * @return die erstellte LineBorder
	 */
	public static TitledBorder getBorder(BorderTitle title, BorderType bordertype){
		TitledBorder ret = null;
		switch(bordertype){
			case NEUTRAL:
				switch(title){
					case IPv4GROUP: ret = b_neutral.get(0);  break;
					case IPv4SOURCE: ret = b_neutral.get(1);  break;
					case PORT: ret = b_neutral.get(2);  break;
					case RATE: ret = b_neutral.get(3);  break;
					case LENGTH: ret = b_neutral.get(4);  break;
					case TTL: ret = b_neutral.get(5);  break;
					case IPv6GROUP: ret = b_neutral.get(6); break;
					case IPv6SOURCE: ret = b_neutral.get(7); break;
				}
				break;
			case TRUE:
				switch(title){
					case IPv4GROUP: ret = b_true.get(0);  break;
					case IPv4SOURCE: ret = b_true.get(1);  break;
					case PORT: ret = b_true.get(2);  break;
					case RATE: ret = b_true.get(3);  break;
					case LENGTH: ret = b_true.get(4);  break;
					case TTL: ret = b_true.get(5);  break;
					case IPv6GROUP: ret = b_true.get(6); break;
					case IPv6SOURCE: ret = b_true.get(7); break;
			}
			break;
			case FALSE:
				switch(title){
					case IPv4GROUP: ret = b_false.get(0);  break;
					case IPv4SOURCE: ret = b_false.get(1);  break;
					case PORT: ret = b_false.get(2);  break;
					case RATE: ret = b_false.get(3);  break;
					case LENGTH: ret = b_false.get(4);  break;
					case TTL: ret = b_false.get(5);  break;
					case IPv6GROUP: ret = b_false.get(6); break;
					case IPv6SOURCE: ret = b_false.get(7); break;
			}
		}
		return ret;
	}
	
}
