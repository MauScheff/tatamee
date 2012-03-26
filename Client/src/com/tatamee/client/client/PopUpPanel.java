/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.GlassPanel;

public class PopUpPanel {

	private static GlassPanel glassPanel = new GlassPanel(false);
	private static LinkedList<PopUp> stack = new LinkedList<PopUp>();
	private static PopUp showing = null;

	private static void showPopup() {
//		 Window.enableScrolling(true);
		RootPanel.get().add(glassPanel, 0, 0);
		PopUpPanel.showing.show();
		PopUpPanel.showing.center();
	}

	public static void hidePopup() {
		if (showing != null) {
			showing.hide();

			if (!stack.isEmpty()) {
				showing = (PopUp) stack.removeFirst();
				showPopup();
			} else {
				showing = null;
				glassPanel.removeFromParent();
			}
		} else {
			glassPanel.removeFromParent();
		}
//		 Window.enableScrolling(true);
	}

	public static void hidePopup(String title) {
		if (showing != null && showing.title.equals(title)) {
			hidePopup();
		} else {
			for (int j = 0; j < stack.size(); j++) {
				if (stack.get(j).title.equals(title)) {
					stack.remove(j);
				}
			}
		}
	}

	public static void alert(String title, Widget content, boolean closeButton) {
		refreshGlass();
		if (showing != null) {
			stack.addFirst(showing);
			showing.hide();
			showing = new PopUp(title, content, closeButton, false);
			showPopup();
		} else {
			PopUpPanel.showing = new PopUp(title, content, closeButton, false);
			showPopup();
		}
	}
	
	public static void alert(String title, Widget content, String closeName, ClickHandler closeHandler) {
		refreshGlass();
		if (showing != null) {
			stack.addFirst(showing);
			showing.hide();
			showing = new PopUp(title, content, closeName, false, closeHandler);
			showPopup();
		} else {
			PopUpPanel.showing = new PopUp(title, content, closeName, false, closeHandler);
			showPopup();
		}
	}
	
	public static void alert(String title, Widget content) {
		refreshGlass();
		if (showing != null) {
			stack.addFirst(showing);
			showing.hide();
			showing = new PopUp(title, content, true, true);
			showPopup();
		} else {
			PopUpPanel.showing = new PopUp(title, content, true, true);
			showPopup();
		}
	}

	public static void centerPopup() {
		refreshGlass();
		if (showing != null) {
			showing.center();
		}
	}

	public static boolean isShowing() {
		return showing != null;
	}
	
	public static void refreshGlass() {
		glassPanel.removeFromParent();
		glassPanel = new GlassPanel(false);
		RootPanel.get().add(glassPanel, 0, 0);
	}
}
