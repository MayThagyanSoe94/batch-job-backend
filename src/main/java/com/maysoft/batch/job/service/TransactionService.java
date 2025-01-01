package com.maysoft.batch.job.service;

import com.maysoft.batch.job.entity.Transaction;
import com.maysoft.batch.job.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    private final Lock lock = new ReentrantLock();

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    public Page<Transaction> getFilteredTransactions(String customerId, String accountNumber, String description, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        System.out.println("customerId: " + customerId + ", accountNumber: " + accountNumber + ", description: " + description + ", page: " + page + ", size: " + size);
        return transactionRepository.findTransactionByCriteria(customerId, accountNumber, description, pageable);
    }

    public long getTotalFilteredTransactions(String customerId, String accountNumber, String description) {
        System.out.println("Count filtered transactions" + "customerId: " + customerId + ", accountNumber: " + accountNumber + ", description: " + description);
        return transactionRepository.countTransactionByCriteria(customerId, accountNumber, description);
    }

    @Transactional
    public Transaction updateTransactionDescription(Long id, String newDescription) {
        lock.lock();
        try {
            Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Transaction ID not found: " + id));

            transaction.setDescription(newDescription);

            return transactionRepository.save(transaction);
        } finally {
            lock.unlock();
        }
    }

}