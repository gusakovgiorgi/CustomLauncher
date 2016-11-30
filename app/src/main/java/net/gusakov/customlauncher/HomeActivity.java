package net.gusakov.customlauncher;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.app.SharedElementCallback;
import android.app.TaskStackBuilder;
import android.app.VoiceInteractor;
import android.app.assist.AssistContent;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Display;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends Activity implements View.OnClickListener{

    private static final String TAG = "myTag";
    private static final int MY_REQUEST_CODE = 1;
    private static final String POSITION_SER_FILE ="position.ser" ;
    private com.rey.material.widget.Switch switchBtn;
    private PackageManager manager;
    private Map<String, AppDetail> apps;
    private SharedPreferences sharedPref;
    private static final String FIRST_TIME_SHARED = "firstTIme";
    private static final String SHARED_PREF_STRING_DEFAULT_VALUE = "no package";
    private static final int VISIBLE_APPS_IN_LAUNCHER_QUANTITY = 6;
    String[] appsPosotion = new String[VISIBLE_APPS_IN_LAUNCHER_QUANTITY];
    private static final int HASH_MAP_SIZE = 15;
    private DragAndDrop dragAndDrop;
    private ImageView firstImageView;
    private LinearLayout firstLinearLayout;
    private ImageView secondImageView;
    private LinearLayout secondLinearLayout;
    private ImageView thirdImageView;
    private LinearLayout thirdLinearLayout;
    private ImageView fourthdImageView;
    private LinearLayout fourthLinearLayout;
    private ImageView fifthImageView;
    private LinearLayout fifthLinearLayout;
    private ImageView sixthImageView;
    private LinearLayout sixthLinearLayout;
    private LinearLayout removeLinearLayout;
    private boolean saved=false;
    private boolean allowPauseActivity=false;


    private final List<String> certifedApp = new ArrayList<>(Arrays.asList("com.android.dialer", "com.android.contacts", "com.android.mms",
            "com.android.calendar","com.android.settings", "com.android.music", "com.android.calculator2", "com.android.deskclock", "com.google.android.music"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.v(TAG, "activityCreated");

        sharedPref = getPreferences(MODE_PRIVATE);
        boolean firtsTime = true;//sharedPref.getBoolean(FIRST_TIME_SHARED, true);
        if (firtsTime) {
            LoadDefaultAppsPosition(appsPosotion);

            SharedPreferences.Editor ed = sharedPref.edit();
            ed.putBoolean(FIRST_TIME_SHARED, false);
            ed.commit();
        }else{
            appsPosotion=loadAppsPositions();
        }
//        ((CustomDigitalClock) findViewById(R.id.digitalClockId)).setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf"));
        initialLComponents();
        initilTies();
        Intent si=new Intent(this,MyService.class);
        si.putExtra("id",getTaskId());
//        startService(si);
        Log.v(TAG,"taskId="+getTaskId());

    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i(TAG,"key code="+event.getKeyCode());
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG,"key code="+keyCode);
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void LoadDefaultAppsPosition(String[] list) {
        if (isPackageExisted(certifedApp.get(0), ".DialtactsActivity")) {
            list[0]=(certifedApp.get(0) + ".DialtactsActivity");
        }
        if (isPackageExisted(certifedApp.get(0), ".LaunchContactsActivity")) {
            list[1]=(certifedApp.get(0) + ".LaunchContactsActivity");
            certifedApp.remove(1);
//            list[1]=(certifedApp.get(1));
//            list[2]=(certifedApp.get(2));
//            list[3]=(certifedApp.get(3));
        }
            list[1]=(certifedApp.get(1));
            list[2]=(certifedApp.get(2));
            list[3]=(certifedApp.get(3));

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
    }

    @Override
    protected void onPause() {

        Log.v(TAG, "onPause()");
        if(!allowPauseActivity) {
//            ActivityManager activityManager = (ActivityManager) getApplicationContext()
//                    .getSystemService(Context.ACTIVITY_SERVICE);
//
//            activityManager.moveTaskToFront(getTaskId(), 0);
//            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            sendBroadcast(closeDialog);
        }else {
            allowPauseActivity=false;
        }
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");
        saveAppsPositions(appsPosotion);
        Log.v(TAG, "saved");
    }

    @Override
    public void onDetachedFromWindow() {
        Log.v(TAG, "onDetach()");
        super.onDetachedFromWindow();

    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy()");
        super.onDestroy();


    }
    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        return false;
    }

    private void initilTies() {
        getCertifiedAppList();

        putAppsOnHome();
    }

    private void putAppsOnHome() {
        LinearLayout firstLinearLayout = (LinearLayout) findViewById(R.id.appFirstLineId);
        LinearLayout secondLinearLayout = (LinearLayout) findViewById(R.id.appSecondLineId);

        for (int i = 0; i < VISIBLE_APPS_IN_LAUNCHER_QUANTITY; i++) {
            if (appsPosotion[i]!=null) {
                String packageStr = appsPosotion[i];
                if (apps.containsKey(packageStr)) {
                    if(i<VISIBLE_APPS_IN_LAUNCHER_QUANTITY/2) {
                        ImageView img = (ImageView) ((LinearLayout) firstLinearLayout.getChildAt(i % 3)).getChildAt(0);
                        img.setTag(apps.get(packageStr).name);
                        TextView tv = (TextView) ((LinearLayout) firstLinearLayout.getChildAt(i % 3)).getChildAt(1);
                        img.setImageDrawable(apps.get(packageStr).icon);
                        tv.setText(apps.get(packageStr).label);
                    }else{
                        ImageView img = (ImageView) ((LinearLayout) secondLinearLayout.getChildAt(i % 3)).getChildAt(0);
                        img.setTag(apps.get(packageStr).name);
                        TextView tv = (TextView) ((LinearLayout) secondLinearLayout.getChildAt(i % 3)).getChildAt(1);
                        img.setImageDrawable(apps.get(packageStr).icon);
                        tv.setText(apps.get(packageStr).label);
                    }
                } else {
                    appsPosotion[i--]=null;
                }
            }

        }
//        for (int i = 0; i < length; i++) {
//            if (i < VISIBLE_APPS_IN_LAUNCHER_QUANTITY / 2) {
//                ImageView img = (ImageView) ((LinearLayout) firstLinearLayout.getChildAt(i)).getChildAt(0);
//                TextView tv = (TextView) ((LinearLayout) firstLinearLayout.getChildAt(i)).getChildAt(1);
//                img.setImageDrawable(apps.get(i).icon);
//                tv.setText(apps.get(i).label);
//            } else {
//                ImageView img = (ImageView) ((LinearLayout) secondLinearLayout.getChildAt(i % 3)).getChildAt(0);
//                TextView tv = (TextView) ((LinearLayout) secondLinearLayout.getChildAt(i % 3)).getChildAt(1);
//                img.setImageDrawable(apps.get(i).icon);
//                tv.setText(apps.get(i).label);
//            }
//        }
    }


    private void saveAppsPositions(String[] list){
        appsPosotion[0]=(String)firstLinearLayout.getChildAt(0).getTag();
        appsPosotion[1]=(String)secondLinearLayout.getChildAt(0).getTag();
        appsPosotion[2]=(String)thirdLinearLayout.getChildAt(0).getTag();
        appsPosotion[3]=(String)fourthLinearLayout.getChildAt(0).getTag();
        appsPosotion[4]=(String)fifthLinearLayout.getChildAt(0).getTag();
        appsPosotion[5]=(String)sixthLinearLayout.getChildAt(0).getTag();
        ObjectOutputStream obOut=null;
        try {
            FileOutputStream fileOut=openFileOutput(POSITION_SER_FILE,MODE_PRIVATE);
            obOut=new ObjectOutputStream(fileOut);
            obOut.writeObject(list);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(obOut!=null){
                try {
                    obOut.flush();
                    obOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private String[] loadAppsPositions(){
        ObjectInputStream objIn = null;
        try{
            FileInputStream fileIn=openFileInput(POSITION_SER_FILE);
            objIn=new ObjectInputStream(fileIn);
            return (String[])objIn.readObject();

        }catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(objIn!=null) {
                try {
                    objIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return new String[VISIBLE_APPS_IN_LAUNCHER_QUANTITY];
    }


    private boolean isPackageExisted(String targetPackage, String activityName) {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(targetPackage);
        List<ResolveInfo> availableActivities = pm.queryIntentActivities(intent, 0);
        if (availableActivities.size() <= 0) {
            return false;
        } else {
            for (int i = 0; i < availableActivities.size(); i++) {
                if (availableActivities.get(i).activityInfo.name != null && availableActivities.get(i).activityInfo.name.equals(targetPackage + activityName)) {
                    return true;
                }
            }

        }
        return false;

    }

    public boolean isPackageExisted(String targetPackage) {
        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(targetPackage);
        if (manager.queryIntentActivities(intent, 0).size() > 0) {
            return true;
        } else {
            return false;
        }

    }

    private void getCertifiedAppList() {
        manager = getPackageManager();
        apps = new HashMap<String, AppDetail>(HASH_MAP_SIZE);

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        boolean firstTime = true;
        for (String str : certifedApp) {
            intent.setPackage(str);
            List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent, 0);
            if (availableActivities.size() > 0) {
                if (firstTime && availableActivities.size() > 0) {
                    for (int i = 0; i < availableActivities.size(); i++) {
                        if (availableActivities.get(i).activityInfo.name != null && availableActivities.get(i).activityInfo.name.equals("com.android.dialer.LaunchContactsActivity")) {
                            AppDetail app = new AppDetail();
                            app.label = availableActivities.get(i).loadLabel(manager);
                            app.name = "com.android.dialer.LaunchContactsActivity";
                            app.icon = availableActivities.get(i).activityInfo.loadIcon(manager);
                            app.additionalCassname="com.android.dialer.LaunchContactsActivity";
                            apps.put("com.android.dialer.LaunchContactsActivity", app);
                        } else if (availableActivities.get(i).activityInfo.name != null && availableActivities.get(i).activityInfo.name.equals("com.android.dialer.DialtactsActivity")) {
                            AppDetail app = new AppDetail();
                            app.label = availableActivities.get(i).loadLabel(manager);
                            app.name = "com.android.dialer.DialtactsActivity";
                            app.icon = availableActivities.get(i).activityInfo.loadIcon(manager);
                            app.additionalCassname="com.android.dialer.DialtactsActivity";
                            apps.put("com.android.dialer.DialtactsActivity", app);
                        }
                    }
                    firstTime = false;
                } else {
                    AppDetail app = new AppDetail();
                    app.label = availableActivities.get(0).loadLabel(manager);
                    app.name = availableActivities.get(0).activityInfo.packageName;
                    app.icon = availableActivities.get(0).activityInfo.applicationInfo.loadIcon(manager);
                    apps.put(str, app);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void initialLComponents() {
        switchBtn = (com.rey.material.widget.Switch) findViewById(R.id.switchId);
        switchBtn.setOnCheckedChangeListener(new com.rey.material.widget.Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(com.rey.material.widget.Switch view, boolean checked) {
                if (checked == false) {
                    startHomeDefaultChooser();
                }
            }
        });
        dragAndDrop=new DragAndDrop(this);
        firstImageView=(ImageView)findViewById(R.id.firstImageViewId);
        secondImageView=(ImageView)findViewById(R.id.secondImageViewId);
        thirdImageView=(ImageView)findViewById(R.id.thirdImageViewId);
        fourthdImageView=(ImageView)findViewById(R.id.fourthImageViewId);
        fifthImageView=(ImageView)findViewById(R.id.fifthImageViewId);
        sixthImageView=(ImageView)findViewById(R.id.sixthImageViewId);

        firstLinearLayout=(LinearLayout)findViewById(R.id.firstTileId);
        secondLinearLayout=(LinearLayout)findViewById(R.id.secondtTileId);
        thirdLinearLayout=(LinearLayout)findViewById(R.id.thirdTileId);
        fourthLinearLayout=(LinearLayout)findViewById(R.id.fourthTileId);
        fifthLinearLayout=(LinearLayout)findViewById(R.id.fifthtTileId);
        sixthLinearLayout=(LinearLayout)findViewById(R.id.sixthTileId);
        removeLinearLayout=(LinearLayout)findViewById(R.id.removeContainerId);


        firstImageView.setOnLongClickListener(dragAndDrop);
        secondImageView.setOnLongClickListener(dragAndDrop);
        thirdImageView.setOnLongClickListener(dragAndDrop);
        fourthdImageView.setOnLongClickListener(dragAndDrop);
        fifthImageView.setOnLongClickListener(dragAndDrop);
        sixthImageView.setOnLongClickListener(dragAndDrop);

        firstLinearLayout.setOnDragListener(dragAndDrop);
        secondLinearLayout.setOnDragListener(dragAndDrop);
        thirdLinearLayout.setOnDragListener(dragAndDrop);
        fourthLinearLayout.setOnDragListener(dragAndDrop);
        fifthLinearLayout.setOnDragListener(dragAndDrop);
        sixthLinearLayout.setOnDragListener(dragAndDrop);
        removeLinearLayout.setOnDragListener(dragAndDrop);

    }

    private void startHomeDefaultChooser() {
        PackageManager p = getPackageManager();
        ComponentName cN = new ComponentName(HomeActivity.this, FakeHomeActivity.class);
        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Intent selector = new Intent(Intent.ACTION_MAIN);
        selector.addCategory(Intent.CATEGORY_HOME);
//        selector.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(selector, MY_REQUEST_CODE);
        p.setComponentEnabledSetting(cN, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public void launchApp(View view) {
            String packageStr= (String) view.getTag();
        Intent intent;
            if(packageStr!=null){
                if(packageStr.contains(".DialtactsActivity") || packageStr.contains(".LaunchContactsActivity")){
                    String newPackageStr=packageStr.substring(0,packageStr.lastIndexOf('.'));
                    intent=new Intent();
                    intent.setComponent(new ComponentName(newPackageStr,packageStr));

                }else {
                    intent = manager.getLaunchIntentForPackage(packageStr);
                }
                allowPauseActivity=true;
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                HomeActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.zoom,0);


            }else{
                CustomDialog customDialog=new CustomDialog(HomeActivity.this,(ViewGroup)view.getParent(),apps,appsPosotion);
                customDialog.show();
            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            switchBtn.setChecked(true);
        }
        Log.v(TAG, "requestCode=" + requestCode + ", resultCode=" + resultCode);
    }


    @Override
    public void onClick(View v) {
        launchApp(v);
    }

}

