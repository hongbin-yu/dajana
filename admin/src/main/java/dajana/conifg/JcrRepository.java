package dajana.conifg;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:jcr-context-master.xml")
public class JcrRepository {

}
