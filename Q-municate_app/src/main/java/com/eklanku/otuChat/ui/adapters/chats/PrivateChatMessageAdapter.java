package com.eklanku.otuChat.ui.adapters.chats;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eklanku.otuChat.ui.activities.chats.PrivateDialogActivity;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.utils.DateUtils;
import com.eklanku.otuChat.utils.listeners.FriendOperationListener;
import com.connectycube.storage.model.ConnectycubeFile;
import com.quickblox.q_municate_core.models.CombinationMessage;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.DialogNotification;
import com.quickblox.q_municate_db.models.State;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.ui.chatmessage.adapter.media.video.thumbnails.VideoThumbnail;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PrivateChatMessageAdapter extends BaseChatMessagesAdapter implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private static final String TAG = PrivateChatMessageAdapter.class.getSimpleName();
    private static final int SECOND_IN_MILLIS = 1000;

    private static int EMPTY_POSITION = -1;
    private int lastRequestPosition = EMPTY_POSITION;
    private int lastInfoRequestPosition = EMPTY_POSITION;
    private FriendOperationListener friendOperationListener;
    PrivateDialogActivity.ItemClickListener itemClickListener;
    ArrayList<CombinationMessage> selectedMessagesList;

    private QMUser opponentUser;

    protected DataManager dataManager;

    public PrivateChatMessageAdapter(BaseActivity baseActivity, QMUser opponentUser, List<CombinationMessage> chatMessages, FriendOperationListener friendOperationListener, ConnectycubeChatDialog chatDialog, PrivateDialogActivity.ItemClickListener itemClickListener) {
        super(baseActivity, chatDialog, chatMessages);
        this.friendOperationListener = friendOperationListener;
        dataManager = DataManager.getInstance();
        this.itemClickListener = itemClickListener;
        this.opponentUser = opponentUser;
        this.chatDialog = chatDialog;
    }

    @Override
    protected void setMsgLayoutResourceByType(int typeLayout, int messageLayoutResource) {
        super.setMsgLayoutResourceByType(typeLayout, messageLayoutResource);
    }

    @Override
    protected void onBindViewCustomHolder(MessageViewHolder holder, CombinationMessage chatMessage, final int position) {
        Log.d(TAG, "onBindViewCustomHolder combinationMessage getBody= " + chatMessage.getBody());
        Log.d("OPPO-1", "onBindViewCustomHolder: "+chatMessage.getBody());
        FriendsViewHolder friendsViewHolder = (FriendsViewHolder) holder;

        boolean friendsRequestMessage = DialogNotification.Type.FRIENDS_REQUEST.equals(
                chatMessage.getNotificationType());
        boolean friendsInfoRequestMessage = chatMessage.getNotificationType() != null && !friendsRequestMessage;

        TextView textView = (TextView) holder.itemView.findViewById(R.id.message_textview);
        TextView timeTextMessageTextView = (TextView) holder.itemView.findViewById(R.id.time_text_message_textview);
        textView.setTextIsSelectable(false);

        if (friendsRequestMessage) {
            textView.setText(chatMessage.getBody());
            timeTextMessageTextView.setText(DateUtils.formatDateSimpleTime(chatMessage.getCreatedDate() * SECOND_IN_MILLIS));

            setVisibilityFriendsActions(friendsViewHolder, View.GONE);
        } else if (friendsInfoRequestMessage) {
            Log.d(TAG, "friendsInfoRequestMessage onBindViewCustomHolder combinationMessage getBody= " + chatMessage.getBody());
            textView.setText(chatMessage.getBody());
            timeTextMessageTextView.setText(DateUtils.formatDateSimpleTime(chatMessage.getCreatedDate() * SECOND_IN_MILLIS));

            setVisibilityFriendsActions(friendsViewHolder, View.GONE);

            lastInfoRequestPosition = position;
        } else {
            Log.d(TAG, "else onBindViewCustomHolderr combinationMessage getBody= " + chatMessage.getBody());
            textView.setText(chatMessage.getBody());
            timeTextMessageTextView.setText(DateUtils.formatDateSimpleTime(chatMessage.getCreatedDate() * SECOND_IN_MILLIS));
        }

        if (!State.READ.equals(chatMessage.getState()) && isIncoming(chatMessage) && baseActivity.isNetworkAvailable()) {
            updateMessageState(chatMessage, chatDialog);
        }
        // check if last messageCombination is request messageCombination
        boolean lastRequestMessage = (position == getItemCount() - 1 && friendsRequestMessage);
        if (lastRequestMessage) {
            findLastFriendsRequest();
        }


        // check if friend was rejected/deleted.
        if (lastRequestPosition != EMPTY_POSITION && lastRequestPosition < lastInfoRequestPosition) {
            lastRequestPosition = EMPTY_POSITION;
        } else if ((lastRequestPosition != EMPTY_POSITION && lastRequestPosition == position)) { // set visible friends actions
            setVisibilityFriendsActions((FriendsViewHolder) holder, View.VISIBLE);
            initListeners((FriendsViewHolder) holder, position, chatMessage.getDialogOccupant().getUser().getId());
        }

        textView.setFocusable(false);
        textView.setTextIsSelectable(false);
        textView.setClickable(false);
    }

    @Override
    protected void onBindViewMsgRightHolder(final TextMessageHolder holder, CombinationMessage chatMessage, final int position) {
        ImageView view = (ImageView) holder.itemView.findViewById(R.id.message_status_image_view);
        setViewVisibility(holder.avatar, View.GONE);

        if (chatMessage.getState() != null) {
            setMessageStatus(view, State.DELIVERED.equals(
                    chatMessage.getState()), State.READ.equals(chatMessage.getState()));
        } else {
            view.setImageResource(android.R.color.transparent);
        }
        handleMessageClickListener(holder, position);
        addReplyView(holder, chatMessage, position);

        super.onBindViewMsgRightHolder(holder, chatMessage, position);
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

    public void setSelectedMessagesList(ArrayList<CombinationMessage> selectedMessagesList) {
        this.selectedMessagesList = selectedMessagesList;
    }

    @Override
    protected void onBindViewMsgLeftHolder(TextMessageHolder holder, CombinationMessage chatMessage, int position) {
        LinearLayout linearLayout = (LinearLayout) holder.itemView.findViewById(R.id.msg_custom_widget_frame_top);
        setViewVisibility(holder.avatar, View.GONE);
        setViewVisibility(linearLayout, View.GONE);
        handleMessageClickListener(holder, position);
        updateMessageState(chatMessage, chatDialog);
        addReplyView(holder, chatMessage, position);

        super.onBindViewMsgLeftHolder(holder, chatMessage, position);
    }

    private void addReplyView(RecyclerView.ViewHolder holder, CombinationMessage chatMessage, int position) {
        try {
            int padLeft = 5;
            int padRight = 5;

            if (opponentUser != null && opponentUser.getId().equals(chatMessage.getDialogOccupant().getUser().getId())) {
                padLeft = 15;
            } else {
                padRight = 15;
            }

            if (position < 5) {
                Log.v("Positions", "Positions: " + position);
            }

            ViewGroup insertPoint = null;

            if (holder instanceof TextMessageHolder) {
                insertPoint = (ViewGroup) ((TextMessageHolder) holder).bubbleFrame;
            } else if (holder instanceof ImageAttachHolder) {
                insertPoint = (ViewGroup) ((ImageAttachHolder) holder).bubbleFrame;
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(padLeft, 125, padRight, 5);
                ((ImageAttachHolder) holder).attachImageView.setLayoutParams(lp);
            } else if (holder instanceof VideoAttachHolder) {
                insertPoint = (ViewGroup) ((VideoAttachHolder) holder).bubbleFrame;
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(padLeft, 125, padRight, 5);
                ((VideoAttachHolder) holder).attachImageView.setLayoutParams(lp);
            } else if (holder instanceof AudioAttachHolder) {
                insertPoint = (ViewGroup) ((AudioAttachHolder) holder).bubbleFrame;
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
                        View v = vi.inflate(R.layout.layout_chat_reply_message, null);

                        LinearLayout llReplyMain = (LinearLayout) v.findViewById(R.id.llReplyMain);

                        LinearLayout.LayoutParams lpReply = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        lpReply.setMargins(padLeft, 5, padRight, 5);
                        llReplyMain.setLayoutParams(lpReply);

                        TextView tvMessage = (TextView) v.findViewById(R.id.tvName);


                        if (opponentUser != null && opponentUser.getId() == obj.getInt("senderID")) {
                            tvMessage.setText(chatMessage.getDialogOccupant().getUser().getFullName());
                        } else {
                            tvMessage.setText("You");
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
                        insertPoint.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        //insertPoint.getChildAt(0).bringToFront();
                    }

                } else {
                    if (insertPoint.getChildCount() >= 2 && insertPoint.getChildAt(0).getId() == R.id.item_reply) {
                        View view0 = insertPoint.getChildAt(0);
                        insertPoint.removeView(view0);
                    }
                    if (holder instanceof ImageAttachHolder) {
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        lp.setMargins(padLeft, 5, padRight, 5);
                        ((ImageAttachHolder) holder).attachImageView.setLayoutParams(lp);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
    protected void onBindViewAttachLeftHolder(final ImageAttachHolder holder, CombinationMessage chatMessage, final int position) {
        setViewVisibility(holder.avatar, View.GONE);
        handleMessageClickListener(holder, position);
        addReplyView(holder, chatMessage, position);
        attachmentClick(holder.attachImageView, holder.itemView, chatMessage, position);
        super.onBindViewAttachLeftHolder(holder, chatMessage, position);
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

    @Override
    public long getHeaderId(int position) {
        CombinationMessage combinationMessage = getItem(position);
        return DateUtils.toShortDateLong(combinationMessage.getCreatedDate());
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_chat_sticky_header_date, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        View view = holder.itemView;

        TextView headerTextView = (TextView) view.findViewById(R.id.header_date_textview);
        CombinationMessage combinationMessage = getItem(position);
        headerTextView.setText(DateUtils.toTodayYesterdayFullMonthDate(combinationMessage.getCreatedDate()));
    }

    @Override
    public int getItemViewType(int position) {
        CombinationMessage combinationMessage = getItem(position);

        if (combinationMessage.getNotificationType() != null) {
            Log.d(TAG, "combinationMessage.getNotificationType()" + combinationMessage.getNotificationType());
            return TYPE_REQUEST_MESSAGE;
        }
        return super.getItemViewType(position);
    }

    @Override
    protected MessageViewHolder onCreateCustomViewHolder(ViewGroup parent, int viewType) {
        return viewType == TYPE_REQUEST_MESSAGE ? new FriendsViewHolder(inflater.inflate(R.layout.item_friends_notification_message, parent, false)) : null;
    }

    @Override
    protected void showAttachUI(ImageAttachHolder viewHolder, boolean isIncoming) {
        setViewVisibility(viewHolder.itemView.findViewById(R.id.msg_bubble_background), View.VISIBLE);
    }

    public void findLastFriendsRequestMessagesPosition() {
        new FindLastFriendsRequestThread().run();
    }

    private void findLastFriendsRequest() {
        for (int i = 0; i < getList().size(); i++) {
            findLastFriendsRequest(i, getList().get(i));
        }
    }

    public void clearLastRequestMessagePosition() {
        lastRequestPosition = EMPTY_POSITION;
    }

    private void findLastFriendsRequest(int position, CombinationMessage combinationMessage) {
        boolean ownMessage;
        boolean friendsRequestMessage;
        boolean isFriend;

        if (combinationMessage.getNotificationType() != null) {
            ownMessage = !combinationMessage.isIncoming(currentUser.getId());
            friendsRequestMessage = DialogNotification.Type.FRIENDS_REQUEST.equals(
                    combinationMessage.getNotificationType());

            if (friendsRequestMessage && !ownMessage) {
                isFriend = dataManager.getFriendDataManager().
                        getByUserId(combinationMessage.getDialogOccupant().getUser().getId()) != null;
                if (!isFriend) {
                    lastRequestPosition = position;
                }
            }
        }
    }

    private void setVisibilityFriendsActions(FriendsViewHolder viewHolder, int visibility) {
        setViewVisibility(viewHolder.acceptFriendImageView, visibility);
        setViewVisibility(viewHolder.dividerView, visibility);
        setViewVisibility(viewHolder.rejectFriendImageView, visibility);
    }

    private void initListeners(FriendsViewHolder viewHolder, final int position, final int userId) {
        viewHolder.acceptFriendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendOperationListener.onAcceptUserClicked(position, userId);
            }
        });

        viewHolder.rejectFriendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendOperationListener.onRejectUserClicked(position, userId);
            }
        });
    }

    private class FindLastFriendsRequestThread extends Thread {

        @Override
        public void run() {
            findLastFriendsRequest();
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

    protected static class FriendsViewHolder extends MessageViewHolder {
        @Nullable
        @Bind(R.id.message_textview)
        TextView messageTextView;

        @Nullable
        @Bind(R.id.time_text_message_textview)
        TextView timeTextMessageTextView;

        @Nullable
        @Bind(R.id.accept_friend_imagebutton)
        ImageView acceptFriendImageView;

        @Nullable
        @Bind(R.id.divider_view)
        View dividerView;

        @Nullable
        @Bind(R.id.reject_friend_imagebutton)
        ImageView rejectFriendImageView;


        public FriendsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            messageTextView.setTextIsSelectable(false);
        }
    }
}