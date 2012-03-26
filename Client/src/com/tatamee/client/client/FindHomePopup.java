/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import org.cobogw.gwt.user.client.ui.Button;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.control.MapTypeControl;
import com.google.gwt.maps.client.event.MapDragHandler;
import com.google.gwt.maps.client.event.MapMoveEndHandler;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FindHomePopup {

	public static VerticalPanel enterLocation = new VerticalPanel();

	public static MapWidget homeMap = new MapWidget();

	public static boolean isCentered = false;

	private static final Marker marker = new Marker(LatLng
			.newInstance(0.0, 0.0), MarkerOptions.newInstance(FindHomePopup
			.makeHomeArrowIcon()));

	private static LatLng home;
	private static String countryCode = "US";

	public FindHomePopup() {
		enterLocation.setSpacing(7);
		enterLocation.setWidth("316px");
		enterLocation
				.add(new HTML(ClientApplication.constants.placeTheHouse()));

		Label gotoLabel = new Label(ClientApplication.constants.goTo() + ":");
		gotoLabel.setStyleName("title2");
		enterLocation.add(gotoLabel);
		final TextBox goTo = new TextBox();
		goTo.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					Geocoder localizer = new Geocoder();
					localizer.getLatLng(goTo.getText(), new LatLngCallback() {
						public void onFailure() {
							goTo.setText(ClientApplication.constants.locationNotFound() + "!");
						}

						public void onSuccess(LatLng point) {
							FindHomePopup.setCenter(point, 7);
						}
					});
				}
			}
		});

		HorizontalPanel goToPanel = new HorizontalPanel();
		goToPanel.add(goTo);
		Button goToButton = new Button(ClientApplication.constants.go());
		goToButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Geocoder localizer = new Geocoder();
				localizer.getLatLng(goTo.getText(), new LatLngCallback() {
					public void onFailure() {
						goTo.setText(ClientApplication.constants.locationNotFound() + "!");
					}

					public void onSuccess(LatLng point) {
						FindHomePopup.setCenter(point, 7);
					}
				});
			}
		});
		goToPanel.add(goToButton);
		enterLocation.add(goToPanel);

		// TODO: Add HOME ICON
		// TODO: Ask user for location if getclientlatitude returns
		// TODO: Add Satelite view and Map view to map

		homeMap.addControl(new MapTypeControl());
		homeMap.addControl(new LargeMapControl());
		homeMap.setSize("300px", "300px");
		// TODO: Add change map type view overlay
		// homeMap.addOverlay(overlay);

		homeMap.addMapDragHandler(new MapDragHandler() {
			public void onDrag(MapDragEvent event) {
				centerMarker();
			}
		});
		homeMap.addMapMoveEndHandler(new MapMoveEndHandler() {
			public void onMoveEnd(MapMoveEndEvent event) {
				centerMarker();
			}
		});

		enterLocation.add(homeMap);

		Button submit = new Button(ClientApplication.constants.save());
		submit.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Calls sendData() when its done
				setCountryCode(FindHomePopup.marker.getLatLng());
			}
		});
		enterLocation.add(submit);
		enterLocation.setCellHorizontalAlignment(submit,
				HorizontalPanel.ALIGN_RIGHT);

		RequestBuilder userCheck = new RequestBuilder(RequestBuilder.GET,
				"/getuser");

		// TODO: Not needed?
		userCheck.setRequestData(new JSONString(ClientApplication.userEmail)
				.toString());

		userCheck.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				PopUpPanel
						.alert(
								"Error",
								new Label(ClientApplication.constants.pleaseCheckConnection()),
								true);
			}

			public void onResponseReceived(Request request, Response response) {
				/*
				 * Returns a boolean if user is not found, otherwise returns the
				 * user data
				 */

				JSONValue jsonResponse = JSONParser.parse(response.getText());
				if (jsonResponse.isBoolean() != null) {
					PopUpPanel.alert(ClientApplication.constants.pleaseMarkHome(), enterLocation,
							false);
				} else {
					FindHomePopup.home = LatLng.newInstance(jsonResponse
							.isObject().get("lat").isNumber().doubleValue(),
							jsonResponse.isObject().get("lon").isNumber()
									.doubleValue());
					ClientApplication.currency = jsonResponse.isObject().get(
							"currency").isString().stringValue();
					MapPanel.map.setCenter(FindHomePopup.home, 11);
					// In case the Maps was initialized first and didn't find "FindHomePopup.home"
					FindHomePopup.setCenter(FindHomePopup.home, 11);
					PopUpPanel.hidePopup(ClientApplication.constants.welcome() + " " + ClientApplication.constants.anonymous());
				}
			}
		});
		try {
			userCheck.send();
		} catch (RequestException e) {
			PopUpPanel.alert(ClientApplication.constants.error(), new Label(e.getMessage()), true);
		}
	}

	public static void setCountryCode(final LatLng location) {
		Geocoder localizer = new Geocoder();
		localizer.getLocations(location, new LocationCallback() {
			public void onFailure(int statusCode) {
				sendData();
			}

			public void onSuccess(JsArray<Placemark> locations) {
				FindHomePopup.countryCode = locations.get(0).getCountry();
				sendData();
			}
		});
	}

	public static void sendData() {
		RequestBuilder updateUser = new RequestBuilder(RequestBuilder.POST,
				"/updateuser");
		String requestData = "&user=" + ClientApplication.user + "&lat="
				+ marker.getLatLng().getLatitude() + "&lng="
				+ marker.getLatLng().getLongitude() + "&country="
				+ FindHomePopup.countryCode;
		updateUser.setHeader("Content-Type",
				"application/x-www-form-urlencoded");
		updateUser.setHeader("Content-Length", requestData.length() + "");

		try {
			updateUser.sendRequest(requestData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					PopUpPanel.alert(ClientApplication.constants.error(),
							new Label(exception.getMessage()), true);
				}

				public void onResponseReceived(Request request,
						Response response) {
					JSONValue jsonResponse = JSONParser.parse(response.getText());
					ClientApplication.currency = jsonResponse.isObject().get("currency").isString().stringValue();
					SellPanel.updateCurrency();
					FindHomePopup.home = homeMap.getCenter();
					MapPanel.updateMap();
					MapPanel.map.setCenter(FindHomePopup.home);
					PopUpPanel.hidePopup();
					Label thankYou = new Label(ClientApplication.constants.yourHome());
					thankYou.setWidth("300px");
					PopUpPanel.hidePopup();
					PopUpPanel.alert(ClientApplication.constants.thankYou() + "!", thankYou, true);
				}
			});
		} catch (RequestException e) {
			PopUpPanel.alert(ClientApplication.constants.error(), new Label(e.getMessage()), true);
		}
	}

	public static LatLng getHomeLocation() {
		return FindHomePopup.home;
	}

	private void centerMarker() {
		marker.setLatLng(homeMap.getCenter());
	}

	public static void setCenter(LatLng center, int zoomLevel) {
		homeMap.setCenter(center, zoomLevel);
		marker.setLatLng(homeMap.getCenter());
		homeMap.addOverlay(marker);
	}

	public static Icon makeHomeArrowIcon() {
		Icon homeIcon = Icon.newInstance("images/markers/image2.png");
		homeIcon.setPrintImageURL("images/markers/printImage2.gif");
		homeIcon.setMozPrintImageURL("images/markers/mozPrintImage2.gif");
		homeIcon.setIconSize(Size.newInstance(32, 32));
		homeIcon.setShadowURL("images/markers/shadow2.png");
		homeIcon.setTransparentImageURL("images/markers/transparent2.png");
		homeIcon.setShadowSize(Size.newInstance(48, 32));
		homeIcon.setIconAnchor(Point.newInstance(16, 32));
		homeIcon.setInfoWindowAnchor(Point.newInstance(16, 0));
		homeIcon.setImageMap(getHomeIconArrowMap());
		return homeIcon;
	}

	public static native JsArrayInteger getHomeIconArrowMap() /*-{
		return [20,0,21,1,22,2,23,3,24,4,25,5,26,6,27,7,28,8,30,9,30,10,31,11,31,12,31,13,31,14,31,15,31,16,29,17,29,18,26,19,26,20,27,21,26,22,26,23,26,24,26,25,26,26,24,27,22,28,20,29,18,30,16,31,15,31,13,30,11,29,9,28,7,27,5,26,5,25,5,24,5,23,5,22,5,21,5,20,5,19,5,18,3,17,1,16,0,15,0,14,0,13,0,12,0,11,1,10,1,9,3,8,3,7,5,6,5,5,7,4,8,3,9,2,11,1,12,0];
	}-*/;

}
