package multicastor.view;

import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jdesktop.swingx.JXTable;

import multicastor.controller.ViewController;

/**
 * Abstrakte Klasse welche fuer die Generierung von PopupMenus im Programm
 * zustaendig ist. Bisher wurden nur Popups fuer den Tabellenkopf implementiert,
 * hier kann das Programm um beliebige weitere Popup Menus erweitert werden.
 */
public abstract class PopUpMenu {
	private static JCheckBox[] columns = null;
	private static JPopupMenu menuPopUp = null;
	private static int selectedColumn = -1;

	/**
	 * Erstellt die zum Table Header Popup gehoerenden Checkboxen
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller
	 */
	public static void createColumnCheckBoxes(final ViewController ctrl) {
		final int count = ctrl.getTable(ctrl.getSelectedTab()).getModel()
				.getColumnCount();
		columns = new JCheckBox[count];
		for (int i = 0; i < count; i++) {
			final String columnName = ctrl.getTable(ctrl.getSelectedTab())
					.getModel().getColumnName(i);
			columns[i] = new JCheckBox(columnName);
			if ((i == 0) || (i == 1)) {
				columns[i].setSelected(true);
				columns[i].setEnabled(false);
			}
			columns[i].addActionListener(ctrl);
			columns[i].setActionCommand("PopupCheckBox");
			columns[i].setFocusable(false);
		}
		updateCheckBoxes(ctrl);
	}

	/**
	 * Statische Funktion welche das Popup fuer den Tabellenkopf erstellt und
	 * anzeigt.
	 * 
	 * @param table
	 *            Die Tabelle zu welcher das Popup gehoert.
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller
	 * @param e
	 *            Das MouseEvent welches das Popup �ffnet
	 */
	public static void createTableHeaderPopup(final JXTable table,
			final ViewController ctrl, final MouseEvent e) {
		createColumnCheckBoxes(ctrl);
		if (menuPopUp != null) {
			menuPopUp.setVisible(false);
		}
		menuPopUp = new JPopupMenu();
		final JMenu m_columns = new JMenu("Columns");
		final JMenuItem mi_hide = new JMenuItem("Hide");
		final JMenuItem mi_showALL = new JMenuItem("Reset View");
		selectedColumn = ctrl.getTable(ctrl.getSelectedTab()).getColumnModel()
				.getColumnIndexAtX(e.getX());
		if ((ctrl.getUserInputData(ctrl.getSelectedTab()).getOriginalIndex(
				selectedColumn) == 0)
				|| (ctrl.getUserInputData(ctrl.getSelectedTab())
						.getOriginalIndex(selectedColumn) == 1)) {
			mi_hide.setEnabled(false);
		}
		mi_hide.addActionListener(ctrl);
		mi_hide.setActionCommand("hide");
		mi_showALL.addActionListener(ctrl);
		mi_showALL.setActionCommand("showall");
		menuPopUp.add(mi_hide);
		menuPopUp.add(m_columns);
		for (int x = 0; x < columns.length; x++) {
			m_columns.add(columns[ctrl.getUserInputData(ctrl.getSelectedTab())
					.getColumnOrder().get(x).intValue()]);
		}
		menuPopUp.add(mi_showALL);
		menuPopUp.show(table.getTableHeader(), e.getX(), e.getY());
	}

	public static JCheckBox[] getColumns() {
		return columns;
	}

	public static int getSelectedColumn() {
		return selectedColumn;
	}

	public static boolean isPopUpVisible() {
		if (menuPopUp != null) {
			return menuPopUp.isVisible();
		}
		return false;
	}

	/**
	 * Funktion welche die Reihenfolge der Checkboxen im Table Header Popup
	 * einstellt
	 * 
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller
	 */
	public static void updateCheckBoxes(final ViewController ctrl) {
		for (int x = 0; x < 11; x++) {
			for (int i = 0; i < ctrl.getUserInputData(ctrl.getSelectedTab())
					.getColumnVisbility().size(); i++) {
				if (ctrl.getUserInputData(ctrl.getSelectedTab())
						.getColumnOrder().get(x).intValue() == ctrl
						.getUserInputData(ctrl.getSelectedTab())
						.getColumnVisbility().get(i).intValue()) {
					columns[x].setSelected(true);
					break;
				} else {
					columns[x].setSelected(false);
				}
			}
		}

	}
}
