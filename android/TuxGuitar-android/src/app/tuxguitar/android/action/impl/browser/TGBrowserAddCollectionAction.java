package app.tuxguitar.android.action.impl.browser;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.android.action.TGActionBase;
import app.tuxguitar.android.browser.TGBrowserCollection;
import app.tuxguitar.android.browser.TGBrowserManager;
import app.tuxguitar.android.browser.model.TGBrowserException;
import app.tuxguitar.util.TGContext;

public class TGBrowserAddCollectionAction extends TGActionBase {

	public static final String NAME = "action.browser.add-collection";

	public static final String ATTRIBUTE_COLLECTION = TGBrowserCollection.class.getName();

	public TGBrowserAddCollectionAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(final TGActionContext context) {
		try {
			TGBrowserCollection collection = context.getAttribute(ATTRIBUTE_COLLECTION);
			TGBrowserManager browserManager = TGBrowserManager.getInstance(getContext());

			browserManager.addCollection(collection);
			browserManager.storeCollections();
		} catch (TGBrowserException e) {
			throw new TGActionException(e);
		}
	}
}
