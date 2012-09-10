   
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


public class PanelMulticastConfigNewSender extends PanelMulticastConfigNew {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3084744998053006001L;
	private static final ResourceBundle messages = ResourceBundle.getBundle("dhbw.multicastor.resources.i18n.messages"); //$NON-NLS-1$
	public PanelMulticastConfigNewSender(ViewController ctrl, ProtocolType type) {
		super(ctrl, type);
		this.setCb_Interface();
	}

	@Override
	public void clearIgmp() {
		this.getTf_address().setText(null);
		this.getTf_PLength().setText(null);
		this.getTf_port().setText(null);
		this.getTf_PRate().setText(null);
		this.getTf_TTL().setText(null);
		if (this.getCb_interface().getItemCount() > 0) {
			this.getCb_interface().setSelectedIndex(0);
		} else {
			this.getCb_interface().setSelectedItem(null);
		}
		this.validate();
	}

	@Override
	public void clearMmrp() {
		this.getTf_address().setText(null);
		this.getTf_PLength().setText(null);
		this.getTf_PRate().setText(null);
		if (this.getCb_interface().getItemCount() > 0) {
			this.getCb_interface().setSelectedIndex(0);
		} else {
			this.getCb_interface().setSelectedItem(null);
		}
		this.validate();
	}

	@Override
	public void clearMld() {
		this.getTf_address().setText(null);
		this.getTf_PLength().setText(null);
		this.getTf_PRate().setText(null);
		this.getTf_port().setText(null);
		this.getTf_TTL().setText(null);
		if (this.getCb_interface().getItemCount() > 0) {
			this.getCb_interface().setSelectedIndex(0);
		} else {
			this.getCb_interface().setSelectedItem(null);
		}
		this.validate();
	}

	/**
	 * Erstellen der IGMP Sender spezifischen Konfigurations Textfelder.
	 */
	@Override
	protected JPanel initIGMP() {
		JPanel pan_config = this.createJPanel();
		pan_config.add(Box.createVerticalStrut(15));

		// IPv4 Group Address
		JLabel lab_address = this.createJLabel(messages.getString("PanelMulticastConfigNewSender.ipv4GrpAdress")); //$NON-NLS-1$
		tf_address = this.createJTextField();
		pan_config.add(lab_address);
		pan_config.add(tf_address);

		// IPv4 Network Interface
		JLabel lab_interface = this.createJLabel(messages.getString("PanelMulticastConfigNewSender.ipv4NetworkInterface")); //$NON-NLS-1$
		cb_interface = this.createWideComboBox(ProtocolType.IGMP);
		pan_config.add(lab_interface);
		pan_config.add(cb_interface);

		// UDP Port
		JLabel lab_port = this.createJLabel(messages.getString("PanelMulticastConfigNewSender.udpPort")); //$NON-NLS-1$
		tf_port = this.createJTextField();
		pan_config.add(lab_port);
		pan_config.add(tf_port);

		// Time to Live
		JLabel lab_TTL = this.createJLabel(messages.getString("PanelMulticastConfigNewSender.ttl")); //$NON-NLS-1$
		tf_TTL = this.createJTextField();
		pan_config.add(lab_TTL);
		pan_config.add(tf_TTL);

		// Packet Rate
		JLabel lab_PRate = this.createJLabel(messages.getString("PanelMulticastConfigNewSender.packetRate")); //$NON-NLS-1$
		tf_PRate = this.createJTextField();
		pan_config.add(lab_PRate);
		pan_config.add(tf_PRate);

		// Packet Length
		JLabel lab_PLength = this.createJLabel(messages.getString("PanelMulticastConfigNewSender.packetLenght")); //$NON-NLS-1$
		tf_PLength = this.createJTextField();
		pan_config.add(lab_PLength);
		pan_config.add(tf_PLength);

		return pan_config;
	}

	/**
	 * Erstellen der MLD Sender spezifischen Konfigurations Textfelder.
	 */
	@Override
	protected JPanel initMLD() {
		JPanel pan_config = this.createJPanel();
		pan_config.add(Box.createVerticalStrut(15));

		// IPv6 Group Address
		JLabel lab_address = this.createJLabel(messages.getString("PanelMulticastConfigNewSender.ipv6GrpAdress")); //$NON-NLS-1$
		tf_address = this.createJTextField();
		pan_config.add(lab_address);
		pan_config.add(tf_address);

		// IPv6 Network Interface
		JLabel lab_interface = this.createJLabel(messages.getString("PanelMulticastConfigNewSender.ipv6NetInterface")); //$NON-NLS-1$
		cb_interface = this.createWideComboBox(ProtocolType.MLD);
		pan_config.add(lab_interface);
		pan_config.add(cb_interface);

		// UDP Port
		JLabel lab_port = this.createJLabel("UDP Port"); //$NON-NLS-1$
		tf_port = this.createJTextField();
		pan_config.add(lab_port);
		pan_config.add(tf_port);

		// Time to Live
		JLabel lab_TTL = this.createJLabel("Time to Live"); //$NON-NLS-1$
		tf_TTL = this.createJTextField();
		pan_config.add(lab_TTL);
		pan_config.add(tf_TTL);

		// Packet Rate
		JLabel lab_PRate = this.createJLabel("Packet Rate"); //$NON-NLS-1$
		tf_PRate = this.createJTextField();
		pan_config.add(lab_PRate);
		pan_config.add(tf_PRate);

		// Packet Length
		JLabel lab_PLength = this.createJLabel("Packet Length"); //$NON-NLS-1$
		tf_PLength = this.createJTextField();
		pan_config.add(lab_PLength);
		pan_config.add(tf_PLength);

		return pan_config;
	}

	/**
	 * Erstellen der MMRP Sender spezifischen Konfigurations Textfelder.
	 */
	@Override
	protected JPanel initMMRP() {
		JPanel pan_config = this.createJPanel();
		pan_config.add(Box.createVerticalStrut(15));

		// Group MAC Address
		JLabel lab_address = this.createJLabel(messages.getString("PanelMulticastConfigNewSender.macGrpAdress")); //$NON-NLS-1$
		tf_address = this.createJTextField();
		pan_config.add(lab_address);
		pan_config.add(tf_address);

		// Network Interface
		JLabel lab_interface = this.createJLabel(messages.getString("PanelMulticastConfigNewSender.networkInterface")); //$NON-NLS-1$
		cb_interface = this.createWideComboBox(ProtocolType.MMRP);
		pan_config.add(lab_interface);
		pan_config.add(cb_interface);

		// Packet Rate
		JLabel lab_PRate = this.createJLabel("Packet Rate"); //$NON-NLS-1$
		tf_PRate = this.createJTextField();
		pan_config.add(lab_PRate);
		pan_config.add(tf_PRate);

		// Packet Length
		JLabel lab_PLength = this.createJLabel("Packet Length"); //$NON-NLS-1$
		tf_PLength = this.createJTextField();
		pan_config.add(lab_PLength);
		pan_config.add(tf_PLength);

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
			getTf_PLength().setText(String.valueOf(data.getPacketLength()));
			getTf_TTL().setText(String.valueOf(data.getTtl()));
			getTf_PRate().setText(String.valueOf(data.getPacketRateDesired()));
		} else {
			MMRPData data = DefaultMulticastData.getMmrp();
			getTf_address().setText(data.getMacGroupId().toString());
			getTf_PLength().setText(String.valueOf(data.getPacketLength()));
			getTf_PRate().setText(String.valueOf(data.getPacketRateDesired()));
		}
	}

	@Override
	public void setEditable(boolean b) {
		if (type.equals(MulticastData.ProtocolType.IGMP)
				|| type.equals(MulticastData.ProtocolType.MLD)) {
			//set enabled
			getTf_address().setEnabled(b);
			getTf_port().setEnabled(b);
			getTf_PLength().setEnabled(b);
			getTf_TTL().setEnabled(b);
			getTf_PRate().setEnabled(b);
			getCb_interface().setEnabled(b);
			
			//set editable 
			getTf_address().setEditable(b);
			getTf_port().setEditable(b);
			getTf_PLength().setEditable(b);
			getTf_TTL().setEditable(b);
			getTf_PRate().setEditable(b);
			getCb_interface().setEditable(b);
		} else {
			//set enabled
			getCb_interface().setEnabled(b);
			getTf_address().setEnabled(b);
			getTf_PLength().setEnabled(b);
			getTf_PRate().setEnabled(b);
			
			//set editable
			getCb_interface().setEditable(b);
			getTf_address().setEditable(b);
			getTf_PLength().setEditable(b);
			getTf_PRate().setEditable(b);
		}
	}
	
	protected void addDocumentListener(DocumentListener listener){
		tf_PLength.getDocument().addDocumentListener(listener);
		tf_PRate.getDocument().addDocumentListener(listener);
		tf_address.getDocument().addDocumentListener(listener);
		if(!(listener instanceof MmrpDocumentListener)){
			tf_TTL.getDocument().addDocumentListener(listener);
			tf_port.getDocument().addDocumentListener(listener);
		}
	}

}
