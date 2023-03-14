package model.dao.model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class SellerDaoJDBC implements SellerDao {

    private Connection conn;
    public SellerDaoJDBC(Connection conn){
        this.conn = conn;
    }

    @Override
    public void insert(Seller obj) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("INSERT INTO seller\n"
                    +"(Name, Email, BirthDate, BaseSalary, DepartmentId)\n"
                    +"VALUES\n"
                    +"(?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(4,obj.getBaseSalary());
            st.setInt(5,obj.getDepartment().getId());

            int rowsAffected = st.executeUpdate();

            if (rowsAffected > 0){
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()){
                    int id = rs.getInt(1);
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            }else {
                throw new DbException("Unexpected error! No rows affected!");
            }
        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void update(Seller obj) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("UPDATE seller\n"
                            + "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ?\n"
                            + "WHERE Id = ?",
                    Statement.RETURN_GENERATED_KEYS);

            st.setString(1, obj.getName());
            st.setString(2, obj.getEmail());
            st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
            st.setDouble(4, obj.getBaseSalary());
            st.setInt(5, obj.getDepartment().getId());
            st.setInt(6, obj.getId());

            st.executeUpdate();

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement("DELETE FROM seller\n"
                    + "WHERE Id = ?");
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Seller findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName\n"
                            + "FROM seller INNER JOIN department\n"
                            + "ON seller.DepartmentId = department.Id\n"
                            + "WHERE seller.Id = ?");

            st.setInt(1, id);
            rs = st.executeQuery();

            if (rs.next()) { //se a minha consulta retornou registro
                Department dep = instantiateDepartment(rs);
                Seller obj = instantiateSeller(rs, dep);
                return obj;
            }
            return null;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
            //A conexão você deixa
        }
    }

    private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
        Seller obj = new Seller();
        obj.setId(rs.getInt("Id"));
        obj.setName(rs.getString("Name"));
        obj.setEmail(rs.getString("Email"));
        obj.setBaseSalary(rs.getDouble("BaseSalary"));
        obj.setBirthDate(rs.getDate("BirthDate"));
        obj.setDepartment(dep);
        return obj;
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
       Department dep = new Department();
        dep.setId(rs.getInt("DepartmentId"));
        dep.setName(rs.getString("DepName"));
        return dep;
    }

    @Override
    public List<Seller> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName\n" +
                            "FROM seller INNER JOIN department\n" +
                            "ON seller.DepartmentId = department.Id\n" +
                            "ORDER BY Name");

            rs = st.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>(); //criou um map vazio

            while (rs.next()) { //Enquanto ter um proximo
                //vai guardar dentro do map qualquer departamento que instanciar
                Department dep = map.get(rs.getInt("DepartmentId"));    //testar se ele ja existe, se não existe vai retornar nullo para o map

                if (dep == null){ //Se ele for nullo
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"),dep); //vai salvar no map
                }

                Seller obj = instantiateSeller(rs, dep);
                list.add(obj);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
            //A conexão você deixa
        }
    }

    @Override
    public List<Seller> findByDepartment(Department department) {
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            st = conn.prepareStatement(
                    "SELECT seller.*,department.Name as DepName\n" +
                            "FROM seller INNER JOIN department\n" +
                            "ON seller.DepartmentId = department.Id\n" +
                            "WHERE DepartmentId = ?\n" +
                            "ORDER BY Name");

            st.setInt(1, department.getId());
            rs = st.executeQuery();

            List<Seller> list = new ArrayList<>();
            Map<Integer, Department> map = new HashMap<>(); //criou um map vazio

            while (rs.next()) { //Enquanto ter um proximo
                //vai guardar dentro do map qualquer departamento que instanciar
                Department dep = map.get(rs.getInt("DepartmentId"));    //testar se ele ja existe, se não existe vai retornar nullo para o map

                if (dep == null){
                    dep = instantiateDepartment(rs);
                    map.put(rs.getInt("DepartmentId"),dep);
                }

                Seller obj = instantiateSeller(rs, dep);
                list.add(obj);
            }
            return list;
        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
            DB.closeResultSet(rs);
            //A conexão você deixa
        }
    }
}
