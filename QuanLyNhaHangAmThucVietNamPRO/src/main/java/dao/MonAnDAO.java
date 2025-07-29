package dao;

import entity.MonAn;
import java.util.List;

public interface MonAnDAO {
    MonAn create(MonAn entity);
    void update(MonAn entity);
    public void deleteById(Integer MaMonAn);
    List<MonAn> findAll();
    MonAn findById(Integer id);
    public MonAn findByTen(String tenMonAn);
    public void deleteByIdall(Integer MaMon);
}
