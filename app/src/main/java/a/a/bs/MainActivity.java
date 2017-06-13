package a.a.bs;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.qiyi.video.playcore.QiyiVideoView;

import a.a.bs.view.ControlView;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivity extends AppCompatActivity {

    private ControlView cv;
    private QiyiVideoView qv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlayout);

        cv=(ControlView)findViewById(R.id.controler);
        qv=(QiyiVideoView)findViewById(R.id.player);
        int w=((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        int h=w*9/16;
        if (w>((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight())
            h=((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();

        qv.setLayoutParams(new RelativeLayout.LayoutParams(w,h));
        cv.setLayoutParams(new RelativeLayout.LayoutParams(w,h));
        cv.setQiyi(qv);
        qv.setPlayData("667737400");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        cv.switchScreen();
        setContentView(R.layout.playlayout);

        cv=(ControlView)findViewById(R.id.controler);
        qv=(QiyiVideoView)findViewById(R.id.player);
        int w=((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        int h=w*9/16;

        if (newConfig.orientation==ORIENTATION_LANDSCAPE)
            h=((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        qv.setLayoutParams(new RelativeLayout.LayoutParams(w,h));
        cv.setLayoutParams(new RelativeLayout.LayoutParams(w,h));
        cv.setQiyi(qv);
        qv.setPlayData("667737400");

    }
}
