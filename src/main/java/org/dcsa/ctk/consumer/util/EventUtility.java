package org.dcsa.ctk.consumer.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class EventUtility {

    public static String getEquipmentEvent()
    {
        String event=FileUtility.loadResourceAsString("events/v2/EquipmentEvent.json");
        Map<String, Object> map =JsonUtility.getObjectFromJson(Map.class,event);
        return JsonUtility.getStringFormat(map);
    }

    public static String getTransportEvent()
    {
        String event=FileUtility.loadResourceAsString("events/v2/TransportEvent.json");
        Map<String, Object> map =JsonUtility.getObjectFromJson(Map.class,event);
        return JsonUtility.getStringFormat(map);
    }

    public static String getShipmentEvent()
    {
        String event=FileUtility.loadResourceAsString("events/v2/ShipmentEvent.json");
        Map<String, Object> map =JsonUtility.getObjectFromJson(Map.class,event);
        return JsonUtility.getStringFormat(map);
    }

    public static boolean checkEventDateDateAfterNow(String FilePath){
        String event = FileUtility.loadFileAsString(FilePath);
        Map map =JsonUtility.getObjectFromJson(Map.class,event);
        String eventCreatedDateTimeStr = (String) map.get("eventCreatedDateTime");
        Instant instant = Instant.parse(eventCreatedDateTimeStr);
        return instant.isAfter(Instant.now());
    }
}
