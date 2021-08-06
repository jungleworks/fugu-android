package com.skeleton.mvp.data.network;

import com.skeleton.mvp.BuildConfig;
import com.skeleton.mvp.MyApplication;
import com.skeleton.mvp.fugudatabase.CommonData;
import com.skeleton.mvp.ui.AppConstants;
import com.skeleton.mvp.utils.FuguUtils;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Developer: Click Labs
 * <p>
 * Rest Client
 */
public final class RestClient {

    private static final int TIME_OUT = 120;
    private static final Integer BKS_KEYSTORE_RAW_FILE_ID = 0;
    // Integer BKS_KEYSTORE_RAW_FILE_ID = R.raw.keystorebks;
    private static final Integer SSL_KEY_PASSWORD_STRING_ID = 0;
    private static Retrofit retrofit = null;
    private static Retrofit retrofitWithIncreaseTimeout = null;
    //Integer SSL_KEY_PASSWORD_STRING_ID = R.string.sslKeyPassword;

    /**
     * Prevent instantiation
     */
    private RestClient() {
    }

    /**
     * Gets api interface.
     *
     * @return object of ApiInterface
     */
    public static ApiInterface getApiInterface(boolean isOfficeChat) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(CommonData.getFuguServerUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient().build())
                    //.client(secureConnection().build())
                    .build();

        return retrofit.create(ApiInterface.class);
    }

    /**
     * Gets image upload api interface.
     *
     * @return the image upload api interface
     */
    public static ApiInterface getImageUploadApiInterface() {
        if (retrofitWithIncreaseTimeout == null) {
            retrofitWithIncreaseTimeout = new Retrofit.Builder()
                    .baseUrl(CommonData.getFuguServerUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getRequestHeader())
                    //.client(secureConnection().build())
                    .build();

        }
        return retrofitWithIncreaseTimeout.create(ApiInterface.class);
    }

    /**
     * Gets retrofit builder.
     *
     * @return object of Retrofit
     */
    static Retrofit getRetrofitBuilder() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(CommonData.getFuguServerUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient().build())
                    .build();
        }
        return retrofit;
    }

    /**
     * Gets request header.
     *
     * @return the request header
     */
    private static OkHttpClient getRequestHeader() {
        return new OkHttpClient.Builder()
                //.addInterceptor(getLoggingInterceptor())
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();
    }

    /**
     * @return object of OkHttpClient.Builder
     */
    private static OkHttpClient.Builder httpClient() {
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header(AppConstants.DOMAIN, FuguUtils.Companion.getDomain())
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });
        if(BuildConfig.DEBUG) httpClient.addInterceptor(getLoggingInterceptor());
        return httpClient;
    }

    /**
     * Method to get object of HttpLoggingInterceptor
     *
     * @return object of HttpLoggingInterceptor
     */
    private static HttpLoggingInterceptor getLoggingInterceptor() {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        //logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
//        if (BuildConfig.DEBUG) {
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        }
        return logging;
    }

    /**
     * Method to create secure connection of api call with out own certificate
     *
     * @return object of OkHttpClient.Builder
     * @throws KeyStoreException        throws exception related to key store
     * @throws CertificateException     throws exception related to certificate
     * @throws NoSuchAlgorithmException throws exception if also not found
     * @throws IOException              throws IO exception
     * @throws KeyManagementException   throws key related exception
     */
    private static OkHttpClient.Builder secureConnection() throws
            KeyStoreException,
            CertificateException,
            NoSuchAlgorithmException,
            IOException,
            KeyManagementException {

        InputStream certificateInputStream = null;
        certificateInputStream = MyApplication.getAppContext().getResources().openRawResource(BKS_KEYSTORE_RAW_FILE_ID);
        final KeyStore trustStore = KeyStore.getInstance("BKS");
        try {
            trustStore.load(certificateInputStream,
                    MyApplication.getAppContext().getString(SSL_KEY_PASSWORD_STRING_ID).toCharArray());
        } finally {
            certificateInputStream.close();
        }
        final TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
        tmf.init(trustStore);
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        //Retrofit 2.0.x
        final TrustManager[] trustManagers = tmf.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
        }
        final X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        final OkHttpClient.Builder client3 = new OkHttpClient.Builder().sslSocketFactory(sslSocketFactory, trustManager);
        client3.addInterceptor(chain -> {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header(AppConstants.DOMAIN, FuguUtils.Companion.getDomain())
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        });
        if(BuildConfig.DEBUG) client3.addInterceptor(getLoggingInterceptor());
        return client3;
    }
}
