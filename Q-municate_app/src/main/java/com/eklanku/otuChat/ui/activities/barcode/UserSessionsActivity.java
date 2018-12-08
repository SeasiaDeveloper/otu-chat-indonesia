package com.eklanku.otuChat.ui.activities.barcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.connectycube.auth.session.ConnectycubeSessionManager;
import com.connectycube.auth.session.ConnectycubeUserSessionDetails;
import com.eklanku.otuChat.R;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.adapters.sessions.UserSessionAdapter;
import com.eklanku.otuChat.ui.fragments.dialogs.base.TwoButtonsDialogFragment;
import com.eklanku.otuChat.utils.helpers.ServiceManager;
import com.quickblox.q_municate_db.utils.ErrorUtils;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;

public class UserSessionsActivity extends BaseLoggableActivity {
    private static final String TAG = UserSessionsActivity.class.getSimpleName();

    private ArrayList<ConnectycubeUserSessionDetails> userSessionDetails = new ArrayList<>();
    private UserSessionAdapter userSessionAdapter;

    @Bind(R.id.rv_user_sessions)
    RecyclerView rvUserSessions;

    public static void start(Context context) {
        Intent intent = new Intent(context, UserSessionsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_user_sessions;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setUpActionBarWithUpButton();
        initViews();
    }

    @Override
    protected void setUpActionBarWithUpButton() {
        title = getString(R.string.web_qr_title);
        super.setUpActionBarWithUpButton();
    }

    private void initViews() {
        ServiceManager.getInstance().getUserSessions()
                .doOnSubscribe(this::showProgress)
                .doOnTerminate(this::hideProgress)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<ConnectycubeUserSessionDetails>>() {
                    @Override
                    public void onCompleted() {
                        proceedInitialization();
                    }

                    @Override
                    public void onError(Throwable e) {
                        ErrorUtils.logError(TAG, e.getLocalizedMessage());
                        finish();
                    }

                    @Override
                    public void onNext(ArrayList<ConnectycubeUserSessionDetails> userSessionList) {
                        userSessionDetails.clear();
                        userSessionDetails.addAll(userSessionList);
                        removeCurrentSessionFromList();
                    }
                });
    }

    private void removeCurrentSessionFromList() {
        for (Iterator<ConnectycubeUserSessionDetails> iterator = userSessionDetails.iterator(); iterator.hasNext(); ) {
            ConnectycubeUserSessionDetails sessionDetails = iterator.next();
            if (sessionDetails.getSessionId().equals(ConnectycubeSessionManager.getInstance().getActiveSession().getId())) {
                iterator.remove();
                break;
            }
        }
    }

    private void updateAdapterByIndex(int index) {
        userSessionDetails.remove(index);
        userSessionAdapter.notifyDataSetChanged();
    }

    private void proceedInitialization() {
        if (!userSessionDetails.isEmpty()) {
            initAdapter();
        } else {
            WebQRCodeActivity.start(this);
            finish();
        }
    }

    private void initAdapter() {
        userSessionAdapter = new UserSessionAdapter(userSessionDetails, new OnItemClickListenerImpl(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvUserSessions.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvUserSessions.getContext(),
                linearLayoutManager.getOrientation());
        rvUserSessions.addItemDecoration(dividerItemDecoration);
        rvUserSessions.setItemAnimator(new DefaultItemAnimator());

        rvUserSessions.setAdapter(userSessionAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_user_sessions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                WebQRCodeActivity.start(this);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class OnItemClickListenerImpl implements OnItemClickListener {

        @Override
        public void onItemClick(ConnectycubeUserSessionDetails sessionDetails, int index) {
            TwoButtonsDialogFragment.show(getSupportFragmentManager(), getString(R.string.dlg_logout_device_confirmation), false,
                    new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            deleteUserSessionWithId(sessionDetails, index);
                        }
                    });
        }

        private void deleteUserSessionWithId(ConnectycubeUserSessionDetails sessionDetails, int index) {
            ServiceManager.getInstance().deleteUserSessionById(sessionDetails.getSessionId())
                    .doOnSubscribe(UserSessionsActivity.this::showProgress)
                    .doOnTerminate(UserSessionsActivity.this::hideProgress)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Void>() {
                        @Override
                        public void onCompleted() {
                            updateAdapterByIndex(index);
                            finishActivityIfNoSessions();
                        }

                        @Override
                        public void onError(Throwable e) {
                            ErrorUtils.logError(TAG, e.getLocalizedMessage());
                        }

                        @Override
                        public void onNext(Void aVoid) {
                        }
                    });
        }

        private void finishActivityIfNoSessions() {
            if (userSessionDetails.isEmpty()) {
                finish();
            }
        }

        @Override
        public void onFooterClick() {
            TwoButtonsDialogFragment.show(getSupportFragmentManager(), getString(R.string.dlg_logout_all_devices_confirmation), false,
                    new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            deleteAllUserSessionsExceptCurrent();
                        }
                    });
        }

        private void deleteAllUserSessionsExceptCurrent() {
            ServiceManager.getInstance().deleteUserSessionsExceptCurrent()
                    .doOnSubscribe(UserSessionsActivity.this::showProgress)
                    .doOnTerminate(UserSessionsActivity.this::hideProgress)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Void>() {
                        @Override
                        public void onCompleted() {
                            finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            ErrorUtils.logError(TAG, e.getLocalizedMessage());
                        }

                        @Override
                        public void onNext(Void aVoid) {
                        }
                    });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ConnectycubeUserSessionDetails item, int index);

        void onFooterClick();
    }
}
