package DataStructure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * User: Sigurd Wien
 * Date: 14.04.13
 * Time: 22:45
 */
public class IndexHeader {
    public final int version;
    public final int numberOfClusters;

    public IndexHeader(int version, int numberOfClusters){
        this.version = version;
        this.numberOfClusters = numberOfClusters;
    }

    public void write(DataOutputStream dataOutputStream) throws IOException{
        dataOutputStream.writeInt(version);
        dataOutputStream.writeInt(numberOfClusters);
    }

    public static IndexHeader read(DataInputStream dataInputStream) throws IOException{
        int version = dataInputStream.readInt();
        int numberOfClusters = dataInputStream.readInt();
        return new IndexHeader(version, numberOfClusters);
    }
}
