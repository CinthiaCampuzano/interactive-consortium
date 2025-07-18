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

import com.utn.interactiveconsortium.batch.wrapper.JasperWrapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PdfGenerationService {

   @SneakyThrows
   public byte[] generateConsortiumFeePdf(JasperWrapper jasperWrapper) { // Cambiado el parámetro
      log.info("Generating PDF for consortium: {}", jasperWrapper.getConsortiumFeePeriod().getConsortium().getName());

      // 1. Cargar y compilar la plantilla .jrxml
      InputStream reportStream = getClass().getResourceAsStream("/reports/consortium-fee-report.jrxml");
      JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

      // 2. Crear los Datasources para los subreportes
      JRBeanCollectionDataSource periodConceptsDataSource = new JRBeanCollectionDataSource(jasperWrapper.getPeriodConcepts());
      JRBeanCollectionDataSource departmentFeesDataSource = new JRBeanCollectionDataSource(jasperWrapper.getDepartmentFees());

      // 3. Definir los parámetros para el reporte PRINCIPAL
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("consortiumName", jasperWrapper.getConsortiumFeePeriod().getConsortium().getName());
      parameters.put("period", jasperWrapper.getConsortiumFeePeriod().getPeriodDate().format(DateTimeFormatter.ofPattern("MM/yyyy")));
      parameters.put("totalAmount", jasperWrapper.getConsortiumFeePeriod().getTotalAmount());

      JasperReport departmentSubReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/reports/subreport_department_fees.jrxml"));
      parameters.put("departmentSubReport", departmentSubReport);
      JasperReport periodSubReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/reports/subreport_period_concepts.jrxml"));
      parameters.put("periodSubReport", periodSubReport);


      // Pasar los datasources de los subreportes como parámetros
      parameters.put("PERIOD_CONCEPTS_DATASOURCE", periodConceptsDataSource);
      parameters.put("DEPARTMENT_FEES_DATASOURCE", departmentFeesDataSource);


      // 4. Llenar el reporte
      // Se usa periodConceptsDataSource como el datasource principal del reporte.
      // Si tu primera tabla está en la banda de detalle, esto funcionará bien.
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, periodConceptsDataSource);

      // 5. Exportar a PDF
      return JasperExportManager.exportReportToPdf(jasperPrint);
   }
}
