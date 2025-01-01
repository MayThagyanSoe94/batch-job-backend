package com.maysoft.batch.job.entity;

import javax.persistence.*;

@Entity
@Table(name = "TRANSACTION")
@lombok.Data
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTION_ID", nullable = false)
    private Long id;

    @Column(name = "ACCOUNT_NUMBER", nullable = false, length = 20)
    private String accountNumber;

    @Column(name = "TRX_AMOUNT", nullable = false, precision = 10, scale = 2)
    private Double trxAmount;

    @Column(name = "DESCRIPTION", length = 100)
    private String description;

    @Column(name = "TRX_DATE", nullable = false)
    private String trxDate;

    @Column(name = "TRX_TIME", nullable = false)
    private String trxTime;

    @Column(name = "CUSTOMER_ID", nullable = false)
    private String customerId;
}


