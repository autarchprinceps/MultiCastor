package multicastor.view;

import java.text.DecimalFormat;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import multicastor.controller.ViewController;
import multicastor.data.MulticastData;
import multicastor.data.MulticastData.Typ;


/**
 * Das Tabellenmodel welches sich um die Anzeige der Daten in der Tabelle
 * kuemmert.
 */
@SuppressWarnings("serial")
public class MiscTableModel extends AbstractTableModel {
	private final ViewController ctrl;
	private boolean stateCheckboxEnabled = true;
	private Typ typ = Typ.UNDEFINED;
	
	// TODO inject into JTable instance
	private TableRowSorter<MiscTableModel> sorter;

	/**
	 * @return sorter
	 */
	public TableRowSorter<MiscTableModel> getSorter() {
		return sorter;
	}

	public MiscTableModel(final ViewController ctrl, final Typ typ) {
		this.typ = typ;
		this.ctrl = ctrl;
		sorter = new TableRowSorter<MiscTableModel>(this);
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast geuendert wird.
	 */
	public void changeUpdate() {
		try {
			fireTableRowsUpdated(0, ctrl.getMCCount(typ));
		} catch(IndexOutOfBoundsException e) {
			// TODO			
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast gelsuecht wird.
	 */
	public void deleteUpdate() {
		try {
			fireTableRowsDeleted(0, ctrl.getMCCount(typ));
		} catch(IndexOutOfBoundsException e) {
			// TODO
		}
	}

	@Override
	/**
	 * Funktion welche den Datentyp einer Spalte bestimmt
	 */
	public Class<?> getColumnClass(final int columnIndex) {
		if((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			switch(columnIndex) {
				case 0:
					return Boolean.class;
				case 1:
				case 2:
				case 3:
				case 6:
					return String.class;
				case 4:
				case 5:
				case 8:
				case 9:
				case 10:
					return Integer.class;
				case 7:
					return Long.class;
				default:
					return null;
			}
		}
		// V1.5: L2 und L3 hinzugefuegt
		if((typ == Typ.L2_RECEIVER) || (typ == Typ.L3_RECEIVER)) {
			switch(columnIndex) {
				case 0:
					return Boolean.class;
				case 1:
				case 2:
				case 3:
				case 6:
					return String.class;
				case 4:
				case 5:
				case 7:
				case 8:
				case 9:
				case 10:
					return Integer.class;
				default:
					return null;
			}
		}
		return null;
	}

	@Override
	/**
	 * Funktion welche die Anzahl an Spalten zurueck gibt.
	 */
	public int getColumnCount() {
		switch(typ) {
			case L2_SENDER:
				return 9;
			case L2_RECEIVER:
				return 10;
			case L3_RECEIVER:
			case L3_SENDER:
				return 11;
			default:
				return 0;
		}
	}

	@Override
	/**
	 * Funktion welche den Namen einer Spalte bestimmt
	 */
	public String getColumnName(final int columnIndex) {
		if((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			switch(columnIndex) {
				case 0:
					return "STATE";
				case 1:
					return "ID";
				case 2:
					return (typ == Typ.L2_SENDER) ? "GRP MAC" : "GRP IP";
				case 3:
					return (typ == Typ.L2_SENDER) ? "SRC MAC" : "SRC IP";
				case 4:
					return "D RATE";
				case 5:
					return "M RATE";
				case 6:
					return "Mbit/s";
				case 7:
					return "#SENT";
				case 8:
					return "LENGTH";
				case 9:
					return "TTL";
				case 10:
					return "PORT";
				default:
					return "error!";
			}
		}
		else if((typ == Typ.L2_RECEIVER) || (typ == Typ.L3_RECEIVER)) {
			switch(columnIndex) {
				case 0:
					return "STATE";
				case 1:
					return "ID";
				case 2:
					return (typ == Typ.L2_RECEIVER) ? "GRP MAC" : "GRP IP";
				case 3:
					return (typ == Typ.L2_RECEIVER) ? "SRC MAC" : "SRC IP";
				case 4:
					return "D RATE";
				case 5:
					return "M RATE";
				case 6:
					return "Mbit/s";
				case 7:
					return "LOSS/S";
				case 8:
					return "LOST";
				case 9:
					return "RCVD";
				case 10:
					return "PORT";
				default:
					return "error!";
			}
		}
		return null;
	}

	@Override
	/**
	 * Funktion welche die Anzahl an Tabellenreihen zurueck gibt.
	 */
	public int getRowCount() {
		return ctrl.getMCCount(typ);
	}

	@Override
	/**
	 * Funktion welche die Daten fuer eine jeweilige Tabellenzelle anfordert
	 */
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final MulticastData data = ctrl.getMCData(rowIndex, typ);
		if((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			switch(columnIndex) {
				case 0:
					return new Boolean(data.isActive());
				case 1:
					return data.getSenderID();
				case 2:
					return (typ == Typ.L2_SENDER) ? data
							.getMmrpGroupMacAsString() : data.getGroupIp()
							.toString().substring(1);
				case 3:
					return (typ == Typ.L2_SENDER) ? data
							.getMmrpSourceMacAsString() : data.getSourceIp()
							.toString().substring(1);
				case 4:
					return new Integer(data.getPacketRateDesired());
				case 5:
					return new Integer(data.getPacketRateMeasured());
				case 6:
					return new DecimalFormat("##0.000").format(((data
							.getTraffic() / 1024.0 / 1024.0) * 8.0));
				case 7:
					return new Long(data.getPacketCount());
				case 8:
					return new Integer(data.getPacketLength());
				case 9:
					return new Integer(data.getTtl());
				case 10:
					return new Integer(data.getUdpPort());
				default:
					System.out.println("TABLEMODEL GETVALUE ERROR");
			}
		}
		else if((typ == Typ.L2_RECEIVER) || (typ == Typ.L3_RECEIVER)) {
			switch(columnIndex) {
				case 0:
					return new Boolean(data.isActive());
				case 1:
					return data.getSenderID();
				case 2:
					return (typ == Typ.L2_RECEIVER) ? data
							.getMmrpGroupMacAsString() : data.getGroupIp()
							.toString().substring(1);
				case 3:
					return (typ == Typ.L2_RECEIVER) ? data
							.getMmrpSourceMacAsString() : data.getSourceIp()
							.toString().substring(1);
				case 4:
					return new Integer(data.getPacketRateDesired());
				case 5:
					return new Integer(data.getPacketRateMeasured());
				case 6:
					return new DecimalFormat("##0.000").format(((data
							.getTraffic() / 1024.0 / 1024.0) * 8.0));
				case 7:
					return new Integer(data.getPacketLossPerSecond());
				case 8:
					return new Integer(data.getLostPackets());
				case 9:
					return new Integer(data.getReceivedPackets());
				case 10:
					return new Integer(data.getUdpPort());
				default:
					System.out.println("TABLEMODEL GETVALUE ERROR");
			}
		}
		return null;
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast hinzugefuegt wird.
	 */
	public void insertUpdate() {
		fireTableRowsInserted(0, ctrl.getMCCount(typ));

	}

	@Override
	/**
	 * Funktion welche angibt ob eine Zelle in der Tabelle editierbar ist oder nicht
	 */
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		if((columnIndex == 0) && stateCheckboxEnabled) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isStateCheckboxEnabled() {
		return stateCheckboxEnabled;
	}

	public void setStateCheckboxEnabled(final boolean stateCheckboxEnabled) {
		this.stateCheckboxEnabled = stateCheckboxEnabled;
	}

	@Override
	/**
	 * Funktion welche angibt was nach dem editieren einer Zelle geschehen soll
	 */
	public void setValueAt(final Object aValue, final int rowIndex,
			final int columnIndex) {
		final MulticastData data = ctrl.getMCData(rowIndex, typ);
		// V1.5: L2 und L3 hinzugefuegt
		if((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			switch(columnIndex) {
				case 0:
					if((Boolean)aValue) {
						ctrl.startMC(rowIndex, typ);
						ctrl.setTBactive(ctrl.getSelectedRows(typ), typ);
					} else {
						ctrl.stopMC(rowIndex, typ);
						ctrl.setTBactive(ctrl.getSelectedRows(typ), typ);
					}
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
					data.setPacketCount(((Long)aValue).longValue());
					System.out.println("SET!!");
				case 9:
				case 10:
				default:
					System.out
							.println("Table Model Error - SetValueAt() - SENDER");
			}
		}
		// V1.5: L2 und L3 hinzugefuegt
		else if((typ == Typ.L2_RECEIVER) || (typ == Typ.L3_RECEIVER)) {
			switch(columnIndex) {
				case 0:
					if((Boolean)aValue) {
						ctrl.startMC(rowIndex, typ);
						ctrl.setTBactive(ctrl.getSelectedRows(typ), typ);
					} else {
						ctrl.stopMC(rowIndex, typ);
						ctrl.setTBactive(ctrl.getSelectedRows(typ), typ);
					}
					break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				default:
					System.out
							.println("Table Model Error - SetValueAt() - RECEIVER");
			}
		}
	}

}
