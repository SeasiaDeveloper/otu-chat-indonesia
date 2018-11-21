package com.eklanku.otuChat.ui.activities.call;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectycube.chat.ConnectycubeChatService;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.users.model.ConnectycubeUser;
import com.connectycube.videochat.RTCTypes;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.RecyclerTouchListener;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.activities.chats.NewMessageActivity;
import com.eklanku.otuChat.ui.activities.chats.PrivateDialogActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModelGroup;
import com.eklanku.otuChat.ui.adapters.call.CallsAdapter;
import com.eklanku.otuChat.ui.adapters.friends.FriendsAdapter;
import com.eklanku.otuChat.ui.adapters.search.ContactsAdapterGroup;
import com.eklanku.otuChat.utils.DateUtils;
import com.eklanku.otuChat.utils.KeyboardUtils;
import com.eklanku.otuChat.utils.ToastUtils;
import com.eklanku.otuChat.utils.helpers.DbHelper;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.OnlineStatusUtils;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class ContactListCallActivity extends BaseLoggableActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    @Bind(R.id.friends_recyclerview)
    RecyclerView friendsRecyclerView;

    private DataManager dataManager;
    private FriendsAdapter friendsAdapter;
    private QMUser selectedUser;

    public static final int RESULT_CODE = 3000;
    private static final int PER_PAGE = 200; //5; //200;
    private static final int COUNTRY_CODE = 62; // 91; 62;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private static boolean mIsNewMessage = false;

    private ArrayList<String> arrayPhone;
    private ArrayList<String> arrayName;
    private int startPosition = 0;
    private ProgressDialog dialog;
    private boolean isload = true, isFirst = true;
    private CallsAdapter callsAdapter;
    private ArrayList<ContactsModelGroup> contactsModels;
    private LinearLayoutManager linearLayoutManager;
    private int intCurrentPage = 0;

    public boolean isToGroup = false;
    private ConnectycubeChatDialog qbDialog;
    public List<Integer> friendIdsList;
    private List<Integer> occupants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = " " + AppSession.getSession().getUser().getFullName();
        setUpActionBarWithUpButton();
        mDbHelper = new DbHelper(this);

        mIsNewMessage = getIntent().getBooleanExtra("isNewMessage", false);
        if (getIntent().hasExtra("isToGroup")) {
            isToGroup = getIntent().getBooleanExtra("isToGroup", false);
            qbDialog = (ConnectycubeChatDialog) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_DIALOG);
            qbDialog.initForChat(ConnectycubeChatService.getInstance());
            addAction(QBServiceConsts.ADD_FRIENDS_TO_GROUP_SUCCESS_ACTION, new AddFriendsToGroupSuccessCommand());
        }

        arrayPhone = new ArrayList<>();
        arrayName = new ArrayList<>();
        contactsModels = new ArrayList<>();
        friendIdsList = new ArrayList<>();
        if (mIsNewMessage) {
            contactsModels = mDbHelper.getContactsSelectedGroup();
            if (contactsModels.size() > 0)
                isFirst = false;
        } else if (isToGroup) {
            title = getString(R.string.add_friends_to_group_title);
            contactsModels = mDbHelper.getContactsExceptGroup(qbDialog.getOccupants());
            if (contactsModels.size() > 0)
                isFirst = false;

        } else {
            contactsModels = mDbHelper.getContactsGroup();
            if (contactsModels.size() > 0)
                isFirst = false;
        }

        showProgressDialog(isFirst);
        callsAdapter = new CallsAdapter(contactsModels, this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        friendsRecyclerView.setLayoutManager(linearLayoutManager);
        friendsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        friendsRecyclerView.setAdapter(callsAdapter);

    }

    @Override
    public void notifyChangedUserStatus(int userId, boolean online) {
        super.notifyChangedUserStatus(userId, online);

        if (selectedUser != null && selectedUser.getId() == userId) {
            if (online) {
                //gets opponentUser from DB with updated field 'last_request_at'
                actualizeOpponentUserFromDb();
            }
        }
    }

    private void actualizeOpponentUserFromDb() {
        QMUser opponentUserFromDb = QMUserService.getInstance().getUserCache().get((long) selectedUser.getId());

        if (opponentUserFromDb != null) {
            selectedUser = opponentUserFromDb;
        }
    }

    @Override
    public boolean onClose() {
        cancelSearch();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        KeyboardUtils.hideKeyboard(this);
        search(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (callsAdapter != null) {
            callsAdapter.getFilter().filter(s);
        }
        return true;
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_friends_list;
    }


    private void showProgressDialog(boolean show) {
        if (show) {
            showProgress();
        }
    }


    private class AddFriendsToGroupSuccessCommand implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            Intent intent = new Intent();
            intent.putExtra(QBServiceConsts.EXTRA_FRIENDS, (Serializable) friendIdsList);
            setResult(RESULT_CODE, intent);
            finish();
        }
    }

    private void cancelSearch() {
        if (friendsAdapter != null) {
            friendsAdapter.flushFilter();
        }
    }

    private void search(String searchQuery) {
        if (friendsAdapter != null) {
            friendsAdapter.setFilter(searchQuery);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_call_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);


        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;

        if (searchMenuItem != null) {
            searchView = (SearchView) searchMenuItem.getActionView();

        }

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this);
        }

        return super.onCreateOptionsMenu(menu);
    }
}
