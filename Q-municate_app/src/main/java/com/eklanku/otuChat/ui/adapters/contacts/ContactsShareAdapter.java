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
import com.eklanku.otuChat.ui.activities.contacts.ContactsModel;
import com.quickblox.q_municate_core.qb.helpers.QBFriendListHelper;
import com.quickblox.q_municate_core.utils.OnlineStatusUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactsShareAdapter extends RecyclerView.Adapter<ContactsShareAdapter.ViewHolder> implements Filterable {
    private static final String TAG = ContactsShareAdapter.class.getSimpleName();

    private QBFriendListHelper qbFriendListHelper;

    private List<ContactsModel> contactsModels;
    private Set<ContactsModel> selectedContacts;
    private List<ContactsModel> mainList;
    private SparseBooleanArray itemStateArray = new SparseBooleanArray();

    private final Context context;

    public ContactsShareAdapter(List<ContactsModel> contactsModels, Context context) {
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
        final ContactsModel contact = contactsModels.get(position);
        holder.bind(position);

        holder.mTvUsername.setText(contact.getFullName());
        Log.v(TAG, "AMBRA onBindViewHolder number: " + contact.getLogin());

        if (contact.getIsReg_type().equals("1")) {
            holder.contactStatus.setVisibility(View.VISIBLE);
            setStatus(holder.contactStatus, contact.getId_user());
        } else {
            holder.contactStatus.setVisibility(View.GONE);
        }

        holder.mRlContacts.setOnClickListener(view -> {
            Log.d(TAG, "AMBRA setOnClickListener contact= " + contact);
            holder.mChkSelect.setChecked(!holder.mChkSelect.isChecked());

            if (!itemStateArray.get(position, false)) {
                Log.d(TAG, "AMBRA setOnClickListener selectedContacts.add");
                holder.mChkSelect.setVisibility(View.VISIBLE);
                selectedContacts.add(contact);
                itemStateArray.put(position, true);
            } else {
                Log.d(TAG, "AMBRA setOnClickListener remove.add");
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
                    List<ContactsModel> filteredList = new ArrayList<>();
                    for (ContactsModel model : mainList) {
                        if (model.getFullName().toLowerCase().contains(charString.toLowerCase())) {
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
                contactsModels = (ArrayList<ContactsModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return contactsModels == null ? 0 : contactsModels.size();
    }

    public Set<ContactsModel> getSelectedContacts() {
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
