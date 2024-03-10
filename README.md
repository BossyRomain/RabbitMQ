# Compile the project

    javac -cp "$(ls -d "$PWD"/libs/* | tr '\n' ':')" $(find src | grep ".java") -d bin/
    
# Run the project
You can run any of the following programs, they take no argument unless specified

- `HelloWorld.Recv` is the receiver for the direct communication (Client-Server) implementation.
- `HelloWorld.Recv` is the sender for the direct communication (Client-Server) implementation.
- `WorkQueues.NewTask ...` is the producer in the MOM communication implementation. Takes the message task to pass as an argument.
- `WorkQueues.Worker` is the consumer in the MOM communication implementation.
- `PublishSubscribe.EmitLog ...` is the sender of "logs" topic in the Publish-Subscribe implementation. Takes the log to pass as an argument, has a default log otherwise.
- `PublishSubscribe.EmitNumber` is the sender of "numbers" topic in the Publish-Subscribe implementation. It sends a random number between 0 and 100.
- `PublishSubscribe.ReceiveLogs` is the receiver for both topics in the Publish-Subscribe implementation. Specifies the topic from which it receives, whether it is "logs" or "numbers".
- `PingPong.PingPong` is the PingPong implementation we have studied during the lecture, using MOM communication.

    java -cp "$(ls -d "$PWD"/libs/* | tr '\n' ':')bin" PROGRAM

