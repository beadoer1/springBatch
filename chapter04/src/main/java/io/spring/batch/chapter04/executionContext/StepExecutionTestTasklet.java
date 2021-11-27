package io.spring.batch.chapter04.executionContext;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;

public class StepExecutionTestTasklet implements Tasklet {

    private static final String HELLO_WORLD = "Hello, %s";


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        String name = (String) chunkContext.getStepContext()
                .getJobParameters()
                .get("name");

        ExecutionContext stepContext = chunkContext.getStepContext()
                .getStepExecution()
                .getExecutionContext();

        stepContext.put("user.name", name);

        System.out.printf(HELLO_WORLD + "%n", name);
        return RepeatStatus.FINISHED;
    }
}
