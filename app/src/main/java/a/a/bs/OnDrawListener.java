package a.a.bs;

/**
 * Created by user on 2017/5/31.
 */

public interface OnDrawListener {
    public static final int START=0;
    public static final int TO=1;

    public abstract void onDraw(int type,float x,float y);

}
