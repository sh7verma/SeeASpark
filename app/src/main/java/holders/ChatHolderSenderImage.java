package holders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.seeaspark.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import customviews.CircularProgressBar;
import customviews.RoundedTransformation;
import jp.wasabeef.blurry.Blurry;
import models.MessagesModel;
import utils.Constants;

public class ChatHolderSenderImage {

    public LinearLayout llSentImage;
    public ImageView imgImageSent, imgFavouriteImageSent, imgRead, imgUpload;
    public TextView txtTime;
    public RelativeLayout rlSentMessage;
    private CircularProgressBar cpbProgress;
    private int imageWidth;

    public ChatHolderSenderImage(Context con, View view, int mWidth) {

        imageWidth = (int) (mWidth * 0.72)-4;

        llSentImage = (LinearLayout) view.findViewById(R.id.llSentImage);

        imgFavouriteImageSent = (ImageView) view.findViewById(R.id.imgFavouriteImageSent);

        LinearLayout.LayoutParams relativePam = new LinearLayout.LayoutParams((int) (mWidth * 0.72), (int) (mWidth * 0.72));
        rlSentMessage = (RelativeLayout) view.findViewById(R.id.rlSentMessage);
        rlSentMessage.setLayoutParams(relativePam);

        RelativeLayout.LayoutParams imageParms = new RelativeLayout.LayoutParams(imageWidth, imageWidth);
        imageParms.addRule(RelativeLayout.CENTER_IN_PARENT);
        imgImageSent = (ImageView) view.findViewById(R.id.imgImageSent);
        imgImageSent.setLayoutParams(imageParms);

        RelativeLayout.LayoutParams cpbParams = new RelativeLayout.LayoutParams((mWidth / 5), (mWidth / 5));
        cpbParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cpbProgress = (CircularProgressBar) view.findViewById(R.id.cpbProgress);
        cpbProgress.setLayoutParams(cpbParams);

        imgUpload = (ImageView) view.findViewById(R.id.imgUpload);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

        imgRead = (ImageView) view.findViewById(R.id.imgRead);

    }

    public void bindHolder(Context mContext, MessagesModel mMessage, String userId) {

        if (TextUtils.isEmpty(mMessage.attachment_path)) {
            //attachment not download yet
            if (!TextUtils.isEmpty(mMessage.attachment_url)) {
                Picasso.with(mContext).load(mMessage.attachment_url)
                        .resize(imageWidth, imageWidth)
                        .centerCrop()
                        .transform(new RoundedTransformation(Constants.dpToPx(8), 0))
                        .into(imgImageSent);
            }
            cpbProgress.setVisibility(View.GONE);
            imgUpload.setVisibility(View.GONE);
        } else {
            File file = new File(mMessage.attachment_path);
            if (file.exists()) {
                Picasso.with(mContext).load(file)
                        .resize(imageWidth, imageWidth)
                        .centerCrop()
                        .transform(new RoundedTransformation(Constants.dpToPx(8), 0))
                        .into(imgImageSent);
            } else {
                if (!TextUtils.isEmpty(mMessage.attachment_url)) {
                    Picasso.with(mContext).load(mMessage.attachment_url)
                            .resize(imageWidth, imageWidth)
                            .centerCrop()
                            .transform(new RoundedTransformation(Constants.dpToPx(8), 0))
                            .into(imgImageSent);
                }
            }
            switch (mMessage.attachment_status) {
                case "" + Constants.FILE_UPLOADING:
                    imgUpload.setVisibility(View.GONE);
                    cpbProgress.setVisibility(View.VISIBLE);
                    cpbProgress.setProgress(Integer.parseInt(mMessage.attachment_progress));
                    break;
                case "" + Constants.FILE_EREROR:
                    imgUpload.setVisibility(View.VISIBLE);
                    cpbProgress.setVisibility(View.GONE);
                    break;
                default: //Success
                    imgUpload.setVisibility(View.GONE);
                    cpbProgress.setVisibility(View.GONE);
                    break;
            }
        }

        txtTime.setText(mMessage.show_message_datetime);

        if (mMessage.favourite_message.get(userId).equals("0")) {
            imgFavouriteImageSent.setImageResource(R.mipmap.ic_heart);
        } else {
            imgFavouriteImageSent.setImageResource(R.mipmap.ic_heart_red);
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
