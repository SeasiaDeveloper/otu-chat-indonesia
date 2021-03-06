package com.eklanku.otuChat.ui.adapters.chats;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eklanku.otuChat.ui.adapters.base.BaseListAdapter;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;
import com.eklanku.otuChat.utils.DateUtils;
import com.eklanku.otuChat.utils.listeners.UserOperationListener;
import com.quickblox.q_municate_core.qb.helpers.QBFriendListHelper;
import com.quickblox.q_municate_core.utils.OnlineStatusUtils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.cache.QMUserCache;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.users.model.ConnectycubeUser;
import com.eklanku.otuChat.R;;
import com.quickblox.q_municate_core.models.AppSession;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.ui.adapters.base.BaseListAdapter;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;

import java.util.List;

public class GroupDialogOccupantsAdapter extends BaseListAdapter<QMUser> {

    private UserOperationListener userOperationListener;
    private QBFriendListHelper qbFriendListHelper;

    public GroupDialogOccupantsAdapter(BaseActivity baseActivity, UserOperationListener userOperationListener, List<QMUser> objectsList) {
        super(baseActivity, objectsList);
        this.userOperationListener = userOperationListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        QMUser user = getItem(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_dialog_friend, null);
            viewHolder = new ViewHolder();

            viewHolder.avatarImageView = (RoundedImageView) convertView.findViewById(R.id.avatar_imageview);
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.name_textview);
            viewHolder.onlineStatusTextView = (TextView) convertView.findViewById(R.id.status_textview);
            viewHolder.addFriendImageView = (ImageView) convertView.findViewById(R.id.add_friend_imagebutton);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String fullName;
       // if (isFriendValid(user)) {
        //    fullName = user.getFullName();
        Log.d("OPPO-1", "getView: "+user.getUsername());
        /*if(user.getUsername().isEmpty()) {
            if (isFriendValid(user)) {
                fullName = user.getFullName();
            }else {
                //fullName = String.valueOf(user.getId());
                fullName = user.getUsername();
            }
        } else {
            fullName = String.valueOf(user.getId());
        }*/
        if(user.getUsername() != null && !user.getUsername().isEmpty()) {
            fullName = user.getUsername();
        } else {
            fullName = user.getFullName();
        }

        viewHolder.nameTextView.setText(fullName);

        setStatus(viewHolder, user);
       // viewHolder.addFriendImageView.setVisibility(isFriend(user) ? View.GONE : View.VISIBLE);
        viewHolder.addFriendImageView.setVisibility(View.GONE);
        initListeners(viewHolder, user.getId());

        displayAvatarImage(user.getAvatar(), viewHolder.avatarImageView);

        return convertView;
    }

    private void initListeners(ViewHolder viewHolder, final int userId) {
        viewHolder.addFriendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userOperationListener.onAddUserClicked(userId);
            }
        });
    }

    private void setStatus(ViewHolder viewHolder, QMUser user) {
        boolean online = qbFriendListHelper != null && qbFriendListHelper.isUserOnline(user.getId());

        if (isMe(user)) {
            online = true;
        }

        if (online) {
            viewHolder.onlineStatusTextView.setText(OnlineStatusUtils.getOnlineStatus(online));
            viewHolder.onlineStatusTextView.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            QMUser userFromDb = QMUserService.getInstance().getUserCache().get((long) user.getId());
            if (userFromDb != null){
                user = userFromDb;
            }

            viewHolder.onlineStatusTextView.setText(context.getString(R.string.last_seen,
                    DateUtils.toTodayYesterdayShortDateWithoutYear2(user.getLastRequestAt() != null ? user.getLastRequestAt().getTime() : 0),
                    DateUtils.formatDateSimpleTime(user.getLastRequestAt() != null ? user.getLastRequestAt().getTime() : 0)));
            viewHolder.onlineStatusTextView.setTextColor(context.getResources().getColor(R.color.white));
        }
    }

    public void setFriendListHelper(QBFriendListHelper qbFriendListHelper) {
        this.qbFriendListHelper = qbFriendListHelper;
        notifyDataSetChanged();
    }

    private boolean isFriendValid(QMUser user) {
        return user.getFullName() != null;
    }

    private boolean isFriend(QMUser user) {
        if (isMe(user)) {
            return true;
        } else {
            boolean outgoingUserRequest = DataManager.getInstance().getUserRequestDataManager().existsByUserId(user.getId());
            boolean friend = DataManager.getInstance().getFriendDataManager().getByUserId(user.getId()) != null;
            return friend || outgoingUserRequest;
        }
    }

    private boolean isMe(QMUser inputUser) {
        ConnectycubeUser currentUser = AppSession.getSession().getUser();
        return currentUser.getId().intValue() == inputUser.getId().intValue();
    }

    private static class ViewHolder {

        RoundedImageView avatarImageView;
        TextView nameTextView;
        ImageView addFriendImageView;
        TextView onlineStatusTextView;
    }
}