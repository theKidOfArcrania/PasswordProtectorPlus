package ui;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import ui.Tools;
import javafx.animation.Animation.Status;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;

public class PasswordProtector extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	private final DisplayMode defaultMode = getLocalGraphicsEnvironment().getDefaultScreenDevice()
		.getDisplayMode();
	private final Dimension displayRes = new Dimension(defaultMode.getWidth(), defaultMode.getHeight());
	private final double dispWidth = displayRes.getWidth();
	private final double dispHeight = displayRes.getHeight();
	private final double wRatio = dispWidth / 1600.0;
	private final double hRatio = dispHeight / 900.0;

	public double sRatio;


	public PasswordProtector() {
		sRatio = checkSmallRatio();
	}

	public double checkSmallRatio() {
		return (wRatio > hRatio) ? hRatio : wRatio;
	}


	/*public void createTransparentStage(Stage primaryStage) {
		Stage second = new Stage();
		second.initStyle(StageStyle.TRANSPARENT);
		second.initOwner(primaryStage);
		
		AnchorPane root = new AnchorPane();
		Blueprint scene = new Blueprint(root, 1300 * wRatio, 800 * hRatio);

		second.setScene(scene);
		second.show();
	}*/

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		Scene trans = new Scene(new AnchorPane(), dispWidth, dispHeight);
		trans.setFill(null);
		primaryStage.setScene(trans);
		primaryStage.show();
		primaryStage.setOpacity(0.0);
		
		Stage s = initIntro();
		s.initOwner(primaryStage);
		s.show();
	}


	private Stage initIntro() {

		Stage primaryStage = new Stage();
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		
		AnchorPane root = new AnchorPane();
		Scene scene = new Scene(root, 400 * wRatio, 200 * hRatio);

		InnerShadow smallShade = new InnerShadow(2.0, Color.BLACK);
		InnerShadow largeShade = new InnerShadow(5.0, Color.BLACK);
		DropShadow out = new DropShadow(2.0, Color.BLACK);
		DropShadow out2 = new DropShadow(3.0, Color.rgb(40,100,120));
		 try{
         	Scanner scan = new Scanner(new File(""));
         	while(scan.hasNextLine())
         	{
         		System.out.println(scan.nextLine());
         	}
         	scan.close();
         }
         catch(IOException e)
         {
         	System.out.println("File not found.");
         }
		
		Image screw = Tools.createImage("screw.png");
		ImageView screw1 = Tools.createImageView(screw, 15, 15, 3, 0.0, sRatio, wRatio, hRatio, out);
		ImageView screw2 = Tools.createImageView(screw, 15, 15, 385, 0.0, sRatio, wRatio, hRatio, out);
		ImageView screw3 = Tools.createImageView(screw, 15, 15, 3, 185, sRatio, wRatio, hRatio, out);
		ImageView screw4 = Tools.createImageView(screw, 15, 15, 385, 185, sRatio, wRatio, hRatio, out);

		ImageView button = Tools.createImageView(screw, 15, 15, 385, 185, sRatio, wRatio, hRatio, out);
		

		Text name = Tools.createText(50, 15, wRatio, hRatio, "Password Protector", Color.GRAY.darker(), smallShade, Tools.createBoldFont(26, sRatio));
		Text instruction = Tools.createText(35, 85, wRatio, hRatio, "Enter Your Password:", Color.rgb(80,215,240), out2, Tools.createRegularFont(15.5, sRatio));
		
		Image background = Tools.createImage("background.jpg");
		BackgroundImage back = new BackgroundImage(background, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(scene.getWidth(), scene.getHeight(), false, false, false, false));
		
		PasswordField passwordField = new PasswordField();
		passwordField.setLayoutX(30);
		passwordField.setLayoutY(110);
		
		passwordField.setBackground(new Background(back));
		passwordField.setEffect(smallShade);
		//Text info = Tools.createText(75.0, 55.0, wRatio, hRatio, "One Password to Save Them All! ", Color.rgb(230,230,230), null, Tools.createRegularFont(13, sRatio));
		
		Rectangle panel = Tools.createRoundedRectangle(350, 120, 5, 5, 25, 50, sRatio, wRatio, hRatio, Color.DARKGRAY.darker().darker(), largeShade);

		root.getChildren().addAll(screw1, screw2, screw3, screw4, panel,name, instruction,passwordField);
		
		root.setBackground(new Background(back));
		root.setEffect(largeShade);
		primaryStage.setScene(scene);

		return primaryStage;

	}
}