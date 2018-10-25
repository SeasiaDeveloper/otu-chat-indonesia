package com.eklanku.otuChat.ui.activities.contacts;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.adapters.contacts.ContactsShareAdapter;
import com.eklanku.otuChat.utils.helpers.AddressBookHelper;
import com.eklanku.otuChat.utils.helpers.ContactJsonHelper;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.service.QBService;
import com.quickblox.q_municate_core.utils.ConstsCore;

import java.util.ArrayList;

import butterknife.Bind;
import rx.Observable;

public class ContactsShareActivity extends BaseLoggableActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = ContactsShareActivity.class.getSimpleName();
    public static final int REQUEST_CONTACTS_LIST = 701;

    private ArrayList<ContactsModel> contactsModels;
    private LinearLayoutManager linearLayoutManager;

    private ContactsShareAdapter contactsAdapter;

    @Bind(R.id.rvContacts)
    RecyclerView recyclerViewContacts;

    @Override
    protected int getContentResId() {
        return R.layout.activity_contacts_share;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = " " + AppSession.getSession().getUser().getFullName();
        setUpActionBarWithUpButton();

        contactsModels = mDbHelper.getContactsSortedByRegType();
        contactsAdapter = new ContactsShareAdapter(contactsModels, this);
        Log.d(TAG, "AMBRA onCreate - contactsModels: " + contactsModels);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewContacts.setLayoutManager(linearLayoutManager);
        recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewContacts.setAdapter(contactsAdapter);
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

    private void updateContactListAndRoster() {
        AddressBookHelper.getInstance().updateRosterContactList(friendListHelper, mDbHelper, AppSession.getSession().getUser().getPassword()).onErrorResumeNext(e -> Observable.empty()).subscribe(success -> {
            Log.d(TAG, "updateRosterContactList success= " + success);
            contactsModels.clear();
            contactsModels.addAll(mDbHelper.getContactsSortedByRegType());
            contactsAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "AMBRA onActivityResult requestCode= " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                ArrayList<ContactsModel> contactsModels = (ArrayList<ContactsModel>) bundle.getSerializable(ContactDetails.EXTRA_CONTACTS_MODEL_LIST);
                sendContacts(contactsModels);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    public void onDetailsContacts(View view) {
        Log.d(TAG, "AMBRA onDetailsContacts");
        Log.d(TAG, "AMBRA onDetailsContacts contactsAdapter.getSelectedContacts()= " + contactsAdapter.getSelectedContacts());
        ArrayList<ContactsModel> contacts = new ArrayList<>(contactsAdapter.getSelectedContacts());
        Log.d(TAG, "AMBRA onDetailsContacts contacts= " + contacts);

        ContactDetails.startForResult(this, REQUEST_CONTACTS_LIST, contacts);
    }

    private void sendContacts(ArrayList<ContactsModel> contacts) {
        String jsonContacts = ContactJsonHelper.createContactsJsonFromList(contacts);
        Log.d(TAG, "AMBRA sendContacts jsonContacts= " + jsonContacts);
//        Bundle extras = new Bundle();
//        extras.putString(ConstsCore.EXTRA_CONTACTS, jsonContacts);
//
//        Intent result = new Intent();
//        result.putExtras(extras);
//        setResult(RESULT_OK, result);
//        finish();
    }
}
