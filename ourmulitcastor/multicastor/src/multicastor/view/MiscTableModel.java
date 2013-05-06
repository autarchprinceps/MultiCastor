package multicastor.view;

import java.text.DecimalFormat;
import javax.swing.table.AbstractTableModel;

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

	public MiscTableModel(final ViewController ctrl, final Typ typ) {
		this.typ = typ;
		this.ctrl = ctrl;
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast geuendert wird.
	 */
	public void changeUpdate() {
		int lastIndex = ctrl.getMCCount(typ) - 1;
		if (lastIndex >= 0) {
			fireTableRowsUpdated(0, lastIndex);
		}
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast gelsuecht wird.
	 */
	public void deleteUpdate() {
		int lastIndex = ctrl.getMCCount(typ) - 1;
		if (lastIndex >= -1) {
			fireTableRowsDeleted(0, lastIndex);
		}
	}

	@Override
	/**
	 * Funktion welche den Datentyp einer Spalte bestimmt
	 */
	public Class<?> getColumnClass(final int columnIndex) {
		if ((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			switch (columnIndex) {
			case 0:
				return Boolean.class;
			case 1:
			case 2:
			case 3:
			case 4:
			case 7:
				return String.class;
			case 5:
			case 6:
			case 9:
			case 10:
			case 11:
				return Integer.class;
			case 8:
				return Long.class;
			default:
				return null;
			}
		}
		// V1.5: L2 und L3 hinzugefuegt
		if ((typ == Typ.L2_RECEIVER) || (typ == Typ.L3_RECEIVER)) {
			switch (columnIndex) {
			case 0:
				return Boolean.class;
			case 1:
			case 2:
			case 3:
			case 4:
			case 7:
				return String.class;
			case 5:
			case 6:
			case 8:
			case 9:
			case 10:
			case 11:
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
		switch (typ) {
		case L2_SENDER:
			return 10;
		case L2_RECEIVER:
			return 11;
		case L3_RECEIVER:
		case L3_SENDER:
			return 12;
		default:
			return 0;
		}
	}

	@Override
	/**
	 * Funktion welche den Namen einer Spalte bestimmt
	 */
	public String getColumnName(final int columnIndex) {
		if ((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			switch (columnIndex) {
			case 0:
				return "STATE";
			case 1:
				return "ID";
			case 2: 
				return "PROTOCOL";
			case 3:
				return (typ == Typ.L2_SENDER) ? "GRP MAC" : "GRP IP";
			case 4:
				return (typ == Typ.L2_SENDER) ? "SRC MAC" : "SRC IP";
			case 5:
				return "D RATE";
			case 6:
				return "M RATE";
			case 7:
				return "Mbit/s";
			case 8:
				return "#SENT";
			case 9:
				return "LENGTH";
			case 10:
				return "TTL";
			case 11:
				return (typ == Typ.L2_SENDER) ? "PROTOCOL" : "PORT";
			default:
				return "error!";
			}
		} else if ((typ == Typ.L2_RECEIVER) || (typ == Typ.L3_RECEIVER)) {
			switch (columnIndex) {
			case 0:
				return "STATE";
			case 1:
				return "ID";
			case 2:
				return "PROTOCOL";
			case 3:
				return (typ == Typ.L2_RECEIVER) ? "GRP MAC" : "GRP IP";
			case 4:
				return (typ == Typ.L2_RECEIVER) ? "SRC MAC" : "SRC IP";
			case 5:
				return "D RATE";
			case 6:
				return "M RATE";
			case 7:
				return "Mbit/s";
			case 8:
				return "LOSS/S";
			case 9:
				return "LOST";
			case 10:
				return "RCVD";
			case 11:
				return (typ == Typ.L2_RECEIVER) ? "PROTOCOL" : "PORT";
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
                if(data == null) {
                    return null;
                }
		if ((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			switch (columnIndex) {
			case 0:
				return new Boolean(data.isActive());
			case 1:
				return data.getSenderID();
			case 2:
				return new String(data.getProtocol().toString());
			case 3:
				return (typ == Typ.L2_SENDER) ? data.getMmrpGroupMacAsString()
						: data.getGroupIp().toString().substring(1);
			case 4:
				return (typ == Typ.L2_SENDER) ? data.getMmrpSourceMacAsString()
						: data.getSourceIp().toString().substring(1);
			case 5:
				return new Integer(data.getPacketRateDesired());
			case 6:
				return new Integer(data.getPacketRateMeasured());
			case 7:
				return new DecimalFormat("##0.000")
						.format(((data.getTraffic() / 1024.0 / 1024.0) * 8.0));
			case 8:
				return new Long(data.getPacketCount());
			case 9:
				return new Integer(data.getPacketLength());
			case 10:
				return new Integer(data.getTtl());
			case 11:
				return new Integer(data.getUdpPort());
			default:
				System.out.println("TABLEMODEL GETVALUE ERROR");
			}
		} else if ((typ == Typ.L2_RECEIVER) || (typ == Typ.L3_RECEIVER)) {
			switch (columnIndex) {
			case 0:
				return new Boolean(data.isActive());
			case 1:
				return data.getSenderID();
			case 2:
				return data.getProtocol().toString();
			case 3:
				return (typ == Typ.L2_RECEIVER) ? data
						.getMmrpGroupMacAsString() : data.getGroupIp()
						.toString().substring(1);
			case 4:
				return (typ == Typ.L2_RECEIVER) ? data
						.getMmrpSourceMacAsString() : data.getSourceIp()
						.toString().substring(1);
			case 5:
				return new Integer(data.getPacketRateDesired());
			case 6:
				return new Integer(data.getPacketRateMeasured());
			case 7:
				return new DecimalFormat("##0.000")
						.format(((data.getTraffic() / 1024.0 / 1024.0) * 8.0));
			case 8:
				return new Integer(data.getPacketLossPerSecond());
			case 9:
				return new Integer(data.getLostPackets());
			case 10:
				return new Integer(data.getReceivedPackets());
			case 11:
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
		int lastIndex = ctrl.getMCCount(typ) - 1;
		if (lastIndex >= 0) {
			fireTableRowsInserted(0, lastIndex);
		}
	}

	@Override
	/**
	 * Funktion welche angibt ob eine Zelle in der Tabelle editierbar ist oder nicht
	 */
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		return ((columnIndex == 0) && stateCheckboxEnabled);
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
		if ((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			switch (columnIndex) {
			case 0:
				if ((Boolean) aValue) {
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
			case 9:
				data.setPacketCount(((Long) aValue).longValue());
				System.out.println("SET!!");
				break;
			case 10:
			case 11:
			default:
				System.out.println("Table Model Error - SetValueAt() - SENDER");
			}
		}
		// V1.5: L2 und L3 hinzugefuegt
		else if ((typ == Typ.L2_RECEIVER) || (typ == Typ.L3_RECEIVER)) {
			switch (columnIndex) {
			case 0:
				if ((Boolean) aValue) {
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
