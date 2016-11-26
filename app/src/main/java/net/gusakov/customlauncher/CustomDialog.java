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

import java.util.List;
import java.util.Map;


public class CustomDialog extends Dialog{

    public Activity activity;
    private int screenWidth;
    private int screenHeight;
    LinearLayout rootViewLayout;
    private List<AppDetail> appsList;
    private Map<String,AppDetail> apps;
    private String[] appsposition;
    ListView list;

    public CustomDialog(Activity a,View view,Map<String,AppDetail> appMap,String[] appsPosition) {
        super(a);
        rootViewLayout=(LinearLayout)view;
        activity=a;
        apps=appMap;
        this.appsposition=appsPosition;
        appsList =new ArrayList<>();
        boolean addPosition=true;
        for(String appKey:appMap.keySet()) {
            addPosition=true;
            for(int i=0;i<appsposition.length;i++){
                if (appKey.equals(appsPosition[i])){
                    addPosition=false;
                    break;
                }
            }
            if(addPosition) {
                appsList.add(appMap.get(appKey));
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        loadListView();
//        manager=activity.getPackageManager();
    }

    private void setDialogSize(double widthScale, double heightScale) {
        getScreenDimensions();
        getWindow().setLayout((int)(screenWidth*widthScale),(int)(screenHeight*heightScale));
    }

    private void loadListView(){
        list = (ListView)findViewById(R.id.apps_list);

        ArrayAdapter<AppDetail> adapter = new ArrayAdapter<AppDetail>(getContext(),R.layout.list_item, appsList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.list_item, null);
                }

                ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
                appIcon.setImageDrawable(appsList.get(position).icon);

                TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
                appLabel.setText(appsList.get(position).label);

                TextView appName = (TextView)convertView.findViewById(R.id.item_app_name);
                appName.setText(appsList.get(position).name);

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
                String packageStr=((TextView)v.findViewById(R.id.item_app_name)).getText().toString();
                ImageView img = (ImageView) rootViewLayout.getChildAt(0);
                img.setTag(apps.get(packageStr).name);
                TextView tv = (TextView)rootViewLayout.getChildAt(1);
                img.setImageDrawable(apps.get(packageStr).icon);
                tv.setText(apps.get(packageStr).label);
                appsposition[Integer.valueOf((String)rootViewLayout.getTag())]=packageStr;
                CustomDialog.this.dismiss();
            }
        });
    }

}