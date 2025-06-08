package com.utn.interactiveconsortium.batch;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.utn.interactiveconsortium.batch.steps.ConsortiumFeePartitioner;
import com.utn.interactiveconsortium.batch.wrapper.ConsortiumFeeWrapper;
import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ConsortiumFeeBatchConfig {

   private final EntityManagerFactory entityManagerFactory;

   private final ConsortiumRepository consortiumRepository;

   private final Integer CHUNK_SIZE = 100;

   @Bean("consortiumFeeReader")
   @StepScope
   public JpaPagingItemReader<ConsortiumEntity> consortiumFeeReader(@Value("#{stepExecutionContext['consortiumId']}") Long consortiumId) {
      LocalDate period = LocalDate.now().withDayOfMonth(1);

      Map<String, Object> parameterValues = new HashMap<>();
      parameterValues.put("consortiumId", consortiumId);
      parameterValues.put("period", period);

      return new JpaPagingItemReaderBuilder<ConsortiumEntity>()
            .name("consortiumFeeReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("""
                  SELECT c FROM ConsortiumEntity c
                  WHERE c.consortiumId = :consortiumId
                  AND EXISTS (
                      SELECT 1
                      FROM ConsortiumFeeConceptEntity cc
                      WHERE cc.active = true
                      AND cc.consortium.consortiumId = c.consortiumId
                  )
                  AND NOT EXISTS (
                      SELECT 1
                      FROM ConsortiumFeePeriodEntity cp
                      WHERE cp.consortium.consortiumId = c.consortiumId
                      AND cp.periodDate = :period
                  )
                  ORDER BY c.consortiumId ASC
                  """)
            .parameterValues(parameterValues)
            .pageSize(CHUNK_SIZE)
            .build();
   }

   @Bean
   public Step slaveStep(
         JobRepository jobRepository,
         PlatformTransactionManager transactionManager,
         JpaPagingItemReader<ConsortiumEntity> consortiumFeeReader,
         ItemProcessor<ConsortiumEntity, ConsortiumFeeWrapper> consortiumFeeProcessor,
         ItemWriter<ConsortiumFeeWrapper> consortiumFeeWriter
   ) {
      return new StepBuilder("slaveStep", jobRepository)
            .<ConsortiumEntity, ConsortiumFeeWrapper>chunk(CHUNK_SIZE, transactionManager)
            .reader(consortiumFeeReader)
            .processor(consortiumFeeProcessor)
            .writer(consortiumFeeWriter)
            .build();
   }

   @Bean
   public Partitioner consortiumFeePartitioner() {
      return new ConsortiumFeePartitioner(consortiumRepository);
   }

   @Bean
   public TaskExecutor consortiumFeeBatchTaskExecutor() {
      SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("spring_batch_");
      taskExecutor.setConcurrencyLimit(5);
      return taskExecutor;   }

   @Bean
   public Step masterStep(JobRepository jobRepository, Step slaveStep, Partitioner constiumFeePartitioner, TaskExecutor consortiumFeeBatchTaskExecutor) {
      return new StepBuilder("masterStep", jobRepository)
            .partitioner(slaveStep.getName() ,constiumFeePartitioner)
            .step(slaveStep)
            .gridSize(5)
            .taskExecutor(consortiumFeeBatchTaskExecutor)
            .build();

   }

   @Bean
   public Job consortiumFeeJob(JobRepository jobRepository, Step masterStep) {
      return new JobBuilder("consortiumFeeJob", jobRepository)
            .incrementer(new RunIdIncrementer()) // Para asegurar que cada ejecución tenga parámetros únicos
            .flow(masterStep)
            .end()
            .build();
   }

}
