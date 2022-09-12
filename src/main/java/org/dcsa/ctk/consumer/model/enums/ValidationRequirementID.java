package org.dcsa.ctk.consumer.model.enums;

public enum ValidationRequirementID {
    TNT_2_2_API_PRV_1("TNT.2.2.API.PRV.1"),
    TNT_2_2_SUB_CSM_2("TNT.2.2.SUB.CSM.2"),
    TNT_2_2_API_CSM_400("TNT.2.2.API.CSM.400"),
    TNT_2_2_API_CSM_200("TNT.2.2.API.CSM.200"),
    TNT_2_2_SUB_CSM_3("TNT.2.2.SUB.CSM.3"),
    TNT_2_2_API_SUB_CSM_200("TNT.2.2.API.SUB.CSM.200"),
    TNT_2_2_API_SUB_CSM_202("TNT.2.2.API.SUB.CSM.202"),
    TNT_2_2_API_SUB_CSM_403("TNT.2.2.API.SUB.CSM.403"),
    TNT_2_2_API_SUB_CSM_400("TNT.2.2.API.SUB.CSM.400");

    private String value;

    ValidationRequirementID(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
