package zisko.multicastor.program.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

import zisko.multicastor.program.controller.ViewController;
import zisko.multicastor.program.lang.LanguageManager;

/**
 * Die Klasse ButtonTabComponent ist ein Panel das in den 
 * Tabs (Titeln) benutzt wird. 
 * 
 * Links steht dabei jeweils ein Icon
 * in der Mitte der Titel und rechts ein Schliessueen Button.
 * 
 * @version 2.0
 */
@SuppressWarnings("serial")
public class ButtonTabComponent extends JPanel{

	private final DraggableTabbedPane pane;
	private LanguageManager lang=LanguageManager.getInstance();
	private ViewController vCtrl;
	
	/**
	 * Der Konstruktor speichert die uebergeben Pane, und erzeugt direkt das neue
	 * Label mit Bild und Text
	 * 
	 * Aussueerdem wird ein TabButton zum schliessueen hinzugefuegt.
	 * 
	 * @param pPane Die TabPane zu der diese Komponente gehoert
	 * @param path Der Pfad zum Icon welches zum Tab geladen werden soll
	 */
	public ButtonTabComponent(final DraggableTabbedPane pPane, String path, ViewController pVCtrl){
		
		// Set the Layout(that Label is left and Button right)
		super(new FlowLayout(FlowLayout.LEFT,0,0));
		
		// Set the Pane(remember it's final, we need to do this here)
		this.pane = pPane;
		setOpaque(false);
		
		this.vCtrl = pVCtrl; 
		
		//Handle Errors
		if(pane == null)
			throw new NullPointerException("The parent TabbedPane is null");
		
		// Set the Label with the Title of the right Compononent of the DraggableTabbedPane
		JLabel label = new JLabel(new ImageIcon(getClass().getResource(path))){
			public String getText(){
				//This gets the index of this Component (or -1 if its not contained in a TabbedPane)
				int i = pane.indexOfTabComponent(ButtonTabComponent.this);
				if(i != -1)
					return pane.getTitleAt(i);
				return null;
			}
		};
		label.setFont(MiscFont.getFont(0, 17));
		
		add(label);
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

		//Add the Button
		JButton button = new TabButton();
		add(button);
		setBorder(BorderFactory.createEmptyBorder(2,0,0,0));
	}
	
	/**
	 * Die private Klasse TabButton.
	 * Sie ist dafuer verantwortlich einen Button zum schliessueen der Tabs zu erzeugen
	 * und enthaelt auch den ActionListener, der die Tabs dann wirklich schliessuet.
	 */
	private class TabButton extends JButton implements ActionListener {
        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText(lang.getProperty("toolTip.closeThisTab"));
            //Make the button looks the same for all Laf's
            setUI(new BasicButtonUI());
            //Make it transparent
            setContentAreaFilled(false);
            //No need to be focusable
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            //Making nice rollover effect
            //we use the same listener for all buttons
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            //Close the proper tab by clicking the button
            addActionListener(this);
        }
 
        /**
         *  actionPerformed, kuemmert sich darum den Tab zu schliessueen
         *  wenn der Button geschlossen wird
         *  
         *  @param ActionEvent e, wird nicht benutzt
         */
        public void actionPerformed(ActionEvent e) {
        	
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1) {
            	pane.closeTab(pane.getTitleAt(i));
            	pane.remove(i);
            	vCtrl.autoSave();
            }
            
        }
 
        /**
         * we don't want to update UI for this button
         */
        public void updateUI() {
        }
 
        /**
         * paint the cross
         * 
         * @param g Standart fuer painComponent Methoden von AWT
         */
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }
 
    private final static MouseListener buttonMouseListener = new MouseAdapter() {
    	/**
    	 * Funktion welche dafuer sorgt den Rahmen
    	 * fuer den "Hover-Effekt" zu zeichnen
    	 * 
    	 * @param e, das MouseEvent ueber welches
    	 * der entsprechende Button ausgewaehlt werden kann
    	 */
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

    	/**
    	 * Funktion welche dafuer sorgt den Rahmen
    	 * fuer den "Hover-Effekt" wieder zu entfernen
    	 * 
    	 * @param e, das MouseEvent ueber welches
    	 * der entsprechende Button ausgewaehlt werden kann
    	 */
        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}
