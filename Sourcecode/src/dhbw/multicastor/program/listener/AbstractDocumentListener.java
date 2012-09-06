   
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
 
 package dhbw.multicastor.program.listener;

import java.awt.Color;
import java.awt.Component;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent.ElementChange;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.Document;
import javax.swing.text.Element;

public abstract class AbstractDocumentListener implements DocumentListener{

	
	protected void setColor(Component component, boolean accepted) {
		if (component != null && component.isEnabled()) {

			if (accepted) {
				component.setBackground(Color.WHITE);
			} else {
				component.setBackground(Color.decode("#FF9595"));
			}
		}
	}

	@Override
	public abstract void changedUpdate(DocumentEvent arg0);

	@Override
	public abstract void insertUpdate(DocumentEvent arg0);

	@Override
	public abstract void removeUpdate(DocumentEvent arg0);
	
	public void insertUpdate(){
		insertUpdate(getDocumentEvent());
	}
	
	protected DocumentEvent getDocumentEvent(){
		return new DocumentEvent() {
			
			@Override
			public EventType getType() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public int getOffset() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public int getLength() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public Document getDocument() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public ElementChange getChange(Element elem) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}
}
