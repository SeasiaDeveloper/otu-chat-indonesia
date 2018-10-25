package com.eklanku.otuChat.ui.adapters.contacts;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModel;

import java.util.List;

public class ContactsDetailsAdapter extends RecyclerView.Adapter<ContactsDetailsAdapter.ViewHolder> {
    private static final String TAG = ContactsDetailsAdapter.class.getSimpleName();

    private List<ContactsModel> contactsModels;

    public ContactsDetailsAdapter(List<ContactsModel> contactsModels) {
        this.contactsModels = contactsModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "AMBRA onCreateViewHolder");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contacts_details, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactsDetailsAdapter.ViewHolder holder, int position) {
        final ContactsModel contact = contactsModels.get(position);
        Log.d(TAG, "AMBRA onBindViewHolder number: " + contact.getPhone());
        holder.name.setText(contact.getFullName());
        holder.number.setText(contact.getPhone());
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return contactsModels.size();
    }

    public List<ContactsModel> getContacts() {
        return contactsModels;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
        public LinearLayout mRlContacts;

        public ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.contact_name);
            number = view.findViewById(R.id.contact_number_view);
            mRlContacts = view.findViewById(R.id.rlContacts);
        }

        void bind(int position) {

        }
    }
}
