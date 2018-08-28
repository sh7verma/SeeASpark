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

public class ChatHolderReceiverNotes {

    public LinearLayout llReceiveNotes, llReceiveMessage;
    public ImageView imgFavouriteNotesReceive;
    public TextView txtMessage, txtTime;
    int mWidth;

    public ChatHolderReceiverNotes(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llReceiveNotes = (LinearLayout) view.findViewById(R.id.llReceiveNotes);

        imgFavouriteNotesReceive = (ImageView) view.findViewById(R.id.imgFavouriteNotesReceive);

        llReceiveMessage = (LinearLayout) view.findViewById(R.id.llReceiveMessage);

        txtMessage = (TextView) view.findViewById(R.id.txtMessage);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

    }

    public void bindHolder(Context mContext, MessagesModel mMessage, String userId, String name) {

        txtMessage.setText(name + " " + mContext.getString(R.string.shared_notes));

        txtTime.setText(mMessage.show_message_datetime);

        if (mMessage.favourite_message.get(userId).equals("0")) {
            imgFavouriteNotesReceive.setImageResource(R.mipmap.ic_heart);
        } else {
            imgFavouriteNotesReceive.setImageResource(R.mipmap.ic_heart_red);
        }

    }

}
