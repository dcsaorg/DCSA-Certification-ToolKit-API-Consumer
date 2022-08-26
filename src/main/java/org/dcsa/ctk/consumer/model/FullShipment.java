package org.dcsa.ctk.consumer.model;

import lombok.Data;
import org.dcsa.core.events.model.EquipmentEvent;
import org.dcsa.core.events.model.ShipmentEvent;
import org.dcsa.core.events.model.TransportEvent;

@Data
public class FullShipment {
    private ShipmentEvent shipmentEvent;
    private EquipmentEvent equipmentEvent;
    private TransportEvent transportEvent;
}
