package org.dcsa.ctk.consumer.reporter.impl;

import org.dcsa.ctk.consumer.model.CheckListItem;
import org.dcsa.ctk.consumer.reporter.Reporter;
import org.dcsa.ctk.consumer.reporter.ExtentReportManager;
import org.dcsa.ctk.consumer.reporter.ExtentReportModifier;
import org.dcsa.ctk.consumer.service.config.impl.ConfigService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class ReporterImpl implements Reporter {
    public String generateHtmlTestReport() {
        Map<String, List<CheckListItem>> groupedCheckListItem = getGroupedCheckListItem(ConfigService.checkListItemMap);
        for( Map.Entry<String, List<CheckListItem>> entry :  groupedCheckListItem.entrySet()){
            List<CheckListItem> value = entry.getValue();
            value.forEach(ExtentReportManager::writeExtentTestReport);
        }
       ExtentReportModifier.modifyFile(ExtentReportManager.getReportPath());
       ExtentReportManager.cleanup();
        return ExtentReportManager.getReportPath();
    }
    public static Map<String, List<CheckListItem>> getGroupedCheckListItem(Map<String, List<CheckListItem>> checkListItemMap) {
        Map<String, List<CheckListItem>> groupedCheckListItem = new TreeMap<>();
        for (Map.Entry<String, List<CheckListItem>> entry : checkListItemMap.entrySet()) {
            List<CheckListItem> checkListItems = entry.getValue();
            for (CheckListItem item : checkListItems) {
                String requirementID = item.getResponseDecoratorWrapper().getRequirementID();
                if (groupedCheckListItem.containsKey(requirementID)) {
                    groupedCheckListItem.get(requirementID).add(item);
                } else {
                    List<CheckListItem> list = new ArrayList<>();
                    list.add(item);
                    groupedCheckListItem.put(requirementID, list);
                }
            }
        }
        return groupedCheckListItem;
    }
}



