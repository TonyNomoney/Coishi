package cn.tonyn.value;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.view.Surface;
import android.view.SurfaceView;
import android.widget.TextView;

import net.mamoe.mirai.Bot;

import java.io.File;

public class Values {
    /**
     * 专门用来传递参数的类
     */
    public static boolean debug=false;
    @SuppressLint("SdCardPath")
    public static final String rootpath="/sdcard/Coishi/";
    public static boolean running=false;
    public static Bot bot;
    public static boolean keepAppRunning=false;
    public static long starttime=0;
    public static int BatteryPrime=0;
    public static int BatteryNow=0;
    public static int NumbeOfThreads=0;
    public static long loginQQ;
    public static boolean keep_screen_on=false;
    public static TextView LOGGER=null;
    public static String LOGSTRING="";
    public static StringBuilder AllLog=new StringBuilder();

}
