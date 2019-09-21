package com.mainacad.dao.connection;

import com.mainacad.entity.Cart;
import com.mainacad.entity.Item;
import com.mainacad.entity.Order;
import com.mainacad.entity.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Profile("test")
public class H2ConnectionFactory implements ConnectionFactory {
  @Override
  public SessionFactory getSessionFactory() {
    try {
      Configuration configuration = new Configuration();

      Properties properties = new Properties();

      properties.setProperty("hibernate.connection.driverClassName", "org.h2.Driver");
      properties.setProperty("hibernate.connection.url", "jdbc:h2:~/sb-ma");
      properties.setProperty("hibernate.connection.username", "sa");
      properties.setProperty("hibernate.connection.password", "qwerty");
      properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
      properties.setProperty("hibernate.hbm2ddl.auto", "update");
      properties.setProperty("hibernate.show_sql", "true");
      properties.setProperty("hibernate.format_sql", "true");

      configuration.addProperties(properties);

      configuration.addAnnotatedClass(Item.class);
      configuration.addAnnotatedClass(User.class);
      configuration.addAnnotatedClass(Order.class);
      configuration.addAnnotatedClass(Cart.class);

      ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
              .applySettings(configuration.getProperties()).build();

      return configuration.buildSessionFactory(serviceRegistry);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }
}
