package org.passwordprotector.ui;

import org.passwordprotector.ui.Tools;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Blueprint extends Scene {
	public Blueprint(Parent root, double width, double height) {
		super(root, width, height);
		InnerShadow largeShade = new InnerShadow(5.0, Color.BLACK);
		((AnchorPane) root).setBackground(new Background(new BackgroundImage(Tools.createImage("background.jpg"), BackgroundRepeat.REPEAT,
				BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, new BackgroundSize(this.getWidth(), this.getHeight(), false, false, false, false))));
		root.setEffect(largeShade);
		
		this.setOnMouseClicked(e -> ((Stage) this.getWindow()).close());

	}
}
