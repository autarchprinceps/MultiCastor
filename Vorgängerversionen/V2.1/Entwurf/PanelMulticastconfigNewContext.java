   
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
 
  

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;


public class PanelMulticastconfigNewContext extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = -65185712812032272L;

	private static final ResourceBundle messages = ResourceBundle.getBundle("dhbw.multicastor.resources.i18n.messages"); //$NON-NLS-1$
	
	private PanelMulticastConfigNew igmp;
	private PanelMulticastConfigNew mld;
	private PanelMulticastConfigNew mmrp;
	
	private PanelMulticastConfigNewButton button;
	
	public PanelMulticastconfigNewContext(ViewController ctrl){
		if(ctrl.getCurrentModus() == Modus.SENDER){
			igmp = new PanelMulticastConfigNewSender(ctrl, ProtocolType.IGMP);
			mld = new PanelMulticastConfigNewSender(ctrl, ProtocolType.MLD);
			mmrp = new PanelMulticastConfigNewSender(ctrl, ProtocolType.MMRP);
		}else{
			igmp = new PanelMulticastConfigNewReceiver(ctrl, ProtocolType.IGMP);
			mld = new PanelMulticastConfigNewReceiver(ctrl, ProtocolType.MLD);
			mmrp = new PanelMulticastConfigNewReceiver(ctrl, ProtocolType.MMRP);
		}
		button = new PanelMulticastConfigNewButton(ctrl);
		setBorder(new MiscBorder(messages.getString("PanelMulticastconfigNewContext.this.borderTitle"))); //$NON-NLS-1$
		this.setLayout(new BorderLayout());
		this.add(button, BorderLayout.NORTH);
		this.add(igmp, BorderLayout.CENTER);
		this.setVisible(true);
		this.setPreferredSize(new Dimension(225, 350));
		this.setMaximumSize(new Dimension(270, 500));
	}

	public PanelMulticastConfigNew getIgmp() {
		return igmp;
	}
	public PanelMulticastConfigNew getMld() {
		return mld;
	}
	public PanelMulticastConfigNew getMmrp() {
		return mmrp;
	}
	
	public void setMld(){
		this.removeAll();
		button.setMld();
		this.add(button, BorderLayout.NORTH);
		this.add(mld, BorderLayout.CENTER);
		this.validate();
	}
	
	public void setMmrp(){
		button.setMmrp();
		this.removeAll();
		this.add(button, BorderLayout.NORTH);
		this.add(mmrp, BorderLayout.CENTER);
		this.validate();
	}
	
	public void setIgmp(){
		button.setIgmp();
		this.removeAll();
		this.add(button, BorderLayout.NORTH);
		this.add(igmp, BorderLayout.CENTER);
		this.validate();
	}
	
	public PanelMulticastConfigNewButton getButton() {
		return button;
	}

	public PanelMulticastConfigNew getPanelMulticastConfigNew() {
		if(button.getRbtn_IGMP().isSelected()){
			return igmp;
		}else if(button.getRbtn_MLD().isSelected()){
			return mld;
		}else{
			return mmrp;
		}
	}
	
	public void setDefaultValues(){
		PanelMulticastConfigNew panel = getPanelMulticastConfigNew();
		panel.setDefaultValues();
	}
	
	public void setEditableFields(boolean bool){
		PanelMulticastConfigNew panel = getPanelMulticastConfigNew();
		panel.setEditable(bool);
	}
	
}
