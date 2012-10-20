package program.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.DocumentListener;

import org.jnetpcap.PcapIf;

import program.controller.Messages;
import program.controller.ViewController;
import program.data.MulticastData.Typ;
import program.model.InputValidator;
import program.model.NetworkAdapter;
import program.view.MiscBorder.BorderTitle;
import program.view.MiscBorder.BorderType;
/**
 * Das KonfigurationPanel f�r Multicasts (links unten im Programm).
 * Dient zum Einstellen und Erstellen von Multicast Sendern und Receivern
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class PanelMulticastConfig extends JPanel {
	/**
	 * Dieser FocusListener makiert den Inhalt eines Textfeldes komplett wenn es im Fokus ist ( angeklickt oder hineingetabbt)
	 * Wenn der Focus wegfällt wird wieder demakiert
	 */
	private FocusListener focusL = new FocusListener() {

		@Override
		public void focusLost(FocusEvent e) {
			if(e.getComponent() instanceof JTextField){
				((JTextField)e.getComponent()).setSelectionStart(0);
				((JTextField)e.getComponent()).setSelectionEnd(0);
			}

		}

		@Override
		public void focusGained(FocusEvent e) {
			if(e.getComponent() instanceof JTextField){
				((JTextField)e.getComponent()).setSelectionStart(0);
				((JTextField)e.getComponent()).setSelectionEnd(((JTextField) e.getComponent()).getText().length());
			}

		}
	};
	private JTextField tf_groupIPaddress;
	private WideComboBox cb_sourceIPaddress;
	private JTextField tf_udp_port;
	private JTextField tf_ttl;
	private JTextField tf_udp_packetlength;
	private JTextField tf_packetrate;
	//private JSlider;
	private JPanel pan_groupIPaddress;
	private JPanel pan_sourceIPaddress;
	private JPanel pan_udp_port;
	private JPanel pan_ttl;
	private JPanel pan_packetlength;
	private JPanel pan_packetrate;
	private JButton bt_add;
	private JButton bt_change;
	private Typ typ;
	DocumentListener dl;
	ItemListener il;

	public WideComboBox getCb_sourceIPaddress() {
		return cb_sourceIPaddress;
	}
	public void setCb_sourceIPaddress(WideComboBox cd_souWideComboBox) {
		cb_sourceIPaddress = cd_souWideComboBox;
	}
	public PanelMulticastConfig(ViewController ctrl,ItemListener il,DocumentListener dl, Typ typ){
		this.dl = dl;
		this.il = il; 
		if(Typ.isMMRP(typ)){
//			System.out.println("Typ");
			this.typ=typ;
		}
		else{
			this.typ=null;
		}
		setBorder(new MiscBorder(Messages.getString("PanelMulticastConfig.0"))); //$NON-NLS-1$
		setLayout(null);
		setPreferredSize(new Dimension(225, 215));
		createAddressFields(ctrl, typ); //GUI Elements for IPv4
		createGUIstandard(typ);	
		
		
	}
	/**
	 * Konstruktor welcher das komplette Configuration Panel initialisiert
	 * @param ctrl Ben�tigte Referenz zum GUI Controller.
	 * @param typ Gibt an zu welchem Programmteil das Panel geh�rt.
	 */
	public PanelMulticastConfig(ViewController ctrl, Typ typ){
		this.il = ctrl;
		if(Typ.isMMRP(typ)){
			this.typ=typ;
		}
		else{
			this.typ=null;
		}
		dl = ctrl;
		setBorder(new MiscBorder(Messages.getString("PanelMulticastConfig.0"))); //$NON-NLS-1$
		setLayout(null);
		setPreferredSize(new Dimension(225, 215));
		createAddressFields(ctrl, typ); //GUI Elements for IPv4
		createGUIstandard(typ);

	}
	/**
	 * Initialisiert die Standard Textfelder des KonfigurationsPanels
	 * @param typ Gibt an zu welchem Programmteil das Panel geh�rt.
	 */
	private void createGUIstandard(Typ typ) {
		add(bt_add);
		add(bt_change);
		add(pan_groupIPaddress);
		add(pan_udp_port);
		if(typ==Typ.SENDER_V4 || typ==Typ.SENDER_V6 || typ==Typ.SENDER_MMRP){
			add(pan_packetrate);
			add(pan_packetlength);
			if(typ==Typ.SENDER_V4 || typ==Typ.SENDER_V6)
				add(pan_ttl);
		}
	}
	/**
	 * Funktion welche die spezifischen Textfelder f�r einen Programmteil erstellt.
	 * @param ctrl Ben�tigte Referenz zum GUI Controller.ctrl
	 * @param typ Gibt an zu welchem Programmteil das Panel geh�rt.
	 */
	private void createAddressFields(ViewController ctrl, Typ typ) {
		pan_groupIPaddress=new JPanel();
		pan_sourceIPaddress=new JPanel();
		pan_udp_port=new JPanel();
		bt_add = new JButton(Messages.getString("PanelMulticastConfig.2")); //$NON-NLS-1$
		bt_add.addActionListener(ctrl);
		bt_add.setBounds(120,180,95,25);
		bt_add.setFont(MiscFont.getFont(0,12));
		bt_add.setEnabled(false);
		bt_add.setMargin(new Insets(0, 0, 0, 0));
		bt_change = new JButton(Messages.getString("PanelMulticastConfig.3")); //$NON-NLS-1$
		bt_change.addActionListener(ctrl);
		bt_change.setBounds(10,180,95,25);
		bt_change.setFont(MiscFont.getFont(0,12));
		bt_change.setEnabled(false);
		bt_change.setMargin(new Insets(0, 0, 0, 0));

		cb_sourceIPaddress = new WideComboBox();
		if(Typ.isMMRP(typ)){
			cb_sourceIPaddress.addItem(""); //$NON-NLS-1$
		} else{
			cb_sourceIPaddress.addItem(Messages.getString("PanelMulticastConfig.5")); //$NON-NLS-1$
		}
		cb_sourceIPaddress.setBounds(5,15,205,20);
		cb_sourceIPaddress.setFont(MiscFont.getFont(0,12));
		cb_sourceIPaddress.setBorder(null);
		((JLabel) cb_sourceIPaddress.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
		cb_sourceIPaddress.addItemListener(il);
		if(Typ.isMMRP(typ)){
			Vector<PcapIf> temp = NetworkAdapter.getMmrpAdapters();
			for (int i = 0; i < temp.size(); i++) {
					try {						
						if(System.getProperty("os.name").contains("Windows")){
							cb_sourceIPaddress.addItem(
									temp.get(i).getDescription()
									+ "   " //$NON-NLS-1$
									+ NetworkAdapter.byteArrayToMac(temp.get(i).getHardwareAddress()));
							
						} else {
							cb_sourceIPaddress.addItem(
									temp.get(i).getName()
									+ "   " //$NON-NLS-1$
									+ NetworkAdapter.byteArrayToMac(temp.get(i).getHardwareAddress()));
							
						}
					} catch (IOException e) {
						// 
						e.printStackTrace();
					}
			}
		}
		cb_sourceIPaddress.setToolTipText(Messages.getString("PanelMulticastConfig.sourceAddress"));
		pan_sourceIPaddress.add(cb_sourceIPaddress,BorderLayout.CENTER);
		add(pan_sourceIPaddress);

		if(typ==Typ.SENDER_V4 || typ==Typ.SENDER_V6 ||typ== Typ.SENDER_MMRP){
			pan_packetrate=new JPanel();
			pan_packetlength=new JPanel();
			pan_packetrate.setLayout(null);
			pan_packetlength.setLayout(null);
			pan_packetrate.setBounds(5,135,105,40);
			pan_packetlength.setBounds(115,135,105,40);
			pan_packetrate.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.NEUTRAL));
			pan_packetlength.setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.NEUTRAL));

			tf_udp_packetlength = new JTextField();
			tf_packetrate = new JTextField();
			tf_udp_packetlength.setBounds(5,15,95,20);
			tf_packetrate.setBounds(5,15,95,20);
			tf_packetrate.setBorder(null);
			tf_udp_packetlength.setBorder(null);
			tf_packetrate.getDocument().addDocumentListener(dl);
			tf_udp_packetlength.getDocument().addDocumentListener(dl);
			tf_packetrate.setFont(MiscFont.getFont(0,14));
			tf_udp_packetlength.setFont(MiscFont.getFont(0,14));
			tf_udp_packetlength.setHorizontalAlignment(SwingConstants.CENTER);
			tf_packetrate.setHorizontalAlignment(SwingConstants.CENTER);
			tf_packetrate.addFocusListener(focusL);
			tf_udp_packetlength.addFocusListener(focusL);		
			tf_udp_packetlength.setToolTipText(Messages.getString("PanelMulticastConfig.packetlength")); //$NON-NLS-1$
			tf_packetrate.setToolTipText(Messages.getString("PanelMulticastConfig.packetrate")); //$NON-NLS-1$
			pan_packetlength.add(tf_udp_packetlength,BorderLayout.CENTER);
			pan_packetrate.add(tf_packetrate,BorderLayout.CENTER);
			if(typ!=Typ.SENDER_MMRP){
				pan_ttl=new JPanel();
				pan_ttl.setLayout(null);
				pan_ttl.setBounds(115,95,105,40);
				pan_ttl.setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.NEUTRAL));
				tf_ttl = new JTextField();
				tf_ttl.setBounds(5,15,95,20);
				tf_ttl.setBorder(null);
				tf_ttl.getDocument().addDocumentListener(dl);
				tf_ttl.setFont(MiscFont.getFont(0,14));
				tf_ttl.setHorizontalAlignment(SwingConstants.CENTER);
				tf_ttl.addFocusListener(focusL);
				tf_ttl.setToolTipText(Messages.getString("PanelMulticastConfig.ttl")); //$NON-NLS-1$
				pan_ttl.add(tf_ttl,BorderLayout.CENTER);
			}

		}

		pan_udp_port.setBounds(5,95,105,40);
		pan_groupIPaddress.setLayout(null);
		pan_sourceIPaddress.setLayout(null);
		pan_sourceIPaddress.setBounds(5,55,215,40);
		pan_udp_port.setLayout(null);

		if(typ == Typ.SENDER_V4 || typ==Typ.RECEIVER_V4 ||typ == Typ.SENDER_V6 || typ==Typ.RECEIVER_V6){
			pan_udp_port.setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.NEUTRAL));}
		pan_groupIPaddress.setBounds(5,15,215,40);
		if(typ == Typ.SENDER_V4 || typ==Typ.RECEIVER_V4){
			pan_groupIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.NEUTRAL));
			pan_sourceIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE, BorderType.NEUTRAL));}
		else{
			pan_groupIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.MMRPGROUP, BorderType.NEUTRAL));
			pan_sourceIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.MMRPSOURCE, BorderType.NEUTRAL));
		}

		tf_groupIPaddress = new JTextField();
		tf_groupIPaddress.setToolTipText(Messages.getString("PanelMulticastConfig.1")); //$NON-NLS-1$
		
		if(Typ.isIP(typ)){
			tf_udp_port = new JTextField();
			tf_udp_port.setToolTipText(Messages.getString("PanelMulticastConfig.udp"));
			pan_udp_port.add(tf_udp_port,BorderLayout.CENTER);
			tf_udp_port.setBounds(5,15,95,20);
			tf_udp_port.setFont(MiscFont.getFont(0,14));
			tf_udp_port.setHorizontalAlignment(SwingConstants.CENTER);
			tf_udp_port.setBorder(null);
			tf_udp_port.getDocument().addDocumentListener(dl);
			tf_udp_port.addFocusListener(focusL);
		}
		//cb_sourceIPaddress.setPreferredSize(new Dimension(500,20));
		tf_groupIPaddress.setBounds(5,15,205,20);
		tf_groupIPaddress.setFont(MiscFont.getFont(0,14));
		tf_groupIPaddress.setHorizontalAlignment(SwingConstants.CENTER);
//		cb_sourceIPaddress.setHorizontalAlignment(JTextField.CENTER);
		tf_groupIPaddress.setBorder(null);
		tf_groupIPaddress.getDocument().addDocumentListener(dl);
		tf_groupIPaddress.addFocusListener(focusL);

//		cb_sourceIPaddress.getDocument().addDocumentListener(dl);
		pan_groupIPaddress.add(tf_groupIPaddress,BorderLayout.CENTER);

	}
	public Typ getTyp(){
		return this.typ;
	}
	/**
	 * Diese Funktion ändert die Dropdown Combo Box
	 * @param typ
	 */
	public void switchTo(Typ typ){
		if(typ==this.typ)
			return;
		this.typ=typ;
		if(Typ.isV4(typ)){
			cb_sourceIPaddress.removeAllItems();
			cb_sourceIPaddress.addItem(""); //$NON-NLS-1$
			Vector<InetAddress> temp = NetworkAdapter.getipv4Adapters();
			for (int i = 0; i < temp.size(); i++) {
				try {
					cb_sourceIPaddress.addItem(
							NetworkInterface.getByInetAddress(temp.get(i))
							.getDisplayName()
							+ "   " //$NON-NLS-1$
							+ temp.get(i).toString().substring(1).split("%")[0]); //$NON-NLS-1$
				} catch (SocketException e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (Typ.isV6(typ)) {
			cb_sourceIPaddress.removeAllItems();
			cb_sourceIPaddress.addItem(""); //$NON-NLS-1$
			Vector<InetAddress> temp = NetworkAdapter.getipv6Adapters();
			for (int i = 0; i < temp.size(); i++) {
				try {
					cb_sourceIPaddress.addItem(
							NetworkInterface.getByInetAddress(temp.get(i))
							.getDisplayName()
							+ "   " //$NON-NLS-1$
							+ temp.get(i).toString().substring(1).split("%")[0]); //$NON-NLS-1$
				} catch (SocketException e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else if(typ==null){
			cb_sourceIPaddress.removeAllItems();
			cb_sourceIPaddress.addItem(""); //$NON-NLS-1$
		}

	}

	public JPanel getPan_udp_port() {
		return pan_udp_port;
	}

	public JPanel getPan_ttl() {
		return pan_ttl;
	}

	public JPanel getPan_packetlength() {
		return pan_packetlength;
	}

	public JPanel getPan_packetrate() {
		return pan_packetrate;
	}

	public JPanel getPan_groupIPaddress() {
		return pan_groupIPaddress;
	}

	public JPanel getPan_sourceIPaddress() {
		return pan_sourceIPaddress;
	}

	public JTextField getTf_groupIPaddress() {
		return tf_groupIPaddress;
	}

	public JComboBox getTf_sourceAddress() {
		return cb_sourceIPaddress;
	}
	public JTextField getTf_udp_port() {
		return tf_udp_port;
	}

	public JTextField getTf_ttl() {
		return tf_ttl;
	}

	public JTextField getTf_udp_packetlength() {
		return tf_udp_packetlength;
	}

	public JTextField getTf_packetrate() {
		return tf_packetrate;
	}

	public JButton getBt_add() {
		return bt_add;
	}

	public void setTf_groupIPaddress(JFormattedTextField tfGroupIPaddress) {
		tf_groupIPaddress = tfGroupIPaddress;
	}
	public void setTf_udp_port(JTextField tfUdpPort) {
		tf_udp_port = tfUdpPort;
	}

	public void setTf_ttl(JTextField tfTtl) {
		tf_ttl = tfTtl;
	}

	public void setTf_udp_packetlength(JTextField tfUdpPacketlength) {
		tf_udp_packetlength = tfUdpPacketlength;
	}

	public void setTf_packetrate(JTextField tfPacketrate) {
		tf_packetrate = tfPacketrate;
	}
	public void setBt_add(JButton btAdd) {
		bt_add = btAdd;
	}
	public String getSourceIP(Typ typ, int i){
		if(typ==Typ.SENDER_V4 || typ==Typ.RECEIVER_V4){
			return NetworkAdapter.getipv4Adapters().get(i).toString().substring(1);
		}
		else{
			return NetworkAdapter.getipv6Adapters().get(i).toString().substring(1).split("%")[0]; //$NON-NLS-1$
		}
	}
	public InetAddress getSelectedAddress(Typ typ){
		if(typ==Typ.SENDER_V4 || typ==Typ.RECEIVER_V4){
			return InputValidator.checkIPv4(getSourceIP(typ, cb_sourceIPaddress.getSelectedIndex()-1));
		}
		else if(Typ.isV6(typ)){
			return InputValidator.checkIPv6(getSourceIP(typ, cb_sourceIPaddress.getSelectedIndex()-1));
		} else
			return null;

	}
	/**
	 * Gibt Pcap Interface für MMRP zurück
	 * @return
	 */
	public PcapIf getSelectedAddress(){
		return NetworkAdapter.getMmrpAdapters().get(cb_sourceIPaddress.getSelectedIndex()-1);
	}
	public int getSelectedSourceIndex(){
		return cb_sourceIPaddress.getSelectedIndex();
	}
	public JButton getBt_change() {
		return bt_change;
	}
}


