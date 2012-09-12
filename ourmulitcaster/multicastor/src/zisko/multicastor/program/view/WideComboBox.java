package zisko.multicastor.program.view;

import java.awt.Dimension;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

/**
 * Hilfsklasse welche die standard Java JComboBox anpasst so dass das Dropdown
 * Menu breiter sein kann als der Button welcher es ausluest (Bei der standard
 * JComboBox ist das nicht der Fall).
 */
@SuppressWarnings("serial")
public class WideComboBox extends JComboBox {

	private boolean layingOut = false;

	public WideComboBox() {
	}

	public WideComboBox(final ComboBoxModel aModel) {
		super(aModel);
	}

	public WideComboBox(final Object items[]) {
		super(items);
	}

	public WideComboBox(final Vector<?> items) {
		super(items);
	}

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
		final Dimension dim = super.getSize();
		if(!layingOut) {
			dim.width = Math.max(dim.width, getPreferredSize().width);
		}
		return dim;
	}
}