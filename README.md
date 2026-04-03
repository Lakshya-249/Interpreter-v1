# 🚀 Lox++ Interpreter (Custom Language Runtime)

A fully-featured **custom scripting language interpreter** built in Java using **recursive descent parsing**, inspired by Lox but extended with:

* ✅ First-class & anonymous functions
* ✅ Closures
* ✅ Classes, inheritance & `super`
* ✅ Static methods
* ✅ Arrays with indexing & mutation
* ✅ Native functions (`len`, `input`, `clock`)
* ✅ For/While loops (desugared into AST)
* ✅ Dynamic typing

---

## 🧠 Architecture Overview

This interpreter follows a **classic compiler pipeline**:

```
Source Code → Lexer → Parser → AST → Interpreter → Output
```

---

## 📂 Project Structure

```
.
├── LoxScripts/              # Sample scripts
│   └── test1.lox
├── scripts/                 # Utility scripts
│   ├── ast.sh
│   └── run.sh
├── src/main/java/com/lakshya/
│   ├── interpreter/
│   │   ├── App.java                # Entry point
│   │   ├── ast/                   # AST definitions
│   │   │   ├── Expr.java
│   │   │   └── Stmt.java
│   │   ├── lexer/                 # Tokenization
│   │   │   ├── Scanner.java
│   │   │   ├── Token.java
│   │   │   └── TokenType.java
│   │   ├── parser/                # Parsing + Resolver
│   │   │   ├── Parser.java
│   │   │   └── Resolver.java
│   │   ├── runtime/               # Execution engine
│   │   │   ├── Interpreter.java
│   │   │   ├── Environment.java
│   │   │   ├── RuntimeError.java
│   │   │   └── Return.java
│   │   ├── callable/              # Functions & Classes
│   │   │   ├── Function.java
│   │   │   ├── Class.java
│   │   │   ├── Instance.java
│   │   │   └── ArrayClass.java
│   │   └── lib/                   # Native functions
│   │       ├── ClockNativeFunction.java
│   │       ├── InputNativeFunction.java
│   │       ├── LenNativeFunction.java
│   │       └── NumberInputNativeFunction.java
│   └── tool/
│       └── GenerateAst.java
├── pom.xml
└── target/
```

---

## 🔥 Language Features (Examples)

### Higher-Order Functions

```lox
fun thrice(fn) {
  for (var i = 1; i <= 3; i = i + 1) {
    fn(i);
  }
}
```

---

### Closures

```lox
fun multiplier(x) {
  return fun (y) {
    return x * y;
  };
}
```

---

### Classes & Inheritance

```lox
class B extends A {
    testB(){
        super.test();
    }
}
```

---

### Arrays

```lox
var lst = [1, 2, 3];
lst[0] = 42;
lst.push_back(7);
print len(lst);
```

---

## ⚙️ Setup & Installation

### ✅ Prerequisites

* Java 17+
* Maven 3+

```bash
java -version
mvn -version
```

---

### 📦 Build Project

```bash
mvn clean install
```

---

### ▶️ Run Interpreter

#### Option 1: Using Maven

```bash
mvn exec:java -Dexec.mainClass="com.lakshya.interpreter.App" -Dexec.args="LoxScripts/test1.lox"
```

---

#### Option 2: Using JAR

```bash
java -jar target/interpreter-1.0-SNAPSHOT.jar LoxScripts/test1.lox
```

---

#### Option 3: Using Script

```bash
./scripts/run.sh LoxScripts/test1.lox
```

---

## 🧩 Key Components Explained

### 🟡 Lexer (`Scanner.java`)

* Converts raw source code → tokens
* Handles literals, identifiers, keywords

---

### 🔵 Parser (`Parser.java`)

* Recursive descent parser
* Builds AST from tokens
* Supports:

  * Expressions
  * Function expressions (`fun`)
  * Arrays (`[]`)
  * Property access (`obj.field`)
  * Indexing (`arr[i]`)

👉 Includes:

* Error recovery (`synchronize()`)
* Desugaring of `for` → `while`

---

### 🟣 Resolver (`Resolver.java`)

* Static scope analysis
* Resolves:

  * Variables
  * Closures
  * `this` and `super`

---

### 🔴 Interpreter (`Interpreter.java`)

* Walks AST using Visitor pattern
* Executes:

  * Expressions
  * Statements
  * Function calls
  * Class instantiation

---

### 🟢 Runtime (`Environment.java`)

* Variable scope chain
* Supports closures via nested environments

---

### 🟠 Callable System

Implements polymorphic call behavior:

* `Function` → user-defined functions
* `Class` → constructors
* `Instance` → object instances
* `ArrayClass` → array behavior

---

### ⚡ Native Functions

Built-in utilities:

| Function        | Description            |
| --------------- | ---------------------- |
| `clock()`       | current time           |
| `input()`       | string input           |
| `numberInput()` | numeric input          |
| `len(x)`        | length of array/string |

---

## 🧪 Sample Script

```lox
var double = multiplier(2);
print double(5); // 10

class Person {
    init(name){
        this.name = name;
    }
}

var p = Person("Lakshya");
p.printName();
```

---

## 🚀 Advanced Features

* Closure capturing via environment chaining
* Function expressions (`fun (a) {}`)
* Dynamic property assignment
* Array indexing with mutation
* Static methods support

---

## 🛠️ Development Tools

### Generate AST Classes

```bash
./scripts/ast.sh
```

---

## 📈 Future Improvements

* Bytecode VM (performance 🚀)
* Garbage collector
* Type system / static typing
* REPL shell
* Debugger

---

## 👨‍💻 Author

**Lakshya Singh**

---

## 📄 License

MIT License
