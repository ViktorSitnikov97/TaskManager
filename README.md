# Проект  "Менеджер задач"

### Hexlet tests and linter status:
[![Actions Status](https://github.com/ViktorSitnikov97/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/ViktorSitnikov97/java-project-99/actions)[![example workflow](https://github.com/ViktorSitnikov97/java-project-99/actions/workflows/main.yml/badge.svg)](https://github.com/ViktorSitnikov97/java-project-99/actions)[![Maintainability](https://api.codeclimate.com/v1/badges/cac9c0b6e1beccdadce0/maintainability)](https://codeclimate.com/github/ViktorSitnikov97/java-project-99/maintainability)[![Test Coverage](https://api.codeclimate.com/v1/badges/cac9c0b6e1beccdadce0/test_coverage)](https://codeclimate.com/github/ViktorSitnikov97/java-project-99/test_coverage)

## Описание 
Task Manager – система управления задачами, подобная http://www.redmine.org/.
Сервис позволяет ставить задачи, назначать исполнителей, менять статусы задач,
а также устанавливать на них метки.
Для работы с системой требуется регистрация и аутентификация.
## Реализация
Приложение Task Manager реализовано с помощью фреймворка Spring Boot согласно концепции MVC и с архитектурным подходом REST API.
Приложение включает в себя механизм аутентификации и авторизации с использованием JWT-токена. Большое внимание в этом проекте
уделяется созданию сущностей с помощью ORM и описанию связей между ними (o2m, m2m) средствами Spring Data Jpa.
Для большего уровня автоматизации, в проекте используется ресурсный роутинг, который позволяет унифицировать и упростить работу с
типичными CRUD–операциями. Как только на сайте появляются пользователи с возможностью что-то создать, тут же возникает авторизация.
Она часто задействуется при попытке изменить запрещенные вещи, например, настройки чужого пользователя. В проекте реализован механизм
фильтрации данных с помощью Specifications Spring Data Jpa. Тестирование приложения реализовано с помощью интеграционных тестов с
использованием MockMvc, т.е. с подменой веб-сервера и без реального соединения по HTTP, чтобы ускорить запуск и выполнение тестов.
Для тестирования автоматически создаются объекты на базе сущностей с помощью библиотек Instancio и DataFaker и сохраняются в базу
данных H2. Для использования приложения в Production используется база данных PostgreSQL. В качестве линтера используется Checkstyle.
Приложение задеплоено на бесплатный сервер Render'а. Для генерации документации по API приложения используется Swagger UI.

## Требования для локального запуска
Иметь, либо установить:

> [Git installed](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
> 
> [Java](https://www.oracle.com/java/technologies/downloads/)
> 
> [Gradle](https://gradle.org/install/)

## Установка и запуск локально
```
git clone git@github.com:ViktorSitnikov97/java-project-99.git
cd java-project-99/
make run
```

## Авторизация
```
Username: admin@example.com
Password: admin
```

## Технологический стек
> * Фреймворк: Spring Boot
> * Аутентификация: Spring Security
> * Автоматический маппинг: Mapstruct
> * Документация по API-приложения: Springdoc Openapi, Swagger
> * Тесты: JUnit 5, Mockwebserver, Datafaker
> * Отчет о тестах: Jacoco
> * Линтер: Checkstyle
> * Базы данных: H2 (development), PostgreSQL (production)
> * Развертывание в production: Docker
> * [Задеплоено](https://task-manager-0831.onrender.com) на бесплатный сервер от Render

## Демонстрация
