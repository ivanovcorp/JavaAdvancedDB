package entities.jpaStyle;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

public class RunnerJPA {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("school");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        //Student teo = new Student("Doncho", 27);

        //em.persist(teo);

/*//        em.getTransaction().commit();

        Student st = em.find(Student.class, 3);

        em.remove(st);

        em.getTransaction().commit();*/

        Query query = em.createNativeQuery("SELECT * FROM students WHERE id='4';");
        Student st1 = query.unwrap(Student.class);
        System.out.println(st1.getName());
    }
}



