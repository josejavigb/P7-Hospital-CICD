package com.uma.example.springuma.integration.base;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

// Esta anotación especifica cómo deben crearse las instancias de prueba. El valor Lifecycle.PER_CLASS garantiza que se cree una instancia de prueba por cada clase de prueba.
@TestInstance(Lifecycle.PER_CLASS)
// Esta anotacion permite la configuración automática de MockMvc en las pruebas MVC de Spring. 
@AutoConfigureMockMvc
//Esta anotación habilita las propiedades de configuración de la aplicación. 
@EnableConfigurationProperties
// Esta anotación limpia el contexto de Spring después de cada método de prueba. Esto garantiza la independencia entre las pruebas.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//  Esta anotación inicia la aplicación Spring Boot en un entorno de pruebas. 
// El parámetro webEnvironment especifica el entorno web a probar, y el valor RANDOM_PORT asegura la selección de un número de puerto aleatorio
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegration {
}
