package org.liris.smartgov.lez.core.output.establishment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.liris.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicle;
import org.liris.smartgov.lez.core.agent.establishment.VehicleCapacity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class FleetSerializer extends StdSerializer<Map<VehicleCapacity, Collection<DeliveryVehicle>>> {

	private static final long serialVersionUID = 1L;

	public FleetSerializer() {
		this(null);
	}
	
	protected FleetSerializer(Class<Map<VehicleCapacity, Collection<DeliveryVehicle>>> t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void serialize(Map<VehicleCapacity, Collection<DeliveryVehicle>> value, JsonGenerator gen,
			SerializerProvider provider) throws IOException {
		Collection<DeliveryVehicle> vehicles = new ArrayList<>();
		for(Collection<DeliveryVehicle> _vehicles : value.values()) {
			vehicles.addAll(_vehicles);
		}
		gen.writeObject(vehicles);
	}

}
