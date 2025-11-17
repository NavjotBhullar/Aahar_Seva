package com.example.aahar100;

public class ReadWriteUserDetails {

    public String fullName;
    public String email;
    public String mobile;
    public String photoUrl;

    // ✅ Empty constructor (required for Firebase)
    public ReadWriteUserDetails() {
    }

    // ✅ Constructor with only mobile (for backward compatibility)
    public ReadWriteUserDetails(String mobile) {
        this.mobile = mobile;
    }

    // ✅ NEW: Full constructor with all fields
    public ReadWriteUserDetails(String fullName, String email, String mobile) {
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
    }
}