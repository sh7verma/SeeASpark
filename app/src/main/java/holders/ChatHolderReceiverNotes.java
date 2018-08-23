package holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seeaspark.R;

/**
 * Created by dev on 30/7/18.
 */

public class ChatHolderReceiverNotes {

    public LinearLayout llReceiveNotes, llReceiveMessage;
    public ImageView imgFavouriteNotesReceive, imgRead;
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

        imgRead = (ImageView) view.findViewById(R.id.imgRead);
    }

    public void bindHolder(Context mContext) {

//        txtTime.setText(mMessage.show_message_datetime);

    }

}
