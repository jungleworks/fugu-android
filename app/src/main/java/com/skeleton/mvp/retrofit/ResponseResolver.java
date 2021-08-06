package com.skeleton.mvp.retrofit;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.skeleton.mvp.constant.FuguAppConstant;
import com.skeleton.mvp.utils.FuguLog;
import com.skeleton.mvp.utils.loadingBox.LoadingBox;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Custom Retrofit ResponseResolver
 *
 * @param <T>
 */
public abstract class ResponseResolver<T> implements Callback<T> {
    private WeakReference<Activity> weakActivity = null;
    private Boolean showLoading = false;
    private Boolean showError = false;

    private final String NO_INTERNET_MESSAGE = "No internet connection. Please try again later.";
    private final String REMOTE_SERVER_FAILED_MESSAGE = "Application server could not respond. Please try later.";
    public final static String UNEXPECTED_ERROR_OCCURRED = "Something went wrong. Please try again later";
    private final static String PARSING_ERROR = "Parsing error";
    private final static String UPLOAD_FAILED = "Upload Failed";

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    /**
     * @param activity
     */
    public ResponseResolver(Activity activity) {
        this.weakActivity = new WeakReference<>(activity);
    }


    /**
     * @param activity
     * @param showLoading
     */
    public ResponseResolver(Activity activity, Boolean showLoading) {
        this.weakActivity = new WeakReference<>(activity);
        this.showLoading = showLoading;
        if (showLoading)
            LoadingBox.showOn(activity);
    }


    /**
     * @param activity
     * @param showLoading
     * @param showError
     */
    public ResponseResolver(Activity activity, Boolean showLoading, Boolean showError) {
        this.weakActivity = new WeakReference<>(activity);
        this.showLoading = showLoading;
        this.showError = showError;
        if (showLoading)
            LoadingBox.showOn(activity);

    }

    public ResponseResolver() {
    }

    public abstract void success(T t);

    public abstract void failure(APIError error);

    @Override
    public void onResponse(Call<T> t, Response<T> tResponse) {
        JSONObject jObjError = null;
        int type = 0;
        try {
            if (tResponse.errorBody() != null) {
                jObjError = new JSONObject(tResponse.errorBody().string());
                try {
                    type = Integer.parseInt(jObjError.getString("type"));
                } catch (Exception e) {

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        LoadingBox.hide();
        if (tResponse.isSuccessful())
            success(tResponse.body());
        else
            fireError(ErrorUtils.parseError(tResponse, type, jObjError));
    }

    @Override
    public void onFailure(Call<T> t, Throwable throwable) {
        LoadingBox.hide();
        fireError(new APIError(900, resolveNetworkError(throwable), 0, new JSONObject()));
    }


    /**
     * @param apiError
     */
    public void fireError(APIError apiError) {


        if (showError) {
            if (weakActivity.get() != null) {
                if (checkAuthorizationError(apiError)) {
                    return;
                }
            }

        }

        failure(apiError);
    }

    /**
     * @param apiError
     * @return
     */
    public Boolean checkAuthorizationError(APIError apiError) {

        if (apiError.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
            /**
             * if authorization error than clear paper db and go back to splash screen on ok press
             * else show the error message
             */
            Toast.makeText(weakActivity.get(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
            weakActivity.get().finish();

            return false;
        } else {
            if (showError) {

                Toast.makeText(weakActivity.get(), apiError.getMessage(), Toast.LENGTH_SHORT).show();
                weakActivity.get().finish();
            }
        }

        return true;
    }

    /**
     * Method resolve network errors
     *
     * @param cause
     * @return
     */

    private String resolveNetworkError(Throwable cause) {
        FuguLog.e("resolveNetworkError =", String.valueOf(cause.toString()));

        if (cause instanceof UnknownHostException)
            return NO_INTERNET_MESSAGE;
        else if (cause instanceof SocketTimeoutException)
            return NO_INTERNET_MESSAGE;
        else if (cause instanceof ConnectException)
            return NO_INTERNET_MESSAGE;
        else if (cause instanceof JsonSyntaxException)
            return PARSING_ERROR;
        else if (cause instanceof SSLException)
            return UPLOAD_FAILED;
        return UNEXPECTED_ERROR_OCCURRED;
    }

}
