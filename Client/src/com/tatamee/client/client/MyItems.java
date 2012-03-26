/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import org.cobogw.gwt.user.client.Color;
import org.cobogw.gwt.user.client.ui.Button;
import org.cobogw.gwt.user.client.ui.RoundedLinePanel;
import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MyItems extends Composite {
	private static int offset = 0;
	private static ItemLight[] items = new ItemLight[0];
	private static VerticalPanel mainPanel = new VerticalPanel();
	private static VerticalPanel itemResults = new VerticalPanel();
	public static boolean loadedFirst = false;
	private static FlowPanel selectedPanel = null;
	private static InlineHTML newItem = new InlineHTML(
			"<a href=\"javascript:void(0)\">"
					+ ClientApplication.constants.addNewItem() + "</a>");
	static Label title = new Label(ClientApplication.constants.myStuffForSale());
	private static HorizontalPanel previousNext = new HorizontalPanel();
	private static HTML line = new HTML("<hr/>");
	private static InlineHTML next = new InlineHTML(
			"<a href=\"javascript:void(0)\">"
					+ ClientApplication.constants.next() + "</a>");
	private static InlineHTML previous = new InlineHTML(
			"<a href=\"javascript:void(0)\">"
					+ ClientApplication.constants.previous() + "</a>");
	static FlowPanel nextPanel = new FlowPanel();
	static FlowPanel previousPanel = new FlowPanel();

	public MyItems() {
		mainPanel.setHeight("520px");
		mainPanel.setStyleName("ListPanel");
		title.setStyleName("title");
		mainPanel.add(title);
		mainPanel.setCellHeight(title, "20px");
		ScrollPanel resultsWrapper = new ScrollPanel();
		resultsWrapper.setHeight("440px");
		resultsWrapper.setWidth("200px");
		resultsWrapper.add(itemResults);
		itemResults.setWidth("100%");
		mainPanel.add(resultsWrapper);

		FlowPanel newItemPanel = new FlowPanel();
		Image whitePage = new Image("images/add.png");
		whitePage.setStyleName("ListPanel-bullet");
		newItemPanel.add(whitePage);
		newItemPanel.add(newItem);
		newItem.setStyleName("MyItems-link");
		newItem.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				MyItems.setEye(selectedPanel, false);
				SellPanel.clear();
				ClientApplication.itemPanelSell.clearPanel();
				ClientApplication.itemPanelSell.loadedItem = null;
			}
		});

		mainPanel.add(newItemPanel);
		mainPanel.add(line);

		next.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				MyItems.offset += 17;
				MyItems.loadItems(true, false);
			}
		});

		previous.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				MyItems.offset -= 17;
				MyItems.loadItems(true, false);
			}
		});

		previousNext.setWidth("100%");
		mainPanel.add(previousNext);

		previousPanel.add(new Image("images/resultset_previous.png"));
		previousPanel.add(previous);
		previousPanel.setVisible(false);
		previousNext.add(previousPanel);
		previousNext.setCellHorizontalAlignment(previousPanel,
				HorizontalPanel.ALIGN_LEFT);

		nextPanel.add(next);
		nextPanel.add(new Image("images/resultset_next.png"));
		nextPanel.setVisible(false);
		previousNext.add(nextPanel);
		previousNext.setCellHorizontalAlignment(nextPanel,
				HorizontalPanel.ALIGN_RIGHT);

		/*
		 * Round Corners
		 */
		RoundedLinePanel rp = new RoundedLinePanel(RoundedPanel.ALL, 5);
		rp.setCornerColor(Color.GREY, Color.WHITE);
		rp.add(mainPanel);
		rp.setStyleName("ListRPanel");
		initWidget(rp);
	}

	public static void refresh(final boolean loadLast) {
		// TODO: For optimizing: when SellerPanel submits a new object reply
		// with list of ojects instead of calling /getitems?action=me

		RequestBuilder getItems = new RequestBuilder(RequestBuilder.GET,
				"/getitems?action=me");
		getItems.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				// TODO Fill this method
				;
			}

			public void onResponseReceived(Request request, Response response) {
				String json = response.getText();
				final JSONArray itemsJSON = JSONParser.parse(json).isObject()
						.get("Items").isArray();
				ItemLight[] items = new ItemLight[itemsJSON.size()];
				for (int j = 0; j < itemsJSON.size(); j++) {
					items[j] = new ItemLight(itemsJSON.get(j).isObject());
				}
				MyItems.items = items;
				if (loadLast) {
					MyItems.offset = ((MyItems.items.length - 1) / 17) * 17;
					loadItems(false, true);
				} else {
					if (MyItems.items.length <= MyItems.offset) {
						MyItems.offset -= 17;
					}
					loadItems(true, false);
				}
			}
		});
		try {
			getItems.send();
		} catch (RequestException e) {
			PopUpPanel.alert(ClientApplication.constants.error(), new Label(e
					.getMessage()), true);
		}
	}

	public static void loadItems(boolean loadFirst, boolean loadLast) {
		itemResults.clear();
		MyItems.loadedFirst = !loadFirst;
		for (int j = MyItems.offset; j >= 0 && j < MyItems.items.length
				&& j < MyItems.offset + 17; j++) {
			final ItemLight item = MyItems.items[j];
			HorizontalPanel itemPanel = new HorizontalPanel();
			itemPanel.setWidth("100%");
			final FlowPanel currentPanel = new FlowPanel();
			InlineHTML current = new InlineHTML(
					"<a href=\"javascript:void(0)\">"
							+ item.getShortenedTitle() + "</a>");
			current.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					MyItems.setEye(MyItems.selectedPanel, false);
					MyItems.selectedPanel = currentPanel;
					MyItems.setEye(MyItems.selectedPanel, true);
					ClientApplication.itemPanelSell.loadItem(item);
					SellPanel.loadItem(item);
				}
			});
			Image icon = new Image("images/bullet_white.png");
			icon.setStyleName("ListPanel-bullet");
			currentPanel.add(icon);
			currentPanel.add(current);
			currentPanel.setHeight("25px");
			itemPanel.add(currentPanel);
			Image delete = new Image("images/delete.png");
			delete.setStyleName("delete");
			delete.addClickHandler(getDeleteHandler(item));
			itemPanel.add(delete);
			itemPanel.setCellHorizontalAlignment(delete,
					HorizontalPanel.ALIGN_RIGHT);
			itemPanel.setCellVerticalAlignment(delete,
					HorizontalPanel.ALIGN_MIDDLE);
			MyItems.itemResults.add(itemPanel);

			if (!MyItems.loadedFirst) {
				ClientApplication.itemPanelSell.loadItem(item);
				SellPanel.loadItem(item);
				MyItems.loadedFirst = true;
				MyItems.selectedPanel = currentPanel;
				MyItems.setEye(MyItems.selectedPanel, true);
			}
			
			if (!loadFirst && loadLast && j == MyItems.items.length - 1) {
				ClientApplication.itemPanelSell.loadItem(item);
				SellPanel.loadItem(item);
				MyItems.loadedFirst = true;
				MyItems.selectedPanel = currentPanel;
				MyItems.setEye(MyItems.selectedPanel, true);
			}
		}

		title.setText(ClientApplication.constants.myStuffForSale() + " ("
				+ MyItems.items.length + ")");
		refreshPreviousNext();
	}

	public static void refreshPreviousNext() {
		previousPanel.setVisible(false);
		nextPanel.setVisible(false);
		if (MyItems.offset < MyItems.items.length) {
			if (MyItems.offset - 17 >= 0) {
				previousPanel.setVisible(true);
			}

			if (MyItems.offset + 17 < MyItems.items.length) {
				nextPanel.setVisible(true);
			}
		}
	}

	public static void setEye(FlowPanel selectedPanel2, boolean showEye) {
		if (selectedPanel2 != null) {
			Image current = (Image) selectedPanel.getWidget(0);
			if (showEye) {
				current.setUrl("images/eye.png");
			} else {
				current.setUrl("images/bullet_white.png");
			}
		}
	}

	public static ClickHandler getDeleteHandler(final ItemLight item) {
		ClickHandler deleteHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				VerticalPanel panel = new VerticalPanel();
				panel.setSpacing(10);
				panel.add(new Label(ClientApplication.constants.deleteItem()
						+ "?"));
				HorizontalPanel buttons = new HorizontalPanel();
				buttons.setSpacing(10);
				panel.add(buttons);
				Button yes = new Button(ClientApplication.constants.yes());
				yes.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						String requestData = "&id=" + item.getID();
						RequestBuilder deleteRequest = new RequestBuilder(
								RequestBuilder.POST, "/delete");
						deleteRequest.setHeader("Content-Type",
								"application/x-www-form-urlencoded");
						deleteRequest.setHeader("Content-Length", requestData
								.length()
								+ "");
						try {
							PopUpPanel.hidePopup();
							PopUpPanel.alert(ClientApplication.constants
									.deleting()
									+ "...", new Image("/images/loading.gif"),
									false);
							deleteRequest.sendRequest(requestData,
									new RequestCallback() {
										public void onError(Request request,
												Throwable exception) {
											PopUpPanel.alert(
													ClientApplication.constants
															.error(),
													new Label(exception
															.getMessage()),
													true);
										}

										public void onResponseReceived(
												Request request,
												Response response) {
											if (SellPanel.getID().equals(
													item.getID())) {
												SellPanel.clear();
											}
											refresh(false);
											PopUpPanel.hidePopup();
										}
									});
						} catch (RequestException e) {
							e.printStackTrace();
						}
					}
				});
				Button no = new Button(ClientApplication.constants.no());
				no.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						PopUpPanel.hidePopup();
					}
				});
				buttons.add(yes);
				buttons.add(no);
				PopUpPanel.alert(ClientApplication.constants.delete(), panel,
						false);
			}
		};
		return deleteHandler;
	}
}