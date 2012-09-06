   
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
 
 package dhbw.multicastor.program.view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class FrameAbout extends JFrame {
	private static final ResourceBundle messages = ResourceBundle.getBundle("dhbw.multicastor.resources.i18n.messages"); //$NON-NLS-1$

	 private String nl = "\n";
     private String t = "\t";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1114384277062786065L;
	
	static SimpleAttributeSet H1 = new SimpleAttributeSet();
	static SimpleAttributeSet H2 = new SimpleAttributeSet();
	static SimpleAttributeSet DEFAULT = new SimpleAttributeSet();
	
	static {
		
		// Ueberschrift 1
    		StyleConstants.setFontFamily(H1, "Helvetica");
    		StyleConstants.setFontSize(H1, 16);
    		StyleConstants.setBold(H1, true);
    		
    	// Ueberschrift 2
    		StyleConstants.setFontFamily(H2, "Helvetica");
    		StyleConstants.setFontSize(H2, 14);
    		StyleConstants.setBold(H2, true);

    	// Default
    		StyleConstants.setForeground(DEFAULT, Color.black);
    		StyleConstants.setFontFamily(DEFAULT, "Helvetica");
    		StyleConstants.setFontSize(DEFAULT, 12);
    		
	}
	
	public FrameAbout() {
		initWindow();
		initTabs();
	}
	
	private void initWindow(){
		setSize(new Dimension(600, 600));
		setMinimumSize(new Dimension(200, 200));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle(messages.getString("FrameAbout.this.title")); //$NON-NLS-1$
		ImageIcon icon = new ImageIcon(getClass().getResource(
				"/dhbw/multicastor/resources/images/icon.png"));
		setIconImage(icon.getImage());
	}
	
	private void initTabs(){
	
		JPanel pan_multi = initMulti();
		JScrollPane pan_authors = initAuthors();
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("MultiCastor", pan_multi);
		tabs.addTab("Authors", pan_authors);
		getContentPane().add(tabs);
	}
	
	private JPanel initMulti(){
		JPanel pan = new JPanel();
		pan.setLayout(new BorderLayout());
		JLabel lb_logo = new JLabel();
		lb_logo.setIcon(new ImageIcon(getClass().getResource("/dhbw/multicastor/resources/images/mc2.png")));	
		
		JTextPane textPane_about = new JTextPane();
		textPane_about.setMargin(new Insets(20, 5, 20, 5));
		textPane_about.setEditable(false);
		
		appendText("MultiCastor ermöglicht den Test der Multicast-Funktionalitäten von " +
				"geswitchten und gerouteten Ethernet-Netzwerken. Dabei wird die " + nl +
				"Endgerätefunktion (Sender und Empfänger) simuliert. In der Version 2.0 " + nl +
				"werden folgende Protokolle zur Multicast-Pfadregistrierung unterstützt: " + nl +
				"IPv4 - IGMP " + nl +
				"IPv6 ? MLD " + nl +
				"Ethernet ? MMRP " + nl +
				"Der MultiCastor ist damit das weltweit erste verfügbare Tool, dass MMRP " + nl +
				"nach IEEE 802.1ak unterstützt. " + nl +
				"MultiCastor ist ein OpenSource Projekt, das unter der GPLv3 Lizenz " + nl +
				"veröffentlicht wurde. Die Software kann auf der Projekthomepage " + nl +
				"heruntergeladen (http://sourceforge.net/projects/multicastor/) und " + nl +
				"kostenlos verwendet werden.", DEFAULT, textPane_about);
		
		pan.add(lb_logo, BorderLayout.WEST);
		pan.add(textPane_about, BorderLayout.CENTER);
		
		return pan;
	}
	
	private JScrollPane initAuthors(){
       
		JTextPane textPane_authors = new JTextPane();
		textPane_authors.setMargin(new Insets(20, 20, 20, 20));
		textPane_authors.setEditable(false);
		
		appendText("Contributors:"  + nl + nl, H1, textPane_authors);
		
		appendText("MultiCastor ist ein Tool zum Senden und Empfangen von " +
					"Multicast-Datenströmen. Es wurde als Projekt im Fach \"Software " +
					"Engineering\" an der Dualen Hochschule Stuttgart unter Leitung der Dozenten " +
					"Markus Rentschler und Andreas Stuckert von unten genannten Studenten " +
					"erstellt:" + nl + nl, DEFAULT, textPane_authors);
		
		appendText("MultiCastor 1.0"  + nl +
					"Kurs TIT09AIB" + nl + nl, H2, textPane_authors);
		
		appendText("Becker, Daniel" + nl +
					"Beutel, Johannes" + nl +
					"Gerz, Daniela" + nl +
					"Lüder, Thomas" + nl +
					"Müller, Jannik" + nl +
					"Wagener, Bastian" + nl + nl, DEFAULT, textPane_authors);
		
		appendText("MultiCastor 2.0"  + nl +
				"Kurs TIT10AID" + nl + nl, H2, textPane_authors);
		
		appendText("Eisenhofer, Manuel" + t + "manueleisenhofer@gmx.de" + nl +
					"Kern, Michael" + t +  "mitchkern@gmx.de" + nl +
					"Michelchen, Tobias" + t + "tobias.michelchen@googlemail.com" + nl +
					"Scharton, Roman" + t  +   "roman.scharton@googlemail.com" + nl +
					"Schumann, Pascal" + t +   "pascal_schumann@gmx.de"  + nl + nl, DEFAULT, textPane_authors);
				
		JScrollPane s = new JScrollPane(textPane_authors, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		return s;
	}
	
	private void appendText(String text, AttributeSet set, JTextPane textPan) {
		insertText(text,set,textPan.getDocument().getLength(), textPan);          
	} 
	 
	private void insertText(String text, AttributeSet set, int pos, JTextPane textPan) {
		try {
			textPan.getDocument().insertString(pos, text, set);
		} catch (BadLocationException e) {
			System.exit(-1);
		}
	}
}
