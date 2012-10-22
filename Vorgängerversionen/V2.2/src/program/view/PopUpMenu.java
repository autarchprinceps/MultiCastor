package program.view;

import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import program.controller.Messages;
import program.controller.ViewController;
/**
 * Abstrakte Klasse welche f�r die Generierung von PopupMenus im Programm zust�ndig ist.
 * Bisher wurden nur Popups f�r den Tabellenkopf implementiert, hier kann das Programm um beliebige
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
	 * Statische Funktion welche das Popup f�r den Tabellenkopf erstellt und anzeigt.
	 * @param table Die Tabelle zu welcher das Popup geh�rt.
	 * @param ctrl Ben�tigte Referenz zum GUI Controller
	 * @param e Das MouseEvent welches das Popup �ffnet
	 */
	public static void createTableHeaderPopup(JTable table, ViewController ctrl, MouseEvent e){
		createColumnCheckBoxes(ctrl);
		if(menuPopUp != null){
			menuPopUp.setVisible(false);
		}
		menuPopUp = new JPopupMenu();
		JMenu m_columns = new JMenu("Columns"); //$NON-NLS-1$
		JMenuItem mi_hide = new JMenuItem(Messages.getString("PopUpMenu.1")); //$NON-NLS-1$
		JMenuItem mi_showALL = new JMenuItem(Messages.getString("PopUpMenu.2")); //$NON-NLS-1$
		selectedColumn = ctrl.getTable(ctrl.getSelectedTab()).getColumnModel().getColumnIndexAtX(e.getX());
		if(ctrl.getUserInputData().getOriginalIndex(selectedColumn,ctrl.getSelectedTab()) == 0
		||  ctrl.getUserInputData().getOriginalIndex(selectedColumn,ctrl.getSelectedTab()) == 1){
			mi_hide.setEnabled(false);
		}
		mi_hide.addActionListener(ctrl);
		mi_hide.setActionCommand("hide"); //$NON-NLS-1$
		mi_showALL.addActionListener(ctrl);
		mi_showALL.setActionCommand("showall"); //$NON-NLS-1$
		menuPopUp.add(mi_hide);
		menuPopUp.add(m_columns);
		for(int x = 0 ; x < columns.length ; x++){
			m_columns.add(columns[ctrl.getUserInputData().getColumnOrder(ctrl.getSelectedTab()).get(x).intValue()]);
		}
		menuPopUp.add(mi_showALL);
		menuPopUp.show(table.getTableHeader(), e.getX(), e.getY());
	}
	public static int getSelectedColumn(){
		return selectedColumn;
	}
	/**
	 * Erstellt die zum Table Header Popup geh�renden Checkboxen
	 * @param ctrl Ben�tigte Referenz zum GUI Controller
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
			columns[i].setActionCommand("PopupCheckBox"); //$NON-NLS-1$
			columns[i].setFocusable(false);
		}
		updateCheckBoxes(ctrl);
	}
	public static JCheckBox[] getColumns() {
		return columns;
	}
	/**
	 * Funktion welche die Reihenfolge der Checkboxen im Table Header Popup einstellt
	 * @param ctrl Ben�tigte Referenz zum GUI Controller
	 */
	public static void updateCheckBoxes(ViewController ctrl){
		for(int x = 0 ; x < 12 ; x++){
			for(int i = 0; i < ctrl.getUserInputData().getColumnVisbility(ctrl.getSelectedTab()).size() ; i++){
				if(ctrl.getUserInputData().getColumnOrder(ctrl.getSelectedTab()).get(x).intValue() ==
					ctrl.getUserInputData().getColumnVisbility(ctrl.getSelectedTab()).get(i).intValue()){
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
