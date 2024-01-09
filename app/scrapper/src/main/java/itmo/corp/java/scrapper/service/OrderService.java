package itmo.corp.java.scrapper.service;

import itmo.corp.java.scrapper.dto.ListOrderResponse;
import itmo.corp.java.scrapper.dto.Order;
import itmo.corp.java.scrapper.dto.OrderRequest;
import itmo.corp.java.scrapper.dto.StatusRequestAndResponse;
import itmo.corp.java.scrapper.exception.DayNotFoundException;
import itmo.corp.java.scrapper.exception.IncorrectStatusChangeException;
import itmo.corp.java.scrapper.exception.NotEnoughCapacityException;
import itmo.corp.java.scrapper.exception.OrderAlreadyExistException;
import itmo.corp.java.scrapper.exception.OrderNotFoundException;
import itmo.corp.java.scrapper.exception.UnchangeableStatusException;
import itmo.corp.java.scrapper.jms.producer.JmsProducer;
import itmo.corp.java.scrapper.model.commonDto.Status;
import itmo.corp.java.scrapper.model.jpa.DayEntity;
import itmo.corp.java.scrapper.model.jpa.OrderEntity;
import itmo.corp.java.scrapper.model.jpa.UserEntity;
import itmo.corp.java.scrapper.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import itmo.corp.java.scrapper.exception.ChatNotFoundException;
import itmo.corp.java.scrapper.jms.model.StatusMessage;
import itmo.corp.java.scrapper.repository.DayRepository;
import itmo.corp.java.scrapper.repository.UserRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository ordersRepository;
    private final UserRepository userRepository;
    private final DayRepository dayRepository;
    private final JmsProducer jmsProducer;

    public StatusRequestAndResponse getCurrentStatus(Long chatId) {
        Optional<DayEntity> optionalDay = dayRepository.findByDate(LocalDate.now());
        Optional<UserEntity> optionalUser = userRepository.findById(chatId);

        Optional<OrderEntity> optionalOrder = ordersRepository.findByDayAndUserAndStatusNot(
            optionalDay.orElseThrow(() -> new DayNotFoundException("Сегодня прачечная закрыта!")),
            optionalUser.orElseThrow(() -> new ChatNotFoundException("Чат с таким id не найден!")),
            Status.CANCELLED);

        return new StatusRequestAndResponse(optionalOrder.orElseThrow(() ->
            new OrderNotFoundException("Заказ по таким реквизитам не найден!")).getStatus());
    }

    public ListOrderResponse getHistory(Long chatId) {
        Optional<UserEntity> optionalUser = userRepository.findById(chatId);

        List<Order> orders =
            ordersRepository.findAllByUser(optionalUser.orElseThrow(() ->
                new ChatNotFoundException("Чат с таким id не найден!")))
                .stream().map(Order::fromEntity).toList();

        return new ListOrderResponse(orders, orders.size());
    }

    public void changeStatus(Long orderId, Status status) {
        Optional<OrderEntity> optionalOrder = ordersRepository.findById(orderId);
        if (optionalOrder.isEmpty()) throw new OrderNotFoundException("Заказа с таким id не найден!");
        OrderEntity orderEntity = optionalOrder.get();
        if (orderEntity.getStatus().equals(Status.DONE) || orderEntity.getStatus().equals(Status.CANCELLED))
            throw new UnchangeableStatusException("Вы не можете поменять статус у этого заказа!");
        if (orderEntity.getStatus().equals(Status.NEW) && (status.equals(Status.DONE)))
            throw new IncorrectStatusChangeException("Нельзя сменить статус этого заказа на предложенный!");
        if (status.equals(Status.CANCELLED)){
            Optional<DayEntity> optionalDay = dayRepository.findByDate(LocalDate.now());
            DayEntity day = optionalDay.orElseThrow(() -> new RuntimeException("Что-то пошло не так!"));
            day.setCapacity(day.getCapacity() + orderEntity.getPacksCount());
            dayRepository.save(day);
        }
        orderEntity.setStatus(status);
        ordersRepository.save(orderEntity);
        jmsProducer.send(new StatusMessage("Статус заказа с ID %s изменился на %s".formatted(orderEntity.getId(),
                                                             status.name()),orderEntity.getUser().getChatId()));
    }

    public void createOrder(OrderRequest request) {
        Optional<DayEntity> optionalDay = dayRepository.findByDate(LocalDate.now());
        Optional<UserEntity> optionalUser = userRepository.findById(request.chatId());

        Optional<OrderEntity> optionalOrder = ordersRepository.findByDayAndUserAndStatusNot(
            optionalDay.orElseThrow(() -> new DayNotFoundException("Сегодня прачечная закрыта!")),
            optionalUser.orElseThrow(() -> new ChatNotFoundException("Чат с таким id не найден!")),
            Status.CANCELLED);

        if (optionalOrder.isPresent() && !optionalOrder.get().getStatus().equals(Status.CANCELLED))
            throw new OrderAlreadyExistException("Вы можете формировать только один заказ за день!");

        DayEntity dayEntity = optionalDay.get();
        if (dayEntity.getCapacity() < request.packsCount()) throw new NotEnoughCapacityException("К сожалению на сегодня мест не осталось!");
        dayEntity.setCapacity(dayEntity.getCapacity() - request.packsCount());
        dayRepository.save(dayEntity);

        ordersRepository.save(
            OrderEntity.builder()
                .createdAt(Timestamp.from(Instant.now()))
                .day(dayEntity)
                .packsCount(request.packsCount())
                .user(optionalUser.get())
                .status(Status.NEW)
                .build());
    }
}
