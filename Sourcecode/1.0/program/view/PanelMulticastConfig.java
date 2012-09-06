package zisko.multicastor.program.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Vector;

import javax.swing.*;

import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.model.InputValidator;
import zisko.multicastor.program.model.NetworkAdapter;
import zisko.multicastor.program.view.MiscBorder.BorderTitle;
import zisko.multicastor.program.view.MiscBorder.BorderType;
/**
 * Das KonfigurationPanel für Multicasts (links unten im Programm).
 * Dient zum Einstellen und Erstellen von Multicast Sendern und Receivern
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class PanelMulticastConfig extends JPanel {
	private JTextField tf_groupIPaddress;
	private WideComboBox cb_sourceIPaddress;
	private JSlider sl_ttl;
	private JSlider sl_port;
	private JSlider sl_length;
	private JSlider sl_rate;
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
	private JToggleButton tb_active;
	private JButton bt_enter;
	
	public WideComboBox getCb_sourceIPaddress() {
		return cb_sourceIPaddress;
	}
	/**
	 * Konstruktor welcher das komplette Configuration Panel initialisiert
	 * @param ctrl Benötigte Referenz zum GUI Controller.
	 * @param typ Gibt an zu welchem Programmteil das Panel gehört.
	 */
	public PanelMulticastConfig(ViewController ctrl, Typ typ){ 
		setBorder(new MiscBorder("MultiCast Configuration"));
		setLayout(null);
		setPreferredSize(new Dimension(225, 215));
		createAddressFields(ctrl, typ); //GUI Elements for IPv4
		createGUIstandard(typ);

	}
	/**
	 * Initialisiert die Standard Textfelder des KonfigurationsPanels
	 * @param typ Gibt an zu welchem Programmteil das Panel gehört.
	 */
	private void createGUIstandard(Typ typ) {
		tf_groupIPaddress.setToolTipText("Enter MultiCast group IP address here!");
		add(bt_enter);
		add(pan_groupIPaddress);
		add(pan_udp_port);
		add(tb_active);
		if(typ==Typ.SENDER_V4 || typ==Typ.SENDER_V6){
			add(pan_packetrate);
			add(pan_packetlength);
			add(pan_ttl);
		}
	}
	/**
	 * Funktion welche die spezifischen Textfelder für einen Programmteil erstellt.
	 * @param ctrl Benötigte Referenz zum GUI Controller.
	 * @param typ Gibt an zu welchem Programmteil das Panel gehört.
	 */
	private void createAddressFields(ViewController ctrl, Typ typ) {
		Vector<InetAddress> temp = null;
		tb_active = new JToggleButton("inactive");
		tb_active.setForeground(Color.red);
		tb_active.setFont(MiscFont.getFont(0,12));
		pan_groupIPaddress=new JPanel();
		pan_sourceIPaddress=new JPanel();
		pan_udp_port=new JPanel();
		bt_enter = new JButton("Add");
		bt_enter.addActionListener(ctrl);
		bt_enter.setBounds(120,180,95,25);
		bt_enter.setFont(MiscFont.getFont(0,12));
		bt_enter.setEnabled(false);
		if(typ==Typ.SENDER_V4 || typ==Typ.SENDER_V6){
			pan_packetrate=new JPanel();
			pan_packetlength=new JPanel();
			pan_ttl=new JPanel();
			pan_packetrate.setLayout(null);
			pan_packetlength.setLayout(null);
			pan_ttl.setLayout(null);
			pan_packetrate.setBounds(5,135,105,40);
			pan_packetlength.setBounds(115,135,105,40);
			pan_ttl.setBounds(115,95,105,40);
			pan_packetrate.setBorder(MiscBorder.getBorder(BorderTitle.RATE, BorderType.NEUTRAL));
			pan_packetlength.setBorder(MiscBorder.getBorder(BorderTitle.LENGTH, BorderType.NEUTRAL));
			pan_ttl.setBorder(MiscBorder.getBorder(BorderTitle.TTL, BorderType.NEUTRAL));
			tf_udp_packetlength = new JTextField();
			tf_packetrate = new JTextField();
			tf_ttl = new JTextField();
			tf_udp_packetlength.setBounds(5,15,95,20);
			tf_ttl.setBounds(5,15,95,20);
			tf_packetrate.setBounds(5,15,95,20);
			tf_packetrate.setBorder(null);
			tf_ttl.setBorder(null);
			tf_udp_packetlength.setBorder(null);
			tf_packetrate.getDocument().addDocumentListener(ctrl);
			tf_udp_packetlength.getDocument().addDocumentListener(ctrl);
			tf_ttl.getDocument().addDocumentListener(ctrl);
			tf_ttl.setFont(MiscFont.getFont(0,14));
			tf_packetrate.setFont(MiscFont.getFont(0,14));
			tf_udp_packetlength.setFont(MiscFont.getFont(0,14));
			tf_udp_packetlength.setHorizontalAlignment(JTextField.CENTER);
			tf_ttl.setHorizontalAlignment(JTextField.CENTER);
			tf_packetrate.setHorizontalAlignment(JTextField.CENTER);
			pan_packetlength.add(tf_udp_packetlength,BorderLayout.CENTER);
			pan_ttl.add(tf_ttl,BorderLayout.CENTER);
			pan_packetrate.add(tf_packetrate,BorderLayout.CENTER);
			cb_sourceIPaddress = new WideComboBox();
			cb_sourceIPaddress.addItem("");
			cb_sourceIPaddress.setBounds(5,15,205,20);
			cb_sourceIPaddress.setFont(MiscFont.getFont(0,12));
			cb_sourceIPaddress.setBorder(null);
			ListCellRenderer renderer = cb_sourceIPaddress.getRenderer();
			((JLabel) renderer).setHorizontalAlignment(SwingConstants.CENTER);
			cb_sourceIPaddress.addItemListener(ctrl);
			if(typ == Typ.SENDER_V4){
				temp = NetworkAdapter.getipv4Adapters();
			}else{
				temp = NetworkAdapter.getipv6Adapters();
			}
			for(int i = 0 ; i < temp.size(); i++){
				if(typ == Typ.SENDER_V4){
					try {
						cb_sourceIPaddress.addItem(temp.get(i).toString().substring(1)+"   "+NetworkInterface.getByInetAddress(temp.get(i)).getDisplayName());
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
							//(0)temp.get(i).toString().substring(1));
				}
				else if(typ == Typ.SENDER_V6){
					try {
						cb_sourceIPaddress.addItem(temp.get(i).toString().substring(1).split("%")[0]+"   "+NetworkInterface.getByInetAddress(temp.get(i)).getDisplayName());
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			pan_sourceIPaddress.add(cb_sourceIPaddress,BorderLayout.CENTER);
			add(pan_sourceIPaddress);
			pan_udp_port.setBounds(5,95,105,40);
			
		}
		else{
			pan_udp_port.setBounds(5,55,105,40);
		}
		pan_groupIPaddress.setLayout(null);
		pan_sourceIPaddress.setLayout(null);
		pan_sourceIPaddress.setBounds(5,55,215,40);
		pan_udp_port.setLayout(null);
		pan_udp_port.setBorder(MiscBorder.getBorder(BorderTitle.PORT, BorderType.NEUTRAL));
		pan_groupIPaddress.setBounds(5,15,215,40);
		tb_active.setBounds(10,180,95,25);
		tb_active.setFocusable(false);
		tb_active.addActionListener(ctrl);	
		
		if(typ == Typ.SENDER_V4 || typ==Typ.RECEIVER_V4){ 
			pan_groupIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.IPv4GROUP, BorderType.NEUTRAL));
			pan_sourceIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.IPv4SOURCE, BorderType.NEUTRAL));
		}
		else{ //IPv6
			pan_groupIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.IPv6GROUP, BorderType.NEUTRAL));
			pan_sourceIPaddress.setBorder(MiscBorder.getBorder(BorderTitle.IPv6SOURCE, BorderType.NEUTRAL));
		}

		tf_udp_port = new JTextField();		
		tf_groupIPaddress = new JTextField();
		pan_udp_port.add(tf_udp_port,BorderLayout.CENTER);
		//cb_sourceIPaddress.setPreferredSize(new Dimension(500,20));
		tf_groupIPaddress.setBounds(5,15,205,20);		
		tf_udp_port.setBounds(5,15,95,20);
		tf_groupIPaddress.setFont(MiscFont.getFont(0,14));
		tf_udp_port.setFont(MiscFont.getFont(0,14));
		tf_udp_port.setHorizontalAlignment(JTextField.CENTER);
		tf_groupIPaddress.setHorizontalAlignment(JTextField.CENTER);
//		cb_sourceIPaddress.setHorizontalAlignment(JTextField.CENTER);
		tf_groupIPaddress.setBorder(null);
		tf_udp_port.setBorder(null);
		tf_groupIPaddress.getDocument().addDocumentListener(ctrl);
//		cb_sourceIPaddress.getDocument().addDocumentListener(ctrl);
		tf_udp_port.getDocument().addDocumentListener(ctrl);
		pan_groupIPaddress.add(tf_groupIPaddress,BorderLayout.CENTER);
				
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

	public JComboBox getTf_sourceIPaddress() {
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

	public JToggleButton getTb_active() {
		return tb_active;
	}

	public JButton getBt_enter() {
		return bt_enter;
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

	public void setTb_active(JToggleButton cbActive) {
		tb_active = cbActive;
	}

	public void setBt_enter(JButton btEnter) {
		bt_enter = btEnter;
	}
	public String getSourceIP(Typ typ, int i){
		if(typ==Typ.SENDER_V4 || typ==Typ.RECEIVER_V4){
			return NetworkAdapter.getipv4Adapters().get(i).toString().substring(1);
		}
		else{
			return NetworkAdapter.getipv6Adapters().get(i).toString().substring(1).split("%")[0];
		}
	}
	public InetAddress getSelectedAddress(Typ typ){
		if(typ==Typ.SENDER_V4 || typ==Typ.RECEIVER_V4){
			return InputValidator.checkIPv4(getSourceIP(typ, cb_sourceIPaddress.getSelectedIndex()-1));
		}
		else{
			return InputValidator.checkIPv6(getSourceIP(typ, cb_sourceIPaddress.getSelectedIndex()-1));
		}
		
	}
	public int getSelectedSourceIndex(){
		return cb_sourceIPaddress.getSelectedIndex();
	}
}
