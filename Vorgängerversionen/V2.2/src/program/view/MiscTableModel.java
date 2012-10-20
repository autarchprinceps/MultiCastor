package program.view;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import program.controller.Messages;
import program.controller.ViewController;
import program.data.MulticastData;
import program.data.MulticastData.Typ;
import program.model.NetworkAdapter;

/**
 * Das Tabellenmodel welches sich um die Anzeige der Daten in der Tabelle k�mmert.
 * @author Daniel Becker
 *
 */
@SuppressWarnings("serial")
public class MiscTableModel extends AbstractTableModel {
	private Typ typ=Typ.UNDEFINED;
	private boolean stateCheckboxEnabled = true;
	private ViewController ctrl;
	/**
	 * Hash Map welche die Suchrichtung für Spalten speichert
	 */
	private HashMap<String, Boolean> directions;
	public MiscTableModel(ViewController ctrl, Typ typ){;
		this.typ=typ;
		this.ctrl=ctrl;
		directions = new HashMap<String, Boolean>();
		for(int i = 0; i < getColumnCount(); i++) {
			directions.put(getColumnName(i), true);
		}
		
	}
	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast hinzugef�gt wird.
	 */
	public void insertUpdate(){
		fireTableRowsInserted(0, ctrl.getMCCount(typ));
	}
	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast gels�cht wird.
	 */
	public void deleteUpdate(){
		fireTableRowsDeleted(0, ctrl.getMCCount(typ));
	}
	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast ge�ndert wird.
	 */
	public void changeUpdate(){
		fireTableRowsUpdated(0, ctrl.getMCCount(typ));
	}
	@Override
	/**
	 * Funktion welche den Datentyp einer Spalte bestimmt
	 */
	public Class<?> getColumnClass(int columnIndex) {
		Class<?> ret = null;
		if(Typ.isSender(typ)){
			switch(columnIndex){
				case 0: ret = Boolean.class; break;
				case 1: ret = String.class; break;
				case 2: ret = String.class; break;
				case 3: ret = String.class; break;
				case 4: ret = Integer.class; break;
				case 5: ret = Integer.class; break;
				case 6: ret = String.class; break;
				case 7: ret = Integer.class; break;
				case 8: ret = String.class; break;
				case 9: ret = Long.class; break;
				case 10: ret = Integer.class; break;
				case 11: ret = Integer.class; break;
				default: ret = null; break;
			}
		}
		if(Typ.isReceiver(typ)){
			switch(columnIndex){
				case 0: ret = Boolean.class; break;
				case 1: ret = String.class; break;
				case 2: ret = String.class; break;
				case 3: ret = String.class; break;
				case 4: ret = Integer.class; break;
				case 5: ret = Integer.class; break;
				case 6: ret = String.class; break;
				case 7: ret = Integer.class; break;
				case 8: ret = Integer.class; break;
				case 9: ret = Integer.class; break;
				case 10: ret = Integer.class; break;
				case 11: ret = String.class; break;
				default: ret = null;
			}
		}
		return ret;
	}
	@Override
	/**
	 * Funktion welche die Anzahl an Spalten zur�ck gibt.
	 */
	public int getColumnCount() {
		int ret = 0;
		if(typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6){
			ret = 12;
		}
		else if(typ == Typ.RECEIVER_V4 || typ == Typ.RECEIVER_V6){
			ret = 12;
		}
		return ret;
	}
	public String getColumnName(int columnIndex) {
		String ret=""; //$NON-NLS-1$
		
		if(Typ.isSender(typ)){
			switch(columnIndex){
				case 0: ret += Messages.getString("TableS0"); break; //$NON-NLS-1$
				case 1: ret += Messages.getString("TableS1");break; //$NON-NLS-1$
				case 2:	ret += Messages.getString("TableS2"); break; //$NON-NLS-1$
				case 3:	ret += Messages.getString("TableS3"); break; //$NON-NLS-1$
				case 4:	ret += Messages.getString("TableS4");; break; //$NON-NLS-1$
				case 5: ret += Messages.getString("TableS5");; break; //$NON-NLS-1$
				case 6: ret += Messages.getString("TableS6");; break; //$NON-NLS-1$
				case 7:	ret += Messages.getString("TableS7");; break; //$NON-NLS-1$
				case 8: ret += Messages.getString("TableS8");; break; //$NON-NLS-1$
				case 9:	ret += Messages.getString("TableS9");; break; //$NON-NLS-1$
				case 10:ret += Messages.getString("TableS10");; break; //$NON-NLS-1$
				case 11: ret += Messages.getString("TableS11");; break; //$NON-NLS-1$
				default: ret += "error!"; break; //$NON-NLS-1$
			}
		}
		else if(Typ.isReceiver(typ)){
			switch(columnIndex){
			case 0: ret += Messages.getString("TableS0"); break; //$NON-NLS-1$
			case 1: ret += Messages.getString("TableS1");break; //$NON-NLS-1$
			case 2:	ret += Messages.getString("TableS2"); break; //$NON-NLS-1$
			case 3:	ret += Messages.getString("TableS3"); break; //$NON-NLS-1$
			case 4:	ret += Messages.getString("TableS4");; break; //$NON-NLS-1$
			case 5: ret += Messages.getString("TableS5");; break; //$NON-NLS-1$
			case 6: ret += Messages.getString("TableS6");; break; //$NON-NLS-1$
			case 7:	ret += Messages.getString("TableS7");; break; //$NON-NLS-1$
			case 8: ret += Messages.getString("TableR0");; break; //$NON-NLS-1$
			case 9:	ret += Messages.getString("TableR1");; break; //$NON-NLS-1$
			case 10:ret += Messages.getString("TableR2");; break; //$NON-NLS-1$
			case 11: ret += Messages.getString("TableR3");; break; //$NON-NLS-1$
				default: ret += "error!"; break; //$NON-NLS-1$
			}
		}
		return ret;
	}
	@Override
	/**
	 * Funktion welche die Anzahl an Tabellenreihen zur�ck gibt.
	 */
	public int getRowCount() {
		return ctrl.getMCCount(typ);
	}
	@Override
	/**
	 * Funktion welche die Daten f�r eine jeweilige Tabellenzelle anfordert
	 */
	public Object getValueAt(int rowIndex, int columnIndex){
		return getValueInMcData( ctrl.getMCData(rowIndex, typ), columnIndex);
	}
	
	private Object getValueInMcData(MulticastData data ,int columnIndex){
	Object ret = null;
	if(Typ.isSender(typ)){
		switch(columnIndex){
			case 0: ret=new Boolean(data.isActive()); break;
			case 1: ret= data.getTyp().toShortString(); break;
			case 2: ret=data.getSenderID(); break;
			case 3: {
				if(Typ.isMMRP(data.getTyp())){
					ret=NetworkAdapter.byteArrayToMac(data.getGroupMac());
				}else
					ret=data.getGroupIp().toString().substring(1); break;
			}
			case 4: ret=new Integer(data.getPacketRateDesired()); break;
			case 5: ret=new Integer(data.getPacketRateMeasured()); break;
			case 6: ret=new DecimalFormat("##0.000").format((data.getTraffic()/1024.0/1024.0*8.0)); break; //$NON-NLS-1$
			case 7: ret=new Integer(data.getUdpPort()); break;
			case 8: {
				if(Typ.isMMRP(data.getTyp())){
					ret=NetworkAdapter.byteArrayToMac(data.getSourceMac());
				}else
					ret=data.getSourceIp().toString().substring(1); break;
			}
			case 9: ret=new Long(data.getPacketCount());break;
			case 10:ret=new Integer(data.getTtl()); break;
			case 11: ret=new Integer(data.getPacketLength()); break;
			default: System.out.println("TABLEMODEL GETVALUE ERROR"); //$NON-NLS-1$
		}
	}
	else if(Typ.isReceiver(typ)){
		switch(columnIndex){
			case 0: ret=new Boolean(data.isActive()); break;
			case 1: ret= data.getTyp().toShortString(); break;
			case 2: ret=data.getSenderID(); break;
			case 3: {
				if(Typ.isMMRP(data.getTyp())){
					ret=NetworkAdapter.byteArrayToMac(data.getGroupMac());
				}else
					ret=data.getGroupIp().toString().substring(1); break;
			}
			case 4: ret=new Integer(data.getPacketRateDesired()); break;
			case 5: ret=new Integer(data.getPacketRateMeasured()); break;
			case 6: ret=new DecimalFormat("##0.000").format((data.getTraffic()/1024.0/1024.0*8.0)); break; //$NON-NLS-1$
			case 7: ret=new Integer(data.getUdpPort()); break;
			case 8: ret=new Integer(data.getPacketLossPerSecond()); break;
			case 9: ret=new Integer(data.getAverageInterruptionTime()); break;
			case 10: ret=new Integer(data.getNumberOfInterruptions()); break;
			case 11: ret = data.getPacketSource().toString(); break;
			default: 
		}
	}
	return ret;
}
	@Override
	/**
	 * Funktion welche angibt ob eine Zelle in der Tabelle editierbar ist oder nicht
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(columnIndex==0 && stateCheckboxEnabled){
			return true;
		}
		else{
			return false;
		}
	}
	@Override
	/**
	 * Funktion welche angibt was nach dem editieren einer Zelle geschehen soll
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		MulticastData data = ctrl.getMCData(rowIndex, typ);
		if(Typ.isSender(typ)){
			switch(columnIndex){
				case 0:
					if((Boolean)aValue){
						ctrl.startMC(rowIndex, typ);
					}
					else{
						ctrl.stopMC(rowIndex, typ);
					}break;
				case 8:	data.setPacketCount(((Long)aValue).longValue()); break;
				default: System.out.println("Table Model Error - SetValueAt() - SENDER"); //$NON-NLS-1$

			}
		}
		else if(Typ.isReceiver(typ)){
			switch(columnIndex){
				case 0:
					if((Boolean)aValue){
						ctrl.startMC(rowIndex, typ);
					}
					else{
						ctrl.stopMC(rowIndex, typ);
					}break;
				default: System.out.println("Table Model Error - SetValueAt() - RECEIVER"); //$NON-NLS-1$
			}
		}
	}
	public boolean isStateCheckboxEnabled() {
		return stateCheckboxEnabled;
	}
	public void setStateCheckboxEnabled(boolean stateCheckboxEnabled) {
		this.stateCheckboxEnabled = stateCheckboxEnabled;
	}
	public void sort(Vector<MulticastData> mc, int col, String colName) {
		if(colName.charAt(0)=='+'||colName.charAt(0)=='-') colName=colName.substring(2);
		boolean dir = directions.get(colName);
		Sorter s = new Sorter(colName, col, dir, typ);
		if(mc.size() > 0) {
			Collections.sort(mc, s);
		}		
		String headerValue;
		for(int i = 0; i<ctrl.getTable(typ).getColumnCount(); i++){
			headerValue=""; //$NON-NLS-1$
			if(ctrl.getUserInputData().getOriginalIndex(col,typ)==ctrl.getUserInputData().getOriginalIndex(i,typ)){
				if(dir){
					headerValue="+ "; //$NON-NLS-1$
				}else {
					headerValue="- "; //$NON-NLS-1$
				}
			}
			headerValue+=getColumnName(ctrl.getUserInputData().getOriginalIndex(i,typ));
			ctrl.getTable(typ).getColumnModel().getColumn(i).setHeaderValue(headerValue);
			
		}
		boolean element = directions.get(colName) == true ? false : true;
		directions.put(colName, element);
		fireTableDataChanged();
	}	
	
	class Sorter implements Comparator<MulticastData> {
		String colName;
		int col;
		boolean direction;
		Typ typ;
		
		public Sorter(String colName, int col, boolean direction, Typ typ) {
			this.col = col;
			this.colName = colName;
			this.direction = direction;
			this.typ = typ;
		}
		@Override
		public int compare(MulticastData m1, MulticastData m2) {
			
			String s1 = "" + getValueInMcData(m1, ctrl.getUserInputData().getOriginalIndex(col,typ)); //$NON-NLS-1$
			String s2 = "" + getValueInMcData(m2, ctrl.getUserInputData().getOriginalIndex(col,typ)); //$NON-NLS-1$
			if(direction == true) {
				return s1.compareTo(s2);
			} else if(direction == false){
				return s2.compareTo(s1);
			} else {
				return 0;
			}
		}
		
	}
	  
	
}