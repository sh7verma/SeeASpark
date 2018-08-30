package holders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seeaspark.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import customviews.CircularProgressBar;
import customviews.RoundedTransformation;
import models.MessagesModel;
import utils.Constants;

public class ChatHolderReceiverDocument {

    public LinearLayout llReceiveDocument, llReceiveMessage;
    public ImageView imgFavouriteDocumentReceive, imgDocument, imgDownload;
    public TextView txtMessage, txtTime;
    int mWidth;
    CircularProgressBar cpbProgress;

    public ChatHolderReceiverDocument(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llReceiveDocument = (LinearLayout) view.findViewById(R.id.llReceiveDocument);

        imgFavouriteDocumentReceive = (ImageView) view.findViewById(R.id.imgFavouriteDocumentReceive);

        imgDocument = (ImageView) view.findViewById(R.id.imgDocument);

        imgDownload = (ImageView) view.findViewById(R.id.imgDownload);

        RelativeLayout.LayoutParams cpbParams = new RelativeLayout.LayoutParams((mWidth / 9), (mWidth / 9));
        cpbParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cpbProgress = (CircularProgressBar) view.findViewById(R.id.cpbProgress);
        cpbProgress.setLayoutParams(cpbParams);

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

        if (TextUtils.isEmpty(mMessage.attachment_path)) {
            //attachment not download yet
            if (mMessage.attachment_status.equals("" + Constants.FILE_UPLOADING)) {
                imgDocument.setVisibility(View.GONE);
                imgDownload.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.VISIBLE);
                cpbProgress.setProgress(Integer.parseInt(mMessage.attachment_progress));
            } else if (mMessage.attachment_status.equals("" + Constants.FILE_EREROR)) {
                imgDownload.setVisibility(View.VISIBLE);
                cpbProgress.setVisibility(View.GONE);
                imgDocument.setVisibility(View.GONE);
            } else {//Success
                imgDownload.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.GONE);
                imgDocument.setVisibility(View.VISIBLE);
            }
        } else {
            cpbProgress.setVisibility(View.GONE);
            imgDownload.setVisibility(View.GONE);
            imgDocument.setVisibility(View.VISIBLE);
        }

    }
}
