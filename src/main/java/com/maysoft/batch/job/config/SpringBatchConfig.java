package com.maysoft.batch.job.config;

import com.maysoft.batch.job.entity.Transaction;
import com.maysoft.batch.job.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.io.File;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class SpringBatchConfig {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private TransactionRepository transactionRepository;

    @Bean
    @StepScope
    public FlatFileItemReader<Transaction> itemReader(@Value("#{jobParameters['inputFilePath']}") String inputFilePath) {
        FlatFileItemReader<Transaction> reader = new FlatFileItemReader<>();
        //reader.setResource(new FileSystemResource("src/main/resources/dataSource.txt"));
        reader.setResource(new FileSystemResource(new File(inputFilePath)));
        reader.setName("txtReader");
        reader.setLinesToSkip(1);
        reader.setLineMapper(lineMapper());
        return reader;
    }

    private LineMapper<Transaction> lineMapper() {
        DefaultLineMapper<Transaction> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter("|");
        delimitedLineTokenizer.setStrict(false);
        delimitedLineTokenizer.setNames("ACCOUNT_NUMBER", "TRX_AMOUNT", "DESCRIPTION", "TRX_DATE", "TRX_TIME", "CUSTOMER_ID");

        BeanWrapperFieldSetMapper<Transaction> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Transaction.class);

        lineMapper.setLineTokenizer(delimitedLineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public TransactionProcessor processor() {
        return new TransactionProcessor();
    }


    public RepositoryItemWriter<Transaction> writer() {

        RepositoryItemWriter<Transaction> writer = new RepositoryItemWriter<>();
        writer.setRepository(transactionRepository);
        writer.setMethodName("save");
        return writer;
    }

    @Bean
    public Step step1(FlatFileItemReader<Transaction> itemReader) {
        return stepBuilderFactory.get("txt-step").<Transaction, Transaction>chunk(10)
                .reader(itemReader)
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob(FlatFileItemReader<Transaction> itemReader) {
        return jobBuilderFactory.get("import-trx-job")
                .flow(step1(itemReader))
                .end()
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }

}
