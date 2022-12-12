package org.dcsa.ctk.consumer.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum ValidationRequirementId {
    TNT_2_2_API_CHK_1("TNT.2.2.API.CHK.1", "GET /events request with Query Parameters eventType"),
    TNT_2_2_API_CHK_2("TNT.2.2.API.CHK.2", "GET /event-subscriptions response with additional enum"),
    TNT_2_2_API_CHK_3("TNT.2.2.API.CHK.3", "HTTP verbs (GET, PUT, PATCH, POST, DELETE, HEAD) must be in request"),
    TNT_2_2_API_CHK_4("TNT.2.2.API.CHK.4", "URL must adhere to Kebab-case"),
    TNT_2_2_API_CHK_5("TNT.2.2.API.CHK.5", "POST /event-subscriptions creates a new subscription check"),
    TNT_2_2_API_CHK_6("TNT.2.2.API.CHK.6", " POST /event-subscriptions Must provide a valid callback URL"),
    TNT_2_2_API_CHK_7("TNT.2.2.API.CHK.7", "POST /event-subscriptions creates a new subscription with major API version provided in the URI"),
    TNT_2_2_API_CHK_8("TNT.2.2.API.CHK.8", "POST /event-subscriptions Must provide a valid secret"),
    TNT_2_2_API_CHK_9("TNT.2.2.API.CHK.9", "POST /event-subscriptions fails to creates  a new subscription if API version not provided in the URI"),
    TNT_2_2_API_CHK_10("TNT.2.2.API.CHK.10", "POST /event-subscriptions fails to creates  a new subscription if no callback  response"),
    TNT_2_2_API_CHK_11("TNT.2.2.API.CHK.11", "POST /event-subscriptions-response with optional field"),
    TNT_2_2_API_CHK_12("TNT.2.2.API.CHK.12", "POST /event-subscriptions response with httpCode 400"),
    TNT_2_2_API_CHK_13("TNT.2.2.API.CHK.13", "POST /event-subscriptions Response with httpCode 500"),
    TNT_2_2_SUB_CSM_1("TNT.2.2.SUB.CSM.1", "Respond to a HEAD request with a HTTP 204 success status to facilitate validation"),
    TNT_2_2_SUB_CSM_2("TNT.2.2.SUB.CSM.2", "PUT /event-subscriptions/{subscriptionId}/secret there is rotation or update of the shared secret key possibility"),
    TNT_2_2_SUB_CSM_3("TNT.2.2.SUB.CSM.3", "the secret shared by subscriber in base64 format and has a minimum of 32 bytes"),
    TNT_2_2_SUB_CSM_4("TNT.2.2.SUB.CSM.4", "The callback endpoint must respond to a correctly formatted and signed notification with response code in the 2XX range for successful responses"),
    TNT_2_2_SUB_CSM_5("TNT.2.2.SUB.CSM.5", "The callback endpoint must respond to an incorrectly signed notification with a response code in the 4XX range, where 401 is recommended"),
    TNT_2_2_SUB_CSM_6("TNT.2.2.SUB.CSM.6", "The callback endpoint must respond to an incorrectly formatted notification with a response code in the 4XX range"),
    TNT_2_2_SUB_CSM_7("TNT.2.2.SUB.CSM.7", "The callback endpoint must reject notifications where eventCreatedDateTime is more than 10 seconds in the future with a response code in the 4XX range"),
    TNT_2_2_SUB_CSM_8("TNT.2.2.SUB.CSM.8", "The callback endpoint is not reachable"),
    TNT_2_2_SUB_CSM_9("TNT.2.2.SUB.CSM.9", "check correct post response with a HTTP 204 no content success status facilitate validation"),
    TNT_2_2_API_CSM_3("TNT.2.2.API.CSM.3", "POST /event-subscriptions Fails to creates  a new subscription if API version not provided in the URI");

    private static final Map<String, ValidationRequirementId> RequirementId = new HashMap<>();
    private static final Map<String, ValidationRequirementId> RequirementDetails = new HashMap<>();

    static {
        for (ValidationRequirementId v : values()) {
            RequirementId.put(v.id, v);
            RequirementDetails.put(v.details, v);
        }
    }
    private final String id;
    private final String details;

    ValidationRequirementId(String id, String details) {
        this.id = id;
        this.details = details;
    }

    public String getId(){
        return id;
    }

    public String getDetails() {
        return details;
    }
    public static ValidationRequirementId getById(String id){
        return RequirementId.get(id);
    }
}
