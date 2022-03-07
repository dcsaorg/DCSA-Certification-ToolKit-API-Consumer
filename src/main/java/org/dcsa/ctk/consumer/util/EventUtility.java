package org.dcsa.ctk.consumer.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class EventUtility {

    public static String getEquipmentEvent()
    {
        String event=FileUtility.loadFileAsString("events/v2/EquipmentEvent.json");
        Map<String, Object> map =JsonUtility.getObjectFromJson(Map.class,event);
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String newDate = DATE_FORMAT.format(new Date());
        map.put("eventDateTime",newDate);
        map.put("eventCreatedDateTime",newDate);
        String response=JsonUtility.getStringFormat(map);
        return response;
    }

    public static String getTransportEvent()
    {
        String event=FileUtility.loadFileAsString("events/v2/TransportEvent.json");
        Map<String, Object> map =JsonUtility.getObjectFromJson(Map.class,event);
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String newDate = DATE_FORMAT.format(new Date());
        map.put("eventDateTime",newDate);
        map.put("eventCreatedDateTime",newDate);
        String response=JsonUtility.getStringFormat(map);
        return response;
    }

    public static String getShipmentEvent()
    {
        String event=FileUtility.loadFileAsString("events/v2/ShipmentEvent.json");
        Map<String, Object> map =JsonUtility.getObjectFromJson(Map.class,event);
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String newDate = DATE_FORMAT.format(new Date());
        map.put("eventDateTime",newDate);
        map.put("eventCreatedDateTime",newDate);
        String response=JsonUtility.getStringFormat(map);
        return response;
    }
}
