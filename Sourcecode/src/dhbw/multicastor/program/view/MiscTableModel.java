   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenströmen. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie können es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation veröffentlicht, 
 *	weitergeben und/oder modifizieren, gemäß Version 3 der Lizenz.
 *
 *  Die Veröffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT FÜR EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
 *
 *  Sie sollten ein Exemplar der GNU General Public License zusammen mit diesem Programm erhalten haben. Falls nicht, siehe <http://www.gnu.org/licenses/>.
 *
 ****************************************************************************************************************************************************************
 *  MultiCastor is a Tool for sending and receiving of Multicast-Data Streams. This project was created for the subject "Software Engineering" at 
 *	Dualen Hochschule Stuttgart under the direction of Markus Rentschler and Andreas Stuckert.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; 
 *  either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 *
 */
 
 package dhbw.multicastor.program.view;

import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.table.AbstractTableModel;

import dhbw.multicastor.program.controller.ViewController;
import dhbw.multicastor.program.controller.MulticastController.Modus;
import dhbw.multicastor.program.data.IgmpMldData;
import dhbw.multicastor.program.data.MMRPData;
import dhbw.multicastor.program.data.MulticastData;
import dhbw.multicastor.program.data.MulticastData.ProtocolType;

/**
 * Das Tabellenmodel welches sich um die Anzeige der Daten in der Tabelle
 * kï¿½mmert.
 * 
 * @author Daniel Becker
 * 
 */
public class MiscTableModel extends AbstractTableModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6948419620310188159L;
	private boolean stateCheckboxEnabled = true;
	private ViewController ctrl;
	private Logger logger = Logger.getLogger("dhbw.multicastor.program.controller.main");

	public MiscTableModel(ViewController ctrl) {
		this.ctrl = ctrl;
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast hinzugefï¿½gt wird.
	 */
	public void insertUpdate() {
		if(ctrl.getMCCount() > 0){
			fireTableRowsInserted(ctrl.getMCCount()-1, ctrl.getMCCount()-1);	
		}
		
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast gelsï¿½cht wird.
	 */
	public void deleteUpdate() {
		fireTableRowsDeleted(0, ctrl.getMCCount());
	}

	/**
	 * Funktion welche aufgerufen wird wenn eine Multicast geï¿½ndert wird.
	 */
	public void changeUpdate() {
		int mcCount = ctrl.getMCCount();
		if(mcCount > 0){
			fireTableRowsUpdated(0, mcCount-1);	
		}
	}

	public void regularUpdate() {
		for (int i = 0; i < ctrl.getMCCount(); i++) {
			fireTableCellUpdated(i, 3);
			fireTableCellUpdated(i, 4);
			fireTableCellUpdated(i, 5);
			// TODO: add other columns to be updated
		}
	}

	@Override
	/**
	 * Funktion welche den Datentyp einer Spalte bestimmt
	 */
	public Class<?> getColumnClass(int columnIndex) {
		Class<?> ret = null;
		if (ctrl.getCurrentModus() == Modus.SENDER) {
			switch (columnIndex) {
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
				ret = String.class;
				break;
			case 9:
				ret = Long.class;
				break;
			case 10:
				ret = Integer.class;
				break;
			case 11:
				ret = Integer.class;
				break;
			case 12:
				ret = Boolean.class;
				break;
			default:
				ret = null;
				break;

			}
		}
		if (ctrl.getCurrentModus() == Modus.RECEIVER) {
			switch (columnIndex) {
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
				ret = Long.class;
				break;
			case 10:
				ret = Integer.class;
				break;
			case 11:
				ret = String.class;
				break;
			case 12:
				ret = Boolean.class;
				break;
			default:
				ret = null;
				break;
			}
		}
		return ret;
	}

	@Override
	/**
	 * Funktion welche die Anzahl an Spalten zurï¿½ck gibt.
	 */
	public int getColumnCount() {
		if (ctrl.getCurrentModus() != Modus.UNDEFINED) {
			return 13;
		}
		return 0;
	}

	// TODO schauen wie man diese Zwei zusammenfÃ¼hren kann
	@Override
	/**
	 * Funktion welche den Namen einer Spalte bestimmt
	 */
	public String getColumnName(int columnIndex) {
		String ret = null;
		if (ctrl.getCurrentModus() == Modus.SENDER) {
			switch (columnIndex) {
			case 0:
				ret = "STATE";
				break;
			case 1:
				ret = "ID";
				break;
			case 2:
				ret = "GRP ID";
				break;
			case 3:
				ret = "PROT";
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
				ret = "PORT";
				break;
			case 8:
				ret = "SRC ID";
				break;
			case 9:
				ret = "#SENT";
				break;
			case 10:
				ret = "TTL";
				break;
			case 11:
				ret = "LENGTH";
				break;
			case 12:
				ret = "Graph";
				break;
			default:
				ret = "error!";
				break;
			}
		} else if (ctrl.getCurrentModus() == Modus.RECEIVER) {
			switch (columnIndex) {
			case 0:
				ret = "STATE";
				break;
			case 1:
				ret = "ID";
				break;
			case 2:
				ret = "GRP ID";
				break;
			case 3:
				ret = "PROT";
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
				ret = "PORT";
				break;
			case 8:
				ret = "LOSS/S";
				break;
			case 9:
				ret = "AVG INT";
				break;
			case 10:
				ret = "#INT";
				break;
			case 11:
				ret = "SRC";
				break;
			case 12:
				ret = "Graph";
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
	 * Funktion welche die Anzahl an Tabellenreihen zurï¿½ck gibt.
	 */
	public int getRowCount() {
		return ctrl.getMCCount();
	}

	@Override
	/**
	 * Funktion welche die Daten fï¿½r eine jeweilige Tabellenzelle anfordert
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		MulticastData data = ctrl.getMCData(rowIndex);
		Object ret = null;
		if (ctrl.getCurrentModus() == Modus.SENDER) {
			switch (columnIndex) {
			case 0:
				ret = new Boolean(data.getActive());
				break;
			case 1:
				ret = data.getSenderID();
				break;
			case 2:
				if (data.getProtocolType().equals(ProtocolType.IGMP) || data.getProtocolType().equals(ProtocolType.MLD)) {
					ret = ((IgmpMldData) data).getGroupIp().toString()
							.substring(1);
				} else {
					ret = ((MMRPData)data).getMacGroupId().toString();
				}
				break;
			case 3:
				ret = data.getProtocolType().toString();
				break;
			case 4:
				ret = new Integer(data.getPacketRateDesired());
				break;
			case 5:
				ret = new Integer(data.getPacketRateMeasured());
				break;
			case 6:
				ret = new DecimalFormat("##0.000")
						.format((data.getTraffic() / 1024.0 / 1024.0 * 8.0));
				break;
			case 7:
				if (data.getProtocolType().equals(ProtocolType.IGMP) || data.getProtocolType().equals(ProtocolType.MLD)) {
					ret = new Integer(((IgmpMldData) data).getUdpPort());
				} else {
					ret = new Integer(0);
				}
				break;
			case 8:
				if (data.getProtocolType().equals(ProtocolType.IGMP) || data.getProtocolType().equals(ProtocolType.MLD)) {
					ret = ((IgmpMldData) data).getSourceIp();
					if (ret != null) {
						ret = ret.toString().substring(1);
					} else {
						ret = "";
					}
				} else {
					ret = ((MMRPData) data).getMacSourceId();
					if (ret != null) {
						ret = ret.toString().substring(1);
					} else {
						ret = "";
					}
				}
				break;
			case 9:
				ret = new Long(data.getPacketCount());
				break;
			case 10:
				if (data.getProtocolType().equals(ProtocolType.IGMP) || data.getProtocolType().equals(ProtocolType.MLD)) {
					ret = new Integer(((IgmpMldData) data).getTtl());
				}
				else {
					ret = new Integer(0);
				}
				break;
			case 11:
				ret = new Integer(data.getPacketLength());
				break;
			case 12:
				ret = new Boolean(data.isGraph());
				break;
			default:
				System.out.println("TABLEMODEL GETVALUE ERROR");
			}
		} else if (ctrl.getCurrentModus() == Modus.RECEIVER) {
			switch (columnIndex) {
			case 0:
				ret = new Boolean(data.getActive());
				break;
			case 1:
				ret = data.getSenderID();
				break;
			case 2:
				if (data.getProtocolType().equals(ProtocolType.IGMP) || data.getProtocolType().equals(ProtocolType.MLD)) {
					ret = ((IgmpMldData) data).getGroupIpToString();
				} else {
					ret = ((MMRPData)data).getMacGroupId().toString();
				}
				break;
			case 3:
				ret = data.getProtocolType().toString();
				break;
			case 4:
				ret = new Integer(data.getPacketRateDesired());
				break;
			case 5:
				ret = new Integer(data.getPacketRateMeasured());
				break;
			case 6:
				ret = new DecimalFormat("##0.000")
						.format((data.getTraffic() / 1024.0 / 1024.0 * 8.0));
				break;
			case 7:
				if (data.getProtocolType().equals(ProtocolType.IGMP) || data.getProtocolType().equals(ProtocolType.MLD)) {
					ret = new Integer(((IgmpMldData) data).getUdpPort());
				}else {
					ret = new Integer(0);
				}
				break;
			case 8:
				ret = new Integer(data.getPacketLossPerSecond());
				break;
			case 9:
				ret = new Integer(data.getAverageInterruptionTime());
				break;
			case 10:
				ret = new Integer(data.getNumberOfInterruptions());
				break;
			case 11:
				if(data.getReceivedPackSource() != null){
					ret = data.getReceivedPackSource().toString();	
				}else{
					ret = "";
				}				
				break;
			case 12:
				ret = new Boolean(data.isGraph());
				break;
			default:
				System.out.println("TABLEMODEL GETVALUE ERROR");
			}
		}
		return ret;
	}

	@Override
	/**
	 * Funktion welche angibt ob eine Zelle in der Tabelle editierbar ist oder nicht
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0 && stateCheckboxEnabled || columnIndex == 12 && stateCheckboxEnabled) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	/**
	 * Funktion welche angibt was nach dem editieren einer Zelle geschehen soll
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		MulticastData data = ctrl.getMCData(rowIndex);
		if(ctrl.getCurrentModus() == Modus.SENDER){
			switch(columnIndex){
				case 0:	
					if((Boolean)aValue){
						ctrl.startMC(data);
						ctrl.displayGraph(true);
					}
					else{
						ctrl.stopMC(data);
						ctrl.displayGraph(false);
					}break;
				case 1:	
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:	data.setPacketCount(((Long)aValue).longValue()); break;
				case 10:
				case 11:
				case 12:ctrl.displayGraph((Boolean)aValue);
					break;
				default: System.out.println("Table Model Error - SetValueAt() - SENDER");
			}
		}
		else if(ctrl.getCurrentModus() == Modus.RECEIVER){
			switch(columnIndex){
				case 0:
					if((Boolean)aValue){
						ctrl.startMC(data);
						ctrl.displayGraph(true);
					}
					else{
						ctrl.stopMC(data);
						ctrl.displayGraph(false);
					}break;
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 12: ctrl.displayGraph((Boolean)aValue);
					break;
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
