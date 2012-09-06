   
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

import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import dhbw.multicastor.program.controller.ViewController;

import java.util.ResourceBundle;
/**
 * Abstrakte Klasse welche für die Generierung von PopupMenus im Programm zuständig ist.
 * Bisher wurden nur Popups für den Tabellenkopf implementiert, hier kann das Programm um beliebige
 * weitere Popup Menus erweitert werden.
 * @author Daniel Becker
 *
 */
public class PopUpMenu {
	private static final ResourceBundle message = ResourceBundle.getBundle("dhbw.multicastor.resources.i18n.messages"); //$NON-NLS-1$
		private JPopupMenu menuPopUp = null;
		private JCheckBox[] columns = null;
		
	public boolean isPopUpVisible(){
		boolean ret = false;
		if(menuPopUp != null){
			ret = menuPopUp.isVisible();
		}
		return ret;
	}
	/**
	 * Statische Funktion welche das Popup für den Tabellenkopf erstellt und anzeigt.
	 * @param table Die Tabelle zu welcher das Popup gehört.
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 * @param e Das MouseEvent welches das Popup Öffnet
	 * @wbp.parser.entryPoint
	 */
	public void createTableHeaderPopup(ViewController ctrl, MouseEvent e){
		createColumnCheckBoxes(ctrl);
		if(menuPopUp != null){
			menuPopUp.setVisible(false);
		}
		menuPopUp = new JPopupMenu();
		int selectedColumn = ctrl.getTable().getColumnModel().getColumnIndexAtX(e.getX());
		JMenuItem mi_hide = new JMenuItem(message.getString("PopUpMenu.mi_hide.text")); //$NON-NLS-1$
		if(	ctrl.getTable().getColumnName(selectedColumn).equals(ctrl.getTable().getModel().getColumnName(0)) || 
			ctrl.getTable().getColumnName(selectedColumn).equals(ctrl.getTable().getModel().getColumnName(1))){
			mi_hide.setEnabled(false);
		}
		JMenu m_columns = new JMenu(message.getString("PopUpMenu.m_columns.text")); //$NON-NLS-1$
		for(int x = 2 ; x < columns.length ; x++){
			m_columns.add(columns[x]);
		}
		JMenuItem mi_resetView = new JMenuItem(message.getString("PopUpMenu.mi_resetView.text")); //$NON-NLS-1$
		
		mi_hide.addActionListener(ctrl);
		mi_hide.setActionCommand("hide");
		mi_resetView.addActionListener(ctrl);
		mi_resetView.setActionCommand("restore_columns");
		menuPopUp.add(mi_hide);
		menuPopUp.add(m_columns);
		
		menuPopUp.add(mi_resetView);
		menuPopUp.show(ctrl.getTable().getTableHeader(), e.getX(), e.getY());
	}
	
	/**
	 * Erstellt die zum Table Header Popup gehörenden Checkboxen
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 */
	public void createColumnCheckBoxes(ViewController ctrl){
		int count = ctrl.getTable().getModel().getColumnCount();
		columns = new JCheckBox[count];
		for(int i = 2 ; i < count ; i++){
			String columnName = ctrl.getTable().getModel().getColumnName(i);
			columns[i] = new JCheckBox(columnName);
			columns[i].addActionListener(ctrl);
			columns[i].setActionCommand("Popup_toggleColumn");
			columns[i].setFocusable(false);
		}
		updateCheckBoxes(ctrl);
	}
	
	/**
	 * Funktion welche die Reihenfolge der Checkboxen im Table Header Popup einstellt
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 */
	public void updateCheckBoxes(ViewController ctrl){
		CustomTableColumnModel columnModel = (CustomTableColumnModel) ctrl.getTable().getColumnModel();
		for(int x = 2 ; x < ctrl.getTable().getModel().getColumnCount() ; x++){
			TableColumn column = columnModel.getAllColumns().get(x);
			if(columnModel.isVisable(column)){
				columns[x].setSelected(true);
			}
			else {
				columns[x].setSelected(false);
			}
		}
	}
	
	public void createTablePopup(JTable table, ViewController crtl, MouseEvent e){
		menuPopUp = new JPopupMenu();
		JMenuItem selectAll = new JMenuItem(message.getString("PopUpMenu.selectAll.text"));
		JMenuItem deselectAll = new JMenuItem(message.getString("PopUpMenu.deselectAll.text"));
		JMenuItem mi_delete = new JMenuItem(message.getString("PopUpMenu.delete.text"));
		JMenuItem groupItems = new JMenuItem(message.getString("PopUpMenu.groupItems.text"));
		selectAll.addActionListener(crtl);
		selectAll.setActionCommand("select_all");
		deselectAll.addActionListener(crtl);
		deselectAll.setActionCommand("deselect_all");
		mi_delete.addActionListener(crtl);
		mi_delete.setActionCommand("delete_popup");
		groupItems.addActionListener(crtl);
		groupItems.setActionCommand("groupItems");
		menuPopUp.add(selectAll);
		menuPopUp.add(deselectAll);
		menuPopUp.add(mi_delete);
		menuPopUp.add(groupItems);
		menuPopUp.show(e.getComponent(), e.getX(), e.getY());
	}
}
