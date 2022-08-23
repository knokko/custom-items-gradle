package nl.knokko.customitems.plugin.container;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nl.knokko.customitems.container.CustomContainerValues;
import nl.knokko.customitems.container.IndicatorDomain;
import nl.knokko.customitems.container.fuel.FuelRegistryValues;
import nl.knokko.customitems.container.slot.*;
import nl.knokko.customitems.container.slot.display.SlotDisplayValues;
import nl.knokko.customitems.itemset.EnergyTypeReference;

/**
 * This class captures information about a given custom container that is useful for
 * managing the inventories based on the container.
 */
public class ContainerInfo {

	private final CustomContainerValues container;
	
	private final Map<String, PlaceholderProps> inputSlots;
	private final Map<String, PlaceholderProps> outputSlots;
	private final Map<String, PlaceholderProps> manualOutputSlots;
	private final Map<String, FuelProps> fuelSlots;
	private final Collection<PlaceholderProps> storageSlots;
	
	private final Collection<IndicatorProps> craftingIndicators;
	private final Collection<EnergyIndicatorProps> energyIndicators;
	
	private final Collection<DecorationProps> decorations;
	
	public ContainerInfo(CustomContainerValues container) {
		this.container = container;
		
		this.inputSlots = new HashMap<>();
		this.outputSlots = new HashMap<>();
		this.manualOutputSlots = new HashMap<>();
		this.fuelSlots = new HashMap<>();
		this.storageSlots = new ArrayList<>();
		this.craftingIndicators = new ArrayList<>();
		this.energyIndicators = new ArrayList<>();
		this.decorations = new ArrayList<>();
		
		// This is only temporarily
		Map<String, Collection<IndicatorProps>> fuelIndicators = new HashMap<>();
		
		int invIndex = 0;
		for (int y = 0; y < container.getHeight(); y++) {
			for (int x = 0; x < 9; x++) {
				
				ContainerSlotValues slot = container.getSlot(x, y);
				if (slot instanceof FuelSlotValues) {
					FuelSlotValues fuelSlot = (FuelSlotValues) slot;
					fuelSlots.put(fuelSlot.getName(), new FuelProps(invIndex, fuelSlot));
					if (!fuelIndicators.containsKey(fuelSlot.getName())) {
						fuelIndicators.put(fuelSlot.getName(), new ArrayList<>());
					}
				} else if (slot instanceof FuelIndicatorSlotValues) {
					FuelIndicatorSlotValues indicatorSlot = (FuelIndicatorSlotValues) slot;
					Collection<IndicatorProps> indicators = fuelIndicators.computeIfAbsent(
							indicatorSlot.getFuelSlotName(), k -> new ArrayList<>(1)
					);
					indicators.add(new IndicatorProps(invIndex,
							indicatorSlot.getDisplay(), indicatorSlot.getPlaceholder(), 
							indicatorSlot.getIndicatorDomain()
					));
				} else if (slot instanceof InputSlotValues) {
					
					InputSlotValues inputSlot = (InputSlotValues) slot;
					inputSlots.put(
							inputSlot.getName(), 
							new PlaceholderProps(invIndex, inputSlot.getPlaceholder())
					);
				} else if (slot instanceof OutputSlotValues) {
					
					OutputSlotValues outputSlot = (OutputSlotValues) slot;
					outputSlots.put(
							outputSlot.getName(), 
							new PlaceholderProps(invIndex, outputSlot.getPlaceholder())
					);
				} else if (slot instanceof ManualOutputSlotValues) {
					ManualOutputSlotValues outputSlot = (ManualOutputSlotValues) slot;
					manualOutputSlots.put(
							outputSlot.getName(),
							new PlaceholderProps(invIndex, outputSlot.getPlaceholder())
					);
				} else if (slot instanceof ProgressIndicatorSlotValues) {
					
					ProgressIndicatorSlotValues indicatorSlot = (ProgressIndicatorSlotValues) slot;
					craftingIndicators.add(new IndicatorProps(invIndex, 
							indicatorSlot.getDisplay(), indicatorSlot.getPlaceholder(),
							indicatorSlot.getIndicatorDomain()
					));
				} else if (slot instanceof DecorationSlotValues) {
					decorations.add(new DecorationProps(invIndex, ((DecorationSlotValues) slot).getDisplay()));
				} else if (slot instanceof StorageSlotValues) {
					StorageSlotValues storageSlot = (StorageSlotValues) slot;
					storageSlots.add(new PlaceholderProps(invIndex, storageSlot.getPlaceholder()));
				} else if (slot instanceof EnergyIndicatorSlotValues) {
					EnergyIndicatorSlotValues energySlot = (EnergyIndicatorSlotValues) slot;
					energyIndicators.add(new EnergyIndicatorProps(
							invIndex, energySlot.getEnergyTypeReference(),
							energySlot.getDisplay(), energySlot.getPlaceholder(), energySlot.getIndicatorDomain()
					));
				}
				invIndex++;
			}
		}
		
		// Link the fuel indicators to the fuel slots
		fuelSlots.forEach((fuelSlotName, fuelSlotProps) -> {
			fuelSlotProps.getIndicators().addAll(fuelIndicators.get(fuelSlotName));
		});
	}
	
	public CustomContainerValues getContainer() {
		return container;
	}
	
	public PlaceholderProps getInputSlot(String slotName) {
		return inputSlots.get(slotName);
	}
	
	public PlaceholderProps getOutputSlot(String slotName) {
		return outputSlots.get(slotName);
	}

	public PlaceholderProps getManualOutputSlot(String slotName) {
		return manualOutputSlots.get(slotName);
	}
	
	public Iterable<IndicatorProps> getCraftingIndicators() {
		return craftingIndicators;
	}

	public Iterable<EnergyIndicatorProps> getEnergyIndicators() {
		return energyIndicators;
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

	public Iterable<Entry<String, PlaceholderProps>> getManualOutputSlots() {
		return manualOutputSlots.entrySet();
	}
	
	public Iterable<Entry<String, FuelProps>> getFuelSlots() {
		return fuelSlots.entrySet();
	}

	public Iterable<PlaceholderProps> getStorageSlots() {
		return storageSlots;
	}
	
	public static class IndicatorProps {
		
		private final int invIndex;
		
		private final SlotDisplayValues display;
		private final SlotDisplayValues placeholder;
		private final IndicatorDomain domain;
		
		private IndicatorProps(int invIndex, SlotDisplayValues display,
				SlotDisplayValues placeholder, IndicatorDomain domain) {
			this.invIndex = invIndex;
			this.display = display;
			this.placeholder = placeholder;
			this.domain = domain;
		}
		
		public int getInventoryIndex() {
			return invIndex;
		}
		
		public SlotDisplayValues getSlotDisplay() {
			return display;
		}
		
		public SlotDisplayValues getPlaceholder() {
			return placeholder;
		}
		
		public IndicatorDomain getIndicatorDomain() {
			return domain;
		}
	}

	public static class EnergyIndicatorProps extends IndicatorProps {

		private final EnergyTypeReference energyType;

		private EnergyIndicatorProps(
				int invIndex, EnergyTypeReference energyType,
				SlotDisplayValues display, SlotDisplayValues placeholder, IndicatorDomain domain
		) {
			super(invIndex, display, placeholder, domain);
			this.energyType = energyType;
		}

		public EnergyTypeReference getEnergyType() {
			return energyType;
		}
	}
	
	public static class DecorationProps {
		
		private final int invIndex;
		
		private final SlotDisplayValues display;
		
		private DecorationProps(int invIndex, SlotDisplayValues display) {
			this.invIndex = invIndex;
			this.display = display;
		}
		
		public int getInventoryIndex() {
			return invIndex;
		}
		
		public SlotDisplayValues getSlotDisplay() {
			return display;
		}
	}
	
	public static class FuelProps {
		
		private final int slotIndex;
		private final Collection<IndicatorProps> indicators;
		private final FuelRegistryValues registry;
		private final SlotDisplayValues placeholder;
		
		private FuelProps(
				int slotIndex,
				Collection<IndicatorProps> indicators, 
				FuelRegistryValues registry,
				SlotDisplayValues placeholder) {
			this.slotIndex = slotIndex;
			this.indicators = indicators;
			this.registry = registry;
			this.placeholder = placeholder;
		}
		
		private FuelProps(int slotIndex, FuelSlotValues slot) {
			this(slotIndex, new ArrayList<>(), slot.getFuelRegistry(), slot.getPlaceholder());
		}
		
		public int getSlotIndex() {
			return slotIndex;
		}
		
		public Collection<IndicatorProps> getIndicators() {
			return indicators;
		}
		
		public FuelRegistryValues getRegistry() {
			return registry;
		}
		
		public SlotDisplayValues getPlaceholder() {
			return placeholder;
		}
	}
	
	public static class PlaceholderProps {
		
		private final int slotIndex;
		private final SlotDisplayValues placeholder;
		
		public PlaceholderProps(int slotIndex, SlotDisplayValues placeholder) {
			this.slotIndex = slotIndex;
			this.placeholder = placeholder;
		}
		
		public int getSlotIndex() {
			return slotIndex;
		}
		
		public SlotDisplayValues getPlaceholder() {
			return placeholder;
		}
	}
}
