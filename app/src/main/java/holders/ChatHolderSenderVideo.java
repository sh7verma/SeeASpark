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

/**
 * Created by Applify on 11/9/2016.
 */
public class ChatHolderSenderVideo {

    public LinearLayout llSentVideo;
    public ImageView imgVideoSent, imgFavouriteVideoSent, imgRead, imgUpload, imgPlayVideoSent;
    public TextView txtTime;
    public RelativeLayout rlSentMessage;
    private CircularProgressBar cpbProgress;
    private int imageWidth;

    public ChatHolderSenderVideo(Context con, View view, int mWidth) {
        // TODO Auto-generated constructor stub

        imageWidth = (int) (mWidth * 0.72)-4;

        llSentVideo = (LinearLayout) view.findViewById(R.id.llSentVideo);

        imgFavouriteVideoSent = (ImageView) view.findViewById(R.id.imgFavouriteVideoSent);

        LinearLayout.LayoutParams relativePam = new LinearLayout.LayoutParams((int) (mWidth * 0.72), (int) (mWidth * 0.72));
        rlSentMessage = (RelativeLayout) view.findViewById(R.id.rlSentMessage);
        rlSentMessage.setLayoutParams(relativePam);

        RelativeLayout.LayoutParams imageParms = new RelativeLayout.LayoutParams(imageWidth, imageWidth);
        imageParms.addRule(RelativeLayout.CENTER_IN_PARENT);
        imgVideoSent = (ImageView) view.findViewById(R.id.imgVideoSent);
        imgVideoSent.setLayoutParams(imageParms);

        RelativeLayout.LayoutParams cpbParams = new RelativeLayout.LayoutParams((mWidth / 5), (mWidth / 5));
        cpbParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cpbProgress = (CircularProgressBar) view.findViewById(R.id.cpbProgress);
        cpbProgress.setLayoutParams(cpbParams);

        imgUpload = (ImageView) view.findViewById(R.id.imgUpload);

        imgPlayVideoSent = (ImageView) view.findViewById(R.id.imgPlayVideoSent);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

        imgRead = (ImageView) view.findViewById(R.id.imgRead);

    }

    public void bindHolder(Context mContext, MessagesModel mMessage, String userId) {

        if (TextUtils.isEmpty(mMessage.attachment_path)) {
            //attachment not download yet
            if (!TextUtils.isEmpty(mMessage.custom_data)) {
                File file = new File(mMessage.custom_data);
                if (file.exists()) {
                    Picasso.with(mContext).load(file)
                            .resize(imageWidth, imageWidth)
                            .centerCrop()
                            .transform(new RoundedTransformation(Constants.dpToPx(8), 0))
                            .into(imgVideoSent);
                }
            }
            imgPlayVideoSent.setVisibility(View.VISIBLE);
            cpbProgress.setVisibility(View.GONE);
            imgUpload.setVisibility(View.GONE);
        } else {
            File file = new File(mMessage.custom_data);
            if (file.exists()) {
                Picasso.with(mContext).load(file).resize(imageWidth, imageWidth)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_image)
                        .transform(new RoundedTransformation(Constants.dpToPx(8), 0))
                        .into(imgVideoSent);
            }
            if (mMessage.attachment_status.equals("" + Constants.FILE_UPLOADING)) {
                imgUpload.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.VISIBLE);
                cpbProgress.setProgress(Integer.parseInt(mMessage.attachment_progress));
                imgPlayVideoSent.setVisibility(View.GONE);
            } else if (mMessage.attachment_status.equals("" + Constants.FILE_EREROR)) {
                imgUpload.setVisibility(View.VISIBLE);
                cpbProgress.setVisibility(View.GONE);
                imgPlayVideoSent.setVisibility(View.GONE);
            } else {//Success
                imgUpload.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.GONE);
                imgPlayVideoSent.setVisibility(View.VISIBLE);
            }
        }

        txtTime.setText(mMessage.show_message_datetime);

        if (mMessage.favourite_message.get(userId).equals("0")) {
            imgFavouriteVideoSent.setImageResource(R.mipmap.ic_heart);
        } else {
            imgFavouriteVideoSent.setImageResource(R.mipmap.ic_heart_red);
        }

        if (mMessage.message_status == Constants.STATUS_MESSAGE_SENT) {
            imgRead.setImageResource(R.mipmap.ic_sent);
        } else if (mMessage.message_status == Constants.STATUS_MESSAGE_DELIVERED) {
            imgRead.setImageResource(R.mipmap.ic_delivered);
        } else if (mMessage.message_status == Constants.STATUS_MESSAGE_SEEN) {
            imgRead.setImageResource(R.mipmap.ic_seen);
        } else if (mMessage.message_status == Constants.STATUS_MESSAGE_PENDING) {
            imgRead.setImageResource(R.mipmap.ic_message_pending);
        }

    }

}