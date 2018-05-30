package com.vince.example.protectdemo;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;

import dalvik.system.DexClassLoader;

public class EnvApp extends Application {
    private static final String PRIMARY_DEX_NAME = "spp.jar";
    private static boolean isInitDone;
    private static final String loggerName = EnvApp.class.getSimpleName();
    private static final String ACTUAL_APPLICATION_NAME = "com.vince.example.protectdemo.MyApplication";

    static {
        isInitDone = false;
        try{
            Log.i(loggerName, "loadLibrary");
            System.loadLibrary("cdp");
        }catch(UnsatisfiedLinkError localUnsatisfiedLinkError){
            Log.i(loggerName, "Exception>>:" + localUnsatisfiedLinkError.getMessage());
            localUnsatisfiedLinkError.printStackTrace();
        }
    }

    private boolean prepareDex_new(AssetManager paramAssetManager, File paramFile){
        if (Initialize() != 0)
            return false;
        decryptPri(paramAssetManager, paramFile.getAbsolutePath());
        return true;
    }

    private void sfntInit1(AssetManager assetManager){
        try{
            String str1 = getDir("dex", 0).getAbsolutePath();
            String str2 = str1 + "/" + "spp.jar";
            prepareDex_new(assetManager, new File(str2));
            Object localObject = RefInvoke.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread", new Class[0], new Object[0]);
            if (localObject == null)
                return;
            String str3 = getPackageName();
            Log.e(loggerName, "Package Name:" + str3);
            WeakReference localWeakReference = (WeakReference)((Map)RefInvoke.getFieldObject("android.app.ActivityThread", localObject, "mPackages")).get(str3);
            ClassLoader localClassLoader = (ClassLoader)RefInvoke.getFieldObject("android.app.LoadedApk", localWeakReference.get(), "mClassLoader");
            DexClassLoader localDexClassLoader = new DexClassLoader(str2, str1, (String)RefInvoke.getFieldObject("android.app.LoadedApk", localWeakReference.get(), "mLibDir"), localClassLoader);
            RefInvoke.setFieldObject("android.app.LoadedApk", "mClassLoader", localWeakReference.get(), localDexClassLoader);
        }catch (Exception localException){
            localException.printStackTrace();
        }
    }

    private Application sfntInit2(){
        Application localApplication = null;
        Log.i(loggerName, "sfntInit2: Starting makeApplication Stuff");
        try{
            Object localObject1 = RefInvoke.invokeStaticMethod("android.app.ActivityThread", "currentActivityThread", new Class[0], new Object[0]);
            Object localObject2 = RefInvoke.getFieldObject("android.app.ActivityThread", localObject1, "mBoundApplication");
            Object localObject3 = RefInvoke.getFieldObject("android.app.ActivityThread$AppBindData", localObject2, "info");
            RefInvoke.setFieldObject("android.app.LoadedApk", "mApplication", localObject3, null);
            Object localObject4 = RefInvoke.getFieldObject("android.app.ActivityThread", localObject1, "mInitialApplication");
            ((ArrayList)RefInvoke.getFieldObject("android.app.ActivityThread", localObject1, "mAllApplications")).remove(localObject4);
            ApplicationInfo localApplicationInfo1 = (ApplicationInfo)RefInvoke.getFieldObject("android.app.LoadedApk", localObject3, "mApplicationInfo");
            ApplicationInfo localApplicationInfo2 = (ApplicationInfo)RefInvoke.getFieldObject("android.app.ActivityThread$AppBindData", localObject2, "appInfo");
            localApplicationInfo1.className = localApplicationInfo2.className = ACTUAL_APPLICATION_NAME;
            Class[] arrayOfClass = new Class[2];
            arrayOfClass[0] = Boolean.TYPE;
            arrayOfClass[1] = Instrumentation.class;
            Object[] arrayOfObject = new Object[2];
            arrayOfObject[0] = Boolean.FALSE;
            arrayOfObject[1] = null;
            localApplication = (Application)RefInvoke.invokeMethod("android.app.LoadedApk", "makeApplication", localObject3, arrayOfClass, arrayOfObject);
            RefInvoke.setFieldObject("android.app.ActivityThread", "mInitialApplication", localObject1, localApplication);
            for (Object o : ((Map) RefInvoke.getFieldObject("android.app.ActivityThread", localObject1, "mProviderMap")).values())
                RefInvoke.setFieldObject("android.content.ContentProvider", "mContext", RefInvoke.getFieldObject("android.app.ActivityThread$ProviderClientRecord", o, "mLocalProvider"), localApplication);
        }catch (Exception localException){
            localException.printStackTrace();
        }
        return localApplication;
    }

    public int Initialize(){
        if (isInitDone)
            return 0;
        int i = initialize();
        if (i == 0)
            isInitDone = true;
        return i;
    }

    protected void attachBaseContext(Context base){
        super.attachBaseContext(base);
        sfntInit1(base.getAssets());
        Log.e(loggerName, "Attaching base context: FINISHED");
    }

    public native int decryptPri(AssetManager paramAssetManager, String paramString);

    public native int initialize();

    public native int terminate();


    public void onCreate() {
        super.onCreate();
        Application localApplication = sfntInit2();
        if (localApplication == null)
            Log.i(loggerName, "Application->onCreate(): app = null");
        else
            localApplication.onCreate();
    }
}
