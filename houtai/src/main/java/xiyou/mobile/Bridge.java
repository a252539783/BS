package xiyou.mobile;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.parser.JSONParser;

/**
 * Created by user on 2017/6/11.
 */

public class Bridge implements Runnable{

    public static final int METHOD_INVOKE=0;
    public static final int METHOD_CONNECT=1;
    public static final String RESULT="result";
    public static final String OK="ok";
    public static final String FAIL="failed";
    public static final String METHOD="method";
    public static final String PARAM="param";
    public static final String CLASS="cls";
    public static final String REQUESTCODE="request";
    public static final String RESPONSECODE="response";
    public static final String CALLER="caller";
    public static final String IP="ip";

    public static final int ADDFRIEND=0;
    public static final int PERMITADD=1;

    public static int port=12543;
    private LinkedBlockingQueue<Socket> sockets=new LinkedBlockingQueue<>();
    private HashMap<String,WriteThread> wts=new HashMap<>();

    public Bridge()
    {}

    public void start()
    {
        try {
            ServerSocket ss=new ServerSocket(port);
            while (true)
            {
                sockets.add(ss.accept());
                new Thread(this).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
            p(e.toString());
        }
    }


    public static void p(String s)
    {
        System.out.println(s);
    }

    @Override
    public void run() {
        final Socket s=sockets.remove();
        boolean login=false;
        String mName=null;
        int datalen=0;
        int readcount=0;
        byte[] cc=null;
        int c;
        InputStream is=null;
        try {
            is=s.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        WriteThread wt=new WriteThread(s);
        wt.start();

        while (true)
        {
            p("wait:");
            cc=new byte[4];
            if (!read(is,cc)) break;
            for (int i=0;i<4;i++)
                datalen=datalen<<8|(((int)0)|cc[i]);
            p("recv data_len"+datalen);

            try {
                c=is.read();
                if (c==-1)
                    break ;
            } catch (IOException e) {
                p(e.toString());
                break;
            }
            if (c==METHOD_INVOKE)
            {
                p("get:");
                cc=new byte[datalen];
                if (!read(is,cc))break;
                String string=new String(cc);
                p(string);
                JSONObject o=JSONObject.fromObject(new String(cc));

                JSONArray param=o.getJSONArray(PARAM);
                Object params[]=new Object[param.size()];
                for (int i=0;i<params.length;i++)
                {
                    params[i]=param.get(i);
                }
                JSONObject r=invoke(o.getString(CLASS),o.getString(METHOD),params);
                if (!login&&o.getString(METHOD).equals("login")&&r.getString(RESULT).equals(User.LOG_SUCCES)) {
                    login = true;
                    p("login success");
                    mName=r.getString(User.NAME);
                    wts.put(mName,wt);

                    LinkedBlockingQueue<byte[]> oldMsg=User.getMsg(mName);
                    while (!oldMsg.isEmpty())
                        try {
                            wt.add(oldMsg.take());
                        } catch (InterruptedException e) {
                            p(e.toString());
                        }
                }
                p("response:\n"+r.toString());
                wt.add(r.toString().getBytes());
            }else if (c==METHOD_CONNECT)
            {
                if (!login)
                {
                    p("login first");
                    continue;
                }
                p("connect:");
                cc=new byte[datalen];
                if (!read(is,cc))break;
                String string=new String(cc);
                p(string);
                JSONObject o=JSONObject.fromObject(new String(cc));
                JSONObject r=connect(mName,o);
                p("response:\n"+r.toString());
                wt.add(r.toString().getBytes());
            }
        }

        if (login)
        User.logout(mName);
        p("end");
        wts.remove(wt);
        wt.dead();
    }

    private JSONObject connect(String caller,JSONObject o)
    {
        JSONObject r=null;
        switch (o.getInt(REQUESTCODE))
        {
            case ADDFRIEND:
                r=addFriend(caller,o.getString(User.NAME));
                break;
            case PERMITADD:
                r=permittAdd(caller,o.getString(User.NAME));
                break;
        }

        return r;
    }

    private JSONObject addFriend(String caller,String name)
    {
        JSONObject o=new JSONObject();
        if (!caller.equals(name)&&User.exist_name(name).get(RESULT).equals(User.EXIST)&&User.exist_friend(caller,name).getString(RESULT).equals(User.NOEXIST))
            o.put(RESULT,OK);
        else {
            o.put(RESULT, FAIL);
            return o;
        }
        JSONObject r=User.getAddFriendMsg(caller,name);
        byte[] msg=r.toString().getBytes();
        if (wts.containsKey(name))
        {
            wts.get(name).add(msg);
        }else
        {
            User.addMsg(name,msg);
        }

        return o;
    }

    private JSONObject permittAdd(String caller,String name)
    {
        JSONObject o=new JSONObject();


        JSONObject r=User.getPermittAddMsg(caller,name);
        if (r!=null)
        {
            byte[] msg=r.toString().getBytes();
            if (wts.containsKey(name))
            {
                wts.get(name).add(msg);
            }else
            {
                User.addMsg(name,msg);
            }
            o.put(RESULT,OK);
        }else
        {
            o.put(RESULT,FAIL);
        }
        return o;
    }


    public JSONObject invoke(String cls,String method,Object ...params)
    {
        JSONObject r=null;
        Class[] types=new Class[params.length];
        for (int i=0;i<types.length;i++)
            types[i]=params[i].getClass();
        try {
            Method m=Class.forName(cls).getDeclaredMethod(method,types);
            m.setAccessible(true);
            r=(JSONObject) m.invoke(null,params);
        } catch (NoSuchMethodException e) {
            p(e.toString());
        } catch (ClassNotFoundException e) {
            p(e.toString());
        } catch (InvocationTargetException e) {
            Throwable ee=e.getCause();
            p(ee.toString());
            while (ee.getCause()!=null)
            {
                ee=ee.getCause();
                p(ee.toString());
            }
        } catch (IllegalAccessException e) {
            p(e.toString());
        }

        return r;
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
                    return false;
                readcount+=x;
                if (readcount==cc.length)
                    break;
            }
        } catch (IOException e) {
            p(e.toString());
        }
        return true;
    }

    public static boolean write(OutputStream os,byte[] cc)
    {
        try {
            int datalen=cc.length;
            byte []len_byte=new byte[4];
            for (int i=0;i<4;i++)
            {
                len_byte[3-i]=(byte)(datalen&(int)0xff);
                datalen=datalen>>8;
            }
            os.write(len_byte);
            os.write(cc,0,cc.length);
        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

}
