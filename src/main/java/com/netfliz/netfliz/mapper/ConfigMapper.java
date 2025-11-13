package com.netfliz.netfliz.mapper;

import com.netfliz.netfliz.entity.ConfigEntity;
import com.netfliz.netfliz.entity.UserEntity;
import com.netfliz.netfliz.model.Config;
import com.netfliz.netfliz.util.JsonUtils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConfigMapper {

    public Config mapToConfig(ConfigEntity from) {
        Config to = new Config();

        to.setId(from.getId());
        to.setConfig(JsonUtils.serialize(from.getConfig()));
        to.setActive(from.getActive());

        return to;
    }

    public ConfigEntity mapToEntity(Config from, UserEntity user) {
        ConfigEntity to = new ConfigEntity();

        to.setId(from.getId());
        to.setConfig(JsonUtils.parse(from.getConfig()));
        to.setActive(from.getActive());
        to.setUser(user);

        return to;
    }

    public List<Config> mapToConfigs(List<ConfigEntity> from) {
        return from.stream().map(this::mapToConfig).toList();
    }
}
