package holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seeaspark.R;

public class ChatHolderSenderDocument {

    public LinearLayout llSentDocumnet, llSentMessage;
    public ImageView imgFavouriteDocumnetSent, imgRead;
    public TextView txtMessage, txtTime;
    int mWidth;

    public ChatHolderSenderDocument(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llSentDocumnet = (LinearLayout) view.findViewById(R.id.llSentDocumnet);

        imgFavouriteDocumnetSent = (ImageView) view.findViewById(R.id.imgFavouriteDocumnetSent);

        llSentMessage = (LinearLayout) view.findViewById(R.id.llSentMessage);

        txtMessage = (TextView) view.findViewById(R.id.txtMessage);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

        imgRead = (ImageView) view.findViewById(R.id.imgRead);

    }

    public void bindHolder(Context mContext) {

//        txtTime.setText(mMessage.show_message_datetime);

//        if (TextUtils.isEmpty(opponentIDs)) {
//            if (mMessage.read_ids.size() > 0) {
//                sent_time_img1.setImageResource(R.drawable.ic_seen);
//            } else if (mMessage.deliver_ids.size() > 0) {
//                sent_time_img1.setImageResource(R.drawable.ic_delivered);
//            } else {
//                sent_time_img1.setImageResource(R.drawable.ic_delivered);
//            }
//        } else {
//            sent_time_img1.setVisibility(View.VISIBLE);
//            if (mMessage.read_ids.contains(opponentIDs)) {
//                sent_time_img1.setImageResource(R.drawable.ic_seen);
//            } else if (mMessage.deliver_ids.contains(opponentIDs)) {
//                sent_time_img1.setImageResource(R.drawable.ic_delivered);
//            } else {
//                if (TextUtils.isEmpty(mMessage.message_status)) {
//                    sent_time_img1.setImageResource(R.drawable.ic_delivered);
//                } else if (mMessage.message_status.equals("" + Consts.STATUS_MESSAGE_PENDING)) {
//                    sent_time_img1.setImageResource(R.drawable.ic_delivered);
//                } else if (mMessage.message_status.equals("" + Consts.STATUS_MESSAGE_SENT)) {
//                    sent_time_img1.setImageResource(R.drawable.ic_delivered);
//                }
//            }
//        }

    }

}
