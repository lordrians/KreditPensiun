package com.example.kreditpensiun;

import android.os.Parcel;
import android.os.Parcelable;

public class Sales implements Parcelable {
    int id,position;
    private long  gaji;
    private String photo, nama, alamat, tgl_lahir, status, pembayaran, respon, no_tlp,nik;

    public Sales() {
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNo_tlp() {
        return no_tlp;
    }

    public void setNo_tlp(String no_tlp) {
        this.no_tlp = no_tlp;
    }

    public long getGaji() {
        return gaji;
    }

    public void setGaji(long gaji) {
        this.gaji = gaji;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTgl_lahir() {
        return tgl_lahir;
    }

    public void setTgl_lahir(String tgl_lahir) {
        this.tgl_lahir = tgl_lahir;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPembayaran() {
        return pembayaran;
    }

    public void setPembayaran(String pembayaran) {
        this.pembayaran = pembayaran;
    }

    public String getRespon() {
        return respon;
    }

    public void setRespon(String respon) {
        this.respon = respon;
    }

    public static Creator<Sales> getCREATOR() {
        return CREATOR;
    }

    protected Sales(Parcel in) {
        nik = in.readString();
        id = in.readInt();
        position = in.readInt();
        no_tlp = in.readString();
        gaji = in.readLong();
        photo = in.readString();
        nama = in.readString();
        alamat = in.readString();
        tgl_lahir = in.readString();
        status = in.readString();
        pembayaran = in.readString();
        respon = in.readString();
    }

    public static final Creator<Sales> CREATOR = new Creator<Sales>() {
        @Override
        public Sales createFromParcel(Parcel in) {
            return new Sales(in);
        }

        @Override
        public Sales[] newArray(int size) {
            return new Sales[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nik);
        dest.writeInt(id);
        dest.writeInt(position);
        dest.writeString(no_tlp);
        dest.writeLong(gaji);
        dest.writeString(photo);
        dest.writeString(nama);
        dest.writeString(alamat);
        dest.writeString(tgl_lahir);
        dest.writeString(status);
        dest.writeString(pembayaran);
        dest.writeString(respon);
    }
}
