package com.eklanku.otuChat.ui.fragments.chats;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.loaders.DialogsListLoader;
import com.eklanku.otuChat.ui.activities.barcode.UserSessionsActivity;
import com.eklanku.otuChat.ui.activities.chats.BroadcastDialogActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsActivity;
import com.eklanku.otuChat.ui.activities.main.MainActivity;

import com.eklanku.otuChat.ui.activities.payment.settingpayment.Register;
import com.eklanku.otuChat.ui.activities.settings.SettingsActivity;
import com.eklanku.otuChat.ui.adapters.chats.DialogsListAdapter;
import com.eklanku.otuChat.ui.fragments.base.BaseLoaderFragment;
import com.eklanku.otuChat.ui.fragments.search.ContactsFragment;
import com.eklanku.otuChat.ui.views.banner.BannerDataManager;
import com.eklanku.otuChat.ui.views.banner.GlideImageLoader;
/*<<<<<<< HEAD
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.helper.CollectionsUtil;
=======*/
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.chat.model.ConnectycubeDialogType;
import com.connectycube.core.helper.CollectionsUtil;
//>>>>>>> origin/feature/migration
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.about.AboutActivity;
import com.eklanku.otuChat.ui.activities.chats.GroupDialogActivity;
import com.eklanku.otuChat.ui.activities.chats.NewMessageActivity;
import com.eklanku.otuChat.ui.activities.chats.PrivateDialogActivity;
import com.eklanku.otuChat.ui.activities.feedback.FeedbackActivity;
import com.eklanku.otuChat.ui.activities.invitefriends.InviteFriendsActivity;
import com.eklanku.otuChat.utils.ToastUtils;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.DialogWrapper;
import com.quickblox.q_municate_core.qb.commands.chat.QBDeleteChatCommand;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoadDialogByIdsCommand;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoadDialogsCommand;
import com.quickblox.q_municate_core.qb.commands.chat.QBLoginChatCompositeCommand;
import com.quickblox.q_municate_core.qb.helpers.QBChatHelper;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.ChatUtils;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.managers.DialogNotificationDataManager;
import com.quickblox.q_municate_db.managers.base.BaseManager;
import com.quickblox.q_municate_db.models.Dialog;
import com.quickblox.q_municate_db.models.DialogNotification;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.models.Message;
import com.quickblox.q_municate_user_cache.QMUserCacheImpl;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.users.model.ConnectycubeUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;

import com.yyydjk.library.BannerLayout;

public class DialogsListFragment extends BaseLoaderFragment<List<DialogWrapper>> {

    public static final int PICK_DIALOG = 100;
    public static final int CREATE_DIALOG = 200;

    private static final String TAG = DialogsListFragment.class.getSimpleName();
    private static final int LOADER_ID = DialogsListFragment.class.hashCode();

    @Bind(R.id.chats_listview)
    ListView dialogsListView;

    @Bind(R.id.empty_list_textview)
    TextView emptyListTextView;

    @Bind(R.id.frameEmptyList)
    FrameLayout mFrameEmptyList;

    @Bind(R.id.fab_dialogs_new_chat)
    FloatingActionButton btnNewChat;

    private DialogsListAdapter dialogsListAdapter;
    private DataManager dataManager;
    private ConnectycubeUser connectycubeUser;
    private Observer commonObserver;
    private DialogsListLoader dialogsListLoader;
    private Queue<LoaderConsumer> loaderConsumerQueue = new ConcurrentLinkedQueue<>();

    Set<String> dialogsIdsToUpdate;

    protected Handler handler = new Handler();
    private State updateDialogsProcess;

    private LoginChatCompositeSuccessAction loginChatCompositeSuccessAction;
    private DeleteDialogSuccessAction deleteDialogSuccessAction;
    private DeleteDialogFailAction deleteDialogFailAction;
    private LoadChatsSuccessAction loadChatsSuccessAction;
    private LoadChatsFailedAction loadChatsFailedAction;
    private UpdateDialogSuccessAction updateDialogSuccessAction;

    BannerLayout banner;
    private Observer mBannerDataObserver;

    enum State {started, stopped, finished}

    public static DialogsListFragment newInstance() {
        return new DialogsListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initFields();
        initChatsDialogs();
        initActions();
        addObservers();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        setActivityBannerVisibility(View.GONE);

        View view = inflater.inflate(R.layout.fragment_dialogs_list, container, false);
        activateButterKnife(view);
        registerForContextMenu(dialogsListView);
        setEmptyMessage();
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.header_banner, dialogsListView,false);
        dialogsListView.addHeaderView(header);
        dialogsListView.setAdapter(dialogsListAdapter);

        banner = header.findViewById(R.id.bannerLayout);
        banner.setVisibility(View.GONE);
        initBanner();

        //dialogsListView.addHeaderView(header, null, false);

       /* Animation slide_down = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_down);

        Animation slide_up = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_up);


        dialogsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (mLastFirstVisibleItem < firstVisibleItem) {
                    Log.i("AYIK SCROLLING DOWN", "TRUE");
                    banner.setVisibility(View.VISIBLE);
                    //banner.startAnimation(slide_down);
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    Log.i("AYIK SCROLLING UP", "TRUE");
                    banner.setVisibility(View.GONE);
                    //banner.startAnimation(slide_up);
                }
                mLastFirstVisibleItem = firstVisibleItem;

            }
        });*/

        return view;
    }

    private void setActivityBannerVisibility(int visibility) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.bannerSlider.setVisibility(visibility);
        }
    }

    @Override
    public void initActionBar() {
        super.initActionBar();
        actionBarBridge.setActionBarUpButtonEnabled(false);
        loadingBridge.hideActionBarProgress();
    }

    private void initFields() {
        dataManager = DataManager.getInstance();
        commonObserver = new CommonObserver();
        connectycubeUser = AppSession.getSession().getUser();
    }

    private void setEmptyMessage() {
        String textBeforeImage = getString(R.string.dialog_no_chats_before_image_string);
        String textAfterImage = getString(R.string.dialog_no_chats_after_image_string);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(textBeforeImage + " ").append(" ");
        builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.default_text_icon_otu_color)), 14, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Drawable chat = getResources().getDrawable(R.drawable.ic_small_chat);
        chat.setBounds(0, 0, 40, 30);
        builder.setSpan(new ImageSpan(chat, ImageSpan.ALIGN_BASELINE),
                builder.length() - 1, builder.length(), 0);
        builder.append(" " + textAfterImage);

        emptyListTextView.setText(builder);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //baseActivity.showSnackbar(R.string.dialog_loading_dialogs, Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dialogs_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_search:
                launchContactsActivity();
                break;
            case R.id.action_start_invite_friends:
                InviteFriendsActivity.start(getActivity());
                break;
            case R.id.action_start_feedback:
                FeedbackActivity.start(getActivity());
                break;
            case R.id.action_start_settings:
                SettingsActivity.startForResult(this);
                break;
            case R.id.action_notification:
                Toast.makeText(getActivity(), "Coming soon...", Toast.LENGTH_SHORT).show();
                //SettingsActivity.startForResult(this);
                break;
            case R.id.action_start_about:
                AboutActivity.start(getActivity());
                break;
            case R.id.action_web_qr_code:
                UserSessionsActivity.start(getActivity());
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        MenuInflater menuInflater = baseActivity.getMenuInflater();
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        ConnectycubeDialogType dialogType = getChatDialogType(adapterContextMenuInfo);
        if(dialogType!= null){
            if (dialogType.equals(ConnectycubeDialogType.GROUP)){
                menuInflater.inflate(R.menu.dialogs_list_group_ctx_menu, menu);
            } else if(dialogType.equals(ConnectycubeDialogType.PRIVATE)) {
                menuInflater.inflate(R.menu.dialogs_list_private_ctx_menu, menu);
            }
        }
    }

    private ConnectycubeDialogType getChatDialogType(AdapterView.AdapterContextMenuInfo adapterContextMenuInfo){
        ConnectycubeDialogType dialogType = null;
        if(dialogsListView != null && adapterContextMenuInfo!= null && dialogsListView.getItemAtPosition(adapterContextMenuInfo.position) != null
                && dialogsListView.getItemAtPosition(adapterContextMenuInfo.position) instanceof DialogWrapper
                && ((DialogWrapper)dialogsListView.getItemAtPosition(adapterContextMenuInfo.position)).getChatDialog() != null)
        {
            dialogType = ((DialogWrapper)dialogsListView.getItemAtPosition(adapterContextMenuInfo.position)).getChatDialog().getType();
        }
        return dialogType;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (baseActivity.checkNetworkAvailableWithError() && checkDialogsLoadFinished()) {
                    ConnectycubeChatDialog chatDialog = ((DialogWrapper)dialogsListView.getItemAtPosition(adapterContextMenuInfo.position)).getChatDialog();
                    deleteDialog(chatDialog);
                }
                break;
        }
        return true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        addActions();
        if (dialogsListAdapter.getCount() == 0) {
            initDataLoader(LOADER_ID);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dialogsListAdapter != null) {
            checkVisibilityEmptyLabel();
        }
        if (dialogsListAdapter != null) {
            dialogsListAdapter.notifyDataSetChanged();
        }
        checkLoaderConsumerQueue();
        checkUpdateDialogs();

        if (State.finished == updateDialogsProcess) {
            baseActivity.hideSnackBar(R.string.dialog_loading_dialogs);
        }
    }

    private void checkUpdateDialogs() {
//        check if needs update dialog list
        if (!CollectionsUtil.isEmpty(dialogsIdsToUpdate)) {
            QBLoadDialogByIdsCommand.start(getContext(), new ArrayList<>(dialogsIdsToUpdate));
            dialogsIdsToUpdate.clear();
        }
    }

    private void checkLoaderConsumerQueue() {
//        check if the update process can be proceeded
        if (State.stopped == updateDialogsProcess) {
            Log.d(TAG, "checkLoaderConsumerQueue proceeded updateDialogsListFromQueue");
            updateDialogsListFromQueue();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        setStopStateUpdateDialogsProcess();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        BannerDataManager.getInstance().deleteObserver(mBannerDataObserver);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActions();
        deleteObservers();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (PICK_DIALOG == requestCode && data != null) {
            String dialogId = data.getStringExtra(QBServiceConsts.EXTRA_DIALOG_ID);
            checkDialogsIds(dialogId);
            updateOrAddDialog(dialogId, data.getBooleanExtra(QBServiceConsts.EXTRA_DIALOG_UPDATE_POSITION, false));
        } else if (CREATE_DIALOG == requestCode && data != null) {
            updateOrAddDialog(data.getStringExtra(QBServiceConsts.EXTRA_DIALOG_ID), true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setStopStateUpdateDialogsProcess() {
        if (updateDialogsProcess != State.finished) {
            updateDialogsProcess = State.stopped;
        }
    }

    private boolean checkDialogsLoadFinished() {
        if (updateDialogsProcess != State.finished) {
            ToastUtils.shortToast(R.string.chat_service_is_initializing);
            return false;
        }
        return true;
    }

    private void checkDialogsIds(String dialogId) {
//       no need update dialog cause it's already updated
        if (dialogsIdsToUpdate != null) {
            for (String dialogIdToUpdate : dialogsIdsToUpdate) {
                if (dialogIdToUpdate.equals(dialogId)) {
                    dialogsIdsToUpdate.remove(dialogId);
                    break;
                }
            }
        }
    }

    private void updateOrAddDialog(String dialogId, boolean updatePosition) {
        ConnectycubeChatDialog connectycubeChatDialog = dataManager.getConnectycubeChatDialogDataManager().getByDialogId(dialogId);
        DialogWrapper dialogWrapper = new DialogWrapper(getContext(), dataManager, connectycubeChatDialog);

        if (updateDialogsProcess == State.finished || dialogsListAdapter.getCount() != 0) {
            dialogsListAdapter.updateItem(dialogWrapper);
        }

        if (updatePosition) {
            dialogsListAdapter.moveToFirstPosition(dialogWrapper);
        }

        if(dialogsListView != null )
        {
            int start = dialogsListView.getFirstVisiblePosition();
            for (int i = start, j = dialogsListView.getLastVisiblePosition(); i <= j; i++)
            {
                if (i < dialogsListView.getHeaderViewsCount())
                {
                    continue;
                }
                DialogWrapper result = (DialogWrapper)dialogsListView.getItemAtPosition(i);
                if (result.getChatDialog().getDialogId().equals(dialogId))
                {
                    View view = dialogsListView.getChildAt(i - start);
                    dialogsListView.getAdapter().getView(i, view, dialogsListView);
                    break;
                }
            }
        }
    }

    @OnItemClick(R.id.chats_listview)
    void startChat(int position) {
        if (position < dialogsListView.getHeaderViewsCount()) {
            Log.d(TAG, "header banner was clicked");
            return;
        }
        ConnectycubeChatDialog chatDialog = ((DialogWrapper) dialogsListView.getItemAtPosition(position)).getChatDialog();
        if (!baseActivity.checkNetworkAvailableWithError() && isFirstOpeningDialog(chatDialog.getDialogId())) {
            return;
        }

        if (ConnectycubeDialogType.PUBLIC_GROUP.equals(chatDialog.getType())) {
            startBroadcastChatActivity(chatDialog);
        } else if (ConnectycubeDialogType.PRIVATE.equals(chatDialog.getType())) {
            startPrivateChatActivity(chatDialog);
        } else {
            startGroupChatActivity(chatDialog);
        }
    }

    @OnClick(R.id.fab_dialogs_new_chat)
    public void onAddChatClick(View view) {

        /*===============dari anand*/
        //baseActivity.setCurrentFragment(ContactsFragment.newInstance(true), true);
//        Intent intent = new Intent(getActivity(), ContactsActivity.class);
//        intent.putExtra("isNewMessage", true);
//        startActivity(intent);
        addChat();
        //disini nnti tidak bisa buat grup---------------
    }

    private boolean isFirstOpeningDialog(String dialogId) {
        return !dataManager.getMessageDataManager().getTempMessagesByDialogId(dialogId).isEmpty();
    }

    @Override
    public void onConnectedToService(QBService service) {
        if (chatHelper == null) {
            if (service != null) {
                chatHelper = (QBChatHelper) service.getHelper(QBService.CHAT_HELPER);
            }
        }
    }

    @Override
    protected Loader<List<DialogWrapper>> createDataLoader() {
        dialogsListLoader = new DialogsListLoader(getActivity(), dataManager);
        return dialogsListLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<DialogWrapper>> loader, List<DialogWrapper> dialogsList) {
        updateDialogsProcess = State.started;
        Log.d(TAG, "onLoadFinished!!! dialogsListLoader.isLoadCacheFinished() " + dialogsListLoader.isLoadCacheFinished());
        if (dialogsListLoader.isLoadCacheFinished()) {
            //clear queue after loading all dialogs from cache before updating all dialogs from REST
            loaderConsumerQueue.clear();
        } else {
            updateDialogsListFromQueue();
        }

        if (needUpdateDialogsAdapter(dialogsList)) {
            updateDialogsAdapter(dialogsList);
        }

        checkEmptyList(dialogsListAdapter.getCount());

        if (!baseActivity.isDialogLoading()) {
            baseActivity.hideSnackBar(R.string.dialog_loading_dialogs);
        }

//        startForResult load dialogs from REST when finished loading from cache
        if (dialogsListLoader.isLoadCacheFinished()) {
            if (baseActivity.isChatInitializedAndUserLoggedIn()) {
                QBLoadDialogsCommand.start(getContext(), true);
            }
        }
    }

    private void updateDialogsAdapter(List<DialogWrapper> dialogsList) {
        if (dialogsListLoader.isLoadAll()) {
            dialogsListAdapter.setNewData(dialogsList);
        } else {
            dialogsListAdapter.addNewData((ArrayList<DialogWrapper>) dialogsList);
        }

        if (dialogsListLoader.isLoadRestFinished()) {
            updateDialogsProcess = State.finished;
        }
    }

    private boolean needUpdateDialogsAdapter(List<DialogWrapper> dialogsList) {
        return dialogsList.size() != 0;
    }

    private void addChat() {
        Intent intent = new Intent(getActivity(), NewMessageActivity.class);
        intent.putExtra("isNewMessage", true);
        startActivity(intent);
    }

    private boolean isFriendsLoading() {
        return QBLoginChatCompositeCommand.isRunning();
    }

    private void checkVisibilityEmptyLabel() {
        emptyListTextView.setVisibility(dialogsListAdapter.isEmpty() ? View.VISIBLE : View.GONE);
        mFrameEmptyList.setVisibility(dialogsListAdapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void addObservers() {
        dataManager.getConnectycubeChatDialogDataManager().addObserver(commonObserver);
        dataManager.getMessageDataManager().addObserver(commonObserver);
        dataManager.getDialogOccupantDataManager().addObserver(commonObserver);
        dataManager.getDialogNotificationDataManager().addObserver(commonObserver);
        ((Observable) QMUserService.getInstance().getUserCache()).addObserver(commonObserver);
    }

    private void deleteObservers() {
        if (dataManager != null) {
            dataManager.getConnectycubeChatDialogDataManager().deleteObserver(commonObserver);
            dataManager.getMessageDataManager().deleteObserver(commonObserver);
            dataManager.getDialogOccupantDataManager().deleteObserver(commonObserver);
            dataManager.getDialogNotificationDataManager().deleteObserver(commonObserver);
            ((Observable) QMUserService.getInstance().getUserCache()).deleteObserver(commonObserver);
        }
    }

    private void removeActions() {
        baseActivity.removeAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION);
        baseActivity.removeAction(QBServiceConsts.DELETE_DIALOG_SUCCESS_ACTION);
        baseActivity.removeAction(QBServiceConsts.DELETE_DIALOG_FAIL_ACTION);
        baseActivity.removeAction(QBServiceConsts.UPDATE_CHAT_DIALOG_ACTION);
        baseActivity.removeAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION);
        baseActivity.removeAction(QBServiceConsts.LOAD_CHATS_DIALOGS_FAIL_ACTION);

        baseActivity.updateBroadcastActionList();
    }

    private void addActions() {
        baseActivity.addAction(QBServiceConsts.LOGIN_CHAT_COMPOSITE_SUCCESS_ACTION, loginChatCompositeSuccessAction);
        baseActivity.addAction(QBServiceConsts.DELETE_DIALOG_SUCCESS_ACTION, deleteDialogSuccessAction);
        baseActivity.addAction(QBServiceConsts.DELETE_DIALOG_FAIL_ACTION, deleteDialogFailAction);
        baseActivity.addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_SUCCESS_ACTION, loadChatsSuccessAction);
        baseActivity.addAction(QBServiceConsts.LOAD_CHATS_DIALOGS_FAIL_ACTION, loadChatsFailedAction);
        baseActivity.addAction(QBServiceConsts.UPDATE_CHAT_DIALOG_ACTION, updateDialogSuccessAction);

        baseActivity.updateBroadcastActionList();
    }

    private void initChatsDialogs() {
        List<DialogWrapper> dialogsList = new ArrayList<>();
        dialogsListAdapter = new DialogsListAdapter(baseActivity, dialogsList);
    }

    private void initActions() {
        loginChatCompositeSuccessAction = new LoginChatCompositeSuccessAction();
        deleteDialogSuccessAction = new DeleteDialogSuccessAction();
        deleteDialogFailAction = new DeleteDialogFailAction();
        loadChatsSuccessAction = new LoadChatsSuccessAction();
        loadChatsFailedAction = new LoadChatsFailedAction();
        updateDialogSuccessAction = new UpdateDialogSuccessAction();
    }

    private void startPrivateChatActivity(ConnectycubeChatDialog chatDialog) {
        List<DialogOccupant> occupantsList = dataManager.getDialogOccupantDataManager()
                .getDialogOccupantsListByDialogId(chatDialog.getDialogId());
        QMUser opponent = ChatUtils.getOpponentFromPrivateDialog(UserFriendUtils.createLocalUser(connectycubeUser), occupantsList);

        if (!TextUtils.isEmpty(chatDialog.getDialogId())) {
            PrivateDialogActivity.startForResult(this, opponent, chatDialog, PICK_DIALOG);
        }
    }

    private void startGroupChatActivity(ConnectycubeChatDialog chatDialog) {
        GroupDialogActivity.startForResult(this, chatDialog, PICK_DIALOG);
    }

    private void startBroadcastChatActivity(ConnectycubeChatDialog chatDialog) {
        BroadcastDialogActivity.startForResult(this, chatDialog, PICK_DIALOG);
    }

    private void updateDialogsList(int startRow, int perPage) {
//        logic for correct behavior of pagination loading dialogs
//        we can't fire onChangedData until we have incomplete loader task in queue
        if (!loaderConsumerQueue.isEmpty()) {
            Log.d(TAG, "updateDialogsList loaderConsumerQueue.add");
            loaderConsumerQueue.offer(new LoaderConsumer(startRow, perPage));
            return;
        }

//        if Loader is in loading process, we don't fire onChangedData, cause we do not want interrupt current load task
        if (dialogsListLoader.isLoading) {
            Log.d(TAG, "updateDialogsList dialogsListLoader.isLoading");
            loaderConsumerQueue.offer(new LoaderConsumer(startRow, perPage));
        } else {
//        we don't have tasks in queue, so load dialogs by pages
            if (!isResumed()) {
                loaderConsumerQueue.offer(new LoaderConsumer(startRow, perPage));
            } else {
                Log.d(TAG, "updateDialogsList onChangedData");
                dialogsListLoader.setPagination(startRow, perPage);
                onChangedData();
            }
        }
    }

    private void updateDialogsList() {
        if (!loaderConsumerQueue.isEmpty()) {
            Log.d(TAG, "updateDialogsList loaderConsumerQueue.add");
            loaderConsumerQueue.offer(new LoaderConsumer(true));
            return;
        }

        if (dialogsListLoader.isLoading) {
            Log.d(TAG, "updateDialogsList dialogsListLoader.isLoading");
            loaderConsumerQueue.offer(new LoaderConsumer(true));
        } else {
//        load All dialogs
            if (!isResumed()) {
                Log.d(TAG, "updateDialogsList !isResumed() offer");
                loaderConsumerQueue.offer(new LoaderConsumer(true));
            } else {
                dialogsListLoader.setLoadAll(true);
                onChangedData();
            }
        }
    }

    private void updateDialogsListFromQueue() {
        if (!loaderConsumerQueue.isEmpty()) {
            LoaderConsumer consumer = loaderConsumerQueue.poll();
            handler.post(consumer);
        }
    }

    private class LoaderConsumer implements Runnable {
        boolean loadAll;
        int startRow;
        int perPage;

        LoaderConsumer(boolean loadAll) {
            this.loadAll = loadAll;
        }

        LoaderConsumer(int startRow, int perPage) {
            this.startRow = startRow;
            this.perPage = perPage;
        }

        @Override
        public void run() {
            Log.d(TAG, "LoaderConsumer onChangedData");
            dialogsListLoader.setLoadAll(loadAll);
            dialogsListLoader.setPagination(startRow, perPage);
            onChangedData();
        }
    }

    private void deleteDialog(ConnectycubeChatDialog chatDialog) {
        if (chatDialog == null || chatDialog.getDialogId() == null) {
            return;
        }

        baseActivity.showProgress();
        QBDeleteChatCommand.start(baseActivity, chatDialog.getDialogId(), chatDialog.getType().getCode());
    }

    private void checkEmptyList(int listSize) {
        if (listSize > 0) {
            emptyListTextView.setVisibility(View.GONE);
            mFrameEmptyList.setVisibility(View.GONE);
        } else {
            emptyListTextView.setVisibility(View.VISIBLE);
            mFrameEmptyList.setVisibility(View.VISIBLE);
        }
    }

    private void launchContactsFragment() {
        //baseActivity.setCurrentFragment(SearchFragment.newInstance(), true);
        baseActivity.setCurrentFragment(ContactsFragment.newInstance(), true);
    }

    private void updateDialogIds(String dialogId) {
        if (dialogsIdsToUpdate == null) {
            dialogsIdsToUpdate = new HashSet<>();
        }
        dialogsIdsToUpdate.add(dialogId);
    }

    private class LoginChatCompositeSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            Log.i(TAG, "LoginChatCompositeSuccessAction bundle= " + bundle);
            if (dialogsListLoader.isLoadCacheFinished()) {
                QBLoadDialogsCommand.start(getContext(), true);
            }
        }
    }

    private class DeleteDialogSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            baseActivity.hideProgress();
            dialogsListAdapter.removeItem(bundle.getString(QBServiceConsts.EXTRA_DIALOG_ID));
        }
    }

    private class DeleteDialogFailAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            ToastUtils.longToast(R.string.dlg_internet_connection_error);
            baseActivity.hideProgress();
        }
    }

    private class LoadChatsSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            Log.d(TAG, "LoadChatsSuccessAction bundle= " + bundle);
            if (bundle != null) {
                if (isLoadPerPage(bundle)) {
                    updateDialogsList(bundle.getInt(ConstsCore.DIALOGS_START_ROW), bundle.getInt(ConstsCore.DIALOGS_PER_PAGE));
                } else if (bundle.getBoolean(ConstsCore.DIALOGS_UPDATE_ALL)) {
                    updateDialogsList();
                }
            }
        }

        private boolean isLoadPerPage(Bundle bundle) {
            return bundle.get(ConstsCore.DIALOGS_START_ROW) != null && bundle.get(ConstsCore.DIALOGS_PER_PAGE) != null;
        }
    }

    private class LoadChatsFailedAction implements Command {

        @Override
        public void execute(Bundle bundle) throws Exception {
            Log.d(TAG, "LoadChatsFailedAction bundle= " + bundle);
            updateDialogsProcess = State.finished;
        }
    }

    private class UpdateDialogSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            baseActivity.hideProgress();
            Log.d(TAG, "UpdateDialogSuccessAction action UpdateDialogSuccessAction bundle= " + bundle);
            if (bundle != null) {
                updateDialogIds((String) bundle.get(QBServiceConsts.EXTRA_DIALOG_ID));
            }
        }
    }

    private class CommonObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            Log.d(TAG, "CommonObserver update " + observable + " data= " + data.toString());
            if (data != null) {
                if (data instanceof Bundle) {
                    String observeKey = ((Bundle) data).getString(BaseManager.EXTRA_OBSERVE_KEY);
                    Log.i(TAG, "CommonObserver update, key=" + observeKey);
                    if (observeKey.equals(dataManager.getMessageDataManager().getObserverKey())
                            && (((Bundle) data).getSerializable(BaseManager.EXTRA_OBJECT) instanceof Message)) {
                        int action = ((Bundle) data).getInt(BaseManager.EXTRA_ACTION);
                        Log.i(TAG, "CommonObserver action =  " + action);
                        Message message = getObjFromBundle((Bundle) data);
                        if (message.getDialogOccupant() != null && message.getDialogOccupant().getDialog() != null) {
                            boolean updatePosition = message.isIncoming(AppSession.getSession().getUser().getId());
                            Log.i(TAG, "CommonObserver getMessageDataManager updatePosition= " + updatePosition);

                            updateOrAddDialog(message.getDialogOccupant().getDialog().getDialogId(), action == BaseManager.CREATE_ACTION);
                        }
                    } else if (observeKey.equals(dataManager.getConnectycubeChatDialogDataManager().getObserverKey())) {
                        int action = ((Bundle) data).getInt(BaseManager.EXTRA_ACTION);
                        if (action == BaseManager.DELETE_ACTION
                                || action == BaseManager.DELETE_BY_ID_ACTION) {
                            return;
                        }
                        Dialog dialog = getObjFromBundle((Bundle) data);
                        if (dialog != null) {
                            updateOrAddDialog(dialog.getDialogId(), false);
                        }
                    } else if (observeKey.equals(dataManager.getDialogOccupantDataManager().getObserverKey())) {
                        DialogOccupant dialogOccupant = getObjFromBundle((Bundle) data);
                        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
                            updateOrAddDialog(dialogOccupant.getDialog().getDialogId(), false);
                        }
                    } else if (observeKey.equals(dataManager.getDialogNotificationDataManager().getObserverKey())) {
                        Bundle observableData = (Bundle) data;
                        DialogNotification dialogNotification = (DialogNotification) observableData.getSerializable(DialogNotificationDataManager.EXTRA_OBJECT);
                        if (dialogNotification != null) {
                            updateOrAddDialog(dialogNotification.getDialogOccupant().getDialog().getDialogId(), true);
                        }
                    }
                } else if (data.equals(QMUserCacheImpl.OBSERVE_KEY)) {
                    Log.d(TAG, "else if (data.equals(QMUserCacheImpl.OBSERVE_KEY))");
//                    updateDialogsList();
                }
            }
        }

    }

    private <T> T getObjFromBundle(Bundle data) {
        return (T) (data).getSerializable(BaseManager.EXTRA_OBJECT);
    }

    private void launchContactsActivity() {
        Intent intent = new Intent(getActivity(), ContactsActivity.class);
        startActivity(intent);
    }

    private void initBanner()
    {
        banner.setImageLoader(new GlideImageLoader());

        mBannerDataObserver = (observable, arg) -> {
            if (observable instanceof BannerDataManager) {
                banner.setViewUrls(((BannerDataManager)observable).getUrls(false));
            }
        };
        BannerDataManager.getInstance().addObserver(mBannerDataObserver);
        if (BannerDataManager.getInstance().getUrls(false).size() > 0)
        {
            banner.setViewUrls(BannerDataManager.getInstance().getUrls(true));
        }
    }

    public void lauchRegister() {
        Intent register = new Intent(getActivity(), Register.class);
        startActivity(register);
        //finish();
    }

}