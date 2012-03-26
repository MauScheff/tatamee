/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import org.cobogw.gwt.user.client.Color;
import org.cobogw.gwt.user.client.ui.RoundedLinePanel;
import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ItemPanel extends Composite {
	public Item loadedItem = null;
	private VerticalPanel mainPanel = new VerticalPanel();
	private Image eye = new Image("images/eye.png");
	private InlineLabel title = new InlineLabel();
	InlineLabel preview = new InlineLabel("");
	private Image thumbnail = new Image();
	private Label price = new Label();
	private HTML tags = new HTML();
	private HTML description = new HTML();
	private FlowPanel daysAgo = new FlowPanel();
	private HTML more = new HTML("");
	// private HTML more = new HTML("<a href=\"javascript:void(0)\">"
	// + ClientApplication.constants.more() + "</a><br/>");
	private HTML report = new HTML("");
	private HTML comments = new HTML();
	private HTML contact = new HTML("");
	private HTML goodRatings = new HTML("");
	private HTML badRatings = new HTML("");
	boolean myItems = false;

	public ItemPanel(final boolean myItems) {
		mainPanel.setHeight("145px");
		this.myItems = myItems;
		mainPanel.addStyleName("ItemPanel");
		HorizontalPanel itemTitle = new HorizontalPanel();
		HorizontalPanel itemAttributes = new HorizontalPanel();

		// TITLE
		itemTitle.setStyleName("ItemPanel-titlebar");
		title.setStyleName("ItemPanel-title");
		price.setStyleName("ItemPanel-price");

		FlowPanel itemTitleFlow = new FlowPanel();
		eye.setStyleName("ListPanel-bullet");
		itemTitleFlow.add(eye);
		itemTitleFlow.add(title);
		if (myItems) {
			preview.setStyleName("commaSeparated");
			itemTitleFlow.add(preview);
		}
		
		itemTitle.add(itemTitleFlow);
		itemTitle.add(price);
		itemTitle.setWidth("100%");
		itemTitle.
				setCellHorizontalAlignment(price, HorizontalPanel.ALIGN_RIGHT);

		// Description
		description.setStyleName("ItemPanel-description");

		// Links
		VerticalPanel linksPanel = new VerticalPanel();
		linksPanel.setWidth("135px");
		linksPanel.setHeight("100px");
		linksPanel.setStyleName("ItemPanel-linksPanel");
		linksPanel.add(more);
		linksPanel.add(goodRatings);
		linksPanel.add(badRatings);
		linksPanel.add(comments);
		linksPanel.add(report);
		linksPanel.add(contact);

		contact.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (loadedItem != null) {
					if (myItems) {
						PopUpPanel.alert(ClientApplication.constants
								.postComment(), new MailPanel(loadedItem,
								myItems), true);
					} else {
						PopUpPanel.alert(ClientApplication.constants
								.contactSeller(), new MailPanel(loadedItem,
								myItems), true);
					}
				}
			}
		});

		comments.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.alert(loadedItem.getComments() + " "
						+ ClientApplication.constants.comments(),
						new MessagesPanel(loadedItem, "comments"), true);
			}
		});

		goodRatings.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.alert(loadedItem.getGoodRatings() + " "
						+ ClientApplication.constants.goodRatings(),
						new MessagesPanel(loadedItem, "good_ratings"), true);
			}
		});

		badRatings.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.alert(loadedItem.getBadRatings() + " "
						+ ClientApplication.constants.badRatings(),
						new MessagesPanel(loadedItem, "bad_ratings"), true);
			}
		});

		report.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.alert(ClientApplication.constants.reportSeller(),
						new ReportSellerPanel(loadedItem), true);
			}
		});

		more.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.alert(loadedItem.getTitle(), new ItemViewPanel(
						loadedItem), true);
			}
		});

		tags.setStyleName("ItemPanel-tags");
		thumbnail.setStyleName("thumbnail");
		itemAttributes.add(thumbnail);
		thumbnail.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (loadedItem != null) {
					PopUpPanel.alert(loadedItem.getTitle(), new PicturesPanel(
							loadedItem), true);
				}
			}
		});

		itemAttributes.add(description);
		itemAttributes.add(tags);
		itemAttributes.add(linksPanel);

		daysAgo.setStyleName("ItemPanel-daysAgo");
		mainPanel.add(itemTitle);
		mainPanel.add(itemAttributes);
		mainPanel.setCellVerticalAlignment(itemAttributes, VerticalPanel.ALIGN_MIDDLE);
		HorizontalPanel userPanel = new HorizontalPanel();
		userPanel.setWidth("100%");
		userPanel.add(daysAgo);
		mainPanel.add(userPanel);
		userPanel.setCellHorizontalAlignment(daysAgo,
				HorizontalPanel.ALIGN_RIGHT);
		clearPanel();

		/*
		 * Rounder Corners
		 */
		RoundedLinePanel rp = new RoundedLinePanel(RoundedPanel.ALL, 5);
		rp.setCornerColor(Color.GREY, Color.WHITE);
		rp.add(mainPanel);
		HorizontalPanel wrapperPanel = new HorizontalPanel();
		wrapperPanel.setHeight("100%");
		wrapperPanel.add(rp);
		initWidget(wrapperPanel);
	}

	public void clearPanel() {
		eye.setVisible(false);
		title.setText("");
		this.setPrice("", "");
		thumbnail.setUrl("images/x.jpg");
		description.setHTML("");
		tags.setHTML("");
		daysAgo.clear();
		contact.setHTML("");
		comments.setHTML("");
		goodRatings.setHTML("");
		badRatings.setHTML("");
		report.setHTML("");
		preview.setText("");
	}

	public void refreshThumbnail() {
		thumbnail.setUrl(loadedItem.getThumbnail());
	}

	public void loadItem(final Item item) {
		clearPanel();
		eye.setVisible(true);
		title.setText(item.getShortenedTitle());
		preview.setText(" (" + ClientApplication.constants.preview() + ")");
		this.setPrice(item.getCurrency(), item.getPrice() + "");
		thumbnail.setUrl(item.getThumbnail());
		description.setHTML(item.getDescription());
		daysAgo.add(new InlineLabel(ClientApplication.constants.from() + " "));
		InlineHTML user = new InlineHTML(item.getOwner());
		if (!item.getNickname().equals("")) {
			user.setHTML("<a href=\"javascript:void(0)\">" + item.getOwner()
					+ "</a>");
			user.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					PopUpPanel.hidePopup();
					PopUpPanel.alert(item.getNickname() + "'s "
							+ ClientApplication.constants.store(),
							new UserPanel(item.getUserID()), true);
				}
			});
		}
		daysAgo.add(user);
		daysAgo.add(new InlineLabel(" "
				+ ClientApplication.paramConstants.daysRemaining(item
						.getDaysRemaining())));
		Image link = new Image("images/link.png");
		link.setStyleName("link");
		link.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				TextBox url = new TextBox();
				url.setWidth("300px");
				url.addKeyDownHandler(new KeyDownHandler(){
					public void onKeyDown(KeyDownEvent event) {
						event.preventDefault();
					}
				});
				url.setText("http://www.tatamee.com/#buy/item/" + item.getID());
				PopUpPanel.hidePopup();
				PopUpPanel.alert("URL",
						url, true);
			}
		});
		daysAgo.add(link);
		setTags(item.getTags());
		comments.setHTML("<a href=\"javascript:void(0)\">" + item.getComments()
				+ " " + ClientApplication.constants.comments() + "</a><br/>");
		if (myItems) {
			contact.setHTML("<a href=\"javascript:void(0)\">"
					+ ClientApplication.constants.postComment() + "</a><br/>");
		} else {
			contact
					.setHTML("<a href=\"javascript:void(0)\">"
							+ ClientApplication.constants.contactSeller()
							+ "</a><br/>");
		}
		goodRatings.setHTML("<a href=\"javascript:void(0)\">"
				+ item.getGoodRatings() + " "
				+ ClientApplication.constants.goodRatings() + "</a><br/>");
		badRatings.setHTML("<a href=\"javascript:void(0)\">"
				+ item.getBadRatings() + " "
				+ ClientApplication.constants.badRatings() + "</a><br/>");
		report.setHTML("<a href=\"javascript:void(0)\">"
				+ ClientApplication.constants.reportSeller() + "</a><br/>");
		loadedItem = item;
		MapPanel.map.setCenter(item.getLocation());
	}

	public void setTags(String tags) {
		this.tags.setText(tags);
	}

	public void setDescription(String string) {
		description.setText(string);
	}

	public void setItemTitle(String string) {
		title.setText(string);
	}

	public void incrementComments() {
		loadedItem.incrementComments();
		comments.setHTML("<a href=\"javascript:void(0)\">"
				+ loadedItem.getComments() + " "
				+ ClientApplication.constants.comments() + "</a><br/>");
	}

	public void setPrice(String currency, String string) {
		String result = "";
		if (string.indexOf('.') > 0) {
			result = addCommas(string.substring(0, string.indexOf('.')))
					+ string.substring(string.indexOf('.'));
		} else {
			result = addCommas(string);
		}
		price.setText(currency + " " + result);
	}

	private String addCommas(String string) {
		if (string.length() <= 3)
			return string;
		else
			return addCommas(string.substring(0, string.length() - 3)) + ","
					+ string.substring(string.length() - 3);
	}

	public void loadItem(String id) {
		thumbnail.setUrl("images/loading.gif");
		String requestData = "?action=item&key=" + id;
		RequestBuilder getItems = new RequestBuilder(RequestBuilder.GET,
				"/getitems" + requestData);
		getItems.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				PopUpPanel.alert(ClientApplication.constants.error(),
						new Label(exception.getMessage()), true);
			}

			public void onResponseReceived(Request request, Response response) {
				String json = response.getText();
				Item item = new Item(JSONParser.parse(json).isObject());
				loadItem(item);
				if (ClientApplication.mode == 2) {
					ListPanel.clearItemsAndSetContent1Result(new HTML(item.getShortenedTitle()));
				}
			}
		});
		try {
			getItems.send();
		} catch (RequestException e) {
			;
		}
	}
	
	public void loadItem(ItemLight lightItem) {
		loadItem(lightItem.getID());
	}
}
