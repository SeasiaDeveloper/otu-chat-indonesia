package com.eklanku.otuChat.ui.adapters.search;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eklanku.otuChat.ui.fragments.search.ContactsModel;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.chats.PrivateDialogActivity;
import com.eklanku.otuChat.ui.fragments.search.ContactsModel;
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

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private final List<ContactsModel> contactsModels;
    private final Context context;
    private DataManager dataManager = DataManager.getInstance();
    private QBUser qbUser = AppSession.getSession().getUser();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTvUsername, mTvMessage, mTvPhonenumber;
        public ImageView mIvChat;
        public RelativeLayout mRlContacts;

        public MyViewHolder(View view) {
            super(view);
            mTvUsername = (TextView) view.findViewById(R.id.tvUsername);
            mTvPhonenumber = (TextView) view.findViewById(R.id.tvPhonenumber);
            mIvChat = (ImageView) view.findViewById(R.id.ivChat);
            mTvMessage = (TextView) view.findViewById(R.id.tvMessage);
            mRlContacts = (RelativeLayout) view.findViewById(R.id.rlContacts);

        }
    }


    public ContactsAdapter(List<ContactsModel> contactsModels, Context context) {
        this.contactsModels = contactsModels;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_contacts, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ContactsModel contact = contactsModels.get(position);

        holder.mTvUsername.setText(contact.getFullName());
        //holder.mTvPhonenumber.setText(contact.getLogin());

        if (contact.getIsReg_type().equals("1")) {
            holder.mIvChat.setVisibility(View.GONE);
            //holder.mTvMessage.setText("Using this App");
        } else {
            holder.mIvChat.setVisibility(View.VISIBLE);
            //holder.mTvMessage.setText("Invite");
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
                    //NewMessageActivity.startForResult(ContactsFragment.newInstance(), CREATE_DIALOG);
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
