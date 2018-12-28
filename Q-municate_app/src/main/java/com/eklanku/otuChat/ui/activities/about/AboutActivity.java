package com.eklanku.otuChat.ui.activities.about;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.ui.activities.main.PreferenceManager;
import com.connectycube.auth.session.ConnectycubeSessionManager;
import com.connectycube.auth.session.ConnectycubeSettings;
import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.agreements.UserAgreementActivity;
import com.eklanku.otuChat.ui.activities.base.BaseLoggableActivity;
import com.eklanku.otuChat.utils.StringObfuscator;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

public class AboutActivity extends BaseLoggableActivity {

    PreferenceManager preferenceManager;
    public String firstLaunch, referrerDate, referrerDataRaw, referrerDataDecoded;

    @Bind(R.id.app_version_textview)
    TextView appVersionTextView;

    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_about;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();
        fillUI();
        preferenceManager = new PreferenceManager(this);
        HashMap<String, String> user = preferenceManager.getUserDetailsPayment();
        firstLaunch = user.get(preferenceManager.KEY_REFF_LAUNCH);
        referrerDate = user.get(preferenceManager.KEY_REFF_DATE);
        referrerDataRaw = user.get(preferenceManager.KEY_REFF_RAW);
        referrerDataDecoded = user.get(preferenceManager.KEY_REFF_DECODE);

        TextView lblOtu = findViewById(R.id.lbl_otu);
        lblOtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog();
            }
        });

    }


    private void dialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_referrer);
        dialog.setCancelable(false);

        final TextView tvData = (TextView) dialog.findViewById(R.id.data_referrer);

        StringBuilder sb = new StringBuilder();
        sb.append("<b>First launch:</b>")
                .append("<br/>")
                .append(firstLaunch)
                .append("<br/><br/>")
                .append("<b>Referrer detection:</b>")
                .append("<br/>")
                .append(referrerDate);
        //if (isReferrerDetected) {
        sb.append("<br/><br/>")
                .append("<b>Raw referrer:</b>")
                .append("<br/>")
                .append(referrerDataRaw);

        //if (ViewDataActivity.mainActivity.referrerDataDecoded != null) {
        sb.append("<br/><br/>")
                .append("<b>Decoded referrer:</b>")
                .append("<br/>")
                .append(referrerDataDecoded);
        // }
        // }

        tvData.setText(Html.fromHtml(sb.toString()));
        tvData.setMovementMethod(new LinkMovementMethod());

        Button dialogButtonX = (Button) dialog.findViewById(R.id.btn_ok);
        dialogButtonX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void initFields() {
        title = getString(R.string.about_title);
    }

    private void fillUI() {
        appVersionTextView.setText(StringObfuscator.getAppVersionName());
    }

    @OnClick(R.id.license_button)
    void openUserAgreement(View view) {
        UserAgreementActivity.start(this);
    }
}
