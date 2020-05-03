package com.thesingleapp.thesingleapp.model;

/**
 * Created by rajan on 10/15/18.
 */

public class Help_model {

    private String help;
    private String answer;



    public Help_model(){

    }

    public Help_model(String help, String answer ) {
        this.help = help;
        this.answer = answer;

    }



    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
