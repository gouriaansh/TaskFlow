
package org.example;

import java.sql.*;
import java.util.ArrayList;

public class TaskDAO {

    // Add a new task
    public boolean addTask(Task task) {

        String sql = "INSERT INTO tasks (title, description, due_date, priority, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());

            if (task.getDueDate() != null) {
                ps.setDate(3, Date.valueOf(task.getDueDate()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            ps.setString(4, task.getPriority());
            ps.setString(5, task.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieve all tasks
    public ArrayList<Task> getAllTasks() {

        ArrayList<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY id DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Date date = rs.getDate("due_date");

                Task task = new Task(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        date != null ? date.toLocalDate() : null,
                        rs.getString("priority"),
                        rs.getString("status")
                );

                tasks.add(task);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    // Update task status
    public boolean updateStatus(int id, String status) {

        String sql = "UPDATE tasks SET status = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete a task
    public boolean deleteTask(int id) {

        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

