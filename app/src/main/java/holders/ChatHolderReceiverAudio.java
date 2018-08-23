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


public class ChatHolderReceiverAudio {

    public LinearLayout llReceiveAudio, llSentMessage;
    public ImageView imgFavouriteAudioReceive, imgPlay, imgReadInvisible, imgRead;
    public TextView txtTimeInvisible, txtTime, txtAudioLength;
    CircularProgressBar cpbProgress;
    public SeekBar audioSeekReceive;
    int mWidth;

//    int id = R.drawable.ic_play;

    public ChatHolderReceiverAudio(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llReceiveAudio = (LinearLayout) view.findViewById(R.id.llReceiveAudio);

        imgFavouriteAudioReceive = (ImageView) view.findViewById(R.id.imgFavouriteAudioReceive);

        llSentMessage = (LinearLayout) view.findViewById(R.id.llSentMessage);

        imgPlay = (ImageView) view.findViewById(R.id.imgPlay);

        RelativeLayout.LayoutParams cpbParams = new RelativeLayout.LayoutParams((mWidth / 9), (mWidth / 9));
        cpbParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cpbProgress = (CircularProgressBar) view.findViewById(R.id.cpbProgress);
        cpbProgress.setLayoutParams(cpbParams);

        txtTimeInvisible = (TextView) view.findViewById(R.id.txtTimeInvisible);

        imgReadInvisible = (ImageView) view.findViewById(R.id.imgReadInvisible);

        audioSeekReceive = (SeekBar) view.findViewById(R.id.audioSeekReceive);
        audioSeekReceive.setThumb(null);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

        imgRead = (ImageView) view.findViewById(R.id.imgRead);

        txtAudioLength = (TextView) view.findViewById(R.id.txtAudioLength);

    }

    public void bindHolder(Context mContext) {

//        txtTime.setText(model.show_message_datetime);
//
//        txtAudioLength.setText(model.custom_data);

//        if (TextUtils.isEmpty(model.attachment_path)) {
//            //attachment not download yet
//            if(TextUtils.isEmpty(model.attachment_status)) {
//                reciever_audio_loading_progress1.setVisibility(View.GONE);
//                reciever_audio_img1.setVisibility(View.VISIBLE);
//            }else if (model.attachment_status.equals("" + Consts.FILE_UPLOADING)) {
//                reciever_audio_loading_progress1.setVisibility(View.VISIBLE);
//                reciever_audio_img1.setVisibility(View.INVISIBLE);
//            } else if (model.attachment_status.equals("" + Consts.FILE_EREROR)) {
//                reciever_audio_loading_progress1.setVisibility(View.GONE);
//                reciever_audio_img1.setVisibility(View.VISIBLE);
//            } else {//Success
//                reciever_audio_loading_progress1.setVisibility(View.GONE);
//                reciever_audio_img1.setVisibility(View.VISIBLE);
//            }
//        } else {
//            //show image either sending or received
//            reciever_audio_loading_progress1.setVisibility(View.GONE);
//			reciever_audio_img1.setVisibility(View.VISIBLE);
//			reciever_audio_img1.setBackgroundResource(R.drawable.ic_play);
//			reciever_audio_img1.setTag(R.drawable.ic_play);
//        }

    }
}
