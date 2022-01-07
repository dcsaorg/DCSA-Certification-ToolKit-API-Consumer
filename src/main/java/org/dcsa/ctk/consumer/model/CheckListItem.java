package org.dcsa.ctk.consumer.model;

import lombok.Data;
import org.dcsa.ctk.consumer.constants.CheckListStatus;

import java.util.UUID;

@Data
public class CheckListItem {
    String id=UUID.randomUUID().toString();
    ResponseDecoratorWrapper responseDecoratorWrapper;
    CustomerLogger log;
    CheckListStatus status=CheckListStatus.NOT_COVERED;
}
