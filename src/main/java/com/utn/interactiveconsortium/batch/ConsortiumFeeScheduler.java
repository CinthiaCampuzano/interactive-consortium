package com.utn.interactiveconsortium.batch;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class ConsortiumFeeScheduler {

   private final JobLauncher jobLauncher;

   @Qualifier("consortiumFeeJob") // Asegúrate que el nombre coincida con el bean del Job
   private final Job consortiumFeeJob;

//   @Scheduled(cron = "0 */2 * * * ?")
   public void runConsortiumFeeJob() {
      try {
         //TODO agregar el periodo
         //TODO agregar el consortium id para poder ejecutar a demanda
         //TODO agregar una flag si es ejecucion automatica, manual o reproceso
         log.info("Iniciando ejecución programada del job de generación de expensas...");
         JobParameters jobParameters = new JobParametersBuilder()
               .addDate("timestamp", new Date()) // Añade un parámetro para que cada ejecución sea única
               // Puedes añadir más parámetros si tu job los necesita para la partición o lógica
               // .addLong("consortiumId", 1L) // Ejemplo si quisieras pasar un ID específico
               .toJobParameters();

         jobLauncher.run(consortiumFeeJob, jobParameters);
         log.info("Job de generación de expensas completado exitosamente.");
      } catch (Exception e) {
         log.error("Error durante la ejecución programada del job de generación de expensas", e);
      }
   }
}
