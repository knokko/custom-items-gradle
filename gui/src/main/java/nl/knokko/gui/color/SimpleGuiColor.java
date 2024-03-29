package nl.knokko.gui.color;

public class SimpleGuiColor implements GuiColor, Comparable<SimpleGuiColor> {
	
	public static final SimpleGuiColor RED = new SimpleGuiColor(255, 0, 0);
	public static final SimpleGuiColor GREEN = new SimpleGuiColor(0, 255, 0);
	public static final SimpleGuiColor BLUE = new SimpleGuiColor(0, 0, 255);
	public static final SimpleGuiColor BLACK = new SimpleGuiColor(0, 0, 0);
	public static final SimpleGuiColor WHITE = new SimpleGuiColor(255, 255, 255);
	public static final SimpleGuiColor TRANSPARENT = new SimpleGuiColor(0, 0, 0, 0);
	
	private final float red;
	private final float green;
	private final float blue;
	private final float alpha;
	
	public SimpleGuiColor(int rgba){
		this(rgba >> 16 & 0xFF, rgba >> 8 & 0xFF, rgba >> 0 & 0xFF, rgba >> 24 & 0xFF);
	}
	
	public SimpleGuiColor(int red, int green, int blue){
		this(red, green, blue, 255);
	}
	
	public SimpleGuiColor(int red, int green, int blue, int alpha){
		this(red / 255f, green / 255f, blue / 255f, alpha / 255f);
	}
	
	public SimpleGuiColor(float red, float green, float blue){
		this(red, green, blue, 1f);
	}
	
	public SimpleGuiColor(float red, float green, float blue, float alpha){
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public float getRedF() {
		return red;
	}

	public float getGreenF() {
		return green;
	}

	public float getBlueF() {
		return blue;
	}

	public float getAlphaF() {
		return alpha;
	}
	
	@Override
	public String toString(){
		return "SimpleGuiColor(" + red + "," + green + "," + blue + "," + alpha + ")";
	}
	
	@Override
	public boolean equals(Object other){
		if(other instanceof SimpleGuiColor){
			SimpleGuiColor color = (SimpleGuiColor) other;
			return red == color.red && green == color.green && blue == color.blue && alpha == color.alpha;
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return (int) (10 * alpha + 100 * blue + 1000 * green + 10000 * red);
	}

	public int compareTo(SimpleGuiColor o) {
		if(red > o.red)
			return 1;
		if(red < o.red)
			return -1;
		if(green > o.green)
			return 1;
		if(green < o.green)
			return -1;
		if(blue > o.blue)
			return 1;
		if(blue < o.blue)
			return -1;
		if(alpha > o.alpha)
			return 1;
		if(alpha < o.alpha)
			return -1;
		return 0;
	}
}