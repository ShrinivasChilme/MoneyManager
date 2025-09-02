package com.shrinivas.MoneyManager.controller;

import com.shrinivas.MoneyManager.dto.ExpenseDto;
import com.shrinivas.MoneyManager.dto.IncomeDto;
import com.shrinivas.MoneyManager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {
private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDto> addExpense(@RequestBody IncomeDto dto){
        IncomeDto saved=incomeService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @GetMapping
    public ResponseEntity<List<IncomeDto>>getExpenses(){
        List<IncomeDto>expenses=incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.ok(expenses);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id){
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }

}
