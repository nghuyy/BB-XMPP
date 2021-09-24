package com.huynguyen.bbchat;

import com.huynguyen.bbchat.OptionsData;
import com.huynguyen.bbchat.LocalizationResource;
import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.i18n.ResourceBundleFamily;

public class App implements LocalizationResource {
    public static int CHAT_HEIGHT = 52;
    public static final String UPDATE_URL = "http://vnapps.com/BBOS/BBChat_Release/manifest.json";
    public static final String UPDATE_JAD = "http://vnapps.com/BBOS/BBChat_Release/BBChat.jad";
    private static ResourceBundleFamily _resources;
    public static ResourceBundle getResource(){
        if(_resources==null){
            _resources = ResourceBundle.getBundle(BUNDLE_ID, BUNDLE_NAME);
        }
        return _resources;
    }
    private static OptionsData _optionDataInstant;

    public static OptionsData getOptionData() {
        if (_optionDataInstant == null) {
            _optionDataInstant = OptionsData.load();
        }
        return _optionDataInstant;
    }
}
