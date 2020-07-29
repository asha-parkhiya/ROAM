package com.sparkle.roam;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "Login_data")
public class LoginData implements Parcelable {

    public LoginData() {
    }

    @ColumnInfo(name = "username")
    String Username;


    @ColumnInfo(name = "refreshtoken")
    String RefreshToken;


    @ColumnInfo(name = "password")
    String Password;

    @ColumnInfo(name = "token")
    String Token;

    @ColumnInfo(name = "usertype")
    String Usertype;

    @PrimaryKey(autoGenerate = true)

    @ColumnInfo(name = "id")
    int id;

    protected LoginData(Parcel in) {
        id = in.readInt();
        Username = in.readString();
        Password = in.readString();
        Token = in.readString();
        Usertype = in.readString();
        RefreshToken =in.readString();
    }

    public static final Creator<LoginData> CREATOR = new Creator<LoginData>() {
        @Override
        public LoginData createFromParcel(Parcel in) {
            return new LoginData(in);
        }

        @Override
        public LoginData[] newArray(int size) {
            return new LoginData[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }

    public String getUsertype() {
        return Usertype;
    }

    public void setUsertype(String usertype) {
        Usertype = usertype;
    }

    public String getRefreshToken() {
        return RefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        RefreshToken = refreshToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(Username);
        dest.writeString(Password);
        dest.writeString(Token);
        dest.writeString(Usertype);
        dest.writeString(RefreshToken);
    }
}
