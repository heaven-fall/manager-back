package com.world.back.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Value("${signature.upload.path:./uploads/signatures/}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        try {
            log.info("开始初始化上传目录");

            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            log.info("项目根目录: {}", projectRoot);

            // 构建上传目录路径
            String uploadDirPath;
            if (uploadPath.startsWith("./")) {
                // 如果是相对路径，转换为绝对路径
                uploadDirPath = projectRoot + File.separator + uploadPath.substring(2);
            } else if (uploadPath.startsWith("/") || uploadPath.contains(":")) {
                // 已经是绝对路径
                uploadDirPath = uploadPath;
            } else {
                // 相对路径但不以./开头
                uploadDirPath = projectRoot + File.separator + uploadPath;
            }

            // 确保路径以分隔符结尾
            if (!uploadDirPath.endsWith(File.separator)) {
                uploadDirPath += File.separator;
            }

            // 创建上传目录
            Path uploadDir = Paths.get(uploadDirPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("创建上传目录: {}", uploadDir.toAbsolutePath());
            }

            log.info("上传目录初始化完成: {}", uploadDir.toAbsolutePath());

        } catch (Exception e) {
            log.error("初始化上传目录失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            log.info("配置静态资源映射");

            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");

            // 构建上传目录的绝对路径
            String uploadDirPath;
            if (uploadPath.startsWith("./")) {
                uploadDirPath = projectRoot + File.separator + uploadPath.substring(2);
            } else if (uploadPath.startsWith("/") || uploadPath.contains(":")) {
                uploadDirPath = uploadPath;
            } else {
                uploadDirPath = projectRoot + File.separator + uploadPath;
            }

            // 确保路径以分隔符结尾
            if (!uploadDirPath.endsWith(File.separator)) {
                uploadDirPath += File.separator;
            }

            // 构建静态资源访问路径
            String externalPath = "file:" + uploadDirPath;

            log.info("映射静态资源:");
            log.info("URL 模式: /api/uploads/signatures/**");
            log.info("物理路径: {}", externalPath);

            registry.addResourceHandler("/api/uploads/signatures/**")
                    .addResourceLocations(externalPath)
                    .setCachePeriod(0);

            registry.addResourceHandler("/uploads/signatures/**")
                    .addResourceLocations(externalPath)
                    .setCachePeriod(0);

            // 添加更通用的映射
            registry.addResourceHandler("/api/uploads/**")
                    .addResourceLocations(externalPath)
                    .setCachePeriod(0);

            log.info("静态资源映射配置完成");

        } catch (Exception e) {
            log.error("配置静态资源映射失败: {}", e.getMessage(), e);
        }
    }
}