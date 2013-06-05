package com.silverchalice.json;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonAccessor {

	public JsonAccessor() {
		super();
	}

	//--------------------------------------------------------------------------
	//
	// Public API
	//
	//--------------------------------------------------------------------------	
	
	/**  @see #getKeyPathRecursively(String, Object) */
	public Object getKeyPath(String keyPath, JSONObject jsonObject) {
		return getKeyPath(keyPath, jsonObject);
	}
	/**  @see #getKeyPathRecursively(String, Object) */
	public Object getKeyPath(String keyPath, JSONArray jsonArray) {
		return getKeyPath(keyPath, jsonArray);
	}
	/**  @see #getKeyPathRecursively(String, Object) */
	public Object getKeyPath(String keyPath, JSONObject json, Object defaultValue) {
		Object result = getKeyPathRecursively(keyPath, json);
		return result == null ? defaultValue : result;
	}
	/**  @see #getKeyPathRecursively(String, Object) */
	public Object getKeyPath(String keyPath, JSONArray json, Object defaultValue) {
		Object result = getKeyPathRecursively(keyPath, json);
		return result == null ? defaultValue : result;
	}
	/**  @see #getKeyPathRecursively(String, Object) */
	@SuppressWarnings("unchecked")
	public <T> T getKeyPath(String keyPath, JSONObject json, Class<T> type) {
		Object value = getKeyPathRecursively(keyPath, json);
		if(type.isInstance(value)) return (T) value;
		else return coerceValue(value, type);
	}
	/**  @see #getKeyPathRecursively(String, Object) */
	@SuppressWarnings("unchecked")
	public <T> T getKeyPath(String keyPath, JSONArray json, Class<T> type) {
		Object value = getKeyPathRecursively(keyPath, json);
		if(type.isInstance(value)) return (T) value;
		else return coerceValue(value, type);
	}
	
	//--------------------------------------------------------------------------
	//
	// Implementation
	//
	//--------------------------------------------------------------------------	
	
	/**
	 * Recursive function to get a key string from a json object. Allows key
	 * strings to be expressed as:<br/>
	 * <br/>
	 * <code>event.details.gameclock</code><br/>
	 * <br/>
	 * OR<br/>
	 * <br/>
	 * <code>newsStories[0].assets[0].links[3].url</code>
	 */
	protected Object getKeyPathRecursively(String keyPath, Object jsonObjectOrArray) {
		if(keyPath == null || jsonObjectOrArray == null) return null;
		//chop off the first part of the key, treating multiple delimiters as one -kg
		String[] keys = keyPath.split("[.\\[\\]]+", 2);
		//if no more keys, return final result -kg
		if(keys.length == 1) return accessKey(keys[0], jsonObjectOrArray);
		//else, recurse -kg
		return getKeyPathRecursively(keys[1], accessKey(keys[0], jsonObjectOrArray));
	}

	@SuppressWarnings("unchecked")
	protected <T> T coerceValue(Object value, Class<T> type) {
		if(value == null) return (T) null;
		/* by definition, 'value' is not an instance of 'type' */
		try {
			if(value instanceof String) {
				if(type.equals(Integer.class)) {
					return (T) Integer.valueOf(Integer.parseInt((String) value));
				} else if(type.equals(Boolean.class)) {
					return (T) Boolean.valueOf(Boolean.parseBoolean((String) value));
				}
			}
		} catch(Throwable cause) {
			throw new IllegalArgumentException("Unable to coerce value from " + value.getClass().getSimpleName() + " to " + type.getSimpleName(), cause);
		}
		throw new IllegalArgumentException("Unable to coerce value from " + value.getClass().getSimpleName() + " to " + type.getSimpleName() + ". Most likely, this object is not one of the supported types that we are able to coerce.");
	}
	
	/**
	 * Accesses the key in the nextJson, behaving as expected depending on
	 * whether <code>nextJson</code> is a JSONObject or JSONArray. When nextJson
	 * is a JSONArray, it's expected that the key is a digit.
	 */
	protected Object accessKey(String key, Object nextJson) {
		if(key == null || key.length() == 0) {
			return nextJson;
		} else if(nextJson instanceof JSONArray) {
			return getArrayIndex(key, (JSONArray) nextJson);
		} else if(nextJson instanceof JSONObject) {
			return ((JSONObject) nextJson).opt(key);
		} else {
			throw new IllegalArgumentException("ERROR [JsonAccessor] : object must be of type JSONObject or JSONArray to access. Instead this object was of type " + nextJson.getClass().getSimpleName());
		}
	}

	protected Object getArrayIndex(String index, JSONArray nextJson) {
		if(index.trim().matches("\\d+")) { 
			int i = Integer.parseInt(index);
			if(i > nextJson.length()) throw new ArrayIndexOutOfBoundsException("ERROR [JsonAccessor] : Invalid index: " + i + "\n" + nextJson);
			return nextJson.opt(i);
		} else { //TODO: consider adding list functionality. If given a keystring for an array, we could return a list. Ex: "newsStories.assets.links.url" could iterate over all assets and all links and return all urls -kg
			throw new IllegalArgumentException("ERROR [JsonAccessor] : tried to index into an array but did not provide a proper number: " + index);
		}
	}

}
