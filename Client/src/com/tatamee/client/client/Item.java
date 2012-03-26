/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.maps.client.geom.LatLng;

public class Item {
	private static int thumbnailReload = 0;
	private String title = null;
	private String currency = "";
	private String id = null;
	private String category = null;
	private String tags = null;
	private double price;
	private String description;
	private String features = "";
	private String daysRemaining;
	private String owner;
	private LatLng location;
	private int comments;
	boolean anonymous = false;
	private int nPictures = 0;
	private String seller;
	private int goodRatings;
	private int badRatings;
	private String nickname;
	private String userId;

	// Date added
	public Item() {
		;
	}

	public Item(String title, String id, String category, double price,
			String description, String features) {
		this.title = title;
		this.id = id;
		this.category = category;
		this.price = price;
		this.description = description;
		this.features = features;
		// this.addedDaysAgo;
		// this.owner;
	}

	// TODO Be careful when parsing JSON because it uses javascript eval().
	// Escape text or something.
	// Maybe server side escape characters like double quotes or something.
	public Item(JSONObject item) {
		this.id = item.get("id").isString().stringValue();
		this.title = item.get("title").isString().stringValue();
		this.category = item.get("category").isString().stringValue();
		this.tags = item.get("tags").isString().stringValue();
		this.price = (double) item.get("price").isNumber().doubleValue();
		this.description = item.get("description").isString().stringValue();
		this.daysRemaining = item.get("days_remaining").isString().stringValue();
		this.nPictures = (int) item.get("n_pictures").isNumber().doubleValue();
		this.comments = Integer.parseInt(item.get("comments").isString().stringValue());
		this.seller = item.get("seller").isString().stringValue();
		this.goodRatings = (int) item.get("good_ratings").isNumber().doubleValue();
		this.badRatings = (int) item.get("bad_ratings").isNumber().doubleValue();
		this.location = LatLng.newInstance(item.get("lat").isNumber()
				.doubleValue(), item.get("lon").isNumber().doubleValue());
		this.nickname = item.get("nickname").isString().stringValue();
		this.userId = item.get("user_id").isString().stringValue();
		this.currency = item.get("currency").isString().stringValue();
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

	public void setAnonymous(boolean _anonymous_) {
		anonymous = _anonymous_;
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

	public String getID() {
		return this.id;
	}
	
	public int getNPictures() {
		return this.nPictures;
	}
	
	public String getCurrency() {
		return this.currency;
	}

	public String getCategory() {
		return this.category;
	}
	
	public String getTags() {
		return this.tags;
	}

	public String getThumbnail() {
		if (this.getNPictures() == 0) {
			return "images/x.jpg"; 
		} else {
			return "/image?id=" + this.id + "&get=default_thumbnail" + "#" + thumbnailReload++;
		}
	}

	public String getDescription() {
		return description;
	}

	public double getPrice() {
		return price;
	}

	public LatLng getLocation() {
		return this.location;
	}

	public String getDaysRemaining() {
		return daysRemaining;
	}

	// Not very anonymous, user could be found inspecting request??
	public String getOwner() {
		return this.seller;
	}

	/*
	 * TODO Should be get from server from user
	 */
	public int getGoodRatings() {
		return this.goodRatings;
	}

	/*
	 * TODO Should be get from server from user
	 */
	public int getBadRatings() {
		return this.badRatings;
	}

	public String toJSON() {
		JSONObject object = new JSONObject();
		object.put("title", new JSONString(this.title));
		object.put("categories", new JSONString(this.category));
		object.put("price", new JSONNumber(this.price));
		object.put("id", new JSONString(this.id));
		object.put("description", new JSONString(this.description));
		JSONObject featuresJSON = new JSONObject();
//		Iterator<Entry<String, String>> features = this.getFeatures()
//				.entrySet().iterator();
//		while (features.hasNext()) {
//			Entry<String, String> current = features.next();
//			featuresJSON.put(current.getKey(), new JSONString(current
//					.getValue()));
//		}
//		object.put("features", featuresJSON);
		return object.toString();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setNPictures(int nPictures) {
		this.nPictures = nPictures;
	}

	public int getComments() {
		return this.comments;
	}

	public void incrementComments() {
		this.comments++;
	}
	
	public String getNickname() {
		return this.nickname;
	}

	public String getUserID() {
		return this.userId;
	}
}
