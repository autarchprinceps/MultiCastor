package program.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import program.controller.Messages;
import program.data.MulticastData;

public class ImportTableModel extends AbstractTableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 4720551130945742448L;
	private Vector<MulticastData> list = new Vector<MulticastData>();
	public static final int ASC = 1;
	public static final int DES = -1;
	public static final int NOT = 0;

	private String[] headers = {Messages.getString("ImportTableModel.0"), Messages.getString("ImportTableModel.1"), Messages.getString("ImportTableModel.2"), Messages.getString("ImportTableModel.3"), Messages.getString("ImportTableModel.4"), Messages.getString("ImportTableModel.5"), Messages.getString("ImportTableModel.6")}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$



	public ImportTableModel(Vector<MulticastData> list) {
		this.list=list;
	}

	@Override
	public int getColumnCount() {
		return 7;
	}

	@Override
	public String getColumnName(int paramInt) {
		return headers[paramInt];
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		switch(arg1) {
			case 0:
				return list.get(arg0).isActive();
			case 1:
				return list.get(arg0).getGroupAddress();
			case 2:
				return list.get(arg0).getUdpPort();
			case 3:
				return list.get(arg0).getSourceAddress();
			case 4:
				return list.get(arg0).getTtl();
			case 5:
				return list.get(arg0).getPacketLength();
			case 6:
				return list.get(arg0).getPacketRateDesired();
			default:
				return null;
		}

	}

	@Override
	public boolean isCellEditable(int row, int col) {
	      if (col == 0) {
	        return true;
	      } else {
	        return false;
	      }
    }

	@Override
	 public Class<?> getColumnClass(int c) {
	      return getValueAt(0, c).getClass();
	 }

	/**
	 * Changes value at selected position.
	 */
	@Override
	public void setValueAt(Object value, int row, int col) {
		list.get(row).setActive(!list.get(row).isActive());
		fireTableCellUpdated(row, 0);
	}

	/**
	 * Add value in to the table
	 * @param d
	 */
	public void addValue(MulticastData d) {
		list.add(d);
		fireTableDataChanged();
	}

	/**
	 * Returns the selected values in the table.
	 * @return Vector<MulticastData>
	 * 			selected values in the table
	 */
	public Vector<MulticastData> getImportFields() {
		Vector<MulticastData> ret = new Vector<MulticastData>();
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).isActive()) {
				list.get(i).setActive(false);
				ret.add(list.get(i));
			}
		}
		return ret;
	}

	/**
	 * Selects all values in the table.
	 */
	public void selectAll() {
		for(MulticastData data : list) {
			data.setActive(true);
		}
		fireTableDataChanged();
	}

	public int getSelectedCount() {
		int count = 0;
		for(MulticastData data : list) {
			if(data.isActive()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Deselects all values in the table.
	 */
	public void deselectAll() {
		for(MulticastData data : list) {
			data.setActive(false);
		}
		fireTableDataChanged();
	}

	/**
	 * Sort table by column value.
	 */
	public void sortByColumn(int col, int dir) {
		Sorter s = new Sorter(col, dir);
		Collections.sort(this.list, s);
		fireTableDataChanged();
	}

	private Object getDataByCol(MulticastData d, int col) {
		switch(col) {
			case 0:
				return d.isActive();
			case 1:
				return d.getGroupIp();
			case 2:
				return d.getUdpPort();
			case 3:
				return d.getSourceIp();
			case 4:
				return d.getTtl();
			case 5:
				return d.getPacketLength();
			case 6:
				return d.getPacketRateDesired();
			default:
				return null;
		}
	}

	class Sorter implements Comparator<MulticastData> {
		int col;
		int direction;

		public Sorter(int col, int direction) {
			this.col = col;
			this.direction = direction;
		}
		@Override
		public int compare(MulticastData m1, MulticastData m2) {

			String s1 = "" + getDataByCol(m1, col); //$NON-NLS-1$
			String s2 = "" + getDataByCol(m2, col); //$NON-NLS-1$

			if(direction == 1) {
				return s1.compareTo(s2);
			} else if(direction == -1){
				return s2.compareTo(s1);
			} else {
				return 0;
			}
		}

	}

}
