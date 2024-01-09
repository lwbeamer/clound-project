package itmo.corp.java.bot.configuration;


import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import javax.jms.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

@Configuration
@EnableJms
@RequiredArgsConstructor
public class JmsConfiguration {

    @Value("${jms.access-key}")
    private String accessKey;

    @Value("${jms.secret-key}")
    private String secretKey;;

    @Bean(name = "SQSProducerConnectionFactory")
    public SQSConnectionFactory SQSConnectionFactory(
        @Value("${jms.queues.scrapper.url}") String serviceEndpoint,
        @Value("${jms.queues.scrapper.region}") String signingRegion
    ) {
        return new SQSConnectionFactory(
            new ProviderConfiguration(),

            AmazonSQSClientBuilder
                .standard()
                .withCredentials(
                    new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(
                            accessKey,
                            secretKey
                        )
                    )
                )
                .withRegion(signingRegion)
                .withEndpointConfiguration(new EndpointConfiguration(
                    serviceEndpoint,
                    signingRegion))
        );
    }

    @Bean(name = "SQSConsumerConnectionFactory")
    public SQSConnectionFactory SQSConsumerConnectionFactory(
        @Value("${jms.queues.scrapper.url}") String serviceEndpoint,
        @Value("${jms.queues.scrapper.region}") String signingRegion
    ) {
        return new SQSConnectionFactory(
            new ProviderConfiguration(),

            AmazonSQSClientBuilder
                .standard()
                .withCredentials(
                    new AWSStaticCredentialsProvider(
                        new BasicAWSCredentials(
                            accessKey,
                            secretKey
                        )
                    )
                )
                .withRegion(signingRegion)
                .withEndpointConfiguration(new EndpointConfiguration(
                    serviceEndpoint,
                    signingRegion)
                )
        );
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
        @Qualifier("SQSConsumerConnectionFactory") SQSConnectionFactory sqsConsumerConnectionFactory
    ) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(sqsConsumerConnectionFactory);
        factory.setDestinationResolver(new DynamicDestinationResolver());
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        return factory;
    }


}
