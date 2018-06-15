package com;

import business.Task;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class AddTaskReq implements CatalystSerializable {
    public String url;

    public AddTaskReq() {}


    public AddTaskReq(Task t) {
        this.url = t.getUrl();
    }


    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(url);
    }


    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.url = bufferInput.readString();
    }
}
