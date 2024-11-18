package com.ecommerce;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory("../category-image", registry);
        exposeDirectory("../brand-logo", registry);
        exposeDirectory("../product-images", registry);
        exposeDirectory("../site-logo", registry);
    }

    private void exposeDirectory(String pathPattern, ResourceHandlerRegistry registry){
        String logicalPath = pathPattern.replace("..","") + "/**";
        String absolutePath = Paths.get(pathPattern).toAbsolutePath().normalize().toString();
        registry.addResourceHandler(logicalPath)
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
