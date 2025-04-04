package app.tuxguitar.app.action.impl.effects;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionManager;
import app.tuxguitar.app.action.impl.view.TGOpenViewAction;
import app.tuxguitar.app.view.dialog.tremolobar.TGTremoloBarDialogController;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGOpenTremoloBarDialogAction extends TGActionBase{

	public static final String NAME = "action.gui.open-tremolo-bar-dialog";

	public TGOpenTremoloBarDialogAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext tgActionContext){
		TGTrack track = tgActionContext.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
		if (!track.isPercussion()) {
			tgActionContext.setAttribute(TGOpenViewAction.ATTRIBUTE_CONTROLLER, new TGTremoloBarDialogController());
			TGActionManager.getInstance(getContext()).execute(TGOpenViewAction.NAME, tgActionContext);
		}
	}
}
