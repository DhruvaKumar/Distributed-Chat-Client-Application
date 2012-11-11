package chatapp;

/**
 *
 * @author Dhruva
 */

public class UserMessage {
        private String user;
        private String message;
        
        public UserMessage(String u, String m) {
        this.user = u;
        this.message=m;
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
}