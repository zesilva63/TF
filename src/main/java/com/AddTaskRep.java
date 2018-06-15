package com;

import business.Task;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class AddTaskRep implements CatalystSerializable {
    public int reqID;
    public Task task;
    public boolean result;

    public AddTaskRep() {}


    public AddTaskRep(int id, Task task, boolean res) {
        this.reqID = id;
        this.task = task;
        this.result = res;
    }


    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(reqID);
        serializer.writeObject(task, bufferOutput);
        bufferOutput.writeBoolean(result);
    }


    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.reqID = bufferInput.readInt();
        this.task = serializer.readObject(bufferInput);
        this.result = bufferInput.readBoolean();
    }
}
