package ru.otus.l111.hibernateORM.dbService;

import java.sql.SQLException;
import java.util.function.Function;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.otus.l111.hibernateORM.dataSets.AddressDataSet;
import ru.otus.l111.hibernateORM.dataSets.DataSet;
import ru.otus.l111.hibernateORM.dataSets.UsersDataSet;

public class DBServiceHibernateImpl implements DBService {
    private final SessionFactory sessionFactory;

    public DBServiceHibernateImpl() {
        Configuration configuration = new Configuration();

        configuration.addAnnotatedClass(UsersDataSet.class);
        configuration.addAnnotatedClass(AddressDataSet.class);


        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/hwl11");
        configuration.setProperty("hibernate.connection.username", "izbro");
        configuration.setProperty("hibernate.connection.password", "Izbrodin123/*");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");
        configuration.setProperty("hibernate.connection.useSSL", "false");

        configuration.setProperty("hibernate.enable_lazy_load_no_trans", "true");

        sessionFactory = createSessionFactory(configuration);
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }


    @Override
    public String getLocalStatus() {
        return runInSession(session -> {
            return session.getTransaction().getStatus().name();
        });
    }



    @Override
    public <T extends DataSet> void save(final T user) throws SQLException {
        try (Session session = sessionFactory.openSession()) {
            UsersDAO dao = new UsersDAO(session);
            dao.save(user);

        }

    }

    @Override
    public <T extends DataSet> T load(final long id, final Class<T> clazz) throws SQLException {
        return runInSession(session -> {
            UsersDAO dao = new UsersDAO(session);
            T object = dao.load(id, clazz);
            return object;
        });
    }

    @Override
    public void shutdown() {
        sessionFactory.close();
    }



    private <R> R runInSession(Function<Session, R> function) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            R result = function.apply(session);
            transaction.commit();
            return result;
        }
    }








}
