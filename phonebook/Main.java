package phonebook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws IOException{
        ArrayList<Directory> darray = readDirectory();
        ArrayList<String> farray = readFind();
        TimeDuration td = new TimeDuration();
        TimeDuration td2 = new TimeDuration();
        TimeDuration td3 = new TimeDuration();
       
        System.out.println("Start searching (linear search)...");
        td.start();
        int found = linearSearch(darray, farray);
        long duration = td.stop();
        System.out.println(String.format("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.", found, farray.size(), td.mm, td.ss, td.ms));
        System.out.println();
        
        long limitDuration = duration * 10;
        System.out.println("Start searching (bubble sort + jump search)...");
        td.start();
        td2.start();
        boolean sortResult = bubbleSort(darray, limitDuration);
        td2.stop();
        if (sortResult) { 
            td3.start();
            found = jumpSearch(darray, farray);
            td3.stop();
            td.stop();
            System.out.println(String.format("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.", found, farray.size(), td.mm, td.ss, td.ms));
            System.out.println(String.format("Sorting time: %d min. %d sec. %d ms.", td2.mm, td2.ss, td2.ms)); 
            System.out.println(String.format("Searching time: %d min. %d sec. %d ms.", td3.mm, td3.ss, td3.ms));
        } else {
            td3.start();
            found = linearSearch(darray, farray);
            td3.stop();
            td.stop();
            System.out.println(String.format("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.", found, farray.size(), td.mm, td.ss, td.ms));
            System.out.println(String.format("Sorting time: %d min. %d sec. %d ms. - STOPPED, moved to linear search", td2.mm, td2.ss, td2.ms));
            System.out.println(String.format("Searching time: %d min. %d sec. %d ms.", td3.mm, td3.ss, td3.ms));
        }

        System.out.println();
        darray = readDirectory();
        System.out.println("Start searching (quick sort + binary search)...");
        td.start();
        td2.start();
        quickSort(darray);
        td2.stop();
        td3.start();
        found = binarySearch(darray, farray);
        td3.stop();
        td.stop();
        System.out.println(String.format("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.", found, farray.size(), td.mm, td.ss, td.ms));
        System.out.println(String.format("Sorting time: %d min. %d sec. %d ms.", td2.mm, td2.ss, td2.ms)); 
        System.out.println(String.format("Searching time: %d min. %d sec. %d ms.", td3.mm, td3.ss, td3.ms));

        System.out.println();
        darray = readDirectory();
        System.out.println("Start searching (hash table)...");
        td.start();
        td2.start();
        HashMap<String, Long> map = createMap(darray);
        td2.stop();
        td3.start();
        found = mapSearch(map, farray);
        td3.stop();
        td.stop();
        System.out.println(String.format("Found %d / %d entries. Time taken: %d min. %d sec. %d ms.", found, farray.size(), td.mm, td.ss, td.ms));
        System.out.println(String.format("Creating time: %d min. %d sec. %d ms.", td2.mm, td2.ss, td2.ms)); 
        System.out.println(String.format("Searching time: %d min. %d sec. %d ms.", td3.mm, td3.ss, td3.ms));
    }

    static HashMap<String, Long> createMap(ArrayList<Directory> darray) {
        HashMap<String, Long> map = new HashMap<>();
        for (Directory entry: darray) {
            map.put(entry.name, entry.id);
        }
        return map;
    }

    static int mapSearch(HashMap<String, Long> map, ArrayList<String> farray) {
        int count = 0;
        for (String name: farray) {
            if (map.get(name) >= 0) {
                count++;
            }
        }
        return count;
    }

    static int binarySearch(ArrayList<Directory> darray, ArrayList<String> farray) {
        int count = 0;
        for (String name: farray) {
            if (binarySearch(darray, name, 0, darray.size() - 1) >= 0) {
                count++;
            }
        }
        return count;
    }

    public static int binarySearch(ArrayList<Directory> array, String name, int left, int right) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            int result = name.compareTo(array.get(mid).name);
            if (result == 0) { 
                return mid;
            } else if (result < 0) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return -1;
    }
  
    static void quickSort(ArrayList<Directory> darray) {
        quickSort(darray, 0, darray.size() - 1);
    }

    public static void quickSort(ArrayList<Directory> array, int left, int right) {
        if (left < right) {
            int pivotIndex = partition(array, left, right);
            quickSort(array, left, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, right); 
        }
    }
        
    private static int partition(ArrayList<Directory> array, int left, int right) {
        Directory pivot = array.get(right);
        int partitionIndex = left;
    
        for (int i = left; i < right; i++) {
            if (array.get(i).name.compareTo(pivot.name) <= 0) { 
                swap(array, i, partitionIndex);
                partitionIndex++;
            }
        }
    
        swap(array, partitionIndex, right);
    
        return partitionIndex;
    }
    
    private static void swap(ArrayList<Directory> array, int i, int j) {
        Directory temp = array.get(i);
        array.set(i, array.get(j));
        array.set(j, temp); 
    }
 
    static boolean bubbleSort(ArrayList<Directory> darray, long limitDuration) {
        TimeDuration td = new TimeDuration();
        td.start();

        for (int k = 0; k < darray.size(); k++) {
            for (int i = 0; i < darray.size() - 1; i++) {
                if (darray.get(i).name.compareTo(darray.get(i + 1).name) > 0) {
                    Directory temp = darray.get(i);
                    darray.set(i, darray.get(i + 1));
                    darray.set(i + 1, temp);
                }
            }
            if (td.stop() > limitDuration) {
                return false; 
            }
        }

        return true;
    }

    static int linearSearch(ArrayList<Directory> darray, ArrayList<String> farray) {
        int count = 0;

        for (String name: farray) {
            for (Directory directory: darray) {
                if (name.equals(directory.name)) {
                    count++;
                    break;
                }
            }
        }

        return count;
    }

    static int jumpSearch(ArrayList<Directory> darray, ArrayList<String> farray) {
        int count = 0;
        for (String name: farray) {
            if (jumpSearch(darray, name) >= 0) {
                count++;
            }
        }
        return count;
    }

    static int jumpSearch(ArrayList<Directory> darray, String name) {
        int currentRight = 0; 
        int prevRight = 0; 
    
        if (darray.size() == 0) {
            return -1;
        }    
        if (darray.get(currentRight).name.equals(name)) {
            return currentRight;
        }
    
        int jumpLength = (int) Math.sqrt(darray.size());
    
        while (currentRight < darray.size() - 1) {
            currentRight = Math.min(darray.size() - 1, currentRight + jumpLength);
            if (darray.get(currentRight).name.compareTo(name) >= 0) {
                break; 
            }
            prevRight = currentRight; 
        }
     
        if ((currentRight == darray.size() - 1) && name.compareTo(darray.get(currentRight).name) > 0) {
            return -1;
        } 
    
        return backwardSearch(darray, name, prevRight, currentRight);
    }
  
    public static int backwardSearch(ArrayList<Directory> darray, String name, int leftExcl, int rightIncl) {
        for (int i = rightIncl; i > leftExcl; i--) {
            if (darray.get(i).name.equals(name)) {
                return i;
            }
            if (darray.get(i).name.compareTo(name) < 0) {
                return -1;
            }
        }
        return -1;  
    }

    static ArrayList<Directory> readDirectory() throws IOException {
        String fileName = "C:\\private\\src\\java\\idea\\data\\directory.txt";
        FileReader r = new FileReader(fileName);
        BufferedReader br = new BufferedReader(r);
        ArrayList<Directory> array = new ArrayList<>();
    
        String rec;
        while ((rec = br.readLine()) != null) {
            String[] strs = rec.split(" ");
            long id = Long.parseLong(strs[0]);
            int i = rec.indexOf(' ');
            String name = rec.substring(i + 1);
            array.add(new Directory(id, name));
        }
    
        br.close();
        return array;
    }

    static ArrayList<String> readFind() throws IOException {
        String fileName = "C:\\private\\src\\java\\idea\\data\\find.txt";
        FileReader r = new FileReader(fileName);
        BufferedReader br = new BufferedReader(r);
        ArrayList<String> array = new ArrayList<>(); 
    
        String rec;
        while ((rec = br.readLine()) != null) {
            array.add(rec);
        }
    
        br.close();
        return array;
    }
}

class Directory {
    long id;
    String name;
    
    Directory(long id, String name) {
        this.id = id;
        this.name = name;
    }
}

class TimeDuration {
    int mm;
    int ss;
    int ms;
    long startTime;

    void start() {
        startTime = System.currentTimeMillis();    
    }

    long stop() {
        long endTime = System.currentTimeMillis();    
        int time = (int) (endTime - startTime);
        mm = time / 60000;
        ss = (time - mm * 60000) / 1000;
        ms = time % 1000;
        return endTime - startTime;
    }
}