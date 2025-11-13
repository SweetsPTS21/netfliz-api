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
        return ResponseEntity.ok(configService.getConfigs(page, pageSize, active).getBody());
    }

    @Override
    public ResponseEntity<Config> getActiveConfig() {
        return ResponseEntity.ok(configService.getActiveConfig().getBody());
    }

    @Override
    public ResponseEntity<Config> getConfigById(Integer id) {
        return ResponseEntity.ok(configService.getConfigById(id).getBody());
    }

    @Override
    public ResponseEntity<Config> createConfig(Config config) {
        return ResponseEntity.ok(configService.createConfig(config).getBody());
    }

    @Override
    public ResponseEntity<Config> updateConfigById(Integer id, Config config) {
        return ResponseEntity.ok(configService.updateConfigById(id, config).getBody());
    }

    @Override
    public ResponseEntity<Boolean> deleteConfigById(Integer id) {
        return ResponseEntity.ok(configService.deleteConfigById(id).getBody());
    }
}
