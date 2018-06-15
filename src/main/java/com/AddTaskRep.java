package com;

import business.Task;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class AddTaskRep implements CatalystSerializable {
    public Task task;
    public boolean result;

    public AddTaskRep() {}


    public AddTaskRep(Task task, boolean res) {
        this.task = task;
        this.result = res;
    }


    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        serializer.writeObject(task, bufferOutput);
        bufferOutput.writeBoolean(result);
    }


    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.task = serializer.readObject(bufferInput);
        this.result = bufferInput.readBoolean();
    }
}
