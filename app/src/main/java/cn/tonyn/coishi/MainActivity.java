package cn.tonyn.coishi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import cn.tonyn.bot.AndroidBot;
import cn.tonyn.bot.EventHandler;
import cn.tonyn.file.Logger;
import cn.tonyn.file.TextFile;
import cn.tonyn.value.Values;
import kotlin.reflect.jvm.internal.ReflectProperties;


public class MainActivity extends AppCompatActivity {
    void getPermissions(){
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                //写
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                //读
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED){
                //网
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.INTERNET},1);
            }
        }
    }

    void mkDirs(){
        new File(Values.rootpath+"data/log").mkdirs();
        new File(Values.rootpath+"data/config/devices").mkdirs();
        new File(Values.rootpath+"data/config/friends").mkdirs();
        new File(Values.rootpath+"data/config/groups").mkdirs();
        new File(Values.rootpath+"data/log").mkdirs();
        new File(Values.rootpath+"data/消息记录/群").mkdirs();
        new File(Values.rootpath+"data/消息记录/好友").mkdirs();
        //new File(Values.rootpath+"data/背包").mkdirs();
        new File(Values.rootpath+"data/信息").mkdirs();
        new File(Values.rootpath+"data/用户").mkdirs();
        /*if(!new File(Values.rootpath+"data/config/食物.txt").isFile()){
            TextFile.Write(Values.rootpath+"data/config/食物.txt","饼干,");
        }*/


    }
    void readConfig(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                File q=new File(Values.rootpath+"QQnum.txt");
                if(q.isFile()){
                    String qs=TextFile.Read(q);
                    EditText qt=findViewById(R.id.enter_qq);
                    qt.setText(qs);
                }
                File p=new File(Values.rootpath+"pwd.txt");
                if(p.isFile()){
                    String ps=TextFile.Read(p);
                    EditText pt=findViewById(R.id.enter_pwd);
                    pt.setText(ps);
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //权限和目录
        getPermissions();
        mkDirs();
        //记录时间
        Values.starttime=System.currentTimeMillis();
        //读取配置
        readConfig();
        //循环线程
        loop();

        findViewById(R.id.登录).setOnClickListener(this::onClick);
        findViewById(R.id.强制保留后台).setOnClickListener(this::onClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.watch_github) {
            Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("http://kzt.mcus.xyz:12366/public/login"));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onStop(){

        super.onStop();
    }
    public void loop(){
        new Thread(){
            @Override
            public void run(){
                Values.NumbeOfThreads++;
                while(true){
                    //获取电量
                    BatteryManager batteryManager = (BatteryManager)getSystemService(BATTERY_SERVICE);
                    int battery = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                    Values.BatteryNow=battery;
                    //1000ms
                    EventHandler.waitFor(1000);
                }
            }
        }.start();
    }
    public void onClick(View view){
        int id=view.getId();
        if(id==R.id.登录){
            EditText q=findViewById(R.id.enter_qq);
            EditText p=findViewById(R.id.enter_pwd);
            if(!q.getText().toString().equals("")){
                Values.loginQQ=Long.parseLong(q.getText().toString());
                if(!p.getText().toString().equals("")){
                    File device_json=new File(Values.rootpath+"data/config/devices/"+Values.loginQQ+".json");
                    if(device_json.isFile()){
                        TextFile.Empty(Values.rootpath+"QQnum.txt");
                        TextFile.Write(Values.rootpath+"QQnum.txt",q.getText().toString());
                        TextFile.Empty(Values.rootpath+"pwd.txt");
                        TextFile.Write(Values.rootpath+"pwd.txt",p.getText().toString());
                        new Thread(){
                            @Override
                            public void run(){
                                Values.NumbeOfThreads++;
                                AndroidBot.login(Values.loginQQ,p.getText().toString());
                            }
                        }.start();
                        new Thread(){
                            @Override
                            public void run(){
                                EventHandler.waitFor(5000);
                                if(Values.bot.isOnline()){
                                    Snackbar.make(findViewById(R.id.LinearLayout), "登录成功", Snackbar.LENGTH_LONG).show();
                                }else{
                                    Snackbar.make(findViewById(R.id.LinearLayout), "登录失败,请检查账号密码以及虚拟设备文件", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }.start();
                        Snackbar.make(findViewById(R.id.LinearLayout), "登录", Snackbar.LENGTH_LONG).show();
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(findViewById(R.id.LinearLayout), "没有准备对应设备文件:"+device_json, Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(R.id.LinearLayout), "您还没有输入密码", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(R.id.LinearLayout), "您还没有输入QQ号", Snackbar.LENGTH_LONG).show();
                    }
                });
            }

            Logger.l("登录按钮点击");
        }
        if(id==R.id.强制保留后台){
            Values.keepAppRunning=!Values.keepAppRunning;
            if(Values.keepAppRunning){
                setKeepRunning();
                Snackbar.make(findViewById(R.id.LinearLayout), "开启", Snackbar.LENGTH_LONG).show();
            }else{
                stopKeepingRunning();
                Snackbar.make(findViewById(R.id.LinearLayout), "关闭", Snackbar.LENGTH_LONG).show();
            }
        }

    }
    MediaPlayer mp3player=null;
    void setKeepRunning(){
        //播放一个空音频
        mp3player=MediaPlayer.create(this,R.raw.music1);
        mp3player.setLooping(true);
        new Thread(){
            @Override
            public void run(){
                Values.NumbeOfThreads++;
                mp3player.start();
            }
        }.start();
    }
    void stopKeepingRunning(){
        mp3player.stop();
        Values.NumbeOfThreads--;
    }

}
