package com.tatamee.client.client;

import org.cobogw.gwt.user.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

public class ChangeLanguagePopup extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private ListBox localeSelect = new ListBox();

	public ChangeLanguagePopup() {
		mainPanel.setSpacing(7);

		final FormPanel form = new FormPanel();
		form.setAction("/updatelocale");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				PopUpPanel.hidePopup();

				VerticalPanel finalPanel = new VerticalPanel();
				finalPanel.setSpacing(7);
				finalPanel.setWidth("300px");
				Label info = new Label(ClientApplication.constants.languageUpdated());
				finalPanel.add(info);
				Button refresh = new Button(ClientApplication.constants.close());
				refresh.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						PopUpPanel.hidePopup();
						refresh();
					}
				});
				finalPanel.add(refresh);

				PopUpPanel.alert(ClientApplication.constants.thankYou() + "!", finalPanel, false);
			}
		});

		Label manually = new Label(ClientApplication.constants.chooseLanguage());
		mainPanel.add(manually);

		localeSelect.setName("locale");
		mainPanel.add(localeSelect);
		populateLocaleList();

		Button save = new Button(ClientApplication.constants.save());
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});
		mainPanel.add(save);
		form.add(mainPanel);

		initWidget(form);
	}

	private void populateLocaleList() {
		localeSelect.addItem(ClientApplication.constants.english(), "en");
		localeSelect.addItem(ClientApplication.constants.spanish(), "es");
//		localeSelect.addItem(ClientApplication.constants.french(), "fr");
//		localeSelect.addItem(ClientApplication.constants.albanian(), "sq");
//		localeSelect.addItem(ClientApplication.constants.bulgarian(), "bg");
//		localeSelect.addItem(ClientApplication.constants.catalan(), "ca");
//		localeSelect.addItem(ClientApplication.constants.chinese(), "zh");
//		localeSelect.addItem(ClientApplication.constants.croatian(), "hr");
//		localeSelect.addItem(ClientApplication.constants.czech(), "cs");
//		localeSelect.addItem(ClientApplication.constants.danish(), "da");
//		localeSelect.addItem(ClientApplication.constants.dutch(), "nl");
//		localeSelect.addItem(ClientApplication.constants.estonian(), "et");
//		localeSelect.addItem(ClientApplication.constants.finnish(), "fi");
//		localeSelect.addItem(ClientApplication.constants.galician(), "gl");
//		localeSelect.addItem(ClientApplication.constants.german(), "de");
//		localeSelect.addItem(ClientApplication.constants.greek(), "el");
//		localeSelect.addItem(ClientApplication.constants.hindi(), "hi");
//		localeSelect.addItem(ClientApplication.constants.hungarian(), "hu");
//		localeSelect.addItem(ClientApplication.constants.indonesian(), "id");
//		localeSelect.addItem(ClientApplication.constants.italian(), "it");
//		localeSelect.addItem(ClientApplication.constants.japanese(), "ja");
//		localeSelect.addItem(ClientApplication.constants.korean(), "ko");
//		localeSelect.addItem(ClientApplication.constants.latvian(), "lv");
//		localeSelect.addItem(ClientApplication.constants.lithuanian(), "lt");
//		localeSelect.addItem(ClientApplication.constants.norweigan(), "no");
//		localeSelect.addItem(ClientApplication.constants.polish(), "pl");
//		localeSelect.addItem(ClientApplication.constants.portuguese(), "pt");
//		localeSelect.addItem(ClientApplication.constants.romanian(), "ro");
//		localeSelect.addItem(ClientApplication.constants.russian(), "ru");
//		localeSelect.addItem(ClientApplication.constants.serbian(), "sr");
//		localeSelect.addItem(ClientApplication.constants.slovak(), "sk");
//		localeSelect.addItem(ClientApplication.constants.slovenian(), "sl");
//		localeSelect.addItem(ClientApplication.constants.swedish(), "sv");
//		localeSelect.addItem(ClientApplication.constants.thai(), "th");
//		localeSelect.addItem(ClientApplication.constants.turkish(), "tr");
//		localeSelect.addItem(ClientApplication.constants.ukranian(), "uk");
//		localeSelect.addItem(ClientApplication.constants.vietnamese(), "vi");
	}

	private native void refresh()/*-{
		$wnd.location.reload();
	}-*/;
}