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
public class ChatHolderReceiverVideo {

    public LinearLayout llReceiveVideo;
    public ImageView imgVideoReceive, imgFavouriteVideoReceive, imgDownload, imgPlayVideoReceive;
    public TextView txtTime;
    public RelativeLayout rlReceiveMessage;
    private CircularProgressBar cpbProgress;
    private int imageWidth;

    public ChatHolderReceiverVideo(Context con, View view, int mWidth) {
        // TODO Auto-generated constructor stub

        imageWidth = (int) (mWidth * 0.72) - 4;

        llReceiveVideo = (LinearLayout) view.findViewById(R.id.llReceiveVideo);

        imgFavouriteVideoReceive = (ImageView) view.findViewById(R.id.imgFavouriteVideoReceive);

        LinearLayout.LayoutParams relativePam = new LinearLayout.LayoutParams((int) (mWidth * 0.72), (int) (mWidth * 0.72));
        rlReceiveMessage = (RelativeLayout) view.findViewById(R.id.rlReceiveMessage);
        rlReceiveMessage.setLayoutParams(relativePam);

        RelativeLayout.LayoutParams imageParms = new RelativeLayout.LayoutParams(imageWidth, imageWidth);
        imageParms.addRule(RelativeLayout.CENTER_IN_PARENT);
        imgVideoReceive = (ImageView) view.findViewById(R.id.imgVideoReceive);
        imgVideoReceive.setLayoutParams(imageParms);

        RelativeLayout.LayoutParams cpbParams = new RelativeLayout.LayoutParams((mWidth / 5), (mWidth / 5));
        cpbParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cpbProgress = (CircularProgressBar) view.findViewById(R.id.cpbProgress);
        cpbProgress.setLayoutParams(cpbParams);

        imgDownload = (ImageView) view.findViewById(R.id.imgDownload);

        imgPlayVideoReceive = (ImageView) view.findViewById(R.id.imgPlayVideoReceive);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

    }

    public void bindHolder(Context mContext, MessagesModel mMessage, String userId) {

        txtTime.setText(mMessage.show_message_datetime);

        if (mMessage.favourite_message.get(userId).equals("0")) {
            imgFavouriteVideoReceive.setImageResource(R.mipmap.ic_heart);
        } else {
            imgFavouriteVideoReceive.setImageResource(R.mipmap.ic_heart_red);
        }

        if (TextUtils.isEmpty(mMessage.attachment_path)) {
            //attachment not download yet
            if (!TextUtils.isEmpty(mMessage.custom_data)) {
                File file = new File(mMessage.custom_data);
                if (file.exists()) {
                    Picasso.with(mContext).load(file).resize(imageWidth, imageWidth)
                            .centerCrop()
                            .transform(new RoundedTransformation(Constants.dpToPx(8), 0))
                            .into(imgVideoReceive);
                }
            }
            if (mMessage.attachment_status.equals("" + Constants.FILE_UPLOADING)) {
                imgDownload.setVisibility(View.GONE);
                imgPlayVideoReceive.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.VISIBLE);
                cpbProgress.setProgress(Integer.parseInt(mMessage.attachment_progress));
            } else if (mMessage.attachment_status.equals("" + Constants.FILE_EREROR)) {
                imgDownload.setVisibility(View.VISIBLE);
                cpbProgress.setVisibility(View.GONE);
                imgPlayVideoReceive.setVisibility(View.GONE);
            } else {//Success
                imgDownload.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.GONE);
                imgPlayVideoReceive.setVisibility(View.VISIBLE);
            }
        } else {
            File file = new File(mMessage.custom_data);
            if (file.exists()) {
                Picasso.with(mContext).load(file)
                        .resize(imageWidth, imageWidth)
                        .centerCrop()
                        .transform(new RoundedTransformation(Constants.dpToPx(8), 0))
                        .into(imgVideoReceive);
            }
            cpbProgress.setVisibility(View.GONE);
            imgDownload.setVisibility(View.GONE);
            imgPlayVideoReceive.setVisibility(View.VISIBLE);
        }
    }
}