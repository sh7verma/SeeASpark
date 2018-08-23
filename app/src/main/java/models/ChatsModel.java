package models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by app on 8/2/2016.
 */
public class ChatsModel implements Serializable {
    public String chat_dialog_id;
    public String last_message;
    public Long last_message_time;
    public String last_message_sender_id;
    public String last_message_id;
    public String last_message_type;
    public String participant_ids;
    public HashMap<String, Integer> unread_count;
    public HashMap<String, String> name;
    public HashMap<String, String> profile_pic;
    public HashMap<String, Long> delete_dialog_time;
    public HashMap<String, String> block_status;
    public HashMap<String, String> user_type;
    public String opponent_user_id;

    public static ChatsModel parseChat(DataSnapshot dataSnapshot, String userId) {
//        ChatsModel chat = dataSnapshot.getValue(ChatsModel.class);
        ChatsModel chat = new ChatsModel();
        chat.chat_dialog_id = dataSnapshot.child("chat_dialog_id").getValue(String.class);
        chat.last_message = dataSnapshot.child("last_message").getValue(String.class);
        chat.last_message_time = dataSnapshot.child("last_message_time").getValue(Long.class);
        chat.last_message_sender_id = dataSnapshot.child("last_message_sender_id").getValue(String.class);
        chat.last_message_id = dataSnapshot.child("last_message_id").getValue(String.class);
        chat.last_message_type = dataSnapshot.child("last_message_type").getValue(String.class);
        chat.participant_ids = dataSnapshot.child("participant_ids").getValue(String.class);

        GenericTypeIndicator<HashMap<String, Integer>> gtUnread = new GenericTypeIndicator<HashMap<String, Integer>>() {
        };
        chat.unread_count = dataSnapshot.child("unread_count").getValue(gtUnread);

        GenericTypeIndicator<HashMap<String, String>> gtName = new GenericTypeIndicator<HashMap<String, String>>() {
        };
        chat.name = dataSnapshot.child("name").getValue(gtName);

        GenericTypeIndicator<HashMap<String, String>> gtPic = new GenericTypeIndicator<HashMap<String, String>>() {
        };
        chat.profile_pic = dataSnapshot.child("profile_pic").getValue(gtPic);

        GenericTypeIndicator<HashMap<String, Long>> gtDelete = new GenericTypeIndicator<HashMap<String, Long>>() {
        };
        chat.delete_dialog_time = dataSnapshot.child("delete_dialog_time").getValue(gtDelete);

        GenericTypeIndicator<HashMap<String, String>> gtBlock = new GenericTypeIndicator<HashMap<String, String>>() {
        };
        chat.block_status = dataSnapshot.child("block_status").getValue(gtBlock);

        GenericTypeIndicator<HashMap<String, String>> gtType = new GenericTypeIndicator<HashMap<String, String>>() {
        };
        chat.user_type = dataSnapshot.child("user_type").getValue(gtType);

        String otherUserId = "";
        for (String id : chat.user_type.keySet()) {
            if (!id.equals(userId)) {
                otherUserId = id;
            }
        }
        chat.opponent_user_id = otherUserId;

        return chat;
    }
}
