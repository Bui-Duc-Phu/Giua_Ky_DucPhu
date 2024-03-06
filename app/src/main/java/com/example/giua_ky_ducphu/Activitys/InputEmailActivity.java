package com.example.giua_ky_ducphu.Activitys;



import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.giua_ky_ducphu.databinding.ActivityInputEmailBinding;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;

public class InputEmailActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    String uri;
    ActivityInputEmailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInputEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init_();
    }

    private void init_() {
        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.sendOTP.setOnClickListener(v -> {
            String receiver = binding.emailEdt.getText().toString();
            creatOtp(receiver);
        });
    }

    private void creatOtp(String receiver) {
        String randomDigits = generateRandomDigits();
        sendOTP(receiver, randomDigits);
    }

    private void sendOTP(String receiver, String otp) {
        progressDialog = ProgressDialog.show(InputEmailActivity.this, "App", "Loading...", true);
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
                Intent intent = new Intent(InputEmailActivity.this, otpsendActivity.class);
                intent.putExtra("OTP", otp);
                intent.putExtra("receiver", receiver);
                progressDialog.dismiss();
                startActivity(intent);
                finish();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private static String generateRandomDigits() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
