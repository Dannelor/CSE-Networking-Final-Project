Mason Beckham 1001073976
Minh-Quan Nguyen 1001032212

Compilation:
    javac src/TCPSimulation/*.java src/TCPSimulation/AgentStories/*.java src/TCPSimulation/Functional/*.java src/TCPSimulation/Main/*.java src/TCPSimulation/Utility/*.java

Execution:
    Windows:
        start.bat will startup all necessary programs
    Others:
        Run these commands in command line
        java -classpath out\production\Project3.2 TCPSimulation.Main.RouterMain A
        java -classpath out\production\Project3.2 TCPSimulation.Main.RouterMain B
        java -classpath out\production\Project3.2 TCPSimulation.Main.RouterMain C
        java -classpath out\production\Project3.2 TCPSimulation.Main.RouterMain D
        java -classpath out\production\Project3.2 TCPSimulation.Main.RouterMain E
        java -classpath out\production\Project3.2 TCPSimulation.Main.RouterMain F
        java -classpath out\production\Project3.2 TCPSimulation.Main.RouterMain G
        java -classpath out\production\Project3.2 TCPSimulation.Main.RouterMain L

        java -classpath out\production\Project3.2 TCPSimulation.Main.AgentMain H
        java -classpath out\production\Project3.2 TCPSimulation.Main.AgentMain Ann
        java -classpath out\production\Project3.2 TCPSimulation.Main.AgentMain Jan
        java -classpath out\production\Project3.2 TCPSimulation.Main.AgentMain Chan

Assumptions and System Requirements:
    Able to have many sockets and threads open at once
    Able to write files to directory running in
