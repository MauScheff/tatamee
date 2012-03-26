/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import org.cobogw.gwt.user.client.Color;
import org.cobogw.gwt.user.client.ui.RoundedLinePanel;
import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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

public class ListPanel extends Composite {
	private static VerticalPanel mainPanel = new VerticalPanel();
	public static int offset = 0;
	public static ItemLight[] items = new ItemLight[0];
	public static HTML nothingHere = new HTML(ClientApplication.constants
			.liveAroundHere()
			+ " <a href=\"/home#sell\">"
			+ ClientApplication.constants.beTheFirst() + "</a>");
	public static HTML zoomCloser = new HTML(ClientApplication.constants
			.pleaseZoom());
	public static boolean loadedFirst = false;
	private static FlowPanel selectedPanel = null;
	private static Label title = new Label(ClientApplication.constants
			.searchResults());
	private static VerticalPanel searchResults = new VerticalPanel();
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

	public ListPanel() {
		mainPanel.setHeight("520px");
		mainPanel.setStyleName("ListPanel");
		title.setStyleName("title");
		mainPanel.add(title);
		mainPanel.setCellHeight(title, "20px");
		ScrollPanel resultsWrapper = new ScrollPanel();
		resultsWrapper.setHeight("440px");
		resultsWrapper.setWidth("200px");
		resultsWrapper.add(searchResults);
		mainPanel.add(resultsWrapper);
		mainPanel.add(line);

		next.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListPanel.offset += 18;
				ListPanel.loadItems();
			}
		});

		previous.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListPanel.offset -= 18;
				ListPanel.loadItems();
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
		 * Rounder Corners
		 */
		RoundedLinePanel rp = new RoundedLinePanel(RoundedPanel.ALL, 5);
		rp.setCornerColor(Color.GREY, Color.WHITE);
		rp.add(mainPanel);
		rp.setStyleName("ListRPanel");
		initWidget(rp);
	}

	public static void loadItems() {
		searchResults.clear();
		ListPanel.loadedFirst = false;
		for (int j = ListPanel.offset; j >= 0 && j < ListPanel.items.length
				&& j < ListPanel.offset + 17; j++) {
			final ItemLight item = ListPanel.items[j];
			final FlowPanel currentPanel = new FlowPanel();
			InlineHTML current = new InlineHTML(
					"<a href=\"javascript:void(0)\">"
							+ item.getShortenedTitle() + "</a>");
			current.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					ListPanel.setEye(ListPanel.selectedPanel, false);
					ListPanel.selectedPanel = currentPanel;
					ListPanel.setEye(ListPanel.selectedPanel, true);
					ClientApplication.itemPanel.loadItem(item);
				}
			});
			Image icon = new Image("images/bullet_white.png");
			icon.setStyleName("ListPanel-bullet");
			currentPanel.add(icon);
			currentPanel.add(current);
			currentPanel.setHeight("25px");
			searchResults.add(currentPanel);
			if (!ListPanel.loadedFirst) {
				ClientApplication.itemPanel.loadItem(item);
				ListPanel.loadedFirst = true;
				ListPanel.selectedPanel = currentPanel;
				ListPanel.setEye(ListPanel.selectedPanel, true);
			}
		}
		title.setText(ListPanel.items.length + " "
				+ ClientApplication.constants.searchResults());
		refreshPreviousNext();
	}

	public static void refreshPreviousNext() {
		previousPanel.setVisible(false);
		nextPanel.setVisible(false);
		if (ListPanel.offset < ListPanel.items.length) {
			if (ListPanel.offset - 18 >= 0) {
				previousPanel.setVisible(true);
			}

			if (ListPanel.offset + 18 < ListPanel.items.length) {
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

	public static void clearItemsAndSetContent(HTML content) {
		ListPanel.items = new ItemLight[0];
		title.setText(ListPanel.items.length + " "
				+ ClientApplication.constants.searchResults());
		ClientApplication.itemPanel.clearPanel();
		ListPanel.loadedFirst = false;
		searchResults.clear();
		searchResults.add(content);
	}
	
	public static void clearItemsAndSetContent1Result(HTML content) {
		title.setText(1 + " "
				+ ClientApplication.constants.searchResults());
		ListPanel.loadedFirst = true;
		searchResults.clear();
		searchResults.add(content);
	}
}