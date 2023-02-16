package io.Jobboard.Contract;

public class CompletedContract extends ActiveContract{

    private String completionURL;

    public CompletedContract(int jobID, String desc, String contractor, String contractee, String completionURL) {
        super(jobID, desc, contractor, contractee, 0);
        this.completionURL = completionURL;
    }

    public final String getCompletionURL() {
        return completionURL;
    }

    public final void setCompletionURL(String completionURL) {
        this.completionURL = completionURL;
    }

}
