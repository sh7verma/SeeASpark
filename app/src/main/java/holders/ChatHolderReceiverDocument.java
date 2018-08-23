package holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seeaspark.R;

public class ChatHolderReceiverDocument {

    public LinearLayout llReceiveDocument, llReceiveMessage;
    public ImageView imgFavouriteDocumentReceive, imgRead;
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

        imgRead = (ImageView) view.findViewById(R.id.imgRead);
    }

    public void bindHolder(Context mContext) {

//        txtTime.setText(mMessage.show_message_datetime);

    }
}
