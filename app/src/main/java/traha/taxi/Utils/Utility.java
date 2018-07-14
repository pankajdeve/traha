package traha.taxi.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import traha.taxi.BuildConfig;
import traha.taxi.R;

import static android.os.Build.VERSION_CODES.M;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

/**
 * Created by Vijendra on 11/4/17.
 */

/*Common class used throughout the clas*/
public class Utility {

    public static final int SETTING_REQUEST_CODE = 102;
    public static boolean isGpsProviderEnabled, isNetworkProviderEnabled;
    private static ProgressDialog dialog;
    private static String PREFERENCES = "Fyndario";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static String photoUrl = "";
    private static File storeImagePath;

    /*----------------------TWITTER----------------------------------------*/
    public static final String PREF_NAME = "sample_twitter_pref";
    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    public static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    public static final String PREF_USER_NAME = "twitter_user_name";
    public static final String PREF_USER_PICTURE = "twitter_user_picture";
    public static final String PREF_USER_SCREEN_NAME = "twitter_user_screen_name";
    private static boolean checkAppBackground = false;

    public static int timeZone() {
        TimeZone tz = TimeZone.getDefault();
        Date now = new Date();
        int offsetFromUtc = tz.getOffset(now.getTime()) / 1000;
        return offsetFromUtc;
    }


    public static void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public static void setHeightWidth(View view, int width, int height) {

        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;
        view.requestLayout();
    }


    public static String toCamelCase(String inputString) {
        String result = "";
        if (inputString.length() == 0) {
            return result;
        }
        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);
        result = result + firstCharToUpperCase;
        for (int i = 1; i < inputString.length(); i++) {
            char currentChar = inputString.charAt(i);
            char previousChar = inputString.charAt(i - 1);
            if (previousChar == ' ') {
                char currentCharToUpperCase = Character.toUpperCase(currentChar);
                result = result + currentCharToUpperCase;
            } else {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
        }
        return result;
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }
        dist = Double.parseDouble(new DecimalFormat("##.#").format(dist));
        return (dist);
    }

    public static void showAlert(Context context, String title, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    // check is email id is valid format someone@website.domain
    public static boolean isValidEmail(String emailAddress) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }


    public static void setStringPreferences(Context context, String key,
                                            String value) {
        SharedPreferences setting = (SharedPreferences) context
                .getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putString(key, value);
        editor.commit();

    }

    public static void setBooleanPreferences(Context context, String key,
                                             Boolean value) {
        SharedPreferences setting = (SharedPreferences) context
                .getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static Boolean getBooleaPreferences(Context context, String key) {
        SharedPreferences setting = (SharedPreferences) context
                .getSharedPreferences(PREFERENCES, 0);
        return setting.getBoolean(key, false);
    }

    public static String getStringPreferences(Context context, String key) {

        SharedPreferences setting = (SharedPreferences) context
                .getSharedPreferences(PREFERENCES, 0);
        return setting.getString(key, "");

    }

    public static void setIntegerPreferences(Context context, String key,
                                             int value) {
        SharedPreferences setting = (SharedPreferences) context
                .getSharedPreferences(PREFERENCES, 0);
        SharedPreferences.Editor editor = setting.edit();
        editor.putInt(key, value);
        editor.commit();

    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    public static String GetCountryZipCode(Context mContext) {
        String CountryID = "";
        String CountryZipCode = "";
        TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = mContext.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }

    public static void clearAllSharedPreferences(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(
                PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

    public static DrawableRequestBuilder<String> loadAllPost(@NonNull String posterPath, Context context) {
        return Glide
                .with(context)
                .load(posterPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image for ALL
                .dontAnimate()
                .dontTransform()
                .fitCenter();
    }

    public static DrawableRequestBuilder<String> loadBlackImage(@NonNull String posterPath, Context context) {
        return Glide
                .with(context)
                .load(posterPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image for ALL
                .crossFade();
    }

    public static void showProgressHUD(Context context, String title, String message) {
        try {
            if (title == null)
                title = "";
            if (message == null)
                message = "";
            dialog = ProgressDialog.show(context, title, message);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideProgressHud() {
        try {
            if (dialog != null)
                dialog.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void showSoftKeyboard(Context mContext, View v) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static boolean isConnectingToInternet(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Error Connecting");
        alertDialog.setMessage("No Internet Connection Found..\nPlease Connect to Internet !");
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        return false;
    }

    public static String getFormatedDate(String strDate, String sourceFormate,
                                         String destinyFormate) {
        SimpleDateFormat df;
        df = new SimpleDateFormat(sourceFormate);
        Date date = null;
        try {
            date = df.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        df = new SimpleDateFormat(destinyFormate);
        return df.format(date);
    }

    private static String getDate(String OurDate) {
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = df.parse(OurDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
            OurDate = dateFormatter.format(value);
        } catch (Exception e) {
            e.printStackTrace();
            OurDate = "00-00-0000 00:00";
        }
        return OurDate;
    }


    public static String getTimeAgo(String timeString) {
        timeString = getDate(timeString);
        String datetime[] = timeString.split("T");
        String date[];
        String time[];
        String am_pm;
        Calendar rightNow = Calendar.getInstance();
        if (datetime.length == 2) {
            date = datetime[0].split("-");
            time = datetime[1].split(":");
            int year = Integer.parseInt(date[0]);
            if (rightNow.get(Calendar.YEAR) > year)
                if (rightNow.get(Calendar.YEAR) - year == 1) {
                    return (rightNow.get(Calendar.YEAR) - year + " year ago");
                } else {
                    return (rightNow.get(Calendar.YEAR) - year + " years ago");
                }
            int month = Integer.parseInt(date[1]);
            if (rightNow.get(Calendar.MONTH) + 1 > month)
                if (rightNow.get(Calendar.MONTH) - month == 1) {
                    return (rightNow.get(Calendar.MONTH) + 1 - month + " month ago");
                } else {
                    return (rightNow.get(Calendar.MONTH) + 1 - month + " months ago");
                }
            int day = Integer.parseInt(date[2]);
            if (rightNow.get(Calendar.DAY_OF_MONTH) > day)
                if (rightNow.get(Calendar.DAY_OF_MONTH) - day == 1) {
                    return (rightNow.get(Calendar.DAY_OF_MONTH) - day + " day ago");
                } else {
                    return (rightNow.get(Calendar.DAY_OF_MONTH) - day + " days ago");
                }
            int hours = Integer.parseInt(time[0]);
            if (rightNow.get(Calendar.HOUR_OF_DAY) > hours)
                if (rightNow.get(Calendar.HOUR_OF_DAY) - hours == 1) {
                    return (rightNow.get(Calendar.HOUR_OF_DAY) - hours + " hour ago");
                } else {
                    return (rightNow.get(Calendar.HOUR_OF_DAY) - hours + " hours ago");
                }
            int minuts = Integer.parseInt(time[1]);
            if (rightNow.get(Calendar.MINUTE) > minuts)
                return (rightNow.get(Calendar.MINUTE) - minuts + " minute ago");
            int second = Integer.parseInt(time[2]);
            if (rightNow.get(Calendar.SECOND) > second)
                return (rightNow.get(Calendar.SECOND) - second + " second ago");
        } else {
            System.out.println("got null");
            return null;
        }
        return null;
    }


    public static String getDifferenceBtwTime(String dateTime) {
        String dtStart = dateTime;
        Date createdDate;

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            createdDate = format.parse(dtStart);
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = new Date();
            SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date dateTime1 = null;
            try {
                dateTime1 = dateParser.parse(format.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long timeDifferenceMilliseconds = dateTime1.getTime()
                    - createdDate.getTime();
            long diffSeconds = timeDifferenceMilliseconds / 1000;
            long diffMinutes = timeDifferenceMilliseconds / (60 * 1000);
            long diffHours = timeDifferenceMilliseconds / (60 * 60 * 1000);
            long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);
            long diffWeeks = timeDifferenceMilliseconds
                    / (60 * 60 * 1000 * 24 * 7);
            long diffMonths = (long) (timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 30.41666666));
            long diffYears = timeDifferenceMilliseconds / (1000 * 60 * 60 * 24 * 365);

            if (diffSeconds < 1) {
                return "few sec ago";
            } else if (diffMinutes < 1) {
                if (diffSeconds == 1) {
                    return diffSeconds + " second ago";
                } else {
                    return diffSeconds + " seconds ago";
                }
            } else if (diffHours < 1) {
                if (diffMinutes == 1) {
                    return diffMinutes + " minute ago";
                } else {
                    return diffMinutes + " minutes ago";
                }
            } else if (diffDays < 1) {
                if (diffHours == 1) {
                    return diffHours + " hour ago";
                } else {
                    return diffHours + " hours ago";
                }
            } else if (diffWeeks < 1) {
                if (diffDays == 1) {
                    return diffDays + " day ago";
                } else {
                    return diffDays + " days ago";
                }
            } else if (diffMonths < 1) {
                if (diffWeeks == 1) {
                    return diffWeeks + " week ago";
                } else {
                    return diffWeeks + " weeks ago";
                }
            } else if (diffYears < 12) {
                if (diffMonths == 1) {
                    return diffMonths + " month ago";
                } else {
                    return diffMonths + " months ago";
                }
            } else {
                if (diffYears == 1) {
                    return diffYears + " year ago";
                } else {
                    return diffYears + " years ago";
                }

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static String compressImage(String filePath) {
        try {
            Bitmap scaledBitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;
            float maxHeight = 1920.0f;
            float maxWidth = 1080.0f;
            float imgRatio = actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];
            try {
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }
            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth,
                        actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                exception.printStackTrace();
            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);
                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 0);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                        true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileOutputStream out = null;
            String filename = getFilename();
            try {
                out = new FileOutputStream(filename);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return filename;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "/Fyndario/Media/sent");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + "IMG_AN_" + System.currentTimeMillis() + ".jpg");
        return uriSting;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float)
                    reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    public static Bitmap getBitmapFromURL(String src) throws IOException {
        URL url = new URL(src);
        return BitmapFactory.decodeStream(url.openConnection().getInputStream());
    }

    private static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());
    }

    public static int timeDiffrence(String time1) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = simpleDateFormat.parse(getCurrentTime());
            date2 = simpleDateFormat.parse(time1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long difference = date2.getTime() - date1.getTime();
        int days = (int) (difference / (1000 * 60 * 60 * 24));
        int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
        int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
        hours = (hours < 0 ? -hours : hours);
        if (days == 0 && hours == 0 && min <= 30) {
            return min;
        } else {
            return 31;
        }
    }


    private static final int PERMISSION_CALLBACK_CONSTANT = 100;

    public static boolean ChackPermissionForStorage(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CALLBACK_CONSTANT);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static String[] permissionsRequired = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    public static boolean ChackPermissionForStorageAndCamera(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static String[] permissionsForVideoRequired = new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static boolean checkPermissionForVideoRecord(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= M) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, permissionsForVideoRequired, PERMISSION_CALLBACK_CONSTANT);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static final String SHARED_PROVIDER_AUTHORITY = "com.fyndario.fyndarioapp" + ".provider";

    public static Uri getOutputMediaFileUri(int mediaTypeImage, Context mContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(mContext, SHARED_PROVIDER_AUTHORITY, createFile());
        } else {
            return Uri.fromFile(getOutputMediaFile(mediaTypeImage));
        }
    }


    @NonNull
    public static File createFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + "/Fyndario/media/sent");
        mediaStorageDir.mkdirs();

        File sharedFile = null;
        try {
            sharedFile = File.createTempFile("IMG_AN_", ".jpg", mediaStorageDir);
            sharedFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sharedFile;
    }


    public static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + File.separator + "/Fyndario/Media/sent");
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(Environment.getExternalStorageDirectory() + File.separator + "/Fyndario/Media/sent/" + "IMG_AN_" + System.currentTimeMillis() + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }


    /*----------------------------------------------------------------------------------------------*/
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getUriPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static File saveImage(Bitmap finalBitmap) {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "/Fyndario/Media/sent");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String path = "IMG_AN_" + System.currentTimeMillis() + ".jpg";

        storeImagePath = new File(dir, path);
        if (storeImagePath.exists())
            storeImagePath.delete();
        try {
            if (finalBitmap != null) {
                FileOutputStream out = new FileOutputStream(storeImagePath);
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storeImagePath;
    }

    public static long getVideoLength(String mVideoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(mVideoPath);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInmillisec = Long.parseLong(time);
        long duration = timeInmillisec / 1000;
        long hours = duration / 3600;
        long minutes = (duration - hours * 3600) / 60;
        long seconds = duration - (hours * 3600 + minutes * 60);
        return seconds;
    }

    public static int getMediaDuration(Uri uriOfFile, Context mContext) {
        try {
            MediaPlayer mp = MediaPlayer.create(mContext, uriOfFile);
            int duration = mp.getDuration();
            return duration;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void showSoftKeyboardWithoutView(Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private static String[] projection = new String[]{
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.Images.ImageColumns.MIME_TYPE
    };

    public static void getLastImageForGalleryIcon(ImageView imageView, Context mContext) {
        try {
            final Cursor cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                    null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
            if (cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                File file = new File(path);
                if (file.exists()) {
                    Bitmap bm = BitmapFactory.decodeFile(path);
                    imageView.setImageBitmap(bm);
                } else {
                    imageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.ic_launcher));
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static String getType(final String filename) {
        String output = "video/mp4";
        int pos = filename.lastIndexOf('.');
        if (pos != -1) {
            String ext = filename.substring(filename.lastIndexOf('.') + 1, filename.length());

            if (ext.equalsIgnoreCase("mp4"))
                output = "video/mp4";
            if (ext.equalsIgnoreCase("avi"))
                output = "video/x-msvideo";
            if (ext.equalsIgnoreCase("wmv"))
                output = "video/x-ms-wmv";
            if (ext.equalsIgnoreCase("m4a"))
                output = "video/m4a";
            if (ext.equalsIgnoreCase("m4v"))
                output = "video/m4v";
            if (ext.equalsIgnoreCase("mkv"))
                output = "video/mkv";
            if (ext.equalsIgnoreCase("mov"))
                output = "video/mov";
            if (ext.equalsIgnoreCase("3gp"))
                output = "video/3gp";

            if (ext.equalsIgnoreCase("png"))
                output = "image/png";
            if (ext.equalsIgnoreCase("jpg"))
                output = "image/jpeg";
            if (ext.equalsIgnoreCase("jpe"))
                output = "image/jpeg";
            if (ext.equalsIgnoreCase("jpeg"))
                output = "image/jpeg";
            if (ext.equalsIgnoreCase("gif"))
                output = "image/gif";
        } else {
            output = " ";
        }
        return output;
    }

    public static File getLocalBitmapFile(Context mContext, Bitmap bmp) {
        Uri bmpUri = null;
        File file = null;
        try {
            file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    public static int Dp2px(Context mContext, float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 1f);
    }

    public static Uri getLocalBitmapUri(Context mContext, Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if (cursor != null) {
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                checkAppBackground = true;
            }else {
                checkAppBackground = false;
            }
        }
        return checkAppBackground;
    }

    public static String getCounts(int count) {
        String totalCount = "";
        if (count > 1000) {
            totalCount = (count / 1000) + "k";
        } else {
            totalCount = "" + count;
        }
        return totalCount;
    }

}
