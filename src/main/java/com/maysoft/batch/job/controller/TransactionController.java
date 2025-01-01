package com.maysoft.batch.job.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maysoft.batch.job.entity.Transaction;
import com.maysoft.batch.job.repository.TransactionRepository;
import com.maysoft.batch.job.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@lombok.AllArgsConstructor
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private final ObjectMapper objectMapper;


    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> getTransactions(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String accountNumber,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        System.out.println("customerId: " + customerId + ", accountNumber: " + accountNumber + ", description: " + description + ", page: " + page + ", size: " + size);

        Page<Transaction> transactionPage = transactionService.getFilteredTransactions(customerId, accountNumber, description, page, size);
        long totalCount = transactionService.getTotalFilteredTransactions(customerId, accountNumber, description);

        Map<String, Object> response = new HashMap<>();
        response.put("transactions", transactionPage.getContent());
        response.put("currentPage", transactionPage.getNumber());
        response.put("totalItems", totalCount);
        response.put("totalPages", transactionPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/transactions/{id}")
    public ResponseEntity<?> updateTransactionDescription(
            @PathVariable Long id,
            @RequestBody String newDescription) {
        try {
            Map<String, String> parsedJson = objectMapper.readValue(newDescription, Map.class);
            String descriptionValue = parsedJson.get("newDescription");

            Transaction updatedTransaction = transactionService.updateTransactionDescription(id, descriptionValue);
            return ResponseEntity.ok(updatedTransaction);

        } catch (OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Concurrent update detected");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON provided");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }
}