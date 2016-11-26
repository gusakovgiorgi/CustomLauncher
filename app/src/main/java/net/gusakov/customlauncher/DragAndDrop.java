package net.gusakov.customlauncher;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hasana on 11/25/2016.
 */

public class DragAndDrop implements View.OnDragListener, View.OnLongClickListener {
    private Activity a;
    Drawable enterShape;
    Drawable normalShape;
    private View draggedImageView;
    private View draggedTextView;
    private TimerTask mTimerTask;
    private Timer mTimer;
    private boolean swipeEnded=false;


     private  void setSwipeEnded(boolean flag){
        swipeEnded=flag;
    }

    private  boolean getSwipeEnded(){
       return swipeEnded;
    }

    DragAndDrop(Activity activity) {
        a = activity;
        enterShape = a.getResources().getDrawable(
                R.drawable.drop_shade);
        normalShape = a.getResources().getDrawable(R.drawable.normal_shade);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        View draggedView = (View) event.getLocalState();
        LinearLayout currentContainer = (LinearLayout) v;

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                v.setBackgroundDrawable(normalShape);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundDrawable(enterShape);



                // Dropped, reassign View to ViewGroup
                draggedView = (View) event.getLocalState();

                currentContainer = (LinearLayout) v;
                ViewGroup previousContainer = (ViewGroup) draggedView.getParent();

                if (currentContainer != previousContainer) {


                    String curretnAppText = ((TextView) currentContainer.getChildAt(1)).getText().toString();

                    String draggedAppText = event.getClipDescription().getLabel().toString();

                    if (mTimer != null) {
                        mTimer.cancel();
                    }
                    mTimer = new Timer();
                    mTimerTask = new MyTimerTask(draggedView, currentContainer, previousContainer, curretnAppText, draggedAppText);
                    mTimer.schedule(mTimerTask,1000L);
                    setSwipeEnded(false);
                }

//                draggedView.setVisibility(View.VISIBLE);


                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundDrawable(normalShape);
                if(mTimer!=null){
                    mTimer.cancel();
                    mTimer=null;
                }
                break;
            case DragEvent.ACTION_DROP:

                if(getSwipeEnded()) {
                    draggedView.setVisibility(View.VISIBLE);
                    currentContainer.getChildAt(1).setVisibility(View.VISIBLE);
                }else{
                    returnDraggedView();
                    if(mTimer!=null){
                        mTimer.cancel();
                        mTimer=null;
                    }
                }

            break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundDrawable(null);
                if (outOfBound(event)) {
                    returnDraggedView();
                }
            default:
                break;
        }
        return true;
    }

    private void returnDraggedView() {
        draggedImageView.setVisibility(View.VISIBLE);
        draggedTextView.setVisibility(View.VISIBLE);
    }

    private void swipeViews(View draggedView, LinearLayout currentContainer, ViewGroup previousContainer, String curretnAppText, String draggedAppText) {
        ((TextView) currentContainer.getChildAt(1)).setText(draggedAppText);
        ((TextView) previousContainer.getChildAt(1)).setText(curretnAppText);

        View thisView = currentContainer.getChildAt(0);


        previousContainer.removeView(draggedView);
        currentContainer.removeView(thisView);

        previousContainer.addView(thisView, 0);
        currentContainer.addView(draggedView, 0);

        currentContainer.getChildAt(1).setVisibility(View.INVISIBLE);
        previousContainer.getChildAt(1).setVisibility(View.VISIBLE);
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
