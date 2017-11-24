package entities;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class Runner {

    public static void main(String[] args) {

        Configuration cfg = new Configuration();
        cfg.configure();
        SessionFactory sessionFactory = cfg.buildSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            /*Student joro = new Student("Ivcho", 35);
            session.save(joro);
            session.getTransaction().commit();
            session.close();*/
            Student st = session.get(Student.class, 2);
            System.out.println(st.getName() + " " + st.getId() + " "  + st.getAge());
        }

        System.exit(1);
    }

}



