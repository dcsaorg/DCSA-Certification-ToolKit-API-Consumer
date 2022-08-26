package org.dcsa.ctk.consumer.model;

import lombok.Data;
import org.dcsa.ctk.consumer.model.enums.EventTimeDuration;
import org.dcsa.ctk.consumer.model.enums.TimeOffset;

@Data
public class EventTimeOffset {
    private EventTimeDuration eventTimeDuration;
    private TimeOffset timeOffset;
    private long amount;

    public static EventTimeOffset makeEventTimeOffset(String str){
        EventTimeOffset eventTimeOffset = new EventTimeOffset();
        if(str.contains("plus") || str.contains("PLUS")){
            eventTimeOffset.setTimeOffset(TimeOffset.FUTURE);
            str = str.replace("plus", "");
            str = str.replace("PLUS", "");
        }else if(str.contains("minus") || str.contains("MINUS")){
            eventTimeOffset.setTimeOffset(TimeOffset.PAST);
            str = str.replace("minus", "");
            str = str.replace("MINUS", "");
        }

        String numberOnly = str.replaceAll("\\D+", "");
        try{
            int number = Integer.parseInt(numberOnly);
            eventTimeOffset.setAmount(number);
        }
        catch (NumberFormatException ex){
            throw new RuntimeException("Expecting number in time offset ");
        }
        // set timeDuration
        if((str.charAt(str.length()-1) == 'D' || str.charAt(str.length()-1) == 'd')){
            eventTimeOffset.setEventTimeDuration(EventTimeDuration.DAY);
        }else if((str.charAt(str.length()-1) == 'M' || str.charAt(str.length()-1) == 'm')){
            eventTimeOffset.setEventTimeDuration(EventTimeDuration.MONTH);
        }else if((str.charAt(str.length()-1) == 'Y' || str.charAt(str.length()-1) == 'y')){
            eventTimeOffset.setEventTimeDuration(EventTimeDuration.YEAR);
        }else if((str.charAt(str.length()-1) == 'H' || str.charAt(str.length()-1) == 'h')){
            eventTimeOffset.setEventTimeDuration(EventTimeDuration.HOUR);
        }
        return eventTimeOffset;
    }
}
