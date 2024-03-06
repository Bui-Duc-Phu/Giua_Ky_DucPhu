package com.example.giua_ky_ducphu.Activitys;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.giua_ky_ducphu.databinding.ActivityOtpsendBinding;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class otpsendActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ActivityOtpsendBinding binding;
    String OTP;
    String receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpsendBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        OTP = getIntent().getStringExtra("OTP");
        receiver = getIntent().getStringExtra("receiver");
        init_();
    }

    private void init_() {
        binding.xacNhanOTPBtn.setOnClickListener(v -> oTPProcessing());
        binding.guiLaiTv.setOnClickListener(v -> creatOtp(receiver));
    }

    private void oTPProcessing() {
        String otp = binding.pinview.getText().toString();
        if (TextUtils.isEmpty(otp) || otp.length() < 6) {
            binding.pinview.setError("Bạn chưa nhập mã OTP");
        } else {
            if (otp.equals(OTP)) {
                startActivity(new Intent(otpsendActivity.this, ForgotPasswordActivity.class)
                        .putExtra("receiver", receiver));
            } else {
                binding.pinview.setText("");
                Toast.makeText(getApplicationContext(), "OTP không chính xác", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void creatOtp(String receiver) {
        progressDialog = ProgressDialog.show(otpsendActivity.this, "App", "Loading...", true);
        String randomDigits = String.valueOf(new Random().nextInt(900000) + 100000);
        sendOTP(receiver, randomDigits);
    }

    private void sendOTP(String receiver, String otp) {
        String stringSenderEmail = "firebase683@gmail.com";
        String stringReceiverEmail = receiver;
        String stringPasswordSenderEmail = "fbpx bpkb exaa acgv";
        String stringHost = "smtp.gmail.com";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", stringHost);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));
            mimeMessage.setSubject("send otp:");
            mimeMessage.setText("OTP : " + otp);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(() -> {
            try {
                Transport.send(mimeMessage);
                Intent intent = new Intent(otpsendActivity.this, otpsendActivity.class);
                intent.putExtra("OTP", otp);
                progressDialog.dismiss();
                startActivity(intent);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
