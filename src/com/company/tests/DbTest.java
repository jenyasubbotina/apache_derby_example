package com.company.tests;

import com.company.database.Database;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DbTest {

    Database database;
    Connection connection;
    private static final String mode = "memory";
    private static final String testDbName = "ContactsTest";

    @Before
    public void createDatabase() throws SQLException {
        database = new Database(testDbName, mode);
        if (!database.isCreated()) {
            database.createTable(testDbName);
        }
        connection = database.getConnection();
    }

    // сохранение контакта
    @Test
    public void database1Insert() {
        Assert.assertTrue(database.addContact("Test", "Test", "Test", "01.02.2002",
                "+78894573", "-", "Test address", "-"));
        System.out.println("После добавления контакта, 1 тест");
        database.showAllContacts(testDbName);
    }

    // поиск контакта
    @Test
    public void database2Select() throws Exception {
        String selectSql = "SELECT Id FROM " + testDbName + " WHERE " + testDbName + ".First_Name = ?";
        PreparedStatement ps = connection.prepareStatement(selectSql);
        ps.setString(1, "Test");
        Assert.assertTrue(ps.executeQuery().next());
    }

    // обновление контакта
    @Test
    public void database3Update() {
        Assert.assertTrue(database.updateByIdSql("Test2", "Test2", "Test2", "01.02.2002",
                "+78894573", "-", "Test2 address", "Note2", 1));
        System.out.println("После обновления контакта, 3 тест");
        database.showAllContacts(testDbName);
    }

    // удаление контакта
    @Test
    public void database4Delete() {
        Assert.assertTrue(database.deleteById(testDbName, 1));
        System.out.println("После удаления контакта, 4 тест");
        database.showAllContacts(testDbName);
    }

    @After
    public void shutdownDatabase() {
        database.exit();
    }
}
