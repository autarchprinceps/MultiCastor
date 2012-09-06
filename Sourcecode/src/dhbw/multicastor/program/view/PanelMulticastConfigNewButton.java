   
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
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;


import java.util.ResourceBundle;

public class PanelMulticastConfigNewButton extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -794090104188845848L;

	private static final ResourceBundle messages = ResourceBundle.getBundle("dhbw.multicastor.resources.i18n.messages"); //$NON-NLS-1$

	// Buttons
	private JButton btn_add;
	private JButton btn_change;

	// Radiobuttons
	protected JRadioButton rbtn_IGMP;
	protected JRadioButton rbtn_MLD;
	protected JRadioButton rbtn_MMRP;

	public PanelMulticastConfigNewButton(ViewController ctrl){
		initButtons(ctrl);
	}
	
	private void initButtons(ViewController ctrl) {
		GridLayout gridLayout = new GridLayout();
		this.setLayout(new BorderLayout());
		// Buttons
		btn_add = new JButton(messages.getString("PanelMulticastConfigNewButton.btn_add.text")); //$NON-NLS-1$
		btn_add.setPreferredSize(new Dimension(100, 20));
		btn_add.addActionListener(ctrl);

		btn_change = new JButton(messages.getString("PanelMulticastConfigNewButton.btn_change.text")); //$NON-NLS-1$
		btn_change.setPreferredSize(new Dimension(100, 20));
		btn_change.setEnabled(false);
		btn_change.addActionListener(ctrl);

		JPanel pan_button = new JPanel();
		gridLayout.setColumns(2);
		pan_button.setLayout(gridLayout);
		pan_button.add(btn_add);
		pan_button.add(btn_change);

		// Radio Buttons
		rbtn_IGMP = new JRadioButton(messages.getString("PanelMulticastConfigNewButton.rbtn_IGMP.text")); //$NON-NLS-1$
		rbtn_IGMP.setActionCommand(PanelMulticastConfigNew.RBTN_IGMP);
		rbtn_MLD = new JRadioButton(messages.getString("PanelMulticastConfigNewButton.rbtn_MLD.text")); //$NON-NLS-1$
		rbtn_MLD.setActionCommand(PanelMulticastConfigNew.RBTN_MLD);
		rbtn_MMRP = new JRadioButton(messages.getString("PanelMulticastConfigNewButton.rbtn_MMRP.text")); //$NON-NLS-1$
		rbtn_MMRP.setActionCommand(PanelMulticastConfigNew.RBTN_MMRP);
		rbtn_IGMP.addActionListener(ctrl);
		rbtn_MLD.addActionListener(ctrl);
		rbtn_MMRP.addActionListener(ctrl);
		JPanel pan_radio = new JPanel();
		gridLayout.setColumns(3);
		pan_radio.setLayout(gridLayout);
		pan_radio.add(rbtn_IGMP);
		pan_radio.add(rbtn_MLD);
		pan_radio.add(rbtn_MMRP);

		this.add(pan_button, BorderLayout.NORTH);
		this.add(pan_radio, BorderLayout.CENTER);

	}
	
	public JButton getBtn_add() {
		return btn_add;
	}

	public JButton getBtn_change() {
		return btn_change;
	}

	public JRadioButton getRbtn_IGMP() {
		return rbtn_IGMP;
	}

	public JRadioButton getRbtn_MLD() {
		return rbtn_MLD;
	}

	public JRadioButton getRbtn_MMRP() {
		return rbtn_MMRP;
	}
	
	public String getSelectedRadioButton(){
		if(rbtn_IGMP.isSelected()){
			return PanelMulticastConfigNew.RBTN_IGMP;
		}else if(rbtn_MLD.isSelected()){
			return PanelMulticastConfigNew.RBTN_MLD;
		}else {
			return PanelMulticastConfigNew.RBTN_MMRP;
		}
	}
	
	public ProtocolType getSelectedProtocolType(){
		if(rbtn_IGMP.isSelected()){
			return ProtocolType.IGMP;
		}else if(rbtn_MLD.isSelected()){
			return ProtocolType.MLD;
		}else {
			return ProtocolType.MMRP;
		}
	}
	
	public void setIgmp(){
		this.rbtn_IGMP.setSelected(true);
		this.rbtn_MLD.setSelected(false);
		this.rbtn_MMRP.setSelected(false);
	}
	
	public void setMld(){
		this.rbtn_IGMP.setSelected(false);
		this.rbtn_MLD.setSelected(true);
		this.rbtn_MMRP.setSelected(false);
	}
	
	public void setMmrp(){
		this.rbtn_IGMP.setSelected(false);
		this.rbtn_MLD.setSelected(false);
		this.rbtn_MMRP.setSelected(true);
	}

	public void btn_ChangeSetEnabled(boolean bool) {
		getBtn_change().setEnabled(bool);		
	}
}
