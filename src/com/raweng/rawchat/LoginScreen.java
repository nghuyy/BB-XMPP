package com.raweng.rawchat;

import com.huynguyen.bbchat.App;
import com.huynguyen.bbchat.LocalizationResource;
import com.huynguyen.bbchat.OptionsData;
import com.raweng.xmppservice.Connection;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.MainScreen;

public class LoginScreen extends MainScreen{
	public static BChat btalk;
	private final OptionsData optionData;
	private Connection connection;

	//private Font labelFont = Font.getDefault().derive(Font.BOLD, 5, Ui.UNITS_pt);
	//private Font textFont = Font.getDefault().derive(Font.PLAIN, 5, Ui.UNITS_pt);
	private AutoTextEditField usernameTextField;
	private AutoTextEditField domainTextField;
	private PasswordEditField passwordTextField;
	private AutoTextEditField connectServerTextField;
	private AutoTextEditField connectPortTextField;
	private AutoTextEditField boshUrlTextField;
	private ButtonField saveButtonField;
	//private ObjectChoiceField networkTypeChoiceField;
	
	public final static int NETWORK_TYPE_INDEX_ARRAY[] = {0, 1, 2, 4, 8, 16};
	public final static String NETWORK_TYPE_NAME_ARRAY[] = {"Auto", "WIFI", "BES", "BIS", "Direct TCP","WAP"};
	private ResourceBundle _resources;


	public LoginScreen(Connection connection, boolean saved) {
		super(NO_VERTICAL_SCROLL | USE_ALL_WIDTH);
		_resources = App.getResource();
		setTitle("Login");
		this.optionData = App.getOptionData();
		this.connection = connection;
		//this.saved = saved;
		this.initUI();
	}


	protected void makeMenu(final Menu menu, int context) {
		menu.add(new MenuItem(_resources.getString(LocalizationResource.UPDATE), 0, 0) {
			public void run() {
				BuddyScreen.CheckUpdate();
			}
		});
	}

	private void initUI() {
		usernameTextField = new AutoTextEditField("Username:","",250,AutoTextEditField.AUTOCAP_OFF);
		if (optionData.userName!=null)usernameTextField.setText(optionData.userName);

		domainTextField = new AutoTextEditField("Domain: ","",250,AutoTextEditField.AUTOCAP_OFF);
		if (optionData.domain!=null)domainTextField.setText(optionData.domain);

		passwordTextField = new PasswordEditField("Password: ","",250,0);
		if (optionData.passWord!=null)passwordTextField.setText(optionData.passWord);

		connectServerTextField = new AutoTextEditField("Connect Server: ","",250,AutoTextEditField.AUTOCAP_OFF);
		if (optionData.server!=null)connectServerTextField.setText(optionData.server);

		connectPortTextField = new AutoTextEditField("Connect Port: ","",250,AutoTextEditField.AUTOCAP_OFF);
		if (optionData.port!=null)connectPortTextField.setText(optionData.port);
		boshUrlTextField = new AutoTextEditField("BOSH Url: ","",250,AutoTextEditField.AUTOCAP_OFF);
		if (optionData.boshurl!=null)boshUrlTextField.setText(optionData.boshurl);
		
		saveButtonField = new ButtonField("Login") {
			protected boolean keyChar(char key, int status, int time) {
				if (key == Keypad.KEY_ENTER) {
					return login();
				} else {
					return false;
				}
			}
			protected boolean navigationClick(int status, int time) {
				return login();
			}
		};

		
		add(usernameTextField);
		add(domainTextField);
		add(passwordTextField);
		add(connectServerTextField);
		add(connectPortTextField);
		add(boshUrlTextField);
		//add(new NullField(Field.NON_FOCUSABLE));
		add(saveButtonField);
		

		
		
		
		
		this.addMenuItem(new MenuItem("Login", 0, 0) {
			public void run() {
				login();
			}
		});
		
		/*this.addMenuItem(new MenuItem("Options", 0, 0){
			public void run() {
				btalk.pushScreen(new SettingsScreen());
			}
		});*/
		
		
		if (BChat.DEBUG) {
			this.addMenuItem(new MenuItem("Debug console", 0x00030006, 0) {
				public void run() {
					btalk.pushScreen(BChat.debugConsole);
				}
			});
		}
	}
	
	private boolean login() {

		if (usernameTextField.getText().length() == 0 || passwordTextField.getText().length() == 0) {
			Dialog.alert("Invalid username/password!");
			return true;
		}
		
		ServerModel serverDef = new ServerModel();
		serverDef.useWifi = false;

		if (connectServerTextField.getText().length() <= 0 && boshUrlTextField.getText().length() <= 0) {
			Dialog.alert("Invalid server address");
			return true;
		}

		serverDef.server = connectServerTextField.getText();
		serverDef.boshUrl = boshUrlTextField.getText();
		if (boshUrlTextField.getText().length() > 0 && connectServerTextField.getText().length() == 0) {
			serverDef.useBosh = true;
		} else {
			serverDef.useBosh = false;
		}
		
		serverDef.port = connectPortTextField.getText().trim();
		if (serverDef.port.equals("5223")) {
			serverDef.usessl = true;
		} else {
			serverDef.usessl = false;
		}

		optionData.userName = usernameTextField.getText();
		optionData.passWord = passwordTextField.getText();
		optionData.domain = domainTextField.getText();
		optionData.server = serverDef.server;
		optionData.boshurl = serverDef.boshUrl;
		optionData.port = serverDef.port;
		optionData.commit();
		connection.getChatHandlerInstance().login(optionData.userName, optionData.passWord, optionData.domain , serverDef, 0);
		return true;
	}
	
	public boolean onClose() {
		this.close();
		return true;
	}
	
	protected boolean onSavePrompt() {
		return true;
	}
	
	
	public static int getNetworkTypeNameArrayIndex(int networkTypeIndexArrayIndex){
		for(int i = 0; i < NETWORK_TYPE_INDEX_ARRAY.length; i++){
			if(NETWORK_TYPE_INDEX_ARRAY[i] == networkTypeIndexArrayIndex){
				return i;
			}
		}
		return 0;
	}

	public static int getNetworkTypeIndexArrayIndex(String networkName){
		for(int i = 0; i < NETWORK_TYPE_NAME_ARRAY.length; i++){
			if(NETWORK_TYPE_NAME_ARRAY[i].equals(networkName)){
				return i;
			}
		}
		return 0;
	}

}
