package net.gusakov.customlauncher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "myTag";
    private static final int MY_REQUEST_CODE = 1;
    private static final String POSITION_SER_FILE = "position.ser";
    private com.rey.material.widget.Switch switchBtn;
    private PackageManager manager;
    private List<AppDetail> apps=apps = new ArrayList<>(HASH_MAP_SIZE);
    private SharedPreferences sharedPref;
    private static final String FIRST_TIME_SHARED = "firstTIme";
    private static final String SHARED_PREF_STRING_DEFAULT_VALUE = "no package";
    private static final int VISIBLE_APPS_IN_LAUNCHER_QUANTITY = 6;
    int[] appsPosotion;
    public static customViewGroup view;
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
    boolean firstTime;
    public static boolean serviceRunning = false;
    public static boolean statusBarHidden=false;
    private boolean homePressed = true;

    // Verify clock implementation
    String clockImpls[][] = {
            {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
            {"Standar Alarm Clock", "com.android.deskclock", "com.android.deskclock.AlarmClock"},
            {"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
            {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
            {"Samsung Galaxy Clock", "com.sec.android.app.clockpackage","com.sec.android.app.clockpackage.ClockPackage"} ,
            {"Sony Ericsson Xperia Z", "com.sonyericsson.organizer", "com.sonyericsson.organizer.Organizer_WorldClock" },
            {"ASUS Tablets", "com.asus.deskclock", "com.asus.deskclock.DeskClock"}};


    private final List<Intent> certifedApp = new ArrayList<>(Arrays.asList(new Intent(Intent.ACTION_DIAL), new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CONTACTS), new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_DEFAULT).setType("vnd.android-dir/mms-sms"),
            new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR).addCategory(Intent.CATEGORY_LAUNCHER), new Intent(android.provider.Settings.ACTION_SETTINGS), new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MUSIC), new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALCULATOR),
            new Intent(Intent.ACTION_MAIN)));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY);
        setContentView(R.layout.activity_home);
        manager=getPackageManager();
        notExpandStatusBar();
        Log.v(TAG, "activityCreated");

        initialLComponents();


        sharedPref = getPreferences(MODE_PRIVATE);
        firstTime = true;//sharedPref.getBoolean(FIRST_TIME_SHARED, true);
        if (firstTime) {
            getCertifiedAppList(firstTime);
//            LoadDefaultAppsPosition(appsPosotion);


            SharedPreferences.Editor ed = sharedPref.edit();
            ed.putBoolean(FIRST_TIME_SHARED, false);
            ed.commit();
        } else {
            getCertifiedAppList(firstTime);
            loadAppsPositions();
        }
        ((CustomDigitalClock) findViewById(R.id.digitalClockId)).setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf"));

        initilTies();
        startService();
        Log.v(TAG, "taskId=" + getTaskId());

    }

    private void startService() {
        if (!serviceRunning) {
            Intent si = new Intent(this, MyService.class);
            si.putExtra("id", getTaskId());
            si.putExtra("pid", android.os.Process.myPid());
            startService(si);
            Log.v(TAG, "service started");
        }
    }

    private void notExpandStatusBar() {
        WindowManager manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (30 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        view = new customViewGroup(this);

        manager.addView(view, localLayoutParams);
    }




//    private void LoadDefaultAppsPosition(String[] list) {
//        if (isPackageExisted(certifedApp.get(0), ".DialtactsActivity")) {
//            list[0] = (certifedApp.get(0) + ".DialtactsActivity");
//        }
//        if (isPackageExisted(certifedApp.get(0), ".LaunchContactsActivity")) {
//            list[1] = (certifedApp.get(0) + ".LaunchContactsActivity");
//            certifedApp.remove(1);
////            list[1]=(certifedApp.get(1));
////            list[2]=(certifedApp.get(2));
////            list[3]=(certifedApp.get(3));
//        }
//        list[1] = (certifedApp.get(1));
//        list[2] = (certifedApp.get(2));
//        list[3] = (certifedApp.get(3));
//
//    }


    private void initilTies() {
//        getCertifiedAppList();

        putAppsOnHome();
    }

    private void putAppsOnHome() {
        LinearLayout firstLinearLayout = (LinearLayout) findViewById(R.id.appFirstLineId);
        LinearLayout secondLinearLayout = (LinearLayout) findViewById(R.id.appSecondLineId);
        List<AppDetail> appsCopySorted=(ArrayList)((ArrayList)apps).clone();
        Collections.sort(appsCopySorted);
        for (int i = 0,j=0; i < VISIBLE_APPS_IN_LAUNCHER_QUANTITY; i++) {
            AppDetail appDetail=appsCopySorted.get(j);
            if (appDetail.getPosition()==i && appDetail.launcherIntent!=null) {
                j++;
                    if (i < VISIBLE_APPS_IN_LAUNCHER_QUANTITY / 2) {
                        ImageView img = (ImageView) ((LinearLayout) firstLinearLayout.getChildAt(i % 3)).getChildAt(0);
//                        img.setTag(appDetail.name);
                        TextView tv = (TextView) ((LinearLayout) firstLinearLayout.getChildAt(i % 3)).getChildAt(1);
                        img.setImageDrawable(appDetail.icon);
                        tv.setText(appDetail.label);
                        appDetail.published();
                    } else {
                        ImageView img = (ImageView) ((LinearLayout) secondLinearLayout.getChildAt(i % 3)).getChildAt(0);
//                        img.setTag(appDetail.name);
                        TextView tv = (TextView) ((LinearLayout) secondLinearLayout.getChildAt(i % 3)).getChildAt(1);
                        img.setImageDrawable(appDetail.icon);
                        tv.setText(appDetail.label);
                        appDetail.published();
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




    private void getCertifiedAppList(boolean firstTime) {

        findClockIntent();

        Intent intent;
        for (int i = 0; i < certifedApp.size(); i++) {
            intent = certifedApp.get(i);
            List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent, 0);
            if (availableActivities.size() > 0) {

                AppDetail app = new AppDetail();
                if(firstTime && i<4) {
                    app.setPosition(i);
                }
                app.label = availableActivities.get(0).loadLabel(manager);
                app.name = availableActivities.get(0).activityInfo.packageName;
                app.launcherIntent=manager.getLaunchIntentForPackage(app.name.toString());
                app.icon = availableActivities.get(0).activityInfo.applicationInfo.loadIcon(manager);
                apps.add(app);

            }
        }
    }

    private void findClockIntent() {
        for(int i=0; i<clockImpls.length; i++) {
            String vendor = clockImpls[i][0];
            String packageName = clockImpls[i][1];
            String className = clockImpls[i][2];
            Intent intent=new Intent(packageName);
//                ComponentName cn = new ComponentName(packageName, className);
//                intent.setComponent(cn);
                List<ResolveInfo> availableActivities = manager.queryIntentActivities(intent, 0);
            if(availableActivities.size()>0){
                certifedApp.get(certifedApp.size());

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
        dragAndDrop = new DragAndDrop(this,apps);
        firstImageView = (ImageView) findViewById(R.id.firstImageViewId);
        secondImageView = (ImageView) findViewById(R.id.secondImageViewId);
        thirdImageView = (ImageView) findViewById(R.id.thirdImageViewId);
        fourthdImageView = (ImageView) findViewById(R.id.fourthImageViewId);
        fifthImageView = (ImageView) findViewById(R.id.fifthImageViewId);
        sixthImageView = (ImageView) findViewById(R.id.sixthImageViewId);

        firstLinearLayout = (LinearLayout) findViewById(R.id.firstTileId);
        secondLinearLayout = (LinearLayout) findViewById(R.id.secondtTileId);
        thirdLinearLayout = (LinearLayout) findViewById(R.id.thirdTileId);
        fourthLinearLayout = (LinearLayout) findViewById(R.id.fourthTileId);
        fifthLinearLayout = (LinearLayout) findViewById(R.id.fifthtTileId);
        sixthLinearLayout = (LinearLayout) findViewById(R.id.sixthTileId);
        removeLinearLayout = (LinearLayout) findViewById(R.id.removeContainerId);


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
//        if(packageStr.contains(".DialtactsActivity") || packageStr.contains(".LaunchContactsActivity")){
//            String newPackageStr=packageStr.substring(0,packageStr.lastIndexOf('.'));
//            intent=new Intent();
//            intent.setComponent(new ComponentName(newPackageStr,packageStr));
//
//        }else {
//            intent = manager.getLaunchIntentForPackage(packageStr);
//        }
        int position = Integer.valueOf((String) view.getTag());
        String packageName=null;
        Intent intent=null;
        for (AppDetail app:apps){
            if (app.getPosition()==position){
                intent=app.launcherIntent;
                packageName=app.name.toString();
                break;
            }
        }
        if (intent != null) {

            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            HomeActivity.this.startActivity(intent);
            overridePendingTransition(R.anim.zoom, 0);
            restartService(packageName);
            homePressed = false;


        } else {
            CustomDialog customDialog = new CustomDialog(HomeActivity.this, (ViewGroup) view.getParent(), apps);
            customDialog.show();
        }
    }

    private void restartService(String packageStr) {
        Intent si = new Intent(this, MyService.class);
        stopService(si);
        Log.v(TAG, "service stopped");
        si.putExtra("id", getTaskId());
        si.putExtra("pid", android.os.Process.myPid());
        startService(si);
        Log.v(TAG, "service start");
    }
    private void saveAppsPositions() {
        int appsSize=apps.size();
        appsPosotion=new int[appsSize];
        for(int i=0;i<appsSize;i++) {
            appsPosotion[i]=apps.get(i).getPosition();
        }

//        appsPosotion[appsSize]=appsSize
//
//        appsPosotion[0] = (String) firstLinearLayout.getChildAt(0).getTag();
//        appsPosotion[1] = (String) secondLinearLayout.getChildAt(0).getTag();
//        appsPosotion[2] = (String) thirdLinearLayout.getChildAt(0).getTag();
//        appsPosotion[3] = (String) fourthLinearLayout.getChildAt(0).getTag();
//        appsPosotion[4] = (String) fifthLinearLayout.getChildAt(0).getTag();
//        appsPosotion[5] = (String) sixthLinearLayout.getChildAt(0).getTag();
        ObjectOutputStream obOut = null;
        try {
            FileOutputStream fileOut = openFileOutput(POSITION_SER_FILE, MODE_PRIVATE);
            obOut=new ObjectOutputStream(fileOut);
            //write apps size in last position
//            obOut.write(appsSize);
            obOut.writeObject(appsPosotion);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (obOut != null) {
                try {
                    obOut.flush();
                    obOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void loadAppsPositions() {
        ObjectInputStream objIn = null;
        try {
            FileInputStream fileIn = openFileInput(POSITION_SER_FILE);
            objIn = new ObjectInputStream(fileIn);
//            int appSize=objIn.readInt();
            appsPosotion=(int[])objIn.readObject();
            if (appsPosotion.length==apps.size()){
                for(int i=0;i<apps.size();i++){
                    apps.get(i).setPosition(appsPosotion[i]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objIn != null) {
                try {
                    objIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private boolean isMyLauncherDefault() {
        PackageManager localPackageManager = getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        String str = localPackageManager.resolveActivity(intent,
                PackageManager.MATCH_DEFAULT_ONLY).activityInfo.packageName;
        return str.equals(getPackageName());
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
    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");
    }


    @Override
    protected void onRestart() {
        startService();
        if(view==null) {
            notExpandStatusBar();
        }
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

        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");
        if (homePressed /*|| isMyLauncherDefault()*/) {
//            stopService(new Intent(this, MyService.class));
            saveAppsPositions();
            Log.v(TAG, "saved");
            Log.v(TAG, "pressed home");
        } else {
                homePressed = true;

            Log.v(TAG, "not pressed home");
        }


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
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }


}

