package com.eklanku.otuChat.ui.adapters.call;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.connectycube.chat.ConnectycubeChatService;
import com.connectycube.chat.model.ConnectycubeChatDialog;
import com.connectycube.users.model.ConnectycubeUser;
import com.connectycube.videochat.RTCTypes;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.call.CallActivity;
import com.eklanku.otuChat.ui.activities.call.ContactListCallActivity;
import com.eklanku.otuChat.ui.activities.chats.PrivateDialogActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsActivity;
import com.eklanku.otuChat.ui.activities.contacts.ContactsModelGroup;
import com.eklanku.otuChat.utils.ToastUtils;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.qb.commands.chat.QBCreatePrivateChatCommand;
import com.quickblox.q_municate_core.utils.UserFriendUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.DialogOccupant;
import com.quickblox.q_municate_db.utils.DialogTransformUtils;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;

import java.util.ArrayList;
import java.util.List;

public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.MyViewHolder> implements Filterable {

    private List<ContactsModelGroup> contactsModelsGroup;
    private List<ContactsModelGroup> mainListGroup;

    private final Context context;
    private DataManager dataManager = DataManager.getInstance();
    private ConnectycubeUser connectycubeUser = AppSession.getSession().getUser();
    private boolean isToGroup = false;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTvUsername, mTvMessage, mTvPhonenumber;
        public ImageView mIvChat;
        public RelativeLayout mRlContacts;
        public CheckBox mChkSelect;
        public ImageView IVIcon;
        ImageButton imgCall, imgVCall;

        public MyViewHolder(View view) {
            super(view);
            mTvUsername = (TextView) view.findViewById(R.id.tvUsername);
            mTvPhonenumber = (TextView) view.findViewById(R.id.tvPhonenumber);
            mIvChat = (ImageView) view.findViewById(R.id.ivChat);
            mTvMessage = (TextView) view.findViewById(R.id.tvMessage);
            mRlContacts = (RelativeLayout) view.findViewById(R.id.rlContacts);
            mChkSelect = (CheckBox) view.findViewById(R.id.chkSelect);
            IVIcon = (ImageView) view.findViewById(R.id.ivIcon);
            imgCall =  view.findViewById(R.id.ivCall);
            imgVCall =  view.findViewById(R.id.ivVideoCall);

        }
    }

    public CallsAdapter(List<ContactsModelGroup> contactsModels, Context context) {
        this.contactsModelsGroup = contactsModels;
        this.mainListGroup = contactsModels;
        this.context = context;
        isToGroup = ((ContactListCallActivity) context).isToGroup;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.row_contact_tab_call, viewGroup, false);

            return new CallsAdapter.MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
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
        Log.v("Contacts New Meesage", "number: " + contact.getLogin());

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
        }

        long cId = contact.getId_user();
        QMUser user = QMUserService.getInstance().getUserCache().get(cId);

        holder.imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callToUser(user, RTCTypes.ConferenceType.CONFERENCE_TYPE_AUDIO);
            }
        });

        holder.imgVCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callToUser(user, RTCTypes.ConferenceType.CONFERENCE_TYPE_VIDEO);

            }
        });

        /*holder.mRlContacts.setOnClickListener(new View.OnClickListener() {
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

        });*/
    }

    private void callToUser(QMUser user, RTCTypes.ConferenceType ConferenceType) {
        Log.d("AYIK", "status calltouser " + isChatInitializedAndUserLoggedIn());
        if (!isChatInitializedAndUserLoggedIn()) {
            ToastUtils.longToast(R.string.call_chat_service_is_initializing);
            return;
        }
        List<ConnectycubeUser> connectycubeUserList = new ArrayList<>(1);
        connectycubeUserList.add(UserFriendUtils.createConnectycubeUser(user));
        CallActivity.start((Activity) context, connectycubeUserList, ConferenceType, null);
    }


    @Override
    public int getItemCount() {
        return contactsModelsGroup == null ? 0 : contactsModelsGroup.size();
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


    public boolean isChatInitializedAndUserLoggedIn() {
        return isAppInitialized() && ConnectycubeChatService.getInstance().isLoggedIn();
    }

    /*private void callToUser(QMUser user, RTCTypes.ConferenceType ConferenceType) {
        Log.d("AYIK", "status calltouser "+isChatInitializedAndUserLoggedIn());
        if (!isChatInitializedAndUserLoggedIn()) {
            ToastUtils.longToast(R.string.call_chat_service_is_initializing);
            return;
        }
        List<ConnectycubeUser> connectycubeUserList = new ArrayList<>(1);
        connectycubeUserList.add(UserFriendUtils.createConnectycubeUser(user));
        CallActivity.start(context, connectycubeUserList, ConferenceType, null);
    }*/

    protected boolean isAppInitialized() {
        return AppSession.getSession().isSessionExist();
    }


}
