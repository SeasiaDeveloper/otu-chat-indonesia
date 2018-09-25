package com.eklanku.otuChat.ui.adapters.search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eklanku.otuChat.ui.activities.chats.NewMessageActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModel;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.chats.PrivateDialogActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModelGroup;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.commands.chat.QBCreatePrivateChatCommand;
import com.quickblox.q_municate_core.qb.helpers.QBChatHelper;
import com.quickblox.q_municate_core.utils.ChatUtils;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.utils.DialogTransformUtils;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import com.eklanku.otuChat.ui.activities.contacts.ContactsActivity;


public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> implements Filterable {

    private List<ContactsModel> contactsModels;
    private List<ContactsModel> mainList;


    private final Context context;
    private DataManager dataManager = DataManager.getInstance();
    private QBUser qbUser = AppSession.getSession().getUser();
    private boolean isToGroup = false;

    //Rina
    PreferenceManager preferenceManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTvUsername, mTvMessage, mTvPhonenumber;
        public ImageView mIvChat;
        public RelativeLayout mRlContacts;
        public CheckBox mChkSelect;
//        public ImageView mCreateGroup;
//        public ImageView mInvitePeople;

        public MyViewHolder(View view) {
            super(view);
            mTvUsername = (TextView) view.findViewById(R.id.tvUsername);
            mTvPhonenumber = (TextView) view.findViewById(R.id.tvPhonenumber);
            mIvChat = (ImageView) view.findViewById(R.id.ivChat);
            mTvMessage = (TextView) view.findViewById(R.id.tvMessage);
            mRlContacts = (RelativeLayout) view.findViewById(R.id.rlContacts);
            mChkSelect = (CheckBox) view.findViewById(R.id.chkSelect);
//            mCreateGroup = (ImageView) view.findViewById(R.id.btn_create_group);
//            mInvitePeople = (ImageView) view.findViewById(R.id.btn_invite_people);

        }
    }


    public ContactsAdapter(List<ContactsModel> contactsModels, Context context) {
        this.contactsModels = contactsModels;
        this.mainList = contactsModels;
        this.context = context;
        isToGroup = ((ContactsActivity) context).isToGroup;
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_contacts, parent, false);
        preferenceManager = new PreferenceManager(context);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final com.eklanku.otuChat.ui.activities.contacts.ContactsModel contact = contactsModels.get(position);

        holder.mTvUsername.setText(contact.getFullName());
        Log.v("Contacts New Meesage","number: "+contact.getLogin());

        //holder.mTvPhonenumber.setText(contact.getLogin());
        if(isToGroup){
//            holder.mCreateGroup.setVisibility(View.GONE);
//            holder.mInvitePeople.setVisibility(View.GONE);
            holder.mIvChat.setVisibility(View.GONE);
            holder.mChkSelect.setVisibility(View.VISIBLE);
            holder.mChkSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Integer userId = Integer.valueOf(contact.getId_user());
                    if(isChecked) {
                        if(!((ContactsActivity) context).friendIdsList.contains(userId))
                            ((ContactsActivity) context).friendIdsList.add(userId);
                    } else {
                        ((ContactsActivity) context).friendIdsList.remove(userId);
                    }
                }
            });
        } else {


            holder.mIvChat.setVisibility(View.VISIBLE);
            holder.mChkSelect.setVisibility(View.GONE);
            if (contact.getIsReg_type().equals("1")) {
                holder.mIvChat.setVisibility(View.GONE);
                //holder.mTvMessage.setText("Using this App");
            } else {
                holder.mIvChat.setVisibility(View.VISIBLE);
                //holder.mTvMessage.setText("Invite");
            }
        }

        holder.mRlContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contact.getIsReg_type().equals("1")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //QBChatDialog privateDialog = new QBChatDialog();
                            QBChatHelper chatHelper = new QBChatHelper(context);
                            QBChatDialog privateDialog = null;
                            try {
                                privateDialog = chatHelper.createPrivateDialogIfNotExist(contact.getId_user());
                                List<DialogOccupant> occupantsList = dataManager.getDialogOccupantDataManager()
                                        .getDialogOccupantsListByDialogId(privateDialog.getDialogId());
                                QMUser opponent = ChatUtils.getOpponentFromPrivateDialog(UserFriendUtils.createLocalUser(qbUser), occupantsList);
                                checkForOpenChat(opponent);

                            } catch (QBResponseException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //NewMessageActivity.startForResult(ContactsActivity.newInstance(), CREATE_DIALOG);
                } else {

                    HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
                    String strUserID = user.get(preferenceManager.KEY_USERID);

                    String number = contact.getLogin();
                    Uri uri = Uri.parse("smsto:" + number);
                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                    it.putExtra("sms_body", "https://play.google.com/store/apps/details?id=com.eklanku.otuChat&referrer=" + strUserID);
                    context.startActivity(it);
                    //context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
                }
            }
        });
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
                    List<com.eklanku.otuChat.ui.activities.contacts.ContactsModel> filteredList = new ArrayList<>();
                    for (com.eklanku.otuChat.ui.activities.contacts.ContactsModel model : mainList) {
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
                contactsModels = (ArrayList<com.eklanku.otuChat.ui.activities.contacts.ContactsModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return contactsModels == null ? 0 : contactsModels.size();
    }

    private void checkForOpenChat(QMUser user) {
        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupantForPrivateChat(user.getId());
        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            QBChatDialog chatDialog = DialogTransformUtils.createQBDialogFromLocalDialog(dataManager, dialogOccupant.getDialog());
            startPrivateChat(chatDialog, user);
        } else {
            QBCreatePrivateChatCommand.start(context, user);

        }
    }

    private void startPrivateChat(QBChatDialog dialog, QMUser selectedUser) {
        PrivateDialogActivity.start(context, selectedUser, dialog);
    }
}
