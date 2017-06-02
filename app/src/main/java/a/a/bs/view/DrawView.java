package a.a.bs.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import a.a.bs.OnDrawListener;

/**
 * Created by user on 2017/5/31.
 */

public class DrawView extends View{

    private ArrayList<Path> paths=new ArrayList<>();
    private Paint p=new Paint();
    private int color=Color.BLUE;
    private float x,y;
    private ArrayList<OnDrawListener> listeners;

    public DrawView(Context c)
    {
        super(c);
        init();
    }


    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setColor(int c)
    {
        color=c;
        p.setColor(c);
    }

    private void init()
    {
        listeners=new ArrayList<>();
        setClickable(true);

        setBackgroundColor(Color.TRANSPARENT);
        p.setAntiAlias(true);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(5);
        p.setColor(Color.BLUE);
    }


    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);


        for (int i=0;i<paths.size();i++)
           c.drawPath(paths.get(i),p);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                paths.add(new Path());
                paths.get(paths.size()-1).reset();;
                paths.get(paths.size()-1).moveTo(motionEvent.getX(),motionEvent.getY());
                x=motionEvent.getX();
                y=motionEvent.getY();

                for (int i=0;i<listeners.size();i++)
                {
                    listeners.get(i).onDraw(OnDrawListener.START,x,y);
                }
                return true;
            case MotionEvent.ACTION_UP:
                return true;
            case MotionEvent.ACTION_MOVE:
                if (paths.size()!=0){
                    paths.get(paths.size()-1).lineTo(motionEvent.getX(),motionEvent.getY());
                    //paths.get(paths.size()-1).moveTo(x,y);

                }

                for (int i=0;i<listeners.size();i++)
                {
                    listeners.get(i).onDraw(OnDrawListener.START,motionEvent.getX(),motionEvent.getY());
                }
                break;
            default:

                break;
        }

        invalidate();
        return false;
    }

}
