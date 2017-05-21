package com.sohu.bp.elite.api.api.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholastang on 2017/3/13.
 */
public class EcologyIndexData {
    private List<EcologyUser> talents;
    private List<EcologyQuestion> questions;

    public List<EcologyUser> getTalents() {
        return talents;
    }

    public void setTalents(List<EcologyUser> talents) {
        this.talents = talents;
    }

    public List<EcologyQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<EcologyQuestion> questions) {
        this.questions = questions;
    }
}
