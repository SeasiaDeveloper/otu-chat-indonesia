package com.eklanku.otuChat.ui.views.banner2;

import android.util.Log;

import com.eklanku.otuChat.ui.activities.payment.models2.DataBannerPayment;
import com.eklanku.otuChat.ui.activities.payment.models2.DataDetailBannerPayment;
import com.eklanku.otuChat.ui.activities.rest2.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest2.ApiInterfacePayment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerDataManager extends Observable {
    private static final String TAG = BannerDataManager.class.getSimpleName();
    private static final String DEFAULT_URL_1 = "https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png";
    private static final String DEFAULT_URL_2 = "https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg";
    private static final String DEFAULT_URL_3 = "https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg";
    private static final String DEFAULT_URL_4 = "https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg";

    private static BannerDataManager instance;
    String strApIUse = "OTU";
    private final ApiInterfacePayment mApiInterfacePayment;
    List<String> urls = new ArrayList<>();
    List<String> link_promo = new ArrayList<>();
    public Map<String, List<String>> map;

    public static BannerDataManager getInstance() {
        if (instance == null) {
            instance = new BannerDataManager();
        }
        return instance;
    }

    public BannerDataManager() {
        mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
        fillByDefault();
    }

    private void updateUrls() {
        Call<DataBannerPayment> callLoadBanner = mApiInterfacePayment.getBanner();
        callLoadBanner.enqueue(new Callback<DataBannerPayment>() {
            @Override
            public void onResponse(Call<DataBannerPayment> call, Response<DataBannerPayment> response) {

                if (response.isSuccessful()) {
                    String status = response.body().getStatus();
                    urls.clear();
                    link_promo.clear();
                    if (status.equals("SUCCESS")) {
                        final List<DataDetailBannerPayment> result = response.body().getData();
                        if (result.size() > 0) {
                            try {
                                for (int i = 0; i < result.size(); i++) {
                                    urls.add(result.get(i).getBanner_promo());
                                    link_promo.add(result.get(i).getLink_banner());

                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                        Log.d("OPPO-1", "onResponse: status >> " + urls);
                    }

                    if (urls.size() == 0) {
                        fillByDefault();
                    }

                    setChanged();
                    notifyObservers();
                }
            }

            @Override
            public void onFailure(Call<DataBannerPayment> call, Throwable t) {
                Log.d("OPPO-1", "onFailure: " + t.getMessage());
                fillByDefault();

                setChanged();
                notifyObservers();
            }
        });
    }


    private void fillByDefault() {
        urls.clear();
        urls.add(DEFAULT_URL_1);
        urls.add(DEFAULT_URL_2);
        urls.add(DEFAULT_URL_3);
        urls.add(DEFAULT_URL_4);
    }

    public List<String> getUrls(boolean fetchUpdates) {
        if (fetchUpdates && urls.size() > 0 && urls.get(0).equals(DEFAULT_URL_1)) {
            updateUrls();
        }
        return urls;

    }


    public Map<String, List<String>> getList(boolean fetchUpdates) {
        if (fetchUpdates && urls.size() > 0 && urls.get(0).equals(DEFAULT_URL_1)) {
            map = new HashMap<>();
            //map.put("urls", urls);
            map.put("link_promo", link_promo);
        }
        return map;
    }
}
