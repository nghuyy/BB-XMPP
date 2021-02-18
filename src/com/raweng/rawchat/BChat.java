package com.raweng.rawchat;

import java.util.Vector;

import net.rim.blackberry.api.messagelist.ApplicationIcon;
import net.rim.blackberry.api.messagelist.ApplicationIndicator;
import net.rim.blackberry.api.messagelist.ApplicationIndicatorRegistry;
import net.rim.blackberry.api.messagelist.ApplicationMessage;
import net.rim.blackberry.api.messagelist.ApplicationMessageFolderRegistry;
import net.rim.device.api.notification.NotificationsConstants;
import net.rim.device.api.notification.NotificationsManager;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.UiApplication;

import com.raweng.xmppservice.Connection;
import com.rim.samples.device.ConsequenceImpl;
import com.rim.samples.device.NotificationsEngineListenerImpl;
import com.rim.samples.device.MessageListDemoDaemon;
import com.rim.samples.device.CommunicationSimulatorThread;

public class BChat extends UiApplication {
	static final long NOTIFICATIONS_ID_1 = 0xdc5bf2f81374095L; 
	public static boolean DEBUG = true;
	public static DebugScreen debugConsole;
	public static ApplicationIcon applicationIcon;
	private Connection connection;
	
	    /* com.rim.samples.device.messagelistdemo */
	    static final long KEY = 0x39d90c5bc6899541L; // Base folder key 

	    /* com.rim.samples.device.messagelistdemo.INBOX_FOLDER_ID */
	    static final long INBOX_FOLDER_ID = 0x2fb5115c0e4a6c33L;

	    /* com.rim.samples.device.messagelistdemo.DELETED_FOLDER_ID */
	    static final long DELETED_FOLDER_ID = 0x78d50a91eff39e5bL;

	    /**
	     * Flag for replied messages. The lower 16 bits are RIM-reserved, so we have
	     * to use higher 16 bits.
	     */
	    static final int FLAG_REPLIED = 1 << 16;

	    /**
	     * Flag for deleted messages. The lower 16 bits are RIM-reserved, so we have
	     * to use higher 16 bits.
	     */
	    static final int FLAG_DELETED = 1 << 17;

	    // All our messages are received, we don't show sent messages
	    static final int BASE_STATUS = ApplicationMessage.Status.INCOMING;

	    static final int STATUS_NEW = BASE_STATUS | ApplicationMessage.Status.UNOPENED;
	    static final int STATUS_OPENED = BASE_STATUS | ApplicationMessage.Status.OPENED;
	    static final int STATUS_REPLIED = BASE_STATUS | ApplicationMessage.Status.OPENED | FLAG_REPLIED;
	    static final int STATUS_DELETED = BASE_STATUS | FLAG_DELETED;

	    // Constant to define number of bulk messages
	    static final int MAX_MSGS = 50;

	    private CommunicationSimulatorThread _commThread;
	private LoginScreen loginscreen;
	

	public static void main(String[] args) {
		if (BChat.DEBUG) {
			BChat.debugConsole = new DebugScreen();
		}
		BChat nd = new BChat();
        if(args.length == 1 && args[0].equals("startup")){
	            // Keep this instance around for rendering
	            // Notification dialogs.
	            MessageListDemoDaemon daemon = new MessageListDemoDaemon();
	           
	            // Register application indicator
	            
	            // Check if this application registered folders already
	            ApplicationMessageFolderRegistry reg = ApplicationMessageFolderRegistry.getInstance();
	            if(reg.getApplicationFolder(INBOX_FOLDER_ID) == null)
	            {
	                daemon.init();
	            }
	
	            // This daemon application will be responsible for
	            // listening for notifications and menu actions, it runs until
	            // the device shuts down or the application is uninstalled.
	            daemon.enterEventDispatcher();
        }
        EncodedImage indicatorIcon = EncodedImage.getEncodedImageResource("img/ic_indicator.png");
        applicationIcon = new ApplicationIcon(indicatorIcon);
        nd.registerNotificationObjects();
        nd.enterEventDispatcher(); 
	}
	  
    /**
     * Registers this application as the notification manager
     */
    private void registerNotificationObjects()
    {
        // A source is registered to tell the system that the application will
        // be sending notification events.  This will will cause a new user
        // editable configuration to be added to the Profiles application. 
        NotificationsManager.registerSource( NOTIFICATIONS_ID_1, new Object()
        {
            public String toString()
            {
                return "Notifications Demo";
            }
        }, NotificationsConstants.IMPORTANT );
        
        // Our NotificationsEngineListener implementation will display a dialog
        // to the user when a deferred event is triggered.
        NotificationsManager.registerNotificationsEngineListener( NOTIFICATIONS_ID_1,
                new NotificationsEngineListenerImpl( this ) );        
                
        // Our Consequence implementation will be invoked whenever an immediate
        // event occurs.        
        NotificationsManager.registerConsequence(ConsequenceImpl.ID, new ConsequenceImpl());
    }
    
	public BChat() {
		ChatManager.bchat = this;
		BuddyScreen.btalk = this;
		Buddy.btalk = this;
		LoginScreen.btalk = this;
		AppSavedData.bchat = this;
		
		
		this.connection = Connection.getInstance();
		
		
		AppSavedData.readOptions();
		Vector up = AppSavedData.getUserInfo();
		if (up != null) {
			String username = "";
			String domain = "";
			if ((String)up.elementAt(0) != null && ((String)up.elementAt(0)).length() > 0) {
				int i = ((String)up.elementAt(0)).indexOf('@');
				username = ((String)up.elementAt(0)).substring(0, i);
				domain = ((String)up.elementAt(0)).substring(i+1);
			}
			String myjid = username + "@" + domain;
			
			this.connection.setUsername(username);
			this.connection.setPassword((String)up.elementAt(1));
			this.connection.setHost(domain);
			this.connection.setMyjid(myjid);
			this.connection.setNetworkType(Integer.parseInt((String) up.elementAt(2)));
			
			ServerModel serverDef = (ServerModel) up.elementAt(3);
			this.connection.setServer(serverDef.server);
			this.connection.setPort(serverDef.port);
			this.connection.setHttpburl(serverDef.boshUrl);
			this.connection.setBosh(serverDef.useBosh);
			this.connection.setSSL(serverDef.usessl);
			
			
			BuddyListField buddyList = new BuddyListField();
			BuddyScreen buddyscreen = new BuddyScreen(buddyList);
			pushScreen(buddyscreen);
			connection.getChatHandlerInstance().login(username, (String)up.elementAt(1), domain, serverDef, this.connection.getNetworkType());
			
			/*this.loginscreen = new LoginScreen(this.connection, true);
			this.pushScreen(this.loginscreen);*/
			
		} else {
			this.loginscreen = new LoginScreen(this.connection, false);
			this.pushScreen(this.loginscreen);
		}
		
	}
	
	
}
