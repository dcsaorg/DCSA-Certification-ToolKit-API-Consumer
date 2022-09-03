package org.dcsa.ctk.consumer.service.sql;

import lombok.extern.java.Log;
import org.dcsa.ctk.consumer.util.SqlUtility;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@Service
public class SqlRemoveHandler {
    public boolean deleteLastEvents(){
        List<String> transportCallIds = SqlUtility.getAllTransportCallId();
        SqlUtility.deleteLastEventFromTableWithEventId("shipment_event");
        SqlUtility.findDeleteShipmentTransportByTransportId(SqlUtility.getLastTransportId());
        SqlUtility.deleteLastEventFromTableWithEventId("transport_event");
        SqlUtility.deleteLastEventFromTableWithId("transport_call_voyage");
        SqlUtility.deleteLastEventFromTableWithEventId("equipment_event");
        SqlUtility.deleteLastEventFromTableWithEventId("operations_event");
        SqlUtility.deleteTransportEventByTransportCallId(transportCallIds);
        SqlUtility.deleteTransportByTransportCallId(transportCallIds);
        SqlUtility.deleteOperationsEventEquipmentEventByTransportCallId(transportCallIds);
        SqlUtility.deleteLastEventFromTableWithId("transport_call");
        SqlUtility.deleteLastReferences();
        SqlUtility.deleteLastSeal();
        return true;
    }

    public boolean deleteAllEvents(){
        SqlUtility.removeAllEventWithEventId("shipment_event");
        SqlUtility.removeAllEventId("shipment_transport");
        SqlUtility.removeAllEventId("transport");
        SqlUtility.removeAllEventWithEventId("transport_event");
        SqlUtility.removeAllEventId("transport_call_voyage");
        SqlUtility.removeAllEventWithEventId("equipment_event");
        SqlUtility.removeAllEventWithEventId("operations_event");
        SqlUtility.removeAllEventId("transport_call");
        return true;
    }

}
