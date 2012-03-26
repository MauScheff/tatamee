package com.tatamee.client.client;

import org.cobogw.gwt.user.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

public class AddPicturesPanel extends Composite {

	private FlowPanel mainPanel = new FlowPanel();
	private static int thumbnailReload = 1000;
	private JSONObject descriptions;
	private Item item = new Item();
	int totalPics = 0;
	public static final int STATUS_CODE_OK = 200;

	public AddPicturesPanel(Item item) {
		mainPanel.setStyleName("AddPicturesPanel");
		mainPanel.setWidth("800px");
		totalPics = item.getNPictures();
		if (item != null) {
			this.item = item;
		}

		String requestData = "/image?get=descriptions&id=" + item.getID();
		RequestBuilder getDescriptions = new RequestBuilder(RequestBuilder.GET,
				requestData);
		getDescriptions.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				;
			}

			public void onResponseReceived(Request request, Response response) {
				descriptions = JSONParser.parse(response.getText()).isObject();
				loadUploaders();
			}
		});
		try {
			getDescriptions.send();
		} catch (RequestException e) {
			;
		}
		initWidget(mainPanel);
	}

	private void loadUploaders() {
		for (int j = 0; j < 6 && j < totalPics + 1; j++) {
			FormPanel uploader = buildUploader(j, item);
			if (uploader != null) {
				mainPanel.add(uploader);
			}
		}
		PopUpPanel.centerPopup();
	}

	private FormPanel buildUploader(final int index, final Item item) {
		HorizontalPanel thumbnailPanel = new HorizontalPanel();
		// thumbnailPanel.setWidth("365px");
		// thumbnailPanel.setHeight("100px");
		thumbnailPanel.setStyleName("AddPicturesPanel-uploader");
		final Image thumbnail = new Image("/image?get=thumbnail&n=" + index
				+ "&id=" + item.getID() + "#"
				+ AddPicturesPanel.thumbnailReload++);
		thumbnail.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.alert(ClientApplication.constants.picture(), new PicturePanel(item, index), true);
			}
		});
		thumbnailPanel.add(thumbnail);
		VerticalPanel uploadPanel = new VerticalPanel();
		uploadPanel.setWidth("250px");
		// uploadPanel.setSpacing(2);
		Label pictureLabel = new Label(ClientApplication.constants.picture());
		pictureLabel.setStyleName("title3");
		uploadPanel.add(pictureLabel);
		FileUpload uploader = new FileUpload();
		uploader.setWidth("250px");
		uploader.setName("img");
		uploadPanel.add(uploader);
		Label descriptionLabel = new Label(ClientApplication.constants.description());
		descriptionLabel.setStyleName("title3");
		uploadPanel.add(descriptionLabel);
		final TextBox description = new TextBox();
		JSONValue currentDescription = this.descriptions.get(index + "");
		if (currentDescription != null) {
			description.setText(currentDescription.isString().stringValue());
		}
		description.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				description.addStyleName("AddPicturesPanel-unsaved");
			}
		});
		description.setWidth("250px");
		description.setName("description");
		uploadPanel.add(description);

		Hidden id = new Hidden();
		id.setValue(item.getID());
		id.setName("id");
		uploadPanel.add(id);
		Hidden N = new Hidden();
		N.setValue(index + "");
		N.setName("n");
		uploadPanel.add(N);

		final FormPanel form = new FormPanel();
		form.setAction("/image");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		form.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				;
			}
		});

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				thumbnail.setUrl(thumbnail.getUrl().substring(0,
						thumbnail.getUrl().lastIndexOf("#"))
						+ "#" + AddPicturesPanel.thumbnailReload++);
				if (index == item.getNPictures()) {
					if (item.getNPictures() < 5) {
						getDescription(item, index, description);
					} else {
						description.removeStyleName("AddPicturesPanel-unsaved");
					}
				} else {
					description.removeStyleName("AddPicturesPanel-unsaved");
				}
			}
		});

		Button submit = new Button(ClientApplication.constants.save());
		submit.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});
		uploadPanel.add(submit);

		thumbnailPanel.add(uploadPanel);
		form.add(thumbnailPanel);
		return form;
	}

	private void getDescription(final Item item, int index, final TextBox description) {
		String url = "/image?get=description&n=" + index + "&id=" + item.getID();
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try {
			Request response = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					;
				}

				public void onResponseReceived(Request request,
						Response response) {
					if (response.getStatusCode() == STATUS_CODE_OK) {
						item.setNPictures(item.getNPictures() + 1);
						mainPanel.add(buildUploader(item.getNPictures(), item));
						PopUpPanel.centerPopup();
						description.removeStyleName("AddPicturesPanel-unsaved");
					}
				}
			});
		} catch (RequestException e) {
			;
		}
	}

}