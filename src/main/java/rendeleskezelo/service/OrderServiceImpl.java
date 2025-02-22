package rendeleskezelo.service;


import rendeleskezelo.exception.NotEnoughLampException;
import rendeleskezelo.model.Lamp;
import rendeleskezelo.model.LitophaneOrder;
import rendeleskezelo.repository.LampRepository;
import rendeleskezelo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final LampRepository lampRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, LampRepository lampRepository) {
        this.orderRepository = orderRepository;
        this.lampRepository = lampRepository;
    }

    @Override
    public LitophaneOrder addOrder(LitophaneOrder litophaneOrder) {
        if (litophaneOrder.getLamp().getSupply() != 0){
            Lamp lamp1 = lampRepository.findLampById(litophaneOrder.getLamp().getId());
            lamp1.setSupply(lamp1.getSupply()-1);
            lampRepository.save(lamp1);
        }
        else {throw new NotEnoughLampException(litophaneOrder.getLamp().getName());
        }
        return orderRepository.save(litophaneOrder);
    }

    @Override
    public LitophaneOrder updateOrder(LitophaneOrder litophaneOrder) {
       LitophaneOrder order1 = orderRepository.findOrderById(litophaneOrder.getId());
       if(!order1.getLamp().getId().equals(litophaneOrder.getLamp().getId())){
        if (litophaneOrder.getLamp().getSupply() != 0){
            Lamp lamp1 = lampRepository.findLampById(litophaneOrder.getLamp().getId());
            lamp1.setSupply(lamp1.getSupply()-1);
            lampRepository.save(lamp1);
            Lamp lamp2 = lampRepository.findLampById(order1.getLamp().getId());
            lamp2.setSupply(lamp2.getSupply()+1);
            lampRepository.save(lamp2);
        }
        else {throw new NotEnoughLampException(litophaneOrder.getLamp().getName());
        }}


        order1.setId(litophaneOrder.getId());
        order1.setComment(litophaneOrder.getComment());
        order1.setMaterialCost(litophaneOrder.getMaterialCost());
        order1.setRevenue(litophaneOrder.getRevenue());
        order1.setLamp(litophaneOrder.getLamp());
        order1.setCustomer(litophaneOrder.getCustomer());
        order1.setStatus(litophaneOrder.getStatus());
        order1.setName(litophaneOrder.getName());
        order1.setDeadline(litophaneOrder.getDeadline());

        if (!orderRepository.existsById(order1.getId())) {
            throw new IllegalArgumentException("Order with ID " + order1.getId() + " does not exist.");
        }
        return orderRepository.save(order1);
    }

    @Override
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Order with ID " + id + " does not exist.");
        }
        orderRepository.deleteById(id);
    }

    @Override
    public LitophaneOrder findOrderById(Long id) {
        return orderRepository.findOrderById(id);
    }

    @Override
    public List<LitophaneOrder> findAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<LitophaneOrder> findOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public List<LitophaneOrder> findOrdersByStatusId(Long statusId) {
        return orderRepository.findByStatusId(statusId);
    }

    @Override
    public List<LitophaneOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<LitophaneOrder> findOrdersWithDeadlineWithinAWeek() {
        LocalDate today = LocalDate.now();
        LocalDate nextWeek = today.plusDays(7);

        return orderRepository.findOrdersWithDeadlineBetween(today,nextWeek);
    }

    @Override
    public int sumPrice() {
        List<LitophaneOrder> Orders = orderRepository.findAll();
        int sum = 0;

        for (LitophaneOrder order : Orders) {
           sum+= order.getRevenue();
        }

        return sum;
    }

    @Override
    public int sumProfit() {
        int out = 0;
        List<LitophaneOrder> Orders = orderRepository.findAll();
        for (LitophaneOrder order : Orders) {
            out+= order.getLamp().getPrice();
            out+= order.getMaterialCost();
        }
        int in = 0;

        for (LitophaneOrder order : Orders) {
            in+= order.getRevenue();
        }



        return in - out;
    }

    @Override
    public int sumOut() {
        List<LitophaneOrder> Orders = orderRepository.findAll();
        int sum = 0;

        for (LitophaneOrder order : Orders) {
            sum+= order.getLamp().getPrice();
            sum+= order.getMaterialCost();
        }

        return sum;
    }


}
