package org.dcsa.ctk.consumer.constant;

import java.util.HashMap;
import java.util.Map;

public class TestRequirement {
    private static Map<String, String> requirements;

    static void init() {
        requirements = new HashMap<>();
        requirements.put("TNT.2.2.SUB.CSM.2", "Response with empty body");
        requirements.put("TNT.2.2.SUB.CSM.3", "Backward compatibility check");
        requirements.put("TNT.2.2.ERR.PRV.400", "Test case validating \"bad request\" - HTTP 400");
        requirements.put("TNT.2.2.ERR.PRV.500", "Test case validating  \"internal server\" error - HTTP 500");
        requirements.put("TNT.2.2.ERR.PRV.404", "Test case validating \"not found\" request - HTTP 404");
        requirements.put("TNT.2.2.API.PRV.1", "Full Version number present in response headers");
        requirements.put("TNT.2.2.API.PRV.2", "GET requests on collection results SHOULD implement pagination");
        requirements.put("TNT.2.2.API.PRV.4", "Links to current, previous, next, first and last page SHOULD be available in the response headers");
        requirements.put("TNT.2.2.EVN.PRV.1", "GET/Event Returns all events");
    }

    public static String getRequirement(String requirementID) {
        if (requirements == null)
            init();
        String requirement=requirements.get(requirementID);
        if(requirement==null)
            requirement=requirementID;
        return requirement;
    }

}
