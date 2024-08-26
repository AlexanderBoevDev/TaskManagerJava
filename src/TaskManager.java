import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TaskManager {
    private List<Task> tasks;
    private Scanner scanner;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public TaskManager() {
        tasks = new ArrayList<>();
        scanner = new Scanner(System.in);
        createDirectories(); // Создание папок при первом запуске
    }

    // Метод для создания необходимых директорий
    private void createDirectories() {
        File tasksDir = new File("../tasks");
        File importDir = new File("../import");

        if (!tasksDir.exists()) {
            tasksDir.mkdirs();
            System.out.println("Создана директория для задач: tasks");
        }

        if (!importDir.exists()) {
            importDir.mkdirs();
            System.out.println("Создана директория для импорта задач: import");
        }
    }

    public void run() {
        // Вывод сообщения при старте программы
        System.out.println("~~Завтра~~ \u001B[31mСегодня!\u001B[0m");

        loadTasksForToday(); // Загрузка задач на сегодня при старте программы

        boolean running = true;

        while (running) {
            System.out.println("\nМенеджер задач:");
            System.out.println("1. Показать задачи");
            System.out.println("2. Добавить задачу");
            System.out.println("3. Удалить задачу");
            System.out.println("4. Завершить задачу");
            System.out.println("5. Редактировать задачу");
            System.out.println("6. Сохранить задачи");
            System.out.println("7. Импортировать задачи");
            if (!tasks.isEmpty()) {
                System.out.println("8. Показать задачи по дате");
                System.out.println("9. Выйти");
            } else {
                System.out.println("8. Выйти");
            }
            System.out.print("Введите ваш выбор: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Поглощение новой строки

                switch (choice) {
                    case 1:
                        selectDateRange();
                        break;
                    case 2:
                        addTask();
                        break;
                    case 3:
                        removeTask();
                        break;
                    case 4:
                        completeTask();
                        break;
                    case 5:
                        editTask();
                        break;
                    case 6:
                        saveTasks();
                        break;
                    case 7:
                        importTasks();
                        break;
                    case 8:
                        if (!tasks.isEmpty()) {
                            selectDateRange();
                            break;
                        } // Переход к выходу, если задач нет
                    case 9:
                        if (!tasks.isEmpty()) {
                            running = false;
                            saveTasks(); // Сохранение задач при выходе
                            break;
                        } // Некорректный ввод
                    default:
                        System.out.println("Введен некорректный запрос, попробуйте снова.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Введен некорректный запрос, попробуйте снова.");
                scanner.nextLine(); // Очистка ввода для предотвращения зацикливания
            }
        }
    }

private void selectDateRange() {
    System.out.println("Выберите период для отображения задач:");
    System.out.println("1. Сегодня");
    System.out.println("2. Завтра");
    System.out.println("3. Неделя");
    System.out.println("4. Месяц");
    System.out.println("5. Вчера");
    System.out.println("6. Прошедшие 3 дня");
    System.out.println("7. Прошедшая неделя");
    System.out.println("8. Прошедший месяц");
    System.out.println("9. Указать свой период");
    System.out.print("Введите ваш выбор: ");

    int choice = scanner.nextInt();
    scanner.nextLine(); // Поглощение новой строки

    LocalDate startDate = LocalDate.now();
    LocalDate endDate = startDate;

    switch (choice) {
        case 1:
            // Сегодня
            break;
        case 2:
            // Завтра
            startDate = startDate.plusDays(1);
            endDate = startDate;
            break;
        case 3:
            // Неделя
            endDate = startDate.plusDays(6);
            break;
        case 4:
            // Месяц
            endDate = startDate.plusMonths(1).minusDays(1);
            break;
        case 5:
            // Вчера
            startDate = startDate.minusDays(1);
            endDate = startDate;
            break;
        case 6:
            // Прошедшие 3 дня
            startDate = startDate.minusDays(3);
            break;
        case 7:
            // Прошедшая неделя
            startDate = startDate.minusDays(7);
            break;
        case 8:
            // Прошедший месяц
            startDate = startDate.minusMonths(1).plusDays(1);
            break;
        case 9:
            // Указать свой период
            System.out.print("Введите начальную дату (дд-мм-гггг): ");
            startDate = LocalDate.parse(scanner.nextLine(), dateFormatter);
            System.out.print("Введите конечную дату (дд-мм-гггг): ");
            endDate = LocalDate.parse(scanner.nextLine(), dateFormatter);
            break;
        default:
            System.out.println("Некорректный выбор. Отображаю задачи на сегодня.");
      }
		  showTasksInRange(startDate, endDate);
		}

    private void showTasksInRange(LocalDate startDate, LocalDate endDate) {
        boolean tasksFound = false;
        int index = 1;

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            loadTasksFromFile("../tasks/" + date.format(dateFormatter) + ".txt");
            for (Task task : tasks) {
                System.out.printf("[%d] %s\n", index, task);
                tasksFound = true;
                index++;
            }
        }

        if (!tasksFound) {
            if (startDate.equals(LocalDate.now())) {
                System.out.println("На сегодня задач нет. Создайте новую задачу на сегодня.");
            } else {
                System.out.println("Задач в выбранный период нет.");
            }
        }
    }

    private void addTask() {
        System.out.print("Введите описание задачи: ");
        String description = scanner.nextLine();
        System.out.print("Введите дату завершения задачи (дд-мм-гггг) или нажмите Enter для завершения в конце дня: ");
        String dueDateInput = scanner.nextLine();

        LocalDateTime dueDate;
        if (dueDateInput.isEmpty()) {
            dueDate = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        } else {
            try {
                dueDate = LocalDate.parse(dueDateInput, dateFormatter).atTime(LocalTime.MAX);
            } catch (DateTimeParseException e) {
                System.out.println("Некорректный формат даты. Используйте дд-мм-гггг.");
                return;
            }
        }

        Task newTask = new Task(description, dueDate);
        tasks.add(newTask);
        System.out.println("Задача добавлена.");

        // Сохранение задачи сразу после добавления
        saveTasks();
    }

    private void removeTask() {
        System.out.print("Введите индекс задачи для удаления: ");
        int index = scanner.nextInt() - 1;
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
            System.out.println("Задача удалена.");
            saveTasks(); // Сохранение задач после удаления
        } else {
            System.out.println("Некорректный индекс задачи.");
        }
    }

    private void completeTask() {
        System.out.print("Введите индекс задачи для завершения: ");
        int index = scanner.nextInt() - 1;
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).complete();
            System.out.println("Задача завершена.");
            saveTasks(); // Сохранение задач после завершения
        } else {
            System.out.println("Некорректный индекс задачи.");
        }
    }

    private void editTask() {
        System.out.print("Введите индекс задачи для редактирования: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine(); // Поглощение новой строки
        if (index >= 0 && index < tasks.size()) {
            Task task = tasks.get(index);

            System.out.print("Введите новое описание задачи (оставьте пустым для сохранения текущего): ");
            String newDescription = scanner.nextLine();
            if (!newDescription.isEmpty()) {
                task.setDescription(newDescription);
            }

            System.out.print("Введите новую дату завершения задачи (дд-мм-гггг) или нажмите Enter для сохранения текущей: ");
            String newDueDateInput = scanner.nextLine();
            if (!newDueDateInput.isEmpty()) {
                try {
                    LocalDateTime newDueDate = LocalDate.parse(newDueDateInput, dateFormatter).atTime(LocalTime.MAX);
                    task.setDueDate(newDueDate);
                } catch (DateTimeParseException e) {
                    System.out.println("Некорректный формат даты. Используйте дд-мм-гггг.");
                    return;
                }
            }

            System.out.println("Задача обновлена.");
            saveTasks(); // Сохранение задач после редактирования
        } else {
            System.out.println("Некорректный индекс задачи.");
        }
    }

    // Метод для сохранения задач в файл
    private void saveTasks() {
        if (tasks.isEmpty()) return;

        LocalDate date = tasks.get(0).getDueDate().toLocalDate();
        String filename = "../tasks/" + date.format(dateFormatter) + ".txt";
        File file = new File(filename);
        File dir = file.getParentFile();

        // Создание директории, если она не существует
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Task task : tasks) {
                writer.println(task.isCompleted() + ";" + task.getDescription() + ";" + task.getCreationDate() + ";" + task.getDueDate());
            }
            System.out.println("Задачи сохранены.");
        } catch (IOException e) {
            System.out.println("Произошла ошибка при сохранении задач: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Метод для загрузки задач из файла
    private void loadTasksFromFile(String filename) {
        tasks.clear();
        File file = new File(filename);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    Task task = new Task(parts[1], LocalDateTime.parse(parts[3]));
                    if (Boolean.parseBoolean(parts[0])) {
                        task.complete();
                    }
                    tasks.add(task);
                }
            } catch (IOException e) {
                System.out.println("Произошла ошибка при загрузке задач: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadTasksForToday() {
        String todayFilename = "../tasks/" + LocalDate.now().format(dateFormatter) + ".txt";
        loadTasksFromFile(todayFilename);
    }

    // Метод для импорта задач из файлов в папке import
    private void importTasks() {
        File importDir = new File("../import");
        File[] files = importDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("Нет файлов для импорта.");
            System.out.println("Загрузите текстовые файлы для импорта задач в папку: import");
            System.out.println("Формат файла: каждая строка должна содержать задачу в следующем формате:");
            System.out.println("[завершена(true/false)];[описание задачи];[дата создания];[дата завершения]");
            System.out.println("Пример строки: false;Сделать домашнее задание;2024-08-25T10:15;2024-08-26T23:59");
            return;
        }

        for (File file : files) {
            loadTasksFromFile(file.getPath());
            System.out.println("Задачи из файла " + file.getName() + " импортированы.");
        }
        saveTasks(); // Сохранение задач после импорта
    }
}
