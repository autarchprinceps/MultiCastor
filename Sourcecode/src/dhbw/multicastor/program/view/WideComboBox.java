   
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

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import dhbw.multicastor.program.data.MulticastData.ProtocolType;
import dhbw.multicastor.program.model.MacAddress;
import dhbw.multicastor.program.model.MmrpAdapter;
import dhbw.multicastor.program.model.NetworkAdapter;


import java.awt.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Vector;

/**
 * Hilfsklasse welche die standard Java JComboBox anpasst so dass das Dropdown
 * Menu breiter sein kann als der Button welcher es auslï¿½st (Bei der standard
 * JComboBox ist das nicht der Fall)
 * 
 * @author Daniel Becker
 * 
 */
public class WideComboBox extends JComboBox {

	ProtocolType type;

	public WideComboBox(ProtocolType type) {
		setUI(new myComboUI());
		this.type = type;
	}

	public WideComboBox(final Object items[], ProtocolType type) {
		super(items);
		setUI(new myComboUI());
		this.type = type;
	}

	public WideComboBox(Vector items, ProtocolType type) {
		super(items);
		setUI(new myComboUI());
		this.type = type;
	}

	public WideComboBox(ComboBoxModel aModel, ProtocolType type) {
		super(aModel);
		setUI(new myComboUI());
		this.type = type;
	}

	private boolean layingOut = false;

	@Override
	public void doLayout() {
		try {
			layingOut = true;
			super.doLayout();
		} finally {
			layingOut = false;
		}
	}

	@Override
	public Dimension getSize() {
		Dimension dim = super.getSize();
		if (!layingOut)
			try {
				dim.width = Math.max(dim.width, getBestSize());
			} catch (SocketException e) {
				e.printStackTrace();
			}
		return dim;
	}

	public class myComboUI extends BasicComboBoxUI {
		protected ComboPopup createPopup() {
			setMaximumRowCount(5);

			setBorder(BorderFactory.createLineBorder(new Color(184, 207, 229)));

			BasicComboPopup popup = new BasicComboPopup(comboBox) {
				@Override
				protected JScrollPane createScroller() {
					return new JScrollPane(list,
							ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
							ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				}// end of method createScroller

				@Override
				protected Rectangle computePopupBounds(int px, int py, int pw,
						int ph) {
					return super
							.computePopupBounds(px, py, Math.max(
									comboBox.getPreferredSize().width, pw), ph);
				}
			};
			return popup;
		}// end of method createPopup
	}// end of inner class myComboUI

	private int getBestSize() throws SocketException {
		int size = 0;
		if (type.equals(ProtocolType.MLD)) {
			for (Object a : NetworkAdapter.getipv6Adapters()) {
//				NetworkInterface ni = NetworkInterface.getByInetAddress((InetAddress)a);
				size = getDropDownSize((NetworkInterface)a, size);
			}
		}else if (type.equals(ProtocolType.IGMP)){
			for(Object a : NetworkAdapter.getipv4Adapters()){
//				NetworkInterface ni = NetworkInterface.getByInetAddress((InetAddress)a);
				size = getDropDownSize((NetworkInterface)a, size);
			}
		}else if (type.equals(ProtocolType.MMRP)){
			int help = 0;
			for(Object a : NetworkAdapter.getMacInterfaces()){
				help = 0;
				MmrpAdapter interfaces = (MmrpAdapter)a;
				if(interfaces.getDescription() != null){
					help += interfaces.getDescription().length()*8;
				}else{
					help += 10;
				}
				help += interfaces.getMacAdress().toString().length()*8;
				if(size < help){
					size = help;
				}
			}
		}
		return size;
	}
	
	private int getDropDownSize(NetworkInterface ni, int max){
		int help = 0;
		help = ni.getInetAddresses().toString().length()*4+ ni.getDisplayName().length()*4;
		if (max < help) {
			max = help;
		}
		return max;
	}
	
}