package com.n26.challenge.transaction.controller;

import com.n26.challenge.transaction.model.Transaction;
import com.n26.challenge.transaction.service.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(value = TransactionController.class)
public class TransactionControllerTest {

    public static final String TRANSACTIONS_URL = "/challenge/transactions";
    @MockBean
    TransactionService transactionService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void givenPostMethodAndServiceRaisesException_whenAddTransaction_thenReturnNoContentResponseCode() throws Exception {
        RuntimeException toBeThrown = new RuntimeException("Transaction is older than 60 seconds");
        doThrow(toBeThrown).when(transactionService).addTransaction(any(Transaction.class));
        mockMvc.perform(post(TRANSACTIONS_URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenPostMethodAndServiceDoesNotRaiseException_whenAddTransaction_thenReturnCreatedResponseCode() throws Exception {
        doNothing().when(transactionService).addTransaction(any(Transaction.class));
        mockMvc.perform(post(TRANSACTIONS_URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void givenGetMethod_whenAddTransaction_thenReturnMethodNotAllowedResponseCode() throws Exception {
        mockMvc.perform(get(TRANSACTIONS_URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isMethodNotAllowed());
    }
}
