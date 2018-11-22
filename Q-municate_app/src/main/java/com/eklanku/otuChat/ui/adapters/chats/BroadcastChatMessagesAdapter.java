package com.eklanku.otuChat.ui.adapters.chats;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.quickblox.q_municate_core.models.CombinationMessage;

import java.util.List;

public class BroadcastChatMessagesAdapter extends BaseChatMessagesAdapter {
    private static final String TAG = GroupChatMessagesAdapter.class.getSimpleName();

    public BroadcastChatMessagesAdapter(BaseActivity baseActivity, ConnectycubeChatDialog chatDialog,
                                        List<CombinationMessage> chatMessages) {
        super(baseActivity, chatDialog, chatMessages);
    }

    @Override
    protected void onBindViewMsgLeftHolder(TextMessageHolder holder, CombinationMessage chatMessage, int position) {
        holder.timeTextMessageTextView.setVisibility(View.GONE);

        TextView customMessageTimeTextView = holder.itemView.findViewById(R.id.custom_msg_text_time_message_top_left);
        customMessageTimeTextView.setText(getDate(chatMessage.getDateSent()));

        updateMessageState(chatMessage, chatDialog);
        super.onBindViewMsgLeftHolder(holder, chatMessage, position);
    }

    @Override
    public String obtainAvatarUrl(int valueType, CombinationMessage chatMessage) {
        return chatDialog.getPhoto();
    }

    @Override
    public void displayAvatarImage(String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.placeholder_group)
                .dontAnimate()
                .into(imageView);
    }
}