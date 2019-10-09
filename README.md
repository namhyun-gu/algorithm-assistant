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
	- Support json output
- Provided information
  - Local variables per line
  - Reference count per line
  - Execution Output

- Preview

Input java source

```java
public class BubbleSort {
    public static void main(String[] args) {
        int[] items = {3, 1, 2};
        new BubbleSort().bubbleSort(items);
        for (int item : items) {
            System.out.println(item);
        }
    }

    void bubbleSort(int[] data) {
        for (int i = 0; i < data.length - 1; i++) {
            for (int j = 0; j < data.length - 1; j++) {
                if (data[j] > data[j + 1]) { // swap
                    int temp = data[j];
                    data[j] = data[j + 1];
                    data[j + 1] = temp;
                }
            }
        }
    }
}
```

Analzer result

```bash
PS C:\Users\namhyun-gu\Downloads> java -jar .\java.analyzer-1.0-SNAPSHOT.jar BubbleSort
Java Analyzer

- mainClassName: BubbleSort
- workingDir: C:\Users\namhyun-gu\Downloads
- inputFile: null
- outputFile: null
- verbose: false

=== Output ===

1
2
3

=== Analyze frames (size: 34) ===

main:3
        MethodEntryFrame(methodName=main, line=3)

main:3
        ArrayVariable(type=Array, name=args, value=[], uniqueId=84, size=0)

main:4
        ArrayVariable(type=Array, name=args, value=[], uniqueId=84, size=0)
        ArrayVariable(type=Array, name=items, value=[3, 1, 2], uniqueId=85, size=3)

bubbleSort:11
        MethodEntryFrame(methodName=bubbleSort, line=11)

bubbleSort:11
        ArrayVariable(type=Array, name=data, value=[3, 1, 2], uniqueId=85, size=3)

bubbleSort:12
        ArrayVariable(type=Array, name=data, value=[3, 1, 2], uniqueId=85, size=3)
        PrimitiveVariable(type=int, name=i, value=0)

bubbleSort:13
        ArrayVariable(type=Array, name=data, value=[3, 1, 2], uniqueId=85, size=3)
        PrimitiveVariable(type=int, name=i, value=0)
        PrimitiveVariable(type=int, name=j, value=0)

	(... Skip 16 frames)

bubbleSort:12
        ArrayVariable(type=Array, name=data, value=[1, 2, 3], uniqueId=85, size=3)
        PrimitiveVariable(type=int, name=i, value=1)
        PrimitiveVariable(type=int, name=j, value=1)

bubbleSort:11
        ArrayVariable(type=Array, name=data, value=[1, 2, 3], uniqueId=85, size=3)
        PrimitiveVariable(type=int, name=i, value=1)

bubbleSort:20
        ArrayVariable(type=Array, name=data, value=[1, 2, 3], uniqueId=85, size=3)

bubbleSort:20
        MethodExitFrame(methodName=bubbleSort, line=20)

main:5
        ArrayVariable(type=Array, name=args, value=[], uniqueId=84, size=0)
        ArrayVariable(type=Array, name=items, value=[1, 2, 3], uniqueId=85, size=3)

main:6
        ArrayVariable(type=Array, name=args, value=[], uniqueId=84, size=0)
        ArrayVariable(type=Array, name=items, value=[1, 2, 3], uniqueId=85, size=3)
        PrimitiveVariable(type=int, name=item, value=1)

main:5
        ArrayVariable(type=Array, name=args, value=[], uniqueId=84, size=0)
        ArrayVariable(type=Array, name=items, value=[1, 2, 3], uniqueId=85, size=3)

main:6
        ArrayVariable(type=Array, name=args, value=[], uniqueId=84, size=0)
        ArrayVariable(type=Array, name=items, value=[1, 2, 3], uniqueId=85, size=3)
        PrimitiveVariable(type=int, name=item, value=2)

main:5
        ArrayVariable(type=Array, name=args, value=[], uniqueId=84, size=0)
        ArrayVariable(type=Array, name=items, value=[1, 2, 3], uniqueId=85, size=3)

main:6
        ArrayVariable(type=Array, name=args, value=[], uniqueId=84, size=0)
        ArrayVariable(type=Array, name=items, value=[1, 2, 3], uniqueId=85, size=3)
        PrimitiveVariable(type=int, name=item, value=3)

main:5
        ArrayVariable(type=Array, name=args, value=[], uniqueId=84, size=0)
        ArrayVariable(type=Array, name=items, value=[1, 2, 3], uniqueId=85, size=3)

main:8
        ArrayVariable(type=Array, name=args, value=[], uniqueId=84, size=0)
        ArrayVariable(type=Array, name=items, value=[1, 2, 3], uniqueId=85, size=3)

main:8
        MethodExitFrame(methodName=main, line=8)


=== References per line ===

        3: 1
        4: 1
        11: 3
        12: 6
        13: 4
        14: 2
        15: 2
        16: 2
        20: 1
        5: 4
        6: 3
        8: 1
```

## License

[Apache License, Version 2.0](https://opensource.org/licenses/Apache-2.0)