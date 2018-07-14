package traha.taxi.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
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

import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import traha.taxi.R;
import traha.taxi.Utils.Constants;
import traha.taxi.Utils.UserModel;
import traha.taxi.Utils.Utility;
import traha.taxi.databinding.ActivityRegistrationBinding;

public class RegistrationActivity extends AppCompatActivity {


    private ActivityRegistrationBinding binding;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        WindowManager.LayoutParams winParams = window.getAttributes();
        winParams.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        window.setAttributes(winParams);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_registration);
        mContext = this;
        binding.tvSign.setMovementMethod(LinkMovementMethod.getInstance());



        Spannable wordtoSpan = new SpannableString("Already have an account? Log in now");


        wordtoSpan.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 25, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mustard_yellow)),25,35,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(true);
            }

            @Override
            public void onClick(View widget) {
                openLogin();
            }
        }, 25, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        binding.tvSign.setText(wordtoSpan);

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.etUserName.getText().toString().isEmpty()){
                    Toast.makeText(mContext, "Please enter Username.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (binding.etPhoneNumber.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, "Please enter your mobile no.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!checkValidation()) {
                    Toast.makeText(mContext, "Please enter valid mobile no.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (binding.etPassword.getText().toString().isEmpty()){
                    Toast.makeText(mContext, "Please enter your password", Toast.LENGTH_SHORT).show();

                }
                if (binding.etPassword.getText().toString().length()>6){
                    Toast.makeText(mContext, "Please enter more than six words.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Utility.showProgressHUD(mContext, "Register", "Please Wait...");

                HashMap<String, Object> map = new HashMap<>();
                map.put("username",binding.etUserName.getText().toString());
                map.put("phone_number",binding.etPhoneNumber.getText().toString());
                map.put("password",binding.etPassword.getText().toString());
                Call<ResponseBody> call = Constants.service.userReg(UserModel.getInstance().Header,map);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Utility.hideProgressHud();
                        if (response.isSuccessful()){
                            openLogin();
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Utility.hideProgressHud();

                    }
                });
            }
        });
    }

    private void openLogin(){
        startActivity(new Intent(mContext,SignInActivity.class));
        finish();
    }

    private boolean checkValidation() {
        String input = binding.etPhoneNumber.getText().toString();
        if (input.contains("@")) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches();
        } else {
            return android.util.Patterns.PHONE.matcher(input).matches();
        }
    }
}
