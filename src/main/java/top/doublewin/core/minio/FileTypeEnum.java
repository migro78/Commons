package top.doublewin.core.minio;

public enum FileTypeEnum {
    图片("picture"),
    EXCEL("excel"),
    WORD("word"),
    其他文件("file");

    private String value;
    FileTypeEnum(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
