package com.srbruninho.contasapagar.api.controller;

import com.srbruninho.contasapagar.api.converter.ContaConverter;
import com.srbruninho.contasapagar.api.dto.ContaDTO;
import com.srbruninho.contasapagar.domain.model.Conta;
import com.srbruninho.contasapagar.domain.services.ContaService;
import com.srbruninho.contasapagar.domain.repositories.projection.TotalValuePaidPerPeriodProjection;
import com.srbruninho.contasapagar.infrastructure.exception.BusinessErrorResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@OpenAPIDefinition(info = @Info(title = "Contas a pagar API", version = "v1"))
@Tag(name = "Conta Controller", description = "Endpoints para gerenciamento de contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @Operation(summary = "Criar uma nova conta")
    @PostMapping("/create-account")
    public ResponseEntity<Object> createAccount(@Valid @RequestBody Conta conta) {
        try {
            if (conta == null)
                return ResponseEntity.badRequest().body("The account cannot be null");

            Conta newAccount = contaService.save(conta);

            return new ResponseEntity<>(newAccount.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            BusinessErrorResponse businessErrorResponse = new BusinessErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(businessErrorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Atualizar uma conta existente")
    @PutMapping("/update-account/{id}")
    public ResponseEntity<Object> updateAccount( @Parameter(description = "ID da conta a ser atualizada", example = "1")@Valid @PathVariable Long id, @RequestBody Conta conta) {
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

    @Operation(summary = "Atualizar situação de pagamento de uma conta")
    @PutMapping("/update-situacao/{id}")
    public ResponseEntity<Object> updateSituacao(@Parameter(description = "ID da conta a ter a situação de pagamento atualizada", example = "1") @Valid @PathVariable Long id, @RequestBody boolean isPaid) {
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

    @Operation(summary = "Obter todas as contas paginadas")
    @GetMapping
    public ResponseEntity<Page<ContaDTO>> getAll(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataVencimento").descending());
        Page<Conta> contas = contaService.findAll(pageable);
        Page<ContaDTO> contaDTOPage = contas.map(ContaConverter::toDTO);

        return ResponseEntity.ok(contaDTOPage);
    }
    @Operation(summary = "Filtrar contas pendentes por data de vencimento e descrição")
    @GetMapping("/filter/due-date/description/unpaid")
    public ResponseEntity<Page<ContaDTO>> getPendingBills(@Parameter(description = "Número da página (começa em 0)", example = "0") @RequestParam(defaultValue = "0") int page,
                                                          @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size,
                                                          @Parameter(description = "Data de início do filtro", example = "2024-06-01") @RequestParam LocalDate startDate,
                                                          @Parameter(description = "Data de término do filtro", example = "2024-06-30") @RequestParam LocalDate endDate,
                                                          @Parameter(description = "Descrição da conta") @RequestParam String description) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dataVencimento").descending());
        Page<Conta> contas = contaService.getAccountsbyDateAndDescription(startDate, endDate, description, pageable);
        Page<ContaDTO> contaDTOPage = contas.map(ContaConverter::toDTO);

        return ResponseEntity.ok(contaDTOPage);
    }

    @Operation(summary = "Obter valor total pago por período")
    @GetMapping("/filter/total-value/period/paid")
    public ResponseEntity<Page<TotalValuePaidPerPeriodProjection>> getTotalValuePerPeriod(@Parameter(description = "Número da página (começa em 0)", example = "0") @RequestParam(defaultValue = "0") int page,
                                                                                          @Parameter(description = "Tamanho da página", example = "10") @RequestParam(defaultValue = "10") int size,
                                                                                          @Parameter(description = "Data de início do período", example = "2024-01-01") @RequestParam LocalDate startDate,
                                                                                          @Parameter(description = "Data de término do período", example = "2024-06-30") @RequestParam LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TotalValuePaidPerPeriodProjection> periodos = contaService.getTotalValuePaidPerPeriod(startDate, endDate, pageable);
        return ResponseEntity.ok(periodos);
    }


    @Operation(summary = "Obter uma conta pelo ID - paginada")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@Parameter(description = "ID da conta a ser recuperada", example = "1")@PathVariable Long id) {
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

    @Operation(summary = "Excluir uma conta pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@Parameter(description = "ID da conta a ser deletada", example = "1")@PathVariable Long id) {
        if (id == null)
            return ResponseEntity.badRequest().body("An Id must be informed!");
        contaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Importar contas a pagar a partir de um arquivo CSV")
    @PostMapping("/import-csv")
    public ResponseEntity<Object> importFromCsv(@Parameter(description = "Arquivo CSV contendo contas a pagar no formato 'Descrição,Valor,Vencimento,Pagamento' separados por vírgula. Exemplo: \"Conta Gás\",39.99,\"2024-06-22\",\"2024-04-22\"", required = true) @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        contaService.processCSV(file);

        return ResponseEntity.noContent().build();
    }
}
