package ui;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class User
{
    private final static String USERNAME_PROP_NAME = "userName";
    private final static String PASSWORD_PROP_NAME = "password";
    
    private final ReadOnlyStringWrapper userName;
    private StringProperty password;
    
    public User()
    {
        userName = new ReadOnlyStringWrapper(this, USERNAME_PROP_NAME, System.getProperty("user.name"));
        password = new SimpleStringProperty(this, PASSWORD_PROP_NAME, "");
    }
    public final String getUserName()
    {
        return userName.get();
    }
    public final String getPassword()
    {
        return password.get();
    }
    
    public final void setPassword(String password)
    {
        this.password.set(password);
    }
    public ReadOnlyStringProperty userNameProperty()
    {
        return userName.getReadOnlyProperty();
    }
    
    public StringProperty passwordProperty()
    {
        return password;
    }
    public String toString()
    {
        return String.format("User[username=%s, password=%s]",getUserName(),getPassword());
    }
}

public class Trial extends Application
{

    private final static String MY_PASS = "<32peoples";
    private final static BooleanProperty GRANTED_ACCESS = new SimpleBooleanProperty(false);
    private final static int MAX_ATTEMPTS = 3;
    private final static IntegerProperty ATTEMPTS = new SimpleIntegerProperty(0);

    private static final Color foregroundColor = Color.rgb(255, 255, 255, 0.9);

    private Stage primaryStage;
    private Group root;
    private User user;

    private Rectangle drawRectangleBackground()
    {
        Rectangle background = new Rectangle(320, 112);
        background.setX(0);
        background.setY(0);
        background.setArcHeight(15);
        background.setArcWidth(15);
        background.setFill(Color.rgb(0, 0, 0, 0.55));
        background.setStrokeWidth(1.5);
        background.setStroke(foregroundColor);

        return background;
    }

    private HBox drawRow1()
    {
        Text userNameField = new Text();
        userNameField.setFont(Font.font("SanSerif", FontWeight.BOLD, 30));
        userNameField.setFill(foregroundColor);
        userNameField.setSmooth(true);

        // bind the field to the user.username
        userNameField.textProperty().bind(user.userNameProperty());

        HBox userNameCell = new HBox();
        userNameCell.prefWidthProperty().bind(primaryStage.widthProperty().subtract(45));
        userNameCell.getChildren().add(userNameField);

        HBox row1 = new HBox();
        row1.getChildren().addAll(userNameCell);

        return row1;
    }

    private HBox drawRow2()
    {
        PasswordField passwordField = new PasswordField();
        passwordField.setFont(Font.font("SanSerif", 20));
        passwordField.setPromptText("Password");
        passwordField.setStyle("-fx-text-fill:black; "
                + "-fx-prompt-text-fill:gray; "
                + "-fx-highlight-text-fill:black; "
                + "-fx-highlight-fill: gray; "
                + "-fx-background-color: rgba(255, 255, 255, .80); ");

        passwordField.prefWidthProperty().bind(primaryStage.widthProperty().subtract(55));
        user.passwordProperty().bind(passwordField.textProperty());

        // error icon
        SVGPath deniedIcon = new SVGPath();
        deniedIcon.setFill(Color.rgb(255, 0, 0, .9));
        deniedIcon.setStroke(Color.WHITE);//
        deniedIcon.setContent("M24.778,21.419 19.276,15.917 24.777,10.415 21.949,7.585 "
                + "16.447,13.08710.945,7.585 8.117,10.415 13.618,15.917 8.116,21.419 10"
                + ".946,24.248 16.447,18.746 21.948,24.248z");
        deniedIcon.setVisible(false);

        SVGPath grantedIcon = new SVGPath();
        grantedIcon.setFill(Color.rgb(0, 255, 0, .9));
        grantedIcon.setStroke(Color.WHITE);//
        grantedIcon.setContent("M2.379,14.729 5.208,11.899 12.958,19.648 25.877,"
                + "6.733 28.707,9.56112.958,25.308z");
        grantedIcon.setVisible(false);

        //
        StackPane accessIndicator = new StackPane();
        accessIndicator.getChildren().addAll(deniedIcon, grantedIcon);
        accessIndicator.setAlignment(Pos.CENTER_RIGHT);

        grantedIcon.visibleProperty().bind(GRANTED_ACCESS);

        // user hits the enter key
        passwordField.setOnAction(actionEvent ->
        {
            if (GRANTED_ACCESS.get())
            {
                System.out.printf("User %s is granted access.\n", user.getUserName());
                try{
                	Scanner scan = new Scanner(new File("Password.txt"));
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
                Platform.exit();
            }
            else
            {
                deniedIcon.setVisible(true);
            }

            ATTEMPTS.set(ATTEMPTS.add(1).get());
            if(ATTEMPTS.get()<MAX_ATTEMPTS)
            {
            	System.out.println("Attempts: " + ATTEMPTS.get());
            }
            
        });

        // listener when the user types into the password field
        passwordField.textProperty().addListener((obs, ov, nv) ->
        {
            boolean granted = passwordField.getText().equals(MY_PASS);
            GRANTED_ACCESS.set(granted);
            if (granted)
            {
                deniedIcon.setVisible(false);
            }
        });

        // listener on number of attempts
        ATTEMPTS.addListener((obs, ov, nv) ->
        {
            if (MAX_ATTEMPTS == nv.intValue() && !GRANTED_ACCESS.get())
            {
                // failed attempts
                System.out.printf("User %s is denied access.\n",user.getUserName());
                Platform.exit();
            }
        });

        // second row
        HBox row2 = new HBox(3);
        row2.getChildren().addAll(passwordField, accessIndicator);

        return row2;
    }

    @Override
    public void start(Stage primaryStage)
    {
        this.primaryStage = primaryStage;
        user = new User();

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        root = new Group();
        Scene scene = new Scene(root, 320, 112, Color.rgb(0, 0, 0, 0));

        primaryStage.setScene(scene);

        Rectangle background = this.drawRectangleBackground();
        HBox row1 = this.drawRow1();
        HBox row2 = this.drawRow2();

        VBox formLayout = new VBox(4); // spacing
        formLayout.setLayoutX(12);
        formLayout.setLayoutY(12);

        formLayout.getChildren().addAll(row1, row2);

        root.getChildren().addAll(background, formLayout);

        primaryStage.show();
    }
    public static void main(String[] args)
    {
        launch(args);
    }
}