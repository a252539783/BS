package a.a.bs.view;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qiyi.video.playcore.ErrorCode;
import com.qiyi.video.playcore.IQYPlayerHandlerCallBack;
import com.qiyi.video.playcore.QiyiVideoView;

import a.a.bs.R;

/**
 * Created by user on 2017/5/31.
 */

public class ControlView extends RelativeLayout implements View.OnClickListener,Runnable{

    private View play,draw,play_control,play_control_back;
    private DrawView dv;
    private ImageButton draw_cancel,draw_ok,draw_more,
    play_back,play_start,play_full,play_more,play_draw;
    private QiyiVideoView qiyi;
    private SeekBar seek;
    private TextView currentTime,duration;
    private static int history=-1;

    private boolean seeking=false;
    private int width;
    private boolean sizeSet=false;
    private Context c;

    private H mh=new H();

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
        qiyi.setPlayerCallBack(new MCallback());
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
        currentTime=(TextView)play.findViewById(R.id.play_time_now);
        duration=(TextView)play.findViewById(R.id.play_time_all);
        seek=(SeekBar)play.findViewById(R.id.play_seek);

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

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int a=progress;
                int sec=a%60;
                int min=a/60%60;
                int h=a/60/60;
                if (h!=0)
                    currentTime.setText(String.format("%02d:%02d:%02d",h,min,sec));
                else
                    currentTime.setText(String.format("%02d:%02d",min,sec));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                qiyi.seekTo(seekBar.getProgress()*1000);
            }
        });

        new Thread(this).start();
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
                if (qiyi.isPlaying())
                {
                    play_start.setImageResource(R.drawable.start);
                    qiyi.pause();
                }else
                {
                    play_start.setImageResource(R.drawable.pause);
                    qiyi.start();
                }
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

    public void switchScreen()
    {
        history=qiyi.getCurrentPosition()-1000;
        if (history<0)
            history=-1;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        sizeSet=false;
        //onSizeChanged(0,0,0,0);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        //if (sizeSet)
        //    return ;
/*
        sizeSet=true;
        w=((WindowManager)c.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        h=w*9/16;
        //qiyi.setLayoutParams(new RelativeLayout.LayoutParams(w,h));
        setLayoutParams(new RelativeLayout.LayoutParams(w,h));*/
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void run() {
        while(true)
        {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (seeking)
                mh.sendEmptyMessage(H.SEEK);
        }
    }

    class MCallback implements IQYPlayerHandlerCallBack {

        public static final int IDLE=0;
        public static final int INITED=1;
        public static final int PREPARED=2;
        public static final int CANREAD=4;
        public static final int ADSING=8;
        public static final int PLAYING=16;
        public static final int ONEEND=32;
        public static final int ERROR=64;
        public static final int ALLEND=128;

        @Override
        public void OnSeekSuccess(long l) {
        }

        @Override
        public void OnWaiting(boolean b) {

        }

        @Override
        public void OnError(ErrorCode errorCode) {

        }

        @Override
        public void OnPlayerStateChanged(int i) {
            Log.e("xx","player state:"+i);

            switch (i)
            {
                case IDLE:
                    break;
                case INITED:
                    break;
                case PREPARED:

                    break;
                case CANREAD:
                    break;
                case ADSING:
                    break;
                case PLAYING:
                    mh.sendEmptyMessage(H.SEEKINIT);
                    seeking=true;
                    if (history!=-1)
                    {
                        qiyi.seekTo(history);
                        history=-1;
                    }
                    break;
                case ONEEND:
                    seeking=false;
                    break;
                case ERROR:
                    seeking=false;
                    break;
                case ALLEND:
                    seeking=false;
                    break;
            }
        }
    }

    class H extends Handler
    {
        public static final int SEEKINIT=0;
        public static final int SEEK=1;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what)
            {
                case SEEKINIT:
                    seek.setMax(qiyi.getDuration()/1000);
                    Log.e("xx","duration:"+qiyi.getDuration());
                    int a=qiyi.getDuration()/1000;
                    int sec=a%60;
                    int min=a/60%60;
                    int h=a/60/60;
                    if (h!=0)
                        duration.setText(String.format("%02d:%02d:%02d",h,min,sec));
                    else
                        duration.setText(String.format("%02d:%02d",min,sec));
                    break;
                case SEEK:
                    seek.setProgress(qiyi.getCurrentPosition()/1000);
                    break;
            }
        }
    }

}
