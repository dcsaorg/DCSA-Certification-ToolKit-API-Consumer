package org.dcsa.ctk.consumer.util;

import lombok.extern.java.Log;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.ctk.consumer.config.AppProperty;
import org.dcsa.tnt.model.transferobjects.TNTEventSubscriptionTO;
import org.springframework.util.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Log
public class SqlUtility {
    public static final String CTK_SUBSCRIPTION_TABLE = "dcsa_im_v3_0.ctk_event_subscription";

    private static final String INSET_INTO_CTK_SUBSCRIPTION = "INSERT INTO "+CTK_SUBSCRIPTION_TABLE +
                                                                "(subscription_id, " +
                                                                "callback_url, " +
                                                                "secret, " +
                                                                "carrier_booking_reference, " +
                                                                "equipment_reference, " +
                                                                "transport_call_id, " +
                                                                "carrier_service_code, " +
                                                                "vessel_imo_number) " +
                                                                "VALUES(";

    static public void makeTableIfNotExist() {
        String tableCreate = //"DROP TABLE IF EXISTS "+CTK_SUBSCRIPTION_TABLE+" CASCADE;"
                            "CREATE TABLE IF NOT EXISTS "+CTK_SUBSCRIPTION_TABLE
                            +"(subscription_id             VARCHAR(48),"
                            +"callback_url                 VARCHAR(256),"
                            +"secret                       VARCHAR(256),"
                            +"carrier_booking_reference     VARCHAR(35),"
                            +"equipment_reference           VARCHAR(15),"
                            +"transport_call_id             VARCHAR(100),"
                            +"carrier_service_code          VARCHAR(5),"
                            +"vessel_imo_number             VARCHAR(7))";
        Statement stmt;
        try {
            stmt = AppProperty.getConnection().createStatement();
            stmt.execute(tableCreate);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    static public String getSubscriptionCallBackUuid(String subscriptionId){
        String selectEventSubscription = "select * from dcsa_im_v3_0.event_subscription"+ " where subscription_id = "
                +StringUtils.quote(subscriptionId);
        String callBackUrl = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectEventSubscription);
            while (resultSet.next()) {
                callBackUrl =  resultSet.getString("callback_url");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        String uuid = "";
        if(callBackUrl.length() > 0){
           uuid = APIUtility.getCallBackUuid(callBackUrl);
        }
        return uuid;
    }

    static public String getCallBackUrl(String callBackUrl){
        String selectCallBackUrl = "select callback_url from dcsa_im_v3_0.event_subscription where callback_url = "
                                            +StringUtils.quote(callBackUrl);
        String callBack = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectCallBackUrl);
            while (resultSet.next()) {
                callBack =  resultSet.getString("callback_url");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

        if(callBack.length() > 0){
          return callBack;
        }
        return "";
    }
    static public TNTEventSubscriptionTO getEventSubscriptionBySubscriptionId(String subscriptionId){
        String selectCallBackUrl = "select * from dcsa_im_v3_0.event_subscription where subscription_id = "
                +StringUtils.quote(subscriptionId);
        TNTEventSubscriptionTO tntEventSubscriptionTO = new TNTEventSubscriptionTO();
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectCallBackUrl);
            while (resultSet.next()) {
                tntEventSubscriptionTO.setSubscriptionID(UUID.fromString(resultSet.getString("subscription_id")));
                tntEventSubscriptionTO.setCallbackUrl(resultSet.getString("callback_url"));
                tntEventSubscriptionTO.setCarrierBookingReference(resultSet.getString("carrier_booking_reference"));
                tntEventSubscriptionTO.setEquipmentReference(resultSet.getString("equipment_reference"));
                tntEventSubscriptionTO.setTransportCallID(resultSet.getString("transport_call_id"));
                tntEventSubscriptionTO.setSecret(resultSet.getBytes("secret"));
                tntEventSubscriptionTO.setCarrierServiceCode(resultSet.getString("carrier_service_code"));
                tntEventSubscriptionTO.setVesselIMONumber(resultSet.getString("vessel_imo_number"));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return tntEventSubscriptionTO;
    }

    static public boolean isEventSubscriptionIdExist(UUID id){
        String selectEventSubscriptionSql = "select subscription_id from dcsa_im_v3_0.event_subscription where subscription_id = "
                                            +StringUtils.quote(id.toString());
        String subscriptionId = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectEventSubscriptionSql);
            while (resultSet.next()) {
                subscriptionId = resultSet.getString("subscription_id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(subscriptionId.length() > 0 ){
            return true;
        }else{
            return false;
        }
    }

    static public String getEventSubscriptionId(UUID id){
        String selectEventSubscriptionSql = "select subscription_id from dcsa_im_v3_0.event_subscription where subscription_id = "
                +StringUtils.quote(id.toString());
        String subscriptionId = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectEventSubscriptionSql);
            while (resultSet.next()) {
                subscriptionId = resultSet.getString("subscription_id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
       return subscriptionId;
    }

    static public String getSecretBySubscriptionId(UUID id){
        String selectEventSubscriptionSql = "select secret from dcsa_im_v3_0.event_subscription where subscription_id = "
                +StringUtils.quote(id.toString());
        String plainSecret = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectEventSubscriptionSql);
            while (resultSet.next()) {
                byte[] secret = resultSet.getBytes("secret");
                plainSecret = Base64.getEncoder().encodeToString(secret);
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return plainSecret;
    }

    static public void updateSecretBySubscriptionId(String subscriptionId, String secret){
        String encryptedSecret = CipherUtil.encrypt(secret);
        String updateSecretBySubscriptionIdSql = "UPDATE " + CTK_SUBSCRIPTION_TABLE + " SET " +
                                                 "secret = "+StringUtils.quote(encryptedSecret) +
                                                 "where subscription_id = "+StringUtils.quote(subscriptionId);
        updateRow(updateSecretBySubscriptionIdSql);
    }

    public static int updateRow(String sqlStatement){
        int effectedRow = 0;
        try( Statement statement = AppProperty.getConnection().createStatement()){
            effectedRow = statement.executeUpdate(sqlStatement);
            log.info(sqlStatement+" was successfully updated "+effectedRow+" rows!");
        }catch (SQLException e){
            log.severe(sqlStatement+" was failed!");
            throw new RuntimeException(e);
        }
        return effectedRow;
    }
    public static boolean checkDeleteTransportCallIfExist(String id) {
        String transportCallId = "";
        String selectTransportCallById = "SELECT id FROM dcsa_im_v3_0.transport_call where id = " +
                StringUtils.quote(id);
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectTransportCallById);
            while (resultSet.next()) {
                transportCallId = resultSet.getString("id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(transportCallId.length() > 1){
            deleteTransportByTransportCallId(List.of(transportCallId));
            return true;
        }else{
            return false;
        }
    }
   public static boolean isVesselExist(String imoNumber) {
        String vesselImoNumber = "";
        String selectVessel = "SELECT vessel_imo_number FROM dcsa_im_v3_0.vessel where vessel_imo_number = " +
                StringUtils.quote(imoNumber);
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectVessel);
            while (resultSet.next()) {
                vesselImoNumber = resultSet.getString("vessel_imo_number");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(vesselImoNumber.length() > 1){
            return true;
        }else{
            return false;
        }
    }

    public static boolean checkDeleteShipmentIfExist(String id) {
        String shipmentId = "";
        String selectVessel = "SELECT id FROM dcsa_im_v3_0.shipment where id = " +
                StringUtils.quote(id);
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectVessel);
            while (resultSet.next()) {
                shipmentId = resultSet.getString("id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(shipmentId.length() > 1){
            deleteReferencesByShipmentId(shipmentId);
            deleteShipmentById(shipmentId);
            return true;
        }else{
            return false;
        }
    }
    public static void deleteShipmentById(String id){
        String deleteShipmentById = "delete from dcsa_im_v3_0.shipment where id = "+
                                    StringUtils.quote(id);
        updateRow(deleteShipmentById);
    }

    public static String insertShipment(String shipmentId, String collectionDatetime, String deliveryDatetime){
        String  insertShipment =  "INSERT INTO dcsa_im_v3_0.shipment (\n" +
                                "id,\n"+
                                "collection_datetime,\n" +
                                "delivery_datetime,\n" +
                                "carrier_id,\n" +
                                "carrier_booking_reference\n" +
                ") VALUES (\n";

        String carrierId = selectCarrierId();

        String insertShipmentSql = insertShipment + StringUtils.quote(shipmentId) + "," +
                                "TIMESTAMP "+StringUtils.quote(collectionDatetime) + "," +
                                "TIMESTAMP "+StringUtils.quote(deliveryDatetime) + "," +
                                StringUtils.quote(carrierId) + "," +
                                StringUtils.quote("CR1239719872") + ")";
        if(updateRow(insertShipmentSql) > 0){
               return "A new shipment is inserted with id: "+shipmentId;
        }else {
            return "";
        }
    }

    public static String selectCarrierId(){
       String carrierId = "";
        String selectCarrierId = "SELECT id FROM dcsa_im_v3_0.carrier WHERE smdg_code = 'MSK'";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectCarrierId);
            while (resultSet.next()) {
                carrierId = resultSet.getString("id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return carrierId;
    }

    public static boolean isEquipmentEventExist(String equipmentEventId) {
        String eventId = "";
        String selectEquipmentEvent = "SELECT event_id FROM dcsa_im_v3_0.equipment_event where event_id = " +
                StringUtils.quote(equipmentEventId);
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectEquipmentEvent);
            while (resultSet.next()) {
                eventId = resultSet.getString("event_id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(eventId.length() > 1){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isShipmentEventExist(String shipmentEventId) {
        String eventId = "";
        String selectEquipmentEvent = "SELECT event_id FROM dcsa_im_v3_0.shipment_event where event_id = " +
                StringUtils.quote(shipmentEventId);
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectEquipmentEvent);
            while (resultSet.next()) {
                eventId = resultSet.getString("event_id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(eventId.length() > 1){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isTransportEventExist(String transportEventId) {
        String eventId = "";
        String selectEquipmentEvent = "SELECT event_id FROM dcsa_im_v3_0.transport_event where event_id = " +
                                        StringUtils.quote(transportEventId);
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectEquipmentEvent);
            while (resultSet.next()) {
                eventId = resultSet.getString("event_id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(eventId.length() > 1){
            return true;
        }else{
            return false;
        }
    }

    public static boolean checkDeleteReferences(String id){
        String referenceId = "";
        String selectEquipmentEvent = "SELECT id, shipment_id FROM dcsa_im_v3_0.references where id = " +
                StringUtils.quote(id);
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectEquipmentEvent);
            while (resultSet.next()) {
                referenceId = resultSet.getString("id");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        if(referenceId.length() > 1){
            deleteReferencesById(referenceId);
            return true;
        }else{
            return false;
        }
    }

    public static void deleteReferencesById(String id){
        String deleteReferencesById = "delete dcsa_im_v3_0.references where id = " +
                                        StringUtils.quote(id);
        updateRow(deleteReferencesById);
    }

    public static void deleteReferencesByShipmentId(String shipmentId){
        String deleteReferencesByShipmentId = "delete from dcsa_im_v3_0.references where shipment_id = " +
                                        StringUtils.quote(shipmentId);
        updateRow(deleteReferencesByShipmentId);
    }

    public static void deleteLastEventFromTableWithEventId(String tableName){
        String deleteLastEventSql = "DELETE FROM dcsa_im_v3_0."+tableName+"\n" +
                                            "WHERE event_id in (\n" +
                                            "SELECT event_id \n" +
                                            "FROM dcsa_im_v3_0."+tableName+"\n" +
                                            "ORDER BY event_id desc\n" +
                                            "LIMIT 1 );";
        updateRow(deleteLastEventSql);
    }

    public static void deleteLastEventFromTableWithId(String tableName) {
        String deleteLastEventSql = "DELETE FROM dcsa_im_v3_0." + tableName + "\n" +
                "WHERE id in (\n" +
                "SELECT id \n" +
                "FROM dcsa_im_v3_0." + tableName + "\n" +
                "ORDER BY id desc\n" +
                "LIMIT 1 );";
        updateRow(deleteLastEventSql);
    }


        public static void deleteLastReferences(){
        String deleteLastReferences = "DELETE FROM dcsa_im_v3_0.references \n" +
                "WHERE id in (\n" +
                "SELECT id \n" +
                "FROM dcsa_im_v3_0.references \n" +
                "ORDER BY id desc\n" +
                "LIMIT 1 );";
        updateRow(deleteLastReferences);
    }

    public static void deleteLastSeal(){
        String deleteLastSeal = "DELETE FROM dcsa_im_v3_0.seal \n" +
                "WHERE id in (\n" +
                "SELECT id \n" +
                "FROM dcsa_im_v3_0.seal \n" +
                "ORDER BY id desc\n" +
                "LIMIT 1 );";
        updateRow(deleteLastSeal);
    }

    public static String getLastTransportId() {
        String lastTransportIdQuery = "select id from transport offset ((select count(*) from transport)-1)";
        String transportId = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(lastTransportIdQuery);
            while (resultSet.next()) {
                transportId = resultSet.getString("id");
            }
        }catch (SQLException e){
            if(e.getMessage().contains("OFFSET must not be negative")){
                return  "";
            }else {
                throw new RuntimeException(e);
            }
        }
        return transportId;
    }
    public static String getLastTransportIdFromShipmentTransport() {
        String lastTransportIdQuery = "select transport_id from dcsa_im_v3_0.shipment_transport offset ((select count(*) from dcsa_im_v3_0.shipment_transport)-1)";
        String transportId = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(lastTransportIdQuery);
            while (resultSet.next()) {
                transportId = resultSet.getString("transport_id");
            }
        }catch (SQLException e){
            if(e.getMessage().contains("OFFSET must not be negative")){
                return  "";
            }else {
                throw new RuntimeException(e);
            }
        }
        return transportId;
    }

    public static void findDeleteShipmentTransportByTransportId(String transportId){
        if(!Objects.equals(transportId, "")){
            String dbTeansportId = "";
            String selectEquipmentEvent = "SELECT transport_id FROM dcsa_im_v3_0.shipment_transport where transport_id = " +
                    StringUtils.quote(transportId);
            try (Statement statement = AppProperty.getConnection().createStatement()) {
                ResultSet resultSet = statement.executeQuery(selectEquipmentEvent);
                while (resultSet.next()) {
                    dbTeansportId = resultSet.getString("transport_id");
                }
            }catch (SQLException e){
                throw new RuntimeException(e);
            }

            String deleteReferencesByShipmentId = "delete from dcsa_im_v3_0.shipment_transport where transport_id = " +
                    StringUtils.quote(dbTeansportId);
            if(Objects.equals(dbTeansportId, "")){
                deleteShipmentTransportByTransportId(List.of(getLastTransportIdFromShipmentTransport()));
            }else{
                updateRow(deleteReferencesByShipmentId);
            }
        }
    }

    public static void deleteShipmentTransportByTransportId(List<String> transportIds){
        transportIds.forEach(transportId -> {
            if(!Objects.equals(transportId, "")){
                String deleteShipmentTransport = "delete from dcsa_im_v3_0.shipment_transport where transport_id = " +
                        StringUtils.quote(transportId);
                updateRow(deleteShipmentTransport);

                String deleteTransportByTransportId = "delete from dcsa_im_v3_0.transport where id = " +
                        StringUtils.quote(transportId);
                updateRow(deleteTransportByTransportId);
            }
        });
    }

    public static String getLastVesselImo(){
        String selectLastVesselImo = "select * from vessel offset ((select count(*) from vessel)-1)";
        Vessel vessel = new Vessel();
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectLastVesselImo);
            while (resultSet.next()) {
                vessel.setVesselIMONumber(resultSet.getString("vessel_imo_number"));
                vessel.setVesselName(resultSet.getString("vessel_name"));
                vessel.setVesselFlag(resultSet.getString("vessel_flag"));
                vessel.setVesselCallSignNumber(resultSet.getString("vessel_call_sign_number"));
                vessel.setVesselOperatorCarrierID(UUID.fromString(resultSet.getString("vessel_operator_carrier_id")));
            }
        }catch (SQLException e){
            if(e.getMessage().contains("OFFSET must not be negative")){
                return  null;
            }else {
                throw new RuntimeException(e);
            }
        }
        return vessel.getVesselIMONumber();
    }

    public static void deleteTransportEventByTransportCallId(List<String> transportCallIds){
        transportCallIds.forEach(id -> {
            if(!Objects.equals(id, "")){
                String deleteTransportEventByTransportCallId = "delete from transport_event where transport_call_id = " +
                                                                StringUtils.quote(id);
                updateRow(deleteTransportEventByTransportCallId);
            }
        });
    }

    public static void deleteTransportByTransportCallId(List<String> transportCallIds){
        transportCallIds.forEach( id -> {
            if(!Objects.equals(id, "")) {
                selectTransportDeleteShipmentTransportIdByTransportCallId(Set.of(id));
                deleteTransportByTransportCallId(id);
                deleteOperationEventByTransportCallId(id);
                deleteTransportById(id);
            }
        });
    }

    public static List<String> selectTransportDeleteShipmentTransportIdByTransportCallId(Set<String> transportCallIds){
        List<String> transportIds = new ArrayList<>();
        transportCallIds.forEach( id -> {
            if(!Objects.equals(id, "")) {
                String selectTransportIdSql = "select id from dcsa_im_v3_0.transport where load_transport_call_id  = "
                        +StringUtils.quote(id)+" OR discharge_transport_call_id  = "+StringUtils.quote(id);

                try (Statement statement = AppProperty.getConnection().createStatement()) {
                    ResultSet resultSet = statement.executeQuery(selectTransportIdSql);
                    while (resultSet.next()) {
                        transportIds.add(resultSet.getString("id"));
                    }
                }catch (SQLException e){
                    if(e.getMessage().contains("OFFSET must not be negative")){
                        return;
                    }else {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        deleteShipmentTransportByTransportId(transportIds);
        return transportIds;
    }

    public static List<String> getAllTransportCallId(){
        List<String> transportCallId = new ArrayList<>();
        String selectAllTransportCallIds = "select id from dcsa_im_v3_0.transport_call where id != ''";
        try( Statement statement = AppProperty.getConnection().createStatement()){
            var resultSet = statement.executeQuery(selectAllTransportCallIds);
            while (resultSet.next()) {
                transportCallId.add(resultSet.getString("id"));
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return transportCallId;
    }
    public static void deleteOperationsEventEquipmentEventByTransportCallId(List<String> transportCallIds){
        transportCallIds.forEach(id -> {
            if(!Objects.equals(id, "")){
                String deleteOperationsEventByTransportCallId = "delete from  dcsa_im_v3_0.operations_event WHERE transport_call_id ="+
                        StringUtils.quote(id);
                updateRow(deleteOperationsEventByTransportCallId);

                String deleteEquipmentEventByTransportCallId = "delete from  dcsa_im_v3_0.equipment_event WHERE transport_call_id ="+
                        StringUtils.quote(id);
                updateRow(deleteEquipmentEventByTransportCallId);
            }
        });
    }

    public static void deleteTransportByTransportCallId(String id){
        if(!Objects.equals(id, "")){
            String deleteTransport = "DELETE FROM dcsa_im_v3_0.transport where load_transport_call_id  = "
                    +StringUtils.quote(id)+" OR discharge_transport_call_id  = "+StringUtils.quote(id);
            updateRow(deleteTransport);
        }
    }
    public static void deleteOperationEventByTransportCallId(String id){
        String deleteTransport = "DELETE FROM dcsa_im_v3_0.operations_event where transport_call_id = "+
                                StringUtils.quote(id);
        updateRow(deleteTransport);
    }

    public static void  deleteTransportById(String transportById){
        deleteEquipmentEventByTransportById(transportById);
        deleteTransportCallVoyageByTransportCallId(transportById);
        String deleteTransportById = "DELETE FROM dcsa_im_v3_0.transport_call where id = "+
                                        StringUtils.quote(transportById);
        updateRow(deleteTransportById);
        }

    public static void deleteTransportCallVoyageByTransportCallId(String transportCallId){
        String deleteTransportCallByImo = "DELETE FROM dcsa_im_v3_0.transport_call_voyage WHERE transport_call_id = "+
                StringUtils.quote(transportCallId);
        updateRow(deleteTransportCallByImo);
    }

    public static void deleteEquipmentEventByTransportById(String transportById){
        String deleteTransportById = "DELETE FROM dcsa_im_v3_0.equipment_event where transport_call_id = "+
                                    StringUtils.quote(transportById);
        updateRow(deleteTransportById);
    }

    public static String getLastTransportCallId() {
        String lastTransportCallId = "select id from transport_call offset ((select count(*) from transport_call)-1)";
        String transportCallId = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(lastTransportCallId);
            while (resultSet.next()) {
                transportCallId = resultSet.getString("id");
            }
        }catch (SQLException e){
            if(e.getMessage().contains("OFFSET must not be negative")){
               return  "";
            }else {
                throw new RuntimeException(e);
            }
        }
        return transportCallId;
    }
    public static String getLastFacilityId(){
        String selectLastVesselImo = "select id from dcsa_im_v3_0.facility offset ((select count(*) from dcsa_im_v3_0.facility)-1)";
        String lastFacilityId = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectLastVesselImo);
            while (resultSet.next()) {
                lastFacilityId = resultSet.getString("id");
            }
        }catch (SQLException e){
            if(e.getMessage().contains("OFFSET must not be negative")){
                return  "";
            }else {
                throw new RuntimeException(e);
            }
        }
        return lastFacilityId;
    }

    public static void deleteShipmentEventByEventId(String eventId){
        String deleteTransportEventSql = "DELETE FROM dcsa_im_v3_0.shipment_event WHERE event_id = "+
                StringUtils.quote(eventId);
        SqlUtility.updateRow(deleteTransportEventSql);
    }

    public static void deleteTableAllRow(String tableName, String columnName, int count){
        String deleteTableAllRow ="DELETE FROM dcsa_im_v3_0."+tableName+"\n" +
                "WHERE "+columnName+" in(\n"+
                "SELECT "+columnName+"\n"+
                "FROM dcsa_im_v3_0."+tableName+"\n"+
                "ORDER BY "+columnName+" desc\n"+
                "LIMIT "+count+");";
        updateRow(deleteTableAllRow);
    }
    public static int getTableRowCount(String tableName){
        String tableCountSql = "SELECT COUNT(*) FROM dcsa_im_v3_0."+tableName;
        int count = 0;
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(tableCountSql);
            while (resultSet.next()) {
                 count = resultSet.getInt("count");
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return count;
    }

    public static void removeAllEventWithEventId(String tableName){
        int count = SqlUtility.getTableRowCount(tableName);
        SqlUtility.deleteTableAllRow(tableName, "event_id", count);
    }

    public static void removeAllEventId(String tableName){
        int count = SqlUtility.getTableRowCount(tableName);
        SqlUtility.deleteTableAllRow(tableName, "id", count);
    }

    public static String getShipmentIdByReferenceType(String ReferenceType){
        String selectShipmentId = "select shipment_id from dcsa_im_v3_0.references where reference_type = "+
                                        StringUtils.quote(ReferenceType);
        String shipmentId = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectShipmentId);
            while (resultSet.next()) {
                shipmentId = resultSet.getString("shipment_id");
            }
        }catch (SQLException e){
            if(e.getMessage().contains("OFFSET must not be negative")){
                return  null;
            }else {
                throw new RuntimeException(e);
            }
        }
        return shipmentId;
    }

    public static boolean isShipmentExist(String shipmentId){
        String selectShipmentId = "select id from dcsa_im_v3_0.shipment where id = "+
                StringUtils.quote(shipmentId);
        String selectedShipmentId = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectShipmentId);
            while (resultSet.next()) {
                selectedShipmentId = resultSet.getString("id");
            }
        }catch (SQLException e){
            if(e.getMessage().contains("OFFSET must not be negative")){
                return false;
            }else {
                throw new RuntimeException(e);
            }
        }
        if(selectedShipmentId.length() > 0){
            return true;
        }else {
            return false;
        }
    }

    public static boolean isReferenceExist(String shipmentId){
        String selectShipmentId = "select shipment_id from dcsa_im_v3_0.references where shipment_id = "+
                StringUtils.quote(shipmentId);
        String selectedShipmentId = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectShipmentId);
            while (resultSet.next()) {
                selectedShipmentId = resultSet.getString("shipment_id");
            }
        }catch (SQLException e){
            if(e.getMessage().contains("OFFSET must not be negative")){
                return false;
            }else {
                throw new RuntimeException(e);
            }
        }
        if(selectedShipmentId.length() > 0){
            return true;
        }else {
            return false;
        }
    }

    public static boolean isShipmentEquipmentExist(String shipmentEquipmentEd){
        String selectShipmentId = "select shipment_equipment_id from dcsa_im_v3_0.shipment_equipment where shipment_equipment_id = "+
                StringUtils.quote(shipmentEquipmentEd);
        String selectedShipmentEquipmentEd = "";
        try (Statement statement = AppProperty.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(selectShipmentId);
            while (resultSet.next()) {
                selectedShipmentEquipmentEd = resultSet.getString("shipment_equipment_id");
            }
        }catch (SQLException e){
            if(e.getMessage().contains("OFFSET must not be negative")){
                return false;
            }else {
                throw new RuntimeException(e);
            }
        }
        if(selectedShipmentEquipmentEd.length() > 0){
            return true;
        }else {
            return false;
        }
    }
}