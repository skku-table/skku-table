package com.skkutable.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dt5qdfabo",
                "api_key", "699498692966635",
                "api_secret", "X6voMmVB_LFJQNGIBgCNDf2MHFw",
                "secure", true
        ));
    }
}
