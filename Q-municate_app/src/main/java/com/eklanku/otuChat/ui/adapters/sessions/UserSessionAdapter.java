package com.eklanku.otuChat.ui.adapters.sessions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.connectycube.auth.session.ConnectycubeUserSessionDetails;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.barcode.UserSessionsActivity;
import com.eklanku.otuChat.utils.DateUtils;

import java.util.ArrayList;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;

public class UserSessionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int FOOTER_VIEW = 1;

    private ArrayList<ConnectycubeUserSessionDetails> userSessionList;
    private UserSessionsActivity.OnItemClickListener listener;
    private Context context;

    public UserSessionAdapter(ArrayList<ConnectycubeUserSessionDetails> userSessionList, UserSessionsActivity.OnItemClickListener listener, Context context) {
        this.userSessionList = userSessionList;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case FOOTER_VIEW:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_session_footer, parent, false));
            default:
                return new UserSessionViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_session, parent, false));
        }
    }

    public void onBindViewUserSessionViewHolder(@NonNull UserSessionViewHolder userSessionViewHolder, int position) {
        ConnectycubeUserSessionDetails sessionDetails = userSessionList.get(position);

        long milliSeconds = sessionDetails.getLastActivity().getTime();

        String dateDay = DateUtils.toTodayYesterdayShortDateWithoutYear2(milliSeconds);
        String dateHours = DateUtils.formatDateSimpleTime(milliSeconds);
        String lastActivity = context.getResources().getString(R.string.last_active, dateDay, dateHours);

        userSessionViewHolder.lastActivityTextView.setText(lastActivity);

        UserAgent userAgentParser = UserAgent.parseUserAgentString(sessionDetails.getUserAgent());

        userSessionViewHolder.userAgentTextView.setText(parseUserAgentOS(userAgentParser));
        userSessionViewHolder.iconImageView.setImageDrawable(parseUserAgentBrowser(userAgentParser));

        userSessionViewHolder.itemView.setOnClickListener(v -> {
            listener.onItemClick(sessionDetails, position);
        });
    }

    private String parseUserAgentOS(UserAgent userAgentParser) {
        String versionOS = userAgentParser.getOperatingSystem().getName();
        if (versionOS != null) {
            versionOS = versionOS.replace(DeviceType.TABLET.getName(), "");
        }
        if (!userAgentParser.getBrowser().getName().equals(Browser.UNKNOWN.getName())) {
            versionOS += " (" + userAgentParser.getBrowser().getName() + ")";
        }
        return versionOS;
    }

    private TextDrawable parseUserAgentBrowser(UserAgent userAgentParser) {
        String name = userAgentParser.getBrowser().getName() != null ? userAgentParser.getBrowser().getName()
                : userAgentParser.getOperatingSystem().getName();
        name = !name.equals(Browser.UNKNOWN.getName()) ? name : userAgentParser.getOperatingSystem().getName();
        String firstLetter = String.valueOf(name.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        return TextDrawable.builder()
                .buildRound(firstLetter, color);
    }

    public void onBindViewFooterViewHolder(@NonNull FooterViewHolder footerViewHolder, int position) {
        footerViewHolder.itemView.setOnClickListener(v -> listener.onFooterClick());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int valueType = getItemViewType(position);
        switch (valueType) {
            case FOOTER_VIEW:
                onBindViewFooterViewHolder((FooterViewHolder) holder, position);
                break;
            default:
                onBindViewUserSessionViewHolder((UserSessionViewHolder) holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (userSessionList == null) {
            return 0;
        }

        if (userSessionList.size() == 0) {
            //Return 1 here to show nothing
            return 1;
        }

        // Add extra view to show the footer view
        return userSessionList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == userSessionList.size()) {
            // This is where we'll add footer.
            return FOOTER_VIEW;
        }

        return super.getItemViewType(position);
    }

    class UserSessionViewHolder extends RecyclerView.ViewHolder {
        TextView lastActivityTextView;
        TextView userAgentTextView;
        ImageView iconImageView;

        UserSessionViewHolder(View view) {
            super(view);
            lastActivityTextView = view.findViewById(R.id.last_activity_view);
            userAgentTextView = view.findViewById(R.id.user_agent_view);
            iconImageView = view.findViewById(R.id.icon_view);

        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
