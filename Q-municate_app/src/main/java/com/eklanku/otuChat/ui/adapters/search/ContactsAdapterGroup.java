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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.chats.NewGroupDialogActivity;
import com.eklanku.otuChat.ui.activities.chats.NewMessageActivity;
import com.eklanku.otuChat.ui.activities.chats.PrivateDialogActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModelGroup;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.core.exception.ResponseException;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.commands.chat.QBCreatePrivateChatCommand;
import com.quickblox.q_municate_core.qb.helpers.QBChatHelper;
import com.quickblox.q_municate_core.utils.ChatUtils;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.utils.DialogTransformUtils;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.users.model.ConnectycubeUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

;


public class ContactsAdapterGroup extends RecyclerView.Adapter<ContactsAdapterGroup.MyViewHolder> implements Filterable {


    private List<ContactsModelGroup> contactsModelsGroup;
    private List<ContactsModelGroup> mainListGroup;

    private final Context context;
    private DataManager dataManager = DataManager.getInstance();
    private ConnectycubeUser connectycubeUser = AppSession.getSession().getUser();
    private boolean isToGroup = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTvUsername, mTvMessage, mTvPhonenumber;
        public ImageView mIvChat;
        public RelativeLayout mRlContacts;
        public LinearLayout layoutCreateGroup;
        public CheckBox mChkSelect;
        public ImageView IVIcon;

        public MyViewHolder(View view) {
            super(view);
            mTvUsername = (TextView) view.findViewById(R.id.tvUsername);
            mTvPhonenumber = (TextView) view.findViewById(R.id.tvPhonenumber);
            mIvChat = (ImageView) view.findViewById(R.id.ivChat);
            mTvMessage = (TextView) view.findViewById(R.id.tvMessage);
            mRlContacts = (RelativeLayout) view.findViewById(R.id.rlContacts);
            mChkSelect = (CheckBox) view.findViewById(R.id.chkSelect);
            IVIcon = (ImageView) view.findViewById(R.id.ivIcon);

            layoutCreateGroup = view.findViewById(R.id.layout_create_group);

        }
    }


    public ContactsAdapterGroup(List<ContactsModelGroup> contactsModels, Context context) {
        this.contactsModelsGroup = contactsModels;
        this.mainListGroup = contactsModels;
        this.context = context;
        isToGroup = ((NewMessageActivity) context).isToGroup;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_contacts_new, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ContactsModelGroup contact = contactsModelsGroup.get(position);

        //get first letter of each String item
        String fullName = contact.getFullName();
        String firstLetter = String.valueOf(fullName.charAt(0));
        String lastLetter = "";
        String identityName = "";
        try {
            if (fullName.contains(" ")) {
                lastLetter = String.valueOf(fullName.substring(fullName.lastIndexOf(" ") + 1).charAt(0));
                identityName = firstLetter + lastLetter;
            } else {
                identityName = firstLetter;
            }
        } catch (Exception e) {
            identityName = firstLetter;
        }


        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int color = generator.getRandomColor();
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(identityName, color); // radius in px
        holder.IVIcon.setImageDrawable(drawable);
        holder.mTvUsername.setText(contact.getFullName());

        //holder.mTvPhonenumber.setText(contact.getLogin());
        if (isToGroup) {
            holder.mIvChat.setVisibility(View.GONE);
            holder.mChkSelect.setVisibility(View.VISIBLE);
            holder.mChkSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Integer userId = Integer.valueOf(contact.getId_user());
                    if (isChecked) {
                        if (!((ContactsActivity) context).friendIdsList.contains(userId))
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

        if (position == 0) {
            holder.layoutCreateGroup.setVisibility(View.VISIBLE);
        } else {
            holder.layoutCreateGroup.setVisibility(View.GONE);
        }

        holder.layoutCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Create group", Toast.LENGTH_SHORT).show();
                NewGroupDialogActivity.start(context);
            }
        });

        holder.mRlContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contact.getIsReg_type().equals("1")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //ConnectycubeChatDialog privateDialog = new ConnectycubeChatDialog();
                            QBChatHelper chatHelper = new QBChatHelper(context);
                            ConnectycubeChatDialog privateDialog = null;
                            try {
                                privateDialog = chatHelper.createPrivateDialogIfNotExist(contact.getId_user());
                                List<DialogOccupant> occupantsList = dataManager.getDialogOccupantDataManager()
                                        .getDialogOccupantsListByDialogId(privateDialog.getDialogId());
                                QMUser opponent = ChatUtils.getOpponentFromPrivateDialog(UserFriendUtils.createLocalUser(connectycubeUser), occupantsList);
                                checkForOpenChat(opponent);

                            } catch (ResponseException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    //NewMessageActivity.startForResult(ContactsActivity.newInstance(), CREATE_DIALOG);
                } else {
                    String number = contact.getLogin();
                    Uri uri = Uri.parse("smsto:" + number);
                    Intent it = new Intent(Intent.ACTION_SENDTO, uri);
                    it.putExtra("sms_body", "The SMS text");
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
                    contactsModelsGroup = mainListGroup;
                } else {
                    List<ContactsModelGroup> filteredList = new ArrayList<>();
                    for (ContactsModelGroup model : mainListGroup) {
                        if (model.getFullName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(model);
                        }
                    }
                    contactsModelsGroup = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = contactsModelsGroup;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactsModelsGroup = (ArrayList<ContactsModelGroup>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getItemCount() {
        return contactsModelsGroup == null ? 0 : contactsModelsGroup.size();
    }

    private void checkForOpenChat(QMUser user) {
        DialogOccupant dialogOccupant = dataManager.getDialogOccupantDataManager().getDialogOccupantForPrivateChat(user.getId());
        if (dialogOccupant != null && dialogOccupant.getDialog() != null) {
            ConnectycubeChatDialog chatDialog = DialogTransformUtils.createQBDialogFromLocalDialog(dataManager, dialogOccupant.getDialog());
            startPrivateChat(chatDialog, user);
        } else {
            QBCreatePrivateChatCommand.start(context, user);

        }
    }

    private void startPrivateChat(ConnectycubeChatDialog dialog, QMUser selectedUser) {
        PrivateDialogActivity.start(context, selectedUser, dialog);
    }
}
