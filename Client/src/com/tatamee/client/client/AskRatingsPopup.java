package com.tatamee.client.client;

import org.cobogw.gwt.user.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

public class AskRatingsPopup extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	public static final String REGEX_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
	
	public AskRatingsPopup(final Item item) {
		Hidden id = new Hidden("id");
		id.setValue(item.getID());
		mainPanel.add(id);
		
		mainPanel.setSpacing(10);
		Label infoLabel = new Label(ClientApplication.constants.buyerEmail());
		mainPanel.add(infoLabel);
		HorizontalPanel emailPanel = new HorizontalPanel();
		emailPanel.setSpacing(5);
		emailPanel.add(new Label(ClientApplication.constants.email()));
		final TextBox email = new TextBox();
		email.setName("email");
		emailPanel.add(email);
		mainPanel.add(emailPanel);
		
		final FormPanel form = new FormPanel();
		form.setAction("/ratings");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		
		form.addSubmitCompleteHandler(new SubmitCompleteHandler(){
			public void onSubmitComplete(SubmitCompleteEvent event) {
				PopUpPanel.hidePopup();
				PopUpPanel.alert(ClientApplication.constants.thankYou() + "!", new Label("Ratings request has been sent!"), true);
			}
		});
		
		form.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				try {
					if (!email.getText().matches(REGEX_EMAIL)) {
						throw new Exception(ClientApplication.constants.invalidEmail());
					}
				} catch (Exception e) {
					PopUpPanel.alert(ClientApplication.constants.error(), new Label(e.getMessage()), true);
					event.cancel();
				}
			}
		});
		
		Button submit = new Button(ClientApplication.constants.askRating());
		mainPanel.add(submit);
		submit.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});
		
		form.add(mainPanel);
		initWidget(form);
	}
}