package com.skeleton.mvp.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public class ProximitySensorController {

	public interface onProximitySensorCallback
	{
		//
		public void onError(int errorCode, String message);
		public void onSensorRegister();
		public void onSensorUnregister();
		public void onPlay();
		public void onPause();
	}

	public static final int MESSAGE_ON_PLAY = 901;

	public static final int MESSAGE_ON_PAUSE = 902;

	public static final int MESSAGE_SENSOR_REGISTER = 903;

	public static final int MESSAGE_SENSOR_UNREGISTER = 904;

	public static final int MESSAGE_ERROR = 404;

	public static final int ERROR_PROXIMITY_SENSOR_NOT_SUPPORTED = 301;

	private Context mContext;

	private ProximityHandler mHandler;

	private SensorEventListener mSensorEventHandler;

	private ProximitySensorManager mProximityManager;

	public ProximitySensorController(Context aContext,onProximitySensorCallback aCallbackListener)
	{
		this.mContext = aContext;

		this.mHandler = new ProximityHandler(mContext.getMainLooper());

		if(aCallbackListener != null)this.mHandler.addListener(aCallbackListener);

		if(mSensorEventHandler == null)
			mSensorEventHandler = new SensorEventListener();

		mProximityManager = new ProximitySensorManager(mContext,mSensorEventHandler);

		if(!mProximityManager.hasProximitySensorAvailable())
		{
			fireError(ERROR_PROXIMITY_SENSOR_NOT_SUPPORTED,"proximity sensor not found");
		}
	}

	public void registerListener()
	{
		if(hasProximityManagerAva())
		{
			fireMessage(MESSAGE_SENSOR_REGISTER,"Sensor Register");

			mProximityManager.enable();
		}
	}

	public void unregisterListener()
	{
		if(hasProximityManagerAva())
		{
			fireMessage(MESSAGE_SENSOR_UNREGISTER,"Sensor Unregister");

			mProximityManager.disable(true);


			//remove the proximity manager
			mProximityManager = null;

			// remove sensor event handler
			mSensorEventHandler = null;

			// remove callback
			mHandler.removeListener();
		}
	}

	public boolean hasProximityManagerAva()
	{
		return mProximityManager != null ? true : false;
	}

	private void fireMessage(int resultCode, Object message)
	{
		if (mHandler != null)
		{
			Message msg = mHandler.obtainMessage();
			msg.arg1 = resultCode;
			msg.obj =  message;
			mHandler.sendMessage(msg);
		}
	}

	private void fireError(int errorCode, String errorMessage)
	{
		if (mHandler != null)
		{
			Message msg = mHandler.obtainMessage();
			msg.arg1 = MESSAGE_ERROR;
			msg.arg2 = errorCode;
			msg.obj =  errorMessage;
			mHandler.sendMessage(msg);
		}
	}

	protected static class ProximityHandler extends Handler
	{

		private onProximitySensorCallback mCallbackListener;

		public ProximityHandler(Looper looper)
		{
			super(looper);
		}

		public void addListener(onProximitySensorCallback aListener)
		{
			this.mCallbackListener = aListener;
		}

		public void removeListener()
		{
			this.mCallbackListener = null;
		}

		@Override
		public void handleMessage(Message msg)
		{
			if(mCallbackListener != null)
			{
				switch (msg.arg1)
				{
					case MESSAGE_ERROR:
					{
						mCallbackListener.onError(msg.arg2,(String)msg.obj);
						break;
					}
					case MESSAGE_ON_PLAY :
					{
						mCallbackListener.onPlay();
						break;
					}
					case MESSAGE_ON_PAUSE :
					{
						mCallbackListener.onPause();
						break;
					}
					case MESSAGE_SENSOR_REGISTER:
					{
						mCallbackListener.onSensorRegister();
						break;
					}
					case MESSAGE_SENSOR_UNREGISTER:
					{
						mCallbackListener.onSensorUnregister();
						break;
					}
					default:
						break;
				}
			}
		}
	}

	class SensorEventListener implements ProximitySensorManager.Listener
	{

		@Override
		public void onNear()
		{
			fireMessage(MESSAGE_ON_PLAY,"near");
		}

		@Override
		public void onFar()
		{
			fireMessage(MESSAGE_ON_PAUSE,"far");
		}
	}

}
