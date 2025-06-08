package com.utn.interactiveconsortium.batch.steps;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import com.utn.interactiveconsortium.entity.ConsortiumEntity;
import com.utn.interactiveconsortium.repository.ConsortiumRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConsortiumFeePartitioner implements Partitioner {

   private static final Logger LOGGER = LoggerFactory.getLogger(ConsortiumFeePartitioner.class);
   public static final String PARTITION_KEY_PREFIX = "partition";
   public static final String CONSORTIUM_ID = "consortiumId";

   private final ConsortiumRepository consortiumRepository;

   @Override
   public Map<String, ExecutionContext> partition(int gridSize) {
      Map<String, ExecutionContext> partitions = new HashMap<>();
      List<ConsortiumEntity> consortiums = consortiumRepository.findAll();

      LOGGER.info("Found {} consortiums to partition.", consortiums.size());

      for (ConsortiumEntity consortium : consortiums) {
         ExecutionContext executionContext = new ExecutionContext();
         Long consortiumId = consortium.getConsortiumId();
         executionContext.putLong(CONSORTIUM_ID, consortiumId);
         String partitionName = PARTITION_KEY_PREFIX + "_ConsortiumId_" + consortiumId;
         partitions.put(partitionName, executionContext);
         LOGGER.debug("Created partition {} for consortiumId: {}", partitionName, consortiumId);
      }

      LOGGER.info("Successfully created {} partitions.", partitions.size());
      return partitions;
   }
}
