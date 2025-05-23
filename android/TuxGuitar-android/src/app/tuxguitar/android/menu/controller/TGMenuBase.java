package app.tuxguitar.android.menu.controller;

import android.view.Menu;
import android.view.MenuItem;

import app.tuxguitar.android.action.TGActionProcessorListener;
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction;
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction;
import app.tuxguitar.android.activity.TGActivity;
import app.tuxguitar.android.application.TGApplicationUtil;
import app.tuxguitar.android.view.dialog.TGDialogController;
import app.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.util.TGContext;

public abstract class TGMenuBase implements TGMenuController {

	private TGActivity activity;

	public TGMenuBase(TGActivity activity) {
		this.activity = activity;
	}

	public TGActivity getActivity() {
		return activity;
	}

	public TGContext findContext() {
		return TGApplicationUtil.findContext(this.getActivity());
	}

	public void initializeItem(Menu menu, int id, MenuItem.OnMenuItemClickListener listener, boolean enabled, boolean checked) {
		MenuItem menuItem = menu.findItem(id);
		menuItem.setOnMenuItemClickListener(listener);
		menuItem.setEnabled(enabled);
		menuItem.setChecked(checked);
		menuItem.setVisible(true);
	}

	public void initializeItem(Menu menu, int id, MenuItem.OnMenuItemClickListener listener, boolean enabled) {
		this.initializeItem(menu, id, listener, enabled, false);
	}

	public void initializeItem(Menu menu, int id, TGDialogController dialogController, boolean enabled) {
		this.initializeItem(menu, id, this.createDialogActionProcessor(dialogController), enabled);
	}

	public void initializeItem(Menu menu, int id, TGMenuController contextMenuController, boolean enabled) {
		this.initializeItem(menu, id, this.createContextMenuActionProcessor(contextMenuController), enabled);
	}

	public TGActionProcessorListener createActionProcessor(String actionId) {
		return new TGActionProcessorListener(findContext(), actionId);
	}

	public TGActionProcessorListener createDialogActionProcessor(TGDialogController controller) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGOpenDialogAction.NAME);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, getActivity());
		return tgActionProcessor;
	}

	public TGActionProcessorListener createContextMenuActionProcessor(TGMenuController controller) {
		TGActionProcessorListener tgActionProcessor = this.createActionProcessor(TGOpenMenuAction.NAME);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller);
		tgActionProcessor.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, getActivity());
		return tgActionProcessor;
	}

	public TGActionProcessorListener createConfirmableActionProcessor(final TGActionProcessor actionProcessor, final String confirmMessage) {
		TGActionProcessorListener tgActionProcessor = this.createDialogActionProcessor(new TGConfirmDialogController());
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, confirmMessage);
		tgActionProcessor.setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, new Runnable() {
			public void run() {
				actionProcessor.process();
			}
		});
		return tgActionProcessor;
	}
}
