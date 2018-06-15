package com;

import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class FinishTaskRep implements CatalystSerializable{
    public int reqID;
    public boolean result;


    public FinishTaskRep() {}


    public FinishTaskRep(int reqID, boolean result) {
        this.reqID = reqID;
        this.result = result;
    }


    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(reqID);
        bufferOutput.writeBoolean(result);
    }


    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        reqID = bufferInput.readInt();
        result = bufferInput.readBoolean();
    }
}
