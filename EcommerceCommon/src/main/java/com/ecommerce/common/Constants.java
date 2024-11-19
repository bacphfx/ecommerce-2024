package com.ecommerce.common;

import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {
    public static final String bucketName = "ecommerce-java-bacph";
    public static final String region = "us-east-1";
    public static final String S3_BASE_URI;

    static {
        String pattern = "https://%s.s3.%s.amazonaws.com";
        String uri = String.format(pattern, bucketName, region);
        S3_BASE_URI = bucketName == null ? "" : uri;
    }
}
