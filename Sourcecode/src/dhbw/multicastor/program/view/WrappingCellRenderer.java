   
/*
 *  MultiCastor ist ein Tool zum Senden und Empfangen von Multicast-Datenstr�men. Es wurde als Projekt im Fach "Software Engineering" an der 
 *	Dualen Hochschule Stuttgart unter Leitung der Dozenten Markus Rentschler und Andreas Stuckert von unten genannten Studenten erstellt.
 *
 *  Copyright (C) 2012 Manuel Eisenhofer, Michael Kern, Tobias Michelchen, Roman Scharton, Pascal Schumann
 *
 *  Dieses Programm ist freie Software. Sie k�nnen es unter den Bedingungen der GNU General Public License, wie von der Free Software Foundation ver�ffentlicht, 
 *	weitergeben und/oder modifizieren, gem�� Version 3 der Lizenz.
 *
 *  Die Ver�ffentlichung dieses Programms erfolgt in der Hoffnung, dass es Ihnen von Nutzen sein wird, aber OHNE IRGENDEINE GARANTIE, sogar ohne die implizite 
 *	Garantie der MARKTREIFE oder der VERWENDBARKEIT F�R EINEN BESTIMMTEN ZWECK. Details finden Sie in der GNU General Public License.
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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import dhbw.multicastor.program.controller.ViewController;


/**
 * Klasse welche die Farben in der Tabelle verwaltet, hierbei muss unterschieden
 * werden ob Multicasts aktiv, inaktiv, selektiert oder deselektiert sind.
 * Weiterhin unterscheidet die Farbe der Tabellenzeilen ob ein Empf�nger von
 * einem Sender empf�ngt (Gr�n), von mehreren Sender empf�ngt (Orange) oder erst
 * k�rzlich eine �nderung in der Art der Daten die empfangen wurden festgestellt
 * hat (Gelb)
 * 
 * @author Daniel Becker
 * 
 */
public class WrappingCellRenderer implements TableCellRenderer {

	private TableCellRenderer wrappedCellRenderer;
	private ViewController ctrl;

	public WrappingCellRenderer(TableCellRenderer cellRenderer,
			ViewController ctrl) {
		super();
		this.wrappedCellRenderer = cellRenderer;
		this.ctrl = ctrl;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component rendererComponent = wrappedCellRenderer
				.getTableCellRendererComponent(table, value, isSelected,
						hasFocus, row, column);
		if (!isSelected && column == 12) {
//			if (((Boolean) table.getModel().getValueAt(table.convertRowIndexToModel(row), 0)).booleanValue()) {
//				rendererComponent.setBackground(new Color(220,255,220));
//				rendererComponent.setForeground(Color.black);
//
//			} else {
//				rendererComponent.setBackground(Color.white);
//				rendererComponent.setForeground(Color.black);
//			}
			if (((Boolean) table.getModel().getValueAt(table.convertRowIndexToModel(row), 12)).booleanValue()) {
				rendererComponent.setBackground(GraphLines.getSpecificColor(ctrl.getMCData(table.convertRowIndexToModel(row))));			
				rendererComponent.setForeground(Color.black);
			}
		} else {
//				rendererComponent.setBackground(Color.gray);
//				rendererComponent.setForeground(Color.white);
		}

		// if(column < 5)
		// rendererComponent.setBackground(Color.RED);
		// else
		// rendererComponent.setBackground(row % 2 == 0 ? null :
		// Color.LIGHT_GRAY );

		return rendererComponent;
	}

}
