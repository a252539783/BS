package a.a.bs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.qiyi.video.playcore.QiyiVideoView;

import a.a.bs.view.ControlView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlayout);

        ControlView cv=(ControlView)findViewById(R.id.controler);
        QiyiVideoView qv=(QiyiVideoView)findViewById(R.id.player);
        cv.setQiyi(qv);
    }
}
