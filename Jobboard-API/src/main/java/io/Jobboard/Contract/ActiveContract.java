package io.Jobboard.Contract;

import java.time.LocalDateTime;

public class ActiveContract extends Contract {

    private String contractee;
    private LocalDateTime deadline;

    public ActiveContract(int jobID, String desc, String contractor, String contractee, int daysRemaining) {
        super(jobID, desc, contractor, daysRemaining);
        this.contractee = contractee;
        this.deadline = this.deadline.now();
        this.deadline = deadline.plusDays(daysRemaining);
    }

    public final LocalDateTime getDeadline() {
        return deadline;
    }

    public final void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public final String getContractee() {
        return contractee;
    }

    public final void setContractee(String contractee) {
        this.contractee = contractee;
    }
}
