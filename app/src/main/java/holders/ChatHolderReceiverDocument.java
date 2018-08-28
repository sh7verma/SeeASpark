package holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seeaspark.R;

import models.MessagesModel;

public class ChatHolderReceiverDocument {

    public LinearLayout llReceiveDocument, llReceiveMessage;
    public ImageView imgFavouriteDocumentReceive;
    public TextView txtMessage, txtTime;
    int mWidth;

    public ChatHolderReceiverDocument(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llReceiveDocument = (LinearLayout) view.findViewById(R.id.llReceiveDocument);

        imgFavouriteDocumentReceive = (ImageView) view.findViewById(R.id.imgFavouriteDocumentReceive);

        llReceiveMessage = (LinearLayout) view.findViewById(R.id.llReceiveMessage);

        txtMessage = (TextView) view.findViewById(R.id.txtMessage);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

    }

    public void bindHolder(Context mContext, MessagesModel mMessage, String userId, String name) {

        txtMessage.setText(name + " " + mContext.getString(R.string.shared_document));

        txtTime.setText(mMessage.show_message_datetime);

        if (mMessage.favourite_message.get(userId).equals("0")) {
            imgFavouriteDocumentReceive.setImageResource(R.mipmap.ic_heart);
        } else {
            imgFavouriteDocumentReceive.setImageResource(R.mipmap.ic_heart_red);
        }

    }
}
