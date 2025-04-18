package app.tuxguitar.ui.jfx.widget;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

import app.tuxguitar.ui.event.UISelectionListener;
import app.tuxguitar.ui.jfx.event.JFXSelectionListenerManager;
import app.tuxguitar.ui.jfx.resource.JFXImage;
import app.tuxguitar.ui.resource.UIImage;
import app.tuxguitar.ui.widget.UIButton;

public class JFXButton extends JFXLabeled<Button> implements UIButton {

	private JFXSelectionListenerManager<ActionEvent> selectionListener;

	private UIImage image;

	public JFXButton(JFXContainer<? extends Region> parent) {
		super(new Button(), parent);

		this.selectionListener = new JFXSelectionListenerManager<ActionEvent>(this);
	}

	public UIImage getImage() {
		return this.image;
	}

	public void setImage(UIImage image) {
		this.image = image;
		this.getControl().setGraphic(this.image != null ? new ImageView(((JFXImage) this.image).getHandle()) : null);
	}

	public void setDefaultButton() {
		this.getControl().setDefaultButton(true);
	}

	public void addSelectionListener(UISelectionListener listener) {
		if( this.selectionListener.isEmpty() ) {
			this.getControl().setOnAction(this.selectionListener);
		}
		this.selectionListener.addListener(listener);
	}

	public void removeSelectionListener(UISelectionListener listener) {
		this.selectionListener.removeListener(listener);
		if( this.selectionListener.isEmpty() ) {
			this.getControl().setOnAction(null);
		}
	}
}
