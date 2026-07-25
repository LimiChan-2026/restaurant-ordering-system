package com.kmbeast.controller;

import com.kmbeast.annotation.AdminOnly;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.*;
import com.kmbeast.service.DishesEvaluationsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dishes-evaluations")
@RequiredArgsConstructor
public class DishesEvaluationsController {
    private final DishesEvaluationsService dishesEvaluationsService;
    @PostMapping("/saveEntity") public R<Void> save(@Valid @RequestBody DishesEvaluationsSaveDTO dto) { return dishesEvaluationsService.save(dto); }
    @PostMapping("/list") public R list(@RequestBody(required = false) DishesEvaluationsQueryDTO dto) { return dishesEvaluationsService.list(dto == null ? new DishesEvaluationsQueryDTO() : dto); }
    @DeleteMapping("/delete/{id}") public R<Void> delete(@PathVariable Integer id) { return dishesEvaluationsService.delete(id); }
    @AdminOnly @PutMapping("/reply") public R<Void> reply(@Valid @RequestBody DishesEvaluationsReplyDTO dto) { return dishesEvaluationsService.reply(dto); }
}
