# java-analyzer

## Getting Started

```bash
# Build Jar
./gradlew jar

# Check Jar output
cd build/libs

# Run jar
java -jar java.analyzer-1.0-SNAPSHOT.jar [OPTIONS] [MAINCLASSNAME]
```

## Usage

```bash
Usage: java-analyzer [OPTIONS] [MAINCLASSNAME]

  Analyze '.class' file by Java Debug Interface.

Options:
  -d, --dir TEXT     Set working directory, defaults current directory
  -i, --input FILE   Redirection standard input using file
  -o, --output FILE  Redirection analyze output using file
  --json             Set output json format
  -v, --verbose      Print analyze logs
  -h, --help         Show this message and exit
```


## Preview

- Input java source

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

- Output

```bash
PS C:\Users\namhyun-gu\Downloads> java -jar .\java.analyzer-1.0-SNAPSHOT.jar BubbleSort
Java Analyzer

- mainClassName: BubbleSort
- workingDir: C:\Users\namhyun-gu\Downloads
- inputFile: null
- outputFile: null
- jsonResult: false
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
        ArrayVariable(type=Array, name=items, value=[PrimitiveVariable(type=int, name=, value=3), PrimitiveVariable(type=int, name=, value=1), PrimitiveVariable(type=int, name=, value=2)], uniqueId=85, size=3)

bubbleSort:11
        MethodEntryFrame(methodName=bubbleSort, line=11)

bubbleSort:11
        ArrayVariable(type=Array, name=data, value=[PrimitiveVariable(type=int, name=, value=3), PrimitiveVariable(type=int, name=, value=1), PrimitiveVariable(type=int, name=, value=2)], uniqueId=85, size=3)

        ... more frames

main:8
        ArrayVariable(type=Array, name=args, value=[], uniqueId=84, size=0)
        ArrayVariable(type=Array, name=items, value=[PrimitiveVariable(type=int, name=, value=1), PrimitiveVariable(type=int, name=, value=2), PrimitiveVariable(type=int, name=, value=3)], uniqueId=85, size=3)

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