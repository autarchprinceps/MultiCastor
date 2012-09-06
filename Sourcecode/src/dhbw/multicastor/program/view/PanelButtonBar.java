   
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;

import dhbw.multicastor.program.controller.ViewController;


public class PanelButtonBar extends JPanel{
	private static final ResourceBundle messages = ResourceBundle.getBundle("dhbw.multicastor.resources.i18n.messages"); //$NON-NLS-1$

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton b_activate;
	private JButton b_display;
	private JButton b_delete;
	private JButton b_reset;
	private JButton b_leave;
	private JButton b_leaveAll;
	
	public PanelButtonBar(ViewController ctrl){
		setMaximumSize(new Dimension(32767, 50));
		setBorder(null);
		initPanelButtonBar(ctrl);
		this.setVisible(true);
		this.setPreferredSize(new Dimension(545, 66));
		
		
	}
	
	private void initPanelButtonBar(ViewController ctrl){
		
		JPanel center = new JPanel();
		center.setPreferredSize(new Dimension(540, 45));
		center.setSize(new Dimension(540, 50));
		center.setMinimumSize(new Dimension(540, 50));
		center.setMaximumSize(new Dimension(540, 50));
		
		this.add(center, BorderLayout.CENTER);
		
		b_delete = new JButton();
		b_delete.setText(messages.getString("PanelButtonBar.b_delete.text")); //$NON-NLS-1$
		b_delete.setEnabled(false);
		b_delete.setSize(100, 9);
		b_delete.addActionListener(ctrl);
		
		b_activate = new JButton();
		b_activate.setAlignmentX(Component.CENTER_ALIGNMENT);
		b_activate.setSize(100, 9);
		b_activate.setText(messages.getString("PanelButtonBar.b_activate.text")); //$NON-NLS-1$
		b_activate.setEnabled(false);
		b_activate.addActionListener(ctrl);
		
		b_display = new JButton();
		b_display.setSize(100, 9);
		b_display.setAlignmentX(1.0f);
		b_display.setText(messages.getString("PanelButtonBar.b_display.text")); //$NON-NLS-1$
		b_display.setEnabled(false);
		b_display.addActionListener(ctrl);
		
		b_reset  = new JButton();
		b_reset.setText(messages.getString("PanelButtonBar.b_reset.text")); //$NON-NLS-1$
		b_reset.setEnabled(false);
		b_reset.setSize(100, 9);
		b_reset.addActionListener(ctrl);
		
		b_leave = new JButton(messages.getString("PanelButtonBar.btnLeave.text")); //$NON-NLS-1$
		b_leave.setEnabled(false);
		b_leave.addActionListener(ctrl);
		
		b_leaveAll = new JButton(messages.getString("PanelButtonBar.btnLeaveAll.text")); //$NON-NLS-1$
		b_leaveAll.setEnabled(false);
		b_leaveAll.addActionListener(ctrl);
		
		center.setLayout(new GridLayout(2, 5, 5, 5));
		
		center.add(b_activate);
		center.add(b_delete);
		center.add(b_reset);
		center.add(b_display);
		center.add(b_leave);
		center.add(b_leaveAll);
	}
	
	public void toggleButtonText(boolean active){
		if (active) {
			getB_activate().setText(messages.getString("PanelButtonBar.b_activate.text"));
			getB_display().setText(messages.getString("PanelButtonBar.b_display.text"));
		} else {
			getB_activate().setText(messages.getString("PanelButtonBar.b_activate.deactivate.text"));
		}
	}
	
	public JButton getB_activate() {
		return b_activate;
	}

	public JButton getB_display() {
		return b_display;
	}

	public JButton getB_delete() {
		return b_delete;
	}

	public JButton getB_reset() {
		return b_reset;
	}
	
	public JButton getB_leave() {
		return b_leave;
	}
	
	public JButton getB_leaveAll() {
		return b_leaveAll;
	}

	public void enableAllButtons(boolean enable) {
		getB_activate().setEnabled(enable);
		getB_display().setEnabled(enable);
		getB_reset().setEnabled(enable);
		getB_delete().setEnabled(enable);
		getB_leave().setEnabled(enable);
		getB_leaveAll().setEnabled(enable);
	}
	
	
}
