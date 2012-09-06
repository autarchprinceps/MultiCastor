   
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;

import java.awt.FlowLayout;

/**
 * Panel, welche die Informationen über den selektierten Multicaststrom anzeigt
 * @author Michael Kern
 *
 */
public class PanelMulticastInfo extends JPanel {
	private ResourceBundle messages = ResourceBundle.getBundle("dhbw.multicastor.resources.i18n.messages"); //$NON-NLS-1$

	/**
	 * 
	 */
	private static final long serialVersionUID = 7336017899324902350L;
	// Labels und Labels für die Values
	private JLabel lab_address;
	private JLabel lab_value_address;
	private JLabel lab_UDPPort;
	private JLabel lab_value_UDPPort;
	private JLabel lab_state;
	private JLabel lab_value_state;
	private JLabel lab_DataRate;
	private JLabel lab_value_DataRate;
	private JLabel lab_TransferRate;
	private JLabel lab_value_TransferRate;
	private JLabel lab_countPack;
	private JLabel lab_value_countPack;
	
	// Panel für Label Value Paare
	private JPanel pan_address;
	private JPanel pan_UDPPort;
	private JPanel pan_state;
	private JPanel pan_DataRate;
	private JPanel pan_TransferRate;
	private JPanel pan_countPack;
	
	public PanelMulticastInfo() {
		setBorder(new MiscBorder("MultiCast Information"));
		setPreferredSize(new Dimension(270, 175));
		this.setMaximumSize(new Dimension(270, 175));
		init();		
	}
	
	
	/**
	 * Erstellen des IGMP spezifischen Informationspanel. 
	 */
	private void init() {
		
		Dimension dimensionLab = new Dimension(125, 15);
		Dimension dimensionVal = new Dimension(75, 15);
		setLayout(new GridLayout(6, 1, 0, 0));
		
		// IPv4 Group Address
		pan_address = new JPanel();
		FlowLayout flowLayout = (FlowLayout) pan_address.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		lab_address = new JLabel();
		lab_address.setText(messages.getString("PanelMulticastInfo.lab_address.text"));
		lab_address.setHorizontalAlignment(SwingConstants.LEFT);
		lab_address.setText(messages.getString("PanelMulticastInfo.lab_address.text")); //$NON-NLS-1$
		lab_address.setPreferredSize(new Dimension(130, 15));
		lab_value_address = new JLabel();
		lab_value_address.setAlignmentX(-4.0f);
		lab_value_address.setText("---");
		lab_value_address.setPreferredSize(new Dimension(115, 15));
		pan_address.add(lab_address);
		pan_address.add(lab_value_address);
		this.add(pan_address);
		
		// UDP Port
		pan_UDPPort = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) pan_UDPPort.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		lab_UDPPort = new JLabel();
		lab_UDPPort.setText(messages.getString("PanelMulticastInfo.lab_UDPPort.text"));
		lab_UDPPort.setText(messages.getString("PanelMulticastInfo.lab_UDPPort.text")); //$NON-NLS-1$
		lab_UDPPort.setPreferredSize(new Dimension(130, 15));
		lab_value_UDPPort = new JLabel();
		lab_value_UDPPort.setText("---");
		lab_value_UDPPort.setPreferredSize(new Dimension(115, 15));
		pan_UDPPort.add(lab_UDPPort);
		pan_UDPPort.add(lab_value_UDPPort);
		this.add(pan_UDPPort);
		
		// State
		pan_state = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) pan_state.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		lab_state = new JLabel();
		lab_state.setText(messages.getString("PanelMulticastInfo.lab_state.text")); //$NON-NLS-1$
		lab_state.setPreferredSize(new Dimension(130, 15));
		lab_value_state = new JLabel();
		lab_value_state.setText("---");
		lab_value_state.setPreferredSize(new Dimension(115, 15));
		pan_state.add(lab_state);
		pan_state.add(lab_value_state);
		this.add(pan_state);
		
		// Data rate
		pan_DataRate = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) pan_DataRate.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		lab_DataRate = new JLabel();
		lab_DataRate.setText(messages.getString("PanelMulticastInfo.lab_DataRate.text")); //$NON-NLS-1$
		lab_DataRate.setPreferredSize(new Dimension(130, 15));
		lab_value_DataRate = new JLabel();
		lab_value_DataRate.setText("---");
		lab_value_DataRate.setPreferredSize(new Dimension(115, 15));
		pan_DataRate.add(lab_DataRate);
		pan_DataRate.add(lab_value_DataRate);
		this.add(pan_DataRate);
		
		// Transfer rate
		pan_TransferRate = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) pan_TransferRate.getLayout();
		flowLayout_4.setAlignment(FlowLayout.LEFT);
		lab_TransferRate = new JLabel();
		lab_TransferRate.setText(messages.getString("PanelMulticastInfo.lab_TransferRate.text")); //$NON-NLS-1$
		lab_TransferRate.setPreferredSize(new Dimension(130, 15));
		lab_value_TransferRate = new JLabel();
		lab_value_TransferRate.setText("---");
		lab_value_TransferRate.setPreferredSize(new Dimension(115, 15));
		pan_TransferRate.add(lab_TransferRate);
		pan_TransferRate.add(lab_value_TransferRate);
		this.add(pan_TransferRate);
		
		// count Packets
		pan_countPack = new JPanel();
		FlowLayout flowLayout_5 = (FlowLayout) pan_countPack.getLayout();
		flowLayout_5.setAlignment(FlowLayout.LEFT);
		lab_countPack = new JLabel();
		lab_countPack.setText(messages.getString("PanelMulticastInfo.lab_countPack.text.Sender"));
		lab_countPack.setPreferredSize(new Dimension(130, 15));
		lab_value_countPack = new JLabel();
		lab_value_countPack.setText("---");
		lab_value_countPack.setPreferredSize(new Dimension(115, 15));
		pan_countPack.add(lab_countPack);
		pan_countPack.add(lab_value_countPack);
		this.add(pan_countPack);	
	}
		
	/**
	 * Methode, die den InfoPanel aktualisiert
	 */
	public void update(MulticastData multicast){
		if(multicast == null){
			return;
		}
		
		if(multicast instanceof IgmpMldData){
			this.removeAll();
			init();
			IgmpMldData igmpMld = (IgmpMldData) multicast;
			lab_value_address.setText(igmpMld.getGroupIpToString());
			lab_value_address.setToolTipText(igmpMld.getGroupIpToString());
			
			lab_value_UDPPort.setText(Integer.toString(igmpMld.getUdpPort()));
			lab_value_UDPPort.setToolTipText(Integer.toString(igmpMld.getUdpPort()));
			lab_value_UDPPort.setEnabled(true);
			lab_UDPPort.setEnabled(true);
			
			if(multicast.getProtocolType() == ProtocolType.IGMP){
				lab_address.setText("IPv4 Group Address: ");
			}else if(multicast.getProtocolType() == ProtocolType.MLD){
				lab_address.setText("IPv6 Group Address: ");
			}
		}else if(multicast instanceof MMRPData){
			lab_address.setText("Group MAC Address: ");
			this.remove(pan_UDPPort);
//			this.validate();
			this.repaint();
			MMRPData mmrp = (MMRPData) multicast;
			lab_value_address.setText(mmrp.getMacGroupId().toString());
			lab_value_address.setToolTipText(mmrp.getMacGroupId().toString());
		}
		updateMultiCast(multicast);
	}
	
	/**
	 * dient zum aktualisieren für Elemente, die nicht unterschieden werden müssen
	 * @param multicast
	 */
	private void updateMultiCast(final MulticastData multicast){
		lab_value_DataRate.setText(Long.toString(multicast.getPacketRateDesired()));
		lab_value_DataRate.setToolTipText(Long.toString(multicast.getPacketRateDesired()));
		
		lab_value_countPack.setText(Long.toString(multicast.getPacketCount()));
		lab_value_countPack.setToolTipText(Long.toString(multicast.getPacketCount()));
		
		lab_value_TransferRate.setText(Integer.toString(multicast.getPacketRateMeasured()));
		lab_value_TransferRate.setToolTipText(Integer.toString(multicast.getPacketRateMeasured()));
		
		if(multicast.getActive()){
			lab_value_state.setForeground(Color.GREEN);
			lab_value_state.setText(messages.getString("PanelMulticastInfo.lab_value_state_active"));
			lab_value_state.setToolTipText(messages.getString("PanelMulticastInfo.lab_value_state_active"));
		}else{
			lab_value_state.setForeground(Color.RED);
			lab_value_state.setText(messages.getString("PanelMulticastInfo.lab_value_state_inactive"));
			lab_value_state.setToolTipText(messages.getString("PanelMulticastInfo.lab_value_state_inactive"));
		}
	}
	
	public void reset(){
		lab_value_address.setText("---");
		lab_value_address.setToolTipText(null);
		
		
		lab_value_UDPPort.setText("---");
		lab_value_UDPPort.setToolTipText(null);
		
		lab_value_state.setText("---");
		lab_value_state.setToolTipText(null);
		lab_value_state.setForeground(null);
		
		lab_value_DataRate.setText("---");
		lab_value_DataRate.setToolTipText(null);
		
		lab_value_TransferRate.setText("---");
		lab_value_TransferRate.setToolTipText(null);
		
		lab_value_countPack.setText("---");
		lab_value_countPack.setToolTipText(null);
	}
	
	public void setSender(){
		lab_countPack.setText(messages.getString("PanelMulticastInfo.lab_countPack.text.Sender"));
	}
	
	public void setReceiver(){
		lab_countPack.setText(messages.getString("PanelMulticastInfo.lab_countPack.text.Receiver"));
	}


	
}
