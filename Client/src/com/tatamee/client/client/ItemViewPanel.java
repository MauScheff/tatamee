package com.tatamee.client.client;

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ItemViewPanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();

	public ItemViewPanel(Item item) {
		mainPanel.setSpacing(7);
		mainPanel.add(new PicturesPanel(item));
		HorizontalPanel locationPanel = new HorizontalPanel();
		MapWidget map = new MapWidget();
		map.setStyleName("usermap");
		map.setSize("200px", "200px");
		map.setCenter(item.getLocation());
		map.setZoomLevel(12);
		map.addOverlay(new Marker(item.getLocation()));
		locationPanel.add(map);

		VerticalPanel featuresPanel = new VerticalPanel();
		Label featuresLabel = new Label(ClientApplication.constants.tags());
		featuresLabel.setStyleName("title2");
		featuresPanel.add(featuresLabel);
		featuresPanel.add(new Label(item.getTags()));
		locationPanel.add(featuresPanel);

		mainPanel.add(locationPanel);
		Label descriptionLabel = new Label(ClientApplication.constants.description());
		descriptionLabel.setStyleName("title2");
		mainPanel.add(descriptionLabel);
		mainPanel.add(new Label(item.getDescription()));

		if (item.getComments() > 0) {
			Label commentsLabel = new Label(ClientApplication.constants.comments());
			commentsLabel.setStyleName("title2");
			mainPanel.add(commentsLabel);
			mainPanel.add(new MessagesPanel(item, "comments"));
		}

		if (item.getGoodRatings() > 0) {
			Label goodRatingsLabel = new Label(ClientApplication.constants.goodRatings());
			goodRatingsLabel.setStyleName("title2");
			mainPanel.add(goodRatingsLabel);
			mainPanel.add(new MessagesPanel(item, "good_ratings"));
		}

		if (item.getBadRatings() > 0) {
			Label badRatingsLabel = new Label(ClientApplication.constants.badRatings());
			badRatingsLabel.setStyleName("title2");
			mainPanel.add(badRatingsLabel);
			mainPanel.add(new MessagesPanel(item, "bad_ratings"));
		}
		
		VerticalPanel contactPanel = new VerticalPanel();
		Label contactLabel =  new Label(ClientApplication.constants.contactSeller());
		contactLabel.setStyleName("title2");
		contactPanel.add(contactLabel);
		MailPanel mailPanel = new MailPanel(item, false);
		mailPanel.setStyleName("border");
		contactPanel.add(mailPanel);
		
		VerticalPanel reportPanel = new VerticalPanel();
		Label reportLabel =  new Label(ClientApplication.constants.reportSeller());
		reportLabel.setStyleName("title2");
		reportPanel.add(reportLabel);
		ReportSellerPanel reportSellerPanel = new ReportSellerPanel(item);
		reportSellerPanel.setStyleName("border");
		reportPanel.add(reportSellerPanel);
		
		HorizontalPanel lastPanel = new HorizontalPanel();
		lastPanel.setSpacing(10);
		lastPanel.add(contactPanel);
		lastPanel.add(reportPanel);
		mainPanel.add(lastPanel);
		
		initWidget(mainPanel);
	}

}