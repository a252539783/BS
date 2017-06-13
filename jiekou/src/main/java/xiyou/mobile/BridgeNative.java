package xiyou.mobile;

import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONBuilder;
import net.sf.json.util.JSONUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.parser.JSONParser;

/**
 * Created by user on 2017/6/11.
 */

public class BridgeNative{

    public static final byte METHOD_INVOKE=0;
    public static final byte METHOD_CONNECT=1;
    public static final String RESULT="result";
    public static final String OK="ok";
    public static final String METHOD="method";
    public static final String PARAM="param";
    public static final String CLASS="cls";
    public static final String REQUESTCODE="request";
    public static final String RESPONSECODE="response";
    public static final String CALLER="caller";
    public static final String IP="ip";

    public static final int ADDFRIEND=0;
    public static final int PERMITADD=1;
    public static final int GETIP=2;

    public static int port=12543;
    private static int times=10;
    static Socket socket=null;
    static OutputStream os=null;
    static InputStream is=null;
    private static boolean alive=true;
    static LinkedBlockingQueue<JSONObject> responses_invoke=new LinkedBlockingQueue<>(),responses_connect=new LinkedBlockingQueue<>();
    private static Thread readThread=null;
    private static ArrayList<OnMessageListener> msglisteners=new ArrayList<>();

    static boolean connect()
    {
        p("connect");
        if (socket!=null)
        {
            try {
                socket.close();
                is.close();
                os.close();
            } catch (IOException e) {
                p(e.toString());
            }
        }

        try {
            socket=new Socket("123.207.152.184",port);
            os=socket.getOutputStream();
            is=socket.getInputStream();
        } catch (IOException e) {
            p(e.toString());
            return false;
        }

        return true;
    }

    static
    {
        start();
        connect();
    }

    public static void addOnMessageListener(OnMessageListener l)
    {
        msglisteners.add(l);
    }

    public static void start()
    {
        alive=true;
        new Thread()
        {
            @Override
            public void run() {
                readThread=this;
                while(alive)
                {
                    byte []cc=new byte[4];
                    int datalen=0;
                    if (!read(is,cc))
                    {
                        if (!connect())return;
                        continue;
                    }
                    for (int i=0;i<4;i++)
                        datalen=datalen<<8|(((int)0)|cc[i]);
                    cc=new byte[datalen];
                    p("recv:"+datalen);
                    if (!read(is,cc))
                    {
                        if (!connect())return;
                        continue;
                    }
                    String r=new String(cc);
                    p("get:");
                    p(r);
                    JSONObject o=JSONObject.fromObject(r);
                    if (o.containsKey(RESULT))
                    {
                        responses_invoke.add(o);
                    }else
                    {
                        responses_connect.add(o);
                        handleMsg(o);
                    }
                }

                super.run();

            }
        }.start();
    }

    private static void handleMsg(JSONObject o)
    {
        switch (o.getInt(REQUESTCODE))
        {
            case ADDFRIEND:
                User.get().notifyAddFriend(o.getString(CALLER));
                break;
            case PERMITADD:
                User.get().notifyPermittAdd(o.getString(CALLER));
                break;
        }
    }

    public static void close()
    {
        alive=false;
        readThread.interrupt();
        readThread.stop();
        try {
            socket.close();
        } catch (IOException e) {
            p(e.toString());
        }
    }

    public static JSONObject invoke(String cls,String method,Object ...params)
    {
        JSONObject o=new JSONObject();
        o.put(CLASS,cls);
        o.put(METHOD,method);
        JSONArray array=new JSONArray();
        for (int i=0;i<params.length;i++)
        {
            array.add(params[i]);
        }
        o.put(PARAM,array);
        for (int time=0;time<times;time++)
        {
            p("start send");
            if (!send(METHOD_INVOKE,o.toString().getBytes()))
            {
                connect();
                continue;
            }
            p("send:");
            p(o.toString());

            try {
                return responses_invoke.take();
            } catch (InterruptedException e) {
                p(e.toString());
            }
        }

        return null;
    }

    public static JSONObject connect(JSONObject request)
    {
        for (int time=0;time<times;time++)
        {
            p("start send");
            if (!send(METHOD_CONNECT,request.toString().getBytes()))
            {
                connect();
                continue;
            }
            p("send:");
            p(request.toString());

            try {
                return responses_invoke.take();
            } catch (InterruptedException e) {
                p(e.toString());
            }
        }

        return null;
    }

    private static boolean send(byte method,byte []cc)
    {
        int datalen=cc.length;
        byte []len_byte=new byte[4];
        for (int i=0;i<4;i++)
        {
            len_byte[3-i]=(byte)(datalen&(int)0xff);
            datalen=datalen>>8;
            p("byte "+i+":"+len_byte[3-i]);
        }
        if (!write(os,len_byte))return false;
        if (!write(os,new byte[]{method}))return false;
        if (!write(os,cc)) return false;

        return true;
    }

    public static boolean write(OutputStream os,byte[] cc)
    {
        try {
            os.write(cc,0,cc.length);
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static boolean read(InputStream is,byte[] cc)
    {
        int readcount=0;
        try {
            int x=0;
            while (true)
            {
                x=is.read(cc,readcount,cc.length-readcount);
                if (x==-1)
                {
                    p("read failed");
                    return false;
                }

                readcount+=x;
                if (readcount==cc.length)
                    break;
            }
        } catch (IOException e) {
            p(e.toString());
        }
        return true;
    }

    public static void p(String s)
    {
        System.out.println(s);
    }
}
