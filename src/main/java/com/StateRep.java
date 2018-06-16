package com;

import business.Task;
import business.TaskerImpl;
import interfaces.Tasker;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class StateRep implements CatalystSerializable {
    private TaskerImpl tasker;
    private int reqId;

    public StateRep() {
    }

    public StateRep(TaskerImpl tasker, int reqId) {

        this.tasker = tasker;
        this.reqId = reqId;
    }

    public TaskerImpl getTasker() {
        return tasker;
    }

    public int getReqId() {
        return reqId;
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeInt(reqId);
        serializer.writeObject(tasker, bufferOutput);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        reqId = bufferInput.readInt();
        tasker = serializer.readObject(bufferInput);
    }
}
