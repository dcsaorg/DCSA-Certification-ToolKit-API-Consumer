package org.dcsa.ctk.consumer.model.enums;

public enum ValidationRequirementID {
    TNT_2_2_API_PRV_1("TNT.2.2.API.PRV.1"),
    TNT_2_2_SUB_CSM_201("TNT.2.2.SUB.CSM.201"),
    TNT_2_2_API_CSM_400("TNT.2.2.API.CSM.400"),
    TNT_2_2_API_CSM_200("TNT.2.2.API.CSM.200"),
    TNT_2_2_SUB_CSM_3("TNT.2.2.SUB.CSM.3"),
    TNT_2_2_API_SUB_CSM_200("TNT.2.2.API.SUB.CSM.200"),
    TNT_2_2_API_SUB_CSM_202("TNT.2.2.API.SUB.CSM.202"),
    TNT_2_2_API_SUB_CSM_403("TNT.2.2.API.SUB.CSM.403"),
    TNT_2_2_API_SUB_CSM_400("TNT.2.2.API.SUB.CSM.400"),
    TNT_2_2_CSM_200("TNT.2.2.CSM.200"),
    TNT_2_2_CSM_HEAD_200("TNT.2.2.CSM.HEAD.200"),
    TNT_2_2_CSM_HEAD_400("TNT.2.2.CSM.HEAD.400"),
    TNT_2_2_CSM_POST_200("TNT.2.2.CSM.POST.200"),
    TNT_2_2_CSM_POST_400("TNT.2.2.CSM.POST.400"),
    TNT_2_2_SUB_CSM_TIME_200("TNT.2.2.SUB.CSM.TIME.200");
    private String value;

    ValidationRequirementID(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
