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
            //long startTime = System.nanoTime(); 
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
        }

        LinkedListElement iter = seznam.getFirst();

        // vpisi st clenov
        izhod.write(String.valueOf(seznam.getNodeCount()) + "\n");
        iter = seznam.getFirst();

        // izpisi clene
        for (int j = 0; j < seznam.getNodeCount(); j++) {
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
    private int nodeCount;

    public LinkedListArray() {
        init(5);
    }

    public void init(int N) {
        this.N = N;
        this.first = new LinkedListElement(N);
        this.nodeCount = 1;
    }

    public int getNodeCount() {
        return this.nodeCount;
    }

    public LinkedListElement getFirst() {
        return this.first;
    }

    private void addNode(int N, LinkedListElement element, LinkedListElement nextnext) {
        this.nodeCount += 1;
        LinkedListElement nov = new LinkedListElement(N, nextnext);
        element.setNext(nov);
    }

    // Odstrani element z pozicije p
    public boolean remove(int pozicija) {
        int index = pozicija2indeks(pozicija);

        LinkedListElement iter = first;

        if (index == -1 || pozicija < 0 || pozicija >= (this.nodeCount * N))
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
      
        // izbrisemo element
        iter.clen[ix_element] = null;
        iter.minusElementCount();
        iter.zamakniLevo(ix_element);

        if (iter.getElementCount() == 0 && iter.getNext() != null) {
            this.nodeCount -= 1;
            if (iter == first) {
                first = iter.getNext();
                iter = first;
            } else {
                LinkedListElement prev = findPrev(iter);
                prev.setNext(iter.getNext());
            }
        }

        // moramo prenesti elemente z desnega clena da jih dobimo N/2
        LinkedListElement iter_next = iter.getNext();
        int i = iter.getElementCount();
        int j = 0;
        if (iter.getElementCount() < N / 2 && iter_next != null && iter_next.getElementCount() != 0) {
            while (iter.getElementCount() != N / 2) {
                iter.clen[i] = iter_next.clen[j];
                iter_next.clen[j] = null;
                i++; j++;
                iter.plusElementCount();
                iter_next.minusElementCount();
            }
            iter_next.zamakniLevo(0);

            // ce je v naslednjem clenu premalo elementov jih prepisemo v tega
            i = iter.st_elementov();
            int st_elementov_next = iter_next.st_elementov();
            j = 0;
            if (st_elementov_next < N / 2) {
                while (st_elementov_next != 0) {
                    iter.clen[i] = iter_next.clen[j];
                    iter_next.clen[j] = null;
                    i++;
                    j++;
                    st_elementov_next--;
                    iter.plusElementCount();
                    iter_next.minusElementCount();
                }
            }
            // ce je prazen ga zbrisemo
            if (iter_next == null || iter_next.getElementCount() == 0) {
                iter.setNext(iter_next.getNext());
                this.nodeCount -= 1;
            }
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

    // Vstavi vrednost v na pozicijo p
    public boolean insert(int vrednost, int pozicija) {
        LinkedListElement iter = first;

        int index = pozicija2indeks(pozicija);
        if (index == -1 || pozicija < 0 || pozicija >= (this.nodeCount * N))
            return false;

        int ix_clen = index / N;
        int ix_element = index % N;

        // najdemo clen z p tim elementom
        for (int i = 0; i < ix_clen; i++) {
            iter = iter.getNext();
        }

        // vstavimo element na p-to mesto (iter[ix_element])
        // Če je v tem členu prostor
        if (iter.hasNull()) {
            if (iter.clen[ix_element] != null)
                iter.zamakniDesno(ix_element);
            iter.clen[ix_element] = vrednost;
            iter.plusElementCount();
            return true;
        } else {
            // Sicer poišči p-ti člen
            int index1 = pozicija2indeks(pozicija);
            if (index1 / N != ix_clen) {
                if (index == -1 || pozicija < 0 || pozicija >= (this.nodeCount * N))
                    return false;

                ix_clen = index1 / N;
                ix_element = index1 % N;
                iter = first;

                // najdemo clen z p tim elementom
                for (int i = 0; i < ix_clen; i++) {
                    iter = iter.getNext();
                }
                if (iter.hasNull()) {
                    if (iter.clen[ix_element + 1] != null)
                        iter.zamakniDesno(ix_element);
                    iter.clen[ix_element + 1] = vrednost;
                    iter.plusElementCount();
                    return true;
                }
            } else {

                // Sicer povezi nov clen
                addNode(N, iter, iter.getNext());
                int st_elementov = N / 2;
                int a = st_elementov;
                if (st_elementov % 2 == 1)
                    a++;

                LinkedListElement iter_new = iter.getNext();

                // polovico pusti v iter, polovico prepisi v naslednjega
                for (int i = N - 1, j = Math.abs(a - N); i >= st_elementov; i--, j--) {
                    iter_new.clen[j] = iter.clen[i];
                    iter.clen[i] = null;
                    iter_new.plusElementCount();
                    iter.minusElementCount();
                }
                iter_new.zamakniLevo();
                return insert(vrednost, pozicija);
            }
        }
        return false;
    }

    // deli z dolzino elementov da dobis pravi element, indeks je ostanek, -1
    // pomeni, da ni dovolj elementov za vrednost
    private int pozicija2indeks(int pozicija) {
        int elementi = 0;
        int dejanska_pozicija = 0;
        LinkedListElement iter = first;
        // ustavi se en element prej
        while (elementi + iter.getElementCount() < pozicija && iter != null) {
            elementi += iter.getElementCount();
            dejanska_pozicija += N;
            iter = iter.getNext();
        }
        
        for (int i = 0; i < N; i++) {
            if (elementi == pozicija) 
                return dejanska_pozicija;
            elementi++;
            dejanska_pozicija++;
        }

        return dejanska_pozicija;

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
    private int elementCount;

    public LinkedListElement(int N) {
        this.N = N;
        this.clen = new Object[N];
        this.next = null;
        this.elementCount = 0;
    }

    public LinkedListElement(int N, LinkedListElement next) {
        this.N = N;
        this.clen = new Object[N];
        this.next = next;
        this.elementCount = 0;
    }

    public LinkedListElement getNext() {
        if (this.next == null)
            return null;
        return this.next;
    }

    public void setNext(LinkedListElement n) {
        this.next = n;
    }

    public Object[] getElement() {
        return this.clen;
    }

    public int getLength() {
        return this.N;
    }

    public int getElementCount() {
        return this.elementCount;
    }

    public void minusElementCount() {
        this.elementCount -= 1; 
    }

    public void plusElementCount() {
        this.elementCount += 1;
    }

    public int st_elementov() {
        if (this == null)
            return 0;
        return this.getElementCount();
    }

    public void zamakniDesno(int index) {
        if (this.clen[N - 1] == null) {
            for (int i = this.N - 1; i > index; i--) {
                this.clen[i] = this.clen[i - 1];
            }
        }
    }

    public void zamakniLevo() {
        if (this.hasNull()) {
            boolean found = false;
            for (int i = 0; i < N - 1; i++) {
                if (!found && this.clen[i] != null)
                    continue;
                if (this.clen[i] == null)
                    found = true;
                if (i == N - 1)
                    this.clen[i] = null;
                this.clen[i] = this.clen[i + 1];
            }
        }
    }

    public void zamakniLevo(int index) {
        if (this.clen[index] == null || this.hasNull()) {
            for (int i = index; i < N - 1; i++) {
                this.clen[i] = this.clen[i + 1];
            }
            this.clen[N - 1] = null;
        }
    }

    public boolean hasNull() {
        if (this == null)
            return false;

        if (this.getElementCount() != N)
            return true;

        return false;
    }



}
