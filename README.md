# SoulPact

## Описание плагина SoulPact

SoulPact — современный клановый плагин для **Paper 1.21+** и совместимых форков (Purpur и аналоги). Весь интерфейс, чат и меню построены на **Adventure** и **MiniMessage**: hex-цвета, кликабельные подсказки в чате, аккуратные GUI без устаревших `§`-костылей.

Ядро отвечает за создание клана, профиль, роли, права, баннер, заявки и приглашения. Остальное подключается **модулями** — ставите только то, что нужно серверу: казна, база, сундук, войны, коалиции. Каждый модуль регистрируется через API расширений и появляется в хаб-меню `/clan`.

Данные хранятся в **SQLite** или **MySQL** (HikariCP, асинхронные запросы, SQL-миграции). Экономика — через **Vault**. Статистика и TAB — через **PlaceholderAPI** (`%spact_...%`, более 60 плейсхолдеров). Головы в меню — с приоритетом **SkinsRestorer**, без лишних запросов к Mojang.

Внутри клана — система **уровней и очков**, K/D игроков и клана, учёт побед в войнах, настраиваемые **роли с правами** (казна, сундук, база, война, коалиция), дружественный огонь и открытый/закрытый набор. Есть **знамя клана** в мире, редактор баннера, список кланов, карточка клана и история участников.

Модули добавляют полноценный геймплей: **казна** с журналом операций, **база** на WorldGuard с расширением и цветной границей, **общий сундук** с несколькими страницами, **войны** с объявлениями, выкупом, BossBar и broadcast победы, **коалиции** до трёх кланов с общей казной и уведомлениями союзникам.

Языки — отдельные файлы `ru` / `en`, темы названий ролей (`military`, `anime` и др.). Конфиги и сообщения правятся в YAML без перекомпиляции. SoulPact задуман как лёгкая модульная основа: подключил нужные JAR — и кланы на сервере живут своей жизнью.

---

## Особенности плагина SoulPact

* Модульная система: **SoulPact** (ядро) + **Bank**, **Lands**, **Chest**, **War**, **Coalition**, **Quests**, **Gladiator** — ставите только нужные аддоны.
* Paper Brigadier-команды `/clan` с подкомандами и подсказками в чате.
* GUI-хаб клана: профиль, настройки, баннер, список кланов, слоты модулей.
* Создание, редактирование описания, роспуск клана; выход, кик, приглашения и заявки на вступление.
* Роли и права: лидер настраивает, кто может кикать, принимать, нанимать, работать с казной, сундуком, базой, войной и коалицией.
* Темы названий ролей (`military`, `anime`…) — отдельные lang-файлы в `lang/roles/`.
* Редактор баннера клана и **знамя** (установка флага клана в мире).
* Уровни клана, очки, слоты участников, верификация, дружественный огонь, открытый набор.
* K/D участников и клана, статистика вступлений / выходов / киков, побед в войнах.
* **SoulPact-Bank** — казна клана, депозит/снятие по правам, журнал и топ вкладчиков (Vault).
* **SoulPact-Lands** — клановая база, регионы WorldGuard, расширение территории, цвет границы, флаги на базе.
* **SoulPact-Chest** — общий сундук с несколькими страницами, покупка ячеек, права deposit/withdraw.
* **SoulPact-War** — объявление войны, принятие, выкуп, BossBar в бою, учёт киллов, broadcast победы; проигравший клан может быть расформирован.
* **SoulPact-Coalition** — коалиция до 3 кланов, приглашения, BossBar, общая казна победителя, война на союзника.
* **SoulPact-Quests** — клановые квесты: ежедневные и разовые, миссии (убийства, добыча, постройка, рыбалка), награды очками, в казну и командами; прогресс в памяти с батч-записью в БД.
* **SoulPact-Gladiator** — гладиаторские PvP-ивенты: админ запускает арену (или расписание DAILY/WEEKLY), кланы вступают без лимита, последний выживший клан получает награды-команды и кастомный тег арены; BossBar, звуки, регион арены жезлом.
* PlaceholderAPI: идентификатор `spact`, 60+ плейсхолдеров для scoreboard, TAB, чата и меню.
* Интеграции: Vault, PlaceholderAPI, SkinsRestorer, WorldGuard, Essentials (soft).
* SQLite и MySQL, HikariCP, асинхронная работа с БД, версионные SQL-миграции.
* Локализация ru / en, fallback-язык, все тексты игрокам — в YAML (`lang/`).
* Конфиги модулей и ядра в YAML; MiniMessage в сообщениях и lore GUI.
* API расширений (`SoulPactApi`, `ExtensionRegistry`, placeholder bridges) для своих модулей.

---

## Команды

### SoulPact (core)

`/clan` — Открыть главное меню клана.

`/clan profile` — Открыть профиль своего клана.

`/clan settings` — Открыть настройки клана (роли и права).

`/clan help` — Список команд в чате.

`/clan list` — Открыть список кланов сервера.

`/clan info` — Показать информацию о своём клане.

`/clan info <тег>` — Показать информацию о клане по тегу.

`/clan create` — Подсказка по созданию клана.

`/clan create <тег> <название>` — Создать клан.

`/clan description` — Подсказка по смене описания.

`/clan description <текст>` — Изменить описание клана.

`/clan leave` — Выйти из клана.

`/clan disband` — Расформировать клан (лидер).

`/clan join <клан>` — Подать заявку на вступление.

`/clan member invite <игрок>` — Пригласить игрока в клан.

`/clan invite accept <id>` — Принять приглашение в клан.

`/clan invite deny <id>` — Отклонить приглашение в клан.

`/clan request accept <id>` — Принять заявку на вступление (модерация).

`/clan request deny <id>` — Отклонить заявку на вступление.

`/clan request block <id>` — Заблокировать заявку на вступление.

`/sclan` — Админ-команды SoulPact (зарегистрирована в plugin.yml, обработчик в разработке).

### SoulPact-Bank

`/clanbank` — Открыть меню казны клана.

`/cbank` — Алиас `/clanbank`.

`/bank` — Алиас `/clanbank`.

### SoulPact-Lands

`/clanland` — Открыть меню базы клана.

`/cland` — Алиас `/clanland`.

`/land` — Алиас `/clanland`.

### SoulPact-Chest

`/clanchest` — Открыть общий сундук клана.

`/cchest` — Алиас `/clanchest`.

`/chest` — Алиас `/clanchest`.

### SoulPact-War

`/clanwar` — Открыть список объявлений войны.

`/clanwar accept <id>` — Принять объявление войны.

`/clanwar ransom <id>` — Выплатить выкуп по объявлению войны.

`/cwar` — Алиас `/clanwar`.

`/war` — Алиас `/clanwar`.

### SoulPact-Coalition

`/clancoalition` — Открыть меню коалиции.

`/clancoalition invite <тег>` — Пригласить клан в коалицию.

`/clancoalition accept <id>` — Принять приглашение в коалицию.

`/clancoalition deny <id>` — Отклонить приглашение в коалицию.

`/clancoalition block <id>` — Заблокировать приглашение в коалицию.

`/clancoalition leave` — Выйти из коалиции.

`/coalition` — Алиас `/clancoalition`.

`/coa` — Алиас `/clancoalition`.

### SoulPact-Quests

`/clanquest` — Открыть меню квестов клана.

`/clanquest list` — Открыть список квестов (GUI).

`/clanquest status` — Статус активного квеста в чате.

`/clanquest start <id>` — Начать квест (лидер).

`/clanquest abandon` — Отказаться от активного квеста (лидер).

`/cquest` — Алиас `/clanquest`.

`/quests` — Алиас `/clanquest`.

### SoulPact-Gladiator

`/gladiator` — Открыть меню гладиаторских арен (ЛКМ — вступить, ПКМ — наблюдать).

`/gladiator help` — Справка по командам ивента.

`/gladiator join <арена>` — Вступить в ивент (нужен клан, фаза лобби).

`/gladiator leave` — Покинуть ивент.

`/gladiator watch <арена>` — Наблюдать за ивентом.

`/glad` — Алиас `/gladiator`.

`/clanglad help` — Справка администратора.

`/clanglad reload` — Перезагрузить настройки и lang модуля.

`/clanglad wand` — Жезл выделения региона арены (ЛКМ/ПКМ — точки).

`/clanglad arena create <имя>` — Создать арену (применяет выделенный регион).

`/clanglad arena delete <имя>` — Удалить арену.

`/clanglad arena list` — Список арен.

`/clanglad arena point <арена> <SPAWN|WATCH|EXIT|LOBBY>` — Установить точку арены в текущей позиции.

`/clanglad arena toggle <имя>` — Включить/выключить арену.

`/clanglad arena seticon <арена> <материал>` — Иконка арены в GUI.

`/clanglad arena settag <арена> <тег>` — Кастомный тег арены (достаётся победителю).

`/clanglad arena desc <арена> <описание>` — Описание арены.

`/clanglad reward list <арена>` — Список наград арены.

`/clanglad reward clean <арена>` — Очистить награды.

`/clanglad reward add <арена> <команда>` — Добавить награду-команду (`{player}`, `{tag}`, `{arena}`).

`/clanglad start <арена>` — Запустить ивент.

`/clanglad stop <арена>` — Остановить ивент.

`/clanglad view <арена>` — Телепорт к арене (точка WATCH).

`/clanglad scheduler list <арена>` — Список расписаний арены.

`/clanglad scheduler remove <арена> <id>` — Удалить расписание.

`/clanglad scheduler create <арена> <DAILY|WEEKLY> <день 1-7> <час:минута>` — Авто-запуск ивента по расписанию.

`/cglad` — Алиас `/clanglad`.

#### Быстрый старт арены

1. Создать арену и поставить обязательные точки (встаньте в нужное место и выполните команду):

```
/clanglad arena create colosseum
/clanglad arena point colosseum LOBBY
/clanglad arena point colosseum SPAWN
/clanglad arena point colosseum EXIT
```

Без точек `SPAWN`, `LOBBY` и `EXIT` ивент не запустится.

2. Опционально — трибуна, тег победителя, описание, иконка и регион:

```
/clanglad arena point colosseum WATCH
/clanglad arena settag colosseum <gold>⚔Чемпион
/clanglad arena desc colosseum Большая арена на спавне
/clanglad arena seticon colosseum DIAMOND_SWORD
```

Регион: `/clanglad wand` → ЛКМ по блоку — первый угол, ПКМ — второй, затем `/clanglad arena create <имя>` (регион берётся из выделения при создании). Бойца, вышедшего за границы, возвращает на `SPAWN`.

3. Награды победителям (выполняются консолью на каждого участника клана-победителя):

```
/clanglad reward add colosseum give {player} diamond 16
/clanglad reward add colosseum eco give {player} 1000
```

4. Запуск и проверка (нужно минимум 2 клана):

```
/clanglad start colosseum
```

Игроки вступают через `/gladiator join colosseum` или меню `/gladiator` во время отсчёта лобби (60с по умолчанию, ключ `lobby-countdown-seconds` в `plugins/SoulPact-Gladiator/config.yml`). После отсчёта всех телепортирует на `SPAWN`; смерть — выбывание; последний выживший клан получает награды и тег арены. Если кланов меньше двух — ивент отменяется.

5. Авто-запуск по расписанию:

```
/clanglad scheduler create colosseum DAILY 1 20:00
/clanglad scheduler create colosseum WEEKLY 6 18:30
```

`DAILY` — каждый день в указанное время (день игнорируется), `WEEKLY` — по дню недели (1 = понедельник … 7 = воскресенье).

---

## Права плагина

Права Bukkit. Имена по умолчанию; можно переименовать в `plugins/SoulPact/config.yml` → `permissions`.

`soulpact.clan.use` — Доступ к командам `/clan` (по умолчанию: все игроки).

`soulpact.admin` — Доступ к `/sclan` и админ-функциям (по умолчанию: OP).

`soulpact.gladiator.admin` — Доступ к `/clanglad` и жезлу арен (по умолчанию: OP; нода настраивается в `plugins/SoulPact-Gladiator/config.yml`).

---

## Права ролей в клане

Настраиваются лидером в `/clan settings`. Это не Bukkit-permission, а права роли внутри клана.

`kick` — Выгонять участников.

`accept` — Принимать заявки и приглашения.

`recruit_lower` — Нанимать участников ниже по рангу.

`bank_deposit` — Вносить в казну.

`bank_withdraw` — Снимать из казны.

`chest_deposit` — Класть в общий сундук.

`chest_withdraw` — Брать из общего сундука.

`land_manage` — Управлять базой клана.

`war_declare` — Объявлять войну.

`war_respond` — Отвечать на объявления войны.

`war_fight` — Участвовать в войне (ломать блоки базы противника).

`coalition_manage` — Управлять коалицией.

---

## PlaceholderAPI

Идентификатор: `spact`. Формат: `%spact_<параметр>%`.

Нужны SoulPact + PlaceholderAPI. Для `%spact_money_*%` — Vault. Проверка: `` `/papi parse me %spact_hasclan%` ``.

Плейсы с `_formated` возвращают MiniMessage; expansion сам переводит в legacy (`&`), если плагин не поддерживает Adventure.

### Членство и настройки клана

`%spact_hasclan%` — Проверка, состоит ли игрок в клане (`true` / `false`).

`%spact_hasclan_formated%` — Отформатированная информация о членстве игрока в клане.

`%spact_hasclan_formated_levelsup%` — Членство в клане с символом повышения (▲).

`%spact_hasclan_formated_levelsub%` — Членство в клане с символом понижения (▼).

`%spact_global_ff%` — Статус глобального дружественного огня в клане (`true` / `false`).

`%spact_global_ff_formated%` — Отформатированный статус глобального дружественного огня.

`%spact_is_ff%` — То же, что `%spact_global_ff%`.

`%spact_is_ff_formated%` — То же, что `%spact_global_ff_formated%`.

`%spact_isopen%` — Проверка, открыт ли клан для вступления (`true` / `false`).

`%spact_isopen_formated%` — Отформатированный статус открытости клана.

`%spact_verified%` — Проверка, верифицирован ли клан (`true` / `false`).

`%spact_clanid%` — ID клана в базе данных.

`%spact_creation_date%` — Дата создания клана (`dd.MM.yyyy`).

`%spact_creation_date_formated%` — Дата создания клана с форматированием из конфига.

`%spact_desc_color%` — Описание клана с цветами.

`%spact_desc_nocolor%` — Описание клана без цветового форматирования.

`%spact_banner%` — Данные баннера клана.

### Тег клана

`%spact_tag_nocolor%` — Тег клана без цветового форматирования.

`%spact_tag_color%` — Цветной тег клана.

`%spact_tag_color_levelsup%` — Цветной тег с символом повышения.

`%spact_tag_color_levelsub%` — Цветной тег с символом понижения.

`%spact_tag_formated%` — Отформатированный тег клана.

`%spact_tag_formated_nocolor%` — Отформатированный тег клана без цветов.

`%spact_tag_formated_levelsup%` — Отформатированный тег с символом повышения.

`%spact_tag_formated_levelsub%` — Отформатированный тег с символом понижения.

`%spact_only_verified_tag%` — Тег клана, только если клан верифицирован.

`%spact_only_verified_tag_formated%` — Отформатированный тег верифицированного клана.

### Уровень и очки

`%spact_level%` — Уровень клана.

`%spact_max_level_reached%` — Достигнут ли максимальный уровень клана (`true` / `false`).

`%spact_points%` — Очки клана (с форматированием).

`%spact_points_unformated%` — Очки клана (число).

`%spact_points_to_nextlevel%` — Очков до следующего уровня.

`%spact_levelsup%` — Символ повышения из конфига (по умолчанию ▲).

`%spact_levelsub%` — Символ понижения из конфига (по умолчанию ▼).

### Участники и роли

`%spact_count_members%` — Количество участников клана.

`%spact_count_onlinemembers%` — Количество участников клана онлайн.

`%spact_clan_members%` — Список участников клана через разделитель.

`%spact_clan_onlinemembers%` — Список участников клана, которые онлайн.

`%spact_leader%` — Ник лидера клана.

`%spact_leader_formated%` — Отформатированный ник лидера клана.

`%spact_leader_head%` — Base64 текстуры головы лидера (если лидер онлайн).

`%spact_patent_name%` — Название роли игрока в клане.

`%spact_patent_formated%` — Отформатированная роль игрока в клане.

`%spact_clan_member_joined%` — Сколько раз участники вступали в клан (статистика).

`%spact_clan_member_leave%` — Сколько раз участники выходили из клана сами (статистика).

`%spact_clan_member_kicked%` — Сколько раз участников кикали (статистика).

### Слоты

`%spact_slots%` — Базовое количество слотов клана.

`%spact_slots_extra%` — Дополнительные слоты из конфига.

`%spact_slots_total%` — Общее количество слотов (`slots + extra`).

### Казна

`%spact_bank_balance%` — Баланс казны клана (число).

`%spact_bank_balance_formated%` — Отформатированный баланс казны клана.

### PvP и войны

`%spact_clan_kills%` — Убийства клана.

`%spact_clan_deaths%` — Смерти клана.

`%spact_clan_kdr%` — K/D ratio клана.

`%spact_clan_souls%` — Души клана (заглушка, всегда `0`).

`%spact_clan_war_win%` — Количество побед клана в войнах.

`%spact_clan_war_lose%` — Количество поражений клана в войнах.

### Союзники

`%spact_clan_ally%` — Список тегов союзников через разделитель.

`%spact_clan_count_ally%` — Количество союзников.

`%spact_clan_rival%` — Соперники клана (заглушка, значение из конфига).

`%spact_clan_count_rival%` — Количество соперников (заглушка, `0`).

### Игрок

`%spact_money_current%` — Баланс игрока через Vault (число).

`%spact_money_current_formated%` — Отформатированный баланс игрока.

`%spact_money_currency%` — Название валюты (множественное число).

`%spact_current_lang%` — Локаль по умолчанию из конфига SoulPact.

`%spact_current_lang_head%` — Base64 строки локали (технический плейс).

`%spact_player_head%` — Base64 текстуры головы игрока.

### Квесты (модуль SoulPact-Quests)

`%spact_quest_has_active%` — Есть ли у клана активный квест (`да`/`нет`).

`%spact_quest_active%` — ID активного квеста (пусто, если нет).

`%spact_quest_active_name%` — Название активного квеста из lang.

`%spact_quest_progress%` — Текущий прогресс активного квеста.

`%spact_quest_target%` — Цель активного квеста.

`%spact_quest_percent%` — Прогресс в процентах (0–100).

`%spact_quest_time_left%` — Оставшееся время ежедневного квеста.

### Гладиатор (модуль SoulPact-Gladiator)

`%spact_glad_inwar%` — Участвует ли игрок в бою прямо сейчас (`да`/`нет`).

`%spact_glad_hastag%` — Держит ли клан игрока хотя бы один тег арены (`да`/`нет`).

`%spact_glad_tags%` — Все теги арен, которые держит клан игрока (через пробел).

`%spact_glad_tag:<арена>%` — Кастомный тег арены.

`%spact_glad_holder:<арена>%` — Тег клана, владеющего тегом арены.

`%spact_glad_arena_state:<арена>%` — Состояние арены (Ожидание/Лобби/Идёт бой/Выключена).

`%spact_glad_next_name%` — Арена ближайшего ивента по расписанию.

`%spact_glad_next_time%` — Время до ближайшего ивента по расписанию.

### Заглушки (ещё не реализовано)

`%spact_count_banned%` — Количество забаненных в клане (заглушка, `0`).

`%spact_banned%` — Список забаненных (заглушка, пусто).

`%spact_mail_amount%` — Количество писем (заглушка).

`%spact_mail_amount_unread%` — Непрочитанные письма (заглушка).

`%spact_mail:<...>%` — Письма по параметру (заглушка).

`%spact_home:<...>%` — Дома клана (заглушка).

`%spact_setting:<...>%` — Настройки (заглушка, `false`).

`%spact_setting_state:<...>%` — Состояние настройки (заглушка).

Шаблоны `_formated` настраиваются в `plugins/SoulPact/config.yml`, секция `placeholders`.
