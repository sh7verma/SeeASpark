package adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seeaspark.ConversationActivity;
import com.seeaspark.DocActivity;
import com.seeaspark.FullViewActivity;
import com.seeaspark.FullViewMessageActivity;
import com.seeaspark.NotesActivity;
import com.seeaspark.R;
import com.seeaspark.VideoDisplayActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import database.Database;
import holders.ChatHolderHeader;
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
import models.ChatsModel;
import models.MessagesModel;
import services.DownloadFileService;
import services.UploadFileService;
import utils.Constants;
import utils.Utils;

/**
 * Created by dev on 30/7/18.
 */

public class ConversationAdapter extends BaseAdapter {

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
    ConversationActivity mConversationActivity;
    ChatsModel mPrivateChat = null;

    String mUserID, mOpponentUserId, mParticipantIds;
    int lastPosition = -1;
    boolean flag = false;

    public ConversationAdapter(Context con, ConversationActivity mActivity, int width, String userId,
                               String otherUserId, String participantIds, ChatsModel mChat) {
        mContext = con;
        mScreenwidth = width;
        mUtils = new Utils(mContext);
        mDb = new Database(mContext);
        mConversationActivity = mActivity;
        mUserID = userId;
        mOpponentUserId = otherUserId;
        mParticipantIds = participantIds;
        mPrivateChat = mChat;
    }

    public void remove_selection() {
        notifyDataSetChanged();
    }

    public void animationStatus(boolean status) {
//        flag = status;
    }

    @Override
    public int getCount() {
        return mConversationActivity.getMMessagesMap().size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final MessagesModel mMessage = mConversationActivity.getMMessagesMap().get(mConversationActivity.getMMessageIds().get(position));
        if (mMessage.is_header) {
            ChatHolderHeader header = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_header, parent, false);
                header = new ChatHolderHeader(mContext, convertView, mScreenwidth);
                convertView.setTag(header);
            } else {
                if (convertView.getTag() instanceof ChatHolderHeader) {
                    header = (ChatHolderHeader) convertView.getTag();
                } else {
                    convertView = LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.item_header, parent, false);
                    header = new ChatHolderHeader(mContext, convertView, mScreenwidth);
                    convertView.setTag(header);
                }
            }
            header.bindHolder(mMessage.show_header_text);
        } else {
            if (mMessage.message_type.equals(Constants.TYPE_TEXT)) {
                if (mMessage.sender_id.equalsIgnoreCase(mUserID)) {
                    ChatHolderSenderText mSentTextHolder = null;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_text_sent, parent, false);
                        mSentTextHolder = new ChatHolderSenderText(mContext, convertView, mScreenwidth);
                        convertView.setTag(mSentTextHolder);
                    } else {
                        if (convertView.getTag() instanceof ChatHolderSenderText) {
                            mSentTextHolder = (ChatHolderSenderText) convertView.getTag();
                        } else {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_text_sent, parent, false);
                            mSentTextHolder = new ChatHolderSenderText(mContext, convertView, mScreenwidth);
                            convertView.setTag(mSentTextHolder);
                        }
                    }
                    mSentTextHolder.bindHolder(mContext, mMessage, mUserID);

                    mSentTextHolder.txtReadMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = new Intent(mContext, FullViewMessageActivity.class);
                            in.putExtra("message", "" + mMessage.message);
                            in.putExtra("pic", "" + mPrivateChat.profile_pic.get(mOpponentUserId));
                            in.putExtra("name", "" + mPrivateChat.name.get(mOpponentUserId));
                            mContext.startActivity(in);
                        }
                    });

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mSentTextHolder.llSentText.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mSentTextHolder.llSentText.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mSentTextHolder.llSentMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            mConversationActivity.is_options_visible();
                            mConversationActivity.make_options_visible(position, mMessage.message_id, 1);
                            notifyDataSetChanged();
                            return true;
                        }
                    });

                    mSentTextHolder.txtMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            mConversationActivity.is_options_visible();
                            mConversationActivity.make_options_visible(position, mMessage.message_id, 1);
                            notifyDataSetChanged();
                            return true;
                        }
                    });
                    if (flag)
                        setAnimation(mSentTextHolder.llSentMessage, position);
                } else {
                    ChatHolderReceiverText mReceiveTextHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_text_received, parent, false);
                        mReceiveTextHolder = new ChatHolderReceiverText(mContext, convertView, mScreenwidth);
                        convertView.setTag(mReceiveTextHolder);
                    } else {
                        if (convertView.getTag() instanceof ChatHolderReceiverText) {
                            mReceiveTextHolder = (ChatHolderReceiverText) convertView.getTag();
                        } else {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_text_received, parent, false);
                            mReceiveTextHolder = new ChatHolderReceiverText(mContext, convertView, mScreenwidth);
                            convertView.setTag(mReceiveTextHolder);
                        }
                    }
                    mReceiveTextHolder.bindHolder(mContext, mMessage, mUserID);

                    mReceiveTextHolder.txtReadMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent in = new Intent(mContext, FullViewMessageActivity.class);
                            in.putExtra("message", "" + mMessage.message);
                            in.putExtra("pic", "" + mPrivateChat.profile_pic.get(mOpponentUserId));
                            in.putExtra("name", "" + mPrivateChat.name.get(mOpponentUserId));
                            mContext.startActivity(in);
                        }
                    });

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mReceiveTextHolder.llReceiveText.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mReceiveTextHolder.llReceiveText.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mReceiveTextHolder.llReceiveMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            mConversationActivity.is_options_visible();
                            mConversationActivity.make_options_visible(position, mMessage.message_id, 1);
                            notifyDataSetChanged();
                            return true;
                        }
                    });

                    mReceiveTextHolder.txtMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            mConversationActivity.is_options_visible();
                            mConversationActivity.make_options_visible(position, mMessage.message_id, 1);
                            notifyDataSetChanged();
                            return true;
                        }
                    });
                    if (flag)
                        setAnimation(mReceiveTextHolder.llReceiveMessage, position);
                }
            } else if (mMessage.message_type.equals(Constants.TYPE_IMAGE)) {

                if (mMessage.sender_id.equalsIgnoreCase(mUserID)) {

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
                    mSentImageHolder.bindHolder(mContext, mMessage, mUserID);

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mSentImageHolder.llSentImage.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mSentImageHolder.llSentImage.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mSentImageHolder.rlSentMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
//                            if (!mMessage.attachment_status.equals(Constants.FILE_EREROR) && !mMessage.attachment_status.equals(Constants.FILE_UPLOADING)) {
                            if (mMessage.message_status < Constants.STATUS_MESSAGE_SENT) {
                                mConversationActivity.is_options_visible();
                                mConversationActivity.make_options_visible(position, mMessage.message_id, 2);
                                notifyDataSetChanged();
                            }
                            return true;
                        }
                    });

                    mSentImageHolder.imgUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
                                File ff = new File(mMessage.attachment_path);
                                if (ff.exists()) {
                                    Intent in = new Intent(mContext, UploadFileService.class);
                                    in.putExtra("attachment_path", "" + mMessage.attachment_path);
                                    mContext.startService(in);
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    mSentImageHolder.imgImageSent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                        mConversationActivity.openFullViewActivity();
                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
                                File ff = new File(mMessage.attachment_path);
                                if (ff.exists()) {
                                    ArrayList<String> paths = new ArrayList<>();
                                    paths = mDb.getImageVideoAttachments(mMessage.chat_dialog_id);
                                    Intent in = new Intent(mContext, FullViewActivity.class);
                                    in.putExtra("paths", paths);
                                    in.putExtra("display", mMessage.attachment_path);
                                    in.putExtra("pic", "" + mPrivateChat.profile_pic.get(mOpponentUserId));
                                    in.putExtra("name", "" + mPrivateChat.name.get(mOpponentUserId));
                                    mContext.startActivity(in);
                                    if (play_seekbar != null) {
                                        remocecall();
                                    }
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    if (flag)
                        setAnimation(mSentImageHolder.rlSentMessage, position);
                } else {
                    final ChatHolderReceiverImage mReceiveImageHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_image_receive, parent, false);
                        mReceiveImageHolder = new ChatHolderReceiverImage(mContext, convertView, mScreenwidth);
                        convertView.setTag(mReceiveImageHolder);
                    } else {
                        if (convertView.getTag() instanceof ChatHolderReceiverImage) {
                            mReceiveImageHolder = (ChatHolderReceiverImage) convertView.getTag();
                        } else {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_image_receive, parent, false);
                            mReceiveImageHolder = new ChatHolderReceiverImage(mContext, convertView, mScreenwidth);
                            convertView.setTag(mReceiveImageHolder);
                        }
                    }
                    mReceiveImageHolder.bindHolder(mContext, mMessage, mUserID);

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mReceiveImageHolder.llReceiveImage.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mReceiveImageHolder.llReceiveImage.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mReceiveImageHolder.rlReceiveMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (/*!mMessage.attachment_status.equals(Constants.FILE_EREROR) &&*/ !mMessage.attachment_status.equals(Constants.FILE_UPLOADING)) {
                                mConversationActivity.is_options_visible();
                                mConversationActivity.make_options_visible(position, mMessage.message_id, 2);
                                notifyDataSetChanged();
                            }
                            return true;
                        }
                    });

                    mReceiveImageHolder.imgDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(mMessage.attachment_path) && mMessage.attachment_status.equals(Constants.FILE_EREROR) && !TextUtils.isEmpty(mMessage.attachment_url)) {
                                if (mConversationActivity.checkGalleryPermissions()) {
                                    Intent in = new Intent(mContext, DownloadFileService.class);
                                    in.putExtra("message_id", "" + mMessage.message_id);
                                    mContext.startService(in);
                                } else {
                                    mConversationActivity.requestGalleryPermission(R.string.download_permission);
                                }
                            }
                        }
                    });

                    mReceiveImageHolder.imgImageReceive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(mMessage.attachment_path) && !TextUtils.isEmpty(mMessage.attachment_url)) {
                                File ff = new File(mMessage.attachment_path);
                                if (ff.exists()) {
                                    ArrayList<String> paths = new ArrayList<>();
                                    paths = mDb.getImageVideoAttachments(mMessage.chat_dialog_id);
                                    Intent in = new Intent(mContext, FullViewActivity.class);
                                    in.putExtra("paths", paths);
                                    in.putExtra("display", mMessage.attachment_path);
                                    in.putExtra("pic", "" + mPrivateChat.profile_pic.get(mOpponentUserId));
                                    in.putExtra("name", "" + mPrivateChat.name.get(mOpponentUserId));
                                    mContext.startActivity(in);
                                    if (play_seekbar != null) {
                                        remocecall();
                                    }
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    if (flag)
                        setAnimation(mReceiveImageHolder.rlReceiveMessage, position);
                }
            } else if (mMessage.message_type.equals(Constants.TYPE_VIDEO)) {
                if (mMessage.sender_id.equalsIgnoreCase(mUserID)) {
                    ChatHolderSenderVideo mSentVideoHolder = null;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_video_sent, parent, false);
                        mSentVideoHolder = new ChatHolderSenderVideo(mContext, convertView, mScreenwidth);
                        convertView.setTag(mSentVideoHolder);
                    } else {
                        if (convertView.getTag() instanceof ChatHolderSenderVideo) {
                            mSentVideoHolder = (ChatHolderSenderVideo) convertView.getTag();
                        } else {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_video_sent, parent, false);
                            mSentVideoHolder = new ChatHolderSenderVideo(mContext, convertView, mScreenwidth);
                            convertView.setTag(mSentVideoHolder);
                        }
                    }
                    mSentVideoHolder.bindHolder(mContext, mMessage, mUserID);

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mSentVideoHolder.llSentVideo.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mSentVideoHolder.llSentVideo.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mSentVideoHolder.rlSentMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
//                            if (!mMessage.attachment_status.equals(Constants.FILE_EREROR) && !mMessage.attachment_status.equals(Constants.FILE_UPLOADING)) {
                            if (mMessage.message_status < Constants.STATUS_MESSAGE_SENT) {
                                mConversationActivity.is_options_visible();
                                mConversationActivity.make_options_visible(position, mMessage.message_id, 2);
                                notifyDataSetChanged();
                            }
                            return true;
                        }
                    });

                    mSentVideoHolder.imgUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
                                File ff = new File(mMessage.attachment_path);
                                if (ff.exists()) {
                                    Intent in = new Intent(mContext, UploadFileService.class);
                                    in.putExtra("attachment_path", "" + mMessage.attachment_path);
                                    mContext.startService(in);
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    mSentVideoHolder.imgPlayVideoSent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (!TextUtils.isEmpty(mMessage.attachment_path)) {
                                    File videoFile = new File(mMessage.attachment_path);
                                    if (play_seekbar != null) {
                                        remocecall();
                                    }
                                    if (videoFile.exists()) {
                                        Intent in = new Intent(mContext, VideoDisplayActivity.class);
                                        in.putExtra("video_path", mMessage.attachment_path);
                                        in.putExtra("video_seek", 0);
                                        in.putExtra("pic", "" + mPrivateChat.profile_pic.get(mOpponentUserId));
                                        in.putExtra("name", "" + mPrivateChat.name.get(mOpponentUserId));
                                        mContext.startActivity(in);
                                        if (play_seekbar != null) {
                                            remocecall();
                                        }
                                    } else {
                                        Toast.makeText(mContext, mContext.getResources().
                                                getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (IllegalArgumentException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (Resources.NotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                    if (flag)
                        setAnimation(mSentVideoHolder.rlSentMessage, position);
                } else {
                    final ChatHolderReceiverVideo mReceiveVideoHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_video_receive, parent, false);
                        mReceiveVideoHolder = new ChatHolderReceiverVideo(mContext, convertView, mScreenwidth);
                        convertView.setTag(mReceiveVideoHolder);
                    } else {
                        if (convertView.getTag() instanceof ChatHolderReceiverVideo) {
                            mReceiveVideoHolder = (ChatHolderReceiverVideo) convertView.getTag();
                        } else {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_video_receive, parent, false);
                            mReceiveVideoHolder = new ChatHolderReceiverVideo(mContext, convertView, mScreenwidth);
                            convertView.setTag(mReceiveVideoHolder);
                        }
                    }
                    mReceiveVideoHolder.bindHolder(mContext, mMessage, mUserID);

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mReceiveVideoHolder.llReceiveVideo.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mReceiveVideoHolder.llReceiveVideo.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mReceiveVideoHolder.rlReceiveMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (/*!mMessage.attachment_status.equals(Constants.FILE_EREROR) &&*/ !mMessage.attachment_status.equals(Constants.FILE_UPLOADING)) {
                                mConversationActivity.is_options_visible();
                                mConversationActivity.make_options_visible(position, mMessage.message_id, 2);
                                notifyDataSetChanged();
                            }
                            return true;
                        }
                    });

                    mReceiveVideoHolder.imgDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(mMessage.attachment_path) && mMessage.attachment_status.equals(Constants.FILE_EREROR) && !TextUtils.isEmpty(mMessage.attachment_url)) {
                                if (mConversationActivity.checkGalleryPermissions()) {
                                    Intent in = new Intent(mContext, DownloadFileService.class);
                                    in.putExtra("message_id", "" + mMessage.message_id);
                                    mContext.startService(in);
                                } else {
                                    mConversationActivity.requestGalleryPermission(R.string.download_permission);
                                }
                            }
                        }
                    });

                    mReceiveVideoHolder.imgPlayVideoReceive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                if (!TextUtils.isEmpty(mMessage.attachment_path)) {
                                    File videoFile = new File(mMessage.attachment_path);
                                    if (play_seekbar != null) {
                                        remocecall();
                                    }
                                    if (videoFile.exists()) {
                                        Intent in = new Intent(mContext, VideoDisplayActivity.class);
                                        in.putExtra("video_path", mMessage.attachment_path);
                                        in.putExtra("video_seek", 0);
                                        in.putExtra("pic", "" + mPrivateChat.profile_pic.get(mOpponentUserId));
                                        in.putExtra("name", "" + mPrivateChat.name.get(mOpponentUserId));
                                        mContext.startActivity(in);
                                        if (play_seekbar != null) {
                                            remocecall();
                                        }
                                    } else {
                                        Toast.makeText(mContext, mContext.getResources().
                                                getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (IllegalArgumentException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (Resources.NotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    });
                    if (flag)
                        setAnimation(mReceiveVideoHolder.rlReceiveMessage, position);
                }
            } else if (mMessage.message_type.equals(Constants.TYPE_DOCUMENT)) {

                if (mMessage.sender_id.equalsIgnoreCase(mUserID)) {

                    ChatHolderSenderDocument mSentDocumentHolder = null;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_document_sent, parent, false);
                        mSentDocumentHolder = new ChatHolderSenderDocument(mContext, convertView, mScreenwidth);
                        convertView.setTag(mSentDocumentHolder);
                    } else {
                        if (convertView.getTag() instanceof ChatHolderSenderDocument) {
                            mSentDocumentHolder = (ChatHolderSenderDocument) convertView.getTag();
                        } else {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_document_sent, parent, false);
                            mSentDocumentHolder = new ChatHolderSenderDocument(mContext, convertView, mScreenwidth);
                            convertView.setTag(mSentDocumentHolder);
                        }
                    }
                    mSentDocumentHolder.bindHolder(mContext, mMessage, mUserID, mPrivateChat.name.get(mOpponentUserId));

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mSentDocumentHolder.llSentDocumnet.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mSentDocumentHolder.llSentDocumnet.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mSentDocumentHolder.llSentMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
//                            if (!mMessage.attachment_status.equals(Constants.FILE_EREROR) && !mMessage.attachment_status.equals(Constants.FILE_UPLOADING)) {
                            if (mMessage.message_status < Constants.STATUS_MESSAGE_SENT) {
                                mConversationActivity.is_options_visible();
                                mConversationActivity.make_options_visible(position, mMessage.message_id, 2);
                                notifyDataSetChanged();
                            }
                            return true;
                        }
                    });

                    mSentDocumentHolder.imgUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
                                File ff = new File(mMessage.attachment_path);
                                if (ff.exists()) {
                                    Intent in = new Intent(mContext, UploadFileService.class);
                                    in.putExtra("attachment_path", "" + mMessage.attachment_path);
                                    mContext.startService(in);
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    mSentDocumentHolder.llSentMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
                                File file = new File(mMessage.attachment_path);
                                if (file.exists()) {
                                    Uri uri = Uri.fromFile(file);
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    if (mMessage.attachment_url.contains(".pdf")) {
                                        intent.setDataAndType(uri, "application/pdf");
                                    } else if (mMessage.attachment_url.contains(".txt")) {
                                        intent.setDataAndType(uri, "text/plain");
                                    } else {
                                        intent.setDataAndType(uri, "application/msword");
                                    }
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    Intent newIntent = Intent.createChooser(intent, "Open File");
                                    try {
                                        mContext.startActivity(newIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
//                                Intent in = new Intent(mContext, DocActivity.class);
//                                in.putExtra("url", mMessage.attachment_url);
//                                in.putExtra("pic", "" + mPrivateChat.profile_pic.get(mOpponentUserId));
//                                in.putExtra("name", "" + mPrivateChat.name.get(mOpponentUserId));
//                                mContext.startActivity(in);
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    if (flag)
                        setAnimation(mSentDocumentHolder.llSentMessage, position);
                } else {
                    ChatHolderReceiverDocument mReceiveDocumentHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_document_receive, parent, false);
                        mReceiveDocumentHolder = new ChatHolderReceiverDocument(mContext, convertView, mScreenwidth);
                        convertView.setTag(mReceiveDocumentHolder);
                    } else {
                        if (convertView.getTag() instanceof ChatHolderReceiverDocument) {
                            mReceiveDocumentHolder = (ChatHolderReceiverDocument) convertView.getTag();
                        } else {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_document_receive, parent, false);
                            mReceiveDocumentHolder = new ChatHolderReceiverDocument(mContext, convertView, mScreenwidth);
                            convertView.setTag(mReceiveDocumentHolder);
                        }
                    }
                    mReceiveDocumentHolder.bindHolder(mContext, mMessage, mUserID, mPrivateChat.name.get(mOpponentUserId));

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mReceiveDocumentHolder.llReceiveDocument.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mReceiveDocumentHolder.llReceiveDocument.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mReceiveDocumentHolder.llReceiveMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (/*!mMessage.attachment_status.equals(Constants.FILE_EREROR) &&*/ !mMessage.attachment_status.equals(Constants.FILE_UPLOADING)) {
                                mConversationActivity.is_options_visible();
                                mConversationActivity.make_options_visible(position, mMessage.message_id, 2);
                                notifyDataSetChanged();
                            }
                            return true;
                        }
                    });

                    mReceiveDocumentHolder.imgDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(mMessage.attachment_path) && mMessage.attachment_status.equals(Constants.FILE_EREROR) && !TextUtils.isEmpty(mMessage.attachment_url)) {
                                if (mConversationActivity.checkGalleryPermissions()) {
                                    Intent in = new Intent(mContext, DownloadFileService.class);
                                    in.putExtra("message_id", "" + mMessage.message_id);
                                    mContext.startService(in);
                                } else {
                                    mConversationActivity.requestGalleryPermission(R.string.download_permission);
                                }
                            }
                        }
                    });

                    mReceiveDocumentHolder.llReceiveMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(mMessage.attachment_url)) {
                                if (!TextUtils.isEmpty(mMessage.attachment_path)) {
                                    File file = new File(mMessage.attachment_path);
                                    if (file.exists()) {
                                        Uri uri = Uri.fromFile(file);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        if (mMessage.attachment_url.contains(".pdf")) {
                                            intent.setDataAndType(uri, "application/pdf");
                                        } else if (mMessage.attachment_url.contains(".txt")) {
                                            intent.setDataAndType(uri, "text/plain");
                                        } else {
                                            intent.setDataAndType(uri, "application/msword");
                                        }
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        Intent newIntent = Intent.createChooser(intent, "Open File");
                                        try {
                                            mContext.startActivity(newIntent);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
//                                Intent in = new Intent(mContext, DocActivity.class);
//                                in.putExtra("url", mMessage.attachment_url);
//                                in.putExtra("pic", "" + mPrivateChat.profile_pic.get(mOpponentUserId));
//                                in.putExtra("name", "" + mPrivateChat.name.get(mOpponentUserId));
//                                mContext.startActivity(in);
                                    } else {
                                        Toast.makeText(mContext, mContext.getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                    if (flag)
                        setAnimation(mReceiveDocumentHolder.llReceiveMessage, position);
                }
            } else if (mMessage.message_type.equals(Constants.TYPE_NOTES)) {
                if (mMessage.sender_id.equalsIgnoreCase(mUserID)) {
                    ChatHolderSenderNotes mSentNotesHolder = null;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_notes_sent, parent, false);
                        mSentNotesHolder = new ChatHolderSenderNotes(mContext, convertView, mScreenwidth);
                        convertView.setTag(mSentNotesHolder);
                    } else {
                        if (convertView.getTag() instanceof ChatHolderSenderNotes) {
                            mSentNotesHolder = (ChatHolderSenderNotes) convertView.getTag();
                        } else {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_notes_sent, parent, false);
                            mSentNotesHolder = new ChatHolderSenderNotes(mContext, convertView, mScreenwidth);
                            convertView.setTag(mSentNotesHolder);
                        }
                    }
                    mSentNotesHolder.bindHolder(mContext, mMessage, mUserID, mPrivateChat.name.get(mOpponentUserId));

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mSentNotesHolder.llSentNotes.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mSentNotesHolder.llSentNotes.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mSentNotesHolder.llSentMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            mConversationActivity.is_options_visible();
                            mConversationActivity.make_options_visible(position, mMessage.message_id, 2);
                            notifyDataSetChanged();
                            return true;
                        }
                    });

                    mSentNotesHolder.llSentMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String values[] = mMessage.message.split(",");
                            Intent in = new Intent(mContext, NotesActivity.class);
                            in.putExtra("noteId", "" + values[0].trim());
                            in.putExtra("chat", "");
                            in.putExtra("noteFileName", "" + values[1].trim());
                            mContext.startActivity(in);
                        }
                    });
                    if (flag)
                        setAnimation(mSentNotesHolder.llSentMessage, position);
                } else {
                    ChatHolderReceiverNotes mReceiveNotesHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_notes_receive, parent, false);
                        mReceiveNotesHolder = new ChatHolderReceiverNotes(mContext, convertView, mScreenwidth);
                        convertView.setTag(mReceiveNotesHolder);
                    } else {
                        if (convertView.getTag() instanceof ChatHolderReceiverNotes) {
                            mReceiveNotesHolder = (ChatHolderReceiverNotes) convertView.getTag();
                        } else {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_notes_receive, parent, false);
                            mReceiveNotesHolder = new ChatHolderReceiverNotes(mContext, convertView, mScreenwidth);
                            convertView.setTag(mReceiveNotesHolder);
                        }
                    }
                    mReceiveNotesHolder.bindHolder(mContext, mMessage, mUserID, mPrivateChat.name.get(mOpponentUserId));

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mReceiveNotesHolder.llReceiveNotes.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mReceiveNotesHolder.llReceiveNotes.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mReceiveNotesHolder.llReceiveMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            mConversationActivity.is_options_visible();
                            mConversationActivity.make_options_visible(position, mMessage.message_id, 2);
                            notifyDataSetChanged();
                            return true;
                        }
                    });

                    mReceiveNotesHolder.llReceiveMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String values[] = mMessage.message.split(",");
                            Intent in = new Intent(mContext, NotesActivity.class);
                            in.putExtra("noteId", "" + values[0].trim());
                            in.putExtra("chat", "");
                            in.putExtra("noteFileName", "" + values[1].trim());
                            mContext.startActivity(in);
                        }
                    });
                    if (flag)
                        setAnimation(mReceiveNotesHolder.llReceiveMessage, position);
                }
            } else if (mMessage.message_type.equals(Constants.TYPE_AUDIO)) {
                if (mMessage.sender_id.equalsIgnoreCase(mUserID)) {
                    final ChatHolderSenderAudio mSentAudioHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_audio_sent, parent, false);
                        mSentAudioHolder = new ChatHolderSenderAudio(mContext, convertView, mScreenwidth);
                        convertView.setTag(mSentAudioHolder);
                    } else {
                        if (convertView.getTag() instanceof ChatHolderSenderAudio) {
                            mSentAudioHolder = (ChatHolderSenderAudio) convertView.getTag();
                        } else {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_audio_sent, parent, false);
                            mSentAudioHolder = new ChatHolderSenderAudio(mContext, convertView, mScreenwidth);
                            convertView.setTag(mSentAudioHolder);
                        }
                    }
                    mSentAudioHolder.bindHolder(mContext, mMessage, mUserID);

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mSentAudioHolder.llSentAudio.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mSentAudioHolder.llSentAudio.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mSentAudioHolder.llSentMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
//                            if (!mMessage.attachment_status.equals(Constants.FILE_EREROR) && !mMessage.attachment_status.equals(Constants.FILE_UPLOADING)) {
                            if (mMessage.message_status < Constants.STATUS_MESSAGE_SENT) {
                                if (play_seekbar != null) {
                                    remocecall();
                                }
                                mConversationActivity.is_options_visible();
                                mConversationActivity.make_options_visible(position, mMessage.message_id, 2);
                                notifyDataSetChanged();
                            }
                            return true;
                        }
                    });

                    mSentAudioHolder.imgPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
                                File ff = new File(mMessage.attachment_path);
                                if (ff.exists()) {
                                    if (mSentAudioHolder.imgPlay.getTag().equals(R.mipmap.ic_play_black)) {
                                        if (play_seekbar != null) {
                                            remocecall();
                                        }
                                        play_seekbar = mSentAudioHolder.audioSeekSent;
                                        play_time = mSentAudioHolder.txtAudioLength;
                                        play_img = mSentAudioHolder.imgPlay;
                                        play_img.setImageResource(R.mipmap.ic_pause_black);
                                        play_img.setTag(R.mipmap.ic_pause_black);
                                        play_audio(mMessage.attachment_path);
                                    } else if (mSentAudioHolder.imgPlay.getTag().equals(R.mipmap.ic_pause_black)) {
                                        //stop playing audio
                                        if (mediaPlayer != null) {
                                            mediaPlayer.pause();
                                            length = mediaPlayer.getCurrentPosition();
                                        }
                                        if (play_img != null) {
                                            play_img.setImageResource(R.mipmap.ic_play_black);
                                            play_img.setTag(R.mipmap.ic_play_black);
                                        }
                                    }
                                } else {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    mSentAudioHolder.imgUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
                                File ff = new File(mMessage.attachment_path);
                                if (ff.exists()) {
                                    Intent in = new Intent(mContext, UploadFileService.class);
                                    in.putExtra("attachment_path", "" + mMessage.attachment_path);
                                    mContext.startService(in);
                                } else {
                                    Toast.makeText(mContext, mContext.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    if (flag)
                        setAnimation(mSentAudioHolder.llSentMessage, position);
                } else {
                    final ChatHolderReceiverAudio mReceiveAudioHolder;
                    if (convertView == null) {
                        convertView = LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_audio_receive, parent, false);
                        mReceiveAudioHolder = new ChatHolderReceiverAudio(mContext, convertView, mScreenwidth);
                        convertView.setTag(mReceiveAudioHolder);
                    } else {
                        if (convertView.getTag() instanceof ChatHolderReceiverAudio) {
                            mReceiveAudioHolder = (ChatHolderReceiverAudio) convertView.getTag();
                        } else {
                            convertView = LayoutInflater.from(parent.getContext()).inflate(
                                    R.layout.item_audio_receive, parent, false);
                            mReceiveAudioHolder = new ChatHolderReceiverAudio(mContext, convertView, mScreenwidth);
                            convertView.setTag(mReceiveAudioHolder);
                        }
                    }
                    mReceiveAudioHolder.bindHolder(mContext, mMessage, mUserID);

                    if (mConversationActivity.getSelectedPosition() == position) {
                        mReceiveAudioHolder.llReceiveAudio.setBackgroundColor(mContext.getResources().getColor(R.color.view_line_color_orange));
                    } else {
                        mReceiveAudioHolder.llReceiveAudio.setBackgroundColor(Color.TRANSPARENT);
                    }

                    mReceiveAudioHolder.llReceiveMessage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if (/*!mMessage.attachment_status.equals(Constants.FILE_EREROR) &&*/ !mMessage.attachment_status.equals(Constants.FILE_UPLOADING)) {
                                if (play_seekbar != null) {
                                    remocecall();
                                }
                                mConversationActivity.is_options_visible();
                                mConversationActivity.make_options_visible(position, mMessage.message_id, 2);
                                notifyDataSetChanged();
                            }
                            return true;
                        }
                    });

                    mReceiveAudioHolder.imgPlay.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            if (!TextUtils.isEmpty(mMessage.attachment_path)) {
                                File ff = new File(mMessage.attachment_path);
                                if (ff.exists()) {
                                    if (mReceiveAudioHolder.imgPlay.getTag().equals(R.mipmap.ic_play_black)) {
                                        if (play_seekbar != null) {
                                            remocecall();
                                        }
                                        play_seekbar = mReceiveAudioHolder.audioSeekReceive;
                                        play_time = mReceiveAudioHolder.txtAudioLength;
                                        play_img = mReceiveAudioHolder.imgPlay;
                                        play_img.setImageResource(R.mipmap.ic_pause_black);
                                        play_img.setTag(R.mipmap.ic_pause_black);
                                        play_audio(mMessage.attachment_path);
                                    } else if (mReceiveAudioHolder.imgPlay.getTag().equals(R.mipmap.ic_pause_black)) {
                                        //stop playing audio
                                        if (mediaPlayer != null) {
                                            mediaPlayer.pause();
                                            length = mediaPlayer.getCurrentPosition();
                                        }
                                        if (play_img != null) {
                                            play_img.setImageResource(R.mipmap.ic_play_black);
                                            play_img.setTag(R.mipmap.ic_play_black);
                                        }
                                    }
                                } else {
                                    Toast.makeText(mContext, mContext.getResources().getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.media_not_found), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    mReceiveAudioHolder.imgDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (TextUtils.isEmpty(mMessage.attachment_path) && mMessage.attachment_status.equals(Constants.FILE_EREROR) && !TextUtils.isEmpty(mMessage.attachment_url)) {
                                if (mConversationActivity.checkGalleryPermissions()) {
                                    Intent in = new Intent(mContext, DownloadFileService.class);
                                    in.putExtra("message_id", "" + mMessage.message_id);
                                    mContext.startService(in);
                                } else {
                                    mConversationActivity.requestGalleryPermission(R.string.download_permission);
                                }
                            }
                        }
                    });
                    if (flag)
                        setAnimation(mReceiveAudioHolder.llReceiveMessage, position);
                }
            }
        }
        return convertView;
    }

    void play_audio(String audio_path) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
        } else {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(audio_path);
                mediaPlayer.prepare();
                mediaPlayer.start();
                finalTime = mediaPlayer.getDuration();
                startTime = 0;
                if (play_seekbar != null)
                    play_seekbar.setMax((int) finalTime);
                if (play_time != null)
                    play_time.setText(String.format(
                            "%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
                                    - TimeUnit.MINUTES
                                    .toSeconds(TimeUnit.MILLISECONDS
                                            .toMinutes((long) finalTime))));
                if (play_seekbar != null)
                    play_seekbar
                            .setProgress((int) mediaPlayer.getCurrentPosition());
                myHandler.postDelayed(UpdateSongTime, 100);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        if (mediaPlayer != null) {
                            remocecall();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                remocecall();
            }
        }
    }

    public void remocecall() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(0);
                mediaPlayer.pause();
                mediaPlayer.release();
                mediaPlayer = null;

            }
            if (play_img != null) {
                if (/*play_img.getTag().equals(R.mipmap.ic_pause) ||*/ play_img.getTag().equals(R.mipmap.ic_play_black)) {
                    play_img.setImageResource(R.mipmap.ic_play_black);
                    play_img.setTag(R.mipmap.ic_play_black);
                } else {
                    play_img.setImageResource(R.mipmap.ic_play_black);
                    play_img.setTag(R.mipmap.ic_play_black);
                }
            }
            length = 0;
            myHandler.removeCallbacks(UpdateSongTime);
            if (play_time != null) {
                play_time.setText(String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                                .toMinutes((long) finalTime))));
                if (play_time.getText().toString().equals("00:00")) {
                    play_time.setText("00:01");
                }
            }
            if (play_seekbar != null)
                play_seekbar.setProgress(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            try {
                startTime = mediaPlayer.getCurrentPosition();
                if (play_time != null)
                    play_time.setText(String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                            toMinutes((long) startTime)))

                    );
                if (play_seekbar != null)
                    play_seekbar.setProgress((int) mediaPlayer.getCurrentPosition());
                myHandler.postDelayed(this, 100);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    };

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.push_up_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
