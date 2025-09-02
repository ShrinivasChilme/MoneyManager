package com.shrinivas.MoneyManager.repository;

import com.shrinivas.MoneyManager.entity.ExpenseEntity;
import com.shrinivas.MoneyManager.entity.IncomeEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<IncomeEntity,Long> {
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(i.amount)FROM IncomeEntity i WHERE i.profile.id=:profileId")
    BigDecimal findTotalIncomesByProfileId(@Param("profileId") Long profileId);

    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            Long profileId,
            LocalDate startDate,
            LocalDate endDate,
            String keyword,
            Sort sort

    );

    List<IncomeEntity> findByProfileIdAndDateBetween(
            long profileId,
            LocalDate startDate,
            LocalDate endDate
    );


}