package org.dcsa.ctk.consumer.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Variant {
Map<String, List<ResponseDecoratorWrapper>> resDecorator;
}
