package com.lycoo.commons.util;

import android.content.Context;

public class ResourceUtils {

    /**
     * 获取资源ID
     *
     * @param context   上下文
     * @param className 资源类型
     * @param name      资源名称
     * @return 资源ID
     *
     * Created by lancy on 2019/5/22 15:07
     */
    @SuppressWarnings("rawtypes")
    public static int getIdByName(Context context, String className, String name) {
        Class clazz = null;
        int resId = 0;

        try {
            clazz = Class.forName(context.getPackageName() + ".R");
            Class[] classes = clazz.getClasses();
            Class desireClass = null;

            for (Class cs : classes) {
                if (cs.getName().split("\\$")[1].equals(className)) {
                    desireClass = cs;
                    break;
                }
            }

            if (null != desireClass) {
                try {
                    resId = desireClass.getField(name).getInt(desireClass);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resId;
    }

    /**
     * 获取图片资源ID
     *
     * @param context 上下文
     * @param name    资源名称
     * @return 图片资源ID
     *
     * Created by lancy on 2019/5/22 15:08
     */
    public static int getDrawableIdByName(Context context, String name) {
        return getIdByName(context, "drawable", name);
    }


    @SuppressWarnings("rawtypes")
    public static int[] getIdArrayByName(Context context, String className, String name) {
        Class clazz = null;
        int[] resIdArray = null;

        try {
            clazz = Class.forName(context.getPackageName() + ".R");
            Class[] classes = clazz.getClasses();
            Class desireClass = null;

            for (Class cs : classes) {
                if (cs.getName().split("\\$")[1].equals(className)) {
                    desireClass = cs;
                    break;
                }
            }

            if ((null != desireClass) //
                    && null != desireClass.getField(name).get(desireClass)//
                    && desireClass.getField(name).get(desireClass).getClass().isArray()//
            ) {
                resIdArray = (int[]) desireClass.getField(name).get(desireClass);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return resIdArray;
    }


    public static int getIdByName(String packageName, String className, String name) {
        Class clazz = null;
        int resId = 0;

        try {
            clazz = Class.forName(packageName);
            Class[] classes = clazz.getClasses();
            Class desireClass = null;

            for (Class cs : classes) {
                if (cs.getName().split("\\$")[1].equals(className)) {
                    desireClass = cs;
                    break;
                }
            }

            if (null != desireClass) {
                try {
                    resId = desireClass.getField(name).getInt(desireClass);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return resId;
    }


}
