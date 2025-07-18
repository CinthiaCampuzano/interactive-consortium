package com.utn.interactiveconsortium.batch.services;

import static com.fasterxml.jackson.databind.MapperFeature.BLOCK_UNSAFE_POLYMORPHIC_BASE_TYPES;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.BatchConfigurationException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.JobOperatorFactoryBean;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.ExecutionContextSerializer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.Jackson2ExecutionContextStringSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
public class JobConfig extends DefaultBatchConfiguration {

   @Value("${spring.batch.job.max-pool-size:5}")
   private int maxPoolSize;

   @Value("${spring.batch.job.core-pool-size:5}")
   private int corePoolSize;

   private DataSource dataSource;

   @PersistenceContext
   private EntityManager entityManager;

   @Autowired(required = false)
   public void setDataSource(DataSource dataSource) {
      if (this.dataSource == null) {
         this.dataSource = dataSource;
      }
   }

   @Bean
   @Primary
   public JobRepository jobRepository() throws BatchConfigurationException {
      JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
      try {
         jobRepositoryFactoryBean.setDataSource(getDataSource());
         jobRepositoryFactoryBean.setTransactionManager(getTransactionManager());
         jobRepositoryFactoryBean.setSerializer(getExecutionContextSerializer());
         jobRepositoryFactoryBean.afterPropertiesSet();
         return jobRepositoryFactoryBean.getObject();
      } catch (Exception e) {
         throw new BatchConfigurationException("Unable to configure the default job repository", e);
      }
   }

   @Bean
   @Primary
   public JobLauncher jobLauncher() {
      TaskExecutorJobLauncher taskExecutorJobLauncher = new TaskExecutorJobLauncher();
      taskExecutorJobLauncher.setJobRepository(jobRepository());
      taskExecutorJobLauncher.setTaskExecutor(getTaskExecutor());
      try {
         taskExecutorJobLauncher.afterPropertiesSet();
         return taskExecutorJobLauncher;
      } catch (Exception e) {
         throw new BatchConfigurationException("Unable to configure the default job launcher", e);
      }
   }

   @Bean
   @Primary
   public JobExplorer jobExplorer() {
      JobExplorerFactoryBean jobExplorerFactoryBean = new JobExplorerFactoryBean();
      jobExplorerFactoryBean.setDataSource(getDataSource());
      jobExplorerFactoryBean.setTransactionManager(getTransactionManager());
      jobExplorerFactoryBean.setSerializer(getExecutionContextSerializer());
      try {
         jobExplorerFactoryBean.afterPropertiesSet();
         return jobExplorerFactoryBean.getObject();
      } catch (Exception e) {
         throw new BatchConfigurationException("Unable to configure the default job explorer", e);
      }
   }

   @Bean
   @Primary
   public JobOperator jobOperator() throws BatchConfigurationException {
      JobOperatorFactoryBean jobOperatorFactoryBean = new JobOperatorFactoryBean();
      jobOperatorFactoryBean.setTransactionManager(getTransactionManager());
      jobOperatorFactoryBean.setJobRepository(jobRepository());
      jobOperatorFactoryBean.setJobExplorer(jobExplorer());
      jobOperatorFactoryBean.setJobRegistry(jobRegistry());
      jobOperatorFactoryBean.setJobLauncher(jobLauncher());
      try {
         jobOperatorFactoryBean.afterPropertiesSet();
         return jobOperatorFactoryBean.getObject();
      } catch (Exception e) {
         throw new BatchConfigurationException("Unable to configure the default job operator", e);
      }
   }

   @Override
   public ExecutionContextSerializer getExecutionContextSerializer() {
      ObjectMapper mapper = JsonMapper.builder().disable(BLOCK_UNSAFE_POLYMORPHIC_BASE_TYPES).build();
      Jackson2ExecutionContextStringSerializer serializer = new Jackson2ExecutionContextStringSerializer();
      serializer.setObjectMapper(mapper);
      return serializer;
   }

   @Bean
   @Primary
   protected PlatformTransactionManager transactionManager() {
      JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
      jpaTransactionManager.setEntityManagerFactory(entityManager.getEntityManagerFactory());
      jpaTransactionManager.afterPropertiesSet();
      return jpaTransactionManager;
   }

   @Bean
   public JobLauncher alternativeJobLauncher() {
      TaskExecutorJobLauncher taskExecutorJobLauncher = new TaskExecutorJobLauncher();
      taskExecutorJobLauncher.setJobRepository(jobRepository());
      taskExecutorJobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
      try {
         taskExecutorJobLauncher.afterPropertiesSet();
         return taskExecutorJobLauncher;
      } catch (Exception e) {
         throw new BatchConfigurationException("Unable to configure the alternative job launcher", e);
      }
   }

   @Bean
   public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
      JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
      jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
      return jobRegistryBeanPostProcessor;
   }

   @Bean
   public TaskExecutor taskExecutor() {
      ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
      taskExecutor.setMaxPoolSize(maxPoolSize);
      taskExecutor.setCorePoolSize(corePoolSize);
      taskExecutor.afterPropertiesSet();
      return taskExecutor;
   }

   @Bean
   public RunIdIncrementer runIdIncrementer() {
      return new RunIdIncrementer();
   }
}
