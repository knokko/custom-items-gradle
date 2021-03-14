package nl.knokko.customitems.plugin.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nl.knokko.customitems.container.CustomContainer;
import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.fuel.CustomFuelRegistry;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.SlotDisplay;

/**
 * This class captures information about a given custom container that is useful for
 * managing the inventories based on the container.
 */
public class ContainerInfo {

	private final CustomContainer container;
	
	private final Map<String, PlaceholderProps> inputSlots;
	private final Map<String, PlaceholderProps> outputSlots;
	private final Map<String, FuelProps> fuelSlots;
	private final Collection<PlaceholderProps> storageSlots;
	
	private final Collection<IndicatorProps> craftingIndicators;
	
	private final Collection<DecorationProps> decorations;
	
	public ContainerInfo(CustomContainer container) {
		this.container = container;
		
		this.inputSlots = new HashMap<>();
		this.outputSlots = new HashMap<>();
		this.fuelSlots = new HashMap<>();
		this.storageSlots = new ArrayList<>();
		this.craftingIndicators = new ArrayList<>();
		this.decorations = new ArrayList<>();
		
		// This is only temporarily
		Map<String, Collection<IndicatorProps>> fuelIndicators = new HashMap<>();
		
		int invIndex = 0;
		for (int y = 0; y < container.getHeight(); y++) {
			for (int x = 0; x < 9; x++) {
				
				CustomSlot slot = container.getSlot(x, y);
				if (slot instanceof FuelCustomSlot) {
					FuelCustomSlot fuelSlot = (FuelCustomSlot) slot;
					fuelSlots.put(fuelSlot.getName(), new FuelProps(invIndex, fuelSlot));
					if (!fuelIndicators.containsKey(fuelSlot.getName())) {
						fuelIndicators.put(fuelSlot.getName(), new ArrayList<>());
					}
				} else if (slot instanceof FuelIndicatorCustomSlot) {
					FuelIndicatorCustomSlot indicatorSlot = (FuelIndicatorCustomSlot) slot;
					Collection<IndicatorProps> indicators = fuelIndicators.get(indicatorSlot.getFuelSlotName());
					if (indicators == null) {
						indicators = new ArrayList<>(1);
						fuelIndicators.put(indicatorSlot.getFuelSlotName(), indicators);
					}
					indicators.add(new IndicatorProps(invIndex, 
							indicatorSlot.getDisplay(), indicatorSlot.getPlaceholder(), 
							indicatorSlot.getDomain()
					));
				} else if (slot instanceof InputCustomSlot) {
					
					InputCustomSlot inputSlot = (InputCustomSlot) slot;
					inputSlots.put(
							inputSlot.getName(), 
							new PlaceholderProps(invIndex, inputSlot.getPlaceholder())
					);
				} else if (slot instanceof OutputCustomSlot) {
					
					OutputCustomSlot outputSlot = (OutputCustomSlot) slot;
					outputSlots.put(
							outputSlot.getName(), 
							new PlaceholderProps(invIndex, outputSlot.getPlaceholder())
					);
				} else if (slot instanceof ProgressIndicatorCustomSlot) {
					
					ProgressIndicatorCustomSlot indicatorSlot = (ProgressIndicatorCustomSlot) slot;
					craftingIndicators.add(new IndicatorProps(invIndex, 
							indicatorSlot.getDisplay(), indicatorSlot.getPlaceHolder(),
							indicatorSlot.getDomain()
					));
				} else if (slot instanceof DecorationCustomSlot) {
					decorations.add(new DecorationProps(invIndex, ((DecorationCustomSlot) slot).getDisplay()));
				} else if (slot instanceof StorageCustomSlot) {
					StorageCustomSlot storageSlot = (StorageCustomSlot) slot;
					storageSlots.add(new PlaceholderProps(invIndex, storageSlot.getPlaceHolder()));
				}
				invIndex++;
			}
		}
		
		// Link the fuel indicators to the fuel slots
		fuelSlots.forEach((fuelSlotName, fuelSlotProps) -> {
			fuelSlotProps.getIndicators().addAll(fuelIndicators.get(fuelSlotName));
		});
	}
	
	public CustomContainer getContainer() {
		return container;
	}
	
	public PlaceholderProps getInputSlot(String slotName) {
		return inputSlots.get(slotName);
	}
	
	public PlaceholderProps getOutputSlot(String slotName) {
		return outputSlots.get(slotName);
	}
	
	public Iterable<IndicatorProps> getCraftingIndicators() {
		return craftingIndicators;
	}
	
	public FuelProps getFuelSlot(String fuelSlotName) {
		return fuelSlots.get(fuelSlotName);
	}
	
	public Iterable<DecorationProps> getDecorations() {
		return decorations;
	}
	
	public Iterable<Entry<String, PlaceholderProps>> getInputSlots() {
		return inputSlots.entrySet();
	}
	
	public Iterable<Entry<String, PlaceholderProps>> getOutputSlots() {
		return outputSlots.entrySet();
	}
	
	public Iterable<Entry<String, FuelProps>> getFuelSlots() {
		return fuelSlots.entrySet();
	}

	public Iterable<PlaceholderProps> getStorageSlots() {
		return storageSlots;
	}
	
	public static class IndicatorProps {
		
		private final int invIndex;
		
		private final SlotDisplay display;
		private final SlotDisplay placeholder;
		private final IndicatorDomain domain;
		
		private IndicatorProps(int invIndex, SlotDisplay display, 
				SlotDisplay placeholder, IndicatorDomain domain) {
			this.invIndex = invIndex;
			this.display = display;
			this.placeholder = placeholder;
			this.domain = domain;
		}
		
		public int getInventoryIndex() {
			return invIndex;
		}
		
		public SlotDisplay getSlotDisplay() {
			return display;
		}
		
		public SlotDisplay getPlaceholder() {
			return placeholder;
		}
		
		public IndicatorDomain getIndicatorDomain() {
			return domain;
		}
	}
	
	public static class DecorationProps {
		
		private final int invIndex;
		
		private final SlotDisplay display;
		
		private DecorationProps(int invIndex, SlotDisplay display) {
			this.invIndex = invIndex;
			this.display = display;
		}
		
		public int getInventoryIndex() {
			return invIndex;
		}
		
		public SlotDisplay getSlotDisplay() {
			return display;
		}
	}
	
	public static class FuelProps {
		
		private final int slotIndex;
		private final Collection<IndicatorProps> indicators;
		private final CustomFuelRegistry registry;
		private final SlotDisplay placeholder;
		
		private FuelProps(
				int slotIndex,
				Collection<IndicatorProps> indicators, 
				CustomFuelRegistry registry, 
				SlotDisplay placeholder) {
			this.slotIndex = slotIndex;
			this.indicators = indicators;
			this.registry = registry;
			this.placeholder = placeholder;
		}
		
		private FuelProps(int slotIndex, FuelCustomSlot slot) {
			this(slotIndex, new ArrayList<>(), slot.getRegistry(), slot.getPlaceholder());
		}
		
		public int getSlotIndex() {
			return slotIndex;
		}
		
		public Collection<IndicatorProps> getIndicators() {
			return indicators;
		}
		
		public CustomFuelRegistry getRegistry() {
			return registry;
		}
		
		public SlotDisplay getPlaceholder() {
			return placeholder;
		}
	}
	
	public static class PlaceholderProps {
		
		private final int slotIndex;
		private final SlotDisplay placeholder;
		
		public PlaceholderProps(int slotIndex, SlotDisplay placeholder) {
			this.slotIndex = slotIndex;
			this.placeholder = placeholder;
		}
		
		public int getSlotIndex() {
			return slotIndex;
		}
		
		public SlotDisplay getPlaceholder() {
			return placeholder;
		}
	}
}
