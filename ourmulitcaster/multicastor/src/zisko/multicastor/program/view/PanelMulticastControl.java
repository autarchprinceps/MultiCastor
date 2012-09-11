package zisko.multicastor.program.view;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JPanel;
import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.lang.LanguageManager;

/**
 * Das Kontrollpanel fuer Multicasts. Mit diesem Panel koennen Multicasts
 * gestartet, gestoppt und geloescht werden.
 */
@SuppressWarnings("serial")
public class PanelMulticastControl extends JPanel {

	private final ViewController ctrl;
	private JButton delete;
	private final LanguageManager lang;
	private MiscBorder mainBorder;
	private JButton newmulticast;
	private JButton select_deselect_all;
	private JButton start_stop;

	/**
	 * Konstruktor fuer das Kontrollpanel welcher alle zugehoerigen GUI
	 * Komponenten initialisiert.
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 */
	public PanelMulticastControl(final ViewController ctrl) {
		this.ctrl = ctrl;
		lang = LanguageManager.getInstance();
		setBorder(mainBorder = new MiscBorder(
				lang.getProperty("miscBorder.mcControl")));
		setLayout(null);
		setPreferredSize(new Dimension(225, 85));
		initButtons(ctrl, true);
	}

	public JButton getDelete() {
		return delete;
	}

	public JButton getNewmulticast() {
		return newmulticast;
	}

	public JButton getSelectDeselect_all() {
		return select_deselect_all;
	}

	public JButton getStartStop() {
		return start_stop;
	}

	/**
	 * Diese Methode aktualisiert den textuellen Inhalt, wenn die Sprache
	 * geaendert wird.
	 */
	public void reloadLanguage() {
		mainBorder.setTitle(lang.getProperty("miscBorder.mcControl"));
		initButtons(ctrl, false);
	}

	public void setDelete(final JButton delete) {
		this.delete = delete;
	}

	public void setSelectDeselect_all(final JButton selectAll) {
		select_deselect_all = selectAll;
	}

	public void setStartStop(final JButton start) {
		start_stop = start;
	}

	/**
	 * Hilfsfunktion welche die Buttons des Kontrollpanels initialisiert
	 * 
	 * @param ctrl
	 *            Benoetigete Referenz zum GUI Controller.
	 * @param firstInit
	 *            Gibt an, ob die Buttons zum ersten Mal initialisiert werden
	 *            oder nicht.
	 */
	private void initButtons(final ViewController ctrl, final boolean firstInit) {

		if(firstInit) {
			start_stop = new JButton();
			delete = new JButton();
			select_deselect_all = new JButton();
			newmulticast = new JButton();

			start_stop.setEnabled(false);
			delete.setEnabled(false);

			final Font myFont = new Font("Helvetica", Font.BOLD, 11);
			start_stop.setFont(myFont);
			delete.setFont(MiscFont.getFont());
			select_deselect_all.setFont(MiscFont.getFont());
			newmulticast.setFont(MiscFont.getFont());

			start_stop.setFocusable(false);
			delete.setFocusable(false);
			select_deselect_all.setFocusable(false);
			newmulticast.setFocusable(false);

			select_deselect_all.setBounds(10, 20, 100, 25);
			delete.setBounds(115, 20, 100, 25);
			newmulticast.setBounds(10, 50, 100, 25);
			start_stop.setBounds(115, 50, 100, 25);

			start_stop.addActionListener(ctrl);
			select_deselect_all.addActionListener(ctrl);
			newmulticast.addActionListener(ctrl);
			delete.addActionListener(ctrl);
			add(start_stop);
			add(newmulticast);
			add(select_deselect_all);
			add(delete);
		}

		start_stop.setText(lang.getProperty("button.start") + " / "
				+ lang.getProperty("button.stop"));
		delete.setText(lang.getProperty("button.delete"));
		select_deselect_all.setText(lang.getProperty("button.deSelectAll"));
		newmulticast.setText(lang.getProperty("button.new"));
	}
}
