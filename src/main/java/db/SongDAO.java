package db;

import com.mongodb.*;

import java.net.UnknownHostException;

/**
 * Created by xenon on 12/15/14.
 */
public class SongDAO {

    MongoClient mongoClient;
    DB db;
    DBCollection coll;

    public SongDAO() {
        try {
            mongoClient = new MongoClient("localhost", 27017);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        db = mongoClient.getDB("testmining");
        coll = db.getCollection("songs");
    }

    public void saveSong(BasicDBObject song){
        coll.insert(song);
    }

    public DBCursor getWordsCounts() {
        BasicDBObject query = new BasicDBObject("wordsCount", new BasicDBObject("$exists", true));
        DBCursor cursor = coll.find(query);
        return cursor;
    }

    public DBCursor getSongs() {
        return coll.find();
    }
}
