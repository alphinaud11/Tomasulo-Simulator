package Components;

import java.util.ArrayList;

public class AddStation {

    public static class Entry {
        public int index;
        public int time;
        public String name;
        public int busy;
        public String op;
        public String Vj;
        public String Vk;
        public String Qj;
        public String Qk;
        public String A;

        public Entry(int number) {
            name = "A" + number;
            busy = 0;
        }

        public void setDefault() {
            busy = 0;
            op = null;
            Vj = null;
            Vk = null;
            Qj = null;
            Qk = null;
            A = null;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "time=" + time +
                    ", name='" + name + '\'' +
                    ", busy=" + busy +
                    ", op='" + op + '\'' +
                    ", Vj='" + Vj + '\'' +
                    ", Vk='" + Vk + '\'' +
                    ", Qj='" + Qj + '\'' +
                    ", Qk='" + Qk + '\'' +
                    ", A='" + A + '\'' +
                    '}';
        }
    }

    public ArrayList<Entry> entries = new ArrayList<>();

    public AddStation() {
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
        AddStation addStation = new AddStation();
        addStation.print();
    }
}
