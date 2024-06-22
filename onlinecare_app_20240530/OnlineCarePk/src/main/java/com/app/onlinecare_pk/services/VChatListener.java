//package com.app.onlinecare.services;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//import org.jivesoftware.smack.XMPPException;
//
//import android.app.Notification;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Handler;
//import android.os.IBinder;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.app.onlinecare.R;
//import com.app.onlinecare.VideoCallScreen;
//import com.quickblox.chat.QBChatService;
//import com.quickblox.core.QBSettings;
//import com.quickblox.users.model.QBUser;
//import com.quickblox.videochat.core.QBVideoChatController;
//import com.quickblox.videochat.model.listeners.OnQBVideoChatListener;
//import com.quickblox.videochat.model.objects.CallState;
//import com.quickblox.videochat.model.objects.VideoChatConfig;
//import com.quickblox.videochat.model.utils.Debugger;
//
//
//public class VChatListener extends Service {
//
//	private VideoChatConfig videoChatConfig;
//	QBUser qbUser;
//	SharedPreferences prefs;
//	/* private AlertDialog alertDialog;*/
//	private static final int notif_id=1;
//	Timer t;
//
//	@Override
//	public IBinder onBind(Intent intent) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public void onCreate() {
//
//		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//
//		t = new Timer();
//		t.scheduleAtFixedRate(new TimerTask() {
//
//			@Override
//			public void run() {
//
//				QBSettings.getInstance().fastConfigInit("18230", "MnuvMucdEDhJfhx", "kzSqHJVJtMW5KnX");
//
//				initChatService();
//
//				qbUser = new QBUser(prefs.getInt("qbUserId", 0));
//				qbUser.setPassword(prefs.getString("qbUserPassword", ""));
//
//
//				try {
//					DATA.print("--online care vchat service");
//					if(qbUser!=null){
//						DATA.print("--online care vchat service qbuser id: "+qbUser.getId());
//					}
//
//					QBVideoChatController.getInstance().setQBVideoChatListener(qbUser, qbVideoChatListener);
//
//				} catch (NullPointerException ex) {
//					ex.printStackTrace();
//				} catch (XMPPException e) {
//					e.printStackTrace();
//				}
//
//			};
//
//		},18000,18000);
//
//
//		// this.startForeground();
//	}
//
//	private void startForeground() {
//		startForeground(notif_id, getMyActivityNotification(""));
//	}
//
//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		// TODO Auto-generated method stub
//		//startForeground(FOREGROUND_ID, notification);
//
//		//         Runnable runnable = new Runnable() {               
//		//
//		//              @Override
//		//              public void run() {
//		//
//		//              	qbUser = new QBUser(prefs.getInt("qbUserId", 0));
//		//            	qbUser.setPassword(prefs.getString("qbUserPassword", ""));
//		//
//		//
//		//                  try {
//		//                    DATA.print("--online care Service Call user Here"+"now*******");
//		//                    if(qbUser!=null){
//		//                    DATA.print("--online care Service Call user Here"+qbUser);
//		//                    }else{
//		//                        DATA.print("--online care Service testing user"+"nothing");
//		//
//		//                    }
//		//                    DATA.print("--online care Still Running");
//		//                      QBVideoChatController.getInstance().setQBVideoChatListener(qbUser, qbVideoChatListener);
//		//                  } catch (NullPointerException ex) {
//		//                      ex.printStackTrace();
//		//                  } catch (XMPPException e) {
//		//                      e.printStackTrace();
//		//                  }
//		//              };
//		//         };
//		//            new Thread(runnable).start();
//
//
//		return 0;
//
//	}
//
//	public OnQBVideoChatListener qbVideoChatListener = new OnQBVideoChatListener() {
//
//		@Override
//		public void onMicrophoneDataReceive(byte[] audioData) {
//			QBVideoChatController.getInstance().sendAudio(audioData);
//		}
//
//		public void onCameraDataReceive(byte[] videoData) {
//
//			//    		DATA.print("--on camera data receive");
//			//    		
//			//    		Intent i = new Intent();
//			//    		i.putExtra("videoData", videoData);
//			//    		i.setAction("ON_OPPONENT_VIDEO_RECEIVE");
//			//    		sendBroadcast(i);
//			//    		DATA.videoData = videoData;
//
//			//        	VideoCallScreen.opponentView.render(videoData);
//
//		};
//
//		@Override
//		public void onOpponentAudioDataReceive(byte[] audioData) {
//			QBVideoChatController.getInstance().playAudio(audioData);
//		}
//
//		@Override
//		public void onVideoChatStateChange(CallState state, VideoChatConfig receivedVideoChatConfig) {
//			Debugger.logConnection("onVideoChatStateChange: " + state);
//			videoChatConfig = receivedVideoChatConfig;
//			/*if (progressDialog != null && progressDialog.isShowing()) {
//                progressDialog.dismiss();
//            }*/
//
//			Log.i("service state",""+state.toString());
//			switch (state) {
//
//
//			case ACCEPT:
//
//				//                	Toast.makeText(getBaseContext(), "accept from service", 0).show();
//
//				//                	count++;
//				//                	
//				//                	if(count == 2) {
//				Intent intent = new Intent(VChatListener.this, VideoCallScreen.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				intent.putExtra(VideoChatConfig.class.getCanonicalName(), videoChatConfig);
//				startActivity(intent);
//				// showCallDialog();
//				Log.i("service state 1",""+state.toString());
//
//				//                	}
//
//				break;
//			case ON_ACCEPT_BY_USER:
//				Log.i("service state 2",""+state.toString());
//
//				/* QBVideoChatController.getInstance().onAcceptFriendCall(videoChatConfig, null);*/
//
//				//startVideoChatActivity();
//				break;
//			case ON_REJECTED_BY_USER:
//				Log.i("service state 3",""+state.toString());
//				/*Toast.makeText(VChatListener.this, "Rejected by user", Toast.LENGTH_SHORT).show();*/
//				break;
//			case ON_DID_NOT_ANSWERED:
//				Log.i("service state 4",""+state.toString());
//
//				/* Toast.makeText(VChatListener.this, "User did not answer", Toast.LENGTH_SHORT).show();*/
//				break;
//			case ON_CANCELED_CALL:
//				videoChatConfig = null;
//				Log.i("service state 5","nothing");
//
//				/*if (alertDialog != null && alertDialog.isShowing()){
//                        alertDialog.dismiss();
//                    }
//                    autoCancelHandler.removeCallbacks(autoCancelTask);*/
//				break;
//
//			case ON_CONNECTED:
//
//
//				//                	VideoCallScreen.opponentView.setVisibility(View.VISIBLE);
//
//				break;
//
//			case ON_CALL_END:
//
//
//
//				//                	VideoCallScreen.opponentView.clear();
//				break;
//			}
//		}
//	};
//
//
//	public void onDestroy(){
//		super.onDestroy();
//
//		DATA.print("--onlince care vchatlistener service destroyed");
//
//		t.cancel();
//		qbVideoChatListener = null;
//	}
//
//
//
//	private Notification getMyActivityNotification(String text){
//		// The PendingIntent to launch our activity if the user selects
//		// this notification
//		/*  PendingIntent contentIntent = PendingIntent.getActivity(this,0, new Intent(this,Appointment.class), 0);*/
//
//		return new Notification.Builder(this)
//		.setContentTitle("QB Demo")
//		.setContentText(text)
//		.setSmallIcon(R.drawable.ic_launcher)
//		.getNotification();     
//	}
//
//
//
//	private void initChatService(){
//		QBChatService.setDebugEnabled(true);
//
//		if (!QBChatService.isInitialized()) {
//			Log.d("ActivityLogin", "InitChat");
//			QBChatService.init(this);
//		}else{
//			Log.d("ActivityLogin", "InitChat not needed");
//		}
//	}
//
//}