package multicastor.view;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTabbedPane;

import multicastor.controller.ViewController;
import multicastor.lang.LanguageManager;

/**
 * Die Klasse DraggableTabbedPane erbt von JTabbedPane und laesst zusaetzlich zu
 * JTabbed Pane ein grafish ansprechendes verschieben von Tabs per Drag&Drop zu.
 * 
 * @version 2.0
 */
@SuppressWarnings("serial")
public class DraggableTabbedPane extends JTabbedPane {

	private Rectangle bounds;
	private Point currentMouseLocation = null;
	private int draggedTabIndex = 0;
	private boolean dragging = false;
	private boolean draggingPlus = false;
	private final FrameMain frame;
	private final LanguageManager lang;
	private int mouseRelX;
	private int mouseRelY;
	private Image tabImage = null;
	private final ViewController vCtrl;

	/**
	 * Im Konstruktor wird ein neuen MouseMotionListener angelegt, welcher
	 * schaut ob ich, wenn ich mit der Maus klicke(mouseDragged) ueber einem tab
	 * bin. Wenn Ja wird ein Bild des "gedragten" Tabs in den Buffer gezeichnet.
	 * 
	 * @param parentFrame
	 *            Referenz auf GUI-Frame
	 */
	public DraggableTabbedPane(final FrameMain parentFrame,
			final ViewController pVCtrl) {
		super();
		vCtrl = pVCtrl;
		lang = LanguageManager.getInstance();
		frame = parentFrame;
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(final MouseEvent e) {

				if (!dragging && !draggingPlus) {
					// Gets the tab index based on the mouse position
					final int tabNumber = getUI().tabForCoordinate(
							DraggableTabbedPane.this, e.getX(), e.getY());

					// TabCount-1 is the Plus Tab therefore set an extra flag
					// and not dragging
					if (tabNumber == (getTabCount() - 1)) {
						draggingPlus = true;
					} else if (tabNumber >= 0) {
						draggedTabIndex = tabNumber;
						bounds = getUI().getTabBounds(DraggableTabbedPane.this,
								tabNumber);

						// Paint the tabbed pane to a buffer
						final Image totalImage = new BufferedImage(getWidth(),
								getHeight(), BufferedImage.TYPE_INT_ARGB);
						final Graphics totalGraphics = totalImage.getGraphics();
						totalGraphics.setClip(bounds);

						// Don't be double buffered when painting to a static
						// image.
						setDoubleBuffered(false);
						paintComponent(totalGraphics);

						// Paint just the dragged tab to the buffer
						tabImage = new BufferedImage(bounds.width,
								bounds.height, BufferedImage.TYPE_INT_ARGB);
						final Graphics graphics = tabImage.getGraphics();
						graphics.drawImage(totalImage, 0, 0, bounds.width,
								bounds.height, bounds.x, bounds.y, bounds.x
										+ bounds.width, bounds.y
										+ bounds.height,
								DraggableTabbedPane.this);

						mouseRelX = e.getX() - bounds.x;
						mouseRelY = bounds.y;

						dragging = true;
						repaint();
					}
				} else {
					final int X = (int) e.getPoint().getX() - mouseRelX;
					final int Y = mouseRelY;
					currentMouseLocation = new Point(X, Y);

					if (getUI().tabForCoordinate(DraggableTabbedPane.this,
							e.getX(), 10) != draggedTabIndex) {
						final int returnValue = insertIt(e);
						if (returnValue != -1) {
							draggedTabIndex = returnValue;
						}
					}

					// Need to repaint
					repaint();
				}

				super.mouseDragged(e);
			}
		});

		/**
		 * Beim Mauswieder loslassen wird nun (falls gedragged wird) alles
		 * Noetige zum Tab gespeichert Dazu gehoeren die Componente, der Titel
		 * und das Icon. Aussueerdem wird der SelectedIndex der TabbedPane(also
		 * der ausgewaehlte Tab) auf den neuen Index gesetzt (damit der
		 * gedraggte Tab im Vordergrund ist, wie man es von modernen Browsern
		 * ebenfalls gewoehnt ist)
		 */
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {

				if (dragging) {
					final int tabNumber = getUI().tabForCoordinate(
							DraggableTabbedPane.this, e.getX(), 10);
					if (tabNumber >= 0) {
						insertIt(e);
					}
				}

				if (draggingPlus) {
					draggingPlus = false;
				}

				dragging = false;
				tabImage = null;
			}
		});
	}

	/**
	 * closeTab dient dazu einen Tab wieder zu schliessueen Um Welchen Tab es
	 * sich handelt wird ueber den command erkannt.
	 * 
	 * @param command
	 *            Der ActionCommand String ueber welchen erkannt wird welcher
	 *            Tab geoeffnet werden soll
	 */
	public void closeTab(final String command) {
		if (command.equals(" " + lang.getProperty("tab.l3r") + " ")) {
			frame.getMi_open_l(1, 0).setSelected(false);
		} else if (command.equals(" " + lang.getProperty("tab.l3s") + " ")) {
			frame.getMi_open_l(1, 1).setSelected(false);
		} else if (command.equals(" " + lang.getProperty("tab.l2r") + " ")) {
			frame.getMi_open_l(0, 0).setSelected(false);
		} else if (command.equals(" " + lang.getProperty("tab.l2s") + " ")) {
			frame.getMi_open_l(0, 1).setSelected(false);
		} else if (command.equals(" " + lang.getProperty("mi.about") + " ")) {
			frame.getMi_open_about().setSelected(false);
		}
	}

	/**
	 * Ermittel den Index eines Tabs.
	 * 
	 * @param command
	 *            Der interne Name des Tabs
	 */
	public int getIndex(final String command) {
		final Map<String, Integer> openTabs = new HashMap<String, Integer>();
		final int openTabsCount = getTabCount();
		Integer index = -1;

		for (int i = 0; i < openTabsCount; i++) {
			openTabs.put(getTitleAt(i), i);
		}

		if (command.equals("open_layer3_r")) {
			index = openTabs.get(" " + lang.getProperty("tab.l3r") + " ");
		} else if (command.equals("open_layer3_s")) {
			index = openTabs.get(" " + lang.getProperty("tab.l3s") + " ");
		} else if (command.equals("open_layer2_r")) {
			index = openTabs.get(" " + lang.getProperty("tab.l2r") + " ");
		} else if (command.equals("open_layer2_s")) {
			index = openTabs.get(" " + lang.getProperty("tab.l2s") + " ");
		} else if (command.equals("open_about")) {
			index = openTabs.get(" " + lang.getProperty("mi.about") + " ");
		}

		return (index == null) ? (-1) : index;
	}

	/**
	 * Aeffnet (oder schliessuet) ein bestimmtes Tab, je nachdem in welchem
	 * Zustand sich das Tab davor befunden hat.
	 * 
	 * @param command
	 *            Der interne Name des Tabs
	 */
	public void openOrCloseTab(final String command) {
		final int index = getIndex(command.substring(2));

		if (index != -1) {
			closeTab(getTitleAt(index));
			this.remove(index);
		} else {
			openTab(command.substring(2));
		}
	}

	/**
	 * openTab dient dazu einen Tab wieder zu oeffnen Um Welechen Tab es sich
	 * handelt wird ueber den command erkannt, wenn der Tab bereits geoeffnet
	 * ist wird er selektiert
	 * 
	 * @param command
	 *            Der ActionCommand String ueber welchen erkannt wird welcher
	 *            Tab geoeffnet werden soll
	 */
	public void openTab(final String command) {
		final Map<String, Integer> openTabs = new HashMap<String, Integer>();
		// System.out.println("open tab: [["+command+"]]");
		final int openTabsCount = getTabCount();

		for (int i = 0; i < openTabsCount; i++) {
			openTabs.put(getTitleAt(i), i);
		}

		if (command.equals("open_layer3_r")) {
			// Pruefen ob Tab bereits geoeffnet ist
			if (openTabs.containsKey(" " + lang.getProperty("tab.l3r") + " ")) {
				// Wenn ja holen wir uns die ID und focusieren(oeffnen) ihn
				setSelectedIndex(openTabs.get(" " + lang.getProperty("tab.l3r")
						+ " "));
			} else {
				insertTab(" " + lang.getProperty("tab.l3r") + " ", null,
						frame.getPanel(0, 1), null, openTabsCount - 1);
				setTabComponentAt(openTabsCount - 1, new ButtonTabComponent(
						this, "/multicastor/images/ipv6receiver.png", vCtrl));
				setSelectedIndex(openTabsCount - 1);
			}
			frame.getMi_open_l(1, 0).setSelected(true);
		} else if (command.equals("open_layer3_s")) {
			if (openTabs.containsKey(" " + lang.getProperty("tab.l3s") + " ")) {
				setSelectedIndex(openTabs.get(" " + lang.getProperty("tab.l3s")
						+ " "));
			} else {
				insertTab(" " + lang.getProperty("tab.l3s") + " ", null,
						frame.getPanel(1, 1), null, openTabsCount - 1);
				setTabComponentAt(openTabsCount - 1, new ButtonTabComponent(
						this, "/multicastor/images/ipv6sender.png", vCtrl));
				setSelectedIndex(openTabsCount - 1);
			}
			frame.getMi_open_l(1, 1).setSelected(true);
		} else if (command.equals("open_layer2_s")) {
			if (openTabs.containsKey(" " + lang.getProperty("tab.l2s") + " ")) {
				setSelectedIndex(openTabs.get(" " + lang.getProperty("tab.l2s")
						+ " "));
			} else {
				insertTab(" " + lang.getProperty("tab.l2s") + " ", null,
						frame.getPanel(1, 0), null, openTabsCount - 1);
				setTabComponentAt(openTabsCount - 1, new ButtonTabComponent(
						this, "/multicastor/images/ipv4sender.png", vCtrl));
				setSelectedIndex(openTabsCount - 1);
			}
			frame.getMi_open_l(0, 1).setSelected(true);
		} else if (command.equals("open_layer2_r")) {
			if (openTabs.containsKey(" " + lang.getProperty("tab.l2r") + " ")) {
				setSelectedIndex(openTabs.get(" " + lang.getProperty("tab.l2r")
						+ " "));
			} else {
				insertTab(" " + lang.getProperty("tab.l2r") + " ", null,
						frame.getPanel(0, 0), null, openTabsCount - 1);
				setTabComponentAt(openTabsCount - 1, new ButtonTabComponent(
						this, "/multicastor/images/ipv4receiver.png", vCtrl));
				setSelectedIndex(openTabsCount - 1);
			}
			frame.getMi_open_l(0, 0).setSelected(true);
		} else if (command.equals("open_about")) {
			if (openTabs.containsKey(" " + lang.getProperty("mi.about") + " ")) {
				setSelectedIndex(openTabs.get(" "
						+ lang.getProperty("mi.about") + " "));
			} else {
				insertTab(" " + lang.getProperty("mi.about") + " ", null,
						frame.getPanel_about(), null, openTabsCount - 1);
				setTabComponentAt(openTabsCount - 1, new ButtonTabComponent(
						this, "/multicastor/images/about.png", vCtrl));
				setSelectedIndex(openTabsCount - 1);
			}
			frame.getMi_open_about().setSelected(true);
		}
	}

	/**
	 * Diese Methode dient dazu das Bild des Tabs zu zeichnen der derzeit
	 * gedraggt wird. Sie wird in der mouseDragged (s.O.) Methode verwendet
	 * 
	 * @param g
	 *            Graphics-Objekt
	 */
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		// Are we dragging?
		if (dragging && (currentMouseLocation != null) && (tabImage != null)) {
			// Draw the dragged tab
			g.drawImage(tabImage, currentMouseLocation.x,
					currentMouseLocation.y, this);
		}
	}

	/**
	 * Fuegt einen Tab an der Stelle die ueber die Position von e gegen ist ein
	 * 
	 * @param e
	 *            Das MouseEvent, welches die Position des Mauszeigers
	 *            beinhaltet, worueber der Tab selektiert werden kann
	 */
	private int insertIt(final MouseEvent e) {
		final int tabNumber = getUI().tabForCoordinate(
				DraggableTabbedPane.this, e.getX(), 10);

		if ((tabNumber >= 0) && (tabNumber != (getTabCount() - 1))
				&& (draggedTabIndex != (getTabCount() - 1)) && !draggingPlus) {
			final Component comp = getComponentAt(draggedTabIndex);
			final Component buttonTabComp = getTabComponentAt(draggedTabIndex);
			final String title = getTitleAt(draggedTabIndex);
			removeTabAt(draggedTabIndex);

			insertTab(title, null, comp, null, tabNumber);
			setTabComponentAt(tabNumber, buttonTabComp);
			setSelectedIndex(tabNumber);
			return tabNumber;
		}
		return -1;
	}

}
