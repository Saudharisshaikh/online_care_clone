package com.app.mhcsn_spe;
//package com.app.onlinecare;
//
//import java.util.List;
//
//import org.jivesoftware.smack.XMPPException;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.app.onlinecare.quickblox.VideoChatApplication;
//import com.quickblox.auth.QBAuth;
//import com.quickblox.auth.model.QBSession;
//import com.quickblox.chat.QBChatService;
//import com.quickblox.core.QBEntityCallbackImpl;
//import com.quickblox.videochat.core.QBVideoChatController;
//
//public class QBLoginForVideoCall extends Activity {
//
//    private ProgressDialog progressDialog;
//    
//    Button btnLogin,btnSignup;
//    TextView tvLoginForgotPass;
//    RadioGroup rgOptions;
//    EditText etLoginUserName,etLoginPassword;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        initChatService();
//
//        // setup UI
//        //
//        setContentView(R.layout.lay_login);
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage(getString(R.string.please_wait));
//        progressDialog.setCancelable(false);
//
//        findViewById(R.id.loginByFirstUserBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressDialog.show();
//                createSession(VideoChatApplication.FIRST_USER_LOGIN, VideoChatApplication.FIRST_USER_PASSWORD);
//            }
//        });
//
//        findViewById(R.id.loginBySecondUserBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                progressDialog.show();
//                createSession(VideoChatApplication.SECOND_USER_LOGIN, VideoChatApplication.SECOND_USER_PASSWORD);
//            }
//        });
//    }
//
//    private void createSession(String login, final String password) {
//        QBAuth.createSession(login, password, new QBEntityCallbackImpl<QBSession>() {
//            @Override
//            public void onSuccess(QBSession qbSession, Bundle bundle) {
//
//                // Save current user
//                //
//                VideoChatApplication app = (VideoChatApplication)getApplication();
//                app.setCurrentUser(qbSession.getUserId(), password);
//
//                // Login to Chat
//                //
//                QBChatService.getInstance().login(app.getCurrentUser(), new QBEntityCallbackImpl() {
//                    @Override
//                    public void onSuccess() {
//                        try {
//                            QBVideoChatController.getInstance().initQBVideoChatMessageListener();
//                        } catch (XMPPException e) {
//                            e.printStackTrace();
//                        }
//                        // show next activity
//                        showCallUserActivity();
//                    }
//
//                    @Override
//                    public void onError(List errors) {
//                        Toast.makeText(QBLoginForVideoCall.this, "Error when login", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }
//
//            @Override
//            public void onError(List<String> errors) {
//                progressDialog.dismiss();
//                Toast.makeText(QBLoginForVideoCall.this, "Error when login, check test users login and password", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void initChatService(){
//        QBChatService.setDebugEnabled(true);
//
//        if (!QBChatService.isInitialized()) {
//            Log.d("ActivityLogin", "InitChat");
//            QBChatService.init(this);
//        }else{
//            Log.d("ActivityLogin", "InitChat not needed");
//        }
//    }
//
//    private void showCallUserActivity() {
//        progressDialog.dismiss();
//        
//        Intent intent = new Intent(this, ActivityVideoChat.class);
//        startActivity(intent);
//        finish();
//    }
//}