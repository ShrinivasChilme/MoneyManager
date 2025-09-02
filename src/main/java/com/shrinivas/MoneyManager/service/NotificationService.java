package com.shrinivas.MoneyManager.service;

import com.shrinivas.MoneyManager.entity.ProfileEntity;
import com.shrinivas.MoneyManager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class NotificationService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

   @Value("${money.manager.frontend.url}")
    private String frontendUrl;

   @Scheduled(cron = "0 0 22 * * *",zone="IST")
   public void sendDailyIncomeExpenseReminder(){
       log.info("Job started:sendDailyIncomeExpenseReminder()");
       List<ProfileEntity> profiles=profileRepository.findAll();
       for(ProfileEntity profile:profiles){
           String body="Hi "+profile.getFullName()+",<br><br>"
                   +"This is a friendly reminder to add your income and expenses for today in Money Manager.<br><br>"
                   +"<a href="+frontendUrl+" style='display:inline-block;padding:10px 20px;background=color:#4CAF50;color:#fff;text-decoration:none;border-redius:5px;font-weight:bold;'>Go to Money manager</a>"
                    + "<br><br>Best regards,<br> Money Manager Team";
        emailService.sendEmail(profile.getEmail(),"daily reminder: Add your income and expense",body);
       }
   }
}
