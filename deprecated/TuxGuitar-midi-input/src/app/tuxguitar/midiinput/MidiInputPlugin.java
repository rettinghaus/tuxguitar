package app.tuxguitar.midiinput;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.tools.custom.TGToolItemPlugin;
import app.tuxguitar.app.view.main.TGWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.error.TGErrorManager;
import app.tuxguitar.util.plugin.TGPluginException;

public class MidiInputPlugin
	extends TGToolItemPlugin
{
	public void connect(TGContext context) {
		MiConfig.init(context);

		// try to setup the environment according to the user's preferences

		String	notesDeviceName = MiConfig.instance().getMidiInputPortName();

		if(notesDeviceName != null)
			{
			try {
				MiPort.setNotesPort(notesDeviceName);
				}
			catch(MiException mie)
				{
				TGErrorManager.getInstance(TuxGuitar.getInstance().getContext()).handleError(mie);
				}
			}

		MiProvider.instance().setBaseChannel	(MiConfig.instance().getMidiBaseChannel());
		MiProvider.instance().setMode			(MiConfig.instance().getMode());
		MiProvider.instance().setMinVelocity	((byte)MiConfig.instance().getMinVelocity());
		MiProvider.instance().setMinDuration	(MiConfig.instance().getMinDuration());
		MiProvider.instance().setEchoTimeOut	(MiConfig.instance().getEchoTimeOut());
		MiProvider.instance().setInputTimeOut	(MiConfig.instance().getInputTimeOut());

		super.connect(context);
	}

	public void disconnect(TGContext context) throws TGPluginException {
		try {
			MiPort.setNotesPort(null);
			MiPort.setControlPort(null);
			}
		catch(MiException mie)
			{
			TGErrorManager.getInstance(TuxGuitar.getInstance().getContext()).handleError(mie);
			}

		super.disconnect(context);
	}

//	public void setEnabled(boolean enabled)
//		throws TGPluginException
//	{
//	if(enabled)
//		{
//		// try to setup the environment according to the user's preferences
//
//		String	notesDeviceName = MiConfig.instance().getMidiInputPortName();
//
//		if(notesDeviceName != null)
//			{
//			try {
//				MiPort.setNotesPort(notesDeviceName);
//				}
//			catch(MiException mie)
//				{
//				TGErrorManager.getInstance(TuxGuitar.getInstance().getContext()).handleError(mie);
//				}
//			}
//
//		MiProvider.instance().setBaseChannel	(MiConfig.instance().getMidiBaseChannel());
//		MiProvider.instance().setMode			(MiConfig.instance().getMode());
//		MiProvider.instance().setMinVelocity	((byte)MiConfig.instance().getMinVelocity());
//		MiProvider.instance().setMinDuration	(MiConfig.instance().getMinDuration());
//		MiProvider.instance().setEchoTimeOut	(MiConfig.instance().getEchoTimeOut());
//		MiProvider.instance().setInputTimeOut	(MiConfig.instance().getInputTimeOut());
//		}
//	else
//		{
//		// try to cleanup the environment
//
//		try {
//			MiPort.setNotesPort(null);
//			MiPort.setControlPort(null);
//			}
//		catch(MiException mie)
//			{
//			TGErrorManager.getInstance(TuxGuitar.getInstance().getContext()).handleError(mie);
//			}
//		}
//
//	super.setEnabled(enabled);
//	}

	/*
	 *	TGPlugin implementation
	 */
	public String getModuleId()
	{
	return MiPlugin.MODULE_ID;
	}

	/*
	 *	TGToolItemPlugin implementation
	 */

	protected void doAction(TGContext context)
	{
	MiPanel.instance().showDialog(context, TGWindow.getInstance(context).getWindow());
	}


	protected String getItemName()
	{
	return "MIDI input";
	}
}
