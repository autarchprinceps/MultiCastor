package zisko.multicastor.program.view;

import java.awt.Component;

/**
 * Klasse, die Daten ueber TabData bereithaelt.
 */
public class TabData {

	ButtonTabComponent btComponent;
	Component component;
	String title;

	public TabData(final String title, final Component component,
			final ButtonTabComponent btComponent) {
		super();
		this.title = title;
		this.component = component;
		this.btComponent = btComponent;
	}

	public ButtonTabComponent getBtComponent() {
		return btComponent;
	}

	public Component getComponent() {
		return component;
	}

	public String getTitle() {
		return title;
	}

	public void setBtComponent(final ButtonTabComponent btComponent) {
		this.btComponent = btComponent;
	}

	public void setComponent(final Component component) {
		this.component = component;
	}

	public void setTitle(final String title) {
		this.title = title;
	}
}
