package de.naeveke.board.database;

import java.io.Serializable;
import javax.inject.Inject;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author Nitek
 * @param <E>
 * @param <P>
 */
public abstract class BaseRepository<E, P extends Serializable> {

    @Inject
    private SessionFactory sessionFactory;

    private final Class<E> entityClass;

    protected BaseRepository(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public E findById(P id) {
        return (E) getCurrentSession().get(entityClass, id);
    }

    public void create(E e) {
        getCurrentSession().save(e);
    }

    public void update(E e) {
        getCurrentSession().update(e);
    }

    public void delete(E e) {
        getCurrentSession().delete(e);
    }

    public void save(E e) {
        getCurrentSession().saveOrUpdate(e);
    }
}
