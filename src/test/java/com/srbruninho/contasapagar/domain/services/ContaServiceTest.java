package com.srbruninho.contasapagar.domain.services;

import com.srbruninho.contasapagar.domain.model.Conta;
import com.srbruninho.contasapagar.domain.model.Situacao;
import com.srbruninho.contasapagar.domain.repositories.ContaRepository;
import com.srbruninho.contasapagar.infraestructure.projection.PeriodProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContaServiceTest {

    @Mock
    private ContaRepository contaRepository;

    @InjectMocks
    private ContaService contaService;

    public ContaServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUpdateSituacao_WhenPaid_ShouldSetSituacaoPaga() {
        // Arrange
        Conta conta = new Conta();
        conta.setDataVencimento(LocalDate.now().minusDays(1));
        conta.setSituacao(Situacao.PENDENTE);

        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        // Act
        Conta updatedConta = contaService.updateSituacao(conta, true);

        // Assert
        assertEquals(Situacao.PAGA, updatedConta.getSituacao());
        assertEquals(LocalDate.now(), updatedConta.getDataPagamento());
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    public void testUpdateSituacao_WhenNotPaidAndDueDatePassed_ShouldSetSituacaoAtrasada() {
        // Arrange
        Conta conta = new Conta();
        conta.setDataVencimento(LocalDate.now().minusDays(1));
        conta.setSituacao(Situacao.PENDENTE);

        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        // Act
        Conta updatedConta = contaService.updateSituacao(conta, false);

        // Assert
        assertEquals(Situacao.ATRASADA, updatedConta.getSituacao());
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    public void testUpdateSituacao_WhenNotPaidAndDueDateInFuture_ShouldSetSituacaoPendente() {
        // Arrange
        Conta conta = new Conta();
        conta.setDataVencimento(LocalDate.now().plusDays(1));
        conta.setSituacao(Situacao.PENDENTE);

        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        // Act
        Conta updatedConta = contaService.updateSituacao(conta, false);

        // Assert
        assertEquals(Situacao.PENDENTE, updatedConta.getSituacao());
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    public void testSave_WhenValidConta_ShouldDefineSituacaoAndSave() {
        // Arrange
        Conta conta = new Conta();
        conta.setDescricao("Conta Teste");
        conta.setValor(BigDecimal.valueOf(99.99));
        conta.setDataVencimento(LocalDate.now().plusDays(1));

        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        // Act
        Conta savedConta = contaService.save(conta);

        // Assert
        assertEquals(Situacao.PENDENTE, savedConta.getSituacao());
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    public void testSave_WhenDataVencimentoIsPast_ShouldDefineSituacaoAsAtrasada() {
        // Arrange
        Conta conta = new Conta();
        conta.setDescricao("Conta Teste");
        conta.setValor(BigDecimal.valueOf(99.99));
        conta.setDataVencimento(LocalDate.now().minusDays(1));

        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        // Act
        Conta savedConta = contaService.save(conta);

        // Assert
        assertEquals(Situacao.ATRASADA, savedConta.getSituacao());
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    public void testSave_WhenDataPagamentoIsNotNull_ShouldDefineSituacaoAsPaga() {
        // Arrange
        Conta conta = new Conta();
        conta.setDescricao("Conta Teste");
        conta.setValor(BigDecimal.valueOf(99.99));
        conta.setDataVencimento(LocalDate.now().plusDays(1));
        conta.setDataPagamento(LocalDate.now());

        when(contaRepository.save(any(Conta.class))).thenReturn(conta);

        // Act
        Conta savedConta = contaService.save(conta);

        // Assert
        assertEquals(Situacao.PAGA, savedConta.getSituacao());
        verify(contaRepository, times(1)).save(conta);
    }

    @Test
    public void testFindById_WhenValidId_ShouldReturnConta() {
        // Arrange
        Long id = 1L;
        Conta conta = new Conta();
        conta.setId(id);
        when(contaRepository.findById(id)).thenReturn(Optional.of(conta));

        // Act
        Optional<Conta> result = contaService.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(conta, result.get());
        verify(contaRepository, times(1)).findById(id);
    }

    @Test
    public void testFindById_WhenInvalidId_ShouldReturnEmpty() {
        // Arrange
        Long id = 1L;
        when(contaRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        Optional<Conta> result = contaService.findById(id);

        // Assert
        assertFalse(result.isPresent());
        verify(contaRepository, times(1)).findById(id);
    }

    @Test
    public void testFindAll_ShouldReturnPageOfContas() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Conta conta = new Conta();
        Page<Conta> page = new PageImpl<>(Collections.singletonList(conta));
        when(contaRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<Conta> result = contaService.findAll(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(contaRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testDeleteById_ShouldDeleteConta() {
        // Arrange
        Long id = 1L;
        doNothing().when(contaRepository).deleteById(id);

        // Act
        contaService.deleteById(id);

        // Assert
        verify(contaRepository, times(1)).deleteById(id);
    }

    @Test
    public void testGetTotalValuePaidPerPeriod_ShouldReturnPageOfPeriodProjections() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        Pageable pageable = PageRequest.of(0, 10);
        PeriodProjection projection = mock(PeriodProjection.class);
        Page<PeriodProjection> page = new PageImpl<>(Collections.singletonList(projection));
        when(contaRepository.getTotalValuePaidPerPeriod(startDate, endDate, pageable)).thenReturn(page);

        // Act
        Page<PeriodProjection> result = contaService.getTotalValuePaidPerPeriod(startDate, endDate, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(contaRepository, times(1)).getTotalValuePaidPerPeriod(startDate, endDate, pageable);
    }

    @Test
    public void testGetAccountsByDateAndDescription_ShouldReturnPageOfContas() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);
        String description = "Teste";
        Pageable pageable = PageRequest.of(0, 10);
        Conta conta = new Conta();
        Page<Conta> page = new PageImpl<>(Collections.singletonList(conta));
        when(contaRepository.findByDataVencimentoBetweenAndDescricaoAndDataPagamentoIsNull(startDate, endDate, description, pageable)).thenReturn(page);

        // Act
        Page<Conta> result = contaService.getAccountsbyDateAndDescription(startDate, endDate, description, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(contaRepository, times(1)).findByDataVencimentoBetweenAndDescricaoAndDataPagamentoIsNull(startDate, endDate, description, pageable);
    }

    @Test
    public void testProcessCSV_ShouldSaveContasFromCSV() throws Exception {
        // Arrange
        String csvContent = "Conta Teste,99.99,2024-05-01";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", new ByteArrayInputStream(csvContent.getBytes()));

        // Act
        contaService.processCSV(file);

        // Assert
        verify(contaRepository, times(1)).save(any(Conta.class));
    }

}