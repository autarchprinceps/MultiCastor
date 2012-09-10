package zisko.multicastor.program.view;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import zisko.multicastor.program.lang.LanguageManager;

/**
 * About-Panel. Enthaelt Informationen zu den Entwicklern und der Lizenz.
 */

@SuppressWarnings("serial")
public class PanelAbout extends javax.swing.JPanel{
	
	private javax.swing.JScrollPane sp_about; /* Scrollbalken horizontal und vertikal (falls Fenster verkleinert wird). */
	private javax.swing.JPanel panel_about_outer;
    private javax.swing.JPanel panel_about_inner;
    private javax.swing.JLabel lb_image;
    private LanguageManager lang;
    
    private JLabel labelAbout1 = new JLabel();
    private JLabel labelAbout2 = new JLabel();
    private JLabel labelAbout3 = new JLabel();
    private JLabel labelMC1 = new JLabel();
    private JLabel labelMC2 = new JLabel();
    private JLabel labelLicense = new JLabel();

    /* Namen der Entwickler der Version 2.0 als Konstanten. */
    public static final String mc2_developer1 = "Jonas Traub";
    public static final String mc2_developer2 = "Matthis Hauschild";
    public static final String mc2_developer3 = "Sebastian Koralewski";
    public static final String mc2_developer4 = "Filip Haase";
    public static final String mc2_developer5 = "Fabian Fäßler";
    public static final String mc2_developer6 = "Christopher Westphal";

    /* Namen der Entwickler der Version 1.0 als Konstanten. */
    public static final String mc1_developer1 = "Bastian Wagener";
    public static final String mc1_developer2 = "Johannes Beutel";
    public static final String mc1_developer3 = "Thomas Lüder";
    public static final String mc1_developer4 = "Daniel Becker";
    public static final String mc1_developer5 = "Daniela Gerz";
    public static final String mc1_developer6 = "Jannik Müller";
    
    public PanelAbout() {
    	initComponents();
    }

    /** Methode, die beim Aendern der Sprache den Inhalt aktualisiert. */
    public void reloadLanguage() {
    	
		labelLicense.setText(lang.getProperty("about.license"));
		labelAbout1.setText(lang.getProperty("about.text1"));
		labelAbout2.setText(lang.getProperty("about.text2"));
		labelAbout3.setText(lang.getProperty("about.text3"));
		labelMC1.setText(lang.getProperty("about.mc1"));
		labelMC2.setText(lang.getProperty("about.mc2"));
		
    }
    
    /** Mit Hilfe dieser Methode werden die einzelnen GUI-Komponenten initialisiert. */
    private void initComponents() {

    	lang = LanguageManager.getInstance();
    	
        sp_about = new javax.swing.JScrollPane();
        panel_about_inner = new javax.swing.JPanel();
        panel_about_outer = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(985, 395));

        sp_about.setMinimumSize(new java.awt.Dimension(0, 0));
        sp_about.setPreferredSize(new java.awt.Dimension(980, 395));

        /* Benutzt wird ein verschachteltes Box-Layout. Zuerst wird das Box-Layout in
         * X-Richtung genutzt (panel_about_outer), um den linken Abstand vom Inhalt 
         * zum Rand zu erreichen. Das zweite Feld in X-Richtung ist wiederum ein Box-Layout,
         * jedoch in Y-Richtung (panel_about_inner). */
        
        panel_about_outer.setPreferredSize(new java.awt.Dimension(965, 380));
        panel_about_outer.setRequestFocusEnabled(false);
        
        /* Box-Layout in Y-Richtung definieren fuer Inhalt. */
        BoxLayout panel_aboutLayout = new BoxLayout(panel_about_inner, BoxLayout.Y_AXIS);
        panel_about_inner.setLayout(panel_aboutLayout);
        
        /* MC 2.0 Logo. */
        lb_image = new javax.swing.JLabel();
        lb_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/mcastor20_logo.png")));
        panel_about_inner.add(lb_image);
        
        /* Lizenzhinweis. */
        labelLicense.setText(lang.getProperty("about.license"));
        labelLicense.setFont(new Font("Helvetica", Font.BOLD, 11));
        panel_about_inner.add(labelLicense);
        
        /* Platzhalter (5 Pixel hoch) */
        panel_about_inner.add(Box.createRigidArea(new Dimension(0, 5)));
        
        /* Entwickler-Überschrift (Sorry für Crap-Code, aber musste schnell gehen! -.-) */
        labelAbout1.setText(lang.getProperty("about.text1"));
        labelAbout2.setText(lang.getProperty("about.text2"));
        labelAbout3.setText(lang.getProperty("about.text3"));
        panel_about_inner.add(labelAbout1);
        panel_about_inner.add(labelAbout2);
        panel_about_inner.add(labelAbout3);
        
        /* Platzhalter (20 Pixel hoch) */
        panel_about_inner.add(Box.createRigidArea(new Dimension(0, 20)));
        
        /* Entwickler-Überschrift */
        labelMC2.setText(lang.getProperty("about.mc2"));
        labelMC2.setFont(new Font("Helvetica", Font.BOLD, 12));
        panel_about_inner.add(labelMC2); 
        
        /* Platzhalter (5 Pixel hoch) */
        panel_about_inner.add(Box.createRigidArea(new Dimension(0, 5)));
        
        /* Auflistung der Entwickler Version 2.0 */
        panel_about_inner.add(new JLabel(mc2_developer1));
        panel_about_inner.add(new JLabel(mc2_developer2));
        panel_about_inner.add(new JLabel(mc2_developer3));
        panel_about_inner.add(new JLabel(mc2_developer4));
        panel_about_inner.add(new JLabel(mc2_developer5));
        panel_about_inner.add(new JLabel(mc2_developer6));
        
        /* Platzhalter (20 Pixel hoch) */
        panel_about_inner.add(Box.createRigidArea(new Dimension(0, 20)));
        
        /* Entwickler-Überschrift */
        labelMC1.setText(lang.getProperty("about.mc1"));
        labelMC1.setFont(new Font("Helvetica", Font.BOLD, 12));
        panel_about_inner.add(labelMC1);  
        
        /* Platzhalter (5 Pixel hoch) */
        panel_about_inner.add(Box.createRigidArea(new Dimension(0, 5)));
        
        /* Auflistung der Entwickler Version 1.0 */
        panel_about_inner.add(new JLabel(mc1_developer1));
        panel_about_inner.add(new JLabel(mc1_developer2));
        panel_about_inner.add(new JLabel(mc1_developer3));
        panel_about_inner.add(new JLabel(mc1_developer4));
        panel_about_inner.add(new JLabel(mc1_developer5));
        panel_about_inner.add(new JLabel(mc1_developer6));
        
        /* Box-Layout in X-Richtung definieren, um den generierten Inhalte einzufuegen. */
        BoxLayout panel_aboutLayoutOuter = new BoxLayout(panel_about_outer, BoxLayout.X_AXIS);
        panel_about_outer.setLayout(panel_aboutLayoutOuter);
        
        /* Abstand zum linken Rand (20 Pixel). */
        panel_about_outer.add(Box.createRigidArea(new Dimension(20, 0)));
        panel_about_outer.add(panel_about_inner);

        /* Definiert das aeussueere Box-Layout als "Viewport" fuer den Scrollbereich. */
        sp_about.setViewportView(panel_about_outer);

        /* Definiert weitere Einstellungen fuer den Scrollbereich. */
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp_about, javax.swing.GroupLayout.DEFAULT_SIZE, 985, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sp_about, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        
    }
}
