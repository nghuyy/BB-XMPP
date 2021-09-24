package com.raweng.rawchat;

import net.rim.blackberry.api.messagelist.ApplicationIndicatorRegistry;
import net.rim.device.api.i18n.*;
import net.rim.device.api.notification.NotificationsConstants;
import net.rim.device.api.system.Bitmap;

import com.raweng.xmppservice.ChatHistory;
import com.rim.samples.device.Event;


public class Buddy{
	private static final long MAX_TIME_INTERVAL = 180000;
	 static final long NOTIFICATIONS_ID_1 = 0xdc5bf2f81374095L; 
	// status value
	public static final int STATUS_OFFLINE 	= 0x00;
	public static final int STATUS_AWAY 	= 0x01;
	public static final int STATUS_BUSY 	= 0x02;
	public static final int STATUS_ONLINE 	= 0x03;
	
	public static BChat btalk;
	public static ChatHistory chatHistory;
	
	private static DateFormat dateGen = DateFormat.getInstance(DateFormat.TIME_DEFAULT);
	
	public String jid;
	public String name;
	public int 	status;
	public String custom_str;
		
	// is the last message from this buddy?
	public boolean lastFrom;
	public long lastTimeStampMe;
	public long lastTimeStampBuddy;
	public String session;
	public Bitmap buddyImage;
	private MessageScreen msgScreen;




	
	public Buddy(String id, String n, int s) {
		if (n == null)
			this.name = id;
		else 
			this.name = n;
		this.jid = id;
		this.status = s;
		this.lastFrom = false;
		this.lastTimeStampBuddy = 0;
		this.lastTimeStampMe = 0;
		this.session = "";
	}
	
	public void sendMessage(String from, String to, String name, String msg) {
		if (lastFrom) {
			this.lastTimeStampMe = System.currentTimeMillis();
			msgScreen.sendMessage(from, to, name, msg, true, dateGen.formatLocal(this.lastTimeStampMe));
		} else {
			long curtime = System.currentTimeMillis();
			if ((curtime - this.lastTimeStampMe) > MAX_TIME_INTERVAL)
				msgScreen.sendMessage(from, to, name, msg, true, dateGen.formatLocal(curtime));
			else
				msgScreen.sendMessage(from, to, name, msg, false, null);
			
			this.lastTimeStampMe = curtime;
		}
		lastFrom = false;
		Buddy.chatHistory.addToChatHistory(System.currentTimeMillis(), from, to, name, msg);
	}
	
	public void receiveMessage(String from, String to, String name, String msg, boolean current) {
		if (!lastFrom) {
			this.lastTimeStampBuddy = System.currentTimeMillis();
			this.getMsgScreen().receiveMessage(from, to, name, msg, current, true, dateGen.formatLocal(this.lastTimeStampBuddy));
		} else {
			long curTime = System.currentTimeMillis();
			if ((curTime - this.lastTimeStampBuddy) > MAX_TIME_INTERVAL) 
				msgScreen.receiveMessage(from, to, name, msg, current, true, dateGen.formatLocal(curTime));
			else
				msgScreen.receiveMessage(from, to, name, msg, current, false, null);
			
			this.lastTimeStampBuddy = curTime;
		}
		lastFrom = true;
		Buddy.chatHistory.addToChatHistory(System.currentTimeMillis(), from, to, name, msg);
	}
	
	
	public void newMessage(String from, String to, String name, String body, boolean showNotification) {
		Buddy.chatHistory.addToChatHistory(System.currentTimeMillis(), from, to, name, body);		
		if (showNotification) {
			msgScreen.newMessage(from, body, System.currentTimeMillis());
		}else{
			msgScreen.notify_event = new Event(NOTIFICATIONS_ID_1 , 1, 500, -1,
					NotificationsConstants.IMPORTANT );
			msgScreen.notify_event.fire();
			msgScreen.indicator = ApplicationIndicatorRegistry.getInstance().register(BChat.applicationIcon, false, false);
		    msgScreen.indicator.setVisible(true);
            /*
            DemoMessage message = new DemoMessage();
            message.setSender(from);
            message.setSubject("Hello!");
            message.setMessage(body);
            message.setReceivedTime(System.currentTimeMillis());
            message.setPreviewPicture(CommunicationSimulatorThread.getRandomPhotoImage());

            // Store message 
            MessageListDemoStore messageStore = MessageListDemoStore.getInstance();
            synchronized(messageStore)
            {
                messageStore.addInboxMessage(message);
            }
            // Notify folder
            ApplicationMessageFolder inboxFolder = messageStore.getInboxFolder();
            inboxFolder.fireElementAdded(message, true); 
            */  
		}		
	}
	
	public MessageScreen getMsgScreen() {
		if (msgScreen == null)
			msgScreen = new MessageScreen(this);
		return msgScreen;
	}
}
