package zisko.multicastor.program.view;

/**
 * Das About-Panel. Enthält Informationen zu dem Zisko-Team und der Programmversion. 
 * @author Thomas Lüder
 */

@SuppressWarnings("serial")
public class PanelAbout extends javax.swing.JPanel{
	
    private javax.swing.JLabel lb_image;
    private javax.swing.JPanel panel_about;
    private javax.swing.JScrollPane sp_about;

    /**
     * Konstruktor, in dem mithilfe von initComponents() die Komponenten inialisiert werden.
     */
    
    public PanelAbout() {
    	
        initComponents();
    }
    
	/**
	 * Panel-Komponenten werden initialisiert und in ihrem Layout ausgerichtet und angezeigt.
	 */
    
    private void initComponents() {

        sp_about = new javax.swing.JScrollPane();
        panel_about = new javax.swing.JPanel();
        lb_image = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(985, 395));

        sp_about.setMinimumSize(new java.awt.Dimension(0, 0));
        sp_about.setPreferredSize(new java.awt.Dimension(980, 395));

        panel_about.setPreferredSize(new java.awt.Dimension(965, 380));
        panel_about.setRequestFocusEnabled(false);

        lb_image.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zisko/multicastor/resources/images/mc.png")));

        javax.swing.GroupLayout panel_aboutLayout = new javax.swing.GroupLayout(panel_about);
        panel_about.setLayout(panel_aboutLayout);
        panel_aboutLayout.setHorizontalGroup(
            panel_aboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_aboutLayout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(lb_image)
                .addContainerGap(146, Short.MAX_VALUE))
        );
        panel_aboutLayout.setVerticalGroup(
            panel_aboutLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_aboutLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lb_image)
                .addContainerGap())
        );

        sp_about.setViewportView(panel_about);

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
