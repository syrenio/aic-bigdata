package aic.bigdata.database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;
import twitter4j.User;

import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class MongoDatabaseHelper {

	Gson g = new Gson();

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

	public User convertToUser(DBObject o) throws TwitterException {
		User usr = null;
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss a");
		if (o.containsField("followersCount") || o.containsField("statusesCount")) {
			DBObject nobj = new BasicDBObject();
			for (String key : o.keySet()) {
				String nkey = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, key);
				nobj.put(nkey, o.get(key));
			}
			nobj.removeField("created_at"); // FIXME erzeugt einen parsing
			if (nobj.containsField("created_at")) {
				String str = (String) nobj.get("created_at");
				try {
					Date d = formatter.parse(str);
					nobj.put("created_at", d.getTime());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}

			usr = TwitterObjectFactory.createUser(nobj.toString());
		} else {
			usr = TwitterObjectFactory.createUser(o.toString());
		}

		return usr;
	}

}
