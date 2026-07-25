package com.kmbeast.service;
import com.kmbeast.pojo.api.R;
import com.kmbeast.pojo.dto.MessageQueryDTO;
import com.kmbeast.pojo.dto.MessageSaveDTO;
import com.kmbeast.pojo.vo.MessageVO;
import java.util.List;
public interface MessageService { R<Void> save(List<MessageSaveDTO> dto); R<List<MessageVO>> list(MessageQueryDTO dto); R<List<MessageVO>> listUser(MessageQueryDTO dto); R<Long> unreadCount(); R<Void> setMessageStatus(); void notifyUser(Integer userId, Integer type, String content); }
