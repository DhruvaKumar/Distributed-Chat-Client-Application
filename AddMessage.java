/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatapp;

import javafx.collections.ObservableList;

/**
 *
 * @author Dhruva
 */
public class AddMessage {
    public AddMessage(String user1, String message, String pName,boolean who)
    {
        for(int i=0;i<UserList.peers.size();i++)
        {
             
            if(UserList.peers.get(i).peerName.equals(pName))
            {
                if(!(UserList.peers.get(i).currentUser.equals(who?user1:pName)))
                {
                UserList.peers.get(i).obs.add(new UserMessage(who?user1:pName, message));
                UserList.peers.get(i).currentUser=who?user1:pName;
                }
            else
                {
                    UserMessage lastUserMessage = UserList.peers.get(i).obs.get(UserList.peers.get(i).obs.size()-1);
                    lastUserMessage.add(message);
                    UserList.peers.get(i).obs.set(UserList.peers.get(i).obs.size() - 1, lastUserMessage);
                }
            
            }
        }
       
}
}