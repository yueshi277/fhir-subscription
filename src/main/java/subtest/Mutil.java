package subtest;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import java.util.ArrayList;
import static com.mongodb.client.model.Filters.eq;

public class Mutil {

    MongoCollection<Document> collection;
    MongoCollection<Document> cdrCollection;

    public Mutil() {
        MongoClientURI mongoClientURI = new MongoClientURI("mongodb+srv://smileqa:smile123@cluster0.w2c1w.mongodb.net/subscription?retryWrites=true&w=majority");
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        MongoDatabase database = mongoClient.getDatabase("subscription");
        collection = database.getCollection("sub");
    }

    public Document writeDocument(Document doc) {
        collection.insertOne(doc);
        return doc;
    }

    public String findById(String id) {
        Document doc = collection.find(eq("id", id)).first();
        if (doc != null) {
            doc.remove("_id");
            return doc.toJson();
        } else {
            return "";
        }
    }

    public ArrayList<String> findAll(String resourceType) {
        final FindIterable<Document> results;
        if (!resourceType.isEmpty()) {
            BasicDBObject query = new BasicDBObject();
            query.put("resourceType", resourceType);
            results = collection.find(query).sort(Sorts.descending("$natural"));
        } else {
            results = collection.find().sort(Sorts.descending("$natural"));
        }
        ArrayList<String> output = new ArrayList<String>();
        for (final Document doc : results) {
            doc.remove("_id");
            output.add(doc.toJson());
        }
        return output;
    }

    public void deleteAll() {
        collection.deleteMany(new Document());
    }

    public void deleteCdrResource(String collection) {
        MongoClientURI mongoClientURI = new MongoClientURI("mongodb+srv://smileqa:smile123@cluster0.w2c1w.mongodb.net/cdr?retryWrites=true&w=majority");
        MongoClient mongoClient = new MongoClient(mongoClientURI);
        MongoDatabase database = mongoClient.getDatabase("cdr");
        cdrCollection = database.getCollection(collection);
        cdrCollection.deleteMany(new Document());
    }

}
