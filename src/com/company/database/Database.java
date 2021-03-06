package com.company.database;

import java.io.File;
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
            conn.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
            conn = null;
        }
        isopen = (conn != null);
    }

    public Database(String name, String mode) {
        try {
            dbName = name;
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            conn = DriverManager.getConnection(
                    "jdbc:derby:" + mode + ":" + dbName + ";create=true");
            conn.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
            conn = null;
        }
        isopen = (conn != null);
    }

    public Connection getConnection() {
        return conn;
    }

    public boolean isopen() {
        return isopen;
    }

    public void createTable(String name) throws SQLException {
        PreparedStatement s;
        String sql = "CREATE TABLE " + name + " (Id INT NOT NULL GENERATED BY DEFAULT AS IDENTITY (START WITH 1, INCREMENT BY 1)," +
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
        String regex = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
        String dateRegex = "(0?[1-9]|[12][0-9]|3[01]).(0?[1-9]|1[012]).((19|20)\\d\\d)";
        String name;
        String lastName;
        String patronymic;
        String birthDate;
        String phone;
        String homePhone;
        String address;
        String notes;

        System.out.println("?????????????? ??????");
        name = scanner.next();

        System.out.println("?????????????? ??????????????");
        lastName = scanner.next();

        System.out.println("?????????????? ???????????????? (?????? '-', ???????? ?????? ??????)");
        patronymic = scanner.next();

        System.out.println("?????????????? ???????? ???????????????? ?? ?????????????? dd.mm.yyyy (?????? '-', ?????????? ???????????????? ???????? ????????????)");
        birthDate = scanner.next();
        while (!birthDate.matches(dateRegex) && !birthDate.equals("-")) {
            birthDate = scanner.next();
            System.out.println(birthDate.equals("-"));
            if (birthDate.matches(dateRegex) || birthDate.equals("-")) {
                break;
            } else {
                System.out.println("?????????????? ???????????????????? ???????? ?? ?????????????? dd.mm.yyyy (?????? '-', ?????????? ???????????????? ???????? ????????????)");
            }
        }

        System.out.println("?????????????? ?????????????????? ?????????????? (?????? '-', ???????? ?????? ??????)");
        System.out.println("????????????: ???????????????????? ?????????????????? ?????? ?????????????????? ?? ?????????? ???? 3 ????????");
        phone = scanner.next();
        while (!phone.matches(regex) && !phone.equals("-")) {
            phone = scanner.next();
            if (!phone.matches(regex)) {
                System.out.println("?????????????? ???????????????????? ?????????? ????????????????");
            }
        }

        System.out.println("?????????????? ???????????????? ?????????????? (?????? '-', ???????? ?????? ??????");
        System.out.println("????????????: ???????????????????? ?????????????????? ?????? ?????????????????? ?? ?????????? ???? 3 ????????");
        homePhone = scanner.next();
        if (phone.equals("-") && homePhone.equals("-")) {
            System.out.println("???? ???? ?????????? ??????. ??????????????, ?????????????? ?????????????? ????????????????");
            while (!homePhone.matches(regex)) {
                homePhone = scanner.next();
                if (!homePhone.matches(regex)) {
                    System.out.println("?????????????? ???????????????????? ?????????? ????????????????");
                }
            }
        }

        System.out.println("?????????????? ?????????? (?????? '-', ???????? ?????? ??????)");
        address = scanner.next();

        System.out.println("?????????????? ?????????????????????? (?????? '-', ???????? ?????? ??????)");
        notes = scanner.next();

        if (addContact(name, lastName, patronymic, birthDate, phone, homePhone, address, notes)) {
            System.out.println("?????????????? ?????????????? ????????????????");
        } else {
            System.out.println("???? ?????????????? ?????????????????? ??????????????");
        }
    }

    public boolean addContact(String n, String ln, String ptrn, String brth,
                           String phn, String hPhn, String addr, String nt) {
        PreparedStatement s;
        String sql = "INSERT INTO " + dbName + " (First_Name, Last_Name, Patronymic, Birth_Date, Phone_number, " +
                "Home_number, Address, Notes) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            s = conn.prepareStatement(sql);
            s.setString(1, n);
            s.setString(2, ln);
            s.setString(3, ptrn);
            s.setString(4, brth);
            s.setString(5, phn);
            s.setString(6, hPhn);
            s.setString(7, addr);
            s.setString(8, nt);
            s.executeUpdate();
            s.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteDb() {
        PreparedStatement s;
        try {
            String sql = "DROP TABLE " + dbName;
            s = conn.prepareStatement(sql);
            s.executeUpdate();
            System.out.println("?????????????? ??????????????");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String findByName(String name) {
        PreparedStatement s;
        try {
            String sql = "SELECT * FROM " + dbName + " WHERE First_Name = ?";
            s = conn.prepareStatement(sql);
            s.setString(1, name);
            return s.executeQuery().getString("Id");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void exportDb(String characterDelimiter, String columnDelimiter) {
        try {
            File file = new File(dbName + "_export.csv");
            if (file.exists())
                file.delete();
            PreparedStatement s = conn.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            s.setString(1, null);
            s.setString(2, "CONTACTS");
            s.setString(3, dbName + "_export.csv");
            s.setString(4, columnDelimiter);
            s.setString(5, characterDelimiter);
            s.setString(6, null);
            s.execute();
            System.out.println("???????????? ???????????????????????????? ??????????????. ???????? Contacts_export.csv ?????? ???????????? ?? ???????????????? ????????????????????");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("???? ?????????????? ?????????????????? ?????????????? ????????????");
        }
    }

    public void importDb(String characterDelimiter, String columnDelimiter, String fullPath) {
        try {
            PreparedStatement s = conn.prepareStatement("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE_LOBS_FROM_EXTFILE (?,?,?,?,?,?,?)");
            s.setString(1, null);
            s.setString(2, "CONTACTS");
            s.setString(3, fullPath);
            s.setString(4, columnDelimiter);
            s.setString(5, characterDelimiter);
            s.setString(6, null);
            s.setInt(7, 1);
            s.execute();
            System.out.println("???????????? ?????????????????????????? ??????????????");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("???? ?????????????? ?????????????????? ???????????? ????????????");
        }
    }

    public void updateById(Scanner scanner, long id) {
        System.out.println("???????????????????? ???????????????? ?? Id="+id);
        String regex = "^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$";
        String dateRegex = "(0?[1-9]|[12][0-9]|3[01]).(0?[1-9]|1[012]).((19|20)\\d\\d)";
        String name;
        String lastName;
        String patronymic;
        String birthDate;
        String phone;
        String homePhone;
        String address;
        String notes;

        System.out.println("?????????????? ??????");
        name = scanner.next();

        System.out.println("?????????????? ??????????????");
        lastName = scanner.next();

        System.out.println("?????????????? ???????????????? (?????? '-', ???????? ?????? ??????)");
        patronymic = scanner.next();

        System.out.println("?????????????? ???????? ???????????????? ?? ?????????????? dd.mm.yyyy (?????? '-', ?????????? ???????????????? ???????? ????????????)");
        birthDate = scanner.next();
        while (!birthDate.matches(dateRegex) && !birthDate.equals("-")) {
            birthDate = scanner.next();
            System.out.println(birthDate.equals("-"));
            if (birthDate.matches(dateRegex) || birthDate.equals("-")) {
                break;
            } else {
                System.out.println("?????????????? ???????????????????? ???????? ?? ?????????????? dd.mm.yyyy (?????? '-', ?????????? ???????????????? ???????? ????????????)");
            }
        }

        System.out.println("?????????????? ?????????????????? ?????????????? (?????? '-', ???????? ?????? ??????)");
        System.out.println("????????????: ???????????????????? ?????????????????? ?????? ?????????????????? ?? ?????????? ???? 3 ????????");
        phone = scanner.next();
        while (!phone.matches(regex) && !phone.equals("-")) {
            phone = scanner.next();
            if (!phone.matches(regex)) {
                System.out.println("?????????????? ???????????????????? ?????????? ????????????????");
            }
        }

        System.out.println("?????????????? ???????????????? ?????????????? (?????? '-', ???????? ?????? ??????");
        System.out.println("????????????: ???????????????????? ?????????????????? ?????? ?????????????????? ?? ?????????? ???? 3 ????????");
        homePhone = scanner.next();
        if (phone.equals("-") && homePhone.equals("-")) {
            System.out.println("???? ???? ?????????? ??????. ??????????????, ?????????????? ?????????????? ????????????????");
            while (!homePhone.matches(regex)) {
                homePhone = scanner.next();
                if (!homePhone.matches(regex)) {
                    System.out.println("?????????????? ???????????????????? ?????????? ????????????????");
                }
            }
        }

        System.out.println("?????????????? ?????????? (?????? '-', ???????? ?????? ??????)");
        address = scanner.next();

        System.out.println("?????????????? ?????????????????????? (?????? '-', ???????? ?????? ??????)");
        notes = scanner.next();

        if (updateByIdSql(name, lastName, patronymic, birthDate, phone, homePhone, address, notes, id)) {
            System.out.println("?????????????? ?????????????? ????????????????");
        } else {
            System.out.println("???? ?????????????? ???????????????? ??????????????");
        }
    }

    public boolean updateByIdSql(String n, String ln, String ptrn, String brth,
                                 String phn, String hPhn, String addr, String nt, long id) {
        try {
            String updateSql = "UPDATE " + dbName + " SET First_Name=?, " +
                    "Last_Name=?, " +
                    "Patronymic=?, " +
                    "Birth_Date=?, " +
                    "Phone_number=?, " +
                    "Home_number=?, " +
                    "Address=?, " +
                    "Notes=? " +
                    "WHERE Id=?";

            PreparedStatement s;
            s = conn.prepareStatement(updateSql);
            s.setString(1, n);
            s.setString(2, ln);
            s.setString(3, ptrn);
            s.setString(4, brth);
            s.setString(5, phn);
            s.setString(6, hPhn);
            s.setString(7, addr);
            s.setString(8, nt);
            s.setLong(9, id);
            s.execute();
            s.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteById(String name, long id) {
        try {
            String sql = "DELETE from " + name + " WHERE Id=?";
            PreparedStatement s = conn.prepareCall(sql);
            s.setLong(1, id);
            s.execute();
            s.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteAll() {
        PreparedStatement s;
        try {
            String sql = "DELETE from " + dbName + " WHERE Id >= 1";
            s = conn.prepareStatement(sql);
            s.executeUpdate();
            System.out.println("?????? ???????????????? ??????????????");
        } catch (SQLException e) {
            System.out.println("???????????? ?????? ????????????????! \t" + e.getMessage());
        }
    }

    public void showAllContacts(String database) {
        PreparedStatement s;
        ResultSet rset;
        String sql = "SELECT * FROM " + database;
        try {
            s = conn.prepareStatement(sql);
            rset = s.executeQuery();
            TableOutput tableOutput = new TableOutput();
            List<String> headers = new ArrayList<>();
            headers.add("Id");
            headers.add("??????");
            headers.add("??????????????");
            headers.add("????????????????");
            headers.add("???????? ????????????????");
            headers.add("??????. ??????????????");
            headers.add("??????. ??????????????");
            headers.add("??????????");
            headers.add("??????????????");
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
                row.add(birthDate);
                row.add(phone);
                row.add(homePhone);
                row.add(address);
                row.add(notes);
                rowsList.add(row);
            }
            System.out.println(tableOutput.generateTable(headers, rowsList));
            s.close();
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
}
