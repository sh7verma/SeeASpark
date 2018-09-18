package holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seeaspark.R;

import models.MessagesModel;
import utils.Constants;

/**
 * Created by dev on 30/7/18.
 */

public class ChatHolderSenderNotes {

    public LinearLayout llSentNotes, llSentMessage;
    public ImageView imgFavouriteNotesSent, imgRead;
    public TextView txtMessage, txtTime;
    int mWidth;

    public ChatHolderSenderNotes(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llSentNotes = (LinearLayout) view.findViewById(R.id.llSentNotes);

        imgFavouriteNotesSent = (ImageView) view.findViewById(R.id.imgFavouriteNotesSent);

        llSentMessage = (LinearLayout) view.findViewById(R.id.llSentMessage);

        txtMessage = (TextView) view.findViewById(R.id.txtMessage);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

        imgRead = (ImageView) view.findViewById(R.id.imgRead);

    }

    public void bindHolder(Context mContext, MessagesModel mMessage, String userId, String name) {

        txtMessage.setText(mContext.getString(R.string.you_shared_notes) + " " + name);

        txtTime.setText(mMessage.show_message_datetime);

        if (mMessage.favourite_message.get(userId).equals("0")) {
            imgFavouriteNotesSent.setImageResource(R.mipmap.ic_heart);
        } else {
            imgFavouriteNotesSent.setImageResource(R.mipmap.ic_heart_red);
        }

        if (mMessage.message_status == Constants.STATUS_MESSAGE_SENT) {
            imgRead.setImageResource(R.mipmap.ic_sent);
        } else if (mMessage.message_status == Constants.STATUS_MESSAGE_DELIVERED) {
            imgRead.setImageResource(R.mipmap.ic_delivered);
        } else if (mMessage.message_status == Constants.STATUS_MESSAGE_SEEN) {
            imgRead.setImageResource(R.mipmap.ic_seen);
        } else {
            imgRead.setImageResource(R.mipmap.ic_message_pending);
        }

    }

}
