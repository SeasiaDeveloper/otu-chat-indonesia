package com.eklanku.otuChat.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eklanku.otuChat.R;;
import com.eklanku.otuChat.ui.activities.payment.TestActivity;
import com.eklanku.otuChat.ui.activities.payment.models.DataBanner;
import com.eklanku.otuChat.ui.activities.payment.models.DataProfile;
import com.eklanku.otuChat.ui.activities.payment.models.LoadBanner;
import com.eklanku.otuChat.ui.activities.payment.settingpayment.Register;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.ui.views.banner.GlideImageLoader;
import com.eklanku.otuChat.utils.PreferenceUtil;
import com.eklanku.otuChat.utils.listeners.LoadingData;
import com.yyydjk.library.BannerLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallFragment extends Fragment implements LoadingData {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @Bind(R.id.CallsList)
    RecyclerView CallsList;
    @Bind(R.id.empty)
    LinearLayout emptyConversations;

    @Bind(R.id.swipeCalls)
    SwipeRefreshLayout mSwipeRefreshLayout;

    FloatingActionButton call;
    FrameLayout frameLayoutEmpty;
    TextView emptyListTextView;

    BannerLayout banner;
    ApiInterfacePayment mApiInterfacePayment;
    String strApIUse = "OTU";
    private static String[] banner_promo;


    public CallFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CallFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CallFragment newInstance(String param1, String param2) {
        CallFragment fragment = new CallFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_call_new, container, false);
        initializeResources(view);
        frameLayoutEmpty.setVisibility(View.VISIBLE);
        emptyListTextView.setVisibility(View.VISIBLE);
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        //loadBanner();
        setEmptyMessage();
        // Inflate the layout for this fragment
        //setHasOptionsMenu(true);
        /*TextView tvCall = view.findViewById(R.id.tv_call_fragment);
        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), TestActivity.class));
            }
        });*/

        Activity activity = getActivity();
        if (activity != null && isAdded()) {

            // isi fungsi loadbanner, cek member
            loadBanner();
            if (!PreferenceUtil.isMemberStatus(getActivity())) {
                cekMember();
            }

        }

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* startActivity(new Intent(getActivity(), TestActivity.class));*/
                Toast.makeText(getActivity(), "Sedang dalam pengembangan", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.call_info_menu, menu);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onErrorLoading(Throwable throwable) {

    }

    private void initializeResources(View view) {
        call= view.findViewById(R.id.fab_dialogs_new_call);
        frameLayoutEmpty = view.findViewById(R.id.frameEmptyList);
        emptyListTextView = view.findViewById(R.id.empty_list_textview);
        banner = view.findViewById(R.id.bannerLayout);

    }

    private void setEmptyMessage() {
        String textBeforeImage = getString(R.string.dialog_no_call_before_image_string);
        String textAfterImage = getString(R.string.dialog_no_call_after_image_string);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(textBeforeImage + " ").append(" ");
        builder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.default_text_icon_otu_color)), 14, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Drawable chat = getResources().getDrawable(R.drawable.ic_tabs_call_otu);
        chat.setBounds(0, 0, 27, 27);
        builder.setSpan(new ImageSpan(chat, ImageSpan.ALIGN_BASELINE),
                builder.length() - 1, builder.length(), 0);
        builder.append(" " + textAfterImage);

        emptyListTextView.setText(builder);
    }

    public void loadBanner() {

        banner.setImageLoader(new GlideImageLoader());
        List<String> urls = new ArrayList<>();
        Call<LoadBanner> callLoadBanner = mApiInterfacePayment.getBanner(PreferenceUtil.getNumberPhone(getActivity()), strApIUse);
        callLoadBanner.enqueue(new Callback<LoadBanner>() {
            @Override
            public void onResponse(Call<LoadBanner> call, Response<LoadBanner> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    if (status.equals("SUCCESS")) {
                        final List<DataBanner> result = response.body().getRespMessage();
                        banner_promo = new String[result.size()];
                        if (result.size() > 0) {
                            try {
                                for (int i = 0; i < result.size(); i++) {
                                    banner_promo[i] = result.get(i).getBaner_promo();
                                    urls.add(banner_promo[i]);
                                }
                            } catch (Exception e) {
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");
                            }
                        } else {
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                            urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");
                        }
                    } else {
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                        urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");
                    }
                    banner.setViewUrls(urls);
                }
            }

            @Override
            public void onFailure(Call<LoadBanner> call, Throwable t) {
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png");
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg");
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg");
                urls.add("https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg");

                banner.setViewUrls(urls);
            }
        });
    }

    //fungsi cek member
    private void cekMember() {
        Call<DataProfile> isMember = mApiInterfacePayment.isMember(PreferenceUtil.getNumberPhone(getActivity()), "OTU");

        isMember.enqueue(new Callback<DataProfile>() {
            @Override
            public void onResponse(Call<DataProfile> call, Response<DataProfile> response) {
                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    String msg = response.body().getRespMessage();
                    String errNumber = response.body().getErrNumber();
                    if (errNumber.equalsIgnoreCase("0")) {
                        PreferenceUtil.setMemberStatus(getActivity(), true);
                    } else if (errNumber.equalsIgnoreCase("5")) {
                        lauchRegister();
                    } else {
                        Toast.makeText(getActivity(), "FAILED GET TOKEN [" + msg + "]", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DataProfile> call, Throwable t) {
                Toast.makeText(getActivity(), getResources().getString(R.string.error_api), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void lauchRegister() {
        Intent register = new Intent(getActivity(), Register.class);
        startActivity(register);
        //finish();
    }
}
