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

public class ChatHolderReceiverImage {

    public LinearLayout llReceiveImage;
    public ImageView imgImageReceive, imgFavouriteImageReceive, imgDownload;
    public TextView txtTime;
    public RelativeLayout rlReceiveMessage;
    CircularProgressBar cpbProgress;
    int mWidth;

    public ChatHolderReceiverImage(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llReceiveImage = (LinearLayout) view.findViewById(R.id.llReceiveImage);

        imgFavouriteImageReceive = (ImageView) view.findViewById(R.id.imgFavouriteImageReceive);

        LinearLayout.LayoutParams relativePam = new LinearLayout.LayoutParams((int) (mWidth * 0.72), (int) (mWidth * 0.72));
        rlReceiveMessage = (RelativeLayout) view.findViewById(R.id.rlReceiveMessage);
        rlReceiveMessage.setLayoutParams(relativePam);

        imgImageReceive = (ImageView) view.findViewById(R.id.imgImageReceive);

        RelativeLayout.LayoutParams cpbParams = new RelativeLayout.LayoutParams((mWidth / 5), (mWidth / 5));
        cpbParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cpbProgress = (CircularProgressBar) view.findViewById(R.id.cpbProgress);
        cpbProgress.setLayoutParams(cpbParams);

        imgDownload = (ImageView) view.findViewById(R.id.imgDownload);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

    }

    public void bindHolder(Context mContext, MessagesModel mMessage, String userId) {

        txtTime.setText(mMessage.show_message_datetime);

        if (mMessage.favourite_message.get(userId).equals("0")) {
            imgFavouriteImageReceive.setImageResource(R.mipmap.ic_heart);
        } else {
            imgFavouriteImageReceive.setImageResource(R.mipmap.ic_heart_red);
        }

        if (TextUtils.isEmpty(mMessage.attachment_path)) {
            //attachment not download yet
            if (!TextUtils.isEmpty(mMessage.attachment_url)) {
                Picasso.with(mContext).load(mMessage.attachment_url).resize((int) (mWidth * 0.72)-1, (int) (mWidth * 0.72)-1).centerCrop().transform(new RoundedTransformation(10, 0)).into(imgImageReceive);
            }
            if (mMessage.attachment_status.equals("" + Constants.FILE_UPLOADING)) {
                imgDownload.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.VISIBLE);
                cpbProgress.setProgress(Integer.parseInt(mMessage.attachment_progress));
            } else if (mMessage.attachment_status.equals("" + Constants.FILE_EREROR)) {
                imgDownload.setVisibility(View.VISIBLE);
                cpbProgress.setVisibility(View.GONE);
            } else {//Success
                imgDownload.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.GONE);
            }
        } else {
            File file = new File(mMessage.attachment_path);
            if (file.exists()) {
                Picasso.with(mContext).load(file).resize((int) (mWidth * 0.72)-1, (int) (mWidth * 0.72)-1).centerCrop().transform(new RoundedTransformation(10, 0)).into(imgImageReceive);
            } else {
                if (!TextUtils.isEmpty(mMessage.attachment_url)) {
                    Picasso.with(mContext).load(mMessage.attachment_url).resize((int) (mWidth * 0.72)-1, (int) (mWidth * 0.72)-1).centerCrop().transform(new RoundedTransformation(10, 0)).into(imgImageReceive);
                }
            }
            cpbProgress.setVisibility(View.GONE);
            imgDownload.setVisibility(View.GONE);
        }
    }
}
