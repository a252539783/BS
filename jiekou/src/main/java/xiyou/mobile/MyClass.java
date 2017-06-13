package xiyou.mobile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyClass {

    public static void main(String []ssssss) throws IOException, InterruptedException {
        User.login("test1","test1");
        //User.get().setIp("1.1.1.1");
        User.get().getFriendIp("test1");
        BridgeNative.close();
    }

    public static void p(String s)
    {
        System.out.println(s);
    }


}
