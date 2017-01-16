package com.min.smalltalk;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.dumpapp.DumperPlugin;
import com.facebook.stetho.inspector.database.DefaultDatabaseConnectionProvider;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;
import com.min.smalltalk.exception.CrashHandler;
import com.min.smalltalk.constant.Const;
import com.min.smalltalk.db.FriendInfoDAOImpl;
import com.min.smalltalk.db.GroupMemberDAOImpl;
import com.min.smalltalk.db.GroupsDAOImpl;
import com.min.smalltalk.wedget.RongDatabaseDriver;
import com.min.smalltalk.wedget.RongDatabaseFilesProvider;

import io.rong.imageloader.cache.disc.naming.Md5FileNameGenerator;
import io.rong.imageloader.core.DisplayImageOptions;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.core.ImageLoaderConfiguration;
import io.rong.imageloader.core.display.FadeInBitmapDisplayer;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.RealTimeLocationMessageProvider;
import io.rong.imlib.ipc.RongExceptionHandler;

/**
 * Created by Min on 2016/11/23.
 */

public class App extends Application {
    private static App sInstance;
    private static DisplayImageOptions options;

    private GroupsDAOImpl groupsDAO;
    private GroupMemberDAOImpl groupMemberDAO;
    private FriendInfoDAOImpl friendInfoDAO;
    private String userid;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance=this;
        //
        Stetho.initialize(new Stetho.Initializer(this) {
            @Override
            protected Iterable<DumperPlugin> getDumperPlugins() {
                return new Stetho.DefaultDumperPluginsBuilder(App.this).finish();
            }

            @Override
            protected Iterable<ChromeDevtoolsDomain> getInspectorModules() {
                Stetho.DefaultInspectorModulesBuilder defaultInspectorModulesBuilder = new Stetho.DefaultInspectorModulesBuilder(App.this);
                defaultInspectorModulesBuilder.provideDatabaseDriver(new RongDatabaseDriver(App.this, new RongDatabaseFilesProvider(App.this), new DefaultDatabaseConnectionProvider()));
                return defaultInspectorModulesBuilder.finish();
            }
        });
        //在这里为应用设置异常处理，然后程序才能获取未处理的异常
        CrashHandler crashHandler=CrashHandler.getsInstance();
        crashHandler.init(this);
//        CrashHandler.getInstance().init(this);

        userid=getSharedPreferences("config",MODE_PRIVATE).getString(Const.LOGIN_ID,"");

        groupsDAO=new GroupsDAOImpl(this);
        friendInfoDAO=new FriendInfoDAOImpl(this);


        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if(getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {
            //LeakCanary.install(this);//内存泄露检测
            /*RongPushClient.registerHWPush(this);
            RongPushClient.registerMiPush(this, "2882303761517473625", "5451747338625");
            try {
                RongPushClient.registerGCM(this);
            } catch (RongException e) {
                e.printStackTrace();
            }*/

            /**
             * 初始化
             */
            RongIM.init(this);
            //自定义功能
            AppContext.init(this);
            SharedPreferencesContext.init(this);
            Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));

            try {
//                RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
                RongIM.registerMessageTemplate(new RealTimeLocationMessageProvider());
//                RongIM.registerMessageType(TestMessage.class);
//                RongIM.registerMessageTemplate(new TestMessageProvider());
            } catch (Exception e) {
                e.printStackTrace();
            }

            openSealDBIfHasCachedToken();

            options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(R.mipmap.ic_launcher) //设置图片Uri为空或是错误的时候显示的图片
                    .showImageOnFail(R.mipmap.ic_launcher) //设置图片加载/解码过程中错误时候显示的图片
                    .showImageOnLoading(R.mipmap.ic_launcher) //设置图片在下载期间显示的图片
                    .displayer(new FadeInBitmapDisplayer(300))
                    .cacheInMemory(true) ///设置下载的图片是否缓存在内存中
                    .cacheOnDisk(true)  //设置下载的图片是否缓存在SD卡中
                    .build();

            //初始化图片下载组件
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheSize(50 * 1024 * 1024)
                    .diskCacheFileCount(200)
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .defaultDisplayImageOptions(options)
                    .build();

            //Initialize ImageLoader with configuration.
            ImageLoader.getInstance().init(config);
        }
    }

    public static  DisplayImageOptions getOptions(){
        return options;
    }

    private void openSealDBIfHasCachedToken() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String cachedToken = sp.getString("loginToken", "");
        if (!TextUtils.isEmpty(cachedToken)) {
            String current = getCurProcessName(this);
            String mainProcessName = getPackageName();
            if (mainProcessName.equals(current)) {
//                SealUserInfoManager.getInstance().openDB();
            }
        }
    }

    /**
     * 获取当前进程的名字
     */
    public static String getCurProcessName(Context context){
        ActivityManager activityManager= (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningAppProcessInfo appProcessInfo : activityManager.getRunningAppProcesses()){
            if(appProcessInfo.pid==android.os.Process.myPid()){
                return appProcessInfo.processName;
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        groupsDAO.delete(userid);
        friendInfoDAO.delete(userid);
    }
}
