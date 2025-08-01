package com.Six_sem_project.PSR;

import com.Six_sem_project.PSR.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByTitle(String title);
    Optional<Item> findByTitleAndPrice(String title,String price);
    List<Item> findByAdminapporalTrue();
    List<Item> findByEmail(String email);
    Item findByIdAndTitle(Long id, String title);
}
