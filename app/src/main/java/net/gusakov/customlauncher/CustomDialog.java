package net.gusakov.customlauncher;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class CustomDialog extends Dialog{

    public Activity activity;
    private int screenWidth;
    private int screenHeight;
    private LinearLayout rootViewLayout;
    private ListView list;
    private List<AppDetail> apps;
    private List<AppDetail> unpublishedApps;

    public CustomDialog(Activity a,View view,List apps) {
        super(a);
        rootViewLayout=(LinearLayout)view;
        activity=a;
        unpublishedApps=new ArrayList<>();
        this.apps=apps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        for(AppDetail app:apps){
            if(!app.isPublished()){
                unpublishedApps.add(app);
            }
        }
        loadListView();
//        manager=activity.getPackageManager();
    }

    private void setDialogSize(double widthScale, double heightScale) {
        getScreenDimensions();
        getWindow().setLayout((int)(screenWidth*widthScale),(int)(screenHeight*heightScale));
    }

    private void loadListView(){
        list = (ListView)findViewById(R.id.apps_list);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(getContext(),R.layout.list_item, unpublishedApps) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                }

                ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(unpublishedApps.get(position).icon);

                TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
                appLabel.setText(unpublishedApps.get(position).label);

                TextView appName = (TextView)convertView.findViewById(R.id.item_app_name);
                appName.setText(unpublishedApps.get(position).name);

                return convertView;
            }
        };

        list.setAdapter(adapter);
        addClickListener();
    }

    private void getScreenDimensions(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }

    private void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                ImageView img = (ImageView) rootViewLayout.getChildAt(0);
//                img.setTag(unpublishedApps.get(pos).name);
                TextView tv = (TextView)rootViewLayout.getChildAt(1);
                img.setImageDrawable(unpublishedApps.get(pos).icon);
                tv.setText(unpublishedApps.get(pos).label);
                unpublishedApps.get(pos).setPosition(Integer.valueOf((String)img.getTag()));
                unpublishedApps.get(pos).published();
                CustomDialog.this.dismiss();
            }
        });
    }

}