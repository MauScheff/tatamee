package com.tatamee.client.client;

import org.cobogw.gwt.user.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PickHandlePanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	TextBox handle = new TextBox();
	Button done = new Button(ClientApplication.constants.save());

	public PickHandlePanel() {
		HorizontalPanel urlPanel = new HorizontalPanel();
		Label url = new Label("http://www.tatamee.com/user/");
		url.setStyleName("big");
		handle.setStyleName("unavailable");
		handle.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				checkAvailable(handle.getText());
			}
		});
		urlPanel.add(url);
		urlPanel.add(handle);
		mainPanel.add(urlPanel);
		done.setEnabled(false);
		done.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				RequestBuilder submit = new RequestBuilder(RequestBuilder.POST,
						"/updatehandle?handle=" + handle.getText());
				submit.setCallback(new RequestCallback() {
					public void onError(Request request, Throwable exception) {
						;
					}

					public void onResponseReceived(Request request,
							Response response) {
						Label thankYou = new Label(ClientApplication.constants.yourHome());
						thankYou.setWidth("300px");
						PopUpPanel.hidePopup();
						PopUpPanel.alert(ClientApplication.constants.thankYou(), thankYou, true);
					}

				});
				try {
					submit.send();
				} catch (RequestException e) {
					;
				}
			}
		});
		mainPanel.add(done);
		initWidget(mainPanel);
	}

	private void checkAvailable(String handleString) {
		RequestBuilder checkAvailable = new RequestBuilder(RequestBuilder.GET,
				"/updatehandle?handle=" + handleString);
		checkAvailable.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {

			}

			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == 204) {
					handle.setStyleName("available");
					done.setEnabled(true);
				} else {
					handle.setStyleName("unavailable");
					done.setEnabled(false);
				}
			}

		});
		try {
			checkAvailable.send();
		} catch (RequestException e) {
			;
		}
	}
}