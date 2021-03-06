
package com.kuding.note.util;

import android.content.Context;
/* MODIFIED-BEGIN by xinlei.sheng, 2016-06-29,BUG-2244901*/
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
/* MODIFIED-END by xinlei.sheng,BUG-2244901*/
import android.os.Vibrator;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.util.Pair;
import android.util.SparseArray;

import java.io.IOException; // MODIFIED by xinlei.sheng, 2016-06-29,BUG-2244901
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class EditorUtils {
    private static final String TAG = "EditorUtils"; // MODIFIED by xinlei.sheng, 2016-06-29,BUG-2244901
    //private static final Pattern PATTERN_CHECK = Pattern.compile("^√ ", 8);
    private static final Pattern PATTERN_CONTACT;
    private static final Pattern PATTERN_IMAGE = Pattern.compile("^☺ (AT_\\d+)$", 8);
    private static final SparseArray<String> TAG_NAME_MAP;
    private static final HashMap<String, Integer> TAG_TYPE_MAP;

    static {
        PATTERN_CONTACT = Pattern.compile("##([^\r\n]+?)@([^\r\n]+?)##");
        TAG_TYPE_MAP = new HashMap(3);
        TAG_TYPE_MAP.put("√ ", Integer.valueOf(2));
        TAG_TYPE_MAP.put("☺ ", Integer.valueOf(3));
        TAG_NAME_MAP = new SparseArray(3);
        TAG_NAME_MAP.put(2, "√ ");
        TAG_NAME_MAP.put(3, "☺ ");
    }

    public static String getContactTag(String paramString1, String paramString2) {
        return String.format("##%s@%s##", new Object[] {
                paramString2, paramString1
        });
    }

    public static Matcher matchContact(CharSequence paramCharSequence) {
        return PATTERN_CONTACT.matcher(paramCharSequence);
    }

    public static Matcher matchImage(String paramString) {
        return PATTERN_IMAGE.matcher(paramString);
    }

    /*public static CharSequence normalizeSnippet(Context paramContext, String paramString) {
        return normalizeSnippet(paramContext, paramString, false);
    }

    public static CharSequence normalizeSnippet(Context paramContext, String paramString,
            RichParserHandler paramRichParserHandler) {
        new RichTextParser(paramString, paramRichParserHandler).parse();
        return paramRichParserHandler.getRichText();
    }

    public static CharSequence normalizeSnippet(Context paramContext, String paramString,
            boolean paramBoolean) {
        if (paramBoolean)
            ;
        for (Object localObject = new RichParserHandler(paramContext) {
            protected CharSequence getImageText(String paramAnonymousString) {
                return "";
            }
        };; localObject = new RichParserHandler(paramContext))
            return normalizeSnippet(paramContext, paramString, (RichParserHandler) localObject);
    }*/

    public static abstract interface ParserHandler {
        public abstract void handleComplete();

        public abstract boolean handleContent(int paramInt, String paramString);
    }

    public static class RichParserHandler implements EditorUtils.ParserHandler {
        private SpannableStringBuilder nBuilder = new SpannableStringBuilder();
        protected Context nContext;

        public RichParserHandler(Context paramContext) {
            this.nContext = paramContext;
        }

        private String replaceContact(String paramString) {
            return EditorUtils.PATTERN_CONTACT.matcher(paramString).replaceAll("[$2]");
        }

        private void setStrikeSpan(Editable paramEditable, int paramInt1, int paramInt2) {
            //[BUGFIX]-Mod-begin by xinlei.sheng,2015/12/16,1123724
            if(paramInt1 > paramEditable.length() || paramInt2 > paramEditable.length()){//PR768159 [Monkey]CRASH update by xiaolu.li 15.10.22
                return;
            }
            paramEditable.setSpan(new StrikethroughSpan(), paramInt1, paramInt2, 17);
            //[BUGFIX]-Mod-end by xinlei.sheng,2015/12/16,1123724
        }

        protected CharSequence getImageText(String paramString) {
            return this.nContext.getString(2131427440) + '\n';
        }

        public CharSequence getRichText() {
            return this.nBuilder;
        }

        public void handleComplete() {
            int i = this.nBuilder.length();
            if (i > 0)
                this.nBuilder.delete(i - 1, i);
        }

        public boolean handleContent(int paramInt, String paramString) {
            int i = this.nBuilder.length();
            //PR708291[Android5.1][Note_v5.1.1.5.0235.0][Monkey]CRASH: com.tct.note when run monkey.update by lxl 2015.10.14
            String temp=replaceContact(paramString);
            switch (paramInt) {
                default:
                    if(temp!=null && !TextUtils.isEmpty(temp) && !(temp.length() < 0)) {
                        this.nBuilder.append(temp).append('\n');
                    }
                    break;
                case 2:

                    if(temp!=null && !TextUtils.isEmpty(temp) && !(temp.length() < 0)) {
                        this.nBuilder.append(temp).append('\n');
                        int j = this.nBuilder.length();
                        setStrikeSpan(this.nBuilder, i, j - 1);
                    }
                    break;
                case 3:
                    this.nBuilder.append(getImageText(paramString));
                    break;
            }
            return true;
        }
    }

    public static class RichTextBuilder {
        private ArrayList<Pair<Integer, String>> nContents = new ArrayList();

        public RichTextBuilder append(int paramInt, String paramString) {
            this.nContents.add(new Pair(Integer.valueOf(paramInt), paramString));
            return this;
        }

        public String build() {
            StringBuilder localStringBuilder = new StringBuilder();
            Iterator localIterator = this.nContents.iterator();
            while (localIterator.hasNext()) {
                Pair localPair = (Pair) localIterator.next();
                if (localStringBuilder.length() > 0)
                    localStringBuilder.append("\n");
                String str = (String) EditorUtils.TAG_NAME_MAP.get(((Integer) localPair.first)
                        .intValue());
                if (str != null)
                    localStringBuilder.append(str);
                localStringBuilder.append((String) localPair.second);
            }
            return localStringBuilder.toString();
        }
    }

    public static class RichTextParser {
        private EditorUtils.ParserHandler nHandler;
        private String nText;

        public RichTextParser(String paramString, EditorUtils.ParserHandler paramParserHandler) {
            if (paramString == null)
                paramString = "";
            nText = paramString;
            nHandler = paramParserHandler;
        }

        public void parse() {
            String[] arrayOfString = this.nText.split("\n");
            int i = arrayOfString.length;
            for (int j = 0; j < i; j++) {
                String str = null;
                str = arrayOfString[j];
                if (str.startsWith((String) EditorUtils.TAG_NAME_MAP.valueAt(0))) {
                    nHandler.handleContent(EditorUtils.TAG_NAME_MAP.keyAt(0),
                            str.substring(((String) EditorUtils.TAG_NAME_MAP.valueAt(0)).length()));
                } else if (str.startsWith((String) EditorUtils.TAG_NAME_MAP.valueAt(1))) {
                    nHandler.handleContent(EditorUtils.TAG_NAME_MAP.keyAt(1),
                            str.substring(((String) EditorUtils.TAG_NAME_MAP.valueAt(1)).length()));
                } else {
                    //[BUGFIX]-ADD-BEGIN by AMNJ.rurong.zhang, 2014-12-31,PR889326
                    if(i==1){
                     nHandler.handleContent(4, str);
                  }else{
                    nHandler.handleContent(1, str);
                  }
                    //[BUGFIX]-ADD-END by AMNJ.rurong.zhang, 2014-12-31,PR889326
                }
            }
            nHandler.handleComplete();
            return;
        }
    }

    //CR925175 Added Vibrator for long press.Added by hz_nanbing.zou at 05/02/2015 begin
    public static void setVibration(Context context){
    	Vibrator vb = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    	vb.vibrate(100);
    }
    //CR925175 Added Vibrator for long press.Added by hz_nanbing.zou at 05/02/2015 end

    //PR412869 START special holding to Pixi3-4.5 4G The picture disappear into gibberish when phone horizontal screen update by xiaolu.li 7/7/2015
    //select whether inside the imagespan or not
    public static boolean isInImageSpan(String str, int index){
        if(index==-1){//PR425499 Start Money error.add protect
            return false;
        }
        String temp;
        if(str.length()>19&&str.length()>index+19) {
            temp=str.toString().substring(index,index+19);
            if (temp.startsWith("AT_") && temp.endsWith("END")) {
                return true;
            }
        }
        return false;
    }
    //PR412869 END


    /* MODIFIED-BEGIN by xinlei.sheng, 2016-06-29,BUG-2244901*/
    /**
     * get rotation angle of an image
     * @param path: image path
     * @return rotation angle
     */
    public static int getBitmapDegree(String path) {
        int degree = 0;
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(path);
            if (exif != null) {
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
            }
        } catch (IOException e) {
            KudingLog.e(TAG, "getBitmapDegree: IOException");
        } catch (Exception e) {
            KudingLog.e(TAG, "getBitmapDegree: Exception");
        }
        return degree;
    }

    /**
     * rotate a bitmap
     * @param degree ： rotation angle
     * @param bm ： bitmap
     * @return rotated bitmap
     */
    public static Bitmap rotateBitmap(int degree, Bitmap bm) {
        Bitmap rotateBm = null;
        Matrix matrix = new Matrix();

        if (matrix != null && bm != null) {
            matrix.postRotate(degree);
            try {
                rotateBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(),
                        matrix, true);
            } catch (IllegalArgumentException e) {
                KudingLog.e(TAG, "rotateBitmap: IllegalArgumentException");
            }
        }
        if (rotateBm == null) {
            KudingLog.e(TAG, "rotateBitmap: rotateBm is null");
            return bm;
        }
        if (bm != null) {
            bm.recycle();
        }
        return rotateBm;
    }
    /* MODIFIED-END by xinlei.sheng,BUG-2244901*/
}

/*
 * Location: /home/likewise-open/SAGEMWIRELESS/93416/Desktop/classes_dex2jar.jar
 * Qualified Name: com.miui.notes.editor.EditorUtils JD-Core Version: 0.6.2
 */
