package com.imtiaz_acedamy.apisecurity;

public class Domain {
    String id;
    String title;
    String encryptedData;

    public Domain(String id, String title, String encryptedData) {
        this.id = id;
        this.title = title;
        this.encryptedData = encryptedData;
    }

    public Domain() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }
}
