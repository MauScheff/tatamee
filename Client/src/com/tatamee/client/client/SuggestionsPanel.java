package com.tatamee.client.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.cobogw.gwt.user.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SuggestionsPanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private HashSet<String> suggestedTags = new HashSet<String>();
	HorizontalPanel featuresPanel = new HorizontalPanel();

	// TODO On close dialog set focus on tags in sell panel
	public SuggestionsPanel(String category) {
		mainPanel.setStyleName("FeaturesPanel");
		mainPanel.setSpacing(10);
		Label about = new Label(ClientApplication.constants.tickMark());
		about.setWidth("300px");
		mainPanel.add(about);
		Label featuresAdded = new Label(ClientApplication.constants.suggestedTags());
		featuresAdded.setStyleName("title2");
		mainPanel.add(featuresAdded);
		mainPanel.add(featuresPanel);
		HorizontalPanel addFeaturePanel = new HorizontalPanel();
		addFeaturePanel.setSpacing(5);
		mainPanel.add(addFeaturePanel);
		requestTags();

		Button save = new Button(ClientApplication.constants.save());
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.hidePopup();
				SellPanel.tags.setFocus(true);
				ClientApplication.itemPanelSell.setTags(SellPanel.getTags());
			}
		});

		mainPanel.add(save);
		initWidget(mainPanel);
	}

	public void plotTags() {
		featuresPanel.clear();
		suggestedTags.removeAll(getTagsInUse());
		Iterator<String> tagsIter = suggestedTags.iterator();
		while (tagsIter.hasNext()) {
			VerticalPanel currentPanel = new VerticalPanel();
			featuresPanel.add(currentPanel);
			for (int k = 0; k < 10; k++) {
				if (tagsIter.hasNext()) {
					final String current = tagsIter.next();
					final HorizontalPanel entry = new HorizontalPanel();
					entry.setSpacing(5);
					entry.add(new Label(current));
					Image add = new Image("images/add.png");
					add.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							suggestedTags.remove(current);
							SellPanel.addTag(current);
							plotTags();
						}
					});
					entry.add(add);
					currentPanel.add(entry);

				} else {
					break;
				}
			}
			if (PopUpPanel.isShowing()) {
				PopUpPanel.centerPopup();
			}
		}
	}

	private void requestTags() {
		// TODO CHANGE FEATURES TO TAGS
		String requestData = "?location=me&action=tags&query="
				+ SellPanel.getCategory();
		RequestBuilder getSuggestions = new RequestBuilder(RequestBuilder.GET,
				"/suggestions" + requestData);
		getSuggestions.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				PopUpPanel.alert(ClientApplication.constants.error(), new Label(exception.getMessage()),
						true);
			}

			public void onResponseReceived(Request request, Response response) {
				String json = response.getText();
				final JSONArray suggestionsJSON = JSONParser.parse(json)
						.isObject().get("Suggestions").isArray();
				if (suggestionsJSON.size() <= 0) {
					featuresPanel.add(new Label(ClientApplication.constants.noSuggestions()));
				} else {
					for (int j = 0; j < suggestionsJSON.size(); j++) {
						JSONArray pair = suggestionsJSON.get(j).isArray();
						String currentKey = pair.get(0).isString()
								.stringValue();
						suggestedTags.add(currentKey);
					}
					plotTags();
				}
			}
		});
		try {
			getSuggestions.send();
		} catch (RequestException e) {
			PopUpPanel.alert(ClientApplication.constants.error(), new Label(e.getMessage()), true);
		}
	}

	private HashSet<String> getTagsInUse() {
		HashSet<String> result = new HashSet<String>();
		String tagsString = SellPanel.getTags();
		String[] tags = tagsString.split(",");
		for (String tag : tags) {
			result.add(tag.trim());
		}
		return result;
	}

	private static String fixSize(String word) {
		String result = word;
		if (result.length() >= 25) {
			result = result.substring(0, 24) + "...";
		}
		return result;
	}
}
