package com;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class ReallocateTasksReq implements CatalystSerializable {
    public int reqID;
    public String client;


    public ReallocateTasksReq() {}


    public ReallocateTasksReq(int reqID, String client) {
        this.reqID = reqID;
        this.client = client;
    }


    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(reqID);
        bufferOutput.writeString(client);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        reqID = bufferInput.readInt();
        client = bufferInput.readString();
    }
}
