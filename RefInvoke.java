package com.vince.example.protectdemo;

import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class RefInvoke {

    public static Object getFieldObject(String paramString1, Object paramObject, String paramString2)
            throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException {
        Field localField = Class.forName(paramString1).getDeclaredField(paramString2);
        localField.setAccessible(true);
        return localField.get(paramObject);
    }

    public static Object invokeMethod(String paramString1, String paramString2, Object paramObject, Class[] paramArrayOfClass, Object[] paramArrayOfObject) {
        try
        {
            return Class.forName(paramString1).getMethod(paramString2, paramArrayOfClass).invoke(paramObject, paramArrayOfObject);
        }
        catch (Exception localException)
        {
            Log.i("RefInvoke", "invokeMethod: " + localException.getMessage());
            localException.printStackTrace();
        }
        return null;
    }

    public static Object invokeStaticMethod(String paramString1, String paramString2, Class[] paramArrayOfClass, Object[] paramArrayOfObject) {
        try
        {
            Method localMethod = Class.forName(paramString1).getDeclaredMethod(paramString2, paramArrayOfClass);
            localMethod.setAccessible(true);
            return localMethod.invoke(null, paramArrayOfObject);
        }
        catch (Exception localException)
        {
            Log.i("RefInvoke", "invokeStaticMethod: " + localException.getMessage());
            localException.printStackTrace();
        }
        return null;
    }

    public static void setFieldObject(String paramString1, String paramString2, Object paramObject1, Object paramObject2)
            throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException {
        Field localField = Class.forName(paramString1).getDeclaredField(paramString2);
        localField.setAccessible(true);
        localField.set(paramObject1, paramObject2);
    }
}
