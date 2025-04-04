package app.tuxguitar.ui.qt.resource;

import java.io.InputStream;

import app.tuxguitar.ui.qt.QTComponent;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.resource.UIPainter;
import org.qtjambi.qt.gui.QIcon;
import org.qtjambi.qt.gui.QImage;
import org.qtjambi.qt.gui.QImageReader;
import org.qtjambi.qt.gui.QPainter;
import org.qtjambi.qt.gui.QPixmap;

public class QTImage extends QTComponent<QImage> implements UIImage {

	public QTImage(QImage pixmap){
		super(pixmap);
	}

	public QTImage(float width, float height){
		this(new QImage(Math.round(width), Math.round(height), QImage.Format.Format_RGB32));
	}

	public QTImage(InputStream inputStream){
		this(new QImageReader(new QTInputStream(inputStream)).read());
	}

	public float getWidth() {
		return this.getControl().width();
	}

	public float getHeight() {
		return this.getControl().height();
	}

	public UIPainter createPainter() {
		return new QTPainter(new QPainter(this.getControl()));
	}

	public QIcon createIcon() {
		return new QIcon(this.createPixmap());
	}

	public QPixmap createPixmap() {
		return QPixmap.fromImage(this.getControl());
	}
}
