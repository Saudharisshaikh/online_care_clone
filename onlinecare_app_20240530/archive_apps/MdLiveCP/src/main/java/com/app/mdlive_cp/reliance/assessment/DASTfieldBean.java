package com.app.mdlive_cp.reliance.assessment;

public class DASTfieldBean {


    public String question;
    public int Yes;
    public int No;

    public int scoreDerived;
    public boolean isGroupSelected;


    public DASTfieldBean(String question, int yes, int no) {
        this.question = question;
        Yes = yes;
        No = no;
    }
}