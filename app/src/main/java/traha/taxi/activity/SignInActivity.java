package traha.taxi.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import traha.taxi.R;
import traha.taxi.Utils.Constants;
import traha.taxi.Utils.UserModel;
import traha.taxi.Utils.Utility;
import traha.taxi.databinding.ActivitySignUpBinding;
import traha.taxi.models.LoginModel;
import traha.taxi.models.Result;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySignUpBinding binding;
    private Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        mContext = this;
        binding.tvSign.setMovementMethod(LinkMovementMethod.getInstance());

        Spannable wordtoSpan = new SpannableString("Don't have an account? Sign up now");


        wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 22, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mustard_yellow)), 22, 34, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {

                startActivity(new Intent(mContext, RegistrationActivity.class));
                finish();

            }
        }, 23, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.tvSign.setText(wordtoSpan);
        binding.btnLoginwithfb.setOnClickListener(this);
        binding.signIn.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        if (v == binding.signIn) {
            if (binding.etPhoneNumber.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "Enter phone no.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding.etPassword.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "Enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            if (binding.etPhoneNumber.getText().toString().length() < 10) {
                Toast.makeText(mContext, "Please enter valid mobile no.", Toast.LENGTH_SHORT).show();
                return;
            }
       //     String isoCode = UserModel.getInstance().isoCode;
           // Utility.showProgressHUD(mContext, "Login", "Please Wait...");
            HashMap<String, Object> map = new HashMap<>();
            map.put("phone_number",binding.etPhoneNumber.getText());
            map.put("password",binding.etPassword.getText());
            Call<LoginModel> call = Constants.service.userLogin(UserModel.getInstance().Header,map);
            call.enqueue(new Callback<LoginModel>() {
                @Override
                public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                    Utility.hideProgressHud();
                    if (response.isSuccessful()){
                        LoginModel user = response.body();
                        if (user != null) {
                            if (user.status.equalsIgnoreCase("success")) {
                                Utility.setBooleanPreferences(mContext, "isLogin", true);
                                UserModel.getInstance().firstName = user.result.firstName;
                                UserModel.getInstance().lastame = user.result.lastName;
                                UserModel.getInstance().phoneNumber = user.result.phoneNumber;
                                UserModel.getInstance().userId = user.result.userId;
                                UserModel.getInstance().email = user.result.email;
                                Utility.setStringPreferences(mContext, "userinfo", new Gson().toJson(user));
                                startActivity(new Intent(mContext, HomeActivity.class));
                                finish();

                            } else {
                                Toast.makeText(SignInActivity.this, "" + user.message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(SignInActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                    }
                    }

                @Override
                public void onFailure(Call<LoginModel> call, Throwable t) {
                    t.printStackTrace();
                }

            });


        }
        if (v == binding.btnLoginwithfb) {

        }
    }
}
