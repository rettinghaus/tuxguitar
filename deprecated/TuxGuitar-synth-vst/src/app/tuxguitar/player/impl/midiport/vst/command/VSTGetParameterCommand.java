package app.tuxguitar.player.impl.midiport.vst.command;

import java.io.IOException;

import app.tuxguitar.midi.synth.remote.TGAbstractCommand;
import app.tuxguitar.midi.synth.remote.TGConnection;

public class VSTGetParameterCommand extends TGAbstractCommand<Float> {

	public static final Integer COMMAND_ID = 12;

	private Integer index;

	public VSTGetParameterCommand(TGConnection connection, Integer index) {
		super(connection);

		this.index = index;
	}

	public Float process() throws IOException {
		this.writeInteger(COMMAND_ID);
		this.writeInteger(this.index);

		return this.readFloat();
	}
}
