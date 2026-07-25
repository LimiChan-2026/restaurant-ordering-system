package com.kmbeast.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kmbeast.context.UserContext;
import com.kmbeast.mapper.MessageMapper;
import com.kmbeast.mapper.UserMapper;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.MessageQueryDTO;
import com.kmbeast.pojo.dto.MessageSaveDTO;
import com.kmbeast.pojo.entity.Message;
import com.kmbeast.pojo.entity.User;
import com.kmbeast.pojo.vo.MessageVO;
import com.kmbeast.service.MessageService;
import com.kmbeast.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> save(List<MessageSaveDTO> dtoList) {
        for (MessageSaveDTO item : dtoList) {
            if (userMapper.selectById(item.getUserId()) == null) {
                return R.error("用户不存在");
            }
            notifyUser(item.getUserId(), 1, item.getContent());
        }
        return R.ok("消息发送成功");
    }

    @Override
    public R<List<MessageVO>> list(MessageQueryDTO dto) {
        return query(dto, null);
    }

    @Override
    public R<List<MessageVO>> listUser(MessageQueryDTO dto) {
        Integer userId = UserContext.getUserId();
        return userId == null ? R.unauthorized() : query(dto, userId);
    }

    @Override
    public R<Long> unreadCount() {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }
        return R.ok(messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .eq(Message::getReadStatus, false)));
    }

    @Override
    public R<Void> setMessageStatus() {
        Integer userId = UserContext.getUserId();
        if (userId == null) {
            return R.unauthorized();
        }
        Message update = new Message();
        update.setReadStatus(true);
        messageMapper.update(update, new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .eq(Message::getReadStatus, false));
        return R.ok("消息全部已读");
    }

    @Override
    public void notifyUser(Integer userId, Integer type, String content) {
        Message message = new Message();
        message.setUserId(userId);
        message.setType(type);
        message.setContent(content);
        message.setReadStatus(false);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
    }

    private R<List<MessageVO>> query(MessageQueryDTO dto, Integer userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(Message::getUserId, userId);
        }
        if (dto.getType() != null) {
            wrapper.eq(Message::getType, dto.getType());
        }
        wrapper.orderByDesc(Message::getCreateTime);
        Page<Message> page = messageMapper.selectPage(PageUtils.of(dto.getCurrent(), dto.getSize()), wrapper);
        return R.ok(page.getRecords().stream().map(this::toVO).toList(), page.getTotal());
    }

    private MessageVO toVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(message, vo);
        User user = userMapper.selectById(message.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
        }
        return vo;
    }
}
