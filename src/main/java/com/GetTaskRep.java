package com;

import business.Task;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class GetTaskRep implements CatalystSerializable {
    public int reqID;
    public Task task;
    public boolean result;


    public GetTaskRep() {}


    public GetTaskRep(int id, Task t, boolean res) {
        this.reqID = id;
        this.task = t;
        this.result = res;
    }


    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(reqID);
        serializer.writeObject(task,bufferOutput);
        bufferOutput.writeBoolean(result);
    }


    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        reqID = bufferInput.readInt();
        task = serializer.readObject(bufferInput);
        result = bufferInput.readBoolean();
    }
}