package com.tatamee.client.client;

import org.cobogw.gwt.user.client.Color;
import org.cobogw.gwt.user.client.ui.Button;
import org.cobogw.gwt.user.client.ui.RoundedLinePanel;
import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ScrollHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopUp extends PopupPanel {

	RoundedLinePanel panel;
	String title;

	public PopUp(String title, Widget w, boolean closeButton, boolean autoHide) {
		super(autoHide);
		this.setAnimationEnabled(true);
		this.setStyleName("PopUpPanel");
		if (autoHide) {
			this.addCloseHandler(new CloseHandler<PopupPanel>() {
				public void onClose(CloseEvent<PopupPanel> event) {
					PopUpPanel.hidePopup();
				}
			});
		}
		this.title = title;
		panel = format(title, w, closeButton);
		setWidget(panel);
	}

	public PopUp(String title, Widget w, String closeName, boolean autoHide,
			ClickHandler closeHandler) {
		super(autoHide);
		this.setAnimationEnabled(true);
		this.setStyleName("PopUpPanel");
		if (autoHide) {
			this.addCloseHandler(new CloseHandler<PopupPanel>() {
				public void onClose(CloseEvent<PopupPanel> event) {
					PopUpPanel.hidePopup();
				}
			});
		}
		this.title = title;
		panel = format(title, w, closeName, closeHandler);
		setWidget(panel);
	}

	public PopUp() {
		panel = null;
	}

	public void center() {
		super.center();
		fixOffset();
	}
	
	public void fixOffset() {
		int left = Math.max(this.getAbsoluteLeft(), 20);
		int top = Math.max(this.getAbsoluteTop(), 20);
		this.setPopupPosition(left, top);
	}

	private RoundedLinePanel format(String title, Widget w, boolean closeButton) {
		VerticalPanel newPanel = new VerticalPanel();
		newPanel.setSpacing(10);
		newPanel.setStyleName("PopUpPanel-content");
		Label titleLabel = new Label(title);
		titleLabel.setStyleName("title");

		newPanel.add(titleLabel);
		newPanel.add(w);
		if (closeButton) {
			Button close = new Button(ClientApplication.constants.close());
			close.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					PopUpPanel.hidePopup();
				}
			});
			newPanel.add(close);
		}

		/*
		 * Rounder Corners
		 */
		RoundedLinePanel rp = new RoundedLinePanel(RoundedPanel.ALL, 5);
		rp.setCornerColor(Color.WHITE, Color.WHITE);
		rp.add(newPanel);

		return rp;
	}

	private RoundedLinePanel format(String title, Widget w, String closeName,
			ClickHandler closeHandler) {
		VerticalPanel newPanel = new VerticalPanel();
		newPanel.setSpacing(10);
		newPanel.setStyleName("PopUpPanel-content");
		Label titleLabel = new Label(title);
		titleLabel.setStyleName("title");

		newPanel.add(titleLabel);
		newPanel.add(w);
		Button close = new Button(closeName);
		close.addClickHandler(closeHandler);
		newPanel.add(close);

		/*
		 * Rounder Corners
		 */
		RoundedLinePanel rp = new RoundedLinePanel(RoundedPanel.ALL, 5);
		rp.setCornerColor(Color.WHITE, Color.WHITE);
		rp.add(newPanel);

		return rp;
	}
}