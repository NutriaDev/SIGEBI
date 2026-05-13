// sigebi.reportsandaudit.config.WebConfig.java
package sigebi.reportsandaudit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${reports.maintenance.pdf-directory:./reports/maintenance}")
    private String pdfDirectory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/reports/maintenance/**")
                .addResourceLocations("file:" + pdfDirectory + "/");
    }
}