package com.chaitanya.my_community;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.chaitanya.my_community.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "";

    //login relative layouts
    private RelativeLayout rellay1, rellay2;

    private Button loginBtn, forgotBtn, signupBtn, google_loginBtn, fb_login_custom_Btn;

    //splash title
    private TextView title;

    private EditText email, password;

    //splash bottom text
    private LinearLayout made_with;

    private Vibrator vibrator;

    private Animation wrong_ip_shake;

    private AlertDialog alert;

    //for splash and login lottie anims
    private LottieAnimationView lottieAnimationView1;

    //handler for creating time delay
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            lottieAnimationView1.setAnimation(R.raw.blood_drop);
            lottieAnimationView1.playAnimation();
            title.setVisibility(View.INVISIBLE);
            //made_with.setVisibility(View.INVISIBLE);
            rellay1.setVisibility(View.VISIBLE);
            rellay2.setVisibility(View.VISIBLE);
        }
    };

    private CoordinatorLayout layout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private DatabaseReference AppUser_Reference;

    private boolean verified = false;
    private EditText OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        layout = (CoordinatorLayout) findViewById(R.id.snackbar_layout);

        lottieAnimationView1 = (LottieAnimationView) findViewById(R.id.logo);
        lottieAnimationView1.setAnimation(R.raw.blood_drop);
        lottieAnimationView1.enableMergePathsForKitKatAndAbove(true);
        lottieAnimationView1.playAnimation();

        email = (EditText) findViewById(R.id.login_ip_email);
        password = (EditText) findViewById(R.id.password);

        //made_with = (LinearLayout) findViewById(R.id.love_linlay);

        title = (TextView) findViewById(R.id.app_title);
        rellay1 = (RelativeLayout) findViewById(R.id.rellay1);
        rellay2 = (RelativeLayout) findViewById(R.id.rellay2);

        loginBtn = (Button) findViewById(R.id.login_btn);
        signupBtn = (Button) findViewById(R.id.signup_Btn);
        forgotBtn = (Button) findViewById(R.id.forgot_Btn);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        wrong_ip_shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);

        loginBtn.setOnClickListener(this);
        signupBtn.setOnClickListener(this);
        forgotBtn.setOnClickListener(this);

        //Checking if User is Logged in or Not
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    //If Logged in, showing splash and skipping login
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            lottieAnimationView1.setAnimation(R.raw.blood_drop);
                            lottieAnimationView1.playAnimation();
                            lottieAnimationView1.enableMergePathsForKitKatAndAbove(true);
                            title.setVisibility(View.INVISIBLE);
                            //made_with.setVisibility(View.INVISIBLE);
                            openDashboard();
                        }
                    };
                    handler.postDelayed(runnable, 2000);
                } else
                    //as Default
                    handler.postDelayed(runnable, 2000);
            }
        };
    }

    public void openDashboard() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void userAndDBReference() {
        user = mAuth.getCurrentUser();
        AppUser_Reference = FirebaseDatabase.getInstance().getReference("Main").child("Users");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //Login Button
            case R.id.login_btn:
                login();
                break;

            //SignUp Button
            case R.id.signup_Btn:
                Intent i = new Intent(this, RegisterActivity.class);
                startActivity(i);
                finish();
                break;

            //Forgot Password Button
            case R.id.forgot_Btn:
                grantPermission();
                break;
        }
    }

    private void grantPermission() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            forgotPassword();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            grantInSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();

    }

    private void grantInSettings() {
        Snackbar location_snackbar = Snackbar
                .make(layout, "We need SMS permission to access OTP", Snackbar.LENGTH_INDEFINITE)
                .setAction("GRANT", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openSettings();
                    }
                });
        location_snackbar.show();
    }

    private void openSettings() {
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        i.setData(uri);
        startActivityForResult(i, 100);
    }

    private void forgotPassword() {

        //Custom AlertDialog
        final AlertDialog.Builder forgotDialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);
        /** Inflated Forgot Password XML **/
        LayoutInflater forgot_view = LayoutInflater.from(this);
        View forgot = forgot_view.inflate(R.layout.activity_forgot_password, null);

        final Button countrycode;
        countrycode = (Button) forgot.findViewById(R.id.country_code);

        final EditText phone_number, forgot_email;
        phone_number = (EditText) forgot.findViewById(R.id.phone_number);
        OTP = (EditText) forgot.findViewById(R.id.forgot_OTP);
        forgot_email = (EditText) forgot.findViewById(R.id.confirm_email);
        final LinearLayout linearLayout = forgot.findViewById(R.id.linlay1);

        linearLayout.setVisibility(View.VISIBLE);
        phone_number.setVisibility(View.VISIBLE);
        countrycode.setVisibility(View.VISIBLE);

        forgotDialog.setView(forgot)
                .setCancelable(false)
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = forgotDialog.create();
        alert.show();
        Button confirm = alert.getButton(AlertDialog.BUTTON_POSITIVE);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phno = phone_number.getText().toString().trim();
                final String ph_no = "+91" + phno;
                final String otp = OTP.getText().toString().trim();

                if (ph_no.isEmpty()) {
                    phone_number.setError("Please enter phone number");
                    phone_number.requestFocus();
                    return;
                } else if (ph_no.length() < 10) {
                    phone_number.setError("Please enter a valid phone number");
                    phone_number.requestFocus();
                    return;
                } else {

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            ph_no,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            LoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks

                    OTP.setVisibility(View.VISIBLE);

                    verifyOTP(forgot_email, alert, otp, linearLayout);

                }
            }
        });
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            verified = true;


        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            //Log.w(TAG, "onVerificationFailed", e);

            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
                Toast.makeText(LoginActivity.this, "Invalid request", Toast.LENGTH_LONG).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
                Toast.makeText(LoginActivity.this, "Quota done", Toast.LENGTH_LONG).show();
            } else {

                // Show a message and update the UI
                Toast.makeText(LoginActivity.this, "Invalid OTP", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void verifyOTP(final EditText forgot_email, final AlertDialog alert, final String otp, final LinearLayout linearLayout) {

        if (verified) {
            linearLayout.setVisibility(View.INVISIBLE);
            resetPassword(forgot_email, alert);
        }
    }

    public void resetPassword(final EditText forgot_email, final AlertDialog alert) {
        final String forgotemail = forgot_email.getText().toString().trim();

        forgot_email.setVisibility(View.VISIBLE);

        if (forgotemail.isEmpty()) {
            forgot_email.setError("Please Enter your Email");
            forgot_email.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(forgotemail).matches()) {
            forgot_email.setError("Please Enter valid Email");
            forgot_email.requestFocus();
            return;
        } else {

            mAuth.sendPasswordResetEmail(forgotemail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Password Reset Email sent ", Toast.LENGTH_LONG);
                        toast.show();
                        alert.cancel();
                    } else
                        Toast.makeText(getApplicationContext(), "Phone number and Email " +
                                "don't Match \n Or \n No accounnt with given Email", Toast.LENGTH_LONG).show();


                }
            });
        }
    }

    private void login() {

        final String e_mail = email.getText().toString().trim();
        final String pwd = password.getText().toString().trim();

        if (e_mail.isEmpty()) {
            email.setError("Please Enter your EMail");
            email.requestFocus();
            email.setAnimation(wrong_ip_shake);
            email.startAnimation(wrong_ip_shake);
            vibrator.vibrate(120);
            return;
        } else if (pwd.isEmpty()) {
            password.setError("Please Enter your Password");
            password.requestFocus();
            password.setAnimation(wrong_ip_shake);
            password.startAnimation(wrong_ip_shake);
            vibrator.vibrate(120);
            return;
        } else if (pwd.length() < 6) {
            password.setError("Minimum Length of Password is 6");
            password.requestFocus();
            password.setAnimation(wrong_ip_shake);
            password.startAnimation(wrong_ip_shake);
            vibrator.vibrate(120);
            return;
        } else {

            if(loggingInLoader())
            //Signing into FireBase
            mAuth.signInWithEmailAndPassword(e_mail, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {

                                alert.cancel();

                                String error;
                                error = task.getException().getMessage();

                                if (error.equals("The password is invalid or the user does not have a password.")) {
                                    Toast.makeText(LoginActivity.this, "You have entered a Wrong Password", Toast.LENGTH_LONG).show();
                                } else if (error.equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                    Toast toast = Toast.makeText(LoginActivity.this, "Account does not exist with the Email \n Have you Registered ?", Toast.LENGTH_LONG);
                                    TextView toast_msg = (TextView) toast.getView().findViewById(android.R.id.message);
                                    toast_msg.setGravity(Gravity.CENTER);
                                    toast.show();
                                } else if (error.equals("The email address is badly formatted.")) {
                                    Toast.makeText(LoginActivity.this, "Please enter a Valid Email", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
        }
    }

    private boolean loggingInLoader() {

        AlertDialog.Builder logging_in = new AlertDialog.Builder(LoginActivity.this, R.style.CustomAlertDialog);
        /** Inflated Loader xml and set loading text **/
        LayoutInflater loader_view = LayoutInflater.from(LoginActivity.this);
        View loader = loader_view.inflate(R.layout.activity_loader, null);
        TextView loadingText = (TextView) loader.findViewById(R.id.loader_text);
        loadingText.setText(R.string.logging);

        logging_in.setView(loader)
                .setCancelable(false);
        alert = logging_in.create();
        alert.show();

        return true;
    }

    //Listeners to check whether Logged in or Not
    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(authStateListener);
    }

    //Listeners to check whether Logged in or Not
    @Override
    public void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(authStateListener);

    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);

        mAuth.removeAuthStateListener(authStateListener);

        if(alert!=null)
            alert.dismiss();

        super.onDestroy();
    }

    //Auto Retrieving OTP to edit text
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                parseCode(message);
            }
        }
    };

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */

    //---This will match any 6 digit number in the message, can use "|" to lookup more possible combinations
    public Pattern p = Pattern.compile("(|^)\\d{6}");

    private String parseCode(String message) {
        /* Now extract the otp*/
        if (message != null) {
            Matcher m = p.matcher(message);
            if (m.find()) {
                OTP.setText(m.group(0));
            } else {
                //something went wrong
                Toast.makeText(this, "Please enter OTP Manually", Toast.LENGTH_SHORT).show();
            }
        }
        return message;
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).
                registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }


}
