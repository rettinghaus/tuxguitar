package app.tuxguitar.ui.resource;

public interface UIImage extends UIResource {

	float getWidth();

	float getHeight();

	UIPainter createPainter();
}
