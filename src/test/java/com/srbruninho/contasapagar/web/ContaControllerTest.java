package com.srbruninho.contasapagar.web;

import com.srbruninho.contasapagar.domain.model.Conta;
import com.srbruninho.contasapagar.domain.model.Situacao;
import com.srbruninho.contasapagar.domain.services.ContaService;
import com.srbruninho.contasapagar.infraestructure.controller.ContaController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ContaControllerTest {

    @Mock
    private ContaService contaService;

    @InjectMocks
    private ContaController contaController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAccount_WhenValidAccount_ShouldReturnCreated() {
        //Arrange
        Conta conta = new Conta();
        conta.setId(1L);
        conta.setDescricao("Conta Teste");

        //Act
        when(contaService.save(any(Conta.class))).thenReturn(conta);
        ResponseEntity<Object> response = contaController.createAccount(conta);

        //Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(conta.getId(), response.getBody());
        verify(contaService, times(1)).save(any(Conta.class));
    }

    @Test
    public void testCreateAccount_WhenAccountIsNull_ShouldReturnBadRequest() {
        //Arrange
        ResponseEntity<Object> response = contaController.createAccount(null);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The account cannot be null", response.getBody());
        verify(contaService, never()).save(any(Conta.class));
    }

    @Test
    public void testUpdateAccount_WhenValidAccountAndId_ShouldReturnUpdatedAccount() {
        //Arrange
        Long id = 1L;
        Conta conta = new Conta();
        conta.setId(id);
        conta.setDescricao("Conta Atualizada");

        when(contaService.findById(id)).thenReturn(Optional.of(conta));
        when(contaService.save(any(Conta.class))).thenReturn(conta);

        //Act
        ResponseEntity<Object> response = contaController.updateAccount(id, conta);

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(conta, response.getBody());

        verify(contaService, times(1)).findById(id);
        verify(contaService, times(1)).save(any(Conta.class));
    }

    @Test
    public void testUpdateAccount_WhenAccountIsNull_ShouldReturnBadRequest() {
        //Arrange
        Long id = 1L;

        //Act
        ResponseEntity<Object> response = contaController.updateAccount(id, null);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("The account cannot be null!", response.getBody());

        verify(contaService, never()).save(any(Conta.class));
    }

    @Test
    public void testUpdateAccount_WhenIdIsNull_ShouldReturnBadRequest() {
        //Arrange
        Conta conta = new Conta();

        //Act
        ResponseEntity<Object> response = contaController.updateAccount(null, conta);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("An Id must be informed!", response.getBody());

        verify(contaService, never()).findById(any(Long.class));
    }

    @Test
    public void testUpdateAccount_WhenIdNotFound_ShouldReturnNotFound() {
        //Arrange
        Long id = 1L;
        Conta conta = new Conta();
        when(contaService.findById(id)).thenReturn(Optional.empty());

        //Act
        ResponseEntity<Object> response = contaController.updateAccount(id, conta);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Id not found!", response.getBody());

        verify(contaService, times(1)).findById(id);
        verify(contaService, never()).save(any(Conta.class));
    }


    @Test
    public void testFindById_WhenValidId_ShouldReturnAccount() {
        // Arrange
        Long id = 1L;
        Conta conta = new Conta();
        conta.setId(id);
        when(contaService.findById(id)).thenReturn(Optional.of(conta));

        // Act
        ResponseEntity<Object> response = contaController.getById(id);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(contaService, times(1)).findById(id);
    }

    @Test
    public void testFindById_WhenInvalidId_ShouldReturnNotFound() {
        // Arrange
        Long id = 999L;
        when(contaService.findById(id)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Object> response = contaController.getById(id);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Id not found!", response.getBody());
        verify(contaService, times(1)).findById(id);
    }

    @Test
    public void testImportFromCsv_WhenValidCsv_ShouldReturnNoContent() throws Exception {
        // Arrange
        String csvData = "Conta Teste,99.99,2024-05-01,PENDENTE\n";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvData.getBytes());

        // Act
        ResponseEntity<Object> response = contaController.importFromCsv(file);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(contaService, times(1)).processCSV(any(MockMultipartFile.class));
    }


    @Test
    public void testImportFromCsv_WhenInvalidCsv_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String csvData = "";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvData.getBytes());

        // Act
        ResponseEntity<Object> response = contaController.importFromCsv(file);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("File is empty", response.getBody());
        verify(contaService, never()).processCSV(any(MockMultipartFile.class));
    }
}