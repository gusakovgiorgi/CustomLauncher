package net.gusakov.customlauncher;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hasana on 11/25/2016.
 */

public class DragAndDrop implements View.OnDragListener, View.OnLongClickListener {
    private Activity a;
    Drawable enterShape;
    Drawable normalShape;
    List<AppDetail> apps;
    private View draggedImageView;
    private View draggedTextView;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private LinearLayout removeLinear;
    private TextView removeTextView;
    private boolean swipeEnded = false;


    private void setSwipeEnded(boolean flag) {
        swipeEnded = flag;
    }

    private boolean getSwipeEnded() {
        return swipeEnded;
    }

    DragAndDrop(Activity activity,List<AppDetail> apps) {
        a = activity;
        removeLinear = (LinearLayout) a.findViewById(R.id.removeContainerId);
        removeTextView=(TextView)a.findViewById(R.id.removeTextViewId);
        enterShape = a.getResources().getDrawable(
                R.drawable.drop_shade);
        normalShape = a.getResources().getDrawable(R.drawable.normal_shade);
        this.apps=apps;

    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        View draggedView = (View) event.getLocalState();
        LinearLayout currentContainer = (LinearLayout) v;
        ViewGroup previousContainer = (ViewGroup) draggedView.getParent();

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                if(v!=removeLinear) {
                    v.setBackgroundDrawable(normalShape);
                }else{
                    removeTextView.setVisibility(View.VISIBLE);
                }
                break;
            case DragEvent.ACTION_DRAG_ENTERED:



                // Dropped, reassign View to ViewGroup
                draggedView = (View) event.getLocalState();

                currentContainer = (LinearLayout) v;
//                ViewGroup previousContainer = (ViewGroup) draggedView.getParent();

                if (currentContainer != previousContainer) {
                    if(currentContainer==removeLinear){
                        removeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,(long)(removeTextView.getTextSize()*1.5));
                        removeTextView.setTextColor(a.getResources().getColor(R.color.redEnter));
                    }else {
                        v.setBackgroundDrawable(enterShape);

                        String curretnAppText = ((TextView) currentContainer.getChildAt(1)).getText().toString();

                        String draggedAppText = event.getClipDescription().getLabel().toString();

                        if (mTimer != null) {
                            mTimer.cancel();
                        }
                        mTimer = new Timer();
                        mTimerTask = new MyTimerTask(draggedView, currentContainer, previousContainer, curretnAppText, draggedAppText);
                        mTimer.schedule(mTimerTask, 500L);
                        setSwipeEnded(false);
                    }
                }


//                draggedView.setVisibility(View.VISIBLE);


                break;
            case DragEvent.ACTION_DRAG_EXITED:
                if(v==removeLinear) {
                    removeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,a.getResources().getDimensionPixelSize(R.dimen.remove_text_size));
                    removeTextView.setTextColor(a.getResources().getColor(R.color.red));
                }else{
                    v.setBackgroundDrawable(normalShape);
                }
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
                break;
            case DragEvent.ACTION_DROP:

                if (currentContainer == removeLinear) {
                    previousContainer.removeView(draggedView);
                    ImageView img = new ImageView(a);
                    img.setImageResource(R.drawable.no_app);
                    img.setOnClickListener((HomeActivity) a);
                    img.setTag(draggedView.getTag());
                    img.setOnLongClickListener(this);
                    previousContainer.addView(img, 0);
                    ((TextView) previousContainer.getChildAt(1)).setText("Choose app");
                    removePositionFromAppDetail(draggedView);
//                    removePositionFromList(appsPosition,draggedView);
                    previousContainer.getChildAt(1).setVisibility(View.VISIBLE);

                } else if (getSwipeEnded()) {
                    draggedView.setVisibility(View.VISIBLE);
                    currentContainer.getChildAt(1).setVisibility(View.VISIBLE);
                } else {
                    returnDraggedView();
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                }

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundDrawable(null);
                if(v==removeLinear) {
                    removeTextView.setVisibility(View.INVISIBLE);
                }
                if (outOfBound(event)) {
                    returnDraggedView();
                }
            default:
                break;
        }
        return true;
    }

    private void removePositionFromAppDetail(View draggedView) {
        int position=Integer.valueOf((String)draggedView.getTag());
        for(AppDetail app:apps){
            if(app.getPosition()==position){
                app.setPosition(-1);
                break;
            }
        }
    }

    private void removePositionFromList(String[] appsPosition, View draggedView) {
        String tag=(String)draggedView.getTag();
        if(tag!=null) {
            for (int i = 0; i < appsPosition.length; i++) {
                if (tag.equals(appsPosition[i])) {
                    appsPosition[i] = null;
                    break;
                }
            }
        }
    }

    private void returnDraggedView() {
        draggedImageView.setVisibility(View.VISIBLE);
        draggedTextView.setVisibility(View.VISIBLE);
    }

    private void swipeViews(View draggedView, LinearLayout currentContainer, ViewGroup previousContainer, String curretnAppText, String draggedAppText) {
        ((TextView) currentContainer.getChildAt(1)).setText(draggedAppText);
        ((TextView) previousContainer.getChildAt(1)).setText(curretnAppText);

        View thisView = currentContainer.getChildAt(0);

        int currentViewPosition=Integer.valueOf((String)thisView.getTag());
        int draggedViewPosition=Integer.valueOf((String)draggedView.getTag());

        swipeAppDetailPositions(currentViewPosition,draggedViewPosition);
        swipeTags(thisView,draggedView);
        previousContainer.removeView(draggedView);
        currentContainer.removeView(thisView);

        previousContainer.addView(thisView, 0);
        currentContainer.addView(draggedView, 0);

        currentContainer.getChildAt(1).setVisibility(View.INVISIBLE);
        previousContainer.getChildAt(1).setVisibility(View.VISIBLE);
    }

    private void swipeTags(View thisView, View draggedView) {
        Object tmpTag=draggedView.getTag();
        draggedView.setTag(thisView.getTag());
        thisView.setTag(tmpTag);
    }

    private void swipeAppDetailPositions(int currentViewPosition, int draggedViewPosition) {
        AppDetail firstApp=null;
        for(AppDetail app: apps){
            if(app.getPosition()==currentViewPosition){
                firstApp=app;
                break;
            }
        }
        for(AppDetail app: apps){
            if(app.getPosition()==draggedViewPosition){
                app.setPosition(currentViewPosition);
                break;
            }
        }
        if(firstApp!=null){
            firstApp.setPosition(draggedViewPosition);
        }
    }

    private boolean outOfBound(DragEvent event) {
        if (event.getResult()) {
            return false;
        }
        return true;

    }

    @Override
    public boolean onLongClick(View view) {

        if (view.getTag() != null) {
            String textViewString = ((TextView) ((ViewGroup) view.getParent()).getChildAt(1)).getText().toString();

            // create it from the object's tag
//            ClipData.Item item = new ClipData.Item(textViewString);
//
//            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData data = ClipData.newPlainText(textViewString, "label");
//            ClipData data = new ClipData(textViewString, mimeTypes, item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag(data, //data to be dragged
                    shadowBuilder, //drag shadow
                    view, //local data about the drag and drop operation
                    0   //no needed flags
            );

            draggedTextView = ((LinearLayout) view.getParent()).getChildAt(1);
            draggedImageView = view;
            draggedImageView.setVisibility(View.INVISIBLE);
            draggedTextView.setVisibility(View.INVISIBLE);

        }
        return true;
    }

    class MyTimerTask extends TimerTask {
        View draggedView;
        LinearLayout currentContainer;
        ViewGroup previousContainer;
        String curretnAppText;
        String draggedAppText;

        MyTimerTask(View draggedView, LinearLayout currentContainer, ViewGroup previousContainer, String curretnAppText, String draggedAppText) {
            this.draggedView = draggedView;
            this.currentContainer = currentContainer;
            this.previousContainer = previousContainer;
            this.curretnAppText = curretnAppText;
            this.draggedAppText = draggedAppText;
        }

        @Override
        public void run() {

            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    swipeViews(draggedView, currentContainer, previousContainer, curretnAppText, draggedAppText);
                    setSwipeEnded(true);
                }
            });


        }
    }

}
