package app.tuxguitar.ui.qt.menu;

import app.tuxguitar.ui.menu.UIMenu;
import app.tuxguitar.ui.menu.UIMenuSubMenuItem;

import io.qt.widgets.QMenu;

public class QTMenuSubMenuItem extends QTMenuItem<QMenu> implements UIMenuSubMenuItem {

	private QTMenu subMenu;

	public QTMenuSubMenuItem(QTAbstractMenu<?> parent) {
		super(parent.createNativeMenu(), parent);

		this.subMenu = new QTMenu(this.getControl());
	}

	public UIMenu getMenu() {
		return this.subMenu;
	}

	public String getText() {
		return this.getControl().title();
	}

	public void setText(String text) {
		this.getControl().setTitle(text);
	}

	public boolean isEnabled() {
		return this.getControl().isEnabled();
	}

	public void setEnabled(boolean enabled) {
		this.getControl().setEnabled(enabled);
	}
}
