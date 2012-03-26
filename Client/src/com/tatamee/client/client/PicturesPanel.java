package com.tatamee.client.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PicturesPanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private Image picture = new Image();
	private Label description = new Label("");
	private static int thumbnailReload = 500;

	public PicturesPanel(final Item item) {
		mainPanel.setStyleName("PicturesPanel");
		HorizontalPanel picturePanel = new HorizontalPanel();
		picturePanel.add(picture);
		FlowPanel thumbnails = new FlowPanel();
		thumbnails.setWidth("230px");
//		thumbnails.setSpacing(10);
		picturePanel.add(thumbnails);
		mainPanel.add(picturePanel);
		mainPanel.add(description);
		description.setStyleName("PicturesPanel-description");
		mainPanel.setCellHorizontalAlignment(description, HorizontalPanel.ALIGN_CENTER);
		mainPanel.setCellHorizontalAlignment(picture, HorizontalPanel.ALIGN_CENTER);
		// TODO add item.getFullSize()
		
		//LOAD DEFAULTS
		picture.setUrl("/image?get=default&n=0&id=" + item.getID() + "#"
				+ PicturesPanel.thumbnailReload++);
		picture.addLoadHandler(new LoadHandler() {
			public void onLoad(LoadEvent event) {
				PopUpPanel.centerPopup();
			}
		});
		
		final RequestCallback loadDescription = new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				PopUpPanel.alert(ClientApplication.constants.error(), new Label(exception.getMessage()),
						true);
			}

			public void onResponseReceived(Request request, Response response) {
				String desc = response.getText();
				description.setText(desc);
			}
		};
		
		String requestData = "/image?get=description&n=0&id=" + item.getID();
		RequestBuilder getDescription = new RequestBuilder(RequestBuilder.GET, requestData);
		getDescription.setCallback(loadDescription);
		try {
			getDescription.send();
		} catch (RequestException e) {
			;
		}

		for (int j = 0; j < item.getNPictures(); j++) {
			final int index = j;
			Image thumbnail = new Image("/image?get=thumbnail&n=" + index + "&id="
					+ item.getID() + "#" + PicturesPanel.thumbnailReload++);
			thumbnail.addClickHandler(new ClickHandler(){
				public void onClick(ClickEvent event) {
					picture.setUrl("/image?get=default&n=" + index + "&id=" + item.getID() + "#"
							+ PicturesPanel.thumbnailReload++);
					String requestData = "/image?get=description&n=" + index + "&id=" + item.getID();
					RequestBuilder getDescription = new RequestBuilder(RequestBuilder.GET, requestData);
					getDescription.setCallback(loadDescription);
					try {
						getDescription.send();
					} catch (RequestException e) {
						;
					}
				}
			});
			thumbnail.setStyleName("PicturesPanel-thumbnail");
			thumbnails.add(thumbnail);
		}
		
		initWidget(mainPanel);
		PopUpPanel.centerPopup();
	}
}