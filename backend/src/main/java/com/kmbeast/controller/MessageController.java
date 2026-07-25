package com.kmbeast.controller;
import com.kmbeast.annotation.AdminOnly;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.MessageQueryDTO;
import com.kmbeast.pojo.dto.MessageSaveDTO;
import com.kmbeast.pojo.vo.MessageVO;
import com.kmbeast.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/messages") @RequiredArgsConstructor public class MessageController {
 private final MessageService service;
 @AdminOnly @PostMapping("/saveMessage") public R<Void> save(@Valid @RequestBody List<@Valid MessageSaveDTO> dto){return service.save(dto);}
 @AdminOnly @PostMapping("/list") public R<List<MessageVO>> list(@RequestBody(required=false) MessageQueryDTO dto){return service.list(dto==null?new MessageQueryDTO():dto);}
 @PostMapping("/listUser") public R<List<MessageVO>> listUser(@RequestBody(required=false) MessageQueryDTO dto){return service.listUser(dto==null?new MessageQueryDTO():dto);}
 @GetMapping("/unreadCount") public R<Long> unreadCount(){return service.unreadCount();}
 @PutMapping("/setMessageStatus") public R<Void> setStatus(){return service.setMessageStatus();}
}
