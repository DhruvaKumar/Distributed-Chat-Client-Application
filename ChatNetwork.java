package chatapp;

/**
 *
 * @author Kaustubh Karkare
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.sql.*;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import javax.crypto.*;
import javax.crypto.spec.*;

import javafx.concurrent.Task;
import javafx.application.Platform;

class Global {
	public static String b16encode(byte[] data){
		String result = "", code = "0123456789ABCDEF";
		for(int i=0; i<data.length; ++i) result+=code.charAt((data[i] & 0xF0)>>4)+""+code.charAt(data[i] & 0x0F);
		return result;
		}
	public static byte[] b16decode(String data){
		byte[] result = new byte[data.length()/2];
		for(int i=0; i<data.length(); i+=2) result[i/2] = (byte)Integer.parseInt(data.substring(i,i+2),16);
		return result;
		}
	public static String b64encode(byte[] data){ return javax.xml.bind.DatatypeConverter.printBase64Binary(data); }
	public static byte[] b64decode(String data){ return javax.xml.bind.DatatypeConverter.parseBase64Binary(data); }
	public static void sleep(long ms){
		try { Thread.sleep(ms); }
		catch (InterruptedException e) { e.printStackTrace(); }
		}
	
	public static final char delimiter1 = '\n';
	public static final char delimiter2 = '|';
	public static final char delimiter3 = ':';
	
	public static final String peerHello = "10"; // code|own-ip:own-port
	public static final String peerAccept = "11"; // code
	public static final String peerReject = "12"; // code
	public static final String peerGet = "13"; // code
	public static final String peerList = "14"; // code|peer1-ip:peer1-port|peer2-ip:peer2-port|...
	
	public static final String serverTimestamp = "20"; // code|route
	public static final String serverHello = "21"; // code|E(root-public){b64e{username1}:b64e{password1}|b64e{aes-key}}|route
	public static final String serverHelloAccept = "22"; // code|E(aes-key){session-id|b64e{friend1}|b64e{friend2}|...}|route
	public static final String serverHelloReject = "23"; // code|route
	public static final String serverRegister = "24"; // code|E(root-public){b64e{username1}:b64e{password1}|b64e{aes-key}}|route
	public static final String serverRegisterAccept = "25"; // code|E(aes-key){session-id|b64e{friend1}|b64e{friend2}|...}|route
	public static final String serverRegisterReject = "26"; // code|route
	public static final String serverFriends = "27"; // code|session-id|E(aes-key){[add/delete]:b64e{friend1}|[add/delete]:b64e{friend2}|...}|route
	public static final String serverFriendsAccept = "28"; // code|E(aes-key){session-id|b64e{friend1}|b64e{friend2}|...}|route
	public static final String serverFriendsReject = "29"; // code|route
	
	public static final String friendLocate = "30"; // code|b64e{src-nick}:b64e{dest-nick}|dhke-pk1|route
	public static final String friendRespond = "31"; // code|b64e{src-nick}:b64e{dest-nick}|dhke-pk2|E(session-key)E(root-public){b64e{dest-pass}:dest-nonce}}|route
	public static final String friendVerify = "32"; // code|session-id|E(aes-key){b64e{dest-name}|E(root-public){b64e{dest-pass}:dest-nonce}}|route
	public static final String friendValid = "33"; // code|E(aes-key){dest-name:dest-nonce}|route
	public static final String friendProve = "34"; // code|b64e{src-nick}:b64e{dest-nick}|E(session-key){src-nonce:dest-nonce}
	public static final String friendConfirm = "35"; // code|b64e{dest-nick}:b64e{src-nick}|E(session-key){src-nonce}
	public static final String friendMessage = "36"; // code|b64e{src-nick}:b64e{dest-nick}|E(session-key){type:sequence|message}
	public static final String friendLinkError = "37"; // code|b64e{dest-nick}:b64e{src-nick}
	
	public static final String udpDiscover = "40"; // code|host:port
	public static final String udpResponse = "41"; // code|host:port
	public static final int tcpPortMin = 420;
	public static final int tcpPortMax = 430;
	public static final int udpPortMin = 420;
	public static final int udpPortMax = 430;
	
	public static final String escape = "\\";
	
	public static volatile int nextSessionId = 1;
	public static final int maxLinks = 5;
	public static volatile int nextLinkId = 1;
	public static volatile ArrayList<String> friends = new ArrayList<String>();
	public static volatile ArrayList<InetSocketAddress> peers = new ArrayList<InetSocketAddress>();
	public static volatile ArrayList<InetSocketAddress> reject = new ArrayList<InetSocketAddress>();
	public static volatile ArrayList<InetSocketAddress> links = new ArrayList<InetSocketAddress>();
	public static volatile ArrayList<TcpClient> clients = new ArrayList<TcpClient>();
	public static volatile ArrayList<ChatSession> chats = new ArrayList<ChatSession>();
	public static volatile ArrayList<ChatLocateRequest> chatLocateRequest = new ArrayList<ChatLocateRequest>();
	public static volatile ArrayList<ChatRoute> chatRoute = new ArrayList<ChatRoute>();
	public static volatile TcpServer tcpServer = null;
	public static volatile UdpServer udpServer = null;

	public static volatile String username;
	public static volatile String username_b64e;
	public static volatile String password;
	public static int debugLevel = 2;
	
	public static volatile RootServer rootServer = null;
	public static volatile StepAction stepAction = null;
	
	public static volatile TcpClient rootHop;
	public static final RsaSystem rootPublic = new RsaSystem(RsaSystem.ENCRYPT_MODE,new BigInteger("65537"),new BigInteger("24888670892546444629481134519908722459865642358862039369925675966806808080628597865844351771657838855628145189784045373478084030264473938528578791633970277081794341622482225201074357672352595890613954137928074046941727465532236060564166631138787470570122639661445819098683313199178585587067871948575753281320782885105466266118229637256672599163075454752198423077697861396651501976522502180085309804749958030400062357780105888868335363638231206716480451433353159645375209864129211773865316150539730983455474600072649414205241229058617426474674426347677540465155788137904339651459087681540610901496236155302325419850033"));
	public static RsaSystem rootPrivate = null;
	public static final String charset = "ISO-8859-1";
	public static final AesSystem rootSession = new AesSystem();
	public static volatile int rootSessionId = -1;
	
	public static final int sessionTime = 15;
	public static String sqlDatabaseFile = "dscc.db";
	public static Connection sqlConnection;
	public static Statement sqlStatement;
	
	public static volatile int timestamp = -1;
	public static volatile int timestampHops = -1;
	public static volatile int localTime = -1;
	
	public static synchronized void arrayPrint(Object[] a,String s){
		System.out.print(s+" ["+a.length+"] : "); for(int i=0;i<a.length;++i) System.out.print(a[i]+" "); System.out.println();
		}
	
	public static synchronized void process(TcpClient that, String command, int LinkId){
		if(command.startsWith(Global.serverTimestamp)){
			String[] temp1 = command.split(Global.escape+Global.delimiter2);
			int t = Integer.parseInt(temp1[1]);
			int h = Integer.parseInt(temp1[2]);
			if(t>Global.timestamp || h<Global.timestampHops || that==null){
				Global.timestamp = t; Global.timestampHops = h; Global.rootHop = that;
				for(int i=0;i<Global.clients.size();++i)
					if(Global.clients.get(i)==that) continue;
					else if(LinkId>0) Global.clients.get(i).send("20"+Global.delimiter2+t+Global.delimiter2+(h+1)+Global.delimiter1);
					else Global.clients.get(i).send(command+Global.delimiter1);
				}
			}
		else if(command.startsWith(Global.serverHello)){
			if(Global.rootPrivate==null) Global.rootHop.send(command+Global.delimiter3+LinkId+Global.delimiter1);
			else {
				// Evaluate as Root, and send back along the same route
				String[] temp1 = command.split(Global.escape+Global.delimiter2);
				String[] temp2 = Global.rootPrivate.decrypt(temp1[1]).split(Global.escape+Global.delimiter2);
				int j = temp2[0].indexOf(Global.delimiter3);
				try {
					String username = new String(Global.b64decode(temp2[0].substring(0,j)), Global.charset);
					String password = new String(Global.b64decode(temp2[0].substring(j+1)), Global.charset);
					if(Global.debugLevel>=2) System.out.println("Verifying credentials : "+username+"/"+password+" ... ");
					PreparedStatement ps = Global.sqlConnection.prepareStatement("SELECT * FROM users WHERE user=? AND pass=?");
					ps.setString(1,username); ps.setString(2,password);
					ResultSet rs = ps.executeQuery();
					if(rs.next()){
						int sess, t;
						try {
							t = Integer.parseInt(rs.getString("time"));
							if(t+Global.sessionTime>Global.timestamp) sess = Integer.parseInt(rs.getString("sid"));
							else sess = Global.nextSessionId++;
							}
						catch(NumberFormatException e){ sess = Global.nextSessionId++; }
						rs.close();
						if(Global.debugLevel>=2) System.out.println("Authenticated : "+username+"/"+password);
						ps = Global.sqlConnection.prepareStatement("UPDATE users SET sid=?, key=?, time=? WHERE user=? AND pass=?");
						ps.setString(1,Integer.toString(sess)); ps.setString(2,temp2[1]); ps.setString(3,Integer.toString(Global.timestamp)); ps.setString(4,username); ps.setString(5,password);
						ps.executeUpdate();
						ps = Global.sqlConnection.prepareStatement("SELECT friend FROM friends WHERE user=?"); ps.setString(1,username);
						ResultSet rs2 = ps.executeQuery();
						String temp3 = Integer.toString(sess);
						while(rs2.next()){
							ps = Global.sqlConnection.prepareStatement("SELECT time FROM users WHERE user=?;"); ps.setString(1,rs2.getString("friend"));
							ResultSet rs3 = ps.executeQuery();
							if(rs3.next() && Integer.parseInt(rs3.getString("time"))+Global.sessionTime>Global.timestamp)
								temp3+=Global.delimiter2+Global.b64encode(rs2.getString("friend").getBytes(Global.charset));
							}
						temp3 = (new AesSystem(new SecretKeySpec(b64decode(temp2[1]),"AES"))).encrypt(temp3);
						temp3 = Global.serverHelloAccept+Global.delimiter2+temp3+Global.delimiter2+(temp1.length==3?temp1[2]:"")+Global.delimiter1;
						that.send(temp3);
						}
					else { rs.close();
						if(Global.debugLevel>=2) System.out.println("Unverifiable : "+username+"/"+password);
						that.send(Global.serverHelloReject+Global.delimiter2+(temp1.length==3?temp1[2]:"")+Global.delimiter1);
						}
					}
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				catch(SQLException e){ e.printStackTrace(); }
				}
			}
		else if(command.startsWith(Global.serverRegister)){
			if(Global.rootPrivate==null) Global.rootHop.send(command+Global.delimiter3+LinkId+Global.delimiter1);
			else {
				String[] temp1 = command.split(Global.escape+Global.delimiter2);
				String[] temp2 = Global.rootPrivate.decrypt(temp1[1]).split(Global.escape+Global.delimiter2);
				int j = temp2[0].indexOf(Global.delimiter3);
				try {
					String username = new String(Global.b64decode(temp2[0].substring(0,j)), Global.charset);
					String password = new String(Global.b64decode(temp2[0].substring(j+1)), Global.charset);
					if(Global.debugLevel>=2) System.out.println("Registering "+username+"/"+password+" ...");
					PreparedStatement ps = Global.sqlConnection.prepareStatement("SELECT * FROM users WHERE user=?");
					ps.setString(1,username);
					ResultSet rs = ps.executeQuery();
					if(rs.next()){
						if(Global.debugLevel>=2) System.out.println("Username already taken.");
						that.send(Global.serverRegisterReject+Global.delimiter2+(temp1.length==3?temp1[2]:"")+Global.delimiter1);
						}
					else {
						int sess = Global.nextSessionId++;
						ps = Global.sqlConnection.prepareStatement("INSERT INTO users (user,pass,sid,key,time) VALUES (?,?,?,?,?);");
						ps.setString(1,username); ps.setString(2,password); ps.setString(3,Integer.toString(sess)); ps.setString(4,temp2[1]); ps.setString(5,Integer.toString(Global.timestamp));
						ps.executeUpdate();
						// code|E(aes-key){session-id|b64e{friend1}|b64e{friend2}|...}|route
						String temp3 = (new AesSystem(new SecretKeySpec(b64decode(temp2[1]),"AES"))).encrypt(Integer.toString(sess));
						temp3 = Global.serverRegisterAccept+Global.delimiter2+temp3+Global.delimiter2+(temp1.length==3?temp1[2]:"")+Global.delimiter1;
						that.send(temp3);
						}
					}
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				catch(SQLException e){ e.printStackTrace(); }
				}
			}
		else if(command.startsWith(Global.serverFriends)){
			if(Global.rootPrivate==null) Global.rootHop.send(command+Global.delimiter3+LinkId+Global.delimiter1);
			else {
				String[] temp1 = command.split(Global.escape+Global.delimiter2);
				// code|session-id|E(aes-key){[add/delete]:b64e{friend1}|[add/delete]:b64e{friend2}|...}|route
				try {
					PreparedStatement ps = Global.sqlConnection.prepareStatement("SELECT * FROM users WHERE sid=?"); ps.setString(1,temp1[1]);
					ResultSet rs = ps.executeQuery();
					if(rs.next()){
						String user = rs.getString("user");
						ps = Global.sqlConnection.prepareStatement("UPDATE users SET time=? WHERE user=?");
						ps.setString(1,Integer.toString(Global.timestamp)); ps.setString(2,user); ps.executeUpdate();
						AesSystem c = new AesSystem(new SecretKeySpec(b64decode(rs.getString("key")),"AES"));
						String[] temp3 = c.decrypt(temp1[2]).split(Global.escape+Global.delimiter2);
						rs.close();
						for(int i=0;i<temp3.length;++i){
							if(i==0 && temp3[i].equals("")) break;
							String[] temp4 = temp3[i].split(Global.escape+Global.delimiter3);
							String friend = new String (Global.b64decode(temp4[1]),Global.charset);
							ps = Global.sqlConnection.prepareStatement("SELECT * FROM users WHERE user=?"); ps.setString(1,friend);
							rs = ps.executeQuery();
							if(rs.next()){
								if(temp4[0].equals("1")){
									if(Global.debugLevel>=2) System.out.println("Friends : "+user+" & "+friend);
									ps = Global.sqlConnection.prepareStatement("SELECT * FROM friends WHERE user=? AND friend=?");
									ps.setString(1,user); ps.setString(2,friend);
									rs = ps.executeQuery();
									if(!rs.next()){ rs.close();
										ps = Global.sqlConnection.prepareStatement("INSERT INTO friends (user,friend) VALUES (?,?);");
										ps.setString(1,user); ps.setString(2,friend);
										ps.executeUpdate();
                                                                                ps = Global.sqlConnection.prepareStatement("INSERT INTO friends (user,friend) VALUES (?,?);");
										ps.setString(2,user); ps.setString(1,friend);
										ps.executeUpdate();
										}
									else rs.close();
									}
								else {
									if(Global.debugLevel>=2) System.out.println("Enemies : "+user+" & "+friend);
									ps = Global.sqlConnection.prepareStatement("DELETE FROM friends WHERE user=? AND friend=?");
									ps.setString(1,user); ps.setString(2,friend);
									ps.executeUpdate();
                                                                        ps = Global.sqlConnection.prepareStatement("DELETE FROM friends WHERE user=? AND friend=?");
									ps.setString(2,user); ps.setString(1,friend);
									ps.executeUpdate();
									}
								}
							else if(Global.debugLevel>=2) System.out.println("Unknown user : "+friend);
							} // for friends
						ps = Global.sqlConnection.prepareStatement("SELECT friend FROM friends WHERE user=?"); ps.setString(1,user);
						ResultSet rs2 = ps.executeQuery();
						String temp4 = temp1[1];
						while(rs2.next()){
							ps = Global.sqlConnection.prepareStatement("SELECT time FROM users WHERE user=?;"); ps.setString(1,rs2.getString("friend"));
							ResultSet rs3 = ps.executeQuery();
							if(rs3.next() && Integer.parseInt(rs3.getString("time"))+Global.sessionTime>Global.timestamp)
								temp4+=Global.delimiter2+Global.b64encode(rs2.getString("friend").getBytes(Global.charset));
							}
						temp4 = Global.serverFriendsAccept+Global.delimiter2+c.encrypt(temp4)+Global.delimiter2+(temp1.length==4?temp1[3]:"")+Global.delimiter1;
						that.send(temp4);
						}
					else {
						rs.close();
						that.send(Global.serverFriendsReject+Global.delimiter2+(temp1.length==4?temp1[3]:"")+Global.delimiter1);
						}
					}
				catch(SQLException e){ e.printStackTrace(); }
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				}
			}
		else if(command.startsWith(Global.serverHelloAccept) || command.startsWith(Global.serverRegisterAccept) || command.startsWith(Global.serverFriendsAccept)){
			String[] temp1 = command.split(Global.escape+Global.delimiter2);
			String[] temp2;
			if(temp1.length==3) temp2 = temp1[2].split(Global.escape+Global.delimiter3);
			else temp2 = new String[0];
			if(temp2.length>0){
				for(int i=0;i<Global.clients.size();++i)
					if(Global.clients.get(i).handler.linkId==Integer.parseInt(temp2[temp2.length-1]))
						Global.clients.get(i).send(command.substring(0,command.lastIndexOf(Global.delimiter3))+Global.delimiter1);
				}
			else {
				if(command.startsWith(Global.serverHelloAccept) && Global.debugLevel>=2){
                                    if(Global.debugLevel>=1) System.out.println("Logged in as "+Global.username+".");
                                    Login.loginStatus=1;
                                }
				if(command.startsWith(Global.serverRegisterAccept) && Global.debugLevel>=2){
                                    if(Global.debugLevel>=1) System.out.println("Registered as "+Global.username+".");
                                    Login.loginStatus=1;
                                }
				if(command.startsWith(Global.serverFriendsAccept) && Global.debugLevel>=3) Global.arrayPrint(Global.friends.toArray(),"Online Friends");
				try {
					String[] temp3 = Global.rootSession.decrypt(temp1[1]).split(Global.escape+Global.delimiter2);
					Global.rootSessionId = Integer.parseInt(temp3[0]);
					Global.friends.clear();
					try { for(int i=1;i<temp3.length;++i) Global.friends.add(new String(Global.b64decode(temp3[i]),Global.charset)); }
					catch(UnsupportedEncodingException e){ e.printStackTrace(); }
					// Dhruva : Do something with the list of friends.
                                        Task task = new Task<Void>() {
                                            @Override public Void call() {
                                                if(UserList.users!=null) UserList.users.setAll(Global.friends);
                                                return null;
                                            }
                                        };
                                        new Thread(task).start();
					}
				catch(Exception e){ e.printStackTrace(); }
				}
			}
		else if(command.startsWith(Global.serverHelloReject) || command.startsWith(Global.serverRegisterReject) || command.startsWith(Global.serverFriendsReject)){
			String[] temp1 = command.split(Global.escape+Global.delimiter2);
			String[] temp2;
			if(temp1.length==2) temp2 = temp1[1].split(Global.escape+Global.delimiter3);
			else temp2 = new String[0];
			if(temp2.length>0){
				for(int i=0;i<Global.clients.size();++i)
					if(Global.clients.get(i).handler.linkId==Integer.parseInt(temp2[temp2.length-1]))
						Global.clients.get(i).send(command.substring(0,command.lastIndexOf(Global.delimiter3))+Global.delimiter1);
				}
			else {
				if(command.startsWith(Global.serverHelloReject)){
					if(Global.debugLevel>=2) System.out.println("Login Rejected.");
					// Try to register then
					try {
						String temp3 = Global.b64encode(Global.username.getBytes(Global.charset))+Global.delimiter3+Global.b64encode(Global.password.getBytes(Global.charset))+Global.delimiter2;
						temp3 = Global.serverRegister+Global.delimiter2+Global.rootPublic.encrypt(temp3+Global.b64encode(Global.rootSession.key))+Global.delimiter2+Global.delimiter1;
						if(Global.rootHop!=null) Global.rootHop.send(temp3);
						}
					catch(UnsupportedEncodingException e){ e.printStackTrace(); }
					}
				else if(command.startsWith(Global.serverRegisterReject)){
					if(Global.debugLevel>=2) System.out.println("Registeration Rejected.");
					Global.close();
                                        Login.loginStatus=2;
					}
				else if(command.startsWith(Global.serverFriendsReject)){
					if(Global.debugLevel>=2) System.out.println("Friend List Refresh Rejected.");
					}
				}
			}
		else if(command.startsWith(Global.friendLocate)){
			String[] temp1 = command.split(Global.escape+Global.delimiter2);
			String[] temp2 = temp1[1].split(Global.escape+Global.delimiter3);
			try {
				String name1 = new String(Global.b64decode(temp2[0]), Global.charset);
				String name2 = new String(Global.b64decode(temp2[1]), Global.charset);
				boolean encountered = false;
				for(int i=0;!encountered && i<Global.chatLocateRequest.size();++i)
					if(Global.chatLocateRequest.get(i).names.equals(temp1[1]) && Global.chatLocateRequest.get(i).time+Global.sessionTime>Global.localTime)
						encountered = true;
				if(!encountered){
					if(name2.equals(Global.username)){
						if(Global.debugLevel>=2) System.out.println("Request to chat from "+name1+".");
						new ChatSession(name1);
						for(int i=0;i<Global.chats.size();++i)
							if(Global.chats.get(i).name.equals(name1)){
								that.send(Global.chats.get(i).respond(temp1[2],temp1.length==4?temp1[3]:"",LinkId));
								break;
								}
						}
					else for(int i=0;i<Global.clients.size();++i)
						if(Global.clients.get(i)!=that)
							Global.clients.get(i).send(command+Global.delimiter3+LinkId+Global.delimiter1);
					Global.chatLocateRequest.add(new ChatLocateRequest(temp1[1],Global.localTime));
					}
				}
			catch(UnsupportedEncodingException e){ e.printStackTrace(); }
			}
		else if(command.startsWith(Global.friendRespond)){
			// code|b64e{src-nick}:b64e{dest-nick}|dhke-pk2|E(dhcs){E(root-public){b64e{dest-pass}:dest-nonce}}|route
			String[] temp1 = command.split(Global.escape+Global.delimiter2);
			String[] temp2 = (temp1.length==5?temp1[4].split(Global.escape+Global.delimiter3):new String[0]);
			if(temp2.length>0){
				int nextStep = Integer.parseInt(temp2[temp2.length-1]);
				temp2 = temp1[1].split(Global.escape+Global.delimiter3);
				String name1 = temp2[0], name2 = temp2[1];
				for(int i=0;i<Global.chatRoute.size();++i)
					if( (Global.chatRoute.get(i).name1.equals(name1) && Global.chatRoute.get(i).name2.equals(name2) )
					||(Global.chatRoute.get(i).name1.equals(name2) && Global.chatRoute.get(i).name2.equals(name1) ) )
						Global.chatRoute.remove(i);
				Global.chatRoute.add(new ChatRoute(name1,name2,LinkId));
				Global.chatRoute.add(new ChatRoute(name2,name1,nextStep));
				for(int i=0;i<Global.clients.size();++i)
					if(Global.clients.get(i).handler.linkId==nextStep)
						Global.clients.get(i).send(command.substring(0,command.lastIndexOf(Global.delimiter3))+Global.delimiter1);
				}
			else {
				try {
					temp2 = temp1[1].split(Global.escape+Global.delimiter3);
					String name1 = new String(Global.b64decode(temp2[0]), Global.charset);
					String name2 = new String(Global.b64decode(temp2[1]), Global.charset);
					if(name1.equals(Global.username)){
						if(Global.debugLevel>=2) System.out.println("Response to char request from "+name2+".");
						for(int i=0;i<Global.chats.size();++i)
							if(Global.chats.get(i).name.equals(name2))
								{ Global.rootHop.send(Global.chats.get(i).verify(temp1[2],temp1[3],LinkId)); break; }
						}
					}
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				}
			}
		else if(command.startsWith(Global.friendVerify)){
			if(Global.rootPrivate==null) Global.rootHop.send(command+Global.delimiter3+LinkId+Global.delimiter1);
			else {
				// code|session-id|E(aes-key){b64e{dest-name}|E(root-public){b64e{dest-pass}:dest-nonce}}|route
				// code|E(aes-key){dest-name:dest-nonce}|route
				String[] temp1 = command.split(Global.escape+Global.delimiter2);
				try {
					PreparedStatement ps = Global.sqlConnection.prepareStatement("SELECT * FROM users WHERE sid=?"); ps.setString(1,temp1[1]);
					ResultSet rs = ps.executeQuery();
					if(rs.next()){
						AesSystem c = new AesSystem(new SecretKeySpec(b64decode(rs.getString("key")),"AES"));
						String[] temp2 = c.decrypt(temp1[2]).split(Global.escape+Global.delimiter2);
						String[] temp3 = Global.rootPrivate.decrypt(temp2[1]).split(Global.escape+Global.delimiter3);
						String user = rs.getString("user");
						String other = new String(Global.b64decode(temp2[0]), Global.charset);
						String pass = new String(Global.b64decode(temp3[0]), Global.charset);
						rs.close();
						ps = Global.sqlConnection.prepareStatement("SELECT * FROM users WHERE user=? AND pass=?;"); ps.setString(1,other); ps.setString(2,pass);
						rs = ps.executeQuery();
						if(rs.next()){
							if(Global.debugLevel>=2) System.out.println("Validating chat session between "+user+" and "+other+".");
							that.send(Global.friendValid+Global.delimiter2+c.encrypt(temp2[0]+Global.delimiter3+temp3[1])+Global.delimiter2+(temp1.length==4?temp1[3]:"")+Global.delimiter1);
							}
						else {} // send invalid message
						}
					else {} // send invalid message
					}
				catch(SQLException e){ e.printStackTrace(); }
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				}
			}
		else if(command.startsWith(Global.friendValid)){
			String[] temp1 = command.split(Global.escape+Global.delimiter2);
			String[] temp2 = (temp1.length==3?temp1[2].split(Global.escape+Global.delimiter3):new String[0]);
			if(temp2.length>0){
				for(int i=0;i<Global.clients.size();++i)
					if(Global.clients.get(i).handler.linkId==Integer.parseInt(temp2[temp2.length-1]))
						Global.clients.get(i).send(command.substring(0,command.lastIndexOf(Global.delimiter3))+Global.delimiter1);
				}
			else {
				try {
					String[] temp3 = Global.rootSession.decrypt(temp1[1]).split(Global.escape+Global.delimiter3);
					String name = new String(Global.b64decode(temp3[0]), Global.charset);
					for(int i=0;i<Global.chats.size();++i)
						if(Global.chats.get(i).name.equals(name))
							for(int j=0;j<Global.clients.size();++j)
								if(Global.clients.get(j).handler.linkId==Global.chats.get(i).link)
									{ Global.clients.get(j).send(Global.chats.get(i).prove(temp3[1])); break; }
					}
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				}
			}
		else if(command.startsWith(Global.friendProve)){
			String[] temp1 = command.split(Global.escape+Global.delimiter2);
			String[] temp2 = temp1[1].split(Global.escape+Global.delimiter3);
			if(!temp2[1].equals(Global.username_b64e)){
				for(int i=0;i<Global.chatRoute.size();++i)
					if(Global.chatRoute.get(i).name1.equals(temp2[0]) && Global.chatRoute.get(i).name2.equals(temp2[1]))
						for(int j=0;j<Global.clients.size();++j)
							if(Global.clients.get(j).handler.linkId==Global.chatRoute.get(i).link)
								Global.clients.get(j).send(command+Global.delimiter1);
				}
			else {
				try {
					String name = new String(Global.b64decode(temp2[0]), Global.charset);
					for(int i=0;i<Global.chats.size();++i)
						if(Global.chats.get(i).name.equals(name))
							for(int j=0;j<Global.clients.size();++j)
								if(Global.clients.get(j).handler.linkId==Global.chats.get(i).link)
									{ Global.clients.get(j).send(Global.chats.get(i).confirm(temp1[2])); break; }
					}
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				}
			}
		else if(command.startsWith(Global.friendConfirm)){
			String[] temp1 = command.split(Global.escape+Global.delimiter2);
			String[] temp2 = temp1[1].split(Global.escape+Global.delimiter3);
			if(!temp2[1].equals(Global.username_b64e)){
				for(int i=0;i<Global.chatRoute.size();++i)
					if(Global.chatRoute.get(i).name1.equals(temp2[0]) && Global.chatRoute.get(i).name2.equals(temp2[1]))
						for(int j=0;j<Global.clients.size();++j)
							if(Global.clients.get(j).handler.linkId==Global.chatRoute.get(i).link)
								Global.clients.get(j).send(command+Global.delimiter1);
				}
			else {
				try {
					String name = new String(Global.b64decode(temp2[0]), Global.charset);
					for(int i=0;i<Global.chats.size();++i)
						if(Global.chats.get(i).name.equals(name))
							{ Global.chats.get(i).start(temp1[2]); break; }
					}
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				}
			}
		else if(command.startsWith(Global.friendMessage)){
			String[] temp1 = command.split(Global.escape+Global.delimiter2);
			String[] temp2 = temp1[1].split(Global.escape+Global.delimiter3);
			if(!temp2[1].equals(Global.username_b64e)){
				if(Global.debugLevel>=2) try {
					String name1 = new String(Global.b64decode(temp2[0]), Global.charset);
					String name2 = new String(Global.b64decode(temp2[1]), Global.charset);
					System.out.println("Forwarding Message from "+name1+" to "+name2+".");
					}
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				for(int i=0;i<Global.chatRoute.size();++i)
					if(Global.chatRoute.get(i).name1.equals(temp2[0]) && Global.chatRoute.get(i).name2.equals(temp2[1])){
						boolean sent = false;
						for(int j=0;j<Global.clients.size();++j)
							if(Global.clients.get(j).handler.linkId==Global.chatRoute.get(i).link)
								{ Global.clients.get(j).send(command+Global.delimiter1); sent = true; }
						if(!sent) that.send(Global.friendLinkError+Global.delimiter2+temp2[1]+Global.delimiter3+temp2[0]+Global.delimiter1);
						}
				}
			else {
				try {
					String name = new String(Global.b64decode(temp2[0]), Global.charset);
					for(int i=0;i<Global.chats.size();++i)
						if(Global.chats.get(i).name.equals(name))
							{ Global.chats.get(i).recv(temp1[2]); break; }
					}
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				}
			}
		else if(command.startsWith(Global.friendLinkError)){
			String[] temp1 = command.split(Global.escape+Global.delimiter2);
			String[] temp2 = temp1[1].split(Global.escape+Global.delimiter3);
			if(!temp2[1].equals(Global.username_b64e)){
				for(int i=0;i<Global.chatRoute.size();++i)
					if(Global.chatRoute.get(i).name1.equals(temp2[0]) && Global.chatRoute.get(i).name2.equals(temp2[1]))
						for(int j=0;j<Global.clients.size();++j)
							if(Global.clients.get(j).handler.linkId==Global.chatRoute.get(i).link)
								Global.clients.get(j).send(command+Global.delimiter1);
				}
			else {
				try {
					String name = new String(Global.b64decode(temp2[0]), Global.charset);
					for(int i=0;i<Global.chats.size();++i)
						if(Global.chats.get(i).name.equals(name))
							{ Global.chats.get(i).active = false; Global.chats.get(i).locate(); break; }
					}
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				}
			}
		else System.out.println("Don't know what to do with command = "+command);
		}
		
	public static void friend(String name,boolean add){
		if(Global.rootSessionId!=-1 && Global.rootHop!=null){
			try {
				String temp = Global.rootSession.encrypt((add?"1":"0")+Global.delimiter3+Global.b64encode(name.getBytes(Global.charset)));
				temp = Global.serverFriends+Global.delimiter2+Global.rootSessionId+Global.delimiter2+temp+Global.delimiter2+Global.delimiter1;
				Global.rootHop.send(temp);
				}
			catch(UnsupportedEncodingException e){ e.printStackTrace(); }
			}
		}
		
	public static void chat(String name,String data){
		boolean sent = false;
		for(int i=0;i<Global.chats.size();++i)
			if(Global.chats.get(i).name.equals(name))
				{ Global.chats.get(i).send(data); sent=true; break; }
		if(!sent && Global.debugLevel>=2){
			new ChatSession(name);
			for(int i=0;i<Global.chats.size();++i)
				if(Global.chats.get(i).name.equals(name))
					{ Global.chats.get(i).send(data); Global.chats.get(i).locate(); }
			}
		}
		
	public static void console(){
		DataInputStream console = new DataInputStream(System.in); String line; while(true){
			try {
				line = console.readLine();
				if(line.equals("x")) break;
				else if(line.equals("|")){
					Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
					Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
					Global.arrayPrint(threadArray,"Threads : ");
					}
				else if(line.equals("?")) System.out.println("Username : "+Global.username);
				else if(line.equals("&")) Global.arrayPrint(Global.friends.toArray(),"Online Friends");
				else if(line.equals("~")) Global.debugLevel = 0;
				else if(line.startsWith("+")) Global.friend(line.substring(1),true);
				else if(line.startsWith("-")) Global.friend(line.substring(1),false);
				else if(line.startsWith("@")){
					new ChatSession(line.substring(1));
					for(int i=0;i<Global.chats.size();++i)
						if(Global.chats.get(i).name.equals(line.substring(1)))
							Global.chats.get(i).locate();
					}
				else if(line.startsWith(">")) Global.chat(line.substring(1,line.indexOf(" ")),line.substring(line.indexOf(" ")));
				} // try
			catch (Exception e){ e.printStackTrace(); break; }
			}
		}
		
	public static void close(){
		System.out.print("Global shutdown ... ");
		if(Global.rootServer!=null) Global.rootServer.active=false;
		if(Global.stepAction!=null) Global.stepAction.active=false;
		for(int i=0;i<Global.clients.size();++i) Global.clients.get(i).close();
		if(Global.tcpServer!=null) Global.tcpServer.close();
		if(Global.udpServer!=null) Global.udpServer.close();
                Login.cn = null;
		try { Global.sqlConnection.close(); }
		catch(SQLException e){ e.printStackTrace(); }
		System.out.println("Complete.");
		}
	}
	
class ChatSession {
	public String name;
	public String path;
	
	public DhkeSystem dhkeSystem;
	public AesSystem aesSystem;
	public int nonce = -1;
	public int link;
	public boolean active = false;
	int sequence = 1;
	public ArrayList<String> messages = new ArrayList<String>();
	        
	ChatSession(String n){
		if(n.equals(Global.username)) return;
		boolean exists = false;
		for(int i=0;i<Global.chats.size();++i)
			if(Global.chats.get(i).name.equals(n)) exists = true;
		if(!exists){
			this.name = n;
			this.dhkeSystem = new DhkeSystem();
			Global.chats.add(this);
			}
		}
	public void locate(){
		if(this.active) return;
		if(Global.debugLevel>=2) System.out.println("Locating and establishing secure connection to "+this.name+" ...");
		for(int i=0;i<Global.clients.size();++i){
			try {
				String temp = Global.username_b64e+Global.delimiter3+Global.b64encode(this.name.getBytes(Global.charset));
				Global.chatLocateRequest.add(new ChatLocateRequest(temp,Global.localTime));
				temp = Global.friendLocate+Global.delimiter2+temp+Global.delimiter2+this.dhkeSystem.step1()+Global.delimiter2+Global.delimiter1;
				Global.clients.get(i).send(temp);
				}
			catch(UnsupportedEncodingException e){ e.printStackTrace(); }
			}
		}
	public String respond(String pk,String route,int l){
		try {
			this.link = l;
			this.nonce = Global.localTime;
			String key = this.dhkeSystem.step2(pk);
			this.aesSystem = new AesSystem(this.dhkeSystem.secretKey);
			String temp = Global.friendRespond+Global.delimiter2+Global.b64encode(this.name.getBytes(Global.charset))+Global.delimiter3+Global.b64encode(Global.username.getBytes(Global.charset))+Global.delimiter2;
			temp+=key+Global.delimiter2+this.aesSystem.encrypt(Global.rootPublic.encrypt(Global.b64encode(Global.password.getBytes(Global.charset))+Global.delimiter3+Integer.toString(this.nonce)))+Global.delimiter2+route+Global.delimiter1;
			return temp;
			}
		catch(UnsupportedEncodingException e){ e.printStackTrace(); }
		return "";
		}
	public String verify(String pk,String cert,int l){
		try {
			this.link = l;
			this.dhkeSystem.step3(pk);
			this.aesSystem = new AesSystem(this.dhkeSystem.secretKey);
			String temp = Global.rootSession.encrypt(Global.b64encode(this.name.getBytes(Global.charset))+Global.delimiter2+this.aesSystem.decrypt(cert));
			temp = Global.friendVerify+Global.delimiter2+Global.rootSessionId+Global.delimiter2+temp+Global.delimiter2+Global.delimiter1;
			return temp;
			}
		catch(UnsupportedEncodingException e){ e.printStackTrace(); }
		return "";
		}
	public String prove(String n){
		try {
			this.path = Global.b64encode(Global.username.getBytes(Global.charset))+Global.delimiter3+Global.b64encode(this.name.getBytes(Global.charset));
			if(Global.debugLevel>=2) System.out.println("Sending nonces to initialize chat session with "+this.name+".");
			this.nonce = Global.localTime;
			return Global.friendProve+Global.delimiter2+this.path+Global.delimiter2+aesSystem.encrypt(Integer.toString(this.nonce)+Global.delimiter3+n)+Global.delimiter1;
			}
		catch(UnsupportedEncodingException e){ e.printStackTrace(); }
		return "";
		}
	public String confirm(String n){
		try {
			String[] temp = this.aesSystem.decrypt(n).split(Global.escape+Global.delimiter3);
			if(Integer.parseInt(temp[1])==this.nonce){
				this.path = Global.b64encode(Global.username.getBytes(Global.charset))+Global.delimiter3+Global.b64encode(this.name.getBytes(Global.charset));
				this.active = true;
				if(Global.debugLevel>=2) System.out.println("Conversation between "+Global.username+" & "+this.name+" now possible.");
				return Global.friendConfirm+Global.delimiter2+this.path+Global.delimiter2+aesSystem.encrypt(temp[0])+Global.delimiter1;
				}
			}
		catch(UnsupportedEncodingException e){ e.printStackTrace(); }
		return "";
		}
	public void start(String n){
		if(Integer.parseInt(this.aesSystem.decrypt(n))==this.nonce){
			this.active = true;
			if(Global.debugLevel>=2) System.out.println("Conversation between "+Global.username+" & "+this.name+" now possible.");
			for(int i=0;i<this.messages.size();++i) this.send_actual(this.messages.get(i));
			}
		}
	public void send(String data){
                if(data.equals("")) data = "0";
                else {
                    data = "2"+Global.delimiter3+this.sequence+Global.delimiter2+data;
                    ++this.sequence;
                }
		this.messages.add(data);
		this.send_actual(data);
		}
	public void send_actual(String data){
		if(!this.active) return;
		String temp = Global.friendMessage+Global.delimiter2+this.path+Global.delimiter2+aesSystem.encrypt(data)+Global.delimiter1;
		boolean sent = false;
		for(int i=0;i<Global.clients.size();++i)
			if(Global.clients.get(i).handler.linkId==this.link)
				{ Global.clients.get(i).send(temp); sent=true; break; }
		if(!sent){ this.active=false; this.locate(); }
		}
	public synchronized void recv(String data){
		data = this.aesSystem.decrypt(data);
                if(data.equals("0")){
                    NewMessage task = new NewMessage(this.name,data);
                    Platform.runLater(task);
                    return;
                }
		int sequence = Integer.parseInt( data.substring(data.indexOf(Global.delimiter3)+1,data.indexOf(Global.delimiter2)) );
		if(data.startsWith("1")){
			for(int i=0;i<this.messages.size();++i)
				if(this.messages.get(i).startsWith("2"+Global.delimiter3+sequence+Global.delimiter2)){
					if(Global.debugLevel>=2) System.out.println("Message acknowledged ["+sequence+"] : "+this.messages.get(i).substring(this.messages.get(i).indexOf(Global.delimiter2)+1));
					this.messages.remove(i);
					}
			}
		if(data.startsWith("2")){
			this.send_actual("1"+Global.delimiter3+sequence+Global.delimiter2);
                        data = data.substring(data.indexOf(Global.delimiter2)+1);
			if(Global.debugLevel>=1) System.out.println("Message from "+this.name+" ["+sequence+"] : "+data);
			// Dhruva : What to do from here
                        NewMessage task = new NewMessage(this.name,data);
                        Platform.runLater(task);
			}
		}
	}
	
//class NewMessage extends Task<Void> {
class NewMessage implements Runnable {
         private String user, msg;

         public NewMessage(String u,String m) {
             user = u;
             msg = m;
         }

         //@Override protected Void run() throws Exception {
         public void run(){
             boolean open = false;
             for(int i=0; i<UserList.peers.size(); i++){
             if(UserList.peers.get(i).peerName.equals(this.user)){
                 if(!this.msg.equals("0")) new AddMessage(Global.username, this.msg, this.user,false);
                 UserList.peers.get(i).stage.show();
                 UserList.peers.get(i).stage.toFront();
                 open = true;
                 
                 }
             }
             if(!open){
                 UserList.peers.add(new Chat(this.user, Global.username));
                 if(!this.msg.equals("0")) new AddMessage(Global.username, this.msg, this.user,false);
                 
             }
             //return null;
         }
     }

class ChatLocateRequest { public String names; public int time; ChatLocateRequest(String n,int t){ this.names=n; this.time=t; } }
	
class ChatRoute { public String name1, name2; int link; ChatRoute(String a,String b,int c){ this.name1=a; this.name2=b; this.link=c; } }
	
class DesSystem { // http://www.exampledepot.com/egs/javax.crypto/DesString.html
    Cipher ecipher;
    Cipher dcipher;

	DesSystem(){
		try {
			KeyGenerator kg = KeyGenerator.getInstance("DES");
			initialize(kg.generateKey());
			}
		catch(NoSuchAlgorithmException e){ e.printStackTrace(); }
		}
	DesSystem(SecretKey key){ initialize(key); }
	
    private void initialize(SecretKey key) {
        try {
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);

        } catch (javax.crypto.NoSuchPaddingException e) {
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch (java.security.InvalidKeyException e) {
        }
    }

    public String encrypt(String str) {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            //return new sun.misc.BASE64Encoder().encode(enc);
			return Global.b64encode(enc);
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }

    public String decrypt(String str) {
        try {
            // Decode base64 to get bytes
            //byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
			byte[] dec = Global.b64decode(str);

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        }
        return null;
    }
}

class AesSystem { // http://java.sun.com/developer/technicalArticles/Security/AES/AES_v1.html
	public byte[] key;
	private Cipher ecipher, dcipher;
	AesSystem(){
		try {
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128);
			initialize(kg.generateKey());
			}
		catch(NoSuchAlgorithmException e){ e.printStackTrace(); }
		}
	AesSystem(SecretKey x){ initialize(x); }
	private void initialize(SecretKey x){
		try {
			key = x.getEncoded();
			if(key.length>16) key = java.util.Arrays.copyOfRange(key,0,16);
			SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
			this.ecipher = Cipher.getInstance("AES");
			this.dcipher = Cipher.getInstance("AES");
			ecipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			dcipher.init(Cipher.DECRYPT_MODE, skeySpec);
			}
		catch(NoSuchAlgorithmException e){ e.printStackTrace(); }
		catch(InvalidKeyException e){ e.printStackTrace(); }
		catch(NoSuchPaddingException e){ e.printStackTrace(); }
		}
	public byte[] encrypt(byte[] data){
		try { return ecipher.doFinal(data); }
		catch(IllegalBlockSizeException e){ e.printStackTrace(); return null; }
		catch(BadPaddingException e){ e.printStackTrace(); return null; }
		}
	public String encrypt(String data){
		return Global.b64encode(this.encrypt(data.getBytes()));
		}
	public byte[] decrypt(byte[] data){
		try { return dcipher.doFinal(data); }
		catch(IllegalBlockSizeException e){ e.printStackTrace(); return null; }
		catch(BadPaddingException e){ e.printStackTrace(); return null; }
		}
	public String decrypt(String data){
		return new String(this.decrypt(Global.b64decode(data)));
		}
	}

class RsaSystem { // Valid only for plaintext byte[] of upto key_length_bytes - 11  ; the padding corresponds to data length and checksum
	public PublicKey publicKey;
	public PrivateKey privateKey;
	public BigInteger publicExponent;
	public BigInteger privateExponent;
	public BigInteger modulus;
	public int length;
	private Cipher ecipher;
	private Cipher dcipher;
	private static final String algorithm = "RSA";
	public static final boolean ENCRYPT_MODE = true;
	public static final boolean DECRYPT_MODE = false;
	private void init_public(PublicKey k){
		try {
			this.publicKey = k;
			this.ecipher = Cipher.getInstance(RsaSystem.algorithm); // NoSuchAlgorithmException NoSuchPaddingException
			this.ecipher.init(Cipher.ENCRYPT_MODE, publicKey); // InvalidKeyException
			KeyFactory fact = KeyFactory.getInstance(RsaSystem.algorithm);
			RSAPublicKeySpec pub = fact.getKeySpec(this.publicKey, RSAPublicKeySpec.class);
			this.publicExponent = pub.getPublicExponent(); this.modulus = pub.getModulus();
			this.length = this.modulus.toString(2).length();
			}
		catch(NoSuchAlgorithmException e){ e.printStackTrace(); }
		catch(InvalidKeyException e){ e.printStackTrace(); }
		catch(NoSuchPaddingException e){ e.printStackTrace(); }
		catch(InvalidKeySpecException e){ e.printStackTrace(); }
		}
	private void init_private(PrivateKey k){
		try {
			this.privateKey = k;
			this.dcipher = Cipher.getInstance(RsaSystem.algorithm); // NoSuchAlgorithmException NoSuchPaddingException
			this.dcipher.init(Cipher.DECRYPT_MODE, privateKey); // InvalidKeyException
			KeyFactory fact = KeyFactory.getInstance(RsaSystem.algorithm);
			RSAPrivateKeySpec priv = fact.getKeySpec(this.privateKey,RSAPrivateKeySpec.class);
			this.privateExponent = priv.getPrivateExponent(); this.modulus = priv.getModulus();
			this.length = this.modulus.toString(2).length();
			}
		catch(NoSuchAlgorithmException e){ e.printStackTrace(); }
		catch(InvalidKeyException e){ e.printStackTrace(); }
		catch(NoSuchPaddingException e){ e.printStackTrace(); }
		catch(InvalidKeySpecException e){ e.printStackTrace(); }
		}
	public RsaSystem(int x){
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(RsaSystem.algorithm);
			kpg.initialize(x); // NoSuchAlgorithmException
			KeyPair kp = kpg.genKeyPair();
			this.init_public(kp.getPublic());
			this.init_private(kp.getPrivate());
			}
		catch(NoSuchAlgorithmException e){ e.printStackTrace(); }
		}
	public RsaSystem(KeyPair kp){
		this.init_public(kp.getPublic());
		this.init_private(kp.getPrivate());
		}
	public RsaSystem(boolean mode,Key key){
		if(mode==RsaSystem.ENCRYPT_MODE) this.init_public((PublicKey)key);
		else this.init_private((PrivateKey)key);
		}
	public RsaSystem(boolean mode,BigInteger exp,BigInteger mod){
		try {
			KeyFactory fact = KeyFactory.getInstance("RSA");
			if(mode==RsaSystem.ENCRYPT_MODE) this.init_public(fact.generatePublic(new RSAPublicKeySpec(mod, exp)));
			else this.init_private(fact.generatePrivate(new RSAPrivateKeySpec(mod, exp)));
			}
		catch(NoSuchAlgorithmException e){ e.printStackTrace(); }
		catch(InvalidKeySpecException e){ e.printStackTrace(); }
		}
	public byte[] encrypt(byte[] src){
		try {
			int i, j=(this.length/8)-11,k;
			byte[] result = new byte[(int)Math.ceil((float)src.length/j)*this.length/8];
			for(i=0,k=0;i<src.length;i+=j,k+=this.length/8){
				this.ecipher.doFinal(src,i,Math.min(j,src.length-i),result,k); // IllegalBlockSizeException BadPaddingException
				}
			return result;
			}
		catch(IllegalBlockSizeException e){ e.printStackTrace(); }
		catch(BadPaddingException e){ e.printStackTrace(); }
		catch(ShortBufferException e){ e.printStackTrace(); }
		return null;
		}
	public byte[] decrypt(byte[] src){
		try {
			int i, j=(this.length/8)-11,k;
			byte[] result = new byte[src.length];
			for(i=0,k=0;i<src.length;i+=this.length/8,k+=j)
				this.dcipher.doFinal(src,i,this.length/8,result,k); // IllegalBlockSizeException BadPaddingException
			return result;
			}
		catch(IllegalBlockSizeException e){ e.printStackTrace(); }
		catch(BadPaddingException e){ e.printStackTrace(); }
		catch(ShortBufferException e){ e.printStackTrace(); }
		return null;
		}
	public String encrypt(String src){
		try { src=src.length()+"~"+src; return Global.b64encode(this.encrypt(src.getBytes(Global.charset))); }
		catch(UnsupportedEncodingException e){ e.printStackTrace(); }
		return null;
		}
	public String decrypt(String src){
		try {
			String temp = new String(this.decrypt(Global.b64decode(src)),Global.charset);
			int x = temp.indexOf("~"), y = Integer.parseInt(temp.substring(0,x));
			return temp.substring(x+1,x+y+1);
			}
		catch(UnsupportedEncodingException e){ e.printStackTrace(); }
		return null;
		}
	}
	
class DhkeSystem { // http://www.exampledepot.com/egs/javax.crypto/GenDhParams.html ; http://www.exampledepot.com/egs/javax.crypto/KeyAgree.html
	private DHParameterSpec dhSpec;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	public SecretKey secretKey;
	private boolean generateParameters(){
		try {
			// Create the parameter generator for a 1024-bit DH key pair
			AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH"); // NoSuchAlgorithmException
			paramGen.init(1024);
			// Generate the parameters
			AlgorithmParameters params = paramGen.generateParameters();
			this.dhSpec = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class); // InvalidParameterSpecException
			return true;
			}
		catch(NoSuchAlgorithmException e){ return false; }
		catch(InvalidParameterSpecException e){ return false; }
		}
	private boolean generateKeys(){
		try {
			// Use the values to generate a key pair
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH"); // NoSuchAlgorithmException
			keyGen.initialize(this.dhSpec); // InvalidAlgorithmParameterException
			KeyPair keypair = keyGen.generateKeyPair();
			// Get the generated public and private keys
			this.privateKey = keypair.getPrivate();
			this.publicKey = keypair.getPublic();
			return true;
			}
		catch(NoSuchAlgorithmException e){ return false; }
		catch(InvalidAlgorithmParameterException e){ return false; }
		}
	private boolean generateSecret(byte[] k){
		try {
			// Convert the public key bytes into a PublicKey object
			X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(k);
			KeyFactory keyFact = KeyFactory.getInstance("DH"); // NoSuchAlgorithmException
			this.publicKey = keyFact.generatePublic(x509KeySpec); // InvalidKeySpecException
			// Prepare to generate the secret key with the private key and public key of the other party
			KeyAgreement ka = KeyAgreement.getInstance("DH");
			ka.init(this.privateKey); // InvalidKeyException
			ka.doPhase(this.publicKey, true); // InvalidKeyException
			// Generate the secret key
			this.secretKey = ka.generateSecret("AES"); // InvalidKeyException
			return true;
			}
		catch(NoSuchAlgorithmException e){ return false; }
		catch(InvalidKeyException e){ return false; }
		catch(InvalidKeySpecException e){ return false; }
		}
	public String step1(){
		if(!generateParameters()) return null;
		if(!generateKeys()) return null;
		// Send the parameters and public key to the other party...
		return ""+this.dhSpec.getP()+Global.delimiter3+this.dhSpec.getG()+Global.delimiter3+this.dhSpec.getL()+Global.delimiter3+Global.b64encode(this.publicKey.getEncoded());
		}
	public String step2(String data){
		// Obtain the parameters from the other side.
		String[] values = data.split(""+Global.delimiter3);
		BigInteger p = new BigInteger(values[0]);
		BigInteger g = new BigInteger(values[1]);
		int l = Integer.parseInt(values[2]);
		byte[] k = Global.b64decode(values[3]);
		this.dhSpec = new DHParameterSpec(p, g, l);
		if(!generateKeys()) return null;
		String temp = Global.b64encode(this.publicKey.getEncoded());
		if(!generateSecret(k)) return null;
		return temp;
		}
	public boolean step3(String data){
		if(data==null) return false;
		return generateSecret(Global.b64decode(data));
		}
	}
	
class Handler {
	public int linkId;
	
	public void start(TcpClient that){}
	public void stop(TcpClient that){}
	public String process(TcpClient that, String data){ return ""; }
	public void sent(TcpClient that,String data){}
	public void server_start(TcpServer that){}
	public void server_stop(TcpServer that){}
	
	public void process(UdpServer that,String source,String data){ }
	public void server_start(UdpServer that){}
	public void server_stop(UdpServer that){}
	}
	
class TcpClient implements Runnable {
	private Thread thread = null;
	private DataInputStream input = null;
	private DataOutputStream output = null;
	private String buffer = "";
	private final Semaphore lock_connect = new Semaphore(1); // to ensure that only one of the two connect() functions are under execution at any given time

	public Socket socket = null;
	public volatile boolean active = false;
	public String name = "AnonymousClient";
	public Handler handler = null;
	
	private synchronized void activate(){
		try {
			this.input = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));
			this.output = new DataOutputStream(this.socket.getOutputStream());
			}
		catch(IOException e){ e.printStackTrace(); return; }
		try { this.socket.setTcpNoDelay(true); }
		catch(SocketException e){ e.printStackTrace(); }
		this.thread = new Thread(this,this.name);
		this.active = true;
		this.thread.start();
		}
	public synchronized boolean connect(String host,int port){
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			String ip1 = localHost.getHostAddress(); if(ip1.indexOf("/")!=-1) ip1 = ip1.substring(ip1.indexOf("/")+1);
			if(host.equals(ip1) && port==Global.tcpServer.socket.getLocalPort()){ return false; }
			}
		catch (UnknownHostException e){ e.printStackTrace(); }
		try { this.lock_connect.acquire(1); } catch (InterruptedException e){ return false; }
		if(this.active){ this.lock_connect.release(1); return false; }
		try { this.socket = new Socket(host, port); }
		catch (Exception e){ if(!e.getMessage().equals("Connection refused: connect")) e.printStackTrace(); this.lock_connect.release(1); return false; }
		this.activate();
		this.lock_connect.release(1);
		return this.active;
		}
	public synchronized boolean connect(Socket _socket){
		try { this.lock_connect.acquire(1); } catch (InterruptedException e){ return false; }
		if(this.active || !_socket.isConnected()){ this.lock_connect.release(1); return false; }
		this.socket = _socket;
		this.activate();
		this.lock_connect.release(1);
		return this.active;
		}
	public synchronized boolean close(){
		if(this.active==false) return true;
		this.active = false;
		try { this.socket.close(); }
		catch(IOException e){ e.printStackTrace(); }
		return true;
		}
	public synchronized boolean send(String data){
		if(!this.active) return false;
		try { this.output.writeUTF(data); if(this.handler!=null) this.handler.sent(this,data); return true; }
		catch(IOException e){ if(!e.getMessage().equals("Connection reset by peer: socket write error")) e.printStackTrace(); this.close(); return false; }
		}
	public void run(){
		if(this.handler!=null) this.handler.start(this);
		while(this.active){
			try {
				this.buffer = this.buffer + this.input.readUTF();
				if(this.handler!=null) this.buffer = this.handler.process(this,this.buffer);
				}
			catch(IOException e){
				if(this.handler!=null) this.handler.stop(this);
				this.active=true; break;
				}
			}
		if(this.active) this.active=false;
		else if(this.handler!=null) this.handler.stop(this);
		try { this.socket.close(); }
		catch(IOException e){ e.printStackTrace(); }
		}
	}
	
class TcpServer implements Runnable {
	private Thread thread = null;
	
	public ServerSocket socket = null;
	public volatile boolean active = false;
	public String name = "AnonymousServer";
	public Handler handler = null;
	public ArrayList<TcpClient> clients;
	
	public synchronized boolean listen(int port){
		if(this.active==true) return false;
		try { this.socket = new ServerSocket(port,0,InetAddress.getLocalHost()); }
		catch(IOException e){ if(!e.getMessage().equals("Address already in use: Cannot bind") && !e.getMessage().equals("Address already in use: JVM_Bind")) e.printStackTrace(); return false; }
		this.clients = new ArrayList<TcpClient>();
		this.thread = new Thread(this,this.name);
		this.active = true;
		this.thread.start();
		return true;
		}
	public void run(){
		if(this.handler!=null) this.handler.server_start(this);
		while(this.active){
			TcpClient client = new TcpClient();
			try { if(this.handler!=null) client.handler = this.handler.getClass().newInstance(); }
			catch(Exception e){ e.printStackTrace(); break; }
			try { client.connect(this.socket.accept()); }
			catch(IOException e){ if(!e.getMessage().equals("socket closed")) e.printStackTrace(); break; }
			this.clients.add(client);
			}
		}
	public synchronized boolean close(){
		if(this.active==false) return true;
		this.active = false;
		if(this.handler!=null) this.handler.server_stop(this);
		while(!this.clients.isEmpty()){
			this.clients.get(0).close();
			this.clients.remove(0);
			}
		try { this.socket.close(); }
		catch(IOException e){ e.printStackTrace(); }
		return true;
		}
	public synchronized boolean broadcast(String data){
		try {
			Iterator i=this.clients.iterator();
			while(this.active && i.hasNext()) ((TcpClient)i.next()).send(data);
			}
		catch (Exception e){ e.printStackTrace(); return false; }
		return true;
		}
	}

class UdpServer implements Runnable {
	private Thread thread = null;
	
	public DatagramSocket socket = null;
	public volatile boolean active = false;
	public Handler handler = null;
	public String name = "Anonymous";
	
	public synchronized boolean listen(int port){
		try { this.socket = new DatagramSocket(port); }
		catch(IOException e){ if(!e.getMessage().equals("Address already in use: Cannot bind") && !e.getMessage().equals("Address already in use: JVM_Bind")) e.printStackTrace(); return false; }
		this.thread = new Thread(this,this.name);
		this.active = true;
		this.thread.start();
		return true;
		}
	public synchronized boolean close(){
		if(this.active==false) return true;
		try { this.active = false; if(this.thread!=null) this.thread.join(); return true; }
		catch(InterruptedException e){ e.printStackTrace(); return false; }
		}
	public void run(){
		if(this.handler!=null) this.handler.server_start(this);
		byte[] data = new byte[1024];
		while(this.active){
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try { this.socket.receive(packet); }
			catch(IOException e){ e.printStackTrace(); break; }
			if(this.handler!=null) this.handler.process(this,packet.getAddress().getHostAddress()+":"+packet.getPort(),new String(packet.getData(),0,packet.getLength()));
			}
		if(this.handler!=null) this.handler.server_stop(this);
		}
	}
	
class UdpClient {
	public static boolean send(String host,int port,String data){
		try {
			DatagramSocket client = new DatagramSocket();
			byte[] _data = new byte[1024]; _data = data.getBytes();
			client.send(new DatagramPacket(_data, _data.length, InetAddress.getByName(host), port));
			if(Global.debugLevel>=3) System.out.println("UDP Client to "+host+":"+port+" = "+data);
			}
		catch(SocketException e){ e.printStackTrace(); return false; }
		catch(IOException e){ e.printStackTrace(); return false; }
		return true;
		}
	}
	
class xxx extends Handler {
	public void start(TcpClient that){ System.out.println("Thread started."); }
	public void stop(TcpClient that){ System.out.println("Thread ended."); }
	public String process(TcpClient that, String data){
		System.out.println("\n"+data);
		return "";
		}
	public void server_start(TcpServer that){ System.out.println("Server started."); }
	public void server_stop(TcpServer that){ System.out.println("Server ended."); }
	
	public void process(UdpServer that,String source,String data){
		System.out.println("UDPS Recv : "+data+" from "+source);
		}
	public void server_start(UdpServer that){ System.out.println("Server started."); }
	public void server_stop(UdpServer that){ System.out.println("Server ended."); }
	}

class LinkServer extends Handler {
	public int state=0;
	public InetSocketAddress remote_socket;
	public void server_start(TcpServer that){
		if(Global.debugLevel>=2) System.out.println("Server listening at "+that.socket.getInetAddress()+":"+Integer.toString(that.socket.getLocalPort()));
		}
	public void server_stop(TcpServer that){
		if(Global.debugLevel>=2) System.out.println("Server terminated at "+that.socket.getInetAddress()+":"+Integer.toString(that.socket.getLocalPort()));
		}
	public void start(TcpClient that){
		this.linkId = ++Global.nextLinkId;
		this.remote_socket = new InetSocketAddress(that.socket.getInetAddress(), that.socket.getPort());
		// if(Global.debugLevel>=2) System.out.println("Connection established from "+this.remote_socket.getAddress().getHostAddress()+":"+this.remote_socket.getPort());
		}
	public void stop(TcpClient that){
		if(Global.debugLevel>=2) System.out.println("Connection terminated from "+this.remote_socket.getAddress().getHostAddress()+":"+this.remote_socket.getPort());
		Global.links.remove(this.remote_socket); Global.clients.remove(that);
		}
	public String process(TcpClient that, String data){
		String command; int offset;
		while((offset=data.indexOf(Global.delimiter1))!=-1){
			command = data.substring(0,offset); data = data.substring(offset+1);
			if(Global.debugLevel>=3) System.out.println("Data from "+this.remote_socket.getAddress().getHostAddress()+":"+this.remote_socket.getPort()+" = "+command);
			if(this.state==0 && command.startsWith(Global.peerHello)){
				String[] temp1 = command.split(Global.escape+Global.delimiter2);
				String[] temp2 = temp1[1].split(Global.escape+Global.delimiter3);
				InetSocketAddress temp3 = new InetSocketAddress(temp2[0],Integer.parseInt(temp2[1]));
				this.remote_socket = temp3;
				if(Global.debugLevel>=2) System.out.println("Connection established from "+this.remote_socket.getAddress().getHostAddress()+":"+this.remote_socket.getPort());
				if(Global.clients.size()<Global.maxLinks){
					Global.links.add(this.remote_socket); Global.clients.add(that);
					that.send(Global.peerAccept+Global.delimiter1); this.state=1;
					}
				else { that.send(Global.peerReject+Global.delimiter1); this.state=0; }
				if(Global.peers.indexOf(temp3)==-1) Global.peers.add(temp3);
				}
			else if(this.state==1 && command.startsWith(Global.peerGet)){
				String temp = Global.peerList;
				for(int i=0;i<Global.peers.size();++i)
					if(!(Global.peers.get(i).getAddress().equals(Global.tcpServer.socket.getInetAddress()) && Global.peers.get(i).getPort()==Global.tcpServer.socket.getLocalPort())
					&& !(Global.peers.get(i).getAddress().equals(this.remote_socket.getAddress()) && Global.peers.get(i).getPort()==this.remote_socket.getPort() ) )
						temp+=Global.delimiter2+Global.peers.get(i).getAddress().getHostAddress()+Global.delimiter3+Integer.toString(Global.peers.get(i).getPort());
				temp+=Global.delimiter1;
				that.send(temp);
				that.send(Global.peerGet+Global.delimiter1);
				}
			else if(this.state==1 && command.startsWith(Global.peerList)){
				String[] temp1 = command.split(Global.escape+Global.delimiter2);
				String[] temp2; InetSocketAddress temp3;
				for(int i=1; i<temp1.length; ++i){
					temp2 = temp1[i].split(Global.escape+Global.delimiter3);
					temp3 = new InetSocketAddress(temp2[0],Integer.parseInt(temp2[1]));
					if(!this.remote_socket.getAddress().equals(Global.tcpServer.socket.getInetAddress()) && !temp3.equals(Global.tcpServer.socket.getInetAddress()) && Global.peers.indexOf(temp3)==-1) Global.peers.add(temp3);
					}
				}
			else if(this.state==1) Global.process(that,command,this.linkId);
			}
		return data;
		}
	public void sent(TcpClient data, String command){
		if(Global.debugLevel>=3) System.out.println("Data sent to "+this.remote_socket.getAddress().getHostAddress()+":"+this.remote_socket.getPort()+" = "+command.substring(0,command.length()-1));
		}
	}
	
class LinkClient extends Handler {
	public int state=0;
	public InetSocketAddress remote_socket;
	public void start(TcpClient that){
		this.linkId = ++Global.nextLinkId;
		this.remote_socket = new InetSocketAddress(that.socket.getInetAddress(), that.socket.getPort());
		if(Global.debugLevel>=2) System.out.println("Connection established to "+this.remote_socket.getAddress().getHostAddress()+":"+this.remote_socket.getPort());
		that.send(Global.peerHello+Global.delimiter2+Global.tcpServer.socket.getInetAddress().getHostAddress()+Global.delimiter3+Integer.toString(Global.tcpServer.socket.getLocalPort())+Global.delimiter1); this.state=1;
		Global.links.add(this.remote_socket); Global.clients.add(that);
		if(Global.peers.indexOf(this.remote_socket)==-1) Global.peers.add(this.remote_socket);
		}
	public void stop(TcpClient that){
		if(Global.debugLevel>=2) System.out.println("Connection terminated to "+this.remote_socket.getAddress().getHostAddress()+":"+this.remote_socket.getPort());
		Global.links.remove(this.remote_socket); Global.clients.remove(that);
		}
	public String process(TcpClient that, String data){
		String command; int offset;
		while((offset=data.indexOf(Global.delimiter1))!=-1){
			command = data.substring(0,offset); data = data.substring(offset+1);
			if(Global.debugLevel>=3) System.out.println("Data from "+this.remote_socket.getAddress().getHostAddress()+":"+this.remote_socket.getPort()+" = "+command);
			if(this.state==1 && command.startsWith(Global.peerAccept)){ this.state=2; that.send(Global.peerGet+Global.delimiter1); }
			else if(this.state==1 && command.startsWith(Global.peerReject)){
				that.close();
				Global.reject.add(this.remote_socket);
				this.state=0;
				}
			else if(this.state==2 && command.startsWith(Global.peerGet)){
				String temp = Global.peerList;
				for(int i=0;i<Global.peers.size();++i)
					if(!(Global.peers.get(i).getAddress().equals(Global.tcpServer.socket.getInetAddress()) && Global.peers.get(i).getPort()==Global.tcpServer.socket.getLocalPort())
					&& !(Global.peers.get(i).getAddress().equals(this.remote_socket.getAddress()) && Global.peers.get(i).getPort()==this.remote_socket.getPort() ) )
						temp+=Global.delimiter2+Global.peers.get(i).getAddress().getHostAddress()+Global.delimiter3+Integer.toString(Global.peers.get(i).getPort());
				temp+=Global.delimiter1;
				that.send(temp);
				}
			else if(this.state==2 && command.startsWith(Global.peerList)){
				String[] temp1 = command.split(Global.escape+Global.delimiter2);
				String[] temp2; InetSocketAddress temp3;
				for(int i=1; i<temp1.length; ++i){
					temp2 = temp1[i].split(Global.escape+Global.delimiter3);
					temp3 = new InetSocketAddress(temp2[0],Integer.parseInt(temp2[1]));
					if(!temp3.equals(Global.tcpServer.socket.getInetAddress()) && Global.peers.indexOf(temp3)==-1) Global.peers.add(temp3);
					}
				}
			else if(this.state==2) Global.process(that,command,this.linkId);
			}
		return data;
		}
	public void sent(TcpClient that, String command){
		try { if(Global.debugLevel>=3) System.out.println("Data sent to "+this.remote_socket.getAddress().getHostAddress()+":"+this.remote_socket.getPort()+" = "+command.substring(0,command.length()-1)); }
		catch(NullPointerException e){}
		}
	}
	
class RootServer implements Runnable {
	private Thread thread;
	public boolean active = false;
	public RootServer(){
		try {
			Class.forName("org.sqlite.JDBC");
			Global.sqlConnection = DriverManager.getConnection("jdbc:sqlite:"+Global.sqlDatabaseFile);
			Global.sqlStatement = Global.sqlConnection.createStatement();
			Global.sqlStatement.executeUpdate("CREATE TABLE IF NOT EXISTS users (user, pass, sid, key, time);");
			Global.sqlStatement.executeUpdate("CREATE TABLE IF NOT EXISTS friends (user, friend);");
			Global.sqlStatement.executeUpdate("UPDATE users SET sid='', time='0';"); // reset all sessions and times
			
			// Reach this place only if the above statements are suceessful.
			this.thread = new Thread(this,"RootServer");
			this.active = true;
			this.thread.start();
			}
		catch(ClassNotFoundException e){ e.printStackTrace(); }
		catch(SQLException e){ e.printStackTrace(); }
		}
	public void run(){
		while(this.active){ Global.sleep(1000);
			++Global.timestamp; if(true || Global.timestamp%5==0) Global.process(null,Global.serverTimestamp+Global.delimiter2+Global.timestamp+Global.delimiter2+"0",0);
			}
		try {
			Global.sqlConnection.close();
			}
		catch(SQLException e){ e.printStackTrace(); }
		}
	}
	
class StepAction implements Runnable {
	private Thread thread;
	public boolean active=false;
	public StepAction(){
		this.thread = new Thread(this,"Step Action");
		this.active = true;
		this.thread.start();
		}
	public void run(){
		while(this.active){ Global.sleep(1000); ++Global.localTime;
			if(false){
				System.out.print("Peers = "); for(int i=0;i<Global.peers.size();++i) System.out.print(Global.peers.get(i).getPort()+" "); System.out.println();
				System.out.print("Links = "); for(int i=0;i<Global.links.size();++i) System.out.print(Global.links.get(i).getPort()+" "); System.out.println();
				}
			
			// Try and make more links
			boolean k = true;
			if(Global.links.size()<Global.maxLinks)
				for(int i=0;k && i<Global.peers.size();++i)
					if(!Global.peers.get(i).equals(Global.tcpServer.socket.getInetAddress()) && Global.links.indexOf(Global.peers.get(i))==-1 && Global.reject.indexOf(Global.peers.get(i))==-1){
						TcpClient t = new TcpClient(); t.handler = new LinkClient();
						k = false;
						if(!t.connect(Global.peers.get(i).getHostName(),Global.peers.get(i).getPort())){ Global.peers.remove(i); k=true; }
						}
			
			// Ask for more peers if more links can be made, but were not in the previous step
			if(Global.links.size()<Global.maxLinks && k==true)
				for(int i=0;i<Global.clients.size();++i) if(Global.clients.get(i).active) Global.clients.get(i).send("13"+Global.delimiter1);
				
			// If excess peers have been connected to, randomly disconnect from some.
			for(int i=0;Global.links.size()>Global.maxLinks;++i) Global.clients.get(i).close();
			
			// If a peer had previously rejected you, try and reconnect after about 5 min
			if(Global.localTime%300==0) Global.reject.clear();
			
			// After approx 5 minutes, send messages to the server
			if(Global.rootHop!=null){
				try {
					String temp="";
					if(Global.rootSessionId==-1){
						temp = Global.b64encode(Global.username.getBytes(Global.charset))+Global.delimiter3+Global.b64encode(Global.password.getBytes(Global.charset))+Global.delimiter2;
						temp = Global.serverHello+Global.delimiter2+Global.rootPublic.encrypt(temp+Global.b64encode(Global.rootSession.key))+Global.delimiter2;
						
						}
					else if(Global.localTime%10==0){
						temp = Global.rootSession.encrypt("");
						temp = Global.serverFriends+Global.delimiter2+Global.rootSessionId+Global.delimiter2+temp+Global.delimiter2;
						}
					if(!temp.equals("")) Global.rootHop.send(temp+Global.delimiter1);
					}
				catch(UnsupportedEncodingException e){ e.printStackTrace(); }
				}
			}
		}
	}
	
class DiscoveryServer extends Handler {
	public void server_start(UdpServer that){
		if(Global.debugLevel>=2) System.out.println("UDP Server started at "+that.socket.getLocalPort()+".");
		}
	public void server_stop(UdpServer that){
		if(Global.debugLevel>=2) System.out.println("UDP Server terminated at "+that.socket.getLocalPort()+".");
		}
	public void process(UdpServer that,String source,String data){
		if(Global.debugLevel>=3) System.out.println("UDP Data from "+source+" = "+data);
		if(data.startsWith(Global.udpDiscover+Global.delimiter2)){
			String[] temp = data.substring(data.indexOf(Global.delimiter2)+1).split(Global.escape+Global.delimiter3);
			String ip = Global.tcpServer.socket.getInetAddress().getHostAddress();
			if(ip.indexOf("/")!=-1) ip = ip.substring(ip.indexOf("/")+1);
			String temp2 = Global.udpResponse+Global.delimiter2+ip+Global.delimiter3+Global.tcpServer.socket.getLocalPort();
			UdpClient.send(temp[0],Integer.parseInt(temp[1]),temp2);
			}
		if(data.startsWith(Global.udpResponse+Global.delimiter2)){
			String[] temp = data.substring(data.indexOf(Global.delimiter2)+1).split(Global.escape+Global.delimiter3);
			InetSocketAddress temp3 = new InetSocketAddress(temp[0],Integer.parseInt(temp[1]));
			if(!temp3.equals(Global.tcpServer.socket.getInetAddress()) && Global.peers.indexOf(temp3)==-1) Global.peers.add(temp3);
			}
		}
	}
	
public class ChatNetwork implements Runnable {
	public Thread thread;
	ChatNetwork(String u,String p){
		this.thread = new Thread(this,"ChatNetwork");
		Global.username = u; Global.password = p;
		this.thread.start();
		}
	public static void main(String args[]){
                if(args.length<2) System.out.println("Insufficient command line arguments.");
		if(args[0].equals("root") && args[1].equals("root")){
			Global.rootPrivate = new RsaSystem(RsaSystem.DECRYPT_MODE,new BigInteger("5255570387444500774026724150037639960969843364882307137043218787931083464727087994040312864000073728169398985022527792293867660937028775123930022695920085822282861493713955697661901457614592894551268921598282141929392044120124736288623251115090403973632104159097131246573361175571546545301620151919371494883713745286848968507305702901283031162157468674299372335655863226860670651913681610393351803369032793326781399258753573560810202936824844272840981798457042125106122203582877788919704762592465220091277492426748481859618139952409158966600891753368530912816012917459472555782833983256734473653836648112711081715089"),Global.rootPublic.modulus);
			String temp = "KaustubhKarkare"; if(Global.rootPrivate!=null && temp.equals(Global.rootPrivate.decrypt(Global.rootPublic.encrypt(temp))) ) Global.rootServer = new RootServer();
			}
		ChatNetwork cn = new ChatNetwork(args[0],args[1]);
		Global.console();
		Global.close();
		}
	public void run(){
		try { Global.username_b64e = Global.b64encode(Global.username.getBytes(Global.charset)); }
		catch(UnsupportedEncodingException e){ e.printStackTrace(); Global.username_b64e = ""; }
			
		Global.tcpServer = new TcpServer(); Global.tcpServer.handler = new LinkServer();
		for(int i=Global.tcpPortMin;i<Global.tcpPortMax && !Global.tcpServer.listen(i); ++i);
		Global.udpServer = new UdpServer(); Global.udpServer.handler = new DiscoveryServer();
		for(int i=Global.udpPortMin;i<Global.udpPortMax && !Global.udpServer.listen(i); ++i);
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
			InetAddress broadcast = networkInterface.getInterfaceAddresses().get(0).getBroadcast();
			String ip1 = localHost.getHostAddress(); if(ip1.indexOf("/")!=-1) ip1 = ip1.substring(ip1.indexOf("/")+1);
			String ip2 = broadcast.getHostAddress(); if(ip2.indexOf("/")!=-1) ip2 = ip2.substring(ip2.indexOf("/")+1);
			for(int i=Global.udpPortMin;i<Global.udpPortMax;++i){
				UdpClient.send(ip2,i,Global.udpDiscover+Global.delimiter2+ip1+Global.delimiter3+Global.tcpServer.socket.getLocalPort());
				}
			}
		catch(Exception e){ e.printStackTrace(); }
		Global.sleep(1000); // Give enough time for the Server to up and running properly
		Global.stepAction = new StepAction();
		}
	public static void main_old(String args[]){
		if(args.length==0){ System.out.println("Insufficient number of Arguments."); return; }
		System.out.println(args[0]);
		if(args[0].equals("rsa")){
			RsaSystem r = new RsaSystem(2048);
			String c = "1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 ";
			System.out.println("Orig = "+c);
			c = r.encrypt(c);
			System.out.println("Code = "+c);
			c = r.decrypt(c);
			System.out.println("Back = "+c);
			System.out.println("Parameters = "+r.publicExponent+", "+r.privateExponent+", "+r.modulus);
			}
		else if(args[0].equals("encrypt")){
			DhkeSystem x = new DhkeSystem(); DhkeSystem y = new DhkeSystem();
			String a = x.step1(); String b = y.step2(a); x.step3(b);
			String c = "Kaustubh";
			AesSystem a1 = new AesSystem(x.secretKey); AesSystem a2 = new AesSystem(y.secretKey);
			System.out.println(c); c = a1.encrypt(c); System.out.println(c); c = a2.decrypt(c); System.out.println(c);
			return;
			}
		else if(args[0].equals("tcpserver") || args[0].equals("tcpclient") || args[0].equals("udpserver") || args[0].equals("udpclient")){
			DataInputStream console = new DataInputStream(System.in);
			String line;
			TcpServer x1 = new TcpServer();
			TcpClient x2 = new TcpClient();
			UdpServer x3 = new UdpServer();
			if(args[0].equals("tcpserver")){ x1.handler = new xxx(); x1.listen(420); }
			else if(args[0].equals("tcpclient")){  x2.handler = new xxx(); if(x2.connect("localhost",420)==false) return; }
			else if(args[0].equals("udpserver")){ x3.handler = new xxx(); x3.listen(420); }
			Global.sleep(100);
			while(true){
				System.out.print("Send Data (\".bye\" to quit) : ");
				try { line = console.readLine(); } // deprecated
				catch (Exception e){ e.printStackTrace(); break; }
				if(args[0].equals("tcpserver")) x1.broadcast(line);
				else if(args[0].equals("tcpclient")) x2.send(line);
				else if(args[0].equals("udpclient")){ UdpClient.send("localhost",420,line); }
				if(line.equals(".bye")) break;
				}
			x1.close(); x2.close(); x3.close();
			}
		else if(args[0].equals("chat1")){
			TcpServer x = new TcpServer();
			x.handler = new LinkServer();
			x.listen(420);
			DataInputStream console = new DataInputStream(System.in); String line;
			while(true){
				try { line = console.readLine(); } // deprecated
				catch (Exception e){ e.printStackTrace(); break; }
				if(line.equals(".bye")) break;
				}
			x.close();
			}
		else if(args[0].equals("chat2")){
			TcpClient x = new TcpClient();
			x.handler = new LinkClient();
			x.connect("localhost",420);
			// Global.peers.add(new InetSocketAddress("localhost",420));
			for(ListIterator listIterator = Global.peers.listIterator(); listIterator.hasNext();) System.out.println(listIterator.next());
			DataInputStream console = new DataInputStream(System.in); String line;
			while(true){
				try { line = console.readLine(); } // deprecated
				catch (Exception e){ e.printStackTrace(); break; }
				if(line.equals(".bye")) break;
				x.send(line+Global.delimiter1);
				}
			x.close();
			}
		else if(args[0].equals("chat3")){
			int port = Integer.parseInt(args[1]);
			UdpServer u = new UdpServer();
			if(u.listen(port)) System.out.println("Server started.");
			try {
				InetAddress localHost = InetAddress.getLocalHost();
				NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
				InetAddress broadcast = networkInterface.getInterfaceAddresses().get(0).getBroadcast();
				for(int i=420;i<430;++i){
					System.out.println("Sending \""+localHost.toString()+":"+Integer.toString(port)+" to "+broadcast.getHostAddress().toString()+":"+Integer.toString(i)+"[UDP]");
					UdpClient.send(broadcast.getHostAddress(),i,localHost.toString()+":"+Integer.toString(port));
					}
				}
			catch(Exception e){ e.printStackTrace(); }
			DataInputStream console = new DataInputStream(System.in); String line;
			while(true){
				try { line = console.readLine(); } // deprecated
				catch (Exception e){ e.printStackTrace(); break; }
				if(line.equals(".bye")) break;
				}
			u.close();
			}
		else if(args[0].equals("sql")){
			try {
				Class.forName("org.sqlite.JDBC");
				Connection conn = DriverManager.getConnection("jdbc:sqlite:"+Global.sqlDatabaseFile);
				Statement stat = conn.createStatement();
				ResultSet rs = stat.executeQuery("select * from users;");
				while (rs.next()) System.out.println(rs.getString("user")+" | "+rs.getString("pass")+" | "+rs.getString("key")+" | "+rs.getString("sid")+" | "+rs.getString("time"));
				rs.close();
				rs = stat.executeQuery("select * from friends;");
				while (rs.next()) System.out.println(rs.getString("user")+" | "+rs.getString("friend"));
				rs.close();
				conn.close();
				}
			catch(ClassNotFoundException e){ e.printStackTrace(); }
			catch(SQLException e){ e.printStackTrace(); }
			}
		else if(args[0].equals("node") || args[0].equals("root")){ // 0:node, 1:username, 2: password, 3:server-port, 4..:peer-port
		
			if(args[0].equals("root")) Global.rootPrivate = new RsaSystem(RsaSystem.DECRYPT_MODE,new BigInteger("5255570387444500774026724150037639960969843364882307137043218787931083464727087994040312864000073728169398985022527792293867660937028775123930022695920085822282861493713955697661901457614592894551268921598282141929392044120124736288623251115090403973632104159097131246573361175571546545301620151919371494883713745286848968507305702901283031162157468674299372335655863226860670651913681610393351803369032793326781399258753573560810202936824844272840981798457042125106122203582877788919704762592465220091277492426748481859618139952409158966600891753368530912816012917459472555782833983256734473653836648112711081715089"),Global.rootPublic.modulus);

			String temp = "KaustubhKarkare"; if(Global.rootPrivate!=null && temp.equals(Global.rootPrivate.decrypt(Global.rootPublic.encrypt(temp))) ) Global.rootServer = new RootServer();
			Global.username = args[1]; Global.password = args[2];
			try { Global.username_b64e = Global.b64encode(Global.username.getBytes(Global.charset)); }
			catch(UnsupportedEncodingException e){ e.printStackTrace(); Global.username_b64e = ""; }
			
			Global.tcpServer = new TcpServer(); Global.tcpServer.handler = new LinkServer(); Global.tcpServer.listen(Integer.parseInt(args[3]));
			Global.sleep(1000); // Give enough time for the Server to up and running properly
			for(int i=4; i<args.length; ++i){
				TcpClient t = new TcpClient();
				t = new TcpClient(); t.handler = new LinkClient();
				try { t.connect(InetAddress.getLocalHost().getHostAddress(),Integer.parseInt(args[i])); }
				catch(UnknownHostException e){ e.printStackTrace(); }
				}
			Global.stepAction = new StepAction();

			Global.console();
			Global.close();
			}
		else { System.out.println("Unknown argument ["+args[0]+"]"); return; }
		}
	}