/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.maps.client.geom.LatLng;


public class ItemLight {

	private String title = null;
	private String id = null;
	private LatLng location;

	public ItemLight(String title, String id, LatLng location) {
		this.title = title;
		this.id = id;
		this.location = location;
	}

	// TODO Be careful when parsing JSON because it uses javascript eval().
	// Escape text or something.
	// Maybe server side escape characters like double quotes or something.
	public ItemLight(JSONObject item) {
		this.id = item.get("id").isString().stringValue();
		this.title = item.get("title").isString().stringValue();
		this.location = LatLng.newInstance(item.get("lat").isNumber()
				.doubleValue(), item.get("lon").isNumber().doubleValue());
	}

	public static JSONObject findItem(String key, JSONArray items) {
		for (int j = 0; j < items.size(); j++) {
			if (key.equals(items.get(j).isObject().get("key").isNumber()
					.doubleValue()
					+ "")) {
				return items.get(j).isObject();
			}
		}
		return null;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}

	public String getShortenedTitle() {
		String result = this.title;
		if (result.length() >= 19) {
			result = result.substring(0, 18) + "...";
		}
		return result;
	}

	public LatLng getLocation() {
		return this.location;
	}

	public String getID() {
		return this.id;
	}
}