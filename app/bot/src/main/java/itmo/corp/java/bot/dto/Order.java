package itmo.corp.java.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @JsonProperty
    private Long id;
    @JsonProperty
    private Timestamp createdAt;
    @JsonProperty
    private Integer packsCount;
    @JsonProperty
    private Status status;

}
