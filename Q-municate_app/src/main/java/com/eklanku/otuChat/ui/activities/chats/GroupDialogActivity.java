package com.eklanku.otuChat.ui.activities.chats;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.eklanku.otuChat.ui.activities.location.MapsActivity;
import com.eklanku.otuChat.ui.activities.others.PreviewImageActivity;
import com.eklanku.otuChat.ui.adapters.chats.GroupChatMessagesAdapter;
import com.eklanku.otuChat.utils.ChatDialogUtils;
import com.connectycube.chat.ConnectycubeRestChatService;
import com.connectycube.chat.model.ConnectycubeAttachment;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.storage.model.ConnectycubeFile;
import com.connectycube.core.EntityCallback;
import com.connectycube.core.exception.ResponseException;
import com.eklanku.otuChat.utils.helpers.files.FileDownloadManager;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.CombinationMessage;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoadDialogByIdsCommand;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.Attachment;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.eklanku.otuChat.R;
import com.connectycube.ui.chatmessage.adapter.media.video.ui.VideoPlayerActivity;
import com.connectycube.ui.chatmessage.adapter.media.view.ConnectycubePlaybackControlView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class GroupDialogActivity extends BaseDialogActivity {

    private static final String TAG = GroupDialogActivity.class.getSimpleName();

    private int operationItemPosition;
    private boolean isMultipleMessageSelect = false;
    private ArrayList<CombinationMessage> selectedMessagesList;
    private ActionMode mActionMode;
    private int longPressPosition = -1;
    private boolean isReply = false;

    public static void start(Context context, ArrayList<QMUser> friends) {
        Intent intent = new Intent(context, GroupDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_FRIENDS, friends);
        context.startActivity(intent);
    }

    public static void start(Context context, ConnectycubeChatDialog chatDialog) {
        Intent intent = new Intent(context, GroupDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, chatDialog);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        context.startActivity(intent);
    }

    public static void startForResult(Fragment fragment, ConnectycubeChatDialog chatDialog, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), GroupDialogActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_DIALOG, chatDialog);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actualizeCurrentDialogInfo();
    }

    private void actualizeCurrentDialogInfo() {
        if (currentChatDialog != null) {
            QBLoadDialogByIdsCommand.start(this, new ArrayList<>(Collections.singletonList(currentChatDialog.getDialogId())));
        }
    }

    @Override
    protected void initChatAdapter() {
        messagesAdapter = new GroupChatMessagesAdapter(this, currentChatDialog, combinationMessagesList, new ItemClickListener() {

            @Override
            public void onItemClick(int position) {
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
                        PreviewImageActivity.start(GroupDialogActivity.this, ConnectycubeFile.getPrivateUrlForUID(fileId));
                    else if (fileType.equals("video")) {
                        canPerformLogout.set(false);
                        VideoPlayerActivity.start(GroupDialogActivity.this, Uri.parse(ConnectycubeFile.getPrivateUrlForUID(fileId)));
                    } else if (fileType.equals("location")) {
                        canPerformLogout.set(false);
                        MapsActivity.startMapForResult(GroupDialogActivity.this, connectycubeAttachment.getData());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_dialog_menu, menu);
        return true;
    }

    @Override
    protected void onConnectServiceLocally(QBService service) {
        onConnectServiceLocally();
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
            case R.id.action_group_details:
                GroupDialogDetailsActivity.start(this, currentChatDialog.getDialogId());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void updateActionBar() {
        if (isNetworkAvailable() && currentChatDialog != null) {
            setActionBarTitle(ChatDialogUtils.getTitleForChatDialog(currentChatDialog, dataManager));
            checkActionBarLogo(currentChatDialog.getPhoto(), R.drawable.placeholder_group);
        }
    }

    @Override
    protected void initFields() {
        super.initFields();
        if (currentChatDialog != null) {
            title = ChatDialogUtils.getTitleForChatDialog(currentChatDialog, dataManager);
        }
    }

    public void sendMessage(View view) {
        //sendMessage();
        if (isReply == false) {
            sendMessage(false);
        } else {
            finishActionForReply();
            isReply = false;
            clearLayout();
            sendMessage(true);
        }
    }

    private ActionMode.Callback mMenuActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.message_delete_menu, menu);
            Drawable drawable = menu.findItem(R.id.action_delete).getIcon();

            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, ContextCompat.getColor(GroupDialogActivity.this, R.color.green));
            menu.findItem(R.id.action_delete).setIcon(drawable);

            Drawable drawable_copy = menu.findItem(R.id.action_copy).getIcon();
            drawable_copy = DrawableCompat.wrap(drawable_copy);
            DrawableCompat.setTint(drawable_copy, ContextCompat.getColor(GroupDialogActivity.this, R.color.green));
            menu.findItem(R.id.action_copy).setIcon(drawable_copy);

            Drawable drawable_share = menu.findItem(R.id.action_share).getIcon();
            drawable_share = DrawableCompat.wrap(drawable_share);
            DrawableCompat.setTint(drawable_share, ContextCompat.getColor(GroupDialogActivity.this, R.color.green));
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
                        Toast.makeText(GroupDialogActivity.this, getString(R.string.share_copy_err), Toast.LENGTH_SHORT).show();
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
                                }
                            }

                            TextView tvMessage = (TextView) v.findViewById(R.id.tvName);
                            if ( AppSession.getSession().getUser().getId().equals(selectedMessagesList.get(0).getDialogOccupant().getUser().getId())) {
                                tvMessage.setText("You");
                            } else {
                                tvMessage.setText(selectedMessagesList.get(0).getDialogOccupant().getUser().getFullName());
                            }

                            TextView tvType = (TextView) v.findViewById(R.id.tvTypeMessage);
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
                        Toast.makeText(GroupDialogActivity.this, getString(R.string.share_copy_err), Toast.LENGTH_SHORT).show();
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

            ((GroupChatMessagesAdapter) messagesAdapter).setSelectedMessagesList(selectedMessagesList);

            if (selectedMessagesList.size() > 0)
                mActionMode.setTitle("" + selectedMessagesList.size());
            else {
                mActionMode.setTitle("");
                mActionMode.finish();
            }
        }
    }

    public interface ItemClickListener {
        public void onItemClick(int position);

        public void onAudioItemClick(ConnectycubePlaybackControlView playbackView, int position);

        public void onItemLongClick(int position);
    }

}