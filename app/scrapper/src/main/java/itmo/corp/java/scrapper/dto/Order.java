package itmo.corp.java.scrapper.dto;

import itmo.corp.java.scrapper.model.commonDto.Status;
import itmo.corp.java.scrapper.model.jpa.OrderEntity;
import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;

@Data
@Builder
public class Order {

    private Long id;
    private Timestamp createdAt;
    private Integer packsCount;
    private Status status;

    public static Order fromEntity(OrderEntity orderEntity) {
        return Order.builder()
            .id(orderEntity.getId())
            .createdAt(orderEntity.getCreatedAt())
            .packsCount(orderEntity.getPacksCount())
            .status(orderEntity.getStatus())
            .build();
    }
}
