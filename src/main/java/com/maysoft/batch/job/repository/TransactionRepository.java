package com.maysoft.batch.job.repository;

import com.maysoft.batch.job.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE "
            + " (:customerId IS NULL OR t.customerId = :customerId) AND "
            + " (:accountNumber IS NULL OR t.accountNumber LIKE %:accountNumber%) AND "
            + " (:description IS NULL OR t.description LIKE %:description%)")
    Page<Transaction> findTransactionByCriteria(@Param("customerId") String customerId,
                                                @Param("accountNumber") String accountNumber,
                                                @Param("description") String description,
                                                Pageable pageable);

    @Query("SELECT count(t) FROM Transaction t WHERE "
            + " (:customerId IS NULL OR t.customerId = :customerId) AND "
            + " (:accountNumber IS NULL OR t.accountNumber LIKE %:accountNumber%) AND "
            + " (:description IS NULL OR t.description LIKE %:description%)")
    long countTransactionByCriteria(@Param("customerId") String customerId,
                                    @Param("accountNumber") String accountNumber,
                                    @Param("description") String description);
}