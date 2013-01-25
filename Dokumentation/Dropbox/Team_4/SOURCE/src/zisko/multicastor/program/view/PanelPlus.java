package zisko.multicastor.program.view;
//FrameMain delete?
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

	private JButton about;
	private JLabel aboutText;
	//private final FrameMain frame;
	private JButton[][] lrs = new JButton[2][2];
	private JLabel[][] lrsText = new JLabel[2][2];

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
	public PanelPlus(/*final FrameMain pFrame,*/ final ViewController pVCtrl) {
		vCtrl = pVCtrl;
		//frame = pFrame;
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

			lrs[0][1] = new JButton();
			lrs[0][1].setMinimumSize(new Dimension(100, 100));
			gridBayLayout.setConstraints(lrs[0][1], new GridBagConstraints(0, 0, 1,
					1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			lrs[0][1].setIcon(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/ipv4sender.png")));
			lrs[0][1].setFont(MiscFont.getFont(0, 17));
			lrs[0][1].setActionCommand("open_layer2_s");
			lrs[0][1].addActionListener(vCtrl);
			add(lrs[0][1]);
			lrsText[0][1] = new JLabel();
			gridBayLayout.setConstraints(lrsText[0][1], new GridBagConstraints(1, 0,
					1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			add(lrsText[0][1]);

			lrs[0][0] = new JButton();
			gridBayLayout.setConstraints(lrs[0][0], new GridBagConstraints(0, 1, 1,
					1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			lrs[0][0].setIcon(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/ipv4receiver.png")));
			lrs[0][0].setFont(MiscFont.getFont(0, 17));
			lrs[0][0].setActionCommand("open_layer2_r");
			lrs[0][0].addActionListener(vCtrl);
			add(lrs[0][0]);
			lrsText[0][0] = new JLabel();
			gridBayLayout.setConstraints(lrsText[0][0], new GridBagConstraints(1, 1,
					1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			add(lrsText[0][0]);

			lrs[1][1] = new JButton();
			gridBayLayout.setConstraints(lrs[1][1], new GridBagConstraints(0, 2, 1,
					1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			lrs[1][1].setIcon(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/ipv6sender.png")));
			lrs[1][1].setFont(MiscFont.getFont(0, 17));
			lrs[1][1].setActionCommand("open_layer3_s");
			lrs[1][1].addActionListener(vCtrl);
			add(lrs[1][1]);
			lrsText[1][1] = new JLabel();
			gridBayLayout.setConstraints(lrsText[1][1], new GridBagConstraints(1, 2,
					1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			add(lrsText[1][1]);

			lrs[1][0] = new JButton();
			gridBayLayout.setConstraints(lrs[1][0], new GridBagConstraints(0, 3, 1,
					1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			lrs[1][0].setIcon(new ImageIcon(getClass().getResource(
					"/zisko/multicastor/resources/images/ipv6receiver.png")));
			lrs[1][0].setFont(MiscFont.getFont(0, 17));
			lrs[1][0].setActionCommand("open_layer3_r");
			lrs[1][0].addActionListener(vCtrl);
			add(lrs[1][0]);
			lrsText[1][0] = new JLabel();
			gridBayLayout.setConstraints(lrsText[1][0], new GridBagConstraints(1, 3,
					1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			add(lrsText[1][0]);

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

		lrs[0][1].setText(lang.getProperty("mi.layer2Sender"));
		lrs[0][0].setText(lang.getProperty("mi.layer2Receiver"));
		lrs[1][1].setText(lang.getProperty("mi.layer3Sender"));
		lrs[1][0].setText(lang.getProperty("mi.layer3Receiver"));
		about.setText(lang.getProperty("mi.about"));

		lrsText[0][1].setText(lang.getProperty("plus.l2sDescription"));
		lrsText[0][0].setText(lang.getProperty("plus.l2rDescription"));
		lrsText[1][1].setText(lang.getProperty("plus.l3sDescription"));
		lrsText[1][0].setText(lang.getProperty("plus.l3rDescription"));
		aboutText.setText(lang.getProperty("plus.aboutDescription"));

		if(!firstInit) {
			// do Layout is needes for for auto resize elements
			doLayout();
		}
	}
}
