package app.tuxguitar.android.action;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.action.TGActionException;
import app.tuxguitar.event.TGEventManager;
import app.tuxguitar.util.error.TGErrorHandler;

public class TGActionAsyncProcess implements TGErrorHandler {

	public static final String ATTRIBUTE_ERROR_HANDLED = "errorHandled";

	private TGActionBase action;
	private TGActionContext actionContext;

	public TGActionAsyncProcess(TGActionBase action, TGActionContext actionContext) {
		this.action = action;
		this.actionContext = actionContext;
	}

	public TGActionBase getAction() {
		return this.action;
	}

	public TGActionContext getActionContext() {
		return this.actionContext;
	}

	public TGEventManager getEventManager() {
		return TGEventManager.getInstance(this.getAction().getContext());
	}

	public void onStart() {
		getEventManager().fireEvent(new TGActionAsyncProcessStartEvent(this.getAction().getName(), this.actionContext));
	}

	public void onFinish() {
		getEventManager().fireEvent(new TGActionAsyncProcessFinishEvent(this.getAction().getName(), this.actionContext));
	}

	public void handleError(Throwable throwable) {
		getEventManager().fireEvent(new TGActionAsyncProcessErrorEvent(this.getAction().getName(), this.actionContext, throwable));

		if( this.actionContext != null ) {
			Boolean errorHandled = this.actionContext.getAttribute(ATTRIBUTE_ERROR_HANDLED);
			if( errorHandled == null || !errorHandled) {
				throw new TGActionException(throwable);
			}
		}
	}
}
