package adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seeaspark.R;

import database.Database;
import holders.ChatHolderReceiverAudio;
import holders.ChatHolderReceiverDocument;
import holders.ChatHolderReceiverImage;
import holders.ChatHolderReceiverNotes;
import holders.ChatHolderReceiverText;
import holders.ChatHolderReceiverVideo;
import holders.ChatHolderSenderAudio;
import holders.ChatHolderSenderDocument;
import holders.ChatHolderSenderImage;
import holders.ChatHolderSenderNotes;
import holders.ChatHolderSenderText;
import holders.ChatHolderSenderVideo;
import utils.Utils;

/**
 * Created by dev on 31/7/18.
 */

public class FavouriteAdapter extends BaseAdapter {

    Context mContext;
    int mScreenwidth;
    Database mDb;
    MediaPlayer mediaPlayer = null;
    int startTime = 0, finalTime = 0, length = 0;
    Utils mUtils;

    TextView play_time = null;
    SeekBar play_seekbar = null;
    ImageView play_img = null;
    Handler myHandler = new Handler();

    public FavouriteAdapter(Context con, int width) {
        mContext = con;
        mScreenwidth = width;
        mUtils = new Utils(mContext);
        mDb = new Database(mContext);
    }

    public void remove_selection() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

//        if (mMessage.message_type.equals(Constants.TYPE_TEXT)) {
//
//            if (mMessage.sender_id.equalsIgnoreCase(loginUserId)) {
//
//                ChatHolderSenderText mSentTextHolder = null;
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.item_text_sent, parent, false);
//                    mSentTextHolder = new ChatHolderSenderText(mContext, convertView, mScreenwidth);
//                    convertView.setTag(mSentTextHolder);
//                } else {
//                    if (convertView.getTag() instanceof ChatHolderSenderText) {
//                        mSentTextHolder = (ChatHolderSenderText) convertView.getTag();
//                    } else {
//                        convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                R.layout.item_text_sent, parent, false);
//                        mSentTextHolder = new ChatHolderSenderText(mContext, convertView, mScreenwidth);
//                        convertView.setTag(mSentTextHolder);
//                    }
//                }
//                mSentTextHolder.bindHolder(mContext);
//
//                mSentTextHolder.txtReadMore.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent in = new Intent(mContext, FullMessageActivity.class);
//                        in.putExtra("message", "" + mMessage.message);
//                        mContext.startActivity(in);
//                    }
//                });
//
//            } else {
//
//                ChatHolderReceiverText mReceiveTextHolder;
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.item_text_received, parent, false);
//                    mReceiveTextHolder = new ChatHolderReceiverText(mContext, convertView, mScreenwidth);
//                    convertView.setTag(mReceiveTextHolder);
//                } else {
//                    if (convertView.getTag() instanceof ChatHolderReceiverText) {
//                        mReceiveTextHolder = (ChatHolderReceiverText) convertView.getTag();
//                    } else {
//                        convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                R.layout.item_text_received, parent, false);
//                        mReceiveTextHolder = new ChatHolderReceiverText(mContext, convertView, mScreenwidth);
//                        convertView.setTag(mReceiveTextHolder);
//                    }
//                }
//                mReceiveTextHolder.bindHolder(mContext);
//
//                mReceiveTextHolder.txtReadMore.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent in = new Intent(mContext, FullMessageActivity.class);
//                        if (TextUtils.isEmpty(mMessage.mytranslation)) {
//                            in.putExtra("message", "" + mMessage.message);
//                        } else {
//                            if (mMessage.show_message_status == 0) {
//                                in.putExtra("message", "" + mMessage.mytranslation);
//                            } else {
//                                in.putExtra("message", "" + mMessage.message);
//                            }
//                        }
//                        mContext.startActivity(in);
//                    }
//                });
//
//            }
//
//        } else if (mMessage.message_type.equals(Constants.TYPE_IMAGE)) {
//
//            if (mMessage.sender_id.equalsIgnoreCase(loginUserId)) {
//
                ChatHolderSenderImage mSentImageHolder = null;
                if (convertView == null) {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.item_image_sent, parent, false);
                    mSentImageHolder = new ChatHolderSenderImage(mContext, convertView, mScreenwidth);
                    convertView.setTag(mSentImageHolder);
                } else {
                    if (convertView.getTag() instanceof ChatHolderSenderImage) {
                        mSentImageHolder = (ChatHolderSenderImage) convertView.getTag();
                    } else {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_image_sent, parent, false);
                        mSentImageHolder = new ChatHolderSenderImage(mContext, convertView, mScreenwidth);
                        convertView.setTag(mSentImageHolder);
                    }
                }
                mSentImageHolder.bindHolder(mContext);
//
//                if (ActivityChat.selectedPosition == position) {
//                    mSentHolder.lay_sent1
//                            .setBackgroundColor(mContext.getResources().getColor(R.color.trans_colorPrimary));
//                } else {
//                    mSentHolder.lay_sent1.setBackgroundColor(Color.TRANSPARENT);
//                }
//
//                mSentHolder.imgUpload.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!TextUtils.isEmpty(mMessage.attachment_path)) {
//                            Intent in = new Intent(mContext, UploadFileService.class);
//                            in.putExtra("attachment_path", "" + mMessage.attachment_path);
//                            in.putExtra("push_token", "" + pushToken);
//                            in.putExtra("access_token", "" + acceessToken);
//                            in.putExtra("participant_ids", "" + participantIds);
//                            mContext.startService(in);
//                        }
//                    }
//                });
//
//                mSentHolder.imgImageSent.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!TextUtils.isEmpty(mMessage.attachment_path)) {
//                            File ff = new File(mMessage.attachment_path);
//                            if (ff.exists()) {
//                                ArrayList<String> paths = new ArrayList<>();
//                                paths = db.getImageVideoAttachments(mMessage.chat_dialog_id);
//
//                                Intent in = new Intent(mContext, Full_View.class);
//                                in.putExtra("paths", paths);
//                                in.putExtra("display", mMessage.attachment_path);
//                                mContext.startActivity(in);
//                                if (play_seekbar != null) {
//                                    remocecall();
//                                }
//                            } else {
//                                Toast.makeText(mContext, mContext.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//
//            } else {
//
//                final ChatHolderReceiverImage mReceiveImageHolder;
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.item_image_receive, parent, false);
//                    mReceiveImageHolder = new ChatHolderReceiverImage(mContext, convertView, mScreenwidth);
//                    convertView.setTag(mReceiveImageHolder);
//                } else {
//                    if (convertView.getTag() instanceof ChatHolderReceiverImage) {
//                        mReceiveImageHolder = (ChatHolderReceiverImage) convertView.getTag();
//                    } else {
//                        convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                R.layout.item_image_receive, parent, false);
//                        mReceiveImageHolder = new ChatHolderReceiverImage(mContext, convertView, mScreenwidth);
//                        convertView.setTag(mReceiveImageHolder);
//                    }
//                }
//                mReceiveImageHolder.bindHolder(mContext);
//
//                mReceiveHolder.reciever_attach_image1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!TextUtils.isEmpty(mMessage.attachment_path) && !TextUtils.isEmpty(mMessage.attachment_url)) {
//                            File ff = new File(mMessage.attachment_path);
//                            if (ff.exists()) {
//                                ArrayList<String> paths = new ArrayList<>();
//                                paths = db.getImageVideoAttachments(mMessage.chat_dialog_id);
//
//                                Intent in = new Intent(mContext, Full_View.class);
//                                in.putExtra("paths", paths);
//                                in.putExtra("display", mMessage.attachment_path);
//                                mContext.startActivity(in);
//                                if (play_seekbar != null) {
//                                    remocecall();
//                                }
//                            } else {
//                                Toast.makeText(mContext, mContext.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                });
//
//                mReceiveHolder.imgDownload.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (TextUtils.isEmpty(mMessage.attachment_path) && mMessage.attachment_status.equals(Consts.FILE_EREROR) && !TextUtils.isEmpty(mMessage.attachment_url)) {
//                            Intent in = new Intent(mContext, DownloadFileService.class);
//                            in.putExtra("message_id", "" + mMessage.message_id);
//                            mContext.startService(in);
//                        }
//                    }
//                });
//
//            }
//
//        } else if (mMessage.message_type.equals(Constants.TYPE_VIDEO)) {
//
//            if (mMessage.sender_id.equalsIgnoreCase(loginUserId)) {
//
//                ChatHolderSenderVideo mSentVideoHolder = null;
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.item_video_sent, parent, false);
//                    mSentVideoHolder = new ChatHolderSenderVideo(mContext, convertView, mScreenwidth);
//                    convertView.setTag(mSentVideoHolder);
//                } else {
//                    if (convertView.getTag() instanceof ChatHolderSenderVideo) {
//                        mSentVideoHolder = (ChatHolderSenderVideo) convertView.getTag();
//                    } else {
//                        convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                R.layout.item_video_sent, parent, false);
//                        mSentVideoHolder = new ChatHolderSenderVideo(mContext, convertView, mScreenwidth);
//                        convertView.setTag(mSentVideoHolder);
//                    }
//                }
//                mSentVideoHolder.bindHolder(mContext);
//
//                mSentHolder.imgUpload.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Intent in = new Intent(mContext, UploadFileService.class);
//                        in.putExtra("attachment_path", "" + mMessage.attachment_path);
//                        in.putExtra("push_token", "" + pushToken);
//                        in.putExtra("access_token", "" + acceessToken);
//                        in.putExtra("participant_ids", "" + participantIds);
//                        mContext.startService(in);
//                    }
//                });
//
//                mSentHolder.sent_attach_play1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        try {
//                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
//                                if (mVideoView != null) {
//                                    mVideoView.pause();
//                                    if (mVideoplayimage != null)
//                                        mVideoplayimage.setVisibility(View.VISIBLE);
//                                    if (mVideoImage != null) {
//                                        mVideoImage.setVisibility(View.VISIBLE);
//                                    }
//                                    mVideoView.setVisibility(View.GONE);
//                                    mVideoView = null;
//                                }
//                                File videoFile = new File(mMessage.attachment_path);
//                                if (videoFile.exists()) {
//                                    Intent in = new Intent(mContext, PlayVideo.class);
//                                    in.putExtra("video_path", mMessage.attachment_path);
//                                    in.putExtra("video_seek", 0);
////                                        in.putExtra("display_text", mMessage.message);
//                                    mContext.startActivity(in);
//                                    if (play_seekbar != null) {
//                                        remocecall();
//                                    }
//                                } else {
//                                    Toast.makeText(mContext, mContext.getResources().
//                                            getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } catch (IllegalArgumentException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        } catch (Resources.NotFoundException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//                mSentHolder.sent_message_lay1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
//                                if (mVideoView != null) {
//                                    mVideoView.pause();
//                                    if (mVideoplayimage != null)
//                                        mVideoplayimage.setVisibility(View.VISIBLE);
//                                    if (mVideoImage != null) {
//                                        mVideoImage.setVisibility(View.VISIBLE);
//                                    }
//                                    mVideoView.setVisibility(View.GONE);
//                                    mVideoView = null;
//                                }
//                                File videoFile = new File(mMessage.attachment_path);
//                                if (videoFile.exists()) {
//                                    Intent in = new Intent(mContext, PlayVideo.class);
//                                    in.putExtra("video_path", mMessage.attachment_path);
//                                    in.putExtra("video_seek", 0);
////                                        in.putExtra("display_text", mMessage.message);
//                                    mContext.startActivity(in);
//                                    if (play_seekbar != null) {
//                                        remocecall();
//                                    }
//                                } else {
//                                    Toast.makeText(mContext, mContext.getResources().
//                                            getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } catch (IllegalArgumentException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        } catch (Resources.NotFoundException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//                if (!TextUtils.isEmpty(mMessage.attachment_path)) {
//                    File f = new File(mMessage.custom_data);
//                    Picasso.with(mContext).load(f).resize((int) (mScreenwidth * 0.75), (int) (mScreenwidth * 0.75)).
//                            centerCrop().transform(new RoundedTransformation(10, 0)).into(mSentHolder.sent_attach_image1);
//
//                    if (mMessage.is_video_visible) {
//                        mSentHolder.sent_videoview.setVisibility(View.VISIBLE);
//                        mSentHolder.sent_attach_image1.setVisibility(View.GONE);
//
//                        if (mMessage.attachment_status.equals("" + Consts.FILE_UPLOADING)) {
//
//                            mSentHolder.sent_attach_loading_lay1.setVisibility(View.VISIBLE);
//                            mSentHolder.sent_attach_loading_lay1.setBackgroundResource(R.drawable.white_circle);
//                            mSentHolder.sent_attach_download_error_play1.setVisibility(View.GONE);
//                            mSentHolder.loading_image_lay.setVisibility(View.VISIBLE);
//                            mSentHolder.cpb_progress.setProgress(Integer.parseInt(mMessage.attachment_progress));
//                            mSentHolder.sent_attach_play1.setVisibility(View.GONE);
//
//                        } else if (mMessage.attachment_status.equals("" + Consts.FILE_EREROR)) {
//
//                            mSentHolder.sent_attach_loading_lay1.setVisibility(View.VISIBLE);
//                            mSentHolder.sent_attach_loading_lay1.setBackgroundResource(R.drawable.white_circle);
//                            mSentHolder.sent_attach_download_error_play1.setVisibility(View.VISIBLE);
//                            mSentHolder.loading_image_lay.setVisibility(View.GONE);
//                            mSentHolder.sent_attach_play1.setVisibility(View.GONE);
//
//                        } else {
//
//                            mSentHolder.loading_image_lay.setVisibility(View.GONE);
//                            mSentHolder.sent_attach_loading_lay1.setVisibility(View.VISIBLE);
//                            mSentHolder.sent_attach_loading_lay1.setBackgroundResource(0);
//                            mSentHolder.sent_attach_download_error_play1.setVisibility(View.GONE);
//                            mSentHolder.sent_attach_play1.setVisibility(View.GONE);
//
//                        }
//
//                        if (mVideoView != null) {
//                            if (mVideoView == mSentHolder.sent_videoview) {
//                                mVideoView.start();
//                            } else {
//                                mVideoView.pause();
//                                if (mVideoplayimage != null)
//                                    mVideoplayimage.setVisibility(View.VISIBLE);
//                                if (mVideoImage != null) {
//                                    mVideoImage.setVisibility(View.VISIBLE);
//                                }
//                                mVideoView.setVisibility(View.GONE);
//                                mVideoView = null;
//                            }
//                        }
//
//                        if (mVideoView == null) {
//                            File videoFile = new File(mMessage.attachment_path);
//                            if (videoFile.exists()) {
//                                mVideoView = mSentHolder.sent_videoview;
//                                mVideoImage = mSentHolder.sent_attach_image1;
//                                mVideoplayimage = mSentHolder.sent_attach_play1;
//                                mVideopath = mMessage.attachment_path;
//                                mVideoView.setVideoPath(mVideopath);
//                                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                    public void onPrepared(MediaPlayer mp) {
//                                        mp.setVolume(0f, 0f);
//                                        if (mVideoView != null)
//                                            mVideoView.start();
//                                    }
//                                });
//                            }
//                        }
//
//                        if (mVideoView != null)
//                            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//                                @Override
//                                public void onCompletion(MediaPlayer mp) {
//                                    try {
//                                        if (mVideoView != null) {
//                                            // TODO Auto-generated method stub
//                                            if (position == chatActivity.lastVideoPosition) {
//                                                chatActivity.lastVideoPosition = -1;
//                                                ActivityChat.mMessagesMap.get(ActivityChat.mMessageIds.get(position)).is_video_visible = false;
//
//                                            }
//                                            if (mVideoplayimage != null)
//                                                mVideoplayimage
//                                                        .setVisibility(View.VISIBLE);
//                                            if (mVideoImage != null) {
//                                                mVideoImage.setVisibility(View.VISIBLE);
//                                            }
//                                            mVideoView.setVisibility(View.GONE);
//                                            mVideopath = "";
//                                            mVideoView = null;
//                                        }
//                                    } catch (Exception e) {
//                                        // TODO Auto-generated catch block
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                    } else {
//                        if (position == chatActivity.lastVideoPosition) {
//                            chatActivity.lastVideoPosition = -1;
//                            ActivityChat.mMessagesMap.get(ActivityChat.mMessageIds.get(position)).is_video_visible = false;
//                        }
//
//                        if (mVideoView != null && mVideoView == mSentHolder.sent_videoview) {
//                            mVideoView.pause();
//                            if (mVideoplayimage != null)
//                                mVideoplayimage.setVisibility(View.VISIBLE);
//                            if (mVideoImage != null) {
//                                mVideoImage.setVisibility(View.VISIBLE);
//                            }
//                            mVideoView.setVisibility(View.GONE);
//                            mVideoView = null;
//                        }
//                        mSentHolder.sent_videoview.setVisibility(View.GONE);
//                        mSentHolder.sent_attach_image1.setVisibility(View.VISIBLE);
//
//                        if (mMessage.attachment_status.equals("" + Consts.FILE_UPLOADING)) {
//
//                            mSentHolder.sent_attach_loading_lay1.setVisibility(View.VISIBLE);
//                            mSentHolder.sent_attach_loading_lay1.setBackgroundResource(R.drawable.white_circle);
//                            mSentHolder.sent_attach_download_error_play1.setVisibility(View.GONE);
//                            mSentHolder.loading_image_lay.setVisibility(View.VISIBLE);
//                            mSentHolder.cpb_progress.setProgress(Integer.parseInt(mMessage.attachment_progress));
//                            mSentHolder.sent_attach_play1.setVisibility(View.GONE);
//
//                        } else if (mMessage.attachment_status.equals("" + Consts.FILE_EREROR)) {
//
//                            mSentHolder.sent_attach_loading_lay1.setVisibility(View.VISIBLE);
//                            mSentHolder.sent_attach_loading_lay1.setBackgroundResource(R.drawable.white_circle);
//                            mSentHolder.sent_attach_download_error_play1.setVisibility(View.VISIBLE);
//                            mSentHolder.loading_image_lay.setVisibility(View.GONE);
//                            mSentHolder.sent_attach_play1.setVisibility(View.GONE);
//
//                        } else {
//
//                            mSentHolder.loading_image_lay.setVisibility(View.GONE);
//                            mSentHolder.sent_attach_loading_lay1.setVisibility(View.VISIBLE);
//                            mSentHolder.sent_attach_loading_lay1.setBackgroundResource(0);
//                            mSentHolder.sent_attach_download_error_play1.setVisibility(View.GONE);
//                            mSentHolder.sent_attach_play1.setVisibility(View.VISIBLE);
//
//                        }
//
//                    }
//                }
//
//            } else {
//
//                final ChatHolderReceiverVideo mReceiveVideoHolder;
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.item_video_receive, parent, false);
//                    mReceiveVideoHolder = new ChatHolderReceiverVideo(mContext, convertView, mScreenwidth);
//                    convertView.setTag(mReceiveVideoHolder);
//                } else {
//                    if (convertView.getTag() instanceof ChatHolderReceiverVideo) {
//                        mReceiveVideoHolder = (ChatHolderReceiverVideo) convertView.getTag();
//                    } else {
//                        convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                R.layout.item_video_receive, parent, false);
//                        mReceiveVideoHolder = new ChatHolderReceiverVideo(mContext, convertView, mScreenwidth);
//                        convertView.setTag(mReceiveVideoHolder);
//                    }
//                }
//                mReceiveVideoHolder.bindHolder(mContext);
//
//                if (ActivityChat.selectedPosition == position) {
//                    mReceiveHolder.lay_reciever1
//                            .setBackgroundColor(mContext.getResources().getColor(R.color.trans_colorPrimary));
//                } else {
//                    mReceiveHolder.lay_reciever1.setBackgroundColor(Color.TRANSPARENT);
//                }
//
//                mReceiveHolder.reciever_attach_download_error_play1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (TextUtils.isEmpty(mMessage.attachment_path) && mMessage.attachment_status.equals(Consts.FILE_EREROR) && !TextUtils.isEmpty(mMessage.attachment_url)) {
//                            Intent in = new Intent(mContext, DownloadFileService.class);
//                            in.putExtra("message_id", "" + mMessage.message_id);
//                            mContext.startService(in);
//                        }
//                    }
//                });
//
//                mReceiveHolder.reciever_message_lay1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        try {
//                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
//                                if (mVideoView != null) {
//                                    mVideoView.pause();
//                                    if (mVideoplayimage != null)
//                                        mVideoplayimage.setVisibility(View.VISIBLE);
//                                    if (mVideoImage != null) {
//                                        mVideoImage.setVisibility(View.VISIBLE);
//                                    }
//                                    mVideoView.setVisibility(View.GONE);
//                                    mVideoView = null;
//                                }
//                                File videoFile = new File(mMessage.attachment_path);
//                                if (videoFile.exists()) {
//                                    if (!TextUtils.isEmpty(mMessage.attachment_path) && !TextUtils.isEmpty(mMessage.attachment_url)) {
//                                        Intent in = new Intent(mContext, PlayVideo.class);
//                                        in.putExtra("video_path", mMessage.attachment_path);
//                                        in.putExtra("video_seek", 0);
//                                        mContext.startActivity(in);
//                                        if (play_seekbar != null) {
//                                            remocecall();
//                                        }
//                                    }
//                                } else {
//                                    Toast.makeText(mContext, mContext.getResources().
//                                            getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } catch (IllegalArgumentException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        } catch (Resources.NotFoundException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//                mReceiveHolder.reciever_attach_play1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        try {
//                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
//                                if (mVideoView != null) {
//                                    mVideoView.pause();
//                                    if (mVideoplayimage != null)
//                                        mVideoplayimage.setVisibility(View.VISIBLE);
//                                    if (mVideoImage != null) {
//                                        mVideoImage.setVisibility(View.VISIBLE);
//                                    }
//                                    mVideoView.setVisibility(View.GONE);
//                                    mVideoView = null;
//                                }
//                                File videoFile = new File(mMessage.attachment_path);
//                                if (videoFile.exists()) {
//                                    if (!TextUtils.isEmpty(mMessage.attachment_path) && !TextUtils.isEmpty(mMessage.attachment_url)) {
//                                        Intent in = new Intent(mContext, PlayVideo.class);
//                                        in.putExtra("video_path", mMessage.attachment_path);
//                                        in.putExtra("video_seek", 0);
//                                        mContext.startActivity(in);
//                                        if (play_seekbar != null) {
//                                            remocecall();
//                                        }
//                                    }
//                                } else {
//                                    Toast.makeText(mContext, mContext.getResources().
//                                            getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        } catch (IllegalArgumentException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        } catch (Resources.NotFoundException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//                if (!TextUtils.isEmpty(mMessage.attachment_path)) {
//                    File f = new File(mMessage.custom_data);
//                    Picasso.with(mContext).load(f).resize((int) (mScreenwidth * 0.75), (int) (mScreenwidth * 0.75)).
//                            centerCrop().transform(new RoundedTransformation(10, 0)).into(mReceiveHolder.reciever_attach_image1);
//
//                    if (mMessage.is_video_visible) {
//                        mReceiveHolder.reciever_videoview.setVisibility(View.VISIBLE);
//                        mReceiveHolder.reciever_attach_image1.setVisibility(View.GONE);
//                        mReceiveHolder.loading_image_lay.setVisibility(View.GONE);
//                        mReceiveHolder.reciever_attach_download_error_play1.setVisibility(View.GONE);
//                        mReceiveHolder.reciever_attach_play1.setVisibility(View.GONE);
//
//                        if (mVideoView != null) {
//                            if (mVideoView == mReceiveHolder.reciever_videoview) {
//                                mVideoView.start();
//                            } else {
//                                mVideoView.pause();
//                                if (mVideoplayimage != null)
//                                    mVideoplayimage.setVisibility(View.VISIBLE);
//                                if (mVideoImage != null) {
//                                    mVideoImage.setVisibility(View.VISIBLE);
//                                }
//                                mVideoView.setVisibility(View.GONE);
//                                mVideoView = null;
//                            }
//                        }
//
//                        if (mVideoView == null) {
//                            File videoFile = new File(mMessage.attachment_path);
//                            if (videoFile.exists()) {
//                                mVideoView = mReceiveHolder.reciever_videoview;
//                                mVideoImage = mReceiveHolder.reciever_attach_image1;
//                                mVideoplayimage = mReceiveHolder.reciever_attach_play1;
//                                mVideopath = mMessage.attachment_path;
//                                mVideoView.setVideoPath(mVideopath);
//                                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                    public void onPrepared(MediaPlayer mp) {
//                                        mp.setVolume(0f, 0f);
//                                        if (mVideoView != null)
//                                            mVideoView.start();
//                                    }
//                                });
//                            }
//                        }
//
//                        if (mVideoView != null)
//                            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//                                @Override
//                                public void onCompletion(MediaPlayer mp) {
//                                    try {
//                                        if (mVideoView != null) {
//                                            // TODO Auto-generated method stub
//                                            if (position == chatActivity.lastVideoPosition) {
//                                                chatActivity.lastVideoPosition = -1;
//                                                ActivityChat.mMessagesMap.get(ActivityChat.mMessageIds.get(position)).is_video_visible = false;
//
//                                            }
//                                            if (mVideoplayimage != null)
//                                                mVideoplayimage.setVisibility(View.VISIBLE);
//                                            if (mVideoImage != null) {
//                                                mVideoImage.setVisibility(View.VISIBLE);
//                                            }
//                                            mVideoView.setVisibility(View.GONE);
//                                            mVideopath = "";
//                                            mVideoView = null;
//                                        }
//                                    } catch (Exception e) {
//                                        // TODO Auto-generated catch block
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                    } else {
//                        if (position == chatActivity.lastVideoPosition) {
//                            chatActivity.lastVideoPosition = -1;
//                            ActivityChat.mMessagesMap.get(ActivityChat.mMessageIds.get(position)).is_video_visible = false;
//                        }
//                        if (mVideoView != null && mVideoView == mReceiveHolder.reciever_videoview) {
//                            mVideoView.pause();
//                            if (mVideoplayimage != null)
//                                mVideoplayimage.setVisibility(View.VISIBLE);
//                            if (mVideoImage != null) {
//                                mVideoImage.setVisibility(View.VISIBLE);
//                            }
//                            mVideoView.setVisibility(View.GONE);
//                            mVideoView = null;
//                        }
//                        mReceiveHolder.reciever_videoview.setVisibility(View.GONE);
//                        mReceiveHolder.reciever_attach_image1.setVisibility(View.VISIBLE);
//
//                        mReceiveHolder.reciever_attach_play1.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    mReceiveHolder.reciever_attach_image1.setImageResource(R.drawable.trans_lay);
//                    if (mMessage.attachment_status.equals("" + Consts.FILE_EREROR)) {
//
//                        mReceiveHolder.reciever_attach_loading_lay1.setBackgroundResource(R.drawable.white_circle);
//                        mReceiveHolder.reciever_attach_download_error_play1.setVisibility(View.VISIBLE);
//                        mReceiveHolder.reciever_attach_play1.setVisibility(View.GONE);
//                        mReceiveHolder.loading_image_lay.setVisibility(View.GONE);
//                    } else if (mMessage.attachment_status.equals("" + Consts.FILE_UPLOADING)) {
//
//                        mReceiveHolder.reciever_attach_loading_lay1.setVisibility(View.VISIBLE);
//                        mReceiveHolder.reciever_attach_loading_lay1.setBackgroundResource(R.drawable.white_circle);
//                        mReceiveHolder.reciever_attach_download_error_play1.setVisibility(View.GONE);
//                        mReceiveHolder.loading_image_lay.setVisibility(View.VISIBLE);
//                        mReceiveHolder.reciever_attach_play1.setVisibility(View.GONE);
//                    }
//                }
//
//            }
//
//        } else if (mMessage.message_type.equals(Constants.TYPE_DOCUMENT)) {
//
//            if (mMessage.sender_id.equalsIgnoreCase(loginUserId)) {
//
//                if (mMessage.sender_id.equalsIgnoreCase(loginUserId)) {
//
//                    ChatHolderSenderDocument mSentDocumentHolder = null;
//                    if (convertView == null) {
//                        convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                R.layout.item_document_sent, parent, false);
//                        mSentDocumentHolder = new ChatHolderSenderDocument(mContext, convertView, mScreenwidth);
//                        convertView.setTag(mSentDocumentHolder);
//                    } else {
//                        if (convertView.getTag() instanceof ChatHolderSenderDocument) {
//                            mSentDocumentHolder = (ChatHolderSenderDocument) convertView.getTag();
//                        } else {
//                            convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                    R.layout.item_document_sent, parent, false);
//                            mSentDocumentHolder = new ChatHolderSenderDocument(mContext, convertView, mScreenwidth);
//                            convertView.setTag(mSentDocumentHolder);
//                        }
//                    }
//                    mSentDocumentHolder.bindHolder(mContext);
//
//                } else {
//
//                    ChatHolderReceiverDocument mReceiveDocumentHolder;
//                    if (convertView == null) {
//                        convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                R.layout.item_document_receive, parent, false);
//                        mReceiveDocumentHolder = new ChatHolderReceiverDocument(mContext, convertView, mScreenwidth);
//                        convertView.setTag(mReceiveDocumentHolder);
//                    } else {
//                        if (convertView.getTag() instanceof ChatHolderReceiverDocument) {
//                            mReceiveDocumentHolder = (ChatHolderReceiverDocument) convertView.getTag();
//                        } else {
//                            convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                    R.layout.item_document_receive, parent, false);
//                            mReceiveDocumentHolder = new ChatHolderReceiverDocument(mContext, convertView, mScreenwidth);
//                            convertView.setTag(mReceiveDocumentHolder);
//                        }
//                    }
//                    mReceiveDocumentHolder.bindHolder(mContext);
//
//                }
//
//            }
//        } else if (mMessage.message_type.equals(Constants.TYPE_NOTES)) {
//
//            if (mMessage.sender_id.equalsIgnoreCase(loginUserId)) {
//
//                ChatHolderSenderNotes mSentNotesHolder = null;
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.item_notes_sent, parent, false);
//                    mSentNotesHolder = new ChatHolderSenderNotes(mContext, convertView, mScreenwidth);
//                    convertView.setTag(mSentNotesHolder);
//                } else {
//                    if (convertView.getTag() instanceof ChatHolderSenderNotes) {
//                        mSentNotesHolder = (ChatHolderSenderNotes) convertView.getTag();
//                    } else {
//                        convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                R.layout.item_notes_sent, parent, false);
//                        mSentNotesHolder = new ChatHolderSenderNotes(mContext, convertView, mScreenwidth);
//                        convertView.setTag(mSentNotesHolder);
//                    }
//                }
//                mSentNotesHolder.bindHolder(mContext);
//
//            } else {
//
//                ChatHolderReceiverNotes mReceiveNotesHolder;
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.item_notes_receive, parent, false);
//                    mReceiveNotesHolder = new ChatHolderReceiverNotes(mContext, convertView, mScreenwidth);
//                    convertView.setTag(mReceiveNotesHolder);
//                } else {
//                    if (convertView.getTag() instanceof ChatHolderReceiverNotes) {
//                        mReceiveNotesHolder = (ChatHolderReceiverNotes) convertView.getTag();
//                    } else {
//                        convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                R.layout.item_notes_receive, parent, false);
//                        mReceiveNotesHolder = new ChatHolderReceiverNotes(mContext, convertView, mScreenwidth);
//                        convertView.setTag(mReceiveNotesHolder);
//                    }
//                }
//                mReceiveNotesHolder.bindHolder(mContext);
//
//            }
//
//        } else if (mMessage.message_type.equals(Constants.TYPE_AUDIO)) {
//
//            if (mMessage.sender_id.equalsIgnoreCase(loginUserId)) {
//
//                final ChatHolderSenderAudio mSentAudioHolder;
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.item_audio_sent, parent, false);
//                    mSentAudioHolder = new ChatHolderSenderAudio(mContext, convertView, mScreenwidth);
//                    convertView.setTag(mSentAudioHolder);
//                } else {
//                    if (convertView.getTag() instanceof ChatHolderSenderAudio) {
//                        mSentAudioHolder = (ChatHolderSenderAudio) convertView.getTag();
//                    } else {
//                        convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                R.layout.item_audio_sent, parent, false);
//                        mSentAudioHolder = new ChatHolderSenderAudio(mContext, convertView, mScreenwidth);
//                        convertView.setTag(mSentAudioHolder);
//                    }
//                }
//                mSentAudioHolder.bindHolder(mContext);
//
//                mSentHolder.sent_audio_img1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (!TextUtils.isEmpty(mMessage.attachment_path)) {
//                            if (mSentHolder.sent_audio_img1.getTag().equals(R.drawable.ic_play)) {
//                                if (play_seekbar != null) {
//                                    remocecall();
//                                }
//                                play_seekbar = mSentHolder.sent_audio_seek1;
//                                play_time = mSentHolder.sent_audio_time1;
//                                play_img = mSentHolder.sent_audio_img1;
//
//                                play_img.setBackgroundResource(R.drawable.ic_pause);
//                                play_img.setTag(R.drawable.ic_pause);
//
//                                play_audio(mMessage.attachment_path);
//                            } else if (mSentHolder.sent_audio_img1.getTag().equals(R.drawable.ic_pause)) {
//                                //stop playing audio
//                                if (mediaPlayer != null) {
//                                    mediaPlayer.pause();
//                                    length = mediaPlayer.getCurrentPosition();
//                                }
//                                if (play_img != null) {
//                                    play_img.setBackgroundResource(R.drawable.ic_play);
//                                    play_img.setTag(R.drawable.ic_play);
//                                }
//                            }
//                        } else {
//                            Toast.makeText(mContext, mContext.getResources().getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//            } else {
//
//                final ChatHolderReceiverAudio mReceiveAudioHolder;
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.item_audio_receive, parent, false);
//                    mReceiveAudioHolder = new ChatHolderReceiverAudio(mContext, convertView, mScreenwidth);
//                    convertView.setTag(mReceiveAudioHolder);
//                } else {
//                    if (convertView.getTag() instanceof ChatHolderReceiverAudio) {
//                        mReceiveAudioHolder = (ChatHolderReceiverAudio) convertView.getTag();
//                    } else {
//                        convertView = LayoutInflater.from(parent.getContext()).inflate(
//                                R.layout.item_audio_receive, parent, false);
//                        mReceiveAudioHolder = new ChatHolderReceiverAudio(mContext, convertView, mScreenwidth);
//                        convertView.setTag(mReceiveAudioHolder);
//                    }
//                }
//                mReceiveAudioHolder.bindHolder(mContext);
//
//                mReceiveHolder.reciever_audio_img1.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // TODO Auto-generated method stub
//                        if (!TextUtils.isEmpty(mMessage.attachment_path)) {
//
//                            if (mReceiveHolder.reciever_audio_img1.getTag().equals(R.drawable.ic_play)) {
//                                {
//                                    if (play_seekbar != null) {
//                                        remocecall();
//                                    }
//                                    play_seekbar = mReceiveHolder.reciever_audio_seek1;
//                                    play_time = mReceiveHolder.reciever_audio_time1;
//                                    play_img = mReceiveHolder.reciever_audio_img1;
//
//                                    play_img.setBackgroundResource(R.drawable.ic_pause);
//                                    play_img.setTag(R.drawable.ic_pause);
//
//                                    play_audio(mMessage.attachment_path);
//                                }
//                            } else if (mReceiveHolder.reciever_audio_img1.getTag().equals(R.drawable.ic_pause)) {
//                                //stop playing audio
//                                if (mediaPlayer != null) {
//                                    mediaPlayer.pause();
//                                    length = mediaPlayer.getCurrentPosition();
//                                }
//                                if (play_img != null) {
//                                    play_img.setBackgroundResource(R.drawable.ic_play);
//                                    play_img.setTag(R.drawable.ic_play);
//                                }
//                            }
//                        } else {
//                            Toast.makeText(mContext, mContext.getResources().getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//            }
//
//        }

        return convertView;
    }

//    void play_audio(String audio_path) {
//        if (mediaPlayer != null) {
//            mediaPlayer.seekTo(length);
//            mediaPlayer.start();
//        } else {
//            mediaPlayer = new MediaPlayer();
//            try {
//                mediaPlayer.setDataSource(audio_path);
//                mediaPlayer.prepare();
//                mediaPlayer.start();
//                finalTime = mediaPlayer.getDuration();
//                startTime = 0;
//                if (play_seekbar != null)
//                    play_seekbar.setMax((int) finalTime);
//                if (play_time != null)
//                    play_time.setText(String.format(
//                            "%02d:%02d",
//                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
//                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
//                                    - TimeUnit.MINUTES
//                                    .toSeconds(TimeUnit.MILLISECONDS
//                                            .toMinutes((long) finalTime))));
//                if (play_seekbar != null)
//                    play_seekbar
//                            .setProgress((int) mediaPlayer.getCurrentPosition());
//                myHandler.postDelayed(UpdateSongTime, 100);
//                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        // TODO Auto-generated method stub
//                        if (mediaPlayer != null) {
//                            remocecall();
//                        }
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//                remocecall();
//            }
//        }
//    }
//
//    public void remocecall() {
//        try {
//            if (mediaPlayer != null) {
//                mediaPlayer.seekTo(0);
//                mediaPlayer.pause();
//                mediaPlayer.release();
//                mediaPlayer = null;
//
//            }
//            if (play_img != null) {
//                if (play_img.getTag().equals(R.mipmap.ic_pause) || play_img.getTag().equals(R.mipmap.ic_play_black)) {
//                    play_img.setBackgroundResource(R.mipmap.ic_play_black);
//                    play_img.setTag(R.mipmap.ic_play_black);
//                } else {
//                    play_img.setBackgroundResource(R.mipmap.ic_play_black);
//                    play_img.setTag(R.mipmap.ic_play_black);
//                }
//            }
//            length = 0;
//            myHandler.removeCallbacks(UpdateSongTime);
//            if (play_time != null) {
//                play_time.setText(String.format(
//                        "%02d:%02d",
//                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
//                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
//                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
//                                .toMinutes((long) finalTime))));
//                if (play_time.getText().toString().equals("00:00")) {
//                    play_time.setText("00:01");
//                }
//            }
//            if (play_seekbar != null)
//                play_seekbar.setProgress(0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Runnable UpdateSongTime = new Runnable() {
//        public void run() {
//            try {
//                startTime = mediaPlayer.getCurrentPosition();
//                if (play_time != null)
//                    play_time.setText(String.format("%02d:%02d",
//                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
//                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
//                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
//                                            toMinutes((long) startTime)))
//
//                    );
//                if (play_seekbar != null)
//                    play_seekbar.setProgress((int) mediaPlayer.getCurrentPosition());
//                myHandler.postDelayed(this, 100);
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        }
//    };
}
