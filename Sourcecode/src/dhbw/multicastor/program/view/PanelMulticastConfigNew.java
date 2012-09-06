   
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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentListener;

import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.listener.IgmpDocumentListener;
import dhbw.multicastor.program.listener.MldDocumentListener;
import dhbw.multicastor.program.listener.MmrpDocumentListener;
import dhbw.multicastor.program.model.MmrpAdapter;
import dhbw.multicastor.program.model.NetworkAdapter;


public abstract class PanelMulticastConfigNew extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -759604487884158072L;

	public static final String RBTN_IGMP = "rbtn_igmp";
	public static final String RBTN_MLD = "rbtn_mld";
	public static final String RBTN_MMRP = "rbtn_mmrp";

	// Configuration
	protected JTextField tf_address;
	protected JTextField tf_port;
	protected JTextField tf_PRate;
	protected JTextField tf_PLength;
	protected JTextField tf_TTL;
	protected JTextField tf_JoinMtTimer;
	
	private IgmpDocumentListener igmpDocumentListener = null;
	private MldDocumentListener mldDocumentListener = null;
	private MmrpDocumentListener mmrpDocumentListener = null;

	


	protected ViewController ctrl;

	protected WideComboBox cb_interface;

	protected JPanel panel = null;

	protected ProtocolType type = null;

	public PanelMulticastConfigNew(ViewController ctrl, ProtocolType type) {
		setPreferredSize(new Dimension(225, 215));
		this.ctrl = ctrl;
		this.type = type;
		igmpDocumentListener = new IgmpDocumentListener(this, ctrl);
		mldDocumentListener = new MldDocumentListener(this, ctrl);
		mmrpDocumentListener = new MmrpDocumentListener(this, ctrl);
		
		if (type.equals(ProtocolType.IGMP)) {
			panel = initIGMP();
			addDocumentListener(igmpDocumentListener);
			this.setType(ProtocolType.IGMP);
		} else if (type.equals(ProtocolType.MLD)) {
			panel = initMLD();
			addDocumentListener(mldDocumentListener);
			this.setType(ProtocolType.MLD);
		} else {
			panel = initMMRP();
			this.setType(ProtocolType.MMRP);
			addDocumentListener(mmrpDocumentListener);
		}

		this.add(panel);
	}

	/**
	 * Erstellen der IGMP spezifischen Konfigurations Textfelder.
	 */
	protected abstract JPanel initIGMP();

	/**
	 * Erstellen der MLD spezifischen Konfigurations Textfelder.
	 */
	protected abstract JPanel initMLD();

	/**
	 * Erstellen der MMRP spezifischen Konfigurations Textfelder.
	 */
	protected abstract JPanel initMMRP();

	protected void setCb_Interface() {
		Vector<NetworkInterface> ipInterfaces = null;
		Vector<MmrpAdapter> mmrpInterfaces = null;
		if (type == ProtocolType.IGMP) {
			ipInterfaces = NetworkAdapter.getipv4Adapters();
			addIPAdapter(ipInterfaces);
		} else if (type == ProtocolType.MLD) {
			ipInterfaces = NetworkAdapter.getipv6Adapters();
			addIPAdapter(ipInterfaces);
		} else if(type == ProtocolType.MMRP){
			mmrpInterfaces = this.ctrl.getMcController().getMmrp_controller().getMmrpAdapterVector();
			addMMRPAdapters(mmrpInterfaces);
		}
	}

	private void addMMRPAdapters(Vector<MmrpAdapter> mmrpInterfaces) {
		for (MmrpAdapter mmrpAdapter : mmrpInterfaces) {
			if (type == ProtocolType.MMRP) {
				try {
					boolean contains = false;
					for(MmrpAdapter a : NetworkAdapter.macInterfaces){
						if(a.getMacAdress().getMacAddress().equals(mmrpAdapter.getMacAdress().getMacAddress())){
							contains = true;
						}
					}
					if(!contains){
						NetworkAdapter.macInterfaces.add(mmrpAdapter);	
					}
					cb_interface.addItem(mmrpAdapter.getMacAdress().toString()
							+"\n "
							+mmrpAdapter.getDescription()
							);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void addIPAdapter(Vector<NetworkInterface> ipInterfaces) {
		for (NetworkInterface ipInt : ipInterfaces) {
			if (type == ProtocolType.IGMP) {
				try {
					Inet4Address ipv4 = getIPv4Adress(ipInt);
					cb_interface
							.addItem(ipv4.toString().substring(1).trim()
									+ "\n "
									+ ipInt.getDisplayName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// (0)temp.get(i).toString().substring(1));
			} else if (type == ProtocolType.MLD) {
				try {
					Inet6Address ipv6 = getIPv6Adress(ipInt);
					cb_interface
							.addItem(ipv6.toString().substring(1)
									.split("%")[0].trim()
									+ "\n "
									+ ipInt.getDisplayName());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private Inet6Address getIPv6Adress(NetworkInterface ipInt) {
		Enumeration<InetAddress> adresses = ipInt.getInetAddresses();
		Inet6Address ipv6 = null;
		while (adresses.hasMoreElements()) {
			InetAddress inetAddress = (InetAddress) adresses
					.nextElement();
			if (inetAddress instanceof Inet6Address) {
				ipv6 = (Inet6Address) inetAddress;
				break;
			}
		}
		return ipv6;
	}

	private Inet4Address getIPv4Adress(NetworkInterface netI) {
		Enumeration<InetAddress> adresses = netI.getInetAddresses();
		Inet4Address ipv4 = null;
		while (adresses.hasMoreElements()) {
			InetAddress inetAddress = (InetAddress) adresses
					.nextElement();
			if (inetAddress instanceof Inet4Address) {
				ipv4 = (Inet4Address) inetAddress;
				break;
			}
		}
		return ipv4;
	}

	public JTextField getTf_address() {
		return tf_address;
	}

	public JTextField getTf_port() {
		return tf_port;
	}

	public JTextField getTf_TTL() {
		return tf_TTL;
	}

	public JTextField getTf_PRate() {
		return tf_PRate;
	}

	public JTextField getTf_PLength() {
		return tf_PLength;
	}
	
	public JTextField getTf_JoinMtTimer() {
		return tf_JoinMtTimer;
	}

	public JComboBox getCb_interface() {
		return cb_interface;
	}
	
	public IgmpDocumentListener getIgmpDocumentListener() {
		return igmpDocumentListener;
	}

	public MldDocumentListener getMldDocumentListener() {
		return mldDocumentListener;
	}

	public MmrpDocumentListener getMmrpDocumentListener() {
		return mmrpDocumentListener;
	}	

	public abstract void clearIgmp();

	public abstract void clearMld();

	public abstract void clearMmrp();

	public JPanel getPanel() {
		return panel;
	}

	public ProtocolType getType() {
		return type;
	}

	public void setType(ProtocolType type) {
		this.type = type;
	}

	protected WideComboBox createWideComboBox(ProtocolType type) {
		WideComboBox wideComboBox = new WideComboBox(type);
		wideComboBox.setFont(MiscFont.getFont(0,12));
		ListCellRenderer renderer = wideComboBox.getRenderer();
		((JLabel) renderer).setHorizontalAlignment(SwingConstants.CENTER);
		wideComboBox.setPreferredSize(new Dimension(160, 20));
		wideComboBox.setMaximumSize(new Dimension(160, 20));
		wideComboBox.setEditable(false);
		
		return wideComboBox;
	}

	protected JLabel createJLabel(String name) {
		JLabel label = new JLabel(name);
		label.setMinimumSize(new Dimension(160, 20));
		label.setPreferredSize(new Dimension(160, 20));
		label.setMaximumSize(new Dimension(160, 20));
		return label;
	}

	protected JTextField createJTextField() {
		JTextField textField = new JTextField();
		textField.setMinimumSize(new Dimension(160, 20));
		textField.setPreferredSize(new Dimension(160, 20));
		textField.setMaximumSize(new Dimension(160, 20));
		textField.setEditable(false);
		textField.addFocusListener(ctrl);
		return textField;
	}

	protected JPanel createJPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1, 0, 5));
		return panel;
	}

	protected void addDocumentListener() {

	}

	public abstract void setDefaultValues();

	public abstract void setEditable(boolean bool);

	protected abstract void addDocumentListener(DocumentListener listener);
}
