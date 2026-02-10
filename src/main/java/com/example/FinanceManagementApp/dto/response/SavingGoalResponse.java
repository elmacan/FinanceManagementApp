package com.example.FinanceManagementApp.dto.response;

import com.example.FinanceManagementApp.model.entity.SavingGoal;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Data
public class SavingGoalResponse {

    private Long id;
    private String title;

    private BigDecimal targetAmount;
    private CurrencyType currency;

    private LocalDate targetDate;
    private Boolean completed;

    private BigDecimal currentAmount;
    private BigDecimal remainingAmount;
    private Integer goalProgressPercent;


    private Boolean overdue;


    public SavingGoalResponse(SavingGoal s,BigDecimal currentAmount) {
        this.id = s.getId();
        this.title = s.getTitle();
        this.targetAmount = s.getTargetAmount();
        this.currency = s.getCurrency();
        this.targetDate = s.getTargetDate();
        this.completed = s.getCompleted();

        if (currentAmount == null) {
            currentAmount = BigDecimal.ZERO;
        }

        this.currentAmount =currentAmount;

        this.remainingAmount = s.getTargetAmount()
                        .subtract(currentAmount)
                        .max(BigDecimal.ZERO);

        if (s.getTargetAmount() != null && s.getTargetAmount().signum() > 0) {
            this.goalProgressPercent = currentAmount
                    .multiply(BigDecimal.valueOf(100))
                    .divide(s.getTargetAmount(), 0, RoundingMode.HALF_UP)
                    .intValue();
        } else {
            this.goalProgressPercent = 0;
        }
        if (this.goalProgressPercent > 100) this.goalProgressPercent = 100;

        LocalDate today = LocalDate.now();

        this.overdue =
                s.getTargetDate() != null &&
                        s.getTargetDate().isBefore(LocalDate.now());

    }
}
