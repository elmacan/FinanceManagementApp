package com.example.FinanceManagementApp.service.impl;
//impl yapılcak

import com.example.FinanceManagementApp.dto.request.SavingEntryRequest;
import com.example.FinanceManagementApp.dto.request.SavingGoalRequest;
import com.example.FinanceManagementApp.dto.request.SavingGoalUpdateRequest;
import com.example.FinanceManagementApp.dto.response.SavingEntryResponse;
import com.example.FinanceManagementApp.dto.response.SavingGoalResponse;
import com.example.FinanceManagementApp.exception.ApiException;
import com.example.FinanceManagementApp.model.entity.SavingEntry;
import com.example.FinanceManagementApp.model.entity.SavingGoal;
import com.example.FinanceManagementApp.model.enums.CurrencyType;
import com.example.FinanceManagementApp.repository.SavingEntryRepo;
import com.example.FinanceManagementApp.repository.SavingGoalRepo;
import com.example.FinanceManagementApp.security.CurrentUserPrincipal;
import com.example.FinanceManagementApp.service.SavingGoalService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SavingGoalServiceImpl implements SavingGoalService {

    private final SavingGoalRepo savingGoalRepo;
    private final SavingEntryRepo entryRepo;
    private final CurrencyServiceImpl currencyService;

    @Transactional
    @Override
    public SavingGoalResponse create(CurrentUserPrincipal p, @Valid SavingGoalRequest dto) {

        SavingGoal savingGoal = new SavingGoal();
        savingGoal.setTitle(dto.getTitle());
        savingGoal.setTargetAmount(dto.getTargetAmount());
        savingGoal.setCurrency(dto.getCurrency());
        savingGoal.setTargetDate(dto.getTargetDate());
        savingGoal.setCompleted(false);
        savingGoal.setUser(p.getUser());

        SavingGoal saved = savingGoalRepo.save(savingGoal);

       BigDecimal currentAmount = BigDecimal.ZERO;

        return new SavingGoalResponse(saved,currentAmount);


    }



    @Override
    public SavingGoalResponse get(CurrentUserPrincipal p, Long id) {
            SavingGoal goal=getGoal(p,id);
            BigDecimal current = sumProgress(goal,p);
            return new SavingGoalResponse(goal,current);
    }

    @Transactional
    @Override
    public SavingGoalResponse update(CurrentUserPrincipal principal, Long id, @Valid SavingGoalUpdateRequest dto) {

        SavingGoal g = getGoal(principal, id);
        if (dto.getTitle() != null && !dto.getTitle().isBlank()) g.setTitle(dto.getTitle());
        if (dto.getTargetAmount() != null) g.setTargetAmount(dto.getTargetAmount());
        if (dto.getTargetDate() != null) g.setTargetDate(dto.getTargetDate());

        SavingGoal saved = savingGoalRepo.save(g);
        updateCompletion(saved,principal);
        BigDecimal current = entryRepo.sumProgress(saved.getId(), principal.getId());

        return new SavingGoalResponse(saved, current);

    }



    @Override
    public List<SavingGoalResponse> list(CurrentUserPrincipal p) {
        return savingGoalRepo.findByUser_Id(p.getId())
                .stream()
                .map(g -> new SavingGoalResponse(g, sumProgress(g,p)))
                .toList();

    }

    @Transactional
    @Override
    public SavingEntryResponse addEntry(CurrentUserPrincipal p,
                                        Long goalId,
                                        SavingEntryRequest dto) {

        SavingGoal goal = getGoal(p,goalId);

        CurrencyType from = dto.getCurrency();
        CurrencyType to = goal.getCurrency();

        BigDecimal rate = currencyService.getRate(from,to);
        BigDecimal converted = currencyService.convert(dto.getAmount(),from,to);

        SavingEntry e = new SavingEntry();
        e.setOriginalAmount(dto.getAmount());
        e.setOriginalCurrency(from);
        e.setConvertedAmount(converted);
        e.setGoalCurrency(to);
        e.setRate(rate);
        e.setDate(LocalDate.now());
        e.setGoal(goal);

        goal.getSavingEntryList().add(e);

        SavingEntry saved = entryRepo.save(e);

        updateCompletion(goal,p);

        return new SavingEntryResponse(saved);
    }

    @Override
    public List<SavingEntryResponse> listEntries(CurrentUserPrincipal p, Long goalId) {

        getGoal(p,goalId);

        return entryRepo
                .findByGoal_IdAndGoal_User_Id(goalId,p.getId())
                .stream()
                .map(SavingEntryResponse::new)
                .toList();
    }





    private SavingGoal getGoal(CurrentUserPrincipal p, Long id) {
        return savingGoalRepo.findByIdAndUser_Id(id,p.getId())
                .orElseThrow(() ->
                        new ApiException(HttpStatus.NOT_FOUND,"SavingGoal not found"));
    }

    private BigDecimal sumProgress(SavingGoal g, CurrentUserPrincipal p) {
        return entryRepo.sumProgress(g.getId(), p.getId());
    }

    private void updateCompletion(SavingGoal goal, CurrentUserPrincipal p) {

        if (goal.getCompleted()) {
            return;
        }

        BigDecimal current = sumProgress(goal,p);
        if (current.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setCompleted(true);
            savingGoalRepo.save(goal);
        }
    }



}
