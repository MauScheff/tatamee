package com.tatamee.client.client;

import org.cobogw.gwt.user.client.ui.Button;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

public class ChangeCurrencyPopup extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private ListBox currencySelect = new ListBox();
	public static final String[] CURRENCIES = { "AED د.", "AFN ", "ALL L",
			"AMD", "ANG ƒ", "AOA Kz", "ARS $", "AUD $", "AWG ƒ", "AZN",
			"BAM KM", "BBD $", "BDT", "BGN лв", "BHD ب.د", "BIF Fr", "BMD $",
			"BND $", "BOB Bs.", "BRL R$", "BSD $", "BWP P", "BYR Br", "BZD $",
			"CAD $", "CDF Fr", "CHF Fr", "CLP $", "CNY 元", "COP $", "CRC ₡",
			"CUP $", "CVE $", "CZK Kč", "DJF Fr", "DKK Kr", "DKK kr", "DOP $",
			"DZD دج", "EEK KR", "EGP £", "ERN Nfk", "ETB ¤", "EUR €", "FJD $",
			"FKP £", "GBP £", "GEL ლ", "GHS ₵", "GIP £", "GMD D", "GNF Fr",
			"GTQ Q", "GYD $", "HKD $", "HNL L", "HRK kn", "HTG G", "HUF Ft",
			"IDR Rp", "ILS ₪", "INR Rs", "IQD ع.", "IRR ﷼", "ISK kr", "JMD $",
			"JOD د.", "JPY ¥", "KES Sh", "KGS ¤", "KHR", "KMF Fr", "KPW ₩",
			"KRW ₩", "KWD د.", "KYD $", "KZT 〒", "LAK ₭", "LBP ل.", "LKR Rs",
			"LRD $", "LSL L", "LTL Lt", "LVL Ls", "MAD د.م.", "MDL L", "MGA ¤",
			"MKD ден", "MMK K", "MNT ₮", "MOP P", "MRO UM", "MUR Rs", "MVR ރ.",
			"MWK MK", "MXN $", "MYR RM", "MZN MTn", "NAD $", "NGN ₦", "NIO C$",
			"NOK Kr", "NPR ₨", "NZD $", "OMR ر.ع.", "PAB B/.", "PEN S/.",
			"PGK K", "PHP ₱", "PKR Rs", "PLN zł", "PYR ₲", "QAR ر.", "RON L",
			"RSD дин", "RUB руб.", "RWF Fr", "SAR ر.", "SBD $", "SCR Rs",
			"SDG £", "SEK Kr", "SHP £", "SLL Le", "SOS Sh", "SRD $", "STD Db",
			"SYP £", "SZL L", "THB ฿", "TJS SM", "TMM m", "TND د.", "TOP T$",
			"TRY TL", "TTD $", "TWD $", "TZS Sh", "UAH ₴", "UGX Sh", "USD $",
			"UYU $", "UZS ¤", "VEF Bs F", "VND ₫", "VUV Vt", "WST T", "XAD Fr",
			"XAF Fr", "XAF Frr", "XCD $", "XOF Fr", "XPF Fr", "YER ﷼", "ZAR R",
			"ZMK ZK", "¤" };

	public ChangeCurrencyPopup() {
		mainPanel.setSpacing(7);

		final FormPanel form = new FormPanel();
		form.setAction("/updatecurrency");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				ClientApplication.currency = JSONParser.parse(
						event.getResults()).isObject().get("currency")
						.isString().stringValue();
				SellPanel.updateCurrency();
				// PARSE TO JSON AND LOAD
				PopUpPanel.hidePopup();
				VerticalPanel finalPanel = new VerticalPanel();
				finalPanel.setSpacing(7);
				finalPanel.setWidth("300px");
				Label sent = new Label(ClientApplication.constants.currencyUpdated());
				finalPanel.add(sent);
				PopUpPanel.alert(ClientApplication.constants.thankYou() + "!", finalPanel, true);
			}
		});

		Label manually = new Label(ClientApplication.constants.chooseCurrency());
		mainPanel.add(manually);

		currencySelect.setName("currency");
		mainPanel.add(currencySelect);
		populateCurrencyList();

		Button save = new Button(ClientApplication.constants.save());
		save.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				form.submit();
			}
		});
		mainPanel.add(save);

		InlineLabel or = new InlineLabel("* " + ClientApplication.constants.or() + " ");
		InlineLabel autoSelectCurrency = new InlineLabel(ClientApplication.constants.currencyInCountry());
		autoSelectCurrency.setStyleName("SettingsPanel-link");
		autoSelectCurrency.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.hidePopup();
				PopUpPanel.alert(ClientApplication.capitalize(ClientApplication.constants.currencyInCountry()),
						FindHomePopup.enterLocation, true);
			}
		});
		FlowPanel alsoPanel = new FlowPanel();
		alsoPanel.add(or);
		alsoPanel.add(autoSelectCurrency);

		mainPanel.add(alsoPanel);
		form.add(mainPanel);

		initWidget(form);
	}

	private void populateCurrencyList() {
		for (String currency : CURRENCIES) {
			currencySelect.addItem(currency);
		}
	}
}