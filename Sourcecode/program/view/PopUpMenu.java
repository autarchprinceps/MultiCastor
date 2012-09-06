package zisko.multicastor.program.view;

import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import zisko.multicastor.program.controller.ViewController;
/**
 * Abstrakte Klasse welche für die Generierung von PopupMenus im Programm zuständig ist.
 * Bisher wurden nur Popups für den Tabellenkopf implementiert, hier kann das Programm um beliebige
 * weitere Popup Menus erweitert werden.
 * @author Daniel Becker
 *
 */
public abstract class PopUpMenu {
		private static JPopupMenu menuPopUp = null;
		private static int selectedColumn = -1;
		private static JCheckBox[] columns = null;
	public static boolean isPopUpVisible(){
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
	 */
	public static void createTableHeaderPopup(JTable table, ViewController ctrl, MouseEvent e){
		createColumnCheckBoxes(ctrl);
		if(menuPopUp != null){
			menuPopUp.setVisible(false);
		}
		menuPopUp = new JPopupMenu();
		JMenu m_columns = new JMenu("Columns");
		JMenuItem mi_hide = new JMenuItem("Hide");
		JMenuItem mi_showALL = new JMenuItem("Reset View");
		selectedColumn = ctrl.getTable(ctrl.getSelectedTab()).getColumnModel().getColumnIndexAtX(e.getX());
		if(ctrl.getUserInputData(ctrl.getSelectedTab()).getOriginalIndex(selectedColumn) == 0 
		||  ctrl.getUserInputData(ctrl.getSelectedTab()).getOriginalIndex(selectedColumn) == 1){
			mi_hide.setEnabled(false);
		}
		mi_hide.addActionListener(ctrl);
		mi_hide.setActionCommand("hide");
		mi_showALL.addActionListener(ctrl);
		mi_showALL.setActionCommand("showall");
		menuPopUp.add(mi_hide);
		menuPopUp.add(m_columns);
		for(int x = 0 ; x < columns.length ; x++){
			m_columns.add(columns[ctrl.getUserInputData(ctrl.getSelectedTab()).getColumnOrder().get(x).intValue()]);
		}
		menuPopUp.add(mi_showALL);
		menuPopUp.show(table.getTableHeader(), e.getX(), e.getY());
	}
	public static int getSelectedColumn(){
		return selectedColumn;
	}
	/**
	 * Erstellt die zum Table Header Popup gehörenden Checkboxen
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 */
	public static void createColumnCheckBoxes(ViewController ctrl){
		int count = ctrl.getTable(ctrl.getSelectedTab()).getModel().getColumnCount();
		columns = new JCheckBox[count];
		for(int i = 0 ; i < count ; i++){
			String columnName = ctrl.getTable(ctrl.getSelectedTab()).getModel().getColumnName(i);
			columns[i] = new JCheckBox(columnName);
			if(i == 0 || i == 1){
				columns[i].setSelected(true);
				columns[i].setEnabled(false);
			}
			columns[i].addActionListener(ctrl);
			columns[i].setActionCommand("PopupCheckBox");
			columns[i].setFocusable(false);
		}
		updateCheckBoxes(ctrl);
	}
	public static JCheckBox[] getColumns() {
		return columns;
	}
	/**
	 * Funktion welche die Reihenfolge der Checkboxen im Table Header Popup einstellt
	 * @param ctrl Benötigte Referenz zum GUI Controller
	 */
	public static void updateCheckBoxes(ViewController ctrl){
		for(int x = 0 ; x < 11 ; x++){
			for(int i = 0; i < ctrl.getUserInputData(ctrl.getSelectedTab()).getColumnVisbility().size() ; i++){
				if(ctrl.getUserInputData(ctrl.getSelectedTab()).getColumnOrder().get(x).intValue() == 
					ctrl.getUserInputData(ctrl.getSelectedTab()).getColumnVisbility().get(i).intValue()){
					columns[x].setSelected(true);
					break;
				}
				else{
					columns[x].setSelected(false);
				}
			}
		}

	}
}
