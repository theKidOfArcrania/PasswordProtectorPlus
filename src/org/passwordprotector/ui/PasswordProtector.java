package org.passwordprotector.ui;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.passwordprotector.PasswordInvalidException;
import org.passwordprotector.PasswordPair;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;

public class PasswordProtector extends Application {

	// Image loading and constant stuff.
	private static final File accountFile = new File("accounts.dat");
	private static final Image SCREW = Tools.createImage("screw.png");
	private static final Image TEXT_IN = Tools.createImage("OverOn.png");
	private static final Image TEXT_IN_PRESSED = Tools.createImage("ClickOn.png");
	private static final Image TEXT_NONE = Tools.createImage("Off.png");
	private static final Image SHINY_BACKGROUND = Tools.createImage("background.jpg");
	private static final Image SETTINGS = Tools.createImage("setting.png");
	private static final Image BACKGROUND = Tools.createImage("tile.png");
	private static final Image DEFAULT_LIST = Tools.createImage("default-list.png");
	private static final InnerShadow SMALL_SHADE = new InnerShadow(2.0, Color.BLACK);
	private static final InnerShadow LARGE_SHADE = new InnerShadow(5.0, Color.BLACK);
	private static final DropShadow OUT = new DropShadow(2.0, Color.BLACK);

	public static void main(String[] args) {
		launch(args);
	}

	// Background images
	public final BackgroundImage BACK = new BackgroundImage(BACKGROUND, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
			new BackgroundSize(BACKGROUND.getWidth() / 3, BACKGROUND.getHeight() / 3, false, false, false, false));
	public final BackgroundImage BACK2 = new BackgroundImage(SHINY_BACKGROUND, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
			new BackgroundSize(SHINY_BACKGROUND.getWidth(), SHINY_BACKGROUND.getHeight(), false, false, false, false));

	// Display res variables
	private final DisplayMode defaultMode = getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
	private final Dimension displayRes = new Dimension(defaultMode.getWidth(), defaultMode.getHeight());
	private final double dispWidth = displayRes.getWidth();
	private final double dispHeight = displayRes.getHeight();
	private final double wRatio = dispWidth / 800.0;
	private final double hRatio = dispHeight / 450.0;
	public final double sRatio = (wRatio > hRatio) ? hRatio : wRatio;

	// The controls.
	private PasswordField passwordField;
	private TextField addUser;
	private TextField addPassword;
	private TextField addDescription;
	private Text errorUser;
	private Text errorPassword;
	private Text errorDescript;
	private ImageView button;
	private Text info;
	private Text help;
	private Stage mainScreen;
	private Stage loginScreen;
	private Text userDisplay;
	private Text passDisplay;
	private ListView<PasswordPair> list;

	// Account data
	private byte[] password;
	private ObservableList<PasswordPair> accountList = FXCollections.observableArrayList();

	public PasswordProtector() {

	}

	public void createAccount(String description, String username, String password) {
		accountList.add(new PasswordPair(description, username, password));
	}

	public void delete() {
		if (list.getSelectionModel().getSelectedIndex() != -1) {
			accountList.remove(list.getFocusModel().getFocusedIndex());
			update();
		}
	}

	public void generateNewAccount() {
		String tempUser = addUser.getText();
		String tempPass = addPassword.getText();
		String tempDescript = addDescription.getText();
		boolean check = true;
		if (tempUser.equals("")) {
			errorUser.setText("Enter a username");
			check = false;
		} else {
			errorUser.setText("");
		}
		if (tempPass.equals("")) {
			errorPassword.setText("Enter a password");
			check = false;
		} else {
			errorPassword.setText("");
		}
		if (tempDescript.equals("")) {
			errorDescript.setText("Enter a description");
			check = false;
		} else {
			errorDescript.setText("");
		}
		if (!check) {
			return;
		}

		createAccount(tempDescript, tempUser, tempPass);
		addUser.setText("");
		addPassword.setText("");
		addDescription.setText("");
		update();
	}

	public void help() {
		if (help.isVisible()) {
			help.setVisible(false);
			return;
		}
		help.setVisible(true);
	}

	/**
	 * Initializes the main screen and closes password dialog.
	 */
	public void initMainScreen() {
		loginScreen.close();
		mainScreen = new Stage();
		mainScreen.initStyle(StageStyle.TRANSPARENT);
		mainScreen.initModality(Modality.WINDOW_MODAL);
		mainScreen.setFullScreen(true);
		mainScreen.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(root, dispWidth, dispHeight);

		scene.getStylesheets().add(PasswordProtector.class.getResource("main.css").toExternalForm());

		ImageView screw1 = Tools.createImageView(SCREW, 15, 15, 1, 0.0, sRatio, wRatio, hRatio, OUT);
		ImageView screw2 = Tools.createImageView(SCREW, 15, 15, 785, 0.0, sRatio, wRatio, hRatio, OUT);
		ImageView screw3 = Tools.createImageView(SCREW, 15, 15, 1, 435, sRatio, wRatio, hRatio, OUT);
		ImageView screw4 = Tools.createImageView(SCREW, 15, 15, 785, 435, sRatio, wRatio, hRatio, OUT);

		ImageView listViewDefault = Tools.createImageView(DEFAULT_LIST, 500, 340, 25, 75, sRatio, wRatio, hRatio, null);
		ImageView settings = Tools.createImageView(SETTINGS, 20, 20, 730, 1, sRatio, wRatio, hRatio, SMALL_SHADE);

		Text close = Tools.createText(770, 1, wRatio, hRatio, "X", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(18, sRatio));
		Text minimize = Tools.createText(755, 5, wRatio, hRatio, "-", Color.web("#2d7df1"), SMALL_SHADE, Tools.createBoldFont(18, sRatio));
		Circle c = new Circle(605 * wRatio, 22 * hRatio, 12 * sRatio, Color.rgb(230, 240, 240));
		c.setEffect(LARGE_SHADE);
		Text q = Tools.createText(600, 11, wRatio, hRatio, "?", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(20, sRatio));
		Text name = Tools.createText(0, 7, wRatio, hRatio, "PASSWORD PROTECTOR", Color.rgb(80, 215, 240), SMALL_SHADE, Tools.createNevisFont(28, sRatio));
		name.setWrappingWidth(800 * wRatio);
		name.setTextAlignment(TextAlignment.CENTER);

		Text viewDescript = Tools.createText(95, 44, wRatio, hRatio, "Account Descriptions", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(22, sRatio));

		close.setOnMousePressed(e -> {
			try {
				PasswordPair.saveAccounts(accountList, accountFile, password);
				// Hide file.
				Files.setAttribute(accountFile.toPath(), "dos:hidden", true);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			mainScreen.close();
			loginScreen.show();
		});

		Rectangle panel = Tools.createRoundedRectangle(10, 20, 5, 5, 755, 0, sRatio, wRatio, hRatio, Color.rgb(100, 100, 100, 0), null);
		panel.setOnMousePressed(e -> mainScreen.setIconified(true));

		Text username = Tools.createText(45, 347, wRatio, hRatio, "Username: ", Color.rgb(10, 10, 20), SMALL_SHADE, Tools.createNevisFont(20, sRatio));
		Text password = Tools.createText(45, 377, wRatio, hRatio, "Password: ", Color.rgb(10, 10, 20), SMALL_SHADE, Tools.createNevisFont(20, sRatio));

		userDisplay = Tools.createText(150, 347, wRatio, hRatio, "", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(20, sRatio));
		passDisplay = Tools.createText(145, 377, wRatio, hRatio, "", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(20, sRatio));
		userDisplay.textProperty().addListener(e -> {
			double originalSize = 20;
			userDisplay.setFont(Tools.createNevisFont(originalSize, sRatio));
			while (userDisplay.getLayoutBounds().getWidth() >= 195 * wRatio) {
				userDisplay.setFont(Tools.createNevisFont(originalSize, sRatio));
				originalSize -= .25;
			}
			userDisplay.setTranslateY((username.getLayoutBounds().getHeight() - userDisplay.getLayoutBounds().getHeight()) * 3 / 4);

		});

		passDisplay.textProperty().addListener(e -> {
			double originalSize = 20;
			passDisplay.setFont(Tools.createNevisFont(originalSize, sRatio));
			while (passDisplay.getLayoutBounds().getWidth() >= 195 * wRatio) {
				passDisplay.setFont(Tools.createNevisFont(originalSize, sRatio));
				originalSize -= .25;
			}
			passDisplay.setTranslateY((username.getLayoutBounds().getHeight() - passDisplay.getLayoutBounds().getHeight()) * 3 / 4);
		});
		Rectangle display = Tools.createRoundedRectangle(320, 80, 5, 5, 35, 335, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), OUT);
		display.setFill(new ImagePattern(SHINY_BACKGROUND));

		Rectangle listFrame = Tools.createRoundedRectangle(350, 250, 2, 2, 20, 70, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), OUT);
		listFrame.setFill(new ImagePattern(SHINY_BACKGROUND));
		list = new ListView<>();
		list.setPlaceholder(listViewDefault);
		list.setItems(accountList);
		list.setPrefWidth(340 * wRatio);
		list.setPrefHeight(240 * hRatio);
		list.setLayoutX(25 * wRatio);
		list.setLayoutY(75 * hRatio);
		list.setEffect(LARGE_SHADE);
		list.setOnMouseClicked(e -> {
			int index = list.getSelectionModel().getSelectedIndex();
			if (index != -1) {
				userDisplay.setText(list.getFocusModel().getFocusedItem().getUserName());
				passDisplay.setText(list.getFocusModel().getFocusedItem().getPassword());
			} else {
				update();
			}
		});

		Text addDescript = Tools.createText(500, 44, wRatio, hRatio, "Modify Accounts", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(22, sRatio));

		Text enterUsername = Tools.createText(465, 95, wRatio, hRatio, "Enter Username:", Color.BLACK, SMALL_SHADE, Tools.createNevisFont(18, sRatio));
		Text enterPassword = Tools.createText(465, 170, wRatio, hRatio, "Enter Password:", Color.BLACK, SMALL_SHADE, Tools.createNevisFont(18, sRatio));
		Text enterDescription = Tools.createText(465, 245, wRatio, hRatio, "Enter Description:", Color.BLACK, SMALL_SHADE, Tools.createNevisFont(18, sRatio));

		addUser = new TextField();
		addPassword = new TextField();
		addDescription = new TextField();

		addUser.setLayoutX(450 * wRatio);
		addUser.setLayoutY(120 * hRatio);
		addUser.setPrefSize(270 * wRatio, 15 * hRatio);
		addUser.setFont(Tools.createNevisFont(14, sRatio));
		addUser.setEffect(SMALL_SHADE);

		addPassword.setLayoutX(450 * wRatio);
		addPassword.setLayoutY(195 * hRatio);
		addPassword.setPrefSize(270 * wRatio, 15 * hRatio);
		addPassword.setFont(Tools.createNevisFont(14, sRatio));
		addPassword.setEffect(SMALL_SHADE);

		addDescription.setLayoutX(450 * wRatio);
		addDescription.setLayoutY(270 * hRatio);
		addDescription.setPrefSize(270 * wRatio, 15 * hRatio);
		addDescription.setFont(Tools.createNevisFont(14, sRatio));
		addDescription.setEffect(SMALL_SHADE);

		Rectangle addContainer = Tools.createRoundedRectangle(300, 330, 5, 5, 435, 72, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), OUT);
		addContainer.setFill(new ImagePattern(SHINY_BACKGROUND));

		Rectangle createButton = Tools.createRoundedRectangle(120, 40, 5, 5, 455, 325, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), OUT);
		Rectangle removeButton = Tools.createRoundedRectangle(120, 40, 5, 5, 600, 325, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), OUT);
		Text create = Tools.createText(475, 330, wRatio, hRatio, "Create", Color.WHITE, SMALL_SHADE, Tools.createNevisFont(24, sRatio));
		Text remove = Tools.createText(623, 330, wRatio, hRatio, "Delete", Color.WHITE, SMALL_SHADE, Tools.createNevisFont(24, sRatio));

		create.setOnMousePressed(e -> {
			createButton.setEffect(LARGE_SHADE);
			generateNewAccount();
		});
		create.setOnMouseReleased(e -> {
			createButton.setEffect(OUT);
		});
		createButton.setOnMousePressed(e -> {
			createButton.setEffect(LARGE_SHADE);
			generateNewAccount();
		});
		createButton.setOnMouseReleased(e -> {
			createButton.setEffect(OUT);
		});

		remove.setOnMousePressed(e -> {
			removeButton.setEffect(LARGE_SHADE);
			delete();
		});
		remove.setOnMouseReleased(e -> {
			removeButton.setEffect(OUT);
		});

		removeButton.setOnMousePressed(e -> {
			removeButton.setEffect(LARGE_SHADE);
			delete();
		});
		removeButton.setOnMouseReleased(e -> {
			removeButton.setEffect(OUT);
		});

		Stop[] grad1 = { new Stop(0.0, Color.web("#cdddf6")), new Stop(0.2, Color.web("#2d7df1")), new Stop(.45, Color.web("#2d7df1")),
				new Stop(.55, Color.web("#1b61c8")), new Stop(1.0, Color.web("#3a75cb")) };
		LinearGradient createGrad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, grad1);
		Stop[] grad2 = { new Stop(0.0, Color.web("caecf2")), new Stop(0.2, Color.web("#50d7f0")), new Stop(.45, Color.web("50d7f0")),
				new Stop(.55, Color.web("#36b7cf")), new Stop(1.0, Color.web("#5fc7da")) };
		LinearGradient removeGrad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, grad2);

		removeButton.setFill(removeGrad);
		createButton.setFill(createGrad);

		errorUser = Tools.createText(610, 100, wRatio, hRatio, "", Color.web("d31414"), SMALL_SHADE, Tools.createNevisFont(10, sRatio));
		errorPassword = Tools.createText(603, 175, wRatio, hRatio, "", Color.web("d31414"), SMALL_SHADE, Tools.createNevisFont(10, sRatio));
		errorDescript = Tools.createText(620, 250, wRatio, hRatio, "", Color.web("d31414"), SMALL_SHADE, Tools.createNevisFont(10, sRatio));

		root.setBackground(new Background(BACK));
		root.getChildren().addAll(screw1, screw2, screw3, screw4, settings, listFrame, list, c, q, display, username, userDisplay, password, passDisplay, addDescript, addContainer, enterUsername, enterPassword, enterDescription, errorUser, errorPassword, errorDescript, addUser, addPassword, addDescription, viewDescript, name, close, minimize, panel, createButton, create, removeButton, remove);

		mainScreen.setScene(scene);
		mainScreen.initOwner(loginScreen);
		mainScreen.show();
	}

	public void reset() {
		accountList = FXCollections.observableArrayList();
		list.setItems(accountList);
		update();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Main Stage Creation
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		Scene trans = new Scene(new AnchorPane(), dispWidth, dispHeight);
		trans.setFill(null);
		primaryStage.setScene(trans);
		primaryStage.show();
		primaryStage.setOpacity(0.0);

		// Rim Creation
		Stage back = initBack();
		back.initOwner(primaryStage);
		back.show();
		back.centerOnScreen();

		// Intro Screen Creation
		Stage s = initIntro(primaryStage);
		s.setY(back.getY() + 10);
		s.setX(back.getX() + 10);
		s.initOwner(back);
		s.show();

	}

	public void update() {
		ObservableList<PasswordPair> temp = list.getItems();
		list.setItems(null);
		list.setItems(temp);
		userDisplay.setText("");
		passDisplay.setText("");
	}

	/**
	 * Checks if the password is equal to the right password by attempting to load the accounts list
	 */
	@SuppressWarnings("unused")
	public void verifyPassword() {

		accountList.clear();

		try {
			password = passwordField.getText().getBytes("UTF-8");
			PasswordPair.loadAccounts(accountList, accountFile, password);
			info.setText("Valid Password!");
			initMainScreen();
		} catch (IOException e) {
			e.printStackTrace();
			info.setText("Unable to open Accounts file");
		} catch (PasswordInvalidException e) {
			info.setText("Invalid Password! Please try again.");
		}

		passwordField.setText("");
		button.setImage(TEXT_NONE);
	}

	private Stage initBack() {
		Stage primaryStage = new Stage();
		primaryStage.initStyle(StageStyle.TRANSPARENT);

		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(root, 410 * wRatio, 170 * hRatio);

		BackgroundImage back2 = new BackgroundImage(SHINY_BACKGROUND, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(scene.getWidth(), scene.getHeight(), false, false, false, false));
		root.setBackground(new Background(back2));
		root.setEffect(new InnerShadow(3.0, Color.BLACK));
		primaryStage.setScene(scene);

		return primaryStage;
	}

	private Stage initIntro(Stage mainStage) {

		loginScreen = new Stage();
		loginScreen.initStyle(StageStyle.TRANSPARENT);

		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(root, 400 * wRatio, 160 * hRatio);

		ImageView screw1 = Tools.createImageView(SCREW, 15, 15, 3, 0.0, sRatio, wRatio, hRatio, OUT);
		ImageView screw2 = Tools.createImageView(SCREW, 15, 15, 385, 0.0, sRatio, wRatio, hRatio, OUT);
		ImageView screw3 = Tools.createImageView(SCREW, 15, 15, 3, 145, sRatio, wRatio, hRatio, OUT);
		ImageView screw4 = Tools.createImageView(SCREW, 15, 15, 385, 145, sRatio, wRatio, hRatio, OUT);

		passwordField = new PasswordField();

		passwordField.setLayoutX(60 * wRatio);
		passwordField.setLayoutY(100 * hRatio);
		passwordField.setPrefSize(200 * wRatio, 15 * hRatio);
		passwordField.setFont(Tools.createNevisFont(12, sRatio));

		button = Tools.createImageView(TEXT_NONE, 60, 60, 280, 75, sRatio, wRatio, hRatio, OUT);
		button.setOnMousePressed(e -> {
			if (passwordField.getText().isEmpty()) {
				return;
			}
			button.setImage(TEXT_IN_PRESSED);
		});
		button.setOnMouseReleased(e -> {
			if (passwordField.getText().isEmpty()) {
				return;
			}
			button.setImage(TEXT_IN);
			verifyPassword();
		});

		Circle c = new Circle(40 * wRatio, 110 * hRatio, 10 * sRatio, Color.rgb(230, 240, 240));
		c.setEffect(LARGE_SHADE);
		Text q = Tools.createText(35, 100, wRatio, hRatio, "?", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(18, sRatio));
		q.setOnMousePressed(e -> help());
		help = Tools.createText(60, 127, wRatio, hRatio, "Default Password: password.\nChange Login password in \"Settings\".", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(12, sRatio));
		help.setVisible(false);

		Text close = Tools.createText(370, 1, wRatio, hRatio, "X", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(18, sRatio));
		Text minimize = Tools.createText(355, 5, wRatio, hRatio, "-", Color.web("#2d7df1"), SMALL_SHADE, Tools.createBoldFont(18, sRatio));

		close.setOnMousePressed(e -> {
			mainStage.close();
		});
		minimize.setOnMousePressed(e -> {
			mainStage.setIconified(true);
		});
		Text name = Tools.createText(0, 20, wRatio, hRatio, "PASSWORD PROTECTOR", Color.rgb(80, 215, 240), SMALL_SHADE, Tools.createNevisFont(24, sRatio));
		name.setWrappingWidth(400 * wRatio);
		name.setTextAlignment(TextAlignment.CENTER);

		Text instruction = Tools.createText(70, 75, wRatio, hRatio, "Enter Your Password:", Color.rgb(80, 215, 240), SMALL_SHADE, Tools.createNevisFont(18, sRatio));

		info = Tools.createText(25, 55.0, wRatio, hRatio, "One Password to Save them ALL!", Color.rgb(200, 220, 230), null, Tools.createNevisFont(10, sRatio));
		info.setWrappingWidth(350 * wRatio);
		info.setTextAlignment(TextAlignment.CENTER);

		BackgroundImage back2 = new BackgroundImage(SHINY_BACKGROUND, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(scene.getWidth(), scene.getHeight(), false, false, false, false));

		passwordField.textProperty().addListener(e -> {
			if (!passwordField.getText().isEmpty()) {
				button.setImage(TEXT_IN);
			} else {
				button.setImage(TEXT_NONE);
			}
		});
		passwordField.setOnKeyPressed(e -> {
			if (passwordField.getText().isEmpty()) {
				return;
			}
			if (e.getCode() == KeyCode.ENTER) {
				button.setImage(TEXT_IN_PRESSED);
			}
		});

		passwordField.setOnKeyReleased(e -> {
			if (passwordField.getText().isEmpty()) {
				return;
			}
			if (e.getCode() == KeyCode.ENTER) {
				button.setImage(TEXT_IN);
				verifyPassword();
			}
		});
		passwordField.setBackground(new Background(back2));
		passwordField.setEffect(SMALL_SHADE);

		Rectangle panel = Tools.createRoundedRectangle(350, 20, 5, 5, 25, 50, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), LARGE_SHADE);

		root.getChildren().addAll(screw1, screw2, screw3, screw4, panel, name, instruction, passwordField, button, info, c, q, help, close, minimize);
		root.setBackground(new Background(BACK));
		root.setEffect(LARGE_SHADE);
		loginScreen.setScene(scene);

		return loginScreen;

	}
}