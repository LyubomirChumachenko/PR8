# lab-8
За основу была взята Практическая работа 7(без авторизации)

## Предварительные требования
1.Убедитесь, что у вас установлен **PostgreSQL** - для работы с базой данных.
Подключитесь к PostgreSQL
```
psql -U your_username
```
Создайте базу данных:
```
CREATE DATABASE sg_db;
```
Перейдите в созданную базу данных
```
\c sg_db
```
Создайте пользователя и предоставьте ему доступ.
```
CREATE USER postgres WITH ENCRYPTED PASSWORD '0890';
GRANT ALL PRIVILEGES ON DATABASE sg_db TO postgres;
```
2.Скачайте и запустите брокер для обмена сообщениями ActiveMQ: https://activemq.apache.org/components/classic/download/ версия: ActiveMQ Classic 6.1.6 (Mar 7th, 2025)
Windows:
-cd [путь-к-activemq]\bin
-activemq.bat start (Запуск ActiveMQ)
-activemq.bat stop (Остановка ActiveMQ, после выхода из приложения)
Linux/Mac:
-cd [путь-к-activemq]/bin
-./activemq start (Запуск ActiveMQ)
-./activemq stop (Остановка ActiveMQ, после выхода из приложения)


## Развёртывание проекта
Откройте новое окно терминала

1.Чтобы начать сборку проекта скачайте Maven: https://maven.apache.org/download.cgi
-- перейдите в корневую папку проекта(где находится файл pom.xml) и введите команду:
-Windows:
"[ваш-путь-к-maven]\bin\mvn.cmd" install -f "pom.xml"
-Linux:
./[ваш-путь-к-maven]/bin/mvn install -f ./pom.xml

2.Чтобы запустить программу после сборки введите команду:
java -jar target/parfume-app-0.0.1-SNAPSHOT.jar --spring.config.location=classpath:/application.properties


## Удаление приложения и зависимостей(свёртывание)

1.Для выхода из приложения:
- Выберите пункт меню 6.Выход
- Зажммите сочетание клавиш Ctrl + C

2.Чтобы удалить зависимости и jar файл:
-- перейдите в корневую папку проекта(где находится файл pom.xml) и введите команду:
-Windows:
"[ваш-путь-к-maven]\bin\mvn.cmd" clean -f "pom.xml"
-Linux:
./[ваш-путь-к-maven]/bin/mvn clean -f ./pom.xml

3.Удалите проектные файлы и папки, если они больше не нужны, вручную или через команду:
-rm -r путь_к_проекту