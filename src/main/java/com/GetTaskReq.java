package com;

import business.Task;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class GetTaskReq implements CatalystSerializable {
    public int reqID;


    public GetTaskReq() {}


    public GetTaskReq(int id) {
        this.reqID = id;
    }


    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(reqID);
    }


    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        this.reqID = bufferInput.readInt();
    }
}
