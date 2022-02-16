package org.dcsa.ctk.consumer.model;


import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
@Data
public class TestResult {
    public static List<Map<CheckListItem, CustomerLogger>> testResult=new LinkedList<>();
}
