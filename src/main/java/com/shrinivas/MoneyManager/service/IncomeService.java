package com.shrinivas.MoneyManager.service;

import com.shrinivas.MoneyManager.dto.ExpenseDto;
import com.shrinivas.MoneyManager.dto.IncomeDto;
import com.shrinivas.MoneyManager.entity.CategoryEntity;
import com.shrinivas.MoneyManager.entity.ExpenseEntity;
import com.shrinivas.MoneyManager.entity.IncomeEntity;
import com.shrinivas.MoneyManager.entity.ProfileEntity;
import com.shrinivas.MoneyManager.repository.CategoryRepository;
import com.shrinivas.MoneyManager.repository.ExpenseRepository;
import com.shrinivas.MoneyManager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final IncomeRepository incomeRepository;
    private final CategoryService categoryService;
private final ProfileService profileService;
private final CategoryRepository categoryRepository;

    public IncomeDto addIncome(IncomeDto dto){
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity category=categoryRepository.findById(Long.valueOf(dto.getCategoryId()))
                .orElseThrow(()->new RuntimeException("Category not found"));
        IncomeEntity newExpense=toEntity(dto,profile,category);
        newExpense=incomeRepository.save(newExpense);
        return toDto(newExpense);
    }
    //retrive all incomes
    //retrive all expenses for the current month or based on start and end date
    public List<IncomeDto>getCurrentMonthIncomeForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate= now.withDayOfMonth(1);
        LocalDate endDate= now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity>list=  incomeRepository.findByProfileIdAndDateBetween(profile.getId(), startDate,endDate);
        return list.stream().map(this::toDto).toList();
    }

    public List<IncomeDto> getCurrentMonthIncomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        LocalDate now=LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity>list=incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map(this::toDto).toList();
    }
    //delete income by id for current user
    public void deleteIncome(Long incomeId){
        ProfileEntity profile=profileService.getCurrentProfile();
        IncomeEntity entity=incomeRepository.findById(incomeId)
                .orElseThrow(()->new RuntimeException("Income not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("Unauthorized to delete the income");

        }
        incomeRepository.delete(entity);
    }

    // get latest 5 incomes for current user
    public List<IncomeDto>getLatest5IncomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity>list=incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();

    }
    //get total incomes for current user
    public BigDecimal getTotalincomesForCurrentUser(){
        ProfileEntity profile=profileService.getCurrentProfile();
        BigDecimal total=incomeRepository.findTotalIncomesByProfileId(profile.getId());
        return total!=null?total:BigDecimal.ZERO;


    }
    //filter incomes
    public List<IncomeDto>filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profile=profileService.getCurrentProfile();
        List<IncomeEntity>list =incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(), startDate,endDate,keyword,sort);
        return list.stream().map(this::toDto).toList();
    }
    //helper method
    private IncomeEntity toEntity(IncomeDto dto, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount()) // Already BigDecimal
                .date(dto.getDate())
// may need LocalDate/LocalDateTime parsing depending on entity
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDto toDto(IncomeEntity entity) {
        return IncomeDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .amount(entity.getAmount()) // BigDecimal -> String
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }


}
