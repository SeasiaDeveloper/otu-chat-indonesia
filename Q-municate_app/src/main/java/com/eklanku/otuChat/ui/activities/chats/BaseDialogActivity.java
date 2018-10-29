package com.eklanku.otuChat.ui.activities.chats;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.connectycube.ui.chatmessage.adapter.listeners.LinkPreviewClickListener;
import com.connectycube.ui.chatmessage.adapter.models.LinkPreview;
import com.connectycube.ui.chatmessage.adapter.utils.MessageTextClickMovement;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.adapters.chats.BaseChatMessagesAdapter;
import com.eklanku.otuChat.ui.fragments.dialogs.base.TwoButtonsDialogFragment;
import com.eklanku.otuChat.ui.views.recyclerview.WrapContentLinearLayoutManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.connectycube.chat.ConnectycubeChatService;
import com.connectycube.chat.model.ConnectycubeAttachment;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;
import com.connectycube.storage.model.ConnectycubeFile;
import com.connectycube.core.exception.ResponseException;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.location.MapsActivity;
import com.eklanku.otuChat.ui.activities.others.PreviewImageActivity;
import com.eklanku.otuChat.utils.ClipboardUtils;
import com.eklanku.otuChat.utils.KeyboardUtils;
import com.eklanku.otuChat.utils.StringUtils;
import com.eklanku.otuChat.utils.ToastUtils;
import com.eklanku.otuChat.utils.ValidationUtils;
import com.eklanku.otuChat.utils.helpers.MediaPickHelper;
import com.eklanku.otuChat.utils.helpers.SystemPermissionHelper;
import com.eklanku.otuChat.utils.image.ImageLoaderUtils;
import com.eklanku.otuChat.utils.MediaUtils;
import com.eklanku.otuChat.utils.listeners.ChatUIHelperListener;
import com.eklanku.otuChat.utils.listeners.OnMediaPickedListener;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.CombinationMessage;
import com.quickblox.q_municate_core.qb.commands.QBLoadAttachFileCommand;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoadDialogMessagesCommand;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.ChatUtils;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.managers.DialogDataManager;
import com.quickblox.q_municate_db.managers.DialogNotificationDataManager;
import com.quickblox.q_municate_db.managers.MessageDataManager;
import com.quickblox.q_municate_db.managers.base.BaseManager;
import com.quickblox.q_municate_db.models.Attachment;
import com.quickblox.q_municate_db.models.Dialog;
import com.quickblox.q_municate_db.models.DialogNotification;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.models.Message;
import com.quickblox.q_municate_db.utils.ErrorUtils;
import com.connectycube.ui.chatmessage.adapter.listeners.ChatAttachClickListener;
import com.connectycube.ui.chatmessage.adapter.listeners.ChatMessageLinkClickListener;
import com.connectycube.ui.chatmessage.adapter.media.SingleMediaManager;
import com.connectycube.ui.chatmessage.adapter.media.recorder.ConnectycubeAudioRecorder;
import com.connectycube.ui.chatmessage.adapter.media.recorder.exceptions.MediaRecorderException;
import com.connectycube.ui.chatmessage.adapter.media.recorder.listeners.MediaRecordListener;
import com.connectycube.ui.chatmessage.adapter.media.recorder.view.RecordAudioButton;
import com.connectycube.ui.chatmessage.adapter.media.video.ui.VideoPlayerActivity;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import butterknife.OnTouch;

public abstract class BaseDialogActivity extends BaseLoggableActivity implements
        EmojiconGridFragment.OnEmojiconClickedListener, EmojiconsFragment.OnEmojiconBackspaceClickedListener,
        ChatUIHelperListener, OnMediaPickedListener {

    private static final String TAG = BaseDialogActivity.class.getSimpleName();
    private static final int TYPING_DELAY = 1000;
    private static final int DELAY_SCROLLING_LIST = 300;
    private static final int DELAY_SHOWING_SMILE_PANEL = 200;
    private static final int DELAY_SHOWING_ATTACH_PANEL = 200;
    private static final int DURATION_ANIMATE_ATTACH_PANEL = 300;
    private static final int MESSAGES_PAGE_SIZE = ConstsCore.DIALOG_MESSAGES_PER_PAGE;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int POST_DELAY_VIEW = 1000;
    private static final int DURATION_VIBRATE = 100;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

    @Bind(R.id.messages_swiperefreshlayout)
    SwipeRefreshLayout messageSwipeRefreshLayout;

    @Bind(R.id.messages_recycleview)
    RecyclerView messagesRecyclerView;

    @Bind(R.id.message_edittext)
    EditText messageEditText;

    @Bind(R.id.attach_button)
    ImageButton attachButton;

    @Bind(R.id.attach_camera)
    ImageButton attachCamera;

    @Bind(R.id.chat_record_audio_button)
    RecordAudioButton recordAudioButton;

    @Bind(R.id.chat_audio_record_chronometer)
    Chronometer recordChronometer;

    @Bind(R.id.chat_audio_record_bucket_imageview)
    ImageView bucketView;

    @Bind(R.id.layout_chat_audio_container)
    LinearLayout audioLayout;

    @Bind(R.id.chat_audio_record_textview)
    TextView audioRecordTextView;

    @Bind(R.id.linReplyLayout)
    LinearLayout linReplyLayout;

    @Bind(R.id.linMainReplyLayout)
    LinearLayout linMainReplyLayout;

    @Bind(R.id.send_button)
    ImageButton sendButton;

    @Bind(R.id.smile_panel_imagebutton)
    ImageButton smilePanelImageButton;

    @Bind(R.id.view_attach_stub)
    View attachViewPanel;

    private String replyMessage = null;

    private boolean isReplyMessage = false;

    protected ConnectycubeChatDialog currentChatDialog;
    protected Resources resources;
    protected DataManager dataManager;
    protected BaseChatMessagesAdapter messagesAdapter;
    protected List<CombinationMessage> combinationMessagesList;
    protected MediaPickHelper mediaPickHelper;
    protected SingleMediaManager mediaManager;
    protected ConnectycubeAudioRecorder audioRecorder;

    private MessagesTextViewLinkClickListener messagesTextViewLinkClickListener;
    private LocationAttachClickListener locationAttachClickListener;
    private ImageAttachClickListener imageAttachClickListener;
    private LinkPreviewClickListener linkPreviewClickListener;
    private VideoAttachClickListener videoAttachClickListener;
    private MediaRecordListenerImpl recordListener;
    private Handler mainThreadHandler;
    private View emojiconsFragment;
    private LoadAttachFileSuccessAction loadAttachFileSuccessAction;
    private LoadDialogMessagesSuccessAction loadDialogMessagesSuccessAction;
    private LoadDialogMessagesFailAction loadDialogMessagesFailAction;
    private Timer typingTimer;
    private boolean isTypingNow;
    private Observer dialogObserver;
    private Observer messageObserver;
    private Observer dialogNotificationObserver;
    private BroadcastReceiver updatingDialogBroadcastReceiver;
    private SystemPermissionHelper systemPermissionHelper;
    private boolean isLoadingMessages;

    private BlockingQueue<Runnable> threadQueue;
    private ThreadPoolExecutor threadPool;
    private boolean needUpdatePosition;
    private Vibrator vibro;
    private int lastVisiblePosition;
    private MessagesScrollListener messagesScrollListener;

    int key = 0;

    @Override
    protected int getContentResId() {
        return R.layout.activity_dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();

        checkMessageSendingPossibility(true);

        if (currentChatDialog == null) {
            finish();
        }

        setUpActionBarWithUpButton();

        initCustomUI();
        initCustomListeners();
        initThreads();
        initAudioRecorder();

        addActions();
        addObservers();
        registerBroadcastReceivers();

        initChatAdapter();

        initMessagesRecyclerView();
        initMediaManager();

        hideSmileLayout();

        if (isNetworkAvailable()) {
            deleteTempMessagesAsync();
        }

        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                getSize();
            }
        });

        messageEditText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                if (isKeyboardShown(messageEditText.getRootView())) {
                    Toast.makeText(BaseDialogActivity.this, "KEYOBARD ACTIVE", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BaseDialogActivity.this, "KEYOBARD ACTIVE", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    private boolean isKeyboardShown(View rootView) {
        /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
        /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

        Log.d(TAG, "isKeyboardShown ? " + isKeyboardShown + ", heightDiff:" + heightDiff + ", density:" + dm.density
                + "root view height:" + rootView.getHeight() + ", rect:" + r);

        return isKeyboardShown;
    }


    private void getSize() {
        if (messageEditText.getLineCount() == messageEditText.getMaxLines()) {
            messageEditText.setMaxLines((messageEditText.getLineCount() + 1));
        }
        // Log.e("lenth", editText.getContentDescription().length()+"");

    }

    @OnTextChanged(R.id.message_edittext)
    void messageEditTextChanged(CharSequence charSequence) {
        setActionButtonVisibility(charSequence);

        if (currentChatDialog != null) {
            if (!isTypingNow) {
                isTypingNow = true;
                sendTypingStatus();
            }
            checkStopTyping();
        }
    }

    @OnClick(R.id.message_edittext)
    void messageEditClicked() {
        hideAttachPanelIfNeed();
    }

    @OnFocusChange(R.id.message_edittext)
    void messageEditFocusChanged() {
        hideAttachPanelIfNeed();
    }

    @OnTouch(R.id.message_edittext)
    boolean touchMessageEdit() {
        hideSmileLayout();
        scrollMessagesWithDelay();
        return false;
    }

    @OnClick(R.id.smile_panel_imagebutton)
    void smilePanelImageButtonClicked() {
        hideAttachPanelIfNeed();
        visibleOrHideSmilePanel();
        scrollMessagesWithDelay();
    }

    @OnClick(R.id.attach_button)
    void attachFile() {
        hideSmilePanelIfNeed();
        showOrHideAttachPanel();
    }

    @OnClick(R.id.attach_camera)
    void attachCamera(View view) {
        mediaPickHelper.pickAnMediaFromActivity(this, MediaUtils.CAMERA_VIDEO_REQUEST_CODE);
    }

    @OnClick(R.id.btn_attach_document)
    void attachPanelDocument(View view) {
        Toast.makeText(this, "Comming soon", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_attach_camera)
    void attachPanelCamera(View view) {
        mediaPickHelper.pickAnMediaFromActivity(this, MediaUtils.CAMERA_PHOTO_REQUEST_CODE);
    }

    @OnClick(R.id.btn_attach_gallery)
    void attachPanelGallery(View view) {
        mediaPickHelper.pickAnMediaFromActivity(this, MediaUtils.IMAGE_REQUEST_CODE);
    }

    @OnClick(R.id.btn_attach_audio)
    void attachPanelAudio(View view) {
        Toast.makeText(this, "Comming soon", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.btn_attach_location)
    void attachPanelLocation(View view) {
        mediaPickHelper.pickAnMediaFromActivity(this, MediaUtils.LOCATION_REQUEST_CODE);
    }

    @OnClick(R.id.btn_attach_contact)
    void attachPanelContact(View view) {
        Toast.makeText(this, "Comming soon", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (isSmilesLayoutShowing()) {
            hideSmileLayout();
        } else if (isAttachLayoutShowing()) {
            slideDownAttachViewPanel();
        } else {
            returnResult();
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        addChatMessagesAdapterListeners();
        addAudioRecorderListener();
        checkPermissionSaveFiles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginToChatOrShowError();
        restoreDefaultCanPerformLogout();

        loadNextPartMessagesAsync();

        checkMessageSendingPossibility();
        resumeMediaPlayer();

        /*switch (key) {
            case 0:
                Toast.makeText(this, "NOT ACTIVE", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, "ACTIVE", Toast.LENGTH_SHORT).show();
                break;
        }*/

    }

    private void checkLoginToChatOrShowError() {
        Log.d("OPPO-1", "authenticated: 7");
        if (!ConnectycubeChatService.getInstance().isLoggedIn()) {
            showSnackbar(R.string.error_disconnected, Snackbar.LENGTH_INDEFINITE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideSmileLayout();
        checkStartTyping();
        suspendMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeChatMessagesAdapterListeners();
        clearAudioRecorder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelTasks();
        closeCurrentChat();
        removeActions();
        deleteObservers();
        unregisterBroadcastReceivers();
    }

    @Override
    protected void onChatReconnected() {
        loadNextPartMessagesAsync();
        initCurrentDialog();
        checkMessageSendingPossibility();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void returnResult() {
        if (getCallingActivity() != null) {
            Log.i(TAG, "return result to " + getCallingActivity().getShortClassName());
            Intent intent = new Intent();
            intent.putExtra(QBServiceConsts.EXTRA_DIALOG_ID, currentChatDialog.getDialogId());
            intent.putExtra(QBServiceConsts.EXTRA_DIALOG_UPDATE_POSITION, needUpdatePosition);
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    public void onConnectedToService(QBService service) {
        super.onConnectedToService(service);
        onConnectServiceLocally(service);
        initCurrentDialog();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(messageEditText, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(messageEditText);
    }

    @Override
    public void onMediaPicked(int requestCode, Attachment.Type type, Object attachment) {
        canPerformLogout.set(true);
        if (ValidationUtils.validateAttachment(getSupportFragmentManager(), getResources().getStringArray(R.array.supported_attachment_types), type, attachment)) {
            startLoadAttachFile(type, attachment, currentChatDialog.getDialogId());
        }
    }

    @Override
    public void onMediaPickClosed(int requestCode) {
        canPerformLogout.set(true);
    }

    @Override
    public void onMediaPickError(int requestCode, Exception e) {
        canPerformLogout.set(true);
        ErrorUtils.logError(e);
    }

    @Override
    public void onScreenResetPossibilityPerformLogout(boolean canPerformLogout) {
        this.canPerformLogout.set(canPerformLogout);
    }

    @Override
    protected void checkShowingConnectionError() {
        if (!isNetworkAvailable()) {
            setActionBarTitle(getString(R.string.dlg_internet_connection_is_missing));
            setActionBarIcon(null);
        } else {
            setActionBarTitle(title);
            updateActionBar();
        }
    }

    private void resumeMediaPlayer() {
        if (mediaManager.isMediaPlayerReady()) {
            mediaManager.resumePlay();
        }
    }

    private void suspendMediaPlayer() {
        if (mediaManager.isMediaPlayerReady()) {
            mediaManager.suspendPlay();
        }
    }

    private void deleteTempMessagesAsync() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                deleteTempMessages();
            }
        });
    }

    private void loadNextPartMessagesAsync() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                loadNextPartMessagesFromDb(false, true);
            }
        });
    }

    private void cancelTasks() {
        threadPool.shutdownNow();
    }

    private void initThreads() {
        threadQueue = new LinkedBlockingQueue<>();
        threadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, threadQueue);
        threadPool.allowCoreThreadTimeOut(true);
    }

    private void initAudioRecorder() {
        audioRecorder = ConnectycubeAudioRecorder.newBuilder()
                // Required
                .useInBuildFilePathGenerator(this)
                .setDuration(ConstsCore.MAX_RECORD_DURATION_IN_SEC)
                .build();
    }

    private void restoreDefaultCanPerformLogout() {
        if (!canPerformLogout.get()) {
            canPerformLogout.set(true);
        }
    }

    private void addChatMessagesAdapterListeners() {
        messagesAdapter.setMessageTextViewLinkClickListener(messagesTextViewLinkClickListener, false);
        messagesAdapter.setAttachLocationClickListener(locationAttachClickListener);
        messagesAdapter.setAttachImageClickListener(imageAttachClickListener);
        messagesAdapter.setAttachVideoClickListener(videoAttachClickListener);
        messagesAdapter.setLinkPreviewClickListener(linkPreviewClickListener, false);
    }

    private void addAudioRecorderListener() {
        audioRecorder.setMediaRecordListener(recordListener);
    }

    private void removeChatMessagesAdapterListeners() {
        if (messagesAdapter != null) {
            messagesAdapter.removeMessageTextViewLinkClickListener();
            messagesAdapter.removeLocationImageClickListener(locationAttachClickListener);
            messagesAdapter.removeAttachImageClickListener(imageAttachClickListener);
            messagesAdapter.removeAttachVideoClickListener(videoAttachClickListener);
            messagesAdapter.removeLinkPreviewClickListener();
        }
    }

    private void clearAudioRecorder() {
        if (audioRecorder != null) {
            audioRecorder.removeMediaRecordListener();
            stopRecordIfNeed();
        }
    }

    private void stopRecordIfNeed() {
        if (audioRecorder.isRecording()) {
            Log.d(TAG, "stopRecordIfNeed");
            stopRecord();
        }
    }

    @Override
    protected void performLoginChatSuccessAction(Bundle bundle) {
        super.performLoginChatSuccessAction(bundle);

        checkMessageSendingPossibility();
        startLoadDialogMessages(false);
    }


    protected void setLayoutForReply(View viewReply) {
        try {
            linMainReplyLayout.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            viewReply.setLayoutParams(params);
            linReplyLayout.addView(viewReply);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void clearLayout() {
        try {
            linMainReplyLayout.setVisibility(View.GONE);
            linReplyLayout.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void clearReply() {
        isReplyMessage = false;
        replyMessage = null;
    }

    protected void initFields() {
        mainThreadHandler = new Handler(Looper.getMainLooper());
        resources = getResources();
        dataManager = DataManager.getInstance();
        loadAttachFileSuccessAction = new LoadAttachFileSuccessAction();
        loadDialogMessagesSuccessAction = new LoadDialogMessagesSuccessAction();
        loadDialogMessagesFailAction = new LoadDialogMessagesFailAction();
        typingTimer = new Timer();
        dialogObserver = new DialogObserver();
        messageObserver = new MessageObserver();
        dialogNotificationObserver = new DialogNotificationObserver();
        updatingDialogBroadcastReceiver = new UpdatingDialogBroadcastReceiver();
        appSharedHelper.saveNeedToOpenDialog(false);
        mediaPickHelper = new MediaPickHelper();
        systemPermissionHelper = new SystemPermissionHelper(this);
        messagesTextViewLinkClickListener = new MessagesTextViewLinkClickListener();
        locationAttachClickListener = new LocationAttachClickListener();
        imageAttachClickListener = new ImageAttachClickListener();
        linkPreviewClickListener = new LinkPreviewClickListenerImpl();
        videoAttachClickListener = new VideoAttachClickListener();
        recordListener = new MediaRecordListenerImpl();
        currentChatDialog = (ConnectycubeChatDialog) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_DIALOG);
        combinationMessagesList = new ArrayList<>();
        vibro = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        messagesScrollListener = new MessagesScrollListener();
    }

    private void initCustomUI() {
        emojiconsFragment = _findViewById(R.id.emojicon_fragment);
    }

    private void initCustomListeners() {
        messageSwipeRefreshLayout.setOnRefreshListener(new RefreshLayoutListener());
        recordAudioButton.setRecordTouchListener(new RecordTouchListener());
        recordChronometer.setOnChronometerTickListener(new ChronometerTickListener());
    }

    protected abstract void initChatAdapter();

    protected void initMessagesRecyclerView() {
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //use deprecated listener for support old devices
        messagesRecyclerView.setOnScrollListener(messagesScrollListener);
    }

    protected void initMediaManager() {
        mediaManager = messagesAdapter.getMediaManagerInstance();
    }

    protected void addActions() {
        addAction(QBServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION, loadAttachFileSuccessAction);
        addAction(QBServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION, failAction);

        addAction(QBServiceConsts.LOAD_DIALOG_MESSAGES_SUCCESS_ACTION, loadDialogMessagesSuccessAction);
        addAction(QBServiceConsts.LOAD_DIALOG_MESSAGES_FAIL_ACTION, loadDialogMessagesFailAction);

        updateBroadcastActionList();
    }

    protected void removeActions() {
        removeAction(QBServiceConsts.LOAD_ATTACH_FILE_SUCCESS_ACTION);
        removeAction(QBServiceConsts.LOAD_ATTACH_FILE_FAIL_ACTION);

        removeAction(QBServiceConsts.LOAD_DIALOG_MESSAGES_SUCCESS_ACTION);
        removeAction(QBServiceConsts.LOAD_DIALOG_MESSAGES_FAIL_ACTION);

        removeAction(QBServiceConsts.ACCEPT_FRIEND_SUCCESS_ACTION);
        removeAction(QBServiceConsts.ACCEPT_FRIEND_FAIL_ACTION);

        removeAction(QBServiceConsts.REJECT_FRIEND_SUCCESS_ACTION);
        removeAction(QBServiceConsts.REJECT_FRIEND_FAIL_ACTION);

        updateBroadcastActionList();
    }

    protected void registerBroadcastReceivers() {
        localBroadcastManager.registerReceiver(updatingDialogBroadcastReceiver,
                new IntentFilter(QBServiceConsts.UPDATE_DIALOG));
    }

    protected void unregisterBroadcastReceivers() {
        localBroadcastManager.unregisterReceiver(updatingDialogBroadcastReceiver);
    }

    protected void addObservers() {
        dataManager.getConnectycubeChatDialogDataManager().addObserver(dialogObserver);
        dataManager.getMessageDataManager().addObserver(messageObserver);
        dataManager.getDialogNotificationDataManager().addObserver(dialogNotificationObserver);
    }

    protected void deleteObservers() {
        dataManager.getConnectycubeChatDialogDataManager().deleteObserver(dialogObserver);
        dataManager.getMessageDataManager().deleteObserver(messageObserver);
        dataManager.getDialogNotificationDataManager().deleteObserver(dialogNotificationObserver);
    }

    protected void updateData() {
        updateActionBar();
        updateMessagesList();
    }

    protected void onConnectServiceLocally() {
    }

    protected void deleteTempMessages() {
        List<DialogOccupant> dialogOccupantsList = dataManager.getDialogOccupantDataManager()
                .getDialogOccupantsListByDialogId(currentChatDialog.getDialogId());
        List<Long> dialogOccupantsIdsList = ChatUtils.getIdsFromDialogOccupantsList(dialogOccupantsList);
        dataManager.getMessageDataManager().deleteTempMessages(dialogOccupantsIdsList);
    }

    private void hideSmileLayout() {
        emojiconsFragment.setVisibility(View.GONE);
        setSmilePanelIcon(R.drawable.ic_smile_green);
    }

    private void showSmileLayout() {
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                emojiconsFragment.setVisibility(View.VISIBLE);
                setSmilePanelIcon(R.drawable.ic_keyboard_dark);
            }
        }, DELAY_SHOWING_SMILE_PANEL);
    }

    protected void checkActionBarLogo(String url, int resId) {
        if (!TextUtils.isEmpty(url)) {
            loadActionBarLogo(url);
        } else {
            setDefaultActionBarLogo(resId);
        }
    }

    private void checkPermissionSaveFiles() {
        boolean permissionSaveFileWasRequested = appSharedHelper.isPermissionsSaveFileWasRequested();
        if (!systemPermissionHelper.isAllPermissionsGrantedForSaveFile() && !permissionSaveFileWasRequested) {
            systemPermissionHelper.requestPermissionsForSaveFile();
            appSharedHelper.savePermissionsSaveFileWasRequested(true);
        }
    }

    protected void loadActionBarLogo(String logoUrl) {
        ImageLoader.getInstance().loadImage(logoUrl, ImageLoaderUtils.UIL_USER_AVATAR_DISPLAY_OPTIONS,
                new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedBitmap) {
                        setActionBarIcon(
                                MediaUtils.getRoundIconDrawable(BaseDialogActivity.this, loadedBitmap));
                    }
                });
    }

    protected void setDefaultActionBarLogo(int drawableResId) {
        setActionBarIcon(MediaUtils
                .getRoundIconDrawable(this, BitmapFactory.decodeResource(getResources(), drawableResId)));
    }

    protected void setCustomParameter(String replyMessage, boolean isReplyMessage) {
        this.replyMessage = replyMessage;
        this.isReplyMessage = isReplyMessage;
    }

    protected void startLoadAttachFile(final Attachment.Type type, final Object attachment, final String dialogId) {
        TwoButtonsDialogFragment.show(getSupportFragmentManager(), getString(R.string.dialog_confirm_sending_attach,
                StringUtils.getAttachmentNameByType(this, type)), false,
                new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        switch (type) {
                            case LOCATION:
                                sendMessageWithAttachment(dialogId, Attachment.Type.LOCATION, attachment, null);
                                break;
                            case IMAGE:
                            case AUDIO:
                            case VIDEO:
                                showProgress();
                                QBLoadAttachFileCommand.start(BaseDialogActivity.this, (File) attachment, dialogId);
                                break;
                        }
                    }
                });
    }

    protected void startLoadDialogMessages(ConnectycubeChatDialog chatDialog, long lastDateLoad, boolean isLoadOldMessages) {
        Log.v(TAG, "startLoadDialogMessages()");
        if (isChatInitializedAndUserLoggedIn()) {
            Log.v(TAG, "startLoadDialogMessages() --- isChatInitializedAndUserLoggedIn()");
            isLoadingMessages = true;
            QBLoadDialogMessagesCommand.start(this, chatDialog, lastDateLoad, isLoadOldMessages);
        }
    }

    private void setActionButtonVisibility(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence) || TextUtils.isEmpty(charSequence.toString().trim())) {
            sendButton.setVisibility(View.GONE);
            //attachButton.setVisibility(View.VISIBLE);
            attachCamera.setVisibility(View.VISIBLE);
            recordAudioButton.setVisibility(View.VISIBLE);
        } else {
            sendButton.setVisibility(View.VISIBLE);
            //attachButton.setVisibility(View.GONE);
            attachCamera.setVisibility(View.GONE);
            recordAudioButton.setVisibility(View.GONE);
        }
    }

    private void checkStopTyping() {
        typingTimer.cancel();
        typingTimer = new Timer();
        typingTimer.schedule(new TypingTimerTask(), TYPING_DELAY);
    }

    private void checkStartTyping() {
        if (currentChatDialog != null) {
            if (isTypingNow) {
                isTypingNow = false;
                sendTypingStatus();
            }
        }
    }

    private void sendTypingStatus() {
        chatHelper.sendTypingStatusToServer(isTypingNow);
    }

    private void setSmilePanelIcon(int resourceId) {
        smilePanelImageButton.setImageResource(resourceId);
    }

    private boolean isSmilesLayoutShowing() {
        return emojiconsFragment.getVisibility() == View.VISIBLE;
    }

    private boolean isAttachLayoutShowing() {
        return attachViewPanel.getVisibility() == View.VISIBLE;
    }

    protected boolean needScrollBottom(int countAddedMessages) {
        if (lastVisiblePosition + countAddedMessages < messagesAdapter.getItemCount() - 1) {
            return false;
        }

        return true;
    }

    protected void scrollMessagesToBottom(int countAddedMessages) {
        if (needScrollBottom(countAddedMessages)) {
            scrollMessagesWithDelay();
        }
    }

    private void scrollMessagesWithDelay() {
        mainThreadHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                messagesRecyclerView.scrollToPosition(messagesAdapter.getItemCount() - 1);
            }
        }, DELAY_SCROLLING_LIST);
    }

    protected void sendMessage() {
        boolean error = false;
        try {
            chatHelper.sendChatMessage(messageEditText.getText().toString());
        } catch (ResponseException e) {
            ErrorUtils.showError(this, e);
            error = true;
        } catch (IllegalStateException e) {
            ErrorUtils.showError(this, this.getString(
                    com.quickblox.q_municate_core.R.string.dlg_not_joined_room));
            error = true;
        } catch (Exception e) {
            ErrorUtils.showError(this, e);
            error = true;
        }

        if (!error) {
            messageEditText.setText(ConstsCore.EMPTY_STRING);
        }
    }

    protected void sendMessage(boolean isReply) {
        boolean error = false;
        try {
            if (isReply) {
                chatHelper.sendChatReplyMessage(messageEditText.getText().toString(), replyMessage);
            } else {
                chatHelper.sendChatMessage(messageEditText.getText().toString());
            }
        } catch (ResponseException e) {
            ErrorUtils.showError(this, e);
            error = true;
        } catch (IllegalStateException e) {
            ErrorUtils.showError(this, this.getString(
                    com.quickblox.q_municate_core.R.string.dlg_not_joined_room));
            error = true;
        } catch (Exception e) {
            ErrorUtils.showError(this, e);
            error = true;
        }

        if (!error) {
            messageEditText.setText(ConstsCore.EMPTY_STRING);
        }
    }

    protected void startLoadDialogMessages(final boolean isLoadOldMessages) {
        if (currentChatDialog == null || isLoadingMessages) {
            return;
        }
        showActionBarProgress();
        long messageDateSent = getMessageDateForLoadByCurrentList(isLoadOldMessages);
        startLoadDialogMessages(currentChatDialog, messageDateSent, isLoadOldMessages);

    }

    protected long getMessageDateForLoad(boolean isLoadOldMessages) {
        long messageDateSent;
        List<DialogOccupant> dialogOccupantsList = dataManager.getDialogOccupantDataManager().getDialogOccupantsListByDialogId(currentChatDialog.getDialogId());
        List<Long> dialogOccupantsIdsList = ChatUtils.getIdsFromDialogOccupantsList(dialogOccupantsList);

        Message message;
        DialogNotification dialogNotification;

        message = dataManager.getMessageDataManager().getMessageByDialogId(isLoadOldMessages, dialogOccupantsIdsList);
        dialogNotification = dataManager.getDialogNotificationDataManager().getDialogNotificationByDialogId(isLoadOldMessages, dialogOccupantsIdsList);
        messageDateSent = ChatUtils.getDialogMessageCreatedDate(!isLoadOldMessages, message, dialogNotification);

        return messageDateSent;
    }

    protected long getMessageDateForLoadByCurrentList(boolean isLoadOld) {
        if (combinationMessagesList.size() == 0) {
            return 0;
        }

        return !isLoadOld
                ? combinationMessagesList.get(combinationMessagesList.size() - 1).getCreatedDate()
                : combinationMessagesList.get(0).getCreatedDate();
    }

    protected void initCurrentDialog() {
        if (service != null) {
            Log.v(TAG, "chatHelper = " + chatHelper + "\n dialog = " + currentChatDialog);
            if (chatHelper != null && currentChatDialog != null) {
                try {
                    chatHelper.initCurrentChatDialog(currentChatDialog, generateBundleToInitDialog());
                } catch (ResponseException e) {
                    ErrorUtils.showError(this, e.getMessage());
                    finish();
                }
            }
        } else {
            Log.v(TAG, "service == null");
        }
    }

    private void closeCurrentChat() {
        if (chatHelper != null && currentChatDialog != null) {
            chatHelper.closeChat(currentChatDialog, generateBundleToInitDialog());
        }
        currentChatDialog = null;
    }

    protected List<CombinationMessage>
    buildLimitedCombinationMessagesListByDate(long createDate, boolean moreDate, long limit) {
        if (currentChatDialog == null || currentChatDialog.getDialogId() == null) {
            return new ArrayList<>();
        }

        Log.d(TAG, "selection parameters: createDate = " + createDate + " moreDate = " + moreDate + " limit = " + limit);

        String currentDialogId = currentChatDialog.getDialogId();
        List<Message> messagesList = dataManager.getMessageDataManager()
                .getMessagesByDialogIdAndDate(currentDialogId, createDate, moreDate, limit);
        Log.d(TAG, " messagesList.size() = " + messagesList.size());

        List<DialogNotification> dialogNotificationsList = dataManager.getDialogNotificationDataManager()
                .getDialogNotificationsByDialogIdAndDate(currentDialogId, createDate, moreDate, limit);
        Log.d(TAG, " dialogNotificationsList.size() = " + dialogNotificationsList.size());
        List<CombinationMessage> combinationMessages = ChatUtils.createLimitedCombinationMessagesList(messagesList, dialogNotificationsList, (int) limit);
        return combinationMessages;
    }

    protected void loadNextPartMessagesFromDb(final boolean isLoadOld, final boolean needUpdateAdapter) {
        long messageDate = getMessageDateForLoadByCurrentList(isLoadOld);

        final List<CombinationMessage> requestedMessages = buildLimitedCombinationMessagesListByDate(
                messageDate, !isLoadOld, ConstsCore.DIALOG_MESSAGES_PER_PAGE);

        if (isLoadOld) {
            combinationMessagesList.addAll(0, requestedMessages);
        } else {
            combinationMessagesList.addAll(requestedMessages);
        }

        if (needUpdateAdapter) {
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateMessagesAdapter(isLoadOld, requestedMessages.size());
                    startLoadDialogMessages(false);
                }
            });
        }
    }

    private void updateMessagesAdapter(boolean isLoadOld, int partSize) {
        messagesAdapter.setList(combinationMessagesList, !isLoadOld);

        if (isLoadOld) {
            messagesAdapter.notifyItemRangeInserted(0, partSize);
        } else {
            scrollMessagesToBottom(partSize);
        }
    }

    private void hideSmilePanelIfNeed() {
        if (isSmilesLayoutShowing()) {
            hideSmileLayout();
            KeyboardUtils.hideKeyboard(BaseDialogActivity.this);
        }
    }

    private void visibleOrHideSmilePanel() {
        if (isSmilesLayoutShowing()) {
            hideSmileLayout();
            KeyboardUtils.showKeyboard(BaseDialogActivity.this);
        } else {
            KeyboardUtils.hideKeyboard(BaseDialogActivity.this);
            showSmileLayout();
        }
    }

    private void showOrHideAttachPanel() {
        if (isAttachLayoutShowing()) {
            slideDownAttachViewPanel();
        } else {
            if (KeyboardUtils.isKeyboardShown(this)) {
                messageEditText.clearFocus();
                KeyboardUtils.hideKeyboard(BaseDialogActivity.this);
                slideUpAttachViewPanelDelayed();
            } else {
                messageEditText.clearFocus();
                slideUpAttachViewPanel();
            }
        }
    }

    protected void hideAttachPanelIfNeed() {
        if (isAttachLayoutShowing()) {
            attachViewPanel.setVisibility(View.GONE);
        }
    }

    protected void slideUpAttachViewPanelDelayed() {
        attachViewPanel.postDelayed(this::slideUpAttachViewPanel, DELAY_SHOWING_ATTACH_PANEL);
    }

    protected void slideUpAttachViewPanel() {
        attachViewPanel.setVisibility(View.VISIBLE);
    }

    protected void slideDownAttachViewPanel() {
        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, attachViewPanel.getHeight());
        animate.setDuration(DURATION_ANIMATE_ATTACH_PANEL);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                attachViewPanel.setVisibility(View.GONE);
                attachViewPanel.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        attachViewPanel.startAnimation(animate);
    }

    protected void checkMessageSendingPossibility(boolean enable) {
        messageEditText.setEnabled(enable);
        smilePanelImageButton.setEnabled(enable);
        attachButton.setEnabled(enable);
        attachCamera.setEnabled(enable);
        recordAudioButton.setEnabled(enable);
    }

    private void replaceMessageInCurrentList(CombinationMessage combinationMessage) {
        if (combinationMessagesList.contains(combinationMessage)) {
            int positionOldMessage = combinationMessagesList.indexOf(combinationMessage);
            combinationMessagesList.set(positionOldMessage, combinationMessage);
        }
    }

    private void updateMessageItemInAdapter(CombinationMessage combinationMessage) {
        replaceMessageInCurrentList(combinationMessage);
        if (combinationMessagesList.contains(combinationMessage)) {
            messagesAdapter.notifyItemChanged(combinationMessagesList.indexOf(combinationMessage));
        } else {
            addMessageItemToAdapter(combinationMessage);
        }
    }

    private void addMessageItemToAdapter(CombinationMessage combinationMessage) {
        combinationMessagesList.add(combinationMessage);
        messagesAdapter.setList(combinationMessagesList, true);
        scrollMessagesToBottom(1);
    }

    private void afterLoadingMessagesActions() {
        messageSwipeRefreshLayout.setRefreshing(false);
        hideActionBarProgress();
        isLoadingMessages = false;
    }

    private boolean checkRecordPermission() {
        if (systemPermissionHelper.isAllAudioRecordPermissionGranted()) {
            return true;
        } else {
            systemPermissionHelper.requestAllPermissionForAudioRecord();
            return false;
        }
    }

    public void startRecord() {
        setMessageAttachViewsEnable(false);
        setRecorderViewsVisibility(View.VISIBLE);
        audioViewVisibility(View.VISIBLE);
        vibrate(DURATION_VIBRATE);
        audioRecorder.startRecord();
        startChronometer();
    }

    public void stopRecordByClick() {
        setMessageAttachViewsEnable(true);
        vibrate(DURATION_VIBRATE);
        stopRecord();
    }

    private void stopRecord() {
        audioViewVisibility(View.GONE);
        stopChronometer();
        audioRecorder.stopRecord();
    }

    public void cancelRecord() {
        stopChronometer();
        setMessageAttachViewsEnable(true);
        setRecorderViewsVisibility(View.GONE);
        animateCanceling();
        vibrate(DURATION_VIBRATE);
        audioViewPostDelayVisibility(View.GONE);
        audioRecorder.cancelRecord();
    }

    private void startChronometer() {
        recordChronometer.setBase(SystemClock.elapsedRealtime());
        recordChronometer.start();
    }

    private void stopChronometer() {
        recordChronometer.stop();
    }

    private void setRecorderViewsVisibility(int visibility) {
        audioRecordTextView.setVisibility(visibility);
        recordChronometer.setVisibility(visibility);
    }

    private void setMessageAttachViewsEnable(boolean enable) {
        messageEditText.setFocusableInTouchMode(enable);
        messageEditText.setFocusable(enable);
        smilePanelImageButton.setEnabled(enable);
        attachButton.setEnabled(enable);
        attachCamera.setEnabled(enable);
    }

    private void audioViewPostDelayVisibility(final int visibility) {
        audioLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                audioViewVisibility(visibility);
            }
        }, POST_DELAY_VIEW);
    }

    private void audioViewVisibility(int visibility) {
        audioLayout.setVisibility(visibility);
    }

    private void vibrate(int duration) {
        vibro.vibrate(duration);
    }

    private void animateCanceling() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        bucketView.startAnimation(shake);
    }

    protected abstract void updateActionBar();

    protected abstract void onConnectServiceLocally(QBService service);

    protected abstract Bundle generateBundleToInitDialog();

    protected abstract void updateMessagesList();

    protected void sendMessageWithAttachment(String dialogId, Attachment.Type attachmentType, Object attachmentObject, String localPath) {
        if (!dialogId.equals(currentChatDialog.getDialogId())) {
            return;
        }
        try {
            if (isReplyMessage && replyMessage != null) {
                chatHelper.sendMessageReplyWithAttachment(attachmentType, attachmentObject, localPath, replyMessage);
                clearReply();
                clearLayout();
            } else {
                chatHelper.sendMessageWithAttachment(attachmentType, attachmentObject, localPath);
            }
        } catch (ResponseException exc) {
            ErrorUtils.showError(this, exc);
        }
    }

    protected abstract void checkMessageSendingPossibility();

    private class MessageObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Log.i(TAG, "==== MessageObserver  'update' ====");
            if (data != null) {
                Bundle observableData = (Bundle) data;
                int action = observableData.getInt(MessageDataManager.EXTRA_ACTION);
                Message message = (Message) observableData.getSerializable(MessageDataManager.EXTRA_OBJECT);

                if (message != null && message.getDialogOccupant() != null && message.getDialogOccupant().getDialog() != null
                        && currentChatDialog.getDialogId().equals(message.getDialogOccupant().getDialog().getDialogId())) {
                    needUpdatePosition = true;
                    CombinationMessage combinationMessage = new CombinationMessage(message);
                    if (action == MessageDataManager.UPDATE_ACTION) {
                        Log.d(TAG, "updated message = " + message);
                        updateMessageItemInAdapter(combinationMessage);
                    } else if (action == MessageDataManager.CREATE_ACTION) {
                        Log.d(TAG, "created message = " + message);
                        addMessageItemToAdapter(combinationMessage);
                    }
                }

                if (action == MessageDataManager.DELETE_BY_ID_ACTION
                        || action == MessageDataManager.DELETE_ACTION) {
                    combinationMessagesList.clear();
                    loadNextPartMessagesFromDb(false, true);
                }
            }
        }
    }

    private class DialogNotificationObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Log.i(TAG, "==== DialogNotificationObserver  'update' ====");
            if (data != null) {
                Bundle observableData = (Bundle) data;
                int action = observableData.getInt(DialogNotificationDataManager.EXTRA_ACTION);
                DialogNotification dialogNotification = (DialogNotification) observableData.getSerializable(DialogNotificationDataManager.EXTRA_OBJECT);

                Log.i(TAG, "==== DialogNotificationObserver  'update' ====observableData= " + observableData);
                if (dialogNotification != null && dialogNotification.getDialogOccupant().getDialog().getDialogId().equals(currentChatDialog.getDialogId())) {
                    needUpdatePosition = true;
                    CombinationMessage combinationMessage = new CombinationMessage(dialogNotification);
                    if (action == DialogNotificationDataManager.UPDATE_ACTION) {
                        Log.d(TAG, "updated dialogNotification = " + dialogNotification);
                        updateMessageItemInAdapter(combinationMessage);
                    } else if (action == DialogNotificationDataManager.CREATE_ACTION) {
                        Log.d(TAG, "created dialogNotification = " + dialogNotification);
                        addMessageItemToAdapter(combinationMessage);
                        if (currentChatDialog != null && ConnectycubeDialogType.PRIVATE.equals(currentChatDialog.getType())) {
                            updateMessagesList();
                            messagesAdapter.notifyDataSetChanged();
                            scrollMessagesToBottom(1);
                        }
                    }

                    if (action == DialogNotificationDataManager.DELETE_BY_ID_ACTION
                            || action == DialogNotificationDataManager.DELETE_ACTION) {
                        combinationMessagesList.clear();
                        loadNextPartMessagesFromDb(false, true);
                    }
                }
            }
        }
    }

    private class DialogObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Log.i(TAG, "==== DialogObserver  'update' ====");
            if (data != null) {
                String observerKey = ((Bundle) data).getString(DialogDataManager.EXTRA_OBSERVE_KEY);
                if (observerKey.equals(dataManager.getConnectycubeChatDialogDataManager().getObserverKey())) {
                    Dialog dialog = (Dialog) ((Bundle) data).getSerializable(BaseManager.EXTRA_OBJECT);
                    currentChatDialog = dataManager.getConnectycubeChatDialogDataManager().getByDialogId(currentChatDialog.getDialogId());
                    if (currentChatDialog != null) {
                        if (dialog != null && dialog.getDialogId().equals(currentChatDialog.getDialogId())) {
                            // need init current dialog after getting from DB
                            initCurrentDialog();
                            updateActionBar();
                        }
                    } else {
                        finish();
                    }
                }
            }
        }
    }

    private class TypingTimerTask extends TimerTask {

        @Override
        public void run() {
            isTypingNow = false;
            sendTypingStatus();
        }
    }

    public class LoadAttachFileSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            ConnectycubeFile file = (ConnectycubeFile) bundle.getSerializable(QBServiceConsts.EXTRA_ATTACH_FILE);
            String dialogId = (String) bundle.getSerializable(QBServiceConsts.EXTRA_DIALOG_ID);
            String localPath = (String) bundle.getSerializable(QBServiceConsts.EXTRA_FILE_PATH);

            sendMessageWithAttachment(dialogId, StringUtils.getAttachmentTypeByFileName(file.getName()), file, localPath);
            hideProgress();
        }
    }

    public class LoadDialogMessagesSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            final int totalEntries = bundle.getInt(QBServiceConsts.EXTRA_TOTAL_ENTRIES,
                    ConstsCore.ZERO_INT_VALUE);
            final long lastMessageDate = bundle.getLong(QBServiceConsts.EXTRA_LAST_DATE_LOAD_MESSAGES,
                    ConstsCore.ZERO_INT_VALUE);
            final boolean isLoadedOldMessages = bundle.getBoolean(QBServiceConsts.EXTRA_IS_LOAD_OLD_MESSAGES);

            String dialogId = bundle.getString(QBServiceConsts.EXTRA_DIALOG_ID);

            Log.d("BaseDialogActivity", "Loading messages finished" + " totalEntries = " + totalEntries
                    + " lastMessageDate = " + lastMessageDate
                    + " isLoadedOldMessages = " + isLoadedOldMessages
                    + " dialogId = " + dialogId);

            if (messagesAdapter != null
                    && totalEntries != ConstsCore.ZERO_INT_VALUE
                    && dialogId.equals(currentChatDialog.getDialogId())) {

                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPartMessagesFromDb(isLoadedOldMessages, false);
                        mainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateMessagesAdapter(isLoadedOldMessages, totalEntries);
                                if (currentChatDialog != null && ConnectycubeDialogType.PRIVATE.equals(currentChatDialog.getType())) {
                                    updateMessagesList();
                                }
                                afterLoadingMessagesActions();
                            }
                        });

                    }
                });

            } else {
                afterLoadingMessagesActions();
            }
        }
    }

    public class LoadDialogMessagesFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            afterLoadingMessagesActions();
        }
    }

    private class UpdatingDialogBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(QBServiceConsts.UPDATE_DIALOG)) {
                updateData();
            }
        }
    }

    private class RefreshLayoutListener implements SwipeRefreshLayout.OnRefreshListener {

        @Override
        public void onRefresh() {
            if (!isNetworkAvailable()) {
                messageSwipeRefreshLayout.setRefreshing(false);
                return;
            }

            if (!isLoadingMessages) {
                if (combinationMessagesList.size() == 0) {
                    startLoadDialogMessages(true);
                    return;
                }

                long oldestMessageInDb = getMessageDateForLoad(true);
                long oldestMessageInCurrentList = combinationMessagesList.get(0).getCreatedDate();

                if ((oldestMessageInCurrentList - oldestMessageInDb) > 0 && oldestMessageInDb != 0) {
                    loadNextPartMessagesFromDb(true, true);
                    messageSwipeRefreshLayout.setRefreshing(false);
                } else {
                    startLoadDialogMessages(true);
                }
            }
        }
    }

    protected class MessagesTextViewLinkClickListener implements ChatMessageLinkClickListener {

        @Override
        public void onLinkClicked(String linkText, MessageTextClickMovement.LinkType qbLinkType, int position) {

            if (!MessageTextClickMovement.LinkType.NONE.equals(qbLinkType)) {
                canPerformLogout.set(false);
            }
        }

        @Override
        public void onLongClick(String text, int positionInAdapter) {

        }

    }

    protected class LocationAttachClickListener implements ChatAttachClickListener {

        @Override
        public void onItemClicked(ConnectycubeAttachment attachment, int position) {
            MapsActivity.startMapForResult(BaseDialogActivity.this, attachment.getData());
        }
    }

    protected class ImageAttachClickListener implements ChatAttachClickListener {

        @Override
        public void onItemClicked(ConnectycubeAttachment attachment, int position) {
            PreviewImageActivity.start(BaseDialogActivity.this, ConnectycubeFile.getPrivateUrlForUID(attachment.getId()));
        }
    }

    protected class VideoAttachClickListener implements ChatAttachClickListener {

        @Override
        public void onItemClicked(ConnectycubeAttachment attachment, int position) {
            canPerformLogout.set(false);
            VideoPlayerActivity.start(BaseDialogActivity.this, Uri.parse(ConnectycubeFile.getPrivateUrlForUID(attachment.getId())));
        }
    }

    protected class RecordTouchListener implements RecordAudioButton.RecordTouchEventListener {

        @Override
        public void onStartClick(View v) {
            if (checkRecordPermission()) {
                startRecord();
            }
        }

        @Override
        public void onCancelClick(View v) {
            cancelRecord();
        }

        @Override
        public void onStopClick(View v) {
            stopRecordByClick();
        }
    }

    protected class ChronometerTickListener implements Chronometer.OnChronometerTickListener {
        private long elapsedSecond;

        @Override
        public void onChronometerTick(Chronometer chronometer) {
            setChronometerAppropriateColor();
        }

        private void setChronometerAppropriateColor() {
            elapsedSecond = TimeUnit.MILLISECONDS.toSeconds(SystemClock.elapsedRealtime() - recordChronometer.getBase());
            if (isStartSecond()) {
                setChronometerBaseColor();
            }
            if (isAlarmSecond()) {
                setChronometerAlarmColor();
            }
        }

        private boolean isStartSecond() {
            return elapsedSecond == 0;
        }

        private boolean isAlarmSecond() {
            return elapsedSecond == ConstsCore.CHRONOMETER_ALARM_SECOND;
        }

        private void setChronometerAlarmColor() {
            recordChronometer.setTextColor(ContextCompat.getColor(BaseDialogActivity.this, android.R.color.holo_red_light));
        }

        private void setChronometerBaseColor() {
            recordChronometer.setTextColor(ContextCompat.getColor(BaseDialogActivity.this, android.R.color.black));
        }
    }

    private class MediaRecordListenerImpl implements MediaRecordListener {

        @Override
        public void onMediaRecorded(File file) {
            audioViewVisibility(View.GONE);
            if (ValidationUtils.validateAttachment(getSupportFragmentManager(), getResources().getStringArray(R.array.supported_attachment_types), Attachment.Type.AUDIO, file)) {
                startLoadAttachFile(Attachment.Type.AUDIO, file, currentChatDialog.getDialogId());
            } else {
                audioRecordErrorAnimate();
            }
        }

        @Override
        public void onMediaRecordError(MediaRecorderException e) {
            audioRecorder.releaseMediaRecorder();
            audioRecordErrorAnimate();
        }

        @Override
        public void onMediaRecordClosed() {
        }

        private void audioRecordErrorAnimate() {
            Animation shake = AnimationUtils.loadAnimation(BaseDialogActivity.this, R.anim.shake_record_button);
            recordAudioButton.startAnimation(shake);
        }
    }

    protected class LinkPreviewClickListenerImpl implements LinkPreviewClickListener {

        @Override
        public void onLinkPreviewClicked(String link, LinkPreview linkPreview, int position) {

        }

        @Override
        public void onLinkPreviewLongClicked(String link, LinkPreview linkPreview, int position) {
            ClipboardUtils.copySimpleTextToClipboard(BaseDialogActivity.this, link);
            ToastUtils.longToast(R.string.link_was_copied);
            Log.v(TAG, linkPreview.toString());
        }
    }

    protected class MessagesScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            WrapContentLinearLayoutManager layoutManager1 = (WrapContentLinearLayoutManager) recyclerView.getLayoutManager();
            lastVisiblePosition = layoutManager1.findLastVisibleItemPosition();
        }
    }


}