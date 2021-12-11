import java.io.*;

public class Naloga4 {

    public static void main(String[] args) throws IOException {
        BufferedReader vhod = new BufferedReader(new FileReader(args[0]));
        BufferedWriter izhod = new BufferedWriter(new FileWriter(args[1]));

        // branje vhodnih podatkov
        int st_korakov = Integer.parseInt(vhod.readLine());
        int st_stolov = Integer.parseInt(vhod.readLine());
        int cas_strizenja = Integer.parseInt(vhod.readLine());
        int k = Integer.parseInt(vhod.readLine());  // za koliko se trajanje strizenja podaljsa z vsako naslednjo stranko
        
        String[] a = vhod.readLine().split(",");
        int[] prihodi = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            prihodi[i] = Integer.parseInt(a[i]);
        } 

        String[] b = vhod.readLine().split(",");
        int[] potrpljenja = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            potrpljenja[i] = Integer.parseInt(b[i]);
        } 

        vhod.close();


        Vrsta vrsta = new Vrsta();
        int korak = prihodi[0];              // Shranjuje na katerem koraku simulacije smo, začnemo pri prihodu prve stranke
        int p = potrpljenja.length;
        boolean zaseden = false;    // Ali je frizerski stol zaseden
        int trenutni_cas_cakanja_strizenje = 0;
        int st_strank = 0;
        int q = prihodi.length;
        int naslednja_stranka = prihodi[st_strank % q];
        Stranka[] ostrizene = new Stranka[st_korakov];
        int ostrizeni_count = 0;
        Stranka se_strize = new Stranka(0, 0);

            
        while (korak <= st_korakov) {
            vrsta.zmanjsajPotrpezljivost();
            
            // Ko stranko ostrižemo se cas strizenja poveca za k
            if (trenutni_cas_cakanja_strizenje == 0 && st_strank != 0) {
                zaseden = false;
                ostrizene[ostrizeni_count] = se_strize;
                se_strize = null;
                cas_strizenja += k;
                ostrizeni_count++;
            }  
            if (korak == st_korakov)
                break;
            
            // Če pride nova stranka in sta čakalnica in stol prazna jo postavimo direktno na stol 
            if (korak == naslednja_stranka && !zaseden && vrsta.empty()) { 
                se_strize = new Stranka(st_strank+1, potrpljenja[(st_strank+1)%p]);
                st_strank++;
                naslednja_stranka += prihodi[st_strank % q];
                trenutni_cas_cakanja_strizenje = cas_strizenja;
                zaseden =  true;
                continue;
            }

            // In na stol posedemo novo stranko
            if (!vrsta.empty() && !zaseden) {
                zaseden = true;
                trenutni_cas_cakanja_strizenje = cas_strizenja;
                se_strize = vrsta.getFirst();
                vrsta.remove();   
            }

            // Pregledamo ce je naslednja stranka v vrsti se tam
            if (!vrsta.empty()) {
                while (!vrsta.empty() && vrsta.getFirst().potrpljenje <= 0) {
                    vrsta.remove();
                }
            }
            
            // Prihod nove stranke na koraku "prihod"
            if (korak == naslednja_stranka) {
                
                // Če je prost stol se usede
                if (vrsta.getLength() < st_stolov && st_strank != 0) {
                    vrsta.add(new Stranka(st_strank+1, potrpljenja[st_strank%p]));
                    st_strank++;
                } else {
                    st_strank++;
                }
                naslednja_stranka += prihodi[st_strank % q];     // v koraku naslednja_stranka bo naslednji prihod
            }
    
            korak++;
            trenutni_cas_cakanja_strizenje--;
        }
     
        for (int i = 0; i < ostrizeni_count; i++) {
            if (ostrizene[i] == null) break;
            izhod.write(String.valueOf(ostrizene[i].id));
            if (i != ostrizeni_count-1 && ostrizene[i+1] != null)
                izhod.write(",");
        }
        izhod.write("\n");
        izhod.close();
    }
}

class Vrsta {
    private Stranka prva;
    private Stranka zadnja;

    public Vrsta() {
        makenull();
    }

    public void makenull() {
        this.prva = null;
        this.zadnja = null;
    }

    public Stranka getFirst() {
        return this.prva;
    }

    public Stranka getLast() {
        return this.zadnja;
    }

    public boolean empty () {
        return (this.prva == null);
    }

    public void add(Stranka s) {
        if (this.empty()) {
            this.prva = s;
            this.zadnja = s;
        } else {
            Stranka tmp = this.zadnja;
            tmp.setNext(s);
            this.zadnja = s;
        }
    }

    public void add(int id, int potrpljenje) {
        Stranka s = new Stranka(id, potrpljenje);
        
        if (this.empty()) {
            this.prva = s;
            this.zadnja = s;
        } else {
            Stranka tmp = this.zadnja;
            tmp.setNext(s);
            this.zadnja = s;
        }
    }

    public void zmanjsajPotrpezljivost() {
        Stranka s = this.prva;
        while (s != null) {
            s.potrpljenje--;
            s = s.getNext();
        }
    }

    // Odstrani prvi element v vrsti
    public void remove() {
        this.prva = this.prva.getNext();
    }

    public int getLength() {
        Stranka iter = this.prva;
        int len = 0;
        while (iter != null) {
            iter = iter.getNext();
            len++;
        }
        return len;
    }

    @Override
    public String toString() {
        String vsebina = "{";
        Stranka iter = this.prva;
        while (iter != null) {
            vsebina += String.valueOf(iter.id);

            if (iter.getNext() != null)
                vsebina += ", ";
            iter = iter.getNext();
        }
    
        return vsebina + "}";
    }
}

class Stranka {
    private Stranka next;
    public int id;
    public int potrpljenje;

    public Stranka(int id, int potrpljenje) {
        this.id = id;
        this.potrpljenje = potrpljenje;
        this.next = null;
    }

    public Stranka getNext() {
        if (this.next != null)
            return this.next;
        return null;
    } 

    public void setNext(Stranka s) {
        if (s != null) {
            Stranka tmp = this.getNext();
            this.next = s;
            s.next = tmp;
        } else {
            this.next = null;
        }
    }
}


