package com.maysoft.batch.job;

import com.maysoft.batch.job.entity.Transaction;
import com.maysoft.batch.job.repository.TransactionRepository;
import com.maysoft.batch.job.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {

    private TransactionService transactionService;
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() {
        transactionRepository = Mockito.mock(TransactionRepository.class);
        transactionService = new TransactionService(transactionRepository);
    }

    @Test
    @Transactional
    public void testConcurrentUpdateHandling() throws Exception {
        Long transactionId = 1L;
        String newDescription = "New Description";

        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);
        existingTransaction.setDescription("Old Description");
        //existingTransaction.setVersion(1);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));

        // Simulate two threads updating the same transaction
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable task1 = () -> {
            transactionService.updateTransactionDescription(transactionId, newDescription);
        };

        Runnable task2 = () -> {
            assertThrows(OptimisticLockingFailureException.class, () -> {
                transactionService.updateTransactionDescription(transactionId, newDescription + " Updated Again");
            });
        };

        executor.submit(task1);
        executor.submit(task2);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
