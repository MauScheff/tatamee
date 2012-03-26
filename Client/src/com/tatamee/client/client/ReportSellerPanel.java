package com.tatamee.client.client;

import org.cobogw.gwt.user.client.CSS;
import org.cobogw.gwt.user.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class ReportSellerPanel extends Composite {

	// TODO: ADD RECAPTCHA
	private VerticalPanel mainPanel = new VerticalPanel();
	private TextArea message = new TextArea();
//	private static NamedFrame recaptcha = new NamedFrame("reCaptcha");

	public ReportSellerPanel(Item item) {
		Hidden id = new Hidden("id");
		id.setValue(item.getID());
		mainPanel.add(id);
		mainPanel.setSpacing(4);

		final FormPanel form = new FormPanel();
		form.setAction("/ratings/report");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
//		final Hidden challengeField = new Hidden();
//		challengeField.setName("recaptcha_challenge_field");
//		final Hidden responseField = new Hidden();
//		responseField.setName("recaptcha_response_field");
//		mainPanel.add(challengeField);
//		mainPanel.add(responseField);

//		form.addSubmitHandler(new SubmitHandler() {
//			public void onSubmit(SubmitEvent event) {
//				challengeField.setValue(getChallengeString());
//				responseField.setValue(getResponseString());
//			}
//		});

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				if (event.getResults() == "500") {

				} else {
					PopUpPanel.hidePopup();
					VerticalPanel finalPanel = new VerticalPanel();
					finalPanel.setSpacing(7);
					finalPanel.setWidth("300px");
					Label sent = new Label(ClientApplication.constants
							.yourReport());
					finalPanel.add(sent);

					PopUpPanel.alert(ClientApplication.constants.thankYou()
							+ "!", finalPanel, true);
				}
			}
		});

		message.setName("message");
		message.setCharacterWidth(40);
		message.setVisibleLines(5);

		/*
		 * Assembly
		 */
		Label ratingLabel = new Label(ClientApplication.constants.reason());
		ratingLabel.setStyleName("title2");
		mainPanel.add(ratingLabel);
		ListBox rating = new ListBox();
		rating.setName("report");
		rating.addItem(ClientApplication.constants.reason7(), "7");
		rating.addItem(ClientApplication.constants.reason1(), "1");
		rating.addItem(ClientApplication.constants.reason2(), "2");
		rating.addItem(ClientApplication.constants.reason3(), "3");
		rating.addItem(ClientApplication.constants.reason4(), "4");
		rating.addItem(ClientApplication.constants.reason5(), "5");
		rating.addItem(ClientApplication.constants.reason6(), "6");

		mainPanel.add(rating);
		Label howLabel = new Label(ClientApplication.constants.explain() + ":");
		howLabel.setStyleName("title2");
		mainPanel.add(howLabel);
		mainPanel.add(message);

//		if (ClientApplication.userEmail.equals("")) {
//			mainPanel.add(recaptcha);
//			recaptcha.setSize("340px", "150px");
//			recaptcha.setStyleName("frame-noborder");
//			recaptcha.setUrl("/captcha");
//		}

		Button submit = new Button(ClientApplication.constants.report());
		submit.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});

		CSS.setProperty(submit, CSS.A.FONT_WEIGHT, CSS.V.FONT_WEIGHT.BOLD);
		HorizontalPanel submitPanel = new HorizontalPanel();
		submitPanel.setWidth("100%");
		submitPanel.add(submit);
		submitPanel.setCellHorizontalAlignment(submit,
				HorizontalPanel.ALIGN_RIGHT);

		mainPanel.add(submitPanel);
		form.add(mainPanel);
		initWidget(form);
	}

	private static native String getChallengeString() /*-{
		var result = $wnd.frames["reCaptcha"].document.Recaptcha.get_challenge();
		return result;
	}-*/;

	private static native String getResponseString() /*-{
		var result = $wnd.frames["reCaptcha"].document.Recaptcha.get_response();
		return result;
	}-*/;
}