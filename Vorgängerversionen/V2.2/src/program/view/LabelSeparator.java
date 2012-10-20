package program.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.border.LineBorder;

public class LabelSeparator extends JComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = 980076016650471580L;
	public static final int OPENED = 1;
	public static final int CLOSED = 0;

	int state;

    private LabelSeparator panel = this;

    private Font font;
    private Color color;
    private String text = ""; //$NON-NLS-1$
    private JLabel label;
    private ImageIcon closed;
    private ImageIcon opened;
    private ImageIcon closed_h;
    private ImageIcon opened_h;
    private int entered = 0;

    /**
     * @wbp.parser.constructor
     */
    public LabelSeparator(String title) {
        this(title, null, null);

    }

    public LabelSeparator(String title, Color color) {
    	this(title, color, null);
    }

    public LabelSeparator(String text, Font font){
        this(text, null, font);
    }

    private LabelSeparator(String text, Color color, Font font){

        this.text = text;
        this.color = color;
        this.font = font;
        this.closed = new ImageIcon(LabelSeparator.class.getClassLoader().getResource("resources/closed.png")); //$NON-NLS-1$
        this.opened = new ImageIcon(LabelSeparator.class.getClassLoader().getResource("resources/opened.png")); //$NON-NLS-1$
        this.closed_h = new ImageIcon(LabelSeparator.class.getClassLoader().getResource("resources/closed_h.png")); //$NON-NLS-1$
        this.opened_h = new ImageIcon(LabelSeparator.class.getClassLoader().getResource("resources/opened_h.png")); //$NON-NLS-1$
        state = CLOSED;
        init();
    }

    /**
     * Alle notwendigen Komponenten werden initialisiert.
     *
     */
    private void init(){
        // Wenn der Text gesetzt ist, wird ein JLabel mit dem Text erzeugt.
    	this.label = new JLabel(text);
    	label.setIcon(closed);

    	label.addMouseListener(new MouseAdapter() {
    		@Override
			public void mouseClicked(MouseEvent paramMouseEvent) {
    			toggle();
    		}
		});
    	label.setFont(new Font("Trebuchet MS", Font.BOLD | Font.ITALIC, 11)); //$NON-NLS-1$

        // Ist keine Font vorhanden, so wird die StandardFont genommen
        if (this.font != null){
        	label.setFont(this.font);
        }
        if(this.color != null) {
        	label.setForeground(color);
        }

        addMouseListener(highlight);
        label.addMouseListener(highlight);

        GridBagLayout layout = new GridBagLayout();

        panel.setLayout(layout);
        Insets in = new Insets(2, 2, 2, 2);
        panel.add(this.label, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, in, 0, 0));
        JSeparator separator = new JSeparator();
        separator.setForeground(SystemColor.scrollbar);
        panel.add(separator, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, in, 0, 0));
    }

    public void setText(String text){
    	label.setText(text);
    }

    @Override
	public void setFont(Font f) {
    	label.setFont(f);
    }

    @Override
	public void setForeground(Color c) {
    	label.setForeground(c);
    }

    public int getState() {
    	return state;
    }

    public void toggle() {
    	state = state == 1 ? 0 : 1;
    	if(state == 1) {
    		if(entered == 1) {
    			label.setIcon(opened_h);
    		} else {
    			label.setIcon(opened);
    		}
    	} else {
    		if(entered == 1) {
    			label.setIcon(closed_h);
    		} else {
    			label.setIcon(closed_h);
    		}
    	}
    }

    public void addMyMouseListener(MouseListener l) {
    	label.addMouseListener(l);
    	addMouseListener(l);
    }

    public MouseAdapter highlight = new MouseAdapter() {

		@Override
		public void mouseExited(MouseEvent paramMouseEvent) {
			setBorder(null);
			deHighlightIcon();
			entered = 0;
		}

		@Override
		public void mouseEntered(MouseEvent paramMouseEvent) {
			//setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			//setBorder(new LineBorder(UIManager.getColor("CheckBoxMenuItem.acceleratorForeground"), 1, true));
			setBorder(new LineBorder(new Color(154, 205, 50), 1, true));
			highlightIcon();
			entered = 1;
		}

		private void highlightIcon() {
			if(state == 1) {
	    		label.setIcon(opened_h);
	    	} else {
	    		label.setIcon(closed_h);
	    	}
		}

		private void deHighlightIcon() {
			if(state == 1) {
	    		label.setIcon(opened);
	    	} else {
	    		label.setIcon(closed);
	    	}
		}
	};
}
