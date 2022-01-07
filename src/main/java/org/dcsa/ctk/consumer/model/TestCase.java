package org.dcsa.ctk.consumer.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TestCase {
    String route;
    Map<String,List<ResponseDecoratorWrapper>> variants;
}
