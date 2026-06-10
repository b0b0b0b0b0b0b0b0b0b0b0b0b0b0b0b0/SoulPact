# SoulPact

## Описание плагина SoulPact

SoulPact — современный клановый плагин для **Paper 1.21+** и совместимых форков (Purpur и аналоги). Весь интерфейс, чат и меню построены на **Adventure** и **MiniMessage**: hex-цвета, кликабельные подсказки в чате, аккуратные GUI без устаревших `§`-костылей.

Ядро отвечает за создание клана, профиль, роли, права, баннер, заявки и приглашения. Остальное подключается **модулями** — ставите только то, что нужно серверу: казна, база, сундук, войны, коалиции, квесты, гладиаторские ивенты, Discord-мост. Каждый модуль регистрируется через API расширений и появляется в хаб-меню `/clan`.

Данные хранятся в **SQLite** или **MySQL** (HikariCP, асинхронные запросы, SQL-миграции). Экономика — через **Vault**. Статистика и TAB — через **PlaceholderAPI** (`%spact_...%`, более 60 плейсхолдеров). Головы в меню — с приоритетом **SkinsRestorer**, без лишних запросов к Mojang.

Внутри клана — система **уровней и очков**, K/D игроков и клана, учёт побед в войнах, настраиваемые **роли с правами** (казна, сундук, база, война, коалиция), дружественный огонь и открытый/закрытый набор. Есть **знамя клана** в мире, редактор баннера, список кланов, карточка клана и история участников.

Модули добавляют полноценный геймплей: **казна** с журналом операций, **база** на WorldGuard с расширением и цветной границей, **общий сундук** с несколькими страницами, **войны** с объявлениями, выкупом, BossBar и broadcast победы, **коалиции** до трёх кланов с общей казной и уведомлениями союзникам.

Языки — отдельные файлы `ru` / `en`, темы названий ролей (`military`, `anime` и др.). Конфиги и сообщения правятся в YAML без перекомпиляции. SoulPact задуман как лёгкая модульная основа: подключил нужные JAR — и кланы на сервере живут своей жизнью.

---

## Особенности плагина SoulPact

* Модульная система: **SoulPact** (ядро) + **Bank**, **Lands**, **Chest**, **War**, **Coalition**, **Quests**, **Gladiator**, **Discord**, **Leaderboard** — ставите только нужные аддоны.
* Paper Brigadier-команды `/clan` с подкомандами и подсказками в чате.
* GUI-хаб клана: профиль, настройки, баннер, список кланов, слоты модулей.
* Создание, редактирование описания, роспуск клана; выход, кик, приглашения и заявки на вступление.
* Роли и права: лидер настраивает, кто может кикать, принимать, нанимать, работать с казной, сундуком, базой, войной и коалицией.
* Темы названий ролей (`military`, `anime`…) — отдельные lang-файлы в `lang/roles/`.
* Редактор баннера клана и **знамя** (установка флага клана в мире).
* Уровни клана, очки, слоты участников, верификация, дружественный огонь, открытый набор.
* K/D участников и клана, статистика вступлений / выходов / киков, побед в войнах.
* **Почта клана** — `/clan mail`: письма участникам, уведомления онлайн-игрокам, страницы, непрочитанные, авто-очистка старых писем.
* **Дома клана** — `/clan home`: до N домов (конфиг), опциональный пароль на телепорт, список с координатами.
* **SoulPact-Bank** — казна клана, депозит/снятие по правам, журнал и топ вкладчиков (Vault).
* **SoulPact-Lands** — клановая база, регионы WorldGuard, расширение территории, цвет границы, флаги на базе.
* **SoulPact-Chest** — общий сундук с несколькими страницами, покупка ячеек, права deposit/withdraw.
* **SoulPact-War** — объявление войны, принятие, выкуп, BossBar в бою, учёт киллов, broadcast победы; проигравший клан может быть расформирован.
* **SoulPact-Coalition** — коалиция до 3 кланов, приглашения, BossBar, общая казна победителя, война на союзника.
* **SoulPact-Quests** — клановые квесты: ежедневные и разовые, миссии (убийства, добыча, постройка, рыбалка), награды очками, в казну и командами; прогресс в памяти с батч-записью в БД.
* **SoulPact-Gladiator** — гладиаторские PvP-ивенты: админ запускает арену (или расписание DAILY/WEEKLY), кланы вступают без лимита, последний выживший клан получает награды-команды и кастомный тег арены; BossBar, звуки, регион арены жезлом.
* **SoulPact-Discord** — мост в Discord через WebHook: красивые embed-сообщения о создании/роспуске кланов, вступлениях, киках, ролях, лидерстве, войнах, квестах и гладиаторских ивентах; цвета, очередь и rate-limit из коробки.
* **SoulPact-Leaderboard** — топы кланов в мире: таблички, стойки с головой лидера и бронёй по месту (алмаз/золото/железо), голограммы на нативных TextDisplay без сторонних плагинов; 7 статистик, авто-обновление по таймеру и событиям.
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

`/clan mail send <текст>` — Отправить письмо в почту клана (видят все участники).

`/clan mail read [страница]` — Читать почту клана (письма помечаются прочитанными).

`/clan mail clear` — Очистить почту клана (лидер).

`/clan home create <имя> [пароль]` — Создать дом клана на текущей позиции (лидер). Пароль опционален.

`/clan home tp <имя> [пароль]` — Телепортироваться к дому клана (`teleport` тоже работает).

`/clan home delete <имя>` — Удалить дом клана (лидер).

`/clan home list` — Список домов клана (`/clan home` без аргументов — то же самое).

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

### SoulPact-Leaderboard

`/clanboard help` — Справка по командам топов.

`/clanboard create <тип> <место> [sign|stand|hologram]` — Создать борд: смотрите на табличку (sign) или блок (stand/hologram).

`/clanboard delete <id>` — Удалить борд.

`/clanboard list` — Список бордов с координатами.

`/clanboard update` — Обновить все борды немедленно.

`/clanboard tp <id>` — Телепорт к борду.

`/clanboard reload` — Перезагрузить настройки и lang модуля.

`/clb`, `/clanlb` — Алиасы `/clanboard`.

---

## Discord-мост (модуль SoulPact-Discord)

События кланов улетают в Discord-канал красивыми embed-сообщениями. Команды не нужны — модуль работает сам.

1. В Discord: настройки канала → Интеграции → Вебхуки → создать вебхук → «Копировать URL».

2. Вставить URL в `plugins/SoulPact-Discord/config.yml`:

```yaml
webhook-url: "https://discord.com/api/webhooks/..."
bot-name: "SoulPact"
server-name: "Мой сервер"
```

3. `/sclan reload` (или перезапуск) — и события польются в канал.

Отправляемые события (каждое можно выключить в секции `events`, цвет — в `colors`):

| Событие | Когда |
|---|---|
| `server-start` / `server-stop` | Запуск и остановка сервера |
| `clan-create` / `clan-delete` | Создание и роспуск клана |
| `member-join` / `member-leave` / `member-kick` | Вступление, выход, кик участника |
| `role-change` / `leader-change` | Смена роли, передача лидерства |
| `desc-change` / `tag-change` | Смена описания и тега клана |
| `war-start` / `war-win` / `war-end` | Начало войны, победа, ничья |
| `quest-complete` | Клан выполнил квест |
| `glad-start` / `glad-win` | Старт гладиаторского ивента и чемпион арены |

Тексты embed (заголовки, строки описания, эмодзи) — в `plugins/SoulPact-Discord/lang/ru.yml` / `en.yml`. Очередь отправки с интервалом `send-interval-millis` (по умолчанию 1500 мс) защищает от rate-limit Discord; при HTTP 429 модуль сам ждёт `Retry-After` и повторяет отправку.

---

## Топы кланов (модуль SoulPact-Leaderboard)

Живые таблицы лидеров прямо в мире — без HolographicDisplays и прочих зависимостей.

Статистики: `POINTS` (очки), `MEMBERS` (участники), `KILLS`, `DEATHS`, `KDR`, `WARS` (победы в войнах), `BANK` (казна — нужен SoulPact-Bank).

Три вида бордов:

| Вид | Что это | Как создать |
|---|---|---|
| `sign` | Табличка с 4 строками | Смотрите на установленную табличку → `/clanboard create KDR 1 sign` |
| `stand` | Стойка для брони: голова лидера клана, броня по месту (1 — алмаз, 2 — золото, 3 — железо) | Смотрите на блок → `/clanboard create POINTS 1 stand` |
| `hologram` | Голограмма на нативном TextDisplay (1.21), градиенты MiniMessage | Смотрите на блок → `/clanboard create WARS 1 hologram` |

Пример пьедестала топ-3 по очкам:

```
/clanboard create POINTS 1 stand
/clanboard create POINTS 2 stand
/clanboard create POINTS 3 stand
```

Борды обновляются сами: по таймеру (`update-interval-seconds`, по умолчанию 300с) и мгновенно по событиям кланов (создание, роспуск, вступления, войны, квесты) с защитой от спама (`event-debounce-seconds`). Формат строк табличек, имён стоек и голограмм — в `plugins/SoulPact-Leaderboard/lang/ru.yml`; броня по местам — в `config.yml` → `stand-equipment`.

---

## Права плагина

Права Bukkit. Имена по умолчанию; можно переименовать в `plugins/SoulPact/config.yml` → `permissions`.

`soulpact.clan.use` — Доступ к командам `/clan` (по умолчанию: все игроки).

`soulpact.admin` — Доступ к `/sclan` и админ-функциям (по умолчанию: OP).

`soulpact.gladiator.admin` — Доступ к `/clanglad` и жезлу арен (по умолчанию: OP; нода настраивается в `plugins/SoulPact-Gladiator/config.yml`).

`soulpact.leaderboard.admin` — Доступ к `/clanboard` (по умолчанию: OP; нода настраивается в `plugins/SoulPact-Leaderboard/config.yml`).

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

### Топы (модуль SoulPact-Leaderboard)

`%spact_lb_<тип>_<место>%` — Тег клана на месте в топе (например `%spact_lb_points_1%`).

`%spact_lb_<тип>_<место>_tag%` — Тег клана (то же самое явно).

`%spact_lb_<тип>_<место>_name%` — Название клана.

`%spact_lb_<тип>_<место>_amount%` — Значение статистики (очки, K/D и т.д.).

Типы: `points`, `members`, `kills`, `deaths`, `kdr`, `wars`, `bank`. Кэш обновляется вместе с бордами.

### Бан-лист, почта, дома и настройки

`%spact_count_banned%` — Количество игроков в бан-листе клана (заблокированные заявки).

`%spact_banned%` — Список забаненных через разделитель.

`%spact_mail_amount%` — Количество писем в почте клана.

`%spact_mail_amount_unread%` — Непрочитанные письма игрока.

`%spact_mail:last%` — Текст последнего письма.

`%spact_mail:last_sender%` — Отправитель последнего письма.

`%spact_home:count%` — Количество домов клана.

`%spact_home:list%` — Имена домов через разделитель.

`%spact_home:<имя>%` — Существует ли дом с таким именем (`true`/`false`).

`%spact_setting:<ключ>%` — Значение настройки клана (`true`/`false`). Ключи: `ff`, `open`, `verified`.

`%spact_setting_state:<ключ>%` — То же, но в формате `да`/`нет` из конфига.

Шаблоны `_formated` настраиваются в `plugins/SoulPact/config.yml`, секция `placeholders`.
