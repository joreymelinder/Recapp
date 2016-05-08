package com.jorey.recapp;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

public class RecordButton extends View {

    private long start,end;

    private final long RES = 1;

    public RecordButton(Context context) {
        super(context);
    }

    public RecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecordButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int xCenter = getWidth()/2;
        int yCenter = getHeight()/2;
        int rad = getHeight()/4;
        Date date = new Date(start);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(start);
        calendar.set(Calendar.MINUTE,0);
        long offset = start - calendar.getTimeInMillis();
        for(long t = start;t<end;t++){
            int length = 20;

        }

    }
}
