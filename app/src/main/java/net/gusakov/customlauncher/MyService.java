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


    import android.app.ActivityManager;
    import android.app.ActivityManager.RunningAppProcessInfo;
    import android.app.Service;
    import android.content.Intent;
    import android.os.Handler;
    import android.os.IBinder;
    import android.util.Log;
    import java.util.List;

    public class MyService extends Service {
        ActivityRunnable activityRunnable;
        int appPid;
        Handler handler;
        int launcherPid;
        String mProcessName;
        ActivityManager manager;
        boolean oneTime;
        int taskId;

        private class ActivityRunnable implements Runnable {
            private ActivityRunnable() {
            }

            public void run() {
                List<RunningAppProcessInfo> runningTasks = MyService.this.manager.getRunningAppProcesses();
                if (runningTasks != null && runningTasks.size() > 0) {
                    Log.v("myTag", "topActivity=" + ((RunningAppProcessInfo) runningTasks.get(0)).processName + ", importance=" + ((RunningAppProcessInfo) runningTasks.get(0)).importance + ",task ids=" + ((RunningAppProcessInfo) runningTasks.get(0)).pid);
                    if (((RunningAppProcessInfo) runningTasks.get(0)).importance != 100) {
                        Log.v("myTag", "menu button");
                        MyService.this.manager.moveTaskToFront(MyService.this.taskId, 0);
                    } else if (MyService.this.oneTime) {
                        if (((RunningAppProcessInfo) runningTasks.get(0)).pid != MyService.this.launcherPid) {
                            MyService.this.appPid = ((RunningAppProcessInfo) runningTasks.get(0)).pid;
                            Log.v("myTag", "appPid=" + MyService.this.appPid);
                            MyService.this.oneTime = false;
                        }
                    } else if (!(((RunningAppProcessInfo) runningTasks.get(0)).pid == MyService.this.launcherPid || ((RunningAppProcessInfo) runningTasks.get(0)).pid == MyService.this.appPid)) {
                        Log.v("myTag", "stopseld");
                        if(HomeActivity.view!=null) {
                            WindowManager manager = ((WindowManager) getApplicationContext()
                                    .getSystemService(Context.WINDOW_SERVICE));
                            manager.removeView(HomeActivity.view);
                            HomeActivity.view = null;
                        }
                        MyService.this.stopSelf();
                        return;
                    }
                    MyService.this.handler.postDelayed(this, 200);
                }
            }
        }

        public MyService() {
            this.oneTime = true;
        }

        public IBinder onBind(Intent intent) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        public void checkActivity() {
            this.handler = new Handler();
            this.activityRunnable = new ActivityRunnable();
            this.handler.postDelayed(this.activityRunnable, 200);
            this.manager = (ActivityManager) getApplicationContext().getSystemService("activity");
        }

        public void onCreate() {
            super.onCreate();
            HomeActivity.serviceRunning = true;
        }

        public void onDestroy() {
            super.onDestroy();
            this.handler.removeCallbacks(this.activityRunnable);
            HomeActivity.serviceRunning = false;
        }

        public int onStartCommand(Intent intent, int flags, int startId) {
            this.taskId = intent.getIntExtra("id", 0);
            this.launcherPid = intent.getIntExtra("pid", 0);
            checkActivity();
            return super.onStartCommand(intent, flags, startId);
        }
    }

//if(HomeActivity.view!=null) {
//                            WindowManager manager = ((WindowManager) getApplicationContext()
//                                    .getSystemService(Context.WINDOW_SERVICE));
//                            manager.removeView(HomeActivity.view);
//                            HomeActivity.view = null;
//                        }

