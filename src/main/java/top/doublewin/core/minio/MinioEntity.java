package top.doublewin.core.minio;

import com.google.api.client.util.Key;
import io.minio.messages.Owner;

import java.util.Date;

public class MinioEntity {
    @Key("Key")
    private String objectName;
    @Key("LastModified")
    private Date lastModified;
    @Key("ETag")
    private String etag;
    @Key("Size")
    private long size;
    @Key("StorageClass")
    private String storageClass;
    @Key("Owner")
    private Owner owner;
    private boolean isDir;

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = storageClass;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public boolean isDir() {
        return isDir;
    }

    public void setDir(boolean dir) {
        isDir = dir;
    }
}
