package holders;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seeaspark.R;
import com.squareup.picasso.Picasso;

import customviews.CircularProgressBar;
import customviews.RoundedTransformation;

/**
 * Created by Applify on 11/9/2016.
 */
public class ChatHolderSenderVideo {

    public LinearLayout llSentVideo;
    public ImageView imgVideoSent, imgFavouriteVideoSent, imgRead, imgUpload,imgPlayVideoSent;
    public TextView txtTime;
    RelativeLayout rlSentMessage;
    CircularProgressBar cpbProgress;
    int mWidth;

    public ChatHolderSenderVideo(Context con, View view, int width) {
        // TODO Auto-generated constructor stub
        mWidth = width;

        llSentVideo = (LinearLayout) view.findViewById(R.id.llSentVideo);

        imgFavouriteVideoSent = (ImageView) view.findViewById(R.id.imgFavouriteVideoSent);

        LinearLayout.LayoutParams relativePam = new LinearLayout.LayoutParams((int) (mWidth * 0.72), (int) (mWidth * 0.72));
        rlSentMessage = (RelativeLayout) view.findViewById(R.id.rlSentMessage);
        rlSentMessage.setLayoutParams(relativePam);

        imgVideoSent = (ImageView) view.findViewById(R.id.imgVideoSent);

        RelativeLayout.LayoutParams cpbParams = new RelativeLayout.LayoutParams((mWidth / 5), (mWidth / 5));
        cpbParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cpbProgress = (CircularProgressBar) view.findViewById(R.id.cpbProgress);
        cpbProgress.setLayoutParams(cpbParams);

        imgUpload = (ImageView) view.findViewById(R.id.imgUpload);

        imgPlayVideoSent= (ImageView) view.findViewById(R.id.imgPlayVideoSent);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

        imgRead = (ImageView) view.findViewById(R.id.imgRead);

    }

    public void bindHolder(Context mContext) {

//        txtTime.setText(model.show_message_datetime);

        Picasso.with(mContext).load(R.mipmap.ic_blur_card).resize((int) (mWidth * 0.72), (int) (mWidth * 0.72)).centerCrop().transform(new RoundedTransformation(8, 0)).into(imgVideoSent);

//        if (!model.attachment_max_color.equals("null") && !TextUtils.isEmpty(model.attachment_max_color))
//            ((GradientDrawable) sent_attach_relative1.getBackground()).setColor(Color.parseColor(model.attachment_max_color));
//
//        if (TextUtils.isEmpty(model.attachment_path)) {
//            //attachment not download yet
//            sent_attach_image1.setImageResource(R.drawable.transparent_shape);
//            sent_attach_loading_lay1.setVisibility(View.GONE);
//            loading_image_lay.setVisibility(View.GONE);
//            sent_attach_download_error_play1.setVisibility(View.GONE);
//            sent_attach_play1.setVisibility(View.GONE);
//        } else {
//            File f = new File(model.custom_data);
//            Picasso.with(mContext).load(f).resize((int) (width * 0.75), (int) (width * 0.75)).centerCrop().transform(new RoundedTransformation(8, 0)).into(sent_attach_image1);
//
//            if (model.attachment_status.equals("" + Consts.FILE_UPLOADING)) {
//                sent_attach_loading_lay1.setVisibility(View.VISIBLE);
//                sent_attach_loading_lay1.setBackgroundResource(R.drawable.white_circle);
//                sent_attach_download_error_play1.setVisibility(View.GONE);
//                loading_image_lay.setVisibility(View.VISIBLE);
//                cpb_progress.setProgress(Integer.parseInt(model.attachment_progress));
//                sent_attach_play1.setVisibility(View.GONE);
//            } else if (model.attachment_status.equals("" + Consts.FILE_EREROR)) {
//                sent_attach_loading_lay1.setVisibility(View.VISIBLE);
//                sent_attach_loading_lay1.setBackgroundResource(R.drawable.white_circle);
//                sent_attach_download_error_play1.setVisibility(View.VISIBLE);
//                loading_image_lay.setVisibility(View.GONE);
//                sent_attach_play1.setVisibility(View.GONE);
//            } else {//Success
//                loading_image_lay.setVisibility(View.GONE);
//                sent_attach_loading_lay1.setVisibility(View.VISIBLE);
//                sent_attach_loading_lay1.setBackgroundResource(0);
//                sent_attach_download_error_play1.setVisibility(View.GONE);
//                sent_attach_play1.setVisibility(View.VISIBLE);
//            }
//        }
//
//        if (TextUtils.isEmpty(opponentIDs)) {
//            if(model.read_ids.size()>0){
//                sent_time_img1.setImageResource(R.drawable.ic_seen);
//            }else if(model.deliver_ids.size()>0){
//                sent_time_img1.setImageResource(R.drawable.ic_delivered);
//            }else{
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
//                }else if (model.message_status.equals("" + Consts.STATUS_MESSAGE_PENDING)) {
//                    sent_time_img1.setImageResource(R.drawable.ic_delivered);
//                } else if (model.message_status.equals("" + Consts.STATUS_MESSAGE_SENT)) {
//                    sent_time_img1.setImageResource(R.drawable.ic_delivered);
//                }
//            }
//        }

    }

}