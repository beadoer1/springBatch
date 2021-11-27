package io.spring.batch.chapter04;

import io.spring.batch.chapter04.executionContext.JobExecutionTestTasklet;
import io.spring.batch.chapter04.executionContext.StepExecutionTestTasklet;
import io.spring.batch.chapter04.incrementer.DailyJobTimestamper;
import io.spring.batch.chapter04.listener.JobLoggerListener;
import io.spring.batch.chapter04.validator.ParameterValidator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.CompositeJobParametersValidator;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.listener.JobListenerFactoryBean;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sun.jvm.hotspot.HelloWorld;

import java.util.Arrays;

@EnableBatchProcessing
@SpringBootApplication
public class HelloWorldJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * CompositeJobParametersValidator.class
     * 하나보다 많은 validator 사용 시 위 클래스를 이용하여
     * 여러 validator 를 하나로 만들어 준다.
     */
    @Bean
    public CompositeJobParametersValidator validator() {
        CompositeJobParametersValidator validator = new CompositeJobParametersValidator();

        DefaultJobParametersValidator defaultJobParametersValidator =
                new DefaultJobParametersValidator(
                        new String[]{"fileName"},
//                        new String[]{"name", "run.id"} // JobParametersIncrementer 를 구현한 RunIdIncrementer 가 기본적으로 run.id 인 long 타입 파라미터 값을 증가시킨다.
                        new String[]{"name", "currentDate"} // custom JobParametersIncrementer 인 DailyJobTimestamper 를 활용하기 위한 param
                );

        defaultJobParametersValidator.afterPropertiesSet();

       validator.setValidators(
                Arrays.asList(new ParameterValidator(),
                        defaultJobParametersValidator)
        );

        return validator;
    }

    @Bean
    public Job job() {
        return this.jobBuilderFactory.get("basicJob")
                .start(step1())
                .validator(validator())
//                .incrementer(new RunIdIncrementer())
                .incrementer(new DailyJobTimestamper())
//                .listener(new JobLoggerListener()) // JobExecutionListener 구현하는 경우
                .listener(JobListenerFactoryBean.getListener(new JobLoggerListener())) // 어노테이션 사용하는 경우
                .build();
    }

    @Bean
    public Step step1() {
        return this.stepBuilderFactory.get("step1")
//                .tasklet(helloWorldTasklet(null, null)) // 늦은 바인딩 시 Scope Bean 생성 시 인자를 넣어주므로 사전에 어떤 값을 넣어도 상관없다.
//                .tasklet(new JobExecutionTestTasklet()) // JobExecutionTestTasklet test 를 위한 tasklet 실행
                .tasklet(new StepExecutionTestTasklet()) // StepExecutionTestTasklet test 를 위한 tasklet 실행
                .build();
    }


    // 스프링 구성을 사용해 JobParameters 접근하기

    /**
     * // @Bean
     * private Tasklet helloWorldTasklet() {
     * return (contribution, chunkContext) -> {
     * String name = (String) chunkContext.getStepContext()
     * .getJobParameters()
     * .get("name");
     * <p>
     * System.out.printf("Hello, %s!!", name);
     * return RepeatStatus.FINISHED;
     * };
     * }
     **/

    // 늦은 바인딩을 사용해 잡 파라미터 얻어오기
    @StepScope
    @Bean
    public Tasklet helloWorldTasklet(
            @Value("#{jobParameters['name']}") String name,
            @Value("#{jobParameters['fileName']}") String fileName) {

        return ((contribution, chunkContext) -> {
            System.out.printf("Hello, %s!%n", name);
            System.out.printf("fileName = %s!%n", fileName);
            return RepeatStatus.FINISHED;
        });
    }

    public static void main(String[] args){
        SpringApplication.run(HelloWorldJob.class, args);
    }
}
