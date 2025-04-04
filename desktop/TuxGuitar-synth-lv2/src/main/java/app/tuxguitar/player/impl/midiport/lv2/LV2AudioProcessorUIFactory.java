package app.tuxguitar.player.impl.midiport.lv2;

import app.tuxguitar.midi.synth.TGAudioProcessor;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUI;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUICallback;
import app.tuxguitar.midi.synth.ui.TGAudioProcessorUIFactory;
import app.tuxguitar.player.impl.midiport.lv2.ui.LV2AudioProcessorUI;
import app.tuxguitar.util.TGContext;

public class LV2AudioProcessorUIFactory implements TGAudioProcessorUIFactory {

	private TGContext context;
	private LV2PluginValidator validator;

	public LV2AudioProcessorUIFactory(TGContext context) {
		this.context = context;
		this.validator = new LV2AudioPluginValidator();
	}

	public String getType() {
		return LV2Module.AUDIO_TYPE;
	}

	public TGAudioProcessorUI create(TGAudioProcessor processor, TGAudioProcessorUICallback callback) {
		return new LV2AudioProcessorUI(this.context, (LV2AudioProcessorWrapper) processor, this.validator, callback);
	}
}
