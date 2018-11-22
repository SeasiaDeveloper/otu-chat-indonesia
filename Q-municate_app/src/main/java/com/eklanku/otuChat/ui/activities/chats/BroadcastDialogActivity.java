package com.eklanku.otuChat.ui.activities.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.adapters.chats.BroadcastChatMessagesAdapter;
import com.eklanku.otuChat.utils.ChatDialogUtils;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoadDialogByIdsCommand;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;

public class BroadcastDialogActivity extends BaseDialogActivity {

    private static final String TAG = BroadcastDialogActivity.class.getSimpleName();

    @Bind(R.id.include_view_input_message_layout)
    View inputViewPanel;

    public static void start(Context context, ConnectycubeChatDialog chatDialog) {
        Intent intent = new Intent(context, BroadcastDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, chatDialog);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        context.startActivity(intent);
    }

    public static void startForResult(Fragment fragment, ConnectycubeChatDialog chatDialog, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), BroadcastDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, chatDialog);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actualizeCurrentDialogInfo();
        inputViewPanel.setVisibility(View.GONE);
    }

    private void actualizeCurrentDialogInfo() {
        if (currentChatDialog != null) {
            QBLoadDialogByIdsCommand.start(this, new ArrayList<>(Collections.singletonList(currentChatDialog.getDialogId())));
        }
    }

    @Override
    protected void initChatAdapter() {
        messagesAdapter = new BroadcastChatMessagesAdapter(this, currentChatDialog, combinationMessagesList);
    }

    @Override
    protected void initMessagesRecyclerView() {
        super.initMessagesRecyclerView();
        messagesRecyclerView.addItemDecoration(
                new StickyRecyclerHeadersDecoration(messagesAdapter));
        messagesRecyclerView.setAdapter(messagesAdapter);

        scrollMessagesToBottom(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.broadcast_dialog_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (GroupDialogDetailsActivity.UPDATE_DIALOG_REQUEST_CODE == requestCode && GroupDialogDetailsActivity.RESULT_DELETE_GROUP == resultCode) {
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected Bundle generateBundleToInitDialog() {
        return null;
    }

    @Override
    protected void updateMessagesList() {

    }

    @Override
    protected void checkMessageSendingPossibility() {
        checkMessageSendingPossibility(isNetworkAvailable());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_broadcast_details:
                BroadcastDialogDetailsActivity.start(this, currentChatDialog.getDialogId());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void updateActionBar() {
        if (isNetworkAvailable() && currentChatDialog != null) {
            checkActionBarLogo(currentChatDialog.getPhoto(), R.drawable.placeholder_group);
        }
    }

    @Override
    protected void onConnectServiceLocally(QBService service) {
        onConnectServiceLocally();
    }
}