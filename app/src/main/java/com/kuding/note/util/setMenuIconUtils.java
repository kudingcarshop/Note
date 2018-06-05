
package com.kuding.note.util;

import android.view.Menu;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class setMenuIconUtils {
	
	public static void setIconEnable(Menu menu, boolean enable){
		
		try {
			Class<?> cla = Class.forName("android.support.v7.internal.view.menu.MenuBuilder");
			try {
				Method m =cla.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
				m.setAccessible(true);
				m.invoke(menu, enable);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}

/*
 * Location: /home/likewise-open/SAGEMWIRELESS/93416/Desktop/classes_dex2jar.jar
 * Qualified Name: com.miui.notes.editor.EditorUtils JD-Core Version: 0.6.2
 */
