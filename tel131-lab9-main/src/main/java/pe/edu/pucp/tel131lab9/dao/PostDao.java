package pe.edu.pucp.tel131lab9.dao;

import pe.edu.pucp.tel131lab9.bean.Employee;
import pe.edu.pucp.tel131lab9.bean.Post;
import pe.edu.pucp.tel131lab9.dto.CantidadComentariosDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostDao extends DaoBase{

    public ArrayList<Post> listPosts() {

        ArrayList<Post> posts = new ArrayList<>();

        String sql = "SELECT * FROM post left join employees e on e.employee_id = post.employee_id";

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Post post = new Post();
                fetchPostData(post, rs);
                posts.add(post);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PostDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return posts;
    }

    public Post getPost(int id) {

        Post post = null;

        String sql = "SELECT * FROM post p left join employees e on p.employee_id = e.employee_id "+
                "where p.post_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    post = new Post();
                    fetchPostData(post, rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return post;
    }

    public Post savePost(Post post) {

        return post;
    }

    private void fetchPostData(Post post, ResultSet rs) throws SQLException {
        post.setPostId(rs.getInt(1));
        post.setTitle(rs.getString(2));
        post.setContent(rs.getString(3));
        post.setEmployeeId(rs.getInt(4));
        post.setDatetime(rs.getTimestamp(5));

        int cantidad = obtenerCantidadComentarios(rs.getInt(1));
        post.setCantidad(cantidad);

        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("e.employee_id"));
        employee.setFirstName(rs.getString("e.first_name"));
        employee.setLastName(rs.getString("e.last_name"));
        post.setEmployee(employee);
    }

    public ArrayList<Post> buscarPorPost(String name) {
        ArrayList<Post> listabusqueda = new ArrayList<>();

        String sql = "SELECT * FROM post p\n" +
                "inner join employees e on p.employee_id = e.employee_id\n" +
                "where (p.title like ?) or (p.content like ?) or (e.first_name like ?) or (e.last_name like ?) ;";

        try (Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1,  "%" + name + "%");
            preparedStatement.setString(2,  "%" + name + "%");
            preparedStatement.setString(3,  "%" + name + "%");
            preparedStatement.setString(4,  "%" + name + "%");
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post();
                    post.setTitle(rs.getString(2));
                    post.setContent(rs.getString(3));
                    post.setDatetime(rs.getTimestamp(5));

                    Employee employee = new Employee();
                    employee.setFirstName(rs.getString(7));
                    employee.setLastName(rs.getString(8));
                    post.setEmployee(employee);

                    int cantidad = obtenerCantidadComentarios(rs.getInt(1));
                    post.setCantidad(cantidad);
                    listabusqueda.add(post);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return listabusqueda;
    }


    public int obtenerCantidadComentarios(int idPost) {

        CantidadComentariosDTO cantidadComentarios = new CantidadComentariosDTO();

        String sql = "SELECT post_id, count(*) as cantidad FROM lab9.comments c\n" +
                "where post_id = ?\n" +
                "group by post_id ;";

        try (Connection connection = this.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idPost);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    cantidadComentarios.setCantidad(rs.getInt(2));
                }
                return cantidadComentarios.getCantidad();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
