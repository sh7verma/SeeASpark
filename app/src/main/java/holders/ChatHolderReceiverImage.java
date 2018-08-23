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

public class ChatHolderReceiverImage {

    public LinearLayout llReceiveImage;
    public ImageView imgImageReceive, imgFavouriteImageReceive, imgRead, imgDownload;
    public TextView txtTime;
    RelativeLayout rlReceiveMessage;
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

        imgRead = (ImageView) view.findViewById(R.id.imgRead);
    }

    public void bindHolder(Context mContext) {

//        txtTime.setText(model.show_message_datetime);

        Picasso.with(mContext).load(R.mipmap.ic_blur_card).resize((int) (mWidth * 0.72), (int) (mWidth * 0.72)).centerCrop().transform(new RoundedTransformation(8, 0)).into(imgImageReceive);

//        if (TextUtils.isEmpty(model.attachment_path)) {
//            //attachment not download yet
//            reciever_attach_image1.setImageResource(R.drawable.transparent_shape);
//            reciever_attach_play1.setVisibility(View.GONE);
//            if(TextUtils.isEmpty(model.attachment_status)){
//                reciever_attach_loading_lay1.setVisibility(View.VISIBLE);
//                reciever_attach_download_error_play1.setVisibility(View.VISIBLE);
//                loading_image_lay.setVisibility(View.GONE);
//            } else if (model.attachment_status.equals("" + Consts.FILE_UPLOADING)) {
//                reciever_attach_loading_lay1.setVisibility(View.VISIBLE);
//                reciever_attach_download_error_play1.setVisibility(View.GONE);
//                loading_image_lay.setVisibility(View.VISIBLE);
//                cpb_progress.setProgress(Integer.parseInt(model.attachment_progress));
//            } else if (model.attachment_status.equals("" + Consts.FILE_EREROR)) {
//                reciever_attach_loading_lay1.setVisibility(View.VISIBLE);
//                reciever_attach_download_error_play1.setVisibility(View.VISIBLE);
//                loading_image_lay.setVisibility(View.GONE);
//            } else {//Success
//                reciever_attach_loading_lay1.setVisibility(View.GONE);
//                loading_image_lay.setVisibility(View.GONE);
//                reciever_attach_download_error_play1.setVisibility(View.GONE);
//            }
//        } else {
//            //show image either sending or received
//            File f = new File(model.attachment_path);
//            Uri imageUri = Uri.fromFile(new File(model.attachment_path));
//            Picasso.with(mContext).load(f).resize((int) (width * 0.75), (int) (width * 0.75)).centerCrop().transform(new RoundedTransformation(8, 0)).into(reciever_attach_image1);
//            reciever_attach_loading_lay1.setVisibility(View.GONE);
//            reciever_attach_loading_lay1.setVisibility(View.GONE);
//            reciever_attach_download_error_play1.setVisibility(View.GONE);
//            reciever_attach_play1.setVisibility(View.GONE);
//        }
    }

}
