package com.example.app.batch.job.writer;

import com.example.app.domain.Pay;
import com.example.app.domain.Pay2;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class JpaItemWriterJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    private final int chunkSize = 10;

    @Bean
    public Job jpaItemWriterJob() {
        return new JobBuilder("jpaItemWriterJob", jobRepository)
                .start(jpaItemWriterStep())
                .build();
    }

    @Bean
    public Step jpaItemWriterStep() {
        return new StepBuilder("jpaPagingItemReaderStep", jobRepository)
                .<Pay, Pay2>chunk(chunkSize, transactionManager)
                .reader(jpaItemWriterReader())
                .processor(jpaItemProcessor())
                .writer(jpaItemWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<Pay> jpaItemWriterReader() {
        return new JpaPagingItemReaderBuilder<Pay>()
                .name("jpaItemWriterReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT p FROM Pay p")
                .build();
    }

    @Bean
    public ItemProcessor<Pay, Pay2> jpaItemProcessor() {
        return pay -> new Pay2(pay.getAmount(), pay.getTxName(), pay.getTxDateTime());
    }

    @Bean
    public JpaItemWriter<Pay2> jpaItemWriter() {
        JpaItemWriter<Pay2> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

}
