package com.netfliz.netfliz.service;

import com.netfliz.netfliz.api.ConfigsApiDelegate;
import com.netfliz.netfliz.constant.CacheKey;
import com.netfliz.netfliz.entity.ConfigEntity;
import com.netfliz.netfliz.exception.BadRequestException;
import com.netfliz.netfliz.mapper.ConfigMapper;
import com.netfliz.netfliz.model.Config;
import com.netfliz.netfliz.model.ConfigPage;
import com.netfliz.netfliz.repository.ConfigRepository;
import com.netfliz.netfliz.util.AuthUtils;
import com.netfliz.netfliz.util.JsonUtils;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfigService implements ConfigsApiDelegate {
    private final ConfigRepository configRepository;
    private final ConfigMapper configMapper;
    private final RedisService redisService;
    private final AuthUtils authUtils;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ConfigPage> getConfigs(Integer page,
                                                 Integer pageSize,
                                                 Boolean active) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").descending());
        Page<ConfigEntity> configEntities = configRepository.findConfigsByActive(active, pageable);

        return ResponseEntity.ok(buildPage(configEntities));
    }

    @Override
    public ResponseEntity<Config> getActiveConfig() {
        var configCache = redisService.get(CacheKey.CACHE_CONFIG_ACTIVE, Config.class);
        if (Objects.nonNull(configCache)) {
            return ResponseEntity.ok(configCache);
        }

        Optional<ConfigEntity> configEntity = configRepository.findByActive(Boolean.TRUE);
        if (configEntity.isEmpty()) {
            throw new BadRequestException("No active config found");
        }

        var config = configMapper.mapToConfig(configEntity.get());
        redisService.set(CacheKey.CACHE_CONFIG_ACTIVE, config, CacheKey.CACHE_ONE_DAY);

        return ResponseEntity.ok(config);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Config> getConfigById(Integer id) {
        ConfigEntity entity = configRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Config not found")
        );

        return ResponseEntity.ok(configMapper.mapToConfig(entity));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Config> createConfig(Config config) {
        validateConfig(config);
        var user = authUtils.getCurrentUser();

        return ResponseEntity.ok(configMapper.mapToConfig(configRepository.save(configMapper.mapToEntity(config, user))));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Config> updateConfigById(Integer id, Config config) {
        validateConfig(config);

        ConfigEntity entity = configRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Config not found")
        );
        entity.setConfig(JsonUtils.parse(config.getConfig()));
        entity.setActive(config.getActive());

        return ResponseEntity.ok(configMapper.mapToConfig(configRepository.save(entity)));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> deleteConfigById(Integer id) {
        ConfigEntity entity = configRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Config not found")
        );

        configRepository.delete(entity);
        return ResponseEntity.ok(true);
    }

    private void validateConfig(Config config) {
        if (Strings.isBlank(config.getConfig())) {
            throw new BadRequestException("Config is required");
        }

        if (!JsonUtils.validJson(config.getConfig())) {
            throw new ValidationException("Config is invalid");
        }
    }

    private ConfigPage buildPage(Page<ConfigEntity> resultPage) {
        ConfigPage configPage = new ConfigPage();
        configPage.setPage(resultPage.getPageable().getPageNumber());
        configPage.setPageSize(resultPage.getPageable().getPageSize());
        configPage.setTotal((int) resultPage.getTotalElements());
        configPage.setTotalPages(resultPage.getTotalPages());
        configPage.setItems(configMapper.mapToConfigs(resultPage.getContent()));
        return configPage;
    }
}
