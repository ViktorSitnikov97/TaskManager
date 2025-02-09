# Проект  "Менеджер задач"

### Hexlet tests and linter status:
[![Actions Status](https://github.com/ViktorSitnikov97/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/ViktorSitnikov97/java-project-99/actions)[![example workflow](https://github.com/ViktorSitnikov97/java-project-99/actions/workflows/main.yml/badge.svg)](https://github.com/ViktorSitnikov97/java-project-99/actions)[![Maintainability](https://api.codeclimate.com/v1/badges/cac9c0b6e1beccdadce0/maintainability)](https://codeclimate.com/github/ViktorSitnikov97/java-project-99/maintainability)[![Test Coverage](https://api.codeclimate.com/v1/badges/cac9c0b6e1beccdadce0/test_coverage)](https://codeclimate.com/github/ViktorSitnikov97/java-project-99/test_coverage)

## Описание 
Task Manager – система управления задачами, подобная http://www.redmine.org/.
Сервис позволяет ставить задачи, назначать исполнителей, менять статусы задач,
а также устанавливать на них метки.
Для работы с системой требуется регистрация и аутентификация.
## Реализация

## Требования для локального запуска
Иметь, либо установить:

> [Git installed](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)
> 
> [Java](https://www.oracle.com/java/technologies/downloads/)
> 
> [Gradle](https://gradle.org/install/)

## Установка и запуск
```
git clone git@github.com:ViktorSitnikov97/java-project-99.git
make run
```
## Технологический стек
> * Фреймворк: Spring Boot
> * Аутентификация: Spring Security
> * Автоматический маппинг: Mapstruct
> * Внешнее отслеживание ошибок: Sentry
> * Документация по API-приложения: Springdoc Openapi, Swagger
> * Тесты: JUnit 5, Mockwebserver, Datafaker
> * Отчет о тестах: Jacoco
> * Линтер: Checkstyle
> * Базы данных: H2 (development), PostgreSQL (production)
> * Развертывание в production: Docker
> * [Задеплоено](https://task-manager-0831.onrender.com) на бесплатный сервер от Render

## Демонстрация