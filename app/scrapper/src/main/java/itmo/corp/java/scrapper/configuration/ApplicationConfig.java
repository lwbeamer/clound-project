package itmo.corp.java.scrapper.configuration;


import javax.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)

public record ApplicationConfig(@NotNull String test) {


}

