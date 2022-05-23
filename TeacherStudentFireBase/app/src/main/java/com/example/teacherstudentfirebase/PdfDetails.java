package com.example.teacherstudentfirebase;

public class PdfDetails {
    String studentName,name, url;

    public PdfDetails(){}
    public PdfDetails(String studentName, String name, String url) {
        this.studentName = studentName;
        this.name = name;
        this.url = url;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
