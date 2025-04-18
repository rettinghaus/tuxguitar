package app.tuxguitar.jack.sequencer;

public class JackEvent {

	public static final int ALL_TRACKS = -1;

	public static final int MIDI_SYSTEM_EVENT = 1;
	public static final int MIDI_EVENT_NOTEON = 2;
	public static final int MIDI_EVENT_NOTEOFF = 3;
	public static final int MIDI_EVENT_PROGRAM_CHANGE = 4;
	public static final int MIDI_EVENT_CONTROL_CHANGE = 5;
	public static final int MIDI_EVENT_PITCH_BEND = 6;

	private long tick;
	private int type;
	private int track;
	private byte[] data;

	public JackEvent(long tick,int type,byte[] data){
		this(tick,type,ALL_TRACKS,data);
	}

	public JackEvent(long tick,int type,int track,byte[] data){
		this.tick = tick;
		this.type = type;
		this.track = track;
		this.data = data;
	}

	public long getTick() {
		return this.tick;
	}

	public int getType() {
		return this.type;
	}

	public int getTrack() {
		return this.track;
	}

	public byte[] getData() {
		return this.data;
	}

	public static JackEvent systemReset(long tick){
		return new JackEvent(tick,MIDI_SYSTEM_EVENT,ALL_TRACKS,null);
	}

	public static JackEvent noteOn(long tick,int track,int channel,int key,int velocity,int voice,boolean bendMode){
		return new JackEvent(tick,MIDI_EVENT_NOTEON,track,new byte[]{(byte)channel,(byte)key,(byte)velocity,(byte)voice,(byte)(bendMode?1:0)});
	}

	public static JackEvent noteOff(long tick,int track,int channel,int key,int velocity,int voice,boolean bendMode){
		return new JackEvent(tick,MIDI_EVENT_NOTEOFF,track,new byte[]{(byte)channel,(byte)key,(byte)velocity,(byte)voice,(byte)(bendMode?1:0)});
	}

	public static JackEvent pitchBend(long tick,int track,int channel,int value,int voice,boolean bendMode){
		return new JackEvent(tick,MIDI_EVENT_PITCH_BEND,track,new byte[]{(byte)channel,(byte)value,(byte)voice,(byte)(bendMode?1:0)});
	}

	public static JackEvent controlChange(long tick,int track,int channel,int controller,int value){
		return new JackEvent(tick,MIDI_EVENT_CONTROL_CHANGE,track,new byte[]{(byte)channel,(byte)controller,(byte)value});
	}

	public static JackEvent programChange(long tick,int track,int channel,int value){
		return new JackEvent(tick,MIDI_EVENT_PROGRAM_CHANGE,track,new byte[]{(byte)channel,(byte)value});
	}

	public static JackEvent tempoInUSQ(long tick,int usq){
		return new JackEvent(tick,MIDI_SYSTEM_EVENT,new byte[]{0x51,(byte) (usq & 0xff),(byte) ((usq >> 8) & 0xff),(byte) ((usq >> 16) & 0xff)});
	}
}
