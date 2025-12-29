package com.world.back.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

@Data
@Component
@ConfigurationProperties(prefix = "template")
public class TemplateConfig {
    private String uploadPath = "./uploads/templates/";
    private String[] allowedExtensions = {"doc", "docx"};
    @DataSizeUnit(DataUnit.MEGABYTES)
    private DataSize maxFileSize = DataSize.ofMegabytes(10); // 默认10MB
}