/*
 * Created on 20-mar-2006
 *
 * TODO: To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package app.tuxguitar.app.transport;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.util.MidiTickUtil;
import app.tuxguitar.document.TGDocumentManager;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.util.TGContext;

/**
 * @author julian
 *
 * TODO: To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class TGTransport{

	private TGContext context;

	public TGTransport(TGContext context) {
		this.context = context;
	}

	public TGSongManager getSongManager(){
		return TGDocumentManager.getInstance(this.context).getSongManager();
	}

	public TGSong getSong(){
		return TGDocumentManager.getInstance(this.context).getSong();
	}

	public void gotoFirst(){
		gotoMeasure(getSongManager().getFirstMeasureHeader(getSong()), true);
	}

	public void gotoLast(){
		gotoMeasure(getSongManager().getLastMeasureHeader(getSong()), true) ;
	}

	public void gotoNext(){
		MidiPlayer player = TuxGuitar.instance().getPlayer();
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(player.getTickPosition()));
		if(header != null){
			gotoMeasure(getSongManager().getNextMeasureHeader(getSong(), header), true);
		}
	}

	public void gotoPrevious(){
		MidiPlayer player = TuxGuitar.instance().getPlayer();
		TGMeasureHeader header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(player.getTickPosition()));
		if(header != null){
			gotoMeasure(getSongManager().getPrevMeasureHeader(getSong(), header), true);
		}
	}

	public void gotoMeasure(TGMeasureHeader header){
		gotoMeasure(header,false);
	}

	public void gotoMeasure(TGMeasureHeader header,boolean moveCaret){
		if(header != null){
			TGMeasure playingMeasure = null;
			if( TuxGuitar.instance().getPlayer().isRunning() ){
				TuxGuitar.instance().getEditorCache().updatePlayMode();
				playingMeasure = TuxGuitar.instance().getEditorCache().getPlayMeasure();
			}
			if( playingMeasure == null || playingMeasure.getHeader().getNumber() != header.getNumber() ){
				TuxGuitar.instance().getPlayer().setTickPosition(MidiTickUtil.getTick(header.getStart()));
				if(moveCaret){
					TuxGuitar.instance().getTablatureEditor().getTablature().getCaret().goToTickPosition();
					TuxGuitar.instance().updateCache(true);
				}
			}
		}
	}
}
