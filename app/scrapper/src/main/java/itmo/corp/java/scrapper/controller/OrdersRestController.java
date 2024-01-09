package itmo.corp.java.scrapper.controller;

import itmo.corp.java.scrapper.dto.ListOrderResponse;
import itmo.corp.java.scrapper.dto.OrderRequest;
import itmo.corp.java.scrapper.dto.StatusRequestAndResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import itmo.corp.java.scrapper.service.OrderService;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrdersRestController {

    private final OrderService orderService;


    @GetMapping("/{chat-id}/status")
    public StatusRequestAndResponse getStatus(@PathVariable(name = "chat-id") Long chatId) {
        log.info("--> GET /orders/{chat-id}/status");
        return orderService.getCurrentStatus(chatId);
    }


    @GetMapping("/{chat-id}")
    public ListOrderResponse getHistory(@PathVariable(name = "chat-id") Long chatId) {
        log.info("--> GET /orders/{chat-id}");
        return orderService.getHistory(chatId);
    }

    @PutMapping("/{id}")
    public void changeStatus(@PathVariable(name = "id") Long orderId, @RequestBody StatusRequestAndResponse request) {
        log.info("--> PUT /orders/{id}");
        orderService.changeStatus(orderId, request.status());
    }

    @PostMapping
    public void createOrder(@RequestBody OrderRequest request) {
        log.info("--> POST /orders");
        orderService.createOrder(request);
    }
}
