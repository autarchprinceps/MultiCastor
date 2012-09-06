   
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
 
 package dhbw.multicastor.program.listener;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.sun.org.apache.xpath.internal.operations.Mod;

import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.model.InputValidator;
import dhbw.multicastor.program.view.PanelMulticastConfigNew;


public class MmrpDocumentListener extends AbstractDocumentListener {
	PanelMulticastConfigNew panel;
	private ViewController ctrl;

	public MmrpDocumentListener(PanelMulticastConfigNew panel) {
		this.panel = panel;
	}

	public MmrpDocumentListener(PanelMulticastConfigNew panel,
			ViewController ctrl) {
		this.panel = panel;
		this.ctrl = ctrl;
	}

	@Override
	public void changedUpdate(DocumentEvent arg0) {
	}

	@Override
	public void insertUpdate(DocumentEvent arg0) {
		boolean accepted = true;
		boolean buttonEnabled = true;
		JComponent component = null;
		component = panel.getTf_address();
		accepted = (InputValidator.checkMACGroupAddress(panel.getTf_address().getText()) != null);
		if (!accepted && buttonEnabled) {
			buttonEnabled = false;
		}
		setColor(component, accepted);

		component = panel.getCb_interface();
		if(panel.getCb_interface().getSelectedItem() != null){
			accepted = (InputValidator.checkMAC(panel.getCb_interface().getSelectedItem().toString()) != null);
		}else{
			accepted = false;
		}
		if (!accepted && buttonEnabled) {
			buttonEnabled = false;
		}
		setColor(component, accepted);
		
		if (ctrl.getCurrentModus().equals(Modus.RECEIVER)){
			component = panel.getTf_JoinMtTimer();
			accepted = (InputValidator.checkJoinMtTimer(panel.getTf_JoinMtTimer().getText()) > -2);
			if (!accepted && buttonEnabled) {
				buttonEnabled = false;
			}
		}
		setColor(component, accepted);
		
		if (ctrl.getCurrentModus().equals(Modus.SENDER)) {
			component = panel.getTf_PRate();
			accepted = (InputValidator.checkPacketRate(panel.getTf_PRate()
					.getText()) > -1);
			if (!accepted && buttonEnabled) {
				buttonEnabled = false;
			}
			setColor(component, accepted);
			component = panel.getTf_PLength();
			accepted = (InputValidator.checkMmrpPacketLength(panel
					.getTf_PLength().getText()) > -1);
			if (!accepted && buttonEnabled) {
				buttonEnabled = false;
			}
			setColor(component, accepted);
		}
		ctrl.enableAddButton(buttonEnabled);
	}

	@Override
	public void removeUpdate(DocumentEvent arg0) {
		insertUpdate(arg0);
	}
}
