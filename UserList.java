/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;


/**
 *
 * @author Dhruva
 */

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import np.com.ngopal.control.AutoFillTextBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.scene.control.ListCell;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.util.Callback;



/*public class UserList extends Application{
    
    public static void main(String[] args){
        Application.launch(args);
    }
           
    public void start(Stage stage){*/

public class UserList {
    
    public static ArrayList<Chat> peers=new ArrayList<Chat>();
    public static ObservableList<String> users;
    public UserList(final String user)
    {
    Stage stage = new Stage();
    stage.setTitle("UserList");
    stage.setWidth(380);
    stage.setHeight(520);
    
    //UserName Text 
    //String user="Dhruva";
    Text t = new Text();
    t.setText("Welcome, "+user);
    t.setFill(Color.rgb(0, 147, 255));;
    t.setBoundsType(TextBoundsType.VISUAL);
    t.setFont(Font.font(Font.getDefault().getFamily(), 25));
    
    
    users = FXCollections.observableArrayList();
    
    users.setAll(Global.friends);
    //ObservableList<String> users = FXCollections.observableArrayList("Kaustubh", "Dexter", "Jim Morrison", "Moriarty", "Morgan", "Jeff Lindsay");
    
    //AutoFillTextBox    
    final AutoFillTextBox box = new AutoFillTextBox(users);
    box.setListLimit(10);
    box.setFilterMode(true);
    box.getListview().setVisible(false);
    box.setTextBoxWidth(290);
    
   //ListView    
   final ListView<String> list = new ListView<String>();
   list.itemsProperty().bind(box.getListview().itemsProperty());
   list.setOnMouseClicked(new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            System.out.println("Clicked on " + list.getSelectionModel().getSelectedItem());

            boolean open = false;
            for(int i=0; i<peers.size(); i++){
                    if(peers.get(i).peerName.equals(list.getSelectionModel().getSelectedItem()))
                    {
                        peers.get(i).stage.show();
                        peers.get(i).stage.toFront();
                        open = true; 
                    }
                        
            }
            if(!open){
                peers.add(new Chat(list.getSelectionModel().getSelectedItem(), user));
                Global.chat(list.getSelectionModel().getSelectedItem(), "");
            }
        }
    });
      //Cell Factory
      list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override public ListCell<String> call(ListView<String> list) {
                 ListCell<String> cell = new ListCell<String>() {
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item);
                            InnerShadow is = new InnerShadow();
                            is.setOffsetX(1.0);
                            is.setColor(Color.web("#c8c8c8"));
                            is.setOffsetY(1.0);
                            setEffect(is);
                            Reflection reflection = new Reflection();
                            is.setInput(reflection);
                        }
                    }
                };
                cell.setPrefHeight(50);
                return cell;
            }
        });
   
   //Search Icon
   Image search = new Image(getClass().getResourceAsStream("images/search.png"), 30, 30, true, true);
   ImageView isearch = new ImageView();
   isearch.setImage(search);
   
   HBox hbox = new HBox();
   hbox.getChildren().addAll(isearch, box);
   hbox.setSpacing(5);
   
   final TextField addT = new TextField();
   addT.setPromptText("Enter UserName");
   addT.setVisible(false);
   //Add Button
   Button addB = new Button("Add User");
   addB.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                       addT.setVisible(true);
                        }
        });
    //Add user TextField
    addT.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent t) 
         {
              Global.friend(addT.getText(), true);
         }
        });
    HBox hbox2 = new HBox();
    hbox2.getChildren().addAll(addB, addT);
    hbox2.setSpacing(10);
   
   //VBox - Overall Layout
   VBox vbox = new VBox();
   vbox.getChildren().addAll(t,hbox,list, hbox2);
   vbox.setSpacing(20);
   VBox.setVgrow(list, Priority.ALWAYS);
   vbox.setPadding(new Insets(20,20,10,20));
   Scene scene = new Scene(vbox,300,200);
   
   //Shadow Effect
   DropShadow ds = new DropShadow();
   ds.setOffsetY(0.5);
   ds.setOffsetX(0.5);
   ds.setColor(Color.GRAY);
   list.setEffect(ds);
   hbox.setEffect(ds);
   

   stage.setScene(scene);
   scene.getStylesheets().addAll(UserList.class.getResource("UserList.css").toExternalForm());
   stage.show();
        
    
    
    }
    
}