

# Java Core Utilities - Базовые утилиты
- Базовая библиотека для [Udav Java Project](https://github.com/mostali/udav_project)
- Используется в вэб-проектах, создания/чтения rest, чат-боты, etc.

## 📚 Описание

### Особенность#1 - Часто-используемые рабочие классы
- Основной рабочий утилитарный класс [X](./src/main/java/mpc/X.java) (т.е. что это?) выполняет 5 ключевых функций - проверка на пустоту объектов, получение размера объектов, форматирование строк, печать объектов и приведение типов
- Аналог guava Preconditions - [ERR](./src/main/java/mpc/ERR.java)
- Также стоит отметить полезные и часто используемые классы 
[Sys](./src/main/java/mpc/Sys.java)(система),
[Arr](./src/main/java/mpc/arr/Arr.java)(массивы),
[ArrItem](./src/main/java/mpc/arr/ArrItem.java)(получение элементов массивов),
[USToken](./src/main/java/mpc/str/USToken.java) (кусает строки спереди и сзади),
[JOIN](./src/main/java/mpc/str/JOIN.java)/[SPLIT](./src/main/java/mpc/str/SPLIT.java) (джоин/сплит строк),
[RFL](./src/main/java/mpc/rfl/RFL.java) (Рефлексия)
[STR](./src/main/java/mpc/str/STR.java) (утилиты для строк),
[UST](./src/main/java/mpc/str/UST.java) (конвертит строки в объекты, и объекты в объекты [ObjTo](./src/main/java/mpc/str/ObjTo.java)), 
[Rt](src%2Fmain%2Fjava%2Fmpc%2Fstring%2FRt.java) (строит pretty отчеты для List&Map), 
[UFS](./src/main/java/mpc/fs/UFS.java) (операции с файлами ),
[RW](./src/main/java/mpc/fs/RW.java) (читаем/пишем файлы, serilazble, etc),
[JHttp](./src/main/java/mpc/net/JHttp.java) - native http-client (опционально okclient/apacheclient) 

### Особенность#2 Специфичная практика написания методов
Управление методом происходит за счет указания спец. множественной переменной ...defRq (default or Required), т.е. метод либо гарантировано вернет не нулевое значение, либо кинет ошибку обязательности, либо вернет значение по умолчанию. Применение такого подхода избавляет от написания и поддержки дополнительных методов-обработчиков ошибок.
```javasctipt
public static Integer INT(String str, Integer... defRq) {
		try {
			return Integer.parseInt(str);
		} catch (Exception ex) {
			return ARG.toDefThrow(() -> new RequiredRuntimeException("Wrong INT from string '%s'", str), defRq);
		}
}
```

### Особенность#3
- Множество классов, маркируемые например как U* (вместо как часто это бывает *Utils) выделяют предметную область или сервис и содежат с-но сервисы (как входные точки), утилиты, константы. Такой подход делает такие классы как бы "микросервисом".
- Ключевой особенностью подобной архитектуры классов является консолидация бизнес логики сервиса в одном методе и с-но сигнатуре метода. Это позволяет избежать дублирования кода и с-но их множественной поддержки, внесения исправлений. По сигнатуре метода сразу видно весь необходимый контекст, который необходимо создавать для вызова метода. А перегрузка таких "сервисных" методов позволяет быстро создавать их различные модификации.
- Применение такого "статического" подхода к вызову метода позволяет обращаться к сервису декларативно, т.е. одной строкой, что позволяет писать и рефакторить код быстрее. К тому же такой код становится более типичным, читаемым, классы группируются, быстрее ищутся и запоминаются

### Особенность#4
- Класс ошибок *EE extends EException/ERxception - так называемые именнованные ошибки - позволяют создавать кастомные enum-типы ошибок - т.е. работать с ошибками как с Enum  
- Впоследствии, такой подход стал применяться и на сервисах - которые кидают именованные EE-ошибки. С-но такие сервисы помечены суффиксом *EE
- В результате такого подхода решается проблема с поддержкой множества ошибок одного сервиса. Поскольку, такой сервис всегда выкидывает только одну EE-ошибку с внтренним типом enum

### Особенность#5
- Приложение на проде, как правило, запускается из папки ~/APPNAME/app.jar  
- Каждое приложение имеет имя - APPNAME (задается в application.properties)  
- Хранилище приложения (local|dev|prod), по умолчанию, располагается в папке ~/.data/APPNAME  
- Т.о. на одной машине можно запускать несколько разных приложений(или модулей), не беспокоясь что их хранилища пересекутся  
- Дополнительной фичей такого подхода является возможность одной командой прокинуть файловую систему прода к себе локально (via sshfs), т.е. протестировать приложение локально на данных с прода
- За формирование и хранение основных рабочих каталогов и путей отвечает класс [Env](./src/main/java/mpc/env/Env.java)
- Класс [AP](./src/main/java/mpc/env/AP.java) отвечает за работу с *application.properties*. Умеет работать с профилями. Свойства располагающиеся в *./.data/APPNAME/application.properties* (если есть) - считаются мастер-данными 
- Дополнительно, о структуре приложения можно почитать в модуле [UtlDb](https://github.com/mostali/udav_project/tree/master/utl-db)

### Фича#6
- Объявляемые логгеры всегда называются ***L*** 
- Также существует отдельный класс [L](./src/main/java/mpc/log/L.java) с методами, аналогичным классу Logger
- Такой подход появился в результате необходимости быстрой подмены стандартных логгеров и перенапрвления вывода в консоль/файл.

### Бага#7
Может содержать откровенные баги и недочеты, поскольку некоторые классы были написаны очень давно


## ✅ Основные классы

[X](./src/main/java/mpc/X.java) - Проверка на пустоту + размер объектов _.size()_  
[ERR](./src/main/java/mpc/UC.java) - Checks ( Аналог _Preconditions_ )  

[Arr](./src/main/java/mpc/arr/Arr.java) - Массивы & Map's  
[ArrItem](./src/main/java/mpc/arr/ARItem.java) - Массивы & Map's - First & Last

[UStr](./src/main/java/mpc/str/STR.java) - Общие методы для работы со строками ( _startsWith, endsWith_ )    
[UST](./src/main/java/mpc/str/UST.java) - Конвертим строки в объекты  
[USToken](./src/main/java/mpc/str/USToken.java) - Получение частей строк по делиметру (_first, last, firstGreedy, lastGreedy_)  
[Cmd3](src%2Fmain%2Fjava%2Fmpc%2Ftypes%2Ftks%2Fcmt%2FCmd3.java)-7 - парсер строки в токены. Последняя простая и самая удачная реализация абстрактного решения задачи парсинга строки в токены (мапинга строки на объект)  
[Rt](src%2Fmain%2Fjava%2Fmpc%2Fstring%2FRt.java) - Report - строим отчеты  из List & Map  
[StringConditionType.java](src%2Fmain%2Fjava%2Fmpc%2Fstring%2Fcondition%2FStringConditionType.java) - Кондиции строк (_EQ,CONTAINS,REGEX,STARTS,STARTSIC,ENDS,..._)  
[URx](src%2Fmain%2Fjava%2Fmpc%2Fregex%2FURx.java) - Рефжекс  
[SYM](src%2Fmain%2Fjava%2Fmpc%2Fstring%2Fsym%2FSYM.java) - Набор популярных символов      
[SYMJ](src%2Fmain%2Fjava%2Fmpc%2Fstring%2Fsym%2FSYMJ.java) - Набор популярных символов Emoji  
[SqlQueryBuilder](src%2Fmain%2Fjava%2Fmpe%2Fsql%2FSqlQueryBuilder.java) - Строим простые SQL-выражения

[UFS](./src/main/java/mpc/fs/UFS.java) - Общие методы для работы с файловой системой  
[RW](./src/main/java/mpc/fs/RW.java) - *Read/Write* - читаем, пишем файлы  
[UDIR](./src/main/java/mpc/fs/UDIR.java)  - Получение данных директории  
[UF](./src/main/java/mpc/fs/UF.java) + [UPath](./src/main/java/mpc/fs/UPath.java) + [UPathToken](./src/main/java/mpc/fs/UPathToken.java) - Нормализация пути + работа с файлами + Получение токенов пути

[EException](src%2Fmain%2Fjava%2Fmpc%2Fexception%2FEException.java)/[ERxception](src%2Fmain%2Fjava%2Fmpc%2Fexception%2FERxception.java) - именованные ошибки

[UMap](./src/main/java/mpc/map/UMap.java) - Работаем с Map  
[ObjTo](./src/main/java/mpc/str/ObjTo.java) - Кастим объекты в нужные типы  
[QDate](./src/main/java/mpc/time/QDate.java) -  Quick Date - Обертка над Date  
[RFL](./src/main/java/mpc/rfl/RFL.java) - Рефлексия  
[EN](./src/main/java/mpc/EN.java) - Конвертим, утилиты с _Enum_ и др.  
[EQ](./src/main/java/mpc/EQ.java) - Сравнение объектов (true/false)

[Env](src%2Fmain%2Fjava%2Fmpc%2Fenv%2FEnv.java) - Класс хранящий окружение, в котором запускается приложение  
[AP](src%2Fmain%2Fjava%2Fmpc%2Fenv%2FAP.java) - Утилиты для работы с application.properties, знает о профилях    
[Ns](src%2Fmain%2Fjava%2Fmpc%2Fns%2FNs.java) - Namespace - объект для работы с путями приложения    
[SeqOptions](src/main/java/mpc/types/opts/SeqOptions.java) - Парсим команду запуска приложения ( одинарные '-key val', двойные (boolean) '--arg' )

[QuestAnswer](src%2Fmain%2Fjava%2Fmpc%2Fconsole%2FQuestAnswer.java) - Взаимодействие с вводом пользователя( консолью )  
[QuestAnswerAsync](src%2Fmain%2Fjava%2Fmpc%2Fconsole%2FQuestAnswerAsync.java) - Асинхронное взаимодействие с вводом пользователя( консолью )      
[ConsoleInput](src%2Fmain%2Fjava%2Fmpc%2Fconsole%2FConsoleInput.java) - Простая реализация консоли (терминала)

[UPref](./src/main/java/mpc/UPref.java) - пишем&читаем Preferences

[AbsType](src%2Fmain%2Fjava%2Fmpc%2Ftypes%2Fabstype%2FAbsType.java) - Обертка над объектом  ( *._name(), *.val(), *.type()_ )    
[Pare3](src%2Fmain%2Fjava%2Fmpc%2Ftypes%2Fpare%2FPare3.java),[Pare4.java](src%2Fmain%2Fjava%2Fmpc%2Ftypes%2Fpare%2FPare4.java) - Расширяет класс Pare  
[RuProps](src%2Fmain%2Fjava%2Fmpc%2Ftypes%2Fruprops%2FRuProps.java),[URuProps](src%2Fmain%2Fjava%2Fmpc%2Ftypes%2Fruprops%2FURuProps.java) - Аналог Properties с кирилицей

~~[JSoon](src%2Fmain%2Fjava%2Fmpc%2Fjson%2FJSoon.java)~~  - не совсем удачная поптыка упростить работу с gson. Требует доработки.      
[UGson](src%2Fmain%2Fjava%2Fmpc%2Fjson%2FUGson.java) + [GsonMap](src%2Fmain%2Fjava%2Fmpc%2Fjson%2FGsonMap.java) - Обработка json

[MapTableContract](src%2Fmain%2Fjava%2Fmpc%2Fmap%2FMapTableContract.java) - Простое решение мапинга ключей таблицы БД (любой мапы) на методы интерфейса

[MultiTask](./src/main/java/mpf/multitask/MultiTask.java) - Расспаралеливаем выполнение задач

[WeightLine](src%2Fmain%2Fjava%2Fmpe%2Fweight%2FWeightLine.java) - Считаем вес по графику прямой линии    
[WeightParabola](src%2Fmain%2Fjava%2Fmpe%2Fweight%2FWeightParabola.java) - Считаем вес по графику пораболы


## ✅ Примеры


[X](./src/main/java/mpc/X.java) - Size & Empty & Format & etc

Возвращает размер объекта + проверка на пустоту + форматирование строк

```shell
X.f("Hello %s", "World")   // String.format
X.f_("Hello %s", "World")   // безопасный String.format
X.fm("Hello {0}", "World") //MessageFormat.format
X.fl("Hello {}", "World")  // Аналог log

//Возвращаем длину объекта  
X.sizeOf(null) // -1
X.sizeOf0(null) // 0
X.sizeOf(object) // size of Collection's, Map , etc

//Проверяем на пустоту  
X.empty(object) // true/false
X.emptyAll(o1, o2, ..) // true/false
X.emptyAny(o1, o2, ..) // true/false
X.notNullOnlyOne(o1, o2, ..) // true/false
//и др.

```

[ERR](./src/main/java/mpc/ERR.java) - Checks ( Аналог Preconditions )

```shell
ERR.notNull(null) // throw error if NULL
ERR.notNull(null, "error-message with %s %s",arg1,argN) //throw CheckException if object is null with 'error-message arg1 argN' or 'error-message;;;$arg1;;;$argN' 
ERR.isNull(..)
ERR.state(..)
ERR.isLength(..)
ERR.isFile(..)
ERR.isType(..)
//и др.
```


[Arr](./src/main/java/mpc/arr/Arr.java) - Массивы & Map's - Общие утилиты для массивов - sublist, merge, isIndex и др.

```shell
Arr.as(o1,oN) // Arrays.asList(o1,oN)
Arr.of(o1,oN) // new Object[]{o1,oN}
Arr.sublist(list, 2, 5, null) // return items with index 2,3,4,5 OR null if not found index's
//и др.
```

[ArrItem](./src/main/java/mpc/arr/ArrItem.java) - Массивы & Map's - First & Last
Получение элементов Array, Collection, Map + др.
```javascript
ARItem.first(list) // return first element or throw error
ARItem.first(list, null) // return first element OR null
ARItem.first(list, 3, null) // return element with index 3 OR null
ARItem.firstMany(list, 3, null) // return 3 element's OR null
//
ARItem.last(..) // тоже самое как и для first только с конца

ARItem.item(els, index, null); //return item or null

//и др.
```

[UMap](./src/main/java/mpc/map/UMap.java) - Получение значений из мап(-ы)  + кастомные типы [WhatIs](./src/main/java/mpc/map/WhatIs.java) ( NN, NE, NB
,REGEX, CONDITION)  

```shell
UMap.getBy*(map, "key"); // Получение элемента по ключу или throw error
UMap.getBy*(map, "key", null); // Получение элемента по ключу или return null
UMap.getBy*(map, { "key1","key2" }, WhatIs.NE); // Получение не пустого элемента по ключу или throw error
UMap.getBy*(map[], WhatIs.RX.of("\\d++").negate(), null); // Получение элемента НЕ соотв. regex или return null
// и др.
```

[MapTableContract](./src/main/java/mpc/map/MapTableContract.java) - Работа с Map via Proxy
- Мапим ключи таблицы БД (или мапы) на методы интерфейса   

```javascript
    interface IMapTestContract {
		String getK1();
		Long getK2();
		String getK3();
		String getK4(String... markNotRq);
	}
	private static void test() {
		String patternMap = "k1=v1;k2=8;";
		IMapTestContract c = MapTableContract.buildContract(UMap.mapOf(patternMap), IMapTestContract.class);
		String patternMapTotal = "k1=%s;k2=%s;";
		patternMapTotal = U.f(patternMapTotal, c.getK1(), c.getK2());
		try {
			String v3 = c.getK3();
			throw new IllegalStateException("error getting");
		} catch (MapTableContractException e) {
			P.w(e);
		}

		String v4 = c.getK4();//OK
		P.p(v4);//null

		String v4_2 = c.getK4("");//OK
		P.p(v4_2);//null
		//
		//
		UC.state(patternMap.equals(patternMapTotal), "not equals", patternMapTotal);
	}
	
	---------------------------- Example 2 - Map from json----------------------------
		public interface IssueContract {
		Long getId();
		String getStatus();

		String getDescription();

		List<Map> getComments();

		List<Map> getAttachments();

		static IssueContract of(JsonObject issueJsonObject) {
			return MapTableContract.buildContract(UGson.toMapFromJO(issueJsonObject), IssueContract.class);
		}
	}
```
[QDate](./src/main/java/mpc/time/QDate.java) - Quick Date

```shell
QDate.now().hour // год, месяц, день, час, секунды
QDate.now().diff() // разница времени
QDate.now().add() // добавление времени
QDate.now().f(F.formatType) // разные форматы
// и др.
```

[EQ](./src/main/java/mpc/EQ.java) - Сравнение объектов (true/false)

[ObjTo](./src/main/java/mpc/str/ObjTo.java) - Кастим типы друг в друга

## ✅ Java Reflection

[RFL](./src/main/java/mpc/rfl/RFL.java) - Различные утилиты для работы с классами, методами и полями  

```shell
// Ищем поле
RFL.field (object, "fildname", null) // find Field 
RFL.fieldSt (object|class, "fildname", null) // find static Field 

// Ищем поля по типам, анотациям, шаблонам имени, значениям
RFL.fields (..)  
RFL.fieldsSt (..)

// Ищем метод
RFL.method (object, "methodName") // Method  

// Значения поля/полей
RFL.fieldValue (object, "methodName") // Object  
RFL.fieldValues (object, "methodName") // List<Object>  

//
RFL.read (object, "filedName") // Read field value
RFL.readSt (class, "filedName") // Read static field value

//
RFL.write (object, "filedName") // Write value to field
RFL.writeSt (class, "filedName") // Write value to static field

//
RFL.invoke (object, "methodName") // Invoke method, return value
RFL.invokeSt (class, "methodName") // Invoke static method, return value

//Invoke static method from jar
RFL.invokeFromJarSt (jarFile, clazzFullName, methodName, parameterTypes, paramterObjects) 

//Если не находим подходяшего по сигнатуре метода - то добавлем (перегружаем) свой
```

[UReflScanner](src%2Fmain%2Fjava%2Fmpc%2Frefl%2FUReflScanner.java) - Сканируем пакеты приложения, ищем классы, etc
- Используется https://github.com/classgraph/classgraph - из множества перепробованных это единственная либа, которая без ошибок ищет классы во всех положениях запуcкаемого приложения, будь то SpringBoot, No-SpringBoot, запуск из IDE или локально

## ✅ Строки

 [UStr](./src/main/java/mpc/str/STR.java) - Общий класс для работы со строками ( startsWith, endsWith и etc )

[UST](./src/main/java/mpc/str/UST.java) конвертация строковых типов

```shell
UST.INT("777") // 777
UST.LNG("wrong777",null) // null
UST.LNG("wrong777") // throw error
UST.DBL("wrong777", 777.0) // return 777.0
//особый метод - кастит строки в разные типы
UST.strTo ("/path_to_file", Path.class) // --> Path
..и другие
```

[USToken](./src/main/java/mpc/str/USToken.java) получение частей(токенов) строк

```shell
// Во всех случаях если делиметр не найден - кидаем ошибку - либо возвращаем дефолтное значение ( если передали ..defRq )
// Основные методы умеют кастить до типов - если передать соотв. тип 
USToken.first("777-888-999", '-') // 777
USToken.firstGreedy("777-888-999", '-') // 777-888
USToken.last("777-888-999", '-') // 999
USToken.lastGreedy("777-888-999", '-') // 888-999
USToken.two("777-888-999", '-') // {"777","888-999"}
USToken.twoGreedy("777-888-999", '-') // {"777-888","999"}
USToken.next("777-888-999", '777') // '-888-999' 
USToken.nextEnd("777-888-999", '999') // '777-888-' 
```

[EN](./src/main/java/mpc/EN.java) - Enum - Конвертации строк в enum и др.

```shell
EN.valueOf("someType", SomeType.class) // SomeType
EN.valueOf("sOmeTyPe", SomeType.class, ignoreCase) // SomeType
EN.valueOf("WrongSomeType", SomeType.class, ignoreCase, null) // return null
//и др.
```

## ✅ Файловая система

[UFS](./src/main/java/mpc/fs/UFS.java) - Общий класс для работс с файловой системой

- Проверка файлов и директорий на empty & exist
- Основные операции для работы с фалами

```shell
UFS.MKDIR.create(..) // Создание директорий
UFS.MKFILE.create(..) // Создание файлов
UFS.MV.copy(..) // Копирование файлов/директорий
UFS.MV.move(..) // Перемещение файлов/директорий
UFS.RM.remove(..) // Удаление файлов/директорий
UFS.IS.remove(..) // Convert InputStream
```

[UDIR](./src/main/java/mpc/fs/UDIR.java) - Работа с директорией

#### Классы  [UF](./src/main/java/mpc/fs/UF.java) + [UPath](./src/main/java/mpc/fs/UPath.java) + [UPathToken](./src/main/java/mpc/fs/UPathToken.java)

Обработка имен фалов и директори

[RW](./src/main/java/mpc/fs/RW.java) - Read & Write files, types, serializable

```shell
//read
RW.readContent(..)
RW.readLines(..)
RW.readProps(..)
RW.Serializable2File.serializable(..)
RW.Serializable2String.serializable(..)
//write
RW.writeContent(..)
RW.writeLines(..)
RW.writeProps(..)
RW.Serializable2File.deserializable(..)
RW.Serializable2String.deserializable(..)
```

[MultiTask](./src/main/java/mpf/multitask/MultiTask.java) - Расспаралеливаем выполнение задач  
```shell
//Обработчик типа <Т> + коллекция типов <Т> + config(не обязательный, содержит параметры распаралеливания)
MultiTask.runMultiThread(executorName, Class<T> , Collection<T> entitys, Map config)
```

[ZipExecEE.java](src%2Fmain%2Fjava%2Fmpe%2Frt%2FZipExecEE.java) + [UnZipExecEE.java](src%2Fmain%2Fjava%2Fmpe%2Frt%2FUnZipExecEE.java) - архивируем, разархивируем средствами ОС Linux


[GrepExecRq](src%2Fmain%2Fjava%2Fmpe%2Frt%2FGrepExecRq.java) - Парсим содержиое директорий via Linux `grep` командой
```javascript
List<String> lines_out = GrepExecRq.execGrepStringInDir(needle, dir, AR.as("target", "src"), null, null);
```

[TeseractPhotoParser](./src/main/java/mpe/docker/TeseractParserPhotoViaDocker.java) 
```shell
//Парсим текст с фото
TeseractParserPhotoViaDocker.parsePhotoRus("/tmp/img-with-hello-world.png") //hello world
```

## ✅ Терминал приложения
[TRM](./src/main/java/mpt/TRM.java) - Terminal/Console - 
- Простое решение создания терминала приложения ( делает приложение как бы ОС )
- Позволяет создавать и вызывать console/rest команды приложения. Значительно повышая гибкость в настройке и эксплуатации приложения за счет управления контекстом приложения в рантайме
- Позволяет писать сложные авто- и интеграционные тесты, например, когда воспроизведение некоторых сценариев ручным способом либо долго, либо зачастую просто не выполнимо

```java
import mpc.ERR;
import mpt.*;

//Создание команды `any`
@TrmEntity(value = "any")
class AnyTrm {

	@TrmCmdEntity(value = "case1")
	ITrmCmd cmd = (usr, cmd) -> {
		return TrmRsp.OK("Hello world");
	};

	@TrmCmdEntity(value = "case2")
	ITrmCmd cmd = (usr, cmd) -> {
		throw new AnyException();
	};

	public static void main(String[] args) {
		//запуск системного NativeConsole + регистрация терминала AnyTrm + SysTrm
		//TRM.run_scan(true, true, "scan_packages");
		TRM.run(true, true, AnyTrm.class);

		TrmRsp rsp = TRM.executeCmd(TrmRq.fromTrm("any case1"));
		P.p(rsp.getMessage());//"Hello world"

		rsp = TRM.executeCmd(TrmRq.fromTrm("any case2"));
		ERR.state(rsp.isFail());
		rsp.throwIsNoOk(); //throw with Cause -> AnyException
	}
}

```
