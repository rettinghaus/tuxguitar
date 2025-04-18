package app.tuxguitar.android.view.dialog.tremoloBar;

import app.tuxguitar.song.models.effects.TGEffectTremoloBar;

public class TGTremoloBarPreset{

	private String name;
	private TGEffectTremoloBar tremoloBar;

	public TGTremoloBarPreset(String name, TGEffectTremoloBar tremoloBar){
		this.name = name;
		this.tremoloBar = tremoloBar;
	}

	public TGEffectTremoloBar getTremoloBar() {
		return this.tremoloBar;
	}

	public String getName() {
		return this.name;
	}
}