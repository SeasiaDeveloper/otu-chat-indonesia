package com.eklanku.otuChat.ui.adapters.contacts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eklanku.otuChat.R;
import com.quickblox.q_municate_core.qb.helpers.QBFriendListHelper;
import com.quickblox.q_municate_core.utils.OnlineStatusUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ezvcard.VCard;

public class ContactsShareAdapter extends RecyclerView.Adapter<ContactsShareAdapter.ViewHolder> implements Filterable {
    private static final String TAG = ContactsShareAdapter.class.getSimpleName();

    private QBFriendListHelper qbFriendListHelper;

    private List<VCard> contactsModels;
    private Set<VCard> selectedContacts;
    private List<VCard> mainList;
    private SparseBooleanArray itemStateArray = new SparseBooleanArray();

    private final Context context;

    public ContactsShareAdapter(List<VCard> contactsModels, Context context) {
        this.contactsModels = contactsModels;
        selectedContacts = new HashSet<>();
        this.mainList = contactsModels;
        this.context = context;
    }

    public void setFriendListHelper(QBFriendListHelper qbFriendListHelper) {
        this.qbFriendListHelper = qbFriendListHelper;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_contacts_share, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final VCard contact = contactsModels.get(position);
        holder.bind(position);

        String contactName = contact.getFormattedName() != null ? contact.getFormattedName().getValue() : contact.getTelephoneNumbers().get(0).getText();
        holder.mTvUsername.setText(contactName);


        holder.mRlContacts.setOnClickListener(view -> {
            holder.mChkSelect.setChecked(!holder.mChkSelect.isChecked());

            if (!itemStateArray.get(position, false)) {
                holder.mChkSelect.setVisibility(View.VISIBLE);
                selectedContacts.add(contact);
                itemStateArray.put(position, true);
            } else {
                holder.mChkSelect.setVisibility(View.INVISIBLE);
                selectedContacts.remove(contact);
                itemStateArray.put(position, false);
            }
        });
    }

    private void setStatus(TextView textView, int userId) {
        boolean online = qbFriendListHelper != null && qbFriendListHelper.isUserOnline(userId);

        textView.setText(OnlineStatusUtils.getOnlineStatus(online));
        if (online) {
            textView.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            textView.setTextColor(context.getResources().getColor(R.color.gray));
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactsModels = mainList;
                } else {
                    List<VCard> filteredList = new ArrayList<>();
                    for (VCard model : mainList) {
                        if (model.getFormattedName().getValue().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(model);
                        }
                    }
                    contactsModels = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = contactsModels;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactsModels = (ArrayList<VCard>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return contactsModels == null ? 0 : contactsModels.size();
    }

    public Set<VCard> getSelectedContacts() {
        return selectedContacts;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvUsername, contactStatus;
        public CheckBox mChkSelect;
        public LinearLayout mRlContacts;

        public ViewHolder(View view) {
            super(view);
            mTvUsername = view.findViewById(R.id.tvUsername);
            contactStatus = view.findViewById(R.id.contact_status);
            mChkSelect = view.findViewById(R.id.chkSelect);
            mRlContacts = view.findViewById(R.id.rlContacts);
        }

        void bind(int position) {
            // use the sparse boolean array to check
            if (!itemStateArray.get(position, false)) {
                mChkSelect.setVisibility(View.INVISIBLE);
                mChkSelect.setChecked(false);
            } else {
                mChkSelect.setVisibility(View.VISIBLE);
                mChkSelect.setChecked(true);
            }
        }
    }
}
