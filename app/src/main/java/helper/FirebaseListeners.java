package helper;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.seeaspark.ConversationActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import database.Database;
import models.ChatsModel;
import models.MessagesModel;
import models.ProfileModel;
import utils.Constants;
import utils.Utils;

/**
 * Created by Applify on 12/30/2016.
 */

public class FirebaseListeners {

    static FirebaseListeners mListener;
    DatabaseReference mFirebaseConfigMessages = FirebaseDatabase.getInstance().getReference().child(Constants.MESSAGES);
    DatabaseReference mFirebaseConfigChat = FirebaseDatabase.getInstance().getReference().child(Constants.CHATS);
    DatabaseReference mFirebaseConfigProfile = FirebaseDatabase.getInstance().getReference().child(Constants.USERS);

    HashMap<String, ChildEventListener> messageListener = new HashMap<>();
    HashMap<String, ChildEventListener> chatsListener = new HashMap<>();
    HashMap<String, ChildEventListener> profileListener = new HashMap<>();

    HashMap<String, Query> messageQuery = new HashMap<>();
    HashMap<String, Query> chatQuery = new HashMap<>();
    HashMap<String, Query> profileQuery = new HashMap<>();

    Utils util;
    Database mDb;
    Query messageQry;
    Query chatsQuery;
    Query usersQuery;
    Context con;

    public static String chatDialogId = "";

    public static void initListener(Context con) {
        if (mListener == null)
            mListener = new FirebaseListeners();
        mListener.initDefaults(con);
    }

    void initDefaults(Context context) {
        util = new Utils(context);
        mDb = new Database(context);
        con = context;
    }

    public static FirebaseListeners getListenerClass(Context con) {
        if (mListener != null)
            return mListener;
        else {
            mListener = new FirebaseListeners();
            mListener.initDefaults(con);
            return mListener;
        }
    }

    ////////////////////  MessagesModel Listener  //////////////////////

    public interface MessageListenerInterface {
        void onMessageAdd(MessagesModel message);

        void onMessageChanged(MessagesModel message);
    }

    public static MessageListenerInterface mMessageInterface;

    public static void setMessageListener(MessageListenerInterface listsner, String dialogId) {
        mMessageInterface = listsner;
        chatDialogId = dialogId;
    }

    public void removeMessageListener(String dialogId) {
        dialogId = dialogId.trim();
        Query mq = messageQuery.get(dialogId);
        if (mq != null && messageListener.containsKey(dialogId)) {
            mq.removeEventListener(messageListener.get(dialogId));
            messageListener.remove(dialogId);
            messageQuery.remove(dialogId);
        }
    }

    public void setListener(String dialogId) {
        dialogId = dialogId.trim();
        if (!messageListener.containsKey(dialogId)) {
            messageListener.put(dialogId, mMessageChildListener);
            messageQry = mFirebaseConfigMessages.child(dialogId);
            messageQry.addChildEventListener(mMessageChildListener);
            messageQuery.put(dialogId, messageQry);
        }
    }

    ChildEventListener mMessageChildListener = new ChildEventListener() {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Log.e("message added", "is " + dataSnapshot + ", " + s);
            MessagesModel message = MessagesModel.parseMessage(dataSnapshot);
            if (message.message_deleted.get(util.getString("user_id", "")).equals("0")) {
                mDb.addMessage(message, util.getString("user_id", ""));
                if (chatDialogId.equals(message.chat_dialog_id)) {
                    if (mMessageInterface != null) {
                        message = mDb.getSingleMessage(message.message_id, util.getString("user_id", ""));
                        mMessageInterface.onMessageAdd(message);
                    }
                } else {
                    if (!message.sender_id.equals(util.getString("user_id", ""))) {
                        if (message.message_status == Constants.STATUS_MESSAGE_SENT) {
                            mFirebaseConfigMessages.child(message.chat_dialog_id).child(message.message_id).child("message_status").setValue(Constants.STATUS_MESSAGE_DELIVERED);
                        }
                    }
                }
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Log.e("message changed", "is " + dataSnapshot + ", " + s);
            MessagesModel message = MessagesModel.parseMessage(dataSnapshot);
            if (message.message_deleted.get(util.getString("user_id", "")).equals("0")) {
                mDb.addMessage(message, util.getString("user_id", ""));
                if (chatDialogId.equals(message.chat_dialog_id)) {
                    if (mMessageInterface != null) {
                        message = mDb.getSingleMessage(message.message_id, util.getString("user_id", ""));
                        mMessageInterface.onMessageChanged(message);
                    }
                } else {
                    if (!message.sender_id.equals(util.getString("user_id", ""))) {
                        if (message.message_status == Constants.STATUS_MESSAGE_SENT) {
                            mFirebaseConfigMessages.child(message.chat_dialog_id).child(message.message_id).child("message_status").setValue(Constants.STATUS_MESSAGE_DELIVERED);
                        }
                    }
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    /////////////////////  Dialog Listener  //////////////////////

    //// Chat Fragment
    public interface ChatDialogsListenerInterface {
        void onDialogAdd(ChatsModel chat);

        void onDialogChanged(ChatsModel chat, int val);

        void onDialogRemoved(String dialogId);
    }

    public static ChatDialogsListenerInterface mChatDialogInterface;

    public static void setChatDialogListener(ChatDialogsListenerInterface listsner) {
        mChatDialogInterface = listsner;
    }

    //// Chat Activity
    public interface ChatDialogsListenerInterfaceForChat {
        void onDialogChanged(ChatsModel chat, int val);
    }

    public static ChatDialogsListenerInterfaceForChat mChatDialogInterfaceForChat;

    public static void setChatDialogListenerForChat(ChatDialogsListenerInterfaceForChat listsner, String dialogId) {
        mChatDialogInterfaceForChat = listsner;
        chatDialogId = dialogId;
    }

    public void removeChatsListener(String dialogId) {
        dialogId = dialogId.trim();
        Query mq = chatQuery.get(dialogId);
        if (mq != null && chatsListener.containsKey(dialogId)) {
            mq.removeEventListener(chatsListener.get(dialogId));
            chatsListener.remove(dialogId);
            chatQuery.remove(dialogId);
        }
    }

    public void setChatsListener(String dialogId) {
        dialogId = dialogId.trim();
        if (!chatsListener.containsKey(dialogId)) {
            chatsQuery = mFirebaseConfigChat.orderByKey().equalTo(dialogId);
            chatsQuery.addChildEventListener(mChatChildListener);
            chatsListener.put(dialogId, mChatChildListener);
            chatQuery.put(dialogId, chatsQuery);
        }
    }

    ChildEventListener mChatChildListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Log.e("chat added", "is " + dataSnapshot + ", " + s);
            ChatsModel chat = ChatsModel.parseChat(dataSnapshot, util.getString("user_id", ""));
            if (!TextUtils.isEmpty(chat.chat_dialog_id)) {
                if (chat.user_type.containsKey(util.getString("user_id", ""))) {
                    setListener(chat.chat_dialog_id);
//                    String otherUserID = "";
//                    for (String userId : chat.user_type.keySet()) {
//                        if (!userId.equals(util.getString("user_id", ""))) {
//                            otherUserID = userId;
//                            break;
//                        }
//                    }
                    mDb.addUpateChat(chat, util.getString("user_id", ""), chat.opponent_user_id);

                    if (mChatDialogInterface != null) {
                        mChatDialogInterface.onDialogAdd(chat);
                    }
                }
            } else {
                mFirebaseConfigProfile.child("Users").child("id_" + util.getString("user_id", "")).child("chat_dialog_ids").child(dataSnapshot.getKey());
                mFirebaseConfigProfile.removeValue();
                removeMessageListener(dataSnapshot.getKey());
                removeChatsListener(dataSnapshot.getKey());
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Log.e("chat changed", "is " + dataSnapshot + ", " + s);
            ChatsModel chat = ChatsModel.parseChat(dataSnapshot, util.getString("user_id", ""));
            if (!TextUtils.isEmpty(chat.chat_dialog_id)) {
                if (chat.user_type.containsKey(util.getString("user_id", ""))) {
                    setListener(chat.chat_dialog_id);
//                    String otherUserID = "";
//                    for (String userId : chat.user_type.keySet()) {
//                        if (!userId.equals(util.getString("user_id", ""))) {
//                            otherUserID = userId;
//                            break;
//                        }
//                    }
                    mDb.addUpateChat(chat, util.getString("user_id", ""), chat.opponent_user_id);
                    if (mChatDialogInterface != null) {
                        mChatDialogInterface.onDialogChanged(chat, 0);
                    }
                    if (mChatDialogInterfaceForChat != null) {
                        if (chatDialogId.equals(chat.chat_dialog_id)) {
                            mChatDialogInterfaceForChat.onDialogChanged(chat, 0);
                        }
                    }
                }
            } else {
                mDb.deleteDialogData(dataSnapshot.getKey());
                mFirebaseConfigProfile.child("Users").child("id_" + util.getString("user_id", "")).child("chat_dialog_ids").child(dataSnapshot.getKey());
                mFirebaseConfigProfile.removeValue();
                removeMessageListener(dataSnapshot.getKey());
                removeChatsListener(dataSnapshot.getKey());
                if (mChatDialogInterface != null) {
                    mChatDialogInterface.onDialogChanged(chat, 1);
                }
                if (mChatDialogInterfaceForChat != null) {
                    if (chatDialogId.equals(chat.chat_dialog_id)) {
                        mChatDialogInterfaceForChat.onDialogChanged(chat, 1);
                    }
                }
            }
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            mDb.deleteDialogData(dataSnapshot.getKey());
            removeMessageListener(dataSnapshot.getKey());
            removeChatsListener(dataSnapshot.getKey());
            if (mChatDialogInterface != null) {
                mChatDialogInterface.onDialogRemoved(dataSnapshot.getKey());
            }
            if (mChatDialogInterfaceForChat != null) {
                if (chatDialogId.equals(dataSnapshot.getKey())) {
                    mChatDialogInterfaceForChat.onDialogChanged(null, 1);
                }
            }
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    //////////////////////  Profile Listener  ////////////////////////

    public interface ProfileListenerInterface {
        void onProfileChanged(String value);
    }

    public static ProfileListenerInterface mProfileInterface;

    public static void setProfileDataListener(ProfileListenerInterface listsner) {
        mProfileInterface = listsner;
    }

    public void setProfileListener(String userId) {
        if (!profileListener.containsKey("id_" + userId)) {
            usersQuery = mFirebaseConfigProfile.orderByKey().equalTo("id_" + userId);
            usersQuery.addChildEventListener(mUserListener);
            profileListener.put(userId, mUserListener);
            profileQuery.put(userId, usersQuery);
        }
    }

    ChildEventListener mUserListener = new ChildEventListener() {

        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            ProfileModel user = ProfileModel.parseProfile(dataSnapshot);
            if (user.online_status != Constants.ONLINE_LONG) {
                if (util.getBoolean("forground", false)) {
                    mFirebaseConfigProfile.child(user.user_id).child("online_status").setValue(Constants.ONLINE_LONG);
                }
            }
            HashMap<String, String> dialogs = user.chat_dialog_ids;
            if (dialogs != null && dialogs.size() > 0) {
                mDb.addDialogs(dialogs, util.getString("user_id", ""));
                for (String userUniqueKey : dialogs.keySet()) {
                    setChatsListener(userUniqueKey);
                }
            }
            if (!user.access_token.equals(util.getString("access_token", ""))) {
                if (mProfileInterface != null) {
                    mProfileInterface.onProfileChanged("");
                }
            }
//            mDb.addBlockedByMeIds(user.blocked_by_me);
//            mDb.addBlockedByOtherIds(user.blocked_from_others);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            ProfileModel user = ProfileModel.parseProfile(dataSnapshot);
            if (user.online_status != Constants.ONLINE_LONG) {
                if (util.getBoolean("forground", false)) {
                    mFirebaseConfigProfile.child(user.user_id).child("online_status").setValue(Constants.ONLINE_LONG);
                }
            }
            HashMap<String, String> dialogs = user.chat_dialog_ids;
            if (dialogs != null && dialogs.size() > 0) {
                mDb.addDialogs(dialogs, util.getString("user_id", ""));
                for (String userUniqueKey : dialogs.keySet()) {
                    setChatsListener(userUniqueKey);
                }
            }

            if (!user.access_token.equals(util.getString("access_token", ""))) {
                if (mProfileInterface != null) {
                    mProfileInterface.onProfileChanged("");
                }
            }
//            mDb.addBlockedByMeIds(user.blocked_by_me);
//            mDb.addBlockedByOtherIds(user.blocked_from_others);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void RemoveAllListeners() {
        for (String ids : chatsListener.keySet()) {
            Query chat = chatQuery.get(ids.trim());
            chat.removeEventListener(chatsListener.get(ids.trim()));
        }
        chatsListener.clear();
        chatQuery.clear();

        for (String ids : messageListener.keySet()) {
            Query message = messageQuery.get(ids.trim());
            message.removeEventListener(messageListener.get(ids.trim()));
        }

        messageListener.clear();
        messageQuery.clear();

        for (String ids : profileListener.keySet()) {
            Query user = profileQuery.get(ids.trim());
            user.removeEventListener(profileListener.get(ids.trim()));
        }
        profileListener.clear();
        profileQuery.clear();

    }

    public void clearApplicationData(Context mCon) {
        mCon.deleteDatabase(Database.DATABASE);
        File cache = mCon.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "************** File /data/data/APP_PACKAGE/"
                            + s + " DELETED *****************");
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}