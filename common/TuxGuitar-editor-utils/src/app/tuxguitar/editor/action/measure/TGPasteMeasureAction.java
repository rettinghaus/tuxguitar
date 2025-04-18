package app.tuxguitar.editor.action.measure;

import app.tuxguitar.action.TGActionContext;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionBase;
import app.tuxguitar.editor.clipboard.TGClipboard;
import app.tuxguitar.song.helpers.TGSongSegment;
import app.tuxguitar.song.helpers.TGSongSegmentHelper;
import app.tuxguitar.song.managers.TGSongManager;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGMeasureHeader;
import app.tuxguitar.song.models.TGSong;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.util.TGContext;

public class TGPasteMeasureAction extends TGActionBase{

	public static final String NAME = "action.measure.paste";

	public static final String ATTRIBUTE_PASTE_MODE = "pasteMode";
	public static final String ATTRIBUTE_PASTE_COUNT = "pasteCount";

	public static final Integer TRANSFER_TYPE_REPLACE = 1;
	public static final Integer TRANSFER_TYPE_INSERT = 2;

	public TGPasteMeasureAction(TGContext context) {
		super(context, NAME);
	}

	protected void processAction(TGActionContext context){
		Integer pasteMode = context.getAttribute(ATTRIBUTE_PASTE_MODE);
		Integer pasteCount = context.getAttribute(ATTRIBUTE_PASTE_COUNT);

		if( pasteMode > 0 && pasteCount > 0 ) {
			TGSongManager songManager = this.getSongManager(context);
			TGSongSegmentHelper helper = new TGSongSegmentHelper(songManager);
			TGSongSegment segment = TGClipboard.getInstance(this.getContext()).getSegment();
			if( segment != null ) {
				segment = helper.createSegmentCopies(segment, pasteCount);
				if(!segment.isEmpty()) {
					TGSong song = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG);
					TGTrack track = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK);
					TGMeasure measure = context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE);
					TGMeasureHeader first = segment.getHeaders().get(0);

					//Si el segmento tiene una sola pista,
					//la pego en la pista seleccionada
					int toTrack = (segment.getTracks().size() == 1 ? track.getNumber() : 0);

					if( pasteMode.equals(TRANSFER_TYPE_REPLACE)) {
						int count = segment.getHeaders().size();
						int freeSpace =  (track.countMeasures() - (measure.getNumber() - 1));
						long theMove = (measure.getStart() - first.getStart());

						for(int i = freeSpace; i < count; i ++){
							songManager.addNewMeasureBeforeEnd(song);
						}
						segment = segment.clone(songManager.getFactory());
						helper.replaceMeasures(song, segment, theMove, toTrack);
					} else if( pasteMode.equals(TRANSFER_TYPE_INSERT)) {
						int fromNumber = measure.getNumber();
						long theMove = (measure.getStart() - first.getStart());
						segment = segment.clone(songManager.getFactory());
						helper.insertMeasures(song, segment, fromNumber, theMove, toTrack);
					}
				}
			}
		}
	}
}
