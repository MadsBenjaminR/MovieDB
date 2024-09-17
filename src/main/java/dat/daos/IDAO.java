package dat.daos;

import java.util.List;

public interface IDAO<T> {

    void create(T t);
    void update(T t);
    void delete(T t);
    List<T> getAll();
    T getById(int id);

}
