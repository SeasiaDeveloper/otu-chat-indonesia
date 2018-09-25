package com.eklanku.otuChat.ui.activities.chats;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.eklanku.otuChat.ui.activities.contacts.ContactsActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModel;
import com.eklanku.otuChat.ui.adapters.friends.FriendsAdapter;
import com.eklanku.otuChat.ui.adapters.friends.SelectableFriendsAdapter;
import com.eklanku.otuChat.ui.adapters.search.ContactsAdapter;
import com.eklanku.otuChat.ui.views.recyclerview.SimpleDividerItemDecoration;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.others.BaseFriendsListActivity;
import com.eklanku.otuChat.ui.adapters.friends.FriendsAdapter;
import com.eklanku.otuChat.ui.adapters.friends.SelectableFriendsAdapter;
import com.eklanku.otuChat.ui.views.recyclerview.SimpleDividerItemDecoration;
import com.eklanku.otuChat.utils.ToastUtils;
import com.eklanku.otuChat.utils.helpers.DbHelper;
import com.eklanku.otuChat.utils.listeners.SelectUsersListener;
import com.eklanku.otuChat.utils.listeners.simple.SimpleOnRecycleItemClickListener;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_user_service.model.QMUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class NewGroupDialogActivity extends BaseFriendsListActivity implements SelectUsersListener {

    @Bind(R.id.members_edittext)
    EditText membersEditText;

    private ArrayList<ContactsModel> contactsModels;
    private LinearLayoutManager linearLayoutManager;

    private boolean isFirst = true;
    private DbHelper mDbHelper;
    private QBChatDialog qbDialog;
    private ContactsAdapter contactsAdapter;

    public static void start(Context context) {
        Intent intent = new Intent(context, NewGroupDialogActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_new_group;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*mDbHelper = new DbHelper(this);
        qbDialog = (QBChatDialog) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_DIALOG);
        qbDialog.initForChat(QBChatService.getInstance());
        contactsModels = mDbHelper.getContactsExcept(qbDialog.getOccupants());
        contactsModels = new ArrayList<>();

        if (contactsModels.size() > 0)
            isFirst = false;

        contactsAdapter = new ContactsAdapter(contactsModels, this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //addAction(QBServiceConsts.ADD_FRIENDS_TO_GROUP_SUCCESS_ACTION, new ContactsActivity.AddFriendsToGroupSuccessCommand());
       */
        initFields();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpActionBarWithUpButton();
        initCustomListeners();
    }

    @Override
    protected void initRecyclerView() {
        super.initRecyclerView();
        ((SelectableFriendsAdapter) friendsAdapter).setSelectUsersListener(this);
        friendsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

    }

    @Override
    protected FriendsAdapter getFriendsAdapter() {
        return new SelectableFriendsAdapter(this, getFriendsList(), true);
    }

    @Override
    protected void performDone() {
        List<QMUser> selectedFriendsList = ((SelectableFriendsAdapter) friendsAdapter).getSelectedFriendsList();
        if (!selectedFriendsList.isEmpty()) {
            CreateGroupDialogActivity.start(this, selectedFriendsList);
        } else {
            ToastUtils.longToast(R.string.new_group_no_friends_for_creating_group);
        }
    }

    @Override
    public void onSelectedUsersChanged(int count, String fullNames) {
        membersEditText.setText(fullNames);
    }

    private void initFields() {
        title = getString(R.string.new_group_title);
    }

    private void initCustomListeners() {
        friendsAdapter.setOnRecycleItemClickListener(new SimpleOnRecycleItemClickListener<QMUser>() {

            @Override
            public void onItemClicked(View view, QMUser entity, int position) {
                ((SelectableFriendsAdapter) friendsAdapter).selectFriend(position);
            }
        });
    }
}