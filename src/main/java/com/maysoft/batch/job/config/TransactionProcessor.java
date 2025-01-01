package com.maysoft.batch.job.config;

import com.maysoft.batch.job.entity.Transaction;
import org.springframework.batch.item.ItemProcessor;

public class TransactionProcessor implements ItemProcessor<Transaction, Transaction> {
    @Override
    public Transaction process(Transaction transaction) throws Exception {
        return transaction;
    }
}
