# algorithm-assistant

![GitHub](https://img.shields.io/github/license/namhyun-gu/algorithm-assistant)
![GitHub repo size](https://img.shields.io/github/repo-size/namhyun-gu/algorithm-assistant)
![GitHub last commit](https://img.shields.io/github/last-commit/namhyun-gu/algorithm-assistant)

> Provide assist tools for solve algorithm problems.

## Overview

### java-analyzer

- Analyze '.class' file by Java Debug Interface.
- Provided features
  - Support input redirection
- Provided information
  - Local variables per line
  - Reference count per line
  - Execution Output

- Preview

Input java source

```java
1   import java.util.Scanner;
2 
3   public class Main2 {
4     public static void main(String[] args) {
5        Scanner scanner = new Scanner(System.in);
6        int count = scanner.nextInt();
7        for (int index = 0; index < count; index++) {
8            System.out.println("Hello");
9        }
10    }
11  }
```

Analzer result

```bash
PS C:\Users\namhyun-gu\Downloads> java -jar .\java.analyzer-1.0-SNAPSHOT.jar -i hello.txt Main2
Java Analyzer

- mainClassName: Main2
- inputFile: hello.txt
- workingDir: C:\Users\namhyun-gu\Downloads
- verbose: false
- outputFile: null

=== Output ===

Hello
Hello
Hello

=== Analyze frames (size: 10) ===

main:5
	ArrayVariable(name='args', values=[], size=0, uniqueId=84)

main:6
	ArrayVariable(name='args', values=[], size=0, uniqueId=84)
	ObjectVariable(type='java.util.Scanner', name='scanner', value=instance of java.util.Scanner(id=377), uniqueId=377)

main:7
	ArrayVariable(name='args', values=[], size=0, uniqueId=84)
	ObjectVariable(type='java.util.Scanner', name='scanner', value=instance of java.util.Scanner(id=377), uniqueId=377)
	Variable(type='int', name='count', value=3)

main:8
	ArrayVariable(name='args', values=[], size=0, uniqueId=84)
	ObjectVariable(type='java.util.Scanner', name='scanner', value=instance of java.util.Scanner(id=377), uniqueId=377)
	Variable(type='int', name='count', value=3)
	Variable(type='int', name='index', value=0)

main:7
	ArrayVariable(name='args', values=[], size=0, uniqueId=84)
	ObjectVariable(type='java.util.Scanner', name='scanner', value=instance of java.util.Scanner(id=377), uniqueId=377)
	Variable(type='int', name='count', value=3)
	Variable(type='int', name='index', value=0)

main:8
	ArrayVariable(name='args', values=[], size=0, uniqueId=84)
	ObjectVariable(type='java.util.Scanner', name='scanner', value=instance of java.util.Scanner(id=377), uniqueId=377)
	Variable(type='int', name='count', value=3)
	Variable(type='int', name='index', value=1)

main:7
	ArrayVariable(name='args', values=[], size=0, uniqueId=84)
	ObjectVariable(type='java.util.Scanner', name='scanner', value=instance of java.util.Scanner(id=377), uniqueId=377)
	Variable(type='int', name='count', value=3)
	Variable(type='int', name='index', value=1)

main:8
	ArrayVariable(name='args', values=[], size=0, uniqueId=84)
	ObjectVariable(type='java.util.Scanner', name='scanner', value=instance of java.util.Scanner(id=377), uniqueId=377)
	Variable(type='int', name='count', value=3)
	Variable(type='int', name='index', value=2)

main:7
	ArrayVariable(name='args', values=[], size=0, uniqueId=84)
	ObjectVariable(type='java.util.Scanner', name='scanner', value=instance of java.util.Scanner(id=377), uniqueId=377)
	Variable(type='int', name='count', value=3)
	Variable(type='int', name='index', value=2)

main:10
	ArrayVariable(name='args', values=[], size=0, uniqueId=84)
	ObjectVariable(type='java.util.Scanner', name='scanner', value=instance of java.util.Scanner(id=377), uniqueId=377)
	Variable(type='int', name='count', value=3)


=== References per line ===

	5: 1
	6: 1
	7: 4
	8: 3
	10: 1

```

## License

[Apache License, Version 2.0](https://opensource.org/licenses/Apache-2.0)