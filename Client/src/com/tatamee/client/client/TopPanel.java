/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/*
 * TODO: Internationalize
 * TODO: Add different sizes for different resolutions
 * TODO: Change the way in which user reports and succeses look
 * TODO: When click on username in item panel load that users items and location
 */

/*
 * Top Panel (Logo, locate, list, login, logout, etc..)
 */
public class TopPanel extends Composite {
	private static final HorizontalPanel mainPanel = new HorizontalPanel();
	private static InlineHTML settings = new InlineHTML("<a href=\"javascript:void(0);\">" + ClientApplication.constants.settings() + "</a>");
	private HorizontalPanel linksPanel = new HorizontalPanel();

	public TopPanel(String user, String links) {
		mainPanel.setWidth("100%");
		if (user.equals("")) {
			user = ClientApplication.constants.anonymous();
		} else {
			linksPanel.add(settings);
			settings.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					PopUpPanel.alert(ClientApplication.constants.settings(), new SettingsPanel(), true);
				}
			});
		}
		mainPanel.setStyleName("TopPanel");
		Image logo = new Image("/images/logo.png");
		mainPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		VerticalPanel logoPanel = new VerticalPanel();
		logoPanel.add(logo);
		InlineLabel classifieds = new InlineLabel(ClientApplication.constants.classifieds());
		classifieds.setStyleName("slogan");
		logoPanel.add(classifieds);
//		logoPanel.setCellHorizontalAlignment(classifieds, HorizontalPanel.ALIGN_CENTER);
		mainPanel.add(logoPanel);
		VerticalPanel accountPanel = new VerticalPanel();
		HTML greeting = new HTML(ClientApplication.constants.hello() + " "
				+ user);
		greeting.setStyleName("TopPanel-greeting");
		accountPanel.add(greeting);
		String login = "Login";
		if (links.indexOf("Logout") > 0) {
			login = "Logout";
		}
		HTML linksHTML = new HTML("<a href=\"" + links + "\">" + login + "</a>");
		linksPanel.setSpacing(3);
		settings.setStyleName("TopPanel-links");
		linksPanel.add(linksHTML);
		accountPanel.add(linksPanel);
		accountPanel.setCellHorizontalAlignment(linksPanel,
				VerticalPanel.ALIGN_RIGHT);
		mainPanel.add(accountPanel);
		mainPanel.setCellHorizontalAlignment(accountPanel,
				HorizontalPanel.ALIGN_RIGHT);
		initWidget(mainPanel);
	}
}