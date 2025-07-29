package com.utn.interactiveconsortium.batch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.utn.interactiveconsortium.service.BookingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BookingProcessingBatchConfig {

    private final BookingService bookingService;

    @Bean
//    public Tasklet bookingProcessingTasklet(@Value("#{jobParameters['dateTime']}") String dateTimeStr) {
    public Tasklet bookingProcessingTasklet() {
        String dateTimeStr = "tes";
        return (contribution, chunkContext) -> {
            log.info("Starting booking processing tasklet");
            
            LocalDateTime dateTime = null;
            if (dateTimeStr != null && !dateTimeStr.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    dateTime = LocalDateTime.parse(dateTimeStr, formatter);
                    log.info("Processing bookings with date and time: {}", dateTime);
                } catch (Exception e) {
                    log.error("Error parsing dateTime parameter: {}", dateTimeStr, e);
                }
            }
            
            // Process bookings with the provided dateTime or use current time if not provided
            bookingService.processBookings(dateTime);
            
            log.info("Booking processing completed successfully");
            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step bookingProcessingStep(JobRepository jobRepository, PlatformTransactionManager transactionManager, Tasklet bookingProcessingTasklet) {
        return new StepBuilder("bookingProcessingStep", jobRepository)
                .tasklet(bookingProcessingTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job bookingProcessingJob(JobRepository jobRepository, Step bookingProcessingStep) {
        return new JobBuilder("bookingProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(bookingProcessingStep)
                .end()
                .build();
    }
}