package app.tuxguitar.app.view.dialog.message;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UIImageView;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.ui.widget.UIWrapLabel;
import app.tuxguitar.util.TGContext;

public class TGMessageDialog {

	public static final String ATTRIBUTE_STYLE = "style";
	public static final String ATTRIBUTE_TITLE = "title";
	public static final String ATTRIBUTE_MESSAGE = "message";

	public static final Integer STYLE_INFO = 1;
	public static final Integer STYLE_WARNING = 2;
	public static final Integer STYLE_ERROR = 3;

	public static final Float WRAP_WIDTH = 400f;

	public void show(final TGViewContext context) {
		final String title = context.getAttribute(ATTRIBUTE_TITLE);
		final String message = context.getAttribute(ATTRIBUTE_MESSAGE);
		final Integer style = context.getAttribute(ATTRIBUTE_STYLE);

		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

		dialog.setLayout(dialogLayout);
		dialog.setText(title);

		//========================================================================
		UITableLayout panelLayout = new UITableLayout();
		UIPanel uiPanel = uiFactory.createPanel(dialog, false);
		uiPanel.setLayout(panelLayout);
		dialogLayout.set(uiPanel, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

		UIImageView uiIcon = uiFactory.createImageView(uiPanel);
		uiIcon.setImage(this.resolveImage(context.getContext(), style));
		panelLayout.set(uiIcon, 1, 1, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);

		UIWrapLabel uiMessage = uiFactory.createWrapLabel(uiPanel);
		uiMessage.setText(message);
		panelLayout.set(uiMessage, 1, 2, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		panelLayout.set(uiMessage, UITableLayout.PACKED_WIDTH, WRAP_WIDTH);

		//========================================================================
		UITableLayout buttonsLayout = new UITableLayout();
		UIPanel buttons = uiFactory.createPanel(dialog, false);
		buttons.setLayout(buttonsLayout);
		dialogLayout.set(buttons, 2, 1, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, true);

		final UIButton buttonOK = uiFactory.createButton(buttons);
		buttonOK.setText(TuxGuitar.getProperty("ok"));
		buttonOK.setDefaultButton();
		buttonOK.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				dialog.dispose();
			}
		});
		buttonsLayout.set(buttonOK, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, 80f, 25f, null);

		TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}

	public UIImage resolveImage(TGContext context, Integer style) {
		if( STYLE_ERROR.equals(style) ) {
			return TGIconManager.getInstance(context).getStatusError();
		}
		if( STYLE_WARNING.equals(style) ) {
			return TGIconManager.getInstance(context).getStatusWarning();
		}
		return TGIconManager.getInstance(context).getStatusInfo();
	}
}
