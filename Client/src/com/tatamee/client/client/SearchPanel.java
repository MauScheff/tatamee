/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import org.cobogw.gwt.user.client.CSS;
import org.cobogw.gwt.user.client.Color;
import org.cobogw.gwt.user.client.ui.Button;
import org.cobogw.gwt.user.client.ui.RoundedLinePanel;
import org.cobogw.gwt.user.client.ui.RoundedPanel;

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
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SearchPanel extends Composite {
	private static VerticalPanel mainPanel = new VerticalPanel();
	private static VerticalPanel outerPanel = new VerticalPanel();
	private static TextBox location = new TextBox();
	public static final TextBox searchBox = new TextBox();
	public static ListBox tags = new ListBox();

	public SearchPanel() {
		mainPanel.setSpacing(7);
		mainPanel.setStyleName("SearchPanel");

		FlowPanel searchLabelPanel = new FlowPanel();
		InlineLabel searchLabel = new InlineLabel(ClientApplication.constants.search());
		searchLabel.setStyleName("title");
		searchLabelPanel.add(new Image("images/find.png"));
		searchLabelPanel.add(searchLabel);
		mainPanel.add(searchLabelPanel);
		Label searchEg = new Label("eg. apartments");
		searchEg.setStyleName("commaSeparated");
		mainPanel.add(searchEg);
		searchBox.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					MapPanel.updateMap();
					ClientApplication.mode = 0;
				}
			}
		});
		mainPanel.add(searchBox);
		searchBox.setWidth("210px");

		// TODO: MAKE SET FOCUS WORK
		Button dummy = new Button(ClientApplication.constants.searchListings());
		CSS.setProperty(dummy, CSS.A.FONT_WEIGHT, CSS.V.FONT_WEIGHT.BOLD);
		dummy.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				MapPanel.updateMap();
				SearchPanel.searchBox.setFocus(true);
				ClientApplication.mode = 0;
			}
		});
		mainPanel.add(dummy);
		tags.setStyleName("listBox");
		FlowPanel filtersPanel = new FlowPanel();
		InlineLabel filters = new InlineLabel(ClientApplication.constants.filters());
		filters.setStyleName("title2-icon-right");
		filtersPanel.add(filters);
		filtersPanel.add(new Image("images/new.png"));
		mainPanel.add(filtersPanel);
		mainPanel.add(tags);
		tags.setWidth("100%");

		Button dummyButton = new Button(ClientApplication.constants.addToSearch());
		dummyButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String tag = tags.getValue(tags.getSelectedIndex());
				SearchPanel.searchBox.setText(SearchPanel.searchBox.getText() + " " + tag); 
				tags.clear();
				MapPanel.updateMap();
				ClientApplication.mode = 0;
			}
		});
		mainPanel.add(dummyButton);
		mainPanel.add(new HTML("<hr/>"));
		
		FlowPanel changeLocationPanel = new FlowPanel();
		InlineLabel changeLocation = new InlineLabel(ClientApplication.constants.changeLocation());
		changeLocation.setStyleName("title2-icon-left");
		changeLocationPanel.add(new Image("images/map_edit.png"));
		
		changeLocationPanel.add(changeLocation);
		mainPanel.add(changeLocationPanel);
//		Label locationEg = new Label("eg. new york");
//		locationEg.setStyleName("commaSeparated");
//		mainPanel.add(locationEg);
		mainPanel.add(location);
		location.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					MapPanel.setLocation(location.getText());
					ClientApplication.mode = 0;
				}
			}
		});
		location.setWidth("210px");
		Button button = new Button(ClientApplication.constants.go());
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				MapPanel.setLocation(location.getText());
				ClientApplication.mode = 0;
			}
		});
		mainPanel.add(button);

		/*
		 * Rounder Corners
		 */
		RoundedLinePanel rp = new RoundedLinePanel(RoundedPanel.ALL, 5);
		rp.setCornerColor(Color.GREY, Color.WHITE);
		rp.add(mainPanel);
		outerPanel.add(rp);
		initWidget(outerPanel);
	}

	private static String fixSize(String word) {
		String result = word;
		if (result.length() >= 18) {
			result = result.substring(0, 17) + "...";
		}
		return result;
	}

	public static void loadTags() {
		if (MapPanel.map.getZoomLevel() >= 11) {
			tags.clear();
			LatLngBounds bounds = MapPanel.map.getBounds();
			LatLng min = bounds.getSouthWest();
			LatLng max = bounds.getNorthEast();
			Double min_lat = min.getLatitude();
			Double min_lng = min.getLongitude();
			Double max_lat = max.getLatitude();
			Double max_lng = max.getLongitude();

			String requestData = "?location=map&action=tags&min_lat=" + min_lat
					+ "&min_lng=" + min_lng + "&max_lat=" + max_lat
					+ "&max_lng=" + max_lng + "&query="
					+ SearchPanel.searchBox.getText();
			RequestBuilder getSuggestions = new RequestBuilder(
					RequestBuilder.GET, "/suggestions" + requestData);
			getSuggestions.setCallback(new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					PopUpPanel.alert(ClientApplication.constants.error(),
							new Label(exception.getMessage()), true);
				}

				public void onResponseReceived(Request request,
						Response response) {
					String json = response.getText();
					final JSONArray suggestionsJSON = JSONParser.parse(json)
							.isObject().get("Suggestions").isArray();
					
					for (int j = 0; j < suggestionsJSON.size(); j++) {
						JSONArray pair = suggestionsJSON.get(j).isArray();
						String currentKey = pair.get(0).isString().stringValue();
						int quantity = (int) pair.get(1).isNumber().doubleValue();

						tags.addItem(currentKey + " (" + quantity + ")",
								currentKey);
					}
				}
			});
			try {
				getSuggestions.send();
			} catch (RequestException e) {
				PopUpPanel.alert(ClientApplication.constants.error(), new Label(e.getMessage()), true);
			}
		}
	}
}
