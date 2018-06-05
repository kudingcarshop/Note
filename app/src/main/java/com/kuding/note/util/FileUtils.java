package com.kuding.note.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by xlli on 6/25/15.
 */
public class FileUtils {
    private static FileUtils instance = new FileUtils();
    private int bgd_id;

    public static final String KUDING_ACTION_MANAGE_APP = "android.intent.action.kuding.MANAGE_PERMISSIONS";
    public static final String KUDING_EXTRA_PACKAGE_NAME = "android.intent.extra.kuding.PACKAGE_NAME";
    public static FileUtils getInstance() {
        return instance;
    }

    /**
     * delete file
     *
     * @param f file
     */
    public void del(File f) throws IOException {
        if (f.exists() && f.isDirectory()) {// determent there are whether file or path or not
            File[] delFile= f.listFiles();
            if (delFile!=null&&delFile.length == 0) {// directly to delete,if the path hasnot contains files
                f.delete();
            } else {// if the path contains files,determent the path has child path.
                if(delFile!=null&&delFile.length>0) {
                    int i = delFile.length;
                    for (int j = 0; j < i; j++) {
                        if (delFile[j].isDirectory()) {
                            del(delFile[j].getAbsolutePath());// recursive invoke del function to delete child path file
                        }
                        delFile[j].delete();// delete file
                    }
                    f.delete();
                }
            }
        }
    }

    /**
     * delete file
     * <p/>
     * param: filepath
     */
    public void del(String filepath) throws IOException {
        del(new File(filepath));
    }

    public long fileSize(String filePath) {
        File f = new File(filePath);
        return fileSize(f);
    }

    public long fileSize(File f) {
        return f.length();
    }

    public long getFileSizes(File f) throws Exception {
        long s = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            s = fis.available();
        }
        return s;
    }

    public long getDirectorySize(File f) throws Exception {

        long size = 0;
        if (f.exists()) {
            File flist[] = f.listFiles();
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getDirectorySize(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
        }
        return size;
    }

    public long getDirectorySize(String filepath) throws Exception {
        return getDirectorySize(new File(filepath));
    }
    public static void gotoSettings(Context context){
        Intent intent;
        if(isIntentExisting(context, KUDING_ACTION_MANAGE_APP)){
            //Goto setting application permission
            intent = new Intent(KUDING_ACTION_MANAGE_APP);
            intent.putExtra(KUDING_EXTRA_PACKAGE_NAME, context.getPackageName());
        }else {
            //Goto settings details
            final Uri packageURI = Uri.parse("package:" + context.getPackageName());
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        }

        context.startActivity(intent);
    }

    public static boolean isIntentExisting(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> resolveInfo =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_ALL);
        if (resolveInfo.size() > 0) {
            return true;
        }
        return false;
    }
}
