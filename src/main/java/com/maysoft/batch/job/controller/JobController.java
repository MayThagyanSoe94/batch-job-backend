package com.maysoft.batch.job.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/job")
public class JobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    private final String TEMP_FILE_STORAGE = "/Users/maythagyansoe/Desktop/batch_files/";

    @PostMapping("/import")
    public void startBatchJob(@RequestParam("file") MultipartFile file) {

        try {
            String originalFileName = file.getOriginalFilename();
            File importFile = new File(TEMP_FILE_STORAGE + originalFileName);
            file.transferTo(importFile);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("inputFilePath", TEMP_FILE_STORAGE + originalFileName)
                    .addLong("time", System.currentTimeMillis()).toJobParameters();

            JobExecution execution = jobLauncher.run(job, jobParameters);

            if ("COMPLETED".equals(execution.getExitStatus().getExitCode())) {
                // delete temp file from temp file storage
                Files.deleteIfExists(Paths.get(TEMP_FILE_STORAGE + originalFileName));
                System.out.println("Successfully deleted temp file from temp file storage");
            }

        } catch (JobRestartException | JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException | IOException e) {
            e.printStackTrace();
        }
    }
}
