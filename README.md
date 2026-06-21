ОБРАЗОВАТЕЛЬНАЯ ПЛАТФОРМА (HIBERNATE-ВЕРСИЯ)

Проект на Java 21 + JavaFX для управления курсами, студентами и сертификатами.
Взаимодействие с базой данных PostgreSQL через ORM-фреймворк Hibernate (JPA).
Эта версия является эволюцией предыдущей JDBC-реализации и использует объектно-реляционное отображение для сокращения шаблонного кода и автоматического управления связями.

АРХИТЕКТУРА
Приложение построено по трёхслойной модели с дополнительным слоем репозиториев: model (JPA-сущности), repository (обобщённый и специализированные репозитории), service (бизнес-логика), controllers (JavaFX-интерфейс). Пакет db содержит утилиты конфигурации Hibernate и заполнения базы данных. Настройка подключения вынесена в persistence.xml (persistence-unit "courses-pu"). Сборка — Maven, целевая СУБД — PostgreSQL.

ПАКЕТ db
HibernateUtil — потокобезопасный синглтон, управляющий фабрикой EntityManagerFactory. При первом вызове getEntityManagerFactory() создаётся фабрика с именем "courses-pu" на основе persistence.xml. Метод createEntityManager() возвращает новый экземпляр EntityManager для каждого запроса. Приватный конструктор и двойная проверка гарантируют единственный экземпляр. Статический метод close() корректно завершает работу фабрики при остановке приложения.

DataSeeder — утилитный класс с единственным статическим методом seed(). Вызывается при старте приложения. Внутри транзакции проверяет количество записей в таблице пользователей: если больше нуля, заполнение пропускается. Иначе последовательно создаются и сохраняются через EntityManager.persist() объекты: 10 пользователей, 3 преподавателя, 7 студентов, 3 курса, 3 модуля, 3 урока, 3 теста, записи прогресса (7 в course_progress, 3 в lesson_progress) и один сертификат. Все операции выполняются в одной транзакции с явным управлением commit/rollback.

ПАКЕТ model
Содержит JPA-аннотированные классы, соответствующие таблицам базы данных. Каждый класс помечен @Entity и @Table, первичные ключи — @Id с @GeneratedValue(strategy = GenerationType.IDENTITY). Связи между сущностями реализованы через @ManyToOne или @OneToOne с ленивой загрузкой (по умолчанию). Внешние ключи настраиваются через @JoinColumn. Все классы имеют конструкторы без аргументов, геттеры/сеттеры, переопределённые equals/hashCode по первичному ключу и toString.

- User (id, name, email, passwordHash, registrationDate) — базовый пользователь
- Teacher (teacherId, user — @OneToOne, name, email, passwordHash, registrationDate) — преподаватель, расширяет User
- Student (studentId, user — @OneToOne, zachId, name, email, passwordHash, registrationDate) — студент с номером зачётной книжки
- Course (id, teacher — @ManyToOne, name, description, duration)
- CourseModule (id, course — @ManyToOne, name, description)
- Lesson (id, module — @ManyToOne, name, description)
- Test (id, lesson — @ManyToOne, name)
- CourseProgress (progressId, course — @ManyToOne, user — @ManyToOne, progress) — прогресс студента по курсу (not_started, in_progress, completed)
- LessonProgress (progressId, courseProgress — @ManyToOne, lesson — @ManyToOne, user — @ManyToOne, progress) — прогресс по отдельному уроку
- Sert (sertId, course — @ManyToOne, user — @ManyToOne) — выданный сертификат

Использование объектных связей вместо числовых внешних ключей позволяет Hibernate автоматически загружать связанные сущности и выполнять каскадные операции.

ПАКЕТ repository
Базовый класс GenericRepository<T, ID> предоставляет универсальные CRUD-операции для любой сущности. Использует EntityManager, получаемый через HibernateUtil. Основные методы:

- findAll() — JPQL-запрос "FROM Entity", возвращает список всех записей
- findById(ID id) — поиск по первичному ключу через em.find(), возвращает Optional
- save(T entity) — сохранение новой сущности в транзакции (em.persist), в случае ошибки — откат
- update(T entity) — обновление существующей сущности через em.merge() в транзакции
- deleteById(ID id) — удаление по ID с предварительным поиском, в случае отсутствия — возврат false
- count() — количество записей через JPQL SELECT COUNT
- existsById(ID id) — проверка наличия

Каждая операция save, update, deleteById выполняется в явной транзакции с ручным управлением commit/rollback. Для findAll, findById, count используется упрощённое управление ресурсами через try-with-resources (EntityManager закрывается автоматически).

Специализированные репозитории наследуются от GenericRepository, передавая класс сущности в конструктор, и при необходимости добавляют дополнительные методы:

- UserRepository, TeacherRepository, StudentRepository, CourseRepository, ModuleRepository, LessonRepository, TestRepository, LessonProgressRepository — только наследуют GenericRepository, дополнительной логики не содержат.

- CourseProgressRepository добавляет методы findByCourseId(int courseId) и findByUserId(int userId) с параметризованными JPQL-запросами.

- SertRepository содержит транзакционный метод issueCertificateIfCompleted(int userId, int courseId). Внутри явной транзакции проверяется завершённость курса (SELECT COUNT ... WHERE progress = 'completed') и отсутствие уже выданного сертификата. При успехе создаётся и сохраняется новый объект Sert, иначе транзакция откатывается с выбросом IllegalStateException.

ПАКЕТ service
CrudDemoService демонстрирует все CRUD-операции. В отличие от JDBC-версии, методы возвращают String, а не пишут в System.out напрямую. Использует экземпляры конкретных репозиториев.

- demoCreate() — создаёт пользователя, преподавателя, курс, модуль, урок и тест, каждый следующий объект связывается с предыдущим через объектные ссылки (например, Teacher teacher = new Teacher(user, ...)). Возвращает текстовый отчёт.
- demoRead() — получает списки всех курсов и пользователей через findAll(), сортирует по ID и формирует строку.
- demoUpdate() — находит пользователя с ID=1, изменяет email, вызывает userRepo.update().
- demoDelete() — создаёт временного пользователя, удаляет его по ID, проверяет удаление несуществующего ID.
- demoTransaction() — вызывает sertRepo.issueCertificateIfCompleted(4, 1), ожидая ошибки (курс не завершён). Возвращает результат или сообщение об ошибке.

BusinessQueryService содержит 8 аналитических методов, каждый из которых выполняет JPQL-запрос через EntityManager и возвращает ObservableList<Map<String, Object>> для JavaFX-таблиц:

- getStudentCountByCourseData() — LEFT JOIN с CourseProgress, GROUP BY, COUNT(DISTINCT)
- getCourseProgressDetailsData(int courseId) — JOIN с User, фильтрация по courseId
- getTopTeachersData() — JOIN Teacher -> Course -> CourseProgress, GROUP BY, LIMIT 3
- getIssuedCertificatesData() — JOIN Sert -> User -> Course
- getUserActivityData() — LEFT JOIN User -> CourseProgress, SUM CASE
- getCourseTeacherListData() — JOIN Course -> Teacher, возвращает ID курса
- getCourseModuleLessonListData() — JOIN Lesson -> CourseModule -> Course
- getStudentCourseTeacherProgressData() — JOIN User -> Student, LEFT JOIN CourseProgress -> Course -> Teacher, COALESCE для обработки NULL

Все запросы используют объектную нотацию (например, cp.user.id вместо табличных колонок), что исключает необходимость ручного маппинга строк ResultSet.

ПАКЕТ controllers
MainController — основной контроллер JavaFX, идентичный по структуре JDBC-версии, но адаптированный под новые сервисы. Управляет двумя режимами: "CRUD Demo" и "Business Queries". При инициализации создаются панели, загружаются начальные данные для бизнес-отчётов. CRUD-операции теперь вызывают методы CrudDemoService, которые возвращают строки, поэтому отпала необходимость в перехвате System.out. Бизнес-запросы отображаются в динамической таблице с фильтрами (текстовые поля, спиннеры) через FilteredList.

ТОЧКА ВХОДА
Класс app.MainApp расширяет Application. При старте вызывает HibernateUtil.getEntityManagerFactory() для инициализации фабрики, затем DataSeeder.seed() для первоначального заполнения БД. После этого загружает FXML main.fxml, создаёт сцену 1200x700 и отображает окно. При закрытии вызывает HibernateUtil.close().

ОТЛИЧИЯ ОТ JDBC-ВЕРСИИ
- Вместо прямых SQL-запросов и ручного маппинга ResultSet используются JPQL и аннотированные сущности.
- Управление соединениями через EntityManager вместо HikariCP (пул настраивается в persistence.xml).
- Связи между сущностями представлены объектами, а не числовыми ID, что упрощает навигацию и загрузку связанных данных.
- Транзакционная логика реализована на уровне репозиториев через EntityTransaction, без ручного управления Connection.setAutoCommit(false).
- Код стал более лаконичным: исчезли классы DAO с шаблонными методами findAll, findById и т.д., вместо них — обобщённый GenericRepository.
- Инициализация тестовых данных выполняется через persist() графа объектов, а не SQL-вставками.

БЫСТРЫЙ СТАРТ
1. Создайте базу данных PostgreSQL.
2. Настройте src/main/resources/META-INF/persistence.xml (укажите URL, пользователя, пароль, диалект PostgreSQL).
3. Соберите и запустите приложение: mvn clean javafx:run
4. Таблицы и тестовые данные создадутся автоматически при первом запуске (свойство hibernate.hbm2ddl.auto должно быть update или create).

ТЕХНОЛОГИИ
- Java 21, JavaFX
- Hibernate 6 (JPA 3.1, Jakarta Persistence)
- PostgreSQL
- Maven
- SLF4J + Logback

Репозиторий: https://github.com/AlexV384/Courses (ветка hibernate)