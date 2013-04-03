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
		fireTableRowsUpdated(0, ctrl.getMCCount(typ));
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast gelsuecht wird.
	 */
	public void deleteUpdate() {
		fireTableRowsDeleted(0, ctrl.getMCCount(typ));
	}

	@Override
	/**
	 * Funktion welche den Datentyp einer Spalte bestimmt
	 */
	public Class<?> getColumnClass(final int columnIndex) {
		Class<?> ret = null;
		// V1.5: L2 und L3 hinzugefuegt
		if((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			switch(columnIndex) {
				case 0:
					ret = Boolean.class;
					break;
				case 1:
					ret = String.class;
					break;
				case 2:
					ret = String.class;
					break;
				case 3:
					ret = String.class;
					break;
				case 4:
					ret = Integer.class;
					break;
				case 5:
					ret = Integer.class;
					break;
				case 6:
					ret = String.class;
					break;
				case 7:
					ret = Long.class;
					break;
				case 8:
					ret = Integer.class;
					break;
				case 9:
					ret = Integer.class;
					break;
				case 10:
					ret = Integer.class;
					break;
				default:
					ret = null;
					break;
			}
		}
		// V1.5: L2 und L3 hinzugefuegt
		if((typ == Typ.L2_RECEIVER) || (typ == Typ.L3_RECEIVER)) {
			switch(columnIndex) {
				case 0:
					ret = Boolean.class;
					break;
				case 1:
					ret = String.class;
					break;
				case 2:
					ret = String.class;
					break;
				case 3:
					ret = String.class;
					break;
				case 4:
					ret = Integer.class;
					break;
				case 5:
					ret = Integer.class;
					break;
				case 6:
					ret = String.class;
					break;
				case 7:
					ret = Integer.class;
					break;
				case 8:
					ret = Integer.class;
					break;
				case 9:
					ret = Integer.class;
					break;
				case 10:
					ret = Integer.class;
					break;
				default:
					ret = null;
			}
		}
		return ret;
	}

	@Override
	/**
	 * Funktion welche die Anzahl an Spalten zurueck gibt.
	 */
	public int getColumnCount() {
		int ret = 0;
		// V1.5: L2 und L3 hinzugefuegt
		switch(typ) {
			case L2_SENDER:
				ret = 9;
				break;
			case L2_RECEIVER:
				ret = 10;
				break;
			case L3_RECEIVER:
			case L3_SENDER:
				ret = 11;
				break;
		}

		return ret;
	}

	@Override
	/**
	 * Funktion welche den Namen einer Spalte bestimmt
	 */
	public String getColumnName(final int columnIndex) {
		String ret = null;
		// V1.5: L2 und L3 hinzugefuegt
		if((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			switch(columnIndex) {
				case 0:
					ret = "STATE";
					break;
				case 1:
					ret = "ID";
					break;
				case 2:
					ret = (typ == Typ.L2_SENDER) ? "GRP MAC" : "GRP IP";
					break;
				case 3:
					ret = (typ == Typ.L2_SENDER) ? "SRC MAC" : "SRC IP";
					break;
				case 4:
					ret = "D RATE";
					break;
				case 5:
					ret = "M RATE";
					break;
				case 6:
					ret = "Mbit/s";
					break;
				case 7:
					ret = "#SENT";
					break;
				case 8:
					ret = "LENGTH";
					break;
				case 9:
					ret = "TTL";
					break;
				case 10:
					ret = "PORT";
					break;
				default:
					ret = "error!";
					break;
			}
		}
		// V1.5: L2 und L3 hinzugefuegt
		else if((typ == Typ.L2_RECEIVER) || (typ == Typ.L3_RECEIVER)) {
			switch(columnIndex) {
				case 0:
					ret = "STATE";
					break;
				case 1:
					ret = "ID";
					break;
				case 2:
					ret = (typ == Typ.L2_RECEIVER) ? "GRP MAC" : "GRP IP";
					break;
				case 3:
					ret = (typ == Typ.L2_RECEIVER) ? "SRC MAC" : "SRC IP";
					break;
				case 4:
					ret = "D RATE";
					break;
				case 5:
					ret = "M RATE";
					break;
				case 6:
					ret = "Mbit/s";
					break;
				case 7:
					ret = "LOSS/S";
					break;
				// Changed this 2 Values to Lost and Received
				// case 8: ret = "AVG INT"; break;
				// case 9: ret = "#INT"; break;
				case 8:
					ret = "LOST";
					break;
				case 9:
					ret = "RCVD";
					break;
				case 10:
					ret = "PORT";
					break;
				default:
					ret = "error!";
					break;
			}
		}
		return ret;
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
		Object ret = null;

		// V1.5: L2 und L3 hinzugefuegt
		if((typ == Typ.L2_SENDER) || (typ == Typ.L3_SENDER)) {
			switch(columnIndex) {
				case 0:
					ret = new Boolean(data.isActive());
					break;
				case 1:
					ret = data.getSenderID();
					break;
				case 2:
					ret = (typ == Typ.L2_SENDER) ? data
							.getMmrpGroupMacAsString() : data.getGroupIp()
							.toString().substring(1);
					break;
				case 3:
					ret = (typ == Typ.L2_SENDER) ? data
							.getMmrpSourceMacAsString() : data.getSourceIp()
							.toString().substring(1);
					break;
				case 4:
					ret = new Integer(data.getPacketRateDesired());
					break;
				case 5:
					ret = new Integer(data.getPacketRateMeasured());
					break;
				case 6:
					ret = new DecimalFormat("##0.000").format(((data
							.getTraffic() / 1024.0 / 1024.0) * 8.0));
					break;
				case 7:
					ret = new Long(data.getPacketCount());
					break;
				case 8:
					ret = new Integer(data.getPacketLength());
					break;
				case 9:
					ret = new Integer(data.getTtl());
					break;
				case 10:
					ret = new Integer(data.getUdpPort());
					break;
				default:
					System.out.println("TABLEMODEL GETVALUE ERROR");
			}
		}
		// V1.5: L2 und L3 hinzugefuegt
		else if((typ == Typ.L2_RECEIVER) || (typ == Typ.L3_RECEIVER)) {
			switch(columnIndex) {
				case 0:
					ret = new Boolean(data.isActive());
					break;
				case 1:
					ret = data.getSenderID();
					break;
				case 2:
					ret = (typ == Typ.L2_RECEIVER) ? data
							.getMmrpGroupMacAsString() : data.getGroupIp()
							.toString().substring(1);
					break;
				case 3:
					ret = (typ == Typ.L2_RECEIVER) ? data
							.getMmrpSourceMacAsString() : data.getSourceIp()
							.toString().substring(1);
					break;
				case 4:
					ret = new Integer(data.getPacketRateDesired());
					break;
				case 5:
					ret = new Integer(data.getPacketRateMeasured());
					break;
				case 6:
					ret = new DecimalFormat("##0.000").format(((data
							.getTraffic() / 1024.0 / 1024.0) * 8.0));
					break;
				case 7:
					ret = new Integer(data.getPacketLossPerSecond());
					break;
				case 8:
					ret = new Integer(data.getLostPackets());
					break;
				case 9:
					ret = new Integer(data.getReceivedPackets());
					break;
				case 10:
					ret = new Integer(data.getUdpPort());
					break;
				// V1.5 [FH] Changed to network interface
				default:
					System.out.println("TABLEMODEL GETVALUE ERROR");
			}
		}
		return ret;
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
