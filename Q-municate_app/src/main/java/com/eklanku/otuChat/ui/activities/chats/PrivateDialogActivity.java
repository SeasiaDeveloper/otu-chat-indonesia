package com.eklanku.otuChat.ui.activities.chats;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.eklanku.otuChat.ui.activities.call.CallActivity;
import com.eklanku.otuChat.ui.activities.location.MapsActivity;
import com.eklanku.otuChat.ui.activities.others.PreviewImageActivity;
import com.eklanku.otuChat.ui.activities.profile.UserProfileActivity;
import com.eklanku.otuChat.ui.adapters.chats.PrivateChatMessageAdapter;
import com.eklanku.otuChat.ui.fragments.dialogs.base.TwoButtonsDialogFragment;
import com.eklanku.otuChat.utils.DateUtils;
import com.eklanku.otuChat.utils.ToastUtils;
import com.eklanku.otuChat.utils.helpers.files.FileDownloadManager;
import com.eklanku.otuChat.utils.listeners.FriendOperationListener;
import com.google.gson.Gson;
import com.connectycube.chat.ConnectycubeRestChatService;
import com.connectycube.chat.model.ConnectycubeAttachment;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;
import com.connectycube.storage.model.ConnectycubeFile;
import com.connectycube.core.EntityCallback;
import com.connectycube.core.exception.ResponseException;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.CombinationMessage;
import com.quickblox.q_municate_core.qb.commands.friend.QBAcceptFriendCommand;
import com.quickblox.q_municate_core.qb.commands.friend.QBRejectFriendCommand;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.OnlineStatusUtils;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.managers.FriendDataManager;
import com.quickblox.q_municate_db.models.Attachment;
import com.quickblox.q_municate_db.models.Friend;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.eklanku.otuChat.R;
import com.connectycube.ui.chatmessage.adapter.media.video.ui.VideoPlayerActivity;
import com.connectycube.ui.chatmessage.adapter.media.view.ConnectycubePlaybackControlView;
import com.connectycube.users.model.ConnectycubeUser;
import com.connectycube.videochat.RTCTypes;
//import com.rockerhieu.emojicon.EmojiconTextView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import com.eklanku.otuChat.utils.StringUtils;
import com.eklanku.otuChat.utils.helpers.DbHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class PrivateDialogActivity extends BaseDialogActivity {

    private QMUser opponentUser;
    private FriendObserver friendObserver;
    private BroadcastReceiver typingMessageBroadcastReceiver;
    private int operationItemPosition;
    private boolean isMultipleMessageSelect = false;
    private final String TAG = PrivateDialogActivity.class.getSimpleName();
    private ArrayList<CombinationMessage> selectedMessagesList;
    private ActionMode mActionMode;
    private int longPressPosition = -1;
    private boolean isReply = false;

    public DbHelper mDbHelper;

    @Bind(R.id.include_view_input_message_layout)
    View inputViewPanel;

    int oppUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oppUsername = opponentUser.getId();
        //get fullname ===> String oppUsername = opponentUser.getFullName();

        Log.d("AYIK", "name->" + oppUsername);
        if (oppUsername == 148490 || oppUsername == 148478 || oppUsername == 148492) {
            inputViewPanel.setVisibility(View.GONE);
        }

    }

    public static void start(Context context, QMUser opponent, ConnectycubeChatDialog chatDialog) {
        Intent intent = getIntentWithExtra(context, opponent, chatDialog);
        intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        context.startActivity(intent);
    }

    public static void startWithClearTop(Context context, QMUser opponent, ConnectycubeChatDialog chatDialog) {
        Intent intent = getIntentWithExtra(context, opponent, chatDialog);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void startForResult(Fragment fragment, QMUser opponent, ConnectycubeChatDialog chatDialog,
                                      int requestCode) {
        Intent intent = getIntentWithExtra(fragment.getContext(), opponent, chatDialog);
        fragment.startActivityForResult(intent, requestCode);
    }

    private static Intent getIntentWithExtra(Context context, QMUser opponent, ConnectycubeChatDialog chatDialog) {
        Intent intent = new Intent(context, PrivateDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_OPPONENT, opponent);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, chatDialog);
        return intent;
    }


    @Override
    protected void updateActionBar() {
        setOnlineStatus(opponentUser);

        checkActionBarLogo(opponentUser.getAvatar(), R.drawable.placeholder_user);
    }

    @Override
    protected void onConnectServiceLocally(QBService service) {
        onConnectServiceLocally();
        setOnlineStatus(opponentUser);
    }

    @Override
    protected Bundle generateBundleToInitDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(QBServiceConsts.EXTRA_OPPONENT_ID, opponentUser.getId());
        return bundle;
    }

    @Override
    protected void initChatAdapter() {
        messagesAdapter = new PrivateChatMessageAdapter(this, opponentUser, combinationMessagesList, currentChatDialog, new ItemClickListener() {

            @Override
            public void onItemClick(int position) {
                Log.d("RINA", "initChatAdapter: " + position);
                if (isMultipleMessageSelect)
                    select(position);

                else if (combinationMessagesList.get(position).getAttachments() != null) {
                    String fileId = "";
                    String fileType = "";
                    ConnectycubeAttachment connectycubeAttachment = null;
                    for (ConnectycubeAttachment attachment : combinationMessagesList.get(position).getAttachments()) {
                        fileId = attachment.getId();
                        fileType = attachment.getType();
                        connectycubeAttachment = attachment;
                        break;
                    }
                    Log.v("Attachment", ConnectycubeFile.getPrivateUrlForUID(fileId));
                    //fileType = fileType.toUpperCase();
                    Log.v("FileType", "FileType: " + fileType);
                    if (fileType.equals("image"))
                        PreviewImageActivity.start(PrivateDialogActivity.this, ConnectycubeFile.getPrivateUrlForUID(fileId));
                    else if (fileType.equals("video")) {
                        canPerformLogout.set(false);
                        VideoPlayerActivity.start(PrivateDialogActivity.this, Uri.parse(ConnectycubeFile.getPrivateUrlForUID(fileId)));
                    } else if (fileType.equals("location")) {
                        canPerformLogout.set(false);
                        MapsActivity.startMapForResult(PrivateDialogActivity.this, connectycubeAttachment.getData());
                    } else if (fileType.equals("doc")) {
                        canPerformLogout.set(false);
                        String fileName = connectycubeAttachment.getName();
                        showProgress();
                        FileDownloadManager.getInstance().downLoadFile(fileName, fileId, fileUtils).subscribe(new rx.Observer<File>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                ErrorUtils.logError(TAG, e);
                            }

                            @Override
                            public void onNext(File file) {
                                hideProgress();
                                startShowDoc(file);
                            }
                        });
                    } else if (fileType.equals("contact")) {
                        startShowContact(connectycubeAttachment);
                    }
                }
            }

            @Override
            public void onAudioItemClick(ConnectycubePlaybackControlView playbackView, int position) {
                if (isMultipleMessageSelect)
                    select(position);
                else
                    playbackView.clickIconPlayPauseView();
            }


            @Override
            public void onItemLongClick(int position) {
                if (!isMultipleMessageSelect) {
                    longPressPosition = position;
                    selectedMessagesList = new ArrayList<CombinationMessage>();
                    isMultipleMessageSelect = true;

                    if (mActionMode == null) {
                        mActionMode = startActionMode(mMenuActionModeCallback);
                    }
                }
                select(position);
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void updateMessagesList() {

    }

    @Override
    public void notifyChangedUserStatus(int userId, boolean online) {
        super.notifyChangedUserStatus(userId, online);

        if (opponentUser != null && opponentUser.getId() == userId) {
            if (online) {
                //gets opponentUser from DB with updated field 'last_request_at'
                actualizeOpponentUserFromDb();
            }

            setOnlineStatus(opponentUser);
        }
    }

    private void actualizeOpponentUserFromDb() {
        QMUser opponentUserFromDb = QMUserService.getInstance().getUserCache().get((long) opponentUser.getId());

        if (opponentUserFromDb != null) {
            opponentUser = opponentUserFromDb;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.private_dialog_menu, menu);

        MenuItem audio = menu.findItem(R.id.action_audio_call);
        MenuItem video = menu.findItem(R.id.switch_camera_toggle);

        if (oppUsername == 148490 || oppUsername == 148478 || oppUsername == 148492 || oppUsername == 44402) {
            audio.setVisible(false);
            video.setVisible(false);
        }

        return true;
    }


    private ActionMode.Callback mMenuActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.message_delete_menu, menu);
            Drawable drawable = menu.findItem(R.id.action_delete).getIcon();

            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(PrivateDialogActivity.this, R.color.green));
            menu.findItem(R.id.action_delete).setIcon(drawable);

            Drawable drawable_copy = menu.findItem(R.id.action_copy).getIcon();
            drawable_copy = DrawableCompat.wrap(drawable_copy);
            DrawableCompat.setTint(drawable_copy, ContextCompat.getColor(PrivateDialogActivity.this, R.color.green));
            menu.findItem(R.id.action_copy).setIcon(drawable_copy);

            Drawable drawable_share = menu.findItem(R.id.action_share).getIcon();
            drawable_share = DrawableCompat.wrap(drawable_share);
            DrawableCompat.setTint(drawable_share, ContextCompat.getColor(PrivateDialogActivity.this, R.color.green));
            menu.findItem(R.id.action_share).setIcon(drawable_share);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    deleteMessages();
                    break;
                case R.id.action_copy:
                    if (selectedMessagesList.size() == 1 && selectedMessagesList.get(0).getAttachments() == null) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Message", selectedMessagesList.get(0).getBody());
                        clipboard.setPrimaryClip(clip);
                    } else
                        Toast.makeText(PrivateDialogActivity.this, getString(R.string.share_copy_err), Toast.LENGTH_SHORT).show();
                    break;

                case R.id.action_reply:
                    if (longPressPosition != -1) {
                        //if(ConnectycubeChatAdapter.TextMessageHolder)
                        if (selectedMessagesList.size() == 1) {

                            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View v = vi.inflate(R.layout.layout_reply_message, null);

                            ImageView mIvImage = (ImageView) v.findViewById(R.id.ivImage);
                            if (selectedMessagesList.get(0).getAttachment() != null) {
                                Attachment.Type fileType = selectedMessagesList.get(0).getAttachment().getType();
                                if (fileType.name().equals("IMAGE") || fileType.name().equals("VIDEO") || fileType.name().equals("LOCATION")) {
                                    mIvImage.setVisibility(View.VISIBLE);
                                    messagesRecyclerView.getLayoutManager().findViewByPosition(longPressPosition);
                                    //Glide.with(PrivateDialogActivity.this).load(combinationMessagesList.get(longPressPosition).getAttachment().getRemoteUrl()).fitCenter().into(mIvImage);
                                }
                            }


                            TextView tvMessage = (TextView) v.findViewById(R.id.tvName);
                            if (opponentUser != null && opponentUser.getId().equals(selectedMessagesList.get(0).getDialogOccupant().getUser().getId())) {
                                tvMessage.setText(selectedMessagesList.get(0).getDialogOccupant().getUser().getFullName());
                            } else {
                                tvMessage.setText("You");
                            }

                            EmojiconTextView tvType = /*(TextView)*/ v.findViewById(R.id.tvTypeMessage);
                            tvType.setUseSystemDefault(false);
                            tvType.setText(selectedMessagesList.get(0).getBody());

                            ImageView mIvClear = (ImageView) v.findViewById(R.id.ivClear);
                            mIvClear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (mActionMode != null) {
                                        mActionMode.finish();
                                    }
                                    finishAction();
                                    //v.setVisibility(View.GONE);
                                    clearLayout();
                                    clearReply();
                                }
                            });
                            Log.v("ReplyMessage MAIN", selectedMessagesList.get(0).toString());
                            setCustomParameter(selectedMessagesList.get(0).getReplyMessageString(), true);
                            //finishActionForReply();
                            isReply = true;
                            setLayoutForReply(v);
                        }
                    }
                    break;
                case R.id.action_share:
                    if (selectedMessagesList.size() == 1 && selectedMessagesList.get(0).getAttachments() == null) {
                        String shareBody = selectedMessagesList.get(0).getBody();
                        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_via)));
                    } else
                        Toast.makeText(PrivateDialogActivity.this, getString(R.string.share_copy_err), Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            finishAction();
        }
    };


    private void finishAction() {
        selectedMessagesList.clear();
        messagesAdapter.notifyDataSetChanged();
        mActionMode = null;
        isMultipleMessageSelect = false;
        longPressPosition = -1;
        isReply = false;
    }

    private void finishActionForReply() {
        mActionMode.finish();
        selectedMessagesList.clear();
        messagesAdapter.notifyDataSetChanged();
        mActionMode = null;
        isMultipleMessageSelect = false;
        longPressPosition = -1;
    }


    private void deleteMessages() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_delete_dialog));

        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                if (selectedMessagesList != null) {
                    Set<String> messagesIds = new HashSet<>();

                    for (int i = 0; i < selectedMessagesList.size(); i++) {
                        messagesIds.add(selectedMessagesList.get(i).getMessageId());
                    }
                    ConnectycubeRestChatService.deleteMessages(messagesIds, false).performAsync(new EntityCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid, Bundle bundle) {
                            if (combinationMessagesList.size() > 0) {
                                for (int i = 0; i < selectedMessagesList.size(); i++) {
                                    DataManager.getInstance().getMessageDataManager().deleteMessageById(selectedMessagesList.get(i).getMessageId());
                                    combinationMessagesList.remove(selectedMessagesList.get(i));
                                }
                            }
                            mActionMode.finish();
                            /*mActionMode = null;
                            isMultipleMessageSelect = false;
                            selectedMessagesList.clear();
                            messagesAdapter.notifyDataSetChanged();*/
                            finishAction();
                        }

                        @Override
                        public void onError(ResponseException e) {
                            e.printStackTrace();

                        }
                    });
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void select(int position) {
        if (mActionMode != null) {
            if (selectedMessagesList.contains(combinationMessagesList.get(position)))
                selectedMessagesList.remove(combinationMessagesList.get(position));
            else
                selectedMessagesList.add(combinationMessagesList.get(position));

            ((PrivateChatMessageAdapter) messagesAdapter).setSelectedMessagesList(selectedMessagesList);

            if (selectedMessagesList.size() > 0)
                mActionMode.setTitle("" + selectedMessagesList.size());
            else {
                mActionMode.setTitle("");
                mActionMode.finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean isFriend = DataManager.getInstance().getFriendDataManager().getByUserId(
                opponentUser.getId()) != null;
        Log.d("OPPO-1", "onOptionsItemSelected = isFriend: " + opponentUser);
        if (!isFriend && item.getItemId() != android.R.id.home) {
            DataManager.getInstance().getFriendDataManager().createOrUpdate(new Friend(opponentUser));
            //QBAddFriendCommand.start(PrivateDialogActivity.this, opponentUser.getId());
            /*QBFriendListHelper qbHelper = new QBFriendListHelper(PrivateDialogActivity.this);
            try {
                qbHelper.addFriend(opponentUser.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            //ToastUtils.shortToast(R.string.dialog_friend_sent);
            //return true;
        }
        switch (item.getItemId()) {
            case R.id.action_audio_call:
                Log.d("AYIK", "privatedialog:process voice call");
                callToUser(opponentUser, RTCTypes.ConferenceType.CONFERENCE_TYPE_AUDIO);
                break;
            case R.id.switch_camera_toggle:
                Log.d("AYIK", "privatedialog:process video call");
                callToUser(opponentUser, RTCTypes.ConferenceType.CONFERENCE_TYPE_VIDEO);
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void checkMessageSendingPossibility() {
        boolean enable = dataManager.getFriendDataManager().existsByUserId(opponentUser.getId()) && isNetworkAvailable();
        //checkMessageSendingPossibility(enable);
    }

    @OnClick(R.id.toolbar)
    void openProfile(View view) {
        UserProfileActivity.start(this, opponentUser.getId());
    }

    @Override
    protected void initFields() {
        super.initFields();
        friendObserver = new FriendObserver();
        typingMessageBroadcastReceiver = new TypingStatusBroadcastReceiver();
        opponentUser = (QMUser) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_OPPONENT);
        title = opponentUser.getFullName();

        mDbHelper = new DbHelper(this);
        if (StringUtils.isNumeric(opponentUser.getFullName())) {
            String name = mDbHelper.getNamebyNumber(opponentUser.getFullName());
            if (!name.isEmpty())
                title = name;
        }
    }

    @Override
    protected void registerBroadcastReceivers() {
        super.registerBroadcastReceivers();
        localBroadcastManager.registerReceiver(typingMessageBroadcastReceiver,
                new IntentFilter(QBServiceConsts.TYPING_MESSAGE));
    }

    @Override
    protected void unregisterBroadcastReceivers() {
        super.unregisterBroadcastReceivers();
        localBroadcastManager.unregisterReceiver(typingMessageBroadcastReceiver);
    }

    @Override
    protected void addObservers() {
        super.addObservers();
        dataManager.getFriendDataManager().addObserver(friendObserver);
    }

    @Override
    protected void deleteObservers() {
        super.deleteObservers();
        dataManager.getFriendDataManager().deleteObserver(friendObserver);
    }

    private void setOnlineStatus(QMUser user) {
        if (oppUsername == 148490 || oppUsername == 148478 || oppUsername == 148492 || oppUsername == 32350 || oppUsername == 44402) {
            // do nothing
        } else {
            if (user != null) {
                if (friendListHelper != null) {
                    String offlineStatus = getString(R.string.last_seen, DateUtils.toTodayYesterdayShortDateWithoutYear2(user.getLastRequestAt() != null ?
                                    user.getLastRequestAt().getTime() : 0),
                            DateUtils.formatDateSimpleTime(user.getLastRequestAt() != null ? user.getLastRequestAt().getTime() : 0));
                    setActionBarSubtitle(
                            OnlineStatusUtils.getOnlineStatus(this, friendListHelper.isUserOnline(user.getId()), offlineStatus));
                }
            }
        }

    }

    public void sendMessage(View view) {
        if (isReply == false) {
            sendMessage(false);
        } else {
            isReply = false;
            clearLayout();
            sendMessage(true);
            finishActionForReply();
        }
    }

    private void callToUser(QMUser user, RTCTypes.ConferenceType ConferenceType) {
        Log.d("AYIK", "status calltouser " + isChatInitializedAndUserLoggedIn());
        if (!isChatInitializedAndUserLoggedIn()) {
            ToastUtils.longToast(R.string.call_chat_service_is_initializing);
            return;
        }
        List<ConnectycubeUser> connectycubeUserList = new ArrayList<>(1);
        connectycubeUserList.add(UserFriendUtils.createConnectycubeUser(user));
        CallActivity.start(PrivateDialogActivity.this, connectycubeUserList, ConferenceType, null);
    }

    private void updateCurrentChatFromDB() {
        ConnectycubeChatDialog updatedDialog = null;
        if (currentChatDialog != null) {
            updatedDialog = dataManager.getConnectycubeChatDialogDataManager().getByDialogId(currentChatDialog.getDialogId());
        } else {
            finish();
        }

        if (updatedDialog == null) {
            finish();
        } else {
            currentChatDialog = updatedDialog;
            initCurrentDialog();
        }
    }

    private void showTypingStatus() {
        setActionBarSubtitle(R.string.dialog_now_typing);
    }

    private void hideTypingStatus() {
        setOnlineStatus(opponentUser);
    }

    private class FriendObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            if (data != null) {
                String observerKey = ((Bundle) data).getString(FriendDataManager.EXTRA_OBSERVE_KEY);
                if (observerKey.equals(dataManager.getFriendDataManager().getObserverKey())) {
                    updateCurrentChatFromDB();
                    checkMessageSendingPossibility();
                }
            }
        }
    }

    private class TypingStatusBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            int userId = extras.getInt(QBServiceConsts.EXTRA_USER_ID);
            // TODO: now it is possible only for Private chats
            if (currentChatDialog != null && opponentUser != null && userId == opponentUser.getId()) {
                if (ConnectycubeDialogType.PRIVATE.equals(currentChatDialog.getType())) {
                    boolean isTyping = extras.getBoolean(QBServiceConsts.EXTRA_IS_TYPING);
                    if (isTyping) {
                        showTypingStatus();
                    } else {
                        hideTypingStatus();
                    }
                }
            }
        }
    }

    public interface ItemClickListener {
        public void onItemClick(int position);

        public void onAudioItemClick(ConnectycubePlaybackControlView playbackView, int position);

        public void onItemLongClick(int position);
    }
}