package io.github.jerryshell.bot.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.github.jerryshell.bot.config.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class BotController {
    @Resource
    private BotConfig botConfig;

    @PostMapping("/bot/taiga/webhook")
    public Object taigaWebhook(
            @RequestParam String channelId,
            @RequestBody String payload
    ) {
        log.info("payload {}", payload);

        JSONObject payloadJson = JSONUtil.parseObj(payload);

        String type = payloadJson.getOrDefault("type", "ERROR").toString();
        log.info("type {}", type);

        String message = buildDetailByType(type, payloadJson);
        log.info("message {}", message);

        if (message == null) {
            return payload;
        }

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("channel_id", channelId);
        messageMap.put("message", message);
        log.info("messageMap {}", messageMap);

        return sendMessage(JSONUtil.toJsonStr(messageMap));
    }

    public String buildDetailByType(String type, JSONObject payloadJson) {
        String action = payloadJson.getOrDefault("action", "ERROR").toString();

        switch (type) {
            case "milestone": {
                return buildMilestoneMessageByAction(action, payloadJson);
            }
            case "userstory": {
                return buildUserStoryMessageByAction(action, payloadJson);
            }
            case "task": {
                return buildTaskMessageByAction(action, payloadJson);
            }
            case "issue": {
                return buildIssueMessageByAction(action, payloadJson);
            }
            case "wikipage": {
                return buildWikiPageMessageByAction(action, payloadJson);
            }
        }

        log.info("```json\n" +
                payloadJson.toString() +
                "\n" +
                "```");

        return null;
    }

    private String buildMilestoneMessageByAction(String action, JSONObject payloadJson) {
        switch (action) {
            case "create": {
                return buildMilestoneCreateMessage(action, payloadJson);
            }
            case "delete": {
                return buildMilestoneDeleteMessage(action, payloadJson);
            }
            case "change": {
                return buildMilestoneChangeMessage(action, payloadJson);
            }
        }
        return "```json\n" +
                payloadJson.toString() +
                "\n" +
                "```";
    }

    private String buildMilestoneCreateMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String name = payloadJson.getByPath("data.name").toString();
        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Create Milestone By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Name\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        name,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildMilestoneDeleteMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String name = payloadJson.getByPath("data.name").toString();
        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Delete Milestone By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Name\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        name,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildMilestoneChangeMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String name = payloadJson.getByPath("data.name").toString();
        String permalink = payloadJson.getByPath("data.permalink").toString();

        String diff = payloadJson.getByPath("change.diff").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Change Milestone By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Name\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        name,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        messageBuilder.append("##### Diff\n")
                .append(StrUtil.format(
                        "`{}`",
                        diff
                ))
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildUserStoryMessageByAction(String action, JSONObject payloadJson) {
        switch (action) {
            case "create": {
                return buildUserStoryCreateMessage(action, payloadJson);
            }
            case "delete": {
                return buildUserStoryDeleteMessage(action, payloadJson);
            }
            case "change": {
                return buildUserStoryChangeMessage(action, payloadJson);
            }
        }
        return "```json\n" +
                payloadJson.toString() +
                "\n" +
                "```";
    }

    private String buildUserStoryCreateMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String subject = payloadJson.getByPath("data.subject").toString();
        String description = payloadJson.getByPath("data.description").toString();

        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Create User Story By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Subject\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        subject,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Description\n")
                .append(description)
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildUserStoryDeleteMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String subject = payloadJson.getByPath("data.subject").toString();
        String description = payloadJson.getByPath("data.description").toString();

        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Delete User Story By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Subject\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        subject,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Description\n")
                .append(description)
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildUserStoryChangeMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String subject = payloadJson.getByPath("data.subject").toString();
        String description = payloadJson.getByPath("data.description").toString();

        String diff = payloadJson.getByPath("change.diff").toString();

        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Change User Story By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Subject\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        subject,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Description\n")
                .append(description)
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        messageBuilder.append("##### Diff\n")
                .append(StrUtil.format(
                        "`{}`",
                        diff
                ))
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildTaskMessageByAction(String action, JSONObject payloadJson) {
        switch (action) {
            case "create": {
                return buildTaskCreateMessage(action, payloadJson);
            }
            case "delete": {
                return buildTaskDeleteMessage(action, payloadJson);
            }
            case "change": {
                return buildTaskChangeMessage(action, payloadJson);
            }
        }
        return "```json\n" +
                payloadJson.toString() +
                "\n" +
                "```";
    }

    private String buildTaskCreateMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String subject = payloadJson.getByPath("data.subject").toString();
        String description = payloadJson.getByPath("data.description").toString();

        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Create Task By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Subject\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        subject,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Description\n")
                .append(description)
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildTaskDeleteMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String subject = payloadJson.getByPath("data.subject").toString();
        String description = payloadJson.getByPath("data.description").toString();

        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Delete Task By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Subject\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        subject,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Description\n")
                .append(description)
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildTaskChangeMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String subject = payloadJson.getByPath("data.subject").toString();
        String description = payloadJson.getByPath("data.description").toString();

        String diff = payloadJson.getByPath("change.diff").toString();

        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Change Task By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Subject\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        subject,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Description\n")
                .append(description)
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        messageBuilder.append("##### Diff\n")
                .append(StrUtil.format(
                        "`{}`",
                        diff
                ))
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildIssueMessageByAction(String action, JSONObject payloadJson) {
        switch (action) {
            case "create": {
                return buildIssueCreateMessage(action, payloadJson);
            }
            case "delete": {
                return buildIssueDeleteMessage(action, payloadJson);
            }
            case "change": {
                return buildIssueChangeMessage(action, payloadJson);
            }
        }
        return "```json\n" +
                payloadJson.toString() +
                "\n" +
                "```";
    }

    private String buildIssueCreateMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String subject = payloadJson.getByPath("data.subject").toString();
        String description = payloadJson.getByPath("data.description").toString();

        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Create Issue By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Subject\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        subject,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Description\n")
                .append(description)
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildIssueDeleteMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String subject = payloadJson.getByPath("data.subject").toString();
        String description = payloadJson.getByPath("data.description").toString();

        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Delete Issue By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Subject\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        subject,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Description\n")
                .append(description)
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildIssueChangeMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String subject = payloadJson.getByPath("data.subject").toString();
        String description = payloadJson.getByPath("data.description").toString();
        String permalink = payloadJson.getByPath("data.permalink").toString();

        String diff = payloadJson.getByPath("change.diff").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Change Issue By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Subject\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        subject,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Description\n")
                .append(description)
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        messageBuilder.append("##### Diff\n")
                .append(StrUtil.format(
                        "`{}`",
                        diff
                ))
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildWikiPageMessageByAction(String action, JSONObject payloadJson) {
        switch (action) {
            case "create": {
                return buildWikiPageCreateMessage(action, payloadJson);
            }
            case "delete": {
                return buildWikiPageDeleteMessage(action, payloadJson);
            }
            case "change": {
                return buildWikiPageChangeMessage(action, payloadJson);
            }
        }
        return "```json\n" +
                payloadJson.toString() +
                "\n" +
                "```";
    }

    private String buildWikiPageCreateMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String slug = payloadJson.getByPath("data.slug").toString();

        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Create Wiki Page By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Slug\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        slug,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildWikiPageDeleteMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String slug = payloadJson.getByPath("data.slug").toString();

        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Delete Wiki Page By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Slug\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        slug,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        return messageBuilder.toString();
    }

    private String buildWikiPageChangeMessage(String action, JSONObject payloadJson) {
        String dateStr = payloadJson.getOrDefault("date", "ERROR").toString();
        LocalDateTime date = DateUtil.parseLocalDateTime(dateStr, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        String userFullName = payloadJson.getByPath("by.full_name").toString();
        String userPermalink = payloadJson.getByPath("by.permalink").toString();

        String projectName = payloadJson.getByPath("data.project.name").toString();
        String projectPermalink = payloadJson.getByPath("data.project.permalink").toString();

        String slug = payloadJson.getByPath("data.slug").toString();

        String permalink = payloadJson.getByPath("data.permalink").toString();

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("#### Project\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        projectName,
                        projectPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Action\n")
                .append(StrUtil.format(
                        "Change Wiki Page By [{}]({})",
                        userFullName,
                        userPermalink
                ))
                .append("\n");
        messageBuilder.append("##### Slug\n")
                .append(StrUtil.format(
                        "[{}]({})",
                        slug,
                        permalink
                ))
                .append("\n");
        messageBuilder.append("##### Date\n")
                .append(date)
                .append("\n");
        return messageBuilder.toString();
    }

    public HttpResponse sendMessage(String message) {
        HttpResponse response = HttpRequest.post(botConfig.getMattermostApi())
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + botConfig.getToken())
                .body(message)
                .execute();
        log.info("response {}", response);

        return response;
    }
}
