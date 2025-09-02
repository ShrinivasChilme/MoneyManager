package com.shrinivas.MoneyManager.service;

import com.shrinivas.MoneyManager.dto.ExpenseDto; // This must be correct

import com.shrinivas.MoneyManager.entity.CategoryEntity;
import com.shrinivas.MoneyManager.entity.ExpenseEntity;
import com.shrinivas.MoneyManager.entity.ProfileEntity;
import com.shrinivas.MoneyManager.repository.CategoryRepository;
import com.shrinivas.MoneyManager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Sort;


@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    public ExpenseDto addExpense(ExpenseDto dto) {
        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ExpenseEntity newExpense = toEntity(dto, profile, category);
        newExpense = expenseRepository.save(newExpense);

        return toDto(newExpense);
    }
    //retrive all expenses for the current month or based on start and end date
    public List<ExpenseDto>getCurrentMonthExpenseForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
       LocalDate startDate= now.withDayOfMonth(1);
      LocalDate endDate= now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity>list=  expenseRepository.findByProfileIdAndDateBetween(profile.getId(), startDate,endDate);
return list.stream().map(this::toDto).toList();
    }
    //delete expense by id for current user
    public void deleteExpense(Long expenseId){
        ProfileEntity profile=profileService.getCurrentProfile();
        ExpenseEntity entity=expenseRepository.findById(expenseId)
                .orElseThrow(()->new RuntimeException("Expense not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete the expense");

        }
        expenseRepository.delete(entity);
    }

    // get latest 5 expemses for current user
    public List<ExpenseDto>getLatest5ExpensesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<ExpenseEntity>list=expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();

    }
    //get total expemnses for current user
    public BigDecimal getTotalExpenseForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal total=expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total!=null?total:BigDecimal.ZERO;

    }
    //filter expenses
    public List<ExpenseDto>filterExpenses(LocalDate startDate,LocalDate endDate,String keyword,Sort sort){
    ProfileEntity profile=profileService.getCurrentProfile();
    List<ExpenseEntity>list =expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate,endDate,keyword,sort);
    return list.stream().map(this::toDto).toList();
    }
    public List<ExpenseDto>getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity>list=expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(this::toDto).toList();
    }
    //Notification
    public List<ExpenseDto>getExpenseForUserOnDate(Long profileId,LocalDate date){
        List<ExpenseEntity>list=expenseRepository.findByProfileIdAndDate(profileId,date);
        return list.stream().map(this::toDto).toList();
    }

    private ExpenseEntity toEntity(ExpenseDto dto, ProfileEntity profile, CategoryEntity category) {
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDto toDto(ExpenseEntity entity) {
        return ExpenseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : null)
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
