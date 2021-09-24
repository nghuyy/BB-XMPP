package com.huynguyen.bbchat;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Persistable;

public class OptionsData implements Persistable {
    private static final long ID = 0x21fcf1530efc5163L;
    public String userName;
    public String passWord;
    public String port;
    public String domain;
    public String server;
    public String boshurl;

    public void commit()
    {
        PersistentObject.commit(this);
    }

    public static OptionsData load()
    {
        PersistentObject persist = PersistentStore.getPersistentObject(OptionsData.ID );
        synchronized( persist )
        {
            if( persist.getContents() == null )
            {
                persist.setContents( new OptionsData() );
                persist.commit();
            }
        }

        return (OptionsData)persist.getContents();
    }
}
