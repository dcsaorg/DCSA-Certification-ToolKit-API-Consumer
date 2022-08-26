package org.dcsa.ctk.consumer.controller;

import org.dcsa.ctk.consumer.init.AppProperty;
import org.dcsa.ctk.consumer.service.uploader.StorageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = DataRequestController.API_VERSION)
public class DataRequestController {

   private final StorageService storageService;
   private final SqlInsertHandler sqlInsertHandler;
   private final SqlRemoveHandler sqlRemoveHandler;
   private final AppProperty appProperty;
   public static final String API_VERSION = "/v2";
   private  static final String GET_JSON_SHIPMENT = "/example-data/full-shipment";
   private static final String POST_JSON_SHIPMENT = "/uploadShipment";
   private  static final String DELETE_LAST_SHIPMENT = "/removeLastShipment";

   private static final String DELETE_ALL_EVENTS = "/removeAllEvent";


    public DataRequestController(StorageService storageService,
                                 SqlInsertHandler sqlInsertHandler,
                                 SqlRemoveHandler sqlRemoveHandler,
                                 AppProperty appProperty) {
        this.storageService = storageService;
        this.sqlInsertHandler = sqlInsertHandler;
        this.sqlRemoveHandler = sqlRemoveHandler;
        this.appProperty = appProperty;
        this.appProperty.init();
    }
    @PostMapping(path = POST_JSON_SHIPMENT)
    public String handleJsonFileUpload(@RequestParam("file") MultipartFile file) {
        String name = file.getOriginalFilename();
        String[] fileName = name.split("\\.");
        String uploadedPath = storageService.store(file, AppProperty.uploadPath);
        if(fileName.length > 1){
            if( fileName[0].toLowerCase().contains("fullshipment")){
                return sqlInsertHandler.insertJsonSqlData(uploadedPath, UploadType.JsonFullShipment);
            }

        }
        return "Successfully inserted in the database JSON file: "+file.getOriginalFilename();
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
