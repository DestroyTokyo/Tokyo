
---

```txt
Что по поводу код-стайла - Требования есть, хоть их и не особо много.
```

---

```java
package some.ex;

// It's very.. VERY bad example but u can read that
public final class CoolClass {

	// Write it on first lines of class
	private CoolClass() {}

	// If u need param only for init and it newer used:
	// Write "_" before name of this variable
	private static String _notUsedByAllTime = "bebebe this text has replaced and never used";

	// Write setters before getters only.
	public set_notUsedByAllTime() {
		_notUsedByAllTime = SomeClass.get();
	}

	// Write getters after getters only.
	public String get_notUsedByAllTime() {
		_notUsedByAllTime = SomeClass.get();
		if (_notUsedByAllTime.isEmpty()) return "No data";
		return _notUsedByAllTime;
	}

	// Another code.

	// ... cool code..

	// Yes u need one line after all ur code and before last "}"

}
```

---

<p align="center">
    <a href="#">
        <img src="https://img.shields.io/github/last-commit/DestroyTokyo/Tokyo?display_timestamp=committer&style=flat-square&color=000000"></a>
    <a href="#">
        <img src="https://img.shields.io/github/created-at/DestroyTokyo/Tokyo?style=flat-square&color=000000"></a>
</p>
