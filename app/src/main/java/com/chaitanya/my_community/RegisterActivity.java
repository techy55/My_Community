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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.chaitanya.my_community.other.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private LottieAnimationView lottieAnimationView;

    private Button registerBtn;

    private String register_submited;

    private Animation wrong_ip_shake;
    private Vibrator vibrator;

    private TextInputLayout pwd_layout;

    private RadioGroup user;
    private RadioButton ownerBtn, assocBtn;
    private String selected_User = "";

    private RadioGroup gender;
    private RadioButton maleBtn, femaleBtn;
    private String selected_Gender = "";

    private TextView terms_of_service, terms_of_use, privacy_policy, OTP_status, OTP_timer, OTP_resend;
    private LinearLayout timer_linlay;

    private EditText ip_name, email, phone, password, confirm_password;

    private CoordinatorLayout layout;

    private Handler handler;

    private FirebaseAuth mAuth;

    private boolean verified = false;
    private EditText editText_OTP;
    private String mVerificationID;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private String Name, e_mail, Gender, phone_number, pwd, confirm_pwd, otp;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        layout = (CoordinatorLayout) findViewById(R.id.snackbar_layout);

        mAuth = FirebaseAuth.getInstance();

        terms_of_service = (TextView) findViewById(R.id.termsofservice);
        terms_of_use = (TextView) findViewById(R.id.termsofuse);
        privacy_policy = (TextView) findViewById(R.id.privpolicy);

        ip_name = (EditText) findViewById(R.id.ip_name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone_number);
        password = (EditText) findViewById(R.id.password);
        confirm_password = (EditText) findViewById(R.id.confirm_password);

        gender = (RadioGroup) findViewById(R.id.gender_radio_group);
        maleBtn = (RadioButton) findViewById(R.id.male);
        femaleBtn = (RadioButton) findViewById(R.id.female);
        maleBtn.setButtonDrawable(R.drawable.ic_gender_male);
        femaleBtn.setButtonDrawable(R.drawable.ic_gender_female);

        user = (RadioGroup) findViewById(R.id.user_radio_group);
        ownerBtn = (RadioButton) findViewById(R.id.owner);
        assocBtn = (RadioButton) findViewById(R.id.assoc);

        pwd_layout = (TextInputLayout) findViewById(R.id.password_layout);
        pwd_layout.setHintAnimationEnabled(false);

        wrong_ip_shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        registerBtn = (Button) findViewById(R.id.btnRegister);
        registerBtn.setOnClickListener(this);

        handler = new Handler();

        genderChecked();
        userChecked();
    }

    private void genderChecked() {
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.male:
                        femaleBtn.setButtonDrawable(R.drawable.ic_gender_female);
                        selected_Gender = "MALE";
                        maleBtn.setButtonDrawable(null);
                        maleBtn.setButtonDrawable(R.drawable.user_male_color);
                        break;
                    case R.id.female:
                        maleBtn.setButtonDrawable(R.drawable.ic_gender_male);
                        selected_Gender = "FEMALE";
                        femaleBtn.setButtonDrawable(null);
                        femaleBtn.setButtonDrawable(R.drawable.user_female_color);
                        break;
                }
            }
        });
    }

    private void userChecked() {
        user.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.owner:
                        selected_User="Owner";
                    break;
                    case R.id.assoc:
                        selected_User="Association";
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //Handle logged in User
        }

    }


    //Handling Register Button
    @Override
    public void onClick(View v) {
        grantPermission();
    }

    public void grantPermission() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            verifyPhoneandOTP();
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

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
// [END resend_verification]

    private void countdownTimer(){
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String hms = String.format(Locale.getDefault(), "%02d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                OTP_timer.setText(String.valueOf(hms));
            }

            @Override
            public void onFinish() {
                OTP_resend.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    public void verifyPhoneandOTP() {
        Name = ip_name.getText().toString().trim();
        e_mail = email.getText().toString().trim();
        Gender = selected_Gender.trim();
        phone_number = "+91 " + phone.getText().toString().trim();
        pwd = password.getText().toString().trim();
        confirm_pwd = confirm_password.getText().toString().trim();

        if("Owner".equals(selected_User) || "Association".equals(selected_User))
        if ("MALE".equals(selected_Gender) || "FEMALE".equals(selected_Gender)) {
            if (Name.isEmpty()) {
                ip_name.setError("Please Enter a ip_name of your Choice");
                ip_name.setAnimation(wrong_ip_shake);
                ip_name.startAnimation(wrong_ip_shake);
                vibrator.vibrate(120);
            } else if (e_mail.isEmpty()) {
                email.setError("Please Enter your Email");
                email.setAnimation(wrong_ip_shake);
                email.startAnimation(wrong_ip_shake);
                vibrator.vibrate(120);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(e_mail).matches()) {
                email.setError("Please enter valid Email");
                email.setAnimation(wrong_ip_shake);
                email.startAnimation(wrong_ip_shake);
                vibrator.vibrate(120);
            } else if (phone_number.isEmpty()) {
                phone.setError("Please Enter your Phone Number");
                phone.setAnimation(wrong_ip_shake);
                phone.startAnimation(wrong_ip_shake);
                vibrator.vibrate(120);
            } else if (phone_number.length() < 10) {
                phone.setError("Please Enter valid Phone Number");
                phone.setAnimation(wrong_ip_shake);
                phone.startAnimation(wrong_ip_shake);
                vibrator.vibrate(120);
            } else if (pwd.isEmpty()) {
                password.setError("Please Enter your Password");
                password.setAnimation(wrong_ip_shake);
                password.startAnimation(wrong_ip_shake);
                vibrator.vibrate(120);
            } else if (pwd.length() < 6) {
                password.setError("Minimum length of Password should be 6");
                password.setAnimation(wrong_ip_shake);
                password.startAnimation(wrong_ip_shake);
                vibrator.vibrate(120);
            } else if (confirm_pwd.isEmpty()) {
                confirm_password.setError("Please Re-enter your Password");
                confirm_password.setAnimation(wrong_ip_shake);
                confirm_password.startAnimation(wrong_ip_shake);
                vibrator.vibrate(120);
            } else if (!pwd.equals(confirm_pwd)) {
                confirm_password.setError("Passwords doesn't Match");
                password.setError("Passwords doesn't Match");
                password.startAnimation(wrong_ip_shake);
                confirm_password.startAnimation(wrong_ip_shake);
                vibrator.vibrate(120);
            } else {

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phone_number,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        this,               // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks

                //Inflating OTP Layout
                final LayoutInflater OTP = LayoutInflater.from(this);
                View custom__otp_view = OTP.inflate(R.layout.activity_forgot_password, null);

                AlertDialog.Builder custom_OTP_dialog = new AlertDialog.Builder(this, R.style.CustomAlertDialog);

                //Getting Texts from OTP Field
                editText_OTP = (EditText) custom__otp_view.findViewById(R.id.forgot_OTP);
                OTP_status = (TextView) custom__otp_view.findViewById(R.id.otp_status_text);
                timer_linlay = (LinearLayout) custom__otp_view.findViewById(R.id.timer_linlay);
                OTP_timer =  (TextView) custom__otp_view.findViewById(R.id.otp_timer);
                OTP_resend = (TextView) custom__otp_view.findViewById(R.id.otp_resend_text);

                OTP_status.append(" " + phone_number);

                final LinearLayout linearLayout = custom__otp_view.findViewById(R.id.linlay1);
                linearLayout.setVisibility(View.VISIBLE);
                editText_OTP.setVisibility(View.VISIBLE);
                timer_linlay.setVisibility(View.VISIBLE);
                OTP_status.setVisibility(View.VISIBLE);
                OTP_timer.setVisibility(View.VISIBLE);

                countdownTimer();

                OTP_resend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        resendVerificationCode(phone_number, mResendToken);
                        OTP_resend.setVisibility(View.GONE);
                        countdownTimer();
                    }
                });

                //OTP alert dialog
                custom_OTP_dialog.setView(custom__otp_view)
                        .setCancelable(false)
                        .setPositiveButton("Confirm", null)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alert = custom_OTP_dialog.create();
                alert.show();

                //Handling +ve Button Click
                Button confirm = alert.getButton(AlertDialog.BUTTON_POSITIVE);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        otp = editText_OTP.getText().toString().trim();
                        if (otp.isEmpty()) {
                            editText_OTP.setError("Please Enter your OTP");
                            editText_OTP.setAnimation(wrong_ip_shake);
                            editText_OTP.startAnimation(wrong_ip_shake);
                            vibrator.vibrate(120);
                        } else {

                            if (verified) {
                                alert.cancel();
                                SharedPreferences sharedPreferences=getSharedPreferences("userCheck",0);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("Name",Name);
                                editor.apply();
                                registerUser(Name, e_mail, phone_number, pwd, Gender);
                            } else {
                                SharedPreferences sharedPreferences=getSharedPreferences("userCheck",0);
                                SharedPreferences.Editor editor=sharedPreferences.edit();
                                editor.putString("Name",Name);
                                editor.apply();
                                AuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, otp);
                                registerUserwithPhone(credential, Name, e_mail, phone_number, alert, Gender);
                            }
                        }
                    }
                });
            }
        } else {
            Toast.makeText(this, "Please select Gender", Toast.LENGTH_SHORT).show();
            vibrator.vibrate(120);
        }

    }

    private void registerUserwithPhone(AuthCredential credential, final String Name,
                                       final String e_mail, final String phone_number, final AlertDialog alert, final String Gender) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    UserDetails user = new UserDetails(
                            Name, e_mail, selected_User, Gender, phone_number, current_user
                    );


                    FirebaseDatabase.getInstance().getReference().child("Main").child("Users").child(selected_User)
                            .child(current_user).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            alert.cancel();
                            if (task.isSuccessful()) {
                                lottieAnimationView = (LottieAnimationView) findViewById(R.id.register_anim);
                                lottieAnimationView.setAnimation(R.raw.register_submitted);
                                registerBtn.setVisibility(View.INVISIBLE);
                                lottieAnimationView.setVisibility(View.VISIBLE);
                                lottieAnimationView.playAnimation();
                                register_submited = "YES";

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        registerSubmitted(register_submited);
                                    }
                                }, 2500);

                            } else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                } else {
                    //if task is not Successful
                    alert.cancel();

                    //Processing error to distinguish B/W Internet and Duplicate User Errors
                    String error;
                    error = task.getException().getMessage();
                    System.out.println(error);

                    if (error.equals("The email address is badly formatted.")) {
                        Toast.makeText(RegisterActivity.this, "The email address is badly formatted.", Toast.LENGTH_SHORT).show();
                    } else if (!error.equals("The email address is already in use by another account.")) {
                        //Creating a Linear Layout for error
                        LinearLayout error_linearLayout = new LinearLayout(RegisterActivity.this);

                        //Defining height, width
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        error_linearLayout.setLayoutParams(params);
                        error_linearLayout.setOrientation(LinearLayout.VERTICAL);

                        //Creating and Initializing Lottie View
                        LottieAnimationView error_anim = new LottieAnimationView(RegisterActivity.this);
                        error_anim.setAnimation(R.raw.error_message);
                        error_anim.setVisibility(View.VISIBLE);
                        error_anim.loop(true);
                        float speed = (float) 0.9;
                        error_anim.setSpeed(speed);
                        error_anim.playAnimation();

                        error_linearLayout.addView(error_anim);

                        AlertDialog.Builder error_dialog = new AlertDialog.Builder(RegisterActivity.this, R.style.CustomAlertDialog)
                                .setCancelable(true)
                                .setView(error_linearLayout)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        register_submited = "NO";
                                        registerSubmitted(register_submited);
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog error_alert = error_dialog.create();
                        error_alert.show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "User with Same Email Already Exists",
                                Toast.LENGTH_LONG).show();
                    }
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
                Toast.makeText(RegisterActivity.this, "Invalid request", Toast.LENGTH_LONG).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
                Toast.makeText(RegisterActivity.this, "Quota done", Toast.LENGTH_LONG).show();
            } else {

                // Show a message and update the UI
                Toast.makeText(RegisterActivity.this, "Unknown error", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCodeSent(String verificationID, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            mVerificationID = verificationID;
            mResendToken = forceResendingToken;

            super.onCodeSent(verificationID, forceResendingToken);
        }
    };

    private void registerUser(final String Name, final String e_mail,
                              final String phone_number, final String pwd, final String Gender) {

        AlertDialog.Builder registering = new AlertDialog.Builder(RegisterActivity.this, R.style.CustomAlertDialog);
        /** Inflated Loader xml and set loading text **/
        LayoutInflater loader_view = LayoutInflater.from(RegisterActivity.this);
        View loader = loader_view.inflate(R.layout.activity_loader, null);
        TextView loadingText = (TextView) loader.findViewById(R.id.loader_text);
        loadingText.setText(R.string.registering);

        registering.setView(loader)
                .setCancelable(false);
        final AlertDialog alert = registering.create();
        alert.show();

        //Registering in FireBase
        mAuth.createUserWithEmailAndPassword(e_mail, pwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Storing Additional Fields if Successful
                            String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            UserDetails user = new UserDetails(
                                    Name, e_mail, selected_User, Gender, phone_number, current_user
                            );

                            FirebaseDatabase.getInstance().getReference("Main").child("Users").child(selected_User)
                                    .child(current_user)
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    alert.cancel();
                                    if (task.isSuccessful()) {
                                        lottieAnimationView = (LottieAnimationView) findViewById(R.id.register_anim);
                                        lottieAnimationView.setAnimation(R.raw.register_submitted);
                                        registerBtn.setVisibility(View.INVISIBLE);
                                        lottieAnimationView.setVisibility(View.VISIBLE);
                                        lottieAnimationView.playAnimation();
                                        register_submited = "YES";

                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                registerSubmitted(register_submited);
                                            }
                                        }, 2500);

                                    } else {
                                        Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });


                        } else {
                            //if task is not Successful
                            alert.cancel();

                            //Processing error to distinguish B/W Internet and Duplicate User Errors
                            String error;
                            error = task.getException().getMessage();
                            System.out.println(error);

                            if (error.equals("The email address is badly formatted.")) {
                                Toast.makeText(RegisterActivity.this, "The email address is badly formatted.", Toast.LENGTH_SHORT).show();
                            } else if (!error.equals("The email address is already in use by another account.")) {
                                //Creating a Linear Layout for error
                                LinearLayout error_linearLayout = new LinearLayout(RegisterActivity.this);

                                //Defining height, width
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                error_linearLayout.setLayoutParams(params);
                                error_linearLayout.setOrientation(LinearLayout.VERTICAL);

                                //Creating and Initializing Lottie View
                                LottieAnimationView error_anim = new LottieAnimationView(RegisterActivity.this);
                                error_anim.setAnimation(R.raw.error_message);
                                error_anim.setVisibility(View.VISIBLE);
                                error_anim.loop(true);
                                error_anim.setSpeed(0.9f);
                                error_anim.playAnimation();

                                error_linearLayout.addView(error_anim);

                                AlertDialog.Builder error_dialog = new AlertDialog.Builder(RegisterActivity.this, R.style.CustomAlertDialog)
                                        .setCancelable(true)
                                        .setView(error_linearLayout)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                register_submited = "NO";
                                                registerSubmitted(register_submited);
                                                dialog.dismiss();
                                            }
                                        });
                                AlertDialog error_alert = error_dialog.create();
                                error_alert.show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "User with Same Email Already Exists",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    public void registerSubmitted(String register_submited) {

        if (register_submited.equals("YES")) {
            Toast toast = Toast.makeText(this, "Successfully Registered\nLogging with your credentials", Toast.LENGTH_LONG);
            TextView toast_msg = (TextView) toast.getView().findViewById(android.R.id.message);
            toast_msg.setGravity(Gravity.CENTER);
            toast.show();
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void loginIn(View view) {
        finish();
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
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

            verified = true;

            if (m.find()) {
                OTP_timer.setVisibility(View.GONE);
                OTP_resend.setVisibility(View.GONE);
                timer_linlay.setVisibility(View.GONE);
                editText_OTP.setText(m.group(0));
                if (verified) {
                    alert.cancel();
                    registerUser(Name, e_mail, phone_number, pwd, Gender);
                } else {
                    AuthCredential credential = PhoneAuthProvider.getCredential(mVerificationID, otp);
                    registerUserwithPhone(credential, Name, e_mail, phone_number, alert, Gender);
                }
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

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy() {

        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        
    }
}
