package cn.tonyn.value;

import android.annotation.SuppressLint;

import net.mamoe.mirai.Bot;

public class Values {
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
}
