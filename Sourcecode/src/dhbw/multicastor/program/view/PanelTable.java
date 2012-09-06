   
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import dhbw.multicastor.program.controller.ViewController;


public class PanelTable extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1685722392120270233L;
	private ViewController viewCtrl = null;
	private JTable table;

	public PanelTable(ViewController ctrl) {
		this.viewCtrl = ctrl;
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();

		table = new JTable();

		table.addKeyListener(viewCtrl);

		MiscTableModel model = new MiscTableModel(ctrl);

		table.setModel(model);

//		table.setDefaultRenderer(Object.class,
//				new WrappingCellRenderer(
//						table.getDefaultRenderer(Object.class), ctrl));
		table.setDefaultRenderer(
				Boolean.class,
				new WrappingCellRenderer(table
						.getDefaultRenderer(Boolean.class), ctrl));
//		table.setDefaultRenderer(
//				Integer.class,
//				new WrappingCellRenderer(table
//						.getDefaultRenderer(Integer.class), ctrl));
//		table.setDefaultRenderer(Double.class,
//				new WrappingCellRenderer(
//						table.getDefaultRenderer(Double.class), ctrl));
//		table.setDefaultRenderer(Long.class,
//				new WrappingCellRenderer(table.getDefaultRenderer(Long.class),
//						ctrl));
		table.setFont(MiscFont.getFont(0, 10));
		table.getTableHeader().setFont(MiscFont.getFont(0, 10));
		table.getTableHeader().addMouseListener(ctrl);
		table.getSelectionModel().addListSelectionListener(ctrl);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.getColumnModel().getSelectionModel().addListSelectionListener(ctrl);
		table.addMouseListener(ctrl);
		table.setColumnModel(new CustomTableColumnModel(table.getModel()));
		table.createDefaultColumnsFromModel();
		table.setAutoCreateRowSorter(true);
		CustomTableColumnModel columnModel = (CustomTableColumnModel) table.getColumnModel();
		columnModel.setColumnSize();
		scrollPane.addMouseListener(viewCtrl);
		
		JLabel empty_lab = new JLabel(" ");
		empty_lab.setFont(MiscFont.getFont(0,5));
		add(empty_lab, BorderLayout.NORTH);
		
		add(scrollPane, BorderLayout.CENTER);

		table.setPreferredScrollableViewportSize(new Dimension(300, 400));
		scrollPane.setViewportView(table);
	}
	
	public void restoreColumns() {
		CustomTableColumnModel columnModel = (CustomTableColumnModel) getTable().getColumnModel();
		columnModel.restoreColumns();
	}
	
	public void removeColumnFromView(ActionEvent e) {
		CustomTableColumnModel columnModel = (CustomTableColumnModel) getTable().getColumnModel();
		columnModel.hideColumn();
	}

	public JTable getTable() {
		return table;
	}
}
