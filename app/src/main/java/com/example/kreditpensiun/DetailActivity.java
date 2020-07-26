package com.example.kreditpensiun;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailActivity extends AppCompatActivity {
    private CircleImageView ivPhoto;
    private EditText etNik, etNama, etTgl, etAlamat, etNotlp, etGaji, etRespon;
    private RadioGroup rgStatus;
    private RadioButton rbAktif, rbPensiun;
    private Button btnUpdate, btnDelete;
    private Spinner spinPembayaran;
    private Sales paketSales;
    private ProgressDialog dialog;
    private Bitmap photo;
    private String imageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_sales);

        paketSales = getIntent().getParcelableExtra("sales");

        init();
    }

    private void init() {

        ivPhoto = findViewById(R.id.iv_detail_photo);
        etNama = findViewById(R.id.et_detail__nama);
        etNik = findViewById(R.id.et_detail__nik);
        etTgl = findViewById(R.id.et_detail_tgl_lahir);
        etAlamat = findViewById(R.id.et_detail_alamat);
        etNotlp = findViewById(R.id.et_detail_notlp);
        etGaji = findViewById(R.id.et_detail_gaji);
        etRespon = findViewById(R.id.et_detail_respon);
        rgStatus = findViewById(R.id.rg_detail_status);
        spinPembayaran = findViewById(R.id.spin_detail_gaji);
        rbAktif = findViewById(R.id.rb_detail_aktif);
        rbPensiun = findViewById(R.id.rb_detail_pensiun);
        btnDelete = findViewById(R.id.btn_detail_delete);
        btnUpdate = findViewById(R.id.btn_detail_update);

        etTgl.setFocusable(false);
        etTgl.setFocusableInTouchMode(false);
        etTgl.setClickable(true);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        fillingView();

        etTgl.setOnClickListener(v -> datePicker());
        ivPhoto.setOnClickListener(v -> pickImage());
        btnDelete.setOnClickListener(v -> deleteData());
        btnUpdate.setOnClickListener(v -> updateData());



    }

    private void datePicker() {
        int mYear, mMonth, mDay;
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    etTgl.setText(dayOfMonth + "/"+ (month + 1) + "/" + year);
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
                }
                ivPhoto.setImageBitmap(photo);
                Toast.makeText(this,imageString, Toast.LENGTH_SHORT).show();
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

    private void updateData() {
        dialog.setMessage("Loading...");
        dialog.show();
        StringRequest request = new StringRequest(StringRequest.Method.POST, Api.UPDATE_ITEM, response -> {
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
                    sales.setNo_tlp(salesObj.getString("no_tlp"));
                    sales.setStatus(salesObj.getString("status"));
                    sales.setGaji(salesObj.getLong("gaji"));
                    sales.setPembayaran(salesObj.getString("pembayaran"));
                    sales.setRespon(salesObj.getString("respon"));
                    sales.setPhoto(salesObj.getString("photo"));

                    MainActivity.salesArrayList.set(paketSales.getPosition(), sales);
                    MainActivity.rvSales.getAdapter().notifyDataSetChanged();
                    finish();
                    dialog.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialog.dismiss();
            }
        },error -> {
            dialog.dismiss();
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("id",paketSales.getId()+"");
                map.put("nik",etNik.getText().toString());
                map.put("nama",etNama.getText().toString());
                map.put("tgl_lahir",etTgl.getText().toString());
                map.put("no_tlp",etNotlp.getText().toString());
                map.put("alamat",etAlamat.getText().toString());
                map.put("gaji",etGaji.getText().toString());
                map.put("pembayaran",spinPembayaran.getSelectedItem().toString());
                map.put("status",getStatus());
                map.put("respon",etRespon.getText().toString());
                if (photo != null){
                    map.put("photo",bitmapToString(photo));
                }
                return map;

            }
        };
        Toast.makeText(this, imageString, Toast.LENGTH_SHORT).show();
        Volley.newRequestQueue(getApplicationContext()).add(request);
    }

    private void deleteData() {
        dialog.setMessage("Loading...");
        dialog.show();
        StringRequest request = new StringRequest(StringRequest.Method.POST, Api.DELETE_ITEM, response -> {
            MainActivity.salesArrayList.remove(paketSales.getPosition());
            MainActivity.rvSales.getAdapter().notifyItemRemoved(paketSales.getPosition());
            MainActivity.rvSales.getAdapter().notifyItemRangeChanged(paketSales.getPosition(), MainActivity.salesArrayList.size());
            finish();
            dialog.dismiss();
        },error -> {
            error.printStackTrace();
            dialog.dismiss();
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("id", paketSales.getId()+"");
                map.put("photo", paketSales.getPhoto());
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

    private void fillingView() {
        Glide.with(getApplicationContext())
                .load(Api.BASE + "img/" + paketSales.getPhoto())
                .apply(new RequestOptions().fitCenter())
                .into(ivPhoto);
        etNama.setText(paketSales.getNama());
        etNik.setText(paketSales.getNik());
        Toast.makeText(this, paketSales.getTgl_lahir(), Toast.LENGTH_SHORT).show();
        etTgl.setText(paketSales.getTgl_lahir());
        etAlamat.setText(paketSales.getAlamat());
        etNotlp.setText(paketSales.getNo_tlp());
        etNotlp.setEnabled(false);
        etGaji.setText(String.valueOf(paketSales.getGaji()));
        etRespon.setText(paketSales.getRespon());

        //Spinner
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,R.array.list_bank,R.layout.support_simple_spinner_dropdown_item);
        adapterSpinner.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinPembayaran.setAdapter(adapterSpinner);
        int index = adapterSpinner.getPosition(paketSales.getPembayaran());
        spinPembayaran.setSelection(index);

        //Radio Button
        if (paketSales.getStatus().equals(rbAktif.getText().toString())){
            rbAktif.setChecked(true);
        }
        if (paketSales.getStatus().equals(rbPensiun.getText().toString())){
            rbPensiun.setChecked(true);
        }
    }
}