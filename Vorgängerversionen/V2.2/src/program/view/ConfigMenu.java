package program.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import program.controller.Messages;
import program.controller.ViewController;
import program.data.MulticastData.Typ;
import program.data.UserInputData;
import program.model.InputValidator;
import program.model.NetworkAdapter;
import program.view.MiscBorder.BorderTitle;
import program.view.MiscBorder.BorderType;

public class ConfigMenu extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6678131699895805271L;
	private final JPanel contentPanel = new JPanel();
	public FrameMain parent;
	private JComboBox comboBox;
	private PanelMulticastConfig mmrpPanel;
	private PanelMulticastConfig igmpPanel;
	private ViewController ctrl;
	private JButton okButton;
	private boolean[][] state = new boolean[2][6];
	
	private ItemListener il = new ItemListener() {
			
			@Override
	public void itemStateChanged(ItemEvent source) {
				if(source.getItem()==igmpPanel.getCb_sourceIPaddress().getSelectedItem()){
					if(igmpPanel.getCb_sourceIPaddress().getSelectedIndex()==0){
						igmpPanel.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE, BorderType.NEUTRAL));
						state[1][5] = false;
					} else {
						igmpPanel.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE, BorderType.TRUE));
						state[1][5] = true;
						}
				} else if(source.getItem()==mmrpPanel.getCb_sourceIPaddress().getSelectedItem()){
					if(mmrpPanel.getCb_sourceIPaddress().getSelectedIndex()==0){
						mmrpPanel.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.MMRPSOURCE, BorderType.NEUTRAL));
						state[0][5] = false;
					} else {
						mmrpPanel.getPan_sourceIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.MMRPSOURCE, BorderType.TRUE));
						state[0][5] = true;
						}
				}
				checkState();
			}
		};
		private DocumentListener dl = new DocumentListener(){

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				insertUpdate(arg0);
			}

			@Override
			public void insertUpdate(DocumentEvent source) {
				if(source.getDocument() == igmpPanel.getTf_groupIPaddress().getDocument()){
					docEventTFgrp(igmpPanel);
				}
				//KEY Event in IPv4 Sender - GroupAddress
				if(source.getDocument() == mmrpPanel.getTf_groupIPaddress().getDocument()){
					docEventTFgrp(mmrpPanel);
				}
				//KEY Event in IPv4 Sender - UDP Port
				else if(source.getDocument() == igmpPanel.getTf_udp_port().getDocument()){
					docEventTFport(igmpPanel);
				}
				//KEY Event in IPv4 Sender - TTL
				else if(source.getDocument() == igmpPanel.getTf_ttl().getDocument()){
					docEventTFttl(igmpPanel);
				}
				//KEY Event in IPv4 Sender - PacketRate
				else if(source.getDocument() == igmpPanel.getTf_packetrate().getDocument()){
					docEventTFrate(igmpPanel);
				}
				//KEY Event in IPv4 Sender - PacketLength
				else if(source.getDocument() == igmpPanel.getTf_udp_packetlength().getDocument()){
					docEventTFlength(igmpPanel);
				}
				//KEY Event in IPv4 Sender - PacketRate
				else if(source.getDocument() == mmrpPanel.getTf_packetrate().getDocument()){
					docEventTFrate(mmrpPanel);
				}
				//KEY Event in IPv4 Sender - PacketLength
				else if(source.getDocument() == mmrpPanel.getTf_udp_packetlength().getDocument()){
					docEventTFlength(mmrpPanel);
				}
				checkState();
			}
			/**
			 * Funktion welche aufgerufen wird wenn der Input des Packet Rate Felds ge?ndert wurde.
			 * @param typ Programmteil in welchem das Packet Rate Feld ge?ndert wurde.
			 */
			private void docEventTFrate(PanelMulticastConfig panel){
				if(InputValidator.checkPacketRate(panel.getTf_packetrate().getText())> 0){
					panel.getPan_packetrate()
					.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.TRUE));
					if(panel.getTyp() == Typ.SENDER_MMRP) {
						state[0][0] = true;
					} else if(panel.getTyp() == Typ.SENDER_V4 || panel.getTyp() == Typ.SENDER_V6) {
						state[1][0] = true;
					}
				}
				else{
					panel.getPan_packetrate()
					.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.FALSE));
					if(panel.getTyp() == Typ.SENDER_MMRP) {
						state[0][0] = false;
					} else if(panel.getTyp() == Typ.SENDER_V4 || panel.getTyp() == Typ.SENDER_V6) {
						state[1][0] = false;
					}
					
				}
				if(panel.getTf_packetrate().getText().equalsIgnoreCase("")){ //$NON-NLS-1$
					panel.getPan_packetrate()
					.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.NEUTRAL));
					if(panel.getTyp() == Typ.SENDER_MMRP) {
						state[0][0] = false;
					} else if(panel.getTyp() == Typ.SENDER_V4 || panel.getTyp() == Typ.SENDER_V6) {
						state[1][0] = false;
					}
				}

			}
			/**
			 * Funktion welche aufgerufen wird wenn der Input des Packet Length Felds ge?ndert wurde.
			 * @param typ Programmteil in welchem das Packet Length Feld ge?ndert wurde.
			 */
			private void docEventTFlength(PanelMulticastConfig panel){
				Typ typ = panel.getTyp();
				if(typ==null||Typ.isIP(typ)){
					if(InputValidator.checkIPv4PacketLength(panel.getTf_udp_packetlength().getText())> 0){
						panel.getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.TRUE));
						state[1][1] = true;
						
					}else{
						panel.getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.FALSE));
						state[1][1] = false;
					}

				}else{
					if(InputValidator.checkMmrpPacketLength(panel.getTf_udp_packetlength().getText())> 0){
						panel.getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.TRUE));
						state[0][1] = true;
					} else{
						panel.getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.FALSE));
						state[0][1] = false;
					}
				}
				if(panel.getTf_udp_packetlength().getText().equalsIgnoreCase("")){ //$NON-NLS-1$
					panel.getPan_packetlength().setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.NEUTRAL));
					if(panel.getTyp() == Typ.SENDER_MMRP) {
						state[0][1] = false;
					} else if(panel.getTyp() == Typ.SENDER_V4 || panel.getTyp() == Typ.SENDER_V6) {
						state[1][1] = false;
					}
				}
			}
			/**
			 * Funktion welche aufgerufen wird wenn der Input des TTL Felds ge?ndert wurde.
			 * @param typ Programmteil in welchem das TTL Feld ge?ndert wurde.
			 */
			private void docEventTFttl(PanelMulticastConfig panel){
				if(InputValidator.checkTimeToLive(panel.getTf_ttl().getText())> 0){
					panel.getPan_ttl().setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.TRUE));
					if(panel.getTyp() == Typ.SENDER_MMRP) {
						state[0][2] = true;
					} else if(panel.getTyp() == Typ.SENDER_V4 || panel.getTyp() == Typ.SENDER_V6) {
						state[1][2] = true;
					}
				}
				else{
					panel.getPan_ttl().setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.FALSE));
					if(panel.getTyp() == Typ.SENDER_MMRP) {
						state[0][2] = false;
					} else if(panel.getTyp() == Typ.SENDER_V4 || panel.getTyp() == Typ.SENDER_V6) {
						state[1][2] = false;
					}
				}
				if(panel.getTf_ttl().getText().equalsIgnoreCase("")){ //$NON-NLS-1$
					panel.getPan_ttl()
					.setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.NEUTRAL));
					if(panel.getTyp() == Typ.SENDER_MMRP) {
						state[0][2] = false;
					} else if(panel.getTyp() == Typ.SENDER_V4 || panel.getTyp() == Typ.SENDER_V6) {
						state[1][2] = false;
					}
				}		
			}

			private void docEventTFport(PanelMulticastConfig panel){
				if(InputValidator.checkPort(panel.getTf_udp_port().getText()) > 0){
					panel.getPan_udp_port().setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.TRUE));
					if(panel.getTyp() == Typ.SENDER_MMRP) {
						state[0][3] = true;
					} else if(panel.getTyp() == Typ.SENDER_V4 || panel.getTyp() == Typ.SENDER_V6) {
						state[1][3] = true;
					}
				}
				else{
					panel.getPan_udp_port().setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.FALSE));
					if(panel.getTyp() == Typ.SENDER_MMRP) {
						state[0][3] = false;
					} else if(panel.getTyp() == Typ.SENDER_V4 || panel.getTyp() == Typ.SENDER_V6) {
						state[1][3] = false;
					}
				}
				if(panel.getTf_udp_port().getText().equalsIgnoreCase("")){ //$NON-NLS-1$
					panel.getPan_udp_port()
					.setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.NEUTRAL));
					if(panel.getTyp() == Typ.SENDER_MMRP) {
						state[0][3] = false;
					} else if(panel.getTyp() == Typ.SENDER_V4 || panel.getTyp() == Typ.SENDER_V6) {
						state[1][3] = false;
					}
				}

			}

			private void docEventTFgrp(PanelMulticastConfig panel) {
				Typ typ = panel.getTyp();
				String groupAddress=panel.getTf_groupIPaddress().getText();
				if(Typ.isV4(typ) || Typ.isV6(typ) || typ == null){ //Wenn aus dem IGMP Panel
					//Wenn g�ltige IPv6 Adresse eingegeben wird
					if(InputValidator.checkMC_IPv6(groupAddress)!= null){	
						panel.getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv6GROUP, BorderType.TRUE));
						panel.switchTo(Typ.SENDER_V6);
						state[1][4] = true;
					} else if(InputValidator.checkMC_IPv4(groupAddress)!= null){	
						panel.getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.TRUE));
						panel.switchTo(Typ.SENDER_V4);
						state[1][4] = true;
					}
					else if(groupAddress.equalsIgnoreCase("")){ //$NON-NLS-1$
						panel.getPan_groupIPaddress()
						.setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.NEUTRAL));
						state[1][4] = false;
					}
					else{
						panel.getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.FALSE));
						state[1][4] = false;
					}
				}
				else if(Typ.isMMRP(typ)){
					if(InputValidator.checkMC_MMRP(groupAddress)){
						panel.getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.MMRPGROUP, BorderType.TRUE));
						state[0][4] = true;
					}else if(groupAddress.equalsIgnoreCase("")){ //$NON-NLS-1$
						panel.getPan_groupIPaddress()
						.setBorder(MiscBorder.getBorder(BorderTitle.MMRPGROUP, BorderType.NEUTRAL));
						state[0][4] = false;
					}else {
						panel.getPan_groupIPaddress().setBorder(MiscBorder.getBorder(BorderTitle.MMRPGROUP, BorderType.FALSE));
						state[0][4] = false;
					}
				}
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {}
		};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ConfigMenu dialog = new ConfigMenu(new FrameMain(new ViewController()), null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ConfigMenu(final FrameMain parent, ViewController ctrl) {
		this.parent = parent;
		this.ctrl = ctrl;
		state[0][2] = true;
		state[0][3] = true;
		setBounds(100, 100, 327, 364);
		getContentPane().setLayout(null);
		setIconImage(parent.getIconImage());
		setTitle(Messages.getString("ConfigMenu.0")); //$NON-NLS-1$
		setResizable(false);
		setBounds(100, 100, 327, 385);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(null);
		setModal(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		setVisible(true);
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 317, 321, 40);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						saveDefaults(igmpPanel);
						saveDefaults(mmrpPanel);
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		contentPanel.setBounds(0, 0, 321, 318);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JPanel configPanel = new JPanel();
		configPanel.setBounds(10, 41, 301, 277);
		contentPanel.add(configPanel);
		configPanel.setBorder(new TitledBorder(Messages.getString("ConfigMenu.5")));
		configPanel.setLayout(null);
								
		JLabel label = new JLabel("Language");
		label.setBounds(10, 28, 92, 19);
		configPanel.add(label);
										
		comboBox = new JComboBox();
		comboBox.setBounds(83, 27, 64, 20);
		configPanel.add(comboBox);
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Englisch", "Deutsch"}));
		
		final String language = ctrl.getUserInputData().getLanguage();
		if(language.equals("en")) {
			comboBox.setSelectedIndex(0);
		} else if(language.equals("de")){
			comboBox.setSelectedIndex(1);
		}
		
		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setBounds(10, 58, 282, 207);
		configPanel.add(tabbedPane);
		
		mmrpPanel = new PanelMulticastConfig(ctrl,il,dl, Typ.SENDER_MMRP);
		tabbedPane.addTab("Layer 2: MMRP", mmrpPanel);
		
		igmpPanel = new PanelMulticastConfig(ctrl,il,dl, Typ.SENDER_V4);
		tabbedPane.add("Layer 3: IGMP", igmpPanel);
		
		initPanel(mmrpPanel);
		initPanel(igmpPanel);
		
		final JLabel labelRestart = new JLabel(Messages.getString("ConfigMenu.lblNewLabel.text"));
		labelRestart.setBounds(0, 0, 321, 0);
		contentPanel.add(labelRestart);
		labelRestart.setBackground(UIManager.getColor("ToolTip.background"));
		labelRestart.setHorizontalAlignment(SwingConstants.CENTER);
		comboBox.addItemListener(new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent arg0) {
			if(!((String)comboBox.getSelectedItem()).equals(language)) {
				labelRestart.setSize(new Dimension(labelRestart.getWidth(), 23));
				labelRestart.setOpaque(true);
				labelRestart.validate();
			}
		}
		});
	}
	
	public String getLanguage() {
		return this.comboBox.getSelectedItem().toString();
	}
	
	private void initPanel(PanelMulticastConfig panel) {
		UserInputData data = ctrl.getUserInputData();
		if(panel.getTyp() != null && panel.getTyp() == Typ.SENDER_MMRP) {
			panel.getTf_groupIPaddress().setText(data.getDefaultL2GroupAddress());
			panel.getTf_packetrate().setText(data.getDefaultL2PacketRate() + "");
			panel.getTf_sourceAddress().setSelectedIndex(NetworkAdapter.findAddressIndex(Typ.SENDER_MMRP, data.getDefaultL2InterfaceAddress())+1);
			panel.getTf_udp_packetlength().setText(data.getDefaultL2PacketLength() + "");
		} else {
			panel.getTf_groupIPaddress().setText(data.getDefaultL3GroupAddress());
			panel.getTf_packetrate().setText(data.getDefaultL3PacketRate() + "");
			if(data.getDefaultL3InterfaceAddress().contains(".")) {
				panel.getTf_sourceAddress().setSelectedIndex(NetworkAdapter.findAddressIndex(Typ.SENDER_V4, data.getDefaultL3InterfaceAddress())+1);
			} else {
				panel.getTf_sourceAddress().setSelectedIndex(NetworkAdapter.findAddressIndex(Typ.SENDER_V6, data.getDefaultL3InterfaceAddress())+1);
			}
			panel.getTf_udp_packetlength().setText(data.getDefaultL3PacketLength() + "");
			panel.getTf_udp_port().setText(data.getDefaultL3UdpPort() + "");
			panel.getTf_ttl().setText(data.getDefaultL3TTL() + "");
		}
	}
	
	public void saveDefaults(PanelMulticastConfig panel) {
		String language = comboBox.getSelectedItem().toString().substring(0, 2).toLowerCase();
		ctrl.getUserInputData().setLanguage(language);
		if (panel.getTyp() != null && panel.getTyp().equals(Typ.SENDER_MMRP)) {
			ctrl.getUserInputData().setDefaultL2GroupAddress(panel.getTf_groupIPaddress().getText());
			try {
				ctrl.getUserInputData().setDefaultL2InterfaceAddress(NetworkAdapter.byteArrayToMac(panel.getSelectedAddress().getHardwareAddress()));
			} catch (IOException e) {}
			if(!panel.getTf_udp_packetlength().getText().equals("")) {
				ctrl.getUserInputData().setDefaultL2PacketLength(Integer.parseInt(panel.getTf_udp_packetlength().getText()));
			} else {
				ctrl.getUserInputData().setDefaultL2PacketLength(0);
			}
			if(!panel.getTf_packetrate().equals("")) {
				ctrl.getUserInputData().setDefaultL2PacketRate(Integer.parseInt(panel.getTf_packetrate().getText()));
			} else {
				ctrl.getUserInputData().setDefaultL2PacketRate(0);
			}
		} else {
			ctrl.getUserInputData().setLanguage(language);
			ctrl.getUserInputData().setDefaultL3GroupAddress(panel.getTf_groupIPaddress().getText());
			
			ctrl.getUserInputData().setDefaultL3InterfaceAddress(panel.getSelectedAddress(Typ.SENDER_V4).getHostAddress());
			if(!panel.getTf_ttl().getText().equals("")) {
				ctrl.getUserInputData().setDefaultL3TTL(Integer.parseInt(panel.getTf_ttl().getText()));
			} else {
				ctrl.getUserInputData().setDefaultL3TTL(0);
			}
			if(!panel.getTf_udp_port().getText().equals("")) {
				ctrl.getUserInputData().setDefaultL3UdpPort(Integer.parseInt(panel.getTf_udp_port().getText()));
			} else {
				ctrl.getUserInputData().setDefaultL3UdpPort(0);
			}
			if(!panel.getTf_udp_packetlength().getText().equals("")) {
				ctrl.getUserInputData().setDefaultL3PacketLength(Integer.parseInt(panel.getTf_udp_packetlength().getText()));
			} else {
				ctrl.getUserInputData().setDefaultL3PacketLength(0);
			}
			if(!panel.getTf_packetrate().getText().equals("")) {
				ctrl.getUserInputData().setDefaultL3PacketRate(Integer.parseInt(panel.getTf_packetrate().getText()));
			} else {
				ctrl.getUserInputData().setDefaultL3PacketRate(0);
			}
		}
	}
	
	public static void showDialog(FrameMain parent, ViewController ctrl) {
		ConfigMenu d = new ConfigMenu(parent, ctrl);
		d.setVisible(true);
	}
	
	private void checkState() {
		boolean ret = true;
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 6; j++) {
				if(!state[i][j]) {
					ret = false;
				}
			}
		}
		okButton.setEnabled(ret);
	}
}
