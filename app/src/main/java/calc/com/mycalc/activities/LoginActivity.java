package calc.com.mycalc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import calc.com.mycalc.R;
import calc.com.mycalc.db.UserPreference;
import calc.com.mycalc.utils.AppConfig;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.code)
    TextView code;
    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.login)
    FloatingActionButton login;
    private static final String TAG = LoginActivity.class.getSimpleName();
    @BindView(R.id.otp)
    EditText otp;
    @BindView(R.id.otpMain)
    CardView otpMain;

    boolean isOtpSent = false;
    @BindView(R.id.edit)
    ImageView edit;

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        login.setOnClickListener(v -> {
            if (!isOtpSent) {
                loginCall();
            } else {
                verifyOtpCall();
            }
        });

        edit.setOnClickListener(view -> {
            edit.setVisibility(View.GONE);
            otpMain.setVisibility(View.GONE);
            phone.setFocusable(true);
            phone.requestFocus();
            isOtpSent = false;
        });
    }

    private void verifyOtpCall() {
        if (otp.getText().toString().trim().length() < 6) {
            showToast(R.string.please_enter_valid_verification_code);
            return;
        }

        showLoading();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp.getText().toString().trim());
        signInWithPhoneAuthCredential(credential);
    }

    private void loginCall() {

        if (phone.getText().toString().trim().length() < 10) {
            showToast(R.string.please_enter_valid_mobile_number);
            return;
        }

        showLoading();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone.getText().toString().trim(),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        showLoading();
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        hideLoading();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        mVerificationId = verificationId;
                        edit.setVisibility(View.VISIBLE);
                        showToast(R.string.code_sent);
                        otpMain.setVisibility(View.VISIBLE);
                        phone.setFocusable(false);
                        otp.requestFocus();
                        isOtpSent = true;
                        hideLoading();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userPreference.isLoggedIn()) {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    hideLoading();
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        userPreference.storeValue(UserPreference.USER_ID, user.getUid());
                        userPreference.storeValue(UserPreference.IS_LOGGED_IN, true);

                        HashMap<String, Object> result = new HashMap<>();
                        result.put("uid", user.getUid());
                        result.put("mobile", user.getPhoneNumber());

                        database.getReference(AppConfig.USERS).child(user.getUid()).child(AppConfig.PRIVATE).setValue(result);
                        startMainActivity();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {

                        }
                    }
                });
    }
}
