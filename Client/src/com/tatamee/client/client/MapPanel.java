/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.cobogw.gwt.user.client.ui.Button;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.control.MapTypeControl;
import com.google.gwt.maps.client.control.SmallZoomControl;
import com.google.gwt.maps.client.event.MapClearOverlaysHandler;
import com.google.gwt.maps.client.event.MapDragEndHandler;
import com.google.gwt.maps.client.event.MapZoomEndHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MapPanel extends Composite {
	// TODO set map's background color to tatamee's background color through
	// MapOptions.setBackgroundcolor
	public static MapWidget map = new MapWidget();
	private static VerticalPanel locationPopup = new VerticalPanel();
	private static TextBox locationTextBox = null;
	private static HashSet<String> areasShowing = new HashSet<String>();

	public MapPanel() {
		Maps
				.loadMapsApi(
						"ABQIAAAAf6ZlGjQ4-Q3GRlW2xkuJKRSfSgbJTj69hENLBg-AzTXfeK-ONBRFcl6-OHjEDhJ6RzmySQ5AbBC36Q",
						"2.x", true, new Runnable() {
							public void run() {
								// Try
								// 0) Loading an item's URL
								if (ClientApplication.mode == 2) {
									map.setCenter(ClientApplication.itemPanel.loadedItem.getLocation(), 14);
									Marker item = new Marker(ClientApplication.itemPanel.loadedItem.getLocation());
									map.addOverlay(item);
								// 1) Registered user's location (if it's loaded yet)
								} else if (FindHomePopup.getHomeLocation() != null) {
									MapPanel.map.setCenter(FindHomePopup
											.getHomeLocation(), 11);
								// 2) Cookies	
								} else if (Cookies.getCookie("lat") != null) {
									LatLng location = LatLng.newInstance(Double
											.parseDouble(Cookies
													.getCookie("lat")), Double
											.parseDouble(Cookies
													.getCookie("lon")));
									map.setCenter(location, 12);
								// 3) Maps API Location	
								} else if (getClientLatitude() != 0) {
									map.setCenter(getLocation(), 12);
								// 4) Ask User (and store as cookie)	
								} else {
									MapPanel
											.askLocation(ClientApplication.constants
													.anonymous());
								}

								if (!FindHomePopup.isCentered) {
									FindHomePopup.setCenter(MapPanel.map
											.getCenter(), 11);
									FindHomePopup.isCentered = true;
								}
							}
						});

		map.addControl(new SmallZoomControl());
		// TODO Change small zoom control for LargeMapControl?
		map.addControl(new MapTypeControl(true));

		map.addMapDragEndHandler(new MapDragEndHandler() {
			public void onDragEnd(MapDragEndEvent event) {
				if (!isShowing() && ClientApplication.mode != 2) {
					updateMap();
				}
			}
		});

		map.addMapZoomEndHandler(new MapZoomEndHandler() {
			public void onZoomEnd(MapZoomEndEvent event) {
				if (!isShowing() && ClientApplication.mode != 2) {
					updateMap();
				}
			}
		});

		map.addMapClearOverlaysHandler(new MapClearOverlaysHandler() {
			public void onClearOverlays(MapClearOverlaysEvent event) {
				if (FindHomePopup.getHomeLocation() != null) {
					Marker home = new Marker(FindHomePopup.getHomeLocation(),
							MarkerOptions.newInstance(makeHomeIcon()));
					map.addOverlay(home);
				}
			}
		});

		map.setSize("360px", "360px");
		initWidget(map);
	}

	private boolean isShowing() {
		LatLngBounds bounds = map.getBounds();
		if (areasShowing.contains(hashArea(bounds.getNorthEast()))
				&& areasShowing.contains(hashArea(bounds.getSouthWest()))
				&& areasShowing.contains(hashArea(LatLng.newInstance(bounds
						.getSouthWest().getLatitude(), bounds.getNorthEast()
						.getLongitude())))
				&& areasShowing.contains(hashArea(LatLng.newInstance(bounds
						.getSouthWest().getLongitude(), bounds.getNorthEast()
						.getLatitude())))) {
			return true;
		}
		return false;
	}

	private static String hashArea(LatLng point) {
		return ((int) point.getLatitude()) + "" + ((int) point.getLongitude())
				+ "";
	}

	public static Icon makeHomeIcon() {
		Icon homeIcon = Icon.newInstance("images/markers/image.png");
		homeIcon.setPrintImageURL("images/markers/printImage.gif");
		homeIcon.setMozPrintImageURL("images/markers/mozPrintImage.gif");
		homeIcon.setIconSize(Size.newInstance(32, 32));
		homeIcon.setShadowURL("images/markers/shadow.png");
		homeIcon.setTransparentImageURL("images/markers/transparent.png");
		homeIcon.setShadowSize(Size.newInstance(48, 32));
		homeIcon.setIconAnchor(Point.newInstance(16, 32));
		homeIcon.setInfoWindowAnchor(Point.newInstance(16, 0));
		homeIcon.setImageMap(getHomeIconMap());
		return homeIcon;
	}

	public static void updateMap() {
		areasShowing.clear();
		map.clearOverlays();
		if (map.getZoomLevel() < 11) {
			ListPanel.clearItemsAndSetContent(ListPanel.zoomCloser);
		} else {
			LatLngBounds bounds = map.getBounds();
			LatLng min = bounds.getSouthWest();
			LatLng max = bounds.getNorthEast();
			Double min_lat = min.getLatitude();
			Double min_lng = min.getLongitude();
			Double max_lat = max.getLatitude();
			Double max_lng = max.getLongitude();

			areasShowing.add(hashArea(bounds.getNorthEast()));
			areasShowing.add(hashArea(bounds.getSouthWest()));
			areasShowing.add(hashArea(LatLng.newInstance(bounds.getSouthWest()
					.getLatitude(), bounds.getNorthEast().getLongitude())));
			areasShowing.add(hashArea(LatLng.newInstance(bounds.getSouthWest()
					.getLongitude(), bounds.getNorthEast().getLatitude())));

			String requestData = "?action=map&min_lat=" + min_lat + "&min_lng="
					+ min_lng + "&max_lat=" + max_lat + "&max_lng=" + max_lng
					+ "&search_term=" + SearchPanel.searchBox.getText();
			RequestBuilder getItems = new RequestBuilder(RequestBuilder.GET,
					"/getitems" + requestData);
			getItems.setCallback(new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					PopUpPanel.alert("Error",
							new Label(exception.getMessage()), true);
				}

				public void onResponseReceived(Request request,
						Response response) {
					// TODO: Update List of results
					// TODO: Update markers on map
					// TODO: Only mark those within view and cache the other
					// ones

					String json = response.getText();
					final JSONArray itemsJSON = JSONParser.parse(json)
							.isObject().get("Items").isArray();
					if (itemsJSON.size() > 0) {
						ItemLight[] items = new ItemLight[itemsJSON.size()];
						for (int j = 0; j < itemsJSON.size(); j++) {
							items[j] = new ItemLight(itemsJSON.get(j)
									.isObject());
						}
						ListPanel.items = items;
						ListPanel.offset = 0;
						ListPanel.loadItems();
						plotItems(
								new ArrayList<ItemLight>(Arrays.asList(items)),
								0);
						SearchPanel.loadTags();
					} else {
						ListPanel
								.clearItemsAndSetContent(ListPanel.nothingHere);
					}
				}
			});
			try {
				ListPanel.clearItemsAndSetContent(new HTML(
						"<img src=\"/images/loading.gif\"/>"));
				getItems.send();
			} catch (RequestException e) {
				;
			}
		}
	}

	// Swap = 0 for no swap
	private static void plotItems(ArrayList<ItemLight> items, int swap) {
		map.clearOverlays();
		items.add(0, items.remove(swap));

		final ArrayList<ItemLight> swappedItems = items;

		for (int j = 0; j < items.size(); j++) {
			final int k = j;
			Marker current = null;
			if (FindHomePopup.getHomeLocation() != null
					&& FindHomePopup.getHomeLocation().isEquals(
							items.get(k).getLocation())) {
				current = new Marker(items.get(k).getLocation(), MarkerOptions
						.newInstance(makeHomeIcon()));
			} else {
				current = new Marker(items.get(k).getLocation());
			}
			current.addMarkerClickHandler(new MarkerClickHandler() {
				public void onClick(MarkerClickEvent event) {
					ClientApplication.itemPanel.loadItem(swappedItems.get(k));
					plotItems(swappedItems, k);
				}
			});
			map.addOverlay(current);
		}
	}

	public static void askLocation(String user) {
		locationPopup.setSpacing(7);

		locationPopup
				.add(new HTML(ClientApplication.constants.pleaseLocation()));
		locationTextBox = new TextBox();
		locationTextBox.setFocus(true);
		locationTextBox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					setLocation(locationTextBox.getText());
					PopUpPanel.hidePopup();
				}
			}
		});
		locationPopup.add(locationTextBox);
		Button go = new Button(ClientApplication.constants.go());
		go.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				setLocation(locationTextBox.getText());
				PopUpPanel.hidePopup();
			}
		});
		locationPopup.add(go);

		PopUpPanel.alert(ClientApplication.constants.welcome() + " " + user,
				locationPopup, false);
	}

	public static void setLocation(final String location) {
		Geocoder localizer = new Geocoder();
		localizer.getLatLng(location, new LatLngCallback() {
			public void onFailure() {
				PopUpPanel.alert("Error", new Label(ClientApplication.constants
						.locationNotFound()
						+ "!"), true);
			}

			public void onSuccess(LatLng point) {
				FindHomePopup.setCenter(point, 7);
				map.setCenter(point);
				map.setZoomLevel(11);
				updateMap();
				Cookies.setCookie("lat", point.getLatitude() + "", new Date(
						System.currentTimeMillis() + 31104000000L));
				Cookies.setCookie("lon", point.getLongitude() + "", new Date(
						System.currentTimeMillis() + 31104000000L));
			}
		});
	}

	public static LatLng getLocation() {
		return LatLng.newInstance(getClientLatitude(), getClientLongitude());
	}

	public static native double getClientLatitude() /*-{
		return ($wnd.google.loader.ClientLocation == null ? 0 :
		  Number($wnd.google.loader.ClientLocation.latitude));
	}-*/;

	public static native double getClientLongitude() /*-{
		return ($wnd.google.loader.ClientLocation == null ? 0 :
		  Number($wnd.google.loader.ClientLocation.longitude));
	}-*/;

	public static native String getClientCountryCode() /*-{
		return ($wnd.google.loader.ClientLocation == null ? "" :
		  $wnd.google.loader.ClientLocation.address.country_code);
	}-*/;

	public static native JsArrayInteger getHomeIconMap() /*-{
		return [18,0,20,1,20,2,22,3,22,4,24,5,24,6,26,7,26,8,28,9,28,10,30,11,30,12,31,13,31,14,31,15,31,16,31,17,31,18,30,19,29,20,26,21,26,22,27,23,26,24,26,25,26,26,26,27,26,28,25,29,0,30,0,30,6,29,5,28,5,27,0,26,0,25,0,24,0,23,5,22,5,21,2,20,1,19,0,18,0,17,0,16,0,15,0,14,0,13,1,12,1,11,3,10,3,9,5,8,5,7,7,6,7,5,9,4,9,3,11,2,11,1,13,0];
	}-*/;
}