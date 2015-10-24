package ui;


import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Tools {
	static {
		Font.loadFont(ClassLoader.getSystemResourceAsStream("ui/Xolonium-Regular.otf"), 10);
		Font.loadFont(ClassLoader.getSystemResourceAsStream("ui/Xolonium-Bold.otf"), 10);
	}

	public static Font createBoldFont(double size, double sRatio) {
		return Font.font("Xolonium", FontWeight.BOLD, size * sRatio);
	}

	public static Image createImage(String res) {
		return new Image(ClassLoader.getSystemResourceAsStream("ui/" + res));
	}

	public static ImageView createImageView(Image img, double h, double w, double x, double y, double sRatio, double wRatio, double hRatio, Effect g) {
		ImageView sv = new ImageView();
		sv.setImage(img);
		sv.setFitHeight(h * sRatio);
		sv.setFitWidth(w * sRatio);
		sv.setPreserveRatio(true);
		sv.setSmooth(true);
		sv.setCache(true);
		sv.setEffect(g);
		AnchorPane.setLeftAnchor(sv, x * wRatio);
		AnchorPane.setTopAnchor(sv, y * hRatio);

		return sv;
	}

	public static Font createRegularFont(double size, double sRatio) {
		return Font.font("Xolonium", size * sRatio);
	}

	public static Rectangle createRoundedRectangle(double w, double h, double xR, double yR, double x, double y, double sRatio, double wRatio, double hRatio,
			Color c, Effect g) {
		Rectangle r = new Rectangle(w * wRatio, h * hRatio);
		r.setArcHeight(yR * hRatio);
		r.setArcWidth(xR * wRatio);
		AnchorPane.setTopAnchor(r, y * hRatio);
		AnchorPane.setLeftAnchor(r, x * wRatio);
		r.setFill(c);
		r.setEffect(g);

		return r;
	}

	public static Text createText(double x, double y, double wRatio, double hRatio, String text, Color color, Effect g, Font f) {
		Text t = new Text(text);
		t.setFill(color);
		t.setFont(f);
		AnchorPane.setTopAnchor(t, y * hRatio);
		AnchorPane.setLeftAnchor(t, x * wRatio);
		t.setEffect(g);

		return t;
	}

	public void setOwner(Stage t, Window p) {
		t.initOwner(p);
	}
}
