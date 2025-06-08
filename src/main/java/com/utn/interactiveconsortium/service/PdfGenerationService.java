package com.utn.interactiveconsortium.service;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.batch.wrapper.ConsortiumFeeWrapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfGenerationService {

   @SneakyThrows
   public byte[] generateConsortiumFeePdf(ConsortiumFeeWrapper feeWrapper) {
      log.info("Generating PDF for consortium: {}", feeWrapper.getConsortiumFeePeriod().getConsortium().getName());

      // 1. Cargar y compilar la plantilla .jrxml
      InputStream reportStream = getClass().getResourceAsStream("/reports/consortium-fee-report.jrxml");
      JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

      // 2. Crear el Datasource con la lista de items de la expensa
      JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(feeWrapper.getPeriodConcepts());

      // 3. Definir los parámetros para el reporte
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("consortiumName", feeWrapper.getConsortiumFeePeriod().getConsortium().getName());
      parameters.put("period", feeWrapper.getConsortiumFeePeriod().getPeriodDate().format(DateTimeFormatter.ofPattern("MM/yyyy")));
      parameters.put("totalAmount", feeWrapper.getConsortiumFeePeriod().getTotalAmount());

      // 4. Llenar el reporte
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

      // 5. Exportar a PDF (en memoria como un array de bytes)
      return JasperExportManager.exportReportToPdf(jasperPrint);
   }
}
