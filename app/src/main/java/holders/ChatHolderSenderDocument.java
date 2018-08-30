package holders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seeaspark.R;

import customviews.CircularProgressBar;
import models.MessagesModel;
import utils.Constants;

public class ChatHolderSenderDocument {

    public LinearLayout llSentDocumnet, llSentMessage;
    public ImageView imgFavouriteDocumnetSent, imgDocument, imgUpload, imgRead;
    public TextView txtMessage, txtTime;
    int mWidth;
    CircularProgressBar cpbProgress;

    public ChatHolderSenderDocument(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llSentDocumnet = (LinearLayout) view.findViewById(R.id.llSentDocumnet);

        imgFavouriteDocumnetSent = (ImageView) view.findViewById(R.id.imgFavouriteDocumnetSent);

        imgDocument = (ImageView) view.findViewById(R.id.imgDocument);

        imgUpload = (ImageView) view.findViewById(R.id.imgUpload);

        RelativeLayout.LayoutParams cpbParams = new RelativeLayout.LayoutParams((mWidth / 9), (mWidth / 9));
        cpbParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cpbProgress = (CircularProgressBar) view.findViewById(R.id.cpbProgress);
        cpbProgress.setLayoutParams(cpbParams);

        llSentMessage = (LinearLayout) view.findViewById(R.id.llSentMessage);

        txtMessage = (TextView) view.findViewById(R.id.txtMessage);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

        imgRead = (ImageView) view.findViewById(R.id.imgRead);

    }

    public void bindHolder(Context mContext, MessagesModel mMessage, String userId, String name) {

        if (TextUtils.isEmpty(mMessage.attachment_url)) {
            //attachment not download yet
            if (mMessage.attachment_status.equals("" + Constants.FILE_UPLOADING)) {
                imgDocument.setVisibility(View.GONE);
                imgUpload.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.VISIBLE);
                cpbProgress.setProgress(Integer.parseInt(mMessage.attachment_progress));
            } else if (mMessage.attachment_status.equals("" + Constants.FILE_EREROR)) {
                imgDocument.setVisibility(View.GONE);
                imgUpload.setVisibility(View.VISIBLE);
                cpbProgress.setVisibility(View.GONE);
            } else {//Success
                imgDocument.setVisibility(View.VISIBLE);
                cpbProgress.setVisibility(View.GONE);
                imgUpload.setVisibility(View.GONE);
            }
        } else {
            cpbProgress.setVisibility(View.GONE);
            imgDocument.setVisibility(View.VISIBLE);
            imgUpload.setVisibility(View.GONE);
        }

        txtMessage.setText(mContext.getString(R.string.you_shared_document) + " " + name);

        txtTime.setText(mMessage.show_message_datetime);

        if (mMessage.favourite_message.get(userId).equals("0")) {
            imgFavouriteDocumnetSent.setImageResource(R.mipmap.ic_heart);
        } else {
            imgFavouriteDocumnetSent.setImageResource(R.mipmap.ic_heart_red);
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
