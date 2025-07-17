package com.utn.interactiveconsortium.batch.services;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import static org.apache.commons.lang3.StringUtils.endsWith;
import static org.apache.commons.lang3.StringUtils.removeEnd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.batch.services.dtos.JobExecutionsResponse;
import com.utn.interactiveconsortium.batch.services.dtos.RestartResponse;
import com.utn.interactiveconsortium.batch.services.dtos.StartResponse;
import com.utn.interactiveconsortium.batch.services.dtos.StatusResponse;
import com.utn.interactiveconsortium.batch.services.dtos.StopResponse;
import com.utn.interactiveconsortium.exception.CustomGenericException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class JobExecutionService {

   public static final String DELIMITER = "_";

   private static final String DATE = "DATE";

   private static final String DATETIME = "DATETIME";

   private static final String LONG = "LONG";

   private static final String DOUBLE = "DOUBLE";

   private final JobLauncher alternativeJobLauncher;

   private final JobOperator jobOperator;

   private final JobExplorer jobExplorer;

   private final JobRegistry jobRegistry;

   public JobExecutionService(@Qualifier("alternativeJobLauncher") JobLauncher jobLauncher, JobOperator operator, JobExplorer explorer,
         JobRegistry registry) {
      this.alternativeJobLauncher = jobLauncher;
      this.jobOperator = operator;
      this.jobExplorer = explorer;
      this.jobRegistry = registry;
   }

   public StartResponse start(String jobName, Map<String, String> params) throws CustomGenericException {
      try {

         JobParametersBuilder builder = getJobParameters(params);
         builder.addLocalDateTime("executionDate", LocalDateTime.now());

         Job job = jobRegistry.getJob(jobName);
         JobExecution jobExec = alternativeJobLauncher.run(job, builder.toJobParameters());

         StartResponse response = new StartResponse();
         response.setStatus(jobExec.getStatus().name());

         response.setStarted(jobExec.getCreateTime());
         response.setJobId(jobExec.getId());
         response.setJobInstanceId(jobExec.getJobInstance().getInstanceId());
         response.setJobName(jobExec.getJobInstance().getJobName());

         return response;

      } catch (JobExecutionAlreadyRunningException e) {
         log.error(e);
         throw new CustomGenericException(e.getMessage());
      } catch (NoSuchJobException | ParseException e) {
         log.error(e);
         throw new CustomGenericException(e.getMessage());
      } catch (JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
         log.error(e);
         throw new CustomGenericException(e.getMessage());
      }
   }

   public JobParametersBuilder getJobParameters(Map<String, String> params) throws ParseException {
      JobParametersBuilder builder = new JobParametersBuilder();
      for (Map.Entry<String, String> entry : params.entrySet()) {
         if (endsWith(entry.getKey(), DATE)) {
            builder.addDate(removeEnd(entry.getKey(), DELIMITER + DATE), new SimpleDateFormat("yyyy-MM-dd").parse(entry.getValue()), false);
         } else if (endsWith(entry.getKey(), DATETIME)) {
            builder.addDate(removeEnd(entry.getKey(), DELIMITER + DATETIME), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(entry.getValue()),
                  false);
         } else if (endsWith(entry.getKey(), LONG)) {
            builder.addLong(removeEnd(entry.getKey(), DELIMITER + LONG), parseLong(entry.getValue()), false);
         } else if (endsWith(entry.getKey(), DOUBLE)) {
            builder.addDouble(removeEnd(entry.getKey(), DELIMITER + DOUBLE), parseDouble(entry.getValue()), false);
         } else {
            builder.addString(entry.getKey(), entry.getValue(), false);
         }
      }
      return builder;
   }

   public RestartResponse restart(Long id) throws CustomGenericException {
      final Optional<JobExecution> optionalRestartResponse = ofNullable(jobExplorer.getJobExecution(id));
      if (optionalRestartResponse.isPresent()) {
         JobExecution restartResponse = optionalRestartResponse.get();
         final Long restartId;
         try {
            restartId = jobOperator.restart(restartResponse.getId());

            RestartResponse response = new RestartResponse();
            response.setStatus(restartResponse.getStatus().name());
            response.setRestartId(restartId);
            response.setJobId(restartResponse.getId());
            response.setJobInstanceId(restartResponse.getJobInstance().getInstanceId());
            response.setJobName(restartResponse.getJobInstance().getJobName());

            return response;

         } catch (JobInstanceAlreadyCompleteException | NoSuchJobExecutionException | NoSuchJobException | JobRestartException e) {
            log.error(e);
         } catch (JobParametersInvalidException e) {
            log.error(e);
         }
      }
      throw new CustomGenericException("JOB_NOT_STARTED");
   }

   public StatusResponse status(Long id) {
      String status = ofNullable(jobExplorer.getJobExecution(id)).map(jobExecution -> jobExecution.getStatus().name())
            .orElse("NOT_FOUND");

      StatusResponse response = new StatusResponse();
      response.setStatus(status);

      return response;
   }

   public List<JobExecutionsResponse> all(String jobName, int start, int end) {
      if (jobName != null) {
         return jobExplorer
               .getJobInstances(jobName, start, end)
               .stream()
               .flatMap(jobInstance -> jobExplorer.getJobExecutions(jobInstance).stream().map(this::buildJobExecutionsResponse))
               .collect(toList());

      } else {
         return jobExplorer
               .getJobNames()
               .stream()
               .flatMap(s -> jobExplorer
                     .getJobInstances(s, start, end)
                     .stream()
                     .flatMap(jobInstance -> jobExplorer.getJobExecutions(jobInstance).stream().map(this::buildJobExecutionsResponse)))
               .collect(toList());
      }
   }

   private JobExecutionsResponse buildJobExecutionsResponse(JobExecution jobExecution) {

      JobExecutionsResponse response = new JobExecutionsResponse();
      response.setExecutions(jobExecution);
      return response;
   }

   public StopResponse stop(long executionId) throws CustomGenericException {
      try {

         StopResponse response = new StopResponse();
         response.setStopped(jobOperator.stop(executionId));

         return response;
      } catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
         log.error(e);
         throw new CustomGenericException("JOB_NOT_RUNNING");
      }
   }
}
