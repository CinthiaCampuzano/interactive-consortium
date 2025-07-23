package com.utn.interactiveconsortium.controller;

import java.io.IOException;

import jakarta.mail.MessagingException;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.utn.interactiveconsortium.dto.PaymentDto;
import com.utn.interactiveconsortium.exception.CustomGenericException;
import com.utn.interactiveconsortium.exception.EntityNotFoundException;
import com.utn.interactiveconsortium.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/payments")
@RestController
public class PaymentController {

   private final PaymentService paymentService;

   @PostMapping()
   @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
   public PaymentDto createPayment(@RequestPart(value = "maintenanceFeePaymentDto") PaymentDto paymentDto,
         @RequestPart(value = "file") MultipartFile file) throws EntityNotFoundException, MessagingException, IOException, CustomGenericException {
      return paymentService.createPayment(paymentDto, file);
   }

}
