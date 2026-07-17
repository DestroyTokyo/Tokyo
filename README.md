<p  align="center" >
	<img src=".github/Cool_Monkey.png">
</p>

<!--  width="160" height="120" -->

---

```txt
Простой майнкрафт сервер, над которым я работаю.
Базируется на Minestom, версии 1.21.4
	(Для версии новее нужна более новая IDE
		(Нууу вообще нет, но IDE моей версии не поддерживает 9 Gradle нативно))

На данный момент тут есть:
1) Косая система плагинов.
	Нужного мне функционала полностью реализовано не было, но частично оно работает
2) Удобный регистратор ивентов
3) Удобный регистратор команд
4) Вайтлист
5) Небольшая система пермишенов
	Потом я её дополню, но на данный момент весь нужный мне функционал она поддерживает

В корне репозитория есть msnt.exitcodes.txt с базовыми кодами ошибок,
Приводящих к крашу сервера. Задокументировано не всё.
```

---

```kts
// How to add api to ur project

repositories {
	mavenCentral()
	maven("https://destroytokyo.github.io/")
}

dependencies {
	// example: delta.cion:tokyo:2.2.0-predemo
	compileOnly("delta.cion:tokyo:{version}")
}
```

---

```txt
Вдохновлялся ими:
stomium (Заброшен разрабом уже год)
	https://github.com/cawtoz/stomium
Ruby (Репо приватный, заброшен разрабом (Мной) уже почти год)
	https://github.com/Project-Violette/Ruby
Emerald (Репо приватный, заброшено мной уже больше года)
	(По сути первая попытка в сервера на Minestom)
	https://github.com/Project-Violette/Emerald
```


---

<H3 align="center"> --==[ PC stats ]==-- </H3>

```json
CPU: Ryzen 3 1200 3.50GHz
GPU: RX 580 2048SP (8 GB)
RAM: 2x16 (32)GB 3200Mhz
ROM: 2TB (2x500GB, 750GB, 250GB)
```

---

<p align="center">
    <a href="#">
        <img src="https://img.shields.io/github/last-commit/DestroyTokyo/Tokyo?display_timestamp=committer&style=flat-square&color=000000"></a>
    <a href="#">
        <img src="https://img.shields.io/github/created-at/DestroyTokyo/Tokyo?style=flat-square&color=000000"></a>
</p>
