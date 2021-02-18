package com.rim.samples.device;


import java.util.Random;
import net.rim.device.api.collection.ReadableList;
import net.rim.device.api.system.*;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.UiApplication;


/**
 * This Thread subclass simulates communication with a server and
 * generates message actions. It can create new messages or update
 * and delete existing ones.
 */
public final class CommunicationSimulatorThread extends Thread
{

    private boolean _keepRunning;
    private static Random _random = new Random();

    private static final String[] NAMES = {"Scott Wyatt", "Tanya Wahl", "Kate Strike", "Mark McMullen", "Beth Horton", "John Graham",
                    "Ho Sung Chan", "Long Feng Wu", "Kevil Wilhelm", "Trevor Van Daele"};

    private static final String[] PICTURES = {"BlueDress.png", "BlueSuit.png", "BlueSweatshirt.png", "BrownShirt.png", "Construction.png",
                    "DarkJacket.png", "DarkSuit.png", "FemaleDoctor.png", "GreenJacket.png", "GreenShirt.png", "GreenTop.png",
                    "LeatherJacket.png", "MaleDoctor.png", "Mechanic.png", "OrangeShirt.png", "PatternShirt.png", "PurpleTop.png",
                    "RedCap.png", "RedJacket.png", "RedShirt.png"};


    /**
     * Creates a new CommunicationSimulatorThread object
     */    
    public CommunicationSimulatorThread()
    {
        _keepRunning = true;
    }


    /**
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        // Perform random actions to the message store every three seconds
        MessageListDemoStore messageStore = MessageListDemoStore.getInstance();
        while(_keepRunning)
        {
            synchronized(messageStore)
            {
                performRandomAction(messageStore);
            }
            try
            {
                synchronized(this)
                {
                    wait(3000);
                }
            }
            catch(final InterruptedException e)
            {
                UiApplication.getUiApplication().invokeLater(new Runnable()
                {
                    public void run()
                    {
                        Dialog.alert("Thread#wait(long) threw " + e.toString());
                    }
                });

                return;
            }
        }
    }


    /**
     * Performs a random action. The action can either be: updating an existing
     * message, deleting an inbox message or deleting a message completely.
     * 
     * @param messageStore The message store to perform the random action to
     */
    private void performRandomAction(MessageListDemoStore messageStore)
    {
        ReadableList inboxMessages = messageStore.getInboxMessages();
        ReadableList deletedMessages = messageStore.getDeletedMessages();

        switch(_random.nextInt(3))
        {
            case 0:

                // Update an existing message
                if(inboxMessages.size() > 0)
                {
                    DemoMessage msg = (DemoMessage) inboxMessages.getAt(_random.nextInt(inboxMessages.size()));
                    if(msg.isNew())
                    {
                        msg.markRead();
                    }
                    else if(!msg.hasReplied())
                    {
                        msg.reply("Auto reply");
                    }
                    else
                    {
                        msg.markAsNew();
                    }
                    messageStore.getInboxFolder().fireElementUpdated(msg, msg);
                }
                else
                {
                    addInboxMessage(messageStore);
                }
                break;

            case 1:

                // Delete an inbox message
                if(inboxMessages.size() > 0)
                {
                    DemoMessage msg = (DemoMessage) inboxMessages.getAt(_random.nextInt(inboxMessages.size()));
                    messageStore.deleteInboxMessage(msg);
                    messageStore.getInboxFolder().fireElementRemoved(msg);
                    messageStore.getDeletedFolder().fireElementAdded(msg);
                }
                else
                {
                    addInboxMessage(messageStore);
                }
                break;

            default:

                // Delete message completely
                if(deletedMessages.size() > 0)
                {
                    DemoMessage msg = (DemoMessage) deletedMessages.getAt(_random.nextInt(deletedMessages.size()));
                    messageStore.deleteMessageCompletely(msg);
                    messageStore.getDeletedFolder().fireElementRemoved(msg);
                }
                else
                {
                    addInboxMessage(messageStore);
                }
                break;
        }
    }


    /**
     * Adds a predefined message to the specified message store
     * 
     * @param messageStore The message store to add the message to
     */
    private void addInboxMessage(MessageListDemoStore messageStore)
    {
        DemoMessage message = new DemoMessage();
        String name = NAMES[_random.nextInt(NAMES.length)];
        message.setSender(name);
        message.setSubject("Hello from " + name);
        message.setMessage("Hello Chris. This is " + name + ". How are you?  Hope to see you at the conference!");
        message.setReceivedTime(System.currentTimeMillis() - 1000 * 60 * _random.nextInt(60 * 24));

        // Assign random preview picture
        message.setPreviewPicture(getRandomPhotoImage());

        // Store message
        messageStore.addInboxMessage(message);

        // Notify folder
        messageStore.getInboxFolder().fireElementAdded(message);
    }


    /**
     * Retrieves a random predefined image
     * 
     * @return The hard coded photo image
     */
    public static EncodedImage getRandomPhotoImage()
    {
        String pictureName = "photo/" + PICTURES[_random.nextInt(PICTURES.length)];
        return EncodedImage.getEncodedImageResource(pictureName);
    }


    /**
     * Stops the thread from continuing its processing
     */
    void stopRunning()
    {
        synchronized(this)
        {
            _keepRunning = false;
            notifyAll();
        }
    }
}
