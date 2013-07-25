package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;

import play.libs.Json;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class LoadData {

	private static final String DATA = "test/data/db.json";

	/**
	 * Drops the database and populates it with test data.
	 */
	public static void load() {
		try {
			TestConnection.dropDatabase();

			// read JSON file
			StringBuilder sb = new StringBuilder();
			try (BufferedReader br = new BufferedReader(new FileReader(DATA))) {
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
			}

			// parse and insert into database
			JsonNode node = Json.parse(sb.toString());
			Iterator<Entry<String, JsonNode>> collections = node.getFields();
			while (collections.hasNext()) {
				Entry<String, JsonNode> curColl = collections.next();
				DBCollection collection = TestConnection.getCollection(curColl.getKey());
				Iterator<JsonNode> documents = curColl.getValue().getElements();
				while (documents.hasNext()) {
					JsonNode curDoc = documents.next();
					collection.insert((DBObject) JSON.parse(curDoc.toString()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
