package com.min.smalltalk.Exception;

/**
 * Created by Min on 2016/11/23.
 * 捕捉闪退异常
 */
/*public class CrashHandler implements UncaughtExceptionHandler {
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // CrashHandler实例
    private static CrashHandler INSTANCE;
    // 程序的Context对象
    private Context mContext;
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashTest/log/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".txt";
    private static final String TAG = "CrashHandler";

    //保证只有一个CrashHandler实例
    private CrashHandler() {

    }

    //获取CrashHandler实例 ,单例模式
    public static CrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    *//**
     * 初始化
     *
     * @param context
     *//*
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    *//**
     * 当UncaughtException发生时会转入该重写的方法来处理
     *//*
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }

    *//**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     *            异常信息
     * @return true 如果处理了该异常信息;否则返回false.
     *//*
    public boolean handleException(Throwable ex) {
        if (ex == null || mContext == null)
            return false;
        final String crashReport = getCrashReport(mContext, ex);
        new Thread() {
            public void run() {
                Looper.prepare();
                File file = save2File(crashReport);
                sendAppCrashReport(mContext, crashReport, file);
                Looper.loop();
            }

        }.start();
        return true;
    }

    private File save2File(String crashReport) {
        //用于格式化日期,作为日志文件名的一部分
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = dateFormat.format(new Date());
        String fileName = FILE_NAME + time + FILE_NAME_SUFFIX;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                //存储路径，是sd卡的crash文件夹
                File dir = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + File.separator + FILE_NAME);
                if (!dir.exists())
                    dir.mkdir();
                File file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(crashReport.toString().getBytes());
                fos.close();
                return file;
            } catch (Exception e) {
                //sd卡存储，记得加上权限，不然这里会抛出异常
                L.e(TAG,"save2File error:"+ e.getMessage());
            }
        }
        return null;
    }

    private void sendAppCrashReport(final Context context,
                                    final String crashReport, final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("app错误")
                .setMessage("错误信息")
                .setPositiveButton("提交报告",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    //这以下的内容，只做参考，因为没有服务器
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    String[] tos = { "18819493906@163.com" };
                                    intent.putExtra(Intent.EXTRA_EMAIL, tos);

                                    intent.putExtra(Intent.EXTRA_SUBJECT, "Android客户端 - 错误报告");
                                    if (file != null) {
                                        intent.putExtra(Intent.EXTRA_STREAM,
                                                Uri.fromFile(file));
                                        intent.putExtra(Intent.EXTRA_TEXT,
                                                "请将此错误报告发送给我，以便我尽快修复此问题，谢谢合作！"
                                        );
                                    } else {
                                        intent.putExtra(Intent.EXTRA_TEXT,
                                                "请将此错误报告发送给我，以便我尽快修复此问题，谢谢合作！"

                                                + crashReport);
                                    }
                                    intent.setType("text/plain");
                                    intent.setType("message/rfc882");
                                    Intent.createChooser(intent, "Choose Email Client");
                                    context.startActivity(intent);

                                } catch (Exception e) {
                                    L.e(TAG,"error:" + e.getMessage());
                                } finally {
                                    dialog.dismiss();
                                    // 退出
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                    System.exit(1);
                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                // 退出
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        });

        AlertDialog dialog = builder.create();
        //需要的窗口句柄方式，没有这句会报错的
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    *//**
     * 获取APP崩溃异常报告
     *
     * @param ex
     * @return
     *//*
    private String getCrashReport(Context context, Throwable ex) {
        PackageInfo pinfo = getPackageInfo(context);
        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("Version:"  + pinfo.versionName +"-"+ pinfo.versionCode);
        exceptionStr.append("Android版本号:"  + android.os.Build.VERSION.RELEASE + "-" + Build.VERSION.SDK_INT);
        exceptionStr.append("手机制造商:" + Build.MANUFACTURER);
        exceptionStr.append("手机型号" + Build.MODEL);
        exceptionStr.append("Exception:"  + ex.getMessage());
        StackTraceElement[] elements = ex.getStackTrace();
        for (int i = 0; i < elements.length; i++) {
            exceptionStr.append(elements[i].toString());
        }
        return exceptionStr.toString();
    }

    */

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
     * 获取App安装包信息
     *
     * @return
     *//*
    private PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info=context.getPackageManager().getPackageInfo(context.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

}*/

public class CrashHandler  implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashTest/log/";
//    private static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/CrashTest/log/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".txt";
    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;
    private CrashHandler(){
    }
    public static CrashHandler getsInstance(){
        return sInstance;
    }
    public void init(Context context){
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }
/**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息
     * @param thread
     * @param ex
     */

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        try {
            //导出异常信息到SD卡中
            dumpExceptionToSDCard(ex);
            //这里可以上传异常信息到服务器，便于开发人员分析日志从而解决bug
            uploadExceptionToServer();
        }catch (IOException e){
            e.printStackTrace();
        }
        ex.printStackTrace();
        //如果系统提供默认的异常处理器，则交给系统去结束程序，否则就由自己结束自己
        if(mDefaultCrashHandler != null){
            mDefaultCrashHandler.uncaughtException(thread, ex);
        }else {
            //自己处理
            try {
                //延迟2秒杀进程
                Thread.sleep(2000);
                Toast.makeText(mContext, "程序出错了~", Toast.LENGTH_SHORT).show();
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
    private void dumpExceptionToSDCard(Throwable ex) throws IOException{
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            if(DEBUG){
                Log.e(TAG, "sdcard unmounted,skip dump exception");
                return;
            }
        }
        File dir = new File(PATH);
        if(!dir.exists()){
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(current));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
            Log.e(TAG, "dump crash info success");
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
            Log.e(TAG,"dump crash info failed");
        }
    }
    private void dumpPhoneInfo(PrintWriter pw)throws PackageManager.NameNotFoundException{
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),                PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);
        //Android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.print(Build.VERSION.SDK_INT);
        //手机制造商
        pw.print("Vendor: ");
        pw.print(Build.MANUFACTURER);
        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);
        //CPU架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
    }
    private void uploadExceptionToServer(){
        //将异常信息发送到服务器
    }
}