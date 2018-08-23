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
public class ChatHolderReceiverVideo {

    public LinearLayout llReceiveVideo;
    public ImageView imgVideoReceive, imgFavouriteVideoReceive, imgRead, imgDownload,imgPlayVideoReceive;
    public TextView txtTime;
    RelativeLayout rlReceiveMessage;
    CircularProgressBar cpbProgress;
    int mWidth;

    public ChatHolderReceiverVideo(Context con, View view, int width) {
        // TODO Auto-generated constructor stub

        mWidth = width;

        llReceiveVideo = (LinearLayout) view.findViewById(R.id.llReceiveVideo);

        imgFavouriteVideoReceive = (ImageView) view.findViewById(R.id.imgFavouriteVideoReceive);

        LinearLayout.LayoutParams relativePam = new LinearLayout.LayoutParams((int) (mWidth * 0.72), (int) (mWidth * 0.72));
        rlReceiveMessage = (RelativeLayout) view.findViewById(R.id.rlReceiveMessage);
        rlReceiveMessage.setLayoutParams(relativePam);

        imgVideoReceive = (ImageView) view.findViewById(R.id.imgVideoReceive);

        RelativeLayout.LayoutParams cpbParams = new RelativeLayout.LayoutParams((mWidth / 5), (mWidth / 5));
        cpbParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        cpbProgress = (CircularProgressBar) view.findViewById(R.id.cpbProgress);
        cpbProgress.setLayoutParams(cpbParams);

        imgDownload = (ImageView) view.findViewById(R.id.imgDownload);

        imgPlayVideoReceive = (ImageView) view.findViewById(R.id.imgPlayVideoReceive);

        txtTime = (TextView) view.findViewById(R.id.txtTime);

        imgRead = (ImageView) view.findViewById(R.id.imgRead);

    }

    public void bindHolder(Context mContext) {

//        txtTime.setText(model.show_message_datetime);

        Picasso.with(mContext).load(R.mipmap.ic_blur_card).resize((int) (mWidth * 0.72), (int) (mWidth * 0.72)).centerCrop().transform(new RoundedTransformation(8, 0)).into(imgVideoReceive);

//        if (TextUtils.isEmpty(model.attachment_path)) {
//            //attachment not download yet
//            reciever_attach_image1.setImageResource(R.drawable.transparent_shape);
//
//            if(TextUtils.isEmpty(model.attachment_status)){
//                reciever_attach_loading_lay1.setVisibility(View.VISIBLE);
//                reciever_attach_loading_lay1.setBackgroundResource(R.drawable.white_circle);
//                reciever_attach_download_error_play1.setVisibility(View.VISIBLE);
//                loading_image_lay.setVisibility(View.GONE);
//            } else if (model.attachment_status.equals("" + Consts.FILE_UPLOADING)) {
//                reciever_attach_loading_lay1.setVisibility(View.VISIBLE);
//                reciever_attach_loading_lay1.setBackgroundResource(R.drawable.white_circle);
//                reciever_attach_download_error_play1.setVisibility(View.GONE);
//                loading_image_lay.setVisibility(View.VISIBLE);
//                reciever_attach_play1.setVisibility(View.GONE);
//            } else if (model.attachment_status.equals("" + Consts.FILE_EREROR)) {
//                reciever_attach_loading_lay1.setVisibility(View.VISIBLE);
//                reciever_attach_loading_lay1.setBackgroundResource(R.drawable.white_circle);
//                reciever_attach_download_error_play1.setVisibility(View.VISIBLE);
//                reciever_attach_play1.setVisibility(View.GONE);
//                loading_image_lay.setVisibility(View.GONE);
//            } else {//Success
//                reciever_attach_loading_lay1.setVisibility(View.VISIBLE);
//                reciever_attach_loading_lay1.setBackgroundResource(0);
//                loading_image_lay.setVisibility(View.GONE);
//                reciever_attach_play1.setVisibility(View.VISIBLE);
//                reciever_attach_download_error_play1.setVisibility(View.GONE);
//            }
//        } else {
//            //show image either sending or received
//            File f = new File(model.custom_data);
//            Picasso.with(mContext).load(f).resize((int) (width * 0.75), (int) (width * 0.75)).centerCrop().transform(new RoundedTransformation(8, 0)).into(reciever_attach_image1);
//            reciever_attach_loading_lay1.setVisibility(View.VISIBLE);
//            reciever_attach_loading_lay1.setBackgroundResource(0);
//            loading_image_lay.setVisibility(View.GONE);
//            reciever_attach_download_error_play1.setVisibility(View.GONE);
//            reciever_attach_play1.setVisibility(View.VISIBLE);
//        }
    }

}