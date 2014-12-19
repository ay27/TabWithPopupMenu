package com.example.ActionBarTab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Proudly to use Intellij IDEA.
 * Created by ay27 on 14/12/18.
 */
public class ViewIndicator extends View {

    public ViewIndicator(Context context) {
        super(context);
    }

    public ViewIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private static Paint paint;
    static  {
        paint = new Paint();
        paint.setStrokeWidth(18);
        paint.setColor(Color.RED);
    }

    private int pageNum;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(start, 0, start+length, 0, paint);
    }

    private int start;
    private int length;

    public void draw(int page, int start) {
        this.start = (page)*length+start/pageNum;
        this.invalidate();
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
        length = getResources().getDisplayMetrics().widthPixels / pageNum;
    }

}
