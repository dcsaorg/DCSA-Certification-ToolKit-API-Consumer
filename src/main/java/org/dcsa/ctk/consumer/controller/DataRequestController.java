package org.dcsa.ctk.consumer.controller;

import org.dcsa.ctk.consumer.init.AppProperty;
import org.dcsa.ctk.consumer.model.enums.UploadType;
import org.dcsa.ctk.consumer.service.sql.SqlInsertHandler;
import org.dcsa.ctk.consumer.service.sql.SqlRemoveHandler;
import org.dcsa.ctk.consumer.service.uploader.FileUploadService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = DataRequestController.API_VERSION)
public class DataRequestController {

    private final FileUploadService fileUploadService;
   private final SqlInsertHandler sqlInsertHandler;
   private final SqlRemoveHandler sqlRemoveHandler;
   private final AppProperty appProperty;
   public static final String API_VERSION = "/v2";
   private  static final String GET_JSON_SHIPMENT = "/example-data/full-shipment";
   private static final String POST_JSON_SHIPMENT = "/uploadShipment";
   private  static final String DELETE_LAST_SHIPMENT = "/removeLastShipment";

   private static final String DELETE_ALL_EVENTS = "/removeAllEvent";


    public DataRequestController(
            FileUploadService fileUploadService, SqlInsertHandler sqlInsertHandler,
            SqlRemoveHandler sqlRemoveHandler,
            AppProperty appProperty) {
        this.fileUploadService = fileUploadService;
        this.sqlInsertHandler = sqlInsertHandler;
        this.sqlRemoveHandler = sqlRemoveHandler;
        this.appProperty = appProperty;
        this.appProperty.init();
    }

    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public  Mono<String>   uploadFile(@RequestPart("file") Mono<FilePart> filePartMono) {
        return  filePartMono
                .flatMap(fp -> fp.transferTo(AppProperty.uploadPath.resolve(fp.filename())))
                .then(Mono.just("Successfully inserted in the database shipment JSON file"));
    }


/*
    @PostMapping(path = POST_JSON_SHIPMENT, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public Mono<String> upload(@RequestPart("file") FilePart filePart) {
        return fileUploadService.getLines(filePart);
    }
*/


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
/*
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public @ResponseBody
    String uploadFileHandler(
            @RequestPart("file") FilePart file) {
         file.content().flatMap()
        if (true) {
            try {
                List<byte[]>  bytes = file.content()
                        .flatMap()
                        .flatMap({ dataBuffer -> Flux.just(dataBuffer) })
            .collectList()
                        .awaitFirst()

                // concat ByteArrays
                val byteStream = ByteArrayOutputStream()
                bytesList.forEach { bytes -> byteStream.write(bytes) }
                return byteStream.toByteArray()
                byte[] bytes = file.filename().getBytes();

                // Creating the directory to store file
                String rootPath = System.getProperty("catalina.home");
                File dir = new File(rootPath + File.separator + "tmpFiles");
                if (!dir.exists())
                    dir.mkdirs();

                // Create the file on server
                File serverFile = new File(dir.getAbsolutePath()
                        + File.separator + file.filename());
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                stream.write(bytes);
                stream.close();

                System.out.printf("Server File Location="
                        + serverFile.getAbsolutePath());

                return "You successfully uploaded file=" + file.filename();
            } catch (Exception e) {
                return "You failed to upload " + file.filename() + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + file.filename()
                    + " because the file was empty.";
        }
    }*/

}
