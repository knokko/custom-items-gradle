package nl.knokko.gui.window.input;

public class CharacterFilter {
	
	private static final char[] BLACKLIST = {
		0,//undefined character
		8//backspace character
	};
	
	public static boolean approve(char c){
		for(char black : BLACKLIST)
			if(black == c)
				return false;
		return true;
	}
}