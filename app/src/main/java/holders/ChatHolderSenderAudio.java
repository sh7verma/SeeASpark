package holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.seeaspark.R;

import customviews.CircularProgressBar;


public class ChatHolderSenderAudio {

    public LinearLayout llSentAudio, llSentMessage;
    public ImageView imgFavouriteAudioSent, imgPlay, imgReadInvisible, imgRead;
    public TextView txtTimeInvisible, txtTime, txtAudioLength;
    CircularProgressBar cpbProgress;
    public SeekBar audioSeekSent;
    int mWidth;

//    int id = R.drawable.ic_play;

    public ChatHolderSenderAudio(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llSentAudio = (LinearLayout) view.findViewById(R.id.llSentAudio);

        imgFavouriteAudioSent = (ImageView) view.findViewById(R.id.imgFavouriteAudioSent);

        llSentMessage = (LinearLayout) view.findViewById(R.id.llSentMessage);

        imgPlay = (ImageView) view.findViewById(R.id.imgPlay);

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

    public void bindHolder(Context mContext) {

//        txtTime.setText(model.custom_data);

//        if (TextUtils.isEmpty(opponentIDs)) {
//            if (model.read_ids.size() > 0) {
//                sent_time_img1.setImageResource(R.drawable.ic_seen);
//            } else if (model.deliver_ids.size() > 0) {
//                sent_time_img1.setImageResource(R.drawable.ic_delivered);
//            } else {
//                sent_time_img1.setImageResource(R.drawable.ic_delivered);
//            }
//        } else {
//            sent_time_img1.setVisibility(View.VISIBLE);
//            if (model.read_ids.contains(opponentIDs)) {
//                sent_time_img1.setImageResource(R.drawable.ic_seen);
//            } else if (model.deliver_ids.contains(opponentIDs)) {
//                sent_time_img1.setImageResource(R.drawable.ic_delivered);
//            } else {
//                if (TextUtils.isEmpty(model.message_status)) {
//                    sent_time_img1.setImageResource(R.drawable.ic_delivered);
//                } else if (model.message_status.equals("" + Consts.STATUS_MESSAGE_PENDING)) {
//                    sent_time_img1.setImageResource(R.drawable.ic_delivered);
//                } else if (model.message_status.equals("" + Consts.STATUS_MESSAGE_SENT)) {
//                    sent_time_img1.setImageResource(R.drawable.ic_delivered);
//                }
//            }
//        }

    }
}
