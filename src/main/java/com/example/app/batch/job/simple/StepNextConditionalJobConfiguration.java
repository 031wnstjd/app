package com.example.app.batch.job.simple;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class StepNextConditionalJobConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public Job stepNextConditionalJob() {
        return new JobBuilder("stepNextConditionalJob", jobRepository)
                .start(conditionalJobStep1())
                    .on("FAILED") // FAILED일 경우
                    .to(conditionalJobStep3()) // step3으로 이동한다.
                    .on("*") // step3의 결과와 관계 없이
                    .end() // step3으로 이동하면 Flow가 종료된다.
                .from(conditionalJobStep1()) // step1으로부터
                    .on("*") // FAILED 외에 모든 경우
                    .to(conditionalJobStep2()) // step2로 이동한다.
                    .next(conditionalJobStep3()) // step2가 정상 종료되면 step3으로 이동한다.
                    .on("*") // step3의 결과와 관계 없이
                    .end() // step3으로 이동하면 Flow가 종료된다.
                .end()
                .build();
    }

    @Bean
    public Step conditionalJobStep1() {
        return new StepBuilder("conditionalJobStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step1");

                    /**
                     * ExitStatus를 FAILED로 지정한다.
                     * 해당 status를 보고 flow가 진행된다.
                     */
//                    contribution.setExitStatus(ExitStatus.FAILED);

                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step conditionalJobStep2() {
        return new StepBuilder("conditionalJobStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step2");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step conditionalJobStep3() {
        return new StepBuilder("conditionalJobStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step3");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
}
