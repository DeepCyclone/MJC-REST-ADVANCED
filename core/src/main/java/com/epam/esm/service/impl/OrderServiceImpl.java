package com.epam.esm.service.impl;

import com.epam.esm.exception.ServiceErrorCode;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.repository.model.GiftCertificate;
import com.epam.esm.repository.model.Order;
import com.epam.esm.repository.model.User;
import com.epam.esm.repository.template.GiftCertificateRepository;
import com.epam.esm.repository.template.OrderRepository;
import com.epam.esm.repository.template.UserRepository;
import com.epam.esm.service.template.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final GiftCertificateRepository giftCertificateRepository;

    
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,GiftCertificateRepository giftCertificateRepository,UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.giftCertificateRepository = giftCertificateRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Order> getAll(int page,int limit) {
        return orderRepository.readAll(page,limit);
    }

    @Override
    public Order getById(long orderId) {
        Order order = orderRepository.findByID(orderId).orElseThrow(
            () -> new ServiceException(ServiceErrorCode.ORDER_NOT_FOUND,"Cannot fetch order with id = " + orderId));
        order.setCertificates(fetchAssociatedCertificates(orderId));
        return order;
    }

    @Override
    @Transactional
    public Order update(Order orderPatch, long orderId) {
        final Optional<BigDecimal> price = Optional.ofNullable(orderPatch.getPrice());
        Optional.ofNullable(orderPatch.getCertificates()).ifPresent(certs -> {
            orderPatch.setPrice(certs.stream()
            .peek(this::checkExistence)
            .map(cert->giftCertificateRepository.findByID(cert.getId()).get().getPrice())
            .reduce(BigDecimal.ZERO, BigDecimal::add));
            orderRepository.findByID(orderId).ifPresent(order->order.setCertificates(certs));
        });
        price.ifPresent(orderPatch::setPrice);
        boolean result = orderRepository.update(orderPatch, orderId);
        if(!result){
            throw new ServiceException(ServiceErrorCode.ORDER_UPDATE_ERROR,"An error occured while updating order");
        }
        return getById(orderId);
    }

    @Override
    @Transactional
    public void delete(long orderId) {
        if(!orderRepository.deleteByID(orderId)){
            throw new ServiceException(ServiceErrorCode.ORDER_DELETION_ERROR," cannot delete order with id = "+orderId);
        }
    }

    @Override
    @Transactional
    public Order makeOrder(List<Long> certificatesIds,long userId) {
        User user = userRepository.findByID(userId).orElseThrow(() -> new ServiceException(ServiceErrorCode.USER_NOT_FOUND,"User not found with ID = "+userId));
        List<GiftCertificate> certificatesEntities = new LinkedList<>();

        certificatesIds.forEach(certId->certificatesEntities.add(giftCertificateRepository.findByID(certId).orElseThrow(
                ()->new ServiceException(ServiceErrorCode.CERTIFICATE_NOT_FOUND,"cert not found with ID = "+certId))));

        BigDecimal sum = countOrderSum(certificatesEntities);

        Order order = orderRepository.makeOrder(sum).orElseThrow(
            ()->new ServiceException(ServiceErrorCode.ORDER_CREATION_ERROR,"Cannot create order"));

        order.setCertificates(new ArrayList<>(certificatesEntities));
        user.getOrders().add(order);
        return order;
    }

    private List<GiftCertificate> fetchAssociatedCertificates(long orderId){
        return orderRepository.
        findByID(orderId).
        map(Order::getCertificates).
        orElse(Collections.emptyList());
    }

    private void checkExistence(GiftCertificate cert){
        if(!giftCertificateRepository.checkExistence(cert.getId())){
            throw new ServiceException(ServiceErrorCode.CERTIFICATE_NOT_FOUND,"Cannot fetch certificate with ID = " + cert.getId());
        }
    }

    private BigDecimal countOrderSum(List<GiftCertificate> certificates){
        return certificates.
        stream().
        map(GiftCertificate::getPrice).
        reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
