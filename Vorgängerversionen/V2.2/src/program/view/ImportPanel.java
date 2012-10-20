package program.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import program.controller.Messages;
import program.data.MulticastData;
import program.model.WrapLayout;

public class ImportPanel extends JPanel {

	/**
	 *
	 */
	private static final long serialVersionUID = -402483833789487228L;

	private JTable headerTable;
	private int animationTimeout = 1;
	private boolean open = false;

	private Map<String, Vector<MulticastData>> daten = new HashMap<String, Vector<MulticastData>>();
	private Map<String, ImportTableModel> models = new HashMap<String, ImportTableModel>();
	private Map<String, LabelSeparator> labelSeparators = new HashMap<String, LabelSeparator>();

	/**
	 * Main method for testing purposes only.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			JFrame f = new JFrame();
			ImportPanel dialog = new ImportPanel(null);
			f.getContentPane().add(dialog);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setSize(600, 200);
			f.setVisible(true);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the panel.
	 */
	public ImportPanel(Vector<MulticastData> data) {
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setSize(new Dimension(590, 230));
		setBackground(Color.WHITE);
		sort(data);
		setLayout(new WrapLayout(FlowLayout.CENTER, 5, 5));

		JPanel headerPanel = new JPanel();
		headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		headerPanel.setAlignmentY(Component.TOP_ALIGNMENT);

		headerPanel.setPreferredSize(new Dimension(590, 21));
		add(headerPanel);
		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));

		headerTable = new JTable();
		headerTable.setGridColor(new Color(220, 220, 220));
		headerTable.setAlignmentX(Component.LEFT_ALIGNMENT);
		headerTable.setDefaultRenderer(JLabel.class, new Renderer());
		headerPanel.add(headerTable);
		headerTable.setShowHorizontalLines(false);
		headerTable.setRowHeight(21);
		headerTable.setCellSelectionEnabled(true);
		final HeaderTableModel headerModel = new HeaderTableModel();
		headerTable.setModel(headerModel);
		headerTable.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent paramMouseEvent) {

				Point p = paramMouseEvent.getPoint();
				int col = headerTable.columnAtPoint(p);
				headerModel.highlight(col);
			}
		});
		headerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		headerTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent paramMouseEvent) {
				Point p = paramMouseEvent.getPoint();
				int col = headerTable.columnAtPoint(p);
				int dir = headerModel.sort(col);
				for(String key : models.keySet()) {
					models.get(key).sortByColumn(col, dir);
				}
			}

			@Override
			public void mouseExited(MouseEvent paramMouseEvent) {
				Point p = paramMouseEvent.getPoint();
				headerModel.dehighlight(headerTable.columnAtPoint(p));
			}
		});

		((JLabel)headerTable.getDefaultRenderer(String.class)).setHorizontalAlignment(SwingConstants.RIGHT);
		setTableWidth(headerTable);
		headerPanel.add(headerTable, BorderLayout.CENTER);

		initGroups();
		selectAll();
	}

	/**
	 * Initialize the LabelSeperators and the JPanels with the Tables inside.
	 */
	private void initGroups() {
		if(daten.keySet().size() == 1) {
			open = true;
		}
		for(String key : daten.keySet()) {
			final LabelSeparator ls = new LabelSeparator(key + " (" + daten.get(key).size() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			final JPanel p = new JPanel();
			ImportTableModel model = new ImportTableModel(daten.get(key));
			if(!open) {
				p.setPreferredSize(new Dimension(getWidth(), 0));
			} else {
				p.setPreferredSize(new Dimension(getWidth(), 21 * (daten.get(key).size() + 1)));
				ls.toggle();
			}
			p.setBackground(Color.WHITE);
			model.addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent arg0) {
					renewLabelText();
				}
			});
			models.put(key, model);
			ls.setText(key + Messages.getString("ImportPanel.1") + models.get(key).getSelectedCount() + Messages.getString("ImportPanel.2") + models.get(key).getRowCount() + Messages.getString("ImportPanel.3")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			JTable t = new JTable(model);

			((JLabel)t.getDefaultRenderer(String.class)).setHorizontalAlignment(SwingConstants.RIGHT);
			setTableWidth(t);
			t.setRowHeight(21);
			t.setPreferredSize(new Dimension(getWidth(), t.getRowCount() * t.getRowHeight()));
			t.setShowHorizontalLines(false);
			t.setGridColor(new Color(220, 220, 220));
			t.setAlignmentY(Component.BOTTOM_ALIGNMENT);
			p.add(t);

			ls.setAlignmentX(Component.LEFT_ALIGNMENT);
			ls.setPreferredSize(new Dimension(getWidth(), 21));
			ls.addMyMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent paramMouseEvent) {
					new Animator(p, ls).start();
				}
			});
			labelSeparators.put(key, ls);
			add(ls);
			add(p);
		}
	}

	/**
	 * Sets the widths of all columns.
	 * @param table
	 */
	private void setTableWidth(JTable table) {
		int[] widths = {50, 180, 50, 180, 40, 60, 50 };
		TableColumnModel model = table.getColumnModel();
		int i = 0;
		for(Enumeration<TableColumn> element = model.getColumns(); element.hasMoreElements();) {
			TableColumn c = element.nextElement();
			c.setPreferredWidth(widths[i]);
			i++;
		}
	}

	/**
	 * Sorts the Vector with the Type and puts it into a Hashmap.
	 * @param data The MulticastData to be sorted.
	 */
	private void sort(MulticastData data) {
		String typ = data.getTyp().toString();
		if(daten.get(typ) == null) {
			daten.put(typ, new Vector<MulticastData>());
		}
		daten.get(typ).add(data);
	}

	private void sort(Vector<MulticastData> data) {
		if(data != null && data.size() != 0) {
			for(MulticastData m : data) {
				sort(m);
			}
		}
	}

	public void selectAll() {
		if(models.keySet().size() != 0) {
			for(String key : models.keySet()) {
				models.get(key).selectAll();
			}
		}
		renewLabelText();
	}

	public void deselectAll() {
		if(models.keySet().size() != 0) {
			for(String key : models.keySet()) {
				models.get(key).deselectAll();
			}
		}
		renewLabelText();
	}

	/**
	 * Get all MulticastData from the models.
	 * @return Vector<MulticastData>
	 * 				The Vector with all selected Streams which should be imported.
	 */
	public Vector<MulticastData> getImportFields() {
		Vector<MulticastData> ret = new Vector<MulticastData>();
		if(models.keySet().size() != 0) {
			for(String key : models.keySet()) {
				ret.addAll(models.get(key).getImportFields());
			}
		}
		return ret;
	}

	private void renewLabelText() {
		String text;
		for(String key : daten.keySet()) {
			text = key;
			text += Messages.getString("ImportPanel.1") + models.get(key).getSelectedCount() + Messages.getString("ImportPanel.2") + models.get(key).getRowCount() + Messages.getString("ImportPanel.3"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			labelSeparators.get(key).setText(text);
		}
	}

	//Animator class for the animation of sliding down the panel.
	class Animator extends Thread {
		JPanel p;
		LabelSeparator s;
		public Animator(JPanel p, LabelSeparator s) {
			this.p = p;
			this.s = s;
		}

		@Override
		public void run() {
			JTable t = (JTable)p.getComponent(0);
			int height;
			height = (s.getState() == LabelSeparator.OPENED) ? (t.getRowCount() + 1) * t.getRowHeight() : 0 ;
			int oldHeight = p.getHeight();
			while(oldHeight != height) {
				p.setPreferredSize(new Dimension(590, oldHeight));
				p.revalidate();
				p.repaint();
				repaint();
				revalidate();

				oldHeight = (oldHeight < height) ? oldHeight + 1 : oldHeight - 1;
				try {
					Thread.sleep(animationTimeout);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class HeaderTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1093615880379814100L;
		private JLabel[] headers = {new JLabel(Messages.getString("ImportPanel.4")), //$NON-NLS-1$
									new JLabel(Messages.getString("ImportPanel.5")), //$NON-NLS-1$
									new JLabel(Messages.getString("ImportPanel.6")), //$NON-NLS-1$
									new JLabel(Messages.getString("ImportPanel.7")), //$NON-NLS-1$
									new JLabel(Messages.getString("ImportPanel.8")), //$NON-NLS-1$
									new JLabel(Messages.getString("ImportPanel.9")), //$NON-NLS-1$
									new JLabel(Messages.getString("ImportPanel.10")) //$NON-NLS-1$
		};

		public HeaderTableModel() {
			for(int i = 1; i < headers.length; i++) {
				headers[i].setIcon(empty);
			}
		}

		private ImageIcon asc = new ImageIcon(ImportPanel.class.getClassLoader().getResource("resources/asc.png")); //$NON-NLS-1$
		private ImageIcon asc_h = new ImageIcon(ImportPanel.class.getClassLoader().getResource("resources/asc_h.png")); //$NON-NLS-1$
		private ImageIcon des = new ImageIcon(ImportPanel.class.getClassLoader().getResource("resources/desc.png")); //$NON-NLS-1$
		private ImageIcon empty = new ImageIcon(ImportPanel.class.getClassLoader().getResource("resources/empty.png")); //$NON-NLS-1$
		private int[] sortDir = {0, 0, 0, 0, 0, 0, 0, 0};

		public int sort(int col) {
			if(col >= 1) {
				for(int i = 1; i < getColumnCount(); i++) {
					if(sortDir[i] != 0 && i != col) {
						sortDir[i] = 0;
						headers[i].setIcon(empty);
					}
				}
				switch(sortDir[col]) {
					case 1:
						headers[col].setIcon(des);
						sortDir[col] = ImportTableModel.DES;
						fireTableDataChanged();
						return ImportTableModel.DES;
					case 0:
					case -1:
						headers[col].setIcon(asc);
						sortDir[col] = ImportTableModel.ASC;
						fireTableDataChanged();
						return ImportTableModel.ASC;
					default:
						return ImportTableModel.NOT;
				}
			} else {
				return 0;
			}
		}

		public void highlight(int col) {
			for(int i  = 1;  i < headers.length ; i++) {
				if(sortDir[i] == 0) {
					headers[i].setIcon(empty);
				}
			}
			if(col >= 1 && sortDir[col] == 0) {
				headers[col].setIcon(asc_h);
			}
			fireTableDataChanged();
		}

		public void dehighlight(int col) {
			for(int i  = 1;  i < headers.length ; i++) {
				if(sortDir[i] == 0) {
					headers[i].setIcon(empty);
				}
			}
			fireTableDataChanged();
		}

		@Override
		public void setValueAt(Object paramObject, int row, int col) {
		    if(sortDir[col] == 0) {

		    }
		  }

		@Override
		public Class<?> getColumnClass(int arg0) {
			return getValueAt(0, arg0).getClass();
		}
		@Override
		public int getColumnCount() {
			return 7;
		}
		@Override
		public String getColumnName(int arg0) {
			return ""; //$NON-NLS-1$
		}
		@Override
		public int getRowCount() {
			return 1;
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			return headers[arg1];
		}
		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}
	}

	class Renderer implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable paramJTable,
				Object paramObject, boolean paramBoolean1,
				boolean paramBoolean2, int paramInt1, int paramInt2) {
			return (JLabel)paramObject;
		}

	}
}
