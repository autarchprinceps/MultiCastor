   
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Panel, welche die Statusbar des jeweiligen Programmteils beinhaltet
 * @author Daniel Becker, Michael Kern
 *
 */
@SuppressWarnings("serial")
public class PanelStatusBar extends JPanel {
	private static final ResourceBundle messages = ResourceBundle.getBundle("dhbw.multicastor.resources.i18n.messages"); //$NON-NLS-1$
	private JPanel pan_east;
	private JPanel pan_west;
	private JLabel lb_multicast_selected;
	private JLabel lb_multicast_count;
	private JLabel lb_traffic;
	
	/**
	 * Standardkonstruktor welcher die komplette Statusbar initialisiert.
	 */
	public PanelStatusBar(){
		
		// Panel Links
		pan_west = new JPanel();
		pan_west.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
		Object[] messageArgument = {new Double(0), new Double(0)};
		MessageFormat formatter = new MessageFormat("");
		formatter.applyPattern(messages
				.getString("PanelStatusBar.lb_traffic.text"));
		lb_traffic = new JLabel(formatter.format(messageArgument)); //$NON-NLS-1$
		lb_traffic.setFont(MiscFont.getFont());
		lb_traffic.setPreferredSize(new Dimension(320,20));
		lb_traffic.setHorizontalAlignment(SwingConstants.LEFT);
		pan_west.add(lb_traffic);
		
		Object[] messageArguments = {
		         new Integer(0)
		};		 
		formatter = new MessageFormat("");
				
		// Panel Rechts
		pan_east = new JPanel();
		pan_east.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		formatter.applyPattern(messages.getString("PanelStatusBar.lb_multicast_selected.text"));
		lb_multicast_selected = new JLabel(formatter.format(messageArguments)); //$NON-NLS-1$
		lb_multicast_selected.setMinimumSize(new Dimension(120, 14));
		lb_multicast_selected.setMaximumSize(new Dimension(120, 14));
		lb_multicast_selected.setFont(MiscFont.getFont());
		lb_multicast_selected.setPreferredSize(new Dimension(125,20));
		lb_multicast_selected.setHorizontalAlignment(SwingConstants.LEFT);
		formatter.applyPattern(messages.getString("PanelStatusBar.lb_multicast_count.text"));
		lb_multicast_count = new JLabel(formatter.format(messageArguments)); //$NON-NLS-1$
		lb_multicast_count.setFont(MiscFont.getFont());
		lb_multicast_count.setPreferredSize(new Dimension(125,20));
		lb_multicast_count.setHorizontalAlignment(SwingConstants.RIGHT);
		pan_east.add(lb_multicast_selected);
		pan_east.add(lb_multicast_count);
		
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(684, 20));
		add(pan_east, BorderLayout.EAST);
		add(pan_west, BorderLayout.WEST);
		this.setMaximumSize(new Dimension(1920, 20));
	}

	public JLabel getLb_multicasts_selected() {
		return lb_multicast_selected;
	}
	
	public JLabel getLb_multicast_count() {
		return lb_multicast_count;
	}
	/**
	 * Funktion welche angezeigten Traffic in der Statusbar neu anfordert.
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 */
	public void updateTraffic(double upTraffic, double downTraffic){
		Object[] messageArguments = {upTraffic, downTraffic};
		MessageFormat formatter = new MessageFormat("");
		formatter.applyPattern(messages
				.getString("PanelStatusBar.lb_traffic.text"));
		lb_traffic.setText(formatter.format(messageArguments));
	}

	public void setLabelMCCountText(int mcCount) {
		Object[] messageArguments = { mcCount };
		MessageFormat formatter = new MessageFormat("");
		formatter.applyPattern(messages
				.getString("PanelStatusBar.lb_multicast_count.text"));
		this.getLb_multicast_count().setText(formatter.format(messageArguments));
	}

	public void setLabelSelectedText(int selected) {
		Object[] messageArguments = { selected };
		MessageFormat formatter = new MessageFormat("");
		formatter.applyPattern(messages
				.getString("PanelStatusBar.lb_multicast_selected.text"));
		this.getLb_multicasts_selected().setText(formatter.format(messageArguments));
	}


}
