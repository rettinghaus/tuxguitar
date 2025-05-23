package app.tuxguitar.ui.jfx.resource;

import app.tuxguitar.ui.resource.UIKey;
import app.tuxguitar.ui.resource.UIKeyCombination;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class JFXKey {

	private static final JFXKeyMap[] KEY_MAP = new JFXKeyMap[] {
		new JFXKeyMap(KeyCode.F1, UIKey.F1),
		new JFXKeyMap(KeyCode.F2, UIKey.F2),
		new JFXKeyMap(KeyCode.F3, UIKey.F3),
		new JFXKeyMap(KeyCode.F4, UIKey.F4),
		new JFXKeyMap(KeyCode.F5, UIKey.F5),
		new JFXKeyMap(KeyCode.F6, UIKey.F6),
		new JFXKeyMap(KeyCode.F7, UIKey.F7),
		new JFXKeyMap(KeyCode.F8, UIKey.F8),
		new JFXKeyMap(KeyCode.F9, UIKey.F9),
		new JFXKeyMap(KeyCode.F10, UIKey.F10),
		new JFXKeyMap(KeyCode.F11, UIKey.F11),
		new JFXKeyMap(KeyCode.F12, UIKey.F12),
		new JFXKeyMap(KeyCode.ESCAPE, UIKey.ESC),
		new JFXKeyMap(KeyCode.PAUSE, UIKey.PAUSE),
		new JFXKeyMap(KeyCode.PRINTSCREEN, UIKey.PRINT_SCREEN),
		new JFXKeyMap(KeyCode.INSERT, UIKey.INSERT),
		new JFXKeyMap(KeyCode.DELETE, UIKey.DELETE),
		new JFXKeyMap(KeyCode.HOME, UIKey.HOME),
		new JFXKeyMap(KeyCode.PAGE_UP, UIKey.PAGE_UP),
		new JFXKeyMap(KeyCode.PAGE_DOWN, UIKey.PAGE_DOWN),
		new JFXKeyMap(KeyCode.END, UIKey.END),
		new JFXKeyMap(KeyCode.ALT, UIKey.ALT),
		new JFXKeyMap(KeyCode.CONTROL, UIKey.CONTROL),
		new JFXKeyMap(KeyCode.COMMAND, UIKey.COMMAND),
		new JFXKeyMap(KeyCode.SHIFT, UIKey.SHIFT),
		new JFXKeyMap(KeyCode.TAB, UIKey.TAB),
		new JFXKeyMap(KeyCode.BACK_SPACE, UIKey.BACKSPACE),
		new JFXKeyMap(KeyCode.SPACE, UIKey.SPACE),
		new JFXKeyMap(KeyCode.ENTER, UIKey.ENTER),
		new JFXKeyMap(KeyCode.UP, UIKey.UP),
		new JFXKeyMap(KeyCode.DOWN, UIKey.DOWN),
		new JFXKeyMap(KeyCode.LEFT, UIKey.LEFT),
		new JFXKeyMap(KeyCode.RIGHT, UIKey.RIGHT),
		new JFXKeyMap(KeyCode.ADD, new UIKey("+")),
		new JFXKeyMap(KeyCode.PLUS, new UIKey("+")),
		new JFXKeyMap(KeyCode.MINUS, new UIKey("-")),
		new JFXKeyMap(KeyCode.SUBTRACT, new UIKey("-")),
		new JFXKeyMap(KeyCode.COMMA, new UIKey(",")),
		new JFXKeyMap(KeyCode.PERIOD, new UIKey(".")),
		new JFXKeyMap(KeyCode.DECIMAL, new UIKey(".")),
		new JFXKeyMap(KeyCode.SLASH, new UIKey("/")),
		new JFXKeyMap(KeyCode.DIVIDE, new UIKey("/")),
		new JFXKeyMap(KeyCode.MULTIPLY, new UIKey("*")),
		new JFXKeyMap(KeyCode.LESS, new UIKey("<")),
		new JFXKeyMap(KeyCode.GREATER, new UIKey(">")),
		new JFXKeyMap(KeyCode.DIGIT0, new UIKey("0")),
		new JFXKeyMap(KeyCode.DIGIT1, new UIKey("1")),
		new JFXKeyMap(KeyCode.DIGIT2, new UIKey("2")),
		new JFXKeyMap(KeyCode.DIGIT3, new UIKey("3")),
		new JFXKeyMap(KeyCode.DIGIT4, new UIKey("4")),
		new JFXKeyMap(KeyCode.DIGIT5, new UIKey("5")),
		new JFXKeyMap(KeyCode.DIGIT6, new UIKey("6")),
		new JFXKeyMap(KeyCode.DIGIT7, new UIKey("7")),
		new JFXKeyMap(KeyCode.DIGIT8, new UIKey("8")),
		new JFXKeyMap(KeyCode.DIGIT9, new UIKey("9")),
		new JFXKeyMap(KeyCode.NUMPAD0, new UIKey("0")),
		new JFXKeyMap(KeyCode.NUMPAD1, new UIKey("1")),
		new JFXKeyMap(KeyCode.NUMPAD2, new UIKey("2")),
		new JFXKeyMap(KeyCode.NUMPAD3, new UIKey("3")),
		new JFXKeyMap(KeyCode.NUMPAD4, new UIKey("4")),
		new JFXKeyMap(KeyCode.NUMPAD5, new UIKey("5")),
		new JFXKeyMap(KeyCode.NUMPAD6, new UIKey("6")),
		new JFXKeyMap(KeyCode.NUMPAD7, new UIKey("7")),
		new JFXKeyMap(KeyCode.NUMPAD8, new UIKey("8")),
		new JFXKeyMap(KeyCode.NUMPAD9, new UIKey("9"))
	};

	public static UIKey getKey(KeyEvent keyEvent) {
		KeyCode keyCode = keyEvent.getCode();
		for(JFXKeyMap keyMap : KEY_MAP) {
			if( keyMap.getCode().equals(keyCode) ) {
				return keyMap.getKey();
			}
		}
		return new UIKey(keyCode.toString().toLowerCase());
	}

	public static UIKeyCombination getCombination(KeyEvent keyEvent) {
		/* workaround for non-EN keyboards without numpad (typ. azerty laptop)
		   with these keyboards, digits can be entered only with key combination, e.g. "shift+&" = "1"
		   Shortcuts configuration dialog shows "1" as an active shortcut, so when user types "shift+&" this shall be considered "1"
		   but, it's not possible to consider the typed character in all cases (even a non-printable character)
		   Workaround: if typed character is a digit, override key code and ignore shift modifier */
		UIKeyCombination keyCombination = new UIKeyCombination();
		boolean isDigit=false;
		if (keyEvent.getText() != null && keyEvent.getText().length()==1)  {
			try {
				Integer.parseInt(keyEvent.getText());
				isDigit = true;
			} catch(NumberFormatException e) {
				// nothing to do
			}
		}
		if( keyEvent.isAltDown() ) {
			keyCombination.getKeys().add(UIKey.ALT);
		}
		if( keyEvent.isShiftDown() && !isDigit) {
			keyCombination.getKeys().add(UIKey.SHIFT);
		}
		if( keyEvent.isControlDown() ) {
			keyCombination.getKeys().add(UIKey.CONTROL);
		}
		if( keyEvent.isMetaDown() ) {
			keyCombination.getKeys().add(UIKey.COMMAND);
		}

		UIKey principalKey = JFXKey.getKey(keyEvent);
		if(!keyCombination.contains(principalKey) && !isDigit) {
			keyCombination.getKeys().add(principalKey);
		}
		else if (isDigit)  {
			keyCombination.getKeys().add(new UIKey(keyEvent.getText()));
		}

		return keyCombination;
	}

	private static class JFXKeyMap {

		private KeyCode code;
		private UIKey key;

		public JFXKeyMap(KeyCode code, UIKey key) {
			this.code = code;
			this.key = key;
		}

		public KeyCode getCode() {
			return this.code;
		}

		public UIKey getKey() {
			return this.key;
		}
	}
}
