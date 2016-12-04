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
import android.view.WindowManager;

import java.util.List;

import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;

public class MyService extends Service {

    Handler handler;
    ActivityRunnable activityRunnable;
    String mProcessName;
    int launcherPid;
    int appPid;
    boolean oneTime=true;
    int taskId;
    ActivityManager manager;

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
        handler.postDelayed(activityRunnable, 200);
        manager= (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HomeActivity.serviceRunning=true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(activityRunnable);
        HomeActivity.serviceRunning=false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        taskId=intent.getIntExtra("id",0);
        launcherPid=intent.getIntExtra("pid",0);
        checkActivity();
        return super.onStartCommand(intent, flags, startId);
    }


    private class ActivityRunnable implements Runnable {
        @Override
        public void run() {
            List<ActivityManager.RunningAppProcessInfo> runningTasks = manager.getRunningAppProcesses();
            if (runningTasks != null && runningTasks.size() > 0) {
                Log.v("myTag","topActivity="+runningTasks.get(0).processName+", importance="+runningTasks.get(0).importance+",task ids="+runningTasks.get(0).pid);
                if(runningTasks.get(0).importance!=ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    Log.v("myTag","menu button");
                        manager.moveTaskToFront(taskId, 0);

                }else{
                    if(oneTime){
                        if(runningTasks.get(0).pid!=launcherPid) {
                            appPid = runningTasks.get(0).pid;
                            Log.v("myTag", "appPid=" + appPid);
                            oneTime = false;
                        }
                    }else if(runningTasks.get(0).pid!=launcherPid && runningTasks.get(0).pid!=appPid) {
                        Log.v("myTag","stopseld");
                        if(HomeActivity.view!=null) {
                            WindowManager manager = ((WindowManager) getApplicationContext()
                                    .getSystemService(Context.WINDOW_SERVICE));
                            manager.removeView(HomeActivity.view);
                            HomeActivity.view = null;
                        }
                        stopSelf();
                        return;
                    }
                }
                handler.postDelayed(this, 5000);
            }
        }
    }
}


