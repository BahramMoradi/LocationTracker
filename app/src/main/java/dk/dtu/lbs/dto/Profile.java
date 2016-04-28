package dk.dtu.lbs.dto;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bahram on 09-12-2015.
 */
public class Profile extends RealmObject {
    @PrimaryKey
    @SerializedName("uid")
    private long uid;
    @SerializedName("name")
    private String name;
    @SerializedName("phone")
    private long phone;
    @SerializedName("mail")
    private String mail;
    @SerializedName("description")
    private String description;

    public Profile() {
        super();
    }

    public Profile(long uid, String name, long phone, String mail, String description) {
        super();
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.mail = mail;
        this.description = description;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
