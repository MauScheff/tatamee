package com.tatamee.client.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserPanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();

	private ItemPanel loadedPanel = new ItemPanel(false);
	private int loadedItemIndex = 2;
	
	//TODO Fix issue with glass panel, when click on an image and close glass panel keeps adding glass.
	//TODO Rounded corners and check color of grey mainPanel background
	//TODO Add previous and next
	public UserPanel(String user_id) {
		loadItems(user_id);
		mainPanel.setSpacing(10);
//		mainPanel.setStyleName("greybackground");
//		TextBox url = new TextBox();
//		url.setWidth("400px");
//		url.addKeyPressHandler(new KeyPressHandler(){
//			public void onKeyPress(KeyPressEvent event) {
//				event.preventDefault();
//			}
//		});
//		url.setText("http://www.tatamee.com/user/" + user_id);
//		Label urlLabel = new Label("url");
//		HorizontalPanel urlPanel = new HorizontalPanel();
//		urlPanel.setSpacing(5);
//		urlPanel.add(urlLabel);
//		urlPanel.add(url);
//		urlLabel.setStyleName("title2");
//		mainPanel.add(urlPanel);
		Label itemsLabel = new Label(ClientApplication.constants.itemsSale());
		itemsLabel.setStyleName("title2");
		mainPanel.add(itemsLabel);
		initWidget(mainPanel);
	}

	private void loadItems(String user_id) {
		String requestData = "/getitems?action=user&user_id=" + user_id + "&offset=0";
		RequestBuilder getItems = new RequestBuilder(RequestBuilder.GET,
				requestData);
		getItems.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
			}

			public void onResponseReceived(Request request, Response response) {
				JSONObject itemsJSON = JSONParser.parse(response.getText())
						.isObject();
				JSONArray items = itemsJSON.get("Items").isArray();
				for (int j = 0; j < items.size(); j++) {
					final int k = j + 2;
					JSONObject item = items.get(j).isObject();
					final ItemLight itemLight = new ItemLight(item);
					final HTML itemLabel = new HTML("<a href=\"javascript:void(0)\">" + itemLight.getTitle() + "</a>" + "  ▸");
					itemLabel.addClickHandler(new ClickHandler(){
						public void onClick(ClickEvent event) {
							mainPanel.remove(loadedPanel);
							HTML loadedLabel = (HTML) mainPanel.getWidget(loadedItemIndex);
							loadedLabel.setHTML(loadedLabel.getHTML().substring(0, loadedLabel.getHTML().length()-1) + "  ▸");
							loadedPanel.loadItem(itemLight);
							itemLabel.setHTML("<a href=\"javascript:void(0)\">" + itemLight.getTitle() + "</a>" + "  ▾");
							loadedItemIndex = mainPanel.getWidgetIndex(itemLabel);
							mainPanel.insert(loadedPanel, k);
							PopUpPanel.centerPopup();
						}
					});
					mainPanel.add(itemLabel);
				}
				if (items.size() > 0) {
					ItemLight firstItem = new ItemLight(items.get(0).isObject());
					loadedPanel.loadItem(firstItem);
					HTML itemLabel = (HTML) mainPanel.getWidget(1);
					itemLabel.setHTML("<a href=\"javascript:void(0)\">" + firstItem.getTitle() + "</a>" + "  ▾");
					loadedItemIndex = mainPanel.getWidgetIndex(itemLabel);
					mainPanel.insert(loadedPanel, 2);
					PopUpPanel.centerPopup();
				}
				PopUpPanel.centerPopup();
			}
		});
		try {
			getItems.send();
		} catch (RequestException e) {
			;
		}
	}
}