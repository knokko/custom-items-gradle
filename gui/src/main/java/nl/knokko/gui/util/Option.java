package nl.knokko.gui.util;

public class Option {
	
	public static class Long {
		
		public static final Long NONE = new Long();
		
		private final long value;
		
		private final boolean hasValue;
		
		public Long(long value) {
			this.value = value;
			this.hasValue = true;
		}
		
		private Long() {
			this.hasValue = false;
			
			// I will have to assign something either way
			this.value = 0;
		}
		
		public boolean hasValue() {
			return hasValue;
		}
		
		public long getValue() throws IllegalStateException {
			if (hasValue) {
				return value;
			} else {
				throw new IllegalStateException("This long option has no value");
			}
		}
		
		public Short toShort() {
			if (hasValue && value >= java.lang.Short.MIN_VALUE && value <= java.lang.Short.MAX_VALUE) {
				return new Short((short) value);
			} else {
				return Short.NONE;
			}
		}
		
		public Int toInt() {
			if (hasValue && value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
				return new Int((int) value);
			} else {
				return Int.NONE;
			}
		}
	}
	
	public static class Int {
		
		public static final Int NONE = new Int();
		
		private final int value;
		
		private final boolean hasValue;
		
		public Int(int value) {
			this.value = value;
			this.hasValue = true;
		}
		
		private Int() {
			this.hasValue = false;
			
			// I will have to assign something either way
			this.value = 0;
		}
		
		public boolean hasValue() {
			return hasValue;
		}
		
		public int getValue() throws IllegalStateException {
			if (hasValue) {
				return value;
			} else {
				throw new IllegalStateException("This int option has no value");
			}
		}
		
		public Short toShort() {
			if (hasValue && value >= java.lang.Short.MIN_VALUE && value <= java.lang.Short.MAX_VALUE) {
				return new Short((short) value);
			} else {
				return Short.NONE;
			}
		}
		
		public Long toLong() {
			if (hasValue) {
				return new Long(value);
			} else {
				return Long.NONE;
			}
		}
	}
	
	public static class Short {
		
		public static final Short NONE = new Short();
		
		private final short value;
		
		private final boolean hasValue;
		
		public Short(short value) {
			this.value = value;
			this.hasValue = true;
		}
		
		private Short() {
			this.hasValue = false;
			
			// I will have to assign something either way
			this.value = 0;
		}
		
		public boolean hasValue() {
			return hasValue;
		}
		
		public short getValue() throws IllegalStateException {
			if (hasValue) {
				return value;
			} else {
				throw new IllegalStateException("This int option has no value");
			}
		}
		
		public Int toInt() {
			if (hasValue) {
				return new Int(value);
			} else {
				return Int.NONE;
			}
		}
		
		public Long toLong() {
			if (hasValue) {
				return new Long(value);
			} else {
				return Long.NONE;
			}
		}
	}
	
	public static class Double {
		
		public static final Double NONE = new Double();
		
		private final double value;
		
		private final boolean hasValue;
		
		public Double(double value) {
			this.value = value;
			this.hasValue = true;
		}
		
		private Double() {
			this.hasValue = false;
			
			// I will have to assign something either way
			this.value = 0;
		}
		
		public boolean hasValue() {
			return hasValue;
		}
		
		public double getValue() throws IllegalStateException {
			if (hasValue) {
				return value;
			} else {
				throw new IllegalStateException("This double option has no value");
			}
		}
		
		public Float toFloat() {
			if (hasValue) {
				return new Float((float) value);
			} else {
				return Float.NONE;
			}
		}
	}
	
	public static class Float {
		
		public static final Float NONE = new Float();
		
		private final float value;
		
		private final boolean hasValue;
		
		public Float(float value) {
			this.value = value;
			this.hasValue = true;
		}
		
		private Float() {
			this.hasValue = false;
			
			// I will have to assign something either way
			this.value = 0;
		}
		
		public boolean hasValue() {
			return hasValue;
		}
		
		public float getValue() throws IllegalStateException {
			if (hasValue) {
				return value;
			} else {
				throw new IllegalStateException("This float option has no value");
			}
		}
		
		public Double toDouble() {
			if (hasValue) {
				return new Double(value);
			} else {
				return Double.NONE;
			}
		}
	}
}