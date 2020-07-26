package com.example.kreditpensiun;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateSalesActivity extends AppCompatActivity {
    private CircleImageView ivPhoto;
    private CountDownTimer timer;
    private TextView tvTimer;
    private EditText etNik, etNama, etTglLahir, etAlamat, etNotlp, etGaji, etRespon, kodeOtp;
    private RadioGroup rgStatus;
    private RadioButton rbAktif, rbPensiun;
    private Spinner spinBank;
    private Button btnOtp, btnSimpan;
    private FirebaseAuth mAuth;
    private String phoneNumber, imageString, VerificationCodeBySystem;
    private ProgressDialog dialog;
    private Bitmap photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sales);

        init();

    }

    private void init() {
        ivPhoto = findViewById(R.id.iv_create_photo);
        etNik = findViewById(R.id.et_create_nik);
        etNama = findViewById(R.id.et_create_nama);
        etTglLahir = findViewById(R.id.et_create_tgl_lahir);
        etAlamat = findViewById(R.id.et_create_alamat);
        etGaji = findViewById(R.id.et_create_gaji);
        etRespon = findViewById(R.id.et_create_respon);
        etNotlp = findViewById(R.id.et_create_notlp);
        kodeOtp = findViewById(R.id.et_create_otp);
        rgStatus = findViewById(R.id.rg_create_status);
        rbAktif = findViewById(R.id.rb_create_aktif);
        rbPensiun = findViewById(R.id.rb_create_pensiun);
        spinBank = findViewById(R.id.spin_create_gaji);
        btnOtp = findViewById(R.id.btn_kirim_kode);
        btnSimpan = findViewById(R.id.btn_create_simpan);
        tvTimer = findViewById(R.id.et_create_detik);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();

        etTglLahir.setFocusable(false);
        etTglLahir.setFocusableInTouchMode(false);
        etTglLahir.setClickable(true);

        etGaji.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                etGaji.removeTextChangedListener(this);

                String original = s.toString();
                long longNumber;
                if (original.contains(",")){
                    original = original.replaceAll(",","");
                }

                longNumber = Long.parseLong(original);

                DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                formatter.applyPattern("#,###,###,###");
                String formattedString = formatter.format(longNumber);

                etGaji.setText(formattedString);
                etGaji.setSelection(etGaji.getText().length());
                etGaji.addTextChangedListener(this);
            }
        });
        kodeOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                cekKode(s.toString());
            }
        });
        etTglLahir.setOnClickListener(v -> datePick());
        ivPhoto.setOnClickListener(v -> pickImage());
        btnOtp.setOnClickListener(v -> {
            timer = new CountDownTimer(60*1000,1000) {
                @SuppressLint("DefaultLocale")
                @Override
                public void onTick(long millisUntilFinished) {
                    tvTimer.setText(String.format("%02d:%02d", (millisUntilFinished/1000)%3600/60, (millisUntilFinished/1000)%60));
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onFinish() {
                    tvTimer.setText("please resend code");
                    timer.cancel();
                }
            }.start();
            sendVerificationCodeToUser(etNotlp.getText().toString());
        });
        btnSimpan.setOnClickListener(v -> create());
    }

    private void datePick() {
        int mYear, mMonth, mDay;
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    etTglLahir.setText(dayOfMonth + "/"+ (month + 1) + "/" + year);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void pickImage() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture,12);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12) {
            if (resultCode == RESULT_OK) {
                Uri imageURI = data.getData();
                photo = (Bitmap) data.getExtras().get("data");
                if (imageURI != null) {
                    photo = Bitmap.createScaledBitmap(photo, 200, 200, true);
                    imageString = bitmapToString(photo);

                    Toast.makeText(this,imageString, Toast.LENGTH_SHORT).show();
                }
                ivPhoto.setImageBitmap(photo);
            }
        }


    }

    private String bitmapToString(Bitmap bitPhoto){
        if (bitPhoto != null){
            bitPhoto = Bitmap.createScaledBitmap(bitPhoto, 200, 200, true);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitPhoto.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageByte = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imageByte, Base64.DEFAULT);
        } else {
            return "";
        }
    }

    private void create() {
        String status = spinBank.getSelectedItem().toString();

        dialog.setMessage("Loading...");
        dialog.show();

        Toast.makeText(this, getStatus(), Toast.LENGTH_SHORT).show();
        StringRequest request = new StringRequest(StringRequest.Method.POST, Api.INSERT_ITEM, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")){
                    JSONObject salesObj = object.getJSONObject("data");
                    Sales sales = new Sales();
                    sales.setId(salesObj.getInt("id"));
                    sales.setNik(salesObj.getString("nik"));
                    sales.setNama(salesObj.getString("nama"));
                    sales.setTgl_lahir(salesObj.getString("tgl_lahir"));
                    sales.setAlamat(salesObj.getString("alamat"));
                    sales.setStatus(salesObj.getString("status"));
                    sales.setGaji(salesObj.getLong("gaji"));
                    sales.setPembayaran(salesObj.getString("pembayaran"));
                    sales.setRespon(salesObj.getString("respon"));
                    sales.setPhoto(salesObj.getString("photo"));

                    MainActivity.salesArrayList.add(sales);
                    MainActivity.rvSales.getAdapter().notifyDataSetChanged();
                    Toast.makeText(this, "Sukses", Toast.LENGTH_SHORT).show();
                    finish();
                }
                dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
            }

        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("nik",etNik.getText().toString());
                map.put("position",etNama.getText().toString());
                map.put("nama",etNama.getText().toString());
                map.put("tgl_lahir",etTglLahir.getText().toString());
                map.put("alamat",etAlamat.getText().toString());
                map.put("no_tlp",etNotlp.getText().toString());
                map.put("status",getStatus());
                map.put("gaji",etGaji.getText().toString());
                map.put("pembayaran",status);
                map.put("respon",etRespon.getText().toString());
                map.put("photo", bitmapToString(photo));
                return map;

            }
        };

        Volley.newRequestQueue(getApplicationContext()).add(request);

    }

    private String getStatus() {
        int radioButtonID = rgStatus.getCheckedRadioButtonId();
        View radioButton = rgStatus.findViewById(radioButtonID);
        int idx = rgStatus.indexOfChild(radioButton);
        RadioButton r = (RadioButton) rgStatus.getChildAt(idx);
        String selectedtext = r.getText().toString();

        return selectedtext;
    }

    private void cekKode(String codeVer) {
//        String codeVer = kodeOtp.getText().toString();

        if (codeVer.isEmpty() || codeVer.length() < 6){
            kodeOtp.setError("Wrong OTP");
            kodeOtp.requestFocus();
            return;
        }

        verifyCode(codeVer);
    }

    private void sendVerificationCodeToUser(String number) {

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(number,"ID");
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164),
                    60,
                    TimeUnit.SECONDS,
                    TaskExecutors.MAIN_THREAD,
                    mCallbacks);
        } catch (NumberParseException e) {
            e.printStackTrace();
            Log.d("errormsg","Error teks =" + e.getMessage());
        }


    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            VerificationCodeBySystem = s;

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code!= null){
//                kodeOtp.setText(code);
//                signInByCredential(phoneAuthCredential);
                kodeOtp.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

            Log.d("errormsg","Error teks =" + e.getMessage());
            Toast.makeText(CreateSalesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationCodeBySystem,code);
        signInByCredential(credential);
    }

    private void signInByCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(CreateSalesActivity.this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            kodeOtp.setFocusableInTouchMode(false);
                            kodeOtp.setFocusable(false);
                            kodeOtp.setEnabled(false);
                            timer.cancel();
                            tvTimer.setText("Verified");
                            tvTimer.setTextColor(getColor(R.color.colorPrimary));
                            Toast.makeText(CreateSalesActivity.this, "Sukses Verifikasi", Toast.LENGTH_SHORT).show();
                        } else {
                            timer.cancel();
                            tvTimer.setText("resend your code");
                            Toast.makeText(CreateSalesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    private void sendVerification(String phoneNumber) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,
//                60,
//                TimeUnit.SECONDS,
//                TaskExecutors.MAIN_THREAD,
//                mCallback
//        );
//    }
//
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
//            mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        @Override
//        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//            String code = phoneAuthCredential.getSmsCode();
//            if (code != null){
//                verifyCode(code);
//            }
//        }
//
//        @Override
//        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//            super.onCodeSent(s, forceResendingToken);
//            verificationId = s;
//        }
//
//        @Override
//        public void onVerificationFailed(FirebaseException e) {
//            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//        }
//    };
//
//    private void verifyCode(String code) {
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
//        signInWithCredential(credential);
//    }
//
//    private void signInWithCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()){
//                        Toast.makeText(getApplicationContext(), "Telah terverifikasi", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Belum terverifikasi", Toast.LENGTH_LONG).show();
//                    }
//                });
//    }
//
//    private void requestOtp() {
//
//        phoneNumber = etNotlp.getText().toString();
//        sendVerification(phoneNumber);
//    }
}