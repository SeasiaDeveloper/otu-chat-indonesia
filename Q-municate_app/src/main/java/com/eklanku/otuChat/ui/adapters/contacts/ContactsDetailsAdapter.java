package com.eklanku.otuChat.ui.adapters.contacts;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.connectycube.core.helper.ToStringHelper;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.contacts.ContactDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ezvcard.VCard;
import ezvcard.property.Email;
import ezvcard.property.Organization;
import ezvcard.property.Telephone;
import ezvcard.property.Url;
import ezvcard.property.VCardProperty;

public class ContactsDetailsAdapter extends RecyclerView.Adapter<ContactsDetailsAdapter.ViewHolder> {
    private static final String TAG = ContactsDetailsAdapter.class.getSimpleName();

    private ContactDetails.ButtonClickListener clickListener;
    private List<VCard> contactsModels;
    private LayoutInflater inflater;
    private ArrayList<VCard> selectedContacts;
    private HashMap<VCardProperty, Boolean> itemStateArray = new HashMap<>();
    private boolean showAddButton;

    public ContactsDetailsAdapter(List<VCard> contactsModels, ArrayList<VCard> selectedContacts, boolean showAddButton) {
        this.contactsModels = contactsModels;
        this.selectedContacts = selectedContacts;
        this.showAddButton = showAddButton;
    }

    public void setButtonClickListener(ContactDetails.ButtonClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater
                .inflate(R.layout.list_item_contacts_details, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactsDetailsAdapter.ViewHolder holder, int position) {
        final VCard contact = contactsModels.get(position);

        String contactName = contact.getFormattedName() != null ? contact.getFormattedName().getValue() : contact.getTelephoneNumbers().get(0).getText();
        holder.name.setText(contactName);

        holder.bind(contact);
//        inflate numbers
        for (Telephone telephone : contact.getTelephoneNumbers()) {
            updateItemStateArray(telephone);

            View inflatedNumberLayout = inflater.inflate(R.layout.item_contact_phone_number_details, (ViewGroup) holder.itemView, false);
            holder.viewStub.addView(inflatedNumberLayout);

            TextView numberView = inflatedNumberLayout.findViewById(R.id.contact_number_view);
            numberView.setText(telephone.getText());

            TextView typeView = inflatedNumberLayout.findViewById(R.id.contact_number_type);

            String type = ToStringHelper.arrayToString(telephone.getTypes()).replaceAll("\\[|\\]", "");
            typeView.setText(type);

            CheckBox checkBox = inflatedNumberLayout.findViewById(R.id.chkSelect);
            bindCheckBoxIfNeed(telephone, checkBox, position);
        }
//        inflate email
        for (Email email : contact.getEmails()) {
            updateItemStateArray(email);

            View inflatedNumberLayout = inflater.inflate(R.layout.item_contact_email_details, (ViewGroup) holder.itemView, false);
            holder.viewStub.addView(inflatedNumberLayout);

            TextView emailView = inflatedNumberLayout.findViewById(R.id.contact_email_view);
            emailView.setText(email.getValue());

            TextView typeView = inflatedNumberLayout.findViewById(R.id.contact_email_type);

            String type = ToStringHelper.arrayToString(email.getTypes()).replaceAll("\\[|\\]", "");
            typeView.setText(type);

            CheckBox checkBox = inflatedNumberLayout.findViewById(R.id.chkSelect);
            bindCheckBoxIfNeed(email, checkBox, position);
        }

//        inflate Url
        for (Url url : contact.getUrls()) {
            updateItemStateArray(url);

            View inflatedNumberLayout = inflater.inflate(R.layout.item_contact_url_details, (ViewGroup) holder.itemView, false);
            holder.viewStub.addView(inflatedNumberLayout);

            TextView urlView = inflatedNumberLayout.findViewById(R.id.contact_url_view);
            urlView.setText(url.getValue());

            TextView typeView = inflatedNumberLayout.findViewById(R.id.contact_url_type);

            String type = ToStringHelper.arrayToString(url.getType()).replaceAll("\\[|\\]", "");
            typeView.setText(type == null || type.equals("null") ? "" : type);

            CheckBox checkBox = inflatedNumberLayout.findViewById(R.id.chkSelect);
            bindCheckBoxIfNeed(url, checkBox, position);
        }
//        inflate organization
        Organization organization = contact.getOrganization();
        if (organization != null) {
            updateItemStateArray(organization);
            String company = organization.getValues().get(0);

            View inflatedNumberLayout = inflater.inflate(R.layout.item_contact_company_details, (ViewGroup) holder.itemView, false);
            holder.viewStub.addView(inflatedNumberLayout);

            TextView companyView = inflatedNumberLayout.findViewById(R.id.contact_company_view);
            companyView.setText(company);

            CheckBox checkBox = inflatedNumberLayout.findViewById(R.id.chkSelect);
            bindCheckBoxIfNeed(organization, checkBox, position);
        }
//        inflate other properties
    }

    private void updateItemStateArray(VCardProperty telephone) {
        if (!itemStateArray.containsKey(telephone)) {
            itemStateArray.put(telephone, true);
        }
    }

    private void bindCheckBoxIfNeed(VCardProperty item, CheckBox checkBox, int position) {
        setCheckBoxVisibility(checkBox);
        if (!showAddButton) {
            updateCheckBoxState(item, checkBox, position);
        }
    }

    private void updateCheckBoxState(VCardProperty item, CheckBox checkBox, int position) {
        // use the sparse boolean array to check
        if (!itemStateArray.get(item)) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!itemStateArray.get(item)) {

                VCard vCard = selectedContacts.get(position);
                vCard.addProperty(item);
                selectedContacts.set(position, vCard);
                itemStateArray.put(item, true);
            } else {
                VCard vCard = selectedContacts.get(position);
                vCard.removeProperty(item);
                selectedContacts.set(position, vCard);
                itemStateArray.put(item, false);
            }
        });
    }

    private void setCheckBoxVisibility(CheckBox checkBox) {
        if (showAddButton) {
            checkBox.setVisibility(View.INVISIBLE);
        } else {
            checkBox.setVisibility(View.VISIBLE);
        }
    }

    private void setAddButtonVisibility(View addButton) {
        if (showAddButton) {
            addButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onViewRecycled(@NonNull ContactsDetailsAdapter.ViewHolder holder) {
        holder.viewStub.removeAllViews();
    }

    public ArrayList<VCard> getSelectedContacts() {
        return selectedContacts;
    }

    @Override
    public int getItemCount() {
        return contactsModels.size();
    }

    public List<VCard> getContacts() {
        return contactsModels;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
        public LinearLayout mRlContacts;
        public LinearLayout viewStub;
        public Button btnAddContact;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.contact_name);
            mRlContacts = view.findViewById(R.id.rlContacts);
            viewStub = view.findViewById(R.id.view_stub_phones);
            btnAddContact = view.findViewById(R.id.btn_add_contact);
        }

        void bind(VCard contact) {
            if (showAddButton) {
                btnAddContact.setVisibility(View.VISIBLE);
                btnAddContact.setOnClickListener(v -> clickListener.onClick(contact));
            } else {
                btnAddContact.setVisibility(View.INVISIBLE);
            }
        }
    }
}
