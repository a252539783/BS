package a.a.bs.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.qiyi.video.playcore.QiyiVideoView;

import a.a.bs.R;

/**
 * Created by user on 2017/5/31.
 */

public class ControlView extends RelativeLayout implements View.OnClickListener{

    private View play,draw,play_control,play_control_back;
    private DrawView dv;
    private ImageButton draw_cancel,draw_ok,draw_more,
    play_back,play_start,play_full,play_more,play_draw;
    private QiyiVideoView qiyi;

    private int width;
    private boolean sizeSet=false;
    private Context c;

    public ControlView(Context context) {
        super(context);
        init(context);
        addView(play);
        addView(draw);
        draw.setVisibility(View.GONE);
    }

    public ControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        addView(play);
        addView(draw);
        draw.setVisibility(View.GONE);
    }

    public void setQiyi(QiyiVideoView v)
    {
        qiyi=v;
    }

    private void init(Context c)
    {
        this.c=c;
        LayoutInflater lf = LayoutInflater.from(c);
        play=lf.inflate(R.layout.play_control,null);
        draw=lf.inflate(R.layout.drawlayout,null);
        dv=(DrawView) draw.findViewById(R.id.draw);

        draw_cancel=(ImageButton) draw.findViewById(R.id.draw_cancel);
        draw_ok=(ImageButton)draw.findViewById(R.id.draw_ok);
        draw_more=(ImageButton)draw.findViewById(R.id.draw_more);
        play_back=(ImageButton)play.findViewById(R.id.play_back);
        play_start=(ImageButton)play.findViewById(R.id.play_start);
        play_full=(ImageButton)play.findViewById(R.id.play_full);
        play_more=(ImageButton)play.findViewById(R.id.play_more);
        play_draw=(ImageButton)play.findViewById(R.id.play_draw);
        play_control=play.findViewById(R.id.play_control);
        play_control_back=play.findViewById(R.id.play_control_back);

        draw_cancel.setOnClickListener(this);
        draw_ok.setOnClickListener(this);
        draw_more.setOnClickListener(this);
        play_back.setOnClickListener(this);
        play_start.setOnClickListener(this);
        play_full.setOnClickListener(this);
        play_more.setOnClickListener(this);
        play_draw.setOnClickListener(this);
        play_control_back.setOnClickListener(this);
        play_control.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.draw_cancel:
                draw.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                break;
            case R.id.draw_ok:
                break;
            case R.id.draw_more:
                break;
            case R.id.play_back:
                break;
            case R.id.play_full:
                Log.e("xx","xx");
                break;
            case R.id.play_more:
                break;
            case R.id.play_start:
                break;
            case R.id.play_draw:
                draw.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
                break;
            case R.id.play_control:
                play_control.setVisibility(View.GONE);
                break;
            case R.id.play_control_back:
                if (play_control.getVisibility()==View.GONE)
                play_control.setVisibility(View.VISIBLE);
                else
                    play_control.setVisibility(View.GONE);
                break;
            default:
                play_control.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sizeSet=false;
        onSizeChanged(0,0,0,0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (sizeSet)
            return ;

        sizeSet=true;
        w=((WindowManager)c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        h=w*9/16;
        qiyi.setLayoutParams(new RelativeLayout.LayoutParams(w,h));
        setLayoutParams(new RelativeLayout.LayoutParams(w,h));
    }
}
