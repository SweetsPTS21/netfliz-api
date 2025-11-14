package com.netfliz.netfliz.api;

import com.netfliz.netfliz.model.Config;
import com.netfliz.netfliz.model.ConfigPage;
import com.netfliz.netfliz.service.ConfigService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ConfigController implements ConfigsApi {
    private final ConfigService configService;

    public ResponseEntity<ConfigPage> getConfigs(Integer page,
                                                 Integer pageSize,
                                                 Boolean active) {
        return configService.getConfigs(page, pageSize, active);
    }

    @Override
    public ResponseEntity<Config> getActiveConfig() {
        return configService.getActiveConfig();
    }

    @Override
    public ResponseEntity<Config> getConfigById(Integer id) {
        return configService.getConfigById(id);
    }

    @Override
    public ResponseEntity<Config> createConfig(Config config) {
        return configService.createConfig(config);
    }

    @Override
    public ResponseEntity<Config> updateConfigById(Integer id, Config config) {
        return configService.updateConfigById(id, config);
    }

    @Override
    public ResponseEntity<Boolean> deleteConfigById(Integer id) {
        return configService.deleteConfigById(id);
    }
}
