package com.company.database;

import java.util.Scanner;

public class Main {
    public static Scanner scanner;

    public static void main(String[] args) {
        String command = "";
        String dbName = "Contacts";
        Database db;

        scanner = new Scanner(System.in);
        db = new Database(dbName);
        System.out.println(db.isopen() ? "Соединение установлено" : "Нет соединения");
        try {
            if (!db.isCreated())
                db.createTable(dbName);
            db.showAllContacts(dbName);
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        showAllComands();
        try {
            while (!command.equals("exit")) {
                System.out.print("> ");
                command = scanner.next();
                switch (command) {
                    case "list":
                        db.showAllContacts(dbName);
                        break;
                    case "add":
                        db.cmdAdd(scanner);
                        break;
                    case "exit":
                        System.out.println("До встречи");
                        db.exit();
                        break;
                    case "find":
                        System.out.println("Введите имя контакта, который нужно найти");
                        String userName = scanner.next();
                        System.out.println("Id пользователя: " + db.findByName(userName));
                        break;
                    case "drop":
                        db.deleteDb();
                        break;
                    case "delete":
                        System.out.println("Введите Id контакта, который нужно удалить");
                        System.out.println("Введите -1, чтобы удалить все контакты");
                        long id = scanner.nextLong();
                        if (id == -1)
                            db.deleteAll();
                        else {
                            if (db.deleteById(dbName, id))
                                System.out.println("Контакт с Id=" + id + " успешно удален");
                            else
                                System.out.println("Не удалось удалить контакт");
                        }
                        break;
                    case "edit":
                        System.out.println("Введите Id контакта, который нужно обновить");
                        long updId = scanner.nextLong();
                        db.updateById(scanner, updId);
                        break;
                    case "created":
                        System.out.println(db.isCreated() ? "База существует" : "База не существует");
                        break;
                    case "export":
                        db.exportDb(null, ",");
                        break;
                    case "import":
                        System.out.println("Введите полный путь к CSV файлу");
                        System.out.println("Пример: C:\\HW6_196_Khojaakhmedov_Boburbek\\Contacts.csv");
                        String path = scanner.next();
                        db.importDb(null, ",", path);
                        break;
                    case "about":
                        System.out.println("Проект был выполнен Хожаахмедовым Бобурбеком, группа БПИ196");
                        System.out.println("   _________\n" +
                                "    / ======= \\\n" +
                                "   / __________\\\n" +
                                "  | ___________ |\n" +
                                "  | | -       | |\n" +
                                "  | |         | |\n" +
                                "  | |_________| |________________________\n" +
                                "  \\=____________/   Apache Derby      )\n" +
                                "  / \"\"\"\"\"\"\"\"\"\"\" \\                    /\n" +
                                " / ::::::::::::: \\                  -'\n" +
                                "(_________________)");
                        break;
                    case "help":
                        showAllComands();
                    default:
                        System.out.println("help - чтобы узнать список команд");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void showAllComands() {
        System.out.println("list - увидеть текущие записи в БД");
        System.out.println("add - добавить новый контакт");
        System.out.println("drop - удалить БД");
        System.out.println("delete - удалить контакт(ы)");
        System.out.println("find - найти контакт по имени");
        System.out.println("edit - обновить контакт");
        System.out.println("created - создана ли БД");
        System.out.println("export - экспортировать записи в файл Contacts.csv");
        System.out.println("import - импортировать записи из файла Contacts.csv");
        System.out.println("about - информация о программе");
        System.out.println("exit - выход");
    }
}
