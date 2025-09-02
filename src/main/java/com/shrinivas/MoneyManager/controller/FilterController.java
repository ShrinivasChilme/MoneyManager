package com.shrinivas.MoneyManager.controller;

import com.shrinivas.MoneyManager.dto.ExpenseDto;
import com.shrinivas.MoneyManager.dto.FilterDto;
import com.shrinivas.MoneyManager.dto.IncomeDto;
import com.shrinivas.MoneyManager.service.ExpenseService;
import com.shrinivas.MoneyManager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class FilterController {
    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping("/filter")
    public ResponseEntity<?>filtertransactions(@RequestBody FilterDto filter){
        LocalDate startDate=filter.getStartDate()!=null?filter.getStartDate():LocalDate.MIN;
        LocalDate endDate=filter.getEndDate()!=null?filter.getEndDate():LocalDate.now();
       String keyword= filter.getKeyword()!=null?filter.getKeyword():" ";
       String sortField=filter.getSortField()!=null?filter.getSortField():"date";
        Sort.Direction direction="desc".equalsIgnoreCase(filter.getSortOrder())?Sort.Direction.DESC:Sort.Direction.ASC;
        Sort sort=Sort.by(direction,sortField);
        if("income".equals(filter.getType())){
            List<IncomeDto>incomes=incomeService.filterIncomes(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(incomes);

        }else if("expense".equals(filter.getType())){
            List<ExpenseDto>expenses=expenseService.filterExpenses(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(expenses);

        }
        else{
            return ResponseEntity.badRequest().body("Invalid type.Must be 'income' or 'expense'");
        }
    }
}
