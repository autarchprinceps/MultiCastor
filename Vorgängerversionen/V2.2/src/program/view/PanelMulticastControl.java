package program.view;

import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.*;

import program.controller.Messages;
import program.controller.ViewController;
import program.data.MulticastData.Typ;
/**
 * Das Kontrollpanel f�r Multicasts.
 * Mit diesem Panel k�nnen Multicasts gestartet, gestoppt und gel�scht werden.
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class PanelMulticastControl extends JPanel {
	private JButton start;
	private JButton stop;
	private JButton delete;
	private JButton selectDeselect_all;
	private JButton newmulticast;

	/**
	 * Konstruktor f�r das Kontrollpanel welcher alle zugeh�rigen GUI Komponenten initialisiert.
	 * @param ctrl Ben�tigete Referenz zum GUI Controller
	 */
	public PanelMulticastControl(ViewController ctrl, Typ typ){
		setBorder(new MiscBorder(Messages.getString("PanelMulticastControl.0"))); //$NON-NLS-1$
		setLayout(null);
		setPreferredSize(new Dimension(225,105));
//		setPreferredSize(new Dimension(225,60));
		initButtons(ctrl,typ);
	}
	/**
	 * Hilfsfunktion welche die Buttons des Kontrollpanels initialisier
	 * @param ctrl Ben�tigete Referenz zum GUI Controller
	 */
	private void initButtons(ViewController ctrl, Typ typ) {
		start = new JButton(Messages.getString("PanelMulticastControl.1")); //$NON-NLS-1$
		stop = new JButton(Messages.getString("PanelMulticastControl.2")); //$NON-NLS-1$
		delete = new JButton(Messages.getString("PanelMulticastControl.3")); //$NON-NLS-1$
		selectDeselect_all = new JButton(Messages.getString("PanelMulticastControl.4")); //$NON-NLS-1$
		newmulticast = new JButton(Messages.getString("PanelMulticastControl.5")); //$NON-NLS-1$
		start.setEnabled(false);
		stop.setEnabled(false);
		delete.setEnabled(false);

		start.setFont(MiscFont.getFont());
		stop.setFont(MiscFont.getFont());
		delete.setFont(MiscFont.getFont());
		selectDeselect_all.setFont(MiscFont.getFont());
		newmulticast.setFont(MiscFont.getFont());

		start.setFocusable(false);
		stop.setFocusable(false);
		delete.setFocusable(false);
		selectDeselect_all.setFocusable(false);
		newmulticast.setFocusable(false);

		start.setMargin(new Insets(0, 0, 0, 0));
		stop.setMargin(new Insets(0, 0, 0, 0));
		delete.setMargin(new Insets(0, 0, 0, 0));
		newmulticast.setMargin(new Insets(0, 0, 0, 0));
		selectDeselect_all.setMargin(new Insets(0, 0, 0, 0));

		start.setMnemonic('s');
		stop.setMnemonic('o');
		delete.setMnemonic('d');
		selectDeselect_all.setMnemonic('a');
		newmulticast.setMnemonic('n');


		start.setBounds(10,20,60,30);
		stop.setBounds(80,20,60,30);
		delete.setBounds(150,20,60,30);
		selectDeselect_all.setBounds(10,60,90,30);
		newmulticast.setBounds(110,60,100,30);

		start.addActionListener(ctrl);
		stop.addActionListener(ctrl);
		selectDeselect_all.addActionListener(ctrl);
		newmulticast.addActionListener(ctrl);
		delete.addActionListener(ctrl);
		add(start);
		add(stop);
		add(delete);

		add(selectDeselect_all);
		add(newmulticast);
	}

	public JButton getStart() {
		return start;
	}

	public JButton getStop() {
		return stop;
	}

	public JButton getDelete() {
		return delete;
	}
	public void setNewmulticast(JButton newmulticast) {
		this.newmulticast = newmulticast;
	}
	public JButton getSelectDeselect_all() {
		return selectDeselect_all;
	}
	public void setSelectDeselect_all(JButton selectDeselect_all) {
		this.selectDeselect_all = selectDeselect_all;
	}
	public void setStart(JButton start) {
		this.start = start;
	}

	public void setStop(JButton stop) {
		this.stop = stop;
	}

	public void setDelete(JButton delete) {
		this.delete = delete;
	}


	public JButton getNewmulticast() {
		return newmulticast;
	}
}
