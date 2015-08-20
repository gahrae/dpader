package dpader;

import java.awt.Point;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

public class DPad {
	public static final char[][] DEFAULT_PAD = new char[][] {
			"0123456789".toCharArray(), 
			"ABCDEFGHIJ".toCharArray(),
			"KLMNOPQRST".toCharArray(), 
			"UVWXYZ_+^=".toCharArray()
	};

	public static final Point DEFAULT_CURSOR_LOCATION = new Point(0, 1);

	Point cursor = new Point(DEFAULT_CURSOR_LOCATION);
	boolean isCapsLockOn = true;
	String message = "";
	DPadStrategy strategy = DPadStrategy.NONE;

	boolean hadPressedShift = false;

	char[][] characters = copyArray(DEFAULT_PAD);
	char capsLockKey = '^';
	char shiftKey = '+';

	private Map<Character, Character> toLower = new Hashtable<>();
	private Map<Character, Character> toUpper = new Hashtable<>();

	public DPad() {
		addCaseChange('-', '_');
	}

	public DPad(char[][] characters) {
		this();
		this.characters = copyArray(characters);
	}

	public DPad(DPad dpad) {
		this.message = dpad.message;
		this.characters = copyArray(dpad.characters);
		this.cursor.setLocation(dpad.cursor);
		this.strategy = dpad.strategy;
		this.isCapsLockOn = dpad.isCapsLockOn;
		this.hadPressedShift = dpad.hadPressedShift;
		this.toLower = dpad.toLower;
		this.toUpper = dpad.toUpper;
	}

	private char[][] copyArray(char[][] array) {
		char[][] newArray = new char[array.length][];
		for (int i = 0; i < array.length; i++)
			newArray[i] = Arrays.copyOf(array[i], array[i].length);
		return newArray;
	}

	public void addCaseChange(Character lower, Character upper) {
		toLower.put(upper, lower);
		toUpper.put(lower, upper);
	}

	public Character getCaseChange(Character key) {
		return toLower.getOrDefault(key, toUpper.getOrDefault(key, null));
	}

	public void clearCaseChange(Character c) {
		toLower.remove(c);
		toUpper.remove(c);
	}

	public boolean isCaseChangeKey(Character key) {
		return getCaseChange(key) != null;
	}

	public boolean hasCaseChange(Character c) {
		return toLower.get(c) != null || toUpper.get(c) != null;
	}

	public void move(DPadAction direction) {
		switch (direction) {
		case MOVE_UP:
			if (cursor.y == 0) {
				cursor.y = characters.length - 1;
			} else {
				cursor.y = cursor.y - 1;
			}
			break;
		case MOVE_DOWN:
			if (cursor.y == characters.length - 1) {
				cursor.y = 0;
			} else {
				cursor.y = cursor.y + 1;
			}
			break;
		case MOVE_LEFT:
			if (cursor.x == 0) {
				cursor.x = characters[cursor.y].length - 1;
			} else {
				cursor.x = cursor.x - 1;
			}
			break;
		case MOVE_RIGHT:
			if (cursor.x == characters[cursor.y].length - 1) {
				cursor.x = 0;
			} else {
				cursor.x = cursor.x + 1;
			}
			break;
		default:
			throw new IllegalStateException("Unexpected action: " + direction);
		}
	}

	public void press() {
		char key = getCursorKey();

		if (key == capsLockKey) {
			toggleCase();
			strategy = DPadStrategy.NONE;
			return;
		}

		if (key == shiftKey) {
			toggleCase();
			hadPressedShift = true;
			strategy = DPadStrategy.NONE;
			return;
		}

		if (hadPressedShift) {
			toggleCase();
			strategy = DPadStrategy.NONE;
			hadPressedShift = false;
		}

		message += key;
	}

	void toggleCase() {

		Function<Character, Character> task = isCapsLockOn ? Character::toLowerCase
				: Character::toUpperCase;

		isCapsLockOn = !isCapsLockOn;

		for (int i = 0; i < characters.length; i++) {
			for (int j = 0; j < characters[i].length; j++) {

				char key = characters[i][j];

				Character replacement = null;
				if (!Character.isAlphabetic(key)) {
					replacement = getCaseChange(key);

					// When no replacement exists, use self
					if (replacement == null) {
						replacement = key;
					}
				} else {
					replacement = task.apply(characters[i][j]);
				}

				characters[i][j] = replacement;
			}
		}
	}

	public char getCursorKey() {
		return characters[cursor.y][cursor.x];
	}

	public Point getLocationOf(char key) {
		for (int row = 0; row < characters.length; row++) {
			for (int col = 0; col < characters[row].length; col++) {
				if (characters[row][col] == key) {
					return new Point(col, row);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the distance to the key, and -1 if the key is unknown.
	 * 
	 * @param key
	 * @return distance to key (-1 if unknown).
	 */
	public int getKeyDistance(char key) {
		Point keyLocation = getLocationOf(key);
		if (keyLocation == null) {
			// System.out.println("Could not find key: " + key);
			return -1;
		}

		int total = 0;

		int distanceGoingLeft = keyLocation.x <= cursor.x 
				? cursor.x - keyLocation.x 
				: cursor.x + characters[cursor.y].length - keyLocation.x;

		int distanceGoingRight = cursor.x <= keyLocation.x 
				? keyLocation.x - cursor.x 
				: keyLocation.x + characters[cursor.y].length - cursor.x;

		total += Math.min(distanceGoingLeft, distanceGoingRight);

		int distanceGoingUp = keyLocation.y <= cursor.y 
				? cursor.y - keyLocation.y 
				: cursor.y + characters.length - keyLocation.y;

		int distanceGoingDown = cursor.y <= keyLocation.y 
				? keyLocation.y - cursor.y 
				: keyLocation.y + characters.length - cursor.y;

		total += Math.min(distanceGoingDown, distanceGoingUp);

		return total;
	}

	public void printKeys() {
		System.out.println("(row, column)");
		for (int row = 0; row < characters.length; row++) {
			for (int column = 0; column < characters[row].length; column++) {
				System.out.print("(" + row + "," + column + ")=" + characters[row][column] + ", ");
			}
			System.out.println();
		}
	}

	public Point getCursor() {
		return cursor;
	}

	public void setCursor(Point cursor) {
		this.cursor = cursor;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String toString() {
		return "'" + message + "'@(" + cursor.x + "," + cursor.y + ")";
	}

	public int getPadHeight() {
		return characters.length;
	}

	public int getPadWidth() {
		return characters[0].length;
	}

	protected Character nextKey(String goalMessage) {

		if (goalMessage == null || goalMessage.length() == 0) {
			throw new IllegalArgumentException(
					"Goal message must not be null or empty");
		}

		Character nextKey = null;

		if (message.length() == 0) {
			nextKey = goalMessage.charAt(0);

		} else if (message.equals(goalMessage)) {
			nextKey = null;

		} else {
			int currentIndex = 0;
			boolean atLeastOneMatched = false;
			int limit = Math.min(message.length(), goalMessage.length());
			for (int i = 0; i < limit; i++) {
				if (message.charAt(i) != goalMessage.charAt(i)) {
					break;
				}
				currentIndex = i;
				atLeastOneMatched = true;
			}

			if (atLeastOneMatched) {
				nextKey = goalMessage.charAt(currentIndex + 1);
			} else {
				nextKey = null;
			}
		}

		if (strategy == DPadStrategy.CAPSLOCK) {
			nextKey = capsLockKey;
		} else if (strategy == DPadStrategy.SHIFT) {
			nextKey = shiftKey;
		}

		return nextKey;
	}

	public boolean isCapsLockOn() {
		return isCapsLockOn;
	}

	public void setCapsLockOn(boolean isCapsLockOn) {
		this.isCapsLockOn = isCapsLockOn;
	}

	public DPadStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(DPadStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cursor == null) ? 0 : cursor.hashCode());
		result = prime * result + (hadPressedShift ? 1231 : 1237);
		result = prime * result + (isCapsLockOn ? 1231 : 1237);
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DPad other = (DPad) obj;
		if (cursor == null) {
			if (other.cursor != null)
				return false;
		} else if (!cursor.equals(other.cursor))
			return false;
		if (hadPressedShift != other.hadPressedShift)
			return false;
		if (isCapsLockOn != other.isCapsLockOn)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

}
