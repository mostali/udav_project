
# Java DB Utilities - Работа с БД

- Базовый Core Utility для https://github.com/mostali/udav_project
- Содержит подключенную библиотеку ORMLite https://ormlite.com/  
- Также содержит утилиты для работы с БД через JDBC
- Писался в основном под SQLite, но также поддерживает и другие БД

## 📚 Описание

### Особенность#1
- Стоит выделить класс [UTree](./src/main/java/mp/utl_odb/tree/UTree.java) - так называеме контекстные деревья БД (см.пример ниже)
- Позволяет быстро создавать хранилища данных key=value по разным конекстам (директориям) и использовать различные подходы к получению данных, создавая свои бд как файлы по различным локациям
- Часто, для написания модуля приложения/прототипа, такой "контекстной БД" хватает для множества задач сохранения контекста, что в свою очередь, избавляет от создания и поддержки множества моделей данных и соотв. им sql-скриптов


### Особенность#2
- Каждое приложение как правило запускается в каком-то [Namespace](https://github.com/mostali/udav_project/blob/master/utl/src/main/java/mpc/fs/Ns.java), чаще всего совпадающем с ~/.data/APPNAME/fooNamespace*
- Класс [AppCore](src%2Fmain%2Fjava%2Fmp%2Futl_odb%2Fnetapp%2FAppCore.java) отвечает за подготовку таких путей и представлен наследником в каждом приложении/модуле


### Особенность#3
- Разрабатываемое приложение по умолчанию считается сетевым, т.е. имеет какое-то сетевое имя(TG,VK,APP) и пользователей этой сети 
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
public static class UTreeExamples {
	public static void main(String[] args) {
//			case_PutGet();
		case_TIME_CACHED_VALUE();
		case_TIME_LIMITED_STATE();
		case_TIME_ACCESS();
	}

	@SneakyThrows
	public static void case_TIME_CACHED_VALUE() {

		UTree fooTree = UTree.tree("foo");
        //UTree fooTree = UTree.tree("parent","key");
        //UTree fooTree = UTree.tree("rootDir", "parent", "key");
        //UTree fooTree = UTree.tree("rootDir", "parent", "key");
        //UTree fooTree = UTree.tree(Component.class, "key");
        //UTree fooTree = UTree.tree(Namespace, "key");
			
		//if lifetime value less that 10sec that return 'foo_value'
		//else throw CtxtlDb$UtreeDelayException: CACHE_MS:-1ms:::2023-09-09 13:09:00:::diffabs=11s
		String fooValue = fooTree.getTC("foo_key", 10_000);
		P.p(fooValue);

	}

	@SneakyThrows
	public static void case_TIME_LIMITED_STATE() {

		UTree fooTree = UTree.tree("foo");

		//Pare4{ ALLOWED, WAIT_MS , NEXT_ACTION_DATE_MS, TIME_MODEL }
		boolean regAction = false;
		Pare4<Boolean, Long, Long, CtxTimeModel> fooValue = fooTree.getTLS("foo_key", 10, regAction);
		UC.state(fooValue.key(), "please wait ( less 10s) ", EPOCH.epochToDate(fooValue.ext().intValue()));
		P.p(fooValue.key());//true if less 10 sec after action, else false

		try{
			fooTree.getTLS("foo_key_wrong", 10, false);
		}catch (RequiredRuntimeException ex){
			//ok
		}
	}

	@SneakyThrows
	public static void case_TIME_ACCESS() {

		UTree fooTree = UTree.tree("foo");

		//return 'foo_value' or throw CtxtlDb$UtreeDelayException: ALLOWED_HOUR:9
		String fooValue = fooTree.getTA("foo_key", ETA.ALLOWED_HOUR.paramHoursOrDays(QDate.now().hour));
		P.p(fooValue);
	}

	public static void case_PutGet() {
		//Put & Get
		UTree fooTree = UTree.tree("foo");
		fooTree.put("foo_key", "foo_value");
		P.p(fooTree.get("foo_key"));//foo_value
		try {
			P.p(fooTree.get("foo_key_wrong"));//key not found
		} catch (RequiredRuntimeException ex) {
			P.w(ex.getMessage());
			P.p(fooTree.get("foo_key_wrong", null));//null
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