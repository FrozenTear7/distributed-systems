import java.util.HashMap;
import java.util.Map;

public class StringMap implements SimpleStringMap {
    private HashMap<String, Integer> hashMap = new HashMap<>();

    @Override
    public boolean containsKey(String key) {
        return hashMap.containsKey(key);
    }

    @Override
    public Integer get(String key) {
        return hashMap.get(key);
    }

    @Override
    public void put(String key, Integer value) {
        hashMap.put(key, value);
    }

    @Override
    public Integer remove(String key) {
        return hashMap.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            result.append("Key = ").append(entry.getKey()).append(", Value = ").append(entry.getValue()).append("\n");
        }

        return result + "";
    }

    void clear() {
        hashMap.clear();
    }

    void deserialize(String list) {
        if (!list.equals("-1")) {
            String[] itemList = list.split("\n");

            for (String item : itemList) {
                String[] items = item.split(" ");

                hashMap.put(items[0], Integer.parseInt(items[1]));
            }
        }
    }

    String serialize() {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            result.append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
        }

        if (hashMap.size() == 0)
            return "-1";
        else
            return result.toString();
    }
}
