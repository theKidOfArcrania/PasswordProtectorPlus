package ui;

import java.awt.Dimension;
import java.awt.DisplayMode;

import data.PasswordPair;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;

public class PasswordProtector extends Application {

	private static final String PASSWORD  = "password";

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

	public static void main(String[] args) {
		launch(args);
	}

	private final DisplayMode defaultMode = getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
	private final Dimension displayRes = new Dimension(defaultMode.getWidth(), defaultMode.getHeight());
	private final double dispWidth = displayRes.getWidth();
	private final double dispHeight = displayRes.getHeight();

	private final double wRatio = dispWidth / 800.0;
	private final double hRatio = dispHeight / 450.0;
	public double sRatio;

	private PasswordField passwordField;
	private ImageView button;
	private Text info;
	private Text help;
	private Stage mainScreen;
	private Stage loginScreen;
	
	public PasswordProtector() {
		sRatio = (wRatio > hRatio) ? hRatio : wRatio;

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

		BackgroundImage back = new BackgroundImage(BACKGROUND, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(BACKGROUND.getWidth() / 3, BACKGROUND.getHeight() / 3, false, false, false, false));
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
		root.setBackground(new Background(back));
		root.setEffect(LARGE_SHADE);
		loginScreen.setScene(scene);

		return loginScreen;

	}
	
	//Checks if the password is equal to the right password
	public void process() {
		if (passwordField.getText().equals(PASSWORD )) {
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
	
	public void initMainScreen() { 
	
	//when init, check for existing out file, if it exists, then read it in and add elements to class array list.
	//if it does not exist, then create a new one.
	
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
		Circle c = new Circle(595* wRatio, 22 * hRatio, 12 * sRatio, Color.rgb(230, 240, 240));
		c.setEffect(LARGE_SHADE);
		Text q = Tools.createText(590, 12, wRatio, hRatio, "?", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(20, sRatio));
		Text name = Tools.createText(0, 7, wRatio, hRatio, "PASSWORD PROTECTOR", Color.rgb(80, 215, 240), SMALL_SHADE, Tools.createNevisFont(26, sRatio));
		name.setWrappingWidth(800*wRatio);
		name.setTextAlignment(TextAlignment.CENTER);
		
		Text viewDescript = Tools.createText(20, 44, wRatio, hRatio, "Account Descriptions", Color.web("#2d7df1"), SMALL_SHADE, Tools.createNevisFont(20, sRatio));
		
		close.setOnMousePressed(e -> {
		//push the array list to an encrypted out file on close
			mainScreen.close();
		});
		minimize.setOnMousePressed(e->{mainScreen.setIconified(true);});
		
		ListView<PasswordPair> list = new ListView<PasswordPair>();
		ObservableList<PasswordPair> items = FXCollections.observableArrayList();
		for(int count = 0;count<20;count++)
		{
			PasswordPair p = new PasswordPair("Password No. " + count, "crystal"+count, "lovelove"); 
			items.add(p);
		}
		list.setItems(items);
		
		list.setPrefWidth(250*wRatio);
		list.setPrefHeight(350*hRatio);
		list.setLayoutX(20*wRatio);
		list.setLayoutY(70*hRatio);

		list.setOnMouseClicked(e->{
			if(list.getFocusModel().getFocusedIndex()!=-1)
			{
				System.out.println(list.getFocusModel().getFocusedItem().getUserName()+ " " + list.getFocusModel().getFocusedItem().getPassword());
			}
		});
		
		BackgroundImage back = new BackgroundImage(BACKGROUND, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(BACKGROUND.getWidth()/3, BACKGROUND.getHeight()/3, false, false, false, false)); 
		
		root.setBackground(new Background(back)); 
		//root.setEffect(new DropShadow(3.0, Color.BLACK)); 
		root.getChildren().addAll(screw1, screw2, screw3, screw4, settings,list,c,q, viewDescript, name, close, minimize);

		mainScreen.setScene(scene); 
		mainScreen.initOwner(loginScreen);
		mainScreen.show();
	} 
}