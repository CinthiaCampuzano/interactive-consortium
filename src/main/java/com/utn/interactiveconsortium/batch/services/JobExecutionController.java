package com.utn.interactiveconsortium.batch.services;

import static java.math.BigDecimal.ONE;
import static java.util.Optional.ofNullable;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utn.interactiveconsortium.batch.services.dtos.JobExecutionsResponse;
import com.utn.interactiveconsortium.batch.services.dtos.RestartResponse;
import com.utn.interactiveconsortium.batch.services.dtos.StartRequest;
import com.utn.interactiveconsortium.batch.services.dtos.StartResponse;
import com.utn.interactiveconsortium.batch.services.dtos.StatusResponse;
import com.utn.interactiveconsortium.batch.services.dtos.StopResponse;
import com.utn.interactiveconsortium.exception.CustomGenericException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Validated
@RestController
@RequestMapping("/batchjob")
public class JobExecutionController {

   public final JobExecutionService jobExecutionService;

   public JobExecutionController(JobExecutionService jobExecutionService) {
      this.jobExecutionService = jobExecutionService;
   }

   @PostMapping(path = "/start")
   public StartResponse start(@Valid @RequestBody StartRequest startRequest) throws CustomGenericException {
      return jobExecutionService.start(startRequest.getJobName(), startRequest.getParams());
   }

   @PostMapping(path = "/stop")
   public StopResponse stop(@RequestParam Long id) throws CustomGenericException {
      return jobExecutionService.stop(id);
   }

   @PostMapping(path = "/restart")
   public RestartResponse restart(@RequestParam Long id) throws CustomGenericException {
      return jobExecutionService.restart(id);
   }

   @PostMapping(path = "/status")
   public StatusResponse status(@RequestParam Long id) {
      return jobExecutionService.status(id);
   }

   @PostMapping(path = "/all")
   public List<JobExecutionsResponse> all(@RequestParam(required = false) String jobName, @RequestParam(required = false) Integer start,
         @RequestParam(required = false) Integer end) {
      return jobExecutionService.all(jobName, ofNullable(start).orElse(ONE.intValue()), ofNullable(end).orElse(50000));
   }

   @PostMapping(path = "/startsynctest")
   public StartResponse startSync(@Valid @RequestBody StartRequest startRequest) throws CustomGenericException, InterruptedException {
      StartResponse response = jobExecutionService.start(startRequest.getJobName(), startRequest.getParams());
      StatusResponse st;
      Instant start = Instant.now();
      do {
         Thread.sleep(1000);
         st = jobExecutionService.status(response.getJobId());
      } while (!st.getStatus().equals("COMPLETED") && !st.getStatus().equals("FAILED") && !st.getStatus().equals("ABANDONED") && !st
            .getStatus()
            .equals("STOPPED") && !st.getStatus().equals("NOT_FOUND") && Duration.between(start, Instant.now()).toSeconds() < 300);
      response.setStatus(st.getStatus());
      return response;
   }

}
