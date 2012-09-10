package zisko.multicastor.program.view;

import java.awt.Component;

/**
 * Klasse, die Daten ueber TabData bereithaelt.
 *
 */
public class TabData {
	
	String title;
	Component component;
	ButtonTabComponent btComponent;
	
	public TabData(String title, Component component,
			ButtonTabComponent btComponent) {
		super();
		this.title = title;
		this.component = component;
		this.btComponent = btComponent;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public ButtonTabComponent getBtComponent() {
		return btComponent;
	}

	public void setBtComponent(ButtonTabComponent btComponent) {
		this.btComponent = btComponent;
	}
}
