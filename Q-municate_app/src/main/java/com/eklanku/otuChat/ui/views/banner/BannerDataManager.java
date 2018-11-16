package com.eklanku.otuChat.ui.views.banner;

import android.util.Log;

import com.eklanku.otuChat.App;
import com.eklanku.otuChat.ui.activities.payment.models.DataBanner;
import com.eklanku.otuChat.ui.activities.payment.models.LoadBanner;
import com.eklanku.otuChat.ui.activities.rest.ApiClientPayment;
import com.eklanku.otuChat.ui.activities.rest.ApiInterfacePayment;
import com.eklanku.otuChat.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BannerDataManager extends Observable
{
  private static final String TAG = BannerDataManager.class.getSimpleName();
  private static final String DEFAULT_URL_1 = "https://res.cloudinary.com/dzmpn8egn/image/upload/c_scale,h_200,w_550/v1516817488/Asset_1_okgwng.png";
  private static final String DEFAULT_URL_2 = "https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287475/tagihan_audevp.jpg";
  private static final String DEFAULT_URL_3 = "https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287476/XL_Combo_egiyva.jpg";
  private static final String DEFAULT_URL_4 = "https://res.cloudinary.com/dzmpn8egn/image/upload/c_mfit,h_170/v1516287866/listrik_g5gtxa.jpg";

  private static BannerDataManager instance;
  String strApIUse = "OTU";
  private final ApiInterfacePayment mApiInterfacePayment;
  List<String> urls = new ArrayList<>();

  public static BannerDataManager getInstance() {
    if (instance == null) {
      instance = new BannerDataManager();
    }
    return instance;
  }

  public BannerDataManager()
  {
    mApiInterfacePayment = ApiClientPayment.getClient().create(ApiInterfacePayment.class);
    fillByDefault();
  }

  private void updateUrls()
  {
    Call<LoadBanner> callLoadBanner = mApiInterfacePayment.getBanner(PreferenceUtil.getNumberPhone(App.getInstance().getApplicationContext()), strApIUse);
    callLoadBanner.enqueue(new Callback<LoadBanner>() {
      @Override
      public void onResponse(Call<LoadBanner> call, Response<LoadBanner> response) {

        if (response.isSuccessful()) {
          String status = response.body().getStatus();
          urls.clear();
          if (status.equals("SUCCESS")) {
            final List<DataBanner> result = response.body().getRespMessage();
            if (result.size() > 0) {
              try {
                for (int i = 0; i < result.size(); i++) {
                  urls.add(result.get(i).getBaner_promo());
                }
              } catch (Exception e) {
                Log.e(TAG, e.getMessage());
              }
            }
          }

          if (urls.size()==0)
          {
            fillByDefault();
          }

          setChanged();
          notifyObservers();
        }
      }

      @Override
      public void onFailure(Call<LoadBanner> call, Throwable t) {
        fillByDefault();

        setChanged();
        notifyObservers();
      }
    });
  }

  private void fillByDefault()
  {
    urls.clear();
    urls.add(DEFAULT_URL_1);
    urls.add(DEFAULT_URL_2);
    urls.add(DEFAULT_URL_3);
    urls.add(DEFAULT_URL_4);
  }

  public List<String> getUrls(boolean fetchUpdates)
  {
    if (fetchUpdates && urls.size() > 0 && urls.get(0).equals(DEFAULT_URL_1))
    {
      updateUrls();
    }
    return urls;

  }
}
