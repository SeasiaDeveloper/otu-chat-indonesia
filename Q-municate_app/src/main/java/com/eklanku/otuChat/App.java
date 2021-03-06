package com.eklanku.otuChat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.connectycube.auth.session.ConnectycubeSettings;
import com.connectycube.chat.ConnectycubeChatService;
import com.connectycube.chat.connections.tcp.TcpChatConnectionFabric;
import com.connectycube.chat.connections.tcp.TcpConfigurationBuilder;
import com.connectycube.core.LogLevel;
import com.connectycube.core.ConnectycubeHttpConnectionConfig;
import com.connectycube.core.ServiceZone;
import com.eklanku.otuChat.BuildConfig;
import com.eklanku.otuChat.utils.ActivityLifecycleHandler;
import com.eklanku.otuChat.utils.StringObfuscator;
import com.eklanku.otuChat.utils.helpers.ServiceManager;
import com.eklanku.otuChat.utils.helpers.SharedHelper;
import com.eklanku.otuChat.utils.image.ImageLoaderUtils;
import com.quickblox.q_municate_auth_service.QMAuthService;
import com.quickblox.q_municate_core.models.AppSession;
import com.quickblox.q_municate_core.utils.ConstsCore;
import com.quickblox.q_municate_db.managers.DataManager;
import com.quickblox.q_municate_user_cache.QMUserCacheImpl;
import com.quickblox.q_municate_user_service.QMUserService;
import com.quickblox.q_municate_user_service.cache.QMUserCache;

import io.fabric.sdk.android.Fabric;

public class App extends MultiDexApplication {

    private static final String TAG = App.class.getSimpleName();

    private static App instance;
    private SharedHelper appSharedHelper;
    private SessionListener sessionListener;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        Log.i(TAG, "onCreate with update");
        initFabric();
        initFirebaseRemoteConfig();
        initApplication();
        registerActivityLifecycleCallbacks(new ActivityLifecycleHandler());

        AppSession.load();
    }

    private void initFirebaseRemoteConfig()
    {
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_default);
    }

    private void initFabric() {
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();

        Fabric.with(this, crashlyticsKit);
    }

    private void initApplication() {
        instance = this;

        sessionListener = new SessionListener();
        getAppSharedHelper();
        initQb();
        initDb();
        initImageLoader(this);
        initServices();
    }

    private void initQb() {
        ConnectycubeSettings.getInstance().init(getApplicationContext(),
                StringObfuscator.getApplicationId(),
                StringObfuscator.getAuthKey(),
                StringObfuscator.getAuthSecret());
        ConnectycubeSettings.getInstance().setAccountKey(StringObfuscator.getAccountKey());

        initDomains();
        initHTTPConfig();

        TcpConfigurationBuilder configurationBuilder = new TcpConfigurationBuilder()
                .setAutojoinEnabled(false)
                .setSocketTimeout(0);

        ConnectycubeChatService.setConnectionFabric(new TcpChatConnectionFabric(configurationBuilder));
        ConnectycubeChatService.setDebugEnabled(true);
    }

    private void initDomains() {
        ConnectycubeSettings.getInstance().setEndpoints(StringObfuscator.getApiEndpoint(), StringObfuscator.getChatEndpoint(), ServiceZone.PRODUCTION);
        ConnectycubeSettings.getInstance().setZone(ServiceZone.PRODUCTION);
        ConnectycubeSettings.getInstance().setLogLevel(LogLevel.DEBUG);
    }

    private void initHTTPConfig(){
        ConnectycubeHttpConnectionConfig.setConnectTimeout(ConstsCore.HTTP_TIMEOUT_IN_SECONDS);
        ConnectycubeHttpConnectionConfig.setReadTimeout(ConstsCore.HTTP_TIMEOUT_IN_SECONDS);
    }

    private void initDb() {
        DataManager.init(this);
    }

    private void initImageLoader(Context context) {
        ImageLoader.getInstance().init(ImageLoaderUtils.getImageLoaderConfiguration(context));
    }

    private void initServices() {
        QMAuthService.init();
        QMUserCache userCache = new QMUserCacheImpl(this);
        QMUserService.init(userCache);

        ServiceManager.getInstance();
    }

    public synchronized SharedHelper getAppSharedHelper() {
        return appSharedHelper == null
                ? appSharedHelper = new SharedHelper(this)
                : appSharedHelper;
    }

    public FirebaseRemoteConfig getFirebaseRemoteConfig()
    {
        return mFirebaseRemoteConfig;
    }

    public boolean isNeedToUpdate()
    {
        long versionServer = mFirebaseRemoteConfig.getLong("versi");
        long versionApp = BuildConfig.VERSION_CODE;

        return versionApp < versionServer;
    }

    public void fetchFirebaseRemoteConfigValues() {
        long cacheExpiration = 3600; // 1 hour in seconds.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                        }
                    }
                });
    }

}