package io.github.jerryshell.bot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class BotConfig {
    @Value("${bot.mattermost.api}")
    private String mattermostApi;
    @Value("${bot.token}")
    private String token;
}
