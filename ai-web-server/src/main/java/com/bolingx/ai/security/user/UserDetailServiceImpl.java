package com.bolingx.ai.security.user;

import com.bolingx.ai.entity.UserEntity;
import com.bolingx.ai.mapper.UserMapper;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
@Setter
public class UserDetailServiceImpl implements UserDetailsService {

    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userMapper.selectByUsername(username);
        if (userEntity == null) {
            userEntity = userMapper.selectOneByEmail(username);
        }
        if (userEntity == null) {
            throw new UsernameNotFoundException("");
        }
        return new UserDetailImpl(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword());
    }
}
