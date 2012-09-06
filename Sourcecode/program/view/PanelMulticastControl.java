package zisko.multicastor.program.view;

import java.awt.Dimension;
import javax.swing.*;
import zisko.multicastor.program.controller.ViewController;
/**
 * Das Kontrollpanel für Multicasts. 
 * Mit diesem Panel können Multicasts gestartet, gestoppt und gelöscht werden.
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class PanelMulticastControl extends JPanel {
	private JButton start;
	private JButton stop;
	private JButton delete;
	private JButton select_all;
	private JButton deselect_all;
	private JButton newmulticast;

	/**
	 * Konstruktor für das Kontrollpanel welcher alle zugehörigen GUI Komponenten initialisiert.
	 * @param ctrl Benötigete Referenz zum GUI Controller
	 */
	public PanelMulticastControl(ViewController ctrl){
		setBorder(new MiscBorder("MultiCast Control"));
		setLayout(null);
		setPreferredSize(new Dimension(225,150));
		initButtons(ctrl);
	}
	/**
	 * Hilfsfunktion welche die Buttons des Kontrollpanels initialisier
	 * @param ctrl Benötigete Referenz zum GUI Controller
	 */
	private void initButtons(ViewController ctrl) {
		start = new JButton("Start");
		stop = new JButton("Stop");
		delete = new JButton("Delete");
		select_all = new JButton("Select All");
		deselect_all = new JButton("Deselect All");
		newmulticast = new JButton("New");
		start.setEnabled(false);
		stop.setEnabled(false);
		delete.setEnabled(false);
		
		start.setFont(MiscFont.getFont());
		stop.setFont(MiscFont.getFont());
		delete.setFont(MiscFont.getFont());
		select_all.setFont(MiscFont.getFont());
		deselect_all.setFont(MiscFont.getFont());
		newmulticast.setFont(MiscFont.getFont());
		
		start.setFocusable(false);
		stop.setFocusable(false);
		delete.setFocusable(false);
		select_all.setFocusable(false);
		deselect_all.setFocusable(false);
		newmulticast.setFocusable(false);
		
		start.setBounds(135,20,70,30);
		stop.setBounds(135,60,70,30);
		delete.setBounds(15,100,110,30);
		select_all.setBounds(15,20,110,30);
		deselect_all.setBounds(15,60,110,30);
		newmulticast.setBounds(135,100,70,30);
		start.addActionListener(ctrl);
		stop.addActionListener(ctrl);
		select_all.addActionListener(ctrl);
		deselect_all.addActionListener(ctrl);
		newmulticast.addActionListener(ctrl);
		delete.addActionListener(ctrl);
		add(start);
		add(select_all);
		add(stop);
		add(deselect_all);
		add(delete);
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

	public JButton getSelect_all() {
		return select_all;
	}

	public JButton getDeselect_all() {
		return deselect_all;
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

	public void setSelect_all(JButton selectAll) {
		select_all = selectAll;
	}

	public void setDeselect_all(JButton deselectAll) {
		deselect_all = deselectAll;
	}

	public JButton getNewmulticast() {
		return newmulticast;
	}
}
