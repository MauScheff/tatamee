/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DescriptionEditor extends Composite {
	
	private static TextArea description = new TextArea();
	private static VerticalPanel mainPanel = new VerticalPanel();
	
	public DescriptionEditor() {
		description.setName("description");
		description.setWidth("303px");
		description.setHeight("160px");
		mainPanel.setSpacing(3);
		FlowPanel descriptionLabelPanel = new FlowPanel();
		InlineLabel descriptionLabel = new InlineLabel(ClientApplication.constants.description());
		descriptionLabel.setStyleName("title2-icon-left");
		descriptionLabelPanel.add(new Image("images/information.png"));
		descriptionLabelPanel.add(descriptionLabel);
		mainPanel.add(descriptionLabelPanel);
		
		
		description.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				ClientApplication.itemPanelSell.setDescription(description.getText());
			}
		});
		mainPanel.add(description);
		initWidget(mainPanel);
	}

	public String getText() {
		return description.getText();
	}
	
	public void setText(String str) {
		description.setText(str);
	}
	
	public void clear() {
		description.setText("");
	}

}
