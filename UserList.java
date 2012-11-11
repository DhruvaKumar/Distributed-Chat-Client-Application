package chatapp;

/**
 *
 * @author Dhruva
 */

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.util.Callback;
import np.com.ngopal.control.AutoFillTextBox;


public class UserList {
    public static ArrayList<Chat> peers=new ArrayList<Chat>();
    public static ObservableList<String> users;
    public UserList(final String user)
    {
    Stage stage = new Stage();
    stage.setTitle("UserList");
    stage.setWidth(380);
    stage.setHeight(520);
    //Welcome <User>
    Text t = new Text();
    t.setText("Welcome, "+user);
    t.setFill(Color.rgb(0, 147, 255));;
    t.setBoundsType(TextBoundsType.VISUAL);
    t.setFont(Font.font(Font.getDefault().getFamily(), 25));
    
    users = FXCollections.observableArrayList();
    users.setAll(Global.friends);
    
    //Search Bar (AutoFillTextBox)    
    final AutoFillTextBox box = new AutoFillTextBox(users);
    box.setListLimit(10);
    box.setFilterMode(true);
    box.getListview().setVisible(false);
    box.setTextBoxWidth(290); 
  
    //ListView (UserList)
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
      
    //Cell Factory (Customization of the cell)
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
    //HBox -[Search Icon, Search bar]
    HBox hbox = new HBox();
    hbox.getChildren().addAll(isearch, box);
    hbox.setSpacing(5);
   
    //Add New User tab
    final TextField addT = new TextField();
    addT.setPromptText("Enter UserName");
    addT.setVisible(false);
    //Add Button
    Button addB = new Button("Add User");
    //Set the AddNewUser field visible only when the Add button is clicked
    addB.setOnAction(new EventHandler<ActionEvent>() {
            @Override 
            public void handle(ActionEvent e) {
                addT.setVisible(true); 
            }
    });
    //AddNewUser TextField - Adds new user
    addT.setOnAction(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent t) {
              Global.friend(addT.getText(), true);
        }
    });
    //HBox - [AddButton, AddNewUserField]
    HBox hbox2 = new HBox();
    hbox2.getChildren().addAll(addB, addT);
    hbox2.setSpacing(10);
   
    //VBox - Overall Layout [Welcome title:Search bar:User List:AddNewUser bar]
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
    scene.getStylesheets().addAll(UserList.class.getResource("UserList.css").toExternalForm()); //CSS
    stage.show();
    }
}