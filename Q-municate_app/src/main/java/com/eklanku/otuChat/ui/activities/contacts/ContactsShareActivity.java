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
import com.eklanku.otuChat.utils.helpers.vcard.ContactVCardConvertHelper;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import ezvcard.VCard;
import rx.Observer;

public class ContactsShareActivity extends BaseLoggableActivity implements SearchView.OnQueryTextListener {
    private static final String TAG = ContactsShareActivity.class.getSimpleName();
    public static final int REQUEST_CONTACTS_LIST = 701;

    private ArrayList<VCard> contactsModels;
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
        contactsModels = new ArrayList<>();
        contactsAdapter = new ContactsShareAdapter(contactsModels, this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewContacts.setLayoutManager(linearLayoutManager);
        recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewContacts.setAdapter(contactsAdapter);
        initAdapter();
    }

    private void initAdapter() {
        showProgress();
        AddressBookHelper.getInstance().getContactVCardListSilentUpdate().subscribe(new Observer<List<VCard>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                hideProgressImmediately();
                ErrorUtils.logError(TAG, e);
            }

            @Override
            public void onNext(List<VCard> vCards) {
                hideProgressImmediately();
                contactsModels.clear();
                contactsModels.addAll(vCards);
                ContactVCardConvertHelper.sortVCards(contactsModels);
                contactsAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult requestCode= " + requestCode + ", resultCode= " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                ArrayList<String> rawContactsModels = bundle.getStringArrayList(ContactDetails.EXTRA_VC_CONTACTS_MODEL_LIST);
                sendContacts(rawContactsModels);
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
        ArrayList<VCard> contactsVCard = new ArrayList<>(contactsAdapter.getSelectedContacts());
        ArrayList<String> contactsRaw = ContactVCardConvertHelper.convertVCardListToStringList(contactsVCard);
        ContactDetails.startForResult(this, REQUEST_CONTACTS_LIST, contactsRaw);
    }

    private void sendContacts(ArrayList<String> rawContacts) {
        Bundle extras = new Bundle();
        extras.putString(ConstsCore.EXTRA_CONTACTS, ContactVCardConvertHelper.createContactsJsonFromList(rawContacts));

        Intent result = new Intent();
        result.putExtras(extras);
        setResult(RESULT_OK, result);
        finish();
    }
}
