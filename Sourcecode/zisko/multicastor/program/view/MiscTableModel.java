package zisko.multicastor.program.view;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData;
import zisko.multicastor.program.data.MulticastData.Typ;

/**
 * Das Tabellenmodel welches sich um die Anzeige der Daten in der Tabelle kümmert.
 * @author Daniel Becker
 *
 */
public class MiscTableModel extends AbstractTableModel {
	private Typ typ=Typ.UNDEFINED;
	private boolean stateCheckboxEnabled = true;
	private ViewController ctrl;
	public MiscTableModel(ViewController ctrl, Typ typ){;
		this.typ=typ;
		this.ctrl=ctrl;
	}
	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast hinzugefügt wird.
	 */
	public void insertUpdate(){
		fireTableRowsInserted(0, ctrl.getMCCount(typ));
	}
	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast gelsöcht wird.
	 */
	public void deleteUpdate(){
		fireTableRowsDeleted(0, ctrl.getMCCount(typ));
	}
	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast geändert wird.
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
		if(typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6){
			switch(columnIndex){
				case 0: ret = Boolean.class; break;
				case 1: ret = String.class; break;
				case 2: ret = String.class; break;
				case 3: ret = Integer.class; break;
				case 4: ret = Integer.class; break;
				case 5: ret = String.class; break;
				case 6: ret = Integer.class; break;
				case 7: ret = String.class; break;
				case 8: ret = Long.class; break;
				case 9: ret = Integer.class; break;
				case 10: ret = Integer.class; break;
				default: ret = null; break;
			}  	
		}
		if(typ == Typ.RECEIVER_V4 || typ == Typ.RECEIVER_V6){
			switch(columnIndex){
				case 0: ret = Boolean.class; break;
				case 1: ret = String.class; break;
				case 2: ret = String.class; break;
				case 3: ret = Integer.class; break;
				case 4: ret = Integer.class; break;
				case 5: ret = String.class; break;
				case 6: ret = Integer.class; break;
				case 7: ret = Integer.class; break;
				case 8: ret = Integer.class; break;
				case 9: ret = Integer.class; break;
				case 10: ret = String.class; break;
				default: ret = null;
			}  	
		}
		return ret;
	}
	@Override
	/**
	 * Funktion welche die Anzahl an Spalten zurück gibt.
	 */
	public int getColumnCount() {
		int ret = 0;
		if(typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6){
			ret = 11;
		}
		else if(typ == Typ.RECEIVER_V4 || typ == Typ.RECEIVER_V6){
			ret = 11;
		}
		return ret;
	}
	@Override
	/**
	 * Funktion welche den Namen einer Spalte bestimmt
	 */
	public String getColumnName(int columnIndex) {
		String ret=null;
		if(typ==Typ.SENDER_V4 || typ == Typ.SENDER_V6){
			switch(columnIndex){
				case 0: ret = "STATE"; break; 
				case 1:	ret = "ID"; break; 		
				case 2:	ret = "GRP IP"; break;
				case 3:	ret = "D RATE"; break;
				case 4: ret = "M RATE"; break;
				case 5: ret = "Mbit/s"; break;
				case 6:	ret = "PORT"; break;
				case 7: ret = "SRC IP"; break;			
				case 8:	ret = "#SENT"; break;
				case 9:	ret = "TTL"; break;
				case 10: ret = "LENGTH"; break;
				default: ret = "error!"; break;
			}
		}
		else if(typ == Typ.RECEIVER_V4 || typ == Typ.RECEIVER_V6){
			switch(columnIndex){
				case 0: ret = "STATE"; break;
				case 1:	ret = "ID"; break; 		
				case 2:	ret = "GRP IP"; break;
				case 3:	ret = "D RATE"; break;
				case 4:	ret = "M RATE"; break;
				case 5: ret = "Mbit/s"; break;
				case 6:	ret = "PORT"; break;			
				case 7:	ret = "LOSS/S"; break;
				case 8:	ret = "AVG INT"; break;
				case 9:	ret = "#INT"; break;
				case 10: ret = "SRC"; break;
				default: ret = "error!"; break;
			}
		}
		return ret;
	}
	@Override
	/**
	 * Funktion welche die Anzahl an Tabellenreihen zurück gibt.
	 */
	public int getRowCount() {
		return ctrl.getMCCount(typ);
	}
	@Override
	/**
	 * Funktion welche die Daten für eine jeweilige Tabellenzelle anfordert
	 */
	public Object getValueAt(int rowIndex, int columnIndex){
		MulticastData data = ctrl.getMCData(rowIndex, typ);
		Object ret = null;
		if(typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6){
			switch(columnIndex){
				case 0: ret=new Boolean(data.isActive()); break; 
				case 1: ret=data.getSenderID(); break;
				case 2: ret=data.getGroupIp().toString().substring(1); break;
				case 3: ret=new Integer(data.getPacketRateDesired()); break;
				case 4: ret=new Integer(data.getPacketRateMeasured()); break;
				case 5: ret=new DecimalFormat("##0.000").format((data.getTraffic()/1024.0/1024.0*8.0)); break;
				case 6: ret=new Integer(data.getUdpPort()); break;
				case 7: ret=data.getSourceIp().toString().substring(1); break;
				case 8: ret=new Long(data.getPacketCount());break;
				case 9: ret=new Integer(data.getTtl()); break;
				case 10: ret=new Integer(data.getPacketLength()); break;
				default: System.out.println("TABLEMODEL GETVALUE ERROR");
			}
		}
		else if(typ == Typ.RECEIVER_V4 || typ == Typ.RECEIVER_V6){
			switch(columnIndex){
				case 0: ret=new Boolean(data.isActive()); break; 
				case 1: ret=data.getSenderID(); break;
				case 2: ret=data.getGroupIp().toString().substring(1); break;
				case 3: ret=new Integer(data.getPacketRateDesired()); break;
				case 4: ret=new Integer(data.getPacketRateMeasured()); break;
				case 5: ret=new DecimalFormat("##0.000").format((data.getTraffic()/1024.0/1024.0*8.0)); break;
				case 6: ret=new Integer(data.getUdpPort()); break;
				case 7: ret=new Integer(data.getPacketLossPerSecond()); break;
				case 8: ret=new Integer(data.getAverageInterruptionTime()); break;
				case 9: ret=new Integer(data.getNumberOfInterruptions()); break;
				case 10: ret = data.getPacketSource().toString(); break;
				default: System.out.println("TABLEMODEL GETVALUE ERROR");
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
		if(typ == Typ.SENDER_V4 || typ == Typ.SENDER_V6){
			switch(columnIndex){
				case 0:	
					if((Boolean)aValue){
						ctrl.startMC(rowIndex, typ);
						ctrl.setTBactive(ctrl.getSelectedRows(typ), typ);
					}
					else{
						ctrl.stopMC(rowIndex, typ);
						ctrl.setTBactive(ctrl.getSelectedRows(typ), typ);
					}break;
				case 1:	
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:	data.setPacketCount(((Long)aValue).longValue()); System.out.println("SET!!");
				case 9:
				case 10:
				default: System.out.println("Table Model Error - SetValueAt() - SENDER");
			}
		}
		else if(typ == Typ.RECEIVER_V4 || typ == Typ.RECEIVER_V6){
			switch(columnIndex){
				case 0:
					if((Boolean)aValue){
						ctrl.startMC(rowIndex, typ);
						ctrl.setTBactive(ctrl.getSelectedRows(typ), typ);
					}
					else{
						ctrl.stopMC(rowIndex, typ);
						ctrl.setTBactive(ctrl.getSelectedRows(typ), typ);
					}break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				default: System.out.println("Table Model Error - SetValueAt() - RECEIVER");		
			}			
		}
	}
	public boolean isStateCheckboxEnabled() {
		return stateCheckboxEnabled;
	}
	public void setStateCheckboxEnabled(boolean stateCheckboxEnabled) {
		this.stateCheckboxEnabled = stateCheckboxEnabled;
	}


}
