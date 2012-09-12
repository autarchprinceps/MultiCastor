package zisko.multicastor.program.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.net.InetAddress;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData.Typ;
import zisko.multicastor.program.lang.LanguageManager;
import zisko.multicastor.program.model.InputValidator;
import zisko.multicastor.program.model.NetworkAdapter;
import zisko.multicastor.program.model.NetworkAdapter.IPType;
import zisko.multicastor.program.view.MiscBorder.BorderTitle;
import zisko.multicastor.program.view.MiscBorder.BorderType;

/**
 * Das KonfigurationPanel fuer Multicasts (links unten im Programm). Dient zum
 * Einstellen und Erstellen von Multicast Sendern und Receivern.
 */
@SuppressWarnings("serial")
public class PanelMulticastConfig extends JPanel {

	private JToggleButton bt_active;
	private JButton bt_enter;
	private WideComboBox cb_sourceIPaddress;
	private final ViewController ctrl;
	private final LanguageManager lang;
	private MiscBorder mainBorder;
	// private JSlider;
	private JPanel pan_groupIPaddress;
	private JPanel pan_packetlength;
	private JPanel pan_packetrate;
	private JPanel pan_sourceIPaddress;
	private JPanel pan_ttl;
	private JPanel pan_udp_port;
	private JTextField tf_groupIPaddress;
	private JTextField tf_packetrate;
	private JTextField tf_ttl;
	private JTextField tf_udp_packetlength;
	private JTextField tf_udp_port;
	private final Typ typ;

	/**
	 * Konstruktor welcher das komplette Configuration Panel initialisiert.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 * @param typ
	 *            Gibt an zu welchem Programmteil das Panel gehoert.
	 */
	public PanelMulticastConfig(final ViewController ctrl, final Typ typ) {
		this.ctrl = ctrl;
		this.typ = typ;
		lang = LanguageManager.getInstance();
		setBorder(mainBorder = new MiscBorder(
				lang.getProperty("miscBorder.mcConfig")));
		setLayout(null);
		setPreferredSize(new Dimension(225, 239));
		createAddressFields(ctrl, typ); // GUI Elements for IPv4
		createGUIstandard(typ);
		setToolTips(typ);

	}

	public JButton getBt_enter() {
		return bt_enter;
	}

	public WideComboBox getCb_sourceIPaddress() {
		return cb_sourceIPaddress;
	}

	public JPanel getPan_groupIPaddress() {
		return pan_groupIPaddress;
	}

	public JPanel getPan_packetlength() {
		return pan_packetlength;
	}

	public JPanel getPan_packetrate() {
		return pan_packetrate;
	}

	public JPanel getPan_sourceIPaddress() {
		return pan_sourceIPaddress;
	}

	public JPanel getPan_ttl() {
		return pan_ttl;
	}

	public JPanel getPan_udp_port() {
		return pan_udp_port;
	}

	public byte[] getSelectedAddress(final Typ typ) {
		return NetworkAdapter.getMacToMMRP(cb_sourceIPaddress
				.getSelectedIndex() - 1);
	}

	public InetAddress getSelectedAddress(final Typ typ, final IPType iptype) {
		// V1.5 [FH] Added L3 with IPv4 Stuff
		/* [MH] Changed to iptype */
		if((typ == Typ.L3_RECEIVER) || (typ == Typ.L3_SENDER)) {
			if(iptype == IPType.IPv4) {
				return InputValidator.checkIPv4(getSourceIP(
						cb_sourceIPaddress.getSelectedIndex() - 1, iptype));
			} else {
				return InputValidator.checkIPv6(getSourceIP(
						cb_sourceIPaddress.getSelectedIndex() - 1, iptype));
			}
		}
		// [FH] Should not happen... if type is L2 we need to call
		// getSelectedAddress(Typ typ)
		else {
			return null;
		}

	}

	public int getSelectedSourceIndex() {
		return cb_sourceIPaddress.getSelectedIndex();
	}

	public String getSourceIP(final int i, final IPType iptype) {
		if(iptype == IPType.IPv4) {
			return NetworkAdapter.getipv4Adapters().get(i).toString()
					.substring(1);
		} else if(iptype == IPType.IPv6) {
			return NetworkAdapter.getipv6Adapters().get(i).toString()
					.substring(1).split("%")[0];
		}
		return null;
	}

	public JToggleButton getTb_active() {
		return bt_active;
	}

	public JTextField getTf_groupIPaddress() {
		return tf_groupIPaddress;
	}

	public JTextField getTf_packetrate() {
		return tf_packetrate;
	}

	public JComboBox getTf_sourceIPaddress() {
		return cb_sourceIPaddress;
	}

	public JTextField getTf_ttl() {
		return tf_ttl;
	}

	public JTextField getTf_udp_packetlength() {
		return tf_udp_packetlength;
	}

	public JTextField getTf_udp_port() {
		return tf_udp_port;
	}

	/**
	 * Diese Methode aktualisiert den textuellen Inhalt, wenn die Sprache
	 * geaendert wird.
	 */
	public void reloadLanguage() {
		PanelTabbed tabpart = null;

		switch(ctrl.getSelectedTab()) {
			case L3_SENDER:
				tabpart = ctrl.getFrame().getPanel_sen_lay3();
				break;
			case L3_RECEIVER:
				tabpart = ctrl.getFrame().getPanel_rec_lay3();
				break;
			case L2_SENDER:
				tabpart = ctrl.getFrame().getPanel_sen_lay2();
				break;
			case L2_RECEIVER:
				tabpart = ctrl.getFrame().getPanel_rec_lay2();
				break;
		}

		if((tabpart != null) && (tabpart.getTable().getSelectedRowCount() == 1)) {
			bt_enter.setText(lang.getProperty("button.change"));
		} else if((tabpart != null)
				&& (tabpart.getTable().getSelectedRowCount() > 1)) {
			bt_enter.setText(lang.getProperty("button.changeAll"));
		} else {
			bt_enter.setText(lang.getProperty("button.add"));
		}

		bt_active.setText(lang.getProperty("button.inactive"));
		mainBorder.setTitle(lang.getProperty("miscBorder.mcConfig"));

		setToolTips(typ);
	}

	public void setBt_enter(final JButton btEnter) {
		bt_enter = btEnter;
	}

	public void setTb_active(final JToggleButton cbActive) {
		bt_active = cbActive;
	}

	public void setTf_groupIPaddress(final JFormattedTextField tfGroupIPaddress) {
		tf_groupIPaddress = tfGroupIPaddress;
	}

	public void setTf_packetrate(final JTextField tfPacketrate) {
		tf_packetrate = tfPacketrate;
	}

	public void setTf_ttl(final JTextField tfTtl) {
		tf_ttl = tfTtl;
	}

	public void setTf_udp_packetlength(final JTextField tfUdpPacketlength) {
		tf_udp_packetlength = tfUdpPacketlength;
	}

	public void setTf_udp_port(final JTextField tfUdpPort) {
		tf_udp_port = tfUdpPort;
	}

	/**
	 * Funktion welche die spezifischen Textfelder fuer einen Programmteil
	 * erstellt.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 * @param typ
	 *            Gibt an zu welchem Programmteil das Panel gehoert.
	 */
	private void createAddressFields(final ViewController ctrl, final Typ typ) {

		bt_active = new JToggleButton(lang.getProperty("button.inactive"));
		bt_active.setForeground(Color.red);
		bt_active.setFont(MiscFont.getFont(0, 11));
		pan_groupIPaddress = new JPanel();
		pan_sourceIPaddress = new JPanel();
		pan_udp_port = new JPanel();
		bt_enter = new JButton(lang.getProperty("button.add"));
		bt_enter.addActionListener(ctrl);
		bt_enter.setBounds(115, 204, 100, 25);
		bt_enter.setFont(MiscFont.getFont(0, 11));
		bt_enter.setEnabled(false);

		// V1.5 [FH] Use cb_sourceIPadress for all kinds
		cb_sourceIPaddress = new WideComboBox();
		if((typ == Typ.L3_RECEIVER) || (typ == Typ.L3_SENDER)) {
			cb_sourceIPaddress.addItem(lang
					.getProperty("config.message.ipFirst"));
		} else {
			cb_sourceIPaddress.addItem("");
			final Vector<String> names = NetworkAdapter.getMacAdapterNames();
			for(final String s : names) {
				cb_sourceIPaddress.addItem(s);
			}

		}
		cb_sourceIPaddress.setBounds(5, 15, 205, 20);
		cb_sourceIPaddress.setFont(MiscFont.getFont(0, 12));
		cb_sourceIPaddress.setBorder(null);
		cb_sourceIPaddress.addItemListener(ctrl);
		// ctrl.insertNetworkAdapters(typ);

		pan_sourceIPaddress.add(cb_sourceIPaddress, BorderLayout.CENTER);
		add(pan_sourceIPaddress);

		// V1.5: Layer 2 und Layer 3 Tabs hinzugefuegt: typ==Typ.L2_SENDER ||
		// typ==Typ.L3_SENDER
		if((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {

			pan_packetrate = new JPanel();
			pan_packetlength = new JPanel();
			pan_ttl = new JPanel();
			pan_packetrate.setLayout(null);
			pan_packetlength.setLayout(null);
			pan_ttl.setLayout(null);
			pan_packetrate.setBounds(5, 140, 105, 40);
			pan_packetlength.setBounds(115, 140, 105, 40);
			pan_ttl.setBounds(115, 100, 105, 40);
			pan_packetrate.setBorder(MiscBorder.getBorder(BorderTitle.RATE,
					BorderType.NEUTRAL));
			pan_packetlength.setBorder(MiscBorder.getBorder(BorderTitle.LENGTH,
					BorderType.NEUTRAL));
			pan_ttl.setBorder(MiscBorder.getBorder(BorderTitle.TTL,
					BorderType.NEUTRAL));
			tf_udp_packetlength = new JTextField();
			if((typ == Typ.L3_RECEIVER) || (typ == Typ.L3_SENDER)) {
				tf_udp_packetlength.setText(lang
						.getProperty("config.message.ipFirstShort"));
			}
			tf_packetrate = new JTextField();
			tf_ttl = new JTextField();
			tf_udp_packetlength.setBounds(5, 15, 95, 20);
			tf_ttl.setBounds(5, 15, 95, 20);
			tf_packetrate.setBounds(5, 15, 95, 20);
			tf_packetrate.setBorder(null);
			tf_ttl.setBorder(null);
			tf_udp_packetlength.setBorder(null);
			tf_packetrate.getDocument().addDocumentListener(ctrl);
			tf_udp_packetlength.getDocument().addDocumentListener(ctrl);
			tf_ttl.getDocument().addDocumentListener(ctrl);
			tf_ttl.setFont(MiscFont.getFont(0, 14));
			tf_packetrate.setFont(MiscFont.getFont(0, 14));
			tf_udp_packetlength.setFont(MiscFont.getFont(0, 14));
			tf_udp_packetlength.setHorizontalAlignment(SwingConstants.CENTER);
			tf_ttl.setHorizontalAlignment(SwingConstants.CENTER);
			tf_packetrate.setHorizontalAlignment(SwingConstants.CENTER);
			pan_packetlength.add(tf_udp_packetlength, BorderLayout.CENTER);
			pan_ttl.add(tf_ttl, BorderLayout.CENTER);
			pan_packetrate.add(tf_packetrate, BorderLayout.CENTER);
		}

		// V1.5 [FH] Use this bounds for sender and receiver
		pan_udp_port.setBounds(5, 100, 105, 40);

		pan_groupIPaddress.setLayout(null);
		pan_sourceIPaddress.setLayout(null);
		pan_sourceIPaddress.setBounds(5, 60, 215, 40);
		pan_udp_port.setLayout(null);
		pan_udp_port.setBorder(MiscBorder.getBorder(BorderTitle.PORT,
				BorderType.NEUTRAL));
		pan_groupIPaddress.setBounds(5, 20, 215, 40);
		bt_active.setBounds(10, 204, 100, 25);
		bt_active.setFocusable(false);
		bt_active.addActionListener(ctrl);

		// V1.5: Added new Tabs
		if((typ == Typ.L3_SENDER) || (typ == Typ.L3_RECEIVER)) {
			pan_groupIPaddress.setBorder(MiscBorder.getBorder(
					BorderTitle.L3GROUP, BorderType.NEUTRAL));
			pan_sourceIPaddress.setBorder(MiscBorder.getBorder(
					BorderTitle.L3SOURCE, BorderType.NEUTRAL));
		} else if((typ == Typ.L2_SENDER) || (typ == Typ.L2_RECEIVER)) {
			pan_groupIPaddress.setBorder(MiscBorder.getBorder(
					BorderTitle.L2GROUP, BorderType.NEUTRAL));
			pan_sourceIPaddress.setBorder(MiscBorder.getBorder(
					BorderTitle.L2SOURCE, BorderType.NEUTRAL));
		}

		tf_udp_port = new JTextField();
		tf_groupIPaddress = new JTextField();
		pan_udp_port.add(tf_udp_port, BorderLayout.CENTER);
		tf_groupIPaddress.setBounds(5, 15, 205, 20);
		tf_udp_port.setBounds(5, 15, 95, 20);
		tf_groupIPaddress.setFont(MiscFont.getFont(0, 14));
		tf_udp_port.setFont(MiscFont.getFont(0, 14));
		tf_udp_port.setHorizontalAlignment(SwingConstants.CENTER);
		tf_groupIPaddress.setHorizontalAlignment(SwingConstants.CENTER);
		tf_groupIPaddress.setBorder(null);
		tf_udp_port.setBorder(null);
		tf_groupIPaddress.getDocument().addDocumentListener(ctrl);
		tf_udp_port.getDocument().addDocumentListener(ctrl);
		pan_groupIPaddress.add(tf_groupIPaddress, BorderLayout.CENTER);

	}

	/**
	 * Initialisiert die Standard Textfelder des KonfigurationsPanels.
	 * 
	 * @param typ
	 *            Gibt an zu welchem Programmteil das Panel gehoert.
	 */
	private void createGUIstandard(final Typ typ) {
		add(bt_enter);
		add(pan_groupIPaddress);
		if((typ == Typ.L3_RECEIVER) || (typ == Typ.L3_SENDER)) {
			add(pan_udp_port);
		}
		add(bt_active);
		// V1.5: typ==Typ.L3_SENDER || typ==Typ.L2_SENDER hinzugefuegt
		if((typ == Typ.L3_SENDER) || (typ == Typ.L2_SENDER)) {
			add(pan_packetrate);
			add(pan_packetlength);
			if(typ == Typ.L3_SENDER) {
				add(pan_ttl);
			}
		}
	}

	/**
	 * Setzt die Tool-Tips fuer die Eingabefelder.
	 * 
	 * @param typ
	 *            Gibt an zu welchem Programmteil das Panel gehoert.
	 */
	private void setToolTips(final Typ typ) {
		if((typ == Typ.L3_RECEIVER) || (typ == Typ.L3_SENDER)) {
			tf_groupIPaddress
					.setToolTipText("<html>"
							+ lang.getProperty("toolTip.mcAddressRange")
							+ "<br />"
							+ "IPv4: 224.0.0.0 - 239.255.255.255<br />"
							+ "IPv6: FF00:: - FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF</html>");
			cb_sourceIPaddress.setToolTipText(lang
					.getProperty("toolTip.ipFirst"));
			tf_udp_port.setToolTipText(lang
					.getProperty("toolTip.specifyUDPPort") + " (1 - 65535).");

			if(typ == Typ.L3_SENDER) {
				tf_ttl.setToolTipText(lang.getProperty("toolTip.specifyTTL")
						+ " (1 - 32)");
				tf_packetrate.setToolTipText(lang
						.getProperty("toolTip.specifyPacketRate")
						+ " (1 - 65535)");
				tf_udp_packetlength.setToolTipText("<html>"
						+ lang.getProperty("toolTip.specifyPacketLength")
						+ "<br />" + "IPv4: 52 - 65507<br />"
						+ "IPv6: 52 - 65527" + "</html>");
			}
		} else if((typ == Typ.L2_RECEIVER) || (typ == Typ.L2_SENDER)) {
			tf_groupIPaddress.setToolTipText("<html>"
					+ lang.getProperty("toolTip.selectMacAddress") + "<br />"
					+ "(any adress with a 1 at last bit in first Byte.<br />"
					+ "Example: 01:00:00:00:00:00)</html>");
			cb_sourceIPaddress.setToolTipText(lang
					.getProperty("toolTip.selectNetInterface"));

			if(typ == Typ.L2_SENDER) {
				tf_packetrate.setToolTipText(lang
						.getProperty("toolTip.specifyPacketRate")
						+ " (1 - 65535)");
				tf_udp_packetlength.setToolTipText(lang
						.getProperty("toolTip.specifyPacketLength")
						+ " (52 - 1500 Byte)");
			}
		}
	}
}
