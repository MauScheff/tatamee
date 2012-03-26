/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

/*
 * Top Panel (Logo, locate, list, login, logout, etc..)
 */
public class BottomPanel extends Composite {
	private static final HorizontalPanel mainPanel = new HorizontalPanel();
	
	public BottomPanel() {
		mainPanel.setStyleName("BottomPanel");
		HTML about = new HTML("<a href=\"/static/about.html\">" + ClientApplication.constants.about() + "</a>");
		about.setStyleName("BottomPanelItem");
//		HTML features = new HTML("<a href=\"#\">" + ClientApplication.constants.features() + "</a>");
//		features.setStyleName("BottomPanelItem");
		HTML help = new HTML("<a href=\"/static/help.html\">" + ClientApplication.constants.help() + "</a>");
		help.setStyleName("BottomPanelItem");
		HTML privacy = new HTML("<a href=\"#\">" + ClientApplication.constants.privacy() + "</a>");
		privacy.setStyleName("BottomPanelItem");
		HTML terms = new HTML("<a href=\"#\">" + ClientApplication.constants.terms() + "</a>");
		terms.setStyleName("BottomPanelItem");
		HTML developers = new HTML("<a href=\"#\">" + "Developers" + "</a>");
//		jobs.setStyleName("BottomPanelItem");
//		HTML blog = new HTML("<a href=\"#\">" + ClientApplication.constants.blog() + "</a>"); 
//		blog.setStyleName("BottomPanelItem");
		HTML copyright = new HTML("© Tatamee 2009");
		copyright.setStyleName("BottomPanelItem");
//		mainPanel.add(developers);
		mainPanel.add(about);
//		mainPanel.add(blog);
//		mainPanel.add(features);
		mainPanel.add(help);
//		mainPanel.add(jobs);
		mainPanel.add(privacy);
		mainPanel.add(terms);
		Image twitter = new Image("images/twitter.png");
		twitter.setStyleName("BottomPanelIcon");
		mainPanel.add(twitter);
		Image facebook = new Image("images/facebook.png");
		facebook.setStyleName("BottomPanelIcon");
		mainPanel.add(facebook);
		mainPanel.add(copyright);
		
		initWidget(mainPanel);
	}
}