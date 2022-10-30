package org.dcsa.ctk.consumer.controller;

import org.dcsa.ctk.consumer.config.AppProperty;
import org.dcsa.ctk.consumer.model.enums.UploadType;
import org.dcsa.ctk.consumer.service.sql.SqlInsertHandler;
import org.dcsa.ctk.consumer.service.sql.SqlRemoveHandler;
import org.dcsa.ctk.consumer.service.uploader.FileUploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.dcsa.ctk.consumer.controller.DataRequestController.REQUEST_URL;

@RestController
@RequestMapping(path = REQUEST_URL, produces = {MediaType.APPLICATION_JSON_VALUE})
public class DataRequestController {
    public static final String REQUEST_URL = "/conformance/data-management";
    private static final String POST_JSON_SHIPMENT = "/uploadShipment";
    private  static final String GET_JSON_SHIPMENT = "/example-data/full-shipment/{timeOffset}";
    private  static final String DELETE_LAST_SHIPMENT = "/removeLastShipment";

    private static final String DELETE_ALL_EVENTS = "/removeAllEvent";
    private final FileUploadService fileUploadService;
    private final SqlInsertHandler sqlInsertHandler;
    private final SqlRemoveHandler sqlRemoveHandler;
    private final AppProperty appProperty;
    public DataRequestController(FileUploadService fileUploadService, SqlInsertHandler sqlInsertHandler, SqlRemoveHandler sqlRemoveHandler, AppProperty appProperty) {
        this.fileUploadService = fileUploadService;
        this.sqlInsertHandler = sqlInsertHandler;
        this.sqlRemoveHandler = sqlRemoveHandler;
        this.appProperty = appProperty;
        this.appProperty.init();
    }

    @PostMapping(value = POST_JSON_SHIPMENT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<String> upload(@RequestPart("file") FilePart filePart) {
        var monoList = fileUploadService.readJson(filePart);
        var list = monoList.share().block();
        String result = Objects.requireNonNull(list).stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
        return Stream.of(result.split("\n"))
                .map(String::trim)
                .collect(Collectors.toList());
    }
    @GetMapping( path = GET_JSON_SHIPMENT)
    public ResponseEntity<byte[]> getFullShipmentTimeOffset(@RequestParam(defaultValue = "plus0h") String timeOffset){
        byte[] jsonByte = sqlInsertHandler.getJsonData( UploadType.JsonFullShipment, timeOffset).getBytes();
        String headerValues = "attachment;filename="+UploadType.JsonFullShipment.name()+".json";
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValues)
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(jsonByte.length)
                .body(jsonByte);
    }

    @DeleteMapping(path = DELETE_LAST_SHIPMENT)
    public String removeLastShipment(){
        if(sqlRemoveHandler.deleteLastEvents()){
            return "Successfully deleted the last ShipmentEvent TransportEvent, EquipmentEvent";
        }else {
            return "Failed delete the last shipmentEvent";
        }
    }
    @DeleteMapping(path = DELETE_ALL_EVENTS)
    public String removeAllEvent(){
        if(sqlRemoveHandler.deleteAllEvents()){
            return "All events removed successfully";
        }else{
            return "Request failed";
        }
    }
}
