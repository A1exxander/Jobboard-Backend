package io.Jobboard.JobboardAPI;

import java.util.HashMap;
import java.util.LinkedList;

import io.Jobboard.Contract.Contract;
import io.Jobboard.User.*;

public final class View { // Monostate

    public View(){}

    public static HashMap<String, String> getUserData(final User user) {

        if (user == null){
            return error();
        }
        else {
            HashMap<String, String> jsonObj = new HashMap<>();
            jsonObj.put("ValidRequest", "TRUE");
            jsonObj.put("Email", user.getEmail());
            jsonObj.put("AccountType", user.getAccountTypeString());
            jsonObj.put("Token", user.getToken());
            return jsonObj;
        }

    }

    public static HashMap<String, Object> getJobContracts(LinkedList<Contract> contracts){
        HashMap<String, Object> jsonObj = new HashMap<>();
        jsonObj.put("ValidRequest", "TRUE");
        jsonObj.put("Contracts", contracts);
        return jsonObj;
    }

    public static HashMap<String, String> valid() {

        HashMap<String, String> jsonObj = new HashMap<>();
        jsonObj.put("ValidRequest", "TRUE");
        return jsonObj;

    }

    public static HashMap<String, String> error() {

        HashMap<String, String> jsonObj = new HashMap<>();
        jsonObj.put("ValidRequest", "FALSE");
        return jsonObj;

    }

    public static HashMap<String, Integer> createContract(final Integer jobID){
        HashMap<String, Integer> jsonObj = new HashMap<>();
        jsonObj.put("JobID", jobID);
        return jsonObj;
    }

}
