package app.tuxguitar.app.view.dialog.bend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.tuxguitar.app.TuxGuitar;
import app.tuxguitar.app.system.icons.TGColorManager;
import app.tuxguitar.app.system.icons.TGColorManager.TGSkinnableColor;
import app.tuxguitar.app.ui.TGApplication;
import app.tuxguitar.app.view.controller.TGViewContext;
import app.tuxguitar.app.view.util.TGDialogUtil;
import app.tuxguitar.document.TGDocumentContextAttributes;
import app.tuxguitar.editor.action.TGActionProcessor;
import app.tuxguitar.editor.action.effect.TGChangeBendNoteAction;
import app.tuxguitar.song.factory.TGFactory;
import app.tuxguitar.song.models.TGNote;
import app.tuxguitar.song.models.effects.TGEffectBend;
import app.tuxguitar.song.models.effects.TGEffectBend.BendPoint;
import app.tuxguitar.ui.UIFactory;
import app.tuxguitar.ui.appearance.UIAppearance;
import app.tuxguitar.ui.appearance.UIColorAppearance;
import app.tuxguitar.ui.event.UIMouseEvent;
import app.tuxguitar.ui.event.UIMouseUpListener;
import app.tuxguitar.ui.event.UIPaintEvent;
import app.tuxguitar.ui.event.UIPaintListener;
import app.tuxguitar.ui.event.UISelectionEvent;
import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.layout.UITableLayout;
import app.tuxguitar.ui.resource.UIColorModel;
import app.tuxguitar.ui.resource.UIPainter;
import app.tuxguitar.ui.resource.UIPosition;
import app.tuxguitar.ui.widget.UIButton;
import app.tuxguitar.ui.widget.UICanvas;
import app.tuxguitar.ui.widget.UIListBoxSelect;
import app.tuxguitar.ui.widget.UIPanel;
import app.tuxguitar.ui.widget.UISelectItem;
import app.tuxguitar.ui.widget.UIWindow;
import app.tuxguitar.util.TGContext;
import app.tuxguitar.util.TGNoteRange;

public class TGBendDialog {

	private static final int X_SPACING = 30;
	private static final int Y_SPACING = 15;
	private static final int X_LENGTH = TGEffectBend.MAX_POSITION_LENGTH + 1;
	private static final int Y_LENGTH = TGEffectBend.MAX_VALUE_LENGTH + 1;

	private static final String COLOR_BACKGROUND = "widget.bendEditor.backgroundColor";
	private static final String COLOR_BORDER = "widget.bendEditor.border";
	private static final String COLOR_BEND_LINE = "widget.bendEditor.bendLine";
	private static final String COLOR_BEND_POINT = "widget.bendEditor.bendPoint";
	private static final String COLOR_LINE_1 = "widget.bendEditor.line.1";
	private static final String COLOR_LINE_2 = "widget.bendEditor.line.2";
	private static final String COLOR_LINE_3 = "widget.bendEditor.line.3";

	private int[] x;
	private int[] y;
	private int width;
	private int height;
	private List<UIPosition> points;
	private UICanvas editor;
	private TGColorManager colorManager;

	public TGBendDialog() {
		this.init();
	}

	private void init(){
		this.x = new int[X_LENGTH];
		this.y = new int[Y_LENGTH];
		this.width = ((X_SPACING * X_LENGTH) - X_SPACING);
		this.height = ((Y_SPACING * Y_LENGTH) - Y_SPACING);
		this.points = new ArrayList<UIPosition>();

		for(int i = 0;i < this.x.length;i++){
			this.x[i] = ((i + 1) * X_SPACING);
		}
		for(int i = 0;i < this.y.length;i++){
			this.y[i] = ((i + 1) * Y_SPACING);
		}
	}

	public void show(final TGViewContext context){
		final TGNoteRange noteRange = (TGNoteRange) context.getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE);
		TGEffectBend bend = null;

		if( (noteRange != null) && !noteRange.isEmpty() ) {
			final UIAppearance appearance = TGApplication.getInstance(context.getContext()).getAppearance();;
			final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
			final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
			final UITableLayout dialogLayout = new UITableLayout();
			final UIWindow dialog = uiFactory.createWindow(uiParent, true, false);

			dialog.setLayout(dialogLayout);
			dialog.setText(TuxGuitar.getProperty("bend.editor"));

			//----------------------------------------------------------------------
			UITableLayout compositeLayout = new UITableLayout();
			UIPanel composite = uiFactory.createPanel(dialog, false);
			composite.setLayout(compositeLayout);
			dialogLayout.set(composite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			UITableLayout leftCompositeLayout = new UITableLayout();
			UIPanel leftComposite = uiFactory.createPanel(composite, false);
			leftComposite.setLayout(leftCompositeLayout);
			compositeLayout.set(leftComposite, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			UITableLayout rightCompositeLayout = new UITableLayout();
			UIPanel rightComposite = uiFactory.createPanel(composite, false);
			rightComposite.setLayout(rightCompositeLayout);
			compositeLayout.set(rightComposite, 1, 2, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			//-------------EDITOR---------------------------------------------------
			this.colorManager = TGColorManager.getInstance(context.getContext());
			this.colorManager.appendSkinnableColors(new TGSkinnableColor[] {
				new TGSkinnableColor(COLOR_BACKGROUND, appearance.getColorModel(UIColorAppearance.InputBackground)),
				new TGSkinnableColor(COLOR_BORDER, appearance.getColorModel(UIColorAppearance.InputForeground)),
				new TGSkinnableColor(COLOR_BEND_POINT, appearance.getColorModel(UIColorAppearance.InputForeground)),
				new TGSkinnableColor(COLOR_BEND_LINE, new UIColorModel(0x80, 0x80, 0x80)),
				new TGSkinnableColor(COLOR_LINE_1, new UIColorModel(0x80, 0x80, 0x80)),
				new TGSkinnableColor(COLOR_LINE_2, new UIColorModel(0x80, 0x00, 0x00)),
				new TGSkinnableColor(COLOR_LINE_3, new UIColorModel(0x00, 0x00, 0x80))
			});

			this.editor = uiFactory.createCanvas(leftComposite, true);
			this.editor.setBgColor(this.colorManager.getColor(COLOR_BACKGROUND));
			this.editor.addPaintListener(new UIPaintListener() {
				public void onPaint(UIPaintEvent event) {
					paintEditor(event.getPainter());
				}
			});
			this.editor.addMouseUpListener(new UIMouseUpListener() {
				public void onMouseUp(UIMouseEvent event) {
					TGBendDialog.this.checkPoint(event.getPosition().getX(), event.getPosition().getY());
					TGBendDialog.this.editor.redraw();
				}
			});
			leftCompositeLayout.set(this.editor, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true, 1, 1, getWidth() + (X_SPACING * 2f), getHeight() + (Y_SPACING * 2f), null);

			//-------------DEFAULT BEND LIST---------------------------------------------------
			final List<UISelectItem<TGEffectBend>> presetItems = this.createPresetItems();
			final UIListBoxSelect<TGEffectBend> defaultBendList = uiFactory.createListBoxSelect(rightComposite);

			for(UISelectItem<TGEffectBend> presetItem : presetItems) {
				defaultBendList.addItem(presetItem);
			}
			defaultBendList.setSelectedItem(presetItems.get(0));
			defaultBendList.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					TGEffectBend selection = defaultBendList.getSelectedValue();
					if( selection != null ){
						setBend(selection);
						TGBendDialog.this.editor.redraw();
					}
				}
			});
			rightCompositeLayout.set(defaultBendList, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);

			//------------------BUTTONS--------------------------
			UIButton buttonClean = uiFactory.createButton(rightComposite);
			buttonClean.setText(TuxGuitar.getProperty("clean"));
			buttonClean.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeBend(context.getContext(), noteRange, null);
					dialog.dispose();
				}
			});
			rightCompositeLayout.set(buttonClean, 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, true, 1, 1, 80f, 25f, null);

			UIButton buttonOK = uiFactory.createButton(rightComposite);
			buttonOK.setDefaultButton();
			buttonOK.setText(TuxGuitar.getProperty("ok"));
			buttonOK.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					changeBend(context.getContext(), noteRange, getBend());
					dialog.dispose();
				}
			});
			rightCompositeLayout.set(buttonOK, 3, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, false, 1, 1, 80f, 25f, null);

			UIButton buttonCancel = uiFactory.createButton(rightComposite);
			buttonCancel.setText(TuxGuitar.getProperty("cancel"));
			buttonCancel.addSelectionListener(new UISelectionListener() {
				public void onSelect(UISelectionEvent event) {
					dialog.dispose();
				}
			});
			rightCompositeLayout.set(buttonCancel, 4, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_BOTTOM, true, false, 1, 1, 80f, 25f, null);

			// look for first note with bend effect within selection to initialize dialog
			Iterator<TGNote> it = noteRange.getNotes().iterator();
			while (it.hasNext() && (bend == null)) {
				TGNote n = it.next();
				if (n.getEffect().isBend()) {
					bend = n.getEffect().getBend();
				}
			}
			if (bend == null) {
				// nothing found, create new
				bend = presetItems.get(0).getValue();
			}
			setBend(bend);
			TGDialogUtil.openDialog(dialog, TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
		}
	}

	private void paintEditor(UIPainter painter){
		for(int i = 0;i < this.x.length;i++){
			this.setStyleX(painter,i);
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo(this.x[i],Y_SPACING);
			painter.lineTo(this.x[i],Y_SPACING + this.height);
			painter.closePath();
		}
		for(int i = 0;i < this.y.length;i++){
			this.setStyleY(painter,i);
			painter.initPath();
			painter.setAntialias(false);
			painter.moveTo(X_SPACING,this.y[i]);
			painter.lineTo(X_SPACING + this.width,this.y[i]);
			painter.closePath();
		}

		painter.setLineStyleSolid();
		painter.setLineWidth(2);
		painter.setForeground(this.colorManager.getColor(COLOR_BEND_LINE));

		UIPosition prevPoint = null;
		for(UIPosition point : this.points) {
			if( prevPoint != null ){
				painter.initPath();
				painter.moveTo(prevPoint.getX(), prevPoint.getY());
				painter.lineTo(point.getX(), point.getY());
				painter.closePath();
			}
			prevPoint = point;
		}

		painter.setLineWidth(5);
		painter.setForeground(this.colorManager.getColor(COLOR_BEND_POINT));

		for(UIPosition point : this.points) {
			painter.initPath();
			painter.setAntialias(false);
			painter.addRectangle(point.getX() - 2,point.getY() - 2, 5, 5);
			painter.closePath();
		}
		painter.setLineWidth(1);
	}

	private void setStyleX(UIPainter painter,int i){
		painter.setLineStyleSolid();
		if(i == 0 || i == (X_LENGTH - 1)){
			painter.setForeground(this.colorManager.getColor(COLOR_BORDER));
		}else{
			painter.setForeground(this.colorManager.getColor(COLOR_LINE_3));
			if((i % 3) > 0){
				painter.setLineStyleDot();
			}
		}
	}

	private void setStyleY(UIPainter painter,int i){
		painter.setLineStyleSolid();
		if(i == 0 || i == (Y_LENGTH - 1)){
			painter.setForeground(this.colorManager.getColor(COLOR_BORDER));
		}else{
			painter.setForeground(this.colorManager.getColor(COLOR_LINE_2));

			if((i % 2) > 0){
				painter.setLineStyleDot();
				painter.setForeground(this.colorManager.getColor(COLOR_LINE_1));
			}else if((i % 4) > 0){
				painter.setLineStyleDot();
			}
		}
	}

	protected void checkPoint(float x, float y){
		UIPosition point = new UIPosition(this.getX(x),this.getY(y));
		if(!this.removePoint(point)){
			this.removePointsAtXLine(point.getX());
			this.addPoint(point);
			this.orderPoints();
		}
	}

	protected boolean removePoint(UIPosition point){
		UIPosition pointToRemove = null;

		Iterator<UIPosition> it = this.points.iterator();
		while(it.hasNext()){
			UIPosition currPoint = (UIPosition)it.next();
			if( currPoint.getX() == point.getX() && currPoint.getY() == point.getY() ){
				pointToRemove = currPoint;
				break;
			}
		}

		if( pointToRemove != null ) {
			this.points.remove(pointToRemove);
			return true;
		}
		return false;
	}

	protected void orderPoints(){
		for(int i = 0; i < this.points.size(); i++){
			UIPosition minPoint = null;
			for(int noteIdx = i;noteIdx < this.points.size();noteIdx++){
				UIPosition point = this.points.get(noteIdx);
				if(minPoint == null || point.getX() < minPoint.getX()){
					minPoint = point;
				}
			}
			this.points.remove(minPoint);
			this.points.add(i,minPoint);
		}
	}

	protected void removePointsAtXLine(float x){
		List<UIPosition> pointsToRemove = new ArrayList<UIPosition>();
		Iterator<UIPosition> it = this.points.iterator();
		while(it.hasNext()){
			UIPosition point = it.next();
			if( point.getX() == x ){
				pointsToRemove.add(point);
				break;
			}
		}
		this.points.removeAll(pointsToRemove);
	}

	protected void addPoint(UIPosition point){
		this.points.add(point);
	}

	protected float getX(float pointX){
		float currPointX = -1;
		for(int i = 0;i < this.x.length;i++){
			if(currPointX < 0){
				currPointX = this.x[i];
			}else{
				float distanceX = Math.abs(pointX - currPointX);
				float currDistanceX = Math.abs(pointX - this.x[i]);
				if( currDistanceX < distanceX ){
					currPointX = this.x[i];
				}
			}
		}
		return currPointX;
	}

	protected float getY(float pointY){
		float currPointY = -1;
		for(int i = 0; i < this.y.length; i++){
			if( currPointY < 0 ){
				currPointY = this.y[i];
			}else{
				float distanceX = Math.abs(pointY - currPointY);
				float currDistanceX = Math.abs(pointY - this.y[i]);
				if( currDistanceX < distanceX){
					currPointY = this.y[i];
				}
			}
		}
		return currPointY;
	}

	public boolean isEmpty(){
		return this.points.isEmpty();
	}

	public TGEffectBend getBend(){
		if(this.points != null && !this.points.isEmpty()){
			TGEffectBend bend = TuxGuitar.getInstance().getSongManager().getFactory().newEffectBend();
			for(UIPosition point : this.points){
				addBendPoint(bend, point);
			}
			return bend;
		}
		return null;
	}

	private void addBendPoint(TGEffectBend effect, UIPosition point){
		int position = 0;
		int value = 0;
		for(int i = 0; i < this.x.length; i++){
			if( point.getX() == this.x[i]){
				position = i;
			}
		}
		for(int i = 0; i < this.y.length; i++){
			if( point.getY() == this.y[i] ){
				value = (this.y.length - i) -1;
			}
		}
		effect.addPoint(position, value);
	}

	public void setBend(TGEffectBend effect){
		this.points.clear();
		Iterator<BendPoint> it = effect.getPoints().iterator();
		while(it.hasNext()){
			TGEffectBend.BendPoint bendPoint = (TGEffectBend.BendPoint)it.next();
			this.makePoint(bendPoint);
		}
	}

	private void makePoint(TGEffectBend.BendPoint bendPoint){
		int indexX = bendPoint.getPosition();
		int indexY = (this.y.length - bendPoint.getValue()) - 1;
		if( indexX >= 0 && indexX < this.x.length && indexY >= 0 && indexY < this.y.length ){
			UIPosition point = new UIPosition(0,0);
			point.setX(this.x[indexX]);
			point.setY(this.y[indexY]);
			this.points.add(point);
		}
	}

	public int getWidth(){
		return this.width;
	}

	public int getHeight(){
		return this.height;
	}

	private List<UISelectItem<TGEffectBend>> createPresetItems() {
		TGEffectBend bend = null;
		TGFactory factory = TuxGuitar.getInstance().getSongManager().getFactory();
		List<UISelectItem<TGEffectBend>> items = new ArrayList<UISelectItem<TGEffectBend>>();

		bend = factory.newEffectBend();
		bend.addPoint(0,0);
		bend.addPoint(6,(TGEffectBend.SEMITONE_LENGTH * 4));
		bend.addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
		items.add(new UISelectItem<TGEffectBend>(TuxGuitar.getProperty("bend.bend"), bend));

		bend = factory.newEffectBend();
		bend.addPoint(0,0);
		bend.addPoint(3,(TGEffectBend.SEMITONE_LENGTH * 4));
		bend.addPoint(6,(TGEffectBend.SEMITONE_LENGTH * 4));
		bend.addPoint(9,0);
		bend.addPoint(12,0);
		items.add(new UISelectItem<TGEffectBend>(TuxGuitar.getProperty("bend.bend-release"), bend));

		bend = factory.newEffectBend();
		bend.addPoint(0,0);
		bend.addPoint(2,(TGEffectBend.SEMITONE_LENGTH * 4));
		bend.addPoint(4,(TGEffectBend.SEMITONE_LENGTH * 4));
		bend.addPoint(6,0);
		bend.addPoint(8,0);
		bend.addPoint(10,(TGEffectBend.SEMITONE_LENGTH * 4));
		bend.addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
		items.add(new UISelectItem<TGEffectBend>(TuxGuitar.getProperty("bend.bend-release-bend"), bend));

		bend = factory.newEffectBend();
		bend.addPoint(0,(TGEffectBend.SEMITONE_LENGTH * 4));
		bend.addPoint(12,(TGEffectBend.SEMITONE_LENGTH * 4));
		items.add(new UISelectItem<TGEffectBend>(TuxGuitar.getProperty("bend.prebend"), bend));

		bend = factory.newEffectBend();
		bend.addPoint(0,(TGEffectBend.SEMITONE_LENGTH * 4));
		bend.addPoint(4,(TGEffectBend.SEMITONE_LENGTH * 4));
		bend.addPoint(8,0);
		bend.addPoint(12,0);
		items.add(new UISelectItem<TGEffectBend>(TuxGuitar.getProperty("bend.prebend-release"), bend));

		return items;
	}

	public void changeBend(TGContext context, TGNoteRange noteRange, TGEffectBend effect) {
		TGActionProcessor tgActionProcessor = new TGActionProcessor(context, TGChangeBendNoteAction.NAME);
		tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE, noteRange);
		tgActionProcessor.setAttribute(TGChangeBendNoteAction.ATTRIBUTE_EFFECT, effect);
		tgActionProcessor.process();
	}
}