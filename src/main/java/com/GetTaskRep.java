package com;

import business.Task;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class GetTaskRep implements CatalystSerializable {
    public int reqID;
    public Task task;


    public GetTaskRep() {}


    public GetTaskRep(int id, Task t) {
        this.reqID = id;
        this.task = t;
    }


    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(reqID);
        serializer.writeObject(task,bufferOutput);
    }


    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        reqID = bufferInput.readInt();
        task = serializer.readObject(bufferInput);
    }
}