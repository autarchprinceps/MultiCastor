   
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import dhbw.multicastor.program.data.MulticastData;


public class GraphLines {

	private int[] data = new int[62];
	private int dataPointer = 0;
	private Color color = null;
	private boolean highlight = false;

	// these are the possible colors
	private static Color[] colors = { Color.red, Color.blue, Color.green,
			Color.yellow, Color.pink };

	// contains the MulticastData referring to a graphline
	private static HashMap<ArrayList<MulticastData>, GraphLines> groupMap = new HashMap<ArrayList<MulticastData>, GraphLines>();

	private GraphLines() {
		reset();
		Collection<GraphLines> graphLines = getAllGraphLines();
		Object[] graphs = graphLines.toArray();
		if (graphs.length == 0) {
			this.color = colors[0];
		} else {
			for (Color c : colors) {
				boolean b = true;
				for (int i = 0; i < graphs.length; i++) {
					if (((GraphLines) graphs[i]).getColor().equals(c)) {
						b = false;
					}
				}
				if (b) {
					this.color = c;
					return;
				}
			}
		}
	}

	/**
	 * returns all instances of the class GraphLines
	 * 
	 * @return
	 */
	private static Collection<GraphLines> getAllGraphLines() {
		ArrayList<GraphLines> graphLines = new ArrayList<GraphLines>();
		if (groupMap.values() != null) {
			graphLines.addAll(groupMap.values());
		}
		return graphLines;
	}
	
	public static void setHighlightMC(MulticastData mc){
		for(ArrayList<MulticastData> array : groupMap.keySet()){
			if(array.contains(mc)){
				groupMap.get(array).setHighlight(true);
			}else{
				groupMap.get(array).setHighlight(false);
			}
		}
	}
	
	public static void clearHighlight(){
		for(ArrayList<MulticastData> array : groupMap.keySet()){
			groupMap.get(array).setHighlight(false);
		}
	}

	/**
	 * @return
	 */
	public static Color getSpecificColor(MulticastData mc) {

		synchronized (groupMap) {
			for (ArrayList<MulticastData> array : groupMap.keySet()) {
				if (array.contains(mc)) {
					return groupMap.get(array).getColor();
				}
			}
		}
		return null;
	}

	public static ArrayList<ArrayList<MulticastData>> getGroupDatas() {
		ArrayList<ArrayList<MulticastData>> aMc = new ArrayList<ArrayList<MulticastData>>();
		aMc.addAll(groupMap.keySet());
		return aMc;
	}

	/**
	 * adds a MulticastData as key to the static Hashmap in GraphLines together
	 * with a instance of GrahLines
	 * 
	 * @param mc
	 * @return
	 */
	public static boolean addMC(ArrayList<MulticastData> mcs) {
		if (groupMap.size() < 5) {
			ArrayList<MulticastData> mc = new ArrayList<MulticastData>();
			for (ArrayList<MulticastData> aMc : groupMap.keySet()) {
				for (MulticastData multicastData : aMc) {
					for (MulticastData multicastData2 : mcs) {
						if (multicastData.equals(multicastData2)) {
							mc.add(multicastData);
						}
					}
				}
			}
			for (MulticastData data : mc) {
				deleteMCSafty(data);
			}
			if (mcs.size() > 0) {
				GraphLines graph = new GraphLines();
				groupMap.put(mcs, graph);
			}
			checkIfGroupIsEmpty();
			return true;
		}
		return false;
	}

	/**
	 * deletes all GraphLines
	 */
	public static void deleteMCs() {
		groupMap.clear();
	}

	/**
	 * deletes a specific MulticastData
	 * 
	 * @param mc
	 */
	public static void deleteMC(MulticastData mc) {

		boolean contains = false;
		for (ArrayList<MulticastData> array : groupMap.keySet()) {
			if (array.contains(mc)) {
				contains = true;
			}
		}
		if (contains)
			deleteMCSafty(mc);
		checkIfGroupIsEmpty();
	}

	private static void checkIfGroupIsEmpty() {
		ArrayList<MulticastData> toDelete = null;
		for (ArrayList<MulticastData> array : groupMap.keySet()) {
			if (array.size() <= 0) {
				toDelete = array;
			}
		}
		synchronized (groupMap) {
			groupMap.remove(toDelete);
		}
	}

	// private static void deleteMCSafty(
	// CopyOnWriteArrayList<MulticastData> array, MulticastData data) {
	// GraphLines graph = groupMap.get(array);
	// groupMap.remove(array);
	// array.remove(data);
	// groupMap.put(array, graph);
	// }

	private static void deleteMCSafty(MulticastData data) {
		ArrayList<MulticastData> a = null;
		GraphLines help = null;
		for (ArrayList<MulticastData> array : groupMap.keySet()) {
			if (array.contains(data)) {
				help = groupMap.get(array);
				a = array;
			}
		}
		synchronized (groupMap) {
			if (a != null) {
				if (help != null) {
					groupMap.remove(a);
					a.remove(data);
					groupMap.put(a, help);
				}
			}
		}
	}

	/**
	 * deletes a specific MulticastData
	 * 
	 * @param mc
	 */
	public static void deleteMC(ArrayList<MulticastData> mc) {
		if (groupMap.containsKey(mc)) {
			groupMap.remove(mc);
		} else {
			ArrayList<MulticastData> toDelete = new ArrayList<MulticastData>();
			for (ArrayList<MulticastData> array : groupMap.keySet()) {
				for (MulticastData mcs : mc) {
					if (array.contains(mcs)) {
						toDelete.add(mcs);
					}
				}
			}
			for (MulticastData mcs : toDelete) {
				deleteMCSafty(mcs);
			}
		}
		checkIfGroupIsEmpty();
	}

	/**
	 * sets a value to the MulticastData
	 * 
	 * @param mc
	 * @param value
	 *            is a integer which shows the actual transfer rate
	 */
	public static void setValue(ArrayList<MulticastData> mc, int value) {
		if (groupMap.containsKey(mc)) {
			groupMap.get(mc).addData(value);
		}
	}
	
	/**
	 * sets a value to the MulticastData
	 * 
	 * @param mc
	 * @param value
	 *            is a integer which shows the actual transfer rate
	 */
	public static void setValue(MulticastData mc, int value) {
		for(ArrayList<MulticastData> array : groupMap.keySet()){
			if (array.contains(mc)) {
				groupMap.get(array).addData(value);
			}
		}
	}

	/**
	 * resets all GraphLines
	 */
	public static void resetAll() {
		for (int[] data : returnAllDatas()) {
			for (int i = 0; i < data.length; i++) {
				data[i] = Integer.MIN_VALUE;
			}
		}
	}

	/**
	 * returns the highest value of all GraphLines
	 * 
	 * @return
	 */
	public static int getHighestValue() {
		int[][] datas = returnAllDatas();
		int max = 0;
		for (int[] valarray : datas) {
			for (int val : valarray) {
				if (max < val) {
					max = val;
				}
			}
		}
		return max;
	}

	private static int[][] returnAllDatas() {
		Collection<GraphLines> graphLines = getAllGraphLines();
		Object[] graphs = graphLines.toArray();
		int[][] datas = new int[graphLines.size()][62];
		for (int i = 0; i < graphs.length; i++) {
			datas[i] = ((GraphLines) graphs[i]).getData();
		}
		return datas;
	}

	/**
	 * returns all instances of GraphLines
	 * 
	 * @return
	 */
	public static GraphLines[] returnAllGraphLines() {
		Collection<GraphLines> graphLines = getAllGraphLines();
		Object[] graphs = graphLines.toArray();
		GraphLines[] g = new GraphLines[graphs.length];
		int i = 0;
		for (Object o : graphs) {
			g[i] = (GraphLines) o;
			i++;
		}
		return g;
	}

	/**
	 * returns the array which contains all transfer rates of a multicast
	 * 
	 * @return
	 */
	public int[] getData() {
		return data;
	}

	/**
	 * adds a integer which shows a actual transfer rate
	 * 
	 * @param data
	 */
	public void addData(int data) {
		if (data >= 0) {
			this.data[this.dataPointer] = data;
		} else {
			this.data[this.dataPointer] = 0;
		}
		dataPointer++;
		if (dataPointer >= 62 || dataPointer <= 0) {
			dataPointer = 0;
		}
	}

	/**
	 * returns the color from the GraphLine
	 * 
	 * @return
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * returns the actual dataPointer
	 * 
	 * @return
	 */
	public int getDataPointer() {
		return dataPointer;
	}

	/**
	 * resets the data-Array
	 */
	public void reset() {
		for (int i = 0; i < this.data.length; i++) {
			data[i] = Integer.MIN_VALUE;
		}
	}
	
	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}

}
