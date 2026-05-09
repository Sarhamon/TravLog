package com.travlog.travlog.common;

import com.samskivert.mustache.Mustache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MustacheConfig {

    // Spring Boot 4.0의 기본 컴파일러는 defaultValue를 설정하지 않아
    // 모델에 없는 키나 null 값에 대해 예외를 던진다. 폼 초기 렌더 시 form.title 등이
    // null일 수 있으므로 빈 문자열로 렌더링되도록 복원한다.
    @Bean
    Mustache.Compiler mustacheCompiler(Mustache.TemplateLoader loader) {
        return Mustache.compiler()
                .withLoader(loader)
                .defaultValue("");
    }
}
