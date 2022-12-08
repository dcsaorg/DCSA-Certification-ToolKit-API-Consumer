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
    TNT_2_2_API_CSM_3("TNT.2.2.API.CSM.3", "POST /event-subscriptionsFails to creates  a new subscription if API version not provided in the URI"),


    TNT_2_2_API_PRV_1("TNT.2.2.API.PRV.1", "Full Version number present in response headers"),
    TNT_2_2_API_PRV_3("TNT.2.2.API.PRV.3", "HTTP verbs (GET, PUT, PATCH, POST, DELETE, HEAD) must be in request"),
    TNT_2_2_API_PRV_4("TNT.2.2.API.PRV.4", "URL must adhere to Kebab-case"),
    TNT_2_2_SUB_PRV_1("TNT.2.2.SUB.PRV.1", "GET /event-subscriptions returns all subscriptions"),
    TNT_2_2_SUB_PRV_2("TNT.2.2.SUB.PRV.2", "POST /event-subscriptions creates a new subscription"),
    TNT_2_2_SUB_PRV_3("TNT.2.2.SUB.PRV.3", "GET /event-subscriptions/{subscriptionID} fetches a specific subscription with ID={subscriptionID}"),
    TNT_2_2_SUB_PRV_4("TNT.2.2.SUB.PRV.4", "PUT /event-subscriptions/{subscriptionID} updates a specific subscription with ID={subscriptionID}"),
    TNT_2_2_SUB_PRV_5("TNT.2.2.SUB.PRV.5", "DELETE /event-subscriptions/{subscriptionID} deletes a specific subscription with ID={subscriptionID}"),
    TNT_2_2_SUB_PRV_6("TNT.2.2.SUB.PRV.6", "PUT /event-subscriptions/{subscriptionID}/secret (Add new endpoint for changing the secret"),
    TNT_2_2_SUB_PRV_7("TNT.2.2.SUB.PRV.7", "Perform validation of callback URL"),
    TNT_2_2_SUB_PRV_8("TNT.2.2.SUB.PRV.8", "Code 204 for subscription URL must be accepted as the only valid response when validating the callback URL"),
    TNT_2_2_SUB_PRV_9("TNT.2.2.SUB.PRV.9","Shared secret is not exposed in API endpoints"),
    TNT_2_2_SUB_PRV_10("TNT.2.2.SUB.PRV.10", "Subscription requested must be rejected if the secrets are not adequate for the signature algorithm"),
    TNT_2_2_EVT_PRV_1("TNT.2.2.EVT.PRV.1", "GET /events returns all events"),
    TNT_2_2_SUB_CSM_201("TNT.2.2.SUB.CSM.201", ""),
    TNT_2_2_API_CSM_400("TNT.2.2.API.CSM.400", ""),
    TNT_2_2_API_CSM_200("TNT.2.2.API.CSM.200", ""),
    TNT_2_2_API_SUB_CSM_200("TNT.2.2.API.SUB.CSM.200", ""),
    TNT_2_2_API_SUB_CSM_202("TNT.2.2.API.SUB.CSM.202", ""),
    TNT_2_2_API_SUB_CSM_403("TNT.2.2.API.SUB.CSM.403", ""),
    TNT_2_2_API_SUB_CSM_400("TNT.2.2.API.SUB.CSM.400", ""),
    TNT_2_2_CSM_200("TNT.2.2.CSM.200", ""),
    TNT_2_2_CSM_HEAD_200("TNT.2.2.CSM.HEAD.200", ""),
    TNT_2_2_CSM_HEAD_404("TNT.2.2.CSM.HEAD.404", ""),
    TNT_2_2_CSM_POST_200("TNT.2.2.CSM.POST.200", ""),
    TNT_2_2_CSM_POST_400("TNT.2.2.CSM.POST.400", ""),
    TNT_2_2_SUB_CSM_TIME_200("TNT.2.2.SUB.CSM.TIME.200", ""),
    TNT_2_2_SUB_CSM_HEAD_404("TNT.2.2.SUB.CSM.HEAD.404", ""),
    TNT_2_2_DPY_PRV_2("TNT_2_2_DPY_PRV_2", "Major Version number must be present in URL"),
    TNT_2_2_ERR_PRV_1("TNT.2.2.ERR.PRV.1","Test case validating bad request - HTTP 400"),
    TNT_2_2_ERR_PRV_2("TNT.2.2.ERR.PRV.2", "Test case validating unauthorized request - HTTP 401"),
    TNT_2_2_ERR_PRV_3("TNT.2.2.ERR.PRV.3", "Test case validating forbidden request - HTTP 403"),
    TNT_2_2_ERR_PRV_4("TNT.2.2.ERR.PRV.4", "Test case validating not found request - HTTP 404" ),
    TNT_2_2_ERR_PRV_5("TNT.2.2.ERR.PRV.5", "Test case validating Method not allowed - HTTP 405"),
    TNT_2_2_ERR_PRV_6("TNT.2.2.ERR.PRV.6", "Test case validating conflict request - HTTP 409"),
    TNT_2_2_ERR_PRV_7("TNT.2.2.ERR.PRV.7", "Test case validating too many request - HTTP 429"),
    TNT_2_2_ERR_PRV_8("TNT.2.2.ERR.PRV.8", "Test case validating   internal server error - HTTP 500"),
    TNT_2_2_ERR_PRV_9("TNT.2.2.ERR.PRV.9", "Test case validating service unavailable error - HTTP 503");
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
