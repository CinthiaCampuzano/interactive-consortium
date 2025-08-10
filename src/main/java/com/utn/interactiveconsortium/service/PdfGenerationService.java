package com.utn.interactiveconsortium.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.stereotype.Service;

import com.utn.interactiveconsortium.batch.wrapper.JasperWrapper;
import com.utn.interactiveconsortium.entity.ConsortiumFeePeriodItemEntity;
import com.utn.interactiveconsortium.entity.DepartmentFeeEntity;
import com.utn.interactiveconsortium.entity.DepartmentFeeItemEntity;
import com.utn.interactiveconsortium.enums.EConsortiumFeeConceptType;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfGenerationService {

   private final PaymentService paymentService;

   @SneakyThrows
   public byte[] generateConsortiumFeePdf(JasperWrapper jasperWrapper) {
      log.info("Generating PDF for consortium: {}", jasperWrapper.getConsortiumFeePeriod().getConsortium().getName());

      // 1. Cargar y compilar la plantilla .jrxml
      InputStream reportStream = getClass().getResourceAsStream("/reports/consortium-fee-report.jrxml");
      JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

      // 2. Crear los Datasources para los subreportes
      JRBeanCollectionDataSource periodConceptsDataSource = new JRBeanCollectionDataSource(jasperWrapper.getPeriodConcepts());
      
      // Preparar los datos para el subreporte de departamentos con los montos por categoría
      List<Map<String, Object>> departmentFeesWithCategories = prepareDepartmentFeesWithCategories(jasperWrapper.getDepartmentFees());
      JRBeanCollectionDataSource departmentFeesDataSource = new JRBeanCollectionDataSource(departmentFeesWithCategories);

      // 3. Definir los parámetros para el reporte PRINCIPAL
      LocalDate periodDate = jasperWrapper.getConsortiumFeePeriod().getPeriodDate();
      String monthNameSpanish = paymentService.getMonthName(periodDate);
      String year = String.valueOf(periodDate.getYear());
      String dueDate = jasperWrapper.getConsortiumFeePeriod().getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
      
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("consortiumName", jasperWrapper.getConsortiumFeePeriod().getConsortium().getName());
      parameters.put("period", periodDate.format(DateTimeFormatter.ofPattern("MM/yyyy")));
      parameters.put("totalAmount", jasperWrapper.getConsortiumFeePeriod().getTotalAmount());
      parameters.put("monthNameSpanish", monthNameSpanish);
      parameters.put("year", year);
      parameters.put("dueDate", dueDate);

      JasperReport departmentSubReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/reports/subreport_department_fees.jrxml"));
      parameters.put("departmentSubReport", departmentSubReport);
      JasperReport periodSubReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/reports/subreport_period_concepts.jrxml"));
      parameters.put("periodSubReport", periodSubReport);

      // Pasar los datasources de los subreportes como parámetros
      parameters.put("PERIOD_CONCEPTS_DATASOURCE", periodConceptsDataSource);
      parameters.put("DEPARTMENT_FEES_DATASOURCE", departmentFeesDataSource);

      // 4. Llenar el reporte
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource(1));

      // 5. Exportar a PDF
      return JasperExportManager.exportReportToPdf(jasperPrint);
   }
   
   @SneakyThrows
   public byte[] generateDepartmentFeePdf(DepartmentFeeEntity departmentFee) {
      log.info("Generating PDF for department: {}", departmentFee.getDepartment().getCode());

      // 1. Cargar y compilar la plantilla .jrxml
      InputStream reportStream = getClass().getResourceAsStream("/reports/department-fee-report.jrxml");
      JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

      // 2. Preparar los datos para el reporte
      List<Map<String, Object>> feeItems = prepareDepartmentFeeItems(departmentFee.getDepartmentFeeItems());
      JRBeanCollectionDataSource feeItemsDataSource = new JRBeanCollectionDataSource(feeItems);

      // 3. Calcular los montos por categoría
      Map<EConsortiumFeeConceptType, BigDecimal> amountsByCategory = calculateAmountsByCategory(departmentFee.getDepartmentFeeItems());
      
      // 4. Definir los parámetros para el reporte
      LocalDate periodDate = departmentFee.getConsortiumFeePeriod().getPeriodDate();
      String monthNameSpanish = paymentService.getMonthName(periodDate);
      String year = String.valueOf(periodDate.getYear());
      String dueDate = departmentFee.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
      
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("consortiumName", departmentFee.getConsortiumFeePeriod().getConsortium().getName());
      parameters.put("departmentCode", departmentFee.getDepartment().getCode());
      parameters.put("period", periodDate.format(DateTimeFormatter.ofPattern("MM/yyyy")));
      parameters.put("monthNameSpanish", monthNameSpanish);
      parameters.put("year", year);
      parameters.put("dueDate", dueDate);
      parameters.put("totalAmount", departmentFee.getTotalAmount());
      parameters.put("ordinaryAmount", amountsByCategory.getOrDefault(EConsortiumFeeConceptType.ORDINARY, BigDecimal.ZERO));
      parameters.put("extraordinaryAmount", amountsByCategory.getOrDefault(EConsortiumFeeConceptType.EXTRAORDINARY, BigDecimal.ZERO));
      parameters.put("amenityAmount", amountsByCategory.getOrDefault(EConsortiumFeeConceptType.AMENITY_USE, BigDecimal.ZERO));
      parameters.put("adjustmentAmount", amountsByCategory.getOrDefault(EConsortiumFeeConceptType.ADJUSTMENT, BigDecimal.ZERO));

      // 5. Llenar el reporte
      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, feeItemsDataSource);

      // 6. Exportar a PDF
      return JasperExportManager.exportReportToPdf(jasperPrint);
   }
   
   private List<Map<String, Object>> prepareDepartmentFeesWithCategories(List<DepartmentFeeEntity> departmentFees) {
      List<Map<String, Object>> result = new ArrayList<>();
      
      for (DepartmentFeeEntity fee : departmentFees) {
         Map<String, Object> feeMap = new HashMap<>();
         feeMap.put("department", fee.getDepartment());
         feeMap.put("totalAmount", fee.getTotalAmount());
         feeMap.put("dueDate", fee.getDueDate());
         
         // Calcular montos por categoría
         Map<EConsortiumFeeConceptType, BigDecimal> amountsByCategory = calculateAmountsByCategory(fee.getDepartmentFeeItems());
         
         feeMap.put("ordinaryAmount", amountsByCategory.getOrDefault(EConsortiumFeeConceptType.ORDINARY, BigDecimal.ZERO));
         feeMap.put("extraordinaryAmount", amountsByCategory.getOrDefault(EConsortiumFeeConceptType.EXTRAORDINARY, BigDecimal.ZERO));
         feeMap.put("amenityAmount", amountsByCategory.getOrDefault(EConsortiumFeeConceptType.AMENITY_USE, BigDecimal.ZERO));
         feeMap.put("adjustmentAmount", amountsByCategory.getOrDefault(EConsortiumFeeConceptType.ADJUSTMENT, BigDecimal.ZERO));
         
         result.add(feeMap);
      }
      
      return result;
   }
   
   private List<Map<String, Object>> prepareDepartmentFeeItems(List<DepartmentFeeItemEntity> feeItems) {
      return feeItems
            .stream()
            .sorted((o1, o2) -> {
               EConsortiumFeeConceptType conceptType1 = o1.getConsortiumFeePeriodItem().getConceptType();
               EConsortiumFeeConceptType conceptType2 = o2.getConsortiumFeePeriodItem().getConceptType();
               return conceptType1.compareTo(conceptType2);
            })
            .map(item -> {
               Map<String, Object> itemMap = new HashMap<>();
               ConsortiumFeePeriodItemEntity periodItem = item.getConsortiumFeePeriodItem();

               itemMap.put("name", item.getDescription());
               itemMap.put("description", periodItem.getDescription());
               itemMap.put("amount", item.getAmount());
               itemMap.put("conceptType", periodItem.getConceptType().singularTranslateToSpanish());

               return itemMap;
            }).collect(Collectors.toList());
   }
   
   private Map<EConsortiumFeeConceptType, BigDecimal> calculateAmountsByCategory(List<DepartmentFeeItemEntity> feeItems) {
      Map<EConsortiumFeeConceptType, BigDecimal> result = new HashMap<>();
      
      for (DepartmentFeeItemEntity item : feeItems) {
         EConsortiumFeeConceptType conceptType = item.getConsortiumFeePeriodItem().getConceptType();
         BigDecimal currentAmount = result.getOrDefault(conceptType, BigDecimal.ZERO);
         result.put(conceptType, currentAmount.add(item.getAmount()));
      }
      
      return result;
   }
}
