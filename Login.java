
package chatapp;

/**
 *
 * @author Dhruva
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Login extends Application {
    public static ChatNetwork cn = null;
    static Text comment = new Text();
    static Stage primaryStage;
    static PasswordField pwBox = new PasswordField();
    static int loginStatus = 0;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(final Stage stage) {
        primaryStage=stage;
        primaryStage.setTitle("Welcome");
        
        //Layout - GridPane
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        //Title - Welcome 
        Text scenetitle = new Text("Welcome");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        //Username
        Label userName = new Label("User Name:");
        grid.add(userName, 0, 2);
        final TextField userTextField = new TextField();
        grid.add(userTextField, 1, 2);

        //Password
        Label pw = new Label("Password:");
        grid.add(pw, 0, 3);
        grid.add(pwBox, 1, 3);
       
        //Sign in button
        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        grid.add(comment, 1, 6); //Comment to sign in status
        
        //Check for correct credentials:
            //By clicking the button "Sign In" (Event Handler)
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                 if(cn==null) 
                     cn = new ChatNetwork(userTextField.getText(), pwBox.getText()); 
                 while(loginStatus==0){
                        Global.sleep(100);
                        passCheck(userTextField.getText(), pwBox.getText(),loginStatus==1?true:false);
                    }
            }
        });
            //By pressing Enter (Event Handler)
        pwBox.setOnKeyReleased(new EventHandler<KeyEvent>() {
        final KeyCombination comb = new KeyCodeCombination(KeyCode.ENTER);
        public void handle(KeyEvent t) 
         {
             if (comb.match(t))
             {
                 if(cn==null)
                     cn = new ChatNetwork(userTextField.getText(), pwBox.getText());
                 while(loginStatus==0){
                     Global.sleep(100);
                     passCheck(userTextField.getText(), pwBox.getText(),loginStatus==1?true:false);
                 }
                 pwBox.clear();
             }
         }
        });
        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);
        scene.getStylesheets().addAll(Login.class.getResource("Login.css").toExternalForm()); //CSS
        primaryStage.show();
    }
    
    //Check credentials
    public static void passCheck(String user, String pass, boolean check)
    {
        if(check) //If True
        {
            new UserList(user); //Create a UserList window and send over the User's name
            primaryStage.close(); //Close the Login window
        }
        else //If False
        {
            comment.setFill(Color.FIREBRICK);
            comment.setText("Incorrect Password!"); //Display comment
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    pwBox.requestFocus();
                }
            });
        }
    }
}


