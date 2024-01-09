package itmo.corp.java.scrapper.dto;

import java.util.List;

public record ListOrderResponse(List<Order> orders, int size) {



}
