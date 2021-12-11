import java.io.*;import java.util.*;//327
interface Naloga5{
    static void main(String[]a)throws Exception{
        try(Writer w=new FileWriter(a[1])){
            Scanner c=new Scanner(new File(a[0]));

            int s=1,x=0,n=c.nextInt(),i=0,b=-1,m,M[]=new int[n];
            for(;i<n;x^=M[i++])
                s+=M[i]=c.nextInt();
            for(;++b<n;)
                i=x>1?(x^(m=M[b]))<m?(m^x)-m+s:i:-1;

            w.write(i+"");
}}}import java.io.*;
import java.util.Arrays;


public class Naloga5 {

    public static void main(String[] args) throws IOException {
      
        BufferedReader vhod = new BufferedReader(new FileReader(args[0]));
        BufferedWriter izhod = new BufferedWriter(new FileWriter(args[1]));

        int st_kupckov = Integer.parseInt(vhod.readLine());
        int[] kupcki = new int[st_kupckov];
        for (int i = 0; i < st_kupckov; i++) {
            kupcki[i] = Integer.parseInt(vhod.readLine());
        }
        
        if (zeroSum(kupcki, st_kupckov))   // Prvi igralec bo izgubil
            izhod.write("-1\n");
        else {
            izhod.write(String.valueOf(odigraj(kupcki, st_kupckov, 1)) + "\n");
            //izhod.write(String.valueOf(najdiUstrezno(kupcki, st_kupckov, 1, 0, 0, 999, 1)) + "\n");
        }
        
        vhod.close();
        izhod.close();
    }
    
    /*
        izhod: stevilo potez do neizogibnega poraza drugega igralca || -1 če bo prvi igralec zgubil ob optimalnih potezah drugega
        drugi se trudi čim kasneje zgubiti
    */
    public static int odigraj(int[] kupcki, int st_kupckov, int igralec_na_potezi) {
        sort(kupcki, st_kupckov);
        //System.out.println(Arrays.toString(kupcki));
        if (gameOver(kupcki, st_kupckov)) {
            System.out.println("Zmagovalec je " + (igralec_na_potezi == 1 ? 2 : 1));
            return 0;
        }
        
        int st_potez = 0;
        
        if (igralec_na_potezi == 1) {
            //System.out.println("Na potezi je igralec st. 1, sum mora biti false, da ga nastavi na true, sum: " + zeroSum(kupcki, st_kupckov));
            // prvi igralec naj naredi optimalno potezo
            makeBestMove(kupcki, st_kupckov);
            st_potez = odigraj(kupcki, st_kupckov, 2) + 1;
        }    
        if (igralec_na_potezi == 2) {
            // Drugi igralec zeli izgubiti pocasi zato izvlece en kamencek iz nekega kupcka
            for (int i = 0; i < st_kupckov; i++) {
                if (kupcki[i] > 0) {
                    kupcki[i]--; 
                    //System.out.println("Igralec 2 je naredil svojo potezo");
                    break;
                }
            }
            st_potez = odigraj(kupcki, st_kupckov, 1) + 1;
            
            // naredil je potezo in na vrsti je prvi igralec, ki pa mora narediti optimalno potezo
        }
        //System.out.println(Arrays.toString(kupcki));
        return st_potez;
    }

    public static void makeBestMove(int[] kupcki, int st_kupckov) {
        if (gameOver(kupcki, st_kupckov))
            return;             

        //for (int i = st_kupckov-1; i >= 0; i--) {
        for (int i = 0; i < st_kupckov; i++) {
            for (int j = kupcki[i]; j > 0; j--) {
                kupcki[i] -= j;             
                if (zeroSum(kupcki, st_kupckov)) {
                    //System.out.println("Igralec 1 je iz kupcka " + i + "  pobral " + j + " kamenckov");
                    return;
                } else {
                    kupcki[i] += j;
                }
            }
        }
    }

    public static void sort(int[] kupcki, int st_kupckov) {
        Arrays.sort(kupcki);
        //System.out.println(Arrays.toString(kupcki));
    }

    public static boolean gameOver(int[] kupcki, int st_kupckov) {
        for (int i = st_kupckov-1; i >= 0; i--) {
            if (kupcki[i] != 0)
                return false;
        }
        return true;
    }
    
    public static boolean zeroSum(int[] kupcki, int st_kupckov) {
        int[] bin = new int[st_kupckov];
        int res = 0;
        for (int i = 0; i < st_kupckov; i++) {
            bin[i] = Integer.parseInt(Integer.toBinaryString(kupcki[i]));
            if (i == 0)
                res = bin[i];
            else {
                res ^= bin[i];
            }
        }
        //System.out.println(Arrays.toString(bin));
        return (res == 0 ? true : false);
    }
}

/*abstractpublic static int odigraj(int[] kupcki, int st_kupckov, int igralec_na_potezi) {
        //sort(kupcki, st_kupckov);
        System.out.println(Arrays.toString(kupcki));
        if (gameOver(kupcki, st_kupckov)) {
            return 0;
        }
        
        int st_potez = 0;
        
        if (igralec_na_potezi == 2) {
            // Drugi igralec zeli izgubiti pocasi zato izvlece en kamencek iz nekega kupcka
            for (int i = 0; i < st_kupckov; i++) {
                if (kupcki[i] > 0) {
                    kupcki[i]--; 
                    System.out.println("Igralec 2 je naredil svojo potezo");
                    break;
                }
            }
            
            // naredil je potezo in na vrsti je prvi igralec, ki pa mora narediti optimalno potezo
            st_potez = odigraj(kupcki, st_kupckov, 1) + 1;
        }
        
        if (igralec_na_potezi == 1) {
            System.out.println("Na potezi je igralec st. 1, sum mora biti 1, da ga nastavi na 0, sum: " + zeroSum(kupcki, st_kupckov));
            // prvi igralec naj naredi optimalno potezo
            makeBestMove(kupcki, st_kupckov);
            st_potez = odigraj(kupcki, st_kupckov, 2) + 1;
        }      
       
        return st_potez;
    }

    public static void makeBestMove(int[] kupcki, int st_kupckov) {
        if (gameOver(kupcki, st_kupckov))
            return;             

        for (int i = st_kupckov-1; i >= 0; i--) {
            for (int j = kupcki[i]; j > 0; j--) {
                kupcki[i] -= j;             
                if (zeroSum(kupcki, st_kupckov)) {
                    System.out.println("Igralec 1 je iz kupcka " + i + "  pobral " + j + " kamenckov");
                    return;
                } else {
                    kupcki[i] += j;
                }
            }
        }
    }
*/