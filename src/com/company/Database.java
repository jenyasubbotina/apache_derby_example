package com.company;

import java.sql.*;
import java.util.*;

public class Database {
    private Connection conn;
    private boolean isopen;
    private String dbName = "";
    public Database(String name) {
        try {
            dbName = name;
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            conn = DriverManager.getConnection(
                    "jdbc:derby:" + dbName + ";create=true");
            conn.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            conn = null;
        }
        isopen = (conn != null);
    }

    public boolean isopen() {
        return isopen;
    }

    public void createTable() throws SQLException {
        PreparedStatement s;
        String sql = "CREATE TABLE Contacts (Id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
                "First_Name VARCHAR(255) NOT NULL, " +
                "Last_name VARCHAR(255) NOT NULL," +
                " Patronymic VARCHAR(255)," +
                "Phone_number VARCHAR(255)," +
                "Home_number VARCHAR(255)," +
                "Address VARCHAR(255)," +
                "Birth_Date VARCHAR(255)," +
                "Notes VARCHAR(255)," +
                "PRIMARY KEY (Id)  )";
        s = conn.prepareStatement(sql);
        s.executeUpdate();
    }

    public void cmdAdd(Scanner scanner) {
        PreparedStatement s;
        ResultSet rset;

        String name = "";
        String lastName = "";
        String patronymic = "";
        String birthDate = "";
        String phone = "";
        String homePhone = "";
        String address = "";
        String notes = "";

        System.out.println("Введите имя");
        name = scanner.next();

        System.out.println("Введите фамилию");
        lastName = scanner.next();

        System.out.println("Введите отчество (или '-', если его нет)");
        patronymic = scanner.next();

        System.out.println("Введите дату рождения");
        birthDate = scanner.next();

        System.out.println("Введите мобильный телефон (или '-', если его нет)");
        phone = scanner.next();

        System.out.println("Введите домашний телефон (или '-', если его нет");
        homePhone = scanner.next();

        System.out.println("Введите адрес");
        address = scanner.next();

        System.out.println("Введите комментарий");
        notes = scanner.next();

        String sql = "INSERT INTO " + dbName + " (First_Name, Last_Name, Patronymic, Phone_number, " +
                "Home_number, Address, Birth_Date, Notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            s = conn.prepareStatement(sql);
            s.setString(1, name);
            s.setString(2, lastName);
            s.setString(3, patronymic);
            s.setString(4, birthDate);
            s.setString(5, phone);
            s.setString(6, homePhone);
            s.setString(7, address);
            s.setString(8, notes);
            s.executeUpdate();
            s.close();
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDb() {
        PreparedStatement s;
        try {
            String sql = "DROP TABLE " + dbName;
            s = conn.prepareStatement(sql);
            s.executeUpdate();
            System.out.println("Table deleted");
        } catch (Exception e) {
            System.out.println(e.getMessage().toString());
        }

    }

    public void deleteAll() {
        PreparedStatement s;
        try {
            String sql = "DELETE from " + dbName + " WHERE Id >= 1";
            s = conn.prepareStatement(sql);
            s.executeUpdate();
            System.out.println("Deleted all records!");
        } catch (SQLException e) {
            System.out.println("Delete Error! (all records )\t" + e.getMessage());
        }
    }

    public void showAllContacts() {
        PreparedStatement s;
        ResultSet rset;
        String sql = "SELECT * FROM " + dbName;
        try {
            s = conn.prepareStatement(sql);
            rset = s.executeQuery();
            TableOutput tableOutput = new TableOutput();
            List<String> headers = new ArrayList<>();
            headers.add("Id");
            headers.add("Имя");
            headers.add("Фамилия");
            headers.add("Отчество");
            headers.add("Моб. телефон");
            headers.add("Дом. телефон");
            headers.add("Адрес");
            headers.add("Дата рождения");
            headers.add("Заметки");
            List<List<String>> rowsList = new ArrayList<>();
            while (rset.next()) {
                List<String> row = new ArrayList<>();
                long id = rset.getLong("Id");
                String name = rset.getString("First_Name");
                String lastName = rset.getString("Last_Name");
                String patronymic = rset.getString("Patronymic");
                String phone = rset.getString("Phone_number");
                String homePhone = rset.getString("Home_number");
                String address = rset.getString("Address");
                String birthDate = rset.getString("Birth_Date");
                String notes = rset.getString("Notes");
                row.add(String.valueOf(id));
                row.add(name);
                row.add(lastName);
                row.add(patronymic);
                row.add(phone);
                row.add(homePhone);
                row.add(address);
                row.add(birthDate);
                row.add(notes);
                rowsList.add(row);
            }
            System.out.println(tableOutput.generateTable(headers, rowsList));
            s.close();
            conn.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean isCreated() throws SQLException {
        try {
            Statement s = conn.createStatement();
            s.execute("update " + dbName + " set First_Name = 'Test', Last_Name = 'Test' where 1=3");
        } catch (SQLException sqlException) {
            String theError = (sqlException).getSQLState();
            if (theError.equals("42X05")) {
                return false;
            } else {
                throw sqlException;
            }
        }
        return true;
    }

    public void exit() {
        if (!isopen)
            return;
        try {
            conn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        isopen = false;
        conn = null;
    }

    public void showAll() {
        PreparedStatement stmt = null;
        ResultSet rset = null;
        ResultSetMetaData meta = null;
        String sql, label, first, last, currentband;
        int ncols, col, id;

        if (!isopen) return;

        try {
            sql = "SELECT Musician.Id, Musician.Fname, Musician.Lname, Musician.CurrentBand "
                    + "FROM Musician";
            stmt = conn.prepareStatement(sql);
            rset = stmt.executeQuery();
            meta = rset.getMetaData();
            ncols = meta.getColumnCount();
            System.out.printf("%n");
            System.out.printf(" Id   First Name    Last Name     CurrentBand%n");
            // Process each row in a loop.
            while (rset.next()) {
                id = rset.getInt(1);
                first = rset.getString(2);
                last = rset.getString(3);
                currentband = rset.getString(4);
                System.out.printf("%4d  %-12s  %-12s  %-12s%n",id, first, last, currentband);
            }
            stmt.close();
            conn.commit();
        } catch (Exception e) {
            System.out.printf("%s%n", e.getMessage());
            try {stmt.close();}
            catch (Exception err) {}
            try {conn.rollback();}
            catch (Exception err) {}
        }
    }

    //this finds a guitar player by first and last name
    public void findGPlayer(Scanner kbd){
        PreparedStatement stmt = null;
        ResultSet rset = null;
        String sql, last, first, favGuitar;
        int id;

        // Return if the database is closed.
        if (!isopen) return;

        try {
            // Create a PreparedStatement for the query.
            sql = "SELECT Musician.Id, Musician.Fname, Musician.Lname, Guitarplayer.FavoriteGuitar " +
                    "FROM Musician " +
                    "INNER JOIN Guitarplayer ON Guitarplayer.Id = Musician.Id " +
                    "WHERE Musician.Lname = ? AND Musician.Fname = ? ";
            stmt = conn.prepareStatement(sql);

            // Read the parameters from the user.
            System.out.printf("First Name? ");
            first = kbd.next();
            System.out.printf("Last Name? ");
            last = kbd.next();

            // Set the parameters in the statement.
            stmt.setString(1, last);
            stmt.setString(2, first);

            // Execute the query and obtain the result set.
            rset = stmt.executeQuery();

            // Print the matching musicians
            System.out.printf("%n");
            System.out.printf(" Id   First Name    Last Name     FavoriteGuitar%n");
            while (rset.next()) {
                id = rset.getInt(1);
                first = rset.getString(2);
                last = rset.getString(3);
                favGuitar = rset.getString(4);
                System.out.printf("%4d  %-12s  %-12s  %-12s%n", id, first, last, favGuitar);
            }
            stmt.close();
            conn.commit();
        } catch (Exception e) {
            System.out.printf("%s%n", e.getMessage());
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e1) {
                e.printStackTrace();
            }
            try {
                conn.rollback();
            }
            catch (Exception er) {}
        }
    }

    //this finds a bass player by first and last name
    public void findBPlayer(Scanner kbd){
        PreparedStatement stmt = null;
        ResultSet rset = null;
        String sql, last, first, favBass;
        int id;

        // Return if the database is closed.
        if (!isopen) return;

        try {
            // Create a PreparedStatement for the query.
            sql = "SELECT Musician.Id, Musician.Fname, Musician.Lname, BassPlayer.FavoriteBass " +
                    "FROM Musician " +
                    "INNER JOIN BassPlayer ON BassPlayer.Id = Musician.Id " +
                    "WHERE Musician.Lname = ? AND Musician.Fname = ? ";
            stmt = conn.prepareStatement(sql);

            // Read the parameters from the user.
            System.out.printf("First Name? ");
            first = kbd.next();
            System.out.printf("Last Name? ");
            last = kbd.next();

            // Set the parameters in the statement.
            stmt.setString(1, last);
            stmt.setString(2, first);

            // Execute the query and obtain the result set.
            rset = stmt.executeQuery();

            // Print the matching musicians
            System.out.printf("%n");
            System.out.printf(" Id   First Name    Last Name     FavoriteBass%n");
            while (rset.next()) {
                id = rset.getInt(1);
                first = rset.getString(2);
                last = rset.getString(3);
                favBass = rset.getString(4);
                System.out.printf("%4d  %-12s  %-12s  %-12s%n", id, first, last, favBass);
            }

            stmt.close();
            conn.commit();
        } catch (Exception e) {
            System.out.printf("%s%n", e.getMessage());
            try {stmt.close();}
            catch (Exception err) {}
            try {conn.rollback();}
            catch (Exception err) {}
        }
    }

    //displays a band and the members that are in it
    public void showBM(Scanner kbd){
        PreparedStatement stmt = null;
        ResultSet rset = null;
        String sql, bName, first, last;

        // Return if the database is closed.
        if (!isopen) return;

        try {
            // Create a PreparedStatement for the query.
            sql = "SELECT Bandlist.Name, Musician.Fname, Musician.Lname " +
                    "FROM BandList " +
                    "INNER JOIN Musician ON Musician.CurrentBand = Bandlist.Name " +
                    "WHERE Bandlist.name = ? ";
            stmt = conn.prepareStatement(sql);

            // Read the parameters from the user.
            System.out.printf("Band Name? ");
            kbd.nextLine();
            bName = kbd.nextLine();

            // Set the parameters in the statement.
            stmt.setString(1, bName);

            // Execute the query and obtain the result set.
            rset = stmt.executeQuery();

            // Print the matching musicians
            System.out.printf("%n");
            System.out.printf("BandName      First Name    Last Name%n");
            while (rset.next()) {
                bName = rset.getString(1);
                first = rset.getString(2);
                last = rset.getString(3);
                System.out.printf("%-12s  %-12s  %-12s%n", bName, first, last);
            }

            stmt.close();
            conn.commit();
        } catch (Exception e) {
            System.out.printf("%s%n", e.getMessage());
            try {stmt.close();}
            catch (Exception err) {}
            try {conn.rollback();}
            catch (Exception err) {}
        }
    }

    //this updates a specific musicians' current band declare by the user
    public void updateBand(Scanner kbd){
        PreparedStatement stmt = null;
        String sql, newBand, updateB;
        int id;

        //Return of the database is closed
        if(!isopen) return;

        try{
            //create a PreparedStatement for the update
            sql = "UPDATE Musician SET " +
                    " Musician.CurrentBand = ? " +
                    "WHERE  Musician.Id = ?";
            stmt = conn.prepareStatement(sql);

            //Read the parameters from the user to update a row by id number
            System.out.printf("Musician Id? ");
            id = kbd.nextInt();
            kbd.nextLine();
            System.out.printf("New band? ");
            newBand = kbd.nextLine();
            kbd.nextLine();

            //Set the parameters in the statement
            stmt.setString(1, newBand);
            stmt.setInt(2, id);

            //Execute the update
            stmt.executeUpdate();
            stmt.close();
            conn.commit();
        } catch (Exception e) {
            System.out.printf("%s%n", e.getMessage());
            try {stmt.close();}
            catch (Exception err) {}
            try {conn.rollback();}
            catch (Exception err) {}
        }
    }

    public void addmusician(Scanner kbd){
        PreparedStatement stmt = null;
        ResultSet rset = null;
        String sql, fname, lname, cband;
        int id = -1;

        if(!isopen) return;

        try{
            //Read in musician's name and current band for creation of a new row
            System.out.print("First name? ");
            fname = kbd.next();
            System.out.print("Last name? ");
            lname = kbd.next();
            System.out.print("Current band? ");
            cband = kbd.next();

            //Reads all the id numbers and generates a new id number
            sql  = "SELECT Musician.Id FROM Musician";
            stmt = conn.prepareStatement(sql);
            rset = stmt.executeQuery();
            while(rset.next()){
                id = rset.getInt(1);
            }
            id++;
            //Create a preparestatement for the update
            sql = "INSERT INTO Musician (Id, Fname, Lname, CurrentBand) "
                    + "VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.setString(2, fname);
            stmt.setString(3, lname);
            stmt.setString(4, cband);

            //execute the update and retrieve the generated key
            stmt.executeUpdate();
            if(rset.next()) {id = rset.getInt(1);}
            System.out.printf("%n");
            //Display the musician was added or not
            if(id >= 0){
                System.out.printf("Musician %d was created for %s %s.%n",
                        id, fname, lname);
            } else{
                System.out.printf("The musician was not added to the records.%n");
            }
            stmt.close();
            conn.commit();
        } catch (Exception e) {
            System.out.printf("%s%n", e.getMessage());
            try {stmt.close();}
            catch (Exception err) {}
            try {conn.rollback();}
            catch (Exception err) {}
        }
    }
}
