package com.Six_sem_project.PSR;

import com.Six_sem_project.PSR.Item;
import com.Six_sem_project.PSR.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
    public List<Item> getAllItems_approve() {
        return itemRepository.findByAdminapporalTrue();
    }
    public void setApproval(Long id, boolean approved) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        item.setAdminapporal(approved);
        itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }



}
