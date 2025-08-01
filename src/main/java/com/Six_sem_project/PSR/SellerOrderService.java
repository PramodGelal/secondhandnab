package com.Six_sem_project.PSR;


import com.Six_sem_project.PSR.OrderEntity;
import com.Six_sem_project.PSR.Item;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SellerOrderService {

    private final OrderRepository orderRepo;
    private final ItemRepository itemRepo;

    public SellerOrderService(OrderRepository orderRepo, ItemRepository itemRepo) {
        this.orderRepo = orderRepo;
        this.itemRepo = itemRepo;
    }

    public List<OrderedItemInfo> getOrderedItemsBySeller(String sellerEmail) {
        List<OrderEntity> orders = orderRepo.findBySellerEmail(sellerEmail);
        List<OrderedItemInfo> orderedItemInfos = new ArrayList<>();


        for (OrderEntity order : orders) {
            Item item = itemRepo.findByIdAndTitle(order.getItemId(), order.getItemTitle());
            System.out.println("Id yesto aaudo ray xa heer ..........."+order.getItemId()+"item ko title "+order.getItemTitle());
            if (item != null) {
                OrderedItemInfo info = new OrderedItemInfo();
                info.setOrderId(order.getId());
                info.setBuyerName(order.getName());
                info.setBuyerEmail(order.getEmail());
                info.setBuyerAddress(order.getAddress());
                info.setOrderedQuantity(order.getItemQuantity());

                info.setItemId(item.getId());
                info.setItemTitle(item.getTitle());
                info.setItemImageUrl(item.getImageUrl());
                info.setItemDescription(item.getDescription());
                info.setItemPrice(item.getPrice());

                orderedItemInfos.add(info);
            }
        }

        return orderedItemInfos;
    }
}
