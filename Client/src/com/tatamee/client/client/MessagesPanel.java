package com.tatamee.client.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.VerticalPanel;

public class MessagesPanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private VerticalPanel messagesPanel = new VerticalPanel();
	private Item loadedItem;
	private int offset = 0;

	// TODO FIX GLASS PANEL SCROLL WHEN SIZE IS BIGGER THAN SCREEN
	public MessagesPanel(Item item, final String type) {
		messagesPanel.setStyleName("MessagesPanel");
		loadedItem = item;
		loadMessages(type);
		mainPanel.setSpacing(10);
		mainPanel.add(messagesPanel);
		HorizontalPanel previousNextPanel = new HorizontalPanel();
		final HTML previous = new HTML("");
		final HTML next = new HTML("");
		if (item.getComments() > 5) {
			next
					.setHTML(("<a href=\"javascript:void(0)\">" + ClientApplication.constants.next() + " "
							+ Math.min(5, loadedItem.getComments()
									- (offset + 5)) + "</a>"));
		}

		previous.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				offset -= 5;
				loadMessages(type);
				if (offset - 5 >= 0) {
					previous.setHTML(("<a href=\"javascript:void(0)\">"
							+ ClientApplication.constants.previous() + " 5" + "</a>"));
				} else {
					previous.setHTML("");
				}

				next
						.setHTML(("<a href=\"javascript:void(0)\">" + ClientApplication.constants.next()  + " "
								+ Math.min(5, loadedItem.getComments()
										- (offset + 5)) + "</a>"));
			}
		});
		next.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				offset += 5;
				loadMessages(type);
				if (previous.getHTML().equals("")) {
					previous.setHTML(("<a href=\"javascript:void(0)\">"
							+ ClientApplication.constants.previous() + " 5" + "</a>"));
				}
				if (Math.min(5, loadedItem.getComments() - (offset + 5)) > 0) {
					next.setHTML(("<a href=\"javascript:void(0)\">" + ClientApplication.constants.next() + " "
							+ Math.min(5, loadedItem.getComments()
									- (offset + 5)) + "</a>"));
				} else {
					next.setHTML("");
				}
			}
		});
		previousNextPanel.add(previous);
		previousNextPanel.add(next);
		previousNextPanel.setWidth("100%");
		previousNextPanel.setCellHorizontalAlignment(next,
				HorizontalPanel.ALIGN_RIGHT);
		mainPanel.add(previousNextPanel);
		initWidget(mainPanel);
	}

	// TODO Only display X comments
	private void loadMessages(final String type) {
		String requestData = "/getitems?action=" + type + "&id="
				+ this.loadedItem.getID() + "&offset=" + this.offset;
		RequestBuilder getMessages = new RequestBuilder(RequestBuilder.GET,
				requestData);
		getMessages.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
			}

			public void onResponseReceived(Request request, Response response) {
				JSONObject commentsJSON = JSONParser.parse(response.getText())
						.isObject();
				JSONArray comments = commentsJSON.get("Messages").isArray();
				messagesPanel.clear();
				if (comments.size() <= 0) {
					GWT.log("TYPE=" + type, null);
					String messageType = (type.equals("good_ratings")) ? ClientApplication.constants.goodRatings() : (type.equals("bad_ratings") ? ClientApplication.constants.badRatings() : type.equals("comments") ? ClientApplication.constants.comments() : ClientApplication.constants.error());
					messagesPanel.add(new Label("No " + messageType));
				} else {
					for (int j = 0; j < comments.size(); j++) {
						VerticalPanel messagePanel = new VerticalPanel();
						JSONObject comment = comments.get(j).isObject();
						HorizontalPanel fromDate = new HorizontalPanel();
						fromDate.setWidth("400px");
						Label from = new Label(comment.get("poster").isString()
								.stringValue());
						from.setStyleName("title2");
						Label date = new Label(comment.get("date").isString()
								.stringValue());
						date.setStyleName("italics");
						fromDate.add(from);
						fromDate.add(date);
						fromDate.setCellVerticalAlignment(date,
								HorizontalPanel.ALIGN_MIDDLE);
						fromDate.setCellHorizontalAlignment(date,
								HorizontalPanel.ALIGN_RIGHT);
						messagePanel.add(fromDate);
						Label message = new Label(comment.get("message")
								.isString().stringValue());
						message.setWidth("400px");
						messagePanel.add(message);
						messagesPanel.add(messagePanel);
						PopUpPanel.centerPopup();
					}
				}
				// PopUpPanel.centerPopup();
			}
		});
		try {
			getMessages.send();
		} catch (RequestException e) {
			;
		}
	}
}