/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/*
 * TESTING BRANCHES
 * TODO: When click on username in item panel load that users items and location 
 * TODO: Change all links for hyperlink objects (for history frame suppor)
 * TODO: Change HTMLs for Labels (Security)
 * TODO: Location not found popup (ADD CLOSE BUTTON OR SOMETHING)
 * TODO: Create new Item / Edit Item label on top of sell Panel
 * TODO: Change layout of Sell Panel so that it doesnt change when different sizes of thumbnails
 * TODO: reCaptcha on Contact Panel
 * TODO: Use custom even handlers for tab changes and clicks 
 * TODO: Event Bus. Make a class to handle aaaaall events and do calls with GWT.runAsync()
 */

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ClientApplication implements EntryPoint {

	private HorizontalPanel searchPanelOuter = null;
	private HorizontalPanel listPanelOuter = null;
	private VerticalPanel itemPanelOuter = null;

	private SellPanel sellPanel = null;
	private ListPanel listPanel = null;
	private MyItems myItems = null;
	public static ItemPanel itemPanel = null;
	public static ItemPanel itemPanelSell = null;

	// private FormTest sellPanel = null;

	private SearchPanel searchPanel = null;

	private MapPanel mapPanel = null;

	public static String user;
	public static String userEmail;

	public static Language constants = GWT.create(Language.class);
	public static ParamLanguage paramConstants = GWT
			.create(ParamLanguage.class);
	public static String currency = "$";

	// 0 = Buy
	// 1 = Sell
	// 2 = URL
	public static int mode = 0;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		/*
		 * Load URL Tokens
		 */
		String[] token = History.getToken().split("/");
		if (token != null && token[0] != null) {
			if (token[0].equals("sell")) {
				mode = 1;
			} else if (token[0].equals("buy")) {
				if (token.length >= 3 && token[1] != null && token[2] != null) {
					if (token[1].equals("item")) {
						mode = 2;
					}
				} else if (getPathName().indexOf("home") >= 0) {
					mode = 1;
				}
			}
		}

		/*
		 * Load Variables from App Engine
		 */
		final String user = loadVariable("user");
		ClientApplication.userEmail = loadVariable("userEmail");
		final String links = loadVariable("links");
		ClientApplication.user = user;
		final String currency = loadVariable("currency");
		ClientApplication.currency = currency;

		/*
		 * Sell/Buy Mode Listener for Tabs
		 */
		ClickHandler modeListener = new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (event.getSource().toString().indexOf("sell") > 0)
					setSellInterface(user);
				else
					setBuyInterface(user);
			}
		};

		/*
		 * Outer Layout
		 */
		VerticalPanel mainPanelOuter = new VerticalPanel();
		mainPanelOuter.setWidth("100%");
		mainPanelOuter.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		searchPanelOuter = new HorizontalPanel();
		searchPanelOuter.setStyleName("searchPanelOuter");
		VerticalPanel topPanelOuter = new VerticalPanel();
		listPanelOuter = new HorizontalPanel();
		itemPanelOuter = new VerticalPanel();

		/*
		 * Inner Layout
		 */
		TopPanel topPanel = new TopPanel(user, links);
		TabPanel tabPanel = new TabPanel(modeListener);
		listPanel = new ListPanel();
		itemPanel = new ItemPanel(false);
		itemPanelSell = new ItemPanel(true);
		searchPanel = new SearchPanel();
		BottomPanel bottomPanel = new BottomPanel();

		sellPanel = new SellPanel();
		myItems = new MyItems();
		mapPanel = new MapPanel();
		mapPanel.setStyleName("MapPanel");

		/*
		 * Layout Assembly
		 */
		searchPanelOuter.add(searchPanel);
		searchPanelOuter.add(mapPanel);
		setBuyInterface(user);
		itemPanelOuter.add(searchPanelOuter);
		itemPanelOuter.add(itemPanel);
		listPanelOuter.add(itemPanelOuter);
		listPanelOuter.add(listPanel);
		topPanelOuter.add(listPanelOuter);

		/*
		 * Rounder Corners
		 */
		RoundedPanel rp = new RoundedPanel(RoundedPanel.ALL, 5);
		rp.setCornerColor("#e2e2e2");
		rp.add(topPanelOuter);
		topPanelOuter.setStyleName("topPanelOuter");
		rp.setStyleName("topPanelOuterR");

		mainPanelOuter.add(topPanel);
		mainPanelOuter.add(tabPanel);
		mainPanelOuter.add(rp);
		mainPanelOuter.add(bottomPanel);
		RootPanel.get().add(mainPanelOuter);

		/*
		 * Panel Sizes
		 */
		topPanel.setSize("835px", "60px");

		/*
		 * History Token Handler
		 */
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event) {
				String token = event.getValue();
				if (token.equals("sell")) {
					setSellInterface(user);
					mode = 1;
				} else if (token.equals("buy")) {
					setBuyInterface(user);
					mode = 0;
				}
			}
		});

		if (mode == 0) {
			trackPageView("buy");
			SearchPanel.searchBox.setFocus(true);
		} else if (mode == 1) {
			trackPageView("sell");
			setSellInterface(user);
		} else if (mode == 2) {
			trackPageView("load_url");
			itemPanel.loadItem(token[2]);
		}

		 if (!ClientApplication.user.equals("") && mode != 2) {
			 new FindHomePopup();
		 }

	}

	private void setSellInterface(String user) {
		TabPanel.setSell();
		searchPanelOuter.clear();
		listPanelOuter.remove(listPanel);
		listPanelOuter.add(myItems);
		searchPanelOuter.add(sellPanel);
		itemPanelOuter.remove(itemPanel);
		itemPanelOuter.add(itemPanelSell);
		MyItems.refresh(false);
		SellPanel.clear();
		ClientApplication.itemPanelSell.clearPanel();
		trackPageView("#sell");
	}

	private void setBuyInterface(String user) {
		TabPanel.setBuy();
		searchPanelOuter.clear();
		listPanelOuter.remove(myItems);
		listPanelOuter.add(listPanel);
		searchPanelOuter.add(searchPanel);
		searchPanelOuter.add(mapPanel);
		itemPanelOuter.remove(itemPanelSell);
		itemPanelOuter.add(itemPanel);
		trackPageView("#buy");
		SearchPanel.searchBox.setFocus(true);
	}

	public static String capitalize(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	private native String loadVariable(String name)/*-{
		return $wnd[name];
	}-*/;

	private static native void trackPageView(String url) /*-{
		var pageTracker = $wnd._gat._getTracker("UA-2942173-2");
		pageTracker._trackPageview(url);
	}-*/;

	public static native String getPathName() /*-{
		return $wnd.location.pathname;
	}-*/;
}
