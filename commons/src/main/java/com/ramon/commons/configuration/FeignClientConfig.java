package com.ramon.commons.configuration;

import com.ramon.commons.clients.FeignErrorDecorer;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    ErrorDecoder errorDecoder(){
        return new FeignErrorDecorer();
    }
}
