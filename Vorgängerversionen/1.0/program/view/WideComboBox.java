package zisko.multicastor.program.view;

import javax.swing.*; 
import java.awt.*; 
import java.util.Vector; 

/**
 * Hilfsklasse welche die standard Java JComboBox anpasst so dass das Dropdown Menu breiter sein kann
 * als der Button welcher es auslöst (Bei der standard JComboBox ist das nicht der Fall)
 * @author Daniel Becker
 *
 */
public class WideComboBox extends JComboBox{ 
 
    public WideComboBox() { 
    } 
 
    public WideComboBox(final Object items[]){ 
        super(items); 
    } 
 
    public WideComboBox(Vector items) { 
        super(items); 
    } 
 
    public WideComboBox(ComboBoxModel aModel) { 
        super(aModel); 
    } 
 
    private boolean layingOut = false; 
 
    public void doLayout(){ 
        try{ 
            layingOut = true; 
            super.doLayout(); 
        }finally{ 
            layingOut = false; 
        } 
    } 
 
    public Dimension getSize(){ 
        Dimension dim = super.getSize(); 
        if(!layingOut) 
            dim.width = Math.max(dim.width, getPreferredSize().width); 
        return dim; 
    } 
}