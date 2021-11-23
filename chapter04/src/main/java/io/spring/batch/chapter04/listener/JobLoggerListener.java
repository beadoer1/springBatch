package io.spring.batch.chapter04.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

/**
 * 1. JobExecutionListener 를 구현하여 Listener 구성하는 예시
 */
//public class JobLoggerListener implements JobExecutionListener {
//
//    private static final String START_MESSAGE = "%s is beginning execution%n";
//    private static final String END_MESSAGE = "%s has completed with the status %s%n";
//
//    @Override
//    public void beforeJob(JobExecution jobExecution) {
//        System.out.printf(START_MESSAGE, jobExecution.getJobInstance().getJobName());
//    }
//
//    @Override
//    public void afterJob(JobExecution jobExecution) {
//        System.out.printf(END_MESSAGE, jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
//    }
//}


/**
 * 2. @BeforeJob, @AfterJob 어노테이션 활용하여 Listener 구성하는 예시
 */
public class JobLoggerListener {

    private static final String START_MESSAGE = "%s is beginning execution%n";
    private static final String END_MESSAGE = "%s has completed with the status %s%n";

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        System.out.printf(START_MESSAGE, jobExecution.getJobInstance().getJobName());
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        System.out.printf(END_MESSAGE, jobExecution.getJobInstance().getJobName(), jobExecution.getStatus());
    }
}
