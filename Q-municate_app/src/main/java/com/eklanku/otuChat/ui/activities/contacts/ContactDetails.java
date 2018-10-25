package com.eklanku.otuChat.ui.activities.contacts;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.adapters.contacts.ContactsDetailsAdapter;
import com.eklanku.otuChat.ui.adapters.contacts.ContactsShareAdapter;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.utils.ConstsCore;

import java.io.Serializable;
import java.util.ArrayList;

import butterknife.Bind;

public class ContactDetails extends BaseLoggableActivity {
    private static String TAG = ContactDetails.class.getSimpleName();
    public static String EXTRA_CONTACTS_MODEL_LIST = "contact_model_list";

    private ArrayList<ContactsModel> contacts;
    private ContactsDetailsAdapter contactsAdapter;

    @Bind(R.id.rvContacts)
    RecyclerView recyclerViewContacts;

    public static void startForResult(Activity activity, int code, ArrayList<ContactsModel> contacts) {
        Intent intent = new Intent(activity, ContactDetails.class);
        intent.putExtra(EXTRA_CONTACTS_MODEL_LIST, contacts);
        activity.startActivityForResult(intent, code);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "AMBRA onCreate");
        title = " " + AppSession.getSession().getUser().getFullName();
        setUpActionBarWithUpButton();

        contacts = (ArrayList<ContactsModel>) getIntent().getSerializableExtra(EXTRA_CONTACTS_MODEL_LIST);
        contactsAdapter = new ContactsDetailsAdapter(contacts);
        Log.d(TAG, "AMBRA onCreate contacts= " + contacts);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewContacts.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewContacts.getContext(),
                linearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_horizontal_big));
        recyclerViewContacts.addItemDecoration(dividerItemDecoration);
        recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewContacts.setAdapter(contactsAdapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_contact_details;
    }


    public void onSendContacts(View view) {
        Log.d(TAG, "AMBRA onSendContacts");
        Bundle extras = new Bundle();
        extras.putSerializable(EXTRA_CONTACTS_MODEL_LIST, (Serializable) contactsAdapter.getContacts());
        Intent result = new Intent();
        result.putExtras(extras);
        setResult(RESULT_OK, result);
        finish();
//        Log.d(TAG, "AMBRA onSendContacts contactsAdapter.getSelectedContacts()= " + contactsAdapter.getSelectedContacts());
//        ArrayList<ContactsModel> contacts = new ArrayList<>(contactsAdapter.getSelectedContacts());
//        Log.d(TAG, "AMBRA onSendContacts contacts= " + contacts);
//
//        ContactDetails.startForResult(this, REQUEST_CONTACTS_LIST, contacts);
//        sendContacts(contacts);
    }
}
