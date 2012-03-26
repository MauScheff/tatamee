package com.tatamee.client.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SettingsPanel extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();

	public SettingsPanel() {
		Label changeHomeLocation = new Label(ClientApplication.constants.changeHomeLocation());
		changeHomeLocation.setStyleName("SettingsPanel-link");
		changeHomeLocation.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.hidePopup(ClientApplication.constants.settings());
				PopUpPanel.alert(
						ClientApplication.constants.pleaseMarkHome(),
						FindHomePopup.enterLocation, false);
			}
		});
		mainPanel.add(changeHomeLocation);
		
		Label changeDefaultCurrency = new Label(ClientApplication.constants.changeCurrency());
		changeDefaultCurrency.setStyleName("SettingsPanel-link");
		changeDefaultCurrency.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.hidePopup(ClientApplication.constants.settings());
				PopUpPanel.alert(
						ClientApplication.constants.pleaseCurrency(),
						new ChangeCurrencyPopup(), true);
			}
		});
		mainPanel.add(changeDefaultCurrency);
		
		Label changeDefaultLocale = new Label(ClientApplication.constants.changeLanguage());
		changeDefaultLocale.setStyleName("SettingsPanel-link");
		changeDefaultLocale.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.hidePopup(ClientApplication.constants.settings());
				PopUpPanel.alert(
						ClientApplication.constants.pleaseLanguage(),
						new ChangeLanguagePopup(), false);
			}
		});
		mainPanel.add(changeDefaultLocale);
		
		initWidget(mainPanel);
	}
}