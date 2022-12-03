import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.*;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Map;

public class ConnectToDB {
    public static String uri1 = "mongodb+srv://admin-shreyash:DClab123@cluster0.0zevofn.mongodb.net/?retryWrites=true&w=majority";
    public static String uri2 = "mongodb+srv://shreyash:DClab123@cluster0.qzjw15o.mongodb.net/?retryWrites=true&w=majority";
    public static String uri3 = "mongodb+srv://shreyash:DClab123@cluster0.stypfsv.mongodb.net/?retryWrites=true&w=majority";
    public void addUidDB(int uid){
        try (MongoClient mongoClient1 = MongoClients.create(uri1)) {

            MongoDatabase database1 = mongoClient1.getDatabase("students-data");

            MongoCollection<Document> collection1 = database1.getCollection("student-uids");

            collection1.insertOne(new Document("uid", String.valueOf(uid)));
        }catch(Exception e){
            System.out.println("error whil writing to database 1");
        }
        try (MongoClient mongoClient2 = MongoClients.create(uri2)) {

            MongoDatabase database2 = mongoClient2.getDatabase("students-data");

            MongoCollection<Document> collection2 = database2.getCollection("student-uids");

            collection2.insertOne(new Document("uid", String.valueOf(uid)));
        }catch(Exception e){
            System.out.println("error whil writing to database 2");
        }
        try (MongoClient mongoClient3 = MongoClients.create(uri3)) {

            MongoDatabase database3 = mongoClient3.getDatabase("students-data");

            MongoCollection<Document> collection3 = database3.getCollection("student-uids");

            collection3.insertOne(new Document("uid", String.valueOf(uid)));
        }catch(Exception e){
            System.out.println("error whil writing to database 3");
        }

    }
    public ArrayList<String> getAllFromDB1(){
        ArrayList<String> arrayString = new ArrayList<>();
        try (MongoClient mongoClient1 = MongoClients.create(uri1)) {
//            this.mongoClient1 = mongoClient1;
            MongoDatabase database1 = mongoClient1.getDatabase("students-data");
//            this.database1 = database1;
            MongoCollection<Document> collection1 = database1.getCollection("student-uids");
//            this.collection1 = collection1;
            Bson projectionFields = Projections.fields(
                    Projections.include("uid"),
                    Projections.excludeId());
            MongoCursor<Document> cursor = collection1.find().projection(projectionFields).iterator();
//            JsonParser parser = new JsonParser();
            ObjectMapper mapper = new ObjectMapper();
            try{
                while(cursor.hasNext()) {
                    String json = cursor.next().toJson().toString();
                    Map<String,Object> map = mapper.readValue(json, Map.class);
                    arrayString.add(map.toString());
                }

            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } finally {
                cursor.close();
            }

        }
        return arrayString;
    }

    public ArrayList<String> getAllFromDB2(){
        ArrayList<String> arrayString = new ArrayList<>();
        try (MongoClient mongoClient2 = MongoClients.create(uri2)) {
//            this.mongoClient1 = mongoClient1;
            MongoDatabase database2 = mongoClient2.getDatabase("students-data");
//            this.database1 = database1;
            MongoCollection<Document> collection2 = database2.getCollection("student-uids");
//            this.collection1 = collection1;
            Bson projectionFields = Projections.fields(
                    Projections.include("uid"),
                    Projections.excludeId());
            MongoCursor<Document> cursor = collection2.find().projection(projectionFields).iterator();
//            JsonParser parser = new JsonParser();
            ObjectMapper mapper = new ObjectMapper();
            try{
                while(cursor.hasNext()) {
                    String json = cursor.next().toJson().toString();
                    Map<String,Object> map = mapper.readValue(json, Map.class);
                    arrayString.add(map.toString());
                }

            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } finally {
                cursor.close();
            }

        }
        return arrayString;
    }

    public ArrayList<String> getAllFromDB3(){
        ArrayList<String> arrayString = new ArrayList<>();
        try (MongoClient mongoClient3 = MongoClients.create(uri3)) {
//            this.mongoClient1 = mongoClient1;
            MongoDatabase database3 = mongoClient3.getDatabase("students-data");
//            this.database1 = database1;
            MongoCollection<Document> collection3 = database3.getCollection("student-uids");
//            this.collection1 = collection1;
            Bson projectionFields = Projections.fields(
                    Projections.include("uid"),
                    Projections.excludeId());
            MongoCursor<Document> cursor = collection3.find().projection(projectionFields).iterator();
//            JsonParser parser = new JsonParser();
            ObjectMapper mapper = new ObjectMapper();
            try{
                while(cursor.hasNext()) {
                    String json = cursor.next().toJson().toString();
                    Map<String,Object> map = mapper.readValue(json, Map.class);
                    arrayString.add(map.toString());
                }

            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } finally {
                cursor.close();
            }

        }
        return arrayString;
    }

    public Boolean write(int uid){
        try{
            this.addUidDB(uid);
            System.out.println("\nWriting Data to Database...\nData Written is : " + uid);
        }catch(Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

//    public static void main( String[] args ) {
//
//        ConnectToDB connectToDB = new ConnectToDB();
//        connectToDB.write(2020300001);
//        System.out.println("databse1");
//        ArrayList<String> arrayList = connectToDB.getAllFromDB1();
//        for(int i = 0 ; i < arrayList.size() ; i++){
//            System.out.println(arrayList.get(i) + " ");
//        }
//        System.out.println("databse2");
//        ArrayList<String> arrayList2 = connectToDB.getAllFromDB2();
//        for(int i = 0 ; i < arrayList2.size() ; i++){
//            System.out.println(arrayList2.get(i) + " ");
//        }
//        System.out.println("databse3");
//        ArrayList<String> arrayList3 = connectToDB.getAllFromDB3();
//        for(int i = 0 ; i < arrayList3.size() ; i++){
//            System.out.println(arrayList3.get(i) + " ");
//        }
//
//    }
}
