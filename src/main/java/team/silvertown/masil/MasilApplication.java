package team.silvertown.masil;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import tech.ailef.snapadmin.external.SnapAdminAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@ImportAutoConfiguration(SnapAdminAutoConfiguration.class)
@ConfigurationPropertiesScan
public class MasilApplication {

    private static final String TIMEZONE = "Asia/Seoul";

    public static void main(String[] args) {
        SpringApplication.run(MasilApplication.class, args);
    }

    @PostConstruct
    public void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE));
    }

}
