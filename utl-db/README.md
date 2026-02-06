
# Java DB Utilities - Работа с БД

- Базовый Core Utility для https://github.com/mostali/udav_project
- Содержит подключенную библиотеку ORMLite https://ormlite.com/  
- Также содержит утилиты для работы с БД через JDBC
- Писался в основном под SQLite, но также поддерживает и другие БД

## 📚 Описание

### Особенность#1
- Стоит выделить класс [UTree](./src/main/java/mp/utl_odb/tree/UTree.java) - так называеме контекстные деревья БД (см.пример ниже)
- Позволяет быстро создавать хранилища данных key=value по разным конекстам (директориям) и использовать различные подходы к получению данных, создавая свои бд как файлы по различным локациям
- Использование бд SQLite позволяет хранить данные бд в одном файле, что обеспечивает быстрый доступ и удобство при работе с данными 
- Часто, для написания модуля приложения/прототипа, такой "контекстной БД" хватает для множества задач сохранения контекста, что в свою очередь, избавляет от создания и поддержки множества моделей данных и соотв. им sql-скриптов


### Особенность#2
- Каждое приложение как правило запускается в каком-то [Namespace](https://github.com/mostali/udav_project/blob/master/utl/src/main/java/mpc/fs/Ns.java), чаще всего совпадающем с ~/.data/APPNAME/fooNamespace*
- Класс [AppCore](src%2Fmain%2Fjava%2Fmp%2Futl_odb%2Fnetapp%2FAppCore.java) отвечает за подготовку таких путей в модуле и быстрый доступ к данным


### Особенность#3
- Разрабатываемое приложение по умолчанию считается сетевым, т.е. имеет какое-то сетевое имя(APP,TG,VK,etc) и пользователей этой сети 
- Модули разрабатываемого приложения также могут иметь своих пользователей, например из Telegram или Vk. Т.о. такие внешние (по отношению к основному приложению) аккаунты объединяются одним аккаунтом пользователя  
- Если не указать имя приложения+сетевое имя - то применится значение по умолчанию *DEF* для имени приложения и для его сетевого имени 


## ✅ Основные классы
[SqlDbUrl](src%2Fmain%2Fjava%2Fmp%2Futl_ndb%2FSqlDbUrl.java) - Первая:) реализация для работы с БД SQLite. По правильному это [JdbcUrl](src%2Fmain%2Fjava%2Fmp%2Futl_ndb%2FJdbcUrl.java), но фактически расширяется некоторыми старыми классами как репозитории БД.    
[NamedDbUrl](src%2Fmain%2Fjava%2Fmp%2Futl_ndb%2FNamedDbUrl.java)  - Именованная БД. БД можно поднять как по имени (в дефолтном контексте), так и по имени файла  
[JdbcUrl](src%2Fmain%2Fjava%2Fmp%2Futl_ndb%2FJdbcUrl.java) - Класс отвечающий за составляющие jdbc url.

## ✅ Work with DB ( via ORMLite )
[DBU](./src/main/java/mp/utl_odb/DBU.java) - Основной класс для создания, получения, удаления записей  
[QP](./src/main/java/mp/utl_odb/QP.java) - QueryParam - используется для создания практически всех видов sql-предикатов в запросе  
[UTree](./src/main/java/mp/utl_odb/tree/UTree.java) - Универсальное хранилище key/value/ext на основе БД Sqlite. Для задания имени хранилища можно использовать Namespace, имя или путь. Поддерживает различные способы работы с данными (TimeAccess, TryCount, Next и др.) Со временем разросся, поэтому постепенно от него отпочковываются классы(см. внутри пакета)    
[TypeDb](./src/main/java/mp/utl_odb/typedb/TypeDb.java) - Каждый экземпляр БД - это 1 тип модели. Для работы создаем модель, расширяющую базовую модель [AModel](src%2Fmain%2Fjava%2Fmp%2Futl_odb%2Fmdl%2FAModel.java)  
[TypeDbEE](./src/main/java/mp/utl_odb/typedb/TypeDbEE.java) - Регистратор, хранилище ссылок на БД и их типов. Решает проблему таскания/запоминания путей. Используется для хранения разных типов (например между разными модулями или приложениями).   
[AppCore](./src/main/java/mp/utl_odb/netapp/AppCore.java) - Класс хранит Namespace приложения - и отдает нужные хранилища (TypeDb, UTree, Path)  


## ✅ Examples 

```java

public class UTree_ExampleSimpleKeyValueStore {

	public static void main(String[] args) {

		DBU.ENABLE_LOG_WARN(); // **clean sqlite-driver log out**

		UTree mydb = UTree.tree("foo"); //create db foo

		mydb.clear(); // clear db if exist

		{ // PUT operation

			mydb.put("key", "value");

			mydb.put("key", "value2", "ext");

			IT.state("value2".equals(mydb.get("key")));

			IT.state(1 == mydb.getCount());

		}

		{ // ADD operation

			mydb.add("key", "value3", null);

			IT.state("value2".equals(mydb.get("key")));

			IT.state("value3".equals(((CtxtDb.CtxTimeModel) mydb.getModelAscDesc(false)).getValue()));

			IT.state(2 == mydb.getCount());

		}

		{// Use
			IT.state(1 == mydb.getModels(QP.limit(1)).size());

			IT.state(2 == mydb.getModels(QP.like("value", "value%")).size());

			IT.state(0 == mydb.getModels(QP.offset(2L), QP.limit(2)).size());

		}

	}
}


public class UTree_ExampleShortLifeCache {
	@SneakyThrows
	public static void main(String[] args) {

		DBU.ENABLE_LOG_WARN();
		UTree myDb = UTree.tree("foo");

		myDb.clear();

		myDb.put("key", "value");

		String value = myDb.getModel_WithMaxLife("key", TimeMark.convertToMs("3s")).getValue();

		value = myDb.getModel_WithMaxLife("key", TimeMark.convertToMs("3s")).getValue(); // *life value 3sec*

		SLEEP.sec(3, "cache is life");

		value = myDb.getModel_WithMaxLife("key", TimeMark.convertToMs("4s")).getValue(); // *value is dead after 3sec*

		SLEEP.sec(1, "cache is death");
		try {
			value = myDb.getModel_WithMaxLife("key", TimeMark.convertToMs("3s")).getValue();
			X.throwException("no here");
		} catch (CtxtlDb.ShortLifeException ex) {
			IT.state(ex.getMessage().contains("ShortLifeException 3000"), ex.getMessage());
		}

	}


}
```
## ✅ Work with DB ( via JDBC)
[Db](src%2Fmain%2Fjava%2Fmp%2Futl_ndb%2FDb.java) - Основной класс для выполнения запросов и получения данных о БД
```javascript
List<List<AbsType>> rows = Db.query_(jdbcUrl, sql, args);
```
[Dbc](src%2Fmain%2Fjava%2Fmp%2Futl_ndb%2FDbc.java) - Мапим ключи таблицы на интерфейс
```javascipt
List<CProject> cProjects = Dbc.query_(CProject.class, jdbcUrl, "select * from projects where uid = %s", uid);
```