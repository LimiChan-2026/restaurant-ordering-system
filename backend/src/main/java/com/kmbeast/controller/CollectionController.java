package com.kmbeast.controller;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesIdDTO;
import com.kmbeast.pojo.vo.CollectionVO;
import com.kmbeast.service.CollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collection")
@RequiredArgsConstructor
public class CollectionController {
    private final CollectionService collectionService;
    @PostMapping("/saveOrCancel") public R<Void> saveOrCancel(@Valid @RequestBody DishesIdDTO dto) { return collectionService.saveOrCancel(dto); }
    @PostMapping("/listUser") public R<List<CollectionVO>> listUser() { return collectionService.listUser(); }
    @GetMapping("/isCollected/{dishesId}") public R<Boolean> isCollected(@PathVariable Integer dishesId) { return collectionService.isCollected(dishesId); }
}
