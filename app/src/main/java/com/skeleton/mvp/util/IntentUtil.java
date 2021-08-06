package com.skeleton.mvp.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.provider.Telephony;
import android.text.TextUtils;

import java.io.File;
import java.net.URL;
import java.util.List;
/**
 * @author Dmitriy Tarasov
 */
public final class IntentUtil {
    /**
     * Empty Constructor
     * not called
     */
    private IntentUtil() {
    }
    /**
     * Open app page at Google Play. If Play Store application isn't available on the device
     * then web browser will be opened
     *
     * @param context Application context
     * @return intent
     */
    public static Intent openPlayStore(final Context context) {
        return openPlayStore(context, true);
    }
    /**
     * Open app page at Google Play
     *
     * @param context       Application context
     * @param openInBrowser Should we try to open application page in web browser
     *                      if Play Store app not found on device
     * @return intent
     */
    public static Intent openPlayStore(final Context context, final boolean openInBrowser) {
        String appPackageName = context.getPackageName();
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
        if (isIntentAvailable(context, marketIntent)) {
            return marketIntent;
        }
        if (openInBrowser) {
            return openLink("https://play.google.com/store/apps/details?id=" + appPackageName);
        }
        return marketIntent;
    }
    /**
     * Send email message
     *
     * @param to      Receiver email
     * @param subject Message subject
     * @param text    Message body
     * @return intent
     * @see #sendEmail(String[], String, String)
     */
    public static Intent sendEmail(final String to, final String subject, final String text) {
        return sendEmail(new String[]{to}, subject, text);
    }
    /**
     * Send email message
     *
     * @param to      Receiver email
     * @param subject Message subject
     * @param text    Message body
     * @return intent
     * @see #sendEmail(String[], String, String)
     */
    public static Intent sendEmail(final String[] to, final String subject, final String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        return intent;
    }
    /**
     * Share text via thirdparty app like twitter, facebook, email, sms etc.
     *
     * @param subject Optional subject of the message
     * @param text    Text to share
     * @return intent
     */
    public static Intent shareText(final String subject, final String text) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        if (!TextUtils.isEmpty(subject)) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setType("text/plain");
        return intent;
    }
    /**
     * Send SMS message using built-in app
     *
     * @param context Application context
     * @param to      Receiver phone number
     * @param message Text to send
     * @return intent
     */
    public static Intent sendSms(final Context context, final String to, final String message) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + to));
            intent.putExtra("sms_body", message);
            if (defaultSmsPackageName != null) {
                intent.setPackage(defaultSmsPackageName);
            }
            return intent;
        } else {
            Uri smsUri = Uri.parse("tel:" + to);
            Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
            intent.putExtra("address", to);
            intent.putExtra("sms_body", message);
            intent.setType("vnd.android-dir/mms-sms");
            return intent;
        }
    }
    /**
     * Opens the Street View application to the given location.
     * The URI scheme is based on the syntax used for Street View panorama information in Google Maps URLs.
     *
     * @param latitude  Latitude
     * @param longitude Longitude
     * @param yaw       Panorama center-of-view in degrees clockwise from North.
     *                  <p/>
     *                  Note: The two commas after the yaw parameter are required.
     *                  They are present for backwards-compatibility reasons.
     * @param pitch     Panorama center-of-view in degrees from -90 (look straight up) to 90 (look straight down.)
     * @param zoom      Panorama zoom. 1.0 = normal zoom, 2.0 = zoomed in 2x, 3.0 = zoomed in 4x, and so on.
     *                  A zoom of 1.0 is 90 degree horizontal FOV for a nominal landscape mode 4 x 3 aspect ratio display Android
     *                  phones in portrait mode will adjust the zoom so that the vertical FOV is approximately the same as the
     *                  landscape vertical FOV. This means that the horizontal FOV of an Android phone in portrait mode is much
     *                  narrower than in landscape mode. This is done to minimize the fisheye lens effect that would be present
     *                  if a 90 degree horizontal FOV was used in portrait mode.
     * @param mapZoom   The map zoom of the map location associated with this panorama.
     *                  This value is passed on to the Maps activity when the Street View "Go to Maps" menu item is chosen.
     *                  It corresponds to the zoomLevel parameter in {@link #showLocation(float, float, Integer)}
     * @return intent
     */
    public static Intent showStreetView(final float latitude,
                                        final float longitude,
                                        final Float yaw,
                                        final Integer pitch,
                                        final Float zoom,
                                        final Integer mapZoom) {
        StringBuilder builder = new StringBuilder("google.streetview:cbll=").append(latitude).append(",").append(longitude);
        if (yaw != null || pitch != null || zoom != null) {
            String cbpParam = "";
            if (yaw != null) {
                cbpParam = yaw + ",";
            }
            if (pitch != null) {
                cbpParam = cbpParam + pitch + ",";
            }
            if (zoom != null) {
                cbpParam = cbpParam + zoom;
            }
            builder.append("&cbp=1,").append(cbpParam);
        }
        if (mapZoom != null) {
            builder.append("&mz=").append(mapZoom);
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(builder.toString()));
        return intent;
    }
    /**
     * Opens the Maps application to the given location.
     *
     * @param latitude  Latitude
     * @param longitude Longitude
     * @param zoomLevel A zoom level of 1 shows the whole Earth, centered at the given lat,lng.
     *                  A zoom level of 2 shows a quarter of the Earth, and so on. The highest zoom level is 23.
     *                  A larger zoom level will be clamped to 23.
     * @return intent
     */
    public static Intent showLocation(final float latitude, final float longitude, final Integer zoomLevel) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String data = "geo:" + latitude + "," + longitude;
        if (zoomLevel != null) {
            data = data + "?z=" + zoomLevel;
        }
        intent.setData(Uri.parse(data));
        return intent;
    }
    /**
     * Open system settings location services screen for turning on/off GPS
     *
     * @return intent
     */
    public static Intent showLocationServices() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        return intent;
    }
    /**
     * Open a browser window to the URL specified.
     *
     * @param url Target url
     * @return intent
     */
    public static Intent openLink(final String url) {
        String mUrl = url;
        // if protocol isn't defined use http by default
        if (!TextUtils.isEmpty(url) && !url.contains("://")) {
            mUrl = "http://" + url;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mUrl));
        return intent;
    }
    /**
     * @param url Url of file to open
     * @return intent
     * @see #openLink(String)
     */
    public static Intent openLink(final URL url) {
        return openLink(url.toString());
    }
    /**
     * Open a video file in appropriate app
     *
     * @param file File to open
     * @return intent
     */
    public static Intent openVideo(final File file) {
        return openVideo(Uri.fromFile(file));
    }
    /**
     * @param file File path to open
     * @return intent
     * @see #openVideo(File)
     */
    public static Intent openVideo(final String file) {
        return openVideo(new File(file));
    }
    /**
     * @param uri Uri of file to open
     * @return intent
     * @see #openVideo(File)
     */
    public static Intent openVideo(final Uri uri) {
        return openMedia(uri, "video/*");
    }
    /**
     * Open an audio file in appropriate app
     *
     * @param file File to open
     * @return intent
     */
    public static Intent openAudio(final File file) {
        return openAudio(Uri.fromFile(file));
    }
    /**
     * @param file File to open
     * @return intent
     * @see #openAudio(File)
     */
    public static Intent openAudio(final String file) {
        return openAudio(new File(file));
    }
    /**
     * @param uri Uri of file to open
     * @return intent
     * @see #openAudio(File)
     */
    public static Intent openAudio(final Uri uri) {
        return openMedia(uri, "audio/*");
    }
    /**
     * Open an image file in appropriate app
     *
     * @param file File to open
     * @return intent
     */
    public static Intent openImage(final String file) {
        return openImage(new File(file));
    }
    /**
     * @param file File to open
     * @return intent
     * @see #openImage(String)
     */
    public static Intent openImage(final File file) {
        return openImage(Uri.fromFile(file));
    }
    /**
     * @param uri Uri of image to open
     * @return intent
     * @see #openImage(String)
     */
    public static Intent openImage(final Uri uri) {
        return openMedia(uri, "image/*");
    }
    /**
     * Open a text file in appropriate app
     *
     * @param file File to open
     * @return intent
     */
    public static Intent openText(final String file) {
        return openText(new File(file));
    }
    /**
     * @param file File to open
     * @return intent
     * @see #openText(String)
     */
    public static Intent openText(final File file) {
        return openText(Uri.fromFile(file));
    }
    /**
     * @param uri Uri of text file
     * @return intent
     * @see #openText(String)
     */
    public static Intent openText(final Uri uri) {
        return openMedia(uri, "text/plain");
    }
    /**
     * Pick file from sdcard with file manager. Chosen file can be obtained from Intent in onActivityResult.
     * See code below for example:
     * <p/>
     * <pre><code>
     *     @Override
     *     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     *         Uri file = data.getData();
     *     }
     * </code></pre>
     *
     * @return intent
     */
    public static Intent pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        return intent;
    }
    /**
     * Calls the entered phone number. Valid telephone numbers as defined in the IETF RFC 3966 are accepted.
     * Valid examples include the following:
     * tel:2125551212
     * tel: (212) 555 1212
     * <p/>
     * Note: This requires your application to request the following permission in your manifest:
     * <code>&lt;uses-permission android:name="android.permission.CALL_PHONE"/&gt;</code>
     *
     * @param phoneNumber Phone number
     * @return intent
     */
    public static Intent callPhone(final String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        return intent;
    }
    /**
     * Pick contact from phone book
     *
     * @return intent
     */
    public static Intent pickContact() {
        return pickContact(null);
    }
    /**
     * Pick contact from phone book
     *
     * @param scope You can restrict selection by passing required content type. Examples:
     *              <p>
     *              // Select only from users with emails
     *              IntentUtil.pickContact(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
     *              <p>
     *              // Select only from users with phone numbers on pre Eclair devices
     *              IntentUtil.pickContact(Contacts.Phones.CONTENT_TYPE);
     *              <p>
     *              // Select only from users with phone numbers on devices with Eclair and higher
     *              IntentUtil.pickContact(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
     * @return intent
     */
    public static Intent pickContact(final String scope) {
        Intent intent;
        if (isSupportsContactsV2()) {
            intent = new Intent(Intent.ACTION_PICK, Uri.parse("content://com.android.contacts/contacts"));
        } else {
            intent = new Intent(Intent.ACTION_PICK, Contacts.People.CONTENT_URI);
        }
        if (!TextUtils.isEmpty(scope)) {
            intent.setType(scope);
        }
        return intent;
    }
    /**
     * Pick contact only from contacts with telephone numbers
     *
     * @return intent
     */
    public static Intent pickContactWithPhone() {
        Intent intent;
        if (isSupportsContactsV2()) {
            intent = pickContact(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        } else {
            // pre Eclair, use old contacts API
            intent = pickContact(Contacts.Phones.CONTENT_TYPE);
        }
        return intent;
    }
    /**
     * Pick image from gallery
     *
     * @return intent
     */
    public static Intent pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        return intent;
    }
    /**
     * Dials (but does not actually initiate the call) the number given.
     * Telephone number normalization described for {@link #callPhone(String)} applies to dial as well.
     *
     * @param phoneNumber Phone number
     * @return intent
     */
    public static Intent dialPhone(final String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        return intent;
    }
    /**
     * Check that cropping application is available
     *
     * @param context Application context
     * @return true if cropping app is available
     * @see #cropImage(Context, File, int, int, int, int, boolean)
     */
    public static boolean isCropAvailable(final Context context) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        return IntentUtil.isIntentAvailable(context, intent);
    }
    /**
     * Crop image. Before using, cropImage requires especial check that differs from
     * {@link #isIntentAvailable(Context, Intent)}
     * see {@link #isCropAvailable(Context)} instead
     *
     * @param context Application context
     * @param image   Image that will be used for cropping. This image is not changed during the cropImage
     * @param outputX Output image width
     * @param outputY Output image height
     * @param aspectX Crop frame aspect X
     * @param aspectY Crop frame aspect Y
     * @param scale   Scale or not cropped image if output image and cropImage frame sizes differs
     * @return Intent with <code>data</code>-extra in <code>onActivityResult</code> which contains result as a
     * {@link android.graphics.Bitmap}. See demo app for details
     */
    public static Intent cropImage(final Context context,
                                   final File image,
                                   final int outputX,
                                   final int outputY,
                                   final int aspectX,
                                   final int aspectY,
                                   final boolean scale) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        ResolveInfo res = list.get(0);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("scale", scale);
        intent.putExtra("return-data", true);
        intent.setData(Uri.fromFile(image));
        intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        return intent;
    }
    /**
     * Check that in the system exists application which can handle this intent
     *
     * @param context Application context
     * @param intent  Checked intent
     * @return true if intent consumer exists, false otherwise
     */
    public static boolean isIntentAvailable(final Context context, final Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
    /**
     * @param uri      Media file uri
     * @param mimeType MimeType of media file
     * @return intent
     */
    private static Intent openMedia(final Uri uri, final String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);
        return intent;
    }
    /**
     * @return boolean
     */
    private static boolean isSupportsContactsV2() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR;
    }
}