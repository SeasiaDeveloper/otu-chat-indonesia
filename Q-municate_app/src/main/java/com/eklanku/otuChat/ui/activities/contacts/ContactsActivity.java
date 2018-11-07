package com.eklanku.otuChat.ui.activities.contacts;


import android.Manifest;
import android.app.Fragment;
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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.activities.chats.NewGroupDialogActivity;
import com.eklanku.otuChat.ui.adapters.search.ContactsAdapter;
import com.eklanku.otuChat.utils.ToastUtils;
import com.eklanku.otuChat.utils.helpers.AddressBookHelper;
import com.eklanku.otuChat.utils.helpers.DbHelper;
import com.connectycube.chat.ConnectycubeChatService;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.core.EntityCallback;
import com.connectycube.core.exception.ResponseException;
import com.connectycube.core.request.PagedRequestBuilder;
import com.quickblox.q_municate_core.core.command.Command;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.commands.chat.QBAddFriendsToGroupCommand;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.service.QBServiceConsts;
import com.connectycube.users.ConnectycubeUsers;
import com.connectycube.users.model.ConnectycubeUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.Observable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsActivity extends BaseLoggableActivity implements SearchView.OnQueryTextListener {


    @Bind(R.id.rvContacts)
    RecyclerView mRvContacts;

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
    private ContactsAdapter contactsAdapter;
    private ArrayList<ContactsModel> contactsModels;
    private LinearLayoutManager linearLayoutManager;
    private int intCurrentPage = 0;

    public boolean isToGroup = false;
    private ConnectycubeChatDialog qbDialog;
    public List<Integer> friendIdsList;
    private List<Integer> occupants;

    @Override
    protected int getContentResId() {
        return R.layout.activity_contacts;
    }

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
            addAction(QBServiceConsts.ADD_FRIENDS_TO_GROUP_FAIL_ACTION, new FailAction());
        }

        arrayPhone = new ArrayList<>();
        arrayName = new ArrayList<>();
        contactsModels = new ArrayList<>();
        friendIdsList = new ArrayList<>();

        if (mIsNewMessage) {
            contactsModels = mDbHelper.getContactsSelected();
            if (contactsModels.size() > 0)
                isFirst = false;
        } else if (isToGroup) {
            title = getString(R.string.add_friends_to_group_title);
            contactsModels = mDbHelper.getContactsExcept(qbDialog.getOccupants());
            if (contactsModels.size() > 0)
                isFirst = false;

        } else {
            contactsModels = mDbHelper.getContactsSortedByRegType();
            Log.d("OPPO-1", "onCreate - contactsModels: " + contactsModels);
            if (contactsModels.size() > 0)
                isFirst = false;
        }
//        readContacts();

        contactsAdapter = new ContactsAdapter(contactsModels, this);

        Log.d("OPPO-1", "onCreate: " + contactsAdapter);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRvContacts.setLayoutManager(linearLayoutManager);
        mRvContacts.setItemAnimator(new DefaultItemAnimator());
        mRvContacts.setAdapter(contactsAdapter);
    }

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if(hasAction(QBServiceConsts.ADD_FRIENDS_TO_GROUP_SUCCESS_ACTION)){
			removeAction(QBServiceConsts.ADD_FRIENDS_TO_GROUP_SUCCESS_ACTION);
		}
		if(hasAction(QBServiceConsts.ADD_FRIENDS_TO_GROUP_FAIL_ACTION)){
			removeAction(QBServiceConsts.ADD_FRIENDS_TO_GROUP_FAIL_ACTION);
		}
	}

	protected void setUpActionBarWithUpButton() {
        initActionBar();
        setActionBarUpButtonEnabled(true);
        setActionBarTitle(title);
    }

    @Override
    public void onConnectedToService(QBService service) {
        super.onConnectedToService(service);
        if (friendListHelper != null) {
            contactsAdapter.setFriendListHelper(friendListHelper);
            contactsAdapter.notifyDataSetChanged();
        }
        updateContactListAndRoster();
    }

    @Override
    public void notifyChangedUserStatus(int userId, boolean online) {
        super.notifyChangedUserStatus(userId, online);
        Log.d(TAG, "notifyChangedUserStatus");
        contactsAdapter.notifyDataSetChanged();
    }

    private void updateContactListAndRoster() {
        AddressBookHelper.getInstance().updateRosterContactList(friendListHelper, mDbHelper, AppSession.getSession().getUser().getPassword()).onErrorResumeNext(e -> Observable.empty()).subscribe(success -> {
            Log.d(TAG, "updateRosterContactList success= " + success);
            contactsModels.clear();
            if (mIsNewMessage) {
                contactsModels.addAll(mDbHelper.getContactsSelected());
            } else if (isToGroup) {
                contactsModels.addAll(mDbHelper.getContactsExcept(qbDialog.getOccupants()));
            } else {
                contactsModels.addAll(mDbHelper.getContactsSortedByRegType());
            }
            contactsAdapter.notifyDataSetChanged();
        });
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                                           int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission is granted
////                readContacts();
//            } else {
//                finish();
//            }
//        }
//    }

//    public void readContacts() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
//            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
//        } else {
//            intCurrentPage++;
//            if (startPosition == 0 && isFirst) {
//                dialog = ProgressDialog.show(this, null, "Please Wait...");
//            }
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        arrayPhone.clear();
//                        ContentResolver cr = getContentResolver();
//                        Cursor cur;
//
//                        String limit = String.valueOf(startPosition) + ", " + PER_PAGE;
//
//                        cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "UPPER(" + ContactsContract.Contacts.DISPLAY_NAME + ") ASC LIMIT " + limit);
//                        if (cur.getCount() > 0) {
//                            arrayName.clear();
//                            while (cur.moveToNext()) {
//                                String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
//                                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                                            new String[]{id}, null);
//                                    while (pCur.moveToNext()) {
//                                        String phonenumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                                        String contactname = pCur.getString(pCur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                                        phonenumber = phonenumber.replaceAll("[+()-]", "");
//                                        phonenumber = phonenumber.replaceAll(" ", "");
//                                        if (phonenumber.startsWith("0")) {
//                                            phonenumber = phonenumber.replaceFirst("0", String.valueOf(COUNTRY_CODE));
//                                        } else if (!phonenumber.startsWith(String.valueOf(COUNTRY_CODE))) {
//                                            phonenumber = COUNTRY_CODE + phonenumber;
//                                        }
//                                        if (!arrayPhone.contains(phonenumber.trim())) {
//                                            if (!phonenumber.isEmpty()) {
//                                                arrayPhone.add(phonenumber);
//                                                arrayName.add(contactname);
//                                            }
//                                        }
//                                    }
//                                    pCur.close();
//                                }
//                            }
//                        } else {
//                            isload = false;
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (isload) {
//                                sendContact();
//                            } else {
//                                if (dialog != null && dialog.isShowing() != false) {
//                                    dialog.dismiss();
//                                }
//                            }
//                        }
//                    });
//                }
//
//            }).start();
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_create_group:
//                NewGroupDialogActivity.start(this);
                Intent intent = new Intent(this, NewGroupDialogActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                intent.putExtra("isToGroup", true);
                intent.putExtra(QBServiceConsts.EXTRA_DIALOG, qbDialog);
                break;
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_done:
                if (checkNetworkAvailableWithError()) {
                    performDone();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


//    private void sendContact() {
//        PagedRequestBuilder pagedRequestBuilder = new PagedRequestBuilder();
//        pagedRequestBuilder.setPage(1);
//        pagedRequestBuilder.setPerPage(arrayPhone.size());
//
//
//        ConnectycubeUsers.getUsersByPhoneNumbers(arrayPhone, pagedRequestBuilder).performAsync(new EntityCallback<ArrayList<ConnectycubeUser>>() {
//            @Override
//            public void onSuccess(ArrayList<ConnectycubeUser> users, Bundle params) {
//                boolean isUpdateContact = false;
//                if (!mIsNewMessage) {
//                    for (int i = 0; i < arrayPhone.size(); i++) {
//                        if (!mDbHelper.isPhoneNumberExists(arrayPhone.get(i))) {
//                            isUpdateContact = true;
//                            mDbHelper.insertContact(new ContactsModel(arrayPhone.get(i), arrayName.get(i), "0"));
//                        }
//                    }
//                }
//
//                if (users.size() > 0) {
//                    for (int j = 0; j < users.size(); j++) {
//                        if (arrayPhone.contains(users.get(j).getPhone())) {
//                            //addAsFriend(users.get(j));
//                            int position = arrayPhone.indexOf(users.get(j).getPhone());
//                            if (position >= 0) {
//                                if (!mIsNewMessage) {
//                                    if (!mDbHelper.isConnectycubeUser(users.get(j).getPhone())) {
//                                        isUpdateContact = true;
//                                        mDbHelper.updateContact(users.get(j).getPhone(), users.get(j).getId());
//                                    }
//                                } else {
//                                    if (!mDbHelper.isPhoneNumberExists(users.get(j).getPhone())) {
//                                        isUpdateContact = true;
//                                        ContactsModel objContact = new ContactsModel(arrayPhone.get(position), arrayName.get(position), "1", users.get(j).getId());
//                                        mDbHelper.insertContact(objContact);
//                                    } else {
//                                        isUpdateContact = true;
//                                        mDbHelper.updateContact(users.get(j).getPhone(), users.get(j).getId());
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                if (dialog != null && dialog.isShowing())
//                    dialog.dismiss();
//                if (isUpdateContact) {
//                    try {
//                        contactsModels.clear();
//                        if (mIsNewMessage) {
//                            contactsModels.addAll(mDbHelper.getContactsSelected());
//                        } else if (isToGroup) {
//                            contactsModels.addAll(mDbHelper.getContactsExcept(qbDialog.getOccupants()));
//                        } else {
//                            contactsModels.addAll(mDbHelper.getContacts());
//                        }
//                        contactsAdapter.notifyDataSetChanged();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                startPosition = intCurrentPage * PER_PAGE;
//                readContacts();
//            }
//
//            @Override
//            public void onError(ResponseException errors) {
//                Log.e("Error", errors.getErrors().toString());
//            }
//        });
//    }

    // Add Selected Friends to the Group
    protected void performDone() {
        Log.v("Menu Action", "onOptionsItemSelected = isFriend: Add to Group " + friendIdsList.toString());

        if (!friendIdsList.isEmpty()) {
            if (isChatInitializedAndUserLoggedIn() && checkNetworkAvailableWithError()) {
                boolean joined = chatHelper != null && qbDialog != null && chatHelper.isDialogJoined(qbDialog);
                if (joined) {
                    showProgress();
                    //friendIdsList = UserFriendUtils.getFriendIds(selectedFriendsList);
                    QBAddFriendsToGroupCommand.start(this, qbDialog.getDialogId(), (ArrayList<Integer>) friendIdsList);
                } else {
                    ToastUtils.longToast(R.string.chat_service_is_initializing);
                }
            } else {
                ToastUtils.longToast(R.string.chat_service_is_initializing);
            }
        } else {
            ToastUtils.longToast(R.string.add_friends_to_group_no_friends_for_adding);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isToGroup) {
            getMenuInflater().inflate(R.menu.done_green_menu, menu);
        } else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.search_menu_new, menu);
            MenuItem searchItem = menu.findItem(R.id.search);
            SearchView searchView = new SearchView(this);
            searchView.setQueryHint(getString(R.string.action_bar_search_hint));
            final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setOnQueryTextListener(this);
            //searchView.setIconifiedByDefault(false);
            //searchItem.expandActionView();
            searchItem.setActionView(searchView);
        }
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (contactsAdapter != null) {
            contactsAdapter.getFilter().filter(newText);
        }
        return true;
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

}
