package org.example.aispringboot.Service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.aispringboot.AiService.StructOutput;
import org.example.aispringboot.DTO.command.ConsultationSessionCreateDTO;
import org.example.aispringboot.entity.ConsultationSession;
import org.example.aispringboot.entity.User;
import org.example.aispringboot.mapper.ConsultationSessionMapper;
import org.example.aispringboot.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConsultationSessionService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ConsultationSessionMapper consultationSessionMapper;

    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO){
        //先认证用户是否存在
        User user = userMapper.selectById(userId);
        if (user != null) {
            //创建会话记录
            ConsultationSession session = ConsultationSession.builder()
                    .userId(userId)
                    .sessionTitle(createDTO.getSessionTitle())
                    .startedAt(LocalDateTime.now())
                    .build();
            //如果未提供标题
            if(StrUtil.isBlank(createDTO.getSessionTitle())){
                session.setSessionTitle(String.format("AI助手 - " + DateUtil.format(LocalDateTime.now(),"MM-dd HH:mm")));
            }
            //插入记录
            consultationSessionMapper.insert(session);
            return session;
        }
        return null;
    }

    public List<ConsultationSession> listByUserId(Long userId) {
        LambdaQueryWrapper<ConsultationSession> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsultationSession::getUserId, userId)
                .orderByDesc(ConsultationSession::getStartedAt);
        return consultationSessionMapper.selectList(wrapper);
    }

    public ConsultationSession getById(Long id) {
        return consultationSessionMapper.selectById(id);
    }

}
