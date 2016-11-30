package net.gusakov.customlauncher;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.DigitalClock;
import android.widget.TextClock;
import android.widget.TextView;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Like AnalogClock, but digital.  Shows seconds.
 *
 * @deprecated It is recommended you use {@link TextClock} instead.
 */
@Deprecated
public class DateView extends TextView {

    Calendar mCalendar;

    private Runnable mTicker;
    private Handler mHandler;

    private boolean mTickerStopped = false;

    String mFormat;

    public DateView(Context context) {
        super(context);
        initDate();
    }

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDate();
    }

    private void initDate() {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
    }


    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();

        setFormat();

        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped) return;
                mCalendar.setTimeInMillis(System.currentTimeMillis());
                setText(DateFormat.format(mFormat, mCalendar));
                invalidate();
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_MONTH, 1);
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                long now=SystemClock.uptimeMillis();
                long neededTime = (c.getTimeInMillis()-System.currentTimeMillis());
                long next=now+neededTime;
                Log.v("dateTag","date time is "+next+"now is="+now);
//                long now = SystemClock.uptimeMillis();
//                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);

            }
        };
        mTicker.run();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;
    }

    private void setFormat() {

        mFormat = new SimpleDateFormat("EEE, MMM d").toLocalizedPattern();

    }


    @Override
    public CharSequence getAccessibilityClassName() {
        //noinspection deprecation
        return DateView.class.getName();
    }
}
