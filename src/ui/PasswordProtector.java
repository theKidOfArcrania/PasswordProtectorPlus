package ui;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

import javax.crypto.spec.SecretKeySpec;

import data.PasswordPair;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
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

	private static final Image SCREW = Tools.createImage("screw.png");
	public static final Image TEXT_IN = Tools.createImage("OverOn.png");
	public static final Image TEXT_IN_PRESSED = Tools.createImage("ClickOn.png");
	public static final Image TEXT_NONE = Tools.createImage("Off.png");
	public static final Image SHINY_BACKGROUND = Tools.createImage("background.jpg");
	public static final Image SETTINGS = Tools.createImage("setting.png");
	public static final Image BACKGROUND = Tools.createImage("tile.png");
	public static final InnerShadow SMALL_SHADE = new InnerShadow(2.0, Color.BLACK);
	public static final InnerShadow LARGE_SHADE = new InnerShadow(5.0, Color.BLACK);
	public static final DropShadow OUT = new DropShadow(2.0, Color.BLACK);
	public final BackgroundImage BACK = new BackgroundImage(BACKGROUND, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
			new BackgroundSize(BACKGROUND.getWidth() / 3, BACKGROUND.getHeight() / 3, false, false, false, false));
	public final BackgroundImage BACK2 = new BackgroundImage(SHINY_BACKGROUND, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
			new BackgroundSize(SHINY_BACKGROUND.getWidth(), SHINY_BACKGROUND.getHeight(), false, false, false, false));
	
	private final DisplayMode defaultMode = getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
	private final Dimension displayRes = new Dimension(defaultMode.getWidth(), defaultMode.getHeight());
	private final double dispWidth = displayRes.getWidth();
	private final double dispHeight = displayRes.getHeight();
	private final double wRatio = dispWidth / 800.0;
	private final double hRatio = dispHeight / 450.0;
	public final double sRatio = (wRatio > hRatio) ? hRatio : wRatio;

	private static String password = "password";
	private PasswordField passwordField;
	private TextField addUser;
	private TextField addPassword;
	private TextField addDescription;
	private ImageView button;
	private Text info;
	private Text help;
	private Stage mainScreen;
	private Stage loginScreen;
	private Text userDisplay;
	private Text passDisplay;
	private static File accountsIn;
	private static File accountsOut;
	private static ObservableList<PasswordPair> accountList;
	private static SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");;
	private static InputStream in;
	private static OutputStream out;
	private MessageDigest digest;
	private byte[] digested;
	
	public static void main(String[] args) throws IOException{
		launch(args);
	}
	
	public PasswordProtector() {
		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		//Main Stage Creation
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		Scene trans = new Scene(new AnchorPane(), dispWidth, dispHeight);
		trans.setFill(null);
		primaryStage.setScene(trans);
		primaryStage.show();
		primaryStage.setOpacity(0.0);
		
		
		//Rim Creation
		Stage back = initBack();
		back.initOwner(primaryStage);
		back.show();
		back.centerOnScreen();
		
		//Intro Screen Creation
		Stage s = initIntro(primaryStage);
		s.setY(back.getY() + 10);
		s.setX(back.getX() + 10);
		s.initOwner(back);
		s.show();

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
			process();
		});
		
		Circle c = new Circle(40 * wRatio, 110 * hRatio, 10 * sRatio, Color.rgb(230, 240, 240));
		c.setEffect(LARGE_SHADE);
		Text q = Tools.createText(35, 100, wRatio, hRatio, "?", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(18, sRatio));
		q.setOnMousePressed(e -> help());
		help = Tools.createText(60, 127, wRatio, hRatio, "Default Password: password.\nChange Login password in \"Settings\".", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(12, sRatio));
		help.setVisible(false);
		
		Text close = Tools.createText(370, 1, wRatio, hRatio, "X", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(18, sRatio));
		Text minimize = Tools.createText(355, 5, wRatio, hRatio, "-", Color.web("#2d7df1"), SMALL_SHADE, Tools.createBoldFont(18, sRatio));
		
		close.setOnMousePressed(e -> {mainStage.close();});
		minimize.setOnMousePressed(e->{mainStage.setIconified(true);});
		Text name = Tools.createText(0, 20, wRatio, hRatio, "PASSWORD PROTECTOR", Color.rgb(80, 215, 240), SMALL_SHADE, Tools.createNevisFont(24, sRatio));
		name.setWrappingWidth(400*wRatio);
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
				process();
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
	
	//Checks if the password is equal to the right password
	public void process() {
		if (passwordField.getText().equals(password)) {
			info.setText("Valid Password!");
			initMainScreen();
			passwordField.setText("");
		} else {
			info.setText("Invalid Password! Please try again.");
			passwordField.setText("");
		}
		button.setImage(TEXT_NONE);
	}

	public void help() {
		if (help.isVisible()) {
			help.setVisible(false);
			return;
		}
		help.setVisible(true);
	}
	
	public void initMainScreen(){ 
		//digest the password
		try{
			digest = MessageDigest.getInstance("SHA-256");
			digested = digest.digest(password.getBytes("UTF-8"));
		}
		catch(Exception e){
		}
		byte[] keyBytes = new byte[16];
		System.arraycopy(digested, 0, keyBytes, 0, keyBytes.length);
		keySpec = new SecretKeySpec(keyBytes, "AES");
		
		accountList = FXCollections.observableArrayList();
		try{
			readIn();
		}
		catch(IOException ex)
		{
		}
		mainScreen = new Stage(); 
		mainScreen.initStyle(StageStyle.TRANSPARENT);
		mainScreen.initModality(Modality.WINDOW_MODAL);
		mainScreen.setFullScreen(true);
		AnchorPane root = new AnchorPane(); 
		Scene scene = new Scene(root, dispWidth, dispHeight);

		scene.getStylesheets().add(PasswordProtector.class.getResource("main.css").toExternalForm());	
		
		ImageView screw1 = Tools.createImageView(SCREW, 15, 15, 1, 0.0, sRatio, wRatio, hRatio, OUT);
		ImageView screw2 = Tools.createImageView(SCREW, 15, 15, 785, 0.0, sRatio, wRatio, hRatio, OUT);
		ImageView screw3 = Tools.createImageView(SCREW, 15, 15, 1, 435, sRatio, wRatio, hRatio, OUT);
		ImageView screw4 = Tools.createImageView(SCREW, 15, 15, 785,  435, sRatio, wRatio, hRatio, OUT);
		ImageView settings = Tools.createImageView(SETTINGS, 20, 20, 730, 1, sRatio, wRatio, hRatio, SMALL_SHADE);
		
		Text close = Tools.createText(770, 1, wRatio, hRatio, "X", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(18, sRatio));
		Text minimize = Tools.createText(755, 5, wRatio, hRatio, "-", Color.web("#2d7df1"), SMALL_SHADE, Tools.createBoldFont(18, sRatio));
		Circle c = new Circle(605* wRatio, 22 * hRatio, 12 * sRatio, Color.rgb(230, 240, 240));
		c.setEffect(LARGE_SHADE);
		Text q = Tools.createText(600, 11, wRatio, hRatio, "?", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(20, sRatio));
		Text name = Tools.createText(0, 7, wRatio, hRatio, "PASSWORD PROTECTOR", Color.rgb(80, 215, 240), SMALL_SHADE, Tools.createNevisFont(28, sRatio));
		name.setWrappingWidth(800*wRatio);
		name.setTextAlignment(TextAlignment.CENTER);
		
		Text viewDescript = Tools.createText(95, 44, wRatio, hRatio, "Account Descriptions", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(22, sRatio));
		
		close.setOnMousePressed(e -> {
			try{
				writeOut();
			}
			catch(IOException ex)
			{
			}
			mainScreen.close();
		});
		
		Rectangle panel = Tools.createRoundedRectangle(10, 20, 5, 5, 755, 0, sRatio, wRatio, hRatio, Color.rgb(100, 100, 100,0), null);
		panel.setOnMousePressed(e->{mainScreen.setIconified(true);});
		
		Text username = Tools.createText(45, 347, wRatio, hRatio, "Username: ", Color.rgb(10, 10, 20), SMALL_SHADE, Tools.createNevisFont(20, sRatio));
		Text password = Tools.createText(45, 377, wRatio, hRatio, "Password: ", Color.rgb(10, 10, 20), SMALL_SHADE, Tools.createNevisFont(20, sRatio));
		
		userDisplay = Tools.createText(150, 347, wRatio, hRatio, "", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(20, sRatio));
		passDisplay = Tools.createText(145, 377, wRatio, hRatio, "", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(20, sRatio));
		userDisplay.textProperty().addListener(e->{
			double originalSize = 20;
			userDisplay.setFont(Tools.createNevisFont(originalSize, sRatio));
			while(userDisplay.getLayoutBounds().getWidth() >= 195*wRatio)
			{
				userDisplay.setFont(Tools.createNevisFont(originalSize, sRatio));
				originalSize-=.25;
			}
			userDisplay.setTranslateY((username.getLayoutBounds().getHeight() - userDisplay.getLayoutBounds().getHeight())*3/4);
			
		});
		
		passDisplay.textProperty().addListener(e->{
			double originalSize = 20;
			passDisplay.setFont(Tools.createNevisFont(originalSize, sRatio));
			while(passDisplay.getLayoutBounds().getWidth() >= 195*wRatio)
			{
				passDisplay.setFont(Tools.createNevisFont(originalSize, sRatio));
				originalSize-=.25;
			}
			passDisplay.setTranslateY((username.getLayoutBounds().getHeight() - passDisplay.getLayoutBounds().getHeight())*3/4);
		});
		Rectangle display = Tools.createRoundedRectangle(320, 80, 5, 5, 35, 335, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), OUT);
		display.setFill(new ImagePattern(SHINY_BACKGROUND));
		
		Rectangle listFrame = Tools.createRoundedRectangle(350, 250, 2, 2, 20, 70, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), OUT);
		listFrame.setFill(new ImagePattern(SHINY_BACKGROUND));
		ListView<PasswordPair> list = new ListView<PasswordPair>();
		
		list.setItems(accountList);
		list.setPrefWidth(340*wRatio);
		list.setPrefHeight(240*hRatio);
		list.setLayoutX(25*wRatio);
		list.setLayoutY(75*hRatio);
		list.setEffect(LARGE_SHADE);
		list.setOnMouseClicked(e->{
			int index = list.getFocusModel().getFocusedIndex();
			if(index!=-1)
			{
				userDisplay.setText(list.getFocusModel().getFocusedItem().getUserName());
				passDisplay.setText(list.getFocusModel().getFocusedItem().getPassword());
			}
		});
		
		Text addDescript = Tools.createText(495, 44, wRatio, hRatio, "Add New Account", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(22, sRatio));
		
		Text enterUsername = Tools.createText(465, 95, wRatio, hRatio, "Enter Username:", Color.BLACK, SMALL_SHADE, Tools.createNevisFont(18, sRatio));
		Text enterPassword = Tools.createText(465, 170, wRatio, hRatio, "Enter Password:", Color.BLACK, SMALL_SHADE, Tools.createNevisFont(18, sRatio));
		Text enterDescription = Tools.createText(465, 245, wRatio, hRatio, "Enter Description:", Color.BLACK, SMALL_SHADE, Tools.createNevisFont(18, sRatio));
		
		addUser = new TextField();
		addPassword = new TextField();
		addDescription = new TextField();
		
		addUser.setLayoutX(465 * wRatio);
		addUser.setLayoutY(120 * hRatio);
		addUser.setPrefSize(240 * wRatio, 15 * hRatio);
		addUser.setFont(Tools.createNevisFont(14, sRatio));
		addUser.setEffect(SMALL_SHADE);
		
		addPassword.setLayoutX(465 * wRatio);
		addPassword.setLayoutY(195 * hRatio);
		addPassword.setPrefSize(240 * wRatio, 15 * hRatio);
		addPassword.setFont(Tools.createNevisFont(14, sRatio));
		addPassword.setEffect(SMALL_SHADE);
		
		addDescription.setLayoutX(465 * wRatio);
		addDescription.setLayoutY(270 * hRatio);
		addDescription.setPrefSize(240 * wRatio, 15 * hRatio);
		addDescription.setFont(Tools.createNevisFont(14, sRatio));
		addDescription.setEffect(SMALL_SHADE);
		
		Rectangle addContainer = Tools.createRoundedRectangle(270, 340, 5, 5, 450, 72, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), OUT);
		addContainer.setFill(new ImagePattern(SHINY_BACKGROUND));
		
		Rectangle createButton = Tools.createRoundedRectangle(200, 50, 5, 5, 465 , 345, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), OUT);
		Stop[] grad = { new Stop(0.0, Color.WHITE), new Stop(0.2, Color.web("#c4e0f4")), new Stop(.49, Color.web("#acd5f2")),
				new Stop(.5, Color.web("#74b2dd")), new Stop(1.0, Color.web("#a9e1f7")) };
		LinearGradient shieldGrad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, grad);
		createButton.setFill(shieldGrad);
		root.setBackground(new Background(BACK)); 
		root.getChildren().addAll(screw1, screw2, screw3, screw4, settings, listFrame, list,c,q, display,username, userDisplay, password, 
				passDisplay, addDescript, addContainer, enterUsername, enterPassword, enterDescription, addUser, addPassword, addDescription,viewDescript, name, close, minimize,
				panel,createButton);

		mainScreen.setScene(scene); 
		mainScreen.initOwner(loginScreen);
		mainScreen.show();
	}
	
	public void createAccount(String description, String username, String password)
	{
		accountList.add(new PasswordPair(description,username,password));
	}
	
	public static ObservableList<PasswordPair> getAccounts()
	{
		return accountList;
	}
	
	public static void readIn() throws IOException
	{
		accountsIn = new File("accounts.out");
		if(accountsIn.exists())
		{
			in = new FileInputStream("accounts.out");
			int numPass = in.read();
			for(int k = 0; k < numPass;k++)
			{
				PasswordPair p = PasswordPair.decryptPasswordPair(keySpec, in);
				accountList.add(p);
			}
		}
	}
	
	public static void writeOut() throws IOException
	{
		out = new FileOutputStream("accounts.out");
		out.write(accountList.size());
		
		for(PasswordPair p: accountList)
		{
			PasswordPair.encryptPasswordPair(keySpec, p, out);
			
		}
		out.close();
	}
	
	public void generateNewAccount()
	{
		String tempUser = addUser.getText();
		String tempPass = addPassword.getText();
		String tempDescript = addDescription.getText();
		
		if(tempUser == ""||tempPass==""||tempDescript=="")
		{
			
		}
	}
	public void reset()
	{
		accountsIn.delete();
	}
}