package com.example.toby.IoCContainer;

import com.example.toby.Hello;
import com.example.toby.StringPrinter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class IoCContainerTest {

    private StaticApplicationContext ac;

    @BeforeEach
    void setUp() {
        ac = new StaticApplicationContext();
    }

    @Test
    void 디폴트_메타_정보를_사용하는_BeanRegistrationTest() {
        ac.registerSingleton("hello1", Hello.class);

        Hello hello1 = ac.getBean("hello1", Hello.class);

        assertThat(hello1).isNotNull();
    }

    @Test
    void 직접_메타_정보를_설정해서_사용하는_BeanRegistrationTest() {
        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring"); // 빈의 name 프로퍼티에 들어갈 값 지정
        ac.registerBeanDefinition("hello2", helloDef);

        Hello hello2 = ac.getBean("hello2", Hello.class);

        assertThat(hello2.sayHello()).isEqualTo("Hello Spring");
    }

    @Test
    void DI_정보_Test() {
        ac.registerBeanDefinition("printer", new RootBeanDefinition(StringPrinter.class));

        BeanDefinition helloDef = new RootBeanDefinition(Hello.class);
        helloDef.getPropertyValues().addPropertyValue("name", "Spring");
        helloDef.getPropertyValues().addPropertyValue("printer", new RuntimeBeanReference("printer"));

        ac.registerBeanDefinition("hello", helloDef);

        Hello hello = ac.getBean("hello", Hello.class);
        hello.print();

        assertThat(ac.getBean("printer").toString()).isEqualTo("Hello Spring");
    }

    @Test
    void GenericApplicationContext_사용방법에_대한_테스트() throws IOException {
        GenericApplicationContext ga = new GenericApplicationContext();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(ga);
        reader.loadBeanDefinitions("classpath:genericApplicationContext.xml");

        ga.refresh();

        Hello hello = ga.getBean("hello", Hello.class);
        hello.print();

        assertThat(ga.getBean("printer").toString()).isEqualTo("Hello Spring");
    }
}
