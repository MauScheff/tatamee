package com.tatamee.client.client;

import org.cobogw.gwt.user.client.CSS;
import org.cobogw.gwt.user.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

public class MailPanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private VerticalPanel wrapperPanel = new VerticalPanel();
	private TextBox from = new TextBox();
	private TextArea message = new TextArea();
	public static final String REGEX_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

	//TODO Limit comment size client size to 400 chars
	public MailPanel(Item item, final boolean myself) {

		wrapperPanel.setSpacing(7);
		Hidden id = new Hidden("id");
		id.setValue(item.getID());
		wrapperPanel.add(id);

		Label fromLabel = new Label(ClientApplication.constants.from());
		fromLabel.setStyleName("title2");
		Label messageLabel = new Label(ClientApplication.constants.message());
		messageLabel.setStyleName("title2");

		final FormPanel form = new FormPanel();
		form.setAction("/mail");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		form.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				try {
					if (!from.getText().matches(REGEX_EMAIL)) {
						throw new Exception(ClientApplication.constants.invalidEmail());
					}
					if (message.getText().length() <= 0) {
						throw new Exception(ClientApplication.constants.pleaseMessage());
					}
				} catch (Exception e) {
					PopUpPanel.alert(ClientApplication.constants.error(), new Label(e.getMessage()), true);
					event.cancel();
				}
			}
		});

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				PopUpPanel.hidePopup();
				if (myself) {
					PopUpPanel.alert(ClientApplication.constants.thankYou(), new Label(
							ClientApplication.constants.commentPosted() + "!"), true);
					ClientApplication.itemPanelSell.incrementComments();
				} else {
					PopUpPanel.alert(ClientApplication.constants.thankYou(), new Label(
							ClientApplication.constants.yourMessage() + "!"), true);
					ClientApplication.itemPanel.incrementComments();
				}
			}
		});

		from.setText(ClientApplication.userEmail);
		from.setEnabled(false);
		if (ClientApplication.userEmail.equals("")) {
			from.setEnabled(true);
		}

		message.setCharacterWidth(40);
		message.setVisibleLines(8);

		/*
		 * Attributes submitted to server
		 */
		from.setName("from");
		message.setName("message");

		/*
		 * Assembly
		 */
		HorizontalPanel fromPanel = new HorizontalPanel();
		fromPanel.setWidth("300px");
		fromPanel.add(fromLabel);
		fromPanel.add(from);
		fromPanel.setCellHorizontalAlignment(from, HorizontalPanel.ALIGN_RIGHT);
		wrapperPanel.add(fromPanel);
		wrapperPanel.add(messageLabel);
		wrapperPanel.add(message);

		/*
		 * TODO: reCaptcha
		 */
		// HTML reCaptcha = new HTML(
		// "<script type=\"text/javascript\" src=\"http://api.recaptcha.net/challenge?k=6LdIdwgAAAAAAARG_GYNF_8RggdRAsANZ1h74zY0\"> </script> <noscript> <iframe src=\"http://api.recaptcha.net/noscript?k=6LdIdwgAAAAAAARG_GYNF_8RggdRAsANZ1h74zY0\" height=\"300\" width=\"500\" frameborder=\"0\"></iframe><br> <textarea name=\"recaptcha_challenge_field\" rows=\"3\" cols=\"40\"> </textarea> <input type=\"hidden\" name=\"recaptcha_response_field\" value=\"manual_challenge\"> </noscript>");
		// mainPanel.add(reCaptcha);
		if (!myself) {
			HorizontalPanel commentPanel = new HorizontalPanel();
			CheckBox comment = new CheckBox();
			comment.setValue(true);
			comment.setName("comment");
			commentPanel.add(comment);
			commentPanel.add(new Label(ClientApplication.constants.postAsComment()));
			commentPanel.setSpacing(5);
			wrapperPanel.add(commentPanel);
		}

		Button submit;
		if (myself) {
			submit = new Button(ClientApplication.constants.post());
		} else {
			submit = new Button(ClientApplication.constants.send());
		}
		submit.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});
		CSS.setProperty(submit, CSS.A.FONT_WEIGHT, CSS.V.FONT_WEIGHT.BOLD);
		HorizontalPanel submitPanel = new HorizontalPanel();
		submitPanel.setWidth("300px");
		submitPanel.add(submit);
		submitPanel.setCellHorizontalAlignment(submit,
				HorizontalPanel.ALIGN_RIGHT);

		VerticalPanel wrapperPanelOuter = new VerticalPanel();
		wrapperPanelOuter.add(wrapperPanel);

		wrapperPanelOuter.add(submitPanel);

		form.add(wrapperPanelOuter);
		mainPanel.add(form);
		initWidget(mainPanel);
	}
}