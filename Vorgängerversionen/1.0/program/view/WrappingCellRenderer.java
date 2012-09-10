package zisko.multicastor.program.view;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.data.MulticastData.senderState;
/**
 * Klasse welche die Farben in der Tabelle verwaltet, hierbei muss unterschieden werden ob Multicasts aktiv,
 * inaktiv, selektiert oder deselektiert sind. Weiterhin unterscheidet die Farbe der Tabellenzeilen ob ein
 * Empfänger von einem Sender empfängt (Grün), von mehreren Sender empfängt (Orange) oder erst kürzlich 
 * eine Änderung in der Art der Daten die empfangen wurden festgestellt hat (Gelb)
 * @author Daniel Becker
 *
 */
public class WrappingCellRenderer implements TableCellRenderer {
    
    private TableCellRenderer wrappedCellRenderer;
    private ViewController ctrl;
    
    public WrappingCellRenderer(TableCellRenderer cellRenderer, ViewController ctrl) {
        super();
        this.wrappedCellRenderer = cellRenderer;
        this.ctrl = ctrl;
    }
 
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component rendererComponent = wrappedCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if(!isSelected){
        	if(((Boolean)table.getModel().getValueAt(row, 0)).booleanValue()){
            	switch(ctrl.getMCData(row, ctrl.getSelectedTab()).getSenders()){
	            	case SINGLE:         	
	            		rendererComponent.setBackground(Color.green);
	            		rendererComponent.setForeground(Color.black); 
	            		break;
	            	case RECENTLY_CHANGED: 
	            		rendererComponent.setBackground(Color.yellow);
	            		rendererComponent.setForeground(Color.black);
	            		break;
	            	case MULTIPLE: 
	            		rendererComponent.setBackground(Color.orange);
	            		rendererComponent.setForeground(Color.black);
	            		break;
	            	case NONE:
	            		rendererComponent.setBackground(Color.green);
	            		rendererComponent.setForeground(Color.black); 
	            		break;
	            	default:
            	}
        	}
        	else{
        		rendererComponent.setBackground(Color.white);
        		rendererComponent.setForeground(Color.black);
        	}
        }
        else{
        	if(((Boolean)table.getModel().getValueAt(row, 0)).booleanValue()){
        	 	switch(ctrl.getMCData(row, ctrl.getSelectedTab()).getSenders()){
	            	case SINGLE:         	
	            		rendererComponent.setBackground(new Color(0,175,0));
	    	        	rendererComponent.setForeground(Color.white);
	            		break;
	            	case RECENTLY_CHANGED: 
	            		rendererComponent.setBackground(new Color(175,175,0));
	            		rendererComponent.setForeground(Color.white);
	            		break;
	            	case MULTIPLE: 
	            		rendererComponent.setBackground(new Color(217,108,0));
	            		rendererComponent.setForeground(Color.white);
	            		break;
	            	case NONE:
	            		rendererComponent.setBackground(new Color(0,175,0));
	    	        	rendererComponent.setForeground(Color.white);
	            		break;
	            	default:
        	 	}

        	}
        	else{
	        	rendererComponent.setBackground(Color.gray);
	        	rendererComponent.setForeground(Color.white);
        	}
        }

//        if(column < 5)
//            rendererComponent.setBackground(Color.RED);
//        else
//            rendererComponent.setBackground(row % 2 == 0 ? null : Color.LIGHT_GRAY );
 
        
        return rendererComponent;
    }
 
}
