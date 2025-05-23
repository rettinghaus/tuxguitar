package app.tuxguitar.player.base;

import app.tuxguitar.song.models.TGDuration;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;

public class MidiRepeatController {

	private TGSong song;
	private int count;
	private int index;
	private int lastIndex;
	private boolean shouldPlay;
	private boolean repeatOpen;
	private long repeatStart;
	private long repeatEnd;
	private long repeatMove;
	private int repeatStartIndex;
	private int repeatNumber;
	private int repeatAlternative;
	private int sHeader;
	private int eHeader;

	public MidiRepeatController(TGSong song, int sHeader , int eHeader){
		this.song = song;
		this.sHeader = sHeader;
		this.eHeader = eHeader;
		this.count = song.countMeasureHeaders();
		this.index = 0;
		this.lastIndex = -1;		// index of last played measure
		this.shouldPlay = true;
		this.repeatOpen = true;
		this.repeatAlternative = 0;
		this.repeatStart = TGDuration.QUARTER_TIME;
		this.repeatEnd = 0;
		this.repeatMove = 0;	// offset (nb of ticks) to add to beat start because of repeats (right shift)
		this.repeatStartIndex = 0;
		this.repeatNumber = 0;
	}

	public void process(){
		TGMeasureHeader header = this.song.getMeasureHeader(this.index);

		// parsing is already finished if measure is after range
		if( this.eHeader >= 0 && header.getNumber() > this.eHeader ) {
			this.shouldPlay = false;
			this.index ++;
			return;
		}

		//Abro repeticion siempre para el primer compas.
		// first measure is default open repeat
		if( (this.sHeader >= 0 && header.getNumber() == this.sHeader ) || header.getNumber() == 1 ){
			this.repeatStartIndex = this.index;
			this.repeatStart = header.getStart();
			this.repeatOpen = true;
		}

		//Por defecto el compas deberia sonar
		// by default measure shall be played
		this.shouldPlay = true;

		//En caso de existir una repeticion nueva,
		//guardo el indice de el compas donde empieza una repeticion
		// if repeat opens, store where it starts
		if (header.isRepeatOpen()) {
			this.repeatStartIndex = this.index;
			this.repeatStart = header.getStart();
			this.repeatOpen = true;

			//Si es la primer vez que paso por este compas
			//Pongo numero de repeticion y final alternativo en cero
			// reset repeat counter if this is the first time this measure is parsed
			if(this.index > this.lastIndex){
				this.repeatNumber = 0;
				this.repeatAlternative = 0;
			}
		}
		else{
			//verifico si hay un final alternativo abierto
			// update current repeat alternative
			if(this.repeatAlternative == 0){
				this.repeatAlternative = header.getRepeatAlternative();
			}
			//Si estoy en un final alternativo.
			//el compas solo puede sonar si el numero de repeticion coincide con el numero de final alternativo.
			// if in an alternative repeat, measure shall only be played if it corresponds to current repeat number
			if (this.repeatOpen && (this.repeatAlternative > 0) && ((this.repeatAlternative & (1 << (this.repeatNumber))) == 0)){
				this.repeatMove -= header.getLength();
				if (header.getRepeatClose() >0){
					this.repeatAlternative = 0;
				}
				this.shouldPlay = false;
			}
		}

		//antes de ejecutar una posible repeticion
		//guardo el indice del ultimo compas tocado
		if (this.shouldPlay) {
			this.lastIndex = Math.max(this.lastIndex,this.index);

			//si hay una repeticion la hago
			// repeat close (ignored if it's the last measure in loop)
			if (this.repeatOpen && (header.getRepeatClose() > 0) && (this.eHeader < 0 || header.getNumber() < this.eHeader)) {
				if (this.repeatNumber < header.getRepeatClose() || (this.repeatAlternative > 0)) {
					this.repeatEnd = header.getStart() + header.getLength();
					this.repeatMove += this.repeatEnd - this.repeatStart;
					this.index = this.repeatStartIndex - 1;
					this.repeatNumber++;
				} else{
					this.repeatStart = 0;
					this.repeatNumber = 0;
					this.repeatEnd = 0;
					this.repeatOpen = false;
				}
				this.repeatAlternative = 0;
			}
		}
		this.index ++;

		//Verifica si el compas esta dentro del rango.
		// check measure is in loop range (if any loop defined)
		if( (this.sHeader >= 0 && header.getNumber() < this.sHeader) || ( this.eHeader >= 0 && header.getNumber() > this.eHeader ) ){
			this.shouldPlay = false;
		}
		// no repeat move before starting loop (if any)
		if( this.sHeader >= 0 && header.getNumber() < this.sHeader ) {
			this.repeatMove = 0;
		}
	}

	public boolean finished(){
		return (this.index >= this.count);
	}

	public boolean shouldPlay(){
		return this.shouldPlay;
	}

	public int getIndex(){
		return this.index;
	}

	public long getRepeatMove(){
		return this.repeatMove;
	}
}
