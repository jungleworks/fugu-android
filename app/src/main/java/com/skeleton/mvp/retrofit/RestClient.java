package com.skeleton.mvp.retrofit;


import android.text.TextUtils;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.ui.AppConstants;
import com.skeleton.mvp.utils.FuguUtils;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static com.skeleton.mvp.constant.FuguAppConstant.LIVE_SERVER;

/**
 * Rest Client
 */
public class RestClient {

    public static Retrofit retrofit = null;


    /**
     * @return
     */
    public static ApiInterface getApiInterface() {
        if (retrofit == null) {
            if (!TextUtils.isEmpty(CommonData.getFuguServerUrl())) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(CommonData.getFuguServerUrl())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient().build())
                        .build();
            } else {
                retrofit = new Retrofit.Builder()
                        .baseUrl(LIVE_SERVER)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient().build())
                        .build();
            }
        }
        return retrofit.create(ApiInterface.class);
    }


    /**
     * @return
     */
    public static Retrofit getRetrofitBuilder() {
        if (retrofit == null) {
            if (!TextUtils.isEmpty(CommonData.getFuguServerUrl())) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(CommonData.getFuguServerUrl())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient().build())
                        .build();
            } else {
                retrofit = new Retrofit.Builder()
                        .baseUrl(LIVE_SERVER)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient().build())
                        .build();
            }
        }
        return retrofit;
    }

    /**
     * @return
     */
    private static OkHttpClient.Builder httpClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        //logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
//        logging.setLevel(HttpLoggingInterceptor.Level.NONE);
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        }
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS);
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header(AppConstants.DOMAIN, FuguUtils.Companion.getDomain())
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });
        if(BuildConfig.DEBUG) httpClient.addInterceptor(logging);
        return httpClient;
    }

}
