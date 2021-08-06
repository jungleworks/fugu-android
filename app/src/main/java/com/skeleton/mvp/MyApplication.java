package com.skeleton.mvp;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.skeleton.mvp.util.Foreground;
import com.skeleton.mvp.util.googlemap.MyEmojiManager;
import com.vanniktech.emoji.twitter.TwitterEmojiProvider;

import java.lang.ref.WeakReference;

import io.branch.referral.Branch;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.paperdb.Paper;

import static com.skeleton.mvp.constant.FuguAppConstant.FONT_REGULAR;


/**
 * Developer: Click Labs
 * <p>
 * The Application class
 */

public class MyApplication extends Application {
    private static WeakReference<Context> mWeakReference;
    /**
     * Getter to access Singleton instance
     *
     * @return instance of MyApplication
     */
    public static Context getAppContext() {
        return mWeakReference.get();
    }
    @Override
    public void onCreate() {
        super.onCreate();
//        MultiDex.install(this);
        Foreground.init(this);
        Paper.init(this);
        MyEmojiManager.install(new TwitterEmojiProvider());
        mWeakReference = new WeakReference<>(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath(FONT_REGULAR)
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
        // Branch logging for debugging
        Branch.enableLogging();

        // Branch object initialization
        Branch.getAutoInstance(this);

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
