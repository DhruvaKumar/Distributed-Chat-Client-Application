/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

/**
 *
 * @author Dhruva
 */


public class UserMessage {
        private String user;
        private String message;
       // private String imageUrl;

        public UserMessage(String u, String m) {
        this.user = u;
        this.message=m;
       // this.imageUrl=i;
        }
        public String getUser() {
        return user;
        }       
        public void setUser(String user) {
        this.user = user;
        }
        public String getMessage() {
        return message;
        }
        public void setMessage(String message) {
        this.message = message;
        }
        
        public void add(String message){
            this.message=this.message.concat(message);
        }
        
       
   /* public UserMessage(String u, String m) {
    setUser(u);
    setMessage(m);
  }
     private static StringProperty user;
  final public void setUser(String value) { 
    userProperty().set(value); 
  }
  final public static String getUser() { 
    return userProperty().get(); 
  }
  final public static StringProperty userProperty() { 
    /*if (user == null) {
      user = new SimpleStringProperty(this, "user");
    }
    return user; 
  }
  
  private static StringProperty message;
  final public void setMessage(String value) { 
    messageProperty().set(value); 
  }
  final public static String getMessage() { 
    return messageProperty().get(); 
  }
  final public static StringProperty messageProperty() { 
    /*if (message == null) {
      message = new SimpleStringProperty(this, "message");
    }
    return message; 
  }
  
  */
  
       
}