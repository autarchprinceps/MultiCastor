package zisko.multicastor.program.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.lang.LanguageManager;

/**
 * Klasse, die den Inhalt des "+"-Panels definiert. Im "+" Panel erscheint eine
 * ss�bersicht, welche die Moeglichkeit bietet, alle verfuegbaren Panels zu
 * oeffnen.
 */

@SuppressWarnings("serial")
public class PanelPlus extends JPanel {

	JLabel aboutText;
	JLabel l2rText;
	JLabel l3rText;
	JLabel l3sText;
	private JButton about;
	@SuppressWarnings("unused")
	private final FrameMain frame;
	private JButton l2r;
	private JButton l2s;
	private JLabel l2sText;
	private JButton l3r;
	private JButton l3s;

	private final LanguageManager lang;

	private final ViewController vCtrl;

	/**
	 * Konstruktor initialisiert die grafischen Komponenten und setzt Referenzen
	 * zum Frame und ViewController.
	 * 
	 * @param pFrame
	 *            Frame-Main-Instanz.
	 * @param pVCtrl
	 *            Instanz des ViewControllers.
	 */
	public PanelPlus(final FrameMain pFrame, final ViewController pVCtrl) {
		vCtrl = pVCtrl;
		frame = pFrame;
		lang = LanguageManager.getInstance();
		initComponents(true);
	}

	/**
	 * Diese Methode aktualisiert den textuellen Inhalt, wenn die Sprache
	 * geaendert wird.
	 */
	public void reloadLanguage() {
		initComponents(false);
	}

	/**
	 * Diese Methode initialisiert die grafischen Komponenten.
	 * 
	 * @param firstInit
	 *            Gibt an, ob die Komponenten zum ersten Mal initialisiert
	 *            werden oder nicht.
	 */
	private void initComponents(final boolean firstInit) {
		if(firstInit) {
			final GridBagLayout gridBayLayout = new GridBagLayout();
			setLayout(gridBayLayout);

			l2s = new JButton();
			l2s.setMinimumSize(new Dimension(100, 100));
			gridBayLayout.setConstraints(l2s, new GridBagConstraints(0, 0, 1,
					1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			l2s.setIcon(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/ipv4sender.png")));
			l2s.setFont(MiscFont.getFont(0, 17));
			l2s.setActionCommand("open_layer2_s");
			l2s.addActionListener(vCtrl);
			add(l2s);
			l2sText = new JLabel();
			gridBayLayout.setConstraints(l2sText, new GridBagConstraints(1, 0,
					1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			add(l2sText);

			l2r = new JButton();
			gridBayLayout.setConstraints(l2r, new GridBagConstraints(0, 1, 1,
					1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			l2r.setIcon(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/ipv4receiver.png")));
			l2r.setFont(MiscFont.getFont(0, 17));
			l2r.setActionCommand("open_layer2_r");
			l2r.addActionListener(vCtrl);
			add(l2r);
			l2rText = new JLabel();
			gridBayLayout.setConstraints(l2rText, new GridBagConstraints(1, 1,
					1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			add(l2rText);

			l3s = new JButton();
			gridBayLayout.setConstraints(l3s, new GridBagConstraints(0, 2, 1,
					1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			l3s.setIcon(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/ipv6sender.png")));
			l3s.setFont(MiscFont.getFont(0, 17));
			l3s.setActionCommand("open_layer3_s");
			l3s.addActionListener(vCtrl);
			add(l3s);
			l3sText = new JLabel();
			gridBayLayout.setConstraints(l3sText, new GridBagConstraints(1, 2,
					1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			add(l3sText);

			l3r = new JButton();
			gridBayLayout.setConstraints(l3r, new GridBagConstraints(0, 3, 1,
					1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			l3r.setIcon(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/ipv6receiver.png")));
			l3r.setFont(MiscFont.getFont(0, 17));
			l3r.setActionCommand("open_layer3_r");
			l3r.addActionListener(vCtrl);
			add(l3r);
			l3rText = new JLabel();
			gridBayLayout.setConstraints(l3rText, new GridBagConstraints(1, 3,
					1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			add(l3rText);

			about = new JButton();
			gridBayLayout.setConstraints(about, new GridBagConstraints(0, 4, 1,
					1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			about.setIcon(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/about.png")));
			about.setFont(MiscFont.getFont(0, 17));
			about.setActionCommand("open_about");
			about.addActionListener(vCtrl);
			add(about);
			aboutText = new JLabel();
			gridBayLayout.setConstraints(aboutText, new GridBagConstraints(1,
					4, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			add(aboutText);
		}

		l2s.setText(lang.getProperty("mi.layer2Sender"));
		l2r.setText(lang.getProperty("mi.layer2Receiver"));
		l3s.setText(lang.getProperty("mi.layer3Sender"));
		l3r.setText(lang.getProperty("mi.layer3Receiver"));
		about.setText(lang.getProperty("mi.about"));

		l2sText.setText(lang.getProperty("plus.l2sDescription"));
		l2rText.setText(lang.getProperty("plus.l2rDescription"));
		l3sText.setText(lang.getProperty("plus.l3sDescription"));
		l3rText.setText(lang.getProperty("plus.l3rDescription"));
		aboutText.setText(lang.getProperty("plus.aboutDescription"));

		if(!firstInit) {
			// do Layout is needes for for auto resize elements
			doLayout();
		}
	}
}
