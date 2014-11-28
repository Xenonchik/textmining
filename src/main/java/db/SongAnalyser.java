package db;

import java.net.UnknownHostException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import com.mongodb.*;

/**
 * Blahblahblah
 *
 * @author Replicating Rat
 * @version 3.6.7
 * @since 3.6.7
 */
public class SongAnalyser {

	MongoClient mongoClient;
	DB db;
	DBCollection coll;

	public SongAnalyser() throws UnknownHostException {
		mongoClient = new MongoClient("localhost", 27017);
		db = mongoClient.getDB("testmining");
		coll = db.getCollection("songs");
	}

	public void countWords() throws Exception {
		System.out.println(coll.count());
		DBCursor curr = coll.find(new BasicDBObject());
		while (curr.hasNext()) {
			DBObject song = curr.next();
			String text = (String) song.get("text");
			if(text != null && text != "") {
				Map<String, Integer> wordsCount = new LinkedHashMap<>();
				Scanner scan = new Scanner(text);
				while (scan.hasNext()) {
					String word = scan.next().toLowerCase();
					word = word.replace(".", "").replace(",", "").replace("!", "").replace("?", "");
					if (wordsCount.containsKey(word)) {
						wordsCount.put(word, wordsCount.get(word) + 1);
					} else wordsCount.put(word, 1);
				}
				BasicDBObject updSong = new BasicDBObject();
				BasicDBObject obj = new BasicDBObject();
				obj.put("wordsCount", new BasicDBObject(wordsCount));
				updSong.append("$set", obj);

				BasicDBObject searchQuery = new BasicDBObject().append("name", song.get("name"));
				coll.update(searchQuery, updSong);
			}
		}
		System.out.println(coll.count());
	}

	public Map<String, Integer> getWordsCount() {
		Map<String, Integer> totalWordsCount = new LinkedHashMap<>();
		BasicDBObject query = new BasicDBObject("wordsCount", new BasicDBObject("$exists", true));
		DBCursor cursor = coll.find(query);
		while (cursor.hasNext()) {
			Map<String, Integer> wordsCount = (Map<String, Integer>) cursor.next().get("wordsCount");
			for(Entry<String, Integer> entry : wordsCount.entrySet()) {
				if (totalWordsCount.containsKey(entry.getKey())) {
					totalWordsCount.put(entry.getKey(), totalWordsCount.get(entry.getKey()) + entry.getValue());
				} else totalWordsCount.put(entry.getKey(), entry.getValue());
			}
		}

		return totalWordsCount;
	}
}
