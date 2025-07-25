package app.tuxguitar.app.view.dialog.piano;

import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.action.TGActionProcessorListener;
import app.tuxguitar.app.action.impl.caret.TGGoLeftAction;
import app.tuxguitar.app.action.impl.caret.TGGoRightAction;
import app.tuxguitar.app.action.impl.tools.TGOpenScaleDialogAction;
import app.tuxguitar.app.system.icons.TGIconManager;
import app.tuxguitar.app.transport.TGTransport;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.component.tab.Caret;
import app.tuxguitar.app.view.component.tab.TablatureEditor;
import app.tuxguitar.app.view.util.TGBufferedPainterListenerLocked;
import app.tuxguitar.app.view.util.TGBufferedPainterLocked.TGBufferedPainterHandle;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.TGEditorManager;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.duration.TGDecrementDurationAction;
import app.tuxguitar.editor.action.duration.TGIncrementDurationAction;
import app.tuxguitar.editor.action.note.TGChangeNoteAction;
import app.tuxguitar.editor.action.note.TGDeleteNoteAction;
import app.tuxguitar.graphics.control.TGNoteImpl;
import app.tuxguitar.player.base.MidiPlayer;
import app.tuxguitar.song.models.TGBeat;
import app.tuxguitar.song.models.TGMeasure;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.TGString;
import app.tuxguitar.song.models.TGTrack;
import app.tuxguitar.song.models.TGVoice;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIMouseUpListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICanvas;
import app.tuxguitar.ui.widget.UIControl;
import app.tuxguitar.ui.widget.UIImageView;
import app.tuxguitar.ui.widget.UILabel;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISeparator;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;

public class TGPiano {

	private static final boolean TYPE_NOTES[] = new boolean[]{true,false,true,false,true,true,false,true,false,true,false,true};
	private static final int NATURAL_NOTES = 7;
	private static final int MAX_OCTAVES = 8;
	private static final int NATURAL_WIDTH = 15;
	private static final int SHARP_WIDTH = 8;
	private static final int NATURAL_HEIGHT = 60;
	private static final int SHARP_HEIGHT = 40;

	private TGContext context;
	private int duration;
	private boolean changes;
	private TGPianoConfig config;
	private UIPanel control;
	private UIPanel toolComposite;
	private UICanvas canvas;
	private UIImageView durationLabel;
	private UILabel scaleName;
	private UIButton scale;
	private UIButton goLeft;
	private UIButton goRight;
	private UIButton increment;
	private UIButton decrement;
	private UIButton settings;
	private TGBeat beat;
	private TGBeat externalBeat;
	private UIImage image;

	public TGPiano(TGContext context, UIWindow parent) {
		this.context = context;
		this.config = new TGPianoConfig(context);
		this.config.load();
		this.control = getUIFactory().createPanel(parent, false);
		this.initToolBar();
		this.initCanvas();
		this.createControlLayout();
		this.loadIcons();
		this.loadProperties();

		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.toolComposite);
		TuxGuitar.getInstance().getKeyBindingManager().appendListenersTo(this.canvas);
	}

	public void createControlLayout() {
		UITableLayout uiLayout = new UITableLayout(0f);
		uiLayout.set(this.toolComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);
		uiLayout.set(this.canvas, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
		uiLayout.set(this.canvas, UITableLayout.PACKED_WIDTH, Float.valueOf(NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES)));
		uiLayout.set(this.canvas, UITableLayout.PACKED_HEIGHT, Float.valueOf(NATURAL_HEIGHT));

		this.control.setLayout(uiLayout);
	}

	private void initToolBar() {
		UIFactory uiFactory = getUIFactory();

		int column = 0;

		this.toolComposite = uiFactory.createPanel(this.control, false);
		this.createToolBarLayout();

		// position
		this.goLeft = uiFactory.createButton(this.toolComposite);
		this.goLeft.addSelectionListener(new TGActionProcessorListener(this.context, TGGoLeftAction.NAME));
		this.createToolItemLayout(this.goLeft, ++column);

		this.goRight = uiFactory.createButton(this.toolComposite);
		this.goRight.addSelectionListener(new TGActionProcessorListener(this.context, TGGoRightAction.NAME));
		this.createToolItemLayout(this.goRight, ++column);

		// separator
		this.createToolSeparator(uiFactory, ++column);

		// duration
		this.increment = uiFactory.createButton(this.toolComposite);
		this.increment.addSelectionListener(new TGActionProcessorListener(this.context, TGIncrementDurationAction.NAME));
		this.createToolItemLayout(this.increment, ++column);

		this.durationLabel = uiFactory.createImageView(this.toolComposite);
		this.createToolItemLayout(this.durationLabel, ++column);

		this.decrement = uiFactory.createButton(this.toolComposite);
		this.decrement.addSelectionListener(new TGActionProcessorListener(this.context, TGDecrementDurationAction.NAME));
		this.createToolItemLayout(this.decrement, ++column);

		// separator
		this.createToolSeparator(uiFactory, ++column);

		// scale
		this.scale = uiFactory.createButton(this.toolComposite);
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.scale.addSelectionListener(new TGActionProcessorListener(this.context, TGOpenScaleDialogAction.NAME));
		this.createToolItemLayout(this.scale, ++column);

		// scale name
		this.scaleName = uiFactory.createLabel(this.toolComposite);
		this.createToolItemLayout(this.scaleName, ++column, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_CENTER, false, false);

		// settings
		this.settings = uiFactory.createButton(this.toolComposite);
		this.settings.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.SETTINGS));
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.settings.addSelectionListener(new UISelectionListener() {
			public void onSelect(UISelectionEvent event) {
				configure();
			}
		});
		this.createToolItemLayout(this.settings, ++column, UITableLayout.ALIGN_RIGHT, UITableLayout.ALIGN_FILL, true, false);

		this.toolComposite.getLayout().set(goLeft, UITableLayout.MARGIN_LEFT, 0f);
		this.toolComposite.getLayout().set(this.settings, UITableLayout.MARGIN_RIGHT, 0f);
	}

	private void createToolBarLayout(){
		UITableLayout uiLayout = new UITableLayout();
		uiLayout.set(UITableLayout.MARGIN_LEFT, 0f);
		uiLayout.set(UITableLayout.MARGIN_RIGHT, 0f);

		this.toolComposite.setLayout(uiLayout);
	}

	private void createToolItemLayout(UIControl uiControl, int column){
		this.createToolItemLayout(uiControl, column, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, false, false);
	}

	private void createToolItemLayout(UIControl uiControl, int column, Integer alignX, Integer alignY, Boolean fillX, Boolean fillY){
		UITableLayout uiLayout = (UITableLayout) this.toolComposite.getLayout();
		uiLayout.set(uiControl, 1, column, alignX, alignY, fillX, fillX);
	}

	private void createToolSeparator(UIFactory uiFactory, int column){
		UISeparator uiSeparator = uiFactory.createVerticalSeparator(this.toolComposite);
		UITableLayout uiLayout = (UITableLayout) this.toolComposite.getLayout();
		uiLayout.set(uiSeparator, 1, column, UITableLayout.ALIGN_CENTER, UITableLayout.ALIGN_CENTER, false, false);
		uiLayout.set(uiSeparator, UITableLayout.PACKED_WIDTH, 20f);
		uiLayout.set(uiSeparator, UITableLayout.PACKED_HEIGHT, 20f);
	}

	private void loadDurationImage(boolean force) {
		int duration = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret().getDuration().getValue();
		if( force || this.duration != duration ){
			this.duration = duration;
			this.durationLabel.setImage(TuxGuitar.getInstance().getIconManager().getDuration(this.duration));
		}
	}

	private void loadScaleName() {
		int scaleKeyIndex = TuxGuitar.getInstance().getScaleManager().getSelectionKeyIndex();
		int scaleIndex = TuxGuitar.getInstance().getScaleManager().getScaleIndex();
		String key = TuxGuitar.getInstance().getScaleManager().getKeyName( scaleKeyIndex );
		String name = TuxGuitar.getInstance().getScaleManager().getScaleName( scaleIndex );
		this.scaleName.setText( ( key != null && name != null ) ? ( key + " - " + name ) : "" );
	}

	private void initCanvas(){
		this.image = makePianoImage();
		this.canvas = getUIFactory().createCanvas(this.control, true);
		this.canvas.addPaintListener(new TGBufferedPainterListenerLocked(this.context, new TGPianoPainterListener()));
		this.canvas.addMouseUpListener(new TGPianoMouseListener(this.context));
		this.canvas.setFocus();
	}

	/**
	 * Crea la imagen del piano
	 *
	 * @return
	 */
	private UIImage makePianoImage(){
		UIFactory factory = getUIFactory();
		UIImage image = factory.createImage((NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES)), NATURAL_HEIGHT);
		UIPainter painter = image.createPainter();

		int x = 0;
		int y = 0;
		painter.setBackground(this.config.getColorNatural());
		painter.initPath(UIPainter.PATH_FILL);
		painter.addRectangle(x,y,(NATURAL_WIDTH * (MAX_OCTAVES * NATURAL_NOTES) ),NATURAL_HEIGHT);
		painter.closePath();
		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){

			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				painter.setForeground(this.config.getColorNotNatural());
				painter.initPath();
				painter.setAntialias(false);
				painter.addRectangle(x,y,NATURAL_WIDTH,NATURAL_HEIGHT);
				painter.closePath();
				x += NATURAL_WIDTH;
			}else{
				painter.setBackground(this.config.getColorNotNatural());
				painter.initPath(UIPainter.PATH_FILL);
				painter.setAntialias(false);
				painter.addRectangle(x - (SHARP_WIDTH / 2),y,SHARP_WIDTH,SHARP_HEIGHT);
				painter.closePath();
			}
		}

		painter.dispose();
		return image;
	}

	/**
	 * Pinta la nota a partir del indice
	 *
	 * @param gc
	 * @param value
	 */
	private void paintScale(UIPainter painter){
		painter.setBackground(this.config.getColorScale());
		painter.setForeground(this.config.getColorScale());
		int posX = 0;

		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			int width = 0;

			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				width = NATURAL_WIDTH;
				if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
				if(!TYPE_NOTES[(i + 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
			}else{
				width = SHARP_WIDTH;
			}

			if(TuxGuitar.getInstance().getScaleManager().getScale().getNote(i)){
				if(TYPE_NOTES[i % TYPE_NOTES.length] ){
					int x = posX;
					if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
						x -= ((SHARP_WIDTH / 2));
					}

					int size = SHARP_WIDTH;
					painter.initPath(UIPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle( (x + 1 + (((NATURAL_WIDTH - size) / 2))) ,(NATURAL_HEIGHT - size - (((NATURAL_WIDTH - size) / 2))),size,size);
					painter.closePath();
				}else{
					painter.initPath(UIPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle(posX + 1, SHARP_HEIGHT - SHARP_WIDTH + 1,SHARP_WIDTH - 2,SHARP_WIDTH - 2);
					painter.closePath();
				}
			}

			posX += width;
		}
	}

	/**
	 * Pinta la nota a partir del indice
	 *
	 * @param gc
	 * @param value
	 */
	protected void paintNote(UIPainter painter,int value){
		if (value < 0)
			return;
		painter.setBackground(this.config.getColorNote());
		int posX = 0;
		int y = 0;

		for(int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++){
			int width = 0;

			if(TYPE_NOTES[i % TYPE_NOTES.length]){
				width = NATURAL_WIDTH;
				if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
				if(!TYPE_NOTES[(i + 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
			}else{
				width = SHARP_WIDTH;
			}

			if(i == value){
				if(TYPE_NOTES[i % TYPE_NOTES.length]){
					painter.initPath(UIPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle(posX + 1,y + 1,width - 1,SHARP_HEIGHT);

					int x = posX;
					if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
						x -= ((SHARP_WIDTH / 2));
					}
					painter.addRectangle(x + 1,(y + SHARP_HEIGHT) + 1,NATURAL_WIDTH - 1,(NATURAL_HEIGHT - SHARP_HEIGHT) - 1);
					painter.closePath();
				}else{
					painter.initPath(UIPainter.PATH_FILL);
					painter.setAntialias(false);
					painter.addRectangle(posX + 1,y + 1,width - 1,SHARP_HEIGHT - 1);
					painter.closePath();
				}
				return;
			}

			posX += width;
		}
	}

	protected void paintEditor(UIPainter painter) {
		this.updateEditor();

		painter.drawImage(this.image, 0, 0);

		// pinto notas
		if( this.beat != null ){
			for(int v = 0; v < this.beat.countVoices(); v ++){
				TGVoice voice = this.beat.getVoice( v );
				Iterator<TGNote> it = voice.getNotes().iterator();
				while(it.hasNext()){
					this.paintNote(painter, getRealNoteValue( it.next() ));
				}
			}
		}
		
		paintScale(painter);
	}

	// Retuns index of selected key or -1
	private int getSelection(float x, float y) {
		int sharpKey = isOnSharpKey(x, y); // if it is on a sharp key, that is returned
		if (sharpKey >= 0)
			return sharpKey;

		float posX = 0;
		
		for (int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++) {
			if (TYPE_NOTES[i % TYPE_NOTES.length]) { // it is a natural key?
				if (x>=posX && x<posX+NATURAL_WIDTH) {
					return i;
				}
				posX += NATURAL_WIDTH;
			}
		}

		return -1;
	}

	// This tells whether the point 'x, y' is on a sharp key and which one.
	// Returns -1 if it is not on a sharp key.
	private int isOnSharpKey(float x, float y) {
		float posX = 0;
		
		for (int i = 0; i < (MAX_OCTAVES * TYPE_NOTES.length); i ++) { // travels through all the keys. 8*12 keys (8 octaves, and 12 notes each)
			float width = 0f;
			boolean isSharp = false;
			
			if (TYPE_NOTES[i % TYPE_NOTES.length]) { // is a natural key?
				width = NATURAL_WIDTH;
				if(i > 0 && !TYPE_NOTES[(i - 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
				if(!TYPE_NOTES[(i + 1)  % TYPE_NOTES.length]){
					width -= ((SHARP_WIDTH / 2));
				}
			} else { // is sharp
				width = SHARP_WIDTH;
				isSharp = true;
			}

			if (x>=posX && x<posX+width && isSharp && y<SHARP_HEIGHT) {
				return i;
			}

			posX += width;
		}

		return -1;
	}

	protected void hit(float x, float y) {
		int value = this.getSelection(x, y);
		if (value <= -1)
			return;

		if(!this.removeNote(value)) {
			this.addNote(value);
		}
	}

	private boolean removeNote(int value) {
		if(this.beat != null){
			for(int v = 0; v < this.beat.countVoices(); v ++){
				TGVoice voice = this.beat.getVoice( v );
				Iterator<TGNote> it = voice.getNotes().iterator();
				while (it.hasNext()) {
					TGNote note = (TGNote) it.next();
					if( getRealNoteValue(note) == value ) {
						TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGDeleteNoteAction.NAME);
						tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, note);
						tgActionProcessor.process();

						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean addNote(int value) {
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();

		List<TGString> strings = caret.getTrack().getStrings();
		for(int i = 0;i < strings.size();i ++){
			TGString string = (TGString)strings.get(i);
			if(value >= string.getValue()){
				boolean emptyString = true;

				if(this.beat != null){
					for(int v = 0; v < this.beat.countVoices(); v ++){
						TGVoice voice = this.beat.getVoice( v );
						Iterator<TGNote> it = voice.getNotes().iterator();
						while (it.hasNext()) {
							TGNoteImpl note = (TGNoteImpl) it.next();
							if (note.getString() == string.getNumber()) {
								emptyString = false;
								break;
							}
						}
					}
				}
				if(emptyString){
					TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context, TGChangeNoteAction.NAME);
					tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_FRET, (value - string.getValue()));
					tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string);
					tgActionProcessor.process();

					return true;
				}
			}
		}
		return false;
	}

	protected int getRealNoteValue(TGNote note){
		TGVoice voice = note.getVoice();
		if( voice != null ){
			TGBeat beat = voice.getBeat();
			if( beat != null ){
				TGMeasure measure = beat.getMeasure();
				if( measure != null ){
					TGTrack track = measure.getTrack();
					if( track != null ){
						return ( note.getValue() + track.getString( note.getString() ).getValue() );
					}
				}
			}
		}
		// If note have no parents, uses current track strings.
		Caret caret = TuxGuitar.getInstance().getTablatureEditor().getTablature().getCaret();
		TGTrack track = caret.getTrack();
		if( track != null ){
			return ( note.getValue() + track.getString( note.getString() ).getValue() );
		}
		return 0;
	}

	public boolean hasChanges(){
		return this.changes;
	}

	public void setChanges(boolean changes){
		this.changes = changes;
	}

	public void setExternalBeat(TGBeat externalBeat){
		this.externalBeat = externalBeat;
	}

	public TGBeat getExternalBeat(){
		return this.externalBeat;
	}

	protected void updateEditor(){
		if( isVisible() ){
			if( hasChanges() ){
				this.image.dispose();
				this.image = makePianoImage();
			}
			if( MidiPlayer.getInstance(this.context).isRunning()){
				this.beat = TGTransport.getInstance(this.context).getCache().getPlayBeat();
			}else if(this.externalBeat != null){
				this.beat = this.externalBeat;
			}else{
				this.beat = TablatureEditor.getInstance(this.context).getTablature().getCaret().getSelectedBeat();
			}
		}
	}

	public void redraw() {
		if(!this.isDisposed()){
			this.control.redraw();
			this.canvas.redraw();
			this.loadDurationImage(false);
		}
	}

	public void redrawPlayingMode(){
		if(!this.isDisposed() ){
			this.canvas.redraw();
		}
	}

	public void setVisible(boolean visible) {
		this.control.setVisible(visible);
	}

	public boolean isVisible() {
		return (this.control.isVisible());
	}

	public boolean isDisposed() {
		return (this.control.isDisposed());
	}

	public void dispose(){
		this.control.dispose();
		this.image.dispose();
		this.config.dispose();
	}

	public void loadProperties(){
		this.scale.setText(TuxGuitar.getProperty("scale"));
		this.settings.setToolTipText(TuxGuitar.getProperty("settings"));
		this.loadScaleName();
		this.control.layout();
	}

	public void loadIcons(){
		this.goLeft.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_LEFT));
		this.goRight.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_RIGHT));
		this.decrement.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_DOWN));
		this.increment.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.ARROW_UP));
		this.settings.setImage(TuxGuitar.getInstance().getIconManager().getImageByName(TGIconManager.SETTINGS));
		this.loadDurationImage(true);
		this.control.layout();
	}

	public void loadScale(){
		this.loadScaleName();
		this.setChanges(true);
		this.control.layout();
	}

	public void configure(){
		this.config.configure((UIWindow) this.control.getParent());
	}

	public void reloadFromConfig() {
		this.setChanges(true);
		this.redraw();
	}

	public UIPanel getControl(){
		return this.control;
	}

	public UICanvas getCanvas() {
		return this.canvas;
	}

	public UIFactory getUIFactory() {
		return TGApplication.getInstance(this.context).getFactory();
	}

	private class TGPianoMouseListener implements UIMouseUpListener {

		private TGContext context;

		public TGPianoMouseListener(TGContext context){
			this.context = context;
		}

		public void onMouseUp(final UIMouseEvent event) {
			getCanvas().setFocus();
			if( event.getButton() == 1 ){
				if(!MidiPlayer.getInstance(this.context).isRunning()){
					TGEditorManager.getInstance(this.context).asyncRunLocked(new Runnable() {
						public void run() {
							if( getExternalBeat() == null ){
								hit(event.getPosition().getX(), event.getPosition().getY());
							}else{
								setExternalBeat( null );
								TuxGuitar.getInstance().updateCache(true);
							}
						}
					});
				}
			}else{
				new TGActionProcessor(TGPiano.this.context, TGGoRightAction.NAME).process();
			}
		}
	}

	private class TGPianoPainterListener implements TGBufferedPainterHandle {

		public TGPianoPainterListener(){
			super();
		}

		public void paintControl(UIPainter painter) {
			TGPiano.this.paintEditor(painter);
		}

		public UICanvas getPaintableControl() {
			return TGPiano.this.canvas;
		}
	}
}
