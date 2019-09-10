package com.mainacad.dao.connection;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.mainacad.model.Cart;
import com.mainacad.model.Item;
import com.mainacad.model.Order;
import com.mainacad.model.User;

import java.util.Properties;

@Component
@Profile("dev")
public class PostgeSQLConnectionFactory implements ConnectionFactory {
  @Override
  public SessionFactory getSessionFactory() {
    try {
      Configuration configuration = new Configuration();

      Properties properties = new Properties();

      properties.setProperty("hibernate.connection.driverClassName", "org.postgresql.Driver");
      properties.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/shop_ma_springboot");
      properties.setProperty("hibernate.connection.username", "postgres");
      properties.setProperty("hibernate.connection.password", "qwerty");
      properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
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
