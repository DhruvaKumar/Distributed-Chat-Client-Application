package chatapp;

/**
 *
 * @author Dhruva
 */

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Chat{
    ObservableList<UserMessage> obs = FXCollections.observableArrayList();
    ListView<UserMessage> list = new ListView<UserMessage>();
    String currentUser="";
    String peerName="";
    Stage stage = new Stage();
    
    public Chat(final String pUser, final String me)
    {
        peerName = pUser;
      
      stage.setTitle(pUser);
      stage.setWidth(380);
      stage.setHeight(480);
      
      BorderPane border= new BorderPane();
     
     //Peer's username
     Text userName = new Text();
     userName.setText(pUser);
     userName.setFill(Color.rgb(38,91,211));
     userName.setEffect(new Lighting());
     userName.setBoundsType(TextBoundsType.VISUAL);
     userName.setFont(Font.font(Font.getDefault().getFamily(), 25));
    
      
    //Hbox 1 - ListView & Clear button
      //ListView
      list.setEditable(true);
      list.setPrefSize(240, 400);
      
      list.setItems(obs);
      //Cell Factory
      list.setCellFactory(new Callback<ListView<UserMessage>, ListCell<UserMessage>>() {
            @Override public ListCell<UserMessage> call(ListView<UserMessage> list) {
                 return new UserMessageListCell();
            }
        });
            
      
      //Clear
      Button clear = new Button();
      clear.setText("Clear");
      
      HBox hbox1 = new HBox();
      hbox1.getChildren().addAll(list, clear);
      hbox1.setSpacing(5);
      
      //HBox 2 Layout - (TextArea,SendButton)
        //Text Area
      final TextArea text = new TextArea();
      //text.setPromptText("Enter text");
      text.setFocusTraversable(true);
      text.setPrefSize(240, 5);
      text.setWrapText(true);
      //Set as default focus
      Platform.runLater(new Runnable() {
	@Override
	public void run() {
	    text.requestFocus();
	}
        });
      //Event Handler (text)
      text.setOnKeyReleased(new EventHandler<KeyEvent>() {
        final KeyCombination comb1 = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
        final KeyCombination comb2 = new KeyCodeCombination(KeyCode.ENTER);

         public void handle(KeyEvent t) 
         {
                if (comb2.match(t))//For Enter 
                {
                        Global.chat(pUser,text.getText());
                        new AddMessage(me, text.getText(), pUser,true);
                        //obs.addAll(text.getText());
                        text.clear();
                }
                else if(comb1.match(t))//For Shift+Enter
                {
                        text.deleteText(text.getSelection());
                        text.insertText(text.getCaretPosition(), "\n");
                        t.consume();
                }
        }
        });
       //Event Handler (clear)
      clear.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                obs.clear();
                currentUser="";
                text.requestFocus();
        }
      }
    );
      //Send Button
      Button send = new Button();
      send.setText("Send");
      //Event Handler for Send
       send.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                        Global.chat(pUser,text.getText()+"\n");
                        new AddMessage(me, text.getText()+"\n", pUser,true);
                        //obs.addAll(text.getText());
                        text.clear();
                        text.requestFocus();
                        }
        });
      
      HBox hbox2 = new HBox();
      hbox2.getChildren().addAll(text, send);
      //hbox2.setPadding(new Insets(5, 5, 20, 30));
      hbox2.setSpacing(5);
      
      //VBox layout
      VBox vbox = new VBox();
      vbox.getChildren().addAll(userName, hbox1, hbox2);
      vbox.setSpacing(20);
      
      border.setCenter(vbox);
      border.setMargin(vbox, new Insets(20,10,30,30));
      
      //Layout contd...
      Scene scene = new Scene(border, 380, 480);
      stage.setScene(scene);
      stage.show();
      
     }
    //Add new message
  
  
  
  
}

       
