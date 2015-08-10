package dpader;

import static org.junit.Assert.assertEquals;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import dpader.DPad;
import dpader.DPadAction;
import dpader.DPadStrategy;


public class DPadTest {
	
	DPad standardPad = null;
	
	@Before
	public void setUp() {
		
		char[][] characters = new char[][] {
			"0123456789".toCharArray(),
			"abcdefghij".toCharArray(),
			"klmnopqrst".toCharArray(),
			"uvwxyz-+^=".toCharArray()
		};
		standardPad = new DPad(characters);
	}
	

	@Test
	public void testPress() throws Exception {
		standardPad.setCursor(new Point(2,1));
		standardPad.press();
		standardPad.setCursor(new Point(0,1));
		standardPad.press();
		standardPad.setCursor(new Point(9,2));
		standardPad.press();
		
		assertEquals("cat", standardPad.getMessage());
	}	
	
	@Test
	public void testGetCursorKey() {
		standardPad.setCursor(new Point(0,0));
		assertEquals((Character)'0', (Character)standardPad.getCursorKey());
				
		standardPad.setCursor(new Point(0,1));
		assertEquals((Character)'a', (Character)standardPad.getCursorKey());
		
		standardPad.setCursor(new Point(1,0));
		assertEquals((Character)'1', (Character)standardPad.getCursorKey());
	}
	
	@Test
	public void testGetLocationOf() throws Exception {
		assertEquals(new Point(0,0), standardPad.getLocationOf('0'));
		assertEquals(new Point(0,1), standardPad.getLocationOf('a'));
		assertEquals(new Point(1,0), standardPad.getLocationOf('1'));
		assertEquals(null, standardPad.getLocationOf('!'));
	}

	@Test
	public void testMove() throws Exception {
		DPad next = null;
		
		standardPad.setCursor(new Point(0,0));
		next = new DPad(standardPad);
		next.move(DPadAction.MOVE_UP);
		assertEquals((Character) 'u', (Character) next.getCursorKey());
		next.move(DPadAction.MOVE_UP);
		assertEquals((Character) 'k', (Character) next.getCursorKey());
		next.move(DPadAction.MOVE_UP);
		assertEquals((Character) 'a', (Character) next.getCursorKey());
		next.move(DPadAction.MOVE_UP);
		assertEquals((Character) '0', (Character) next.getCursorKey());
		
		standardPad.setCursor(new Point(0,0));
		next = new DPad(standardPad);
		next.move(DPadAction.MOVE_DOWN);
		assertEquals((Character) 'a', (Character) next.getCursorKey());
		next.move(DPadAction.MOVE_DOWN);
		assertEquals((Character) 'k', (Character) next.getCursorKey());
		next.move(DPadAction.MOVE_DOWN);
		assertEquals((Character) 'u', (Character) next.getCursorKey());
		next.move(DPadAction.MOVE_DOWN);
		assertEquals((Character) '0', (Character) next.getCursorKey());
		
		standardPad.setCursor(new Point(0,0));
		next = new DPad(standardPad);
		next.move(DPadAction.MOVE_LEFT);
		assertEquals((Character) '9', (Character) next.getCursorKey());
		next.move(DPadAction.MOVE_LEFT);
		assertEquals((Character) '8', (Character) next.getCursorKey());		
		
		standardPad.setCursor(new Point(9,0));
		next = new DPad(standardPad);
		next.move(DPadAction.MOVE_RIGHT);
		assertEquals((Character) '0', (Character) next.getCursorKey());
		next.move(DPadAction.MOVE_RIGHT);
		assertEquals((Character) '1', (Character) next.getCursorKey());
	}

	@Test
	public void testGetKeyDistanceVertical() throws Exception {
		
		char[][] longCharacters = {
				{'a'},
				{'b'},
				{'c'},
				{'d'},
				{'e'},
				{'f'},
				{'g'}
		};
		DPad longPad = new DPad(longCharacters);
		
		longPad.setCursor(new Point(0,2));
		assertEquals(2, longPad.getKeyDistance('a'), 0);
		assertEquals(1, longPad.getKeyDistance('b'), 0);
		assertEquals(0, longPad.getKeyDistance('c'), 0);
		assertEquals(1, longPad.getKeyDistance('d'), 0);
		assertEquals(2, longPad.getKeyDistance('e'), 0);
		assertEquals(3, longPad.getKeyDistance('f'), 0);
		assertEquals(3, longPad.getKeyDistance('g'), 0);
		
		assertEquals(-1, longPad.getKeyDistance('!'), 0);
	}	
	
	
	@Test
	public void testGetKeyDistanceHorizontal() throws Exception {
		// 
		char[][] wideCharacters = {
				{'a','b','c','d','e','f','g'}
		};
		DPad widePad = new DPad(wideCharacters);
		
		widePad.setCursor(new Point(2,0));
		assertEquals(2, widePad.getKeyDistance('a'), 0);
		assertEquals(1, widePad.getKeyDistance('b'), 0);
		assertEquals(0, widePad.getKeyDistance('c'), 0);
		assertEquals(1, widePad.getKeyDistance('d'), 0);
		assertEquals(2, widePad.getKeyDistance('e'), 0);
		assertEquals(3, widePad.getKeyDistance('f'), 0);
		assertEquals(3, widePad.getKeyDistance('g'), 0);
		
		assertEquals(-1, widePad.getKeyDistance('!'), 0);
	}	
	
	@Test
	public void testGetKeyDistanceBoth() throws Exception {
		
		char[][] longCharacters = {
				{'a','b','c','d','e','f','g'},
				{'h','i','j','k','l','m','n'},
				{'o','p','q','r','s','t','u'},
				{'v','w','x','y','z','0','1'},
				{'2','3','4','5','6','7','8'},
				{'9','.','@','#','$','%','^'},
				{'*','(',')','-','_','+','='}
		};
		DPad fatPad = new DPad(longCharacters);
		
		fatPad.setCursor(new Point(2,2));
		assertEquals(2, fatPad.getKeyDistance('s'), 0);
		assertEquals(1, fatPad.getKeyDistance('r'), 0);
		assertEquals(0, fatPad.getKeyDistance('q'), 0);
		assertEquals(1, fatPad.getKeyDistance('p'), 0);
		assertEquals(2, fatPad.getKeyDistance('o'), 0);
		assertEquals(3, fatPad.getKeyDistance('u'), 0);
		assertEquals(3, fatPad.getKeyDistance('t'), 0);
		
		assertEquals(2, fatPad.getKeyDistance('c'), 0);
		assertEquals(1, fatPad.getKeyDistance('j'), 0);
		assertEquals(0, fatPad.getKeyDistance('q'), 0);
		assertEquals(1, fatPad.getKeyDistance('x'), 0);
		assertEquals(2, fatPad.getKeyDistance('4'), 0);
		assertEquals(3, fatPad.getKeyDistance('@'), 0);
		assertEquals(3, fatPad.getKeyDistance(')'), 0);
		
		assertEquals(4, fatPad.getKeyDistance('a'), 0);
		assertEquals(2, fatPad.getKeyDistance('i'), 0);
		assertEquals(0, fatPad.getKeyDistance('q'), 0);
		assertEquals(2, fatPad.getKeyDistance('y'), 0);
		assertEquals(4, fatPad.getKeyDistance('6'), 0);
		assertEquals(6, fatPad.getKeyDistance('%'), 0);
		assertEquals(6, fatPad.getKeyDistance('='), 0);

		assertEquals(-1, fatPad.getKeyDistance('!'), 0);
		
	}


	@Test
	public void testNextKey() throws Exception {
		
		String goalMessage = "c";
		standardPad.setMessage("");
		assertEquals((Character) 'c', standardPad.nextKey(goalMessage));
		
		goalMessage = "c";
		standardPad.setMessage("c");
		assertEquals((Character) null, standardPad.nextKey(goalMessage));

		goalMessage = "cat";
		standardPad.setMessage("ca");
		assertEquals((Character) 't', standardPad.nextKey(goalMessage));
		
		goalMessage = "cat";
		standardPad.setMessage("cat");
		assertEquals((Character) null, standardPad.nextKey(goalMessage));

		goalMessage = "c";
		standardPad.setMessage("d");
		assertEquals((Character) null, standardPad.nextKey(goalMessage));
		
		goalMessage = "c";
		standardPad.setMessage("dd");
		assertEquals((Character) null, standardPad.nextKey(goalMessage));
		
		goalMessage = "C";
		standardPad.setMessage("");
		standardPad.setCapsLockOn(false);
		standardPad.setStrategy(DPadStrategy.SHIFT);
		assertEquals((Character) '+', standardPad.nextKey(goalMessage));
		
		goalMessage = "c";
		standardPad.setMessage("");
		standardPad.setCapsLockOn(true);
		standardPad.setStrategy(DPadStrategy.SHIFT);
		assertEquals((Character) '+', standardPad.nextKey(goalMessage));
		
		goalMessage = "C";
		standardPad.setMessage("");
		standardPad.setCapsLockOn(false);
		standardPad.setStrategy(DPadStrategy.CAPSLOCK);
		assertEquals((Character) '^', standardPad.nextKey(goalMessage));
		
		goalMessage = "c";
		standardPad.setMessage("");
		standardPad.setCapsLockOn(true);
		standardPad.setStrategy(DPadStrategy.CAPSLOCK);
		assertEquals((Character) '^', standardPad.nextKey(goalMessage));

	}


	@Test
	public void testToggleCase() throws Exception {
		char[][] characters = {
				{'a','b','c','d','e','f','g', '-', '$'}
		};
		DPad dpad = new DPad(characters);
		dpad.setCapsLockOn(false);
		
		dpad.toggleCase();
		
		assertEquals((Character) 'A', (Character) dpad.characters[0][0]);
		assertEquals((Character) 'B', (Character) dpad.characters[0][1]);
		assertEquals((Character) 'C', (Character) dpad.characters[0][2]);
		assertEquals((Character) 'D', (Character) dpad.characters[0][3]);
		assertEquals((Character) 'E', (Character) dpad.characters[0][4]);
		assertEquals((Character) 'F', (Character) dpad.characters[0][5]);
		assertEquals((Character) 'G', (Character) dpad.characters[0][6]);
		// - 'cases to' _
		assertEquals((Character) '_', (Character) dpad.characters[0][7]);
		// $ is unknown, so 'cases to' itself
		assertEquals((Character) '$', (Character) dpad.characters[0][8]);
		
		dpad.toggleCase();
		assertEquals((Character) 'a', (Character) dpad.characters[0][0]);
		assertEquals((Character) 'b', (Character) dpad.characters[0][1]);
		assertEquals((Character) 'c', (Character) dpad.characters[0][2]);
		assertEquals((Character) 'd', (Character) dpad.characters[0][3]);
		assertEquals((Character) 'e', (Character) dpad.characters[0][4]);
		assertEquals((Character) 'f', (Character) dpad.characters[0][5]);
		assertEquals((Character) 'g', (Character) dpad.characters[0][6]);
		assertEquals((Character) '-', (Character) dpad.characters[0][7]);
		assertEquals((Character) '$', (Character) dpad.characters[0][8]);
		
	}
}
