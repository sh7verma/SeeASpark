package models;

import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.io.Serializable;
import java.util.HashMap;

import utils.Constants;

/**
 * Created by app on 8/2/2016.
 */
public class MessagesModel implements Serializable {

    public String message_id;
    public String message;
    public String message_type;
    public String message_time;
    public Long firebase_message_time;
    public String chat_dialog_id;
    public String sender_id;
    public int message_status;
    public String attachment_url;
    public HashMap<String, String> message_deleted;
    public HashMap<String, String> favourite_message;
    public boolean is_header;
    public String attachment_progress;
    public String attachment_path;
    public String attachment_status;
    public String show_message_datetime;
    public String show_header_text;

    public static MessagesModel parseMessage(DataSnapshot dataSnapshot) {
//        MessagesModel msg = dataSnapshot.getValue(MessagesModel.class);
        MessagesModel msg = new MessagesModel();
        msg.message_id = dataSnapshot.child("message_id").getValue(String.class);
        msg.message = dataSnapshot.child("message").getValue(String.class);
        msg.message_type = dataSnapshot.child("message_type").getValue(String.class);
        long time = Constants.getLocalTime(Long.parseLong(dataSnapshot.child("message_time").getValue(String.class)));
        msg.message_time = "" + time;  //  Comment for show local time
        msg.firebase_message_time = dataSnapshot.child("firebase_message_time").getValue(Long.class);
        msg.chat_dialog_id = dataSnapshot.child("chat_dialog_id").getValue(String.class);
        msg.sender_id = dataSnapshot.child("sender_id").getValue(String.class);
        if (TextUtils.isEmpty(dataSnapshot.child("message_status").getValue(String.class))) {
            msg.message_status = Constants.STATUS_MESSAGE_SENT;
        } else {
            msg.message_status = dataSnapshot.child("message_status").getValue(Integer.class);
        }
        msg.attachment_url = dataSnapshot.child("attachment_url").getValue(String.class);

        GenericTypeIndicator<HashMap<String, String>> gtDelete = new GenericTypeIndicator<HashMap<String, String>>() {
        };
        msg.message_deleted = dataSnapshot.child("message_deleted").getValue(gtDelete);

        GenericTypeIndicator<HashMap<String, String>> gtFavourite = new GenericTypeIndicator<HashMap<String, String>>() {
        };
        msg.favourite_message = dataSnapshot.child("favourite_message").getValue(gtFavourite);

        msg.is_header = false;
        msg.attachment_progress = "0";
        msg.attachment_path = "";
        msg.attachment_status = "";
        msg.show_message_datetime = "";
        msg.show_header_text = "";

        return msg;
    }

}
