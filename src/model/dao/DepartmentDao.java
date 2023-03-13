package model.dao;

import model.entities.Department;

import java.util.List;

public interface DepartmentDao {
//Down de Department
    void insert(Department obj);
    void update(Department obj);
    void deleteById(Integer id);
    Department findById(Integer id);    //se existir vai retornar, se não existir vai retornar nullo
    List<Department> findAll();
}
