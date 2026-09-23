package org.example.aispringboot.Service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.example.aispringboot.DTO.command.UserLoginCommandDTO;
import org.example.aispringboot.DTO.command.UserRegisterCommandDTO;
import org.example.aispringboot.DTO.response.UserLoginResponseDTO;
import org.example.aispringboot.Service.convert.UserConvert;
import org.example.aispringboot.Util.JwtTokenUtil;
import org.example.aispringboot.common.Result;
import org.example.aispringboot.entity.User;
import org.example.aispringboot.enumClass.UserType;
import org.example.aispringboot.exception.BusinessException;
import org.example.aispringboot.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Resource
    private UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public UserLoginResponseDTO login(UserLoginCommandDTO commandDTO){
        //构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,commandDTO.getUsername())
                .or()
                .eq(User::getEmail,commandDTO.getUsername());
        //调用MP API查询
        User user = userMapper.selectOne(queryWrapper);
        System.out.println(user);
        //查询用户是否存在
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        //验证密码
        String inputPassword = commandDTO.getPassword().trim();
        if(!bCryptPasswordEncoder.matches(inputPassword, user.getPassword())){
            throw new BusinessException("密码错误");
        }

        //检查用户的状态
        if (!user.isActive()) {
            throw new BusinessException("用户已禁用，请联系管理员");
        }


        //生成token
        String token = JwtTokenUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
        System.out.println(token);
        UserLoginResponseDTO.UserDetailResponseDTO userInfo = UserConvert.entityToDetailResponse(user);
        return UserConvert.entityToLoginResponse(token, userInfo);
    }

    public UserLoginResponseDTO.UserDetailResponseDTO register(UserRegisterCommandDTO commandDTO){
        System.out.println(JSONUtil.parseObj(commandDTO));
        //验证密码是否一致
        if (!commandDTO.getPassword().equals(commandDTO.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        //检查用户名是否存在
        LambdaQueryWrapper<User> userNameQuery = new LambdaQueryWrapper<>();
        userNameQuery.eq(User::getUsername, commandDTO.getUsername());
        if (userMapper.selectCount(userNameQuery) > 0) {
            throw new BusinessException("用户名已存在");
        }

        //检查邮箱是否存在
        LambdaQueryWrapper<User> emailQuery = new LambdaQueryWrapper<>();
        emailQuery.eq(User::getEmail, commandDTO.getEmail());
        if (userMapper.selectCount(emailQuery) > 0) {
            throw new BusinessException("邮箱已存在");
        }

        //验证用户类型
        if(!UserType.isValidCode(commandDTO.getUserType())){
            throw new BusinessException("无效的用户类型");
        }

        //创建用户
        String password = commandDTO.getPassword().trim();
        String encodePassword = bCryptPasswordEncoder.encode(password);
        User user = UserConvert.registerCommandToEntity(commandDTO, encodePassword);
        userMapper.insert(user);
        return UserConvert.entityToDetailResponse(user);
    }

    public UserLoginResponseDTO.UserDetailResponseDTO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return UserConvert.entityToDetailResponse(user);
    }

    public void logout(String token) {
        System.out.println("登出操作，token = " + token);
    }

}
