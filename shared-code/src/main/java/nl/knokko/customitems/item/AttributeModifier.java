/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2019 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.customitems.item;

import java.util.Locale;

import nl.knokko.customitems.NameHelper;

public class AttributeModifier {
	
	private final Attribute attribute;
	private final Slot slot;
	private final Operation operation;
	private final double value;
	
	public AttributeModifier(Attribute attribute, Slot slot, Operation operation, double value) {
		this.attribute = attribute;
		this.slot = slot;
		this.operation = operation;
		this.value = value;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof AttributeModifier) {
			AttributeModifier am = (AttributeModifier) other;
			return am.attribute == attribute && am.operation == operation && am.slot == slot && am.value == value;
		} else {
			return false;
		}
	}
	
	public Attribute getAttribute() {
		return attribute;
	}
	
	public Slot getSlot() {
		return slot;
	}
	
	public Operation getOperation() {
		return operation;
	}
	
	public double getValue() {
		return value;
	}
	
	public static enum Attribute {
		
		MAX_HEALTH("generic.maxHealth"),
		KNOCKBACK_RESISTANCE("generic.knockbackResistance"),
		MOVEMENT_SPEED("generic.movementSpeed"),
		ATTACK_DAMAGE("generic.attackDamage"),
		ARMOR("generic.armor"),
		ARMOR_TOUGHNESS("generic.armorToughness"),
		ATTACK_SPEED("generic.attackSpeed"),
		LUCK("generic.luck");
		
		private final String attributeName;
		
		private Attribute(String name) {
			attributeName = name;
		}
		
		@Override
		public String toString() {
			return getName();
		}
		
		public String getName() {
			return attributeName;
		}
	}
	
	public static enum Slot {
		
		FEET,
		LEGS,
		CHEST,
		HEAD,
		MAINHAND,
		OFFHAND;
		
		@Override
		public String toString() {
			return NameHelper.getNiceEnumName(name());
		}
		
		public String getSlot() {
			return name().toLowerCase(Locale.ROOT);
		}
	}
	
	public static enum Operation {
		
		ADD,
		ADD_FACTOR,
		MULTIPLY;
		
		public int getOperation() {
			return ordinal();
		}
		
		@Override
		public String toString() {
			return NameHelper.getNiceEnumName(name());
		}
	}
}