package itmo.corp.java.bot.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import itmo.corp.java.bot.dto.CapacityResponse;
import itmo.corp.java.bot.dto.ListOrderResponse;
import itmo.corp.java.bot.dto.OrderRequest;
import itmo.corp.java.bot.dto.StatusRequestAndResponse;
import itmo.corp.java.bot.dto.UserAddDto;


@Slf4j
public class ScrapperClient {

    private final WebClient webClient;

    private final String wentWrongMessage = "Что-то пошло не так. Проблема на нашей стороне, повторите попытку позже";

    public ScrapperClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public ScrapperClient(String baseUrl) {
        this.webClient = WebClient.create(baseUrl);
    }

    public void registerChat(Long tgChatId, UserAddDto userAddDto) {
        log.info("Sending request POST /tg-chat/{id}");
        webClient.post().uri("/tg-chat/{id}", tgChatId).bodyValue(userAddDto).exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new ScrapperClientException("Некорректно указан ID или такой чат уже зарегистрирован");
            } else if (response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                throw new ScrapperClientException(
                    wentWrongMessage);
            }
            return Mono.empty();
        }).block();
    }

    public void deleteChat(Long tgChatId) {
        webClient.delete().uri("/tg-chat/{id}", tgChatId).exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new ScrapperClientException("Некорректно указан ID");
            } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new ScrapperClientException("Чат с таким ID не найден");
            } else if (response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                throw new ScrapperClientException(
                    wentWrongMessage);
            }
            return Mono.empty();
        }).block();
    }

    public CapacityResponse getCapacity() {
        log.info("Sending request GET /capacity");
        return webClient.get().uri("/capacity").exchangeToMono(r -> {
            if (r.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                throw new ScrapperClientException(
                    wentWrongMessage);
            }
            return r.bodyToMono(CapacityResponse.class);
        }).block();
    }

    public StatusRequestAndResponse getStatus(Long tgChatId) {
        log.info("Sending request GET /orders/{chat-id}/status");
        return webClient.get().uri("/orders/{chat-id}/status", tgChatId).exchangeToMono(r -> {
            if (r.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new ScrapperClientException("Некорректно указан ID");
            } else if (r.statusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new ScrapperClientException("У вас нет заказов сегодня или прачечная закрыта!");
            } else if (r.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                throw new ScrapperClientException(
                    wentWrongMessage);
            }
            return r.bodyToMono(StatusRequestAndResponse.class);
        }).block();
    }

    public ListOrderResponse getHistory(Long tgChatId) {
        log.info("Sending request GET /orders/{chat-id}");
        return webClient.get().uri("/orders/{chat-id}", tgChatId).exchangeToMono(r -> {
            if (r.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new ScrapperClientException("Некорректно указан ID");
            } else if (r.statusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new ScrapperClientException("Чат с таким ID не найден!");
            } else if (r.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                throw new ScrapperClientException(
                    wentWrongMessage);
            }
            return r.bodyToMono(ListOrderResponse.class);
        }).block();
    }

    public void changeStatus(Long orderId, StatusRequestAndResponse request) {
        log.info("Sending request PUT /orders/{id}");
        webClient.put().uri("/orders/{id}", orderId).bodyValue(request).exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new ScrapperClientException("Вы не можете менять статус DONE или CANCELLED заказа!");
            } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new ScrapperClientException("Заказ с таким ID не найден!");
            } else if (response.statusCode().equals(HttpStatus.UNPROCESSABLE_ENTITY)) {
                throw new ScrapperClientException("Вы не можете сменить статус этого заказа на предложенный!");
            }   else if (response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                throw new ScrapperClientException(wentWrongMessage);
            }
            return Mono.empty();
        }).block();
    }

    public void addCapacity(long capacity) {
        log.info("Sending request PUT /capacity/add");
        webClient.put().uri(uriBuilder -> uriBuilder
            .path("/capacity/add")
            .queryParam("value", capacity)
            .build()).exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new ScrapperClientException("Некорректные параметры запроса!");
            } else if (response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                throw new ScrapperClientException(wentWrongMessage);
            }
            return Mono.empty();
        }).block();
    }

    public void createOrder(OrderRequest request) {
        log.info("Sending request POST /orders");
        webClient.post().uri(uriBuilder -> uriBuilder.path("/orders").build())
            .bodyValue(request).exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new ScrapperClientException("Некорректные параметры запроса или заказ уже сформирован на сегодня!");
            } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new ScrapperClientException("Пользователь с вашим ID не найден или прачечная сегодня закрыта!");
            } else if (response.statusCode().equals(HttpStatus.UNPROCESSABLE_ENTITY)) {
                throw new ScrapperClientException("К сожалению на сегодня мест не осталось!");
            } else if (response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                throw new ScrapperClientException(wentWrongMessage);
            }
            return Mono.empty();
        }).block();
    }

}
