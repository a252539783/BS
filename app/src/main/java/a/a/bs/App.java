package a.a.bs;

import android.app.Application;

import com.qiyi.video.playcore.QiyiVideoView;

/**
 * Created by user on 2017/6/1.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        QiyiVideoView.init(this);
    }
}
