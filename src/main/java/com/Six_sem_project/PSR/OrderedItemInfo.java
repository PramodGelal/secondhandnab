package com.Six_sem_project.PSR;

import lombok.Data;

@Data
public class OrderedItemInfo {
    private Long orderId;
    private String buyerName;
    private String buyerEmail;
    private String buyerAddress;
    private int orderedQuantity;

    private Long itemId;
    private String itemTitle;
    private String itemImageUrl;
    private String itemDescription;
    private String itemPrice;
}
