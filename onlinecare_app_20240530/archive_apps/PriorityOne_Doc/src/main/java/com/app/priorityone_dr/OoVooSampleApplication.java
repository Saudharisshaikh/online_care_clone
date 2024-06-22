//
// OoVooSampleApplication.java
// 
// Created by ooVoo on July 22, 2013
//
// © 2013 ooVoo, LLC.  Used under license. 
//
package com.app.priorityone_dr;

import android.app.Application;
import android.content.res.Resources;

public class OoVooSampleApplication extends Application 
{
	 private static Application mInstance;

	 @Override
	 public void onCreate() 
	 {
		 super.onCreate();
		 mInstance = this;
	 }
	 
	 public static Resources getOoVooSampleResources() 
	 {
		 return mInstance.getResources();	  
	 }
}
