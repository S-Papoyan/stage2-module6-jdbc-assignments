package jdbc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SimpleJDBCRepository {
    private static final String createUserSQL = "INSERT INTO myusers (firstname,lastname,age) VALUES (?,?,?)";
    private static final String updateUserSQL = "UPDATE myusers SET firstname=?,lastname=?,age=? WHERE id=?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id=?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id=?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname=?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user) {
        Long id = null;
        try(
                Connection connection = CustomDataSource.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(createUserSQL,Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setObject(1, user.getFirstName());
            preparedStatement.setObject(2, user.getLastName());
            preparedStatement.setObject(3, user.getAge());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()){
                id = resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    public User findUserById(Long userId) {
        User user = new User();

        try(
                Connection connection = CustomDataSource.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(findUserByIdSQL)
        ){
            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next())throw new SQLException("No user");

            user.setId(resultSet.getLong("id"));
            user.setFirstName(resultSet.getString("firstname"));
            user.setLastName(resultSet.getString("lastname"));
            user.setAge(resultSet.getInt("age"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public User findUserByName(String userName) {
        User user = new User();
        try (
                Connection connection = CustomDataSource.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(findUserByNameSQL)
        ){

            preparedStatement.setString(1, userName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next())throw new SQLException("No user");

            user.setId(resultSet.getLong("id"));
            user.setFirstName(resultSet.getString("firstname"));
            user.setLastName(resultSet.getString("lastname"));
            user.setAge(resultSet.getInt("age"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    public List<User> findAllUser() {
        List<User> users = new ArrayList<>();
        try(
                Connection connection = CustomDataSource.getInstance().getConnection();
                Statement statement = connection.createStatement()
        ){

            ResultSet resultSet = statement.executeQuery(findAllUserSQL);
            while (resultSet.next()){
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName(resultSet.getString("lastname"));
                user.setAge(resultSet.getInt("age"));
                users.add(user);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    public User updateUser(User user) {
        try(
                Connection connection = CustomDataSource.getInstance().getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(updateUserSQL)
        ){

            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setInt(3, user.getAge());
            preparedStatement.setLong(4, user.getId());
            if (preparedStatement.executeUpdate() == 0) throw new SQLException("No user");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void deleteUser(Long userId) {
        try (Connection connection = CustomDataSource.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(deleteUser);) {
            ps.setLong(1, userId);
            if (ps.executeUpdate() == 0) throw new SQLException("No such user exists");
        } catch (SQLException throwable) {
            throw new RuntimeException(throwable);
        }
    }
}