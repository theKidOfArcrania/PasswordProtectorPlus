package ui;

//
import java.awt.Dimension;
import java.awt.DisplayMode;

import javafx.application.Application;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;

public class PasswordProtector extends Application {

	private static final String pseudoPass = "password";

	private static final Image screw = Tools.createImage("screw.png");

	private static final Image textIn = Tools.createImage("OverOn.png");
	private static final Image textInPressed = Tools.createImage("ClickOn.png");
	private static final Image textNone = Tools.createImage("Off.png");
	private static final Image shinyBack = Tools.createImage("background.jpg");
	private static final Image background = Tools.createImage("tile.png");

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
	// private Stage helpScreen;

	public PasswordProtector() {
		sRatio = checkSmallRatio();

	}

	public double checkSmallRatio() {
		return (wRatio > hRatio) ? hRatio : wRatio;
	}

	public void help() {
		if (help.isVisible()) {
			help.setVisible(false);
			return;
		}
		help.setVisible(true);
	}
	/*
	 * public void genHelp(Stage s) { helpScreen = new Stage(); helpScreen.initStyle(StageStyle.TRANSPARENT);
	 * 
	 * AnchorPane root = new AnchorPane(); Scene scene = new Scene(root, 120 * wRatio, 60 * hRatio);
	 * 
	 * BackgroundImage back = new BackgroundImage(background, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(background.getWidth()/3, background.getHeight()/3, false, false, false, false)); root.setBackground(new Background(back)); root.setEffect(new
	 * DropShadow(3.0, Color.BLACK)); helpScreen.setX(100*wRatio); helpScreen.setY(175*hRatio); helpScreen.setScene(scene); helpScreen.initOwner(s); } public void help() { if(helpScreen.isShowing()) { helpScreen.hide(); return; } helpScreen.show(); }
	 */

	public void process() {
		if (passwordField.getText().equals(pseudoPass)) {
			info.setText("Valid Password!");
			passwordField.setText("");
		} else {
			info.setText("Invalid Password! Please try again.");
			passwordField.setText("");
		}
		button.setImage(textNone);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		Scene trans = new Scene(new AnchorPane(), dispWidth, dispHeight);
		trans.setFill(null);
		primaryStage.setScene(trans);
		primaryStage.show();
		primaryStage.setOpacity(0.0);
		Stage back = initBack();
		back.initOwner(primaryStage);
		back.show();
		back.centerOnScreen();
		Stage s = initIntro();
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

		BackgroundImage back2 = new BackgroundImage(shinyBack, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(scene.getWidth(), scene.getHeight(), false, false, false, false));
		root.setBackground(new Background(back2));
		root.setEffect(new InnerShadow(3.0, Color.BLACK));
		primaryStage.setScene(scene);

		return primaryStage;
	}

	private Stage initIntro() {

		Stage primaryStage = new Stage();
		primaryStage.initStyle(StageStyle.TRANSPARENT);

		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(root, 400 * wRatio, 160 * hRatio);

		InnerShadow smallShade = new InnerShadow(2.0, Color.BLACK);
		InnerShadow largeShade = new InnerShadow(5.0, Color.BLACK);
		DropShadow out = new DropShadow(2.0, Color.BLACK);
		DropShadow out2 = new DropShadow(10.0, Color.BLACK);

		ImageView screw1 = Tools.createImageView(screw, 15, 15, 3, 0.0, sRatio, wRatio, hRatio, out);
		ImageView screw2 = Tools.createImageView(screw, 15, 15, 385, 0.0, sRatio, wRatio, hRatio, out);
		ImageView screw3 = Tools.createImageView(screw, 15, 15, 3, 145, sRatio, wRatio, hRatio, out);
		ImageView screw4 = Tools.createImageView(screw, 15, 15, 385, 145, sRatio, wRatio, hRatio, out);

		passwordField = new PasswordField();

		passwordField.setLayoutX(60 * wRatio);
		passwordField.setLayoutY(100 * hRatio);
		passwordField.setPrefSize(200 * wRatio, 15 * hRatio);
		passwordField.setFont(Tools.createBoldFont(12, sRatio));

		button = Tools.createImageView(textNone, 60, 60, 280, 75, sRatio, wRatio, hRatio, out);
		button.setOnMousePressed(e -> {
			if (passwordField.getText().isEmpty()) {
				return;
			}
			button.setImage(textInPressed);
		});
		button.setOnMouseReleased(e -> {
			if (passwordField.getText().isEmpty()) {
				return;
			}
			button.setImage(textIn);
			process();
		});
		Circle c = new Circle(40 * wRatio, 110 * hRatio, 10 * sRatio, Color.rgb(230, 240, 240));
		c.setEffect(largeShade);
		Text q = Tools.createText(34, 100, wRatio, hRatio, "?", Color.rgb(80, 215, 240), smallShade, Tools.createBoldFont(18, sRatio));
		// genHelp(primaryStage);
		q.setOnMousePressed(e -> help());
		help = Tools.createText(60, 127, wRatio, hRatio, "Default Password: password.\nChange Login password in \"Settings\".", Color.rgb(220, 240, 250), smallShade, Tools.createBoldFont(10, sRatio));
		help.setVisible(false);
		Text name = Tools.createText(50, 15, wRatio, hRatio, "Password Protector", Color.rgb(80, 215, 240), smallShade, Tools.createBoldFont(26, sRatio));
		Text instruction = Tools.createText(50, 75, wRatio, hRatio, "Enter Your Password:", Color.rgb(80, 215, 240), smallShade, Tools.createRegularFont(18, sRatio));

		info = Tools.createText(25, 55.0, wRatio, hRatio, "One Password to Save them ALL!", Color.rgb(200, 220, 230), null, Tools.createRegularFont(10, sRatio));
		info.setWrappingWidth(350 * wRatio);
		info.setTextAlignment(TextAlignment.CENTER);

		BackgroundImage back = new BackgroundImage(background, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(background.getWidth() / 3, background.getHeight() / 3, false, false, false, false));
		BackgroundImage back2 = new BackgroundImage(shinyBack, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(scene.getWidth(), scene.getHeight(), false, false, false, false));

		passwordField.textProperty().addListener(e -> {
			if (!passwordField.getText().isEmpty()) {
				button.setImage(textIn);
			} else {
				button.setImage(textNone);
			}
		});
		passwordField.setOnKeyPressed(e -> {
			if (passwordField.getText().isEmpty()) {
				return;
			}
			if (e.getCode() == KeyCode.ENTER) {
				button.setImage(textInPressed);
			}
		});

		passwordField.setOnKeyReleased(e -> {
			if (passwordField.getText().isEmpty()) {
				return;
			}
			if (e.getCode() == KeyCode.ENTER) {
				button.setImage(textIn);
				process();
			}
		});
		passwordField.setBackground(new Background(back2));
		passwordField.setEffect(smallShade);

		Rectangle panel = Tools.createRoundedRectangle(350, 20, 5, 5, 25, 50, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), largeShade);

		root.getChildren().addAll(screw1, screw2, screw3, screw4, panel, name, instruction, passwordField, button, info, c, q, help);
		root.setBackground(new Background(back));
		root.setEffect(largeShade);
		primaryStage.setScene(scene);

		return primaryStage;

	}
}