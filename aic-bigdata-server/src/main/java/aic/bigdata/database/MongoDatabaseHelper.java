package aic.bigdata.database;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoDatabaseHelper {

	public void createIndex(String name, DBCollection col, int order) {
		String indexName = name + "_idx";
		DBObject idx = new BasicDBObject(name, order);
		DBObject opt = new BasicDBObject();
		opt.put("name", indexName);
		if (!checkIndexExists(indexName, col)) {
			col.createIndex(idx, opt);
		}
	}

	public void createUniqueIndex(String name, DBCollection col) {
		String indexName = "uq_" + name + "_idx";
		DBObject idx = new BasicDBObject(name, 1);
		DBObject opt = new BasicDBObject("unique", true);
		opt.put("name", indexName);
		// index exists
		if (!checkIndexExists(indexName, col)) {
			col.createIndex(idx, opt);
		}
	}

	private boolean checkIndexExists(String name, DBCollection col) {
		List<DBObject> list = col.getIndexInfo();
		for (DBObject i : list) {
			if (i.get("name").equals(name)) {
				return true;
			}
		}
		return false;
	}
}
