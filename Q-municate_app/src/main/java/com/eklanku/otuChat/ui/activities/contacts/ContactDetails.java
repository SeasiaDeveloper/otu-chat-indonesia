package com.eklanku.otuChat.ui.activities.contacts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.adapters.contacts.ContactsDetailsAdapter;
import com.eklanku.otuChat.utils.helpers.vcard.ContactVCardConvertHelper;
import com.eklanku.otuChat.utils.helpers.vcard.ContactVCardIntentHelper;
import com.quickblox.q_municate_core.models.AppSession;

import java.util.ArrayList;

import butterknife.Bind;
import ezvcard.Ezvcard;
import ezvcard.VCard;

public class ContactDetails extends BaseLoggableActivity {
    private static String TAG = ContactDetails.class.getSimpleName();
    public static String EXTRA_VC_CONTACTS_MODEL_LIST = "vc_contact_model_list";
    public static String EXTRA_SHOW_ADD = "show_add";

    private ArrayList<VCard> contacts;
    private ArrayList<VCard> selectedContacts;
    private ContactsDetailsAdapter contactsAdapter;
    private boolean showAddButton;

    @Bind(R.id.rvContacts)
    RecyclerView recyclerViewContacts;

    @Bind(R.id.fab_send_contacts)
    FloatingActionButton sendButton;

    public static void startForResult(Activity activity, int code, ArrayList<String> contacts) {
        Intent intent = new Intent(activity, ContactDetails.class);
        intent.putStringArrayListExtra(EXTRA_VC_CONTACTS_MODEL_LIST, contacts);
        activity.startActivityForResult(intent, code);
    }

    public static void start(Activity activity, ArrayList<String> contacts, boolean showAdd) {
        Intent intent = new Intent(activity, ContactDetails.class);
        intent.putStringArrayListExtra(EXTRA_VC_CONTACTS_MODEL_LIST, contacts);
        intent.putExtra(EXTRA_SHOW_ADD, showAdd);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = " " + AppSession.getSession().getUser().getFullName();
        setUpActionBarWithUpButton();
        initExtras();
        initContacts();

        setSendButtonVisibility(!showAddButton);

        contactsAdapter = new ContactsDetailsAdapter(contacts, selectedContacts, showAddButton);
        contactsAdapter.setButtonClickListener(new ButtonClickListenerImpl());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewContacts.setLayoutManager(linearLayoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewContacts.getContext(),
//                linearLayoutManager.getOrientation());
//        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_horizontal_big));
//        recyclerViewContacts.addItemDecoration(dividerItemDecoration);
        recyclerViewContacts.setItemAnimator(new DefaultItemAnimator());
        recyclerViewContacts.setAdapter(contactsAdapter);
    }

    private void initContacts() {
        ArrayList<String> contactsRow = getIntent().getStringArrayListExtra(EXTRA_VC_CONTACTS_MODEL_LIST);
        contacts = new ArrayList<>();
        selectedContacts = new ArrayList<>();
        for (String vCardStr : contactsRow) {
            contacts.add(Ezvcard.parse(vCardStr).first());
            selectedContacts.add(Ezvcard.parse(vCardStr).first());
        }
        ContactVCardConvertHelper.sortVCards(contacts);
    }

    private void initExtras() {
        showAddButton = getIntent().getBooleanExtra(EXTRA_SHOW_ADD, false);
    }

    private void setSendButtonVisibility(boolean showButton) {
        if (showButton) {
            sendButton.show();
        } else {
            sendButton.hide();
        }
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_contact_details;
    }

    public void onSendContacts(View view) {
        Log.d(TAG, "onSendContacts getSelectedContacts= " + contactsAdapter.getSelectedContacts());
        ArrayList<String> contactsRaw = ContactVCardConvertHelper.convertVCardListToStringList(contactsAdapter.getSelectedContacts());

        Bundle extras = new Bundle();
        extras.putStringArrayList(EXTRA_VC_CONTACTS_MODEL_LIST, contactsRaw);
        Intent result = new Intent();
        result.putExtras(extras);
        setResult(RESULT_OK, result);
        finish();
    }

    public interface ButtonClickListener {
        void onClick(VCard card);
    }

    public class ButtonClickListenerImpl implements ButtonClickListener {

        @Override
        public void onClick(VCard card) {
            Intent intent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
            intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
            ContactVCardIntentHelper.fillContactIntentInsertData(card, intent);

            startActivity(intent);
        }
    }
}
