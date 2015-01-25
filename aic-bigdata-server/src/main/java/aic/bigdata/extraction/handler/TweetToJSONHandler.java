package aic.bigdata.extraction.handler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.Status;
import aic.bigdata.extraction.TweetHandler;

public class TweetToJSONHandler implements TweetHandler {

	private String path;
	private JSONArray array = new JSONArray();
	private int counter = 0;
	private int page = 0;

	public TweetToJSONHandler(String path) {
		this.path = path;
	}

	@Override
	public void HandleStatusTweet(Status status, String tweet) {
		if (counter > 100) {
			PrintStream out;
			try {
				String file = path + "_" + page + ".json";
				Path p = java.nio.file.Paths.get(file);
				while (java.nio.file.Files.exists(p)) {
					page++;
					file = path + "_" + page + ".json";
					p = java.nio.file.Paths.get(file);
				}

				out = new PrintStream(new FileOutputStream(file));
				out.print(array.toString());
				System.out.println("write to file: " + file);
				array = new JSONArray();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			counter = 0;
			page++;
		}

		JSONObject obj;
		try {
			obj = new JSONObject(tweet);
			array.put(obj);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		counter++;
		System.out.println(tweet);
	}

	@Override
	public void HandleTweet(String tweet) {
	}

}
