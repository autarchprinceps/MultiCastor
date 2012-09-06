   
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import dhbw.multicastor.program.controller.ViewController;

/**
 * FileChooser fï¿½r Speichern und Laden von Config Files
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class FrameFileChooser extends JFrame {
	/**
	 * JFileChooser Menu welches angepasst wird je nach dem ob es sich um einen Speichern oder Laden Dialog handelt.
	 */
	private JFileChooser chooser;
	/**
	 * Filter welcher bestimmt das nur .xml Dateien gespeichert und geladen werden.
	 */
	private FileNameExtensionFilter filter;
	/**
	 * Hilfsvariable welche bestimmt um was fï¿½r eine Art von Dialog es sich handelt (Speichern/Laden).
	 */
	private boolean typeSave;
	/**
	 * Checkbox welche angibt ob inkrementell geladen werden soll oder nicht.
	 */
	private JCheckBox cb_incremental;
	/**
	 * Kontruktir des FileChoosers
	 * @param ctrl Benï¿½igte Referenz zum GUI Controller
	 * @param save gibt an ob es sich um einen Datei speichern Dialog handelt
	 */
	public FrameFileChooser(ViewController ctrl, boolean save){
		typeSave = save;
		initWindow();
		initFileChooser(ctrl);
		initSelection();
	}
	/**
	 * Initialisiert die Checkboxen des Filechoosers
	 */
	private void initSelection() {
		JPanel selection = new JPanel();
		selection.setLayout(null);
		selection.setPreferredSize(new Dimension(600,60));

		if(!typeSave){
			cb_incremental = new JCheckBox("Incremental Load");
			cb_incremental.setFont(MiscFont.getFont());
			cb_incremental.setFocusable(false);
			cb_incremental.setBounds(220,0,140,20);
			selection.add(cb_incremental);
		}
		add(selection,BorderLayout.SOUTH);
	}
	/**
	 * Initialisiert die Dateiauswahl.
	 * @param ctrl Benï¿½tigte Referenz zum GUI Controller.
	 */
	private void initFileChooser(ViewController ctrl) {
		filter = new FileNameExtensionFilter("XML Config Files", "xml");
		chooser = new JFileChooser();
		if(typeSave){
			chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		}
		else{
			chooser.setDialogType(JFileChooser.OPEN_DIALOG);
		}
		//chooser.setControlButtonsAreShown(false);
		chooser.setFileFilter(filter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFont(MiscFont.getFont());
		chooser.addActionListener(ctrl);
		add(chooser, BorderLayout.CENTER);
	}
	/**
	 * Setzt den Fenstertitel fï¿½r Laden
	 */
	private void initLoad() {
		setTitle("Load Configuration");
		
	}
	/**
	 * Setzt den Fenstertitel fï¿½r Speichern
	 */
	private void initSave() {
		setTitle("Save Configuration");
		
	}
	/**
	 * Konfiguriert das Frame in welchem sich der FileChooser befindet
	 */
	private void initWindow(){
		setLayout(new BorderLayout());
		setSize(600,600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		if(typeSave){
			initSave();
		}
		else{
			initLoad();
		}
	}
	/**
	 * Funktion zum anzeigen und verbergen des FileChoosers
	 */
	public void toggle(){
		if(this.isVisible()){
			setVisible(false);
		}
		else{
			setVisible(true);
		}
	}
	public JFileChooser getChooser() {
		return chooser;
	}
	public FileNameExtensionFilter getFilter() {
		return filter;
	}
	public boolean isTypeSave() {
		return typeSave;
	}

	public JCheckBox getCb_incremental() {
		return cb_incremental;
	}

	public boolean isCbIncrementalSelected(){
		return cb_incremental.isSelected();
	}
	public String getSelectedFile(){
		if(chooser.getSelectedFile().getAbsolutePath().endsWith(".xml")){
			return ""+chooser.getSelectedFile().getAbsolutePath();
		}
		else{
			return ""+chooser.getSelectedFile().getAbsolutePath()+".xml";
		}
		
	}
}
