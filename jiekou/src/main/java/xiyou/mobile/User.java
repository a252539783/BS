package xiyou.mobile;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 2017/6/11.
 */

public class User {

    public static final String FRIENDS="friends_";
    public static final String FROM="user";
    public static final String USR="usrname";
    public static final String NAME="name";
    public static final String PASSWD="passwd";
    public static final String LV="lv";
    public static final String SEX="sex";
    public static final String AGE="age";
    public static final String ID="id";
    public static final String LOG_FAIL="fail";
    public static final String LOG_SUCCES="success";
    public static final String EXIST="exist";
    public static final String NOEXIST="no_exist";
    public static final String EXIST_NAME="exist_name";
    public static final String EXIST_USR="exist_usr";
    public static final String ONLINE="online";

    public static final String METHOD_REGISTER="user_register";
    public static final String METHOD_LOGIN="user_login";

    private static User current=null;

    private String usrname,passwd,name;
    private boolean online=false;
    public ArrayList<User> friends=new ArrayList<>();
    private ArrayList<OnAddFriendListener> onAddFriendListeners=new ArrayList<>();
    private ArrayList<OnPermittAddListener> onPermittAddListeners=new ArrayList<>();

    private User(String usrname,String name,String passwd)
    {
        this.usrname=usrname;
        this.passwd=passwd;
        this.name=name;
    }

    private User(String usrname,String name,String passwd,boolean online)
    {
        this(usrname, name, passwd);
        this.online=online;
    }

    public static String login(String usrname,String passwd)
    {
        JSONObject r=BridgeNative.invoke(User.class.getName(),"login",usrname,passwd);
        if (r.getString(BridgeNative.RESULT).equals(LOG_SUCCES))
        {
            current=new User(usrname,r.getString(NAME),passwd);
            current.freshFriends();
        }

        return r.getString(BridgeNative.RESULT);
    }

    static void login()
    {
        if (current!=null)
        {
            login(current.usrname,current.passwd);
        }
    }

    public static void logout()
    {
        if (current==null)
            return ;

        current=null;
        BridgeNative.close();
        BridgeNative.connect();
        BridgeNative.start();
    }

    public static boolean register(String usrname,String passwd,String name)
    {
        JSONObject r=BridgeNative.invoke(User.class.getName(),"register",usrname,passwd,name);
        if (r.getString(BridgeNative.RESULT).equals(BridgeNative.OK))
            return true;

        return false;
    }

    public void addOnAddFriendListener(OnAddFriendListener l)
    {
        onAddFriendListeners.add(l);
    }

    public void addPermittAddListener(OnPermittAddListener l)
    {
        onPermittAddListeners.add(l);
    }

    void notifyAddFriend(String name)
    {
        for (int i=0;i<onAddFriendListeners.size();i++)
        {
            onAddFriendListeners.get(i).onAddFriend(name);
        }
    }

    void notifyPermittAdd(String name)
    {
        for (int i=0;i<onPermittAddListeners.size();i++)
        {
            onPermittAddListeners.get(i).onPermittAdd(name);
        }
    }


    public void freshFriends()
    {
        JSONObject r=BridgeNative.invoke(User.class.getName(),"friendList",name);
        JSONArray ar=r.getJSONArray(BridgeNative.RESULT);
        for (int i=0;i<ar.size();i++)
        {
            JSONObject o=ar.getJSONObject(i);
            current.friends.add(new User(null,o.getString(NAME),null,o.getBoolean(ONLINE)));
        }
    }

    public void setIp(String ip)
    {
        BridgeNative.invoke(User.class.getName(),"setIp",name,ip);
    }

    public String getFriendIp(String fname)
    {
        return BridgeNative.invoke(User.class.getName(),"getIp",fname).getString(BridgeNative.IP);
    }

    public JSONObject addFriend(String name)
    {
        JSONObject o=new JSONObject();
        o.put(BridgeNative.REQUESTCODE,BridgeNative.ADDFRIEND);
        o.put(NAME,name);
        return BridgeNative.connect(o);
    }

    public static JSONObject permittAdd(String name)
    {
        JSONObject o=new JSONObject();
        o.put(BridgeNative.REQUESTCODE,BridgeNative.PERMITADD);
        o.put(NAME,name);
        return BridgeNative.connect(o);
    }

    public static User get()
    {
        return current;
    }

    public interface OnAddFriendListener
    {
        public void onAddFriend(String name);
    }

    public interface OnPermittAddListener
    {
        public void onPermittAdd(String name);
    }

}
