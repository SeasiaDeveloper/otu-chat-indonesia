package com.eklanku.otuChat.ui.adapters.chats;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.ui.activities.chats.GroupDialogActivity;
import com.eklanku.otuChat.utils.ColorUtils;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.storage.model.ConnectycubeFile;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.CombinationMessage;
import com.quickblox.q_municate_db.models.State;
import com.eklanku.otuChat.R;
import com.connectycube.ui.chatmessage.adapter.media.video.thumbnails.VideoThumbnail;
import com.connectycube.ui.chatmessage.adapter.utils.LinkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class GroupChatMessagesAdapter extends BaseChatMessagesAdapter {
    private static final String TAG = GroupChatMessagesAdapter.class.getSimpleName();
    private ColorUtils colorUtils;
    GroupDialogActivity.ItemClickListener itemClickListener;
    ArrayList<CombinationMessage> selectedMessagesList;

    public GroupChatMessagesAdapter(BaseActivity baseActivity, ConnectycubeChatDialog chatDialog,
                                    List<CombinationMessage> chatMessages, GroupDialogActivity.ItemClickListener itemClickListener) {
        super(baseActivity, chatDialog, chatMessages);
        colorUtils = new ColorUtils();
        this.itemClickListener = itemClickListener;
    }

    @Override
    protected void onBindViewCustomHolder(MessageViewHolder holder, CombinationMessage chatMessage, int position) {
        RequestsViewHolder viewHolder = (RequestsViewHolder) holder;
        boolean notificationMessage = chatMessage.getNotificationType() != null;

        if (notificationMessage) {
            viewHolder.messageTextView.setText(chatMessage.getBody());
            viewHolder.timeTextMessageTextView.setText(getDate(chatMessage.getCreatedDate()));
        } else {
            Log.d(TAG, "onBindViewCustomHolder else");
        }

        if (!State.READ.equals(chatMessage.getState()) && isIncoming(chatMessage) && baseActivity.isNetworkAvailable()) {
            updateMessageState(chatMessage, chatDialog);
        }
        addReplyView(holder, chatMessage, position);
        handleMessageClickListener(holder, position);
    }

    @Override
    protected MessageViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        return viewType == TYPE_REQUEST_MESSAGE ? new RequestsViewHolder(inflater.inflate(R.layout.item_notification_message, parent, false)) : null;
    }

    @Override
    protected void onBindViewMsgLeftHolder(TextMessageHolder holder, CombinationMessage chatMessage, int position) {
        holder.timeTextMessageTextView.setVisibility(View.GONE);

        String senderName;
        senderName = chatMessage.getDialogOccupant().getUser().getFullName();

        TextView opponentNameTextView = (TextView) holder.itemView.findViewById(R.id.opponent_name_text_view);
        opponentNameTextView.setTextColor(colorUtils.getRandomTextColorById(chatMessage.getDialogOccupant().getUser().getId()));
        opponentNameTextView.setText(senderName);

        TextView customMessageTimeTextView = (TextView) holder.itemView.findViewById(R.id.custom_msg_text_time_message_top_left);
        customMessageTimeTextView.setText(getDate(chatMessage.getDateSent()));

        updateMessageState(chatMessage, chatDialog);

        View customViewTopLeft = holder.itemView.findViewById(R.id.custom_view_top_left);
        ViewGroup.LayoutParams layoutParams = customViewTopLeft.getLayoutParams();

        final List<String> urlsList = LinkUtils.extractUrls(chatMessage.getBody());
        if (!urlsList.isEmpty()) {
            layoutParams.width = (int) context.getResources().getDimension(com.connectycube.ui.chatmessage.adapter.R.dimen.link_preview_width);
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        }

        customViewTopLeft.setLayoutParams(layoutParams);
        addReplyView(holder, chatMessage, position);
        handleMessageClickListener(holder, position);
        super.onBindViewMsgLeftHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewMsgRightHolder(TextMessageHolder holder, CombinationMessage chatMessage, int position) {
        ImageView view = (ImageView) holder.itemView.findViewById(R.id.message_status_image_view);
        setViewVisibility(holder.avatar, View.GONE);

        if (chatMessage.getState() != null) {
            setMessageStatus(view, State.DELIVERED.equals(
                    chatMessage.getState()), State.READ.equals(chatMessage.getState()));
        } else {
            view.setImageResource(android.R.color.transparent);
        }

        addReplyView(holder, chatMessage, position);
        handleMessageClickListener(holder, position);
        super.onBindViewMsgRightHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachLeftHolder(final ImageAttachHolder holder, CombinationMessage chatMessage, final int position) {
        handleMessageClickListener(holder, position);
        addReplyView(holder, chatMessage, position);
        attachmentClick(holder.attachImageView, holder.itemView, chatMessage, position);
        super.onBindViewAttachLeftHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachRightHolder(ImageAttachHolder holder, CombinationMessage chatMessage, int position) {
        showSendStatusView(holder, chatMessage);
        handleMessageClickListener(holder, position);
        addReplyView(holder, chatMessage, position);
        attachmentClick(holder.attachImageView, holder.itemView, chatMessage, position);
        super.onBindViewAttachRightHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachRightVideoHolder(VideoAttachHolder holder, CombinationMessage chatMessage, int position) {
        showSendStatusView(holder, chatMessage);
        handleMessageClickListener(holder, position);
        addReplyView(holder, chatMessage, position);
        attachmentClick(holder.attachImageView, holder.itemView, chatMessage, position);
        super.onBindViewAttachRightVideoHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachLeftVideoHolder(VideoAttachHolder holder, CombinationMessage chatMessage, int position) {
        setViewVisibility(holder.avatar, View.GONE);
        handleMessageClickListener(holder, position);
        addReplyView(holder, chatMessage, position);
        attachmentClick(holder.attachImageView, holder.itemView, chatMessage, position);
        super.onBindViewAttachLeftVideoHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachLeftAudioHolder(AudioAttachHolder holder, CombinationMessage chatMessage, int position) {
        setViewVisibility(holder.avatar, View.GONE);
        handleMessageClickListener(holder, position);
        addReplyView(holder, chatMessage, position);
        attachmentAudioClick(holder, position);
        super.onBindViewAttachLeftAudioHolder(holder, chatMessage, position);
    }

    @Override
    protected void onBindViewAttachRightAudioHolder(AudioAttachHolder holder, CombinationMessage chatMessage, int position) {
        showSendStatusView(holder, chatMessage);
        handleMessageClickListener(holder, position);
        addReplyView(holder, chatMessage, position);
        attachmentAudioClick(holder, position);
        super.onBindViewAttachRightAudioHolder(holder, chatMessage, position);
    }

    private void showSendStatusView(BaseAttachHolder holder, CombinationMessage chatMessage) {
        ImageView signAttachView = holder.signAttachView;
        if (chatMessage.getState() != null) {
            setMessageStatus(signAttachView, State.DELIVERED.equals(
                    chatMessage.getState()), State.READ.equals(chatMessage.getState()));
        } else {
            signAttachView.setImageResource(android.R.color.transparent);
        }
    }

    protected void setMessageStatus(ImageView imageView, boolean messageDelivered, boolean messageRead) {
        imageView.setImageResource(getMessageStatusIconId(messageDelivered, messageRead));
    }

    protected int getMessageStatusIconId(boolean isDelivered, boolean isRead) {
        int iconResourceId = 0;

        if (isRead) {
            iconResourceId = R.drawable.ic_status_msg_sent_receive_blue;
        } else if (isDelivered) {
            iconResourceId = R.drawable.ic_status_msg_sent_gray;
        }

        return iconResourceId;
    }

    private void handleMessageClickListener(final RecyclerView.ViewHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(position);
                if (selectedMessagesList != null) {
                    if (selectedMessagesList.contains(chatMessages.get(position))) {
                        holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
                    } else {
                        holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_transparent));
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClickListener.onItemLongClick(position);

                holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
                return true;
            }
        });

        if (holder instanceof TextMessageHolder) {
            ((TextMessageHolder) holder).messageTextView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemClickListener.onItemLongClick(position);
                    holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
                    return true;
                }
            });
        }

        if (selectedMessagesList != null) {
            if (selectedMessagesList.contains(chatMessages.get(position))) {
                holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
            } else {
                holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_transparent));
            }
        }
    }

    protected void attachmentClick(final ImageView imgAttach, final View itemView, CombinationMessage chatMessage, final int position) {
        imgAttach.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClickListener.onItemLongClick(position);

                itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
                return true;
            }
        });

        imgAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(position);
                if (selectedMessagesList != null) {
                    if (selectedMessagesList.contains(chatMessages.get(position))) {
                        itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
                    } else {
                        itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_transparent));
                    }
                }
            }
        });

        if (selectedMessagesList != null) {
            if (selectedMessagesList.contains(chatMessages.get(position))) {
                itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClick(position);
                        if (selectedMessagesList != null) {
                            if (selectedMessagesList.contains(chatMessages.get(position))) {
                                itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
                            } else {
                                itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_transparent));
                            }
                        }
                    }
                });
            } else {
                itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_transparent));
            }
        }
    }

    protected void attachmentAudioClick(final AudioAttachHolder holder, final int position) {
        holder.playerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemClickListener.onItemLongClick(position);

                holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
                return true;
            }
        });

        holder.playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onAudioItemClick(holder.playerView, position);
                if (selectedMessagesList != null) {
                    if (selectedMessagesList.contains(chatMessages.get(position))) {
                        holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
                    } else {
                        holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_transparent));
                    }
                }
            }
        });

        if (selectedMessagesList != null) {
            if (selectedMessagesList.contains(chatMessages.get(position))) {
                holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onItemClick(position);
                        if (selectedMessagesList != null) {
                            if (selectedMessagesList.contains(chatMessages.get(position))) {
                                holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_buttonShadow));
                            } else {
                                holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_transparent));
                            }
                        }
                    }
                });
            } else {
                holder.itemView.setBackgroundColor(baseActivity.getResources().getColor(R.color.fui_transparent));
            }
        }
    }

    public void setSelectedMessagesList(ArrayList<CombinationMessage> selectedMessagesList) {
        this.selectedMessagesList = selectedMessagesList;
    }

    private void addReplyView(RecyclerView.ViewHolder holder, CombinationMessage chatMessage, int position) {
        int x = 1;
        try {
            int padLeft = 20;
            int padRight = 0;
            Log.v("User IDs", "User IDs: " + AppSession.getSession().getUser().getId() + " == " + chatMessage.getDialogOccupant().getUser().getId());
            if (AppSession.getSession().getUser().getId().equals(chatMessage.getDialogOccupant().getUser().getId())) {
                padLeft = 10;
                padRight = 10;
                x = 0;
            }

            if (position < 5) {
                Log.v("Positions", "Positions: " + position);
            }

            ViewGroup insertPoint = null;

            insertPoint = (ViewGroup) ((MessageViewHolder) holder).bubbleFrame;

           /* LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.RIGHT | Gravity.END;
            insertPoint.getChildAt(0).setLayoutParams(params);*/

            if (holder instanceof TextMessageHolder) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.RIGHT | Gravity.END;
                insertPoint.getChildAt(0).setLayoutParams(params);
            } else if (holder instanceof ImageAttachHolder) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(padLeft, 120, padRight, 5);
                ((ImageAttachHolder) holder).attachImageView.setLayoutParams(lp);
            } else if (holder instanceof VideoAttachHolder) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(padLeft, 120, padRight, 5);
                ((VideoAttachHolder) holder).attachImageView.setLayoutParams(lp);
            }
            if (insertPoint != null) {
                if (chatMessage.getReplyMessage() != null) {
                    JSONObject obj = new JSONObject(chatMessage.getReplyMessage());
                    if (obj.has("text")) {

                        Log.v("Reply Obj", obj.toString());
                        if (insertPoint.getChildCount() >= 2) {
                            insertPoint.removeViewAt(0);
                        }
                        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        View v;
                        LinearLayout llReplyMain;

                        if (x == 0) {
                            v = vi.inflate(R.layout.layout_chat_reply_message, null);
                            llReplyMain = (LinearLayout) v.findViewById(R.id.llReplyMain);
                        } else {
                            v = vi.inflate(R.layout.layout_chat_reply_message_left, null);
                            llReplyMain = (LinearLayout) v.findViewById(R.id.llReplyMainleft);
                        }

                       /* v = vi.inflate(R.layout.layout_chat_reply_message, null);
                        llReplyMain = (LinearLayout) v.findViewById(R.id.llReplyMain);*/

                        LinearLayout.LayoutParams lpReply = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        lpReply.setMargins(padLeft, 5, padRight, 5);
                        llReplyMain.setLayoutParams(lpReply);

                        TextView tvMessage = (TextView) v.findViewById(R.id.tvName);
                        tvMessage.setPadding(8, 0, 8, 0);


                        if (AppSession.getSession().getUser().getId() == obj.getInt("senderID")) {
                            tvMessage.setText("You");
                        } else {
                            tvMessage.setText(chatMessage.getDialogOccupant().getUser().getFullName());
                        }

                        TextView tvType = (TextView) v.findViewById(R.id.tvTypeMessage);
                        tvType.setText(obj.getString("text"));

                        ImageView mIvClear = (ImageView) v.findViewById(R.id.ivClear);
                        mIvClear.setVisibility(View.GONE);
                        v.setTag(chatMessage.getMessageId());

                        JSONArray arrAttachemnts = obj.getJSONArray("attachments");
                        if (arrAttachemnts.length() > 0) {
                            ImageView imgAttach = (ImageView) v.findViewById(R.id.ivImage);
                            JSONObject objAttachment = arrAttachemnts.getJSONObject(0);
                            String url = "";
                            if (objAttachment.getString("type").equals("location")) {
                                url = objAttachment.getString("url");
                            } else if (objAttachment.getString("type").equals("image")) {
                                url = ConnectycubeFile.getPrivateUrlForUID(objAttachment.getString("ID"));
                            } else if (objAttachment.getString("type").equals("video")) {
                                String strVideoUrl = ConnectycubeFile.getPrivateUrlForUID(objAttachment.getString("ID"));
                                VideoThumbnail model = new VideoThumbnail(strVideoUrl);
                                Glide.with(this.context).load(model).override(42, 42).error(com.connectycube.ui.chatmessage.adapter.R.drawable.ic_error).into(imgAttach);
                            }
                            if (url.length() > 0) {
                                imgAttach.setVisibility(View.VISIBLE);
                                Glide.with(this.context).load(url).override(42, 42).error(com.connectycube.ui.chatmessage.adapter.R.drawable.ic_error).into(imgAttach);
                            }

                        }
                        insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }

                } else {
                    if (insertPoint.getChildCount() >= 2 && insertPoint.getChildAt(0).getId() == R.id.item_reply) {
                        View view0 = insertPoint.getChildAt(0);
                        view0.setPadding(50, 50, 10, 10);
                        insertPoint.removeView(view0);
                    }
                    if (holder instanceof ImageAttachHolder) {
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        lp.setMargins(padLeft, 5, padRight, 5);
                        ((ImageAttachHolder) holder).attachImageView.setLayoutParams(lp);
                    } else if (holder instanceof VideoAttachHolder) {
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        lp.setMargins(padLeft, 5, padRight, 5);
                        ((VideoAttachHolder) holder).attachImageView.setLayoutParams(lp);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}