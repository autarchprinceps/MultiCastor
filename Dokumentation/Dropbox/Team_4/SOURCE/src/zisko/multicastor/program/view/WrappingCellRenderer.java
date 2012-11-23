package zisko.multicastor.program.view;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import zisko.multicastor.program.controller.ViewController;

/**
 * Klasse welche die Farben in der Tabelle verwaltet, hierbei muss unterschieden
 * werden ob Multicasts aktiv, inaktiv, selektiert oder deselektiert sind.
 * Weiterhin unterscheidet die Farbe der Tabellenzeilen ob ein Empfuenger von
 * einem Sender empfuengt (Gruen), von mehreren Sender empfuengt (Orange) oder
 * erst kuerzlich eine uenderung in der Art der Daten die empfangen wurden
 * festgestellt hat (Gelb)
 */
public class WrappingCellRenderer implements TableCellRenderer {

	private final ViewController ctrl;
	private final TableCellRenderer wrappedCellRenderer;

	/**
	 * Konstruktur.
	 * 
	 * @param cellRenderer
	 *            Erwartet den entsprechenden Zellen-Renderer.
	 * @param ctrl
	 *            Benoetigte Referenz zum GUI Controller.
	 */
	public WrappingCellRenderer(final TableCellRenderer cellRenderer,
			final ViewController ctrl) {
		super();
		wrappedCellRenderer = cellRenderer;
		this.ctrl = ctrl;
	}

	/**
	 * Ermittelt die Komponente des Tabellen-Zellen-Renderers.
	 * 
	 * @param table
	 *            Tabelle.
	 * @param value
	 *            Wert der Zelle.
	 * @param isSelected
	 *            Ob derzeit ausgewaehlt ja / nein.
	 * @param hasFocus
	 *            Ob derzeit fokussiert ja / nein.
	 * @param row
	 *            Zeile.
	 * @param column
	 *            Spalte.
	 * @return Komponente.
	 */
	@Override
	public Component getTableCellRendererComponent(final JTable table,
			final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		final Component rendererComponent = wrappedCellRenderer
				.getTableCellRendererComponent(table, value, isSelected,
						hasFocus, row, column);
		// [Daniel Becker] Funktioniert nur so
		try {
			if(!isSelected) {
				if(((Boolean)table.getModel().getValueAt(row, 0))
						.booleanValue()) {
					switch(ctrl.getMCData(row, ctrl.getSelectedTab())
							.getSenders()) {
						case SINGLE:
							rendererComponent.setBackground(Color.green);
							rendererComponent.setForeground(Color.black);
							break;
						case RECENTLY_CHANGED:
							rendererComponent.setBackground(Color.yellow);
							rendererComponent.setForeground(Color.black);
							break;
						case MULTIPLE:
							rendererComponent.setBackground(Color.orange);
							rendererComponent.setForeground(Color.black);
							break;
						case NONE:
							rendererComponent.setBackground(Color.green);
							rendererComponent.setForeground(Color.black);
							break;
						case NETWORK_ERROR:
							rendererComponent.setBackground(Color.red);
							rendererComponent.setForeground(Color.yellow);
							break;
						default:
					}
				} else {
					rendererComponent.setBackground(Color.white);
					rendererComponent.setForeground(Color.black);
				}
			} else {
				if(((Boolean)table.getModel().getValueAt(row, 0))
						.booleanValue()) {
					switch(ctrl.getMCData(row, ctrl.getSelectedTab())
							.getSenders()) {
						case SINGLE:
							rendererComponent
									.setBackground(new Color(0, 175, 0));
							rendererComponent.setForeground(Color.white);
							break;
						case RECENTLY_CHANGED:
							rendererComponent.setBackground(new Color(175, 175,
									0));
							rendererComponent.setForeground(Color.white);
							break;
						case MULTIPLE:
							rendererComponent.setBackground(new Color(217, 108,
									0));
							rendererComponent.setForeground(Color.white);
							break;
						case NONE:
							rendererComponent
									.setBackground(new Color(0, 175, 0));
							rendererComponent.setForeground(Color.white);
							break;
						case NETWORK_ERROR:
							rendererComponent.setBackground(Color.red);
							rendererComponent.setForeground(Color.yellow);
							break;
						default:
					}

				} else {
					rendererComponent.setBackground(Color.gray);
					rendererComponent.setForeground(Color.white);
				}
			}
		} catch(final Exception e) {
		}
		return rendererComponent;
	}

}
