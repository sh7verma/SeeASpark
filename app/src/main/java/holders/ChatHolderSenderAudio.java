package holders;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.seeaspark.R;
import customviews.CircularProgressBar;
import models.MessagesModel;
import utils.Constants;


public class ChatHolderSenderAudio {

    public LinearLayout llSentAudio, llSentMessage;
    public ImageView imgFavouriteAudioSent, imgUpload,imgPlay, imgReadInvisible, imgRead;
    public TextView txtTimeInvisible, txtTime, txtAudioLength;
    CircularProgressBar cpbProgress;
    public SeekBar audioSeekSent;
    int mWidth;
    int id = R.mipmap.ic_play_black;

    public ChatHolderSenderAudio(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llSentAudio = (LinearLayout) view.findViewById(R.id.llSentAudio);

        imgFavouriteAudioSent = (ImageView) view.findViewById(R.id.imgFavouriteAudioSent);

        llSentMessage = (LinearLayout) view.findViewById(R.id.llSentMessage);

        imgUpload = (ImageView) view.findViewById(R.id.imgUpload);

        imgPlay = (ImageView) view.findViewById(R.id.imgPlay);
        imgPlay.setTag(id);

        RelativeLayout.LayoutParams cpbParams = new RelativeLayout.LayoutParams((mWidth / 9), (mWidth / 9));
        cpbParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cpbProgress = (CircularProgressBar) view.findViewById(R.id.cpbProgress);
        cpbProgress.setLayoutParams(cpbParams);

        txtTimeInvisible = (TextView) view.findViewById(R.id.txtTimeInvisible);

        imgReadInvisible = (ImageView) view.findViewById(R.id.imgReadInvisible);

        audioSeekSent = (SeekBar) view.findViewById(R.id.audioSeekSent);
        audioSeekSent.setThumb(null);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

        imgRead = (ImageView) view.findViewById(R.id.imgRead);

        txtAudioLength = (TextView) view.findViewById(R.id.txtAudioLength);

    }

    public void bindHolder(Context mContext, MessagesModel mMessage, String userId) {

        if (TextUtils.isEmpty(mMessage.attachment_path)) {
            //attachment not download yet
            cpbProgress.setVisibility(View.GONE);
            imgPlay.setVisibility(View.VISIBLE);
            imgUpload.setVisibility(View.GONE);
        } else {
            if (mMessage.attachment_status.equals("" + Constants.FILE_UPLOADING)) {
                imgPlay.setVisibility(View.GONE);
                imgUpload.setVisibility(View.GONE);
                cpbProgress.setVisibility(View.VISIBLE);
                cpbProgress.setProgress(Integer.parseInt(mMessage.attachment_progress));
            } else if (mMessage.attachment_status.equals("" + Constants.FILE_EREROR)) {
                imgPlay.setVisibility(View.GONE);
                imgUpload.setVisibility(View.VISIBLE);
                cpbProgress.setVisibility(View.GONE);
            } else {//Success
                imgPlay.setVisibility(View.VISIBLE);
                cpbProgress.setVisibility(View.GONE);
                imgUpload.setVisibility(View.GONE);
                audioSeekSent.setProgress(0);
            }
        }

        txtTime.setText(mMessage.show_message_datetime);
        txtAudioLength.setText(mMessage.message);

        if (mMessage.favourite_message.get(userId).equals("0")) {
            imgFavouriteAudioSent.setImageResource(R.mipmap.ic_heart);
        } else {
            imgFavouriteAudioSent.setImageResource(R.mipmap.ic_heart_red);
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
