package com.eklanku.otuChat.ui.activities.chats;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModel;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModelGroup;
import com.eklanku.otuChat.ui.adapters.friends.FriendsAdapter;
import com.eklanku.otuChat.ui.adapters.search.ContactsAdapter;
import com.eklanku.otuChat.ui.adapters.search.ContactsAdapterGroup;
import com.eklanku.otuChat.utils.helpers.DbHelper;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.adapters.friends.FriendsAdapter;
import com.eklanku.otuChat.utils.KeyboardUtils;
import com.eklanku.otuChat.utils.listeners.simple.SimpleOnRecycleItemClickListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.commands.chat.QBCreatePrivateChatCommand;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.models.Friend;
import com.quickblox.q_municate_db.utils.DialogTransformUtils;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class NewMessageActivity extends BaseLoggableActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    @Bind(R.id.friends_recyclerview)
    RecyclerView friendsRecyclerView;

    private DataManager dataManager;
    private FriendsAdapter friendsAdapter;
    private QMUser selectedUser;

    public static final int RESULT_CODE = 3000;
    private static final int PER_PAGE = 200; //5; //200;
    private static final int COUNTRY_CODE = 62; // 91; 62;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final String TAG = ContactsActivity.class.getSimpleName();

    private static boolean mIsNewMessage = false;

    private DbHelper mDbHelper;

    private ArrayList<String> arrayPhone;
    private ArrayList<String> arrayName;
    private int startPosition = 0;
    private ProgressDialog dialog;
    private boolean isload = true, isFirst = true;
    private ContactsAdapterGroup contactsAdapter;
    private ArrayList<ContactsModelGroup> contactsModels;
    private LinearLayoutManager linearLayoutManager;
    private int intCurrentPage = 0;

    public boolean isToGroup = false;
    private QBChatDialog qbDialog;
    public List<Integer> friendIdsList;
    private List<Integer> occupants;

    public static void startForResult(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), NewMessageActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_friends_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = " " + AppSession.getSession().getUser().getFullName();
        setUpActionBarWithUpButton();
        mDbHelper = new DbHelper(this);

        mIsNewMessage = getIntent().getBooleanExtra("isNewMessage", false);
        if (getIntent().hasExtra("isToGroup")) {
            isToGroup = getIntent().getBooleanExtra("isToGroup", false);
            qbDialog = (QBChatDialog) getIntent().getExtras().getSerializable(QBServiceConsts.EXTRA_DIALOG);
            qbDialog.initForChat(QBChatService.getInstance());
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
        readContacts();

        contactsAdapter = new ContactsAdapterGroup(contactsModels, this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        friendsRecyclerView.setLayoutManager(linearLayoutManager);
        friendsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        friendsRecyclerView.setAdapter(contactsAdapter);
        //old
        /*
        initFields();
        setUpActionBarWithUpButton();

        initRecyclerView();
        initCustomListeners();

        addActions();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_message_menu, menu);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_group:
                NewGroupDialogActivity.start(this);
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onClose() {
        cancelSearch();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String searchQuery) {
        KeyboardUtils.hideKeyboard(this);
        search(searchQuery);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchQuery) {
        if (contactsAdapter != null) {
            contactsAdapter.getFilter().filter(searchQuery);
        }
        return true;
    }

//    @Override
//    public void onConnectedToService(QBService service) {
//        super.onConnectedToService(service);
//        if (friendListHelper != null) {
//            friendsAdapter.setFriendListHelper(friendListHelper);
//        }
//    }

//    @Override
//    public void onChangedUserStatus(int userId, boolean online) {
//        super.onChangedUserStatus(userId, online);
//        friendsAdapter.notifyDataSetChanged();
//    }

    private void initFields() {
        title = getString(R.string.new_message_title);
        dataManager = DataManager.getInstance();
    }

    private void initRecyclerView() {
        List<Friend> friendsList = dataManager.getFriendDataManager().getAllSorted();
        friendsAdapter = new FriendsAdapter(this, UserFriendUtils.getUsersFromFriends(friendsList), true);
        friendsAdapter.setFriendListHelper(friendListHelper);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        friendsRecyclerView.setAdapter(friendsAdapter);
    }

    private void initCustomListeners() {
        friendsAdapter.setOnRecycleItemClickListener(new SimpleOnRecycleItemClickListener<QMUser>() {

            @Override
            public void onItemClicked(View view, QMUser user, int position) {
                super.onItemClicked(view, user, position);
                selectedUser = user;
                checkForOpenChat(user);
            }
        });
    }

    private void addActions() {
        addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION, new CreatePrivateChatSuccessAction());
        addAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION, failAction);

        updateBroadcastActionList();
    }

    private void removeActions() {
        removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_SUCCESS_ACTION);
        removeAction(QBServiceConsts.CREATE_PRIVATE_CHAT_FAIL_ACTION);

        updateBroadcastActionList();
    }

    private void checkForOpenChat(QMUser user) {
        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupantForPrivateChat(user.getId());
        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            QBChatDialog chatDialog = DialogTransformUtils.createQBDialogFromLocalDialog(dataManager, dialogOccupant.getDialog());
            startPrivateChat(chatDialog);
        } else {
            if (checkNetworkAvailableWithError()) {
                showProgress();
                QBCreatePrivateChatCommand.start(this, user);
            }
        }
    }

    private void startPrivateChat(QBChatDialog dialog) {
        PrivateDialogActivity.start(this, selectedUser, dialog);
        finish();
    }

    private void search(String searchQuery) {
        if (friendsAdapter != null) {
            friendsAdapter.setFilter(searchQuery);
        }
    }

    private void cancelSearch() {
        if (friendsAdapter != null) {
            friendsAdapter.flushFilter();
        }
    }

    private class CreatePrivateChatSuccessAction implements Command {

        @Override
        public void execute(Bundle bundle) {
            hideProgress();
            QBChatDialog qbDialog = (QBChatDialog) bundle.getSerializable(QBServiceConsts.EXTRA_DIALOG);
            startPrivateChat(qbDialog);
        }
    }

    private void startAddFriendsActivity() {
        Intent intent = new Intent(NewMessageActivity.this, ContactsActivity.class);
        intent.putExtra("isNewMessage", true);
        startActivity(intent);
    }

    public void readContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            intCurrentPage++;
            if (startPosition == 0 && isFirst) {
                dialog = ProgressDialog.show(this, null, "Please Wait...");
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        arrayPhone.clear();
                        ContentResolver cr = getContentResolver();
                        Cursor cur;

                        String limit = String.valueOf(startPosition) + ", " + PER_PAGE;

                        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC LIMIT " + limit);
                        if (cur.getCount() > 0) {
                            arrayName.clear();
                            while (cur.moveToNext()) {
                                String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
                                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                            new String[]{id}, null);
                                    while (pCur.moveToNext()) {
                                        String phonenumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                        String contactname = pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                        phonenumber = phonenumber.replaceAll("[+()-]", "");
                                        phonenumber = phonenumber.replaceAll(" ", "");
                                        if (phonenumber.startsWith("0")) {
                                            phonenumber = phonenumber.replaceFirst("0", String.valueOf(COUNTRY_CODE));
                                        } else if (!phonenumber.startsWith(String.valueOf(COUNTRY_CODE))) {
                                            phonenumber = COUNTRY_CODE + phonenumber;
                                        }
                                        if (!arrayPhone.contains(phonenumber.trim())) {
                                            if (!phonenumber.isEmpty()) {
                                                arrayPhone.add(phonenumber);
                                                arrayName.add(contactname);
                                            }
                                        }
                                    }
                                    pCur.close();
                                }
                            }
                        } else {
                            isload = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isload) {
                                sendContact();
                            } else {
                                if (dialog != null && dialog.isShowing() != false) {
                                    dialog.dismiss();
                                }
                            }
                        }
                    });
                }

            }).start();
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

    private void sendContact() {
        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(arrayPhone.size());


        QBUsers.getUsersByPhoneNumbers(arrayPhone, pagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle params) {
                boolean isUpdateContact = false;
                if (!mIsNewMessage) {
                    for (int i = 0; i < arrayPhone.size(); i++) {
                        if (!mDbHelper.isPhoneNumberExists(arrayPhone.get(i))) {
                            isUpdateContact = true;
                            mDbHelper.insertContact(new ContactsModel(arrayPhone.get(i), arrayName.get(i), "0"));
                        }
                    }
                }

                if (users.size() > 0) {
                    for (int j = 0; j < users.size(); j++) {
                        if (arrayPhone.contains(users.get(j).getPhone())) {
                            //addAsFriend(users.get(j));
                            int position = arrayPhone.indexOf(users.get(j).getPhone());
                            if (position >= 0) {
                                if (!mIsNewMessage) {
                                    if (!mDbHelper.isQbUser(users.get(j).getPhone())) {
                                        isUpdateContact = true;
                                        mDbHelper.updateContact(users.get(j).getPhone(), users.get(j).getId());
                                    }
                                } else {
                                    if (!mDbHelper.isPhoneNumberExists(users.get(j).getPhone())) {
                                        isUpdateContact = true;
                                        ContactsModel objContact = new ContactsModel(arrayPhone.get(position), arrayName.get(position), "1", users.get(j).getId());
                                        mDbHelper.insertContact(objContact);
                                    } else {
                                        isUpdateContact = true;
                                        mDbHelper.updateContact(users.get(j).getPhone(), users.get(j).getId());
                                    }
                                }
                            }
                        }
                    }
                }
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                if (isUpdateContact) {
                    try {
                        contactsModels.clear();
                        if (mIsNewMessage) {
                            contactsModels.addAll(mDbHelper.getContactsSelectedGroup());
                        } else if (isToGroup) {
                            contactsModels.addAll(mDbHelper.getContactsExceptGroup(qbDialog.getOccupants()));
                        } else {
                            contactsModels.addAll(mDbHelper.getContactsGroup());
                        }
                        contactsAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                startPosition = intCurrentPage * PER_PAGE;
                readContacts();
            }

            @Override
            public void onError(QBResponseException errors) {
                Log.e("Error", errors.getErrors().toString());
            }
        });
    }
}