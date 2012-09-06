   
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

import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.DocumentListener;

import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.data.DefaultMulticastData;
import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.listener.MmrpDocumentListener;


public class PanelMulticastConfigNewReceiver extends PanelMulticastConfigNew{
	private static final ResourceBundle messages = ResourceBundle.getBundle("dhbw.multicastor.resources.i18n.messages"); //$NON-NLS-1$
	/**
	 * 
	 */
	private static final long serialVersionUID = -8501582591912967784L;

	public PanelMulticastConfigNewReceiver(ViewController ctrl, ProtocolType type) {
		super(ctrl, type);
		if(type == ProtocolType.MMRP){
			this.setCb_Interface();
		}
	}
	
	@Override
	public void clearIgmp(){
		this.getTf_address().setText("");
		this.getTf_port().setText("");
		this.validate();
	}
	
	@Override
	public void clearMmrp(){
		this.getTf_address().setText("");
		this.getTf_JoinMtTimer().setText("");
		if (this.getCb_interface().getItemCount() > 0) {
			this.getCb_interface().setSelectedIndex(0);
		}else{
			this.getCb_interface().setSelectedItem(null);
		}
		this.validate();
	}
	
	@Override
	public void clearMld(){
		this.getTf_address().setText("");
		this.getTf_port().setText("");
		this.validate();
	}
	
	
	
	/**
	 * Erstellen der IGMP Receiver spezifischen Konfigurations Textfelder.
	 */
	@Override
	protected JPanel initIGMP() {
		
		JPanel pan_config = this.createJPanel();
		pan_config.add(Box.createVerticalStrut(15));
		// IPv4 Group Address
		JLabel lab_address = this.createJLabel(messages.getString("PanelMulticastconfigNewReceiver.ipv4GrpAdress"));
		tf_address = this.createJTextField();
		pan_config.add(lab_address);
		pan_config.add(tf_address);
		
		// UDP Port
		JLabel lab_port = this.createJLabel(messages.getString("PanelMulticastconfigNewReceiver.UdpPort"));
		tf_port = this.createJTextField();
		pan_config.add(lab_port);
		pan_config.add(tf_port);
		
		return pan_config;
		
	}
	
	/**
	 * Erstellen der MLD Receiver spezifischen Konfigurations Textfelder.
	 */
	@Override
	protected JPanel initMLD() {
		JPanel pan_config = this.createJPanel();
		pan_config.add(Box.createVerticalStrut(15));
		// IPv6 Group Address
		JLabel lab_address = this.createJLabel(messages.getString("PanelMulticastconfigNewReceiver.ipv6GrpAdress"));
		tf_address = this.createJTextField();
		pan_config.add(lab_address);
		pan_config.add(tf_address);
		
		// UDP Port
		JLabel lab_port = this.createJLabel(messages.getString("PanelMulticastconfigNewReceiver.UdpPort"));
		tf_port = this.createJTextField();
		pan_config.add(lab_port);
		pan_config.add(tf_port);
		
		return pan_config;
	}
	
	/**
	 * Erstellen der MMRP Receiver spezifischen Konfigurations Textfelder.
	 */
	@Override
	protected JPanel initMMRP() {
		JPanel pan_config = this.createJPanel();
		pan_config.add(Box.createVerticalStrut(15));
		// Group MAC Address
		JLabel lab_address = this.createJLabel(messages.getString("PanelMulticastconfigNewReceiver.GrpMacAdress"));
		tf_address = this.createJTextField();
		pan_config.add(lab_address);
		pan_config.add(tf_address);
		
		// Network Interface
		JLabel lab_interface = this.createJLabel(messages.getString("PanelMulticastconfigNewReceiver.NetworkInterface"));
		cb_interface = this.createWideComboBox(ProtocolType.MMRP);
		pan_config.add(lab_interface);
		pan_config.add(cb_interface);
		
		// JoinMT Timer
		JLabel lab_joinMtTimer = this.createJLabel("JoinMT Timer");
		tf_JoinMtTimer = this.createJTextField();
		pan_config.add(lab_joinMtTimer);
		pan_config.add(tf_JoinMtTimer);
		
		return pan_config;
	}

	@Override
	public void setDefaultValues() {
		if (type.equals(MulticastData.ProtocolType.IGMP)
				|| type.equals(MulticastData.ProtocolType.MLD)) {
			IgmpMldData data = null;
			if (type.equals(MulticastData.ProtocolType.IGMP)) {
				data = DefaultMulticastData.getIgmp();
			} else {
				data = DefaultMulticastData.getMld();
			}
			getTf_address().setText(data.getGroupIpToString());
			getTf_port().setText(String.valueOf(data.getUdpPort()));
		} else {
			MMRPData data = DefaultMulticastData.getMmrp();
			getTf_address().setText(data.getMacGroupId().toString());
			getTf_JoinMtTimer().setText(String.valueOf(data.getTimerJoinMt()));
		}
	}

	@Override
	public void setEditable(boolean bool) {
		if (type.equals(MulticastData.ProtocolType.IGMP)
				|| type.equals(MulticastData.ProtocolType.MLD)) {
			//set enabled
			getTf_address().setEnabled(bool);
			getTf_port().setEnabled(bool);
			
			//set editable
			getTf_address().setEditable(bool);
			getTf_port().setEditable(bool);
		} else {
			
			//set enabled
			getTf_address().setEnabled(bool);
			getCb_interface().setEnabled(bool);
			getTf_JoinMtTimer().setEnabled(bool);
			
			//set editable
			getTf_address().setEditable(bool);
			getCb_interface().setEditable(bool);
			getTf_JoinMtTimer().setEditable(bool);
		}
	}
	
	protected void addDocumentListener(DocumentListener listener){
		tf_address.getDocument().addDocumentListener(listener);
		if(!(listener instanceof MmrpDocumentListener)){
			tf_port.getDocument().addDocumentListener(listener);
		}else{
			tf_JoinMtTimer.getDocument().addDocumentListener(listener);
		}
	}

}
