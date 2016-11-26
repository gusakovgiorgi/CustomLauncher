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

/**
 * Created by hasana on 11/25/2016.
 */

public class DragAndDrop implements View.OnDragListener,View.OnLongClickListener{
    private Activity a;
    Drawable enterShape;
    Drawable normalShape;

    DragAndDrop(Activity activity){
        a=activity;
        enterShape=a.getResources().getDrawable(
                R.drawable.drop_shade);
        normalShape = a.getResources().getDrawable(R.drawable.normal_shade);
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                v.setBackgroundDrawable(normalShape);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                v.setBackgroundDrawable(enterShape);
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                v.setBackgroundDrawable(normalShape);
                break;
            case DragEvent.ACTION_DROP:


                // Dropped, reassign View to ViewGroup
                View draggedView = (View) event.getLocalState();

                LinearLayout currentContainer = (LinearLayout) v;
                ViewGroup previousContainer = (ViewGroup) draggedView.getParent();

                String curretnAppText=((TextView)currentContainer.getChildAt(1)).getText().toString();
                ((TextView)previousContainer.getChildAt(1)).setText(curretnAppText);

                String draggedAppText=event.getClipData().getItemAt(0).getText().toString();
                ((TextView)currentContainer.getChildAt(1)).setText(draggedAppText);

                View thisView=currentContainer.getChildAt(0);


                previousContainer.removeView(draggedView);
                currentContainer.removeView(thisView);

                previousContainer.addView(thisView,0);
                currentContainer.addView(draggedView,0);

                draggedView.setVisibility(View.VISIBLE);
                previousContainer.getChildAt(1).setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                v.setBackgroundDrawable(null);
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onLongClick(View view) {

        if(!((ImageView)view).getDrawable().getConstantState().equals(a.getResources().getDrawable(R.drawable.no_app).getConstantState()))
        {
            String textViewString=((TextView)((ViewGroup)view.getParent()).getChildAt(1)).getText().toString();

            // create it from the object's tag
            ClipData.Item item = new ClipData.Item(textViewString);

            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData data = new ClipData(textViewString, mimeTypes, item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag(data, //data to be dragged
                    shadowBuilder, //drag shadow
                    view, //local data about the drag and drop operation
                    0   //no needed flags
            );

            view.setVisibility(View.INVISIBLE);
            ((LinearLayout) view.getParent()).getChildAt(1).setVisibility(View.INVISIBLE);
        }
        return true;
    }
}
