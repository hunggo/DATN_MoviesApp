package com.example.doantotnghiep_xemphim;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private Button Btn_logout;
    private static final int RC_GOOGLE_SIGN_IN = 123;
    private TextView textView;
    private GoogleSignInClient client;
    private EditText Edittext_email, Edittext_password;
    private TextInputLayout Tiplayout_login_email, Tiplayout_login_password;
    private TextView Tv_register;
    private FirebaseAuth mAuth;
    private Button Btn_login;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private CheckBox CheckBox_rememberlogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googlelogin();
        initUi();
        initListener();
        rememberLogin();

    }

    private void initUi(){
        Tv_register = findViewById(R.id.tv_register);

        Edittext_email = findViewById(R.id.edittext_login_email);
        Edittext_password = findViewById(R.id.edittext_login_password);
        Btn_login = findViewById(R.id.btn_login);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Tiplayout_login_email = findViewById(R.id.tiplayout_login_email);
        Tiplayout_login_password = findViewById(R.id.tiplayout_login_password);

        progressDialog = new ProgressDialog(this);

        sharedPreferences = getSharedPreferences("remember_login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        CheckBox_rememberlogin = findViewById(R.id.checkBox_rememberlogin);
    }

    private void initListener() {
        Tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i1);
            }
        });

        //Khi nhấn vào button login
        btnLoginOnClick();

        CheckBox_rememberlogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    editor.putBoolean("checked", true);
                    editor.commit();
                }
                else {
                    editor.putBoolean("checked", false);
                    editor.commit();
                }
            }
        });

    }

    private void rememberLogin(){
        String shared_email = sharedPreferences.getString("email", "");
        String shared_password = sharedPreferences.getString("password", "");
        boolean checked = sharedPreferences.getBoolean("checked", false);

        if(checked){
            if (shared_email != "" && shared_password != ""){
                Edittext_email.setText(shared_email);
                Edittext_password.setText(shared_password);
                CheckBox_rememberlogin.setChecked(true);
            }
        }
        else {
            if (shared_email != "" && shared_password != ""){
                Edittext_email.setText(shared_email);
                CheckBox_rememberlogin.setChecked(false);
            }
        }

    }

    public void btnLoginOnClick(){
        Btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Edittext_email.getText().toString().trim().equalsIgnoreCase("")) {
                    Tiplayout_login_email.setError(getString(R.string.error_not_be_empty));
                }
                else {
                    Tiplayout_login_email.setErrorEnabled(false);
                }
                if (Edittext_password.getText().toString().trim().equalsIgnoreCase("")) {
                    Tiplayout_login_password.setError(getString(R.string.error_not_be_empty));
                }
                else {
                    Tiplayout_login_password.setErrorEnabled(false);
                }

                if (Edittext_password.getText().toString().trim().length() >= 6){
                    if (Edittext_email.getText().toString().trim().equalsIgnoreCase("") == false
                            && Edittext_password.getText().toString().trim().equalsIgnoreCase("")  == false){
                        login();
                    }
                } else {
                    Tiplayout_login_password.setError(getString(R.string.error_password_more_chr));
                }
            }
        });
    }

    private void login() {
        String strEmail = Edittext_email.getText().toString().trim();
        String strPass = Edittext_password.getText().toString().trim();
        progressDialog.setMessage(getString(R.string.progressDialog_logging));
        progressDialog.show();
        mAuth.signInWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.toast_logging,Toast.LENGTH_SHORT).show();
                            Intent i1 = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i1);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        saveDataLogin(strEmail,strPass);

    }

    public void saveDataLogin(String strEmail, String strPass){
        if (CheckBox_rememberlogin.isChecked()){
            editor.putString("email", strEmail);
            editor.putString("password", strPass);
            editor.commit();
        }
        else {
            editor.putString("email", strEmail);
            editor.commit();
        }
    }
    private void googlelogin(){
        textView = findViewById(R.id.singInWithGoogle);
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestId() // Yêu cầu người dùng chọn tài khoản mỗi lần đăng nhập
                .build();
        client = GoogleSignIn.getClient(this, options);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Yêu cầu hiển thị danh sách tài khoản Google để người dùng chọn
                Intent signInIntent = client.getSignInIntent();
                startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Xử lý kết quả khi người dùng chọn tài khoản Google
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            } else {
                // Đăng nhập không thành công, bạn có thể xử lý tại đây nếu cần
                Toast.makeText(this, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Tạo Firebase credential từ ID token của tài khoản Google
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            // Đăng nhập vào Firebase với credential
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Đăng nhập thành công, chuyển đến MainActivity
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Đăng nhập không thành công, hiển thị thông báo lỗi
                                Toast.makeText(LoginActivity.this, "Đăng nhập không thành công: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (ApiException e) {
            // Đăng nhập không thành công, hiển thị thông báo lỗi
            Toast.makeText(LoginActivity.this, "Đăng nhập không thành công: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}