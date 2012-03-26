/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;

/*
 * Top Panel (Logo, locate, list, login, logout, etc..)
 */
public class TabPanel extends Composite {
	private static final HorizontalPanel mainPanel = new HorizontalPanel();
	final static RoundedPanel sellTab = new RoundedPanel(RoundedPanel.TOP, 5);
	final static HorizontalPanel sell = new HorizontalPanel();
	final static RoundedPanel buyTab = new RoundedPanel(RoundedPanel.TOP, 5);
	final static HorizontalPanel buy = new HorizontalPanel();
	final static HTML sellLabel = new HTML("<a href=\"home#sell\">" + ClientApplication.constants.sell() + "</a>");
	final static Hyperlink buyLabel = new Hyperlink(ClientApplication.constants.buy(), "buy");

	public TabPanel(ClickHandler modeListener) {
		mainPanel.setStyleName("TabPanel");
		mainPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);

		/*
		 * Sell Tab
		 */
		HorizontalPanel sellWrapper = new HorizontalPanel();
		sell.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		sell.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		sellLabel.setStyleName("tabLabel");
		sell.add(sellLabel);
		sellTab.add(sell);
		sellWrapper.add(sellTab);
		sellLabel.addClickHandler(modeListener);

		/*
		 * Buy Tab
		 */
		HorizontalPanel buyWrapper = new HorizontalPanel();
		buy.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		buy.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		buyLabel.setStyleName("tabLabel");
		buy.add(buyLabel);
		buyTab.add(buy);
		buyWrapper.add(buyTab);
		buyWrapper.setWidth("100%");
		buyLabel.addClickHandler(modeListener);
		
		HorizontalPanel superWrapper = new HorizontalPanel();

		superWrapper.add(buyWrapper);
		superWrapper.add(sellWrapper);
		mainPanel.add(superWrapper);

		initWidget(mainPanel);
	}

	public static void setBuy() {
		sellTab.setCornerColor("#acacac");
		sell.setStyleName("inactiveTab");
		sellTab.setStyleName("inactiveRTab");
		buyTab.setCornerColor("#e2e2e2");
		buy.setStyleName("activeTab");
		buyTab.setStyleName("activeRTab");
	}

	public static void setSell() {
		sellTab.setCornerColor("#e2e2e2");
		buy.setStyleName("inactiveTab");
		buyTab.setStyleName("inactiveRTab");
		buyTab.setCornerColor("#acacac");
		sell.setStyleName("activeTab");
		sellTab.setStyleName("activeRTab");
	}
}