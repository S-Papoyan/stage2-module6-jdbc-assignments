package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) values (?,?,?)";
    private static final String updateUserSQL = "UPDATE myusers set firstname = ?, lastname = ?, age = ? where id = ?";
    private static final String deleteUser = "DELETE FROM public.myusers where id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers where id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers where firstname LIKE concat('%', ?, '%')";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user){
        User createdUser;
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(createUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeQuery();
            createdUser = findUserByName(user.getFirstName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return createdUser.getId();
    }

    public User findUserById(Long userId) {
        User user = new User();
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);
            resultSet(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    private void resultSet(User user) throws SQLException {
        ResultSet resultSet = ps.executeQuery();
        while (resultSet.next()) {
            user.setId(resultSet.getLong("id"));
            user.setFirstName(resultSet.getString("firstname"));
            user.setLastName((resultSet.getString("lastname")));
            user.setAge(resultSet.getInt("age"));
        }
    }

    public User findUserByName(String userName) {
        User user = new User();
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);
            resultSet(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> userList = new ArrayList<>();
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(findAllUserSQL);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName((resultSet.getString("lastname")));
                user.setAge(resultSet.getInt("age"));
                userList.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userList;
    }

    public User updateUser(User user) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(updateUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return findUserById(user.getId());
    }

    private void deleteUser(Long userId) {
        try {
            connection = CustomDataSource.getInstance().getConnection();
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);
            ps.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}