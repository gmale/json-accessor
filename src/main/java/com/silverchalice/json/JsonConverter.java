package com.silverchalice.json;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class JsonConverter {
	/* Gson is a final class so we'll use composition here,
	 * although I'd rather use inheritance, in this case :( -kg */
	protected Gson gson;
	protected JsonAccessor jsonAccessor;

	public Gson getGson() {
		if(gson == null) gson = new Gson();
		return gson;
	}

	public JsonAccessor getAccessor() {
		if(jsonAccessor == null) jsonAccessor = new JsonAccessor();
		return jsonAccessor;
	}
	
	public <T> T fromUrl(String url, Class<T> classRepresentedByJson)
			throws MalformedURLException, IOException {
		String json = getUrlAsString(url);
		return getGson().fromJson(json, classRepresentedByJson);
	}
	
	/**
	 * Given a url, extract the given keyPath and then convert it to the 
	 * specified type.
	 * 
	 * @param url the url to use. Any protocol supported by java.net.URL should work. Only HTTP: and FILE: have been tested.
	 * @param keyPath keyPath to extract from the JSON. Ex: news.stories[0]
	 * @param classRepresentedByJson class to which the json will be converted.
	 * @return
	 */
	public <T> T fromUrl(String url, String keyPath, Class<T> classRepresentedByJson)
			throws MalformedURLException, IOException, JSONException {
		String json = getUrlAsString(url);
		Object result = getAccessor().getKeyPath(keyPath, new JSONObject(json));
		if(result == null) return null;
		return getGson().fromJson(result.toString(), classRepresentedByJson);
	}
	
	/**
	 * Given a url, extract the given keyPath and then convert it to a list of 
	 * the specified type. In this case, the keyPath must reference a json array.
	 * 
	 * @param url the url to use. Any protocol supported by java.net.URL should work. Only HTTP: and FILE: have been tested.
	 * @param keyPath keyPath to extract from the JSON. Ex: news.stories. The keyPath must reference a json array.
	 * @param classRepresentedByJson class that will be returned as a list
	 * @return list of type 'classRepresentedByJson' representing the json array found at the specified keyPath
	 */
	public <T> List<T> fromUrlAsList(String url, String keyPath, Class<T> classRepresentedByJson)
			throws MalformedURLException, IOException, JSONException {
		String json = getUrlAsString(url);
		JSONArray array = getAccessor().getKeyPath(keyPath, new JSONObject(json), JSONArray.class);
		if(array == null) return null;
		
		List<T> finalResults = new ArrayList<T>(array.length());
		Object nextJsonArrayItem;
		for (int i = 0; i < array.length(); i++) {
			nextJsonArrayItem = array.get(i);
			if(nextJsonArrayItem != null) {
				T nextResult = getGson().fromJson(nextJsonArrayItem.toString(), classRepresentedByJson);
				finalResults.add(nextResult);
			}
		}
		
		return finalResults;
	}
	
	public <T> T fromUrlWithJsonArray(String url, String keyPath, Class<T> classRepresentedByJson)
			throws MalformedURLException, IOException, JSONException {
		String json = getUrlAsString(url);
		Object result = getAccessor().getKeyPath(keyPath, new JSONArray(json));
		return getGson().fromJson(result.toString(), classRepresentedByJson);
	}

	/* TODO: move this to a networking utility class -kg */
	protected String getUrlAsString(String address)
			throws MalformedURLException, IOException {
		String content = null;
		Scanner scanner = null;
		try {
			scanner = new Scanner(new URL(address).openStream(), "UTF-8");
			content = scanner.useDelimiter("\\A").next(); //regex for beginning of input: \A -kg
		} finally {
			if(scanner != null) scanner.close();
		}
		return content;
	}
	
}
