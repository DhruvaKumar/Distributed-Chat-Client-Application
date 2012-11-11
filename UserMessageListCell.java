package chatapp;

/**
 *
 * @author Dhruva
 */

import java.io.File;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

//ListCell  
  public class UserMessageListCell extends ListCell<UserMessage> {
  private HBox hbox = new HBox();
  private VBox msgBox = new VBox();
  //private Button butt1 = new Button();
  private Image profilePic = new Image("");
  private ImageView iv2 = new ImageView();
  private Text msg = new Text();
  private Text userN = new Text();
  
  public UserMessageListCell() {
    userN.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 12));
    iv2.setImage(profilePic);
    msgBox.getChildren().addAll(userN, msg);
    msgBox.setSpacing(5);
    hbox.getChildren().addAll(iv2, msgBox);
    hbox.setSpacing(5);
  }
  
  @Override
        public void updateItem(UserMessage item, boolean empty) {
          super.updateItem(item, empty);
            if (item != null)
            {
              StringBuilder sb1 = new StringBuilder();
              sb1.append("images/").append(item.getUser()).append(".png");
               try {
                    File file = new File(getClass().getResource(sb1.toString()).getFile());
                    profilePic = new Image(getClass().getResourceAsStream(sb1.toString()), 50, 50, true, true);
               }
               catch(Exception e){
                    profilePic = new Image(getClass().getResourceAsStream("images/user.png"), 50, 50, true, true);
                }
              iv2.setImage(profilePic);
              
              userN.setText(item.getUser().concat(": "));
              msg.setText(item.getMessage());
              setGraphic(hbox);
            }
  }
  }
    

