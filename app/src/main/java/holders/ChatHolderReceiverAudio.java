package holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.seeaspark.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import customviews.CircularProgressBar;
import customviews.RoundedTransformation;
import models.MessagesModel;
import utils.Constants;


public class ChatHolderReceiverAudio {

    public LinearLayout llReceiveAudio, llReceiveMessage;
    public ImageView imgFavouriteAudioReceive, imgDownload, imgPlay, imgReadInvisible;
    public TextView txtTimeInvisible, txtTime, txtAudioLength;
    CircularProgressBar cpbProgress;
    public SeekBar audioSeekReceive;
    int mWidth;
    int id = R.mipmap.ic_play_black;

    public ChatHolderReceiverAudio(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llReceiveAudio = (LinearLayout) view.findViewById(R.id.llReceiveAudio);

        imgFavouriteAudioReceive = (ImageView) view.findViewById(R.id.imgFavouriteAudioReceive);

        llReceiveMessage = (LinearLayout) view.findViewById(R.id.llReceiveMessage);

        imgDownload = (ImageView) view.findViewById(R.id.imgDownload);

        imgPlay = (ImageView) view.findViewById(R.id.imgPlay);
        imgPlay.setTag(id);

        RelativeLayout.LayoutParams cpbParams = new RelativeLayout.LayoutParams((mWidth / 9), (mWidth / 9));
        cpbParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cpbProgress = (CircularProgressBar) view.findViewById(R.id.cpbProgress);
        cpbProgress.setLayoutParams(cpbParams);

        txtTimeInvisible = (TextView) view.findViewById(R.id.txtTimeInvisible);

        imgReadInvisible = (ImageView) view.findViewById(R.id.imgReadInvisible);

        audioSeekReceive = (SeekBar) view.findViewById(R.id.audioSeekReceive);
        audioSeekReceive.setThumb(null);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

        txtAudioLength = (TextView) view.findViewById(R.id.txtAudioLength);

    }

    public void bindHolder(Context mContext, MessagesModel mMessage, String userId) {

        txtTime.setText(mMessage.show_message_datetime);

        if (mMessage.favourite_message.get(userId).equals("0")) {
            imgFavouriteAudioReceive.setImageResource(R.mipmap.ic_heart);
        } else {
            imgFavouriteAudioReceive.setImageResource(R.mipmap.ic_heart_red);
        }

        txtAudioLength.setText(mMessage.message);

        if (TextUtils.isEmpty(mMessage.attachment_path)) {
            //attachment not download yet
            if (mMessage.attachment_status.equals("" + Constants.FILE_UPLOADING)) {
                imgPlay.setVisibility(View.GONE);
                imgDownload.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.VISIBLE);
                cpbProgress.setProgress(Integer.parseInt(mMessage.attachment_progress));
            } else if (mMessage.attachment_status.equals("" + Constants.FILE_EREROR)) {
                imgPlay.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.GONE);
                imgDownload.setVisibility(View.VISIBLE);
            } else {//Success
                imgPlay.setVisibility(View.VISIBLE);
                cpbProgress.setVisibility(View.GONE);
                imgDownload.setVisibility(View.GONE);
            }
        } else {
            cpbProgress.setVisibility(View.GONE);
            imgPlay.setVisibility(View.VISIBLE);
            imgDownload.setVisibility(View.GONE);
            audioSeekReceive.setProgress(0);
        }
    }
}
