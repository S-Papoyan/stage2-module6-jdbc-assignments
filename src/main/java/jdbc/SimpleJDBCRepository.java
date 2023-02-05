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
    private static final String deleteUser = "DELETE FROM myusers where id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers where id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers where firstname LIKE concat('%', ?, '%')";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user){
        User createdUser;
        try {
            Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(createUserSQL);
            preparedStatement.setObject(1, user.getFirstName());
            preparedStatement.setObject(2, user.getLastName());
            preparedStatement.setObject(3, user.getAge());
            preparedStatement.executeQuery();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            createdUser = findUserByName(user.getFirstName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return createdUser.getId();
    }

    public User findUserById(Long userId) {
        User user = new User();
        try {
            Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(findUserByIdSQL);
            preparedStatement.setObject(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName((resultSet.getString("lastname")));
                user.setAge(resultSet.getInt("age"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;
        try {
            Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(findUserByNameSQL);
            preparedStatement.setObject(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                user = new User();
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName((resultSet.getString("lastname")));
                user.setAge(resultSet.getInt("age"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> userList = new ArrayList<>();
        try {
            Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(findAllUserSQL);
            ResultSet resultSet = preparedStatement.executeQuery();
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
            Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(updateUserSQL);
            preparedStatement.setObject(1, user.getFirstName());
            preparedStatement.setObject(2, user.getLastName());
            preparedStatement.setObject(3, user.getAge());
            preparedStatement.setObject(4, user.getId());
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return findUserById(user.getId());
    }

    private void deleteUser(Long userId) {
        try {
            Connection connection = CustomDataSource.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(deleteUser);
            preparedStatement.setObject(1, userId);
            preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}