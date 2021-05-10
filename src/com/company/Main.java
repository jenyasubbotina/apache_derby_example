package com.company;

import java.util.Scanner;

public class Main {
    public static Scanner scanner;

    public static void main(String[] args) {
        String command = "";
        String dbName = "contacts";
        Database db;

        scanner = new Scanner(System.in);
        db = new Database(dbName);
        System.out.println(db.isopen() ? "Соединение установлено" : "Нет соединения");
        try {
            if (!db.isCreated())
                db.createTable();
            db.showAllContacts();
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }

        try {
            while (!command.equals("exit")) {
                System.out.print("> ");
                command = scanner.next();
                switch (command) {
                    case "list":
                        db.showAllContacts();
                        break;
                    case "add":
                        db.cmdAdd(scanner);
                        break;
                    case "exit":
                        db.exit();
                        break;
                    case "delete":
                        db.deleteAll();
                        break;
                    case "created":
                        System.out.println(db.isCreated() ? "База существует" : "База не существует");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
