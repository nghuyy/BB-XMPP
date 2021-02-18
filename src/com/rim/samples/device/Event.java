package com.rim.samples.device;

import net.rim.device.api.notification.*;

/**
 * A class representing a notification event.  This class is used to start/stop
 * immediate and deferred events simultaneously. 
 */
public final class Event
{
	public static final long NOTIFICATIONS_ID_1 = 0xdc5bf2f81374095L; 
    private long _sourceId;
    long _eventId;
    int _priority;
    private int _triggerIndex;
    private long _timeout;

    /**
     * Creates a new Event object
     */
    public Event( long sourceid, long eventid, int priority, long timeout, int triggerIndex )
    {
        _sourceId = sourceid;
        _eventId = eventid;
        _priority = priority;
        _triggerIndex = triggerIndex;
        _timeout = timeout;
    }

    /**
     * Invoke the event.
     */ 
    public void fire()
    {
        // negotiateDeferredEvent() will cause the event to be queued. 
        // Ultimately, NotificationsEngineListener.proceedWithDeferredEvent()
        // will be fired in response to the event.
        NotificationsManager.negotiateDeferredEvent( _sourceId, 0, this, _timeout, _triggerIndex, null );

        // triggerImmediateEvent() causes non-interactable events to fire, such
        // as tunes, vibrations and LED flashing as specified by the user in the
        // Profiles settings.  This call will cause the startNotification()
        // method for all registered Consequence objects to be invoked.
        // By default, the Profiles application has a Consequence registered
        // and will look up the current profile's configurations to determine
        // what needs to be done (in terms of vibrate and tone) for a given
        // notifications source.  This application's profile configuration
        // appears as "Notifications Demo" in the Profiles settings.
        NotificationsManager.triggerImmediateEvent( _sourceId, 0, this, null );
    }

    /**
     * Cancel the event.
     */    
    public void cancel()
    {
        // If event exists in the queue, it will be removed and 
        // NotificationsEngineListener.deferredEventWasSuperseded()
        // will be fired.
        NotificationsManager.cancelDeferredEvent( _sourceId, 0, this, _triggerIndex, null );

        // The stopNotification() method for all registered Consequence objects
        // will be called.
        NotificationsManager.cancelImmediateEvent( _sourceId, 0, this, null );
    }
}
