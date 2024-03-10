# Compile the project

    javac -cp "$(ls -d "$PWD"/libs/* | tr '\n' ':')" $(find src | grep ".java") -d bin/
    
# Run the project
You can run any of the following programs:

- `HelloWorld.Recv` is the receiver for the direct communication (Client-Server) implementation.
- `HelloWorld.Recv` is the sender for the direct communication (Client-Server) implementation.
- `WorkQueues.NewTask` is the producer in the MOM communication implementation.
- `WorkQueues.Worker` is the consumer in the MOM communication implementation.
- `PublishSubscribe.EmitLog` is the sender of "log" topic in the Publish-Subscribe implementation.
- `PublishSubscribe.EmitNumber` is the sender of "number" topic in the Publish-Subscribe implementation.
- `PublishSubscribe.ReceiveLogs` is the receiver for both topics in the Publish-Subscribe implementation.
- `PingPong.PingPong` is the PingPong implementation we have studied during the lecture, using .

    java -cp "$(ls -d "$PWD"/libs/* | tr '\n' ':')bin" PROGRAM

