package com.srbruninho.contasapagar.api.controller;

import com.srbruninho.contasapagar.api.converter.ContaConverter;
import com.srbruninho.contasapagar.api.dto.ContaDTO;
import com.srbruninho.contasapagar.domain.model.Conta;
import com.srbruninho.contasapagar.domain.services.ContaService;
import com.srbruninho.contasapagar.domain.repositories.projection.TotalValuePaidPerPeriodProjection;
import com.srbruninho.contasapagar.infrastructure.exception.BusinessErrorResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @PostMapping("/create-account")
    public ResponseEntity<Object> createAccount(@Valid @RequestBody Conta conta) {
        try {
            if (conta == null)//validação incorreta
                return ResponseEntity.badRequest().body("The account cannot be null");

            Conta newAccount = contaService.save(conta);

            return new ResponseEntity<>(newAccount.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            BusinessErrorResponse businessErrorResponse = new BusinessErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(businessErrorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-account/{id}")
    public ResponseEntity<Object> updateAccount(@Valid @PathVariable Long id, @RequestBody Conta conta) {
        try {
            if (conta == null)
                return ResponseEntity.badRequest().body("The account cannot be null!");
            if (id == null)
                return ResponseEntity.badRequest().body("An Id must be informed!");

            Optional<Conta> existingAccount = contaService.findById(id);

            if (existingAccount.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id not found!");
            conta.setId(id);
            Conta updatedAccount = contaService.save(conta);

            return new ResponseEntity<>(ContaConverter.toDTO(updatedAccount), HttpStatus.OK);
        } catch (Exception e) {
            BusinessErrorResponse businessErrorResponse = new BusinessErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(businessErrorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update-situacao/{id}")
    public ResponseEntity<Object> updateSituacao(@Valid @PathVariable Long id, @RequestBody boolean isPaid) {
        try {

            if (id == null)
                return ResponseEntity.badRequest().body("An Id must be informed!");

            Optional<Conta> existingAccount = contaService.findById(id);

            if (existingAccount.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id not found!");

            Conta updatedAccount = contaService.updateSituacao(existingAccount.get(), isPaid);

            return new ResponseEntity<>(ContaConverter.toDTO(updatedAccount), HttpStatus.OK);
        } catch (Exception e) {
            BusinessErrorResponse businessErrorResponse = new BusinessErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(businessErrorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<Page<ContaDTO>> getAll(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataVencimento").descending());
        Page<Conta> contas = contaService.findAll(pageable);
        Page<ContaDTO> contaDTOPage = contas.map(ContaConverter::toDTO);

        return ResponseEntity.ok(contaDTOPage);
    }

    @GetMapping("/filter/due-date/description/unpaid")
    public ResponseEntity<Page<ContaDTO>> getPendingBills(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam LocalDate startDate,
                                                       @RequestParam LocalDate endDate,
                                                       @RequestParam String description) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataVencimento").descending());
        Page<Conta> contas = contaService.getAccountsbyDateAndDescription(startDate, endDate, description, pageable);
        Page<ContaDTO> contaDTOPage = contas.map(ContaConverter::toDTO);

        return ResponseEntity.ok(contaDTOPage);
    }

    @GetMapping("/filter/total-value/period/paid")
    public ResponseEntity<Page<TotalValuePaidPerPeriodProjection>> getTotalValuePerPeriod(@RequestParam(defaultValue = "0") int page,
                                                                                          @RequestParam(defaultValue = "10") int size,
                                                                                          @RequestParam LocalDate startDate,
                                                                                          @RequestParam LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TotalValuePaidPerPeriodProjection> periodos = contaService.getTotalValuePaidPerPeriod(startDate, endDate, pageable);
        return ResponseEntity.ok(periodos);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable Long id) {
        try {
            if (id == null)
                return ResponseEntity.badRequest().body("An Id must be informed!");

            Optional<Conta> existingAccount = contaService.findById(id);

            if (existingAccount.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id not found!");

            Page<Conta> contaPage = new PageImpl<>(List.of(existingAccount.get()));
            Page<ContaDTO> contaDTOPage = contaPage.map(ContaConverter::toDTO);

            return new ResponseEntity<>(contaDTOPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(ResponseEntity.noContent(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable Long id) {
        if (id == null)
            return ResponseEntity.badRequest().body("An Id must be informed!");
        contaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/import-csv")
    public ResponseEntity<Object> importFromCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        contaService.processCSV(file);

        return ResponseEntity.noContent().build();
    }
}
