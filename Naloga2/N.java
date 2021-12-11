import java.io.*;

public class Naloga2 {
    
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.exit(1);
        }

        BufferedReader vhod = new BufferedReader(new FileReader(args[0]));
        PrintWriter izhod = new PrintWriter(new FileWriter(args[1]));
        
        String[][] besedilo = new String[10000][10000];
        String v; 
        int k = 0;
        while ((v = vhod.readLine()) != null) {
            String[] vrstica = v.split(" ");
            besedilo[k] = vrstica;
            k++;
        }

        String[][] okrnjeno = new String[k][1000];
        for (int ii = 0; ii < k; ii++) {
            okrnjeno[ii] = besedilo[ii];
        }
        /*
        System.out.println("Original: ");
        izpisi(okrnjeno);
        System.out.println();

        System.out.println("Obrnjene crke: ");
        obrniCrke(okrnjeno);
        izpisi(okrnjeno);
        System.out.println();
        
        System.out.println("Zamenjane besede: ");
        zamenjajBesede(okrnjeno);
        izpisi(okrnjeno);
        System.out.println();

        System.out.println("Obrnjene povedi: ");
        okrnjeno = obrni_povedi(okrnjeno);
        izpisi(okrnjeno);
        System.out.println();

        System.out.println("Obrnjeni lihi odstavki: ");
        okrnjeno = obrniLiheOdstavke(okrnjeno);
        izpisi(okrnjeno);
        System.out.println();
        */

        //for (int n = 0; n < povedi.length; n++) {
        //    System.out.printf("%s ", povedi[n]);
        //}
        //System.out.println();

        obrniCrke(okrnjeno);
        zamenjajBesede(okrnjeno);
        okrnjeno = obrni_povedi(okrnjeno);
        okrnjeno = obrniLiheOdstavke(okrnjeno);

        //System.out.println(okrnjeno.length);

        for (int i = 0; i < okrnjeno.length; i++) {
            //System.out.println(okrnjeno[i].length);
            for (int j = 0; j < okrnjeno[i].length; j++) {
                if (okrnjeno[i][j] == null) continue;
                //System.out.print(okrnjeno[i][j] + " (" + j + ") ");
                izhod.write(okrnjeno[i][j]);
                if (j != okrnjeno[i].length - 1) 
                    izhod.write(" ");
            }
            //System.out.println();
            if (i != okrnjeno.length-1)
                izhod.write("\n");
        } 

        vhod.close();
        izhod.close();
    }

    static int povedi = 0;

    // Obrne crke v besedah
    public static void obrniCrke(String[][] besedilo) {
        
        for (int i = 0; i < besedilo.length; i++) {
            for (int j = 0; j < besedilo[i].length; j++) {
                StringBuilder str = new StringBuilder();
                str.append(besedilo[i][j]);
                str.reverse();
                besedilo[i][j] = str.toString();
                if (besedilo[i][j].endsWith("."))
                    povedi++;
            }
        }
    }
    
    // Zamenja sode in lihe besede v povedih
    public static void zamenjajBesede(String[][] vsebina) {
        int stBesed = 1;
        for (int i = vsebina.length-1; i >= 0; i--) {
            // Zamenjaj besede v povedih v odstavku
            for (int j = vsebina[i].length-1; j > 0; j -=2 ) {

                // ce ima zadnja beseda piko ima poved liho stevilo besed, obrnemo vse razen zadnje.
                if (vsebina[i][j].endsWith(".") && stBesed % 2 == 1) { 
                    j--;
                }
            
                // gremo na naslednjo besedo
                if (Character.isUpperCase(vsebina[i][j].charAt(0))) {
                    stBesed = (vsebina[i][j] == "" ? -1 : 0);
                }   
                
                String tmp = vsebina[i][j];
                vsebina[i][j] = vsebina[i][j-1];
                vsebina[i][j-1] = tmp;
                stBesed++;
            }
        }
    }

    public static String[][] obrni_povedi (String[][] vsebina) {
        // Obrni najprej odstavke, potem se povedi
        String[][] obrnjeno = new String[vsebina.length][1];
        int o = vsebina.length-1;
        
        // Obrni odstavke
        for (int i = 0; i < vsebina.length; i++) {
            obrnjeno[o] = vsebina[i];
            o--;
        }          

        // Obrni povedi
        for (int i = 0; i < obrnjeno.length; i++) {
            String[] vrstica = obrnjeno[i];
            String[] obrnjena_vrstica = new String[vrstica.length];
            int op = 0;
            // Obrnemo povedi znotraj vsake vrstice (odstavka)
            for (int j = vrstica.length-1; j >= 0; j--) {
                // Izlusci poved - gremo od velike zacetnice do pike
                if (Character.isUpperCase(vrstica[j].charAt(0))) {
                    for (int beseda_povedi = j; beseda_povedi < vrstica.length; beseda_povedi++) {
                        if (op == obrnjena_vrstica.length) break;
                        obrnjena_vrstica[op] = vrstica[beseda_povedi];  
                        op++;
                        if (vrstica[beseda_povedi].endsWith("."))
                            break;
                    }  
                }
            }
            obrnjeno[i] = obrnjena_vrstica;
        }
        o = obrnjeno.length-1;
        for (int i = 0; i < obrnjeno.length; i++) {
            vsebina[i] = obrnjeno[o];
            o--;
        }
        return vsebina;
    }

    public static String[][] obrniLiheOdstavke(String[][] besedilo) {
        int st_odstavkov = besedilo.length;

        String[][] obrnjeno = new String[st_odstavkov][1];
        String[][] lihi = new String[st_odstavkov/2+1][1];
        //System.out.println("St odstavkov: " + st_odstavkov);
        
        int obratno = besedilo.length-1;

        // v obrnjeno zapišem lihe odstavke v rikverc
        if (st_odstavkov % 2 == 1) {
            for (int i = 0; i < st_odstavkov; i++) {
                //System.out.println("i: " + i + " obratno " + obratno);
                if (i % 2 == 0) {
                    //System.out.println("obrnjeno[" + i + "] = besedilo[" + obratno + "]");
                    obrnjeno[i] = besedilo[obratno];
                } else {
                    //System.out.println("obrnjeno[" + i + "] = besedilo[" + i + "]");
                    obrnjeno[i] = besedilo[i];
                } 
                obratno--;
            }
        } else {
            int ix_lihi = 0;
            for (int i = 0; i < st_odstavkov; i++) {
                //System.out.println("i: " + i + " obratno " + obratno);
                if (i % 2 == 0) {
                    //System.out.println("obrnjeno[" + i + "] = besedilo[" + obratno + "]");
                    lihi[ix_lihi] = besedilo[i];
                    ix_lihi++;
                }
                obrnjeno[i] = besedilo[i];
                obratno--;
            }
            ix_lihi--;
            for (int i = 0; i < st_odstavkov; i++) {
                if (i % 2 == 0) {
                    obrnjeno[i] = lihi[ix_lihi];
                    ix_lihi--;
                }
            }   
        }
        return obrnjeno;
    }
    

    public static void izpisi(String[][] besedilo) {
        boolean hadNull = false;
        for (int i = 0; i < besedilo.length; i++) {
            for (int j = 0; j < besedilo[i].length; j++) {
                if (besedilo[i][j] == null) {
                    hadNull = true;
                    break;
                }
                System.out.printf("%s ", besedilo[i][j]);
            }
            if (!hadNull)
                System.out.println();
        }
    }
}
