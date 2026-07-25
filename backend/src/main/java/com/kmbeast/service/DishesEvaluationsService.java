package com.kmbeast.service;

import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.DishesEvaluationsQueryDTO;
import com.kmbeast.pojo.dto.DishesEvaluationsReplyDTO;
import com.kmbeast.pojo.dto.DishesEvaluationsSaveDTO;

public interface DishesEvaluationsService {
    R<Void> save(DishesEvaluationsSaveDTO saveDTO);
    R list(DishesEvaluationsQueryDTO queryDTO);
    R<Void> delete(Integer id);
    R<Void> reply(DishesEvaluationsReplyDTO replyDTO);
}
