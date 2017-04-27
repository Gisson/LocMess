package pt.ulisboa.tecnico.ist.cmu.locmess.dto;

/**
 * Created by nuno on 26/04/17.
 */

public class TopicDto implements LocMessDto {
    private String key;
    private String value;

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TopicDto) {
            return key.equals(((TopicDto) obj).key)
                    && value.equals(((TopicDto) obj).value);
        }
        return false;
    }

    public TopicDto(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /** create a topic from a 'key=value' string */
    public TopicDto(String topic) {
        String[] fields = topic.split("=");
        key = fields[0];
        value = fields[1];
    }

    public String getKey() { return key; }
    public String getValue() { return value; }
    public String toString() {
        return key + "=" + value;
    }
}
