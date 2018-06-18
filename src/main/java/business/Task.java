package business;

import interfaces.Tasker;
import io.atomix.catalyst.buffer.BufferInput;
import io.atomix.catalyst.buffer.BufferOutput;
import io.atomix.catalyst.serializer.CatalystSerializable;
import io.atomix.catalyst.serializer.Serializer;

public class Task implements CatalystSerializable {
    private String client = "";
    private int id = 0;
    private String url = "";

    public Task() {}

    public Task(String url) {
        this.url = url;
    }

    public Task(int id, String url) {
        this.client = null;
        this.id = id;
        this.url = url;
    }


    public String getClient() {
        return client;
    }


    public void setClient(String client) {
        this.client = client;
    }


    public int getID() {
        return id;
    }


    public void setID(int id) {
        this.id = id;
    }


    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "id: " + id + " client: " + client + " url: " + url;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Task))
            return false;

        Task t = (Task) o;
        return this.id == t.getID() && this.url.equals(t.getUrl()) && this.client.equals(t.getClient());
    }

    @Override
    public void writeObject(BufferOutput<?> bufferOutput, Serializer serializer) {
        bufferOutput.writeString(client);
        bufferOutput.writeInt(id);
        bufferOutput.writeString(url);
    }

    @Override
    public void readObject(BufferInput<?> bufferInput, Serializer serializer) {
        client = bufferInput.readString();
        id = bufferInput.readInt();
        url = bufferInput.readString();
    }
}
