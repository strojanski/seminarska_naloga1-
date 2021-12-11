
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Naloga3 {

    public static void main(String[] args) throws IOException {
        BufferedReader ukazi = new BufferedReader(new FileReader(args[0]));
        BufferedWriter izhod = new BufferedWriter(new FileWriter(args[1]));

        LinkedListArray seznam = new LinkedListArray();

        int st_ukazov = Integer.parseInt(ukazi.readLine());
        String ukaz;
        int n = 5;
        for (int i = 0; i < st_ukazov; i++) {
            
            //long startTime = System.currentTimeMillis(); 
            ukaz = ukazi.readLine();
            switch (ukaz.charAt(0)) {
                case 's':
                    n = Integer.parseInt(ukaz.split(",")[1]);
                    seznam.init(n);
                    break;
                
                case 'i':
                    int v = Integer.parseInt(ukaz.split(",")[1]);
                    int p = Integer.parseInt(ukaz.split(",")[2]);
                    seznam.insert(v, p);    
                    break;

                case 'r':
                    p = Integer.parseInt(ukaz.split(",")[1]);
                    seznam.remove(p);
                    break;

                default:
                    System.out.println("Invalid command");
                    break;
                }
          //      long endTime = System.currentTimeMillis();
            
          //      long duration = (endTime - startTime);
          //      if (duration > 1)
          //          System.out.println("izvajanje ukaza je trajalo: " + duration + " milisekund. Ukaz: " + ukaz);
                
        }
        //System.out.println("Velikost tabele: " + n + ". St. ukazov: " + st_ukazov);

     
        LinkedListElement iter = seznam.getFirst();
        // prestej st clenov
        int st_clenov = 0;
        while (iter != null) {
            st_clenov++;
            iter = iter.getNext();
        }
        /*
        String cleni = String.valueOf(st_clenov);
        izhod.write(cleni);
        izhod.write("\n");
        iter = seznam.getFirst();

        // izpisi clene
        while (iter != null) {
            for (int i = 0; i < n; i++) {
                if (iter.clen[i] == null)
                    izhod.write("NULL");
                else
                    izhod.write(iter.clen[i].toString());
                if (i != iter.clen.length - 1)
                    izhod.write(",");
            }
            izhod.write("\n");
            iter = iter.getNext();
        }*/
       
        String cleni = String.valueOf(st_clenov);
        System.out.println(cleni);
        iter = seznam.getFirst();

        // izpisi clene
        while (iter != null) {
            for (int i = 0; i < n; i++) {
                if (iter.clen[i] == null)
                    System.out.print("NULL");
                else
                    System.out.print(iter.clen[i].toString());
                if (i != iter.clen.length - 1)
                    System.out.print(",");
            }
            System.out.print("\n");
            iter = iter.getNext();
        }

        ukazi.close();
        izhod.close();
    }
}

/*
 * izgled: člen = [ ... ], element = int ==> [1,2,3]->[4,5,6]->[7,8,9]
 */

// povezani seznam klasicnih seznamov
class LinkedListArray {

    private int N;
    private LinkedListElement first;

    public LinkedListArray() {
        init(5);
    }

    public void init(int N) {
        this.N = N;
        this.first = new LinkedListElement(N);
    }

    public LinkedListElement getFirst() {
        return this.first;
    }

    private void add_node(int N, LinkedListElement element, LinkedListElement nextnext) {
        LinkedListElement nov = new LinkedListElement(N, nextnext);
        element.setNext(element, nov);
    }

    // Odstrani element z pozicije p
    public boolean remove(int pozicija) {
        int index = pozicija2indeks(pozicija);

        LinkedListElement iter = first;

        if (index == -1 || pozicija < 0 || pozicija >= (st_clenov(iter) * N))
            return false;

        int ix_clen = index / N;
        int ix_element = index % N;

        // najdemo clen z p-tim elementom
        for (int i = 0; i < ix_clen; i++) {
            iter = iter.getNext();
        }

        if (iter.clen[ix_element] == null) {
            int i = ix_element;
            while (iter.clen[i] == null) {
                if (i == N - 1) {
                    i = 0;
                    if (iter.getNext() != null)
                        iter = iter.getNext();
                }
                if (iter.clen[i] != null)
                    break;
                i++;
            }
            ix_element = i;
        }
        // System.out.println("Izbrisan bo element: " + iter.clen[ix_element] + " z
        // indeksom " + index + " in položajem " + pozicija);

        // izbrisemo element
        iter.clen[ix_element] = null;
        zamakniLevo(iter, ix_element);

        if (isNull(iter) && iter.getNext() != null) {
            if (iter == first) {
                first = iter.getNext();
                iter = first;
            } else {
                LinkedListElement prev = findPrev(iter);
                iter.setNext(prev, iter.getNext());
            }
        }

        // moramo prenesti elemente z desnega clena da jih dobimo N/2

        int st_elementov = st_elementov(iter);

        LinkedListElement iter_next = iter.getNext();
        int i = st_elementov;
        int j = 0;
        if (st_elementov < N / 2 && !isNull(iter_next)) {
            while (st_elementov != N / 2) {
                iter.clen[i] = iter_next.clen[j];
                iter_next.clen[j] = null;
                i++;
                j++;
                st_elementov++;
            }
            zamakniLevo(iter_next, 0);

            // ce je v naslednjem clenu premalo elementov jih prepisemo v tega
            i = st_elementov(iter);
            int st_elementov_next = st_elementov(iter_next);
            j = 0;
            if (st_elementov_next < N / 2) {
                while (st_elementov_next != 0) {
                    iter.clen[i] = iter_next.clen[j];
                    iter_next.clen[j] = null;
                    i++;
                    j++;
                    st_elementov_next--;
                }
            }
            // ce je prazen ga zbrisemo
            if (isNull(iter_next))
                iter.setNext(iter, iter_next.getNext());
        }

        return false;
    }

    private LinkedListElement findPrev(LinkedListElement e) {
        LinkedListElement iter = first;
        if (e != first) {
            while (iter.getNext() != e)
                iter = iter.getNext();
        }
        return iter;
    }

    private boolean isNull(LinkedListElement e) {
        if (e != null) {
            for (int i = 0; i < N; i++) {
                if (e.clen[i] != null)
                    return false;
            }
        }
        return true;
    }

    // Vstavi vrednost v na pozicijo p
    public boolean insert(int vrednost, int pozicija) {
        LinkedListElement iter = first;

        int index = pozicija2indeks(pozicija);
        if (index == -1 || pozicija < 0 || pozicija >= (st_clenov(first) * N))
            return false;

        int ix_clen = index / N;
        int ix_element = index % N;

        // najdemo clen z p tim elementom
        for (int i = 0; i < ix_clen; i++) {
            iter = iter.getNext();
        }

        // vstavimo element na p-to mesto (iter[ix_element])
        // Če je v tem členu prostor
        if (hasNull(iter)) {
            if (iter.clen[ix_element] != null)
                zamakniDesno(iter, ix_element);
            iter.clen[ix_element] = vrednost;
            return true;
        } else {
            // Sicer poišči p-ti člen
            int index1 = pozicija2indeks(pozicija);
            if (index1 / N != ix_clen) {
                if (index == -1 || pozicija < 0 || pozicija >= (st_clenov(first) * N))
                    return false;

                ix_clen = index1 / N;
                ix_element = index1 % N;
                iter = first;

                // najdemo clen z p tim elementom
                for (int i = 0; i < ix_clen; i++) {
                    iter = iter.getNext();
                }
                if (hasNull(iter)) {
                    if (iter.clen[ix_element + 1] != null)
                        zamakniDesno(iter, ix_element);
                    iter.clen[ix_element + 1] = vrednost;
                    return true;
                }
            } else {

                // Sicer povezi nov clen
                add_node(N, iter, iter.getNext());
                int st_elementov = N / 2;
                int a = st_elementov;
                if (st_elementov % 2 == 1)
                    a++;

                LinkedListElement iter_new = iter.getNext();

                // polovico pusti v iter, polovico prepisi v naslednjega
                for (int i = N - 1, j = Math.abs(a - N); i >= st_elementov; i--, j--) {
                    iter_new.clen[j] = iter.clen[i];
                    iter.clen[i] = null;
                }
                zamakniLevo(iter_new);
                return insert(vrednost, pozicija);
            }
        }
        return true;
    }

    private int st_clenov(LinkedListElement first) {
        int count = 0;
        LinkedListElement e = first;
        while (e != null) {
            e = e.getNext();
            count++;
        }
        return count;
    }

    private int st_elementov(LinkedListElement e) {
        int count = 0;
        if (e == null)
            return 0;
        for (int i = 0; i < N; i++) {
            if (e.clen[i] != null)
                count++;
        }
        return count;
    }

    private boolean hasNull(LinkedListElement e) {
        if (e == null)
            return false;

        for (int i = 0; i < N; i++) {
            if (e.clen[i] == null)
                return true;
        }

        return false;
    }

    private void zamakniDesno(LinkedListElement e, int index) {
        if (e.clen[N - 1] == null) {
            for (int i = this.N - 1; i > index; i--) {
                e.clen[i] = e.clen[i - 1];
            }
        }
    }

    private void zamakniLevo(LinkedListElement e, int index) {
        if (e.clen[index] == null || hasNull(e)) {
            for (int i = index; i < N - 1; i++) {
                e.clen[i] = e.clen[i + 1];
            }
            e.clen[N - 1] = null;
        }
    }

    private void zamakniLevo(LinkedListElement e) {
        if (hasNull(e)) {
            boolean found = false;
            for (int i = 0; i < N - 1; i++) {
                if (!found && e.clen[i] != null)
                    continue;
                if (e.clen[i] == null)
                    found = true;
                if (i == N - 1)
                    e.clen[i] = null;
                e.clen[i] = e.clen[i + 1];
            }
        }
    }

    // deli z dolzino elementov da dobis pravi element, indeks je ostanek, -1
    // pomeni, da ni dovolj elementov za vrednost
    private int pozicija2indeks(int pozicija) {
        int index = 0;
        int ix_iter = 0;
        int i = 0;
        LinkedListElement iter = first;
        while (true) {
            if (iter == null) {
                return -1;
            }
            if (i == pozicija)
                break;
            if (iter.clen[ix_iter] != null)
                i++;
            if (ix_iter == iter.clen.length - 1) {
                iter = iter.getNext();
                ix_iter = -1;
            }
            index++;
            ix_iter++;
        }
        return index;
    }

    @Override
    public String toString() {
        String vsebina = "";
        LinkedListElement iter = first;
        while (iter != null) {
            for (int i = 0; i < iter.clen.length; i++) {
                vsebina += iter.clen[i] + " ";
            }
            if (iter.getNext() != null)
                vsebina += "\n";
            iter = iter.getNext();
        }
        return vsebina;
    }
}

// klasicen seznam z kazalcem na naslednji seznam
class LinkedListElement {
    private int N;
    private LinkedListElement next;
    public Object[] clen;

    public LinkedListElement(int N) {
        this.N = N;
        this.clen = new Object[N];
        next = null;
    }

    public LinkedListElement(int N, LinkedListElement next) {
        this.N = N;
        this.clen = new Object[N];
        this.next = next;
    }

    public LinkedListElement getNext() {
        if (this.next == null)
            return null;
        return this.next;
    }

    public void setNext(LinkedListElement e, LinkedListElement n) {
        e.next = n;
    }

    public Object[] getElement() {
        return this.clen;
    }

    public int getLength() {
        return this.N;
    }

}