# Distributed Computing Assignment Phase - 1

## Problem-Statement : 

Process a Heavy file of 1GB. Assign a document id to each line. Perform this using : 
- Sequential Program
- Multi-Threaded
- Using Hadoop Map only

## Pre-requisites : 

- Download Hadoop 3.3.6 (as `hadoop`) and set up properly
- Download wikipedia dump file of atleast 1GB from wikimedia.org and name it as wiki_dump.xml

## Sequential : 

A simple sequential program using Java `BufferedReader` and `BufferedWriter` libraries.

Note that, By default, A buffer of 64kB is used. You can modify it if you want. We went with default only.

The output will be written to `sequence_output.txt`.

To run : 
```
javac Sequence.java
java Sequence
```

## Multi-Threaded : 

Say there are `n` threads. Our program divides file into `n` equal parts and assigns docId concurrently.

Used java `Executors` framework for threads.

Used `RandomAccessFile` for each thread to reach to starting byte of its assigned chunk of file. Then converting it back to `BufferedReader` 
for better performance.

To run : 
```
javac ConcurrentFileProcessing.java
java ConcurrentFileProcessing <no. of threads>
```

## using Hadoop (Pseudo-Distributed Mode): 

1. First start HDFS.
   `./hadoop/sbin/start-dfs.sh`
2. Format and push the input file to HDFS.
   ```
   ./hadoop/bin/hdfs namenode -format
    ./hadoop/bin/hdfs dfs -mkdir -p user/<username>
    ./hadoop/bin/hdfs dfs -mkdir input
    ./hadoop/bin/hdfs dfs -put ./wiki_dump.xml input
   ```
3. Compile MapOnly.java
   `./hadoop/bin/hadoop com.sun.tools.javac.Main MapOnly.java`
4. Compress all `.class` files to a `.jar` file
   `jar cf mo.jar MapOnly*.class`
5. Then execute Map Reduce :
   `./hadoop/bin/hadoop jar mo.jar MapOnly /user/<user>/input /user/<username>/output`
6. Get back the output from HDFS to local
   `./hadoop/bin/hdfs dfs -get output output`

## PC specs : 

| **Property**    | **Spec** |
| :--- | :--- |
| **Processor**  | intel core i5 8th gen    |
| **No of Cores** | 4 |
| **Speed** | 1.6 GHz
| **RAM** | 8GB    |
| **OS** | Linux Mint    |
| **Disk Write Speed** | 85.2 MB/s | 

## Evaluating Performance

| **Run** | **Sequential** | **Threads (n = 2)** | **Threads (n=4)** | **Map-Only** |
| :--- | :---:  | :---: | :---: | :---: |
| Run-1 | 48.576 | 50.845 | 46.94 | 64.56 |
| Run-2 | 29.174 | 49.937 | 48.56 | 63.96 |
| Average (in s) | 38.875 | 50.391 | 47.75 | 64.26 |

**Note** : We observed that on subsequent runs runtimes got unbelievably better (almost got halved). Later we found out that it is because my OS is caching inodes of file in main memory. So for better evaluation we need to drop cache. Run the following to do that : 
`sudo sh -c "sync && echo 3 > /proc/sys/vm/drop_caches"`

Probably, because of thread switching and other thread related overheads, using multiple threads is performing poor than Sequential. and also Map-Only program is taking a lot of time because it is run in psudeo-distributed mode in a single node.

## Team Members : 
- Nikesh Tadela (22CSB0B21)
- Prateek Kumar (22CSB0B37)



  
