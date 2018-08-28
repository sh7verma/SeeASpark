package models;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.GenericTypeIndicator;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Applify on 8/17/2016.
 */
public class ProfileModel implements Serializable {

    private static final long serialVersionUID = 1L;

    public String user_id;
    public Long online_status;
    public String access_token;
    public String user_name;
    public String user_pic;
    public HashMap<String, String> chat_dialog_ids;
//    public HashMap<String, String> blocked_by_me;
//    public HashMap<String, String> blocked_by_others;

    public static ProfileModel parseProfile(DataSnapshot dataSnapshot) {

//        ProfileModel profile = dataSnapshot.getValue(ProfileModel.class);
        ProfileModel profile = new ProfileModel();
        profile.user_id = dataSnapshot.child("user_id").getValue(String.class);
        profile.online_status = dataSnapshot.child("online_status").getValue(Long.class);
        profile.access_token = dataSnapshot.child("access_token").getValue(String.class);
        profile.user_name = dataSnapshot.child("user_name").getValue(String.class);
        profile.user_pic = dataSnapshot.child("user_pic").getValue(String.class);
        GenericTypeIndicator<HashMap<String, String>> gtDialogs = new GenericTypeIndicator<HashMap<String, String>>() {
        };
        profile.chat_dialog_ids = dataSnapshot.child("chat_dialog_ids").getValue(gtDialogs);
//        profile.blocked_by_me = dataSnapshot.child("blocked_by_me").getValue(HashMap.class);
//        profile.blocked_by_others = dataSnapshot.child("blocked_by_others").getValue(HashMap.class);
        return profile;
    }

}