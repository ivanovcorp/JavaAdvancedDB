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
            session.save(new Student("Ivan", 25));
            session.save(new Student("Georgi", 45));
            session.save(new Student("Peter", 27));
            session.save(new Student("Glavon", 21));
            session.save(new Student("Ceco", 33));
            session.getTransaction().commit();
            
            Student st = session.get(Student.class, 2);
            System.out.println(st.getName() + " " + st.getId() + " "  + st.getAge());
        }

        System.exit(1);
    }

}



