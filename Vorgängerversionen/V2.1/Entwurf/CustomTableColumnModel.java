   
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
 
  

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class CustomTableColumnModel extends DefaultTableColumnModel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2946600853093336022L;
	private ArrayList<TableColumn> allColumns = new ArrayList<TableColumn>();
	private ArrayList<TableColumn> hiddenColumns = new ArrayList<TableColumn>();
	private TableModel tableModel = null;
	private TableColumn selectedColumn = null;
	
	public CustomTableColumnModel(TableModel tableModel){
		this.tableModel = tableModel;
	}
	
	public void hideColumn() {
		setAllColumns();
		hiddenColumns.add(getSelectedColumn());
		super.removeColumn(getSelectedColumn());
	}
	
	public void restoreColumns(){
		setAllColumns();
		for (TableColumn hiddenCol : hiddenColumns) {
			super.addColumn(hiddenCol);
		}
		hiddenColumns.clear();
		Vector<TableColumn> tempColumns = new Vector<TableColumn>();
		for (TableColumn tableColumn : super.tableColumns) {
			tempColumns.add(tableColumn);
		}
		for (TableColumn tableColumn : tempColumns) {
			if (super.getColumnIndex(tableColumn.getIdentifier()) != tableColumn.getModelIndex()){
				super.moveColumn(super.getColumnIndex(tableColumn.getIdentifier()), tableColumn.getModelIndex());
			}
		} 
			
	}
	
	public void toggleColumnVisable(boolean visable, int modelIndex){
		setAllColumns();
		TableColumn column = allColumns.get(modelIndex);
		if(!visable){
			hiddenColumns.add(column);
			super.removeColumn(column);			
		} else {
			if(hiddenColumns.contains(column)){
				super.addColumn(column);
				hiddenColumns.remove(column);
				Vector<TableColumn> tempColumns = new Vector<TableColumn>();
				for (TableColumn tableColumn : super.tableColumns) {
					tempColumns.add(tableColumn);
				}
				for (TableColumn tableColumn : tempColumns) {
					int currentIndex = super.getColumnIndex(tableColumn.getIdentifier());
					if(tableColumn.getModelIndex() > modelIndex){
						super.moveColumn(tempColumns.size()-1, currentIndex);
					}
				} 
			}
		}
	}
	
	public boolean isVisable(TableColumn column){
		setAllColumns();
		return (!hiddenColumns.contains(column));
	}
	
	public void setColumnSize(){
		super.getColumn(0).setMinWidth(45);
		super.getColumn(1).setMinWidth(100);
		super.getColumn(2).setMinWidth(100);
		super.getColumn(3).setMinWidth(45);
		super.getColumn(4).setMinWidth(60);
		super.getColumn(5).setMinWidth(60);
		super.getColumn(6).setMinWidth(60);
		super.getColumn(0).setPreferredWidth(45);
		super.getColumn(1).setPreferredWidth(100);
		super.getColumn(2).setPreferredWidth(100);
		super.getColumn(3).setPreferredWidth(45);
		super.getColumn(4).setPreferredWidth(60);
		super.getColumn(5).setPreferredWidth(60);
		super.getColumn(6).setPreferredWidth(60);

		super.getColumn(7).setMinWidth(50);
		super.getColumn(8).setMinWidth(60);
		super.getColumn(9).setMinWidth(60);
		super.getColumn(10).setMinWidth(45);
		super.getColumn(11).setMinWidth(45);
		super.getColumn(7).setPreferredWidth(50);
		super.getColumn(8).setPreferredWidth(60);
		super.getColumn(9).setPreferredWidth(60);
		super.getColumn(10).setPreferredWidth(45);
		super.getColumn(11).setPreferredWidth(45);
	}
	
	public TableColumn getSelectedColumn() {
		return selectedColumn;
	}
	public void setSelectedColumn(int i) {		
		this.selectedColumn = this.getColumn(i);;
	}

	public ArrayList<TableColumn> getAllColumns() {
		setAllColumns();
		return allColumns;
	}

	public void setAllColumns(ArrayList<TableColumn> allColumns) {
		this.allColumns = allColumns;
	}
	
	private void setAllColumns(){
		if(allColumns.size() != tableModel.getColumnCount()){
			allColumns.addAll(super.tableColumns);
		}
	}
}
