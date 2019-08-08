package com.smartgov.lez.core.agent.driver.vehicle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.smartgov.lez.core.copert.Copert;
import com.smartgov.lez.core.copert.fields.CopertField;
import com.smartgov.lez.core.copert.fields.EuroNorm;
import com.smartgov.lez.core.copert.fields.Fuel;
import com.smartgov.lez.core.copert.fields.Technology;
import com.smartgov.lez.core.copert.fields.VehicleCategory;
import com.smartgov.lez.core.copert.fields.VehicleSegment;
import com.smartgov.lez.core.copert.inputParser.CopertProfile;
import com.smartgov.lez.core.copert.inputParser.CopertRate;
import com.smartgov.lez.core.copert.tableParser.CopertHeader;
import com.smartgov.lez.core.copert.tableParser.CopertParser;
import com.smartgov.lez.core.copert.tableParser.CopertSelector;
import com.smartgov.lez.core.copert.tableParser.CopertTree;

/**
 * A delivery vehicle factory, used to build vehicle sets with a given size
 * according to input copert parameters.
 */
public class DeliveryVehicleFactory {

	private int index = 0;
	private CopertProfile copertProfile;
	private CopertParser copertParser;
	
	/**
	 * DeliveryVehicleFactory constructor.
	 *
	 * @param copertProfile a loaded copert profile, that contains information about
	 * vehicle proportions to generate
	 * @param copertParser a loaded copert parser, from which vehicles and associated
	 * pollution parameters will be selected
	 */
	public DeliveryVehicleFactory(CopertProfile copertProfile, CopertParser copertParser) {
		this.copertProfile = copertProfile;
		this.copertParser = copertParser;
	}
	
	/**
	 * Creates a list of vehicles, distributed according to the input copert profile
	 * and copert parser.
	 *
	 * <p>
	 * Notice that the generation method is determinist : if the copert profiles specifies
	 * 50/50 proportions for DIESEL and PETROL, it doesn't mean that for each vehicle in
	 * the generated set, we have 50% chance that the fuel is DIESEL and PETROL, but that
	 * the generated set <b>contains exactly 5 DIESEL and 5 PETROL vehicles</b> if 10 vehicles
	 * are created. 
	 * </p>
	 * <p>
	 * If this last case, if we want to create 9 vehicles, the algorithm ensures that we will
	 * obtain 5 DIESEL / 4 PETROL <b>OR</b> 4 DIESEL / 5 PETROL, but which case between those
	 * two will occur is not guaranteed.
	 * </p>
	 *
	 * @param vehicleCount number of vehicle to generate
	 * @return generated vehicles
	 */
	public List<DeliveryVehicle> create(int vehicleCount) {
		List<DeliveryVehicle> vehicles = new ArrayList<>();
		LinkedList<CopertSelector> selectors = new LinkedList<>();
		selectors.add(new CopertSelector()); // Behaves as a seed
		
		// The initial childs are a copy of the originalSelectors
		LinkedList<CopertSelector> initialSelectors = new LinkedList<>();
		initialSelectors.addAll(selectors);
		
		_generateSelectors(selectors, initialSelectors, copertProfile, vehicleCount);

		while(vehicles.size() < vehicleCount ) {
			/*
			 * TODO
			 * The vehicle counts can be ceiled multiple times during the process.
			 * With a LinkedList, we only select the last added vehicles.
			 * It would be better to use a Priority Queue with a random order, in order
			 * to have more uniform results that better represent the originally specified
			 * rates.
			 */
			vehicles.add(generateVehicle(selectors.poll()));
		}
		return vehicles;
	}
	
	/*
	 * Recursive function to generate copert selectors according to the given profile.
	 */
	void _generateSelectors(LinkedList<CopertSelector> originalSelectors, LinkedList<CopertSelector> currentSelectors, CopertProfile copertProfile, int vehicleCount) {
		
		if (copertProfile == null) {
			return;
		}
		
		int requiredNumberOfVehicles = 0;
		for(CopertRate rate : copertProfile.getValues()) {
			/*
			 * We take the ceil of the vehicle number, so that we will have a final selector count
			 * that is at least the number of vehicles to generate.
			 * This will ensure that the final selectors list contains enough selectors to generate
			 * the required number of vehicles, without using any RANDOM selectors.
			 */
			requiredNumberOfVehicles += (int) Math.ceil(vehicleCount * rate.getRate());
		}
		while (currentSelectors.size() < requiredNumberOfVehicles) {
			/*
			 * Feed the childSelectors AND the originalSelectors with new CopertSelectors,
			 * initialized as copies of the last childSelector.
			 * Because the same newSelector is added to the two lists, any change performed
			 * on the selector from childSelectors will also be reflected in originalSelectors.
			 */
			CopertSelector newSelector = new CopertSelector();
			newSelector.putAll(currentSelectors.peek());
			currentSelectors.add(newSelector);
			originalSelectors.add(newSelector);
		}
		CopertHeader header = copertProfile.getHeader();
		/*
		 * We build a selectors copy to easily dispatch selectors between child rates.
		 * Notice that CopertSelector references are always the same, and so the modified
		 * selectors are always the original selectors.
		 */
		LinkedList<CopertSelector> copertSelectorCopy = new LinkedList<>();
		copertSelectorCopy.addAll(currentSelectors);
		
		for(CopertRate rate : copertProfile.getValues()) {
			/*
			 * We take the ceil, as previously explained.
			 */
			int vehicleCountForThisRate = (int) Math.ceil(vehicleCount * rate.getRate());
			LinkedList<CopertSelector> childSelectors = new LinkedList<>();
			for (int i = 0 ; i < vehicleCountForThisRate; i++) {
				/*
				 * Because of the newSelectors eventually generated to fit the "ceil" count
				 * requirements, we are sure that we have enough selectors to poll.
				 */
				childSelectors.add(copertSelectorCopy.poll());
			}
			for(CopertSelector selector : childSelectors) {
				/*
				 * Configure the child selectors with the value specified in the input file.
				 * (along with the corresponding rate)
				 */
				selector.put(header,
						CopertField.valueOf(header, rate.getValue()));
			}
			_generateSelectors(originalSelectors, childSelectors, rate.getSubProfile(), vehicleCountForThisRate);
		}
	}
	
	
	
	private DeliveryVehicle generateVehicle(CopertSelector copertSelector) {
		/*
		 * The previously generated CopertSelectors are initialized with fixed values from 
		 * the input file.
		 * However, the required field to reach a single Copert parameters set is not necessarily
		 * complete yet.
		 * For example, we could have such a selector :
		 * (LIGHT_WEIGHT, RANDOM, NI-I, EURO 6, RANDOM)
		 * 
		 * When we apply select, we actually replace the RANDOM fields with an actual path in the
		 * Copert Tree to fix ALL the fields until the Copert parameters.
		 * 
		 * The final selector is returned by completeTree.getPath().
		 */
		CopertTree completeTree = copertParser.getCopertTree()
				.select(copertSelector.get(CopertHeader.CATEGORY).matcher()) // "Category"
				.select(copertSelector.get(CopertHeader.FUEL).matcher()) // "Fuel"
				.select(copertSelector.get(CopertHeader.SEGMENT).matcher()) // "Segment"
				.select(copertSelector.get(CopertHeader.EURO_STANDARD).matcher()) // "Euro Standard"
				.select(copertSelector.get(CopertHeader.TECHNOLOGY).matcher()); // "Technology"
		
		// The complete selector (no more RANDOM fields)
		CopertSelector finalSelector = completeTree.getPath();
		
		Copert copert = new Copert(completeTree);

		return new DeliveryVehicle(
				String.valueOf(index++),
				(VehicleCategory) finalSelector.get(CopertHeader.CATEGORY),
				(Fuel) finalSelector.get(CopertHeader.FUEL),
				(VehicleSegment) finalSelector.get(CopertHeader.SEGMENT),
				(EuroNorm) finalSelector.get(CopertHeader.EURO_STANDARD),
				(Technology) finalSelector.get(CopertHeader.TECHNOLOGY),
				copert
				);
	}
}
