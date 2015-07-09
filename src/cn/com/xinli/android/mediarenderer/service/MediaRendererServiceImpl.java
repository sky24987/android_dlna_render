package cn.com.xinli.android.mediarenderer.service;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.AndroidWifiSwitchableRouter;
import org.fourthline.cling.model.ModelUtil;

import android.os.AsyncTask;
import cn.com.xinli.android.mediarenderer.UpnpApp;
import cn.com.xinli.android.mediarenderer.UpnpSingleton;

public class MediaRendererServiceImpl extends AndroidUpnpServiceImpl {
	
	final static String TAG = "MediaRendererServiceImpl";
	
	@Override
    protected AndroidUpnpServiceConfiguration createConfiguration(Object manager) {
        return new AndroidUpnpServiceConfiguration(manager) {

        	/* This is optimization for Android device
        	 * 1.set registry maintenance interval time
        	 * 2.If you are not writing a control point but a server application, you can 
        	 *   return null in the getExclusiveServiceTypes() method. This will disable 
        	 *   discovery completely, now all device and service advertisements are dropped 
        	 *   as soon as they are received.
        	 * 3.This is a control point.So,we need selective discovery of UPnP devices.
        	 *    If instead we return an empty array (the default behavior), all services 
        	 *    and devices will be discovered and no advertisements will be dropped.
        	 */
        	
            /* The only purpose of this class is to show you how you'd
             * configure the AndroidUpnpServiceImpl in your application:
             */
           @Override
           public int getRegistryMaintenanceIntervalMillis() {
               return 7000;
           }

           /*@Override
           public ServiceType[] getExclusiveServiceTypes() {
               return new ServiceType[] {
                       new UDAServiceType("RenderingControl"),
                       new UDAServiceType("AVTransport")
               };
           }*/

        };
    }

	@Override
	public void onDestroy() {
//		super.onDestroy();
		
		/*new Thread(new Runnable(){

			@Override
			public void run() {
				if (!ModelUtil.ANDROID_EMULATOR && isListeningForConnectivityChanges()){
		  		  unregisterReceiver(((AndroidWifiSwitchableRouter) upnpService.getRouter()).getBroadcastReceiver());
				}

				new Shutdown().execute(upnpService);
				
			}}).run();*/
		
		if (!ModelUtil.ANDROID_EMULATOR && isListeningForConnectivityChanges()){
	  	  	unregisterReceiver(((AndroidWifiSwitchableRouter) upnpService.getRouter()).getBroadcastReceiver());
		}
		
		new Shutdown().execute(upnpService);
		
		upnpService.getRegistry().removeDevice(UpnpSingleton.udn);
		UpnpApp application = (UpnpApp)getApplication();
		application.CancelNotification();
	}
		
	/**
	 * Do shutdown in separate thread else android.os.NetworkOnMainThreadException
	 */ 
	class Shutdown extends AsyncTask<UpnpService, Void, Void>{
	    @Override
	    protected Void doInBackground(UpnpService... svcs) {
	      UpnpService svc = svcs[0];
	      if (null != svc) {
	    	  try{
		    	 svc.shutdown();
	    	  }
	      	  catch(java.lang.IllegalArgumentException ex){
	      		  // java.lang.IllegalArgumentException: Receiver not registered: org.teleal.cling.android.AndroidWifiSwitchableRouter$1@417b3c80
	      		  ex.printStackTrace();
	      	  }
	      }
	      return null;
	    }
	}


}
