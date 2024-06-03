package com.srbruninho.contasapagar.domain.services;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.srbruninho.contasapagar.domain.model.Conta;
import com.srbruninho.contasapagar.domain.repositories.ContaRepository;
import com.srbruninho.contasapagar.infraestructure.projection.PeriodProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    private static final Logger LOGGER = Logger.getLogger(ContaService.class.getName());

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Conta save(Conta conta) {
        conta.defineSituacao();
        return contaRepository.save(conta);
    }

    public Conta updateSituacao(Conta conta, boolean isPaid) {
        if (isPaid) {
            conta.confirmPayment();
        } else {
            conta.defineSituacao();
        }
        return contaRepository.save(conta);
    }
    public Optional<Conta> findById(Long id) {
        return contaRepository.findById(id);
    }

    public Page<Conta> findAll(Pageable pageable) {
        return contaRepository.findAll(pageable);
    }

    public void deleteById(Long id) {
        contaRepository.deleteById(id);
    }

    public Page<PeriodProjection> getTotalValuePaidPerPeriod(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return contaRepository.getTotalValuePaidPerPeriod(startDate, endDate, pageable);
    }

    public Page<Conta> getAccountsbyDateAndDescription(LocalDate startDate, LocalDate endDate, String description, Pageable pageable){
        return contaRepository.findByDataVencimentoBetweenAndDescricaoAndDataPagamentoIsNull(startDate, endDate, description, pageable);
    }

    public void processCSV(MultipartFile file){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {

            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                Double amount = Double.MIN_NORMAL;
                LocalDate dueDate = null;
                LocalDate paidDate = null;

                String description = nextRecord[0];

                try {
                    amount = Double.parseDouble(nextRecord[1]);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "#c12cb043 - " + e.getMessage());
                }

                try {
                    dueDate = LocalDate.parse(nextRecord[2], DATE_FORMATTER);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "#385e5f21 - " + e.getMessage());
                }

                try {
                    paidDate = LocalDate.parse(nextRecord[3], DATE_FORMATTER);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "#9460e0a0 - " + e.getMessage());
                }

                Conta conta = Conta.builder()
                        .dataVencimento(dueDate)
                        .dataPagamento(paidDate)
                        .descricao(description)
                        .valor(BigDecimal.valueOf(amount))
                        .build();

                conta.defineSituacao();

                save(conta);
                LOGGER.log(Level.INFO, "#96b85d24 - Save Succesfully ", conta);
            }

        } catch (IOException | CsvValidationException e) {
            LOGGER.log(Level.SEVERE, "#c262d751 - Error reading CSV file", e);
        }
    }
}
