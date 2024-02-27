package team.silvertown.masil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MasilApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasilApplication.class, args);
    }

}
