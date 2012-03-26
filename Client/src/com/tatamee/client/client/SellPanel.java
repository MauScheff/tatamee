/*
 *	Copyright 2008 Tatamee
 */

package com.tatamee.client.client;

import org.cobogw.gwt.user.client.CSS;
import org.cobogw.gwt.user.client.Color;
import org.cobogw.gwt.user.client.ui.Button;
import org.cobogw.gwt.user.client.ui.RoundedLinePanel;
import org.cobogw.gwt.user.client.ui.RoundedPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;

//TODO: Fix prices like 999999999 (Scientific Notation?) 
//TODO: Change Description Box to native TextBox

public class SellPanel extends Composite {
	public static Item loadedItem;
	private static int thumbnailReload = 10000;
	private static final HorizontalPanel mainPanel = new HorizontalPanel();
	private static final VerticalPanel outerMainPanel = new VerticalPanel();
	private static TextBox title = new TextBox();
	private static TextBox category = new TextBox();
	private static TextBox price = new TextBox();
	public static TextBox tags = new TextBox();
	private static Hidden descriptionHTML = new Hidden();
	private static Image thumbnail = new Image();
	private static DescriptionEditor description = new DescriptionEditor();
	private static VerticalPanel panel2 = new VerticalPanel();
	private static FileUpload imageSelect = new FileUpload();
	private static Hidden id = new Hidden("id");
	private static String features = "";
	private static Label currency = new Label(ClientApplication.currency);
	static HTML addMoreImages = new HTML("");
	static HTML sold = new HTML("");

	public SellPanel() {

		VerticalPanel panel1 = new VerticalPanel();
		panel1.setSpacing(10);

		/*
		 * Form Handler
		 */
		final FormPanel form = new FormPanel();
		form.setAction("/post");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		form.addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				try {
					descriptionHTML.setValue(description.getText());
					if (title.getText().trim().equals("")) {
						throw new Exception(ClientApplication.constants
								.pleaseAddTitle());
					}

					try {
						Double.parseDouble(getEditorPrice());
					} catch (Exception e) {
						throw new Exception(ClientApplication.constants.pleasePrice());
					}
					PopUpPanel.alert(ClientApplication.constants
							.uploadingItem()
							+ ": " + title.getText(), new Image(
							"/images/loading.gif"), false);
				} catch (Exception e) {
					// TODO: Handle Exceptions on server image size invalid
					// image etc. (On Server)
					PopUpPanel.alert(ClientApplication.constants.error(),
							new Label(e.getMessage()), true);
					event.cancel();
				}
			}
		});

		form.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				PopUpPanel.hidePopup();
				if (id.getValue().equals("")) {
					PopUpPanel.alert(ClientApplication.constants.thankYou(),
							new Label(ClientApplication.constants.itemCreated()
									+ "!"), true);
				} else {
					PopUpPanel.alert(ClientApplication.constants.thankYou(),
							new Label(ClientApplication.constants.itemUpdated()
									+ "!"), true);
				}
				Item response = new Item(JSONParser.parse(event.getResults())
						.isObject());
				ClientApplication.itemPanelSell.loadItem(response);
				SellPanel.loadItem(response);
				MyItems.refresh(true);
			}
		});

		/*
		 * Panel 1 (Left Side)
		 */
		thumbnail.setStyleName("thumbnail");
		thumbnail.setUrl("images/x.jpg");
		title.setWidth("190px");
		title.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				ClientApplication.itemPanelSell.setItemTitle(title.getText());
			}
		});

		// to hide the two
		// optional boxes and
		// when clicked show
		// them
		category.setWidth("190px");

		/*
		 * Main Panel Assembly (Left Side)
		 */
		HorizontalPanel imagePanel = new HorizontalPanel();
		VerticalPanel itemTitle = new VerticalPanel();
		itemTitle.setStyleName("SellPanel-ItemTitles");
		itemTitle.setHeight("100px");
		itemTitle.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		imagePanel.add(thumbnail);
		FlowPanel titleLabelPanel = new FlowPanel();
		InlineLabel titleLabel = new InlineLabel(ClientApplication.constants.title());
		titleLabel.setStyleName("title2-icon-left");
		titleLabelPanel.add(new Image("images/bullet_star.png"));
		titleLabelPanel.add(titleLabel);
		itemTitle.add(titleLabelPanel);
		itemTitle.add(title);
		FlowPanel categoryLabelPanel = new FlowPanel();
		InlineLabel categoryLabel = new InlineLabel(ClientApplication.constants.category());
		categoryLabel.setStyleName("title2-icon-left");
		categoryLabelPanel.add(new Image("images/chart_organisation.png"));
		categoryLabelPanel.add(categoryLabel);
		itemTitle.add(categoryLabelPanel);
		itemTitle.add(category);
		imagePanel.add(itemTitle);
		panel1.add(imagePanel);
		panel1.add(description);
		panel1.setHeight("100%");

		/*
		 * Panel 2 (Right Side)
		 */
		/*
		 * Image Loader
		 */

		panel2.setStyleName("SellPanel-panel2");
		panel2.setSpacing(10);
		FlowPanel picturesLabelPanel = new FlowPanel();
		InlineLabel picturesLabel = new InlineLabel(ClientApplication.constants.pictures());
		InlineLabel limit = new InlineLabel(" * < 1 MB ");
		limit.setStyleName("commaSeparated");
		picturesLabel.setStyleName("title2-icon-left");
		picturesLabelPanel.add(new Image("images/image_add.png"));
		picturesLabelPanel.add(picturesLabel);
		picturesLabelPanel.add(limit);
		panel2.add(picturesLabelPanel);
		HorizontalPanel imageLoadPanel = new HorizontalPanel();
		panel2.add(imageSelect);

		// Image Load Panel
		imageLoadPanel.add(SellPanel.addMoreImages);
		imageLoadPanel.setCellHorizontalAlignment(SellPanel.addMoreImages,
				HorizontalPanel.ALIGN_CENTER);
		imageLoadPanel.setCellVerticalAlignment(SellPanel.addMoreImages,
				HorizontalPanel.ALIGN_MIDDLE);
		panel2.add(imageLoadPanel);
		SellPanel.addMoreImages.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.alert(ClientApplication.constants.addMorePics(),
						new AddPicturesPanel(SellPanel.loadedItem),
						ClientApplication.constants.close(),
						new ClickHandler() {
							public void onClick(ClickEvent event) {
								PopUpPanel.hidePopup();
								ClientApplication.itemPanelSell
										.refreshThumbnail();
								SellPanel.refreshThumbnail();
							}
						});
			}
		});

		thumbnail.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (loadedItem != null) {
					PopUpPanel.alert(loadedItem.getTitle(), new PicturesPanel(
							loadedItem), true);
				}
			}
		});

		/*
		 * Price
		 */
		FlowPanel priceLabelPanel = new FlowPanel();
		InlineLabel priceLabel = new InlineLabel(ClientApplication.constants.price());
		priceLabel.setStyleName("title2-icon-left");
		priceLabelPanel.add(new Image("images/money.png"));
		priceLabelPanel.add(priceLabel);
		panel2.add(priceLabelPanel);
		HorizontalPanel pricePanel = new HorizontalPanel();
		pricePanel.setSize("240px", "25px");
		price.setMaxLength(11);
		price.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				ClientApplication.itemPanelSell.setPrice(
						ClientApplication.currency, price.getText());
			}
		});
		price.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				char keyCode = event.getCharCode();
				Widget sender = (Widget) event.getSource();
				if (!Character.isDigit(keyCode)
						&& (keyCode != (char) KeyCodes.KEY_TAB)
						&& (keyCode != (char) KeyCodes.KEY_BACKSPACE)
						&& (keyCode != (char) KeyCodes.KEY_DELETE)
						&& (keyCode != (char) KeyCodes.KEY_ENTER)
						&& (keyCode != (char) KeyCodes.KEY_HOME)
						&& (keyCode != (char) KeyCodes.KEY_END)
						&& (keyCode != (char) KeyCodes.KEY_LEFT)
						&& (keyCode != (char) KeyCodes.KEY_UP)
						&& (keyCode != (char) KeyCodes.KEY_RIGHT)
						&& (keyCode != (char) KeyCodes.KEY_DOWN)) {
					((TextBox) sender).cancelKey();
				}
			}
		});

		// TODO: add custom currency symbols like euro, yen, pound, etc.
		currency.setStyleName("SellPanel-Currency");
		pricePanel.add(currency);
		pricePanel.add(price);
		currency.setWidth("95px");
		price.setWidth("100%");
		pricePanel.setCellHorizontalAlignment(currency,
				HorizontalPanel.ALIGN_CENTER);
		pricePanel.setCellVerticalAlignment(currency,
				HorizontalPanel.ALIGN_MIDDLE);
		pricePanel.setCellHorizontalAlignment(price,
				HorizontalPanel.ALIGN_RIGHT);
		panel2.add(pricePanel);

		/*
		 * Tags
		 */
		FlowPanel featuresLabelPanel = new FlowPanel();
		InlineLabel featuresLabel = new InlineLabel(ClientApplication.constants.tags());
		featuresLabel.setStyleName("title2-icon-left");
		featuresLabelPanel.add(new Image("images/tag_blue_add.png"));
		featuresLabelPanel.add(featuresLabel);
		panel2.add(featuresLabelPanel);
		HTML suggestions = new HTML("<a href=\"javascript:void(0);\">"
				+ ClientApplication.constants.suggestionsSimilar() + "</a>");
		suggestions.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (category.getText().equals("")) {
					PopUpPanel
							.alert(ClientApplication.constants.noSuggestions(),
									new Label(ClientApplication.constants
											.pleaseCategory()), true);
				} else {
					PopUpPanel.alert(ClientApplication.constants
							.suggestionsFor()
							+ " " + category.getText(), new SuggestionsPanel(
							category.getText()), false);
				}
			}
		});
		tags.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				ClientApplication.itemPanelSell.setTags(tags.getText());
			}
		});
		tags.setWidth("250px");
		tags.setStyleName("tags");
		Label commaSeparated = new Label(ClientApplication.constants
				.separateCommas());
		commaSeparated.setStyleName("commaSeparated");
		commaSeparated.setWidth("245px");
		panel2.add(commaSeparated);
		panel2.add(suggestions);
		panel2.add(tags);

		/*
		 * Submit Button
		 */
		HorizontalPanel submitPanel = new HorizontalPanel();
		submitPanel.setWidth("100%");
		Button submit = new Button(ClientApplication.constants.save(),
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						form.submit();
					}
				});
		CSS.setProperty(submit, CSS.A.FONT_WEIGHT, CSS.V.FONT_WEIGHT.BOLD);

		submitPanel.add(sold);
		sold.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				PopUpPanel.alert(ClientApplication.constants.askRating(),
						new AskRatingsPopup(loadedItem), true);
			}
		});
		submitPanel.setCellHorizontalAlignment(sold,
				HorizontalPanel.ALIGN_CENTER);
		submitPanel.setCellVerticalAlignment(sold, VerticalPanel.ALIGN_MIDDLE);
		submitPanel.add(submit);
		submitPanel.setCellHorizontalAlignment(submit,
				HorizontalPanel.ALIGN_RIGHT);
		panel2.add(submitPanel);
		panel2.add(descriptionHTML);

		/*
		 * Attributes submitted to server
		 */
		title.setName("title");
		category.setName("category");
		imageSelect.setName("img");
		imageSelect.setWidth("250px");
		price.setName("price");
		// contact.setName("contact");
		tags.setName("tags");
		descriptionHTML.setName("description");

		// Attributes
		// Contact
		// anonymous

		/*
		 */
		HorizontalPanel wrapperPanel = new HorizontalPanel();

		wrapperPanel.add(panel1);
		wrapperPanel.add(panel2);
		wrapperPanel.add(id);

		form.add(wrapperPanel);

		mainPanel.add(form);

		FlowPanel createEditLabelPanel = new FlowPanel();
		InlineLabel createEditLabel = new InlineLabel(ClientApplication.constants
				.createEdit());
		createEditLabel.setStyleName("title");
		Image editIcon = new Image("images/page_white_edit.png");
		editIcon.setStyleName("SellPanel-topIcon");
		createEditLabelPanel.add(editIcon);
		createEditLabelPanel.add(createEditLabel);
		outerMainPanel.add(createEditLabelPanel);
		outerMainPanel.add(mainPanel);
		outerMainPanel.setStyleName("SellPanel");

		/*
		 * Rounder Corners
		 */
		RoundedLinePanel rp = new RoundedLinePanel(RoundedPanel.ALL, 5);
		rp.setCornerColor(Color.GREY, Color.WHITE);
		rp.add(outerMainPanel);
		rp.setStyleName("SellRPanel");
		initWidget(rp);

	}

	static void refreshThumbnail() {
		thumbnail.setUrl(loadedItem.getThumbnail() + "#" + thumbnailReload++);
	}

	public static void clear() {
		thumbnail.setUrl("images/x.jpg");
		title.setText("");
		category.setText("");
		tags.setText("");
		price.setText("");
		description.clear();
		ClientApplication.itemPanelSell.clearPanel();
		id.setValue("");
		features = "";
		loadedItem = null;
		SellPanel.addMoreImages.setText("");
		SellPanel.sold.setHTML("");
	}

	public static void loadItem(Item item) {
		title.setText(item.getTitle());
		category.setText(item.getCategory());
		tags.setText(item.getTags());
		description.setText(item.getDescription());
		thumbnail.setUrl(item.getThumbnail() + "#" + thumbnailReload++);
		price.setText(item.getPrice() + "");
		// If id is commented out items will be repeated, useful for DB
		id.setValue(item.getID());
		SellPanel.features = item.getTags();
		loadedItem = item;
		if (item.getNPictures() > 0) {
			SellPanel.addMoreImages.setHTML("<a href=\"javascript:void(0)\">"
					+ ClientApplication.constants.addMorePics() + "</a>");
		}

		SellPanel.sold.setHTML("<a href=\"javascript:void(0)\">"
				+ ClientApplication.constants.askRating() + "..." + "</a>");
	}

	public static String getEditorTitle() {
		return title.getText();
	}

	public static String getDescriptionText() {
		return description.getText();
	}

	public static String getID() {
		return id.getValue();
	}

	public static String getEditorPrice() {
		return price.getText();
	}

	public static void loadItem(ItemLight lightItem) {
		thumbnail.setUrl("images/loading.gif");
		String requestData = "?action=item&key=" + lightItem.getID();
		RequestBuilder getItems = new RequestBuilder(RequestBuilder.GET,
				"/getitems" + requestData);
		getItems.setCallback(new RequestCallback() {
			public void onError(Request request, Throwable exception) {
				PopUpPanel.alert(ClientApplication.constants.error(),
						new Label(exception.getMessage()), true);
			}

			public void onResponseReceived(Request request, Response response) {
				String json = response.getText();
				loadItem(new Item(JSONParser.parse(json).isObject()));
			}
		});
		try {
			getItems.send();
		} catch (RequestException e) {
			;
		}
	}

	public static String getCategory() {
		return category.getText();
	}

	public static String getTags() {
		return tags.getValue();
	}

	public static void addTag(String tag) {
		String currentTags = tags.getText().trim();
		if (currentTags.equals("")) {
			currentTags = tag;
		} else {
			if (!currentTags.endsWith(",")) {
				currentTags += ",";
			}
			currentTags += " " + tag;
		}
		tags.setText(currentTags);
	}

	public static void updateCurrency() {
		currency.setText(ClientApplication.currency);
	}
}