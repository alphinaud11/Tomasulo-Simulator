package Components;

import java.util.ArrayList;

public class LoadBuffer {

    public static class Entry {
        public int index;
        public int time;
        public String name;
        public int busy;
        public String address;

        public Entry(int number) {
            name = "L" + number;
            busy = 0;
        }

        public void setDefault() {
            busy = 0;
            address = null;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "time=" + time +
                    ", name='" + name + '\'' +
                    ", busy='" + busy + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

    public ArrayList<Entry> entries = new ArrayList<>();

    public LoadBuffer() {
        for (int i=1; i<=3; i++) {
            entries.add(new Entry(i));
        }
    }

    public void print() {
        for (Entry entry : entries) {
            System.out.println(entry);
        }
    }

    public static void main(String[] args) {
        LoadBuffer loadBuffer = new LoadBuffer();
        loadBuffer.print();
    }
}
