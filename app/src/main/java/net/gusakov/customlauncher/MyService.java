package net.gusakov.customlauncher;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

public class MyService extends Service {

    Handler handler;
    ActivityRunnable activityRunnable;
    String mProcessName;
    int taskId;

    public void setParameter(String mProcessName){
        this.mProcessName=mProcessName;
    }
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void checkActivity() {
        handler = new Handler();
        activityRunnable = new ActivityRunnable();
        handler.postDelayed(activityRunnable, 500);
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        checkActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(activityRunnable);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkActivity();
        taskId=intent.getIntExtra("id",0);
        return super.onStartCommand(intent, flags, startId);
    }

    private class ActivityRunnable implements Runnable {
        @Override
        public void run() {
            ActivityManager manager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningTasks = manager.getRunningAppProcesses();
//            manager.getAppTasks();
            if (runningTasks != null && runningTasks.size() > 0) {

//                ComponentName topActivity = runningTasks.get(0).baseActivity;
                Log.v("myTag","topActivity="+runningTasks.get(0).processName+", importance="+runningTasks.get(0).importance+",task ids=");
                if(runningTasks.get(0).importance!=ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    Log.v("myTag","menu button");
                        manager.moveTaskToFront(taskId, 0);

                }
                // Here you can get the TopActivity for every 500ms
//                if(!topActivity.getPackageName().equals(getPackageName())){
//                    Log.v("myTag","other app is oppend");
//                }
                handler.postDelayed(this, 200);
            }
        }
    }
}


