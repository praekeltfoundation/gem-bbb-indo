package org.gem.indo.dooit.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import org.gem.indo.dooit.models.bot.Answer;
import org.gem.indo.dooit.models.bot.BaseBotModel;
import org.gem.indo.dooit.models.enums.BotType;
import org.gem.indo.dooit.views.helpers.activity.CurrencyHelper;
import org.gem.indo.dooit.views.main.fragments.bot.adapters.BotAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by herman on 2016/11/12.
 */

public class Utils {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public static float dpTopx(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static float spTopx(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkExternalStoragePermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static int weekDiff(long time, ROUNDWEEK round) {
        switch (round) {
            case UP:
                return (int) Math.ceil(TimeUnit.MILLISECONDS.toDays(time - System.currentTimeMillis()) / 7.0);
            case DOWN:
            default:
                return (int) Math.floor(TimeUnit.MILLISECONDS.toDays(time - System.currentTimeMillis()) / 7.0);

        }
    }

    public static int dayDiff(long time) {
        return (int) Math.ceil(TimeUnit.MILLISECONDS.toDays(time - System.currentTimeMillis()));
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
    }

    public static String formatDateToLocal(Date date) {
        Locale locale = Locale.getDefault();
        return DateFormat.getDateInstance(DateFormat.SHORT, locale).format(date);
    }

    public static String populateFromPersisted(Persisted persisted, BotAdapter botAdapter, String text, String[] params) {
        if (!text.contains("%"))
            return text;

        for (int i = 0; i < params.length; i++) {
            String param = params[i];

            switch (param) {
                case "USER_USERNAME":
                    params[i] = persisted.getCurrentUser().getUsername();
                    break;
                case "FIRSTNAME":
                    params[i] = persisted.getCurrentUser().getFirstName();
                    break;
                case "LASTNAME":
                    params[i] = persisted.getCurrentUser().getLastName();
                    break;
                case "LOCAL_CURRENCY":
                    params[i] = CurrencyHelper.getCurrencySymbol();
                    break;
                case "GOAL_DEPOSIT_GOAL_NAME":
                    params[i] = persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getName();
                    break;
                case "GOAL_DEPOSIT_TARGET":
                    params[i] = String.format(Locale.getDefault(), "%s %s", CurrencyHelper.getCurrencySymbol(), (int) persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getTarget());
                    break;
                case "GOAL_DEPOSIT_WEEKLY_TARGET":
                    params[i] = String.format(Locale.getDefault(), "%s %s", CurrencyHelper.getCurrencySymbol(), (int) Math.ceil(persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getWeeklyTarget()));
                    break;
                case "GOAL_DEPOSIT_END_DATE":
                    params[i] = Utils.formatDate(persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getEndDate().toDate());
                    break;
                case "GOAL_DEPOSIT_WEEKS_LEFT":
                    params[i] = String.valueOf(Utils.weekDiff(persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getEndDate().toDate().getTime(), Utils.ROUNDWEEK.DOWN));
                    break;
                case "GOAL_DEPOSIT_DAYS_LEFT":
                    int days = Utils.dayDiff(persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getEndDate().toDate().getTime());
                    params[i] = String.valueOf(days - (Utils.weekDiff(persisted.loadConvoGoal(BotType.GOAL_DEPOSIT).getEndDate().toDate().getTime(), Utils.ROUNDWEEK.DOWN) * 7));
                    break;
                case "TIP_INTRO":
                    params[i] = persisted.loadConvoTip().getIntro();
                    break;
                default:
                    for (BaseBotModel baseBotModel : botAdapter.getDataSet()) {
                        if (baseBotModel.getClassType().equals(Answer.class.toString())
                                && param.equals(baseBotModel.getName())) {
                            params[i] = ((Answer) baseBotModel).getValue();
                        }
                    }
            }
        }
        return String.format(text, (Object[]) params);
    }

    public enum ROUNDWEEK {
        UP,
        DOWN
    }
}
