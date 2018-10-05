package com.eklanku.otuChat.ui.adapters.search;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eklanku.otuChat.ui.adapters.base.BaseClickListenerViewHolder;
import com.eklanku.otuChat.ui.adapters.base.BaseFilterAdapter;
import com.eklanku.otuChat.ui.adapters.base.BaseViewHolder;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.base.BaseActivity;
import com.eklanku.otuChat.ui.adapters.base.BaseClickListenerViewHolder;
import com.eklanku.otuChat.ui.adapters.base.BaseViewHolder;
import com.eklanku.otuChat.ui.adapters.base.BaseFilterAdapter;
import com.eklanku.otuChat.utils.DateUtils;
import com.eklanku.otuChat.utils.listeners.UserOperationListener;
import com.eklanku.otuChat.ui.views.roundedimageview.RoundedImageView;
import com.eklanku.otuChat.utils.helpers.TextViewHelper;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.models.UserCustomData;
import com.quickblox.q_municate_core.qb.helpers.QBFriendListHelper;
import com.quickblox.q_municate_core.utils.OnlineStatusUtils;
import com.quickblox.q_municate_core.utils.Utils;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_db.models.Friend;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.model.QMUser;
import com.connectycube.users.model.ConnectycubeUser;

import java.util.List;

import butterknife.Bind;

public class GlobalSearchAdapter extends BaseFilterAdapter<ConnectycubeUser, BaseClickListenerViewHolder<ConnectycubeUser>> {

    private DataManager dataManager;
    private UserOperationListener userOperationListener;
    private QBFriendListHelper friendListHelper;

    public GlobalSearchAdapter(BaseActivity baseActivity, List<ConnectycubeUser> list) {
        super(baseActivity, list);
        dataManager = DataManager.getInstance();
    }

    @Override
    protected boolean isMatch(ConnectycubeUser item, String query) {
        return item.getFullName() != null && item.getFullName().toLowerCase().contains(query);
    }

    @Override
    public BaseClickListenerViewHolder<ConnectycubeUser> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(this, layoutInflater.inflate(R.layout.item_user, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseClickListenerViewHolder<ConnectycubeUser> baseClickListenerViewHolder, int position) {
        ConnectycubeUser user = getItem(position);
        ViewHolder holder = (ViewHolder) baseClickListenerViewHolder;

        if (user.getFullName() != null) {
            holder.fullNameTextView.setText(user.getFullName());
        } else {
            holder.fullNameTextView.setText(user.getId());
        }

        final UserCustomData userCustomData = Utils.customDataToObject(user.getCustomData());
        String avatarUrl = userCustomData.getAvatarUrl();
        displayAvatarImage(avatarUrl, holder.avatarImageView);

        checkVisibilityItems(holder, user);

        initListeners(holder, user.getId());

        if (!TextUtils.isEmpty(query)) {
            TextViewHelper.changeTextColorView(baseActivity, holder.fullNameTextView, query);
        }
    }

    private void initListeners(ViewHolder viewHolder, final int userId) {
        viewHolder.addFriendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userOperationListener.onAddUserClicked(userId);
            }
        });
    }

    public void setUserOperationListener(UserOperationListener userOperationListener) {
        this.userOperationListener = userOperationListener;
    }

    public void setFriendListHelper(QBFriendListHelper friendListHelper) {
        this.friendListHelper = friendListHelper;
        notifyDataSetChanged();
    }

    private void checkVisibilityItems(ViewHolder viewHolder, ConnectycubeUser user) {
        if (isFriendOrPending(user)) {
            checkVisibilityItemsMyContacts(viewHolder, user);
        } else {
            checkVisibilityItemsAllUsers(viewHolder, user);
        }
    }

    private void checkVisibilityItemsAllUsers(ViewHolder viewHolder, ConnectycubeUser user) {
        boolean me = AppSession.getSession().getUser().getId().equals(user.getId());
        viewHolder.addFriendImageView.setVisibility(me ? View.GONE : View.VISIBLE);
        viewHolder.statusTextView.setVisibility(View.GONE);
    }

    private void checkVisibilityItemsMyContacts(ViewHolder viewHolder, ConnectycubeUser user) {
        String status;
        QMUser pendingUser = dataManager.getUserRequestDataManager().getUserRequestById(user.getId());

        if (pendingUser != null) {
            status = resources.getString(R.string.search_pending_request_status);
            viewHolder.statusTextView.setTextColor(resources.getColor(R.color.dark_gray));
            viewHolder.statusTextView.setText(status);
        } else {
            setOnlineStatus(viewHolder, user);
        }

        viewHolder.statusTextView.setVisibility(View.VISIBLE);
        viewHolder.addFriendImageView.setVisibility(View.GONE);
    }

    private boolean isFriendOrPending(ConnectycubeUser user) {
        Friend friend = dataManager.getFriendDataManager().getByUserId(user.getId());
        QMUser pendingUser = dataManager.getUserRequestDataManager().getUserRequestById(user.getId());
        return friend != null || pendingUser != null;
    }

    private void setOnlineStatus(ViewHolder viewHolder, ConnectycubeUser user) {
        boolean online = friendListHelper != null && friendListHelper.isUserOnline(user.getId());

        if (online) {
            viewHolder.statusTextView.setText(OnlineStatusUtils.getOnlineStatus(online));
            viewHolder.statusTextView.setTextColor(resources.getColor(R.color.green));
        } else {
            QMUser userFromDb = QMUserService.getInstance().getUserCache().get((long) user.getId());
            if (userFromDb != null){
                user = userFromDb;
            }

            viewHolder.statusTextView.setText(resources.getString(R.string.last_seen,
                    DateUtils.toTodayYesterdayShortDateWithoutYear2(user.getLastRequestAt().getTime()),
                    DateUtils.formatDateSimpleTime(user.getLastRequestAt().getTime())));
            viewHolder.statusTextView.setTextColor(resources.getColor(R.color.dark_gray));
        }
    }

    protected static class ViewHolder extends BaseViewHolder<ConnectycubeUser> {

        @Bind(R.id.avatar_imageview)
        RoundedImageView avatarImageView;

        @Bind(R.id.name_textview)
        TextView fullNameTextView;

        @Bind(R.id.status_textview)
        TextView statusTextView;

        @Bind(R.id.add_friend_imagebutton)
        ImageView addFriendImageView;

        public ViewHolder(GlobalSearchAdapter adapter, View view) {
            super(adapter, view);
        }
    }
}